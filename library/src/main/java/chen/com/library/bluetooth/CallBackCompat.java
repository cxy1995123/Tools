package chen.com.library.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.os.Build;

import java.util.List;

public class CallBackCompat {

    private Object callback;

    private BluetoothAdapter adapter;


    public CallBackCompat(BluetoothAdapter adapter) {
        this.adapter = adapter;
        getCallBack();
    }


    public void startScan() {
        if (adapter == null) return;
        if (Build.VERSION.SDK_INT >= 21) {
            if (adapter.getBluetoothLeScanner() != null) {
                adapter.getBluetoothLeScanner().startScan((ScanCallback) callback);
            }
        } else {
            adapter.startLeScan((BluetoothAdapter.LeScanCallback) callback);
        }
    }

    public void stopScan() {
        if (adapter == null || callback == null) return;
        if (Build.VERSION.SDK_INT >= 21) {
            if (adapter.getBluetoothLeScanner() != null) {
                adapter.getBluetoothLeScanner().stopScan((ScanCallback) callback);
            }
        } else {
            adapter.stopLeScan((BluetoothAdapter.LeScanCallback) callback);
        }
    }

    public void getCallBack() {
        if (Build.VERSION.SDK_INT >= 21) {
            callback = new CallBack21() {
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    super.onScanResult(callbackType, result);
                    BluetoothDevice device = result.getDevice();
                    ScanRecord record = result.getScanRecord();
                    String deviceName = parseDeviceName(record);

                    CallBackCompat.this.onScanResult(device);
                }

                @Override
                public void onBatchScanResults(List<ScanResult> results) {
                    super.onBatchScanResults(results);
                }

                @Override
                public void onScanFailed(int errorCode) {
                    super.onScanFailed(errorCode);
                }
            };
        } else {
            callback = new CallBack19() {
                @Override
                public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                    super.onLeScan(device, rssi, scanRecord);
                    onScanResult(device);

                }
            };
        }
    }

    public void onScanResult(BluetoothDevice device) {

    }

    public void onDestroy() {

        stopScan();

        if (adapter != null) {
            adapter = null;
        }

        if (callback != null) {
            callback = null;
        }
    }


}
