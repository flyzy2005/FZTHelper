package cn.flyzy2005.fztutil.okhttp.builder;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by Fly on 2017/9/14.
 */

public class PostBodyBuilder {
    private static MediaType MEDIA_TYPE_PLAIN = MediaType.parse("text/plain;charset=utf-8");
    private String content;

    public PostBodyBuilder content(String content) {
        this.content = content;
        return this;
    }

    public RequestBody build() {
        return RequestBody.create(MEDIA_TYPE_PLAIN, content);
    }
}
