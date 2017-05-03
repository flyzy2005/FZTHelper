package cn.flyzy2005.fztutil.util;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

/**
 * Created by Fly on 2017/5/3.
 */

public class ConfigHelper {
    private static final String APP_CONFIG = "config";
    private static volatile ConfigHelper instance = null;
    private Context mContext;
    private ConfigHelper(Context context){
        this.mContext = context;
    }

    /**
     * Double-Check
     * 避免每次访问都需要同步，因此不直接使用同步方法
     * 加volatile关键字是因为Java内存模型允许“无序写入”，可能存在线程1到达步骤1时，instance变成非null，但是还
     * 没有完成初始化，此时线程2调用getInstance()方法，直接返回了一个还会初始化的对象，造成系统崩溃
     * @return
     */
    public static ConfigHelper getInstance(Context context){
        if(null == instance){
            synchronized (ConfigHelper.class){
                if(null == instance){
                    instance = new ConfigHelper(context);//1
                }
            }
        }
        return instance;
    }

    public String get(String key) {
        Properties props = getProps();
        return (props != null) ? props.getProperty(key) : null;
    }

    public void set(Properties ps) {
        Properties props = getProps();
        props.putAll(ps);
        setProps(props);
    }

    public void set(String key, String value) {
        Properties props = getProps();
        props.setProperty(key, value);
        setProps(props);
    }

    public void remove(String... key) {
        Properties props = getProps();
        for (String k : key)
            props.remove(k);
        setProps(props);
    }

    private Properties getProps() {
        FileInputStream fis = null;
        Properties props = new Properties();
        try {
            // 读取app_config目录下的config
            File dirConf = mContext.getDir(APP_CONFIG, Context.MODE_PRIVATE);
            fis = new FileInputStream(dirConf.getPath() + File.separator
                    + APP_CONFIG);

            props.load(fis);
        } catch (Exception e) {
        } finally {
            try {
                fis.close();
            } catch (Exception e) {
            }
        }
        return props;
    }

    private void setProps(Properties props){
        FileOutputStream fos = null;
        try {
            // 把config建在(自定义)app_config的目录下
            File dirConf = mContext.getDir(APP_CONFIG, Context.MODE_PRIVATE);
            File conf = new File(dirConf, APP_CONFIG);
            fos = new FileOutputStream(conf);

            props.store(fos, null);
            fos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (Exception e) {
            }
        }
    }
}
