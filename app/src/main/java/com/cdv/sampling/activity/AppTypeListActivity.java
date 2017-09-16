package com.cdv.sampling.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.cdv.sampling.R;
import com.cdv.sampling.SamplingApplication;
import com.cdv.sampling.adapter.CommonAdapter;
import com.cdv.sampling.adapter.CommonDataItem;
import com.cdv.sampling.bean.AppTypes;
import com.cdv.sampling.rxandroid.CommonSubscriber;
import com.cdv.sampling.utils.ListUtils;
import com.cdv.sampling.widget.EPocketAlertDialog;
import com.jakewharton.rxbinding.support.v7.widget.RxSearchView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class AppTypeListActivity extends BaseActivity {

    public static final String RESULT_KEY_SELECTED = "RESULT_KEY_SELECTED";

    private static final int REQUEST_CODE_ADD_TYPE = 0;
    private static final String EXTRA_APP_TYPE = "EXTRA_APP_TYPE";

    @BindView(R.id.listView)
    ListView mListView;

    private CommonAdapter mAdapter;

    private List<CommonDataItem> mDataSource = new ArrayList<>();
    private SearchView mSearchView;

    private String type;

    public static Intent getStartIntent(Context context, String type) {
        Intent intent = new Intent(context, AppTypeListActivity.class);
        intent.putExtra(EXTRA_APP_TYPE, type);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_company);

        type = getIntent().getStringExtra(EXTRA_APP_TYPE);

        String typeTitle = null;
        switch (type){
            case AppTypes.TYPE_SHOUYAO_JINHUO_FANGSHI:
            case AppTypes.TYPE_ZHILIANG_JINHUO_FANGSHI:
                typeTitle = "进货方式";
                break;
            case AppTypes.TYPE_SHOUYAO_SAMPLE_TYPE:
            case AppTypes.TYPE_ZHILIANG_SAMPLE_TYPE:
                typeTitle = "抽样环节";
                break;
            case AppTypes.TYPE_YANGPIN_LEIXING:
                typeTitle = "样品类型";
                break;
            case AppTypes.TYPE_YANGPIN_MING:
                typeTitle = "样品名";
                break;
        }
        setMyTitle(typeTitle);
        mAdapter = new CommonAdapter(this, mDataSource);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AppTypes unit = (AppTypes) mDataSource.get(position).getTag();
                Intent intent = new Intent();
                intent.putExtra(RESULT_KEY_SELECTED, unit);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long l) {

                EPocketAlertDialog.getInstance().showAlertContent(getSupportFragmentManager(), "是否删除该样品信息？", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AppTypes unit = (AppTypes) mDataSource.get(position).getTag();
                        SamplingApplication.getDaoSession().getAppTypesDao().delete(unit);
                        showToast("删除成功！");
                        initData(mSearchView.getQuery().toString());
                    }
                });
                return true;
            }
        });
        initData(null);
    }

    private void initData(String searchKey) {
        Observable.just(searchKey).map(new Func1<String, List<AppTypes>>() {
            @Override
            public List<AppTypes> call(String s) {
                if (TextUtils.isEmpty(s)) {
                    return SamplingApplication.getDaoSession().getAppTypesDao().queryRaw("where Value_Type = ?", type);
                }
                return SamplingApplication.getDaoSession().getAppTypesDao().queryRaw("where Value_Name like ? AND Value_Type = ?", "%" + s + "%", type);
            }
        }).map(new Func1<List<AppTypes>, List<CommonDataItem>>() {
            @Override
            public List<CommonDataItem> call(List<AppTypes> appTypes) {
                List<CommonDataItem> itemList = new ArrayList<>();
                if (ListUtils.isEmpty(appTypes)) {
                    return itemList;
                }
                for (AppTypes type : appTypes) {
                    CommonDataItem dataItem = new CommonDataItem(R.layout.item_single_title);
                    dataItem.bindView(R.id.tv_title, type.getValueName());
                    dataItem.setTag(type);
                    itemList.add(dataItem);
                }
                return itemList;
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommonSubscriber<List<CommonDataItem>>() {

                    @Override
                    public void onNext(List<CommonDataItem> dataItemList) {
                        super.onNext(dataItemList);
                        mDataSource.clear();
                        mDataSource.addAll(dataItemList);
                        mAdapter.notifyDataSetChanged();
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_company_search, menu);
        SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView = (SearchView) menu.findItem(R.id.search).getActionView();
        EditText searchEditText = (EditText) mSearchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        if (searchEditText != null){
            searchEditText.setTextColor(getResources().getColor(R.color.white));
            searchEditText.setHintTextColor(getResources().getColor(R.color.color_ddd));
        }

        mSearchView.setSearchableInfo(manager.getSearchableInfo(getComponentName()));
        RxSearchView.queryTextChanges(mSearchView).debounce(500, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<CharSequence>() {
                    @Override
                    public void call(CharSequence charSequence) {
                        initData(charSequence.toString());
                    }
                });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.add) {
            startActivityForResult(AddAppTypeActivity.getStartIntent(this, type), REQUEST_CODE_ADD_TYPE);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            mSearchView.setQuery("", true);
            initData("");
        }
    }
}
