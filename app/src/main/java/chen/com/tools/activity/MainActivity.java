package chen.com.tools.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import chen.com.library.systembar.StatusBarCompat;

import chen.com.library.view.PolylineView;
import chen.com.tools.R;


public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        StatusBarCompat.translucentStatusBar(this, true);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);


    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.i("MainActivity", "onConfigurationChanged: " + newConfig.orientation);
    }


    public void openCameraActivity(View view) {
        List<PolylineView.PointF> points = new ArrayList<>();
        PolylineView.create(1,2);
        points.add(PolylineView.create(0,0));
        points.add(PolylineView.create(0.3f,1));
        points.add(PolylineView.create(1,1));
        points.add(PolylineView.create(2,2));
        points.add(PolylineView.create(2.1f,2.2f));
        points.add(PolylineView.create(2.4f,2.2f));
        points.add(PolylineView.create(2.6f,2.2f));
        points.add(PolylineView.create(9f,4f));
        PolylineView view1 = findViewById(R.id.polyView);
        view1.setPoints(points);
        view1.setXCalibrationMax(10);
        view1.setYCalibrationMax(4);
        view1.setXCalibrationInterval(2);
        view1.refresh();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }


}
