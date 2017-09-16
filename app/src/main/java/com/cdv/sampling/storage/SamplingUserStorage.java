package com.cdv.sampling.storage;

import android.app.Application;

import com.cdv.sampling.SamplingApplication;
import com.cdv.sampling.repository.UserRepository;
import com.cdv.sampling.rxandroid.CommonSubscriber;
import com.cdv.sampling.rxandroid.RxLocalBroadReceiver;
import com.cdv.sampling.utils.AppUtils;

import rx.android.schedulers.AndroidSchedulers;

public class SamplingUserStorage extends BaseStorage{

    private SamplingUserStorage(Application application) {
        super(application);
        AppUtils.getLoginStatusBroadcast().subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommonSubscriber<RxLocalBroadReceiver.IntentWithContext>(){
                    @Override
                    public void onNext(RxLocalBroadReceiver.IntentWithContext o) {
                        init();
                    }
                });
    }

    public static SamplingUserStorage getInstance(){
        return EPocketUserStorageHandler.INSTANCE;
    }

    private static class EPocketUserStorageHandler{
        public static final SamplingUserStorage INSTANCE = new SamplingUserStorage(SamplingApplication.getInstance());
    }

    @Override
    public String getStorageFileName() {
        return "epocket_user_cache_" + UserRepository.getInstance().getCurrentUser().getID();
    }
}
