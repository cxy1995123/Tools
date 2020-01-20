package chen.com.library.view;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.OverScroller;

import androidx.annotation.Nullable;

public class TestScrollView extends FrameLayout {

    private GestureDetector gestureDetector;
    private OverScroller scroller;
    private int widthPixels;
    private LinearLayout content;

    public TestScrollView(Context context) {
        this(context, null);
    }

    public TestScrollView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TestScrollView(Context context, @Nullable AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
//        content = new LinearLayout(getContext());
//        content.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//        content.setOrientation(LinearLayout.HORIZONTAL);
//        addView(content);
        widthPixels = getResources().getDisplayMetrics().widthPixels;
        AccelerateDecelerateInterpolator interpolator = new AccelerateDecelerateInterpolator();
        scroller = new OverScroller(context, interpolator);
        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if (getScrollX() == 0) {
                    if (distanceX < 0) {
                        distanceX = 0;
                    }
                }

                if (getScrollX() >= getWidth() - widthPixels) {
                    if (distanceX > 0) {
                        distanceX = 0;
                    }
                }
                scrollBy(floatToInt(distanceX), 0);
                return false;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, final float velocityX, float velocityY) {
                scroller.fling(getScrollX(), 0, (int) (-velocityX / 1.5), 0, 0, getWidth() - widthPixels, 0, 0);
                invalidate();
                return false;
            }
        });
        setLongClickable(true);
    }

    private int floatToInt(float value) {
        return (int) (value + 0.5f);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int childWidth = 0;
        int childHeight = 0;
        if (getChildCount() > 0) {
            childWidth = getChildAt(0).getMeasuredWidth();
            childHeight = getChildAt(0).getMeasuredHeight();
            getChildAt(0).layout(0, 0, childWidth, childHeight);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int mPaddingLeft = getPaddingLeft();
        int mPaddingRight = getPaddingRight();
        int mPaddingBottom = getPaddingBottom();
        int mPaddingTop = getPaddingTop();

        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        if (widthMode == MeasureSpec.UNSPECIFIED) {
            return;
        }

        if (getChildCount() > 0) {
            final View child = getChildAt(0);
            final int widthPadding;
            final int heightPadding;
            final FrameLayout.LayoutParams lp = (LayoutParams) child.getLayoutParams();
            final int targetSdkVersion = getContext().getApplicationInfo().targetSdkVersion;
            if (targetSdkVersion >= Build.VERSION_CODES.M) {
                widthPadding = mPaddingLeft + mPaddingRight + lp.leftMargin + lp.rightMargin;
                heightPadding = mPaddingTop + mPaddingBottom + lp.topMargin + lp.bottomMargin;
            } else {
                widthPadding = mPaddingLeft + mPaddingRight;
                heightPadding = mPaddingTop + mPaddingBottom;
            }

            int desiredWidth = getMeasuredWidth() - widthPadding;
            if (child.getMeasuredWidth() < desiredWidth) {
                final int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(
                        desiredWidth, MeasureSpec.EXACTLY);
                final int childHeightMeasureSpec = getChildMeasureSpec(
                        heightMeasureSpec, heightPadding, lp.height);
                child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return true;
    }

    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()) {
            scrollTo(scroller.getCurrX(), scroller.getCurrY());
            postInvalidate();
        }
        super.computeScroll();
    }
}
