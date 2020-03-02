package chen.com.tools;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.os.Bundle;
import android.view.View;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import chen.com.library.activity.permission.OnRequestPermissionResultListener;
import chen.com.library.activity.permission.PermissionRequest;
import chen.com.library.google.camera.AspectRatio;
import chen.com.library.google.camera.CameraView;
import chen.com.library.systembar.StatusBarCompat;
import chen.com.library.window.BasePopupView;

public class CameraActivity extends AppCompatActivity implements OnRequestPermissionResultListener {

    private CameraView cameraView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        StatusBarCompat.translucentStatusBar(this, true);
        cameraView = findViewById(R.id.cameraView);
        cameraView.setAutoFocus(true);
        cameraView.setFlash(CameraView.FLASH_AUTO);
        cameraView.addCallback(new CameraView.Callback() {
            @Override
            public void onCameraOpened(CameraView cameraView) {
                super.onCameraOpened(cameraView);
            }

            @Override
            public void onCameraClosed(CameraView cameraView) {
                super.onCameraClosed(cameraView);
            }

            @Override
            public void onPictureTaken(CameraView cameraView, byte[] data) {
                super.onPictureTaken(cameraView, data);
            }
        });

        PermissionRequest.builder().permission(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .proxyListener(this)
                .create()
                .request(this);
    }


    public void takePhoto(View view) {
        cameraView.takePicture();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraView.stop();
    }

    @Override
    public void onPermissionGranted() {
        cameraView.start();
    }

    @Override
    public void onPermissionDenied(List<String> denied) {

    }
}
