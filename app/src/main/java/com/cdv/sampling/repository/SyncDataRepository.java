package com.cdv.sampling.repository;

import android.app.Application;
import android.text.TextUtils;

import com.cdv.sampling.SamplingApplication;
import com.cdv.sampling.bean.AppTypes;
import com.cdv.sampling.bean.ClientUnit;
import com.cdv.sampling.bean.ClientUnitDao;
import com.cdv.sampling.bean.NameList;
import com.cdv.sampling.exception.FormException;
import com.cdv.sampling.net.HttpService;
import com.cdv.sampling.net.OperatorRequestMap;
import com.cdv.sampling.utils.AppUtils;
import com.cdv.sampling.utils.ListUtils;
import com.cdv.sampling.utils.TimeUtils;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func6;
import rx.schedulers.Schedulers;

public class SyncDataRepository {

    private Application application;
    private SyncDataRepository() {
        application = SamplingApplication.getInstance();
    }

    public static SyncDataRepository getInstance() {
        return SyncDataRepository.SyncDataRepositoryHandler.INSTANCE;
    }

    private static class SyncDataRepositoryHandler {
        public static final SyncDataRepository INSTANCE = new SyncDataRepository();
    }

    private Observable<Boolean> syncAllShouYaoJinHuoType(){
        return HttpService.getApi().searchShouYaoJinHuoType().lift(new OperatorRequestMap<List<NameList>>())
                .map(new Func1<List<NameList>, Boolean>() {
                    @Override
                    public Boolean call(List<NameList> nameLists) {
                        if (ListUtils.isEmpty(nameLists)){
                            return true;
                        }
                        List<AppTypes> syncList = new ArrayList<>();
                        for (NameList type : nameLists){
                            AppTypes types = new AppTypes();
                            types.setValueType(AppTypes.TYPE_SHOUYAO_JINHUO_FANGSHI);
                            types.setValueName(type.getName());
                            List<AppTypes> list = SamplingApplication.getDaoSession().getAppTypesDao().queryRaw("where Value_Type = ? and " +
                                    "Value_Name = ?", types.getValueType(), types.getValueName());
                            if (ListUtils.isEmpty(list)){
                                syncList.add(types);
                            }
                        }
                        SamplingApplication.getDaoSession().getAppTypesDao().saveInTx(syncList);
                        return true;
                    }
                }).subscribeOn(Schedulers.io());
    }

    private Observable<Boolean> syncAllZhiliangJinHuoType(){
        return HttpService.getApi().searchZhiLiangJinHuoType().lift(new OperatorRequestMap<List<NameList>>())
                .map(new Func1<List<NameList>, Boolean>() {
                    @Override
                    public Boolean call(List<NameList> nameLists) {
                        if (ListUtils.isEmpty(nameLists)){
                            return true;
                        }
                        List<AppTypes> syncList = new ArrayList<>();
                        for (NameList type : nameLists){
                            AppTypes types = new AppTypes();
                            types.setValueType(AppTypes.TYPE_ZHILIANG_JINHUO_FANGSHI);
                            types.setValueName(type.getName());
                            List<AppTypes> list = SamplingApplication.getDaoSession().getAppTypesDao().queryRaw("where Value_Type = ? and " +
                                    "Value_Name = ?", types.getValueType(), types.getValueName());
                            if (ListUtils.isEmpty(list)){
                                syncList.add(types);
                            }
                        }
                        SamplingApplication.getDaoSession().getAppTypesDao().saveInTx(syncList);
                        return true;
                    }
                }).subscribeOn(Schedulers.io());
    }

    private Observable<Boolean> syncAllZhiliangSampleType(){
        return HttpService.getApi().searchZhiLiangSampleType().lift(new OperatorRequestMap<List<NameList>>())
                .map(new Func1<List<NameList>, Boolean>() {
                    @Override
                    public Boolean call(List<NameList> nameLists) {
                        if (ListUtils.isEmpty(nameLists)){
                            return true;
                        }
                        List<AppTypes> syncList = new ArrayList<>();
                        for (NameList type : nameLists){
                            AppTypes types = new AppTypes();
                            types.setValueType(AppTypes.TYPE_ZHILIANG_SAMPLE_TYPE);
                            types.setValueName(type.getName());
                            List<AppTypes> list = SamplingApplication.getDaoSession().getAppTypesDao().queryRaw("where Value_Type = ? and " +
                                    "Value_Name = ?", types.getValueType(), types.getValueName());
                            if (ListUtils.isEmpty(list)){
                                syncList.add(types);
                            }
                        }
                        SamplingApplication.getDaoSession().getAppTypesDao().saveInTx(syncList);
                        return true;
                    }
                }).subscribeOn(Schedulers.io());
    }

    private Observable<Boolean> syncAllShouYaoSampleType(){
        return HttpService.getApi().searchShouYaoSampleType().lift(new OperatorRequestMap<List<NameList>>())
                .map(new Func1<List<NameList>, Boolean>() {
                    @Override
                    public Boolean call(List<NameList> nameLists) {
                        if (ListUtils.isEmpty(nameLists)){
                            return true;
                        }
                        List<AppTypes> syncList = new ArrayList<>();
                        for (NameList type : nameLists){
                            AppTypes types = new AppTypes();
                            types.setValueType(AppTypes.TYPE_SHOUYAO_SAMPLE_TYPE);
                            types.setValueName(type.getName());
                            List<AppTypes> list = SamplingApplication.getDaoSession().getAppTypesDao().queryRaw("where Value_Type = ? and " +
                                    "Value_Name = ?", types.getValueType(), types.getValueName());
                            if (ListUtils.isEmpty(list)){
                                syncList.add(types);
                            }
                        }
                        SamplingApplication.getDaoSession().getAppTypesDao().saveInTx(syncList);
                        return true;
                    }
                }).subscribeOn(Schedulers.io());
    }

    private Observable<Boolean> syncAllSampleName(){
        return HttpService.getApi().searchSampleName().lift(new OperatorRequestMap<List<NameList>>())
                .map(new Func1<List<NameList>, Boolean>() {
                    @Override
                    public Boolean call(List<NameList> nameLists) {
                        if (ListUtils.isEmpty(nameLists)){
                            return true;
                        }
                        List<AppTypes> syncList = new ArrayList<>();
                        for (NameList type : nameLists){
                            AppTypes types = new AppTypes();
                            types.setValueType(AppTypes.TYPE_YANGPIN_MING);
                            types.setValueName(type.getName());
                            List<AppTypes> list = SamplingApplication.getDaoSession().getAppTypesDao().queryRaw("where Value_Type = ? and " +
                                    "Value_Name = ?", types.getValueType(), types.getValueName());
                            if (ListUtils.isEmpty(list)){
                                syncList.add(types);
                            }
                        }
                        SamplingApplication.getDaoSession().getAppTypesDao().saveInTx(syncList);
                        return true;
                    }
                }).subscribeOn(Schedulers.io());
    }

    private Observable<List<ClientUnit>> syncClientUnit(){
        return updateRemoteClientUnit().map(new Func1<Boolean, List<ClientUnit>>() {
            @Override
            public List<ClientUnit> call(Boolean aBoolean) {
                return SamplingApplication.getDaoSession().getClientUnitDao().queryRaw("WHERE Is_Invalidate = ?", "0");
            }
        }).flatMap(new Func1<List<ClientUnit>, Observable<List<ClientUnit>>>() {
            @Override
            public Observable<List<ClientUnit>> call(List<ClientUnit> clientUnits) {
                if (ListUtils.isEmpty(clientUnits)){
                    return Observable.just(clientUnits);
                }
                return HttpService.getApi().modifyClientUnitList(clientUnits).lift(new OperatorRequestMap<List<ClientUnit>>());
            }
        }).map(new Func1<List<ClientUnit>, List<ClientUnit>>() {
            @Override
            public List<ClientUnit> call(List<ClientUnit> clientUnits) {
                List<ClientUnit> list = new ArrayList<>();
                List<ClientUnit> errList = new ArrayList<>();
                for (ClientUnit clientUnit : clientUnits){
                    if (TextUtils.isEmpty(clientUnit.getErrInfo())){
                        clientUnit.setIsInvalidate(1);
                        list.add(clientUnit);
                    }else{
                        errList.add(clientUnit);
                    }
                    if (!AppUtils.isHaveId(clientUnit.getLocalID())){
                        clientUnit.setLocalID(null);
                    }
                }
                SamplingApplication.getDaoSession().getClientUnitDao().saveInTx(list);
                return errList;
            }
        });
    }

    public Observable<Boolean> updateRemoteClientUnit(){
        List<ClientUnit> maxItem = SamplingApplication.getDaoSession().getClientUnitDao().queryBuilder().orderDesc(ClientUnitDao.Properties.CreateTime).where(new WhereCondition.StringCondition("LAST_MODIFY_TIME = (select max(LAST_MODIFY_TIME) from CLIENT_UNIT) AND Is_Invalidate = 1")).build().list();
        String lastModifyTime = "2000-01-01 00:00:00";
        if (!ListUtils.isEmpty(maxItem)){
            lastModifyTime = TimeUtils.getNormalTimeFormat(maxItem.get(0).getLastModifyTime());
        }
        return HttpService.getApi().getUpdateClientUnitList(lastModifyTime).lift(new OperatorRequestMap<List<ClientUnit>>())
                .flatMap(new Func1<List<ClientUnit>, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(List<ClientUnit> clientUnits) {
                        if (!ListUtils.isEmpty(clientUnits)){
                            for (ClientUnit clientUnit : clientUnits){
                                clientUnit.setErrInfo(null);
                                clientUnit.setIsInvalidate(1);
                                if (!AppUtils.isHaveId(clientUnit.getLocalID())){
                                    clientUnit.setLocalID(null);
                                }
                                List<ClientUnit> list = SamplingApplication.getDaoSession().getClientUnitDao().queryRaw("where ID = ?", clientUnit.getID());
                                if (!ListUtils.isEmpty(list)){
                                    clientUnit.setLocalID(list.get(0).getLocalID());
                                }
                            }
                            SamplingApplication.getDaoSession().getClientUnitDao().saveInTx(clientUnits);
                            if (clientUnits.size() < 50){
                                return Observable.just(true);
                            }
                            return updateRemoteClientUnit();
                        }
                        return Observable.just(true);
                    }
                });
    }

    public Observable<List<ClientUnit>> syncAllData(){
        return Observable.combineLatest(syncClientUnit(), syncAllShouYaoJinHuoType(), syncAllZhiliangJinHuoType(), syncAllShouYaoSampleType(), syncAllZhiliangSampleType(), syncAllSampleName(), new Func6<List<ClientUnit>, Boolean, Boolean, Boolean, Boolean, Boolean, List<ClientUnit>>() {
            @Override
            public List<ClientUnit> call(List<ClientUnit> clientUnits, Boolean syncShouYaoJinHuoResult, Boolean syncZhiLiangJinHuoResult, Boolean syncShouyaoSampleResult, Boolean syncZhiLiangSampleResult, Boolean syncSampleNameResult) {

                if (!syncShouYaoJinHuoResult){
                    throw new FormException("同步残留抽样进货类型数据失败！");
                }

                if (!syncZhiLiangJinHuoResult){
                    throw new FormException("同步兽药抽样进货类型数据失败！");
                }
                if (!syncShouyaoSampleResult){
                    throw new FormException("同步残留抽样抽样类型数据失败！");
                }
                if (!syncZhiLiangSampleResult){
                    throw new FormException("同步兽药抽样抽样类型数据失败！");
                }
                if (!syncSampleNameResult){
                    throw new FormException("同步样品名数据失败！");
                }
                return clientUnits;
            }
        }).subscribeOn(Schedulers.io());
    }
}
