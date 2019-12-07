package chen.com.tools;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.List;

import chen.com.library.activity.permission.OnRequestPermissionResultListener;
import chen.com.library.activity.permission.PermissionRequest;
import chen.com.library.systembar.StatusBarCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        StatusBarCompat.translucentStatusBar(this, true);

    }

    public void send(View view) {
        PermissionRequest.builder()
                .permission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .requestCode(9)
                .proxyListener(new OnRequestPermissionResultListener() {
                    @Override
                    public void onPermissionGranted() {
                        Log.i("MainActivity", "onPermissionGranted: ");
                    }

                    @Override
                    public void onPermissionDenied(List<String> denied) {
                        Log.i("MainActivity", "onPermissionDenied: " + denied.toString());
                    }
                }).request(this);

    }
}
