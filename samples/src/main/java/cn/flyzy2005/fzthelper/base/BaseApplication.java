package cn.flyzy2005.fzthelper.base;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import java.io.IOException;
import java.util.Properties;

import cn.flyzy2005.fzthelper.R;
import cn.flyzy2005.fzthelper.bean.User;
import cn.flyzy2005.fzthelper.db.SQLiteHelper;
import cn.flyzy2005.fztutil.util.ConfigHelper;

/**
 * Created by Fly on 2017/5/3.
 */

public class BaseApplication extends Application {
    private static BaseApplication instance;
    private SQLiteDatabase database;
    private SQLiteHelper sqLiteHelper;
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        initDatabase();
    }

    public static BaseApplication getInstance(){
        return instance;
    }

    public SQLiteDatabase getDatabase(){
        return database;
    }

    public void closeDatabase(){
        sqLiteHelper.closeDatabase();
    }

    public User getUserInfo(){
        User user = new User();
        user.setId(getProperty("user_id"));
        user.setPassword(getProperty("user_password"));
        user.setUsername(getProperty("user_username"));
        return user;
    }

    public void setUserInfo(final User user){
        ConfigHelper.getInstance(this).set(new Properties(){
            {
                setProperty("user_id", user.getId());
                setProperty("user_password", user.getPassword());
                setProperty("user_username", user.getUsername());
            }
        });
    }

    private String getProperty(String key){
        return ConfigHelper.getInstance(this).get(key);
    }

    private void initDatabase(){
        sqLiteHelper = new SQLiteHelper("test.db", "cn.flyzy2005.fzthelper", 1, R.raw.test, this);
        try {
            sqLiteHelper.openDatabase();
        } catch (IOException e) {
            //打开文件失败，文件不存在
            e.printStackTrace();
        }
        database = sqLiteHelper.getDatabase();
    }
}
