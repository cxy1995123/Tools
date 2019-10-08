package chen.com.library.camera;

import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Range;
import android.util.Size;
import android.view.TextureView;
import android.view.ViewGroup;

import androidx.annotation.RequiresApi;

import chen.com.library.R;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class CameraView21 extends AbstractCamera21 {

    public CameraView21(Context context) {
        this(context, null);
    }

    public CameraView21(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CameraView21(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.view_camera2, this);
        textureView = findViewById(R.id.textureView);
    }

    @Override
    protected Range<Integer> getFpsRange() {
        Range<Integer> result = null;
        CameraCharacteristics chars = null;
        try {
            chars = getCameraManager().getCameraCharacteristics(getCameraId());
            Range<Integer>[] ranges = chars.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_TARGET_FPS_RANGES);
            if (ranges == null) return null;
            for (Range<Integer> range : ranges) {
                //帧率不能太低，大于10
                if (range.getLower() < 10)
                    continue;
                if (result == null)
                    result = range;
                    //FPS下限小于15，弱光时能保证足够曝光时间，提高亮度。range范围跨度越大越好，光源足够时FPS较高，预览更流畅，光源不够时FPS较低，亮度更好。
                else if (range.getLower() <= 15 && (range.getUpper() - range.getLower()) > (result.getUpper() - result.getLower()))
                    result = range;
            }

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        return result;
    }


    @Override
    protected CaptureRequest.Builder createPreViewCaptureRequest() {
        // 设置预览尺寸
        try {
            //构建预览请求
            CaptureRequest.Builder mCaptureBuilder = getCurrentCameraDevice().createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            //对焦触发器设置为空闲状态
            mCaptureBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CaptureRequest.CONTROL_AF_TRIGGER_START);
            //图片方向
            mCaptureBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(getWindowRotation()));
            // 设置为自动对焦
            mCaptureBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_AUTO);
            //每个3A例程使用设置
            mCaptureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
            //设置自动曝光帧率范围
            Range<Integer> fpsRange = getFpsRange();
            if (fpsRange != null) {
                mCaptureBuilder.set(CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE, fpsRange);
            }
            return mCaptureBuilder;
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected void resetDisplayViewSize(TextureView surfaceView, Size size) {

        float surfaceRatio = (surfaceView.getWidth() * 0.1f) / (surfaceView.getHeight() * 0.1f);
        float resultRatio = (size.getWidth() * 0.1f) / (size.getHeight() * 0.1f);

        int resultWidth = 0;
        int resultHeigth = 0;
        resultWidth = surfaceView.getWidth();
        if (Math.abs(surfaceRatio - resultRatio) > 0.1) {
            float widthRatio = (surfaceView.getWidth() * 0.1f) / (size.getWidth() * 0.1f);
            if (widthRatio > 1) {
                resultHeigth = (int) (size.getHeight() * widthRatio);
            } else {
                resultHeigth = (int) (size.getHeight() / widthRatio);
            }

            if (resultHeigth < surfaceView.getHeight()) {
                float heightRatio = (surfaceView.getHeight() * 0.1f) / (resultHeigth * 0.1f);
                if (heightRatio > 1) {
                    resultWidth = (int) (resultWidth * widthRatio);
                } else {
                    resultWidth = (int) (resultWidth / widthRatio);
                }
                resultHeigth = surfaceView.getHeight();
            }
            int offsetY = (resultHeigth - surfaceView.getHeight()) / 2;
            int offsetX = (resultWidth - surfaceView.getWidth()) / 2;
            ViewGroup.LayoutParams params = textureView.getLayoutParams();
            params.width = resultWidth;
            params.height = resultHeigth;
            textureView.setLayoutParams(params);
            configureTransform(textureView.getWidth(), textureView.getHeight());
        }
    }

    @Override
    public TextureView getTextureView() {
        return textureView;
    }

    @Override
    public void takePhoto() {
        captureStillPicture();
    }


}
