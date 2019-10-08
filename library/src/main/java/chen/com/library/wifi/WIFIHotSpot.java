package chen.com.library.wifi;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.ResultReceiver;
import android.provider.Settings;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import chen.com.library.wifi.client.WifiClient;

import static android.content.Context.WIFI_SERVICE;

/**
 * 需要权限
 * <uses-permission android:name="android.permission.WRITE_SETTINGS"/>
 */
public class WIFIHotSpot {


    private WifiManager mWifManager;
    private Context context;

    public WIFIHotSpot(WifiManager manager, Context context) {
        this.mWifManager = manager;
        this.context = context;
    }

    /**
     * 打开热点并且创建WiFi热点的方法
     *
     * @param ssid     要创建WiFi热点的账号名称
     * @param password 要创建WiFi热点的密码
     *                 注意,此方法直接使用WPA2_PSK 的安全策略创建WiFi热点,低版本的Android系统如果需要使用请切换。
     */
    private void openWiFiAP24(String ssid, String password) {
        if (!isEditSystem()) {
            return;
        }

        if (mWifManager.isWifiEnabled()) {
            //如果wifi处于打开状态，则关闭wifi,
            mWifManager.setWifiEnabled(false);
        }
        WifiConfiguration config = new WifiConfiguration();
        config.SSID = ssid;
        config.preSharedKey = password;
        config.hiddenSSID = false;//是否隐藏热点true=隐藏
        config.allowedAuthAlgorithms
                .set(WifiConfiguration.AuthAlgorithm.OPEN);//开放系统认证
        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);

        //public static final int NONE = 0;
        //public static final int WPA_PSK = 1;
        //public static final int WPA_EAP = 2;
        //public static final int IEEE8021X = 3
        //publib static final int WPA2_PSK = 4; 6.0以上只有这个值
        int indexOfWPA2_PSK;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            indexOfWPA2_PSK = 4;
        } else {
            indexOfWPA2_PSK = 1;
        }
        //从WifiConfiguration.KeyMgmt数组中查找WPA2_PSK的值
        //for (int i = 0; i < WifiConfiguration.KeyMgmt.strings.length; i++) {
        //            if (WifiConfiguration.KeyMgmt.strings[i].equals("WPA2_PSK")) {
        //                indexOfWPA2_PSK = i;
        //                break;
        //            }
        //        }
        config.allowedKeyManagement.set(indexOfWPA2_PSK);
        config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        config.status = WifiConfiguration.Status.ENABLED;
        //通过反射调用设置热点
        try {
            Method method = mWifManager.getClass().getMethod(
                    "setWifiApEnabled", WifiConfiguration.class, Boolean.TYPE);
            boolean enable = (Boolean) method.invoke(mWifManager, config, true);
            if (enable) {
                Log.e("WiFiAP", "热点已开启 SSID:" + ssid + " Password:" + password);
            } else {
                Log.e("WiFiAP", "创建热点失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("WiFiAP", "创建热点失败" + e);
        }

    }

    private void openWiFiAP26(String ssid, String password) {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (mWifManager != null) {
            int wifiState = mWifManager.getWifiState();
            boolean isWifiEnabled = ((wifiState == WifiManager.WIFI_STATE_ENABLED) || (wifiState == WifiManager.WIFI_STATE_ENABLING));
            if (isWifiEnabled)
                mWifManager.setWifiEnabled(false);
        }
        if (mConnectivityManager != null) {
            try {
                Field internalConnectivityManagerField = ConnectivityManager.class.getDeclaredField("mService");
                internalConnectivityManagerField.setAccessible(true);
                WifiConfiguration apConfig = new WifiConfiguration();
                apConfig.SSID = ssid;
                apConfig.preSharedKey = password;

                Class internalConnectivityManagerClass = Class.forName("android.net.IConnectivityManager");
                ResultReceiver dummyResultReceiver = new ResultReceiver(null);
                try {

                    WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);
                    Method mMethod = wifiManager.getClass().getMethod("setWifiApConfiguration", WifiConfiguration.class);
                    mMethod.invoke(wifiManager, apConfig);
                    Method startTetheringMethod = internalConnectivityManagerClass.getDeclaredMethod("startTethering",
                            int.class,
                            ResultReceiver.class,
                            boolean.class);

                    startTetheringMethod.invoke(internalConnectivityManagerClass,
                            0,
                            dummyResultReceiver,
                            true);
                } catch (NoSuchMethodException e) {
//                    Method startTetheringMethod = internalConnectivityManagerClass.getDeclaredMethod("startTethering",
//                            int.class,
//                            ResultReceiver.class,
//                            boolean.class,
//                            String.class);
//
//                    startTetheringMethod.invoke(internalConnectivityManagerClass,
//                            0,
//                            dummyResultReceiver,
//                            false,
//                            context.getPackageName());
                } catch (InvocationTargetException e) {

                    e.printStackTrace();
                } finally {
//                    log.setText(sb.toString());
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void openWifiHotSpot(String ssid, String password) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            openWiFiAP26(ssid, password);
        } else {
            openWiFiAP24(ssid, password);
        }
    }

    public static String getWifiApIpAddress(Context context) {
        if (context == null) return null;
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        DhcpInfo info = wifiManager.getDhcpInfo();
        Log.i("WIFIHotSpot", "本机IP: " + info.ipAddress);
        Log.i("WIFIHotSpot", "服务端IP: " + WifiClient.intIP2StringIP(info.serverAddress));
        return WifiClient.intIP2StringIP(info.serverAddress);
    }

    /**
     * 关闭WiFi热点
     */
    public void closeWifiHotspot() {
        try {
            Method method = mWifManager.getClass().getMethod("getWifiApConfiguration");
            method.setAccessible(true);
            WifiConfiguration config = (WifiConfiguration) method.invoke(mWifManager);
            Method method2 = mWifManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            method2.invoke(mWifManager, config, false);
        } catch (Exception e) {
        }
    }


    public boolean isEditSystem() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 判断是否有WRITE_SETTINGS权限if(!Settings.System.canWrite(this))
            if (!Settings.System.canWrite(context)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS,
                        Uri.parse("package:" + context.getPackageName()));
//                startActivityForResult(intent, 1);
                context.startActivity(intent);
                return false;
            } else {
                //创建热点
                return true;
            }
        }
        return true;
    }

}
