package chen.com.library.window;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.PixelFormat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import chen.com.library.view.RootFrameLayout;

import static android.view.View.SYSTEM_UI_FLAG_FULLSCREEN;
import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
import static android.view.WindowManager.LayoutParams.FLAG_DIM_BEHIND;
import static android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN;
import static android.view.WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR;
import static android.view.WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
import static android.view.WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;

public abstract class BasePopupView extends SimpleAnimatorListener
        implements RootFrameLayout.onBackPressed, View.OnTouchListener {

    private boolean isFullScreen;
    private boolean outsideTouchable;
    private Context context;
    private View contentView;
    private boolean attached = false;
    private WindowManager wm;
    private int animDuration = 200;
    private View decorView;

    public BasePopupView(Fragment fragment) {
        this(fragment.getContext());
    }

    protected abstract int onContentLayout();

    public BasePopupView(@Nullable Context context) {
        if (context == null) {
            throw new NullPointerException("context Can not be empty");
        }
        this.context = context;
        wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        View view = LayoutInflater.from(context).inflate(onContentLayout(), null, false);
        if (view instanceof ViewGroup) {
            if (view instanceof RootFrameLayout) {
                ((RootFrameLayout) view).setOnBackPressed(this);
            }
        } else {
            RootFrameLayout rootFrameLayout = new RootFrameLayout(context);
            rootFrameLayout.addView(view);
            view = rootFrameLayout;
            rootFrameLayout.setOnBackPressed(this);
        }
        this.contentView = view;
    }

    protected void setOutsideTouchable(boolean outsideTouchable) {
        this.outsideTouchable = outsideTouchable;
    }

    protected void setFullScreen(boolean fullScreen) {
        isFullScreen = fullScreen;
    }

    public Context getContext() {
        return context;
    }

    public void setAnimDuration(int animDuration) {
        this.animDuration = animDuration;
    }

    public View getContentView() {
        return contentView;
    }

    private AnimatorSet inAnimate;

    private AnimatorSet outAnimate;

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
            params.flags = FLAG_DIM_BEHIND | FLAG_WATCH_OUTSIDE_TOUCH;
            params.width = WindowManager.LayoutParams.WRAP_CONTENT;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            if (outsideTouchable) {
                contentView.setOnTouchListener(this);
            }
        }
    }

    protected AnimatorSet buildOutAnim() {
        AnimatorSet set = new AnimatorSet();
        ObjectAnimator animate = ObjectAnimator.ofFloat(contentView, View.ALPHA, 1, 0);
        ObjectAnimator animate2 = ObjectAnimator.ofFloat(contentView, View.SCALE_Y, 1, 0);
        ObjectAnimator animate3 = ObjectAnimator.ofFloat(contentView, View.SCALE_X, 1, 0);
        set.setDuration(animDuration);
        set.addListener(this);
        set.play(animate).with(animate2).with(animate3);
        return set;
    }

    protected AnimatorSet buildIntAnim() {
        AnimatorSet set = new AnimatorSet();
        ObjectAnimator animate = ObjectAnimator.ofFloat(contentView, View.ALPHA, 0, 1);
        ObjectAnimator animate2 = ObjectAnimator.ofFloat(contentView, View.SCALE_Y, 0, 1);
        ObjectAnimator animate3 = ObjectAnimator.ofFloat(contentView, View.SCALE_X, 0, 1);
        set.setDuration(animDuration);
        set.addListener(new SimpleAnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                attached = true;
            }
        });
        set.play(animate).with(animate2).with(animate3);
        return set;
    }



    public void show(final View view) {
        if (wm == null || view == null) return;
        decorView = view;
        decorView.addOnAttachStateChangeListener(listener);
        if (view.isAttachedToWindow()) {
            invokePopup(view);
        }

    }

    private void invokePopup(View view) {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.token = view.getWindowToken();
        params.format = PixelFormat.TRANSLUCENT;
        setWindowSize(params, isFullScreen);
        params.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE;
        params.dimAmount = 0.4f;
        params.type = WindowManager.LayoutParams.TYPE_APPLICATION_PANEL;
        wm.addView(contentView, params);
        if (inAnimate == null && !attached) {
            inAnimate = buildIntAnim();
            if (inAnimate != null) {
                inAnimate.start();
            } else {
                attached = true;
            }
        }
    }

    private void invokeDismiss() {
        if (attached && wm != null) {
            attached = false;
            context = null;
            wm.removeViewImmediate(contentView);
            wm = null;
        }
        if (decorView != null) {
            decorView.removeOnAttachStateChangeListener(listener);
            listener = null;
            decorView = null;
        }
    }

    public void dismiss() {
        if (inAnimate != null) {
            inAnimate.cancel();
            inAnimate = null;
        }
        if (attached && contentView != null && outAnimate == null) {
            outAnimate = buildOutAnim();
            if (outAnimate != null) {
                outAnimate.start();
            } else {
                invokeDismiss();
            }
        }
    }

    @Override
    public void onAnimationEnd(Animator animation) {
        invokeDismiss();
    }


    private View.OnAttachStateChangeListener listener = new View.OnAttachStateChangeListener() {
        @Override
        public void onViewAttachedToWindow(View v) {
            if (!attached) {
                invokePopup(v);
            }
        }

        @Override
        public void onViewDetachedFromWindow(View v) {
            if (attached){
                invokeDismiss();
            }
        }
    };


    @Override
    public boolean onBack() {
        dismiss();
        return true;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            int height = contentView.getMeasuredHeight() + 30;
            int width = contentView.getMeasuredWidth() + 30;
            float x = event.getX();
            float y = event.getY();
            if (x < 0 || y < 0) {
                if (attached) {
                    dismiss();
                }
                return true;
            }
            if (x > width || y > height) {
                if (attached) {
                    dismiss();
                }
                return true;
            }
        }
        return false;
    }
}
