package chen.com.tools;
import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import java.util.List;
import chen.com.library.activity.permission.OnRequestPermissionResultListener;
import chen.com.library.activity.permission.PermissionRequest;
import chen.com.library.activity.result.ActivityProxyListener;
import chen.com.library.activity.result.ActivityResultProxy;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void send(View view) {
        PermissionRequest
                .builder()
                .permission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .proxyListener(new OnRequestPermissionResultListener() {
                    @Override
                    public void onPermissionGranted() {
                        // do something
                    }

                    @Override
                    public void onPermissionDenied(List<String> denied) {
                        // do something
                    }
                })
                .requestCode(100)
                .create()
                .request(this);



    }
}
