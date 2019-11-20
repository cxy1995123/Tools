package chen.com.tools;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

import java.util.List;

import chen.com.library.activity.permission.OnRequestPermissionResultListener;
import chen.com.library.activity.permission.PermissionRequest;
import chen.com.library.systembar.StatusBarCompat;
import chen.com.library.tools.JobServiceBuilder;
import chen.com.library.view.RootFrameLayout;
import chen.com.library.window.CustomizeBaseWindow;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        StatusBarCompat.translucentStatusBar(this, true);
        PermissionRequest.builder()
                .permission(Manifest.permission.RECEIVE_BOOT_COMPLETED)
                .requestCode(200)
                .proxyListener(new OnRequestPermissionResultListener() {
                    @Override
                    public void onPermissionGranted() {

                    }

                    @Override
                    public void onPermissionDenied(List<String> denied) {

                    }
                }).create()
                .request(this);
    }

    CustomizeBaseWindow window;

    public void send(View view2) {
        View view = LayoutInflater.from(this).inflate(R.layout.view_float, null);
        window = new CustomizeBaseWindow(this, view);
        window.show();
    }

    public void cancel(View view) {
        window.dismiss();
    }
}
