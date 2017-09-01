package cn.flyzy2005.fztutil.okhttp.callback;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Fly on 2017/5/18.
 */
public abstract class Callback<T> {
    public void onStart() {
    }

    public abstract void onFailure(Call call, Exception e);

    /**
     * 非UI线程，其他都是在UI线程中
     */
    public abstract T parseResponse(Response response) throws IOException;

    public abstract void onResponse(Call call, T t);

    public void onFinish() {
    }
}

