package com.cdv.sampling.bean;

import android.graphics.drawable.BitmapDrawable;

public class Signature<T> {
    private BitmapDrawable shoujianren;
    private BitmapDrawable caiyangren;
    private BitmapDrawable shoujianriqi;
    private BitmapDrawable caiyangriqi;

    private T sampleInfo;

    public BitmapDrawable getShoujianren() {
        return shoujianren;
    }

    public void setShoujianren(BitmapDrawable shoujianren) {
        this.shoujianren = shoujianren;
    }

    public BitmapDrawable getCaiyangren() {
        return caiyangren;
    }

    public void setCaiyangren(BitmapDrawable caiyangren) {
        this.caiyangren = caiyangren;
    }

    public BitmapDrawable getShoujianriqi() {
        return shoujianriqi;
    }

    public void setShoujianriqi(BitmapDrawable shoujianriqi) {
        this.shoujianriqi = shoujianriqi;
    }

    public BitmapDrawable getCaiyangriqi() {
        return caiyangriqi;
    }

    public void setCaiyangriqi(BitmapDrawable caiyangriqi) {
        this.caiyangriqi = caiyangriqi;
    }

    public T getSampleInfo() {
        return sampleInfo;
    }

    public void setSampleInfo(T sampleInfo) {
        this.sampleInfo = sampleInfo;
    }

    public boolean isValid(){
        return shoujianren == null || caiyangren == null || caiyangriqi == null;
    }
}
