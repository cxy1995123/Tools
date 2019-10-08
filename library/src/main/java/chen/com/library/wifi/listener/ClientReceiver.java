package chen.com.library.wifi.listener;


import chen.com.library.wifi.entity.Message;

public interface ClientReceiver {

    void onSendMessage(Message message);

    void onReceiver(Message message);

    void onError(Exception e);

    void onConnected();

    void onDisconnect();

}
