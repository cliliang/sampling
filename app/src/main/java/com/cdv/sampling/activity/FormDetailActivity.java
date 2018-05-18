package com.cdv.sampling.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.databinding.library.baseAdapters.BR;
import com.cdv.sampling.R;
import com.cdv.sampling.SamplingApplication;
import com.cdv.sampling.bean.AppFiles;
import com.cdv.sampling.bean.AppTypes;
import com.cdv.sampling.bean.ClientUnit;
import com.cdv.sampling.bean.FormBean;
import com.cdv.sampling.bean.FormView;
import com.cdv.sampling.bean.JianCeDan;
import com.cdv.sampling.bean.ShouYaoCanLiuSample;
import com.cdv.sampling.bean.Signature;
import com.cdv.sampling.bean.YangPinHeShi;
import com.cdv.sampling.bean.ZhiLiangChouYang;
import com.cdv.sampling.constants.Constants;
import com.cdv.sampling.exception.ErrorMessageFactory;
import com.cdv.sampling.exception.FormException;
import com.cdv.sampling.image.ImageLoaderUtils;
import com.cdv.sampling.net.HttpService;
import com.cdv.sampling.net.OperatorRequestMap;
import com.cdv.sampling.repository.FormRepository;
import com.cdv.sampling.repository.UserRepository;
import com.cdv.sampling.rxandroid.CommonSubscriber;
import com.cdv.sampling.utils.AppUtils;
import com.cdv.sampling.utils.ListUtils;
import com.cdv.sampling.utils.MD5Utils;
import com.cdv.sampling.utils.ScreenUtils;
import com.cdv.sampling.utils.ToastUtils;
import com.cdv.sampling.utils.io.FileUtils;
import com.cdv.sampling.widget.EPocketAlertDialog;
import com.cdv.sampling.widget.ItemIndicator;
import com.cdv.sampling.widget.PreferenceRightDetailView;
import com.cdv.sampling.widget.SendEmailDialog;
import com.nostra13.universalimageloader.core.download.ImageDownloader;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class FormDetailActivity extends BaseActivity {

    private static final int REQUEST_CODE_ADD_COMPANY = 0;
    private static final int REQUEST_CODE_ADD_SAMPLE_TYPE = 3;
    private static final int REQUEST_CODE_ADD_SAMPLE = 1;
    private static final int REQUEST_CODE_ADD_SIGNATURE = 2;
    private static final int REQUEST_CODE_PRINT_SETTINGS = 4;
    private static final String EXTRA_FORM_ID = "EXTRA_FORM_ID";

    private static final String EXTRA_SAMPLING_TYPE = "EXTRA_SAMPLING_TYPE";

    @BindView(R.id.panel_company_area)
    LinearLayout panelCompanyArea;
    @BindView(R.id.panel_sample_area)
    LinearLayout panelSampleArea;
    @BindView(R.id.tv_company)
    TextView tvCompany;
    @BindView(R.id.panel_form_image)
    LinearLayout panelFormImage;
    @BindView(R.id.panel_form_image_parent)
    LinearLayout panelFormImageParent;
    @BindView(R.id.panel_qrcode_area)
    View panelQRCodeArea;
    @BindView(R.id.item_jingbanren)
    PreferenceRightDetailView itemJingBanren;

    @BindView(R.id.item_add_sampleType)
    ItemIndicator itemAddSampleType;

    private JianCeDan jianCeDan;
    private String sampleType;
    private ArrayList<String> simpleCodeList = new ArrayList<>();

    public static Intent getStartIntent(Context context, long formId) {
        Intent intent = new Intent(context, FormDetailActivity.class);
        intent.putExtra(EXTRA_FORM_ID, formId);
        return intent;
    }

    public static Intent getStartIntent(Context context, String samplingType) {
        Intent intent = new Intent(context, FormDetailActivity.class);
        intent.putExtra(EXTRA_SAMPLING_TYPE, samplingType);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_detail);

        sampleType = getIntent().getStringExtra(EXTRA_SAMPLING_TYPE);
        long sampleId = getIntent().getLongExtra(EXTRA_FORM_ID, -1);
        if (sampleId <= 0) {
            setMyTitle(AppUtils.getSampleType(sampleType));
            jianCeDan = new JianCeDan();
            jianCeDan.setCreateTime(new Date());
//            jianCeDan.setTestUser(UserRepository.getInstance().getCurrentUser().getUserName());
            jianCeDan.setEDanType(sampleType);
            jianCeDan.setStatus(JianCeDan.STATUS_INIT);
            jianCeDan.setCreateUser(UserRepository.getInstance().getCurrentUser().getUserName());
            initViews();
            return;
        }
        Observable.just(sampleId).map(new Func1<Long, JianCeDan>() {
            @Override
            public JianCeDan call(Long aLong) {
                return SamplingApplication.getDaoSession().getJianCeDanDao().loadDeep(aLong);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommonSubscriber<JianCeDan>() {
                    @Override
                    public void onNext(JianCeDan o) {
                        super.onNext(o);
                        jianCeDan = o;
                        switch (jianCeDan.getEDanType()) {
                            case JianCeDan.DAN_TYPE_SAMPLING_DRUG:
                                setMyTitle("残留抽样采样");
                                break;
                            case JianCeDan.DAN_TYPE_SAMPLING_QUALITY:
                                setMyTitle("兽药抽样采样");
                                break;
                        }
                        itemAddSampleType.setContent(jianCeDan.getSampleType());
                        initClientUnit();
                        initSampleArea();
                        initFormImage();
                        initSignatureArea();
                        initQRCodeArea();
                    }
                });
    }

    private void initViews() {
        if (AppUtils.isHaveId(jianCeDan.getClientID())) {
            Observable.just(jianCeDan.getClientID()).subscribeOn(Schedulers.io())
                    .map(new Func1<Long, ClientUnit>() {
                        @Override
                        public ClientUnit call(Long id) {
                            return SamplingApplication.getDaoSession().getClientUnitDao().load(id);
                        }
                    }).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new CommonSubscriber<ClientUnit>() {
                        @Override
                        public void onNext(ClientUnit o) {
                            super.onNext(o);
                            initClientUnit();
                        }
                    });
        }
        initSampleArea();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD_COMPANY && resultCode == RESULT_OK) {
            ClientUnit client = (ClientUnit) data.getSerializableExtra(SearchCompanyActivity.RESULT_KEY_SELECTED);
            jianCeDan.setClientUnit(client);
            jianCeDan.setClientID(client.getLocalID());
            SamplingApplication.getDaoSession().getJianCeDanDao().save(jianCeDan);
            initClientUnit();
            checkJinHuoUnit(jianCeDan);
        } else if (requestCode == REQUEST_CODE_ADD_SIGNATURE && resultCode == RESULT_OK) {
            SamplingApplication.getDaoSession().getJianCeDanDao().refresh(jianCeDan);
            initSignatureArea();
        } else if (requestCode == REQUEST_CODE_ADD_SAMPLE && resultCode == RESULT_OK) {
            initSampleArea();
            if (SamplingApplication.getInstance().getShouYaoCanLiuSample() != null){
                showSampling();
            }
        } else if (requestCode == REQUEST_CODE_ADD_SAMPLE_TYPE && resultCode == RESULT_OK) {
            AppTypes types = (AppTypes) data.getSerializableExtra(AppTypeListActivity.RESULT_KEY_SELECTED);
            jianCeDan.setSampleHuanjie(types);
            jianCeDan.setSampleType(types.getValueName());
            jianCeDan.setSampleTypeId(types.getLocalId());
            itemAddSampleType.setContent(types.getValueName());
            if (AppUtils.isHaveId(jianCeDan.getClientID())){
                return;
            }
            SamplingApplication.getDaoSession().getJianCeDanDao().rx().save(jianCeDan).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(new CommonSubscriber<JianCeDan>() {
                @Override
                public void onNext(JianCeDan o) {
                    super.onNext(o);
                }
            });
        }else if (requestCode == REQUEST_CODE_PRINT_SETTINGS && resultCode == RESULT_OK){
            ArrayList<String> formImageList = getFormImageList();
            PrintActivity.start(FormDetailActivity.this, formImageList);
            return;
        }

        if (RESULT_OK == resultCode) {
            deleteFormImages();
        }
    }

    private void deleteFormImages() {
        Observable.just(jianCeDan).map(new Func1<JianCeDan, JianCeDan>() {
            @Override
            public JianCeDan call(JianCeDan jianCeDan) {
                if (jianCeDan.getFileIDs() == null) {
                    return jianCeDan;
                }
                String[] idArr = jianCeDan.getFileIDs().split(Constants.FILE_ID_SEPARATOR);
                String fileIds = "";
                int i = 0;
                for (String idString : idArr) {
                    i++;
                    AppFiles appFile = SamplingApplication.getDaoSession().getAppFilesDao().load(Long.parseLong(idString));
                    if (appFile == null) {
                        continue;
                    }
                    File file = new File(appFile.getFilePath());
                    if (!file.exists()) {
                        continue;
                    }
                    if (!MD5Utils.checkMD5(appFile.getMD5(), file)) {
                        continue;
                    }
                    if (Constants.TYPE_FORM_IMAGE.equals(appFile.getFileType())) {
                        file.delete();
                        continue;
                    }
                    fileIds = fileIds.concat(String.valueOf(appFile.getID()));
                    fileIds = fileIds.concat(Constants.FILE_ID_SEPARATOR);
                }
                if (fileIds.length() > 0) {
                    fileIds = fileIds.substring(0, fileIds.length() - 1);
                }
                if (!fileIds.equals(jianCeDan.getFileIDs())) {
                    jianCeDan.setFileIDs(fileIds);
                }
                SamplingApplication.getDaoSession().getJianCeDanDao().save(jianCeDan);
                return jianCeDan;
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommonSubscriber<JianCeDan>() {
                    @Override
                    public void onNext(JianCeDan o) {
                        super.onNext(o);
                        jianCeDan = o;
                        initFormImage();
                    }
                });
    }

    @OnClick(R.id.item_add_sampleType)
    void showAddSampleType() {
        if (!AppUtils.isEditable(jianCeDan)) {
            return;
        }
        if (JianCeDan.DAN_TYPE_SAMPLING_DRUG.equals(jianCeDan.getEDanType())) {
            startActivityForResult(AppTypeListActivity.getStartIntent(this, AppTypes.TYPE_SHOUYAO_SAMPLE_TYPE), REQUEST_CODE_ADD_SAMPLE_TYPE);
        } else if (JianCeDan.DAN_TYPE_SAMPLING_QUALITY.equals(jianCeDan.getEDanType())) {
            startActivityForResult(AppTypeListActivity.getStartIntent(this, AppTypes.TYPE_ZHILIANG_SAMPLE_TYPE), REQUEST_CODE_ADD_SAMPLE_TYPE);
        }
    }

    @OnClick(R.id.item_add_company)
    void showCompany() {
        if (!AppUtils.isEditable(jianCeDan)) {
            return;
        }
        if (jianCeDan.getSampleType() == null) {
            showToast("请先选择抽样环节！");
            return;
        }
        startActivityForResult(AddCompanyActivity.getStartIntent(this, null), REQUEST_CODE_ADD_COMPANY);
    }

    @OnClick(R.id.item_add_sampling)
    void showSampling() {
        if (!AppUtils.isEditable(jianCeDan)) {
            return;
        }
        if (jianCeDan == null || !AppUtils.isHaveId(jianCeDan.getClientID())) {
            showToast("请先选择受检单位！");
            return;
        }
        if (jianCeDan.getSampleType() == null) {
            showToast("请先选择抽样环节！");
            return;
        }
        switch (jianCeDan.getEDanType()) {
            case JianCeDan.DAN_TYPE_SAMPLING_DRUG:
                ShouYaoCanLiuSample canLiuSample;
                if (SamplingApplication.getInstance().getShouYaoCanLiuSample() != null){
                    canLiuSample = SamplingApplication.getInstance().getShouYaoCanLiuSample();
                    SamplingApplication.getInstance().setShouYaoCanLiuSample(null);
                }else{
                    canLiuSample = new ShouYaoCanLiuSample();
                }
                canLiuSample.setFormId(jianCeDan.getID());
                startActivityForResult(AddSamplingActivity.getStartIntent(this, canLiuSample, jianCeDan.getClientUnit(), jianCeDan.getSampleHuanjie(), simpleCodeList), REQUEST_CODE_ADD_SAMPLE);
                break;
            case JianCeDan.DAN_TYPE_SAMPLING_QUALITY:
                ZhiLiangChouYang zhiLiangChouYang = new ZhiLiangChouYang();
                zhiLiangChouYang.setFormId(jianCeDan.getID());
                startActivityForResult(AddSamplingActivity.getStartIntent(this, zhiLiangChouYang, jianCeDan.getClientUnit(), jianCeDan.getSampleHuanjie()), REQUEST_CODE_ADD_SAMPLE);
                break;
            case JianCeDan.DAN_TYPE_SAMPLING_VERIFICATION:
                YangPinHeShi yangPinHeShi = new YangPinHeShi();
                yangPinHeShi.setFormId(jianCeDan.getID());
                startActivityForResult(AddSamplingActivity.getStartIntent(this, yangPinHeShi, jianCeDan.getClientUnit()), REQUEST_CODE_ADD_SAMPLE);
                break;
        }
    }

    @OnClick(R.id.item_add_signature)
    void showSignature() {
        if (!AppUtils.isEditable(jianCeDan)) {
            return;
        }
        if (jianCeDan == null || !AppUtils.isHaveId(jianCeDan.getClientID())) {
            showToast("请先选择受检单位！");
            return;
        }
        if (jianCeDan.getSampleType() == null) {
            showToast("请先选择抽样环节！");
            return;
        }
        startActivityForResult(SignatureActivity.getStartIntent(this, jianCeDan), REQUEST_CODE_ADD_SIGNATURE);
    }

    void initClientUnit() {
        final ClientUnit clientUnit = jianCeDan.getClientUnit();
        if (clientUnit != null) {
            panelCompanyArea.setVisibility(View.VISIBLE);
            tvCompany.setText(clientUnit.getName());
            panelCompanyArea.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!AppUtils.isEditable(jianCeDan)) {
                        return;
                    }
                    startActivityForResult(AddCompanyActivity.getStartIntent(FormDetailActivity.this, clientUnit), REQUEST_CODE_ADD_COMPANY);
                }
            });
        } else {
            panelCompanyArea.setVisibility(View.GONE);
        }
    }

    void initSampleArea() {
        if (!AppUtils.isHaveId(jianCeDan.getID())) {
            panelSampleArea.removeAllViews();
            return;
        }
        switch (jianCeDan.getEDanType()) {
            case JianCeDan.DAN_TYPE_SAMPLING_DRUG:
                initShouYaoCanLiu();
                break;
            case JianCeDan.DAN_TYPE_SAMPLING_QUALITY:
                initZhiliangChouyang();
                break;
            case JianCeDan.DAN_TYPE_SAMPLING_VERIFICATION:
                initYangPinHeShi();
                break;
        }
    }

    void initFormImage() {
        final ArrayList<String> formImageList = new ArrayList<>();
        panelFormImage.setVisibility(View.GONE);
        panelFormImageParent.removeAllViews();
        String fileIds = jianCeDan.getFileIDs();
        Observable.just(fileIds).flatMap(new Func1<String, Observable<String>>() {
            @Override
            public Observable<String> call(String s) {
                if (TextUtils.isEmpty(s)) {
                    return Observable.empty();
                }
                String[] idArr = s.split(Constants.FILE_ID_SEPARATOR);
                return Observable.from(idArr);
            }
        }).map(new Func1<String, AppFiles>() {
            @Override
            public AppFiles call(String s) {
                if (TextUtils.isEmpty(s)) {
                    return null;
                }
                return SamplingApplication.getDaoSession().getAppFilesDao().load(Long.parseLong(s));
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommonSubscriber<AppFiles>() {

                    @Override
                    public void onNext(final AppFiles appFile) {
                        super.onNext(appFile);
                        if (appFile == null || !FileUtils.isFileExist(appFile.getFilePath())) {
                            return;
                        }
                        switch (appFile.getFileType()) {
                            case Constants.TYPE_FORM_IMAGE:
                                panelFormImage.setVisibility(View.VISIBLE);
                                ImageView ivForm = new ImageView(FormDetailActivity.this);
                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                                params.bottomMargin = UIUtils.convertDpToPixel(16, FormDetailActivity.this);
                                ivForm.setLayoutParams(params);
                                ivForm.setAdjustViewBounds(true);

                                LinearLayout container = (LinearLayout) LayoutInflater.from(FormDetailActivity.this).inflate(R.layout.container_image_layout, null);
                                container.addView(ivForm);
                                container.findViewById(R.id.form_print_icon).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String imagePath = appFile.getFilePath();
                                        try {
                                            File file = new File(imagePath);
                                            Intent i = new Intent(Intent.ACTION_VIEW);
                                            i.setPackage("com.dynamixsoftware.printershare");
                                            i.setDataAndType(Uri.fromFile(file),"image/jpeg");
                                            startActivity(i);
                                        }catch (Exception e){
                                            ToastUtils.shortToast(FormDetailActivity.this, "出现错误，请重新生成");
                                        }
                                    }
                                });
                                panelFormImageParent.addView(container);
                                ivForm.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        startActivity(ImageShowActivity.getStartIntent(FormDetailActivity.this, formImageList, formImageList.indexOf(appFile.getFilePath())));
                                    }
                                });
                                formImageList.add(appFile.getFilePath());
                                ImageLoaderUtils.displayImageForIv(ivForm, ImageDownloader.Scheme.FILE.wrap(appFile.getFilePath()));
                                break;
                        }
                    }
                });
    }

    private void initSignatureArea() {
        itemJingBanren.setContent(jianCeDan.getClientUser());
        itemJingBanren.setVisibility(TextUtils.isEmpty(jianCeDan.getClientUser()) ? View.GONE : View.VISIBLE);
    }

    private void initYangPinHeShi() {
        Observable.just(jianCeDan.getID()).flatMap(new Func1<Long, Observable<YangPinHeShi>>() {
            @Override
            public Observable<YangPinHeShi> call(Long jianCeDanId) {
                return Observable.from(SamplingApplication.getDaoSession().getYangPinHeShiDao().queryRaw("where Form_ID=?", String.valueOf(jianCeDanId)));
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommonSubscriber<YangPinHeShi>() {

                    private int number;

                    @Override
                    public void onStart() {
                        super.onStart();
                        number = 1;
                        panelSampleArea.removeAllViews();
                    }

                    @Override
                    public void onNext(YangPinHeShi o) {
                        super.onNext(o);
                        View view = mInflater.inflate(R.layout.item_sample, null);
                        TextView tvNumber = (TextView) view.findViewById(R.id.tv_number);
                        tvNumber.setText(String.valueOf(number));
                        number++;
                        panelSampleArea.addView(view);
                        initYangPinHeShi(view, o);
                        if (o.getFinished()) {
                            view.setBackgroundResource(number % 2 == 0 ? R.color.color_sample : R.color.color_sample_even);
                        } else {
                            view.setBackgroundResource(R.color.color_sample_not_finish);
                        }
                    }
                });
    }

    private void initZhiliangChouyang() {
        Observable.just(jianCeDan.getID()).flatMap(new Func1<Long, Observable<ZhiLiangChouYang>>() {
            @Override
            public Observable<ZhiLiangChouYang> call(Long jianCeDanId) {
                return Observable.from(SamplingApplication.getDaoSession().getZhiLiangChouYangDao().queryRaw("where Form_ID=?", String.valueOf(jianCeDanId)));
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommonSubscriber<ZhiLiangChouYang>() {

                    private int number;

                    @Override
                    public void onStart() {
                        super.onStart();
                        number = 1;
                        panelSampleArea.removeAllViews();
                    }

                    @Override
                    public void onNext(ZhiLiangChouYang o) {
                        super.onNext(o);
                        View view = mInflater.inflate(R.layout.item_sample, null);
                        TextView tvNumber = (TextView) view.findViewById(R.id.tv_number);
                        tvNumber.setText(String.valueOf(number));
                        number++;
                        panelSampleArea.addView(view);
                        initZhiliangChouyang(view, o);
                        if (o.getFinished()) {
                            view.setBackgroundResource(number % 2 == 0 ? R.color.color_sample : R.color.color_sample_even);
                        } else {
                            view.setBackgroundResource(R.color.color_sample_not_finish);
                        }
                    }
                });
    }

    private void initShouYaoCanLiu() {
        Observable.just(jianCeDan.getID()).flatMap(new Func1<Long, Observable<ShouYaoCanLiuSample>>() {
            @Override
            public Observable<ShouYaoCanLiuSample> call(Long jianCeDanId) {
                return Observable.from(SamplingApplication.getDaoSession().getShouYaoCanLiuSampleDao().queryRaw("where Form_ID=?", String.valueOf(jianCeDanId)));
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommonSubscriber<ShouYaoCanLiuSample>() {
                    private int number;

                    @Override
                    public void onStart() {
                        super.onStart();
                        number = 1;
                        panelSampleArea.removeAllViews();
                    }

                    @Override
                    public void onNext(ShouYaoCanLiuSample o) {
                        super.onNext(o);
                        View view = mInflater.inflate(R.layout.item_sample, null);
                        TextView tvNumber = (TextView) view.findViewById(R.id.tv_number);
                        tvNumber.setText(String.valueOf(number));
                        number++;
                        panelSampleArea.addView(view);
                        String code = o.getCode();
                        if (!simpleCodeList.contains(code)){
                            simpleCodeList.add(code);
                        }
                        initShouYaoCanLiuView(view, o);
                        if (o.getFinished()) {
                            view.setBackgroundResource(number % 2 == 0 ? R.color.color_sample : R.color.color_sample_even);
                        } else {
                            view.setBackgroundResource(R.color.color_sample_not_finish);
                        }
                    }
                });
    }

    private void initYangPinHeShi(View view, final YangPinHeShi yangPinHeShi) {
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (!AppUtils.isEditable(jianCeDan)) {
                    return true;
                }
                EPocketAlertDialog.getInstance().showAlertContent(getSupportFragmentManager(), "是否删除该条信息？", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SamplingApplication.getDaoSession().getYangPinHeShiDao().delete(yangPinHeShi);
                        showToast("删除成功！");
                        initYangPinHeShi();
                        deleteFormImages();
                    }
                });

                return true;
            }
        });
        PreferenceRightDetailView bianhao = (PreferenceRightDetailView) view.findViewById(R.id.item_yangpinhao);
        PreferenceRightDetailView mingcheng = (PreferenceRightDetailView) view.findViewById(R.id.item_yangpinmingcheng);
        mingcheng.setVisibility(View.GONE);
        bianhao.setContent(yangPinHeShi.getCode());
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!AppUtils.isEditable(jianCeDan)) {
                    return;
                }
                startActivityForResult(AddSamplingActivity.getStartIntent(FormDetailActivity.this, yangPinHeShi, jianCeDan.getClientUnit()), REQUEST_CODE_ADD_SAMPLE);
            }
        });
    }

    private void initZhiliangChouyang(View view, final ZhiLiangChouYang zhiLiangChouYang) {
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (!AppUtils.isEditable(jianCeDan)) {
                    return true;
                }
                EPocketAlertDialog.getInstance().showAlertContent(getSupportFragmentManager(), "是否删除该抽样信息？", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SamplingApplication.getDaoSession().getZhiLiangChouYangDao().delete(zhiLiangChouYang);
                        showToast("删除成功！");
                        initZhiliangChouyang();
                        deleteFormImages();
                    }
                });

                return true;
            }
        });
        PreferenceRightDetailView bianhao = (PreferenceRightDetailView) view.findViewById(R.id.item_yangpinhao);
        PreferenceRightDetailView mingcheng = (PreferenceRightDetailView) view.findViewById(R.id.item_yangpinmingcheng);
        bianhao.setContent(zhiLiangChouYang.getCode());
        mingcheng.setContent(zhiLiangChouYang.getSampleName());
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!AppUtils.isEditable(jianCeDan)) {
                    return;
                }
                startActivityForResult(AddSamplingActivity.getStartIntent(FormDetailActivity.this, zhiLiangChouYang, jianCeDan.getClientUnit(), jianCeDan.getSampleHuanjie()), REQUEST_CODE_ADD_SAMPLE);
            }
        });
    }

    private void initShouYaoCanLiuView(View view, final ShouYaoCanLiuSample sample) {
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (!AppUtils.isEditable(jianCeDan)) {
                    return true;
                }
                EPocketAlertDialog.getInstance().showAlertContent(getSupportFragmentManager(), "是否删除该样品信息？", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SamplingApplication.getDaoSession().getShouYaoCanLiuSampleDao().delete(sample);
                        showToast("删除成功！");
                        initShouYaoCanLiu();
                        deleteFormImages();
                    }
                });

                return true;
            }
        });
        PreferenceRightDetailView bianhao = (PreferenceRightDetailView) view.findViewById(R.id.item_yangpinhao);
        PreferenceRightDetailView mingcheng = (PreferenceRightDetailView) view.findViewById(R.id.item_yangpinmingcheng);

        bianhao.setContent(sample.getCode());
        mingcheng.setContent(sample.getName());

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!AppUtils.isEditable(jianCeDan)) {
                    return;
                }
                startActivityForResult(AddSamplingActivity.getStartIntent(FormDetailActivity.this, sample, jianCeDan.getClientUnit(), jianCeDan.getSampleHuanjie(), simpleCodeList), REQUEST_CODE_ADD_SAMPLE);
            }
        });
    }

    @OnClick({R.id.tab_clear_form, R.id.tab_create_image, R.id.tab_print, R.id.tab_archive})
    public void onClick(View view) {

        if (!AppUtils.isHaveId(jianCeDan.getID())) {
            return;
        }
        switch (view.getId()) {
            case R.id.tab_clear_form:
                EPocketAlertDialog.getInstance().showAlertContent(getSupportFragmentManager(), "确定要清空表单？", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SamplingApplication.getDaoSession().getJianCeDanDao().delete(jianCeDan);
                        showToast("删除成功！");
                        finish();
                    }
                });
                break;
            case R.id.tab_create_image:
                createImage();
                break;
            case R.id.tab_print:
                printFormImage();
                break;
            case R.id.tab_archive:
                uploadArchives();
                break;
        }
    }

    private void printFormImage() {
        ArrayList<String> formImageList = getFormImageList();
        if (ListUtils.isEmpty(formImageList)) {
            return;
        }
        ToastUtils.shortToast(FormDetailActivity.this, "请点击表单右上方打印按钮，完成打印");
//        Intent starter = new Intent(this, PrintSettingsActivity.class);
//        startActivityForResult(starter, REQUEST_CODE_PRINT_SETTINGS);
    }

    private ArrayList<String> getFormImageList() {
        if (TextUtils.isEmpty(jianCeDan.getFileIDs())) {
            showToast("请先完善表单数据！");
            return null;
        }
        String[] idArr = jianCeDan.getFileIDs().split(Constants.FILE_ID_SEPARATOR);
        ArrayList<String> formImageList = new ArrayList<>();
        for (String idString : idArr) {
            AppFiles appFile = SamplingApplication.getDaoSession().getAppFilesDao().load(Long.parseLong(idString));
            switch (appFile.getFileType()) {
                case Constants.TYPE_FORM_IMAGE:
                    if (appFile == null) {
                        throw new FormException("文件不存在！");
                    }
                    File file = new File(appFile.getFilePath());
                    if (!file.exists()) {
                        throw new FormException("文件不存在！");
                    }
                    if (!MD5Utils.checkMD5(appFile.getMD5(), file)) {
                        throw new FormException("文件被篡改！表单无效！");
                    }
                    formImageList.add(file.getAbsolutePath());
                    break;
            }
        }
        if (ListUtils.isEmpty(formImageList)) {
            showToast("请先生成表单图片！");
            return null;
        }
        return formImageList;
    }

    private void uploadArchives() {
        if (TextUtils.isEmpty(jianCeDan.getFileIDs())) {
            showToast("请先完善表单数据！");
            return;
        }
        if (ListUtils.isEmpty(getFormImageList())) {
            return;
        }
        if (jianCeDan.getStatus() == JianCeDan.STATUS_ARCHIVED) {
            showToast("该表单已经归档！");
            return;
        }
        showProgressDialog("正在加载..");
        switch (jianCeDan.getEDanType()) {
            case JianCeDan.DAN_TYPE_SAMPLING_DRUG:
                FormRepository.getInstance().uploadShouYaoJianCe(jianCeDan).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new CommonSubscriber<FormBean<ShouYaoCanLiuSample>>() {

                            @Override
                            public void onNext(FormBean<ShouYaoCanLiuSample> o) {
                                super.onNext(o);
                                dismissProgressDialog();
                                jianCeDan.setStatus(JianCeDan.STATUS_ARCHIVED);
                                showToast("上传成功！");
                                initQRCodeArea();
                            }

                            @Override
                            public void onError(Throwable e) {
                                super.onError(e);
                                dismissProgressDialog();
                                showToast(ErrorMessageFactory.create(e));
                            }
                        });
                break;
            case JianCeDan.DAN_TYPE_SAMPLING_QUALITY:
                FormRepository.getInstance().uploadZhiLiangChouYang(jianCeDan).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new CommonSubscriber<FormBean<ZhiLiangChouYang>>() {

                            @Override
                            public void onNext(FormBean<ZhiLiangChouYang> o) {
                                super.onNext(o);
                                initQRCodeArea();
                                dismissProgressDialog();
                                jianCeDan.setStatus(JianCeDan.STATUS_ARCHIVED);
                                showToast("上传成功！");
                            }

                            @Override
                            public void onError(Throwable e) {
                                super.onError(e);
                                dismissProgressDialog();
                                showToast(ErrorMessageFactory.create(e));
                            }
                        });
                break;
            case JianCeDan.DAN_TYPE_SAMPLING_VERIFICATION:
                FormRepository.getInstance().uploadYangPinHeShi(jianCeDan).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new CommonSubscriber<FormBean<YangPinHeShi>>() {

                            @Override
                            public void onNext(FormBean<YangPinHeShi> o) {
                                super.onNext(o);
                                initQRCodeArea();
                                dismissProgressDialog();
                                jianCeDan.setStatus(JianCeDan.STATUS_ARCHIVED);
                                showToast("上传成功！");
                            }

                            @Override
                            public void onError(Throwable e) {
                                super.onError(e);
                                dismissProgressDialog();
                                showToast(ErrorMessageFactory.create(e));
                            }
                        });
                break;
            default:
                return;
        }
    }

    private void initQRCodeArea() {
        if (jianCeDan.getStatus() != JianCeDan.STATUS_ARCHIVED) {
            panelQRCodeArea.setVisibility(View.GONE);
            return;
        }
        ZXingLibrary.initDisplayOpinion(this);
        panelQRCodeArea.setVisibility(View.VISIBLE);
        ItemIndicator itemSendEmail = (ItemIndicator) panelQRCodeArea.findViewById(R.id.item_send_email);
        final ImageView ivQRCode = (ImageView) panelQRCodeArea.findViewById(R.id.iv_qrcode);
        Observable.just(jianCeDan).map(new Func1<JianCeDan, Bitmap>() {
            @Override
            public Bitmap call(JianCeDan jianCeDan) {
                return CodeUtils.createImage(jianCeDan.getFormImageUrl(), 600, 600, null);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommonSubscriber<Bitmap>() {

                    @Override
                    public void onNext(Bitmap bitmap) {
                        super.onNext(bitmap);
                        ivQRCode.setImageBitmap(bitmap);
                    }
                });
        itemSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendEmailDialog.getInstance().showEmail(getSupportFragmentManager(), jianCeDan.getClientUnit().getEmail(), new SendEmailDialog.InputListener() {
                    @Override
                    public void onInput(String input) {
                        sendEmail(input);
                    }
                });
            }
        });

    }

    private void sendEmail(String email) {

        showProgressDialog("发送中...");
        HttpService.getApi().sendEmailFormJianCeDan(jianCeDan.getFormRemoteId(), email)
                .lift(new OperatorRequestMap<String>())
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommonSubscriber<String>() {

                    @Override
                    public void onNext(String o) {
                        super.onNext(o);
                        dismissProgressDialog();
                        showToast("发送成功！");
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        dismissProgressDialog();
                        showToast(ErrorMessageFactory.create(e));
                    }
                });
    }

    private void createImage() {
        if (!AppUtils.isEditable(jianCeDan)) {
            showToast("已归档不能修改！");
            return;
        }
        Observable<List<View>> sampleListObservable = null;
        switch (jianCeDan.getEDanType()) {
            case JianCeDan.DAN_TYPE_SAMPLING_DRUG:
                sampleListObservable = getShouYoCanliuViewList();
                break;
            case JianCeDan.DAN_TYPE_SAMPLING_QUALITY:
                sampleListObservable = getZhiLiangChouYangViewList();
                break;
            case JianCeDan.DAN_TYPE_SAMPLING_VERIFICATION:
                sampleListObservable = getYangPinHeshiViewList();
                break;
            default:
                return;
        }

        showProgressDialog("正在加载..");
        sampleListObservable.flatMap(new Func1<List<View>, Observable<FormView>>() {
            @Override
            public Observable<FormView> call(final List<View> viewList) {
                ImageLoaderUtils.clearCache();
                return Observable.from(viewList).flatMap(new Func1<View, Observable<FormView>>() {
                    @Override
                    public Observable<FormView> call(View view) {
                        return getSignatureInfo(view, viewList.indexOf(view) + 1);
                    }
                });
            }
        }).observeOn(Schedulers.io()).map(new Func1<FormView, FormView>() {
            @Override
            public FormView call(FormView formView) {
                formView.setFormImageFile(AppUtils.generateImage(formView.getFormView(), AppUtils.generateFormFileName(jianCeDan.getID(), formView.getOrder())));
                return formView;
            }
        }).subscribeOn(AndroidSchedulers.mainThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommonSubscriber<FormView>() {

                    List<FormView> formViewList = new ArrayList<>();

                    @Override
                    public void onStart() {
                        super.onStart();
                        if (TextUtils.isEmpty(jianCeDan.getFileIDs())) {
                            return;
                        }
                        String[] idArr = jianCeDan.getFileIDs().split(Constants.FILE_ID_SEPARATOR);
                        String fileIds = "";
                        int i = 0;
                        for (String idString : idArr) {
                            i++;
                            AppFiles appFile = SamplingApplication.getDaoSession().getAppFilesDao().load(Long.parseLong(idString));
                            if (appFile == null) {
                                continue;
                            }
                            File file = new File(appFile.getFilePath());
                            if (!file.exists()) {
                                continue;
                            }
                            if (!MD5Utils.checkMD5(appFile.getMD5(), file)) {
                                continue;
                            }
                            if (Constants.TYPE_FORM_IMAGE.equals(appFile.getFileType())) {
                                continue;
                            }
                            fileIds = fileIds.concat(String.valueOf(appFile.getID()));
                            fileIds = fileIds.concat(Constants.FILE_ID_SEPARATOR);
                        }
                        if (fileIds.length() > 0) {
                            fileIds = fileIds.substring(0, fileIds.length() - 1);
                        }
                        if (!fileIds.equals(jianCeDan.getFileIDs())) {
                            jianCeDan.setFileIDs(fileIds);
                        }
                        SamplingApplication.getDaoSession().getJianCeDanDao().save(jianCeDan);
                    }

                    @Override
                    public void onNext(FormView formView) {
                        super.onNext(formView);
                        formViewList.add(formView);
                    }

                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                        Collections.sort(formViewList);
                        for (FormView formView : formViewList) {
                            File file = formView.getFormImageFile();
                            AppFiles files = new AppFiles();
                            files.setCreateTime(new Date());
                            files.setTitle("表单" + jianCeDan.getID());
                            files.setFilePath(file.getAbsolutePath());
                            files.setFileType("JPEG");
                            files.setFileLength(String.valueOf(file.length()));
                            files.setMD5(MD5Utils.calculateMD5(file));
                            files.setFileSize(String.valueOf(file.length()));
                            files.setFileType(Constants.TYPE_FORM_IMAGE);
                            SamplingApplication.getDaoSession().getAppFilesDao().save(files);
                            String ids = jianCeDan.getFileIDs().concat(Constants.FILE_ID_SEPARATOR) + files.getID();
                            jianCeDan.setFileIDs(ids);
                        }
                        jianCeDan.setStatus(JianCeDan.STATUS_PRINTED);
                        SamplingApplication.getDaoSession().getJianCeDanDao().save(jianCeDan);

                        initFormImage();
                        dismissProgressDialog();
                        showToast("生成表单成功！");
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        dismissProgressDialog();
                        showToast(ErrorMessageFactory.create(e));
                    }
                });
    }

    //兽药产品质量监督抽样单
    private Observable<List<View>> getZhiLiangChouYangViewList() {
        return Observable.just(jianCeDan).map(new Func1<JianCeDan, List<ZhiLiangChouYang>>() {
            @Override
            public List<ZhiLiangChouYang> call(JianCeDan jianCeDan) {
                List<ZhiLiangChouYang> sampleList = SamplingApplication.getDaoSession().getZhiLiangChouYangDao().queryDeep("where Form_ID=?", String.valueOf(jianCeDan.getID()));
                if (ListUtils.isEmpty(sampleList)) {
                    throw new FormException("请先填写表单数据！");
                }
                for (ZhiLiangChouYang sample : sampleList) {
                    if (!sample.getFinished()) {
//                        String failDes = sample.getFailDes();
//                        if (!TextUtils.isEmpty(failDes) && failDes.endsWith("，")){
//                            failDes = failDes.substring(0, failDes.length() - 1);
//                        }
                        throw new FormException("样品编号" + sample.getCode() + "的"  + "信息未填写完整，不能生成图片");
                    }
                }
                return sampleList;
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).map(new Func1<List<ZhiLiangChouYang>, List<View>>() {
            @Override
            public List<View> call(List<ZhiLiangChouYang> zhiLiangChouYangList) {
                List<View> formViewList = new ArrayList<View>();
                View formView = mInflater.inflate(R.layout.view_validate_sampling, null);
                formViewList.add(formView);
                LinearLayout panelSampleArea = (LinearLayout) formView.findViewById(R.id.panel_sample_area);
                if (!ListUtils.isEmpty(zhiLiangChouYangList)) {
                    for (ZhiLiangChouYang sample : zhiLiangChouYangList) {
                        View chouYangView = mInflater.inflate(R.layout.item_zhiliangchouyang_form, null);
                        panelSampleArea.addView(chouYangView);
                        ViewDataBinding binding = DataBindingUtil.bind(chouYangView);
                        binding.setVariable(BR.chouyang, sample);
                        binding.executePendingBindings();
                        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(Constants.FORM_IMAGE_WIDTH, View.MeasureSpec.AT_MOST);
                        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                        formView.measure(widthMeasureSpec, heightMeasureSpec);
                        if (formView.getMeasuredHeight() > Constants.FORM_IMAGE_HEIGHT) {
                            panelSampleArea.removeView(chouYangView);
                            formView = mInflater.inflate(R.layout.view_validate_sampling, null);
                            formViewList.add(formView);
                            panelSampleArea = (LinearLayout) formView.findViewById(R.id.panel_sample_area);
                            panelSampleArea.addView(chouYangView);
                        }
                    }
                    formView = null;
                    if (!"生产".equals(jianCeDan.getSampleType())) {
                        for (ZhiLiangChouYang sample : zhiLiangChouYangList) {
                            if (formView == null) {
                                formView = mInflater.inflate(R.layout.view_quality_sampling, null);
                                formViewList.add(formView);
                                panelSampleArea = (LinearLayout) formView.findViewById(R.id.panel_sample_area);
                            }
                            View heshiView = mInflater.inflate(R.layout.item_quality_sampling, null);
                            panelSampleArea.addView(heshiView);
                            ViewDataBinding binding = DataBindingUtil.bind(heshiView);
                            binding.setVariable(BR.heshi, sample);
                            binding.executePendingBindings();
                            int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(Constants.FORM_IMAGE_WIDTH, View.MeasureSpec.AT_MOST);
                            int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                            formView.measure(widthMeasureSpec, heightMeasureSpec);
                            if (formView.getMeasuredHeight() > Constants.FORM_IMAGE_HEIGHT) {
                                panelSampleArea.removeView(heshiView);
                                formView = mInflater.inflate(R.layout.view_quality_sampling, null);
                                formViewList.add(formView);
                                panelSampleArea = (LinearLayout) formView.findViewById(R.id.panel_sample_area);
                                panelSampleArea.addView(heshiView);
                            }
                        }
                    }
                }
                return formViewList;
            }
        });
    }

    //兽药产品质量抽样核实单
    private Observable<List<View>> getYangPinHeshiViewList() {
        return Observable.just(jianCeDan).map(new Func1<JianCeDan, List<YangPinHeShi>>() {
            @Override
            public List<YangPinHeShi> call(JianCeDan jianCeDan) {
                List<YangPinHeShi> sampleList = SamplingApplication.getDaoSession().getYangPinHeShiDao().queryDeep("where Form_ID=?", String.valueOf(jianCeDan.getID()));
                ;
                if (ListUtils.isEmpty(sampleList)) {
                    throw new FormException("请先填写表单数据！");
                }
                for (YangPinHeShi sample : sampleList) {
                    if (!sample.getFinished()) {
                        throw new FormException("样品编号 " + sample.getCode() + " 的信息未填写完整，不能生成图片");
                    }
                }
                return sampleList;
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).map(new Func1<List<YangPinHeShi>, List<View>>() {
            @Override
            public List<View> call(List<YangPinHeShi> shouYaoCanLiuSamples) {
                List<View> formViewList = new ArrayList<View>();
                View formView = mInflater.inflate(R.layout.view_quality_sampling, null);
                formViewList.add(formView);
                LinearLayout panelSampleArea = (LinearLayout) formView.findViewById(R.id.panel_sample_area);
                if (!ListUtils.isEmpty(shouYaoCanLiuSamples)) {
                    for (YangPinHeShi sample : shouYaoCanLiuSamples) {
                        View canLiuView = mInflater.inflate(R.layout.item_quality_sampling, null);
                        panelSampleArea.addView(canLiuView);
                        ViewDataBinding binding = DataBindingUtil.bind(canLiuView);
                        binding.setVariable(BR.heshi, sample);
                        binding.executePendingBindings();
                        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(Constants.FORM_IMAGE_WIDTH, View.MeasureSpec.AT_MOST);
                        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                        formView.measure(widthMeasureSpec, heightMeasureSpec);
                        if (formView.getMeasuredHeight() > Constants.FORM_IMAGE_HEIGHT) {
                            panelSampleArea.removeView(canLiuView);
                            formView = mInflater.inflate(R.layout.view_quality_sampling, null);
                            formViewList.add(formView);
                            panelSampleArea = (LinearLayout) formView.findViewById(R.id.panel_sample_area);
                            panelSampleArea.addView(canLiuView);
                        }
                    }
                }
                return formViewList;
            }
        });
    }

    private void checkJinHuoUnit(JianCeDan _jianCeDan) {
        if (_jianCeDan == null){
            return;
        }
        List<ShouYaoCanLiuSample> sampleList = SamplingApplication.getDaoSession().getShouYaoCanLiuSampleDao().queryDeep("where Form_ID=?", String.valueOf(_jianCeDan.getID()));
        if (ListUtils.isEmpty(sampleList)) {
            return;
        }
        for (ShouYaoCanLiuSample sample : sampleList) {
            checkJinHuoAndReset(sample);
        }
    }

    private void checkJinHuoAndReset(final ShouYaoCanLiuSample sample){
        if (sample == null){
            return;
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
                            String value = appType.getValueName();
                            if ("自产".equals(value) || "自养".equals(value)){
                                long sampleSourceId = sample.getSampleSourceID();
                                long formSourceId = jianCeDan.getClientID();
                                if (sampleSourceId != formSourceId){
                                    sample.setSampleSource(jianCeDan.getClientUnit());
                                    sample.setSampleSourceID(jianCeDan.getClientID());
                                    SamplingApplication.getDaoSession().getShouYaoCanLiuSampleDao().rx().save(sample);
                                }
                            }
                        }
                    });
        }
    }

    private Observable<List<View>> getShouYoCanliuViewList() {
        return Observable.just(jianCeDan).map(new Func1<JianCeDan, List<ShouYaoCanLiuSample>>() {
            @Override
            public List<ShouYaoCanLiuSample> call(JianCeDan jianCeDan) {
                List<ShouYaoCanLiuSample> sampleList = SamplingApplication.getDaoSession().getShouYaoCanLiuSampleDao().queryDeep("where Form_ID=?", String.valueOf(jianCeDan.getID()));
                if (ListUtils.isEmpty(sampleList)) {
                    throw new FormException("请先填写表单数据！");
                }
                List<String> codes = new ArrayList<>();
                for (ShouYaoCanLiuSample sample : sampleList) {
                    String code = sample.getCode();
                    if (codes.contains(code)){
                        throw new FormException("样品编号" + code + "有重复");
                    }else {
                        codes.add(code);
                    }

                    if (!sample.getFinished()) {
                        String failDes = sample.getFailDes();
                        if (!TextUtils.isEmpty(failDes) && failDes.endsWith("，")){
                            failDes = failDes.substring(0, failDes.length() - 1);
                        }
                        throw new FormException("样品编号" + sample.getCode() + "的" + failDes + "的信息未填写完整，不能生成图片");
                    }
                }
                return sampleList;
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).map(new Func1<List<ShouYaoCanLiuSample>, List<View>>() {
            @Override
            public List<View> call(List<ShouYaoCanLiuSample> shouYaoCanLiuSamples) {
                List<View> formViewList = new ArrayList<View>();
                View formView = mInflater.inflate(R.layout.view_drug_residues_sample, null);
                formViewList.add(formView);
                LinearLayout panelSampleArea = (LinearLayout) formView.findViewById(R.id.panel_sample_area);
                if (!ListUtils.isEmpty(shouYaoCanLiuSamples)) {
                    for (ShouYaoCanLiuSample sample : shouYaoCanLiuSamples) {
                        View canLiuView = mInflater.inflate(R.layout.item_shouyaocanliu, null);
                        ViewDataBinding binding = DataBindingUtil.bind(canLiuView);
                        binding.setVariable(BR.shouYao, sample);
                        binding.executePendingBindings();
                        TextView desView = (TextView) canLiuView.findViewById(R.id.item_shouyao_jinHuo_description);
                        desView.setText(sample.getJinHuoDescription());
                        panelSampleArea.addView(canLiuView);
                        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(Constants.FORM_IMAGE_WIDTH, View.MeasureSpec.AT_MOST);
                        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                        formView.measure(widthMeasureSpec, heightMeasureSpec);

                        if (formView.getMeasuredHeight() > Constants.FORM_IMAGE_HEIGHT) {
                            panelSampleArea.removeView(canLiuView);
                            formView = mInflater.inflate(R.layout.view_drug_residues_sample, null);
                            formViewList.add(formView);
                            panelSampleArea = (LinearLayout) formView.findViewById(R.id.panel_sample_area);
                            panelSampleArea.addView(canLiuView);
                        }
                    }
                }
                return formViewList;
            }
        });
    }

    private Observable<FormView> getSignatureInfo(final View shouYaoSampleView, final int order) {
        return Observable.just(jianCeDan).map(new Func1<JianCeDan, Signature>() {
            @Override
            public Signature call(JianCeDan jianCeDan) {
                if (TextUtils.isEmpty(jianCeDan.getFileIDs())) {
                    throw new FormException("请先完成签名！");
                }
                Signature signature = new Signature();
                String[] idArr = jianCeDan.getFileIDs().split(Constants.FILE_ID_SEPARATOR);
                if (idArr.length < 3){
                    throw new FormException("请完成3人签名");
                }
                String fileIds = "";
                int i = 0;
                for (String idString : idArr) {
                    i++;
                    AppFiles appFile = SamplingApplication.getDaoSession().getAppFilesDao().load(Long.parseLong(idString));
                    if (appFile == null) {
                        throw new FormException("签名文件不存在！");
                    }
                    File file = new File(appFile.getFilePath());
                    if (!file.exists()) {
                        throw new FormException("签名文件不存在！");
                    }
                    if (!MD5Utils.checkMD5(appFile.getMD5(), file)) {
                        throw new FormException("签名文件被篡改！表单无效！");
                    }
                    boolean isNeedSeparator = i != idArr.length;
                    fileIds = fileIds.concat(String.valueOf(appFile.getID()));
                    if (isNeedSeparator) {
                        fileIds = fileIds.concat(Constants.FILE_ID_SEPARATOR);
                    }
                    switch (appFile.getFileType()) {
                        case Constants.TYPE_BEIJIANREN_QIANZI:
                            signature.setShoujianren(new BitmapDrawable(BitmapFactory.decodeFile(appFile.getFilePath())));
                            break;
                        case Constants.TYPE_BEIJIANREN_QIANZI_2:
                            signature.setShoujianriqi(new BitmapDrawable(BitmapFactory.decodeFile(appFile.getFilePath())));
                            break;
                        case Constants.TYPE_CAIYANGREN_QIANZI:
                            signature.setCaiyangren(new BitmapDrawable(BitmapFactory.decodeFile(appFile.getFilePath())));
                            break;
                        case Constants.TYPE_CAIYANGREN_QIANZI_2:
                            signature.setCaiyangriqi(new BitmapDrawable(BitmapFactory.decodeFile(appFile.getFilePath())));
                            break;
                    }
                }
                if (!fileIds.equals(jianCeDan.getFileIDs())) {
                    jianCeDan.setFileIDs(fileIds);
                }
                return signature;
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).map(new Func1<Signature, FormView>() {
            @Override
            public FormView call(Signature signature) {
                ViewDataBinding binding = DataBindingUtil.bind(shouYaoSampleView);
                ImageView ivShoujianren = (ImageView) shouYaoSampleView.findViewById(R.id.iv_shoujianren);
                ImageView ivCaiyangren = (ImageView) shouYaoSampleView.findViewById(R.id.iv_caiyangren);
                ImageView ivCaiyangriqi = (ImageView) shouYaoSampleView.findViewById(R.id.iv_caiyangriqi);
                ImageView ivShoujianriqi = (ImageView) shouYaoSampleView.findViewById(R.id.iv_shoujianriqi);
                if (signature.isValid()) {
                    throw new FormException("表单签名不完整！");
                }
                ivShoujianren.setImageDrawable(signature.getShoujianren());
                ivCaiyangren.setImageDrawable(signature.getCaiyangren());
                ivCaiyangriqi.setImageDrawable(signature.getCaiyangriqi());
                ivShoujianriqi.setImageDrawable(signature.getShoujianriqi());

                binding.setVariable(BR.jianCedan, jianCeDan);
                shouYaoSampleView.setLayoutParams(new ViewGroup.LayoutParams(ScreenUtils.dpToPxInt(FormDetailActivity.this, 1050), ScreenUtils.dpToPxInt(FormDetailActivity.this, 1485)));
                binding.executePendingBindings();

                TextView tvClientUser = (TextView) shouYaoSampleView.findViewById(R.id.tv_client_user);
                tvClientUser.setText(jianCeDan.getClientUser());
                TextView tvTestUser = (TextView) shouYaoSampleView.findViewById(R.id.tv_test_user);
                tvTestUser.setText(jianCeDan.getTestUser());
                return new FormView(shouYaoSampleView, order);
            }
        });
    }

    @Override
    protected void onDestroy() {
        try {
            if (jianCeDan != null){
                if (jianCeDan.getClientUnit() == null || jianCeDan.getClientID() <= 0){
                    Log.e("chen", "delete success");
                    SamplingApplication.getDaoSession().getJianCeDanDao().delete(jianCeDan);
                }
            }
        }catch (Exception e){
            Log.e("chen", e.getMessage());
        }
        super.onDestroy();
    }
}
