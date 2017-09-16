package com.cdv.sampling.rxandroid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import rx.Observable;
import rx.Subscriber;
import rx.android.MainThreadSubscription;

/**
 * @Description: 本地广播
 * @author: guolili
 * @date: 2016-03-30
 */
public final class RxLocalBroadReceiver {

    public static class IntentWithContext {
        private Context context;
        private Intent intent;

        public IntentWithContext(Context context, Intent intent) {
            this.context = context;
            this.intent = intent;
        }

        public Context getContext() {
            return context;
        }

        public Intent getIntent() {
            return intent;
        }
    }

    public static Observable<IntentWithContext> fromBroadcast(final Context context, final String ...actionArray) {
        return Observable.create(new Observable.OnSubscribe<IntentWithContext>() {

            @Override
            public void call(final Subscriber<? super IntentWithContext> subscriber) {
                MainThreadSubscription.verifyMainThread();
                final BroadcastReceiver receiver = new BroadcastReceiver() {

                    @Override
                    public void onReceive(Context context, Intent intent) {
                        subscriber.onNext(new IntentWithContext(context, intent));
                    }

                };
                IntentFilter filter = new IntentFilter();
                if (actionArray != null && actionArray.length > 0){
                    for (String action : actionArray){
                        filter.addAction(action);
                    }
                }
                LocalBroadcastManager.getInstance(context).registerReceiver(receiver, filter);
                subscriber.add(new MainThreadSubscription() {
                    @Override
                    protected void onUnsubscribe() {
                        LocalBroadcastManager.getInstance(context).unregisterReceiver(receiver);
                    }
                });
            }
        });
    }
}
