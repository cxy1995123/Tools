package chen.com.library.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.DynamicLayout;
import android.text.Layout;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import chen.com.library.R;

/**
 * @author cehnxingyu
 */
public class ExpendTextView extends LinearLayout {

    private TextView display, tips;

    private View expend;
    private ImageView arrowIm;

    private String defaultStr = "暂无", expendText = "展开", collapseText = "收缩";
    private int minLines = 2;
    private int tipsRes = R.drawable.icon_arrow_refresh;
    private String displayText;
    private boolean isExpend = true;

    public ExpendTextView(Context context) {
        this(context, null);
    }

    public ExpendTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExpendTextView(Context context, AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.view_expend_text, this);
        display = findViewById(R.id.textView);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ExpendTextView);
        int count = array.getIndexCount();
        for (int i = 0; i < count; i++) {
            int index = array.getIndex(i);
            if (index == R.styleable.ExpendTextView_expend_default_text) {
                defaultStr = array.getString(index);
            } else if (index == R.styleable.ExpendTextView_expend_min_line) {
                minLines = array.getIndex(index);
            } else if (index == R.styleable.ExpendTextView_expend_tips_icon) {
                tipsRes = array.getIndex(index);
            } else if (index == R.styleable.ExpendTextView_expend_tips_str) {
                expendText = array.getString(index);
            } else if (index == R.styleable.ExpendTextView_collapse_tips_str) {
                collapseText = array.getString(index);
            } else if (index == R.styleable.ExpendTextView_android_textColor) {
                display.setTextColor(array.getInt(index, 0));
            } else if (index == R.styleable.ExpendTextView_android_text) {
                displayText = array.getString(index);
            } else if (index == R.styleable.ExpendTextView_android_textSize) {
                int size = array.getDimensionPixelSize(index, 14);
                display.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
            }

        }

        array.recycle();

        display.setMaxLines(999);
        tips = findViewById(R.id.tips);
        arrowIm = findViewById(R.id.arrowIm);
        expend = findViewById(R.id.expend);
        expend.setClickable(true);
        expend.setClickable(true);
        display.setText(TextUtils.isEmpty(displayText) ? defaultStr : displayText);
        tips.setText(expendText);
        arrowIm.setImageResource(tipsRes);
        measureTextLines();
        expend.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isExpend) {
                    display.setMaxLines(minLines);
                    arrowIm.setRotation(180);
                } else {
                    display.setMaxLines(999);
                    arrowIm.setRotation(0);
                }
            }
        });

    }


    public void setText(String str) {
        displayText = str;
        measureTextLines();
    }

    private void measureTextLines() {
        post(new Runnable() {
            @Override
            public void run() {
                int width = getMeasuredWidth() - getPaddingLeft() + getPaddingRight();
                DynamicLayout dynamicLayout = new DynamicLayout(displayText, display.getPaint(), width, Layout.Alignment.ALIGN_NORMAL, display.getLineSpacingMultiplier(), display.getLineSpacingExtra(), false);
                int lineCount = dynamicLayout.getLineCount();
                Log.i("ExpendTextView", "measureTextLines: " + lineCount);
                if (lineCount > minLines) {
                    expend.setVisibility(VISIBLE);
                } else {
                    expend.setVisibility(GONE);
                }
            }
        });

    }
}
