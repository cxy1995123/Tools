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
 * @author chenxingyu
 * data 2019/8/22
 */
public class ExpendTextView extends LinearLayout {

    private TextView display, tips;
    private View expend;
    private ImageView arrowIm;
    //内容null时展示的数据
    private String defaultStr = "暂无";
    //展开按钮上的字
    private String expendText = "收起", collapseText = "展开";
    //展开按钮上的图标
    private int tipsRes = R.drawable.icon_arrow_refresh;
    //大于这个行数就可以折叠
    private int minLines = 2;
    //用于展示的字符
    private String displayText;
    //是否展开
    private boolean expanded = true;

    /**
     * TextView 字符行间距倍数
     *
     * @see TextView#getLineSpacingMultiplier()
     */
    private float mSpacingMult = 1.0f;


    /**
     * TextView 字符行间距增量
     *
     * @see TextView#getLineSpacingExtra()
     */
    private float mSpacingAdd = 0.0f;

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
        tips = findViewById(R.id.tips);
        arrowIm = findViewById(R.id.arrowIm);
        expend = findViewById(R.id.expend);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ExpendTextView);
        int count = array.getIndexCount();
        for (int i = 0; i < count; i++) {
            int index = array.getIndex(i);
            if (index == R.styleable.ExpendTextView_android_lineSpacingExtra) {
                mSpacingAdd = array.getDimensionPixelOffset(index, (int) mSpacingAdd);
            } else if (index == R.styleable.ExpendTextView_android_lineSpacingMultiplier) {
                mSpacingMult = array.getFloat(index, mSpacingMult);
            } else if (index == R.styleable.ExpendTextView_expend_default_text) {
                defaultStr = array.getString(index);
            } else if (index == R.styleable.ExpendTextView_expend_min_line) {
                minLines = array.getInt(index, minLines);
            } else if (index == R.styleable.ExpendTextView_expend_tips_icon) {
                tipsRes = array.getResourceId(index, tipsRes);
            } else if (index == R.styleable.ExpendTextView_expend_tips_str) {
                expendText = array.getString(index);
            } else if (index == R.styleable.ExpendTextView_collapse_tips_str) {
                collapseText = array.getString(index);
            } else if (index == R.styleable.ExpendTextView_android_textColor) {
                display.setTextColor(array.getInt(index, R.color.design_default_color_primary));
            } else if (index == R.styleable.ExpendTextView_android_text) {
                displayText = array.getString(index);
            } else if (index == R.styleable.ExpendTextView_android_textSize) {
                int size = array.getDimensionPixelSize(index, 14);
                display.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
            } else if (index == R.styleable.ExpendTextView_expanded) {
                expanded = array.getBoolean(index, false);
            }

        }
        array.recycle();

        display.setLineSpacing(mSpacingAdd, mSpacingMult);
        arrowIm.setImageResource(tipsRes);
        setText(TextUtils.isEmpty(displayText) ? defaultStr : displayText);

        setExpanded(expanded);
        expend.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setExpanded(!expanded);
            }
        });
    }

    private void setExpanded(boolean expanded) {
        if (expanded) {
            expand();
        } else {
            collapse();
        }
    }

    public void expand() {
        display.setMaxLines(999);
        arrowIm.setRotation(180);
        expanded = true;
        tips.setText(expendText);
    }

    public void collapse() {
        display.setMaxLines(minLines);
        arrowIm.setRotation(0);
        expanded = false;
        tips.setText(collapseText);
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setText(String str) {
        displayText = str;
        if (TextUtils.isEmpty(displayText)) {
            display.setText(defaultStr);
        } else {
            display.setText(displayText);
        }
        measureTextLines();
    }

    private void measureTextLines() {
        if (TextUtils.isEmpty(displayText)) {
            displayText = "";
        }

        post(new Runnable() {
            @Override
            public void run() {
                int width = getMeasuredWidth() - getPaddingLeft() + getPaddingRight();
                DynamicLayout dynamicLayout = new DynamicLayout(displayText, display.getPaint(), width, Layout.Alignment.ALIGN_NORMAL, mSpacingMult, mSpacingAdd, false);
                int lineCount = dynamicLayout.getLineCount();
                Log.i("ExpendTextView", "lineCount: " + lineCount + ",minLines:" + minLines);
                if (lineCount > minLines) {
                    expend.setVisibility(VISIBLE);
                } else {
                    expend.setVisibility(GONE);
                }
            }
        });

    }
}