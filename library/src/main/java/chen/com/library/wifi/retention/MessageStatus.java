package chen.com.library.wifi.retention;


import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


@IntDef({MessageStatus.SUCCESS, MessageStatus.FAIL, MessageStatus.SENDING, MessageStatus.UNSEND})
@Retention(RetentionPolicy.SOURCE)
public @interface MessageStatus {
    int SUCCESS = 1;
    int FAIL = 2;
    int SENDING = 3;
    int UNSEND = 0;
}
