package chen.com.library.camera;

import android.app.Activity;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.view.TextureView;
import android.widget.FrameLayout;

public abstract class AbstractCamera extends FrameLayout implements Camera, TextureView.SurfaceTextureListener {

    protected TextureView textureView;

    private int windowRotation;

    public AbstractCamera(Context context) {
        super(context);
    }

    public AbstractCamera(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AbstractCamera(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void initialization() {
        windowRotation = ((Activity) getContext()).getWindow().getWindowManager().getDefaultDisplay().getRotation();

    }

    @Override
    public boolean isLandscape() {
        return windowRotation == 1 || windowRotation == 3;
    }


    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }


    public int getWindowRotation() {
        return windowRotation;
    }
}
