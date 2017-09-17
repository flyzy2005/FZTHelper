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