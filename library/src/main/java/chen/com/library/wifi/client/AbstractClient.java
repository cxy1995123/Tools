package chen.com.library.wifi.client;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import java.io.IOException;
import java.net.Socket;

import chen.com.library.wifi.ThreadPool;
import chen.com.library.wifi.entity.Message;
import chen.com.library.wifi.handler.MessageHandler;
import chen.com.library.wifi.listener.ClientReceiver;

public abstract class AbstractClient extends MessageHandler implements Runnable {

    private ClientReceiver receiver;

    //<editor-fold desc=属性”>
    private String serviceIP;
    private Socket socket;
    private Context context;
    //</editor-fold>

    protected Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void sendMessage(final Message message) {
        if (receiver != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    receiver.onSendMessage(message);
                }
            });
        }
    }

    @Override
    protected void receiveMessage(final Message message) {
        if (receiver != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    receiver.onReceiver(message);
                }
            });
        }
    }

    @Override
    protected void onError(final Exception e) {
        if (receiver != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    receiver.onError(e);
                }
            });
        }
    }

    @Override
    protected void onConnected() {
        if (receiver != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    receiver.onConnected();
                }
            });
        }
    }

    @Override
    protected void onDisconnect() {
        if (receiver != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    receiver.onConnected();
                }
            });
        }
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public void setReceiver(ClientReceiver receiver) {
        this.receiver = receiver;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setServiceIP(String serviceIP) {
        this.serviceIP = serviceIP;
    }

    public ClientReceiver getReceiver() {
        return receiver;
    }

    public Socket getSocket() {
        return socket;
    }

    public Handler getHandler() {
        return handler;
    }

    public Context getContext() {
        return context;
    }

    public String getServiceIP() {
        return serviceIP;
    }

    @Override
    public boolean isConnect() {
        return socket != null && (socket.isConnected() && (getInputStream() != null && getOutputStream() != null));
    }

    /**
     * 启动Socket线程
     */
    public void start() {
        ThreadPool.execute(this);
    }

    @Override
    public void run() {
        initSocket();
    }

    protected abstract void initSocket();

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
