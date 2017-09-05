
## 1.数据库模块
- 定义与表对应的entity对象

```
public class Book {
    @PrimaryKey
    private int id;
    @ColumnAlias(columnName = "name")
    private String name1;
    private String author;
    private String publisher;
    @Ignore
    private String test2;
    //...省略getset方法
}
```
提供3个注解，```@PrimaryKey```代表这个字段是主键，```@ColumnAlias```指明这个成员变量在数据库中的列名称，```@Ignore```表示忽略这个成员变量，不会参与映射与存储。
- 定义Dao类

```
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
AbstractDao封装了常用的数据库操作，只需要在构造函数中设置数据源以及设置好表名称即可。如果封装的操作不满足操作，可以获得数据源后自己处理。
- 封装的方法

```
/**
     * 根据primaryKey进行查询
     * 需要设置{@link PrimaryKey}
     *
     * @param primaryKey 主键的值
     * @return entity实例，没有则为null
     */
    T getByPrimaryKey(Object primaryKey);

    /**
     * 根据条件条件进行查询
     *
     * @param condition 查询条件{"name":"fly"}(封装在JSONObject中)
     * @return 满足条件的第一个实例，没有则为null
     */
    T getByParams(JSONObject condition);

    /**
     * 根据条件条件进行查询
     *
     * @param condition 查询条件{"name":"fly"}(封装在JSONObject中)
     * @return 所有满足条件的entity实例，没有则为空ArrayList
     */
    List<T> listByParams(JSONObject condition);

    /**
     * 查询表中所有数据
     *
     * @return 所有entity实例，没有则为空ArrayList
     */
    List<T> listAll();

    /**
     * 根据sql语句进行查询
     *
     * @param sql sql语句
     * @return 满足条件的第一个实例，没有则为null
     */
    T getBySql(String sql);

    /**
     * 根据sql语句进行查询
     *
     * @param sql sql语句
     * @return 满足查询条件的entity实例，没有则为空ArrayList
     */
    List<T> listBySql(String sql);

    /**
     * 添加一条记录
     *
     * @param model  entity实例
     * @param withId 是否需要插入PrimaryKey，不插入的话就采用SQLite自带的自增策略（不插入需要设置{@link PrimaryKey}）
     * @return 是否插入成功
     */
    boolean insert(T model, boolean withId);

    /**
     * 根据primaryKey删除一条记录
     *
     * @param model entity实例
     * @return 是否删除成功
     */
    boolean delete(T model);

    /**
     * 根据Id删除一条记录
     *
     * @param primaryKey primaryKey
     * @return 是否删除成功
     */
    boolean deleteByPrimaryKey(Object primaryKey);

    /**
     * 根据条件删除一条（多条）记录
     *
     * @param condition 删除条件
     * @return 是否删除成功
     */
    boolean deleteByParams(JSONObject condition);

    /**
     * 删除表中所有数据
     *
     * @return 是否删除成功
     */
    boolean deleteAll();

    /**
     * 修改一条记录，会根据传入的entity的primaryKey进行匹配修改，并且会用除primaryKey之外的所有其他属性的新值更新数据库
     *
     * @param model 修改的entity实例
     * @return 是否修改成功
     */
    boolean update(T model);

    /**
     * 根据条件修改一条记录，并且会用除primaryKey的所有其他属性的新值更新数据库
     *
     * @param model     修改的entity实例
     * @param condition 条件
     * @return 是否修改成功
     */
    boolean updateByParams(T model, JSONObject condition);

    /**
     * 所有行数
     *
     * @return int
     */
    int count();

    /**
     * 满足条件的所有行数
     *
     * @param condition 条件
     * @return int
     */
    int countByParams(JSONObject condition);
```
- 导入外部数据库

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
                    //更新操作包括4个步骤
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
```
## 2. 网络请求模块
- get请求：
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
1. 提供cn.flyzy2005.fztutil.callback.Callback抽象类，可以通过继承它来实现对response的处理，目前工具里已提供StringCallback和FileCallback，分别提供将response转成string和将response转成file的功能。
2. okHttpClient采用了默认的设置，如果对超时等设置有特殊需求，可以通过OkHttpHelper.getInstance().getOkHttpClient()方法获得okHttpClient对象后自行设置。
3. 如果所提供的功能不满足需求，同样可以获取okHttpClient对象自行处理。

## 3. 权限获取模块

![image](https://github.com/Flyzy2005/FZTHelper/blob/master/pictures/UML_PermissionHelper.png?raw=true%20PermisionHelper)

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
#### 注意：即使是运行时权限，也需要在AndroidManifest.xml中声明的，不然这个会不起效果。

### 4. 常用工具类
- 开机动画

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
- 双击退出

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
- 坐标装换（百度、火星、WGS84互转，墨卡托与经纬度互转）

直接调用```CoordinateHelper```里的方法即可。

- 配置文件设置（例如可以存储用户登录信息、手机型号信息等）
1. 在实现Application的类中封装保存以及获取配置信息的方法
2. 通过BaseApplication在任何地方获取到配置文件中的内容
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
