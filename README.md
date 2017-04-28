#503快速搭建Android项目帮助工具

## 1.AbstractWelcomeActivity，通过继承它可以实现欢迎界面的设置
1. 新建一个欢迎界面layout，重写getLayoutId()方法，返回该layout；
2. 重写getListener，可以在onAnimationEnd()方法中写跳转事件，在onAnimationStart()方法中做一些预处理事件；
3. 提供继承方法setDuration，可以设置动画耗时，默认为800毫秒