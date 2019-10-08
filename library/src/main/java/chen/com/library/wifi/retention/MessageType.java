package chen.com.library.wifi.retention;

import androidx.annotation.IntDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
@IntDef({MessageType.STRING, MessageType.FILE, MessageType.EXIT})
@Retention(RetentionPolicy.SOURCE)
public @interface MessageType {
    int FILE = 1, STRING = 0, EXIT = -1;
}
