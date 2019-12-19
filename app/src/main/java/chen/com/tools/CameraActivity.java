package chen.com.tools;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageAnalysisConfig;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureConfig;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.lifecycle.LifecycleOwner;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Rational;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import chen.com.library.activity.permission.OnRequestPermissionResultListener;
import chen.com.library.activity.permission.PermissionRequest;
import chen.com.library.google.camera.AspectRatio;
import chen.com.library.google.camera.CameraView;
import chen.com.library.google.camera.Constants;
import chen.com.library.google.camera.Size;
import chen.com.library.google.camera.SizeMap;
import chen.com.library.systembar.StatusBarCompat;

import static chen.com.library.google.camera.Constants.FLASH_AUTO;

public class CameraActivity extends AppCompatActivity implements OnRequestPermissionResultListener {

    private CameraView cameraView;

    public static void main(String[] args) {
        float target = 4.0f / 3.0f;
        Set<AspectRatio> ratios = new TreeSet<>();
        ratios.add(AspectRatio.parse("4:3"));
        ratios.add(AspectRatio.parse("1:1"));
        ratios.add(AspectRatio.parse("2:1"));
        ratios.add(AspectRatio.parse("16:9"));
        ratios.add(AspectRatio.parse("20:15"));
        ratios.add(AspectRatio.parse("30:17"));
        ratios.add(AspectRatio.parse("10:6"));
        ratios.add(AspectRatio.parse("12:7"));
        Iterator<AspectRatio> iterator = ratios.iterator();
        float offset = 99999;
        AspectRatio currentAspectRatio = null;
        for (; iterator.hasNext(); ) {
            AspectRatio next = iterator.next();
            float aFloat = next.toFloat();

            float newOffset = Math.abs(aFloat - target);
            if (newOffset < offset) {
                currentAspectRatio = next;
                offset = newOffset;
            }
            System.out.println(aFloat + "," + next + "," + newOffset);
        }
        System.out.println(currentAspectRatio);
    }

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