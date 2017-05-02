package cn.flyzy2005.fzthelper.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;

import cn.flyzy2005.fzthelper.R;
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
}
