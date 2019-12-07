package chen.com.library.net.Okhttp;


import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static chen.com.library.net.Okhttp.RequestType.FROM;
import static chen.com.library.net.Okhttp.RequestType.GET;
import static chen.com.library.net.Okhttp.RequestType.JSON;
import static chen.com.library.net.Okhttp.RequestType.MULTI_PART;


@IntDef(value = {GET, JSON, FROM, MULTI_PART})
@Retention(RetentionPolicy.SOURCE)
public @interface RequestType {
    int GET = 1, JSON = 2, FROM = 3, MULTI_PART = 4;
}
