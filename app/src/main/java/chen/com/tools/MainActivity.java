package chen.com.tools;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import chen.com.library.net.okHttp3.client.RequestClient;
import chen.com.library.systembar.StatusBarCompat;
import chen.com.tools.bt.Torrent;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;


public class MainActivity extends AppCompatActivity{
    LightManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        StatusBarCompat.translucentStatusBar(this, true);
        manager = LightManager.create(this);
        manager.setListener(new LightManager.OnSensorChangedListener(){
            @Override
            public void OnSensorChanged(boolean isBelowStandard, float value) {
                Log.i("MainActivity", "OnSensorChanged: " + isBelowStandard + "," + value);
            }
        });

    }


    public void openCameraActivity(View view) throws IOException {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        manager.destory();
    }
}
