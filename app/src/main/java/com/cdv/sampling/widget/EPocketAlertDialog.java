package com.cdv.sampling.widget;

import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.cdv.sampling.R;


public class EPocketAlertDialog extends EPDialog {

    private TextView tvContent;
    private TextView tvTitle;
    private String alertText;
    private String confirmText;
    private String titleText;
    private String cancelText;

    private OnClickListener confirmListener, cancelListener;

    public EPocketAlertDialog() {
    }

    public static EPocketAlertDialog getInstance() {
        return new EPocketAlertDialog();
    }

    @Override
    protected int getContentViewId() {
        return R.layout.dialog_alert_layout;
    }

    @Override
    protected void initViews() {
        super.initViews();
        tvContent = (TextView) contentView.findViewById(R.id.tips_content_tv);
        TextView cancelTv = (TextView) contentView
                .findViewById(R.id.dialog_cancel_tv);
        tvContent.setText(alertText);
        if (cancelListener != null) {
            cancelTv.setOnClickListener(cancelListener);
        } else {
            cancelTv.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    dismissAllowingStateLoss();
                }
            });

        }
        TextView confirmTv = (TextView) contentView
                .findViewById(R.id.dialog_confirm_tv);

        if (confirmListener == null) {
            confirmTv.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    dismissAllowingStateLoss();
                }
            });
        } else {
            confirmTv.setOnClickListener(new OnContinuousClickListener() {
                @Override
                public void onContinuousClick(View v) {
                    dismissAllowingStateLoss();
                    confirmListener.onClick(v);
                }
            });
        }

        if (!TextUtils.isEmpty(cancelText)) {
            cancelTv.setText(cancelText);
        } else {
            cancelTv.setText("取消");
        }
        if (!TextUtils.isEmpty(confirmText)) {
            confirmTv.setText(confirmText);
        } else {
            confirmTv.setText("确定");
        }
        tvTitle = (TextView) contentView.findViewById(R.id.tv_title);
        if (tvTitle != null){
            tvTitle.setText(titleText);
        }
    }

    /**
     * @param manager
     * @param alertText
     * @Description: 展示弹出框
     */
    public void showAlertContent(FragmentManager manager, String alertText) {
        this.alertText = alertText;
        this.confirmText = null;
        this.cancelText = null;
        show(manager, alertText + EPocketAlertDialog.class.getName());
    }

    public void showAlertContent(FragmentManager manager, String alertText, OnClickListener confirmListner) {
        this.alertText = alertText;
        this.confirmListener = confirmListner;
        this.confirmText = null;
        this.cancelText = null;
        show(manager, alertText + EPocketAlertDialog.class.getName());
    }

    public void showAlertContent(FragmentManager manager, String title, String alertText, OnClickListener confirmListner) {
        this.alertText = alertText;
        this.confirmListener = confirmListner;
        this.confirmText = null;
        this.cancelText = null;
        this.titleText = title;
        show(manager, alertText + EPocketAlertDialog.class.getName());
    }

    public void showAlertContent(FragmentManager manager, String title, String alertText, OnClickListener confirmListner, OnClickListener cancelListener) {
        this.alertText = alertText;
        this.confirmListener = confirmListner;
        this.confirmText = null;
        this.cancelText = null;
        this.titleText = title;
        this.cancelListener = cancelListener;
        show(manager, alertText + EPocketAlertDialog.class.getName());
    }

    public void showAlertContent(FragmentManager manager, String alertText, OnClickListener confirmListner, OnClickListener cancelListener) {
        this.alertText = alertText;
        this.confirmText = null;
        this.cancelText = null;
        this.confirmListener = confirmListner;
        this.cancelListener = cancelListener;
        show(manager, alertText + EPocketAlertDialog.class.getName());
    }

    public void showAlertContent(FragmentManager manager, String confirmText, String cancelText, String alertText, OnClickListener confirmListner, OnClickListener cancelListener) {
        this.alertText = alertText;
        this.confirmText = confirmText;
        this.cancelText = cancelText;
        this.confirmListener = confirmListner;
        this.cancelListener = cancelListener;
        show(manager, alertText + EPocketAlertDialog.class.getName());
    }

    public void showAlertContent(FragmentManager manager, String titleText, String confirmText, String cancelText, String alertText, OnClickListener confirmListner, OnClickListener cancelListener) {
        this.alertText = alertText;
        this.titleText = titleText;
        this.confirmText = confirmText;
        this.cancelText = cancelText;
        this.confirmListener = confirmListner;
        this.cancelListener = cancelListener;
        show(manager, alertText + EPocketAlertDialog.class.getName());
    }
}
