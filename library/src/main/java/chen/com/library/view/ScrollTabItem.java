package chen.com.library.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import chen.com.library.R;
import chen.com.library.tools.Util;

public class ScrollTabItem extends LinearLayout {

    private TextView textView;
    private ImageView indicator;
    private String title;
    private int textSize = 16;
    private int textColor = Color.parseColor("#000000");
    private int selectedTextColor = Color.parseColor("#4e91f9");
    private boolean isSelected = false;

    @Override
    public void setSelected(boolean selected) {
        if (isSelected!=selected){
            isSelected = selected;
            changeStatus();
        }
    }

    public ScrollTabItem(@NonNull Context context) {
        this(context, null);
    }

    public ScrollTabItem(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScrollTabItem(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.item_scroll_bar_item, this);
        textView = findViewById(R.id.text);
        indicator = findViewById(R.id.indicator);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ScrollTabItem);
        int count = array.getIndexCount();
        textSize = Util.sp2px(context, 14);
        for (int i = 0; i < count; i++) {
            int index = array.getIndex(i);
            if (index == R.styleable.ScrollTabItem_android_text) {
                title = array.getString(index);
            } else if (index == R.styleable.ScrollTabItem_android_textColor) {
                textColor = array.getColor(index, textColor);
            } else if (index == R.styleable.ScrollTabItem_android_textSize) {
                textSize = array.getDimensionPixelOffset(index, textSize);
            } else if (index == R.styleable.ScrollTabItem_selectedTextColor) {
                selectedTextColor = array.getColor(index, selectedTextColor);
            } else if (index == R.styleable.ScrollTabItem_isSelect) {
                isSelected = array.getBoolean(index, isSelected);
            }
        }
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        array.recycle();
        changeStatus();
    }

    private void changeStatus() {
        if (isSelected) {
            textView.setTextColor(selectedTextColor);
            indicator.setVisibility(VISIBLE);
        } else {
            textView.setTextColor(textColor);
            indicator.setVisibility(GONE);
        }
        textView.setText(title);
    }
}
