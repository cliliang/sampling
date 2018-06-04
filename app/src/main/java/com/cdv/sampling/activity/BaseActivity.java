package com.cdv.sampling.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.apkfuns.xprogressdialog.XProgressDialog;
import com.cdv.sampling.R;
import com.cdv.sampling.SamplingApplication;
import com.cdv.sampling.repository.UserRepository;
import com.cdv.sampling.rxandroid.CommonSubscriber;
import com.cdv.sampling.rxandroid.RxLocalBroadReceiver;
import com.cdv.sampling.utils.AppUtils;
import com.cdv.sampling.utils.ToastUtils;

import butterknife.ButterKnife;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class BaseActivity extends AppCompatActivity {

    protected static final String TAG = BaseActivity.class.getSimpleName();

    protected SamplingApplication application;

    protected RelativeLayout mainPanel;
    protected LayoutInflater mInflater;
    protected Context mContext;

    protected TextView tvTitleView;
    protected Toolbar mToolBar;
    protected ViewGroup mPanelTitleBarContainer;
    protected LinearLayout panelTitleArea;

    private Subscription subscription;

    private XProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.application = (SamplingApplication) getApplicationContext();
        this.mContext = this;
        mInflater = LayoutInflater.from(this);
        super.onCreate(savedInstanceState);

        super.setContentView(R.layout.title_layout);
        initTitleBar();

        subscription = AppUtils.getLoginStatusBroadcast().subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommonSubscriber<RxLocalBroadReceiver.IntentWithContext>() {
                    @Override
                    public void onNext(RxLocalBroadReceiver.IntentWithContext o) {
                        super.onNext(o);
                        if (!UserRepository.isLogin() && !(BaseActivity.this instanceof LoginActivity)) {
                            finish();
                            startActivity(new Intent(BaseActivity.this, LoginActivity.class));
                        }
                    }
                });
    }

    private void initTitleBar() {
        this.panelTitleArea = (LinearLayout) findViewById(R.id.panel_title_area);
        if (fetchTitleLayout() > 0) {
            View titleView = mInflater.inflate(fetchTitleLayout(), null);
            panelTitleArea.removeAllViews();
            panelTitleArea.addView(titleView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }
        this.tvTitleView = (TextView) findViewById(R.id.tv_title);
        this.mToolBar = (Toolbar) findViewById(R.id.toolbar);
        this.mPanelTitleBarContainer = (ViewGroup) findViewById(R.id.title_container);
        if (mToolBar != null) {
            setSupportActionBar(mToolBar);
            getSupportActionBar().setTitle("");
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_common_home);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    public void hideToolbar() {
        if (this.mPanelTitleBarContainer != null) {
            this.mPanelTitleBarContainer.setVisibility(View.GONE);
        }
    }

    public void showToolbar() {
        if (this.mPanelTitleBarContainer != null) {
            this.mPanelTitleBarContainer.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setContentView(int layoutResID) {
        View childView = LayoutInflater.from(this).inflate(layoutResID, null);
        this.setContentView(childView);
    }

    @Override
    public void setContentView(View view) {
        this.setContentView(view, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {

        mainPanel = (RelativeLayout) findViewById(R.id.main_panel);
        RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(params);
        mainPanel.addView(view, relativeParams);
        ButterKnife.bind(this);
    }

    public void setMyTitle(String text) {
        if (mToolBar == null){
            return;
        }
        mToolBar.setTitle(text);
    }

    public void setMyTitle(int resID) {
        setMyTitle(getString(resID));
    }

    public String getMyTitle() {
        if (this.tvTitleView != null) {
            return tvTitleView.getText().toString();
        }
        return null;
    }

    @Override
    public void onBackPressed() {
        if (onBackAction()) {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 返回的操作处理事件
     *
     * @return true表示添加自定义操作后，自动finish
     */
    protected boolean onBackAction() {
        return true;
    }


    protected int fetchTitleLayout() {
        return -1;
    }

    protected void addFragment(int containerViewId, Fragment fragment) {
        FragmentTransaction fragmentTransaction = this.getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(containerViewId, fragment);
        fragmentTransaction.commit();
    }

    protected void addFragment(int containerViewId, Fragment fragment, String tag) {
        FragmentTransaction fragmentTransaction = this.getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(containerViewId, fragment, tag);
        fragmentTransaction.commit();
    }

    private PreferenceManager.OnActivityResultListener listener;

    public void startActivityForResult(Intent intent, int requestCode, PreferenceManager.OnActivityResultListener listener) {
        if (listener != null) {
            this.listener = listener;
        }
        super.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (listener != null) {
            this.listener.onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void showToast(String msg) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        ToastUtils.show(this, msg);
    }

    public void showProgressDialog(String msg) {
        if (mProgressDialog != null){
            mProgressDialog.dismiss();

        }
        mProgressDialog = new XProgressDialog(this, msg, XProgressDialog.THEME_CIRCLE_PROGRESS);
        mProgressDialog.show();

    }

    public void dismissProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    public void showToast(int resId) {
        showToast(getString(resId));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (subscription != null || !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }
}
