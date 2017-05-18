#503快速搭建Android项目帮助工具

## 1.AbstractWelcomeActivity，欢迎界面基类，通过继承它可以实现欢迎界面的设置
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

## 2.ExitHelper，退出帮助类，可以实现误点退出软件提示是否退出的功能
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
## 3.ConfigHelper，配置信息帮助类，单例模式，无需实例化，直接调用方法即可。可以存储一些信息到配置文件中，例如用户登录信息等等（配置文件路径在/data/data/cn.flyzy2005.fzthelper/app_config）
- 在实现Application的类中封装保存以及获取配置信息的方法：
```
    public User getUserInfo(){
        User user = new User();
        user.setId(getProperty("user_id"));
        user.setPassword(getProperty("user_password"));
        user.setUsername(getProperty("user_username"));
        return user;
    }

    public void setUserInfo(final User user){
        ConfigHelper.getInstance(this).set(new Properties(){
            {
                setProperty("user_id", user.getId());
                setProperty("user_password", user.getPassword());
                setProperty("user_username", user.getUsername());
            }
        });
    }

    private String getProperty(String key){
        return ConfigHelper.getInstance(this).get(key);
    }
```
- 通过BaseApplication在任何地方获取到配置文件中的内容：
```
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
```
## 4.PermissionHelper，Android6.0动态权限获取。修改自：[PermissionUtil][1]
![此处输入图片的描述][2]

> Android
> 6.0之前，权限在应用安装过程中只询问一次，以列表的形式展现给用户，然而大多数用户并不会注意到这些，直接就下一步了，应用安装成功后就会被赋予清单文件中的所有权限，应用就可以在用户不知情的情况下进行非法操作（比如偷偷的上传用户数据）。
> 
> Android
> 6.0版本中运行时权限的出现解决了这一问题，一些高危权限会在应用的运行过程中动态申请，这样用户就可以选择是否允许，比如一个单机游戏要获取通讯录权限，那肯定要禁止了。
> 
> 并不是所有的权限都需要动态申请，需要申请的权限如下表所示： 
> 
> 注意：同一组内的任何一个权限被授权了，其他权限也自动被授权。例如，一旦READ_CALENDAR被授权了，应用也有WRITE_CALENDAR权限了。

Permission Group| Permissions
---|---
CALENDAR | READ_CALENDAR, WRITE_CALENDAR
CAMERA|CAMERA
CONTACTS|READ_CONTACTS,WRITE_CONTACTS,GET_ACCOUNTS
LOCATION|ACCESS_FINE_LOCATION,ACCESS_COARSE_LOCATION
MICROPHONE|RECORD_AUDIO
PHONE|READ_PHONE_STATE,CALL_PHONE,READ_CALL_LOG,WRITE_CALL_LOG,ADD_VOICEMAIL,USE_SIP,PROCESS_OUTGOING_CALLS
SENSORS|BODY_SENSORS
SMS|SEND_SMS,RECEIVE_SMS,READ_SMS,RECEIVE_WAP_PUSH,RECEIVE_MMS
STORAGE|READ_EXTERNAL_STORAG，WRITE_EXTERNAL_STORAGE
  
  

- 在Activity，Fragment，AppCompatActivity中任何可能会用到运行时权限的地方，实例化一个PermissionHelper.PermissionRequestObject：
```
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
```
- 重写onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)方法：
```
@Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mPermissionObject.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
```
## 5.OkHttpHelper，对[Okhttp3][3]的封装。重定义Callback将onFailure(),onResponse()里的事件写在主线程中，可以直接更新UI界面。可以通过继承Callback实现对response的处理（重写parseResponse(Response)方法，该方法在子线程中进行，因此不会造成界面卡顿）。封装了[Okhttp3][3]中提供的上传MultipartFile方法，可以与SpringMVC中的MultipartFile[]结合使用。
- get方法：
```
String url = "http://www.391k.com/api/xapi.ashx/info.json?key=bd_hyrzjjfb4modhj&size=10&page=1";

Request request = new Request.Builder()
        .url(url)
        .build();

OkHttpHelper.getInstance().execute(request, new StringCallback() {
    @Override
    public void onFailure(Call call, Exception e) {
        ((TextView) findViewById(R.id.html_result)).setText(e.getMessage());
    }

    @Override
    public void onResponse(Call call, String s) {
        ((TextView) findViewById(R.id.html_result)).setText(s);
    }
 });
```
- 下载文件：
```
String url = "https://github.com/Flyzy2005/FZTHelper/blob/master/pictures/UML_PermissionHelper.png?raw=true%20PermisionHelper";

Request request = new Request.Builder()
         .url(url)
         .build();
OkHttpHelper.getInstance().execute(request, new FileCallback(Environment.getExternalStorageDirectory() + File.separator + "FZTHelper", "test.png") {
        @Override
        public void onFailure(Call call, Exception e) {
             ((TextView) findViewById(R.id.file_result)).setText(e.getMessage());
        }

        @Override
        public void onResponse(Call call, File file) {
            ((TextView) findViewById(R.id.file_result)).setText("OK");
        }
 });                        
```
- 上传文件：
```
File file = new File(Environment.getExternalStorageDirectory() + File.separator + "FZTHelper"+ File.separator + "test.png");
RequestBody body = OkHttpHelper.getInstance()
        .postMultipartFile()
        .addParam("pointString", "111point")
        .addParam("mediaString", "111media")
        .addFile("imgFile", "test.png", file)
        .build();
Request request = new Request.Builder().
        url(url).post(body).build();
OkHttpHelper.getInstance().execute(request, new StringCallback() {
        @Override
        public void onFailure(Call call, Exception e) {
            ((TextView) findViewById(R.id.up_file_result)).setText(e.getMessage());
        }

        @Override
        public void onResponse(Call call, String s) {
            ((TextView) findViewById(R.id.up_file_result)).setText("OK");
        }
});
```
- post请求：
```
        String url = "http://192.168.1.111:28080/GeoDisaster/SyncData";
        FormBody formBody=new FormBody.Builder()
                .add("countyName", "111county")
                .add("townName", "111town")
                .add("code", "111code")
                .add("typ", "1")
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();
```
1. 提供cn.flyzy2005.fztutil.callback.Callback抽象类，可以通过继承它来实现对response的处理，目前工具里已提供StringCallback和FileCallback，分别提供将response转成string和将response转成file的功能。
2. okHttpClient采用了默认的设置，如果对超时等设置有特殊需求，可以通过OkHttpHelper.getInstance().getOkHttpClient()方法获得okHttpClient对象后自行设置。
3. 如果所提供的功能不满足需求，同样可以获取okHttpClient对象自行处理。
  
  [1]: https://github.com/kayvannj/PermissionUtil
  [2]: https://github.com/Flyzy2005/FZTHelper/blob/master/pictures/UML_PermissionHelper.png?raw=true%20PermisionHelper
  [3]: https://github.com/square/okhttp%20Okhttp3