package chen.com.library.wifi.client;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.util.List;

import chen.com.library.wifi.WIFIHotSpot;
import chen.com.library.wifi.listener.WiFiConnectionChangeListener;
import chen.com.library.wifi.listener.WiFiScanResultListener;
import chen.com.library.wifi.listener.WiFiStateChangeListener;
import chen.com.library.wifi.receiver.WIFIScanResultReceiver;
import chen.com.library.wifi.receiver.WifiChangeBroadcastReceiver;

import static android.content.Context.WIFI_SERVICE;
import static android.net.wifi.WifiConfiguration.Status.ENABLED;

/**
 * wifi 所需要的权限
 * <p>
 * <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
 * <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
 * <uses-permission android:name="android.permission.CHANGE_NETWORK_STATE" /> <!-- 允许应用程序改变WIFI连接状态 -->
 * <uses-permission android:name="android.permission.CHANGE_WIFI_STATE" /> <!-- 允许应用程序访问有关的网络信息 -->
 * <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" /> <!-- 允许应用程序访问WIFI网卡的网络信息 -->
 * <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" /> <!-- 允许应用程序完全使用网络 -->
 */

public class WifiClient {

    private static final String TAG = "WifiClient";

    private WifiManager manager;

    private WifiChangeBroadcastReceiver receiver;

    private WIFIScanResultReceiver resultReceiver;

    private Context context;

    public WifiClient(Context context) {
        if (context == null) return;
        this.context = context;
        manager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        receiver = WifiChangeBroadcastReceiver.registerReceiver(context);
        resultReceiver = WIFIScanResultReceiver.bind(context, manager);
    }

    public void setWiFiConnectionChangeListener(WiFiConnectionChangeListener listener) {
        receiver.setWiFiConnectionChangeListener(listener);
    }

    public void setWiFiStateChangeListener(WiFiStateChangeListener listener) {
        receiver.setWiFiStateChangeListener(listener);
    }

    public void setWiFiScanResultListener(WiFiScanResultListener listener) {
        resultReceiver.setListener(listener);
    }

    public void startScan() {
        if (!manager.isWifiEnabled()) {
            manager.setWifiEnabled(true);
        }
        boolean startScan = manager.startScan();
    }


    /**
     * 此方法肯能存在问题
     */
    @Deprecated
    public WIFIHotSpot openHotSpot() {
        WIFIHotSpot spot = new WIFIHotSpot(manager, context);
        spot.openWifiHotSpot("ABCDEFG123456", "123456789");
        return spot;
    }

    /**
     * 连接wifi
     */
    public void addNetWork(String SSID, String PASSW) {

        int netId = -1;
        if (removeWifi(SSID)) {
            //移除,新建config
            netId = manager.addNetwork(createWifiInfo(SSID, PASSW));
        } else {
            WifiConfiguration wifiConfig = getExitsWifiConfig(SSID);
            if (wifiConfig != null) {
                //这个wifi是连接过
                netId = wifiConfig.networkId;
            } else {
                //没连接过的，新建一个wifi配置
                netId = manager.addNetwork(createWifiInfo(SSID, PASSW));
            }
        }
        Log.i(TAG, "addNetWork: " + netId);
        // 返回ture 表示正在执行连接操作，不代表连接成功
        boolean b = manager.enableNetwork(netId, true);
    }

    /**
     * 存在过的wifiConfiguration
     */
    private WifiConfiguration getExitsWifiConfig(String SSID) {
        List<WifiConfiguration> wifiConfigurationList = manager.getConfiguredNetworks();
        for (WifiConfiguration wifiConfiguration : wifiConfigurationList) {
            if (wifiConfiguration.SSID.equals("\"" + SSID + "\"")) {
                return wifiConfiguration;
            }
        }
        return null;
    }

    /**
     * 移除在mWifiManager移除 里面的wifi
     */
    private boolean removeWifi(int netId) {
        return manager.removeNetwork(netId);
    }

    /**
     * config里存在； 在mWifiManager移除；
     */
    private boolean removeWifi(String SSID) {
        if (getExitsWifiConfig(SSID) != null) {
            return removeWifi(getExitsWifiConfig(SSID).networkId);
        } else {
            return false;
        }
    }

    /**
     * 创建wifi连接配置
     *
     * @param SSID
     * @param password
     * @return
     */
    private WifiConfiguration createWifiInfo(String SSID, String password) {
        WifiConfiguration config = new WifiConfiguration();

        if (config != null) {
            config.allowedAuthAlgorithms.clear();
            config.allowedGroupCiphers.clear();
            config.allowedKeyManagement.clear();
            config.allowedPairwiseCiphers.clear();
            config.allowedProtocols.clear();
            config.SSID = "\"" + SSID + "\"";
            //如果有相同配置的，就先删除
            WifiConfiguration tempConfig = getExitsWifiConfig(SSID);
            if (tempConfig != null) {
                manager.removeNetwork(tempConfig.networkId);
                manager.saveConfiguration();
            }
            config.preSharedKey = "\"" + password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            //config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = ENABLED;
            Log.i(TAG, "createWifiInfo: " + config.SSID);
            return config;
        } else {
            Log.i(TAG, "createWifiInfo: 创建失败");
            return null;
        }

    }

    /**
     * 将得到的int类型的IP转换为String类型
     *
     * @param ip
     */
    public static String intIP2StringIP(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                (ip >> 24 & 0xFF);
    }

    /**
     * 获取当前连接的wifi
     */
    public static WifiInfo getConnectionWifi(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(WIFI_SERVICE);
        if (wifiManager != null) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();    //当前wifi连接信息
            if (wifiInfo == null) return null;
            Log.i(TAG, "getIpAddress: " + wifiInfo.getIpAddress());
            Log.i(TAG, "getNetworkId: " + wifiInfo.getNetworkId());
            Log.i(TAG, "getBSSID: " + wifiInfo.getBSSID());
            Log.i(TAG, "getSSID: " + wifiInfo.getSSID());
            Log.i(TAG, "getMacAddress: " + wifiInfo.getMacAddress());
            Log.i(TAG, "intIP2StringIP: " + intIP2StringIP(wifiInfo.getIpAddress()));
            return wifiInfo;
        }
        return null;
    }

    public void onDestroy() {
        receiver.unRegisterReceiver(context);
        resultReceiver.unRegisterReceiver(context);
    }


}
