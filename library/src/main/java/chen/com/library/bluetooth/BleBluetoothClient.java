package chen.com.library.bluetooth;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BleBluetoothClient {

    private static String TAG = "BleBluetoothUtil";

    private boolean isBle = false;
    private BluetoothAdapter adapter;

    private Context context;

    /**
     * 一些通信的UUID
     */
    private String notifyUUID;
    private String writeUUID;
    private String descriptorUUID;
    private String gattServiceUUID;

    private List<BluetoothDevice> deviceList = new ArrayList<>();

    private BluetoothGatt mbluetoothGatt;

    private boolean isSystem = false;

    /**
     * 蓝牙连接状态
     */
    private int bluethhthState = BluetoothProfile.STATE_DISCONNECTED;

    public BleBluetoothClient() {

    }

    private BleBluetoothClient(Context context) {
        init(context, false);
    }

    private BleBluetoothClient(Context context, boolean system) {
        init(context, system);
    }

    private void init(Context context, boolean system) {
        this.context = context;
        isBleBluetooth(context);
        getBluetoothAdapter();
        this.isSystem = system;
        if (system) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(BluetoothDevice.ACTION_FOUND);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            context.registerReceiver(mBluetoothReceiver, filter);
        }
    }

    public boolean isConnect() {
        return bluethhthState == BluetoothProfile.STATE_CONNECTED;
    }


    /**
     * 是否支持Ble
     */
    private boolean isBleBluetooth(Context context) {
        isBle = context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
        return isBle;
    }

    private BluetoothAdapter getBluetoothAdapter() {
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        if (bluetoothManager != null && adapter == null) {
            adapter = bluetoothManager.getAdapter();
        }
        return adapter;
    }

    /**
     * 自动打开蓝牙
     */
    public void openBluetooth() {
        if (adapter == null) {
            Log.i(TAG, "打开蓝牙失败");
        }
        if (!adapter.isEnabled()) {
            adapter.enable();
        }
    }


    public void startScan() {
        if (isSystem) {
            startScan2();
        } else {
            startScan1();
        }
    }

    public void stopScan() {
        if (isSystem) {
            stopScan2();
        } else {
            stopScan();
        }
    }


    private void startScan1() {
        if (adapter == null) return;
        if (callBack == null) {
            callBack = new CallBackCompat(adapter) {
                @Override
                public void onScanResult(final BluetoothDevice device) {
                    super.onScanResult(device);
                    if (!deviceList.contains(device)) {
                        deviceList.add(device);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (bleCallBack != null) {
                                    bleCallBack.onDeviceChange(device);
                                }
                            }
                        });
                    }
                }
            };
        }
        callBack.startScan();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (callBack != null) {
                    callBack.stopScan();
                }
            }
        }, 1000 * 15);


    }

    private void startScan2() {

        if (adapter != null) {
            adapter.startDiscovery();
        }
    }

    private void stopScan2() {
        if (adapter != null) {
            adapter.cancelDiscovery();
        }
    }

    private void stopScan1() {
        callBack.stopScan();
    }


    /**
     * 手动连接蓝牙
     */
    public void connectGatt(BluetoothDevice device) {
        BluetoothGattCallback callback = new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, final int newState) {
                super.onConnectionStateChange(gatt, status, newState);

                if (bleCallBack != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            bleCallBack.onConnectionStateChange(newState);
                        }
                    });
                }

                bluethhthState = newState;
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    Log.i(TAG, "蓝牙已连接");
                    mbluetoothGatt.discoverServices();
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    Log.i(TAG, "蓝牙已断开");
                    if (mbluetoothGatt != null) {
                        mbluetoothGatt.close();
                        mbluetoothGatt = null;
                    }

                } else if (newState == BluetoothProfile.STATE_DISCONNECTING) {
                    Log.i(TAG, "蓝牙正在连接");
                }
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt2, int status) {
                // GATT Services discovered
                //发现新的服务
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    Log.i(TAG, "发现蓝牙服务");
//                    createCancel();

                    if (bleCallBack != null) {
                        bleCallBack.onFindService();
                    }
                }
            }

            //通过 Descriptor 写监听
            @Override
            public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    //开启监听成功，可以像设备写入命令了
                    Log.e(TAG, "onDescriptorWrite GATT_SUCCESS");
                }
            }

            // 通过 Descriptor 读监听
            @Override
            public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    //开启监听成功，读取信息
                    Log.e(TAG, "onDescriptorWrite GATT_SUCCESS");

                }
            }

            @Override
            public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic
                    characteristic, int status) {
                //write操作会调用此方法
                Log.i(TAG, "onCharacteristicWrite: ");
                if (status == BluetoothGatt.GATT_SUCCESS) {

                } else {

                }
            }

            @Override
            public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                // 接收数据
                Log.i(TAG, "onCharacteristicRead: ");
            }

            //
            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                //notify 会回调用此方法
                // // value为设备发送的数据，根据数据协议进行解析
                byte[] value = characteristic.getValue();
                Log.i(TAG, "onCharacteristicChanged: ");

            }

        };
        mbluetoothGatt = device.connectGatt(context, false, callback);
    }

    /**
     * 建立双方的通信通道
     */
    public void createCancel() {
        if (mbluetoothGatt == null) return;

        //获取服务列表  // 例如形式如：49535343-fe7d-4ae5-8fa9-9fafd205e455
        BluetoothGattService service = mbluetoothGatt.getService(UUID.fromString(gattServiceUUID));
        //开启监听，即建立与设备的通信的首发数据通道，BLE开发中只有当上位机成功开启监听后才能与下位机收发数据。开启监听的方式如下：
        BluetoothGattCharacteristic notifyCharacteristic = service.getCharacteristic(UUID.fromString(notifyUUID));
        //接收通道

        final int charaProp = notifyCharacteristic.getProperties();

        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
            mbluetoothGatt.setCharacteristicNotification(notifyCharacteristic, true);


            boolean enable_notifi = false;
            //发送通道
            //若开启监听成功则会回调BluetoothGattCallback中的onDescriptorWrite()方法
            BluetoothGattCharacteristic writeCharacteristic = service.getCharacteristic(UUID.fromString(writeUUID));
            if (writeCharacteristic != null) {
                BluetoothGattDescriptor descriptor = writeCharacteristic.getDescriptor(UUID.fromString(descriptorUUID));
                if (descriptor != null) {
                    descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    enable_notifi = mbluetoothGatt.writeCharacteristic(writeCharacteristic);
                }
            }

            //只发送 ENABLE_NOTIFICATION_VALUE 有可能打不开 notifyCharacteristic
            //所以再发送 ENABLE_INDICATION_VALUE
            if (!enable_notifi) {
                BluetoothGattCharacteristic writeCharacteristic2 = service.getCharacteristic(UUID.fromString(writeUUID));
                if (writeCharacteristic2 != null) {
                    BluetoothGattDescriptor descriptor2 = writeCharacteristic2.getDescriptor(UUID.fromString(descriptorUUID));
                    descriptor2.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
                    mbluetoothGatt.writeCharacteristic(writeCharacteristic2);
                }
            }

        } else {
            Log.i(TAG, "createCancel: " + "不支持 notify ");
        }

    }

    public List<BluetoothGattService> getService() {
        return mbluetoothGatt.getServices();
    }

    public BluetoothGattService getBluetoothGattService(BluetoothGatt gatt) {
        if (gatt == null) return null;
        return gatt.getService(UUID.fromString(gattServiceUUID));
    }

    public BluetoothGattCharacteristic getCharacteristic(String uuId) {
        BluetoothGattService service = getBluetoothGattService(mbluetoothGatt);
        if (service != null) {
            return service.getCharacteristic(UUID.fromString(uuId));
        }
        return null;
    }

    /**
     * 外部回调
     */
    private BleCallBack bleCallBack;

    private void setCallBack(BleCallBack callBack) {
        this.bleCallBack = callBack;
    }

    public interface BleCallBack {

        void onConnectionStateChange(int state);

        void onMessageChange(byte[] bytes);

        void onFindService();

        void onDeviceChange(BluetoothDevice device);

    }

    private void setDescriptorUUID(String descriptorUUID) {
        this.descriptorUUID = descriptorUUID;
    }

    private void setNotifyUUID(String notifyUUID) {
        this.notifyUUID = notifyUUID;
    }

    private void setWriteUUID(String writeUUID) {
        this.writeUUID = writeUUID;
    }

    private void setGattServiceUUID(String gattServiceUUID) {
        this.gattServiceUUID = gattServiceUUID;
    }


    private Handler handler = new Handler(Looper.getMainLooper());

    /**
     * @see #startScan1()
     */
    private CallBackCompat callBack;

    /**
     * @see #startScan2()
     */
    private BroadcastReceiver mBluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (BluetoothDevice.ACTION_FOUND.equals(intent.getAction())) {
                final BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                int rssi = intent.getExtras().getShort(BluetoothDevice.EXTRA_RSSI);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (bleCallBack != null) {
                            bleCallBack.onDeviceChange(device);
                        }
                    }
                });

            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(intent.getAction())) {

            }
        }
    };

    public static boolean checkPermission(Context context) {
        int permission = PermissionChecker.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
        int permission1 = PermissionChecker.checkSelfPermission(context, Manifest.permission.BLUETOOTH);
        return permission == PackageManager.PERMISSION_GRANTED && permission1 == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestPermission(Activity activity) {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.BLUETOOTH}, 100);
    }

    public void onDestroy() {
        if (mbluetoothGatt != null) {
            mbluetoothGatt.close();
            mbluetoothGatt = null;
        }

        if (context != null) {
            context.unregisterReceiver(mBluetoothReceiver);
        }

        if (callBack != null) {
            callBack.onDestroy();
        }

        if (adapter != null) {
            if (adapter.isDiscovering()) {
                adapter.cancelDiscovery();
            }

            adapter.disable();
            adapter = null;
        }

    }


    public static class Builder {
        private Context context;
        private BleCallBack bleCallBack;
        private String notifyUUID;
        private String writeUUID;
        private String descriptorUUID;
        private String gattServiceUUID;
        private boolean isSystem;

        public Builder context(Context context) {
            this.context = context;
            return this;
        }

        public Builder BleCallBack(BleCallBack bleCallBack) {
            this.bleCallBack = bleCallBack;
            return this;
        }

        public Builder isSystem(boolean isSystem) {
            this.isSystem = isSystem;
            return this;
        }

        public Builder notifyuuId(String uuid) {
            this.notifyUUID = uuid;
            return this;
        }

        public Builder writeuuid(String uuid) {
            this.writeUUID = uuid;
            return this;
        }

        public Builder descriptoruuid(String uuid) {
            this.descriptorUUID = uuid;
            return this;
        }

        public Builder gattServiceuuid(String uuid) {
            this.gattServiceUUID = uuid;
            return this;
        }

        public BleBluetoothClient builder() {
            BleBluetoothClient client = new BleBluetoothClient(context, isSystem);
            client.setDescriptorUUID(descriptorUUID);
            client.setNotifyUUID(notifyUUID);
            client.setGattServiceUUID(gattServiceUUID);
            client.setWriteUUID(writeUUID);
            return client;
        }


    }

}
