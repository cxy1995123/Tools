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

import chen.com.library.systembar.StatusBarCompat;

import chen.com.library.videoPlayer.MVideoPlayerView;
import chen.com.tools.R;


public class MainActivity extends AppCompatActivity{


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

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }


}
