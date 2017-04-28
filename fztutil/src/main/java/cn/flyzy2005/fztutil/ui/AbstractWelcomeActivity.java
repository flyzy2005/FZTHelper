package cn.flyzy2005.fztutil.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

/**
 * Created by Fly on 2017/4/28.
 */

public abstract class AbstractWelcomeActivity extends Activity {
    protected long durationTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final View view = View.inflate(this, getLayoutId(), null);
        setContentView(view);

        AlphaAnimation animation = new AlphaAnimation(0.3f, 1.0f);
        setDuration();
        animation.setDuration(getDuration());
        view.startAnimation(animation);
        animation.setAnimationListener(getListener());
    }

    /**
     * 获取欢迎界面的layout
     * @return 返回新建的欢迎界面的layoutId
     */
    protected abstract int getLayoutId();

    /**
     * 设置动画状态监听
     * @return 动画监听
     */
    protected abstract Animation.AnimationListener getListener();

    /**
     * 设置动画耗时
     */
    protected void setDuration(){
        durationTime = 800;
    }

    private long getDuration(){
        return durationTime;
    }
}
