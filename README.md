#503快速搭建Android项目帮助工具

## 1.AbstractWelcomeActivity，通过继承它可以实现欢迎界面的设置
- 新建一个欢迎界面layout，重写getLayoutId()方法，返回该layout；
- 重写getListener，可以在onAnimationEnd()方法中写跳转事件，在onAnimationStart()方法中做一些预处理事件；
```
@Override
    protected Animation.AnimationListener getListener() {
        return new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                Toast.makeText(AppStart.this, "动画开始啦", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Toast.makeText(AppStart.this, "动画结束啦", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(AppStart.this, MainActivity.class);
                startActivity(intent);
                finish();//记得调用finish()
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        };
    }
```
- 提供继承方法setDuration，可以设置动画耗时，默认为800毫秒

## 2.ExitHelper，实例化该类，可以实现误点退出软件提示是否退出的功能
- 实例化一个ExitHelper类：
```
    private ExitHelper exitHelper;
    
    //....
    
    exitHelper = new ExitHelper(this);//初始化
    exitHelper.setBackMessage("退出咯");//设置单击退出时的提示语，也可以默认不设置
```
- 重写onKeyDown():
```
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return exitHelper.onKeyDown(keyCode, event);
    }
```