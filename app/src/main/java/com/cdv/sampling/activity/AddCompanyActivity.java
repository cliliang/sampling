package com.cdv.sampling.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.apkfuns.xprogressdialog.XProgressDialog;
import com.cdv.sampling.R;
import com.cdv.sampling.SamplingApplication;
import com.cdv.sampling.bean.ClientUnit;
import com.cdv.sampling.exception.DataFormatException;
import com.cdv.sampling.exception.ErrorMessageFactory;
import com.cdv.sampling.rxandroid.CommonSubscriber;
import com.cdv.sampling.utils.AppUtils;
import com.cdv.sampling.utils.ListUtils;
import com.cdv.sampling.utils.PinYinUtil;
import com.cdv.sampling.widget.EPocketAlertDialog;
import com.cdv.sampling.widget.InputLayout;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class AddCompanyActivity extends BaseActivity {

    private static final String EXTRA_COMPANY_BEAN = "EXTRA_COMPANY_BEAN";
    private static final int REQUEST_CODE_SELECTED_BEAN = 10001;
    private static final int REQUEST_CODE_SCAN = 1;

    @BindView(R.id.input_company_name)
    InputLayout inputCompanyName;
    @BindView(R.id.input_business_license)
    InputLayout inputBusinessLicense;
    @BindView(R.id.input_address)
    InputLayout inputAddress;
    @BindView(R.id.input_postcode)
    InputLayout inputPostcode;
    @BindView(R.id.input_contact_people)
    InputLayout inputContactPeople;
    @BindView(R.id.input_contact_phone)
    InputLayout inputContactPhone;
    @BindView(R.id.input_contact_fax)
    InputLayout inputContactFax;
    @BindView(R.id.input_email)
    InputLayout inputEmail;
    @BindView(R.id.activity_add_company)
    LinearLayout activityAddCompany;

    private List<InputLayout> mInputList = new ArrayList<>();

    public static Intent getStartIntent(Context context, ClientUnit clientUnit) {
        Intent intent = new Intent(context, AddCompanyActivity.class);
        intent.putExtra(EXTRA_COMPANY_BEAN, clientUnit);
        return intent;
    }

    private ClientUnit clientUnit;
    private String clientName = "", clientAddress = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_company);
        ButterKnife.bind(this);

        setMyTitle("单位");
        clientUnit = (ClientUnit) getIntent().getSerializableExtra(EXTRA_COMPANY_BEAN);
        if (clientUnit != null) {
            clientName = clientUnit.getName();
            clientAddress = clientUnit.getAddress();
            inputCompanyName.setContent(clientUnit.getName());
            inputEmail.setContent(clientUnit.getEmail());
            inputAddress.setContent(clientUnit.getAddress());
            inputBusinessLicense.setContent(clientUnit.getBusinessCode());
            inputPostcode.setContent(clientUnit.getZip());
            inputContactPhone.setContent(clientUnit.getTelephone());
            inputContactFax.setContent(clientUnit.getFax());
            inputContactPeople.setContent(clientUnit.getContactUser());
        }

        mInputList.add(inputCompanyName);
        mInputList.add(inputEmail);
        mInputList.add(inputAddress);
        mInputList.add(inputBusinessLicense);
        mInputList.add(inputPostcode);
        mInputList.add(inputContactPhone);
        mInputList.add(inputContactFax);
        mInputList.add(inputContactPeople);
    }


    private void scan() {
        startActivityForResult(new Intent(this, ScanActivity.class), REQUEST_CODE_SCAN, new PreferenceManager.OnActivityResultListener() {

            @Override
            public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
                if (REQUEST_CODE_SCAN == requestCode && resultCode == Activity.RESULT_OK) {
                    String str = data.getStringExtra(ScanActivity.EXTRA_SCAN_RESULT);
                    if (str != null) {
                        str = str.replace("\n", "").trim();
                        String[] params = str.replace("\n", "").trim().split("，");
                        for (String item : params) {
                            String[] itemParam = item.split("：");
                            if (itemParam.length != 2) {
                                continue;
                            }
                            for (InputLayout inputLayout : mInputList) {
                                if (inputLayout.getTitle().equals(itemParam[0])) {
                                    inputLayout.setContent(itemParam[1]);
                                    break;
                                }
                            }
                        }
                    }

                    return true;
                }
                return false;
            }
        });

    }

    private void save() {
        if (!inputCompanyName.validate()) {
            showToast("单位名称输入不正确！");
            return;
        }
        if (clientUnit != null){
            String name = inputCompanyName.getContent();
            String address = inputAddress.getContent();
            if (!clientName.equals(name) || !clientAddress.equals(address)){
                clientUnit = new ClientUnit();
            }
        }else {
            clientUnit = new ClientUnit();
        }

        final XProgressDialog dialog = new XProgressDialog(this, "正在加载..", XProgressDialog.THEME_CIRCLE_PROGRESS);
        dialog.show();
        Observable.just(clientUnit)
                .map(new Func1<ClientUnit, ClientUnit>() {
                    @Override
                    public ClientUnit call(ClientUnit clientUnit) {
                        if (!AppUtils.isHaveId(clientUnit.getLocalID())) {
                            List<ClientUnit> list = SamplingApplication.getDaoSession().getClientUnitDao().queryRaw("where Name=?", inputCompanyName.getContent());
                            if (!ListUtils.isEmpty(list)) {
                                throw new DataFormatException("该单位名已存在，不能重复添加！");
                            }
                        }
                        clientUnit.setEmail(inputEmail.getContent());
                        clientUnit.setName(inputCompanyName.getContent());
                        clientUnit.setAddress(inputAddress.getContent());
                        clientUnit.setBusinessCode(inputBusinessLicense.getContent());
                        clientUnit.setZip(inputPostcode.getContent());
                        clientUnit.setTelephone(inputContactPhone.getContent());
                        clientUnit.setFax(inputContactFax.getContent());
                        clientUnit.setIsInvalidate(0);
                        clientUnit.setLastModifyTime(new Date());
                        clientUnit.setContactUser(inputContactPeople.getContent());
                        clientUnit.setShortName(PinYinUtil.getSearchKeyForPinyin(inputCompanyName.getContent()));
                        SamplingApplication.getDaoSession().getClientUnitDao().save(clientUnit);
                        SamplingApplication.getDaoSession().getClientUnitDao().refresh(clientUnit);
                        return clientUnit;
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommonSubscriber<ClientUnit>() {

                    @Override
                    public void onNext(ClientUnit clientUnit) {
                        super.onNext(clientUnit);
                        Intent intent = new Intent();
                        intent.putExtra(SearchCompanyActivity.RESULT_KEY_SELECTED, clientUnit);
                        setResult(RESULT_OK, intent);
                        finish();
                        showToast("保存成功！");
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        dialog.dismiss();
                        showToast(ErrorMessageFactory.create(e));
                    }

                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                        dialog.dismiss();
                    }
                });
    }

    @OnClick({R.id.tab_save, R.id.tab_clear, R.id.tab_search, R.id.tab_scan})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tab_save:
                save();
                break;
            case R.id.tab_clear:
                clearForm();
                break;
            case R.id.tab_search:
                search();
                break;
            case R.id.tab_scan:
                scan();
                break;
        }
    }

    private void search() {
        ClientUnit currentUnit = new ClientUnit();
        currentUnit.setEmail(inputEmail.getContent());
        currentUnit.setName(inputCompanyName.getContent());
        currentUnit.setAddress(inputAddress.getContent());
        currentUnit.setBusinessCode(inputBusinessLicense.getContent());
        currentUnit.setZip(inputPostcode.getContent());
        currentUnit.setTelephone(inputContactPhone.getContent());
        currentUnit.setFax(inputContactFax.getContent());
        currentUnit.setContactUser(inputContactPeople.getContent());
        currentUnit.setShortName(PinYinUtil.getSearchKeyForPinyin(inputCompanyName.getContent()));
        startActivityForResult(SearchCompanyActivity.getStartIntent(this, currentUnit, inputCompanyName.getContent()), REQUEST_CODE_SELECTED_BEAN);
    }

    private void clearForm() {
        EPocketAlertDialog.getInstance().showAlertContent(getSupportFragmentManager(), "确定要清空单位？", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputCompanyName.setContent("");
                inputEmail.setContent("");
                inputAddress.setContent("");
                inputBusinessLicense.setContent("");
                inputPostcode.setContent("");
                inputContactPhone.setContent("");
                inputContactFax.setContent("");
                inputContactPeople.setContent("");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_SELECTED_BEAN) {
            setResult(RESULT_OK, data);
            finish();
        }
    }
}
