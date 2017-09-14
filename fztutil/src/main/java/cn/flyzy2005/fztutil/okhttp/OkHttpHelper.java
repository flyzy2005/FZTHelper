package cn.flyzy2005.fztutil.okhttp;

import android.os.Handler;
import android.os.Looper;

import java.io.IOException;

import cn.flyzy2005.fztutil.okhttp.builder.PostBodyBuilder;
import cn.flyzy2005.fztutil.okhttp.builder.PostFileBuilder;
import cn.flyzy2005.fztutil.okhttp.callback.Callback;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Fly on 2017/5/17.
 * refer to https://github.com/hongyangAndroid/okhttputils
 */

public class OkHttpHelper {
    private OkHttpClient okHttpClient;
    private volatile static OkHttpHelper instance;

    private OkHttpHelper(OkHttpClient okHttpClient) {
        if (null == okHttpClient)
            this.okHttpClient = new OkHttpClient();
        else
            this.okHttpClient = okHttpClient;
    }

    public static OkHttpHelper initClient(OkHttpClient okHttpClient) {
        if (null == instance) {
            synchronized (OkHttpHelper.class) {
                if (null == instance)
                    instance = new OkHttpHelper(okHttpClient);
            }
        }
        return instance;
    }

    public static OkHttpHelper getInstance() {
        return initClient(null);
    }

    public OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }

    public <T> void execute(Request request, final Callback<T> callback) {
        if (callback != null)
            callback.onStart();
        okHttpClient.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (callback == null)
                    return;
                sendFailResultCallback(call, e, callback);

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (callback == null)
                    return;
                try {
                    if (call.isCanceled()) {
                        sendFailResultCallback(call, new Exception("call is canceled!"), callback);
                        return;
                    }
                    T t = callback.parseResponse(response);
                    sendSuccessResultCallback(call, t, callback);
                } catch (Exception e) {
                    sendFailResultCallback(call, e, callback);
                } finally {
                    if (response.body() != null)
                        response.body().close();
                }
            }
        });
    }

    public PostFileBuilder postMultipartFile() {
        return new PostFileBuilder();
    }

    public PostBodyBuilder postBody() {
        return new PostBodyBuilder();
    }

    private void sendFailResultCallback(final Call call, final Exception e, final Callback callback) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                callback.onFailure(call, e);
                callback.onFinish();
            }
        });
    }

    private <T> void sendSuccessResultCallback(final Call call, final T t, final Callback<T> callback) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                callback.onResponse(call, t);
                callback.onFinish();
            }
        });
    }

}
