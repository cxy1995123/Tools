package chen.com.library.net.Okhttp.client;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import chen.com.library.net.Okhttp.Headers;
import chen.com.library.net.Okhttp.RequestType;
import chen.com.library.net.Okhttp.builder.Params;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import static chen.com.library.net.Okhttp.RequestType.FROM;
import static chen.com.library.net.Okhttp.RequestType.GET;
import static chen.com.library.net.Okhttp.RequestType.JSON;
import static chen.com.library.net.Okhttp.RequestType.MULTI_PART;

@SuppressLint("CheckResult")
public class RequestClient {
    private final static String TAG = "RequestClient";

    private int request_type = GET;
    private String url;
    private Params params;
    private Headers headers;
    private static final OkHttpClient client;

    public static OkHttpClient getClient() {
        return client;
    }

    static {
        client = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(@NonNull Chain chain) throws IOException {
                        Request.Builder request = chain.request().newBuilder();
                        Headers headers = new Headers();
                        headers.add("header1", "1");
                        headers.add("header2", "2");
                        headers.add("header3", "3");
                        Log.i(TAG, "RequestHeaders:" + headers.toString());
                        request.headers(okhttp3.Headers.of(headers));
                        return chain.proceed(request.build());
                    }
                })
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request();
                        Log.i(TAG, "RequestUrl:" + request.url());
                        Log.i(TAG, "RequestMethod:" + request.method());
                        return chain.proceed(request);
                    }
                })
                .writeTimeout(1, TimeUnit.MINUTES)
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(1, TimeUnit.MINUTES)
                .build();
        client.dispatcher().setMaxRequests(64);
        client.dispatcher().setMaxRequestsPerHost(5);
    }

    private RequestClient() {

    }

    public static RequestClient Build() {
        return new RequestClient();
    }

    public RequestClient json() {
        this.request_type = JSON;
        return this;
    }

    public RequestClient get() {
        this.request_type = GET;
        return this;
    }

    public RequestClient from() {
        this.request_type = FROM;
        return this;
    }

    public RequestClient multipart() {
        this.request_type = MULTI_PART;
        return this;
    }

    /**
     * {@link #get()} ()}
     * {@link #toFrom()}
     * {@link #toJson()}
     * {@link #multipartBody()}
     */
    @Deprecated
    public RequestClient type(@RequestType int type) {
        request_type = type;
        return this;
    }

    public RequestClient url(String url) {
        this.url = url;
        return this;
    }

    public RequestClient headers(Headers headers) {
        this.headers = headers;
        return this;
    }

    private Request.Builder addHeader() {
        Request.Builder builder = new Request.Builder();
        if (headers != null && !headers.isEmpty()) {
            builder.headers(okhttp3.Headers.of(headers));
        }
        return builder;
    }

    public RequestClient params(Params params) {
        this.params = params;
        return this;
    }

    private void addRequestBody(Request.Builder request) {
        switch (this.request_type) {
            case GET:
                request.get();
                break;
            case FROM:
                RequestBody formBody = toFrom();
                request.post(formBody);
                break;
            case JSON:
                RequestBody jsonBody = toJson();
                request.post(jsonBody);
                break;
            case MULTI_PART:
                MultipartBody body = multipartBody();
                request.post(body);
                break;
        }
    }

    /**
     * json
     */
    private RequestBody toJson() {
        String gson = new Gson().toJson(params);
        FormBody.create(MediaTypeCode.getTypeJson(), gson);
        return RequestBody.create(MediaTypeCode.getTypeJson(), gson);
    }

    /**
     * from
     **/
    private RequestBody toFrom() {
        FormBody.Builder builder = new FormBody.Builder();
        if (params == null || params.isEmpty()) {
            builder.add("", "");
        } else {
            Set<String> set = params.keySet();
            for (String key : set) {
                Object s = params.get(key);
                if (s instanceof Integer) {
                    builder.add(key, String.valueOf(s));
                } else if (s instanceof String) {
                    builder.add(key, (String) s);
                } else if (s instanceof Collection) {
                    builder.add(key, new Gson().toJson(s));
                }
            }
        }
        return builder.build();
    }

    public void newCall(Callback callBack) {
        Request.Builder builder = addHeader();
        builder.url(url);
        addRequestBody(builder);
        Request request = builder.build();
        Log.i(TAG, "url:" + url + "\n");
        Log.i(TAG, "method:" + request.method() + "\n");
        if (params != null) {
            Log.i(TAG, "Params:" + params.toString() + "\n");
        }
        client.newCall(request).enqueue(callBack);
    }

    /**
     * 混合表单
     */
    private MultipartBody multipartBody() {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        Set<String> set = params.keySet();
        for (String next : set) {
            Object s = params.get(next);
            if (s instanceof Integer) {
                builder.addPart(MultipartBody.Part.createFormData(next, null, RequestBody.create(MediaTypeCode.getTypeString(), String.valueOf(s))));
            } else if (s instanceof String) {
                builder.addPart(MultipartBody.Part.createFormData(next, null, RequestBody.create(MediaTypeCode.getTypeString(), (String) s)));
            } else if (s instanceof File) {
                builder.addPart(MultipartBody.Part.createFormData(next, ((File) s).getName(), RequestBody.create(MediaTypeCode.getTypeOctetStream(), (File) s)));
            }
        }
        return builder.build();
    }

    /**
     * 混合表单
     */
    private MultipartBody multipartBody2() {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        Set<String> set = params.keySet();
        for (String next : set) {
            Object s = params.get(next);
            if (s instanceof Integer) {
                builder.addFormDataPart(next, String.valueOf(s));
            } else if (s instanceof String) {
                builder.addFormDataPart(next, (String) s);
            } else if (s instanceof File) {
                RequestBody body = RequestBody.create(MediaTypeCode.getTypeOctetStream(), (File) s);
                builder.addFormDataPart(next, ((File) s).getName(), body);
            }
        }
        return builder.build();
    }
}
