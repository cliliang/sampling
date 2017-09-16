package com.cdv.sampling.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.apkfuns.xprogressdialog.XProgressDialog;
import com.cdv.sampling.R;
import com.cdv.sampling.bean.UserBean;
import com.cdv.sampling.exception.ErrorMessageFactory;
import com.cdv.sampling.net.HttpService;
import com.cdv.sampling.net.OperatorRequestMap;
import com.cdv.sampling.rxandroid.CommonSubscriber;
import com.cdv.sampling.widget.InputLayout;

import butterknife.BindView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class AddUserActivity extends BaseActivity {

    private static final String EXTRA_USER = "EXTRA_USER";

    @BindView(R.id.username_input)
    InputLayout mUserNameInput;
    @BindView(R.id.account_input)
    InputLayout mAccountInput;
    @BindView(R.id.phone_input)
    InputLayout mPhoneInput;
    @BindView(R.id.pwd_input)
    InputLayout mPwdInput;

    private UserBean userBean;
    private boolean isForModify = false;

    public static Intent getStartIntent(Context context, UserBean userBean) {
        Intent intent = new Intent(context, AddUserActivity.class);
        intent.putExtra(EXTRA_USER, userBean);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);
        userBean = (UserBean) getIntent().getSerializableExtra(EXTRA_USER);
        if (userBean != null) {
            isForModify = true;
            mUserNameInput.setContent(userBean.getUserName());
            mPhoneInput.setContent(userBean.getTelephone());
            mAccountInput.setContent(userBean.getAccount());
            mPwdInput.setContent(userBean.getPassword());
            mAccountInput.setEnabled(false);
            setMyTitle("修改用户");
        } else {
            setMyTitle("添加用户");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_user, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.confirm:
                if (isForModify) {
                    modifyUser();
                } else {
                    addUser();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void modifyUser() {
        if (TextUtils.isEmpty(mUserNameInput.getContent())) {
            showToast("请输入姓名！");
            return;
        }
        final XProgressDialog dialog = new XProgressDialog(this, "正在加载..", XProgressDialog.THEME_CIRCLE_PROGRESS);
        dialog.show();
        HttpService.getApi().modify(userBean.getID(), mUserNameInput.getContent(), mPhoneInput.getContent(), mPwdInput.getContent()).lift(new OperatorRequestMap<UserBean>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommonSubscriber<UserBean>() {
                    @Override
                    public void onNext(UserBean o) {
                        super.onNext(o);
                        dialog.dismiss();
                        showToast("操作成功！");
                        setResult(RESULT_OK);
                        finish();
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        dialog.dismiss();
                        showToast(ErrorMessageFactory.create(e));
                    }
                });
    }

    private void addUser() {

        if (TextUtils.isEmpty(mAccountInput.getContent())) {
            showToast("请输入用户名！");
            return;
        }
        if (TextUtils.isEmpty(mUserNameInput.getContent())) {
            showToast("请输入姓名！");
            return;
        }

        if (TextUtils.isEmpty(mPwdInput.getContent())) {
            showToast("请输入密码！");
            return;
        }

        if (userBean == null) {
            userBean = new UserBean();
        }
        userBean.setUserName(mUserNameInput.getContent());
        userBean.setAccount(mAccountInput.getContent());
        userBean.setTelephone(mPhoneInput.getContent());
        userBean.setPassword(mPwdInput.getContent());
        final XProgressDialog dialog = new XProgressDialog(this, "正在加载..", XProgressDialog.THEME_CIRCLE_PROGRESS);
        dialog.show();
        HttpService.getApi().createUser(userBean).lift(new OperatorRequestMap<UserBean>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommonSubscriber<UserBean>() {
                    @Override
                    public void onNext(UserBean o) {
                        super.onNext(o);
                        dialog.dismiss();
                        showToast("操作成功！");
                        setResult(RESULT_OK);
                        finish();
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        dialog.dismiss();
                        showToast(ErrorMessageFactory.create(e));
                    }
                });
    }
}
