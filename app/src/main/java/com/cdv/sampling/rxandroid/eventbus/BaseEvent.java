package com.cdv.sampling.rxandroid.eventbus;

import android.text.TextUtils;

public class BaseEvent<T> {

    public static final String EVENT_NAME_DOWNLOAD_BOOK_SUCCESS = "EVENT_NAME_DOWNLOAD_BOOK_SUCCESS";
    public static final String EVENT_NAME_OPEN_GUIDE = "EVENT_NAME_OPEN_GUIDE";

    private String eventName;
    private long eventTime;
    private Object data;

    public BaseEvent(T t) {
        this.data = t;
        this.eventTime = System.currentTimeMillis();
        if (t != null){
            this.eventName = t.toString();
        }else{
            this.eventName = null;
        }
    }

    public String getEventName() {
        if (TextUtils.isEmpty(eventName)){
            eventName = this.getClass().getName();
        }
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public long getEventTime() {
        return eventTime;
    }

    public void setEventTime(long eventTime) {
        this.eventTime = eventTime;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
