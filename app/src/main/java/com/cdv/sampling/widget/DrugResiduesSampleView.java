package com.cdv.sampling.widget;

import android.content.Context;
import android.widget.LinearLayout;

import com.cdv.sampling.bean.JianCeDan;

public class DrugResiduesSampleView extends LinearLayout {

    public DrugResiduesSampleView(Context context, JianCeDan jianCeDan) {
        super(context);
        if (jianCeDan == null){
            throw new RuntimeException("jianCedan is null");
        }
    }
}
