package chen.com.library.view;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 通过裁切画布的形式形成圆角
 */
public class ClipRoundedRectangleFrameLayout extends FrameLayout {

    public ClipRoundedRectangleFrameLayout(@NonNull Context context) {
        this(context, null);
    }

    public ClipRoundedRectangleFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClipRoundedRectangleFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private Path path;
    private RectF rectF;
    public RectF getRectF() {
        if (rectF == null) {
            rectF = new RectF();
        }
        rectF.set(0, 0, getMeasuredWidth(), getMeasuredHeight());
        return rectF;
    }

    public Path getPath() {
        if (path == null) {
            path = new Path();
        }
        path.addRoundRect(getRectF(), 25, 25, Path.Direction.CW);
        return path;
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.clipPath(getPath());
        super.draw(canvas);
    }


}
