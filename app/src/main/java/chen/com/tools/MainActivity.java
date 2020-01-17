package chen.com.tools;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import chen.com.library.data.TimeDate;
import chen.com.library.systembar.StatusBarCompat;
import chen.com.library.wifi.client.WifiClient;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        StatusBarCompat.translucentStatusBar(this, true);

    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.i("MainActivity", "onConfigurationChanged: " + newConfig.orientation);



    }


    public void openCameraActivity(View view) {
        WifiClient client = WifiClient.getInstance(this);
        client.getCurrentWifiConfig();
        client.saveWifiStatus();
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
