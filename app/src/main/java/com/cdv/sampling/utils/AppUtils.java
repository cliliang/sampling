package com.cdv.sampling.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.cdv.sampling.SamplingApplication;
import com.cdv.sampling.bean.JianCeDan;
import com.cdv.sampling.constants.Constants;
import com.cdv.sampling.constants.FileConstants;
import com.cdv.sampling.constants.IntentConstants;
import com.cdv.sampling.exception.FormException;
import com.cdv.sampling.image.ImageLoaderUtils;
import com.cdv.sampling.rxandroid.CommonSubscriber;
import com.cdv.sampling.rxandroid.RxLocalBroadReceiver;
import com.cdv.sampling.utils.io.FileUtils;
import com.cdv.sampling.utils.io.IOUtils;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.UUID;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * AppUtils
 * <ul>
 * <li>{@link AppUtils#isNamedProcess(Context, String)}</li>
 * </ul>
 *
 * @author <a href="http://www.trinea.cn" target="_blank">Trinea</a> 2014-5-07
 */
public class AppUtils {

    /**
     * whether this process is named with processName
     *
     * @param context
     * @param processName
     * @return <ul>
     * return whether this process is named with processName
     * <li>if context is null, return false</li>
     * <li>if {@link ActivityManager#getRunningAppProcesses()} is null, return false</li>
     * <li>if one process of {@link ActivityManager#getRunningAppProcesses()} is equal to processName, return
     * true, otherwise return false</li>
     * </ul>
     */
    public static boolean isNamedProcess(Context context, String processName) {
        if (context == null) {
            return false;
        }

        int pid = android.os.Process.myPid();
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> processInfoList = manager.getRunningAppProcesses();
        if (processInfoList == null) {
            return true;
        }

        for (RunningAppProcessInfo processInfo : manager.getRunningAppProcesses()) {
            if (processInfo.pid == pid && ObjectUtils.isEquals(processName, processInfo.processName)) {
                return true;
            }
        }
        return false;
    }

    public static Observable<RxLocalBroadReceiver.IntentWithContext> getLoginStatusBroadcast() {
        return RxLocalBroadReceiver.fromBroadcast(SamplingApplication.getInstance(), IntentConstants.ACTION_LOGIN_USER_CHANGED);
    }

    public static void runOnUiThread(final Runnable runnable) {
        if (runnable == null) {
            return;
        }
        Observable.empty().observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommonSubscriber<Object>() {

                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                        runnable.run();
                    }
                });
    }

    public static void runOnWorkerThread(final Runnable runnable) {
        if (runnable == null) {
            return;
        }
        Observable.empty().observeOn(Schedulers.computation())
                .subscribe(new CommonSubscriber<Object>() {

                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                        runnable.run();
                    }
                });
    }


    public static boolean isWifiEnable() {
        return NetworkUtil.getCurrentNetworkStatus(SamplingApplication.getInstance()) == NetworkUtil.NETWORK_STATUS_WIFI;
    }

    public static boolean isNetworkAvailable() {
        return NetworkUtil.isNetworkAvailable(SamplingApplication.getInstance());
    }

    public static boolean isNetworkUnavailable() {
        return !isNetworkAvailable();
    }

    public static void hideKeyboard(Context context) {
        if (context != null && context instanceof Activity) {
            InputMethodManager imm = (InputMethodManager) context
                    .getSystemService(context.INPUT_METHOD_SERVICE);
            View v = ((Activity) context).getCurrentFocus();
            if (v != null) {
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        }

    }

    public static void showKeyboard(Context context, View v) {
        if (context == null) {
            return;
        }
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(v, 0);
    }

    public static String generateId() {
        return UUID.randomUUID().toString();
    }

    public static boolean isHaveId(Long id) {
        return id != null && id.longValue() > 0;
    }

    public static boolean isValidId(String id) {
        boolean isValid = id != null && TextUtils.isDigitsOnly(id);
        if (!isValid) {
            return false;
        }
        try {
            Long.parseLong(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String getSampleType(String sampleType) {
        if (JianCeDan.DAN_TYPE_SAMPLING_DRUG.equals(sampleType)) {
            return Constants.SAMPLING_DRUG;
        } else if (JianCeDan.DAN_TYPE_SAMPLING_VERIFICATION.equals(sampleType)) {
            return Constants.SAMPLING_VERIFICATION;
        } else if (JianCeDan.DAN_TYPE_SAMPLING_QUALITY.equals(sampleType)) {
            return Constants.SAMPLING_QUALITY;
        }
        return null;
    }

    public static File generateImage(View view, String filePath) {
        if (view == null) {
            return null;
        }
        ImageLoaderUtils.clearCache();
        Bitmap origin = ViewUtils.getViewBitmap(view, Constants.FORM_IMAGE_WIDTH, Constants.FORM_IMAGE_HEIGHT);
        try {
            File file = new File(filePath);
            saveBitmapToFile(origin, file);
            if (!origin.isRecycled()) {
                origin.recycle();
            }
            return file;
        } catch (Exception e) {
            CrashReport.postCatchedException(e);
            throw new FormException("生成表单出错！", e);
        }
    }

    private static Bitmap rotateBitmap(Bitmap origin, float alpha) {
        if (origin == null) {
            return null;
        }
        int width = origin.getWidth();
        int height = origin.getHeight();
        Matrix matrix = new Matrix();
        matrix.setRotate(alpha);
        // 围绕原地进行旋转
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (newBM.equals(origin)) {
            return newBM;
        }
        origin.recycle();
        return newBM;
    }

    private static void saveBitmapToFile(Bitmap bitmap, File file) throws IOException {
        FileOutputStream fos = null;
        try {
            if (file.exists()) {
                file.delete();
            }
            if (!file.exists()) {
                FileUtils.createNewFile(file.getAbsolutePath());
            }
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 99, fos);
        } finally {
            IOUtils.closeQuietly(fos);
        }
    }

    public static boolean isEditable(JianCeDan jianCeDan) {
        return jianCeDan == null || jianCeDan.getStatus() != JianCeDan.STATUS_ARCHIVED;
    }

    public static String generateFormFileName(Long formId, int order) {
        JianCeDan jianCeDan = SamplingApplication.getDaoSession().getJianCeDanDao().load(formId);
        String fileName = TimeUtils.getTime(jianCeDan.getCreateTime().getTime(), new SimpleDateFormat("yyyyMMddHHmm"));
        String filePath = getFormRootPath(formId) + fileName + "-" + order + ".jpg";
        return filePath;
    }

    public static String getFormRootPath(Long formId) {
        JianCeDan jianCeDan = SamplingApplication.getDaoSession().getJianCeDanDao().load(formId);
        String dirName = TimeUtils.getTime(jianCeDan.getCreateTime().getTime(), new SimpleDateFormat("yyyyMMddHHmm"));
        String filePath = FileConstants.ROOT_IMAGE_PATH + File.separator + dirName + File.separator;
        return filePath;
    }
}
