package chen.com.library.activity.result;

import android.content.Intent;

public interface ActivityProxyListener {

    Intent constructIntent();

    void onResult(int resultCode, Intent data);
}
