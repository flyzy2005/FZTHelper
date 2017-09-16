package cn.flyzy2005.fzthelper.db;

import android.content.Context;

import cn.flyzy2005.daoutils.AbstractSQLiteManger;


/**
 * Created by Fly on 2017/5/22.
 */

public class SQLiteHelper extends AbstractSQLiteManger {
    /**
     * 构造函数
     *
     * @param databaseName    保存的数据库文件名，如test.db
     * @param packageName     工程包名，如cn.flyzy2005.fzthelper，在工程的build.gradle文件可以看到
     * @param databaseVersion 当前的数据库版本
     * @param databaseRawId   需要写入的database文件所对应的R.raw的id，如R.id.test（把test.db文件拷贝到res下的raw下面即可）
     * @param context         ApplicationContext
     */
    public SQLiteHelper(String databaseName, String packageName, int databaseVersion, int databaseRawId, Context context) {
        super(databaseName, packageName, databaseVersion, databaseRawId, context);
    }

    @Override
    protected void updateDatabase(int oldVersion, int newVersion) {
        if(oldVersion >= newVersion)
            return;

        //根据数据库版本依次更新
        for(int i = oldVersion; i < newVersion; ++i){
            switch (i){
                case 0:
                    //更新操作包括4个步骤
                    //1.将所有表重命名成temp表 String TEMP_TABLE = "ALTER TABLE \"routeline\" RENAME TO \"_temp_routeline\"";
                    //2.建立一个新表
                    //String NEW_TABLE = "CREATE TABLE \"routeline\" (\n" +
                    //"\"ID\"  INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,\n" +
                    //        "\"userName\"  TEXT,\n" +
                    //        "\"RouteLine\"  BLOB NOT NULL,\n" +
                    //        "\"beginDate\"  TEXT NOT NULL,\n" +
                    //        "\"stopDate\"  TEXT NOT NULL,\n" +
                    //        "\"upload\"  INTEGER NOT NULL DEFAULT 0\n" +
                    //        ")";
                    //3.转移数据 String INSERT_DATA = "INSERT INTO \"routeline\" (\"ID\", \"userName\", \"RouteLine\", \"beginDate\", \"stopDate\") SELECT \"ID\", \"userName\", \"RouteLine\", \"beginDate\", \"stopDate\" FROM \"_temp_routeline\"";
                    //4.删除temp表 String DROP_TEMP = "drop table _temp_routeline";
                    //依次调用getDatabase().execSQL(sql)即可
                    break;
                case 1:

                    break;
                default:
                    break;
            }
        }
    }
}
