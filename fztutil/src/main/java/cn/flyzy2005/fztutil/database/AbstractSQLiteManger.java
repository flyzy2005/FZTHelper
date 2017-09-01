package cn.flyzy2005.fztutil.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Fly on 2017/5/22.
 */
public abstract class AbstractSQLiteManger {
    /**
     * 当前的数据库版本
     */
    private int databaseVersion;
    /**
     * 需要写入的database文件所对应的R.raw的id，如R.id.test（把test.db文件拷贝到res下的raw下面即可）
     */
    private int databaseRawId;
    /**
     * 数据库在手机中的位置（/data/data/cn.flyzy2005.fzthelper/test.db）
     */
    private String databasePath;


    private SQLiteDatabase database;
    private Context context;

    /**
     * 构造函数
     *
     * @param databaseName    保存的数据库文件名，如test.db
     * @param packageName     工程包名，如cn.flyzy2005.fzthelper，在工程的build.gradle文件可以看到
     * @param databaseVersion 当前的数据库版本
     * @param databaseRawId   需要写入的database文件所对应的R.raw的id，如R.id.test（把test.db文件拷贝到res下的raw下面即可）
     * @param context         ApplicationContext
     */
    public AbstractSQLiteManger(String databaseName, String packageName, int databaseVersion, int databaseRawId, Context context) {
        this.databaseVersion = databaseVersion;
        this.databaseRawId = databaseRawId;
        this.context = context;

        this.databasePath = "/data" + Environment.getDataDirectory().getAbsolutePath() +
                File.separator + packageName + File.separator + databaseName;
    }

    public void openDatabase() throws IOException {
        try {
            this.database = this.openDatabase(databasePath);
        } catch (IOException exception) {
            throw new IOException("写入db文件出错，请检查路径是否正确");
        }
        updateDatabase(database.getVersion(), databaseVersion);
    }

    public SQLiteDatabase getDatabase() {
        return database;
    }

    public void closeDatabase() {
        if (null != database)
            database.close();
    }

    abstract protected void updateDatabase(int oldVersion, int newVersion);

    private SQLiteDatabase openDatabase(String filePath) throws IOException {
        InputStream inputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            if (!(new File(filePath).exists())) {
                inputStream = context.getResources().openRawResource(databaseRawId);

                fileOutputStream = new FileOutputStream(filePath);
                int buffer_size = 400000;
                byte[] buffer = new byte[buffer_size];
                int count;
                while ((count = inputStream.read(buffer)) > 0) {
                    fileOutputStream.write(buffer, 0, count);
                }
                SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(filePath, null);
                database.setVersion(databaseVersion);
                return database;
            }
            return SQLiteDatabase.openOrCreateDatabase(filePath, null);
        } finally {
            if (null != fileOutputStream)
                fileOutputStream.close();
            if (null != inputStream)
                inputStream.close();
        }
    }
}
