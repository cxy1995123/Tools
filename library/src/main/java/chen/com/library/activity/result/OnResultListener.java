package chen.com.library.activity.result;

import android.app.Fragment;
import android.content.Intent;


public interface OnResultListener {

    void onCreate(Fragment fragment);

    void onDestroy();

    void onResult(int requestCode, int resultCode, Intent data);
}
