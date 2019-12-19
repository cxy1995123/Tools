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

import chen.com.library.systembar.StatusBarCompat;

import static android.hardware.SensorManager.SENSOR_DELAY_NORMAL;

public class MainActivity extends AppCompatActivity {
    LightManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        StatusBarCompat.translucentStatusBar(this, true);
        manager = LightManager.create(this);
        manager.setListener(new LightManager.OnSensorChangedListener() {
            @Override
            public void OnSensorChanged(boolean isBelowStandard, float value) {
                Log.i("MainActivity", "OnSensorChanged: " + isBelowStandard + "," + value);
            }
        });

    }


    public void openCameraActivity(View view) {
        startActivity(new Intent(this, CameraActivity.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        manager.destory();
    }
}
