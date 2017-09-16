package com.cdv.sampling.widget;

import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.cdv.sampling.R;


public class SendEmailDialog extends EPDialog {

    protected static SendEmailDialog mDialog;
    protected EditText etEmail;
    protected InputListener inputListener;


    private String email;

    public SendEmailDialog() {
    }

    public static SendEmailDialog getInstance() {
        return new SendEmailDialog();
    }

    @Override
    protected int getContentViewId() {
        return R.layout.dialog_send_email;
    }

    @Override
    protected void initViews() {
        super.initViews();

        final EditText editText = (EditText) contentView.findViewById(R.id.et_email);
        TextView tv = (TextView) contentView.findViewById(R.id.dialog_confirm_tv);
        setCancelable(false);
        if (editText != null){
            editText.setText(email);
        }
        setCancelable(true);
        if (tv != null){
            tv.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    dismissAllowingStateLoss();
                    if (inputListener != null){
                        inputListener.onInput(editText.getText().toString());
                    }
                }
            });
        }
    }

    public void showEmail(FragmentManager manager, String email, InputListener inputListener) {
        show(manager, email + SendEmailDialog.class.getName());
        this.email = email;
        this.inputListener = inputListener;
    }

    public interface InputListener{
        void onInput(String input);
    }
}