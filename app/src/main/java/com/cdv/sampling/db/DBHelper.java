package com.cdv.sampling.db;

import android.content.Context;

import com.cdv.sampling.BuildConfig;
import com.cdv.sampling.SamplingApplication;
import com.cdv.sampling.utils.LogUtil;
import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;

import rx.schedulers.Schedulers;

public class DBHelper {

    private SqlBrite sqlBrite;
    private BriteDatabase samplingDatabase;

    private Context mContext;
    private DBHelper(){
        mContext = SamplingApplication.getInstance();
        SqlBrite.Builder builder = new SqlBrite.Builder();
        if (BuildConfig.DEBUG){
            builder = builder.logger(new SqlBrite.Logger() {
                @Override
                public void log(String message) {
                    LogUtil.i(message);
                }
            });
        }
        sqlBrite = builder.build();
        samplingDatabase = sqlBrite.wrapDatabaseHelper(new SamplingDB(mContext), Schedulers.io());
        samplingDatabase.setLoggingEnabled(BuildConfig.DEBUG);
    }

    private static class DBHelperHandler{
        public static final DBHelper INSTANCE = new DBHelper();
    }

    public static DBHelper getInstance(){
        return DBHelperHandler.INSTANCE;
    }

    public static BriteDatabase getSamplingDatabase() {
        return getInstance().samplingDatabase;
    }
}
