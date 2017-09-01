package cn.flyzy2005.fztutil.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fly on 2017/5/22.
 */

public abstract class AbstractDao<T> implements IBaseDao<T> {
    private String tableName;
    private SQLiteDatabase database;

    @Override
    public T findById(Object id) {
        T t = null;
        if (null != id) {
            String sql = "SELECT * FROM " + getTableName() + " WHERE ID = ?";
            Cursor cursor = null;
            try {
                cursor = database.rawQuery(sql, new String[]{String.valueOf(id)});
                t = BeanUtils.getEntity(getTClass(), cursor);
            } finally {
                if (null != cursor)
                    cursor.close();
            }
        }
        return t;
    }

    @Override
    public List<T> findByParams(JSONObject condition) {
        Object[] params = ParamsTransfer.paramsAnalyseSQL(condition);
        List<T> tList = new ArrayList<>();
        if (params[0] != null && params[1] != null) {
            String sql = " SELECT * FROM " + getTableName() + " WHERE " + params[0];
            Cursor cursor = null;
            try {
                cursor = database.rawQuery(sql, (String[]) params[1]);
                tList = BeanUtils.getEntityList(getTClass(), cursor);
            } finally {
                if (null != cursor)
                    cursor.close();
            }
        }
        return tList;
    }

    @Override
    public List<T> findAll() {
        List<T> tList = new ArrayList<>();
        String sql = "SELECT * FROM " + getTableName();
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(sql, null);
            tList = BeanUtils.getEntityList(getTClass(), cursor);
        } finally {
            if (null != cursor)
                cursor.close();
        }
        return tList;
    }

    @Override
    public List<T> findBySql(String sql) {
        List<T> tList = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(sql, null);
            tList = BeanUtils.getEntityList(getTClass(), cursor);
        } finally {
            if (null != cursor)
                cursor.close();
        }
        return tList;
    }

    @Override
    public boolean insert(T model, boolean withId) {
        if (model != null) {
            JSONObject jModel = JSONObject.parseObject(JSON.toJSONString(model));
            ContentValues values = new ContentValues();
            for (String key : jModel.keySet()) {
                if (!withId && key.equals("id")) {
                    continue;
                }
                values.put(key, jModel.get(key) + "");
            }
            return database.insert(getTableName(), null, values) > 0;
        }
        return false;
    }

    @Override
    public boolean delete(T model) {
        if (null != model) {
            JSONObject jModel = JSONObject.parseObject(JSON.toJSONString(model));
            return deleteById(jModel.get("id"));
        }
        return false;
    }

    @Override
    public boolean deleteById(Object id) {
        return database.delete(getTableName(), "id=?", new String[]{id + ""}) > 0;
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
    public boolean update(T model) {
        if (model != null) {
            JSONObject jModel = JSONObject.parseObject(JSON.toJSONString(model));
            ContentValues values = new ContentValues();
            for (String key : jModel.keySet()) {
                if (key.equals("id")) {
                    continue;
                }
                values.put(key, jModel.get(key) + "");
            }
            return database.update(getTableName(), values, "id=?", new String[]{jModel.get("id") + ""}) > 0;
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
            JSONObject jModel = JSONObject.parseObject(JSON.toJSONString(model));
            ContentValues values = new ContentValues();
            for (String key : jModel.keySet()) {
                if (key.equals("id")) {
                    continue;
                }
                values.put(key, jModel.get(key) + "");
            }
            return database.update(getTableName(), values, params[0] + "", (String[]) params[1]) > 0;
        }
        return false;
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
