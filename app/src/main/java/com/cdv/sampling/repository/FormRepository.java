package com.cdv.sampling.repository;

import android.text.TextUtils;

import com.cdv.sampling.SamplingApplication;
import com.cdv.sampling.bean.AppFiles;
import com.cdv.sampling.bean.AppTypes;
import com.cdv.sampling.bean.BaseBean;
import com.cdv.sampling.bean.ClientUnit;
import com.cdv.sampling.bean.FormBean;
import com.cdv.sampling.bean.JianCeDan;
import com.cdv.sampling.bean.ShouYaoCanLiuSample;
import com.cdv.sampling.bean.YangPinHeShi;
import com.cdv.sampling.bean.ZhiLiangChouYang;
import com.cdv.sampling.constants.Constants;
import com.cdv.sampling.exception.FormException;
import com.cdv.sampling.exception.RequestIllegalException;
import com.cdv.sampling.net.HttpService;
import com.cdv.sampling.net.OperatorRequestMap;
import com.cdv.sampling.utils.ListUtils;
import com.cdv.sampling.utils.MD5Utils;
import com.cdv.sampling.utils.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Response;
import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

public class FormRepository {

    private SamplingApplication application;

    private FormRepository() {
        application = SamplingApplication.getInstance();
    }

    public static FormRepository getInstance() {
        return FormRepository.FormRepositoryHandler.INSTANCE;
    }

    private static class FormRepositoryHandler {
        public static final FormRepository INSTANCE = new FormRepository();
    }

    public Observable<String> uploadImages(int fileType, String[] fileArr) {
        if (fileArr == null || fileArr.length == 0) {
            return Observable.just("");
        }
        HashMap<String, RequestBody> map = new HashMap<>(fileArr.length);
        for (int i = 0; i < fileArr.length; i++) {
            File file = new File(fileArr[i]);
            FileNameMap fileNameMap = URLConnection.getFileNameMap();
            String contentType = fileNameMap.getContentTypeFor(file.getAbsolutePath());
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
            map.put("" + fileType + "\"; filename=\"" + i + file.getName(), RequestBody.create(MediaType.parse(contentType), file));
        }

        return HttpService.getApi().uploadFiles(map, fileType).lift(new OperatorRequestMap<String>())
                .subscribeOn(Schedulers.io());
    }

    public String uploadImagesSync(int fileType, String[] fileArr) {
        if (fileArr == null || fileArr.length == 0) {
            return "";
        }
        HashMap<String, RequestBody> map = new HashMap<>(fileArr.length);
        for (int i = 0; i < fileArr.length; i++) {
            File file = new File(fileArr[i]);
            FileNameMap fileNameMap = URLConnection.getFileNameMap();
            String contentType = fileNameMap.getContentTypeFor(file.getAbsolutePath());
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
            map.put("" + fileType + "\"; filename=\"" + i + "." + FileUtils.getFileExtension(file.getAbsolutePath()), RequestBody.create(MediaType.parse(contentType), file));
        }
        try {
            Response<BaseBean<String>> response = HttpService.getApi().uploadFilesSync(map, fileType).execute();
            BaseBean<String> responseBody = response.body();
            if (responseBody.getErrCode() != 0) {
                throw new RequestIllegalException(responseBody.getErrMsg());
            }
            return responseBody.getData();
        } catch (IOException e) {
            e.printStackTrace();
            throw new FormException("网络出错！");
        }
    }

    public Observable<String> uploadClientUnit(final ClientUnit clientUnit) {
        if (clientUnit == null) {
            throw new FormException("没有单位信息！");
        }
        List<ClientUnit> list = new ArrayList<>();
        list.add(clientUnit);
        return HttpService.getApi().modifyClientUnitList(list).lift(new OperatorRequestMap<List<ClientUnit>>()).map(new Func1<List<ClientUnit>, String>() {
            @Override
            public String call(List<ClientUnit> clientUnits) {
                if (ListUtils.isEmpty(clientUnits)) {
                    return null;
                }
                SamplingApplication.getDaoSession().getClientUnitDao().saveInTx(clientUnits);
                return clientUnits.get(0).getID();
            }
        });
    }

    public Observable<String> uploadSampleSource(AppTypes types) {
        if (types == null || TextUtils.isEmpty(types.getValueName())) {
            throw new FormException("样品来源数据错误！");
        }
        if (TextUtils.isEmpty(types.getID())) {
            return HttpService.getApi().createSampleSource(types.getValueName()).lift(new OperatorRequestMap<String>());
        }
        return HttpService.getApi().modifySampleSource(types.getID(), types.getValueName()).lift(new OperatorRequestMap<String>());
    }

    public Observable<FormBean<ShouYaoCanLiuSample>> uploadShouYaoJianCe(final JianCeDan jianCeDan) {
        Observable<List<ShouYaoCanLiuSample>> observable = uploadClientUnit(jianCeDan.getClientUnit()).map(new Func1<String, Long>() {
            @Override
            public Long call(String remoteClientId) {
                jianCeDan.getClientUnit().setID(remoteClientId);
                return jianCeDan.getID();
            }
        }).map(new Func1<Long, List<ShouYaoCanLiuSample>>() {
            @Override
            public List<ShouYaoCanLiuSample> call(Long jianCeDanId) {
                return SamplingApplication.getDaoSession().getShouYaoCanLiuSampleDao().queryDeep("where Form_ID=?", String.valueOf(jianCeDanId));
            }
        }).map(new Func1<List<ShouYaoCanLiuSample>, List<ShouYaoCanLiuSample>>() {
            @Override
            public List<ShouYaoCanLiuSample> call(List<ShouYaoCanLiuSample> shouYaoCanLiuSamples) {
                if (ListUtils.isEmpty(shouYaoCanLiuSamples)) {
                    throw new FormException("没有样品信息！");
                }
                for (ShouYaoCanLiuSample sample : shouYaoCanLiuSamples) {
                    sample.setGouMaiType(sample.getGouMaiLeiXing().getValueName());
//                    if (!TextUtils.isEmpty(sample.getLocalFileIds())) {
//                        Object[] idArr = sample.getLocalFileIds().split(Constants.FILE_ID_SEPARATOR);
//                        List<AppFiles> filesList = SamplingApplication.getDaoSession().getAppFilesDao().queryBuilder().where(AppFilesDao.Properties.ID.in(idArr)).build().list();
//                        String[] fileArr = new String[filesList.size()];
//                        for (int i = 0; i < filesList.size(); i++) {
//                            fileArr[i] = filesList.get(i).getFilePath();
//                        }
//                        String remoteIds = uploadImagesSync(0, fileArr);
//                        if (TextUtils.isEmpty(sample.getSampleFileIDs())) {
//                            sample.setSampleFileIDs(remoteIds);
//                        } else {
//                            sample.setSampleFileIDs(sample.getSampleFileIDs() + Constants.FILE_ID_SEPARATOR + remoteIds);
//                        }
//                        SamplingApplication.getDaoSession().getShouYaoCanLiuSampleDao().save(sample);
//                        if (AppUtils.isHaveId(sample.getTanWeiLocalId()) && TextUtils.isEmpty(sample.getTanWeiUserFile())) {
//                            AppFiles files = SamplingApplication.getDaoSession().getAppFilesDao().load(sample.getTanWeiLocalId());
//                            File file = new File(files.getFilePath());
//                            if (!file.exists()) {
//                                throw new FormException("摊主签名缺失！");
//                            }
//                            sample.setTanWeiUserFile(uploadImagesSync(3, new String[]{files.getFilePath()}));
//                        }
//                    }

                }
                return shouYaoCanLiuSamples;
            }
        }).subscribeOn(Schedulers.io());

        return Observable.combineLatest(observable, uploadArchives(jianCeDan), new Func2<List<ShouYaoCanLiuSample>, JianCeDan, FormBean<ShouYaoCanLiuSample>>() {
            @Override
            public FormBean<ShouYaoCanLiuSample> call(List<ShouYaoCanLiuSample> shouYaoCanLiuSamples, JianCeDan jianCeDan) {
                FormBean<ShouYaoCanLiuSample> formBean = new FormBean<ShouYaoCanLiuSample>();
                formBean.setClientID(jianCeDan.getClientUnit().getID());
                formBean.setClientUserFileID(jianCeDan.getClientUserFileID());
                formBean.setJianCeDanFileID(jianCeDan.getJianCeDanFileID());
                formBean.setItems(shouYaoCanLiuSamples);
                formBean.setTestUser(jianCeDan.getTestUser());
                formBean.setSampleType(jianCeDan.getSampleType());
                formBean.setClientUser(jianCeDan.getClientUser());
                formBean.setTestUserFileID(jianCeDan.getTestUserFileID());
                formBean.setCreateTime(jianCeDan.getShowCreateTime());
                return formBean;
            }
        }).flatMap(new Func1<FormBean<ShouYaoCanLiuSample>, Observable<FormBean<ShouYaoCanLiuSample>>>() {
            @Override
            public Observable<FormBean<ShouYaoCanLiuSample>> call(FormBean<ShouYaoCanLiuSample> formBean) {
                return HttpService.getApi().uploadShouYaoForm(formBean).lift(new OperatorRequestMap<FormBean<ShouYaoCanLiuSample>>()).map(new Func1<FormBean<ShouYaoCanLiuSample>, FormBean<ShouYaoCanLiuSample>>() {
                    @Override
                    public FormBean<ShouYaoCanLiuSample> call(FormBean<ShouYaoCanLiuSample> formBean) {
                        jianCeDan.setStatus(JianCeDan.STATUS_ARCHIVED);
                        jianCeDan.setFormImageUrl(formBean.getJianCeDanUrl());
                        jianCeDan.setFormRemoteId(formBean.getJianCeDanID());
                        SamplingApplication.getDaoSession().getJianCeDanDao().save(jianCeDan);
                        return formBean;
                    }
                });
            }
        });
    }

    public Observable<JianCeDan> uploadForm(JianCeDan jianCeDan) {
        return Observable.combineLatest(uploadClientUnit(jianCeDan.getClientUnit()), uploadArchives(jianCeDan), new Func2<String, JianCeDan, JianCeDan>() {
            @Override
            public JianCeDan call(String clientRemoteId, JianCeDan jianCeDan) {
                jianCeDan.getClientUnit().setID(clientRemoteId);
                SamplingApplication.getDaoSession().getClientUnitDao().save(jianCeDan.getClientUnit());
                return jianCeDan;
            }
        });
    }

    public Observable<JianCeDan> uploadArchives(final JianCeDan jianCeDan) {
        String[] idArr = jianCeDan.getFileIDs().split(Constants.FILE_ID_SEPARATOR);
        return Observable.just(idArr).flatMap(new Func1<String[], Observable<JianCeDan>>() {
            @Override
            public Observable<JianCeDan> call(String[] idArr) {
                List<String> clientFileList = new ArrayList<String>();
                List<String> testFileList = new ArrayList<String>();
                List<String> formFileList = new ArrayList<String>();
                for (String idString : idArr) {
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
                    switch (appFile.getFileType()) {
                        case Constants.TYPE_BEIJIANREN_QIANZI:
                        case Constants.TYPE_BEIJIANREN_QIANZI_2:
                            clientFileList.add(file.getAbsolutePath());
                            break;
                        case Constants.TYPE_CAIYANGREN_QIANZI:
                        case Constants.TYPE_CAIYANGREN_QIANZI_2:
                            testFileList.add(file.getAbsolutePath());
                            break;
                        case Constants.TYPE_FORM_IMAGE:
                            formFileList.add(file.getAbsolutePath());
                            break;
                    }
                }
                Observable<String> clientFileObservable;
                Observable<String> testFileObservable = null;
                Observable<String> formFileObservable = null;
//                if (TextUtils.isEmpty(jianCeDan.getClientUserFileID())) {
//                    String[] arr = new String[clientFileList.size()];
//                    clientFileObservable = FormRepository.getInstance().uploadImages(1, clientFileList.toArray(arr));
//                } else {
//                    clientFileObservable = Observable.just(jianCeDan.getClientUserFileID());
//                }
//                if (TextUtils.isEmpty(jianCeDan.getTestUserFileID())) {
//                    String[] arr = new String[testFileList.size()];
//                    testFileObservable = FormRepository.getInstance().uploadImages(2, testFileList.toArray(arr));
//                } else {
//                    testFileObservable = Observable.just(jianCeDan.getTestUserFileID());
//                }
                if (TextUtils.isEmpty(jianCeDan.getJianCeDanFileID())) {
                    if (ListUtils.isEmpty(formFileList)) {
                        throw new FormException("请先生成表单图片！");
                    }
                    String[] arr = new String[formFileList.size()];
                    formFileObservable = FormRepository.getInstance().uploadImages(10, formFileList.toArray(arr));
                } else {
                    formFileObservable = Observable.just(jianCeDan.getJianCeDanFileID());
                }
//                return Observable.combineLatest(clientFileObservable, testFileObservable, formFileObservable, new Func3<String, String, String, JianCeDan>() {
//                    @Override
//                    public JianCeDan call(String clientFileIds, String testFileIds, String formFileIds) {
//                        jianCeDan.setClientUserFileID(clientFileIds);
//                        jianCeDan.setTestUserFileID(testFileIds);
//                        jianCeDan.setJianCeDanFileID(formFileIds);
//                        SamplingApplication.getDaoSession().getJianCeDanDao().save(jianCeDan);
//                        return jianCeDan;
//                    }
//                });

                return formFileObservable.subscribeOn(Schedulers.io()).flatMap(new Func1<String, Observable<JianCeDan>>() {
                    @Override
                    public Observable<JianCeDan> call(String formFileIds) {
                        jianCeDan.setJianCeDanFileID(formFileIds);
                        SamplingApplication.getDaoSession().getJianCeDanDao().save(jianCeDan);
                        return Observable.just(jianCeDan);
                    }
                });
            }
        });
    }

    public Observable<FormBean<ZhiLiangChouYang>> uploadZhiLiangChouYang(final JianCeDan jianCeDan) {
        Observable<List<ZhiLiangChouYang>> observable = uploadClientUnit(jianCeDan.getClientUnit()).map(new Func1<String, Long>() {
            @Override
            public Long call(String remoteClientId) {
                jianCeDan.getClientUnit().setID(remoteClientId);
                return jianCeDan.getID();
            }
        }).map(new Func1<Long, List<ZhiLiangChouYang>>() {
            @Override
            public List<ZhiLiangChouYang> call(Long jianCeDanId) {
                return SamplingApplication.getDaoSession().getZhiLiangChouYangDao().queryDeep("where Form_ID=?", String.valueOf(jianCeDanId));
            }
        }).map(new Func1<List<ZhiLiangChouYang>, List<ZhiLiangChouYang>>() {
            @Override
            public List<ZhiLiangChouYang> call(List<ZhiLiangChouYang> zhiLiangChouYangs) {
                if (ListUtils.isEmpty(zhiLiangChouYangs)) {
                    throw new FormException("没有样品信息！");
                }
//                for (ZhiLiangChouYang chouYang : zhiLiangChouYangs) {
//                    if (!TextUtils.isEmpty(chouYang.getLocalFileIds())) {
//                        Object[] idArr = chouYang.getLocalFileIds().split(Constants.FILE_ID_SEPARATOR);
//                        List<AppFiles> filesList = SamplingApplication.getDaoSession().getAppFilesDao().queryBuilder().where(AppFilesDao.Properties.ID.in(idArr)).build().list();
//                        String[] fileArr = new String[filesList.size()];
//                        for (int i = 0; i < filesList.size(); i++) {
//                            fileArr[i] = filesList.get(i).getFilePath();
//                        }
//                        String remoteIds = uploadImagesSync(0, fileArr);
//                        if (TextUtils.isEmpty(chouYang.getSampleFileIDs())) {
//                            chouYang.setSampleFileIDs(remoteIds);
//                        } else {
//                            chouYang.setSampleFileIDs(chouYang.getSampleFileIDs() + Constants.FILE_ID_SEPARATOR + remoteIds);
//                        }
//                        chouYang.setLocalFileIds(null);
//                        SamplingApplication.getDaoSession().getZhiLiangChouYangDao().save(chouYang);
//                    }
//                }
                return zhiLiangChouYangs;
            }
        }).subscribeOn(Schedulers.io());
        return Observable.combineLatest(observable, uploadArchives(jianCeDan), new Func2<List<ZhiLiangChouYang>, JianCeDan, FormBean<ZhiLiangChouYang>>() {
            @Override
            public FormBean<ZhiLiangChouYang> call(List<ZhiLiangChouYang> zhiLiangChouYangs, JianCeDan jianCeDan) {
                FormBean<ZhiLiangChouYang> formBean = new FormBean<>();
                formBean.setClientID(jianCeDan.getClientUnit().getID());
                formBean.setClientUser(jianCeDan.getClientUser());
                formBean.setTestUser(jianCeDan.getTestUser());
                formBean.setClientUserFileID(jianCeDan.getClientUserFileID());
                formBean.setJianCeDanFileID(jianCeDan.getJianCeDanFileID());
                formBean.setItems(zhiLiangChouYangs);
                formBean.setSampleType(jianCeDan.getSampleType());
                formBean.setTestUserFileID(jianCeDan.getTestUserFileID());
                formBean.setCreateTime(jianCeDan.getShowCreateTime());
                return formBean;
            }
        }).flatMap(new Func1<FormBean<ZhiLiangChouYang>, Observable<FormBean<ZhiLiangChouYang>>>() {
            @Override
            public Observable<FormBean<ZhiLiangChouYang>> call(FormBean<ZhiLiangChouYang> formBean) {
                return HttpService.getApi().uploadZhiLiangChouYangForm(formBean).lift(new OperatorRequestMap<FormBean<ZhiLiangChouYang>>()).map(new Func1<FormBean<ZhiLiangChouYang>, FormBean<ZhiLiangChouYang>>() {
                    @Override
                    public FormBean<ZhiLiangChouYang> call(FormBean<ZhiLiangChouYang> zhiLiangChouYangFormBean) {
                        jianCeDan.setStatus(JianCeDan.STATUS_ARCHIVED);
                        jianCeDan.setFormImageUrl(zhiLiangChouYangFormBean.getJianCeDanUrl());
                        jianCeDan.setFormRemoteId(zhiLiangChouYangFormBean.getJianCeDanID());
                        SamplingApplication.getDaoSession().getJianCeDanDao().save(jianCeDan);
                        return zhiLiangChouYangFormBean;
                    }
                });
            }
        });
    }

    public Observable<FormBean<YangPinHeShi>> uploadYangPinHeShi(final JianCeDan jianCeDan) {
        Observable<List<YangPinHeShi>> observable = uploadClientUnit(jianCeDan.getClientUnit()).map(new Func1<String, Long>() {
            @Override
            public Long call(String remoteClientId) {
                jianCeDan.getClientUnit().setID(remoteClientId);
                return jianCeDan.getID();
            }
        }).map(new Func1<Long, List<YangPinHeShi>>() {
            @Override
            public List<YangPinHeShi> call(Long jianCeDanId) {
                return SamplingApplication.getDaoSession().getYangPinHeShiDao().queryDeep("where Form_ID=?", String.valueOf(jianCeDanId));
            }
        }).map(new Func1<List<YangPinHeShi>, List<YangPinHeShi>>() {
            @Override
            public List<YangPinHeShi> call(List<YangPinHeShi> yangPinHeShiList) {
                if (ListUtils.isEmpty(yangPinHeShiList)) {
                    throw new FormException("没有样品信息！");
                }
                for (YangPinHeShi yangPinHeShi : yangPinHeShiList) {
                    yangPinHeShi.setGouMaiType(yangPinHeShi.getGouMaiLeiXing().getValueName());
//                    if (!TextUtils.isEmpty(yangPinHeShi.getLocalFileIds())) {
//                        Object[] idArr = yangPinHeShi.getLocalFileIds().split(Constants.FILE_ID_SEPARATOR);
//                        List<AppFiles> filesList = SamplingApplication.getDaoSession().getAppFilesDao().queryBuilder().where(AppFilesDao.Properties.ID.in(idArr)).build().list();
//                        String[] fileArr = new String[filesList.size()];
//                        for (int i = 0; i < filesList.size(); i++) {
//                            fileArr[i] = filesList.get(i).getFilePath();
//                        }
//                        String remoteIds = uploadImagesSync(0, fileArr);
//                        if (TextUtils.isEmpty(yangPinHeShi.getSampleFileIDs())) {
//                            yangPinHeShi.setSampleFileIDs(remoteIds);
//                        } else {
//                            yangPinHeShi.setSampleFileIDs(yangPinHeShi.getSampleFileIDs() + Constants.FILE_ID_SEPARATOR + remoteIds);
//                        }
//                        yangPinHeShi.setLocalFileIds(null);
//                        SamplingApplication.getDaoSession().getYangPinHeShiDao().save(yangPinHeShi);
//                    }
                }
                return yangPinHeShiList;
            }
        }).subscribeOn(Schedulers.io());
        return Observable.combineLatest(observable, uploadArchives(jianCeDan), new Func2<List<YangPinHeShi>, JianCeDan, FormBean<YangPinHeShi>>() {
            @Override
            public FormBean<YangPinHeShi> call(List<YangPinHeShi> yangPinHeShiList, JianCeDan jianCeDan) {
                FormBean<YangPinHeShi> formBean = new FormBean<>();
                formBean.setClientID(jianCeDan.getClientUnit().getID());
                formBean.setClientUserFileID(jianCeDan.getClientUserFileID());
                formBean.setClientUser(jianCeDan.getClientUser());
                formBean.setTestUser(jianCeDan.getTestUser());
                formBean.setSampleType(jianCeDan.getSampleType());
                formBean.setJianCeDanFileID(jianCeDan.getJianCeDanFileID());
                formBean.setItems(yangPinHeShiList);
                formBean.setTestUserFileID(jianCeDan.getTestUserFileID());
                formBean.setCreateTime(jianCeDan.getShowCreateTime());
                return formBean;
            }
        }).flatMap(new Func1<FormBean<YangPinHeShi>, Observable<FormBean<YangPinHeShi>>>() {
            @Override
            public Observable<FormBean<YangPinHeShi>> call(FormBean<YangPinHeShi> formBean) {
                return HttpService.getApi().uploadYangPinHeShi(formBean).lift(new OperatorRequestMap<FormBean<YangPinHeShi>>()).map(new Func1<FormBean<YangPinHeShi>, FormBean<YangPinHeShi>>() {
                    @Override
                    public FormBean<YangPinHeShi> call(FormBean<YangPinHeShi> yangPinHeShiFormBean) {
                        jianCeDan.setStatus(JianCeDan.STATUS_ARCHIVED);
                        jianCeDan.setFormRemoteId(yangPinHeShiFormBean.getJianCeDanID());
                        jianCeDan.setFormImageUrl(yangPinHeShiFormBean.getJianCeDanUrl());
                        SamplingApplication.getDaoSession().getJianCeDanDao().save(jianCeDan);
                        return yangPinHeShiFormBean;
                    }
                });
            }
        });
    }
}
