package cn.flyzy2005.fztutil.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Fly on 2017/9/17.
 */

public class ToastUtils {
    private static volatile Toast toast;

    public static void show(Context context, String text) {
        showText(context, text, Toast.LENGTH_SHORT);
    }

    public static void show(Context context, String text, int duration) {
        showText(context, text, duration);
    }

    public static void show(Context context, int textId) {
        showText(context, context.getText(textId).toString(), Toast.LENGTH_SHORT);
    }

    public static void show(Context context, int textId, int duration) {
        showText(context, context.getText(textId).toString(), duration);
    }

    private static void showText(Context context, String text, int duration) {
        if (null == toast) {
            synchronized (ToastUtils.class) {
                if (null == toast) {
                    toast = Toast.makeText(context, text, duration);
                }
            }
        }
        toast.setText(text);
        toast.setDuration(duration);
        toast.show();
    }
}
