package chen.com.library.bluetooth;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.os.Build;
import androidx.annotation.RequiresApi;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class CallBack21 extends ScanCallback {

    @Override
    public void onScanResult(int callbackType, ScanResult result) {
        super.onScanResult(callbackType, result);
    }

    @Override
    public void onBatchScanResults(List<ScanResult> results) {
        super.onBatchScanResults(results);
    }

    @Override
    public void onScanFailed(int errorCode) {
        super.onScanFailed(errorCode);
    }

    // return name String(successful) or null(failed)
    public static String parseDeviceName(ScanRecord record) {

        if (record == null) return null;

        byte[] scanRecord = record.getBytes();

        String ret = null;
        if (null == scanRecord) {
            return ret;
        }

        ByteBuffer buffer = ByteBuffer.wrap(scanRecord).order(ByteOrder.LITTLE_ENDIAN);
        while (buffer.remaining() > 2) {
            byte length = buffer.get();
            if (length == 0)
                break;

            byte type = buffer.get();
            length -= 1;
            switch (type) {
                case 0x01: // Flags
                    buffer.get(); // flags
                    length--;
                    break;
                case 0x02: // Partial list of 16-bit UUIDs
                case 0x03: // Complete list of 16-bit UUIDs
                case 0x14: // List of 16-bit Service Solicitation UUIDs
                    while (length >= 2) {
                        buffer.getShort();
                        length -= 2;
                    }
                    break;
                case 0x04: // Partial list of 32 bit service UUIDs
                case 0x05: // Complete list of 32 bit service UUIDs
                    while (length >= 4) {
                        buffer.getInt();
                        length -= 4;
                    }
                    break;
                case 0x06: // Partial list of 128-bit UUIDs
                case 0x07: // Complete list of 128-bit UUIDs
                case 0x15: // List of 128-bit Service Solicitation UUIDs
                    while (length >= 16) {
                        long lsb = buffer.getLong();
                        long msb = buffer.getLong();
                        length -= 16;
                    }
                    break;
                case 0x08: // Short local device name
                case 0x09: // Complete local device name
                    byte sb[] = new byte[length];
                    buffer.get(sb, 0, length);
                    length = 0;
                    ret = new String(sb).trim();
                    return ret;
                case (byte) 0xFF: // Manufacturer Specific Data
                    buffer.getShort();
                    length -= 2;
                    break;
                default: // skip
                    break;
            }
            if (length > 0) {
                buffer.position(buffer.position() + length);
            }
        }
        return ret;
    }

}
