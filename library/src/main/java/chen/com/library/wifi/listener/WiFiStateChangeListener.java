package chen.com.library.wifi.listener;

public interface WiFiStateChangeListener {

    void onDisabling();

    void onDisabled();

    void onEnabled();

    void onEnabling();

}
