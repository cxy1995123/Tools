package chen.com.library.activity.permission;

import java.util.List;

public interface OnRequestPermissionResultListener {

    void onPermissionGranted();

    void onPermissionDenied(List<String> denied);
}
