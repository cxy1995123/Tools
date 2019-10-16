package chen.com.library.activity.permission;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;

@SuppressLint("ValidFragment")
public class PermissionFragment extends Fragment {
    private PermissionRequest request;

    public PermissionFragment(PermissionRequest request) {
        this.request = request;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 23) {
            if (!checkSelfPermission(request.permission)) {
                requestPermissions(request.permission, request.requestCode);
            } else {
                request.hasPermission(this);
            }
        }else {
            request.hasPermission(this);
        }

    }

    private boolean checkSelfPermission(String... permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getTargetSdkVersion(getContext()) >= 23) {
                for (String p : permission) {
                    if (ContextCompat.checkSelfPermission(getContext(), p) != PackageManager.PERMISSION_GRANTED) {
                        return false;
                    }
                }
            } else {
                for (String p : permission) {
                    if (PermissionChecker.checkSelfPermission(getContext(), p) != PermissionChecker.PERMISSION_GRANTED) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private int getTargetSdkVersion(Context context) {
        try {
            final PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            return info.applicationInfo.targetSdkVersion;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 23;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        request.onResult(this, requestCode, permissions, grantResults);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        request.onDestroy(this);
    }
}
