package cn.flyzy2005.fzthelper.ui;

import android.Manifest;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.File;
import java.util.List;

import cn.flyzy2005.fzthelper.R;
import cn.flyzy2005.fzthelper.base.BaseApplication;
import cn.flyzy2005.fzthelper.bean.Book;
import cn.flyzy2005.fzthelper.bean.User;
import cn.flyzy2005.fzthelper.dao.BookDao;
import cn.flyzy2005.fztutil.okhttp.callback.FileCallback;
import cn.flyzy2005.fztutil.okhttp.callback.StringCallback;
import cn.flyzy2005.fztutil.permission.func.FuncCall;
import cn.flyzy2005.fztutil.permission.func.FuncRational;
import cn.flyzy2005.fztutil.permission.func.FuncResult;
import cn.flyzy2005.fztutil.utils.ExitHelper;
import cn.flyzy2005.fztutil.okhttp.OkHttpHelper;
import cn.flyzy2005.fztutil.permission.PermissionHelper;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;



/**
 * Created by Fly on 2017/5/2.
 */

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static int REQUEST_CODE_CAMERA = 101;
    private ExitHelper mExitHelper;
    private PermissionHelper.PermissionRequestObject mPermissionRequestObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mExitHelper = new ExitHelper(this);
        mExitHelper.setBackMessage("退出咯");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return mExitHelper.onKeyDown(keyCode, event);
    }

    public void setUser(View view) {
        User user = new User();
        user.setId(((EditText) findViewById(R.id.user_id)).getText().toString());
        user.setPassword(((EditText) findViewById(R.id.user_password)).getText().toString());
        user.setUsername(((EditText) findViewById(R.id.user_username)).getText().toString());
        BaseApplication.getInstance().setUserInfo(user);
    }

    public void getUser(View view) {
        User user = BaseApplication.getInstance().getUserInfo();
        ((EditText) findViewById(R.id.user_id)).setText(user.getId());
        ((EditText) findViewById(R.id.user_password)).setText(user.getPassword());
        ((EditText) findViewById(R.id.user_username)).setText(user.getUsername());
    }

    public void getPermission(View view) {
        mPermissionRequestObject = PermissionHelper.with(MainActivity.this).request(Manifest.permission.CAMERA).onAllGranted(new FuncCall() {
            @Override
            public void call() {
                ((TextView) findViewById(R.id.permission_result)).setText("get!");
            }
        }).onAnyDenied(new FuncCall() {
            @Override
            public void call() {
                ((TextView) findViewById(R.id.permission_result)).setText("lose!");
            }
        }).onRational(new FuncRational() {
            @Override
            public void call(String permissionName) {
                //只有有拒绝的情况才会调用，并且会优先onAnyDenied，即onAnyDenied的方法不会得到回调
                ((TextView) findViewById(R.id.permission_result)).setText(permissionName + " lose");
            }
        }).onResult(new FuncResult() {
            @Override
            /**
             * @param requestCode ask()的REQUEST_CODE_CAMERA
             * @param permissions 动态获取的权限
             * @param grantResults 是否允许 PackageManager.PERMISSION_GRANTED
             */
            public void call(int requestCode, String[] permissions, int[] grantResults) {
                //自己写代码处理，onAllGranted，onAnyDenied，onRational里定义的代码都不会得到回调
                ((TextView) findViewById(R.id.permission_result)).setText("I do it by myself!");
            }
        }).ask(REQUEST_CODE_CAMERA);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mPermissionRequestObject.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void getHtml(View view) {
        String url = "http://www.391k.com/api/xapi.ashx/info.json?key=bd_hyrzjjfb4modhj&size=10&page=1";

        Request request = new Request.Builder()
                .url(url)
                .build();

        OkHttpHelper.getInstance().execute(request, new StringCallback() {
            @Override
            public void onFailure(Call call, Exception e) {
                ((TextView) findViewById(R.id.html_result)).setText(e.getMessage());
            }

            @Override
            public void onResponse(Call call, String s) {
                ((TextView) findViewById(R.id.html_result)).setText(s);
            }
        });
    }

    public void getFile(View view) {
        mPermissionRequestObject = PermissionHelper.with(this).request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .onAllGranted(new FuncCall() {
                    @Override
                    public void call() {
                        String url = "https://github.com/Flyzy2005/FZTHelper/blob/master/pictures/UML_PermissionHelper.png?raw=true%20PermisionHelper";

                        Request request = new Request.Builder()
                                .url(url)
                                .build();
                        OkHttpHelper.getInstance().execute(request, new FileCallback(Environment.getExternalStorageDirectory() + File.separator + "FZTHelper", "test.png") {
                            @Override
                            public void onFailure(Call call, Exception e) {
                                ((TextView) findViewById(R.id.file_result)).setText(e.getMessage());
                            }

                            @Override
                            public void onResponse(Call call, File file) {
                                ((TextView) findViewById(R.id.file_result)).setText("OK");
                            }
                        });
                    }
                }).ask(101);
    }

    public void upFile(View view) {
        mPermissionRequestObject = PermissionHelper.with(this).request(Manifest.permission.WRITE_EXTERNAL_STORAGE).onAllGranted(new FuncCall() {
            @Override
            public void call() {
                String url = "http://192.168.1.111:28080/GeoDisaster/UploadData/DangerPoint";
                File file = new File(Environment.getExternalStorageDirectory() + File.separator + "FZTHelper"+ File.separator + "test.png");
                RequestBody body = OkHttpHelper.getInstance()
                        .postMultipartFile()
                        .addParam("pointString", "111point")
                        .addParam("mediaString", "111media")
                        .addFile("imgFile", "test.png", file)
                        .build();
                Request request = new Request.Builder().
                        url(url).post(body).build();
                OkHttpHelper.getInstance().execute(request, new StringCallback() {
                    @Override
                    public void onFailure(Call call, Exception e) {
                        ((TextView) findViewById(R.id.up_file_result)).setText(e.getMessage());
                    }

                    @Override
                    public void onResponse(Call call, String s) {
                        ((TextView) findViewById(R.id.up_file_result)).setText("OK");
                    }
                });
            }
        }).ask(102);
    }

    public void post(View view) {
        String url = "http://192.168.1.111:28080/GeoDisaster/SyncData";
        FormBody formBody=new FormBody.Builder()
                .add("countyName", "111county")
                .add("townName", "111town")
                .add("code", "111code")
                .add("typ", "1")
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();
        OkHttpHelper.getInstance().execute(request, new StringCallback() {
            @Override
            public void onFailure(Call call, Exception e) {
                ((TextView) findViewById(R.id.post_result)).setText(e.getMessage());
            }

            @Override
            public void onResponse(Call call, String s) {
                ((TextView) findViewById(R.id.post_result)).setText(s);
            }
        });
    }

    public void query(View view) {
        BookDao bookDao = new BookDao();
        Book bookInsert = new Book();
        bookInsert.setId(1);//并不会用到
        bookInsert.setPublisher("whu1");
        bookInsert.setName("心灵鸡汤1");
        bookInsert.setAuthor("fly1");
        if(bookDao.insert(bookInsert, false)){
            Log.i(TAG, "insert: " + "插入成功，id采用自增模式");
        }
        bookInsert.setId(6);//会用这个作为id插入到表中
        bookInsert.setPublisher("whu2");
        bookInsert.setAuthor("fly2");
        bookInsert.setName("心灵鸡汤2");
        if(bookDao.insert(bookInsert, true)){
            Log.i(TAG, "insert: " + "插入成功， id为设置的id");
        }

        Book bookFind = bookDao.findById(1);
        Log.i(TAG, "find: " + "根据id找到book：" + JSON.toJSONString(bookFind));

        List<Book> bookList1 = bookDao.findAll();
        Log.i(TAG, "find: " + "查询出所有book：" + JSON.toJSONString(bookList1));

        JSONObject condition = new JSONObject();
        condition.put("author", "fly");
        condition.put("publisher", "whu");
        List<Book> bookList2 = bookDao.findByParams(condition);
        Log.i(TAG, "find: " + "根据条件查询出所有book：" + JSON.toJSONString(bookList2));

        String sql = "select * from book where author = 'fly'";
        List<Book> bookList3 = bookDao.findBySql(sql);
        Log.i(TAG, "find: " + "根据sql语句查询出所有book：" + JSON.toJSONString(bookList3));

        if(bookDao.deleteById(1)){
            Log.i(TAG, "delete: " + "根据id删除成功，成功删除id为1的book");
        }

        Book bookDelete = new Book();
        bookDelete.setId(2);
        if(bookDao.delete(bookDelete)){
            Log.i(TAG, "delete: " + "根据model删除成功，成功删除实体bookDelete，实质是删除id为2的book");
        }

        Book bookModify = new Book();
        bookModify.setId(3);
        bookModify.setAuthor("flyModify");
        bookModify.setName("心灵鸡汤Modify");
        bookModify.setPublisher("whuModify");
        if(bookDao.update(bookModify)){
            Log.i(TAG, "update: " + "成功修改id为" + bookModify.getId() + "的书籍，书籍信息修改为：" + JSON.toJSONString(bookModify));
        }

        condition = new JSONObject();
        condition.put("author", "fly1");
        condition.put("publisher", "whu1");
        if(bookDao.updateByParams(bookModify, condition)){
            Log.i(TAG, "update: " + "成功修改满足条件" + condition + "的书籍，书籍信息修改为：" + JSON.toJSONString(bookModify));
        }

        condition = new JSONObject();
        condition.put("author", "flyModify");
        condition.put("publisher", "whuModify");
        if(bookDao.deleteByParams(condition)){
            Log.i(TAG, "delete: " + "成功删除满足条件" + condition + "的书籍");
        }

    }
}
