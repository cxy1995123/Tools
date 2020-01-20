package chen.com.tools;

import android.content.Context;
import android.view.View;

import androidx.viewpager.widget.ViewPager;

import chen.com.library.tools.Util;

public class ZoomOutPageTransformer implements ViewPager.PageTransformer {

    private static final float MIN_SCALE = 0.7f;//0.85f

    @Override
    public void transformPage(View page, float position) {
        if (position >= 1 || position <= -1) {
            page.setScaleY(MIN_SCALE);

        } else if (position < 0) {
            float scaleY = MIN_SCALE + (1 + position) * (1 - MIN_SCALE);
            page.setScaleY(scaleY);
        } else {
            float scaleY = (1 - MIN_SCALE) * (1 - position) + MIN_SCALE;
            page.setScaleY(scaleY);
        }

    }
}
