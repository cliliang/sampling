//package com.cdv.sampling.activity;
//
//import android.content.Context;
//import android.content.Intent;
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import com.cdv.sampling.R;
//import com.cdv.sampling.SamplingApplication;
//import com.cdv.sampling.bean.AppFiles;
//import com.cdv.sampling.bean.AppTypes;
//import com.cdv.sampling.bean.ClientUnit;
//import com.cdv.sampling.bean.JianCeDan;
//import com.cdv.sampling.bean.ShouYaoCanLiuSample;
//import com.cdv.sampling.bean.YangPinHeShi;
//import com.cdv.sampling.bean.ZhiLiangChouYang;
//import com.cdv.sampling.constants.Constants;
//import com.cdv.sampling.image.ImageLoaderUtils;
//import com.cdv.sampling.repository.UserRepository;
//import com.cdv.sampling.rxandroid.CommonSubscriber;
//import com.cdv.sampling.utils.AppUtils;
//import com.cdv.sampling.utils.io.FileUtils;
//import com.cdv.sampling.widget.EPocketAlertDialog;
//import com.cdv.sampling.widget.PreferenceRightDetailView;
//import com.nostra13.universalimageloader.core.download.ImageDownloader;
//
//import java.util.Date;
//
//import butterknife.BindView;
//import butterknife.OnClick;
//import rx.Observable;
//import rx.android.schedulers.AndroidSchedulers;
//import rx.functions.Func1;
//import rx.schedulers.Schedulers;
//
//public class AddFormActivity extends BaseActivity {
//
//    private static final int REQUEST_CODE_ADD_COMPANY = 0;
//    private static final int REQUEST_CODE_MODIFY_COMPANY = 1;
//    private static final int REQUEST_CODE_ADD_SAMPLE = 1;
//    private static final int REQUEST_CODE_ADD_SIGNATURE = 2;
//
//    public static final int SAMPLING_DRUG = 0;
//    public static final int SAMPLING_QUALITY = 1;
//    public static final int SAMPLING_VERIFICATION = 2;
//    private static final String EXTRA_SAMPLING_TYPE = "EXTRA_SAMPLING_TYPE";
//
//    @BindView(R.id.panel_company_area)
//    LinearLayout panelCompanyArea;
//    @BindView(R.id.panel_sample_area)
//    LinearLayout panelSampleArea;
//    @BindView(R.id.activity_add_veterinary_drug)
//    LinearLayout activityAddVeterinaryDrug;
//    @BindView(R.id.tv_license)
//    TextView tvLicense;
//    @BindView(R.id.tv_address)
//    TextView tvAddress;
//    @BindView(R.id.tv_postCode)
//    TextView tvPostCode;
//    @BindView(R.id.tv_contact_people)
//    TextView tvContactPeople;
//    @BindView(R.id.tv_fax)
//    TextView tvFax;
//
//    @BindView(R.id.iv_beijianren)
//    ImageView ivBeijianren;
//    @BindView(R.id.iv_beijianriqi)
//    ImageView ivCaiyangrenQianzi2;
//    @BindView(R.id.iv_caiyangren)
//    ImageView ivCaiyangren;
//    @BindView(R.id.iv_caiyang_riqi)
//    ImageView ivBeijianrenQianming2;
//
//    private JianCeDan jianCeDan;
//
//    private int sampleType;
//
//    public static Intent getStartIntent(Context context, int samplingType) {
//        Intent intent = new Intent(context, AddFormActivity.class);
//        intent.putExtra(EXTRA_SAMPLING_TYPE, samplingType);
//        return intent;
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_add_form);
//        sampleType = getIntent().getIntExtra(EXTRA_SAMPLING_TYPE, SAMPLING_DRUG);
//        setMyTitle(AppUtils.getSampleType(sampleType));
//
//        jianCeDan = new JianCeDan();
//        jianCeDan.setCreateTime(new Date());
//        jianCeDan.setTestUser(UserRepository.getInstance().getCurrentUser().getUserName());
//        if (SAMPLING_DRUG == sampleType) {
//            jianCeDan.setEDanType(JianCeDan.DAN_TYPE_SAMPLING_DRUG);
//        } else if (SAMPLING_QUALITY == sampleType) {
//            jianCeDan.setEDanType(JianCeDan.DAN_TYPE_SAMPLING_QUALITY);
//        } else {
//            jianCeDan.setEDanType(JianCeDan.DAN_TYPE_SAMPLING_VERIFICATION);
//        }
//        jianCeDan.setStatus(JianCeDan.STATUS_INIT);
//        jianCeDan.setCreateUser(UserRepository.getInstance().getCurrentUser().getUserName());
//        initViews();
//    }
//
//    private void initViews() {
//        if (AppUtils.isHaveId(jianCeDan.getClientID())) {
//            Observable.just(jianCeDan.getClientID()).subscribeOn(Schedulers.io())
//                    .map(new Func1<Long, ClientUnit>() {
//                        @Override
//                        public ClientUnit call(Long id) {
//                            return SamplingApplication.getDaoSession().getClientUnitDao().load(id);
//                        }
//                    }).observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(new CommonSubscriber<ClientUnit>() {
//                        @Override
//                        public void onNext(ClientUnit o) {
//                            super.onNext(o);
//                            initClientUnit();
//                        }
//                    });
//        }
//        initSampleArea();
//    }
//
//    @OnClick(R.id.item_add_company)
//    void showCompany() {
//        startActivityForResult(SearchCompanyActivity.getStartIntent(this), REQUEST_CODE_ADD_COMPANY);
//    }
//
//    @OnClick(R.id.item_add_sampling)
//    void showSampling() {
//        if (!AppUtils.isHaveId(jianCeDan.getClientID())) {
//            showToast("请先选择受检单位！");
//            return;
//        }
//        if (sampleType == SAMPLING_DRUG) {
//            ShouYaoCanLiuSample canLiuSample = new ShouYaoCanLiuSample();
//            canLiuSample.setFormId(jianCeDan.getID());
//            startActivityForResult(AddSamplingActivity.getStartIntent(this, canLiuSample), REQUEST_CODE_ADD_SAMPLE);
//        } else if (sampleType == SAMPLING_QUALITY) {
//            ZhiLiangChouYang zhiLiangChouYang = new ZhiLiangChouYang();
//            zhiLiangChouYang.setFormId(jianCeDan.getID());
//            startActivityForResult(AddSamplingActivity.getStartIntent(this, zhiLiangChouYang), REQUEST_CODE_ADD_SAMPLE);
//        } else if (sampleType == SAMPLING_VERIFICATION) {
//            YangPinHeShi yangPinHeShi = new YangPinHeShi();
//            yangPinHeShi.setFormId(jianCeDan.getID());
//            startActivityForResult(AddSamplingActivity.getStartIntent(this, yangPinHeShi), REQUEST_CODE_ADD_SAMPLE);
//        }
//    }
//
//    @OnClick(R.id.item_add_signature)
//    void showSignature() {
//        if (!AppUtils.isHaveId(jianCeDan.getClientID())) {
//            showToast("请先选择受检单位！");
//            return;
//        }
//        startActivityForResult(SignatureActivity.getStartIntent(this, jianCeDan), REQUEST_CODE_ADD_SIGNATURE);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == REQUEST_CODE_ADD_COMPANY && resultCode == RESULT_OK) {
//            ClientUnit client = (ClientUnit) data.getSerializableExtra(SearchCompanyActivity.RESULT_KEY_SELECTED);
//            jianCeDan.setClientUnit(client);
//            jianCeDan.setClientID(client.getLocalID());
//            SamplingApplication.getDaoSession().getJianCeDanDao().rx().save(jianCeDan).subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread()).subscribe(new CommonSubscriber<JianCeDan>() {
//                @Override
//                public void onNext(JianCeDan o) {
//                    super.onNext(o);
//                    initClientUnit();
//                }
//            });
//        } else if (requestCode == REQUEST_CODE_ADD_SIGNATURE && resultCode == RESULT_OK) {
//            SamplingApplication.getDaoSession().getJianCeDanDao().refresh(jianCeDan);
//            initSignatureArea();
//        } else if (requestCode == REQUEST_CODE_ADD_SAMPLE && resultCode == RESULT_OK) {
//            initSampleArea();
//        } else if (requestCode == REQUEST_CODE_MODIFY_COMPANY && resultCode == RESULT_OK) {
//            SamplingApplication.getDaoSession().getJianCeDanDao().rx().refresh(jianCeDan).subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread()).subscribe(new CommonSubscriber<JianCeDan>() {
//                @Override
//                public void onNext(JianCeDan o) {
//                    super.onNext(o);
//                    initClientUnit();
//                }
//            });
//        }
//    }
//
//    void initClientUnit() {
//        final ClientUnit clientUnit = jianCeDan.getClientUnit();
//        if (clientUnit != null) {
//            panelCompanyArea.setVisibility(View.VISIBLE);
//            tvAddress.setText(clientUnit.getAddress());
//            tvContactPeople.setText(clientUnit.getContactUser());
//            tvFax.setText(clientUnit.getFax());
//            tvPostCode.setText(clientUnit.getZip());
//            tvLicense.setText(clientUnit.getBusinessCode());
//            panelCompanyArea.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    startActivityForResult(AddCompanyActivity.getStartIntent(AddFormActivity.this, clientUnit), REQUEST_CODE_MODIFY_COMPANY);
//                }
//            });
//        } else {
//            panelCompanyArea.setVisibility(View.GONE);
//        }
//    }
//
//    void initSampleArea() {
//        if (!AppUtils.isHaveId(jianCeDan.getID())) {
//            panelSampleArea.removeAllViews();
//            return;
//        }
//        if (sampleType == SAMPLING_DRUG) {
//            initShouYaoCanLiu();
//        } else if (sampleType == SAMPLING_QUALITY) {
//            initZhiliangChouyang();
//        } else if (sampleType == SAMPLING_VERIFICATION) {
//            initYangPinHeShi();
//        }
//    }
//
//    private void initYangPinHeShi() {
//        Observable.just(jianCeDan.getID()).flatMap(new Func1<Long, Observable<YangPinHeShi>>() {
//            @Override
//            public Observable<YangPinHeShi> call(Long jianCeDanId) {
//                return Observable.from(SamplingApplication.getDaoSession().getYangPinHeShiDao().queryRaw("where Form_ID=?", String.valueOf(jianCeDanId)));
//            }
//        }).observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new CommonSubscriber<YangPinHeShi>() {
//
//                    @Override
//                    public void onStart() {
//                        super.onStart();
//                        panelSampleArea.removeAllViews();
//                    }
//
//                    @Override
//                    public void onNext(YangPinHeShi o) {
//                        super.onNext(o);
//
//                        View view = mInflater.inflate(R.layout.item_yangpinheshi, null);
//                        panelSampleArea.addView(view);
//                        initYangPinHeShi(view, o);
//                    }
//                });
//    }
//
//    private void initZhiliangChouyang() {
//        Observable.just(jianCeDan.getID()).flatMap(new Func1<Long, Observable<ZhiLiangChouYang>>() {
//            @Override
//            public Observable<ZhiLiangChouYang> call(Long jianCeDanId) {
//                return Observable.from(SamplingApplication.getDaoSession().getZhiLiangChouYangDao().queryRaw("where Form_ID=?", String.valueOf(jianCeDanId)));
//            }
//        }).observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new CommonSubscriber<ZhiLiangChouYang>() {
//
//                    @Override
//                    public void onStart() {
//                        super.onStart();
//                        panelSampleArea.removeAllViews();
//                    }
//
//                    @Override
//                    public void onNext(ZhiLiangChouYang o) {
//                        super.onNext(o);
//
//                        View view = mInflater.inflate(R.layout.item_zhiliangchouyang, null);
//                        panelSampleArea.addView(view);
//                        initZhiliangChouyang(view, o);
//                    }
//                });
//    }
//
//
//    private void initSignatureArea() {
//        String fileIds = jianCeDan.getFileIDs();
//        Observable.just(fileIds).flatMap(new Func1<String, Observable<String>>() {
//            @Override
//            public Observable<String> call(String s) {
//                if (TextUtils.isEmpty(s)) {
//                    return Observable.empty();
//                }
//                String[] idArr = s.split(Constants.FILE_ID_SEPARATOR);
//                return Observable.from(idArr);
//            }
//        }).map(new Func1<String, AppFiles>() {
//            @Override
//            public AppFiles call(String s) {
//                if (TextUtils.isEmpty(s)) {
//                    return null;
//                }
//                return SamplingApplication.getDaoSession().getAppFilesDao().load(Long.parseLong(s));
//            }
//        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new CommonSubscriber<AppFiles>() {
//
//                    @Override
//                    public void onNext(final AppFiles appFile) {
//                        super.onNext(appFile);
//                        if (appFile == null || !FileUtils.isFileExist(appFile.getFilePath())) {
//                            return;
//                        }
//                        switch (appFile.getFileType()) {
//                            case Constants.TYPE_BEIJIANREN_QIANZI:
//                                ImageLoaderUtils.displayImageForIv(ivBeijianren, ImageDownloader.Scheme.FILE.wrap(appFile.getFilePath()));
//                                break;
//                            case Constants.TYPE_BEIJIAN_RIQI:
//                                ImageLoaderUtils.displayImageForIv(ivCaiyangrenQianzi2, ImageDownloader.Scheme.FILE.wrap(appFile.getFilePath()));
//                                break;
//                            case Constants.TYPE_CAIYANGREN_QIANZI:
//                                ImageLoaderUtils.displayImageForIv(ivCaiyangren, ImageDownloader.Scheme.FILE.wrap(appFile.getFilePath()));
//                                break;
//                            case Constants.TYPE_CAIYANGREN_QIANZI_2:
//                                ImageLoaderUtils.displayImageForIv(ivBeijianrenQianming2, ImageDownloader.Scheme.FILE.wrap(appFile.getFilePath()));
//                                break;
//                        }
//                    }
//                });
//    }
//
//
//    private void initShouYaoCanLiu() {
//        Observable.just(jianCeDan.getID()).flatMap(new Func1<Long, Observable<ShouYaoCanLiuSample>>() {
//            @Override
//            public Observable<ShouYaoCanLiuSample> call(Long jianCeDanId) {
//                return Observable.from(SamplingApplication.getDaoSession().getShouYaoCanLiuSampleDao().queryRaw("where Form_ID=?", String.valueOf(jianCeDanId)));
//            }
//        }).observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new CommonSubscriber<ShouYaoCanLiuSample>() {
//
//                    @Override
//                    public void onStart() {
//                        super.onStart();
//                        panelSampleArea.removeAllViews();
//                    }
//
//                    @Override
//                    public void onNext(ShouYaoCanLiuSample o) {
//                        super.onNext(o);
//
//                        View view = mInflater.inflate(R.layout.item_sample, null);
//                        panelSampleArea.addView(view);
//                        initShouYaoCanLiuView(view, o);
//                    }
//                });
//    }
//
//    private void initYangPinHeShi(View view, final YangPinHeShi yangPinHeShi) {
//        view.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View view) {
//                EPocketAlertDialog.getInstance().showAlertContent(getSupportFragmentManager(), "是否删除该条信息？", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        SamplingApplication.getDaoSession().getYangPinHeShiDao().delete(yangPinHeShi);
//                        showToast("删除成功！");
//                        initYangPinHeShi();
//                    }
//                });
//
//                return true;
//            }
//        });
//        PreferenceRightDetailView bianhao = (PreferenceRightDetailView) view.findViewById(R.id.item_yangpinhao);
//        PreferenceRightDetailView pizhunwenhao = (PreferenceRightDetailView) view.findViewById(R.id.item_pizhunwenhao);
//        PreferenceRightDetailView shengchanxuke = (PreferenceRightDetailView) view.findViewById(R.id.item_shengchanxuke);
//        PreferenceRightDetailView gmpzhenghao = (PreferenceRightDetailView) view.findViewById(R.id.item_gmpzhenghao);
//        final PreferenceRightDetailView danwei = (PreferenceRightDetailView) view.findViewById(R.id.item_danwei);
//        final PreferenceRightDetailView jinhuofangshi = (PreferenceRightDetailView) view.findViewById(R.id.item_jinhuofangshi);
//
//        bianhao.setContent(yangPinHeShi.getCode());
//        pizhunwenhao.setContent(yangPinHeShi.getPiZhunWenHao());
//        shengchanxuke.setContent(yangPinHeShi.getXukeCode());
//        gmpzhenghao.setContent(yangPinHeShi.getGMPCode());
//
//        if (AppUtils.isHaveId(yangPinHeShi.getSampleSourceID())) {
//            Observable.just(yangPinHeShi.getSampleSourceID()).map(new Func1<Long, ClientUnit>() {
//                @Override
//                public ClientUnit call(Long aLong) {
//                    return SamplingApplication.getDaoSession().getClientUnitDao().load(aLong);
//                }
//            }).subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(new CommonSubscriber<ClientUnit>() {
//                        @Override
//                        public void onNext(ClientUnit clientUnit) {
//                            super.onNext(clientUnit);
//                            danwei.setContent(clientUnit.getName());
//                        }
//                    });
//        }
//
//        if (AppUtils.isHaveId(yangPinHeShi.getGouMaiTypeId())) {
//            Observable.just(yangPinHeShi.getGouMaiTypeId()).map(new Func1<Long, AppTypes>() {
//                @Override
//                public AppTypes call(Long aLong) {
//                    return SamplingApplication.getDaoSession().getAppTypesDao().load(aLong);
//                }
//            }).subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(new CommonSubscriber<AppTypes>() {
//                        @Override
//                        public void onNext(AppTypes appType) {
//                            super.onNext(appType);
//                            jinhuofangshi.setContent(appType.getValueName());
//                        }
//                    });
//        }
//    }
//
//    private void initZhiliangChouyang(View view, final ZhiLiangChouYang zhiLiangChouYang) {
//        view.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View view) {
//                EPocketAlertDialog.getInstance().showAlertContent(getSupportFragmentManager(), "是否删除该抽样信息？", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        SamplingApplication.getDaoSession().getZhiLiangChouYangDao().delete(zhiLiangChouYang);
//                        showToast("删除成功！");
//                        initZhiliangChouyang();
//                    }
//                });
//
//                return true;
//            }
//        });
//        PreferenceRightDetailView bianhao = (PreferenceRightDetailView) view.findViewById(R.id.item_yangpinhao);
//        PreferenceRightDetailView mingcheng = (PreferenceRightDetailView) view.findViewById(R.id.item_yangpinmingcheng);
//        PreferenceRightDetailView shangpinming = (PreferenceRightDetailView) view.findViewById(R.id.item_shangpinming);
//        PreferenceRightDetailView pihao = (PreferenceRightDetailView) view.findViewById(R.id.item_yangpinpihao);
//        PreferenceRightDetailView pizhunwenhao = (PreferenceRightDetailView) view.findViewById(R.id.item_pizhunwenhao);
//        PreferenceRightDetailView guige = (PreferenceRightDetailView) view.findViewById(R.id.item_guige);
//        PreferenceRightDetailView chouyangshuliang = (PreferenceRightDetailView) view.findViewById(R.id.item_chouyangshuliang);
//        PreferenceRightDetailView kucun = (PreferenceRightDetailView) view.findViewById(R.id.item_kucun);
//        final PreferenceRightDetailView danwei = (PreferenceRightDetailView) view.findViewById(R.id.item_danwei);
//        PreferenceRightDetailView beizhu = (PreferenceRightDetailView) view.findViewById(R.id.item_beizhu);
//        bianhao.setContent(zhiLiangChouYang.getCode());
//        mingcheng.setContent(zhiLiangChouYang.getSampleName());
//        shangpinming.setContent(zhiLiangChouYang.getName());
//        pihao.setContent(zhiLiangChouYang.getPiHao());
//        pizhunwenhao.setContent(zhiLiangChouYang.getPiZhunWenHao());
//        guige.setContent(zhiLiangChouYang.getSampleGuiGe());
//        chouyangshuliang.setContent(zhiLiangChouYang.getNumber());
//        kucun.setContent(zhiLiangChouYang.getKuCun());
//        beizhu.setContent(zhiLiangChouYang.getDescription());
//        if (AppUtils.isHaveId(zhiLiangChouYang.getSampleSourceID())) {
//            Observable.just(zhiLiangChouYang.getSampleSourceID()).map(new Func1<Long, ClientUnit>() {
//                @Override
//                public ClientUnit call(Long aLong) {
//                    return SamplingApplication.getDaoSession().getClientUnitDao().load(aLong);
//                }
//            }).subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(new CommonSubscriber<ClientUnit>() {
//                        @Override
//                        public void onNext(ClientUnit clientUnit) {
//                            super.onNext(clientUnit);
//                            danwei.setContent(clientUnit.getName());
//                        }
//                    });
//        }
//    }
//
//    private void initShouYaoCanLiuView(View view, final ShouYaoCanLiuSample sample) {
//        view.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View view) {
//                EPocketAlertDialog.getInstance().showAlertContent(getSupportFragmentManager(), "是否删除该样品信息？", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        SamplingApplication.getDaoSession().getShouYaoCanLiuSampleDao().delete(sample);
//                        showToast("删除成功！");
//                        initShouYaoCanLiu();
//                    }
//                });
//
//                return true;
//            }
//        });
//        PreferenceRightDetailView bianhao = (PreferenceRightDetailView) view.findViewById(R.id.item_yangpinhao);
//        PreferenceRightDetailView mingcheng = (PreferenceRightDetailView) view.findViewById(R.id.item_yangpinmingcheng);
//        PreferenceRightDetailView shuliang = (PreferenceRightDetailView) view.findViewById(R.id.item_yangpinshuliang);
//        PreferenceRightDetailView jishu = (PreferenceRightDetailView) view.findViewById(R.id.item_yangpinjishu);
//        PreferenceRightDetailView jianyizhenghao = (PreferenceRightDetailView) view.findViewById(R.id.item_jianyizhenghao);
//        PreferenceRightDetailView pihao = (PreferenceRightDetailView) view.findViewById(R.id.item_pihao);
//        final PreferenceRightDetailView danwei = (PreferenceRightDetailView) view.findViewById(R.id.item_danwei);
//        final PreferenceRightDetailView lianxiren = (PreferenceRightDetailView) view.findViewById(R.id.item_lianxiren);
//        final PreferenceRightDetailView dianhua = (PreferenceRightDetailView) view.findViewById(R.id.item_dianhua);
//        final PreferenceRightDetailView jinhuofangshi = (PreferenceRightDetailView) view.findViewById(R.id.item_jinhuofangshi);
//        PreferenceRightDetailView beizhu = (PreferenceRightDetailView) view.findViewById(R.id.item_beizhu);
//
//        bianhao.setContent(sample.getCode());
//        mingcheng.setContent(sample.getName());
//        shuliang.setContent(sample.getNumber());
//        jishu.setContent(sample.getBase());
//        jianyizhenghao.setContent(sample.getCheckCode());
//        pihao.setContent(sample.getPiHao());
//        beizhu.setContent(sample.getDescription());
//        if (AppUtils.isHaveId(sample.getSampleSourceID())) {
//            Observable.just(sample.getSampleSourceID()).map(new Func1<Long, ClientUnit>() {
//                @Override
//                public ClientUnit call(Long aLong) {
//                    return SamplingApplication.getDaoSession().getClientUnitDao().load(aLong);
//                }
//            }).subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(new CommonSubscriber<ClientUnit>() {
//                        @Override
//                        public void onNext(ClientUnit clientUnit) {
//                            super.onNext(clientUnit);
//                            danwei.setContent(clientUnit.getName());
//                            lianxiren.setContent(clientUnit.getContactUser());
//                            dianhua.setContent(clientUnit.getTelephone());
//                        }
//                    });
//        }
//
//        if (AppUtils.isHaveId(sample.getGouMaiTypeId())) {
//            Observable.just(sample.getGouMaiTypeId()).map(new Func1<Long, AppTypes>() {
//                @Override
//                public AppTypes call(Long aLong) {
//                    return SamplingApplication.getDaoSession().getAppTypesDao().load(aLong);
//                }
//            }).subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(new CommonSubscriber<AppTypes>() {
//                        @Override
//                        public void onNext(AppTypes appType) {
//                            super.onNext(appType);
//                            jinhuofangshi.setContent(appType.getValueName());
//                        }
//                    });
//        }
//
//        view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivityForResult(AddSamplingActivity.getStartIntent(AddFormActivity.this, sample), REQUEST_CODE_ADD_SAMPLE);
//            }
//        });
//    }
//}
