package com.cdv.sampling.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.cdv.sampling.R;
import com.cdv.sampling.SamplingApplication;
import com.cdv.sampling.activity.AddCompanyActivity;
import com.cdv.sampling.activity.AppTypeListActivity;
import com.cdv.sampling.activity.PaintActivity;
import com.cdv.sampling.activity.SearchCompanyActivity;
import com.cdv.sampling.bean.AppTypes;
import com.cdv.sampling.bean.ClientUnit;
import com.cdv.sampling.bean.ShouYaoCanLiuSample;
import com.cdv.sampling.image.ImageLoaderUtils;
import com.cdv.sampling.rxandroid.CommonSubscriber;
import com.cdv.sampling.utils.AppUtils;
import com.cdv.sampling.widget.AttachContainerView;
import com.cdv.sampling.widget.InputLayout;
import com.cdv.sampling.widget.PreferenceRightDetailView;
import com.nostra13.universalimageloader.core.download.ImageDownloader;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

import static android.R.id.input;
import static com.cdv.sampling.activity.AddSamplingActivity.EXTRA_CHOUYANG_HUANJIE;
import static com.cdv.sampling.activity.AddSamplingActivity.EXTRA_COMPANY;
import static com.cdv.sampling.activity.AddSamplingActivity.EXTRA_SAMPLE;
import static com.tencent.bugly.crashreport.crash.c.n;
import static java.lang.Integer.parseInt;


public class DrugFragment extends SampleOperateFragment {

    private static final int REQUEST_CODE_ADD_COMPANY = 1;
    private static final int REQUEST_CODE_SELECT_JINHUO = 2;
    private static final int REQUEST_CODE_SELECT_SAMPLE_NAME = 3;
    private static final int REQUEST_CODE_TANZHU_QIANMING = 4;


    @BindView(R.id.iv_tanzhu_qianming)
    ImageView ivTanZhuQianming;

    @BindView(R.id.input_sampling_no)
    InputLayout inputSamplingNo;
    @BindView(R.id.input_sampling_count)
    InputLayout inputSamplingCount;
    @BindView(R.id.input_sampling_base_count)
    InputLayout inputSamplingBaseCount;
    @BindView(R.id.item_shengchan_danwei)
    PreferenceRightDetailView itemDanwei;
    @BindView(R.id.item_jinhuofangshi)
    PreferenceRightDetailView itemJinhuofangshi;
    @BindView(R.id.item_sampling_name)
    PreferenceRightDetailView itemSampleName;
    @BindView(R.id.input_remark)
    InputLayout inputRemark;
    @BindView(R.id.cb_have_booth)
    CheckBox cbHaveBooth;
    @BindView(R.id.input_booth_number)
    InputLayout inputBoothNumber;
    @BindView(R.id.input_booth_contact_people)
    InputLayout inputBoothContactPeople;
    @BindView(R.id.panel_booth_area)
    LinearLayout panelBoothArea;
    @BindView(R.id.view_attach_container)
    AttachContainerView panelAttachContainer;

    @BindView(R.id.spinner_jianyizhenghao)
    Spinner spinnerJianYiZhengHao;
    @BindView(R.id.et_jianyizhhenghao)
    EditText etJianYiZhengHao;

    private static final String[] JIAN_YI_ARR = new String[]{"（动物A）", "（动物B）", "（产品A）", "（产品B）", "（无）"};

    private ShouYaoCanLiuSample sample;
    private ClientUnit clientUnit;
    private AppTypes jinHuoType;
    private AppTypes sampleNameType;
    private AppTypes chouYangHuanJie;

    private ClientUnit defaultClient;

    public static DrugFragment newInstance(ShouYaoCanLiuSample sample, ClientUnit clientUnit, AppTypes chouYangHuanjie) {
        Bundle args = new Bundle();
        DrugFragment fragment = new DrugFragment();
        args.putSerializable(EXTRA_SAMPLE, sample);
        args.putSerializable(EXTRA_COMPANY, clientUnit);
        args.putSerializable(EXTRA_CHOUYANG_HUANJIE, chouYangHuanjie);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_drug, null);
        ButterKnife.bind(this, contentView);
        chouYangHuanJie = (AppTypes) getArguments().getSerializable(EXTRA_CHOUYANG_HUANJIE);
        sample = (ShouYaoCanLiuSample) getArguments().getSerializable(EXTRA_SAMPLE);
        defaultClient = (ClientUnit) getArguments().getSerializable(EXTRA_COMPANY);

        spinnerJianYiZhengHao.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, JIAN_YI_ARR));
        if (!TextUtils.isEmpty(sample.getCheckCode())) {
            boolean isFind = false;
            for (int i = 0; i < JIAN_YI_ARR.length; i++) {
                if (sample.getCheckCode().contains(JIAN_YI_ARR[i])) {
                    spinnerJianYiZhengHao.setSelection(i);
                    etJianYiZhengHao.setText(sample.getCheckCode().substring(JIAN_YI_ARR[i].length()));
                    isFind = true;
                    break;
                }
            }

            if (!isFind){
                spinnerJianYiZhengHao.setSelection(JIAN_YI_ARR.length - 1);
            }
        }
        inputSamplingBaseCount.setContent(sample.getBase());
        inputRemark.setContent(sample.getDescription());
        inputSamplingNo.setContent(sample.getCode());
        itemSampleName.setContent(sample.getName());
        inputSamplingCount.setContent(sample.getNumber());
        if (AppUtils.isHaveId(sample.getSampleSourceID())) {
            Observable.just(sample.getSampleSourceID()).map(new Func1<Long, ClientUnit>() {
                @Override
                public ClientUnit call(Long aLong) {
                    return SamplingApplication.getDaoSession().getClientUnitDao().load(aLong);
                }
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new CommonSubscriber<ClientUnit>() {
                        @Override
                        public void onNext(ClientUnit clientUnit) {
                            super.onNext(clientUnit);
                            DrugFragment.this.clientUnit = clientUnit;
                            itemDanwei.setContent(clientUnit.getName());
                        }
                    });
        } else if (chouYangHuanJie != null && "养殖".equals(chouYangHuanJie.getValueName())) {
            clientUnit = defaultClient;
            itemDanwei.setContent(clientUnit.getName());
        }

        if (AppUtils.isHaveId(sample.getGouMaiTypeId())) {
            Observable.just(sample.getGouMaiTypeId()).map(new Func1<Long, AppTypes>() {
                @Override
                public AppTypes call(Long aLong) {
                    return SamplingApplication.getDaoSession().getAppTypesDao().load(aLong);
                }
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new CommonSubscriber<AppTypes>() {
                        @Override
                        public void onNext(AppTypes appType) {
                            super.onNext(appType);
                            jinHuoType = appType;
                            itemJinhuofangshi.setContent(appType.getValueName());
                        }
                    });
        }

        if (!TextUtils.isEmpty(sample.getTanWeiCode()) || !TextUtils.isEmpty(sample.getTanWeiUser())) {
            cbHaveBooth.setChecked(true);
            inputBoothNumber.setContent(sample.getTanWeiCode());
            inputBoothContactPeople.setContent(sample.getTanWeiUser());
        } else {
            cbHaveBooth.setChecked(false);
        }

        panelAttachContainer.setFileIds(sample.getLocalFileIds());
        return contentView;
    }

    @OnCheckedChanged(R.id.cb_have_booth)
    void onTanweiChanged() {
        panelBoothArea.setVisibility(cbHaveBooth.isChecked() ? View.VISIBLE : View.GONE);
    }

    @OnClick(R.id.item_shengchan_danwei)
    void showDanwei() {
        startActivityForResult(AddCompanyActivity.getStartIntent(getContext(), clientUnit), REQUEST_CODE_ADD_COMPANY);
    }

    @OnClick(R.id.item_jinhuofangshi)
    void showJinhuofangshi() {
        startActivityForResult(AppTypeListActivity.getStartIntent(getContext(), AppTypes.TYPE_SHOUYAO_JINHUO_FANGSHI), REQUEST_CODE_SELECT_JINHUO);
    }

    @OnClick(R.id.item_sampling_name)
    void showSampleName() {
        startActivityForResult(AppTypeListActivity.getStartIntent(getContext(), AppTypes.TYPE_YANGPIN_MING), REQUEST_CODE_SELECT_SAMPLE_NAME);
    }

    @Override
    public void onSave() {

        if (sample == null) {
            sample = new ShouYaoCanLiuSample();
        }
        boolean isFinished = true;
        if (jinHuoType == null) {
        } else {
            sample.setGouMaiTypeId(jinHuoType.getLocalId());
        }
        if (clientUnit == null) {
        } else {
            sample.setSampleSource(clientUnit);
            sample.setSampleSourceID(clientUnit.getLocalID());
        }

        if (TextUtils.isEmpty(itemSampleName.getContent())) {
            isFinished = false;
        }

        if (TextUtils.isEmpty(inputSamplingNo.getContent())) {
            showToast("样品编号不能为空！");
            return;
        }

        if (TextUtils.isEmpty(inputSamplingCount.getContent())) {
            isFinished = false;
        }

        if (TextUtils.isEmpty(inputSamplingBaseCount.getContent())) {
            isFinished = false;
        }

        if (TextUtils.isEmpty(etJianYiZhengHao.getText().toString())) {
            sample.setCheckCode("无");
        }else{
            sample.setCheckCode(spinnerJianYiZhengHao.getSelectedItem() + etJianYiZhengHao.getText().toString());
        }
        sample.setBase(inputSamplingBaseCount.getContent());
        sample.setDescription(inputRemark.getContent());
        sample.setCode(inputSamplingNo.getContent());
        sample.setName(itemSampleName.getContent().toString());
        sample.setNumber(inputSamplingCount.getContent());

        if (cbHaveBooth.isChecked()) {

            if (TextUtils.isEmpty(inputBoothNumber.getContent())) {
                isFinished = false;
            }
            sample.setTanWeiCode(inputBoothNumber.getContent());
            sample.setTanWeiUser(inputBoothContactPeople.getContent());
            sample.setFinished(isFinished);
        } else {
            sample.setFinished(isFinished);
        }
        Observable<ShouYaoCanLiuSample> observable = SamplingApplication.getDaoSession().getShouYaoCanLiuSampleDao().rx().save(sample);
        Observable.combineLatest(observable, panelAttachContainer.getSelectedFileIds(AppUtils.getFormRootPath(sample.getFormId()), inputSamplingNo.getContent()), new Func2<ShouYaoCanLiuSample, String, ShouYaoCanLiuSample>() {
            @Override
            public ShouYaoCanLiuSample call(ShouYaoCanLiuSample shouYaoCanLiuSample, String fileIds) {
                shouYaoCanLiuSample.setLocalFileIds(fileIds);
                SamplingApplication.getDaoSession().getShouYaoCanLiuSampleDao().save(shouYaoCanLiuSample);
                return shouYaoCanLiuSample;
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommonSubscriber<ShouYaoCanLiuSample>() {

                    @Override
                    public void onNext(ShouYaoCanLiuSample o) {
                        super.onNext(o);
                        showToast("保存数据成功！");
                        Intent intent = new Intent();
                        intent.putExtra(EXTRA_SAMPLE, o);
                        getActivity().setResult(Activity.RESULT_OK, intent);
                        getActivity().finish();
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        showToast("保存数据失败！");
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && REQUEST_CODE_ADD_COMPANY == requestCode) {
            clientUnit = (ClientUnit) data.getSerializableExtra(SearchCompanyActivity.RESULT_KEY_SELECTED);
            itemDanwei.setContent(clientUnit.getName());
        } else if (resultCode == Activity.RESULT_OK && REQUEST_CODE_SELECT_JINHUO == requestCode) {
            jinHuoType = (AppTypes) data.getSerializableExtra(AppTypeListActivity.RESULT_KEY_SELECTED);
            itemJinhuofangshi.setContent(jinHuoType.getValueName());
            if (defaultClient != null && ("自产".equals(jinHuoType.getValueName()) || "自养".equals(jinHuoType.getValueName()))) {
                clientUnit = defaultClient;
                itemDanwei.setContent(clientUnit.getName());
            }
        } else if (resultCode == Activity.RESULT_OK && REQUEST_CODE_SELECT_SAMPLE_NAME == requestCode) {
            sampleNameType = (AppTypes) data.getSerializableExtra(AppTypeListActivity.RESULT_KEY_SELECTED);
            itemSampleName.setContent(sampleNameType.getValueName());
        } else if (resultCode == Activity.RESULT_OK && REQUEST_CODE_TANZHU_QIANMING == requestCode) {
            String id = data.getStringExtra(PaintActivity.EXTRA_FILE_ID);
            String imagePath = data.getStringExtra(PaintActivity.EXTRA_FILE_PATH);
            if (AppUtils.isValidId(id)) {
                ImageLoaderUtils.displayImageForIv(ivTanZhuQianming, ImageDownloader.Scheme.FILE.wrap(imagePath));
                sample.setTanWeiLocalId(Long.parseLong(id));
            }
        }
    }

    @Override
    public void onSaveAndCopy() {
        super.onSaveAndCopy();

        final ShouYaoCanLiuSample copyShouYao = new ShouYaoCanLiuSample();
        if (jinHuoType != null) {
            copyShouYao.setGouMaiTypeId(jinHuoType.getLocalId());
        }
        if (clientUnit != null) {
            copyShouYao.setSampleSourceID(clientUnit.getLocalID());
            sample.setSampleSource(clientUnit);
        }

        if (!"无".equals(spinnerJianYiZhengHao.getSelectedItem())){
            copyShouYao.setCheckCode(spinnerJianYiZhengHao.getSelectedItem() + etJianYiZhengHao.getText().toString());
        }else{
            copyShouYao.setCheckCode(etJianYiZhengHao.getText().toString());
        }
        copyShouYao.setBase(inputSamplingBaseCount.getContent());
        copyShouYao.setDescription(inputRemark.getContent());
        copyShouYao.setName(itemSampleName.getContent().toString());
        copyShouYao.setNumber(inputSamplingCount.getContent());

        String simpleNo = inputSamplingNo.getContent();
        if (!TextUtils.isEmpty(simpleNo)){
            String s = formatString(simpleNo);
            copyShouYao.setCode(s);
        }

        if (cbHaveBooth.isChecked()) {
            copyShouYao.setTanWeiCode(inputBoothNumber.getContent());
            copyShouYao.setTanWeiUser(inputBoothContactPeople.getContent());
        }
        SamplingApplication.getInstance().setShouYaoCanLiuSample(copyShouYao);
        onSave();
    }

    private String formatString(String string){
        int length = string.length();
        if (isNumberChar(string.charAt(length - 1))){
            int index = length - 1;
            for (int i = length - 1; i >= 0; i--){
                char chartAt = string.charAt(i);
                if (!isNumberChar(chartAt)){
                    index = i;
                    break;
                }
            }
            String subString = string.substring(index + 1, length);
            try {
                int subInt = Integer.parseInt(subString);
                String s1 = string.substring(0, index + 1);
                String s2 = formatNumber(subString, subInt);
                return s1 + s2;
            }catch (NumberFormatException e){
                e.printStackTrace();
            }
        }else {
            return string + "-01";
        }
        return "";
    }

    private boolean isNumberChar(char c){
        return c >= '0' && c <= '9';
    }

    private String formatNumber(String numberString, int oldInt){
        int lengthOld = numberString.length();
        int newInt = oldInt + 1;
        String newString = String.valueOf(newInt);
        if (newString.length() < lengthOld){
            do {
                newString = "0" + newString;
            }while (newString.length() != lengthOld);
            return newString;
        }else {
            return newString;
        }
    }
}
