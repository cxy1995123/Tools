package chen.com.library.wifi.client;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import chen.com.library.wifi.ThreadPool;

public class HotSpotService extends AbstractClient {

    private ServerSocket serverSocket;

    public HotSpotService() {

    }

    public void start() {
        ThreadPool.execute(this);
    }

    @Override
    protected void initSocket() {
        try {
            serverSocket = new ServerSocket(8888);
            Socket socket = serverSocket.accept();
            setSocket(socket);
            start(socket);
            onConnected();
        } catch (IOException e) {
            onError(e);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (serverSocket!=null){
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
