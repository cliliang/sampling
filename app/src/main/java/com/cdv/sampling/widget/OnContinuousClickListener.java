package com.cdv.sampling.widget;

import android.view.View;
import android.view.View.OnClickListener;

public abstract class OnContinuousClickListener implements OnClickListener {
	private long lastClickTime;
	private int maxTime = 500;
	
	public OnContinuousClickListener() {
		super();
	}
	
	public OnContinuousClickListener(int time) {
		super();
		this.maxTime = time;
	}
	public abstract void onContinuousClick(View v);
	
	
	
	@Override
	public void onClick(View v) {
		if (isContinuousClick()) {
			return;
		}
		onContinuousClick(v);
		
	}
	public void reset(){
		lastClickTime = 0;
	}
	
	private boolean isContinuousClick(){
		long time = System.currentTimeMillis();
        long sub = time - lastClickTime;
        if ( 0 < sub && sub < maxTime) {   
            return true;   
        }   
        lastClickTime = time;   
        return false;   
	}
}
