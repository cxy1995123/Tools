package chen.com.tools;
import android.app.Activity;
import chen.com.library.window.BasePopupView;

public class TestFloatView extends BasePopupView {

    public TestFloatView(Activity context) {
        super(context);
        setFullScreen(false);
        setOutsideTouchable(true);
        setAnimDuration(300);
    }

    @Override
    protected int onContentLayout() {
        return R.layout.view_float;
    }
}
