package android.woniu.com.eventbuse.annotation;
import androidx.annotation.IntDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static android.woniu.com.eventbuse.annotation.ThreadMode.BackGround;
import static android.woniu.com.eventbuse.annotation.ThreadMode.MinThread;

@IntDef({MinThread, BackGround})
@Retention(RetentionPolicy.RUNTIME)
public @interface ThreadMode {
    int MinThread = 0;
    int BackGround = 1;
}
