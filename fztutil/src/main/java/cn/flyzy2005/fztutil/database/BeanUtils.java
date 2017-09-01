package cn.flyzy2005.fztutil.database;

import android.database.Cursor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import cn.flyzy2005.fztutil.exception.EntityInstantiateException;

/**
 * Created by Fly on 2017/5/22.
 */

public class BeanUtils {
    public static <T> List<T> getEntityList(Class<T> clazz, Cursor cursor) throws EntityInstantiateException {
        List<T> tList = new ArrayList<>();
        Method[] methods = clazz.getMethods();
        String[] columnNames = cursor.getColumnNames();
        int columnCount = columnNames.length;
        while (cursor.moveToNext()) {
            T t = instantiateEntity(clazz);
            mapEntity(t, clazz, columnNames, columnCount, methods, cursor);
            tList.add(t);
        }
        return tList;
    }

    public static <T> T getEntity(Class<T> clazz, Cursor cursor) throws EntityInstantiateException {
        T t = null;
        if (cursor.moveToNext()) {
            t = instantiateEntity(clazz);
            Method[] methods = t.getClass().getMethods();
            String[] columnNames = cursor.getColumnNames();
            int columnCount = columnNames.length;
            mapEntity(t, clazz, columnNames, columnCount, methods, cursor);
        }
        return t;
    }

    private static <T> T instantiateEntity(Class<T> clazz) throws EntityInstantiateException {
        if (clazz.isInterface())
            throw new EntityInstantiateException(clazz, "不能实例化接口");
        try {
            return clazz.newInstance();
        } catch (InstantiationException e) {
            throw new EntityInstantiateException(clazz, "实例化抽象类？", e);
        } catch (IllegalAccessException e) {
            throw new EntityInstantiateException(clazz, "构造函数可获取吗？", e);
        }
    }

    private static <T> void mapEntity(T t, Class<T> clazz, String[] columnNames, int columnCount, Method[] methods, Cursor cursor) throws EntityInstantiateException {
        for (int i = 0; i < columnCount; ++i) {
            String methodName = "set" + columnNames[i];
            for (Method method : methods) {
                if (methodName.equalsIgnoreCase(method.getName())) {
                    try {
                        Object value;
                        switch (method.getParameterTypes()[0].getName()) {
                            case "int":
                            case "java.lang.Integer":
                                value = cursor.getInt(i);
                                break;
                            case "long":
                            case "java.lang.Long":
                                value = cursor.getLong(i);
                                break;
                            case "boolean":
                                value = cursor.getInt(i) == 1;
                                break;
                            case "java.lang.String":
                                value = cursor.getString(i);
                                break;
                            default:
                                throw new EntityInstantiateException(clazz, "暂不支持这个类型的映射：" + method.getParameterTypes()[0].getName());
                        }

                        method.invoke(t, value);
                    } catch (IllegalAccessException e) {
                        throw new EntityInstantiateException(clazz, "set方法是public吗？", e);
                    } catch (InvocationTargetException e) {
                        throw new EntityInstantiateException(clazz, "set方法抛出了异常，没有处理？", e);
                    }
                }
            }
        }
    }
}
