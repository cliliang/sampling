package com.cdv.sampling.fragments;

import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.widget.Toast;

import com.cdv.sampling.utils.ToastUtils;

public class BaseFragment extends Fragment {

    public void showToast(String text){
        if (getActivity() == null){
            return;
        }
        if(TextUtils.isEmpty(text)){
            return;
        }
        ToastUtils.show(getActivity(), text, Toast.LENGTH_SHORT);
    }

    public void showToast(int resId){
        if (getActivity() == null){
            return;
        }
        ToastUtils.show(getActivity(), resId, Toast.LENGTH_SHORT);
    }
}
