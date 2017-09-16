package com.cdv.sampling.widget;

import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.cdv.sampling.R;


public class EPocketTipsDialog extends EPDialog {

    protected static EPocketTipsDialog mDialog;
    protected TextView tvContent;
    protected OnContinuousClickListener confirmListener;


    private String tipsText;

    public EPocketTipsDialog() {
    }

    public static EPocketTipsDialog getInstance() {
        return new EPocketTipsDialog();
    }

    @Override
    protected int getContentViewId() {
        return R.layout.dialog_tips;
    }

    @Override
    protected void initViews() {
        super.initViews();

        tvContent = (TextView) contentView.findViewById(R.id.tips_content_tv);
        TextView tv = (TextView) contentView.findViewById(R.id.dialog_confirm_tv);
        setCancelable(false);
        if (tvContent != null){
            tvContent.setText(tipsText);
        }
        if (tv != null){
            tv.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    dismissAllowingStateLoss();
                    if (confirmListener != null){
                        confirmListener.onClick(v);
                    }
                }
            });
        }
    }

    /**
     *
     * @Description: 展示弹出框
     * @param manager
     * @param tipsText
     */
    public void showTipsContent(FragmentManager manager, String tipsText) {
        show(manager, tipsText + EPocketTipsDialog.class.getName());
        this.tipsText = tipsText;
    }

    /**
     *
     * @Description: 展示弹出框
     * @param manager
     * @param tipsText
     */
    public void showTipsContent(FragmentManager manager, String tipsText, OnContinuousClickListener confirmListener) {
        show(manager, tipsText + EPocketTipsDialog.class.getName());
        this.tipsText = tipsText;
        this.confirmListener = confirmListener;
    }
}