package com.cdv.sampling.rxandroid.eventbus;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rx.Observable;
import rx.functions.Action1;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * @Description: 事件发送者
 * @author: XuYingjian
 * @date: 2016-03-29
 */
public class RxEventBus {

    private final Subject<Object, Object> _bus = new SerializedSubject<>(PublishSubject.create());
    private static final RxEventBus INSTANCE = new RxEventBus();
    private List<WeakReference> eventOnMainHandlerList = Collections.synchronizedList(new ArrayList<WeakReference>());
    private RxEventBus(){};
    public static RxEventBus getInstance(){
        return INSTANCE;
    }

    public void registerOnMain(Action1<BaseEvent> eventAction1){
        synchronized (this){
            for (int i = eventOnMainHandlerList.size() - 1; i >= 0; i --){
                if (eventOnMainHandlerList.get(i).get() == null){
                    eventOnMainHandlerList.remove(i);
                }
            }
            eventOnMainHandlerList.add(new WeakReference(eventAction1));
        }
    }

    public void send(BaseEvent o) {
        _bus.onNext(o);
    }


    public Observable<Object> toObserverable() {
        return _bus;
    }

    public boolean hasObservers() {
        return _bus.hasObservers();
    }
}
