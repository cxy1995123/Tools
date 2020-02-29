package chen.com.library.activity.permission;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;

import java.util.ArrayList;
import java.util.List;

public class PermissionRequest {
    private static final String TAG = "PermissionFragment";
    String[] permission;
    int requestCode;
    private Activity activity;
    OnRequestPermissionResultListener listener;

    public static Builder builder() {
        return new Builder();
    }

    public void request(Activity activity) {
        if (permission != null && permission.length > 0) {
            this.activity = activity;
            FragmentTransaction beginTransaction = activity.getFragmentManager().beginTransaction();
            beginTransaction.add(new PermissionFragment(this), TAG);
            beginTransaction.commitAllowingStateLoss();
        } else {
            hasPermission(null);
        }
    }

    void hasPermission(Fragment fragment) {
        if (listener != null) {
            listener.onPermissionGranted();
        }
        if (activity != null && fragment != null) {
            activity.getFragmentManager().beginTransaction().remove(fragment).commitAllowingStateLoss();
        }
        onDestroy(fragment);
    }

    void onResult(Fragment fragment, int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == this.requestCode) {
            List<String> denied = new ArrayList<>(5);
            int length = grantResults.length;
            for (int i = 0; i < length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    denied.add(permissions[i]);
                }
            }

            if (denied.isEmpty()) {
                if (listener != null) {
                    listener.onPermissionGranted();
                }
            } else {
                if (listener != null) {
                    listener.onPermissionDenied(denied);
                }
            }

            if (activity != null && !activity.isFinishing()) {
                activity.getFragmentManager().beginTransaction().remove(fragment).commitAllowingStateLoss();
            }
            onDestroy(fragment);
        }
    }

    void onDestroy(Fragment fragment) {
        if (activity != null) {
            activity = null;
        }

        if (listener != null) {
            listener = null;
        }

        if (permission != null) {
            permission = null;
        }
    }


    public static boolean checkSelfPermission(Context context, String... permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getTargetSdkVersion(context) >= 23) {
                for (String p : permission) {
                    if (ContextCompat.checkSelfPermission(context, p) != PackageManager.PERMISSION_GRANTED) {
                        return false;
                    }
                }
            } else {
                for (String p : permission) {
                    if (PermissionChecker.checkSelfPermission(context, p) != PermissionChecker.PERMISSION_GRANTED) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private static int getTargetSdkVersion(Context context) {
        try {
            final PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            return info.applicationInfo.targetSdkVersion;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 23;
    }

}
