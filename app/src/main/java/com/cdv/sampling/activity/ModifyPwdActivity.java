package com.cdv.sampling.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.cdv.sampling.R;
import com.cdv.sampling.bean.UserBean;
import com.cdv.sampling.exception.ErrorMessageFactory;
import com.cdv.sampling.net.HttpService;
import com.cdv.sampling.net.OperatorRequestMap;
import com.cdv.sampling.repository.UserRepository;
import com.cdv.sampling.rxandroid.CommonSubscriber;
import com.cdv.sampling.widget.InputLayout;

import butterknife.BindView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ModifyPwdActivity extends BaseActivity {

    @BindView(R.id.phone_input)
    InputLayout phoneInput;
    @BindView(R.id.pwd_input)
    InputLayout pwdInput;

    public static Intent getStartIntent(Context context){
        return new Intent(context, ModifyPwdActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_pwd);
        setMyTitle("修改账户");

        phoneInput.setContent(UserRepository.getInstance().getCurrentUser().getTelephone());
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
                modifyUser();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void modifyUser() {
        if (TextUtils.isEmpty(pwdInput.getContent())){
            showToast("密码不能为空！");
            return;
        }
        if (TextUtils.isEmpty(phoneInput.getContent())){
            showToast("电话不能为空！");
            return;
        }
        showProgressDialog("处理中..");
        HttpService.getApi().changeInfo(UserRepository.getInstance().getCurrentUser().getID(), pwdInput.getContent(), phoneInput.getContent())
                .lift(new OperatorRequestMap<UserBean>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommonSubscriber<UserBean>(){

                    @Override
                    public void onNext(UserBean o) {
                        super.onNext(o);
                        dismissProgressDialog();
                        showToast("修改成功！");
                        finish();
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        dismissProgressDialog();
                        showToast(ErrorMessageFactory.create(e));
                    }
                });
    }
}
