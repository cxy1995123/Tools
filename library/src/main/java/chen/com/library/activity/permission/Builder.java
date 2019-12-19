package chen.com.library.activity.permission;

import android.app.Activity;

public class Builder {
    private String[] permission;
    private int requestCode = 0x0014;
    private OnRequestPermissionResultListener listener;

    public Builder permission(String... permission) {
        this.permission = permission;
        return this;
    }


    public int getRequestCode() {
        return requestCode;
    }

    public Builder requestCode(int requestCode) {
        this.requestCode = requestCode;
        return this;
    }

    public Builder proxyListener(OnRequestPermissionResultListener listener) {
        this.listener = listener;
        return this;
    }

    public PermissionRequest create() {
        PermissionRequest request = new PermissionRequest();
        request.requestCode = requestCode;
        request.permission = permission;
        request.listener = listener;
        return request;
    }

    public void request(Activity activity) {
        PermissionRequest request = new PermissionRequest();
        request.requestCode = requestCode;
        request.permission = permission;
        request.listener = listener;
        request.request(activity);
    }


}
