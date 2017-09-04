package cn.flyzy2005.fztutil.database;

import android.content.ContentValues;
import android.database.Cursor;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.flyzy2005.fztutil.database.anno.ColumnAlias;
import cn.flyzy2005.fztutil.database.anno.Ignore;
import cn.flyzy2005.fztutil.database.anno.PrimaryKey;
import cn.flyzy2005.fztutil.exception.EntityInstantiateException;

/**
 * Created by Fly on 2017/5/22.
 */

public class ReflectUtils {
    public static <T> List<T> getEntityList(Class<T> clazz, Cursor cursor) throws EntityInstantiateException {
        List<T> tList = new ArrayList<>();
        while (cursor.moveToNext()) {
            T t = instantiateEntity(clazz);
            mapEntity(t, clazz, cursor);
            tList.add(t);
        }
        return tList;
    }

    public static <T> T getEntity(Class<T> clazz, Cursor cursor) throws EntityInstantiateException {
        T t = null;
        if (cursor.moveToNext()) {
            t = instantiateEntity(clazz);
            mapEntity(t, clazz, cursor);
        }
        return t;
    }

    public static <T> String getPrimaryKey(Class<T> tClass, boolean column) {
        List<Field> fields = getAllFields(tClass);
        for (Field field : fields) {
            if (field.isAnnotationPresent(PrimaryKey.class)) {
                if (field.isAnnotationPresent(ColumnAlias.class) && column)
                    return field.getAnnotation(ColumnAlias.class).columnName();
                return field.getName();
            }
        }
        throw new EntityInstantiateException(tClass, "没有设置主键");
    }

    public static <T> ContentValues getContentValues(T entity, Class<T> tClass, boolean withId) {
        ContentValues values = new ContentValues();
        List<Field> fields = getAllFields(tClass);
        for (Field field : fields) {
            if (!withId && field.isAnnotationPresent(PrimaryKey.class))
                continue;

            if (!field.isAccessible())
                field.setAccessible(true);

            String strColName = getColumnName(field);

            try {
                if (field.getGenericType().toString().equals("class java.lang.String")) {
                    values.put(strColName, (String) (field.get(entity)));
                } else if (field.getGenericType().toString().equals("class java.lang.Boolean")
                        || field.getGenericType().toString().equals("boolean")) {
                    values.put(strColName, (((Boolean) (field.get(entity))) ? 1 : 0));
                } else if (field.getGenericType().toString().equals("class java.lang.Byte")
                        || field.getGenericType().toString().equals("byte")) {
                    values.put(strColName, (Byte) field.get(entity));
                } else if (field.getGenericType().toString().equals("class [B")) {
                    values.put(strColName, (byte[]) field.get(entity));
                } else if (field.getGenericType().toString().equals("class java.lang.Double")
                        || field.getGenericType().toString().equals("double")) {
                    values.put(strColName, (Double) field.get(entity));
                } else if (field.getGenericType().toString().equals("class java.lang.Float")
                        || field.getGenericType().toString().equals("float")) {
                    values.put(strColName, (Float) field.get(entity));
                } else if (field.getGenericType().toString().equals("class java.lang.Integer")
                        || field.getGenericType().toString().equals("int")) {
                    values.put(strColName, (Integer) field.get(entity));
                } else if (field.getGenericType().toString().equals("class java.lang.Long")
                        || field.getGenericType().toString().equals("long")) {
                    values.put(strColName, (Long) field.get(entity));
                } else if (field.getGenericType().toString().equals("class java.lang.Short")
                        || field.getGenericType().toString().equals("short")) {
                    values.put(strColName, (Short) field.get(entity));
                } else {
                    throw new EntityInstantiateException(tClass, "不支持该类型字段的映射" + field.getGenericType().toString());
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return values;
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

    private static <T> void mapEntity(T t, Class<T> clazz, Cursor cursor) throws EntityInstantiateException {
        List<Field> fields = getAllFields(clazz);
        for (Field field : fields) {
            String fieldName = field.getName();
            String columnName = fieldName;
            if (field.isAnnotationPresent(ColumnAlias.class)) {
                ColumnAlias alias = field.getAnnotation(ColumnAlias.class);
                columnName = alias.columnName();
            }
            int columnIndex = cursor.getColumnIndex(columnName);
            if (columnIndex == -1)
                continue;

            int columnType = cursor.getType(columnIndex);
            try {
                switch (columnType) {
                    case Cursor.FIELD_TYPE_NULL:
                        continue;
                    case Cursor.FIELD_TYPE_FLOAT:
                        clazz.getMethod("set" + upperFirstLetter(fieldName), field.getType())
                                .invoke(t, cursor.getFloat(columnIndex));
                        break;
                    case Cursor.FIELD_TYPE_BLOB:
                        clazz.getMethod("set" + upperFirstLetter(fieldName), field.getType())
                                .invoke(t, cursor.getBlob(columnIndex));
                        break;
                    case Cursor.FIELD_TYPE_INTEGER:
                        switch (field.getType().toString()) {
                            case "int":
                            case "class java.lang.Integer":
                                clazz.getMethod("set" + upperFirstLetter(fieldName), field.getType())
                                        .invoke(t, cursor.getInt(columnIndex));
                                break;
                            case "long":
                            case "class java.lang.Long":
                                clazz.getMethod("set" + upperFirstLetter(fieldName), field.getType())
                                        .invoke(t, (long) cursor.getInt(columnIndex));
                                break;
                            case "short":
                            case "class java.lang.Short":
                                clazz.getMethod("set" + upperFirstLetter(fieldName), field.getType())
                                        .invoke(t, (short) cursor.getInt(columnIndex));
                                break;
                            case "byte":
                            case "class java.lang.Byte":
                                clazz.getMethod("set" + upperFirstLetter(fieldName), field.getType())
                                        .invoke(t, (byte) cursor.getInt(columnIndex));
                                break;
                        }
                        break;
                    case Cursor.FIELD_TYPE_STRING:
                        clazz.getMethod("set" + upperFirstLetter(fieldName), field.getType())
                                .invoke(t, cursor.getString(columnIndex));
                        break;
                    default:
                        throw new EntityInstantiateException(clazz, "列" + columnName + "类型错误");
                }
            } catch (Exception e) {
                throw new EntityInstantiateException(clazz, e.getMessage(), e);
            }
        }
    }

    private static String getColumnName(Field field) {
        if (field.isAnnotationPresent(ColumnAlias.class))
            return field.getAnnotation(ColumnAlias.class).columnName();
        return field.getName();
    }

    private static String upperFirstLetter(String columnName) {
        StringBuilder builder = new StringBuilder(columnName);
        String firstLetter = columnName.substring(0, 1).toUpperCase();
        builder.replace(0, 1, firstLetter);
        return builder.toString();
    }

    private static List<Field> getAllFields(Class clazz) {
        if (clazz == null)
            return null;

        List<Field> all = new ArrayList<>();
        Field[] fields = clazz.getDeclaredFields();
        Collections.addAll(all, fields);

        for (clazz = clazz.getSuperclass(); clazz != Object.class; clazz = clazz.getSuperclass()) {
            Field[] fieldsSuper = clazz.getDeclaredFields();
            Collections.addAll(all, fieldsSuper);
        }

        List<Field> result = new ArrayList<>();
        for (Field field : all) {
            /**忽略编译产生的属性**/
            if (field.isSynthetic())
                continue;

            /**忽略serialVersionUID**/
            if (field.getName().equals("serialVersionUID"))
                continue;

            if (field.isAnnotationPresent(Ignore.class))
                continue;

            if (!result.contains(field))
                result.add(field);
        }
        return result;
    }
}
