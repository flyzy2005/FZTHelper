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