package chen.com.library.wifi.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import java.util.List;
import java.util.Objects;

import chen.com.library.wifi.listener.WiFiScanResultListener;

public class WIFIScanResultReceiver extends BroadcastReceiver {

    private WifiManager manager;

    private WiFiScanResultListener listener;

    public void setListener(WiFiScanResultListener listener) {
        this.listener = listener;
    }

    public static WIFIScanResultReceiver bind(Context context, WifiManager manager) {
        WIFIScanResultReceiver resultReceiver = new WIFIScanResultReceiver(manager);
        IntentFilter filter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        context.registerReceiver(resultReceiver, filter);
        return resultReceiver;
    }

    public WIFIScanResultReceiver(WifiManager manager) {
        this.manager = manager;
    }

    public void unRegisterReceiver(Context context) {
        context.unregisterReceiver(this);
        listener = null;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Objects.equals(intent.getAction(), WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
            List<ScanResult> results = manager.getScanResults();
            if (listener != null) {
                listener.onScanResult(results);
            }
        }
    }
}
