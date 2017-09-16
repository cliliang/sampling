package com.cdv.sampling.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.cdv.sampling.R;
import com.cdv.sampling.SamplingApplication;
import com.cdv.sampling.adapter.CommonAdapter;
import com.cdv.sampling.adapter.CommonDataItem;
import com.cdv.sampling.bean.ClientUnit;
import com.cdv.sampling.bean.JianCeDan;
import com.cdv.sampling.bean.JianCeDanDao;
import com.cdv.sampling.rxandroid.CommonSubscriber;
import com.cdv.sampling.utils.ListUtils;
import com.cdv.sampling.utils.TimeUtils;
import com.cdv.sampling.widget.EPocketAlertDialog;
import com.tencent.bugly.crashreport.CrashReport;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class FormListActivity extends BaseActivity {

    private static final int TYPE_ALL = 0;
    private static final int TYPE_NO_FINISH = 1;
    private static final int TYPE_ARCHIVED = 2;

    @BindView(R.id.listView)
    ListView listView;
    @BindView(R.id.tab_all)
    LinearLayout tabAll;
    @BindView(R.id.tab_not_finish)
    LinearLayout tabNotFinish;
    @BindView(R.id.tab_archived)
    LinearLayout tabArchived;

    private int mCurrentStatus = TYPE_ALL;

    private CommonAdapter mAdapter;
    private List<CommonDataItem> mDataSource = new ArrayList<>();

    private List<JianCeDan> mAllData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_list);
        ButterKnife.bind(this);

        setMyTitle("样品列表");
        mAdapter = new CommonAdapter(this, mDataSource);
        mAdapter.setViewHooker(new CommonAdapter.ViewHooker() {
            @Override
            public boolean isHookSuccess(int position, View view, Object viewData) {
                if (view.getId() == R.id.panel_root && viewData instanceof Integer){
                    int resId = (int) viewData;
                    view.setBackgroundResource(resId);
                    return true;
                }
                return false;
            }
        });
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CommonDataItem dataItem = mDataSource.get(position);
                JianCeDan dan = (JianCeDan) dataItem.getTag();
                startActivity(FormDetailActivity.getStartIntent(FormListActivity.this, dan.getID()));
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                CommonDataItem dataItem = mDataSource.get(position);
                final JianCeDan dan = (JianCeDan) dataItem.getTag();
                EPocketAlertDialog.getInstance().showAlertContent(getSupportFragmentManager(), "是否删除该表单？", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SamplingApplication.getDaoSession().getJianCeDanDao().delete(dan);
                        showToast("删除表单成功！");
                        initData();
                    }
                });
                return true;
            }
        });
        tabAll.setSelected(true);
    }


    @OnClick({R.id.tab_all, R.id.tab_not_finish, R.id.tab_archived})
    public void onClick(View view) {
        int status = mCurrentStatus;
        switch (view.getId()) {
            case R.id.tab_all:
                status = TYPE_ALL;
                break;
            case R.id.tab_not_finish:
                status = TYPE_NO_FINISH;
                break;
            case R.id.tab_archived:
                status = TYPE_ARCHIVED;
                break;
        }
        tabAll.setSelected(false);
        tabNotFinish.setSelected(false);
        tabArchived.setSelected(false);
        view.setSelected(true);
        if (status == mCurrentStatus) {
            return;
        }
        tabAll.setSelected(false);
        tabNotFinish.setSelected(false);
        tabArchived.setSelected(false);
        view.setSelected(true);
        mCurrentStatus = status;
        initData();
    }

    private void initData() {
        Observable<List<JianCeDan>> observable;
        if (ListUtils.isEmpty(mAllData)) {
            observable = SamplingApplication.getDaoSession().getJianCeDanDao().queryBuilder().orderDesc(JianCeDanDao.Properties.ID).rx().list();
        } else {
            observable = Observable.just(mAllData);
        }
        if (mCurrentStatus == TYPE_NO_FINISH) {
            observable = observable.map(new Func1<List<JianCeDan>, List<JianCeDan>>() {
                @Override
                public List<JianCeDan> call(List<JianCeDan> jianCeDen) {
                    List<JianCeDan> list = new ArrayList<JianCeDan>();
                    if (ListUtils.isEmpty(jianCeDen)) {
                        return list;
                    }
                    for (JianCeDan dan : jianCeDen) {
                        if (dan.getStatus() != JianCeDan.STATUS_ARCHIVED) {
                            list.add(dan);
                        }
                    }
                    return list;
                }
            });
        } else if (mCurrentStatus == TYPE_ARCHIVED) {
            observable = observable.map(new Func1<List<JianCeDan>, List<JianCeDan>>() {
                @Override
                public List<JianCeDan> call(List<JianCeDan> jianCeDen) {
                    List<JianCeDan> list = new ArrayList<JianCeDan>();
                    if (ListUtils.isEmpty(jianCeDen)) {
                        return list;
                    }
                    for (JianCeDan dan : jianCeDen) {
                        if (dan.getStatus() == JianCeDan.STATUS_ARCHIVED) {
                            list.add(dan);
                        }
                    }
                    return list;
                }
            });
        }
        observable.map(new Func1<List<JianCeDan>, List<CommonDataItem>>() {
            @Override
            public List<CommonDataItem> call(List<JianCeDan> jianCeDen) {
                List<CommonDataItem> itemList = new ArrayList<CommonDataItem>();
                for (JianCeDan dan : jianCeDen) {
                    CommonDataItem item = new CommonDataItem(R.layout.item_form);
                    item.setTag(dan);
                    item.bindView(R.id.tv_time, TimeUtils.getNormalTimeFormat(dan.getCreateTime()));
                    ClientUnit clientUnit = SamplingApplication.getDaoSession().getClientUnitDao().load(dan.getClientID());
                    if (clientUnit == null){
                        CrashReport.postCatchedException(new IllegalArgumentException("client is null"));
                        continue;
                    }
                    item.bindView(R.id.tv_company, clientUnit.getName());
                    switch (dan.getEDanType()) {
                        case JianCeDan.DAN_TYPE_SAMPLING_DRUG:
                            item.bindView(R.id.iv_form_type, R.drawable.ic_drug);
                            item.bindView(R.id.tv_form_type, "残留抽样");
                            item.bindView(R.id.panel_root, R.color.white);
                            break;
                        case JianCeDan.DAN_TYPE_SAMPLING_QUALITY:
                            item.bindView(R.id.iv_form_type, R.drawable.ic_quality);
                            item.bindView(R.id.tv_form_type, "兽药抽样");
                            item.bindView(R.id.panel_root, R.color.color_sample_even);
                            break;
                        case JianCeDan.DAN_TYPE_SAMPLING_VERIFICATION:
                            item.bindView(R.id.iv_form_type, R.drawable.ic_sample);
                            item.bindView(R.id.tv_form_type, "样品核实");
                            item.bindView(R.id.panel_root, R.color.color_sample);
                            break;
                    }
                    itemList.add(item);
                }
                return itemList;
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommonSubscriber<List<CommonDataItem>>() {
                    @Override
                    public void onNext(List<CommonDataItem> o) {
                        super.onNext(o);
                        mDataSource.clear();
                        mDataSource.addAll(o);
                        mAdapter.notifyDataSetChanged();
                    }
                });
    }

    public static Intent getStartIntent(Context context) {
        return new Intent(context, FormListActivity.class);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }
}
