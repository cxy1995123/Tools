package chen.com.library.camera;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraDevice.StateCallback;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;

import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Range;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@SuppressLint("MissingPermission")
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public abstract class AbstractCamera21 extends AbstractCamera {
    public static final String TAG = "CameraView2";
    public static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    private boolean isResetPreSize = true;

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    private CameraManager cameraManager;

    private CameraDevice mCameraDevice;

    private Size mPreviewSize;

    /**
     * 是否支持闪光
     */
    private boolean mFlashSupported;

    /**
     * 打开的相机ID
     */
    private String mCameraId;

    private Handler mHandler;

    /**
     * 相机预览请求
     */
    private CaptureRequest preViewCaptureRequest;

    /**
     * 预览请求会话
     */
    private CameraCaptureSession mCameraCaptureSession;

    /**
     * 相机参数获取类
     */
    private CameraCharacteristics characteristics;

    private CaptureRequest.Builder mCaptureBuilder;

    private HandlerThread handlerThread;

    protected ImageReader mImageReader;

    public AbstractCamera21(Context context) {
        super(context);
    }

    public AbstractCamera21(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AbstractCamera21(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void initialization() {
        super.initialization();
        handlerThread = new HandlerThread("Handler-Thread");
        handlerThread.start();
        mHandler = new Handler(handlerThread.getLooper());
        textureView = getTextureView();
        textureView.setSurfaceTextureListener(this);
        cameraManager = (CameraManager) getContext().getSystemService(Context.CAMERA_SERVICE);
        openCamera();
    }

    /**
     * 获取一个适合的分辨率
     */
    protected Size getPreviewSize(Size[] outputSizes, int width, int height) {
        if (!isLandscape()) {
            int temporarily = height;
            height = width;
            width = temporarily;
        }

        List<Size> sizes = Arrays.asList(outputSizes);
        Collections.sort(sizes, new Comparator<Size>() {
            @Override
            public int compare(Size o1, Size o2) {
                int aPixels = o1.getHeight() * o1.getWidth();
                int bPixels = o2.getHeight() * o2.getWidth();
                if (bPixels < aPixels) {
                    return -1;
                }
                if (bPixels > aPixels) {
                    return 1;
                }
                return 0;
            }
        });

        Size outSize = null;

        for (Size size : sizes) {
            if (size.getHeight() <= height && size.getWidth() <= width) {
                outSize = size;
                break;
            }
        }
//        final double ASPECT_TOLERANCE = 0.1;
//        double targetRatio = (double) width / height;
//        if (sizes == null) return null;
//
//        double minDiff = Double.MAX_VALUE;
//        int targetHeight = height;
//        for (Size size : sizes) {
//            double ratio = (double) size.getWidth() / size.getHeight();
//            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
//            if (Math.abs(size.getHeight() - targetHeight) < minDiff) {
//                outSize = size;
//                minDiff = Math.abs(size.getHeight() - targetHeight);
//            }
//        }
//        // Cannot find the one match the aspect ratio, ignore the requirement
//        if (outSize == null) {
//            minDiff = Double.MAX_VALUE;
//            for (Size size : sizes) {
//                if (Math.abs(size.getHeight() - targetHeight) < minDiff) {
//                    outSize = size;
//                    minDiff = Math.abs(size.getHeight() - targetHeight);
//                }
//            }
//        }
        Log.i(TAG, "视图的宽高: " + width + "," + height);
        Log.i(TAG, "筛选出来的宽高: " + outSize.getWidth() + "," + outSize.getHeight());
        if (!isLandscape()) {
            outSize = new Size(outSize.getHeight(), outSize.getWidth());
        }

        return outSize;
    }


    /**
     * 获取一个合适的帧率
     */
    protected abstract Range<Integer> getFpsRange();

    /**
     * 获取相机预览分辨率后
     * 调整 TextureView 的大小
     */
    protected abstract void resetDisplayViewSize(TextureView textureView, Size mPreviewSize);

    /**
     * 调整 TextureView 的预览方向
     */
    protected void configureTransform(int viewWidth, int viewHeight) {
        if (null == textureView || null == mPreviewSize) {
            return;
        }
        int rotation = ((Activity) getContext()).getWindowManager().getDefaultDisplay().getRotation();
        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
        RectF bufferRect = new RectF(0, 0, mPreviewSize.getHeight(), mPreviewSize.getWidth());
        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();
        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
            float scale1 = Math.max(
                    (float) viewHeight / mPreviewSize.getHeight(),
                    (float) viewWidth / mPreviewSize.getWidth());
            float scale2 = Math.min(
                    (float) mPreviewSize.getHeight() / viewHeight,
                    (float) mPreviewSize.getWidth() / viewWidth);
            matrix.postScale(scale1, scale2, centerX, centerY);
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);
        }
        textureView.setTransform(matrix);
    }

    protected void stopPreview() {
        Log.i(TAG, "stopPreview: ");
        if (mCameraCaptureSession != null) {
            try {
                mCameraCaptureSession.stopRepeating();
                mCameraCaptureSession.abortCaptures();
                mCameraCaptureSession.close();
                mCameraCaptureSession = null;
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
        if (mCameraDevice != null) {
            mCameraDevice.close();
            mCameraDevice = null;
        }
    }

    private StateCallback stateCallback = new StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            mCameraDevice = camera;
            startPreView();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            camera.close();
            mCameraDevice = null;
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            camera.close();
            mCameraDevice = null;
        }
    };

    /**
     * 开始预览
     */
    protected void startPreView() {
        try {
            Size pictureSzie = getPictureSzie();
            mImageReader = ImageReader.newInstance(pictureSzie.getWidth(), pictureSzie.getHeight(), ImageFormat.JPEG, 1);
            mImageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader reader) {
                    Image image = reader.acquireLatestImage();
                    ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                    byte[] bytes = new byte[buffer.remaining()];
                    buffer.get(bytes);//由缓冲区存入字节数组
                    final Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    File dir = new File(Environment.getExternalStorageDirectory() + "/TestDir");
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    File des = new File(dir, "pc.jpg");
                    try {
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(des));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }, getWorkHandler());

            mCaptureBuilder = createPreViewCaptureRequest();
            Surface surface = new Surface(textureView.getSurfaceTexture());
            // 添加输出的surface
            mCaptureBuilder.addTarget(surface);
            preViewCaptureRequest = mCaptureBuilder.build();
            // 创建预览 CameraCaptureSession
            getCurrentCameraDevice().createCaptureSession(Arrays.asList(surface, mImageReader.getSurface()), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    try {
                        mCameraCaptureSession = cameraCaptureSession;
                        //设置反复捕获数据的请求，这样预览界面就会一直有数据显示
                        mCameraCaptureSession.setRepeatingRequest(preViewCaptureRequest, null, getWorkHandler());// 设置成预览
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                    stopPreview();
                }
            }, getWorkHandler());


        } catch (Exception e) {

        }
    }


    public CameraDevice getCurrentCameraDevice() {
        return mCameraDevice;
    }

    public CameraManager getCameraManager() {
        return cameraManager;
    }

    public Handler getWorkHandler() {
        return mHandler;
    }

    public String getCameraId() {
        return mCameraId;
    }

    public CameraCaptureSession getCapterSeesion() {
        return mCameraCaptureSession;
    }

    protected abstract CaptureRequest.Builder createPreViewCaptureRequest();

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        super.onSurfaceTextureAvailable(surface, width, height);
        openCamera();
    }


    public boolean isPreView = false;

    private void openCamera() {

        if (isPreView || !textureView.isAvailable() || cameraManager == null) {
            return;
        }

        StreamConfigurationMap map = null;
        try {
            //获取可用摄像头列表
            for (String cameraId : cameraManager.getCameraIdList()) {
                //获取相机的相关参数
                characteristics = cameraManager.getCameraCharacteristics(cameraId);
                // 不使用前置摄像头。
                Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
                if (facing != null && facing == CameraCharacteristics.LENS_FACING_BACK) {
                    map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                    // 检查闪光灯是否支持。
                    Float aFloat = characteristics.get(CameraCharacteristics.LENS_INFO_MINIMUM_FOCUS_DISTANCE);
                    Integer integer = characteristics.get(CameraCharacteristics.LENS_INFO_FOCUS_DISTANCE_CALIBRATION);
                    Log.i("AbstractCamera21", "openCamera: " + aFloat + "," + integer);
                    Boolean available = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                    mFlashSupported = available == null ? false : available;
                    mCameraId = cameraId;
                    break;
                }
            }

            if (map != null && isResetPreSize) {
                mPreviewSize = getPreviewSize(map.getOutputSizes(SurfaceTexture.class), textureView.getWidth(), textureView.getHeight());
                isResetPreSize = false;
                resetDisplayViewSize(getTextureView(), mPreviewSize);
                //根据屏幕方向设置 textureView 的缓冲大小以及缓冲区的方向
                if (isLandscape()) {
                    textureView.getSurfaceTexture().setDefaultBufferSize(textureView.getWidth(), textureView.getHeight());
                } else {
                    textureView.getSurfaceTexture().setDefaultBufferSize(textureView.getHeight(), textureView.getWidth());
                }
            }

            if (!TextUtils.isEmpty(mCameraId)) {
                isPreView = true;
                cameraManager.openCamera(mCameraId, stateCallback, mHandler);
            }
        } catch (Exception ignored) {

        }
    }

    protected Size getPictureSzie() {
        if (characteristics == null || mPreviewSize == null) return null;
        StreamConfigurationMap configurationMap = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        Size[] outputSizes = configurationMap.getOutputSizes(ImageFormat.JPEG);
        Size size = getPreviewSize(outputSizes, textureView.getWidth(), textureView.getHeight());
        return size;
    }

    protected void captureStillPicture() {
        if (null == getCurrentCameraDevice()) {
            return;
        }
        if (mCameraDevice == null) return;
        // 创建拍照需要的CaptureRequest.Builder
        final CaptureRequest.Builder captureRequestBuilder;
        try {
            captureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            // 将imageReader的surface作为CaptureRequest.Builder的目标
            captureRequestBuilder.addTarget(mImageReader.getSurface());
            // 自动对焦
            captureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            // 自动曝光
            captureRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);

            // 根据设备方向计算设置照片的方向
            captureRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(getWindowRotation()));
            //拍照
            CaptureRequest mCaptureRequest = captureRequestBuilder.build();
            mCameraCaptureSession.stopRepeating();
            mCameraCaptureSession.capture(mCaptureRequest, null, getWorkHandler());
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    protected void restartPreview() {
        try {
            //执行setRepeatingRequest方法就行了，注意mCaptureRequest是之前开启预览设置的请求
            mCameraCaptureSession.setRepeatingRequest(preViewCaptureRequest, null, getWorkHandler());
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

}
