package cn.flyzy2005.fztutil.database.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

import cn.flyzy2005.fztutil.database.ReflectUtils;


/**
 * Created by Fly on 2017/5/22.
 */

public abstract class AbstractDao<T> implements IBaseDao<T> {
    private String tableName;
    private SQLiteDatabase database;

    @Override
    public T getByPrimaryKey(Object primaryKey) {
        T t = null;
        if (null != primaryKey) {
            Class<T> clazz = getTClass();
            String keyColumn = ReflectUtils.getPrimaryKey(clazz, true);
            String sql = "SELECT * FROM " + getTableName() + " WHERE " + keyColumn + " = ?";
            Cursor cursor = null;
            try {
                cursor = database.rawQuery(sql, new String[]{String.valueOf(primaryKey)});
                t = ReflectUtils.getEntity(getTClass(), cursor);
            } finally {
                if (null != cursor)
                    cursor.close();
            }
        }
        return t;
    }

    @Override
    public T getByParams(JSONObject condition) {
        List<T> list = listByParams(condition);
        if (list.size() > 0)
            return list.get(0);
        return null;
    }

    @Override
    public List<T> listByParams(JSONObject condition) {
        Object[] params = ParamsTransfer.paramsAnalyseSQL(condition);
        List<T> tList = new ArrayList<>();
        if (params[0] != null && params[1] != null) {
            String sql = " SELECT * FROM " + getTableName() + " WHERE " + params[0];
            Cursor cursor = null;
            try {
                cursor = database.rawQuery(sql, (String[]) params[1]);
                tList = ReflectUtils.getEntityList(getTClass(), cursor);
            } finally {
                if (null != cursor)
                    cursor.close();
            }
        }
        return tList;
    }

    @Override
    public List<T> listAll() {
        List<T> tList = new ArrayList<>();
        String sql = "SELECT * FROM " + getTableName();
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(sql, null);
            tList = ReflectUtils.getEntityList(getTClass(), cursor);
        } finally {
            if (null != cursor)
                cursor.close();
        }
        return tList;
    }

    @Override
    public T getBySql(String sql) {
        List<T> list = listBySql(sql);
        if (list.size() > 0)
            return list.get(0);
        return null;
    }

    @Override
    public List<T> listBySql(String sql) {
        List<T> tList = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(sql, null);
            tList = ReflectUtils.getEntityList(getTClass(), cursor);
        } finally {
            if (null != cursor)
                cursor.close();
        }
        return tList;
    }

    @Override
    public boolean insert(T model, boolean withId) {
        if (model != null) {
            Class<T> tClass = getTClass();
            if (!withId)
                ReflectUtils.getPrimaryKey(tClass, false);
            ContentValues values = ReflectUtils.getContentValues(model, tClass, withId);
            return database.insert(getTableName(), null, values) > 0;
        }
        return false;
    }

    @Override
    public boolean delete(T model) {
        if (null != model) {
            Class<T> clazz = getTClass();
            String keyField = ReflectUtils.getPrimaryKey(clazz, false);
            JSONObject jModel = JSONObject.parseObject(JSON.toJSONString(model));
            return deleteByPrimaryKey(jModel.get(keyField));
        }
        return false;
    }

    @Override
    public boolean deleteByPrimaryKey(Object primaryKey) {
        Class<T> clazz = getTClass();
        String keyColumn = ReflectUtils.getPrimaryKey(clazz, true);
        return database.delete(getTableName(), keyColumn + "=?", new String[]{primaryKey + ""}) > 0;
    }

    @Override
    public boolean deleteByParams(JSONObject condition) {
        Object[] params = ParamsTransfer.paramsAnalyseSQL(condition);
        if (params[0] != null && params[1] != null) {
            return database.delete(getTableName(), params[0] + "", (String[]) params[1]) > 0;
        }
        return false;
    }

    @Override
    public boolean deleteAll() {
        return database.delete(getTableName(), null, null) > 0;
    }

    @Override
    public boolean update(T model) {
        if (model != null) {
            Class<T> tClass = getTClass();
            ContentValues contentValues = ReflectUtils.getContentValues(model, tClass, false);
            String keyField = ReflectUtils.getPrimaryKey(tClass, false);
            String keyColumn = ReflectUtils.getPrimaryKey(tClass, true);
            JSONObject jModel = JSONObject.parseObject(JSON.toJSONString(model));
            return database.update(getTableName(), contentValues, keyColumn + "=?", new String[]{jModel.get(keyField) + ""}) > 0;
        }
        return false;
    }

    @Override
    public boolean updateByParams(T model, JSONObject condition) {
        if (model != null) {
            Object[] params = ParamsTransfer.paramsAnalyseSQL(condition);
            if (params[0] == null || params[1] == null) {
                return false;
            }
            Class<T> tClass = getTClass();
            ContentValues values = ReflectUtils.getContentValues(model, tClass, false);
            return database.update(getTableName(), values, params[0] + "", (String[]) params[1]) > 0;
        }
        return false;
    }

    @Override
    public int count() {
        String sql = "SELECT COUNT(*) FROM " + getTableName();
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(sql, null);
            if (cursor.moveToNext())
                return cursor.getInt(0);
            return 0;
        } finally {
            if (null != cursor)
                cursor.close();
        }
    }

    @Override
    public int countByParams(JSONObject condition) {
        Object[] params = ParamsTransfer.paramsAnalyseSQL(condition);
        if (params[0] != null && params[1] != null) {
            String sql = " SELECT COUNT(*) FROM " + getTableName() + " WHERE " + params[0];
            Cursor cursor = null;
            try {
                cursor = database.rawQuery(sql, (String[]) params[1]);
                if (cursor.moveToNext())
                    return cursor.getInt(0);
                return 0;
            } finally {
                if (null != cursor)
                    cursor.close();
            }
        }
        return 0;
    }

    protected void setTableName(String tableName) {
        this.tableName = tableName;
    }

    protected void setDatabase(SQLiteDatabase database) {
        this.database = database;
    }

    protected SQLiteDatabase getDatabase() {
        return database;
    }

    @SuppressWarnings("unchecked")
    private Class<T> getTClass() {
        Class<T> tClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        return tClass;
    }

    private String getTableName() {
        return tableName;
    }

}
