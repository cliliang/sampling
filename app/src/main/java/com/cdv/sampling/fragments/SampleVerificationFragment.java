package com.cdv.sampling.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cdv.sampling.R;
import com.cdv.sampling.SamplingApplication;
import com.cdv.sampling.activity.AddCompanyActivity;
import com.cdv.sampling.activity.AppTypeListActivity;
import com.cdv.sampling.activity.SearchCompanyActivity;
import com.cdv.sampling.bean.AppTypes;
import com.cdv.sampling.bean.ClientUnit;
import com.cdv.sampling.bean.YangPinHeShi;
import com.cdv.sampling.rxandroid.CommonSubscriber;
import com.cdv.sampling.utils.AppUtils;
import com.cdv.sampling.widget.AttachContainerView;
import com.cdv.sampling.widget.InputLayout;
import com.cdv.sampling.widget.PreferenceRightDetailView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

import static com.cdv.sampling.activity.AddSamplingActivity.EXTRA_COMPANY;
import static com.cdv.sampling.activity.AddSamplingActivity.EXTRA_SAMPLE;

public class SampleVerificationFragment extends SampleOperateFragment {

    private static final int REQUEST_CODE_SELECT_COMPANY = 1;
    private static final int REQUEST_CODE_SELECT_JINHUO = 2;

    private static final String ARG_YANGPIN_HESHI = "ARG_YANGPIN_HESHI";

    @BindView(R.id.input_sampling_no)
    InputLayout inputSamplingNo;
    @BindView(R.id.input_pizhunwenhao)
    InputLayout inputPizhunwenhao;
    @BindView(R.id.input_shengchanxuke)
    InputLayout inputShengchanxuke;
    @BindView(R.id.input_gmpzhenghao)
    InputLayout inputGmpzhenghao;
    @BindView(R.id.item_shengchan_danwei)
    PreferenceRightDetailView itemDanwei;
    @BindView(R.id.item_jinhuofangshi)
    PreferenceRightDetailView itemJinhuofangshi;
    @BindView(R.id.input_jinhuoshuliang)
    InputLayout inputJinhuoshuliang;
    @BindView(R.id.item_jinhuoshijian)
    PreferenceRightDetailView itemJinhuoshijian;
    @BindView(R.id.input_remark)
    InputLayout inputRemark;
    @BindView(R.id.view_attach_container)
    AttachContainerView panelAttachContainer;

    private YangPinHeShi yangPinHeShi;
    private ClientUnit clientUnit;
    private ClientUnit defaultClient;
    private AppTypes jinHuoType;

    public static SampleVerificationFragment newInstance(YangPinHeShi yangPinHeShi, ClientUnit clientUnit) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_YANGPIN_HESHI, yangPinHeShi);
        args.putSerializable(EXTRA_COMPANY, clientUnit);
        SampleVerificationFragment fragment = new SampleVerificationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_sample_verification, null);
        ButterKnife.bind(this, contentView);

        defaultClient = (ClientUnit) getArguments().getSerializable(EXTRA_COMPANY);
        yangPinHeShi = (YangPinHeShi) getArguments().getSerializable(ARG_YANGPIN_HESHI);
        if (!TextUtils.isEmpty(yangPinHeShi.getGMPCode())){
            inputGmpzhenghao.setContent(yangPinHeShi.getGMPCode());
        }
        if (!TextUtils.isEmpty(yangPinHeShi.getXukeCode())){
            inputShengchanxuke.setContent(yangPinHeShi.getXukeCode());
        }
        itemJinhuoshijian.setContent(yangPinHeShi.getJinHuoTime());
        inputJinhuoshuliang.setContent(yangPinHeShi.getNumber());
        inputPizhunwenhao.setContent(yangPinHeShi.getPiZhunWenHao());
        inputRemark.setContent(yangPinHeShi.getDescription());
        inputSamplingNo.setContent(yangPinHeShi.getCode());


        if (AppUtils.isHaveId(yangPinHeShi.getSampleSourceID())) {
            Observable.just(yangPinHeShi.getSampleSourceID()).map(new Func1<Long, ClientUnit>() {
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
                            SampleVerificationFragment.this.clientUnit = clientUnit;
                            itemDanwei.setContent(clientUnit.getName());
                        }
                    });
        }

        if (AppUtils.isHaveId(yangPinHeShi.getGouMaiTypeId())) {
            Observable.just(yangPinHeShi.getGouMaiTypeId()).map(new Func1<Long, AppTypes>() {
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
        panelAttachContainer.setFileIds(yangPinHeShi.getLocalFileIds());

        return contentView;
    }

    @OnClick(R.id.item_shengchan_danwei)
    void showDanwei() {
        startActivityForResult(AddCompanyActivity.getStartIntent(getContext(), null), REQUEST_CODE_SELECT_COMPANY);
    }

    @OnClick(R.id.item_jinhuofangshi)
    void showJinhuofangshi() {
//        startActivityForResult(AppTypeListActivity.getStartIntent(getContext(), AppTypes.TYPE_JINHUO_FANGSHI), REQUEST_CODE_SELECT_JINHUO);
    }

    @OnClick(R.id.item_jinhuoshijian)
    void showJinHuoShiJian() {
    }

    @Override
    public void onSave() {
        boolean isFinished = true;
        if (TextUtils.isEmpty(inputSamplingNo.getContent())) {
            showToast("样品编号不能为空！");
            return;
        }
        if (TextUtils.isEmpty(inputGmpzhenghao.getContent())) {
            isFinished = false;
        }
        if (TextUtils.isEmpty(inputShengchanxuke.getContent())) {
            isFinished = false;
        }
        if (TextUtils.isEmpty(itemJinhuoshijian.getContent())) {
            isFinished = false;
        }
        if (TextUtils.isEmpty(inputJinhuoshuliang.getContent())) {
            isFinished = false;
        }
        if (TextUtils.isEmpty(inputPizhunwenhao.getContent())) {
            isFinished = false;
        }
        if (jinHuoType == null) {
            isFinished = false;
        }else{
            yangPinHeShi.setGouMaiTypeId(jinHuoType.getLocalId());
        }
        if (clientUnit == null) {
            isFinished = false;
        }else{
            yangPinHeShi.setSampleSourceID(clientUnit.getLocalID());
        }
        yangPinHeShi.setGMPCode(inputGmpzhenghao.getContent());
        yangPinHeShi.setXukeCode(inputShengchanxuke.getContent());
        yangPinHeShi.setDescription(inputRemark.getContent());
        yangPinHeShi.setCode(inputSamplingNo.getContent());
        yangPinHeShi.setJinHuoTime(itemJinhuoshijian.getContent().toString());
        yangPinHeShi.setNumber(inputJinhuoshuliang.getContent());
        yangPinHeShi.setPiZhunWenHao(inputPizhunwenhao.getContent());
        yangPinHeShi.setFinished(isFinished);
        Observable.combineLatest(SamplingApplication.getDaoSession().getYangPinHeShiDao().rx().save(yangPinHeShi), panelAttachContainer.getSelectedFileIds(AppUtils.getFormRootPath(yangPinHeShi.getFormId()), inputSamplingNo.getContent()), new Func2<YangPinHeShi, String, YangPinHeShi>() {
            @Override
            public YangPinHeShi call(YangPinHeShi yangPinHeShi, String fileIds) {
                yangPinHeShi.setLocalFileIds(fileIds);
                SamplingApplication.getDaoSession().getYangPinHeShiDao().save(yangPinHeShi);
                return yangPinHeShi;
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommonSubscriber<YangPinHeShi>() {

                    @Override
                    public void onNext(YangPinHeShi o) {
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
        if (resultCode == Activity.RESULT_OK && REQUEST_CODE_SELECT_COMPANY == requestCode) {
            clientUnit = (ClientUnit) data.getSerializableExtra(SearchCompanyActivity.RESULT_KEY_SELECTED);
            itemDanwei.setContent(clientUnit.getName());
        } else if (resultCode == Activity.RESULT_OK && REQUEST_CODE_SELECT_JINHUO == requestCode) {
            jinHuoType = (AppTypes) data.getSerializableExtra(AppTypeListActivity.RESULT_KEY_SELECTED);
            itemJinhuofangshi.setContent(jinHuoType.getValueName());
            if (defaultClient != null && "自产".equals(jinHuoType.getValueName())){
                clientUnit = defaultClient;
                itemDanwei.setContent(clientUnit.getName());
            }
        }
    }
}
