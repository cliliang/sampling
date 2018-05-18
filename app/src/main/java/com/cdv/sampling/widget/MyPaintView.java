package com.cdv.sampling.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.lht.paintview.PaintView;

/**
 * desc:
 * Created by:chenliliang
 * Created on:2018/3/22.
 */

public class MyPaintView extends PaintView {
    private boolean isEmpty = true;

    public MyPaintView(Context context) {
        super(context);
    }

    public MyPaintView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        isEmpty = false;
        return super.onTouchEvent(event);
    }

    public void setIsEmpty(boolean empty){
        isEmpty = empty;
    }

    public boolean isEmpty(){
        return isEmpty;
    }
}
