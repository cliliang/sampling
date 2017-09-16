package com.cdv.sampling.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.cdv.sampling.R;
import com.cdv.sampling.SamplingApplication;
import com.cdv.sampling.bean.ClientUnit;
import com.cdv.sampling.bean.FormBean;
import com.cdv.sampling.bean.JianCeDan;
import com.cdv.sampling.bean.JianCeDanDao;
import com.cdv.sampling.bean.ShouYaoCanLiuSample;
import com.cdv.sampling.bean.YangPinHeShi;
import com.cdv.sampling.bean.ZhiLiangChouYang;
import com.cdv.sampling.constants.FileConstants;
import com.cdv.sampling.constants.StorageConstants;
import com.cdv.sampling.exception.ErrorMessageFactory;
import com.cdv.sampling.repository.FormRepository;
import com.cdv.sampling.repository.SyncDataRepository;
import com.cdv.sampling.rxandroid.CommonSubscriber;
import com.cdv.sampling.storage.SamplingUserStorage;
import com.cdv.sampling.utils.ListUtils;
import com.cdv.sampling.utils.TimeUtils;
import com.cdv.sampling.utils.io.FileUtils;
import com.cdv.sampling.widget.DeleteDataDialog;

import org.greenrobot.greendao.AbstractDao;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class DataManagerActivity extends BaseActivity {


    @BindView(R.id.tv_last_sync_data)
    TextView tvLastSyncData;
    @BindView(R.id.tv_un_archive_count)
    TextView tvUnArchiveCount;
    @BindView(R.id.tv_clear_data)
    TextView tvClearData;

    public static Intent getStartIntent(Context context) {
        return new Intent(context, DataManagerActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_manager);
        initViews();
        findViewById(R.id.btn_setting_ip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DataManagerActivity.this, IPConfigActivity.class));
            }
        });
        setMyTitle("数据管理");
    }

    private void initViews() {
        File file = new File(FileConstants.ROOT_PATH);
        Observable.just(file).subscribeOn(Schedulers.io())
                .map(new Func1<File, Long>() {
                    @Override
                    public Long call(File file) {
                        if (file.exists()) {
                            return FileUtils.getFileSize(file);
                        }
                        return 0L;
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommonSubscriber<Long>() {
                    @Override
                    public void onNext(Long bytesCount) {
                        super.onNext(bytesCount);
                        long lastSyncTime = SamplingUserStorage.getInstance().getLongValue(StorageConstants.KEY_LAST_SYNC_TIME, -1);
                        if (lastSyncTime > 0) {
                            tvLastSyncData.setText(TimeUtils.getTime(lastSyncTime));
                        } else {
                            tvLastSyncData.setText("暂未同步");
                        }
                        tvClearData.setText(bytesCount / 1024 / 1024 + "MB");
                    }
                });

        Observable.just(String.valueOf(JianCeDan.STATUS_ARCHIVED)).map(new Func1<String, Integer>() {
            @Override
            public Integer call(String isFinished) {
                return SamplingApplication.getDaoSession().getJianCeDanDao().queryRaw("WHERE status != ?", isFinished).size();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommonSubscriber<Integer>() {

                    @Override
                    public void onNext(Integer count) {
                        super.onNext(count);

                        tvUnArchiveCount.setText(count + "个未上传");
                    }
                });
    }

    @OnClick({R.id.btn_download, R.id.btn_upload, R.id.btn_clear, R.id.btn_setting})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_download:
                startSyncData();
                break;
            case R.id.btn_upload:
                uploadAllForm();
                break;
            case R.id.btn_clear:
                DeleteDataDialog.getInstance().showDeleteDialog(getSupportFragmentManager(), new DeleteDataDialog.OnDeleteListener() {
                    @Override
                    public void deleteDataByType(int type) {
                        clearAllData(type);
                    }
                });
                break;
            case R.id.btn_setting:
                Intent operation = new Intent();
                operation.setClass(this, PrintSettingsActivity.class);
                startActivity(operation);
                break;
        }
    }

    private void uploadAllForm() {
        uploadShouYaoForm();
    }

    private void uploadShouYaoForm() {
        showProgressDialog("开始上传残留抽样表单..");
        Observable.just(JianCeDan.STATUS_PRINTED).flatMap(new Func1<Integer, Observable<JianCeDan>>() {
            @Override
            public Observable<JianCeDan> call(Integer integer) {
                return Observable.from(SamplingApplication.getDaoSession().getJianCeDanDao().queryRaw("WHERE status = ? and EDAN_TYPE = ?", String.valueOf(integer), JianCeDan.DAN_TYPE_SAMPLING_DRUG));
            }
        }).flatMap(new Func1<JianCeDan, Observable<FormBean<ShouYaoCanLiuSample>>>() {
            @Override
            public Observable<FormBean<ShouYaoCanLiuSample>> call(JianCeDan jianCeDan) {
                return FormRepository.getInstance().uploadShouYaoJianCe(jianCeDan);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommonSubscriber<FormBean<ShouYaoCanLiuSample>>() {

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        dismissProgressDialog();
                        showToast("上传残留抽样表单过程中出错！" + ErrorMessageFactory.create(e));
                        initViews();
                    }

                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                        showToast("上传残留抽样表单成功！");
                        uploadZhiLiangChouYangForm();
                    }
                });
    }

    private void uploadZhiLiangChouYangForm() {
        showProgressDialog("开始上传兽药抽样表单..");
        Observable.just(JianCeDan.STATUS_PRINTED).flatMap(new Func1<Integer, Observable<JianCeDan>>() {
            @Override
            public Observable<JianCeDan> call(Integer integer) {
                return Observable.from(SamplingApplication.getDaoSession().getJianCeDanDao().queryRaw("WHERE status = ? and EDAN_TYPE = ?", String.valueOf(integer), JianCeDan.DAN_TYPE_SAMPLING_QUALITY));
            }
        }).flatMap(new Func1<JianCeDan, Observable<FormBean<ZhiLiangChouYang>>>() {
            @Override
            public Observable<FormBean<ZhiLiangChouYang>> call(JianCeDan jianCeDan) {
                return FormRepository.getInstance().uploadZhiLiangChouYang(jianCeDan);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommonSubscriber<FormBean<ZhiLiangChouYang>>() {

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        dismissProgressDialog();
                        initViews();
                        showToast("上传兽药抽样表单过程中出错！" + ErrorMessageFactory.create(e));
                    }

                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                        showToast("上传兽药抽样表单成功！");
                        uploadYangPinHeShiForm();
                    }
                });
    }


    private void uploadYangPinHeShiForm() {
        showProgressDialog("开始上传样品核实表单..");
        Observable.just(JianCeDan.STATUS_PRINTED).flatMap(new Func1<Integer, Observable<JianCeDan>>() {
            @Override
            public Observable<JianCeDan> call(Integer integer) {
                return Observable.from(SamplingApplication.getDaoSession().getJianCeDanDao().queryRaw("WHERE status = ? and EDAN_TYPE = ?", String.valueOf(integer), JianCeDan.DAN_TYPE_SAMPLING_QUALITY));
            }
        }).flatMap(new Func1<JianCeDan, Observable<FormBean<YangPinHeShi>>>() {
            @Override
            public Observable<FormBean<YangPinHeShi>> call(JianCeDan jianCeDan) {
                return FormRepository.getInstance().uploadYangPinHeShi(jianCeDan);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommonSubscriber<FormBean<YangPinHeShi>>() {

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        dismissProgressDialog();
                        initViews();
                        showToast("上传样品核实表单过程中出错！" + ErrorMessageFactory.create(e));
                    }

                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                        dismissProgressDialog();
                        initViews();
                        showToast("上传所有表单成功！");
                    }
                });
    }


    private void clearAllData(final int type) {

        Observable.just(FileConstants.ROOT_PATH).map(new Func1<String, Boolean>() {
            @Override
            public Boolean call(String s) {
                if (type == DeleteDataDialog.TYPE_ALL_DATA || type == DeleteDataDialog.TYPE_ALL_FORM){
                    FileUtils.deleteFile(s);
                }
                return true;
            }
        }).flatMap(new Func1<Boolean, Observable<Boolean>>() {
            @Override
            public Observable<Boolean> call(Boolean aBoolean) {
                if (type == DeleteDataDialog.TYPE_ALL_DATA){
                    return Observable.from(SamplingApplication.getDaoSession().getAllDaos()).map(new Func1<AbstractDao<?, ?>, Boolean>() {
                        @Override
                        public Boolean call(AbstractDao<?, ?> abstractDao) {
                            abstractDao.deleteAll();
                            return true;
                        }
                    });
                }else if (type == DeleteDataDialog.TYPE_ARCHIVED_FORM){
                    List<JianCeDan> jianCeDanList = SamplingApplication.getDaoSession().getJianCeDanDao().queryBuilder().where(JianCeDanDao.Properties.Status.eq(JianCeDan.STATUS_ARCHIVED)).list();
                    SamplingApplication.getDaoSession().getJianCeDanDao().deleteInTx(jianCeDanList);
                } else if (type == DeleteDataDialog.TYPE_NOT_FINISHED_FORM){
                    List<JianCeDan> jianCeDanList = SamplingApplication.getDaoSession().getJianCeDanDao().queryBuilder().where(JianCeDanDao.Properties.Status.eq(JianCeDan.STATUS_INIT)).list();
                    SamplingApplication.getDaoSession().getJianCeDanDao().deleteInTx(jianCeDanList);
                } else if (type == DeleteDataDialog.TYPE_ALL_FORM){
                    SamplingApplication.getDaoSession().getJianCeDanDao().deleteAll();
                }
                return Observable.just(true);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommonSubscriber<Boolean>() {

                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                        initViews();
                        showToast("删除成功！");
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        initViews();
                        showToast("删除过程中出错！");
                    }
                });
    }

    private void startSyncData() {
        showProgressDialog("正在同步..");
        SyncDataRepository.getInstance().syncAllData().observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommonSubscriber<List<ClientUnit>>() {

                    @Override
                    public void onNext(List<ClientUnit> errList) {
                        super.onNext(errList);
                        dismissProgressDialog();
                        SamplingUserStorage.getInstance().storeLongValue(StorageConstants.KEY_LAST_SYNC_TIME, System.currentTimeMillis());
                        if (ListUtils.isEmpty(errList)) {
                            showToast("同步数据成功！");
                            initViews();
                            return;
                        }
                        showToast(errList.size() + "条单位信息同步失败！");
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        dismissProgressDialog();
                        showToast(ErrorMessageFactory.create(e));
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
