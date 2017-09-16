package com.cdv.sampling.widget;

import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.cdv.sampling.R;


public class DeleteDataDialog extends EPDialog {

    public static final int TYPE_ARCHIVED_FORM = 0;
    public static final int TYPE_NOT_FINISHED_FORM = 1;
    public static final int TYPE_ALL_FORM = 2;
    public static final int TYPE_ALL_DATA = 3;

    protected static DeleteDataDialog mDialog;
    protected OnDeleteListener deleteListener;

    private RadioButton rbArchived;
    private RadioButton rbNotFinished;
    private RadioButton rbAllData;
    private RadioButton rbAllForm;
    private RadioGroup radioGroup;

    public DeleteDataDialog() {
    }

    public static DeleteDataDialog getInstance() {
        return new DeleteDataDialog();
    }

    @Override
    protected int getContentViewId() {
        return R.layout.dialog_delete_data;
    }

    @Override
    protected void initViews() {
        super.initViews();

        TextView tv = (TextView) contentView.findViewById(R.id.dialog_confirm_tv);
        setCancelable(true);
        rbAllData = (RadioButton) contentView.findViewById(R.id.rb_all_data);
        rbArchived = (RadioButton) contentView.findViewById(R.id.rb_archived_form);
        rbNotFinished = (RadioButton) contentView.findViewById(R.id.rb_not_finished_form);
        rbAllForm = (RadioButton) contentView.findViewById(R.id.rb_all_form);
        radioGroup = (RadioGroup) contentView.findViewById(R.id.rb_clear_type);
        if (tv != null){
            tv.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    dismissAllowingStateLoss();
                    if (deleteListener != null){
                        int type = TYPE_NOT_FINISHED_FORM;
                        if (radioGroup.getCheckedRadioButtonId() == rbNotFinished.getId()) {
                            type = TYPE_NOT_FINISHED_FORM;
                        } else if (radioGroup.getCheckedRadioButtonId() == rbArchived.getId()) {
                            type = TYPE_ARCHIVED_FORM;
                        } else if (radioGroup.getCheckedRadioButtonId() == rbAllForm.getId()) {
                            type = TYPE_ALL_FORM;
                        } else if (radioGroup.getCheckedRadioButtonId() == rbAllData.getId()) {
                            type = TYPE_ALL_DATA;
                        }
                        deleteListener.deleteDataByType(type);
                    }
                }
            });
        }
    }

    public void showDeleteDialog(FragmentManager manager, OnDeleteListener deleteListener) {
        show(manager, DeleteDataDialog.class.getName());
        this.deleteListener = deleteListener;
    }

    public interface OnDeleteListener{
        void deleteDataByType(int type);
    }
}