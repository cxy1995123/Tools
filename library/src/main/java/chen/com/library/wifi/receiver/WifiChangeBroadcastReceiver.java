package chen.com.library.wifi.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Parcelable;
import android.util.Log;

import chen.com.library.wifi.listener.WiFiConnectionChangeListener;
import chen.com.library.wifi.listener.WiFiStateChangeListener;

public class WifiChangeBroadcastReceiver extends BroadcastReceiver {

    public static String TAG = "WIFI_CHANGE";

    private int WIFI_POWER_TYPE = -1;
    private NetworkInfo.DetailedState detailedState;

    private WiFiStateChangeListener wiFiStateChangeListener;
    private WiFiConnectionChangeListener wiFiConnectionChangeListener;

    public static WifiChangeBroadcastReceiver registerReceiver(Context context) {
        WifiChangeBroadcastReceiver receiver = new WifiChangeBroadcastReceiver();
        IntentFilter mFilter = new IntentFilter();
//        mFilter.addAction(WifiManager.RSSI_CHANGED_ACTION); //信号强度变化
        mFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION); //网络状态变化
        mFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION); //wifi状态，是否连上，密码
//        mFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION); //是不是正在获得IP地址
//        mFilter.addAction(WifiManager.NETWORK_IDS_CHANGED_ACTION);
//        mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);//连上与否
        context.registerReceiver(receiver, mFilter);
        return receiver;
    }

    public void setWiFiStateChangeListener(WiFiStateChangeListener listener) {
        wiFiStateChangeListener = listener;
    }

    public void setWiFiConnectionChangeListener(WiFiConnectionChangeListener wiFiConnectionChangeListener) {
        this.wiFiConnectionChangeListener = wiFiConnectionChangeListener;
    }

    public void unRegisterReceiver(Context context) {
        context.unregisterReceiver(this);
        wiFiStateChangeListener = null;
        wiFiConnectionChangeListener = null;
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        // 这个监听wifi的打开与关闭，与wifi的连接无关
        if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {
            int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
            if (wifiState == WIFI_POWER_TYPE) {
                return;
            }
            WIFI_POWER_TYPE = wifiState;
            switch (wifiState) {
                case WifiManager.WIFI_STATE_DISABLED:
                    Log.i(TAG, "onReceive: WIFI已经关闭");
                    if (wiFiStateChangeListener != null) {
                        wiFiStateChangeListener.onDisabled();
                    }
                    break;
                case WifiManager.WIFI_STATE_DISABLING:
                    Log.i(TAG, "onReceive: 正在关闭WIFI");
                    if (wiFiStateChangeListener != null) {
                        wiFiStateChangeListener.onDisabling();
                    }
                    break;
                case WifiManager.WIFI_STATE_ENABLED:
                    Log.i(TAG, "onReceive: WIFI 已经打开");
                    if (wiFiStateChangeListener != null) {
                        wiFiStateChangeListener.onEnabled();
                    }
                    break;
                case WifiManager.WIFI_STATE_ENABLING:
                    Log.i(TAG, "onReceive: 正在打开Wifi");
                    if (wiFiStateChangeListener != null) {
                        wiFiStateChangeListener.onEnabling();
                    }
                    break;
                case WifiManager.WIFI_STATE_UNKNOWN:
                    Log.i(TAG, "onReceive: WIFI_STATE_UNKNOWN");
                    break;
            }
        }
        // 这个监听wifi的连接状态即是否连上了一个有效无线路由，
        // 当上边广播的状态是WifiManager.WIFI_STATE_DISABLING，和WIFI_STATE_DISABLED的时候，根本不会接到这个广播。
        // 在上边广播接到广播是WifiManager.WIFI_STATE_ENABLED状态的同时也会接到这个广播，
        // 当然刚打开wifi肯定还没有连接到有效的无线
        if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
            Parcelable parcelableExtra = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if (null != parcelableExtra) {
                NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
                NetworkInfo.DetailedState state = networkInfo.getDetailedState();
                if (detailedState != null && detailedState == state) {
                    return;
                }
                detailedState = state;
                switch (state) {
                    case IDLE:
                        Log.i(TAG, "onReceive: 准备启动数据连接设置");
                        break;
                    case SCANNING:
                        Log.i(TAG, "onReceive: 正在扫描");

                        if (wiFiConnectionChangeListener != null) {
                            wiFiConnectionChangeListener.onScanning();
                        }

                        break;
                    case CONNECTING:
                        Log.i(TAG, "onReceive: 正在连接");
                        if (wiFiConnectionChangeListener != null) {
                            wiFiConnectionChangeListener.onConnecting();
                        }
                        break;
                    case AUTHENTICATING:
                        Log.i(TAG, "onReceive: 认证");
                        if (wiFiConnectionChangeListener != null) {
                            wiFiConnectionChangeListener.onAuthenticating();
                        }
                        break;
                    case OBTAINING_IPADDR:
                        Log.i(TAG, "onReceive: 获取IP地址");
                        if (wiFiConnectionChangeListener != null) {
                            wiFiConnectionChangeListener.onObtainingIPAddr();
                        }
                        break;
                    case DISCONNECTING:
                        Log.i(TAG, "onReceive: 正在断开数据连接");
                        break;
                    case DISCONNECTED:
                        Log.i(TAG, "onReceive: 已经断开");
                        if (wiFiConnectionChangeListener != null) {
                            wiFiConnectionChangeListener.onDisconnected();
                        }
                        break;
                    case FAILED:
                        Log.i(TAG, "onReceive: 连接失败");
                        if (wiFiConnectionChangeListener != null) {
                            wiFiConnectionChangeListener.onFailed();
                        }
                        break;
                    case BLOCKED:
                        Log.i(TAG, "onReceive: 禁止访问网络");
                        if (wiFiConnectionChangeListener != null) {
                            wiFiConnectionChangeListener.onBlocked();
                        }
                        break;
                }

            }
        }
    }

}
