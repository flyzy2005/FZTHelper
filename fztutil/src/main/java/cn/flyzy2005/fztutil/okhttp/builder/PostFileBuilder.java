package cn.flyzy2005.fztutil.okhttp.builder;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.net.URLEncoder;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by Fly on 2017/5/18.
 */

public class PostFileBuilder {
    private MultipartBody.Builder builder;

    public PostFileBuilder() {
        this.builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
    }

    public PostFileBuilder addFile(String name, String fileName, File file) {
        RequestBody fileBody = RequestBody.create(MediaType.parse(guessMimeType(fileName)), file);
        builder.addFormDataPart(name, fileName, fileBody);
        return this;
    }

    public PostFileBuilder addParam(String name, String value) {
        builder.addFormDataPart(name, value);
        return this;
    }

    public RequestBody build() {
        return builder.build();
    }

    private String guessMimeType(String path) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentTypeFor = null;
        try {
            contentTypeFor = fileNameMap.getContentTypeFor(URLEncoder.encode(path, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (contentTypeFor == null) {
            contentTypeFor = "application/octet-stream";
        }
        return contentTypeFor;
    }
}
