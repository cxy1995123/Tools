package chen.com.library.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;


public class ScrollTabLayout extends HorizontalScrollView implements View.OnClickListener {

    private LinearLayout content;

    public ScrollTabLayout(Context context) {
        this(context, null);
    }

    public ScrollTabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScrollTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        content = new LinearLayout(getContext());
        content.setOrientation(LinearLayout.HORIZONTAL);
        content.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        addView(content);
    }

    @Override
    public void addView(View child) {
        if (getChildCount() > 0) {
            content.addView(child);
        } else {
            super.addView(child);
        }
    }

    private void setItemClick(View click) {
        if (click instanceof ScrollTabItem) {
            click.setOnClickListener(this);
        }
    }

    @Override
    public void addView(View child, ViewGroup.LayoutParams params) {
        setItemClick(child);
        if (getChildCount() > 0) {
            content.addView(child, params);
        } else {
            super.addView(child, params);
        }
    }

    @Override
    public void addView(View child, int width, int height) {
        setItemClick(child);
        if (getChildCount() > 0) {
            content.addView(child, width, height);
        } else {
            super.addView(child, width, height);
        }
    }

    @Override
    public void addView(View child, int index) {
        setItemClick(child);
        if (getChildCount() > 0) {
            content.addView(child, index);
        } else {
            super.addView(child, index);
        }
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        setItemClick(child);
        if (getChildCount() > 0) {
            content.addView(child, index, params);
        } else {
            super.addView(child, index, params);
        }
    }

    private OnItemChangeListener onItemChangeListener;

    public void setOnItemChangeListener(OnItemChangeListener onItemChangeListener) {
        this.onItemChangeListener = onItemChangeListener;
    }

    private int currentItemIndex = 0;

    public interface OnItemChangeListener {
        void onSelectedItemChange(int pos);
    }

    @Override
    public void onClick(View v) {
        int indexOfChild = content.indexOfChild(v);
        if (indexOfChild != currentItemIndex) {
            ScrollTabItem oldItem = (ScrollTabItem) content.getChildAt(currentItemIndex);
            oldItem.setSelected(false);
            ScrollTabItem newItem = (ScrollTabItem) content.getChildAt(indexOfChild);
            newItem.setSelected(true);
            currentItemIndex = indexOfChild;
            int[] location = new int[2];
            v.getLocationOnScreen(location);
            smoothScrollBy(location[0], 0);
            if (onItemChangeListener != null) {
                onItemChangeListener.onSelectedItemChange(currentItemIndex);
            }
        }
    }
}
