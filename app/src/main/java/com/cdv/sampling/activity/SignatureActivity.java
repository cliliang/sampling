package com.cdv.sampling.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.apkfuns.xprogressdialog.XProgressDialog;
import com.cdv.sampling.R;
import com.cdv.sampling.SamplingApplication;
import com.cdv.sampling.bean.AppFiles;
import com.cdv.sampling.bean.JianCeDan;
import com.cdv.sampling.constants.Constants;
import com.cdv.sampling.image.ImageLoaderUtils;
import com.cdv.sampling.repository.UserRepository;
import com.cdv.sampling.rxandroid.CommonSubscriber;
import com.cdv.sampling.utils.AppUtils;
import com.cdv.sampling.widget.InputLayout;
import com.nostra13.universalimageloader.core.download.ImageDownloader;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class SignatureActivity extends BaseActivity {

    private static final int REQUEST_CODE_BEIJIANRENQIANZI = 0;
    private static final int REQUEST_CODE_BEIJIANRENQIANZI_2 = 1;
    private static final int REQUEST_CODE_CAIYANGRENQIANZI = 2;
    private static final int REQUEST_CODE_CAIYANGRENQIANZI_2 = 3;

    public static final String EXTRA_JIANCEDAN = "EXTRA_JIANCEDAN";
    @BindView(R.id.input_client_user)
    InputLayout inputClientUser;
    @BindView(R.id.iv_beijianren_qianming)
    ImageView ivBeijianrenQianming;
    @BindView(R.id.iv_beijianren_qianming2)
    ImageView ivBeijianrenQianming2;
    @BindView(R.id.iv_caiyangren_qianzi2)
    ImageView ivCaiyangrenQianzi2;
    @BindView(R.id.input_test_user)
    InputLayout inputTestUser;
    @BindView(R.id.iv_caiyangren_qianzi)
    ImageView ivCaiyangrenQianzi;

    private String beiJianRenQianziId, beijianRiqiId, caiyangRenQianziId, caiyangRiqiId;

    private JianCeDan jianCeDan;
    private boolean isModified = false;

    public static Intent getStartIntent(Context context, JianCeDan jianCeDan) {
        Intent intent = new Intent(context, SignatureActivity.class);
        intent.putExtra(EXTRA_JIANCEDAN, jianCeDan);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signature);
        setMyTitle("签字");
        jianCeDan = (JianCeDan) getIntent().getSerializableExtra(EXTRA_JIANCEDAN);
        if (jianCeDan == null || !AppUtils.isHaveId(jianCeDan.getID())) {
            finish();
            return;
        }
        inputClientUser.setContent(jianCeDan.getClientUser());
        String testUser = jianCeDan.getTestUser();
        if (!TextUtils.isEmpty(testUser)){
            inputTestUser.setContent(testUser);
        }else {
            inputTestUser.setContent(UserRepository.getInstance().getCurrentUser().getUserName() + "，抽检人2");
        }

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
                ImageLoaderUtils.clearCache();
                return SamplingApplication.getDaoSession().getAppFilesDao().load(Long.parseLong(s));
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommonSubscriber<AppFiles>() {

                    @Override
                    public void onNext(AppFiles appFile) {
                        super.onNext(appFile);
                        if (appFile == null) {
                            return;
                        }
                        switch (appFile.getFileType()) {
                            case Constants.TYPE_BEIJIANREN_QIANZI:
                                beiJianRenQianziId = String.valueOf(appFile.getID());
                                ImageLoaderUtils.displayImageForIv(ivBeijianrenQianming, ImageDownloader.Scheme.FILE.wrap(appFile.getFilePath()));
                                break;
                            case Constants.TYPE_BEIJIANREN_QIANZI_2:
                                beijianRiqiId = String.valueOf(appFile.getID());
                                ImageLoaderUtils.displayImageForIv(ivCaiyangrenQianzi2, ImageDownloader.Scheme.FILE.wrap(appFile.getFilePath()));
                                break;
                            case Constants.TYPE_CAIYANGREN_QIANZI:
                                caiyangRenQianziId = String.valueOf(appFile.getID());
                                ImageLoaderUtils.displayImageForIv(ivCaiyangrenQianzi, ImageDownloader.Scheme.FILE.wrap(appFile.getFilePath()));
                                break;
                            case Constants.TYPE_CAIYANGREN_QIANZI_2:
                                caiyangRiqiId = String.valueOf(appFile.getID());
                                ImageLoaderUtils.displayImageForIv(ivBeijianrenQianming2, ImageDownloader.Scheme.FILE.wrap(appFile.getFilePath()));
                                break;
                        }
                    }
                });
    }

    @OnClick(R.id.iv_clear_beijianqianzi)
    void showBeijianrenQianming() {
        startActivityForResult(PaintActivity.getStartIntent(this, Constants.TYPE_BEIJIANREN_QIANZI, AppUtils.getFormRootPath(jianCeDan.getID())), REQUEST_CODE_BEIJIANRENQIANZI);
    }

    @OnClick(R.id.iv_clear_beijianriqi)
    void showBeijianRiqi() {
        startActivityForResult(PaintActivity.getStartIntent(this, Constants.TYPE_BEIJIANREN_QIANZI_2, AppUtils.getFormRootPath(jianCeDan.getID())), REQUEST_CODE_BEIJIANRENQIANZI_2);
    }

    @OnClick(R.id.iv_clear_caiyangrne)
    void showCaiyangrenQianming() {
        startActivityForResult(PaintActivity.getStartIntent(this, Constants.TYPE_CAIYANGREN_QIANZI, AppUtils.getFormRootPath(jianCeDan.getID())), REQUEST_CODE_CAIYANGRENQIANZI);
    }

    @OnClick(R.id.iv_clear_caiyangriqi)
    void showCaiyangRiqi() {
        startActivityForResult(PaintActivity.getStartIntent(this, Constants.TYPE_CAIYANGREN_QIANZI_2, AppUtils.getFormRootPath(jianCeDan.getID())), REQUEST_CODE_CAIYANGRENQIANZI_2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK != resultCode) {
            return;
        }
        isModified = true;
        ImageLoaderUtils.clearCache();
        String id = data.getStringExtra(PaintActivity.EXTRA_FILE_ID);
        String imagePath = data.getStringExtra(PaintActivity.EXTRA_FILE_PATH);
        if (requestCode == REQUEST_CODE_BEIJIANRENQIANZI) {
            beiJianRenQianziId = id;
            ImageLoaderUtils.displayImageForIv(ivBeijianrenQianming, ImageDownloader.Scheme.FILE.wrap(imagePath));
        } else if (requestCode == REQUEST_CODE_BEIJIANRENQIANZI_2) {
            beijianRiqiId = id;
            ImageLoaderUtils.displayImageForIv(ivCaiyangrenQianzi2, ImageDownloader.Scheme.FILE.wrap(imagePath));
        } else if (requestCode == REQUEST_CODE_CAIYANGRENQIANZI) {
            caiyangRenQianziId = id;
            ImageLoaderUtils.displayImageForIv(ivCaiyangrenQianzi, ImageDownloader.Scheme.FILE.wrap(imagePath));
        } else if (requestCode == REQUEST_CODE_CAIYANGRENQIANZI_2) {
            caiyangRiqiId = id;
            ImageLoaderUtils.displayImageForIv(ivBeijianrenQianming2, ImageDownloader.Scheme.FILE.wrap(imagePath));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_save, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.save) {
            save();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void save() {
        if (!isModified && inputClientUser.getContent().equals(jianCeDan.getClientUser()) && inputTestUser.getContent().equals(jianCeDan.getTestUser() + "，抽检人2")) {
            showToast("签字信息未修改！");
            return;
        }
        if (TextUtils.isEmpty(inputClientUser.getContent())) {
            showToast("请输入受检单位经办人！");
            return;
        }
//        if (TextUtils.isEmpty(beiJianRenQianziId)) {
//            showToast("请录入被检人1未签字！");
//            return;
//        }
//        if (TextUtils.isEmpty(caiyangRenQianziId)) {
//            showToast("请录入采样人1签字！");
//            return;
//        }
//        if (TextUtils.isEmpty(caiyangRiqiId)) {
//            showToast("请录入采样人2签字！");
//            return;
//        }

        if (TextUtils.isEmpty(beiJianRenQianziId) && TextUtils.isEmpty(caiyangRenQianziId) && TextUtils.isEmpty(caiyangRiqiId)){
            showToast("请录入至少一人签字");
            return;
        }
        final XProgressDialog dialog = new XProgressDialog(this, "正在加载..", XProgressDialog.THEME_CIRCLE_PROGRESS);
        dialog.show();
        String fileIds;
        if (TextUtils.isEmpty(beijianRiqiId)) {
//            fileIds = beiJianRenQianziId.concat(Constants.FILE_ID_SEPARATOR)
//                    .concat(caiyangRenQianziId).concat(Constants.FILE_ID_SEPARATOR).concat(caiyangRiqiId);
            StringBuilder builder = new StringBuilder();
            if (!TextUtils.isEmpty(beiJianRenQianziId)){
                builder.append(beiJianRenQianziId).append(Constants.FILE_ID_SEPARATOR);
            }
            if (!TextUtils.isEmpty(caiyangRenQianziId)){
                builder.append(caiyangRenQianziId).append(Constants.FILE_ID_SEPARATOR);
            }
            if (!TextUtils.isEmpty(caiyangRiqiId)){
                builder.append(caiyangRiqiId);
            }
            fileIds = builder.toString();
            if (!TextUtils.isEmpty(fileIds) && fileIds.endsWith(Constants.FILE_ID_SEPARATOR)){
                fileIds = fileIds.substring(0, fileIds.length() - 1);
            }
        } else {
//            fileIds = beiJianRenQianziId.concat(Constants.FILE_ID_SEPARATOR).concat(beijianRiqiId).concat(Constants.FILE_ID_SEPARATOR)
//                    .concat(caiyangRenQianziId).concat(Constants.FILE_ID_SEPARATOR).concat(caiyangRiqiId);
            StringBuilder builder = new StringBuilder();
            if (!TextUtils.isEmpty(beiJianRenQianziId)){
                builder.append(beiJianRenQianziId).append(Constants.FILE_ID_SEPARATOR);
            }
            if (!TextUtils.isEmpty(beijianRiqiId)){
                builder.append(beijianRiqiId).append(Constants.FILE_ID_SEPARATOR);
            }
            if (!TextUtils.isEmpty(caiyangRenQianziId)){
                builder.append(caiyangRenQianziId).append(Constants.FILE_ID_SEPARATOR);
            }
            if (!TextUtils.isEmpty(caiyangRiqiId)){
                builder.append(caiyangRiqiId);
            }
            fileIds = builder.toString();
            if (!TextUtils.isEmpty(fileIds) && fileIds.endsWith(Constants.FILE_ID_SEPARATOR)){
                fileIds = fileIds.substring(0, fileIds.length() - 1);
            }
        }
        jianCeDan.setClientUser(inputClientUser.getContent());
        jianCeDan.setTestUser(inputTestUser.getContent());
        Observable.just(fileIds).map(new Func1<String, Boolean>() {
            @Override
            public Boolean call(String s) {
                jianCeDan.setFileIDs(s);
                SamplingApplication.getDaoSession().getJianCeDanDao().save(jianCeDan);
                return true;
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommonSubscriber<Boolean>() {

                    @Override
                    public void onNext(Boolean o) {
                        super.onNext(o);
                        dialog.dismiss();
                        showToast("保存成功！");
                        setResult(RESULT_OK);
                        finish();
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        showToast("保存失败！");
                        dialog.dismiss();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ImageLoaderUtils.clearCache();
    }
}
