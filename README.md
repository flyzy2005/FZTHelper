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
### 注意：即使是运行时权限，也需要在AndroidManifest.xml中声明的，不然这个会不起效果。
## 5.OkHttpHelper，对[Okhttp3][3]的封装。重定义Callback将onFailure(),onResponse()里的事件写在主线程中，可以直接更新UI界面。同时提供了onStart和onFinish方法，分别是在请求前以及请求结束后调用（UI线程中）。自己可以通过继承Callback实现对response的处理（重写parseResponse(Response)方法，该方法在子线程中进行，因此不会造成界面卡顿）。封装了[Okhttp3][3]中提供的上传MultipartFile方法（觉得本身的实现实在有点繁琐。。就做了一点封装），可以与SpringMVC中的MultipartFile[]结合使用。
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
## 6.对数据库操作进行了一些封装，包括导入数据库以及封装了CRUD操作。
- 导入数据库，这里主要是可以通过Navicat等软件生成的db文件直接写入到手机中去，当然你也可以自己写create方法，动态创建表。继承AbstractSQLiteManger，在Application类中实例化即可：

```
public class SQLiteHelper extends AbstractSQLiteManger {
    /**
     * 构造函数
     *
     * @param databaseName    保存的数据库文件名，如test.db
     * @param packageName     工程包名，如cn.flyzy2005.fzthelper，在工程的build.gradle文件可以看到
     * @param databaseVersion 当前的数据库版本
     * @param databaseRawId   需要写入的database文件所对应的R.raw的id，如R.id.test（把test.db文件拷贝到res下的raw下面即可）
     * @param context         ApplicationContext
     */
    public SQLiteHelper(String databaseName, String packageName, int databaseVersion, int databaseRawId, Context context) {
        super(databaseName, packageName, databaseVersion, databaseRawId, context);
    }

    @Override
    protected void updateDatabase(int oldVersion, int newVersion) {
        if(oldVersion >= newVersion)
            return;

        //根据数据库版本依次更新
        for(int i = oldVersion; i < newVersion; ++i){
            switch (i){
                case 0:
                    //更新操作包括4个步骤（这些语句都可以通过Navicat直接生成）
                    //1.将所有表重命名成temp表 String TEMP_TABLE = "ALTER TABLE \"routeline\" RENAME TO \"_temp_routeline\"";
                    //2.建立一个新表
                    //String NEW_TABLE = "CREATE TABLE \"routeline\" (\n" +
                    //"\"ID\"  INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,\n" +
                    //        "\"userName\"  TEXT,\n" +
                    //        "\"RouteLine\"  BLOB NOT NULL,\n" +
                    //        "\"beginDate\"  TEXT NOT NULL,\n" +
                    //        "\"stopDate\"  TEXT NOT NULL,\n" +
                    //        "\"upload\"  INTEGER NOT NULL DEFAULT 0\n" +
                    //        ")";
                    //3.转移数据 String INSERT_DATA = "INSERT INTO \"routeline\" (\"ID\", \"userName\", \"RouteLine\", \"beginDate\", \"stopDate\") SELECT \"ID\", \"userName\", \"RouteLine\", \"beginDate\", \"stopDate\" FROM \"_temp_routeline\"";
                    //4.删除temp表 String DROP_TEMP = "drop table _temp_routeline";
                    //依次调用getDatabase().execSQL(sql)即可
                    break;
                case 1:

                    break;
                default:
                    break;
            }
        }
    }
}

//在Application中调用
    //获得SQLiteDatabase
    public SQLiteDatabase getDatabase(){
        return database;
    }
    //关闭SQLiteDatabase
    public void closeDatabase(){
        sqLiteHelper.closeDatabase();
    }
    //初始化
    private void initDatabase(){
        sqLiteHelper = new SQLiteHelper("test.db", "cn.flyzy2005.fzthelper", 1, R.raw.test, this);
        try {
            sqLiteHelper.openDatabase();
        } catch (IOException e) {
            //打开文件失败，文件不存在
            e.printStackTrace();
        }
        database = sqLiteHelper.getDatabase();
    }
```

- 对CRUD进行了一些封装，对查询结果进行了映射，可以直接转成Entity类，主要封装的函数包括：

```
/**
 * Created by Fly on 2017/5/22.
 * <p>
 *     很关键的一个字段就是<b>id</b>，并且判断就是通过id来判断的，并不会识别其他如bookId，carId
 *     在{@link AbstractDao}中对这些方法进行了封装，代码并不复杂，可以稍微理解一下最终效果再使用
 *     暂时插入和更新只支持String和int类型，查找自动映射只支持long、String、boolean，详见{@link cn.flyzy2005.fztutil.database.ReflectUtils}
 *     如果功能不够，可以通过{@code getDatabase()}获得SQLiteDatabase自己写具体方法
 * </p>
 */

interface IBaseDao<T> {
    /**
     * 根据id进行查询
     * @param id id
     * @return entity实例
     */
    T findById(Object id);

    /**
     *根据条件条件进行查询
     * @param condition 查询条件{"name":"fly"}(封装在JSONObject中)
     * @return 所有满足条件的entity实例
     */
    List<T> findByParams(JSONObject condition);

    /**
     * 查询表中所有数据
     * @return 所有entity实例
     */
    List<T> findAll();

    /**
     * 根据sql语句进行查询
     * @param sql sql语句
     * @return 满足查询条件的entity实例
     */
    List<T> findBySql(String sql);

    /**
     * 添加一条记录
     * @param model entity实例
     * @param withId 是否需要插入Id，不插入的话就采用SQLite自带的自增策略
     * @return 是否插入成功
     */
    boolean insert(T model, boolean withId);

    /**
     * 根据Id删除一条记录，依然是使用entity的id属性来进行删除操作，并不会用到其他字段
     * @param model entity实例
     * @return 是否删除成功
     */
    boolean delete(T model);

    /**
     * 根据Id删除一条记录
     * @param id Id
     * @return 是否删除成功
     */
    boolean deleteById(Object id);

    /**
     * 根据条件删除一条记录
     * @param condition 删除条件
     * @return 是否删除成功
     */
    boolean deleteByParams(JSONObject condition);

    /**
     * 修改一条记录，会根据传入的entity的id进行匹配修改，并且会用除id的所有其他属性的新值更新数据库
     * @param model 修改的entity实例
     * @return 是否修改成功
     */
    boolean update(T model);

    /**
     * 根据条件修改一条记录，并且会用除id的所有其他属性的新值更新数据库
     * @param model 修改的entity实例
     * @param condition 条件
     * @return 是否修改成功
     */
    boolean updateByParams(T model, JSONObject condition);
}
```
各个方法的介绍都有详细的注释，实现的功能&&缺陷都写明了，各个方法的实现都在AbstracDao进行了实现，因此对于一个数据库表，要做的事包括两件：1:定义一个与之对应的entity类，属性名称与表的列名相一致，2：建一个Dao类，实现AbstracDao<T>:

```
//refer to table book
public class Book {
    private int id;
    private String name;
    private String author;
    private String publisher;
    
    
    //省略set get方法
}

//实现AbstractDao<T>，将相应的entity类作为类型参数传进去
//需要提供一个构造函数，在构造函数里对database以及tableName进行赋值
//当然，你也可以获取到database自己进行数据库操作
public class BookDao extends AbstractDao<Book> {
    public BookDao() {
        setDatabase(BaseApplication.getInstance().getDatabase());
        setTableName("book");
    }

    public void myOpera(){
        SQLiteDatabase myDatabase =  getDatabase();
        //...do anything you want with SQLiteDatabase
    }
}
```
使用方法：

```
        BookDao bookDao = new BookDao();
        Book bookInsert = new Book();
        bookInsert.setId(1);//并不会用到
        bookInsert.setPublisher("whu1");
        bookInsert.setName("心灵鸡汤1");
        bookInsert.setAuthor("fly1");
        if(bookDao.insert(bookInsert, false)){
            Log.i(TAG, "insert: " + "插入成功，id采用自增模式");
        }
        bookInsert.setId(6);//会用这个作为id插入到表中
        bookInsert.setPublisher("whu2");
        bookInsert.setAuthor("fly2");
        bookInsert.setName("心灵鸡汤2");
        if(bookDao.insert(bookInsert, true)){
            Log.i(TAG, "insert: " + "插入成功， id为设置的id");
        }

        Book bookFind = bookDao.findById(1);
        Log.i(TAG, "find: " + "根据id找到book：" + JSON.toJSONString(bookFind));

        List<Book> bookList1 = bookDao.findAll();
        Log.i(TAG, "find: " + "查询出所有book：" + JSON.toJSONString(bookList1));

        JSONObject condition = new JSONObject();
        condition.put("author", "fly");
        condition.put("publisher", "whu");
        List<Book> bookList2 = bookDao.findByParams(condition);
        Log.i(TAG, "find: " + "根据条件查询出所有book：" + JSON.toJSONString(bookList2));

        String sql = "select * from book where author = 'fly'";
        List<Book> bookList3 = bookDao.findBySql(sql);
        Log.i(TAG, "find: " + "根据sql语句查询出所有book：" + JSON.toJSONString(bookList3));

        if(bookDao.deleteById(1)){
            Log.i(TAG, "delete: " + "根据id删除成功，成功删除id为1的book");
        }

        Book bookDelete = new Book();
        bookDelete.setId(2);
        if(bookDao.delete(bookDelete)){
            Log.i(TAG, "delete: " + "根据model删除成功，成功删除实体bookDelete，实质是删除id为2的book");
        }

        Book bookModify = new Book();
        bookModify.setId(3);
        bookModify.setAuthor("flyModify");
        bookModify.setName("心灵鸡汤Modify");
        bookModify.setPublisher("whuModify");
        if(bookDao.update(bookModify)){
            Log.i(TAG, "update: " + "成功修改id为" + bookModify.getId() + "的书籍，书籍信息修改为：" + JSON.toJSONString(bookModify));
        }

        condition = new JSONObject();
        condition.put("author", "fly1");
        condition.put("publisher", "whu1");
        if(bookDao.updateByParams(bookModify, condition)){
            Log.i(TAG, "update: " + "成功修改满足条件" + condition + "的书籍，书籍信息修改为：" + JSON.toJSONString(bookModify));
        }

        condition = new JSONObject();
        condition.put("author", "flyModify");
        condition.put("publisher", "whuModify");
        if(bookDao.deleteByParams(condition)){
            Log.i(TAG, "delete: " + "成功删除满足条件" + condition + "的书籍");
        }
```

---
## 引用方法：

```
compile 'cn.flyzy2005:fztutil:1.0.0'
```

---
# 1.0.0版本后记
至此，FztHelper第一版的功能已经整合结束了，详细的使用说明在此READ.md里已经说清楚了（说清楚了吧？ = =）。当然，所有的类都在samples里有详细代码，如果有哪里有不清楚的可以clone工程，在Android Studio中打开再看。

有bug在所难免。。有问题大家一起改。以后如果大家有什么想加进来的帮助类，也可以提交添加请求，总结出一些公用类，以后开发也会容易很多。

谢谢^ ^。 2017.5.23晚




  [1]: https://github.com/kayvannj/PermissionUtil
  [2]: https://github.com/Flyzy2005/FZTHelper/blob/master/pictures/UML_PermissionHelper.png?raw=true%20PermisionHelper
  [3]: https://github.com/square/okhttp%20Okhttp3