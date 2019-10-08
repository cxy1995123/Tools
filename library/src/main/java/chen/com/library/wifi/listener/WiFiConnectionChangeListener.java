package chen.com.library.wifi.listener;

public interface WiFiConnectionChangeListener {

    void onScanning();

    void onConnecting();

    void onAuthenticating();

    /**
     * 认证wifi
     */
    void onObtainingIPAddr();

    void onDisconnected();

    void onFailed();

    /**
     * 禁止访问网络
     */
    void onBlocked();

}
