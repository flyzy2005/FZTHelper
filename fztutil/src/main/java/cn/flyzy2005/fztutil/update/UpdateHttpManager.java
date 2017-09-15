package cn.flyzy2005.fztutil.update;

import android.support.annotation.NonNull;

import com.vector.update_app.HttpManager;

import java.io.File;
import java.util.Map;

import cn.flyzy2005.fztutil.okhttp.OkHttpHelper;
import cn.flyzy2005.fztutil.okhttp.callback.StringCallback;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.Request;

/**
 * Created by Fly on 2017/9/14.
 */

public class UpdateHttpManager implements HttpManager {
    @Override
    public void asyncGet(@NonNull String url, @NonNull Map<String, String> params, @NonNull final Callback callBack) {
        Request request = new Request.Builder()
                .url(url)
                .build();
        OkHttpHelper.getInstance().execute(request, new StringCallback() {
            @Override
            public void onFailure(Call call, Exception e) {
                callBack.onError(e.getMessage());
            }

            @Override
            public void onResponse(Call call, String s) {
                callBack.onResponse(s);
            }
        });
    }

    @Override
    public void asyncPost(@NonNull String url, @NonNull Map<String, String> params, @NonNull final Callback callBack) {
        FormBody formBody = new FormBody.Builder().build();
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();
        OkHttpHelper.getInstance().execute(request, new StringCallback() {
            @Override
            public void onFailure(Call call, Exception e) {
                callBack.onError(e.getMessage());
            }

            @Override
            public void onResponse(Call call, String s) {
                callBack.onResponse(s);
            }
        });
    }

    @Override
    public void download(@NonNull String url, @NonNull String path, @NonNull String fileName, @NonNull final FileCallback callback) {
        Request request = new Request.Builder()
                .url(url)
                .build();
        OkHttpHelper.getInstance().execute(request, new cn.flyzy2005.fztutil.okhttp.callback.FileCallback(path, fileName) {
            @Override
            public void onStart() {
                callback.onBefore();
            }

            @Override
            public void onFailure(Call call, Exception e) {
                callback.onError(e.getMessage());
            }

            @Override
            public void onResponse(Call call, File file) {
                callback.onResponse(file);
            }

            @Override
            public void inProgress(float progress, long total) {
                callback.onProgress(progress, total);
            }
        });
    }
}
