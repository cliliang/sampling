package com.cdv.sampling.rxandroid;

import com.cdv.sampling.BuildConfig;
import com.tencent.bugly.crashreport.CrashReport;

import rx.Subscriber;

public class CommonSubscriber<T> extends Subscriber<T> {
    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {
        if (BuildConfig.DEBUG){
            e.printStackTrace();
        }else{
            CrashReport.postCatchedException(e);
        }
    }

    @Override
    public void onNext(T o) {

    }
}
