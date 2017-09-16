package com.cdv.sampling.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.apkfuns.xprogressdialog.XProgressDialog;
import com.cdv.sampling.R;
import com.cdv.sampling.bean.HomeMenu;
import com.cdv.sampling.bean.JianCeDan;
import com.cdv.sampling.net.HttpService;
import com.cdv.sampling.repository.UserRepository;
import com.cdv.sampling.rxandroid.CommonSubscriber;
import com.cdv.sampling.utils.UIUtils;
import com.cdv.sampling.widget.EPocketAlertDialog;
import com.cdv.sampling.widget.OnContinuousClickListener;

import butterknife.BindView;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends BaseActivity {

    private static final HomeMenu[] allMenuArray = new HomeMenu[]{HomeMenu.USER_MANAGER, HomeMenu.VETERINARY_DRUG_RESIDUES, HomeMenu.SAMPLE_VERIFICATION, HomeMenu.QUALITY_SAMPLING, HomeMenu.SAMPLE_LIST, HomeMenu.DATA_MANAGEMENT, HomeMenu.ACCOUNT_MANAGER};
    private static final HomeMenu[] adminMenuArray = new HomeMenu[]{HomeMenu.USER_MANAGER, HomeMenu.ACCOUNT_MANAGER};
    private static final HomeMenu[] normalMenuArray = new HomeMenu[]{HomeMenu.VETERINARY_DRUG_RESIDUES, HomeMenu.SAMPLE_VERIFICATION, HomeMenu.QUALITY_SAMPLING, HomeMenu.SAMPLE_LIST, HomeMenu.DATA_MANAGEMENT, HomeMenu.ACCOUNT_MANAGER};

    private HomeMenu[] menuArray = null;

    @BindView(R.id.panel_menu_parent)
    LinearLayout panelMenuParent;

    @BindView(R.id.tv_username)
    TextView tvUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        hideToolbar();

        initMenuItems();
        tvUserName.setText(UserRepository.getInstance().getCurrentUser().getAccount());
    }

    private void initMenuItems() {
        menuArray = allMenuArray;

        LinearLayout panelLinearParent = null;
        int menuHorCount = 3;
        for (int i = 0; i < menuArray.length; i++) {

            if (i % menuHorCount == 0) {
                panelLinearParent = new LinearLayout(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.topMargin = UIUtils.convertDpToPixel(12, this);
                params.bottomMargin = UIUtils.convertDpToPixel(12, this);
                panelLinearParent.setLayoutParams(params);
                if (menuArray.length % menuHorCount == 1) {
                    panelLinearParent.setGravity(Gravity.CENTER);
                }
                panelLinearParent.setOrientation(LinearLayout.HORIZONTAL);
                panelMenuParent.addView(panelLinearParent);
                panelLinearParent.setWeightSum(menuHorCount);

                if (menuArray.length > (i + 1)) {
                    View view = new View(this);
                    view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, UIUtils.convertDpToPixel(1, this)));
                    view.setBackgroundResource(R.drawable.menu_hor_divider);
                    panelMenuParent.addView(view);
                }
            }
            final View menuView = mInflater.inflate(R.layout.item_home_menu, null);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.weight = 1;
            panelLinearParent.addView(menuView, params);

            TextView tvMenu = (TextView) menuView.findViewById(R.id.tv_menu);
            ImageView ivMenu = (ImageView) menuView.findViewById(R.id.iv_menu);
            tvMenu.setText(menuArray[i].getMenuName());
            ivMenu.setImageResource(menuArray[i].getImageResId());
            menuView.setTag(menuArray[i]);
            if (UserRepository.isAdmin()){
                tvMenu.setTextColor(isAdminMenu(menuArray[i]) ? getResources().getColor(R.color.black) : getResources().getColor(R.color.color_ddd));
                if (isAdminMenu(menuArray[i])){
                    menuView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onMenuClicked((HomeMenu) v.getTag());
                        }
                    });
                }
            }else{
                tvMenu.setTextColor(isNormalMenu(menuArray[i]) ? getResources().getColor(R.color.black) : getResources().getColor(R.color.color_ddd));
                if (isNormalMenu(menuArray[i])){
                    menuView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onMenuClicked((HomeMenu) v.getTag());
                        }
                    });
                }
            }
        }
    }

    private boolean isAdminMenu(HomeMenu menu){
        for (HomeMenu homeMenu : adminMenuArray){
            if (homeMenu == menu){
                return true;
            }
        }
        return false;
    }

    private boolean isNormalMenu(HomeMenu menu){
        for (HomeMenu homeMenu : normalMenuArray){
            if (homeMenu == menu){
                return true;
            }
        }
        return false;
    }

    private void onMenuClicked(HomeMenu menu) {
        if (menu == null) {
            return;
        }
        switch (menu) {
            case USER_MANAGER:
                startActivity(UserListActivity.getStartIntent(this));
                break;
            case VETERINARY_DRUG_RESIDUES:
                startActivity(FormDetailActivity.getStartIntent(this, JianCeDan.DAN_TYPE_SAMPLING_DRUG));
                break;
            case QUALITY_SAMPLING:
                startActivity(FormDetailActivity.getStartIntent(this, JianCeDan.DAN_TYPE_SAMPLING_QUALITY));
                break;
            case SAMPLE_VERIFICATION:
                startActivity(FormDetailActivity.getStartIntent(this, JianCeDan.DAN_TYPE_SAMPLING_VERIFICATION));
                break;
            case SAMPLE_LIST:
                startActivity(FormListActivity.getStartIntent(this));
                break;
            case DATA_MANAGEMENT:
                startActivity(DataManagerActivity.getStartIntent(this));
                break;
            case ACCOUNT_MANAGER:
                startActivity(ModifyPwdActivity.getStartIntent(this));
                break;
        }
    }

    @OnClick(R.id.tv_exit_login)
    public void onClick() {
        EPocketAlertDialog.getInstance().showAlertContent(getSupportFragmentManager(), "确定要退出账号？", new OnContinuousClickListener() {
            @Override
            public void onContinuousClick(View v) {
                logOut();
            }
        });
    }

    void logOut() {
        final XProgressDialog dialog = new XProgressDialog(this, "正在加载..", XProgressDialog.THEME_CIRCLE_PROGRESS);
        dialog.show();
        HttpService.getApi().logOut()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommonSubscriber() {
                    @Override
                    public void onNext(Object o) {
                        super.onNext(o);
                        dialog.dismiss();
                        UserRepository.getInstance().logOut();
                        finish();
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        dialog.dismiss();
                        UserRepository.getInstance().logOut();
                        finish();
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    }
                });
    }
}
