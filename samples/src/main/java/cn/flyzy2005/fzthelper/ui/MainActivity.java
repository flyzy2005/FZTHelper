package cn.flyzy2005.fzthelper.ui;

import android.Manifest;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;

import cn.flyzy2005.fzthelper.R;
import cn.flyzy2005.fzthelper.base.BaseApplication;
import cn.flyzy2005.fzthelper.bean.User;
import cn.flyzy2005.fztutil.callback.FileCallback;
import cn.flyzy2005.fztutil.callback.StringCallback;
import cn.flyzy2005.fztutil.func.FuncCall;
import cn.flyzy2005.fztutil.func.FuncRational;
import cn.flyzy2005.fztutil.func.FuncResult;
import cn.flyzy2005.fztutil.util.ExitHelper;
import cn.flyzy2005.fztutil.util.OkHttpHelper;
import cn.flyzy2005.fztutil.util.PermissionHelper;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by Fly on 2017/5/2.
 */

public class MainActivity extends AppCompatActivity {
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
}
