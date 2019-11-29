package chen.com.tools;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import java.util.List;

import chen.com.library.systembar.StatusBarCompat;
import chen.com.library.window.CustomizeBaseWindow;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        StatusBarCompat.translucentStatusBar(this, true);
    }

    CustomizeBaseWindow window;

    public void send(View view2) {
        startActivity(new Intent(this, ChatActivity.class));
    }

    public void cancel(View view) {
        window.dismiss();
    }

    public String getAppMetaData(String var0) {
        try {
            PackageManager var1 = getPackageManager();
            String var2 = getPackageName();
            ApplicationInfo applicationInfo;

            if ((applicationInfo = var1.getApplicationInfo(var2, PackageManager.GET_META_DATA)) != null
                    && applicationInfo.metaData != null && applicationInfo.metaData.containsKey(var0)) {
                return String.valueOf(applicationInfo.metaData.get(var0));
            }
        } catch (PackageManager.NameNotFoundException var3) {
            Log.e("AMS", "Meta data name " + var0 + " not found!");
        }

        return null;
    }
}
