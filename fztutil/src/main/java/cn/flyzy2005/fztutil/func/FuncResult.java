package cn.flyzy2005.fztutil.func;

/**
 * Created by Fly on 2017/5/7.
 */

public abstract class FuncResult {
    public abstract void call(int requestCode, String permissions[], int[] grantResults);
}
