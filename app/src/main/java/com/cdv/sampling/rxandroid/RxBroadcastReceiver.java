package com.cdv.sampling.rxandroid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import rx.Observable;
import rx.Subscriber;
import rx.android.MainThreadSubscription;

public final class RxBroadcastReceiver {

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
                context.registerReceiver(receiver, filter);
                subscriber.add(new MainThreadSubscription() {
                    @Override
                    protected void onUnsubscribe() {
                        context.unregisterReceiver(receiver);
                    }
                });
            }
        });
    }
}