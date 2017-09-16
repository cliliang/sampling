package com.cdv.sampling.storage;

import android.app.Application;

import com.cdv.sampling.SamplingApplication;


/**
 * Created by yingjianxu on 15/6/18.
 */
public class SamplingStorage extends BaseStorage{
    private SamplingStorage(Application application) {
        super(application);
    }

    public static SamplingStorage getInstance(){
        return EPocketStorageHandler.INSTANCE;
    }

    private static class EPocketStorageHandler{
        public static final SamplingStorage INSTANCE = new SamplingStorage(SamplingApplication.getInstance());
    }

    @Override
    public String getStorageFileName() {
        return "epocket_local_cache";
    }
}
