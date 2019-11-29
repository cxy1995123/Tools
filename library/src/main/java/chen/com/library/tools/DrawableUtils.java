package chen.com.library.tools;
import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

public class DrawableUtils {
    public static Drawable tintDrawable(Context context, @DrawableRes int res, @ColorInt int color) {
        Drawable drawable = DrawableCompat.wrap(ContextCompat.getDrawable(context, res)).mutate();
        DrawableCompat.setTint(drawable, color);
        return drawable;
    }
}
