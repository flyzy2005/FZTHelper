集成了[AppUpdate](https://github.com/WVector/AppUpdate)，提供了对接口```HttpManager```的默认实现。
```
public void update(View view) {
        String url = "https://raw.githubusercontent.com/WVector/AppUpdateDemo/master/json/json.txt";
        new UpdateAppManager.Builder()
                .setActivity(this)
                .setUpdateUrl(url)
                .setHttpManager(new UpdateHttpManager())
                .build()
                .update();
    }
```
返回格式参考[返回格式](https://github.com/WVector/AppUpdate/blob/master/java.md)