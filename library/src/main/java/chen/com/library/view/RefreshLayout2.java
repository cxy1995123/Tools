package chen.com.library.view;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.view.ViewCompat;

import chen.com.library.R;


public class RefreshLayout2 extends ViewGroup {

    protected View titleView;
    protected View contentView;
    protected View footView;

    /**
     * 最低刷新距离
     **/
    protected int min_distance = 0;

    /**
     * 最大拖拽距离
     **/
    protected int max_distance;

    /**
     * 是否启用最大拖拽距离
     **/
    protected boolean enabledMaxDistance = false;

    /**
     * 滑动阻尼系数
     **/
    protected float damp = 2.5f;

    /**
     * 是否开启下拉刷新
     **/
    protected boolean enabled_refresh = true;

    /**
     * 是否开启加载更多
     **/
    protected boolean enabled_load = true;

    /**
     * 是否进行拦截
     **/
    protected boolean isDispatch = false;

    /**
     * 是否进行刷新
     **/
    protected boolean isRefresh = false;

    /**
     * 刷新动画状态
     **/
    protected int refresh_status = NOT_STARTED;

    /**
     * 正在刷新
     */
    protected final static int START_ING = 1;

    /**
     * 停止状态
     */
    protected final static int NOT_STARTED = 2;

    /**
     * 滚动方向
     **/
    protected refresh_type type;

    protected enum refresh_type {
        UP,
        DOWN,
        NULL
    }

    private View titleProgress;
    private View footProgress;
    private View titleArrow;

    public RefreshLayout2(Context context) {
        this(context, null);
    }

    public RefreshLayout2(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefreshLayout2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }


    public void setEnabledMaxDistance(boolean enabledMaxDistance) {
        this.enabledMaxDistance = enabledMaxDistance;
    }

    public void setMax_distance(int max_distance) {
        this.max_distance = max_distance;
    }

    private void initView() {
        LayoutInflater inflater = LayoutInflater.from(getContext());

        View title = inflater.inflate(R.layout.refresh_title, this, true);
        titleProgress = title.findViewById(R.id.titleImage);
        titleArrow = title.findViewById(R.id.arrowIm);

        View foot = inflater.inflate(R.layout.refresh_foot, this, true);
        footProgress = foot.findViewById(R.id.titleImage);
        footProgress.setVisibility(VISIBLE);

        setFocusable(true);
        setClickable(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureChildren(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        titleView = getChildAt(0);
        footView = getChildAt(1);
        contentView = getChildAt(2);
        titleView.layout(0, -titleView.getMeasuredHeight(), getMeasuredWidth(), 0);
        footView.layout(0, getHeight(), getWidth(), getMeasuredHeight() + footView.getMeasuredHeight());
        contentView.layout(0, 0, getMeasuredWidth(), contentView.getMeasuredHeight());
        setMinSlipDistance();
    }

    /**
     * 判断是否下拉到刷新状态
     **/
    protected int isRefreshDown(float offset) {

        if (refresh_status == START_ING) {
            offset = 0;
            isRefresh = false;
            isDispatch = false;
        } else {
            isRefresh = Math.abs(offset) >= min_distance;
            if (isRefresh) {
                setRefreshText("松开刷新");
                animArrow(180, 0);
            } else {
                setRefreshText("下拉刷新");
                animArrow(0, 1);
            }
            isDispatch = true;
        }
        return enabledMaxDistance((int) offset);
    }

    private ObjectAnimator animator;

    private int lastTimeDirection = -1;

    private void animArrow(int p, int direction) {
        if (direction == this.lastTimeDirection) return;
        this.lastTimeDirection = direction;
        if (animator != null && animator.isRunning()) {
            animator.cancel();
        }
        animator = ObjectAnimator.ofFloat(titleArrow, View.ROTATION, titleArrow.getRotation(), p);
        animator.setDuration(300).start();
    }


    /**
     * 判断是否上拉到加载状态
     **/
    protected int isRefreshUp(float offset) {
        if (refresh_status == START_ING) {
            offset = 0;
            isRefresh = false;
            isDispatch = false;
        } else {
            isRefresh = Math.abs(offset) > min_distance;
            if (isRefresh) {
                setLoadMoreText("松手加载");
            } else {
                setLoadMoreText("下拉加载");
            }
            isDispatch = true;
        }
        return enabledMaxDistance((int) offset);
    }

    /**
     * 最大拖拽距离判定
     **/
    protected int enabledMaxDistance(int offset) {

        if (enabledMaxDistance && Math.abs(offset) > max_distance) {

            if (max_distance <= 0) {
                throw new IllegalArgumentException("max_distance must be greater than 0");
            }

            if (offset < 0) {
                offset = -max_distance;
            } else {
                offset = max_distance;
            }
        }
        return offset;

    }

    /**
     * 松手状态判断是否进行加载或者刷新
     **/
    protected void actionUp() {
        //是否需要刷新
        if (isRefresh) {
            smoothScrollRefresh();
        } else {
            //如不，判断是否拦截事件,对View 的位置进行还原
            if (isDispatch) {
                restoreLayout();
            }
        }
        isRefresh = false;
        isDispatch = false;
    }

    /**
     * 是否开始滚动
     **/
    protected boolean canScroll(int type) {

        if (refresh_status == START_ING) {
            return true;
        }
        return contentView.canScrollVertically(type);
    }

    /**
     * 滚动到显示Header或者Foot 位置，表明开始刷新或加载状态
     **/
    protected void smoothScrollRefresh() {
        refresh_status = START_ING;
        ValueAnimator animator;
        if (type == refresh_type.DOWN) {
            setRefreshText("正在刷新");
            animator = ValueAnimator.ofInt(getScrollY(), -min_distance);
        } else if (type == refresh_type.UP) {
            setLoadMoreText("正在加载");
            animator = ValueAnimator.ofInt(getScrollY(), min_distance);
        } else {
            animator = ValueAnimator.ofInt(getScrollY(), 0);
        }
        animator.setDuration(300);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (type == refresh_type.DOWN) {
                    titleArrow.setVisibility(GONE);
                    titleProgress.setVisibility(VISIBLE);
                    if (onRefreshListener != null) {
                        onRefreshListener.onRefresh(RefreshLayout2.this);
                    }
                } else if (type == refresh_type.UP) {
                    if (onRefreshListener != null) {
                        onRefreshListener.onLoadMore(RefreshLayout2.this);
                    }
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                scrollTo(0, value);
            }
        });
        animator.start();
    }


    /**
     * 还原
     **/
    protected void restoreLayout() {
        ValueAnimator animator = ValueAnimator.ofInt(getScrollY(), 0);
        animator.setDuration(200);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                refresh_status = NOT_STARTED;
                if (type == refresh_type.DOWN) {
                    setRefreshText("下拉刷新");
                    titleArrow.setVisibility(VISIBLE);
                    titleArrow.setRotation(0);
                    titleProgress.setVisibility(GONE);
                } else if (type == refresh_type.UP) {
                    setLoadMoreText("上拉加载");

                }
                type = refresh_type.NULL;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                scrollTo(0, value);
            }

        });
        animator.start();
    }

    //加载完成
    public void refreshComplete() {
        if (type == refresh_type.DOWN) {
            titleProgress.setVisibility(GONE);
            setRefreshText("刷新完成");
        } else if (type == refresh_type.UP) {
            setLoadMoreText("加载完成");
            footProgress.setVisibility(GONE);
        } else {
//            ToastUtil.showToast(getContext(), "刷新错误");
            return;
        }

        if (ViewCompat.isAttachedToWindow(this)) {
            restoreLayout();
        }
    }

    public void autoRefresh() {
        this.postDelayed(new Runnable() {
            @Override
            public void run() {
                refresh_status = START_ING;
                type = refresh_type.DOWN;
                ValueAnimator animator = ValueAnimator.ofInt(0, min_distance);
                animator.setDuration(200);
                animator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        setRefreshText("下拉刷新");
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (onRefreshListener != null)
                            onRefreshListener.onLoadMore(RefreshLayout2.this);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                });
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int value = (int) animation.getAnimatedValue();
                        scrollTo(0, -value);
                        isRefreshDown(value);
                    }
                });
                animator.start();
            }
        }, 200);


    }

    private void setRefreshText(String string) {
        if (!string.equals(((TextView) titleView.findViewById(R.id.titleTv)).getText())) {
            ((TextView) titleView.findViewById(R.id.titleTv)).setText(string);
        }
    }

    private void setLoadMoreText(String string) {
        if (!string.equals(((TextView) titleView.findViewById(R.id.titleTv)).getText())) {
            ((TextView) footView.findViewById(R.id.titleTv)).setText(string);
        }
    }

    /**
     * 手指开始的位置
     **/
    protected float startY = 0;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float offset = (startY - ev.getY()) / damp;

                if (offset < 0 && !canScroll(-1) && enabled_refresh) {
                    type = refresh_type.DOWN;
                    return true;
                }

                if (offset > 0 && !canScroll(1) && enabled_load) {
                    type = refresh_type.UP;
                    return true;
                }
                break;
        }

        return super.onInterceptTouchEvent(ev);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                titleProgress.setVisibility(GONE);
                titleArrow.setVisibility(VISIBLE);
                break;

            case MotionEvent.ACTION_MOVE:
                float offset = (startY - ev.getY()) / damp;

                if (offset < 0 && !canScroll(-1) && enabled_refresh) {
                    type = refresh_type.DOWN;
                    scrollTo(0, isRefreshDown(offset));
                }

                if (offset > 0 && !canScroll(1) && enabled_load) {
                    type = refresh_type.UP;
                    scrollTo(0, isRefreshUp(offset));
                }

                break;
            case MotionEvent.ACTION_UP:
                actionUp();
                break;
        }
        return super.onTouchEvent(ev);
    }

    protected OnRefreshListener onRefreshListener;

    public void setOnRefreshListener(OnRefreshListener listener) {
        this.onRefreshListener = listener;
    }

    public interface OnRefreshListener {

        void onRefresh(RefreshLayout2 layout);

        void onLoadMore(RefreshLayout2 layout);
    }

    public void setEnabledRefresh(boolean enabled) {
        this.enabled_refresh = enabled;
    }

    public void setEnabledLoadMore(boolean enabled) {
        this.enabled_load = enabled;
    }

    public void setDamp(float damp) {
        if (damp < 2.5f) {
            damp = 2.5f;
        }
        if (damp > 10f) {
            damp = 10f;
        }
        this.damp = damp;
    }

    protected void setMinSlipDistance() {
        min_distance = titleView.getMeasuredHeight();
        if (min_distance == 0) {
            min_distance = 100;
        }
    }

    public void setEnabledRefreshOrMore(boolean enabled) {
        enabled_load = enabled;
        enabled_refresh = enabled;
    }


}
