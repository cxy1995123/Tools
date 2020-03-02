package chen.com.tools;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.io.InputStream;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class TestNetConnectImpl {

    private boolean isConnected = true;

    private ExecutorService service = Executors.newFixedThreadPool(1);

    private Vector<OnNetWorkChangeListener> listeners = new Vector<>();


    public void addListener(OnNetWorkChangeListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public boolean removeListener(OnNetWorkChangeListener listener) {
        return listeners.remove(listener);
    }

    public void  release(){
        stop();
        listeners.clear();
    }


    interface OnNetWorkChangeListener {
        void onChange(boolean isConnected);
    }

    private Handler handler = new Handler(Looper.myLooper(), new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 1) {
                ping();
            }
            return false;
        }
    });

    private static TestNetConnectImpl db;

    public static final String DO_MAIN = "www.2woniu.cn";

    static {
        db = new TestNetConnectImpl();
    }

    private TestNetConnectImpl() {
        start();
    }

    public static TestNetConnectImpl getInstance() {
        return db;
    }

    public void start() {
        handler.removeCallbacksAndMessages(null);
        handler.sendEmptyMessageDelayed(1, 0);
    }

    public void stop() {
        handler.removeCallbacksAndMessages(null);
    }

    public synchronized void onChange() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                for (OnNetWorkChangeListener listener : listeners) {
                    listener.onChange(isConnected);
                }
            }
        });
    }

    public void ping() {
        service.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Process p;
                    p = Runtime.getRuntime().exec("ping -c 1 -w 5 " + DO_MAIN);
                    InputStream input = p.getInputStream();
                    int read = input.read();
                    boolean b = read != -1;

                    if (b != isConnected) {
                        isConnected = b;
                        onChange();
                    }

                    handler.sendEmptyMessageDelayed(1, 1000 * 10);
                } catch (Exception e) {
                    handler.sendEmptyMessageDelayed(1, 1000 * 10);
                    if (isConnected) {
                        isConnected = false;
                        onChange();
                    }
                }
            }
        });
    }
}
