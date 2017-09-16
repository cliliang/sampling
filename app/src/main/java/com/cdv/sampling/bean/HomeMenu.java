package com.cdv.sampling.bean;

import android.support.annotation.DrawableRes;

import com.cdv.sampling.R;

public enum HomeMenu {

    USER_MANAGER("用户管理", R.drawable.ic_user_manager),
    VETERINARY_DRUG_RESIDUES("残留抽样", R.drawable.ic_drug),
    SAMPLE_VERIFICATION("样品核实", R.drawable.ic_sample),
    QUALITY_SAMPLING("兽药抽样", R.drawable.ic_quality),
    DATA_MANAGEMENT("数据管理", R.drawable.ic_data_manager),
    SAMPLE_LIST("样品列表", R.drawable.ic_sample_list),
    ACCOUNT_MANAGER("账号管理", R.drawable.ic_account);

    private String menuName;
    private int imageResId;

    HomeMenu(String menuName, @DrawableRes int imageResId) {
        this.menuName = menuName;
        this.imageResId = imageResId;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public int getImageResId() {
        return imageResId;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }
}
