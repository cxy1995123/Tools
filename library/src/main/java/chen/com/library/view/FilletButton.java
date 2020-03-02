package chen.com.library.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import chen.com.library.R;
import chen.com.library.tools.Util;

public class FilletButton extends View {
    private int backgroundColor = 0;
    private int pressColor = 0;
    private int textColor = 0;
    private int textSize = 0;
    private float left_top_fillet = 0;
    private float right_top_fillet = 0;
    private float right_bottom_fillet = 0;
    private float left_bottom_fillet = 0;
    private String text = null;
    private boolean onTouch = false;

    private Paint bgPaint = new Paint();

    private Paint textPaint = new Paint();

    public FilletButton(Context context) {
        this(context, null);
    }

    public FilletButton(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FilletButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        bgPaint.setAntiAlias(true);
        textPaint.setAntiAlias(true);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.FilletButton);
        int count = array.getIndexCount();
        for (int i = 0; i < count; i++) {
            int index = array.getIndex(i);
            if (index == R.styleable.FilletButton_backgroundColor) {
                backgroundColor = array.getColor(index, backgroundColor);
            } else if (index == R.styleable.FilletButton_pressColor) {
                pressColor = array.getColor(index, pressColor);
            } else if (index == R.styleable.FilletButton_android_text) {
                text = array.getString(index);
            } else if (index == R.styleable.FilletButton_android_textColor) {
                textColor = array.getColor(index, textColor);
            } else if (index == R.styleable.FilletButton_android_textSize) {
                textSize = array.getDimensionPixelOffset(index, textSize);
            } else if (index == R.styleable.FilletButton_buttonRadius) {
                float radius = array.getDimensionPixelOffset(index, 0);
                left_top_fillet = radius;
                right_top_fillet = radius;
                left_bottom_fillet = radius;
                right_bottom_fillet = radius;
            } else if (index == R.styleable.FilletButton_left_top_fillet) {
                left_top_fillet = array.getDimensionPixelOffset(index, 0);
            } else if (index == R.styleable.FilletButton_right_top_fillet) {
                right_top_fillet = array.getDimensionPixelOffset(index, 0);
            } else if (index == R.styleable.FilletButton_left_bottom_fillet) {
                left_bottom_fillet = array.getDimensionPixelOffset(index, 0);
            } else if (index == R.styleable.FilletButton_right_bottom_fillet) {
                right_bottom_fillet = array.getDimensionPixelOffset(index, 0);
            }
        }

        if (textSize == 0) {
            textSize = Util.sp2px(context, 14);
        }

        if (textColor == 0) {
            textColor = Color.BLACK;
        }

        array.recycle();
        textPaint.setTextSize(textSize);
        textPaint.setColor(textColor);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int w = widthMeasureSpec;
        int h = heightMeasureSpec;
        if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.AT_MOST) {
            w = Util.dip2px(getContext(), 96);
        }
        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST) {
            h = Util.dip2px(getContext(), 48);
        }
        setMeasuredDimension(MeasureSpec.getSize(w), MeasureSpec.getSize(h));
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    @Override
    public void setBackground(Drawable background) {

    }

    @Override
    public void setBackgroundDrawable(Drawable background) {

    }

    @Override
    public void setBackgroundResource(int resid) {

    }

    @Override
    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (onTouch) {
            bgPaint.setColor(pressColor);
        } else {
            bgPaint.setColor(backgroundColor);
        }
        canvas.drawPath(initPath(), bgPaint);
        if (!TextUtils.isEmpty(text)) {
            getTextBounds();
            int height = textBounds.height() / 2;
            int width = textBounds.width() / 2;
            canvas.drawText(text, rectF.centerX() - width, rectF.centerY() + height, textPaint);
        }
    }

    Rect textBounds = new Rect();

    private void getTextBounds() {
        textPaint.getTextBounds(text, 0, text.length(), textBounds);
    }

    private RectF rectF = new RectF();
    private Path path = new Path();

    public void initRectF() {
        int right = getMeasuredWidth();
        int bottom = getMeasuredHeight();
        rectF.set(0, 0, right, bottom);
    }

    public Path initPath() {
        initRectF();
        path.reset();
        float[] radii = {left_top_fillet, left_top_fillet, right_top_fillet, right_top_fillet,
                right_bottom_fillet, right_bottom_fillet, left_bottom_fillet, left_bottom_fillet};
        path.addRoundRect(rectF, radii, Path.Direction.CW);
        path.close();
        return path;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            onTouch = true;
            invalidate();
            return true;
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            onTouch = false;
            invalidate();
            float eventX = event.getX();
            float eventY = event.getY();
            boolean contains = rectF.contains(eventX, eventY);
            if (contains) {
                performClick();
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

}
