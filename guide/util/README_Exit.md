1. 实例化一个ExitHelper
2. 重写onKeyDown()
```
    private ExitHelper exitHelper;
    
    //....
    
    exitHelper = new ExitHelper(this);//初始化
    exitHelper.setBackMessage("退出咯");//设置单击退出时的提示语，也可以默认不设置
```

```
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return exitHelper.onKeyDown(keyCode, event);
    }
```