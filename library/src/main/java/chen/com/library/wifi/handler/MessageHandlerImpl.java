package chen.com.library.wifi.handler;


import java.net.Socket;

import chen.com.library.wifi.entity.Message;

public interface MessageHandlerImpl {

    void start(Socket socket);

    void writeMessage(Message message);

    boolean isConnect();

}
