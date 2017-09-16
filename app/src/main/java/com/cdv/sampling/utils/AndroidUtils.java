package com.cdv.sampling.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListAdapter;
import android.widget.ListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AndroidUtils {
    private static final String[] WEEK_ARRAY = new String[]{"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
    private static final String[] WEEK_DAY_ARRAY = new String[]{"日", "一", "二", "三", "四", "五", "六"};
    public static final String DEFAULT_TIME_FORMAT = "yyyy-MM-dd HH:mm";
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";

    private static ProgressDialog progressDialog;
    private static long lastClickTime = 0;

    public static boolean isFastClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        lastClickTime = time;
        if (timeD < 800) {
            return true;
        }
        return false;

    }

    public static int getPictureDegree(int rotation) {
        // 获得手机的方向
        int degree = 0;
        // 根据手机的方向计算相机预览画面应该选择的角度
        switch (rotation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                degree = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                degree = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                degree = 270;
                break;
            default:
                degree = 0;
                break;
        }
        return degree;
    }

    /**
     * 检测电话号码是否合法
     */
    public static boolean checkIsPhoneNum(String phoneNo) {
        if (TextUtils.isEmpty(phoneNo)) {
            return false;
        }
        //String pattern = "^[1]([3][0-9]{1}|59|58|88|89)[0-9]{8}$";
        String pattern = "^((13[0-9])|(15[^4,\\D])|(18[0-9]))\\d{8}$";

        return phoneNo.matches(pattern);
    }

    /**
     * 检测手机号是否合法
     */
    public static boolean checkPhoneNum(String phoneNum) {
        String pattern = "1[3458][0-9]{9}";
        return phoneNum.matches(pattern) || TextUtils.isEmpty(phoneNum);
    }

    /**
     * 检测邮箱是否合法
     */
    public static boolean checkEmail(String emailName) {
        String pattern = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        return emailName.matches(pattern) || TextUtils.isEmpty(emailName);
    }

    /**
     * 检测xxx
     */
    public static boolean checkLandLine(String phoneNo) {
        String pattern = "^(0[0-9]{2,3}-)?([2-9][0-9]{6,7})+(-[0-9]{1,4})?";
        return phoneNo.matches(pattern) || TextUtils.isEmpty(phoneNo);
    }

    // 将 BASE64 编码的字符串 s 进行解码
    public static String getFromBASE64(String s) {
        if (s == null)
            return null;
        byte b[] = Base64.decode(s, Base64.DEFAULT);
        try {
            return new String(b);
        } catch (Exception e) {
            return null;
        }
    }

    // 将 s 进行 BASE64 编码
    public static String getBASE64(String s) {
        if (s == null)
            return null;
        try {
            return Base64.encodeToString(s.getBytes("UTF-8"), Base64.DEFAULT);
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    public static String getActivityCommentDate(String str) {
        if (TextUtils.isEmpty(str)) {
            return "";
        }
        SimpleDateFormat dataFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        try {
            date = dataFormat.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
        SimpleDateFormat dataFormat1 = new SimpleDateFormat("MM月dd日HH:mm");
        String dateStr = dataFormat1.format(date);
        return dateStr;
    }

    public static String formatDate(String time, String format) {
        if (TextUtils.isEmpty(time)) {
            return "";
        }
        SimpleDateFormat dataFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        try {
            date = dataFormat.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
        SimpleDateFormat dataFormat1 = new SimpleDateFormat(format);
        String dateStr = dataFormat1.format(date);
        return dateStr;
    }

    public static String formatDate(Date time, String format) {
        if (time == null) {
            return null;
        }
        SimpleDateFormat dataFormat1 = new SimpleDateFormat(format);
        String dateStr = dataFormat1.format(time);
        return dateStr;

    }

    /**
     * 判断文字长度规范[按字节]
     *
     * @param text
     * @param maxBytes
     * @return
     */
    public static boolean validateTextLength(String text, int maxBytes) {
        byte[] bytes = null;
        try {
            bytes = text.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            bytes = text.getBytes();
        }
        if (bytes.length <= maxBytes) {
            return true;
        }
        return false;
    }

    public static JSONObject getActivityCommentRef(JSONObject optJSONObject) {
        String ownString = optJSONObject.optString("own");
        JSONObject optJSONObjectRef = optJSONObject.optJSONObject("ref");

        try {
            if (optJSONObjectRef == null) {
                optJSONObjectRef = new JSONObject();
                optJSONObjectRef.put(("ref" + 1), ownString);
            } else {
                optJSONObjectRef.put("ref" + (optJSONObjectRef.length() + 1), ownString);
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return optJSONObjectRef;
    }

    public static boolean isDialogShow() {
        if (progressDialog != null && progressDialog.isShowing()) {
            return true;
        }
        return false;
    }

    public static void showProgress(Context context, boolean cancelable) {
        showProgress(context, "正在加載...", cancelable, false);
    }

    public static void showProgress(Context context, String text,
                                    boolean cancelable, boolean outCancelable) {
        try {
            progressDialog = new ProgressDialog(context);
            if (!(context instanceof Activity)) {
                progressDialog.getWindow().setType(
                        WindowManager.LayoutParams.TYPE_SYSTEM_DIALOG);
                progressDialog.getWindow().setType(
                        WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            }
            progressDialog.setMessage(text);
            progressDialog.setCancelable(cancelable);
            progressDialog.setCanceledOnTouchOutside(outCancelable);
            progressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭进度框
     *
     * @param context
     */
    public static void diessProgerss(Context context) {
        try {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
                progressDialog.cancel();
            }
            progressDialog = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 圆角缩小处理高效版
     *
     * @param resultBitmap
     * @param length
     * @param roundPx
     * @return
     */
    public static Bitmap roundAndScale(Bitmap resultBitmap, int length,
                                       float roundPx) {
        return roundAndScale(resultBitmap, length, roundPx, true);
    }

    public static Bitmap roundAndScale(Bitmap resultBitmap, int length,
                                       float roundPx, boolean antiAlias) {
        if (resultBitmap == null)
            return resultBitmap;
        int width = resultBitmap.getWidth();
        int height = resultBitmap.getHeight();
        Rect srcRect = new Rect(0, 0, width, height);
        Rect desRect = new Rect(0, 0, length, length);
        RectF rectF = new RectF(desRect);

        Paint paint = new Paint();
        paint.setAntiAlias(antiAlias);
        paint.setFilterBitmap(antiAlias);
        Bitmap.Config config = Bitmap.Config.ARGB_8888;

        Bitmap bitmap;
        try {
            bitmap = Bitmap.createBitmap(length, length, config);
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
        Canvas roundCanvas = new Canvas(bitmap);

        // 给canvas设置锯齿优化
        if (antiAlias) {
            roundCanvas.setDrawFilter(new PaintFlagsDrawFilter(0,
                    Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        }

        roundCanvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        PorterDuff.Mode mode = PorterDuff.Mode.SRC_IN;
        PorterDuffXfermode porterDuffXfermode = new PorterDuffXfermode(mode);
        paint.setXfermode(porterDuffXfermode);
        roundCanvas.drawBitmap(resultBitmap, srcRect, desRect, paint);

        return bitmap;
    }

    /**
     * 圆角缩放灰度处理高效版
     *
     * @param resultBitmap
     * @param length
     * @param roundPx
     * @return
     */
    public static Bitmap roundAndScaleToGray(Bitmap resultBitmap, int length,
                                             float roundPx) {
        return roundAndScaleToGray(resultBitmap, length, roundPx, true);
    }

    public static Bitmap roundAndScaleToGray(Bitmap resultBitmap, int length,
                                             float roundPx, boolean antiAlias) {
        if (resultBitmap == null)
            return resultBitmap;
        int width = resultBitmap.getWidth();
        int height = resultBitmap.getHeight();
        Rect srcRect = new Rect(0, 0, width, height);
        Rect desRect = new Rect(0, 0, length, length);
        RectF rectF = new RectF(desRect);

        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        paint.setAntiAlias(antiAlias);

        Bitmap.Config config = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = Bitmap.createBitmap(length, length, config);
        Canvas roundCanvas = new Canvas(bitmap);
        if (antiAlias)
            roundCanvas.setDrawFilter(new PaintFlagsDrawFilter(0,
                    Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        roundCanvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        PorterDuff.Mode mode = PorterDuff.Mode.SRC_IN;
        PorterDuffXfermode porterDuffXfermode = new PorterDuffXfermode(mode);
        paint.setXfermode(porterDuffXfermode);
        roundCanvas.drawBitmap(resultBitmap, srcRect, desRect, paint);
        return bitmap;
    }

    /**
     * 图片去色,返回灰度图片
     *
     * @param bmpOriginal 传入的图片
     *                    <p>
     *                    去色后的图片
     */

    public static Bitmap toGrayscale(Bitmap bmpOriginal) {

        int width, height;

        height = bmpOriginal.getHeight();

        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height,
                Bitmap.Config.RGB_565);

        Canvas c = new Canvas(bmpGrayscale);

        Paint paint = new Paint();

        ColorMatrix cm = new ColorMatrix();

        cm.setSaturation(0);

        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);

        paint.setColorFilter(f);

        c.drawBitmap(bmpOriginal, 0, 0, paint);

        return bmpGrayscale;

    }

    /**
     * 圆角
     *
     * @param resultBitmap
     * @param roundPx
     * @return
     */
    public static final Bitmap roundAndScale(Bitmap resultBitmap, float roundPx) {
        if (resultBitmap == null || roundPx < 0)
            return resultBitmap;
        Bitmap mBitmap = comp(resultBitmap, 0, 0);
        int height;
        int width = mBitmap.getWidth();
        if (roundPx == 0) {
            roundPx = mBitmap.getWidth() / 2;
            height = width;
        } else {
            height = mBitmap.getHeight();
        }
        Rect srcRect = new Rect(0, 0, width, height);
        Rect desRect = new Rect(0, 0, width, height);
        RectF rectF = new RectF(desRect);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        Bitmap.Config config = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = Bitmap.createBitmap(width, height, config);
        Canvas roundCanvas = new Canvas(bitmap);
        roundCanvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        PorterDuff.Mode mode = PorterDuff.Mode.SRC_IN;
        PorterDuffXfermode porterDuffXfermode = new PorterDuffXfermode(mode);
        paint.setXfermode(porterDuffXfermode);
        roundCanvas.drawBitmap(mBitmap, srcRect, desRect, paint);
        mBitmap.recycle();
        return bitmap;
    }


    /**
     * 圆角
     *
     * @param resultBitmap
     * @param roundPx
     * @return
     */
    public static final Bitmap roundAndScale(Bitmap resultBitmap,
                                             float roundPx, int lenght) {
        if (resultBitmap == null || roundPx < 0)
            return resultBitmap;
        Bitmap newBitmap = resultBitmap;
        Bitmap mBitmap = comp(newBitmap, 0, 0);
        int height;
        int width = mBitmap.getWidth();
        if (roundPx == 0) {
            roundPx = mBitmap.getWidth() / 2;
            height = width;
        } else {
            height = mBitmap.getHeight();
        }
        Rect srcRect = new Rect(0, 0, width, height);
        Rect desRect = new Rect(0, 0, lenght, lenght);
        RectF rectF = new RectF(desRect);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        Bitmap.Config config = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = Bitmap.createBitmap(lenght, lenght, config);
        Canvas roundCanvas = new Canvas(bitmap);
        roundCanvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        PorterDuff.Mode mode = PorterDuff.Mode.SRC_IN;
        PorterDuffXfermode porterDuffXfermode = new PorterDuffXfermode(mode);
        paint.setXfermode(porterDuffXfermode);
        roundCanvas.drawBitmap(mBitmap, srcRect, desRect, paint);
        mBitmap.recycle();
        resultBitmap.recycle();
        return bitmap;
    }

    /**
     * 压缩bitmap 防止oom
     *
     * @param image
     * @return
     */
    public static Bitmap comp(Bitmap image, int widh, int heigt) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 20, baos);
        /*
         * if( baos.toByteArray().length / 1024>1024)
		 * {//判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
		 * baos.reset();//重置baos即清空baos
		 * image.compress(Bitmap.CompressFormat.JPEG, 50,
		 * baos);//这里压缩50%，把压缩后的数据存放到baos中 }
		 */
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        // 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = 1280f;// 这里设置高度为800f
        float ww = 720f;// 这里设置宽度为480f
        if (widh != 0) {
            ww = widh;
        }
        if (heigt != 0) {
            hh = heigt;
        }
        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;// be=1表示不缩放
        if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;// 设置缩放比例
        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        isBm = new ByteArrayInputStream(baos.toByteArray());
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        try {
            baos.close();
            isBm.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
    }

    /**
     * 质量压缩
     *
     * @param image
     * @return
     */

    private static Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 20, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        /*
         * while ( baos.toByteArray().length / 1024>100) {
		 * //循环判断如果压缩后图片是否大于100kb,大于继续压缩 baos.reset();//重置baos即清空baos
		 * image.compress(Bitmap.CompressFormat.JPEG, options,
		 * baos);//这里压缩options%，把压缩后的数据存放到baos中 options -= 10;//每次都减少10 }
		 */
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
        try {
            baos.close();
            isBm.close();
        } catch (IOException e) {

            e.printStackTrace();
        }
        return bitmap;
    }

    public static void hideIME(Context context) {
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(context.INPUT_METHOD_SERVICE);
        if (context != null && context instanceof Activity) {
            View v = ((Activity) context).getCurrentFocus();
            if (v != null) {
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        }

    }

    public static void showIME(Context context, View v) {
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        // 得到InputMethodManager的实例
        // if (!imm.isActive()) {
        // //如果开启
        // imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,
        // InputMethodManager.HIDE_NOT_ALWAYS);
        // //关闭软键盘，开启方法相同，这个方法是切换开启与关闭状态的
        // }
        imm.showSoftInput(v, 0);
    }

    public static String getWeekday(Date date) {
        Calendar target = Calendar.getInstance();
        target.setTime(date);
        String week = WEEK_DAY_ARRAY[target.get(Calendar.DAY_OF_WEEK) - 1];
        return week;
    }


    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    private static final int IDENTITYCODE_OLD = 15;
    private static final int IDENTITYCODE_NEW = 18;

    public static boolean isIdentityCode(String code) {

        if (TextUtils.isEmpty(code)) {
            return false;
        }

        String birthDay = "";
        code = code.trim();

        // 长度只有15和18两种情况
        if ((code.length() != IDENTITYCODE_OLD)
                && (code.length() != IDENTITYCODE_NEW)) {
            return false;
        }

        // 身份证号码必须为数字(18位的新身份证最后一位可以是x)
        Pattern pt = Pattern.compile("\\d{15,17}([\\dxX]{1})?");
        Matcher mt = pt.matcher(code);
        if (!mt.find()) {
            return false;
        }

        // 验证生日
        if (code.length() == IDENTITYCODE_OLD) {
            birthDay = "19" + code.substring(6, 12);
        } else {
            birthDay = code.substring(6, 14);
        }

        return true;
    }

    public static boolean validateIdCard(String idcard) {
        if (TextUtils.isEmpty(idcard)) {
            return false;
        }
        Pattern idNumPattern = Pattern.compile("(\\d{14}[0-9a-zA-Z])|(\\d{17}[0-9a-zA-Z])");
        Matcher idNumMatcher = idNumPattern.matcher(idcard);
        //判断用户输入是否为身份证号
        if (idNumMatcher.matches()) {
            System.out.println("您的出生年月日是：");
            //如果是，定义正则表达式提取出身份证中的出生日期
            Pattern birthDatePattern = Pattern.compile("\\d{6}(\\d{4})(\\d{2})(\\d{2}).*");//身份证上的前6位以及出生年月日
            //通过Pattern获得Matcher
            Matcher birthDateMather = birthDatePattern.matcher(idcard);
            //通过Matcher获得用户的出生年月日
            if (birthDateMather.find()) {
                return true;
            }
        }
        return false;
    }

    public static String getRealFilePath(final Context context, final Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }
}

