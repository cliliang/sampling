package com.cdv.sampling.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

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

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

import static com.cdv.sampling.activity.AddSamplingActivity.EXTRA_CHOUYANG_HUANJIE;
import static com.cdv.sampling.activity.AddSamplingActivity.EXTRA_COMPANY;
import static com.cdv.sampling.activity.AddSamplingActivity.EXTRA_SAMPLE;
import static com.cdv.sampling.activity.AddSamplingActivity.EXTRA_SIMPLE_CODE_LIST;


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

    @BindView(R.id.et_jianyizhhenghao)
    EditText etJianYiZhengHao;

    private static final String[] JIAN_YI_ARR = new String[]{"（动物A）", "（动物B）", "（产品A）", "（产品B）", "（无）"};

    private ShouYaoCanLiuSample sample;
    private ClientUnit clientUnit;
    private AppTypes jinHuoType;
    private AppTypes sampleNameType;
    private AppTypes chouYangHuanJie;

    private ClientUnit defaultClient;
    private ArrayList<String> codeList;

    public static DrugFragment newInstance(ShouYaoCanLiuSample sample, ClientUnit clientUnit, AppTypes chouYangHuanjie, ArrayList<String> list) {
        Bundle args = new Bundle();
        DrugFragment fragment = new DrugFragment();
        args.putSerializable(EXTRA_SAMPLE, sample);
        args.putSerializable(EXTRA_COMPANY, clientUnit);
        args.putSerializable(EXTRA_CHOUYANG_HUANJIE, chouYangHuanjie);
        args.putStringArrayList(EXTRA_SIMPLE_CODE_LIST, list);
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
        codeList = getArguments().getStringArrayList(EXTRA_SIMPLE_CODE_LIST);

        inputSamplingBaseCount.setContent(sample.getBase());
        inputRemark.setContent(sample.getDescription());
        inputSamplingNo.setContent(sample.getCode());
        itemSampleName.setContent(sample.getName());
        inputSamplingCount.setContent(sample.getNumber());
        etJianYiZhengHao.setText(sample.getCheckCode());

        Log.i("chen", "show list:" + codeList.toString());

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
        }else {
            clientUnit = null;
            itemDanwei.setContent("");
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

    private void showSameCodeDialog(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle("提示");
        alertDialog.setMessage("表单编号重复，是否修改");
        alertDialog.setNegativeButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!getActivity().isFinishing()){
                    dialog.dismiss();
                }
                saveData();
            }
        });
        alertDialog.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!getActivity().isFinishing()){
                    dialog.dismiss();
                }
            }
        });
        alertDialog.show();
    }

    @Override
    public void onSave() {
        if (sample == null) {
            sample = new ShouYaoCanLiuSample();
        }
//        String simpleCode = inputSamplingNo.getContent();
//        if (codeList != null && codeList.size() > 0 && !TextUtils.isEmpty(simpleCode)){
//            for (String code : codeList){
//                if (simpleCode.equals(code)){
//                    showSameCodeDialog();
//                    return;
//                }
//            }
//        }
//        Log.e("chen", "same return ");
        saveData();
    }

    private void saveData(){
        boolean isFinished = true;
        StringBuilder builder = new StringBuilder();
        if (jinHuoType == null) {
            isFinished = false;
            builder.append("进货方式，");
        } else {
            sample.setGouMaiTypeId(jinHuoType.getLocalId());
        }
        if (clientUnit == null) {
            isFinished = false;
            builder.append("进货单位，");
        } else {
            sample.setSampleSource(clientUnit);
            sample.setSampleSourceID(clientUnit.getLocalID());
        }

        if (TextUtils.isEmpty(itemSampleName.getContent())) {
            isFinished = false;
            builder.append("样品名称，");
        }

        if (TextUtils.isEmpty(inputSamplingNo.getContent())) {
            showToast("样品编号不能为空！");
            return;
        }

        if (TextUtils.isEmpty(inputSamplingCount.getContent())) {
            isFinished = false;
            builder.append("抽样数量，");
        }

        if (TextUtils.isEmpty(inputSamplingBaseCount.getContent())) {
            isFinished = false;
            builder.append("样品基数，");
        }

        if (TextUtils.isEmpty(etJianYiZhengHao.getText().toString())) {
            isFinished = false;
            builder.append("检疫证号，");
        }
        sample.setBase(inputSamplingBaseCount.getContent());
        sample.setDescription(inputRemark.getContent());
        sample.setCode(inputSamplingNo.getContent());
        sample.setName(itemSampleName.getContent().toString());
        sample.setNumber(inputSamplingCount.getContent());
        sample.setCheckCode(etJianYiZhengHao.getText().toString());

        if (cbHaveBooth.isChecked()) {
            if (TextUtils.isEmpty(inputBoothNumber.getContent())) {
                isFinished = false;
                builder.append("摊位号");
            }
            sample.setTanWeiCode(inputBoothNumber.getContent());
            sample.setTanWeiUser(inputBoothContactPeople.getContent());
            sample.setFinished(isFinished);
        } else {
            sample.setFinished(isFinished);
        }
        sample.setFailDes(builder.toString());
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
            }else {
                clientUnit = null;
                itemDanwei.setContent("");
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
            copyShouYao.setSampleSource(clientUnit);
        }

        copyShouYao.setCheckCode(etJianYiZhengHao.getText().toString());
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
