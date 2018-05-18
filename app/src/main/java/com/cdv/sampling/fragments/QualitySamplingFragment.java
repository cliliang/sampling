package com.cdv.sampling.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.cdv.sampling.R;
import com.cdv.sampling.SamplingApplication;
import com.cdv.sampling.activity.AddCompanyActivity;
import com.cdv.sampling.activity.AppTypeListActivity;
import com.cdv.sampling.activity.ScanActivity;
import com.cdv.sampling.activity.SearchCompanyActivity;
import com.cdv.sampling.bean.AppTypes;
import com.cdv.sampling.bean.ClientUnit;
import com.cdv.sampling.bean.ClientUnitDao;
import com.cdv.sampling.bean.ZhiLiangChouYang;
import com.cdv.sampling.rxandroid.CommonSubscriber;
import com.cdv.sampling.utils.AppUtils;
import com.cdv.sampling.widget.AttachContainerView;
import com.cdv.sampling.widget.InputLayout;
import com.cdv.sampling.widget.PreferenceRightDetailView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

import static com.cdv.sampling.activity.AddSamplingActivity.EXTRA_CHOUYANG_HUANJIE;
import static com.cdv.sampling.activity.AddSamplingActivity.EXTRA_COMPANY;
import static com.cdv.sampling.activity.AddSamplingActivity.EXTRA_SAMPLE;

public class QualitySamplingFragment extends SampleOperateFragment {
    private static final int REQUEST_CODE_SELECT_COMPANY = 1;
    private static final int REQUEST_CODE_SELECT_JINHUO = 3;
    private static final int REQUEST_CODE_SELECT_JINHUO_COMPANY = 4;
    private static final int REQUEST_CODE_SCAN = 5;

    private static final String ARG_SAMPLING = "ARG_SAMPLING";
    @BindView(R.id.input_sampling_no)
    InputLayout inputSamplingNo;
    @BindView(R.id.input_sampling_name)
    InputLayout inputSamplingName;
    @BindView(R.id.input_shangpinming)
    InputLayout inputShangpinming;
    @BindView(R.id.input_yangpinpihao)
    InputLayout inputYangpinpihao;
    @BindView(R.id.input_pizhunwenhao)
    InputLayout inputPizhunwenhao;
    @BindView(R.id.input_yangpinguige)
    InputLayout inputYangpinguige;
    @BindView(R.id.input_chouyangshuliang)
    InputLayout inputChouyangshuliang;
    @BindView(R.id.input_kucunshu)
    InputLayout inputKucunshu;
    @BindView(R.id.item_shengchan_danwei)
    PreferenceRightDetailView itemDanwei;
    @BindView(R.id.input_remark)
    InputLayout inputRemark;
    @BindView(R.id.iv_add_attach)
    ImageView ivAddAttach;
    @BindView(R.id.view_attach_container)
    AttachContainerView panelAttachContainer;
    @BindView(R.id.panel_heshi_source_area)
    LinearLayout panelHeshiSourceArea;
    @BindView(R.id.panel_heshi_sample_area)
    LinearLayout panelHeshiSampleArea;
    @BindView(R.id.input_shengchanxuke)
    InputLayout inputShengchanxuke;
    @BindView(R.id.input_gmpzhenghao)
    InputLayout inputGmpzhenghao;
    @BindView(R.id.item_jinhuofangshi)
    PreferenceRightDetailView itemJinhuofangshi;
    @BindView(R.id.input_jinhuoshuliang)
    InputLayout inputJinhuoshuliang;
    @BindView(R.id.item_jinhuoshijian)
    InputLayout itemJinhuoshijian;
    @BindView(R.id.item_create_time)
    InputLayout itemCreateTime;
    @BindView(R.id.item_jinhuo_danwei)
    PreferenceRightDetailView itemJinhuodanwei;

    private ZhiLiangChouYang zhiLiangChouYang;
    private ClientUnit shengChanClientUnit;
    private ClientUnit jinHuoClientUnit;
    private AppTypes jinHuoType;

    private AppTypes chouYangHuanJie;

    private ClientUnit defaultClient;

    public static QualitySamplingFragment newInstance(ZhiLiangChouYang zhiLiangChouYang, ClientUnit clientUnit, AppTypes chouYangHuanjie) {

        Bundle args = new Bundle();
        args.putSerializable(ARG_SAMPLING, zhiLiangChouYang);
        args.putSerializable(EXTRA_COMPANY, clientUnit);
        args.putSerializable(EXTRA_CHOUYANG_HUANJIE, chouYangHuanjie);
        QualitySamplingFragment fragment = new QualitySamplingFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_quality_sampling, null);
        ButterKnife.bind(this, contentView);
        chouYangHuanJie = (AppTypes) getArguments().getSerializable(EXTRA_CHOUYANG_HUANJIE);
        defaultClient = (ClientUnit) getArguments().getSerializable(EXTRA_COMPANY);

        zhiLiangChouYang = (ZhiLiangChouYang) getArguments().getSerializable(ARG_SAMPLING);
        inputSamplingNo.setContent(zhiLiangChouYang.getCode());
        inputPizhunwenhao.setContent(zhiLiangChouYang.getPiZhunWenHao());
        inputShangpinming.setContent(zhiLiangChouYang.getName());
        inputSamplingName.setContent(zhiLiangChouYang.getSampleName());
        inputYangpinguige.setContent(zhiLiangChouYang.getSampleGuiGe());
        inputRemark.setContent(zhiLiangChouYang.getDescription());
        inputKucunshu.setContent(zhiLiangChouYang.getKuCun());
        inputYangpinpihao.setContent(zhiLiangChouYang.getPiHao());
        inputChouyangshuliang.setContent(zhiLiangChouYang.getNumber());
        itemCreateTime.setContent(zhiLiangChouYang.getShengChanRiQi());

        inputGmpzhenghao.setContent(zhiLiangChouYang.getGMPCode());
        inputShengchanxuke.setContent(zhiLiangChouYang.getXukeCode());
        inputJinhuoshuliang.setContent(zhiLiangChouYang.getJinHuoNumber());
        itemJinhuoshijian.setContent(zhiLiangChouYang.getJinHuoTime());

        checkChouYangHuanJie();

        if (AppUtils.isHaveId(zhiLiangChouYang.getSampleSourceID())) {
            Observable.just(zhiLiangChouYang.getSampleSourceID()).map(new Func1<Long, ClientUnit>() {
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
                            QualitySamplingFragment.this.jinHuoClientUnit = clientUnit;
                            itemJinhuodanwei.setContent(clientUnit.getName());
                        }
                    });
        }

        if (AppUtils.isHaveId(zhiLiangChouYang.getGouMaiTypeId())) {
            Observable.just(zhiLiangChouYang.getGouMaiTypeId()).map(new Func1<Long, AppTypes>() {
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
                            itemJinhuofangshi.setContent(jinHuoType.getValueName());
                        }
                    });
        }

        if (AppUtils.isHaveId(zhiLiangChouYang.getShengChanUnitID())){
            Observable.just(zhiLiangChouYang.getShengChanUnitID()).map(new Func1<Long, ClientUnit>() {
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
                            QualitySamplingFragment.this.shengChanClientUnit = clientUnit;
                            itemDanwei.setContent(clientUnit.getName());
                        }
                    });
        }
        panelAttachContainer.setFileIds(zhiLiangChouYang.getLocalFileIds());
        return contentView;
    }

    @OnClick(R.id.item_shengchan_danwei)
    void showDanwei() {
        startActivityForResult(AddCompanyActivity.getStartIntent(getContext(), shengChanClientUnit), REQUEST_CODE_SELECT_COMPANY);
    }

    @OnClick(R.id.item_jinhuo_danwei)
    void showJinHuoDanwei() {
        startActivityForResult(AddCompanyActivity.getStartIntent(getContext(), jinHuoClientUnit), REQUEST_CODE_SELECT_JINHUO_COMPANY);
    }

    @OnClick(R.id.item_jinhuofangshi)
    void showJinhuofangshi() {
        startActivityForResult(AppTypeListActivity.getStartIntent(getContext(), AppTypes.TYPE_ZHILIANG_JINHUO_FANGSHI), REQUEST_CODE_SELECT_JINHUO);
    }

    @Override
    public void onScan() {
        super.onScan();
        startActivityForResult(new Intent(getContext(), ScanActivity.class), REQUEST_CODE_SCAN);
    }

    @Override
    public void onSaveAndCopy() {
        super.onSaveAndCopy();
        final ZhiLiangChouYang copyChouYang = new ZhiLiangChouYang();


    }

    @Override
    public void onSave() {
        boolean isFinished = true;
        StringBuilder builder = new StringBuilder();
        if (TextUtils.isEmpty(inputSamplingNo.getContent())) {
            showToast("样品编号不能为空！");
            return;
        }
        if (shengChanClientUnit == null) {
            isFinished = false;
            builder.append("生产单位，");
        }
        if (TextUtils.isEmpty(inputYangpinpihao.getContent())) {
            isFinished = false;
            builder.append("样品批号，");
        }
        if (TextUtils.isEmpty(inputShangpinming.getContent())) {
            isFinished = false;
            builder.append("商品名，");
        }
        if (TextUtils.isEmpty(inputSamplingName.getContent())) {
            isFinished = false;
            builder.append("样品名称，");
        }
        if (TextUtils.isEmpty(inputYangpinguige.getContent())) {
            isFinished = false;
            builder.append("样品规格，");
        }
        if (TextUtils.isEmpty(inputKucunshu.getContent())) {
            isFinished = false;
            builder.append("库存数，");
        }
        if (TextUtils.isEmpty(inputPizhunwenhao.getContent())) {
            isFinished = false;
            builder.append("批准文号，");
        }
        if (TextUtils.isEmpty(inputChouyangshuliang.getContent())) {
            isFinished = false;
            builder.append("抽样数量，");
        }

        if (zhiLiangChouYang.getHaveHeshi()){
            if (TextUtils.isEmpty(inputGmpzhenghao.getContent())) {
                isFinished = false;
                builder.append("兽药GMP证字号，");
            }
            if (TextUtils.isEmpty(inputShengchanxuke.getContent())) {
                isFinished = false;
                builder.append("兽药生产证字号，");
            }
            if (TextUtils.isEmpty(inputJinhuoshuliang.getContent())) {
                isFinished = false;
                builder.append("进货数量，");
            }
            if (TextUtils.isEmpty(itemJinhuoshijian.getContent())) {
                isFinished = false;
                builder.append("进货时间，");
            }

            zhiLiangChouYang.setGMPCode(inputGmpzhenghao.getContent());
            zhiLiangChouYang.setXukeCode(inputShengchanxuke.getContent());
            zhiLiangChouYang.setJinHuoNumber(inputJinhuoshuliang.getContent());
            zhiLiangChouYang.setJinHuoTime(itemJinhuoshijian.getContent().toString());

            if (jinHuoClientUnit == null) {
                isFinished = false;
                builder.append("进货单位，");
            }else{
                zhiLiangChouYang.setSampleSource(jinHuoClientUnit);
                zhiLiangChouYang.setSampleSourceID(jinHuoClientUnit.getLocalID());
            }

            if (jinHuoType == null){
                isFinished = false;
                builder.append("进货方式");
            }else{
                zhiLiangChouYang.setGouMaiType(jinHuoType.getValueName());
                zhiLiangChouYang.setGouMaiTypeId(jinHuoType.getLocalId());
                zhiLiangChouYang.setGouMaiLeiXing(jinHuoType);
            }
        }
        zhiLiangChouYang.setCode(inputSamplingNo.getContent());
        zhiLiangChouYang.setPiHao(inputYangpinpihao.getContent());
        zhiLiangChouYang.setName(inputShangpinming.getContent());
        zhiLiangChouYang.setSampleName(inputSamplingName.getContent());
        zhiLiangChouYang.setSampleGuiGe(inputYangpinguige.getContent());
        zhiLiangChouYang.setDescription(inputRemark.getContent());
        zhiLiangChouYang.setKuCun(inputKucunshu.getContent());
        zhiLiangChouYang.setPiZhunWenHao(inputPizhunwenhao.getContent());
        zhiLiangChouYang.setNumber(inputChouyangshuliang.getContent());
        zhiLiangChouYang.setShengChanRiQi(itemCreateTime.getContent().toString());
        if (shengChanClientUnit != null) {
            zhiLiangChouYang.setShengChanUnitID(shengChanClientUnit.getLocalID());
            zhiLiangChouYang.setShengChanUnit(shengChanClientUnit);
        }
        zhiLiangChouYang.setFinished(isFinished);
//        zhiLiangChouYang.setFailDes(builder.toString());

        Observable.combineLatest(SamplingApplication.getDaoSession().getZhiLiangChouYangDao().rx().save(zhiLiangChouYang), panelAttachContainer.getSelectedFileIds(AppUtils.getFormRootPath(zhiLiangChouYang.getFormId()), inputSamplingNo.getContent()), new Func2<ZhiLiangChouYang, String, ZhiLiangChouYang>() {
            @Override
            public ZhiLiangChouYang call(ZhiLiangChouYang zhiLiangChouYang, String fileIds) {
                zhiLiangChouYang.setLocalFileIds(fileIds);
                SamplingApplication.getDaoSession().getZhiLiangChouYangDao().save(zhiLiangChouYang);
                return zhiLiangChouYang;
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommonSubscriber<ZhiLiangChouYang>() {
                    @Override
                    public void onNext(ZhiLiangChouYang o) {
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
            shengChanClientUnit = (ClientUnit) data.getSerializableExtra(SearchCompanyActivity.RESULT_KEY_SELECTED);
            itemDanwei.setContent(shengChanClientUnit.getName());
        } else if (resultCode == Activity.RESULT_OK && REQUEST_CODE_SELECT_JINHUO == requestCode) {
            jinHuoType = (AppTypes) data.getSerializableExtra(AppTypeListActivity.RESULT_KEY_SELECTED);
            itemJinhuofangshi.setContent(jinHuoType.getValueName());
            if (defaultClient != null && "厂家直供".equals(jinHuoType.getValueName())){
                jinHuoClientUnit = shengChanClientUnit;
                itemJinhuodanwei.setContent(jinHuoClientUnit.getName());
            }
        } else if (resultCode == Activity.RESULT_OK && REQUEST_CODE_SELECT_JINHUO_COMPANY == requestCode) {
            jinHuoClientUnit = (ClientUnit) data.getSerializableExtra(SearchCompanyActivity.RESULT_KEY_SELECTED);
            itemJinhuodanwei.setContent(jinHuoClientUnit.getName());
        }  else if (resultCode == Activity.RESULT_OK && REQUEST_CODE_SCAN == requestCode) {
            String str = data.getStringExtra(ScanActivity.EXTRA_SCAN_RESULT);
            if (str != null){
                str = str.replace("\n", "").trim();
            }
            String[] param = str.split("，");
            if (param.length >= 2){
                inputSamplingName.setContent(param[1]);
            }
            if (param.length >= 3){
                inputPizhunwenhao.setContent(param[2]);
            }
            if (param.length >= 4){
                final String companyName = param[3];
                String scanTelephone = null;
                if (param.length >= 5){
                    scanTelephone = param[4];
                }
                final String telephone = scanTelephone;
                Observable.just(companyName).map(new Func1<String, ClientUnit>() {
                    @Override
                    public ClientUnit call(String s) {
                        List<ClientUnit> list = SamplingApplication.getDaoSession().getClientUnitDao().queryBuilder().where(ClientUnitDao.Properties.Name.like("%" + s + "%"))
                                .build().list();
                        ClientUnit clientUnit;
                        if (list != null && list.size() > 0){
                            list.get(0).setTelephone(telephone);
                            clientUnit = list.get(0);
                        }else{
                            clientUnit = new ClientUnit();
                            clientUnit.setName(companyName);
                            clientUnit.setTelephone(telephone);
                        }
                        SamplingApplication.getDaoSession().getClientUnitDao().save(clientUnit);
                        return clientUnit;
                    }
                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new CommonSubscriber<ClientUnit>(){
                            @Override
                            public void onNext(ClientUnit o) {
                                super.onNext(o);
                                if (o != null){
                                    shengChanClientUnit = o;
                                    itemDanwei.setContent(shengChanClientUnit.getName());
                                }
                            }
                        });
            }
        }
    }

    private void checkChouYangHuanJie() {
        boolean isNotShengChan = !"生产".equals(chouYangHuanJie.getValueName());
        zhiLiangChouYang.setHaveHeshi(isNotShengChan);
        if (isNotShengChan){
            if (jinHuoType != null && "厂家直供".equals(jinHuoType.getValueName())){
                if (shengChanClientUnit != null){
                    jinHuoClientUnit = shengChanClientUnit;
                    itemJinhuodanwei.setContent(jinHuoClientUnit.getName());
                }
            }
        }else{
            shengChanClientUnit = defaultClient;
            itemDanwei.setContent(shengChanClientUnit.getName());
        }
        panelHeshiSampleArea.setVisibility(isNotShengChan ? View.VISIBLE : View.GONE);
        panelHeshiSourceArea.setVisibility(isNotShengChan ? View.VISIBLE : View.GONE);
    }
}
