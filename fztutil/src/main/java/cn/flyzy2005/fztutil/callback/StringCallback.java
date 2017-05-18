package cn.flyzy2005.fztutil.callback;

import java.io.IOException;

import okhttp3.Response;

/**
 * Created by Fly on 2017/5/18.
 */

public abstract class StringCallback extends Callback<String> {
    @Override
    public String parseResponse(Response response) throws IOException {
        return response.body().string();
    }
}
