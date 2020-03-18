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
        MVideoPlayerView myVideoPlayerView = findViewById(R.id.myVideoPlayerView);
        String url = "https://vdept.bdstatic.com/326e666a6b37587437504e61514e596c/" +
                "384e696732635249/9342b026175c74bfb337c430327c443e57c602e81021249d7f25" +
                "327444a5e5635fc59f89651a6134033a9d5e8a7c0348.mp4" +
                "?auth_key=1584178560-0-0-0960cbb63513f5e925e7fb7369fa629a";
        myVideoPlayerView.play("https://x.wuxoo4.com/mp4/f29fef11df2d42a3960d4ba0b553737f.mp4");
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }


}
