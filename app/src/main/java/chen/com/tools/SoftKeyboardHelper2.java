package chen.com.tools;

import android.app.Activity;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

public class SoftKeyboardHelper2 {

    private View contentView;
    private int countHeight = -1;
    private ViewGroup.LayoutParams frameLayoutParams;
    private int usableHeightPrevious;
    private OnSoftKeyboardChangeListener keyboardChangeListener;
    private boolean isFullScreen = false;

    public void setFullScreen(boolean fullScreen) {
        isFullScreen = fullScreen;
    }

    public void setKeyboardChangeListener(OnSoftKeyboardChangeListener keyboardChangeListener) {
        this.keyboardChangeListener = keyboardChangeListener;
    }

    interface OnSoftKeyboardChangeListener {
        void onSoftKeyboardChange(boolean isShow, int softKeyBoardHeight);
    }

    public SoftKeyboardHelper2(Activity activity) {
        FrameLayout content = activity.findViewById(android.R.id.content);
        //2､获取到setContentView放进去的View
        contentView = content.getChildAt(0);
        frameLayoutParams = contentView.getLayoutParams();
        contentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (countHeight == -1) {
                    countHeight = contentView.getHeight();
                }
                int usableHeight = computeUsableHeight();
                if (usableHeight != usableHeightPrevious) {
                    if (countHeight - usableHeight > 400) {
                        int softKeyBoardHeight = countHeight - usableHeight;
                        frameLayoutParams.height = countHeight - softKeyBoardHeight;
                        contentView.requestLayout();
                        if (keyboardChangeListener != null) {
                            keyboardChangeListener.onSoftKeyboardChange(true, softKeyBoardHeight);
                        }
                    } else {
                        frameLayoutParams.height = countHeight;
                        contentView.requestLayout();
                        if (keyboardChangeListener != null) {
                            keyboardChangeListener.onSoftKeyboardChange(false, 0);
                        }
                    }
                    usableHeightPrevious = usableHeight;
                }
            }
        });
    }

    private int computeUsableHeight() {
        Rect r = new Rect();
        contentView.getWindowVisibleDisplayFrame(r);
        // 全屏模式下：直接返回r.bottom，r.top其实是状态栏的高度
        StringBuilder builder = new StringBuilder();
        builder.append("top:").append(r.top).append("\n");
        builder.append("count:").append(countHeight).append("\n");
        builder.append("bottom:").append(r.bottom);
        Log.i("SoftKeyboardHelper2", "computeUsableHeight: " + builder);
        if (!isFullScreen) {
            return r.bottom - r.top;
        } else {
            return r.bottom;
        }

    }
}
