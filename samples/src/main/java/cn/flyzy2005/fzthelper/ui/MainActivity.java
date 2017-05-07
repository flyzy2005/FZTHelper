package cn.flyzy2005.fzthelper.ui;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import cn.flyzy2005.fzthelper.R;
import cn.flyzy2005.fzthelper.base.BaseApplication;
import cn.flyzy2005.fzthelper.bean.User;
import cn.flyzy2005.fztutil.func.FuncCall;
import cn.flyzy2005.fztutil.func.FuncRational;
import cn.flyzy2005.fztutil.func.FuncResult;
import cn.flyzy2005.fztutil.util.ExitHelper;
import cn.flyzy2005.fztutil.util.PermissionHelper;

/**
 * Created by Fly on 2017/5/2.
 */

public class MainActivity extends AppCompatActivity {
    private static int REQUEST_CODE_CAMERA = 101;
    private ExitHelper mExitHelper;
    private PermissionHelper.PermissionRequestObject mPermissionObject;
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
        user.setId(((EditText)findViewById(R.id.user_id)).getText().toString());
        user.setPassword(((EditText)findViewById(R.id.user_password)).getText().toString());
        user.setUsername(((EditText)findViewById(R.id.user_username)).getText().toString());
        BaseApplication.getInstance().setUserInfo(user);
    }

    public void getUser(View view) {
        User user = BaseApplication.getInstance().getUserInfo();
        ((EditText)findViewById(R.id.user_id)).setText(user.getId());
        ((EditText)findViewById(R.id.user_password)).setText(user.getPassword());
        ((EditText)findViewById(R.id.user_username)).setText(user.getUsername());
    }

    public void getPermission(View view) {
        mPermissionObject = PermissionHelper.with(MainActivity.this).request(Manifest.permission.CAMERA).onAllGranted(new FuncCall() {
            @Override
            public void call() {
                ((TextView)findViewById(R.id.permission_result)).setText("get!");
            }
        }).onAnyDenied(new FuncCall() {
            @Override
            public void call() {
                ((TextView)findViewById(R.id.permission_result)).setText("lose!");
            }
        }).onRational(new FuncRational() {
            @Override
            public void call(String permissionName) {
                //只有有拒绝的情况才会调用，并且会优先onAnyDenied，即onAnyDenied的方法不会得到回调
                ((TextView)findViewById(R.id.permission_result)).setText(permissionName + " lose");
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
                ((TextView)findViewById(R.id.permission_result)).setText("I do it by myself!");
            }
        }).ask(REQUEST_CODE_CAMERA);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mPermissionObject.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
