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
- post body
```
StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 10000; i++)
            builder.append("123");
        RequestBody requestBody = OkHttpHelper.getInstance().postBody().content(builder.toString()).build();
        Request request = new Request.Builder()
                .url("http://192.168.1.111:8080/GeoDisaster/mobiledata/collects/routes")
                .post(requestBody)
                .build();
        OkHttpHelper.getInstance().execute(request, new StringCallback() {
            @Override
            public void onFailure(Call call, Exception e) {
                ((TextView)findViewById(R.id.post_body_result)).setText(e.getMessage());
            }

            @Override
            public void onResponse(Call call, String s) {
                ((TextView)findViewById(R.id.post_body_result)).setText(s);
            }
        });
```
1. 提供cn.flyzy2005.fztutil.callback.Callback抽象类，可以通过继承它来实现对response的处理，目前工具里已提供StringCallback和FileCallback，分别提供将response转成string和将response转成file的功能。
2. okHttpClient采用了默认的设置，如果对超时等设置有特殊需求，可以在最开始通过OkHttpHelper.getInstance().initClient(OkHttpClient)方法设置OkHttpClient。
3. 如果所提供的功能不满足需求，同样可以获取okHttpClient对象自行处理。