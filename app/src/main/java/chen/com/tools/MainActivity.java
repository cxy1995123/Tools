package chen.com.tools;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import chen.com.library.systembar.StatusBarCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
//        StatusBarCompat.setStatusBarColor(this, Color.parseColor("#FF9B00"));
        StatusBarCompat.translucentStatusBar(this,true);
    }

    public void send(View view) {
        ToastUtil.getInstance().show("213", this);
    }
}
