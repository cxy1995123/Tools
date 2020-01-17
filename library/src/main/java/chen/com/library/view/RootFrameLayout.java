package chen.com.library.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class RootFrameLayout extends FrameLayout {

    public RootFrameLayout(@NonNull Context context) {
        super(context);
    }

    public RootFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RootFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private onBackPressed onBackPressed;

    public interface onBackPressed {
        boolean onBack();
    }

    public void setOnBackPressed(RootFrameLayout.onBackPressed onBackPressed) {
        this.onBackPressed = onBackPressed;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            if (onBackPressed != null) {
                return onBackPressed.onBack();
            }
        }
        return super.dispatchKeyEvent(event);
    }
}
