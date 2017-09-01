package cn.flyzy2005.fzthelper.dao;

import android.database.sqlite.SQLiteDatabase;

import cn.flyzy2005.fzthelper.base.BaseApplication;
import cn.flyzy2005.fzthelper.bean.Book;
import cn.flyzy2005.fztutil.database.AbstractDao;

/**
 * Created by Fly on 2017/5/22.
 */

public class BookDao extends AbstractDao<Book> {
    public BookDao() {
        setDatabase(BaseApplication.getInstance().getDatabase());
        setTableName("book");
    }

    public void myOpera(){
        SQLiteDatabase myDatabase =  getDatabase();
        //...do anything you want with SQLiteDatabase
    }
}
