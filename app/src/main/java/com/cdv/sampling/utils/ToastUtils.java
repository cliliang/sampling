package com.cdv.sampling.utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/**
 * ToastUtils
 *
 * @author <a href="http://www.trinea.cn" target="_blank">Trinea</a> 2013-12-9
 */
public class ToastUtils {

    public static void show(Context context, int resId) {
        show(context, context.getResources().getText(resId), Toast.LENGTH_SHORT);
    }

    public static void show(Context context, int resId, int duration) {
        show(context, context.getResources().getText(resId), duration);
    }

    public static void show(Context context, CharSequence text) {
        show(context, text, Toast.LENGTH_SHORT);
    }

    public static void show(Context context, CharSequence text, int duration) {
        toast(context, text.toString(), duration);
    }

    public static void show(Context context, int resId, Object... args) {
        show(context, String.format(context.getResources().getString(resId), args), Toast.LENGTH_SHORT);
    }

    public static void show(Context context, String format, Object... args) {
        show(context, String.format(format, args), Toast.LENGTH_SHORT);
    }

    public static void show(Context context, int resId, int duration, Object... args) {
        show(context, String.format(context.getResources().getString(resId), args), duration);
    }

    public static void show(Context context, String format, int duration, Object... args) {
        show(context, String.format(format, args), duration);
    }

    private static Toast toast;

    /**
     * 单例模式,优化用户体验,避免连续toast时,多次弹出.
     *
     * @param context
     * @param text
     * @param duration
     */
    public static void toast(Context context, String text, int duration) {

        if (context == null) {
            return;
        }

        if (toast == null) {
            toast = Toast.makeText(context, text, duration);
        } else {
            toast.setText(text);
            toast.setDuration(duration);
        }
        toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 150);
        toast.show();
    }

    public static void toast(Context context, int id, int duration) {

        if (context == null) {
            return;
        }

        if (toast == null) {
            toast = Toast.makeText(context, id, duration);
        } else {
            toast.setText(context.getString(id));
            toast.setDuration(duration);
        }
        toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 150);
        toast.show();
    }

    public static void shortToast(Context context, int id) {
        toast(context, id, Toast.LENGTH_SHORT);
    }

    public static void shortToast(Context context, String text) {
        toast(context, text, Toast.LENGTH_SHORT);
    }

    public static void longToast(Context context, int id) {
        toast(context, id, Toast.LENGTH_LONG);
    }

    public static void longToast(Context context, String text) {
        toast(context, text, Toast.LENGTH_LONG);
    }
}
