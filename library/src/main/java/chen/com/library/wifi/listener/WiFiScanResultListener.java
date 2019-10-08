package chen.com.library.wifi.listener;

import android.net.wifi.ScanResult;

import java.util.List;

public interface WiFiScanResultListener {

    void onScanResult(List<ScanResult> scanResults);
}
