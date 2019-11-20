package chen.com.library.window;

import android.animation.Animator;
import android.content.Context;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.WindowManager;

import androidx.annotation.LayoutRes;

import chen.com.library.view.RootFrameLayout;

import static android.view.View.SYSTEM_UI_FLAG_FULLSCREEN;
import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
import static android.view.WindowManager.LayoutParams.FLAG_DIM_BEHIND;
import static android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN;
import static android.view.WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR;
import static android.view.WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;

public class CustomizeBaseWindow extends SimpleAnimatorListener implements RootFrameLayout.onBackPressed {

    private boolean isFullScreen;
    private Context context;
    private View contentView;
    private boolean attached = false;
    WindowManager wm;

    public CustomizeBaseWindow(Context context, @LayoutRes int res) {
        this(context, View.inflate(context, res, null));
    }

    public CustomizeBaseWindow(Context context, View view) {
        this.context = context;
        wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        RootFrameLayout rootFrameLayout = new RootFrameLayout(context);
        rootFrameLayout.addView(view);
        view = rootFrameLayout;
        rootFrameLayout.setOnBackPressed(this);
        this.contentView = view;
        contentView.setAlpha(0);
        contentView.setScaleX(0);
        contentView.setScaleY(0);
    }

    private ViewPropertyAnimator inAnimate;

    private ViewPropertyAnimator outAnim;

    private void setWindowSize(WindowManager.LayoutParams params, boolean isFullScreen) {
        if (isFullScreen) {
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.MATCH_PARENT;
            params.systemUiVisibility =
                    SYSTEM_UI_FLAG_FULLSCREEN |
                            SYSTEM_UI_FLAG_LAYOUT_STABLE |
                            SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            params.flags = FLAG_DIM_BEHIND
                    | FLAG_LAYOUT_IN_SCREEN
                    | FLAG_LAYOUT_INSET_DECOR
                    | FLAG_FULLSCREEN;
        } else {
            params.width = WindowManager.LayoutParams.WRAP_CONTENT;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            params.flags = FLAG_DIM_BEHIND;
        }
    }

    public void dismiss() {
        if (inAnimate != null) {
            inAnimate.cancel();
        }
        if (attached && contentView != null && outAnim == null) {
            outAnim = contentView.animate();
            outAnim.setDuration(300).scaleX(0).scaleY(0)
                    .alpha(0).setListener(this).start();
        }
    }

    public void show() {
        if (wm == null) return;
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.format = PixelFormat.TRANSLUCENT;
        setWindowSize(params, isFullScreen);
        params.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE;
        params.dimAmount = 0.4f;
        params.type = WindowManager.LayoutParams.TYPE_APPLICATION;
        wm.addView(contentView, params);
        attached = true;
        if (inAnimate == null) {
            contentView.post(new Runnable() {
                @Override
                public void run() {
                    if (contentView == null) return;
                    inAnimate = contentView.animate();
                    inAnimate.alpha(1)
                            .setStartDelay(50)
                            .scaleX(1)
                            .scaleY(1)
                            .setDuration(300)
                            .start();
                }
            });
        }
    }

    @Override
    public void onAnimationEnd(Animator animation) {
        if (attached && wm != null) {
            attached = false;
            context = null;
            wm.removeViewImmediate(contentView);
            wm = null;
        }
    }

    @Override
    public boolean onBack() {
        dismiss();
        return true;
    }
}
