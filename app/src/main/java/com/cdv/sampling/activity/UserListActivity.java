package com.cdv.sampling.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.apkfuns.xprogressdialog.XProgressDialog;
import com.cdv.sampling.R;
import com.cdv.sampling.adapter.CommonAdapter;
import com.cdv.sampling.adapter.CommonDataItem;
import com.cdv.sampling.bean.UserBean;
import com.cdv.sampling.exception.ErrorMessageFactory;
import com.cdv.sampling.net.HttpService;
import com.cdv.sampling.net.OperatorRequestMap;
import com.cdv.sampling.repository.UserRepository;
import com.cdv.sampling.rxandroid.CommonSubscriber;
import com.cdv.sampling.utils.ListUtils;
import com.cdv.sampling.widget.EPocketAlertDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class UserListActivity extends BaseActivity {

    @BindView(R.id.listView)
    ListView mLv;

    SearchView mSearchView;

    private CommonAdapter mAdapter;
    private List<CommonDataItem> mDataSource = new ArrayList<>();

    public static Intent getStartIntent(Context context) {
        return new Intent(context, UserListActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        setMyTitle("用户列表");
        mAdapter = new CommonAdapter(this, mDataSource);
        mLv.setAdapter(mAdapter);

        initData("");

        mLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final UserBean userBean = (UserBean) mDataSource.get(i).getTag();
                startActivityForResult(AddUserActivity.getStartIntent(UserListActivity.this, userBean), 0);
            }
        });

        mLv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final UserBean userBean = (UserBean) mDataSource.get(i).getTag();
                EPocketAlertDialog.getInstance().showAlertContent(getSupportFragmentManager(), "确定要删除改用户么？", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteUser(userBean);
                    }
                });
                return true;
            }
        });
    }

    private void deleteUser(UserBean userBean) {
        final XProgressDialog dialog = new XProgressDialog(this, "正在加载..", XProgressDialog.THEME_CIRCLE_PROGRESS);
        dialog.show();
        HttpService.getApi().deleteUser(userBean.getID()).lift(new OperatorRequestMap<UserBean>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommonSubscriber<UserBean>(){
                    @Override
                    public void onNext(UserBean o) {
                        super.onNext(o);
                        dialog.dismiss();
                        showToast("删除成功！");
                        initData("");
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        dialog.dismiss();
                        showToast(ErrorMessageFactory.create(e));
                    }
                });
    }

    private void initData(String keyword) {
        final XProgressDialog dialog = new XProgressDialog(this, "正在加载..", XProgressDialog.THEME_CIRCLE_PROGRESS);
        dialog.show();
        UserRepository.searchUserList(keyword)
                .subscribeOn(Schedulers.io())
                .map(new Func1<List<UserBean>, List<CommonDataItem>>() {
                    @Override
                    public List<CommonDataItem> call(List<UserBean> userBeen) {
                        List<CommonDataItem> itemList = new ArrayList<CommonDataItem>();
                        if (ListUtils.isEmpty(userBeen)){
                            return itemList;
                        }
                        for (UserBean userBean : userBeen){
                            CommonDataItem item = new CommonDataItem(R.layout.item_user_list);
                            item.bindView(R.id.tv_username, userBean.getUserName());
                            item.setTag(userBean);
                            itemList.add(item);
                        }
                        return itemList;
                    }
                }).observeOn(AndroidSchedulers.mainThread())
        .subscribe(new CommonSubscriber<List<CommonDataItem>>(){

            @Override
            public void onNext(List<CommonDataItem> list) {
                super.onNext(list);
                dialog.dismiss();
                mDataSource.clear();
                if (!ListUtils.isEmpty(list)){
                    mDataSource.addAll(list);
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                dialog.dismiss();
                showToast(ErrorMessageFactory.create(e));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user_list, menu);
//        SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//        mSearchView = (SearchView) menu.findItem(R.id.search).getActionView();
//        mSearchView.setSearchableInfo(manager.getSearchableInfo(getComponentName()));
//        RxSearchView.queryTextChanges(mSearchView).debounce(500, TimeUnit.MILLISECONDS)
//                .subscribe(new Action1<CharSequence>() {
//                    @Override
//                    public void call(CharSequence charSequence) {
//                        initData(charSequence.toString());
//                    }
//                });
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.add) {
            startActivityForResult(AddUserActivity.getStartIntent(this, null), 0);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
//            mSearchView.setQuery("", true);
            initData("");
        }
    }
}
