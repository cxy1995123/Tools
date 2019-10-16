package chen.com.library.camera;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.widget.AppCompatSeekBar;

import java.io.File;
import java.io.IOException;
import java.util.List;

import chen.com.library.R;
import chen.com.library.data.TimeDate;


public class CustomCameraView extends RelativeLayout implements TextureView.SurfaceTextureListener, View.OnClickListener {
    private TextureView textureView;
    private Button button;
    private TextView emptyView;
    private Camera camera;
    private AppCompatSeekBar seekBar;
    private boolean isPreView = false;
    private int cameraId = 0;

    public Camera getCamera() {
        return camera;
    }

    public CustomCameraView(Context context) {
        this(context, null);
    }

    public CustomCameraView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @SuppressLint("ClickableViewAccessibility")
    public CustomCameraView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        inflate(context, R.layout.view_camera, this);

        textureView = findViewById(R.id.textureView);
        seekBar = findViewById(R.id.seekBar);
        button = findViewById(R.id.start);

        emptyView = findViewById(R.id.empty_view);
        button.setOnClickListener(this);
        textureView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                handleFocus(event, textureView.getWidth(), textureView.getHeight());
                return true;
            }
        });

        post(new Runnable() {
            @Override
            public void run() {
                init();
            }
        });


    }

    private void init() {
        //EATURE_CAMERA ： 后置相机
        //FEATURE_CAMERA_FRONT ： 前置相机
        //判断是否有相机硬件
        PackageManager pm = getContext().getPackageManager();
        boolean hasACamera = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);
        if (!hasACamera) {
            emptyView.setVisibility(VISIBLE);
            return;
        }
        camera = Camera.open(cameraId);//打开指定相机 0为后置 1为前置
        Camera.Parameters parameters = camera.getParameters();//得到摄像头的参数
        parameters.setJpegQuality(100);//设置照片的质量
        List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
        List<Camera.Size> pictureSizes = parameters.getSupportedPictureSizes();
        Camera.Size size = setPreviewSize(sizes);
        parameters.setPreviewSize(size.width, size.height);//设置预览尺寸
        parameters.setPictureSize(pictureSizes.get(0).width, pictureSizes.get(0).height);//设置照片尺寸

        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);// 连续对焦模式
        camera.setParameters(parameters);
        textureView.setSurfaceTextureListener(CustomCameraView.this);
        resetSurfaceViewSize(textureView, size);
        setRotation();
        startPreviewCamera(textureView.getSurfaceTexture());
    }


    private void resetSurfaceViewSize(TextureView surfaceView, Camera.Size size) {

        if (surfaceView.getWidth() < surfaceView.getHeight()) {
            int t = size.height;
            size.height = size.width;
            size.width = t;
        }
        float surfaceRatio = (surfaceView.getWidth() * 0.1f) / (surfaceView.getHeight() * 0.1f);
        float resultRatio = (size.width * 0.1f) / (size.height * 0.1f);

        int resultWidth = 0;
        int resultHeigth = 0;
        resultWidth = surfaceView.getWidth();
        if (Math.abs(surfaceRatio - resultRatio) > 0.1) {
            float widthRatio = (surfaceView.getWidth() * 0.1f) / (size.width * 0.1f);
            if (widthRatio > 1) {
                resultHeigth = (int) (size.height * widthRatio);
            } else {
                resultHeigth = (int) (size.height / widthRatio);
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

            ViewGroup.LayoutParams params = surfaceView.getLayoutParams();
            params.width = resultWidth;
            params.height = resultHeigth;
            surfaceView.setLayoutParams(params);
            surfaceView.setTranslationY(-offsetY);
            surfaceView.setTranslationX(-offsetX);
            Log.i("CameraManager", "宽：" + resultWidth + "高：" + resultHeigth + "偏移：X" + offsetX + ",Y:" + offsetY);
        }


    }

    private Camera.Size setPreviewSize(List<Camera.Size> sizes) {
        if (sizes == null) return null;
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        boolean horizontal = width > height;
        if (!horizontal) {
            int temporarily = height;
            height = width;
            width = temporarily;
        }
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) width / height;
        if (sizes == null) return null;
        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;
        int targetHeight = height;
        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }
        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    private void setRotation() {
        //有些手机上面会出现旋转90度的情况（Android兼容性问题）所以我们要在这里适配一下
        Camera.CameraInfo info = new Camera.CameraInfo();
        //获取摄像头信息
        Camera.getCameraInfo(cameraId, info);

        WindowManager manager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);

        if (manager == null) return;

        int rotation = manager.getDefaultDisplay().getRotation();
        //获取摄像头当前的角度
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            // 前置摄像头
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;
        } else {
            // 后置摄像头
            result = (info.orientation - degrees + 360) % 360;
        }
        Log.i("CustomCameraView", "setRotation: " + result);
        camera.setDisplayOrientation(result);

    }

    private void startPreviewCamera(SurfaceTexture texture) {
        try {
            if (isPreView) {
                return;
            }

            if (textureView.isAvailable()) {
                camera.setPreviewTexture(texture);//通过SurfaceView显示取景画面
                camera.startPreview();//开始预览
                isPreView = true;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopPreviewCamera() {
        camera.stopPreview();
        camera.release();
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        startPreviewCamera(surface);
    }


    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        stopPreviewCamera();
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    /**
     * 手动聚焦
     */
    public void handleFocus(MotionEvent event, int viewWidth, int viewHeight) {
        Log.i("handleFocus", "handleFocus: ");
        try {
            if (!isPreView) return;
            if (camera == null) return;
//            Rect focusRect = calculateTapArea(event.getX(), event.getY(), viewWidth, viewHeight, 1.0f);
            //一定要首先取消，否则无法再次开启
            camera.cancelAutoFocus();
            Camera.Parameters params = camera.getParameters();
//            if (params.getMaxNumFocusAreas() > 0) {
//                List<Camera.Area> focusAreas = new ArrayList<>();
//                focusAreas.add(new Camera.Area(focusRect, 800));
//                params.setFocusAreas(focusAreas);
//            } else {
//                //focus areas not supported
//            }
            //首先保存原来的对焦模式，然后设置为macro，对焦回调后设置为保存的对焦模式
            final String currentFocusMode = params.getFocusMode();
            List<String> focusModes = params.getSupportedFocusModes();
            if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            } else if (focusModes.contains(Camera.Parameters.FOCUS_MODE_MACRO)) {
                params.setFocusMode(Camera.Parameters.FOCUS_MODE_MACRO);
            } else if (focusModes.contains(Camera.Parameters.ANTIBANDING_AUTO)) {
                params.setFocusMode(Camera.Parameters.ANTIBANDING_AUTO);
            }
            camera.setParameters(params);
            camera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean success, Camera camera) {
                    //回调后 还原模式
                    Camera.Parameters params = camera.getParameters();
                    params.setFocusMode(currentFocusMode);
                    camera.setParameters(params);
                    if (success) {
                        camera.cancelAutoFocus();
                    }
                }
            });
        } catch (Exception e) {

        }
    }

    private Rect calculateTapArea(float x, float y, int width, int height, float coefficient) {
        float focusAreaSize = 200;
        //这段代码可以看出coefficient的作用，只是为了扩展areaSize。
        int areaSize = (int) (focusAreaSize * coefficient);
        int surfaceWidth = width;
        int surfaceHeight = height;
        //解释一下为什么*2000，因为要把surfaceView的坐标转换为范围(-1000, -1000, 1000, 1000)，则SurfaceView的中心点坐标会转化为（0,0），x/surfaceWidth ，得到当前x坐标占总宽度的比例，然后乘以2000就换算成了（0,0，2000,2000）的坐标范围内，然后减去1000，就换算为了范围(-1000, -1000, 1000, 1000)的坐标。
        //得到了x,y转换后的坐标，利用areaSize就可以得到聚焦区域。
        int centerX = (int) (x / surfaceHeight * 2000 - 1000);
        int centerY = (int) (y / surfaceWidth * 2000 - 1000);
        int left = clamp(centerX - (areaSize / 2), -1000, 1000);
        int top = clamp(centerY - (areaSize / 2), -1000, 1000);
        int right = clamp(left + areaSize, -1000, 1000);
        int bottom = clamp(top + areaSize, -1000, 1000);
        return new Rect(left, top, right, bottom);
    }

    //不大于最大值，不小于最小值
    private int clamp(int x, int min, int max) {
        if (x > max) {
            return max;
        }
        if (x < min) {
            return min;
        }
        return x;
    }


    @Override
    public void onClick(View v) {
        if (camera == null) return;
        if (!isPreView) return;
        String dir = Environment.getExternalStorageDirectory() + "/TestDir";
        File file = new File(dir, TimeDate.getInstance() + ".jpeg");
        camera.takePicture(null, null, new JPEGCallBack(file.getAbsolutePath()));
    }


    public static class JPEGCallBack implements Camera.PictureCallback {

        private String filePath;

        public JPEGCallBack(String filePath) {
            this.filePath = filePath;
        }

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

        }

    }

}
