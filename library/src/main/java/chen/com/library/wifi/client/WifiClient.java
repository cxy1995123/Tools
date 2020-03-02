package chen.com.library.wifi.client;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;

import java.util.List;


import chen.com.library.tools.ArrayUtil;
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

    private static WifiClient client;

    public static WifiClient getInstance(Context context) {
        if (client == null) {
            synchronized (WifiClient.class) {
                if (client == null) {
                    client = new WifiClient(context);
                }
            }
        }
        return client;
    }

    private WifiClient(Context context) {
        if (context == null) return;
        this.context = context;
        manager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    }

    public void registerReceiver() {
        receiver = WifiChangeBroadcastReceiver.registerReceiver(context);
        resultReceiver = WIFIScanResultReceiver.bind(context, manager);
    }

    /**
     * 断开当前连接的wifi
     */
    public boolean disconnect() {
        try {
            return manager.disconnect();
        } catch (Exception ignored) {
        }
        return false;
    }

    /**
     * wifi 连接状态监听
     */
    public void setWiFiConnectionChangeListener(WiFiConnectionChangeListener listener) {
        receiver.setWiFiConnectionChangeListener(listener);
    }

    /**
     * wifi 开关状态监听
     */
    public void setWiFiStateChangeListener(WiFiStateChangeListener listener) {
        receiver.setWiFiStateChangeListener(listener);
    }

    /**
     * wifi 扫描状态监听
     */
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
     * 连接wifi
     */
    public void addNetWork(String SSID, String PASSW) {

        int netId = -1;
        if (removeWifi(SSID)) {
            //移除,新建config
            netId = manager.addNetwork(createWifiInfo(SSID, PASSW));
        } else {
            WifiConfiguration wifiConfig = getExitsWifiConfigForSSID(SSID);
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
     *
     * @param SSID 中存在转义符等
     */
    private WifiConfiguration getExitsWifiConfigForSSID(String SSID) {
        if (TextUtils.isEmpty(SSID)) return null;
        List<WifiConfiguration> wifiConfigurationList = manager.getConfiguredNetworks();
        if (ArrayUtil.isEmpty(wifiConfigurationList)) return null;

        for (WifiConfiguration wifiConfiguration : wifiConfigurationList) {
            if (wifiConfiguration.SSID.equals("\"" + SSID + "\"")) {
                return wifiConfiguration;
            } else if (wifiConfiguration.SSID.equals(SSID)) {
                return wifiConfiguration;
            }
        }
        return null;
    }


    public WifiConfiguration getCurrentWifiConfig() {
        if (context == null || manager == null) return null;
        WifiInfo wifiInfo = getConnectionWifi(context);
        if (wifiInfo != null) {
            return getExitsWifiConfigForSSID(wifiInfo.getSSID());
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
        WifiConfiguration exitsWifiConfig = getExitsWifiConfigForSSID(SSID);
        if (exitsWifiConfig != null) {
            return removeWifi(exitsWifiConfig.networkId);
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
            WifiConfiguration tempConfig = getExitsWifiConfigForSSID(SSID);
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

    public void openWifi() {
        manager.setWifiEnabled(false);
    }

    public void closeWifi() {
        manager.setWifiEnabled(false);
    }


    public boolean connectionWifi(WifiConfiguration wifiConfiguration) {
        if (wifiConfiguration == null) return false;
        int netId = wifiConfiguration.networkId;
        return manager.enableNetwork(netId, true);
    }


    private WifiConfiguration oldWifi;

    public void saveWifiStatus() {
        try {
            if (manager == null) return;
            WifiInfo wifiInfo = manager.getConnectionInfo();
            if (wifiInfo != null) {
                oldWifi = getExitsWifiConfigForSSID(wifiInfo.getSSID());
            }
        } catch (Exception ignored) {

        }

    }

    public void reConnection() {
        connectionWifi(oldWifi);
    }

    public void onDestroy() {
        if (receiver != null) {
            receiver.unRegisterReceiver(context);
        }
        if (resultReceiver != null) {
            resultReceiver.unRegisterReceiver(context);
        }

        client = null;
    }


}
