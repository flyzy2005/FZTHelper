# 503 Android开发工具库

## 工具库目前集成功能
### 1. 数据库模块
[daoutils](https://github.com/Flyzy2005/daoutils)封装了一个轻量级的易上手的SQLite ORM框架，简化对数据库CRUD操作。
### 2. 网络请求模块
对[OkHttp](https://github.com/square/okhttp)的封装，重定义Callback将onFailure(),onResponse()里的事件写在主线程中，同时提供了onStart和onFinish方法，分别是在请求前以及请求结束后调用（UI线程）。

可以通过继承Callback实现对response的处理（重写parseResponse(Response)方法，该方法在子线程中进行）

封装了[OkHttp](https://github.com/square/okhttp)中提供的上传MultipartFile方法，可以与Spring中的MultipartFile[]结合使用。
### 3. 权限获取模块
对Android 6.0的动态权限获取进行了封装，提供多种回调方法（权限被拒，权限通过，自定义处理）。
### 4. 软件更新模块
集成了[AppUpdate](https://github.com/WVector/AppUpdate)，提供了对接口```HttpManager```的默认实现。
### 5. 自定义view
- 开机动画
- 懒加载Fragment
### 6. 常用工具类
- 双击退出
- 坐标装换（百度、火星、WGS84互转，墨卡托与经纬度互转）
- 配置文件设置（例如可以存储用户登录信息、手机型号信息等）
- ToastUtils
## 使用
[How to use?](https://github.com/Flyzy2005/FZTHelper/blob/master/GUIDE.md)
## 依赖

```
compile 'cn.flyzy2005:fztutil:1.1.0'
```
