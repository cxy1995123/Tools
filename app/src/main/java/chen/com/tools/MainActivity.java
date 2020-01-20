package chen.com.tools;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import chen.com.library.data.TimeDate;
import chen.com.library.systembar.StatusBarCompat;
import chen.com.library.wifi.client.WifiClient;
import chen.com.library.window.BasePopupView;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        StatusBarCompat.translucentStatusBar(this, true);
        findViewById(R.id.blocking).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("MainActivity", "onClick: ");
            }
        });
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.i("MainActivity", "onConfigurationChanged: " + newConfig.orientation);
    }


    public void openCameraActivity(View view) {
//        startActivity(new Intent(this, RecyclerViewActivity.class));
        TestFloatView view1 = new TestFloatView(this);
        view1.show(getWindow().getDecorView());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    public static void main(String[] args) {
        TimeDate date = TimeDate.getInstance(1660953600000L);
        System.out.println(date.getTime());
    }

}
