package com.cdv.sampling;

import android.app.Application;
import android.content.Context;

import com.cdv.sampling.bean.DaoMaster;
import com.cdv.sampling.bean.DaoSession;
import com.cdv.sampling.bean.ShouYaoCanLiuSample;
import com.cdv.sampling.constants.FileConstants;
import com.cdv.sampling.image.ImageLoaderUtils;
import com.cdv.sampling.repository.UserRepository;
import com.cdv.sampling.utils.LogUtil;
import com.tencent.bugly.crashreport.CrashReport;

import org.greenrobot.greendao.database.Database;

public class SamplingApplication extends Application{

    private static SamplingApplication application;

    private DaoSession daoSession;
    private DaoMaster.DevOpenHelper mDBHelper;

    private ShouYaoCanLiuSample shouYaoCanLiuSample;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        ImageLoaderUtils.initImageLoader(this);
        LogUtil.plant(new LogUtil.DebugTree(), FileConstants.ROOT_LOG_PATH, BuildConfig.DEBUG);
        initDBFormUser();
        Context context = getApplicationContext();
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(context);
        strategy.setBuglyLogUpload(BuildConfig.DEBUG);
        CrashReport.initCrashReport(context, "151e580b13", BuildConfig.DEBUG, strategy);
    }

    public void initDBFormUser(){
        if (UserRepository.getInstance().getCurrentUser() == null){
            return;
        }
        if (mDBHelper != null){
            mDBHelper.close();
        }
        mDBHelper = new DaoMaster.DevOpenHelper(this,"sampling-db-" + UserRepository.getInstance().getCurrentUser().getAccount()){
            @Override
            public void onCreate(Database db) {
                super.onCreate(db);
                LogUtil.i("on create db");
            }
        };
        Database db = mDBHelper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();
    }

    public static SamplingApplication getInstance(){
        return application;
    }

    public static DaoSession getDaoSession() {
        return getInstance().daoSession;
    }

    public ShouYaoCanLiuSample getShouYaoCanLiuSample() {
        return shouYaoCanLiuSample;
    }

    public void setShouYaoCanLiuSample(ShouYaoCanLiuSample shouYaoCanLiuSample) {
        this.shouYaoCanLiuSample = shouYaoCanLiuSample;
    }
}
