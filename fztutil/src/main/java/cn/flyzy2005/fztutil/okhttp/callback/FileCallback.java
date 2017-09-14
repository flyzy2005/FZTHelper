package cn.flyzy2005.fztutil.okhttp.callback;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Response;

/**
 * Created by Fly on 2017/5/18.
 */

public abstract class FileCallback extends Callback<File> {

    private String filePath;
    private String fileName;
    private float progress;
    private long total;
    private Handler handler;

    public FileCallback(String filePath, String fileName) {
        this.fileName = fileName;
        this.filePath = filePath;
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                inProgress(progress, total);
            }
        };
    }

    @Override
    public File parseResponse(Response response) throws IOException {
        return saveFile(response);
    }

    private File saveFile(Response response) throws IOException {
        InputStream is = null;
        byte[] buf = new byte[2048];
        int len;
        FileOutputStream fos = null;
        try {
            total = response.body().contentLength();
            long sum = 0;
            is = response.body().byteStream();
            File dir = new File(filePath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(dir, fileName);
            fos = new FileOutputStream(file);
            while ((len = is.read(buf)) != -1) {
                sum += len;
                fos.write(buf, 0, len);
                progress = sum * 1.0f /total;
                handler.sendEmptyMessage(0);
            }
            fos.flush();
            return file;
        } finally {
            response.body().close();
            if (is != null)
                is.close();
            if (fos != null)
                fos.close();
        }
    }
}
