package chen.com.tools;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {

    private static ToastUtil toastUtil;
    private Toast toast;

    static {
        toastUtil = new ToastUtil();
    }

    private ToastUtil() {

    }

    public static ToastUtil getInstance() {
        return toastUtil;
    }

    public void show(String m, Context context) {

        toast = getInstance().toast;
        if (toast == null) {
            toast = Toast.makeText(context, m, Toast.LENGTH_SHORT);
            toast.show();
        } else {
            toast.cancel();
            toast.setText(m);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}
