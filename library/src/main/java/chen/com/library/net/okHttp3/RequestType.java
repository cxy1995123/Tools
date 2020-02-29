package chen.com.library.net.okHttp3;


import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static chen.com.library.net.okHttp3.RequestType.FROM;
import static chen.com.library.net.okHttp3.RequestType.GET;
import static chen.com.library.net.okHttp3.RequestType.JSON;
import static chen.com.library.net.okHttp3.RequestType.MULTI_PART;


@IntDef(value = {GET, JSON, FROM, MULTI_PART})
@Retention(RetentionPolicy.SOURCE)
public @interface RequestType {
    int GET = 1, JSON = 2, FROM = 3, MULTI_PART = 4;
}
