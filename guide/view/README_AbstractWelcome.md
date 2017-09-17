1. 设计一个欢迎界面的布局，新建一个Activity继承AbstractWelcomeActivity，重写getLayoutId()，返回该布局的id
2. 重写getListener，可以在onAnimationEnd()方法中写跳转事件，在onAnimationStart()方法中做一些预处理事件；

```
public class AppStart extends AbstractWelcomeActivity {
    @Override
    protected int getLayoutId() {
        return R.layout.activity_start;
    }

    @Override
    protected Animation.AnimationListener getListener() {
        return new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                Toast.makeText(AppStart.this, "动画开始啦", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAnimationEnd(Animation animation) {
//                Toast.makeText(AppStart.this, "动画结束啦", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(AppStart.this, MainActivity.class);
                startActivity(intent);
                finish();//记得调用finish()
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        };
    }
}
```