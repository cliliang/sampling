package com.cdv.sampling.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.cdv.sampling.R;
import com.cdv.sampling.SamplingApplication;
import com.cdv.sampling.bean.AppTypes;
import com.cdv.sampling.exception.ErrorMessageFactory;
import com.cdv.sampling.rxandroid.CommonSubscriber;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class AddAppTypeActivity extends BaseActivity {

    private static final String EXTRA_APP_TYPE = "EXTRA_APP_TYPE";

    @BindView(R.id.et_type_name)
    EditText etTypeName;
    @BindView(R.id.text_input_layout)
    TextInputLayout inputLayout;
    @BindView(R.id.delete_text)
    ImageView deleteText;

    private String type;
    private String typeTitle;

    public static Intent getStartIntent(Context context, String type) {
        Intent intent = new Intent(context, AddAppTypeActivity.class);
        intent.putExtra(EXTRA_APP_TYPE, type);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        type = getIntent().getStringExtra(EXTRA_APP_TYPE);
        if (TextUtils.isEmpty(type)){
            finish();
            return;
        }

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

        setContentView(R.layout.activity_add_app_type);
        setMyTitle(typeTitle);
        etTypeName.setHint("请输入" + typeTitle);
        etTypeName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(etTypeName.getText().toString())) {
                    deleteText.setVisibility(View.GONE);
                    return;
                }
                deleteText.setVisibility(View.VISIBLE);

            }
        });
    }

    @OnClick(R.id.delete_text)
    void deleteText(){
        etTypeName.getText().clear();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_save, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.save){
            submit();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void submit() {
        String name = etTypeName.getText().toString();
        Observable.just(name).map(new Func1<String, Boolean>() {
            @Override
            public Boolean call(String s) {
                int count = SamplingApplication.getDaoSession().getAppTypesDao().queryRaw("where Value_Name = ? AND Value_Type = ?", s, type)
                        .size();
                if (count > 0){
                    throw new RuntimeException("该类型已存在!");
                }
                AppTypes types = new AppTypes();
                types.setValueName(s);
                types.setValueType(type);
                SamplingApplication.getDaoSession().getAppTypesDao().save(types);
                return true;
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommonSubscriber<Boolean>(){

                    @Override
                    public void onNext(Boolean o) {
                        super.onNext(o);
                        showToast("添加成功！");
                        setResult(RESULT_OK, new Intent());
                        finish();
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        showToast(ErrorMessageFactory.create(e));
                    }
                });
    }
}
