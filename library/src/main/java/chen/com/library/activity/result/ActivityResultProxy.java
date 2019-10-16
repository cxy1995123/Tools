package chen.com.library.activity.result;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;

public class ActivityResultProxy extends SimpleResult {

    public static final String TAG = "ResultManagerFragment";

    private ActivityProxyListener listener;
    private FragmentManager manager;
    private int requestCode = 0x0018;

    public static ActivityResultProxy create() {
        return new ActivityResultProxy();
    }

    public ActivityResultProxy requestCode(int requestCode) {
        this.requestCode = requestCode;
        return this;
    }

    public ActivityResultProxy proxyListener(ActivityProxyListener listener) {
        this.listener = listener;
        return this;
    }

    public void start(Activity activity) {
        FragmentManager manager = activity.getFragmentManager();
        this.manager = manager;
        ResultManagerFragment fragment = new ResultManagerFragment(this);
        manager.beginTransaction().add(fragment, TAG).commitAllowingStateLoss();
    }

    @Override
    public void onCreate(Fragment fragment) {
        super.onCreate(fragment);
        if (listener != null) {
            Intent intent = listener.constructIntent();
            fragment.startActivityForResult(intent, requestCode);
        }
    }

    @Override
    public void onResult(int requestCode, int resultCode, Intent data) {
        super.onResult(requestCode, resultCode, data);
        if (listener != null && requestCode == this.requestCode) {
            listener.onResult(resultCode, data);
        }
        onDestroy();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (manager!=null){
            Fragment fragment = manager.findFragmentByTag(TAG);
            if (fragment != null) {
                manager.beginTransaction().remove(fragment).commitAllowingStateLoss();
            }
            manager = null;
        }
    }
}
