package chen.com.library.camera;

import android.view.TextureView;

public interface Camera {

    void initialization();

    TextureView getTextureView();

    boolean isLandscape();

    void takePhoto();

}
