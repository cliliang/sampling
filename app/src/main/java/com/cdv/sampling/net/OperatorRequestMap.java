package com.cdv.sampling.net;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.cdv.sampling.BuildConfig;
import com.cdv.sampling.SamplingApplication;
import com.cdv.sampling.bean.BaseBean;
import com.cdv.sampling.constants.IntentConstants;
import com.cdv.sampling.exception.NetworkConnectionException;
import com.cdv.sampling.exception.RequestIllegalException;
import com.cdv.sampling.repository.UserRepository;
import com.cdv.sampling.utils.AppUtils;

import retrofit2.adapter.rxjava.HttpException;
import rx.Observable.Operator;
import rx.Subscriber;
import rx.exceptions.Exceptions;

/**
 * Created by apple on 16/3/21.
 */
public class OperatorRequestMap<T> implements Operator<T, BaseBean<T>> {

    @Override
    public Subscriber<? super BaseBean<T>> call(final Subscriber<? super T> subscriber) {
        return new Subscriber<BaseBean<T>>() {
            @Override
            public void onCompleted() {
                subscriber.onCompleted();
            }

            @Override
            public void onError(Throwable e) {
                if (BuildConfig.DEBUG){
                    e.printStackTrace();
                }
                if(e instanceof HttpException){
                    HttpException exception = (HttpException) e;
                    if (exception.code() == 403){
                        AppUtils.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                UserRepository.logOut();
                                LocalBroadcastManager.getInstance(SamplingApplication.getInstance()).sendBroadcast(new Intent(IntentConstants.ACTION_LOGIN_USER_CHANGED));
                            }
                        });
                        subscriber.onError(new RequestIllegalException("登录失效！"));
                    }else{
                        subscriber.onError(new NetworkConnectionException(e));
                    }
                }else if (e instanceof RequestIllegalException){
                    subscriber.onError(e);
                }else{
                    subscriber.onError(new NetworkConnectionException(e));
                }
            }

            @Override
            public void onNext(BaseBean<T> tBaseBean) {
                try {
                    if (tBaseBean.getErrCode() != 0){
                        throw new RequestIllegalException(tBaseBean.getErrMsg());
                    }else{
                        subscriber.onNext(tBaseBean.getData());
                    }
                } catch (Throwable e) {
                    Exceptions.throwOrReport(e, this, tBaseBean);
                }
            }
        };
    }
}
