package cn.flyzy2005.fzthelper.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import cn.flyzy2005.fzthelper.R;
import cn.flyzy2005.fzthelper.base.BaseApplication;
import cn.flyzy2005.fzthelper.bean.User;
import cn.flyzy2005.fztutil.util.ExitHelper;

/**
 * Created by Fly on 2017/5/2.
 */

public class MainActivity extends Activity {
    private ExitHelper exitHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        exitHelper = new ExitHelper(this);
        exitHelper.setBackMessage("退出咯");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return exitHelper.onKeyDown(keyCode, event);
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
}
