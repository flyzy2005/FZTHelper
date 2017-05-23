package cn.flyzy2005.fztutil.exception;

/**
 * Created by Fly on 2017/5/22.
 */

public class EntityInstantiateException extends RuntimeException{

    public EntityInstantiateException(Class<?> clazz, String message){
        super("实例化" + clazz.getName() + "失败，" + message);
    }
    public EntityInstantiateException(Class<?> clazz, String message, Throwable cause){
        super("实例化" + clazz.getName() + "失败，" + message, cause);
    }
}
