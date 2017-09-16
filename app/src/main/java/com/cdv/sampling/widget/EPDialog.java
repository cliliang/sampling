/**
 * @Title: FanrDialog.java
 * @Package com.whistle.gamefanr.widget
 * @Description: TODO(用一句话描述该文件做什么)
 * @author xuyingjian@ruijie.com.cn
 * @date 2015年4月21日 下午3:33:17
 */
package com.cdv.sampling.widget;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cdv.sampling.R;
import com.cdv.sampling.utils.UIUtils;

import java.lang.reflect.Field;


public abstract class EPDialog extends DialogFragment {

    protected static final int DEFAULT_LEFT_MARGIN_DIP = 24;
    protected FragmentActivity baseActivity;
    protected View contentView;
    protected OnEPDismissListener mDismissListener;


    protected abstract int getContentViewId();

    /**
     *
     */
    public EPDialog() {
        baseActivity = getActivity();
    }

    protected int getMargins() {
        return UIUtils.getScreenSize(getActivity()).x / 18;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        contentView = inflater.inflate(getContentViewId(), container);
        initViews();
        return contentView;
    }

    protected void initViews() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.base_dialog);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int height = getHeight();
            if (height < 0){
                height = ViewGroup.LayoutParams.WRAP_CONTENT;
            }
            dialog.getWindow().setLayout(UIUtils.getScreenSize(getActivity()).x - 2 * getMargins(), height);
        }
    }

    protected int getHeight(){
        return  -1;
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        if (isAdded()) {
            return;
        }

        FragmentTransaction ft = manager.beginTransaction();
        ft.add(this, tag);
        ft.commitAllowingStateLoss();

        changeDialogFragmentFieldValue("mDismissed", false);
        changeDialogFragmentFieldValue("mShownByMe", true);

    }

    private void changeDialogFragmentFieldValue(String fieldName, boolean value) {
        try {
            Field field = DialogFragment.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(this, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param mDismissListener the mDismissListener to set
     */
    public void setmDismissListener(OnEPDismissListener mDismissListener) {
        this.mDismissListener = mDismissListener;
    }

    @Override
    public void onDestroyView() {
        if (mDismissListener != null) {
            mDismissListener.onDismiss(this);
        }
        super.onDestroyView();
    }

    @Override
    public void dismiss() {
        dismissAllowingStateLoss();
    }

    /**
     * fix bug : https://code.google.com/p/android/issues/detail?id=42601
     */
    @Override
    public void onDetach() {
        super.onDetach();
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        } catch (Throwable e){
            e.printStackTrace();
        }
    }

    public interface OnEPDismissListener {
        public void onDismiss(DialogFragment dialogFragment);
    }
}
