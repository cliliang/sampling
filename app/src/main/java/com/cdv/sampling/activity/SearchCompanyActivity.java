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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.cdv.sampling.R;
import com.cdv.sampling.SamplingApplication;
import com.cdv.sampling.adapter.CommonAdapter;
import com.cdv.sampling.adapter.CommonDataItem;
import com.cdv.sampling.bean.ClientUnit;
import com.cdv.sampling.bean.ClientUnitDao;
import com.cdv.sampling.rxandroid.CommonSubscriber;
import com.cdv.sampling.utils.ListUtils;
import com.cdv.sampling.utils.PinYinUtil;
import com.jakewharton.rxbinding.support.v7.widget.RxSearchView;

import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import butterknife.BindView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static android.R.attr.key;

public class SearchCompanyActivity extends BaseActivity {

    public static final String RESULT_KEY_SELECTED = "RESULT_KEY_SELECTED";
    public static final String EXTRA_CLIENT_UNIT = "EXTRA_CLIENT_UNIT";
    public static final String EXTRA_SEARCH_KEY = "EXTRA_SEARCH_KEY";

    @BindView(R.id.listView)
    ListView mListView;

    private CommonAdapter mAdapter;
    private SearchView mSearchView;
    private ClientUnit clientUnit;
    private String searchKey = "";
    private List<CommonDataItem> mDataSource = new ArrayList<>();

    private boolean haveNoMore = true;
    private boolean isFirstPage = true;
    private boolean onCreateSearch = true;

    public static Intent getStartIntent(Context context, ClientUnit clientUnit) {
        Intent intent = new Intent(context, SearchCompanyActivity.class);
        intent.putExtra(EXTRA_CLIENT_UNIT, clientUnit);
        return intent;
    }

    public static Intent getStartIntent(Context context, ClientUnit clientUnit, String key) {
        Intent intent = new Intent(context, SearchCompanyActivity.class);
        intent.putExtra(EXTRA_CLIENT_UNIT, clientUnit);
        intent.putExtra(EXTRA_SEARCH_KEY, key);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_company);

        setMyTitle("单位列表");
        clientUnit = (ClientUnit) getIntent().getSerializableExtra(EXTRA_CLIENT_UNIT);
        searchKey = getIntent().getStringExtra(EXTRA_SEARCH_KEY);
        mAdapter = new CommonAdapter(this, mDataSource);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ClientUnit unit = (ClientUnit) mDataSource.get(position).getTag();
                Intent intent = new Intent();
                intent.putExtra(RESULT_KEY_SELECTED, unit);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

//        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && view.getLastVisiblePosition() == view.getCount() - 1) {
//                    if (!haveNoMore){
//                        initData(null);
//                    }
//                }
//            }
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//
//            }
//        });
//        initData(null);
    }

    private void initData(final String searchKey) {
        Observable<List<ClientUnit>> observable;
//        if (clientUnit != null){
//            observable = Observable.just(clientUnit).map(new Func1<ClientUnit, List<ClientUnit>>() {
//                @Override
//                public List<ClientUnit> call(ClientUnit clientUnit) {
//                    QueryBuilder<ClientUnit> queryBuilder = SamplingApplication.getDaoSession().getClientUnitDao().queryBuilder().orderDesc(ClientUnitDao.Properties.ID);
//                    addLikeProperty(queryBuilder, ClientUnitDao.Properties.Address, clientUnit.getAddress());
//                    addLikeProperty(queryBuilder, ClientUnitDao.Properties.BusinessCode, clientUnit.getBusinessCode());
//                    addLikeProperty(queryBuilder, ClientUnitDao.Properties.Code, clientUnit.getCode());
//                    addLikeProperty(queryBuilder, ClientUnitDao.Properties.ContactUser, clientUnit.getContactUser());
//                    addLikeProperty(queryBuilder, ClientUnitDao.Properties.Email, clientUnit.getEmail());
//                    addLikeProperty(queryBuilder, ClientUnitDao.Properties.Fax, clientUnit.getFax());
//                    if (!TextUtils.isEmpty(clientUnit.getName())){
//                        String param = PinYinUtil.ChineseToHanYuPinYin(clientUnit.getName());
//                        param = "%" + param + "%";
//                        if (isEnglishOrNumber(clientUnit.getName())){
//                            queryBuilder.where(ClientUnitDao.Properties.ShortName.like(param));
//                        }else{
//                            queryBuilder.where(ClientUnitDao.Properties.Name.like("%" + clientUnit.getName() + "%"));
//                        }
//                    }else{
//                        addLikeProperty(queryBuilder, ClientUnitDao.Properties.Name, clientUnit.getName());
//                    }
//                    queryBuilder = queryBuilder.offset(mDataSource.size()).limit(20);
//                    return queryBuilder.build().list();
//                }
//            });
//        }else{
//            observable = Observable.just(searchKey).map(new Func1<String, List<ClientUnit>>() {
//                @Override
//                public List<ClientUnit> call(String s) {
//                    if (TextUtils.isEmpty(s)){
//                        return SamplingApplication.getDaoSession().getClientUnitDao().queryBuilder().orderDesc(ClientUnitDao.Properties.ID).build().list();
//                    }
//                    String param = PinYinUtil.ChineseToHanYuPinYin(s);
//                    param = "%" + param + "%";
//                    return SamplingApplication.getDaoSession().getClientUnitDao().queryBuilder().where(ClientUnitDao.Properties.ShortName.like(param)).orderDesc(ClientUnitDao.Properties.ID).build().list();
//                }
//            });
//        }
        observable = Observable.just(searchKey).map(new Func1<String, List<ClientUnit>>() {
            @Override
            public List<ClientUnit> call(String s) {
                if (TextUtils.isEmpty(s)) {
                    return SamplingApplication.getDaoSession().getClientUnitDao().queryBuilder().orderDesc(ClientUnitDao.Properties.ID).build().list();
                }
                String param;
                if (check(s)){
                    param = PinYinUtil.ChineseToHanYuPinYin(s);
                    param = "%" + param + "%";
                    return SamplingApplication.getDaoSession().getClientUnitDao().queryBuilder().where(ClientUnitDao.Properties.ShortName.like(param)).orderDesc(ClientUnitDao.Properties.ID).build().list();
                }else {
                    param = "%" + s + "%";
                    return SamplingApplication.getDaoSession().getClientUnitDao().queryBuilder().where(ClientUnitDao.Properties.Name.like(param)).orderDesc(ClientUnitDao.Properties.ID).build().list();
                }

            }
        });

        observable.map(new Func1<List<ClientUnit>, List<CommonDataItem>>() {
            @Override
            public List<CommonDataItem> call(List<ClientUnit> clientUnits) {
                List<CommonDataItem> itemList = new ArrayList<>();
                if (ListUtils.isEmpty(clientUnits)) {
                    return itemList;
                }
                for (ClientUnit units : clientUnits) {
                    CommonDataItem dataItem = new CommonDataItem(R.layout.item_company_list);
                    dataItem.bindView(R.id.tv_name, units.getName());
                    dataItem.bindView(R.id.tv_license, units.getBusinessCode());
                    dataItem.bindView(R.id.tv_address, units.getAddress());
                    dataItem.bindView(R.id.tv_contact_people, units.getContactUser());
                    dataItem.bindView(R.id.tv_fax, units.getFax());
                    dataItem.bindView(R.id.tv_postCode, units.getZip());
                    dataItem.setTag(units);
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
//
                        if (ListUtils.isEmpty(dataItemList)) {
                            if (ListUtils.isEmpty(mDataSource)) {
                                showToast("没有数据！");
                            } else {
                                showToast("没有更多数据！");
                            }
//                            haveNoMore = true;
                        } else {
//                            haveNoMore = false;
                        }
//                        if (isFirstPage){
//                            mDataSource.clear();
//                        }
//                        isFirstPage = false;
//                        mDataSource.addAll(dataItemList);
//                        mAdapter.notifyDataSetChanged();

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
        mSearchView.setSearchableInfo(manager.getSearchableInfo(getComponentName()));
        RxSearchView.queryTextChanges(mSearchView).debounce(500, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<CharSequence>() {
                    @Override
                    public void call(CharSequence charSequence) {
                        if (onCreateSearch) {
                            initData(searchKey);
                            onCreateSearch = false;
                        } else {
                            initData(charSequence.toString());
                        }

                    }
                });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.add) {
            startActivityForResult(AddCompanyActivity.getStartIntent(this, null), 0);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static boolean isEnglishOrNumber(String str) {
        Pattern pattern = Pattern.compile("^[A-Za-z0-9]+$");
        return pattern.matcher(str).matches();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            setResult(RESULT_OK, data);
            finish();
        }
    }

    private QueryBuilder<ClientUnit> addLikeProperty(QueryBuilder<ClientUnit> queryBuilder, Property property, String value) {
        if (TextUtils.isEmpty(value)) {
            return queryBuilder;
        }
        if (property == null) {
            return queryBuilder;
        }
        return queryBuilder.where(property.like("%" + value + "%"));
    }

    public boolean check(String data) {
        char c = data.charAt(0);
        if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
            return true;
        } else {
            return false;
        }
    }
}
