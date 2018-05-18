package com.cdv.sampling.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.cdv.sampling.R;
import com.cdv.sampling.bean.AppTypes;
import com.cdv.sampling.bean.ClientUnit;
import com.cdv.sampling.bean.ShouYaoCanLiuSample;
import com.cdv.sampling.bean.YangPinHeShi;
import com.cdv.sampling.bean.ZhiLiangChouYang;
import com.cdv.sampling.fragments.DrugFragment;
import com.cdv.sampling.fragments.QualitySamplingFragment;
import com.cdv.sampling.fragments.SampleOperateFragment;
import com.cdv.sampling.fragments.SampleVerificationFragment;
import com.cdv.sampling.widget.EPocketAlertDialog;

import java.util.ArrayList;
import java.util.List;

import static android.R.id.list;

public class AddSamplingActivity extends BaseActivity {

    public static final String EXTRA_SAMPLE = "EXTRA_SAMPLE";
    public static final String EXTRA_COMPANY = "EXTRA_COMPANY";
    public static final String EXTRA_CHOUYANG_HUANJIE = "EXTRA_CHOUYANG_HUANJIE";
    public static final String EXTRA_SIMPLE_CODE_LIST = "EXTRA_SIMPLE_CODE_LIST";

    public static final int SAMPLING_DRUG = 0;
    public static final int SAMPLING_QUALITY = 1;
    public static final int SAMPLING_VERIFICATION = 2;

    private static final String EXTRA_SAMPLING = "EXTRA_SAMPLING";

    private static final String FRAGMENT_TAG = "FRAGMENT_TAG";

    private SampleOperateFragment fragment;

    public static Intent getStartIntent(Context context, YangPinHeShi yangPinHeShi, ClientUnit clientUnit) {
        Intent intent = new Intent(context, AddSamplingActivity.class);
        intent.putExtra(EXTRA_SAMPLING, yangPinHeShi);
        intent.putExtra(EXTRA_COMPANY, clientUnit);
        return intent;
    }

    public static Intent getStartIntent(Context context, ShouYaoCanLiuSample shouYaoCanLiu, ClientUnit clientUnit, AppTypes chouYangHuanJie, ArrayList<String> array) {
        Intent intent = new Intent(context, AddSamplingActivity.class);
        intent.putExtra(EXTRA_SAMPLING, shouYaoCanLiu);
        intent.putExtra(EXTRA_COMPANY, clientUnit);
        intent.putExtra(EXTRA_CHOUYANG_HUANJIE, chouYangHuanJie);
        intent.putStringArrayListExtra(EXTRA_SIMPLE_CODE_LIST, array);
        return intent;
    }

    public static Intent getStartIntent(Context context, ZhiLiangChouYang zhiLiangChouYang, ClientUnit clientUnit, AppTypes chouYangHuanJie) {
        Intent intent = new Intent(context, AddSamplingActivity.class);
        intent.putExtra(EXTRA_SAMPLING, zhiLiangChouYang);
        intent.putExtra(EXTRA_COMPANY, clientUnit);
        intent.putExtra(EXTRA_CHOUYANG_HUANJIE, chouYangHuanJie);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sampling);
        setMyTitle("样品");
        Object obj = getIntent().getSerializableExtra(EXTRA_SAMPLING);
        ClientUnit clientUnit = (ClientUnit) getIntent().getSerializableExtra(EXTRA_COMPANY);
        AppTypes chouYangHuanjie = (AppTypes) getIntent().getSerializableExtra(EXTRA_CHOUYANG_HUANJIE);
        ArrayList<String> list = getIntent().getStringArrayListExtra(EXTRA_SIMPLE_CODE_LIST);
        if (obj == null) {
            finish();
            return;
        }
        fragment = (SampleOperateFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
        if (fragment == null) {
            if (obj instanceof ShouYaoCanLiuSample) {
                fragment = DrugFragment.newInstance((ShouYaoCanLiuSample) obj, clientUnit, chouYangHuanjie, list);
            } else if (obj instanceof ZhiLiangChouYang) {
                fragment = QualitySamplingFragment.newInstance((ZhiLiangChouYang) obj, clientUnit, chouYangHuanjie);
            } else if (obj instanceof YangPinHeShi) {
                fragment = SampleVerificationFragment.newInstance((YangPinHeShi) obj, clientUnit);
            } else {
                finish();
                return;
            }
            addFragment(R.id.activity_add_sampling, fragment, FRAGMENT_TAG);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Object object = getIntent().getSerializableExtra(EXTRA_SAMPLING);
        if (object != null && object instanceof ZhiLiangChouYang){
            getMenuInflater().inflate(R.menu.menu_scan, menu);
        }else{
            getMenuInflater().inflate(R.menu.menu_save_copy, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.save) {
            fragment.onSave();
            return true;
        } else if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.scan) {
            fragment.onScan();
            return true;
        } else if (item.getItemId() == R.id.save_and_copy) {
            fragment.onSaveAndCopy();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        EPocketAlertDialog.getInstance().showAlertContent(getSupportFragmentManager(), "是否保存？", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment.onSave();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public interface OnSampleEditListener {
        void save();
    }
}
