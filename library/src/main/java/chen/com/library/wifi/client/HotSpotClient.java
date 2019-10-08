package chen.com.library.wifi.client;

import android.content.Context;
import java.io.IOException;
import java.net.Socket;
import chen.com.library.wifi.WIFIHotSpot;

public class HotSpotClient extends AbstractClient {

    public HotSpotClient(Context context) {
        setContext(context);
        setServiceIP(WIFIHotSpot.getWifiApIpAddress(getContext()));
    }

    /**
     * 连接socket
     * 启动消息监听线程
     */


    @Override
    protected void initSocket() {
        try {
            if (getServiceIP() == null) return;
            Socket socket = new Socket(getServiceIP(), 8888);
            setSocket(socket);
            start(socket);
            onConnected();
        } catch (IOException e) {
            onError(e);
        }
    }

    //</editor-fold>


}
