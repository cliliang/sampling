package com.cdv.sampling.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;

import com.apkfuns.xprogressdialog.XProgressDialog;
import com.cdv.sampling.R;
import com.cdv.sampling.SamplingApplication;
import com.cdv.sampling.bean.UserBean;
import com.cdv.sampling.constants.StorageConstants;
import com.cdv.sampling.exception.ErrorMessageFactory;
import com.cdv.sampling.net.HttpService;
import com.cdv.sampling.net.OperatorRequestMap;
import com.cdv.sampling.repository.UserRepository;
import com.cdv.sampling.storage.SamplingStorage;
import com.cdv.sampling.utils.AppUtils;
import com.cdv.sampling.utils.ToastUtils;

import butterknife.BindView;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class LoginActivity extends BaseActivity implements ViewTreeObserver.OnGlobalLayoutListener {

    @BindView(R.id.et_username)
    EditText etUsername;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.rootView)
    View rootView;
    @BindView(R.id.bottomPanel)
    View bottomView;
    @BindView(R.id.topPanel)
    View topPanel;


    public static Intent getStartIntent(Context context) {
        return new Intent(context, LoginActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etUsername.setText(SamplingStorage.getInstance().getStringValue(StorageConstants.KEY_LAST_USER_NAME, ""));
        hideToolbar();
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        rootView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
    }

    @OnClick(R.id.tv_submit)
    void login() {
        AppUtils.hideKeyboard(this);
        final String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();
        if (TextUtils.isEmpty(username)) {
            ToastUtils.show(this, "请输入用户名！");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            ToastUtils.show(this, "请输入密码！");
            return;
        }
        final XProgressDialog dialog = new XProgressDialog(this, "正在加载..", XProgressDialog.THEME_CIRCLE_PROGRESS);
        dialog.show();
        HttpService.getInstance().getApi().login(username, password)
                .lift(new OperatorRequestMap<UserBean>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<UserBean>() {
                    @Override
                    public void call(UserBean userBean) {
                        dialog.dismiss();
                        SamplingStorage.getInstance().storeStringValue(StorageConstants.KEY_LAST_USER_NAME, username);
                        UserRepository.getInstance().setUser(userBean);
                        SamplingApplication.getInstance().initDBFormUser();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        dialog.dismiss();
                        showToast(ErrorMessageFactory.create(throwable));
                    }
                });
    }

    @OnClick(R.id.tv_ip_config)
    void configIp(){
        startActivity(new Intent(LoginActivity.this, IPConfigActivity.class));
    }

    @Override
    public void onGlobalLayout() {
        Rect r = new Rect();
        rootView.getWindowVisibleDisplayFrame(r);
        int heightDiff = rootView.getRootView().getHeight() - (r.bottom - r.top);
        if (heightDiff > 100) {
            topPanel.setVisibility(View.GONE);
            bottomView.setVisibility(View.GONE);
        } else {
            topPanel.setVisibility(View.VISIBLE);
            bottomView.setVisibility(View.VISIBLE);
        }
    }
}
