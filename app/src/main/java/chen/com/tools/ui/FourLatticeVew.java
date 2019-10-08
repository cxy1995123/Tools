package chen.com.tools.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class FourLatticeVew extends ViewGroup {

    private int offset = 20;

    public FourLatticeVew(Context context) {
        super(context);
    }

    public FourLatticeVew(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FourLatticeVew(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int count = getChildCount();
        if (count == 1) {
            View view = getChildAt(0);
            view.layout(l + offset, t + offset, r - offset, b - offset);
//            view.setLayoutParams(new ViewGroup.LayoutParams(getWidth() - offset*2,getHeight()-offset*2));
        }
//        if (count == 2) {
//            int itemW = (getWidth() - offset * 3) / 2;
//            getChildAt(0).layout(l+offset, l+offset+itemW, r, b);
//            getChildAt(1).layout(l, t, r, b);
//        }


    }
}
