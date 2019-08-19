package com.cdv.sampling.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.cdv.sampling.R;
import com.cdv.sampling.SamplingApplication;
import com.cdv.sampling.bean.AppFiles;
import com.cdv.sampling.exception.DataFormatException;
import com.cdv.sampling.exception.ErrorMessageFactory;
import com.cdv.sampling.image.ImageLoaderUtils;
import com.cdv.sampling.rxandroid.CommonSubscriber;
import com.cdv.sampling.utils.ImageUtil;
import com.cdv.sampling.utils.MD5Utils;
import com.cdv.sampling.utils.ToastUtils;
import com.cdv.sampling.utils.UIUtils;
import com.cdv.sampling.widget.MyPaintView;
import com.lht.paintview.pojo.DrawShape;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class PaintActivity extends BaseActivity {
    public static final String EXTRA_FILE_ID = "EXTRA_FILE_ID";
    public static final String EXTRA_FILE_PATH = "EXTRA_FILE_PATH";

    private static final String EXTRA_PAINT_TYPE = "EXTRA_PAINT_TYPE";
    private static final String EXTRA_ROOT_PATH = "EXTRA_ROOT_PATH";

    @BindView(R.id.view_paint)
    MyPaintView viewPaint;

    String paintType;
    String rootPath;

    public static Intent getStartIntent(Context context, String fileType, String rootDir) {
        Intent intent = new Intent(context, PaintActivity.class);
        intent.putExtra(EXTRA_PAINT_TYPE, fileType);
        intent.putExtra(EXTRA_ROOT_PATH, rootDir);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paint);
        paintType = getIntent().getStringExtra(EXTRA_PAINT_TYPE);
        viewPaint.setColor(getResources().getColor(R.color.black));
        viewPaint.setBackgroundColor(Color.TRANSPARENT);
        viewPaint.setStrokeWidth(UIUtils.convertDpToPixel(8, this));
        rootPath = getIntent().getStringExtra(EXTRA_ROOT_PATH);
    }

    @OnClick(R.id.btn_undo)
    void undo(){
        if (!viewPaint.undo()){
            viewPaint.setIsEmpty(true);
        }
    }

    @OnClick(R.id.btn_clear)
    void clear(){
        viewPaint.setDrawShapes(new ArrayList<DrawShape>());
        viewPaint.invalidate();
        viewPaint.setIsEmpty(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_save, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.save){
            if (viewPaint.isEmpty()){
                ToastUtils.show(this, "保存失败，请先签名");
            }else {
                final Intent intent = new Intent();
                Bitmap bitmap = viewPaint.getBitmap(true);

                Observable.just(bitmap).map(new Func1<Bitmap, String>() {
                    @Override
                    public String call(Bitmap bitmap) {
                        String imagePath = rootPath + "Paint-" + paintType + ".png";
                        boolean success = ImageUtil.saveBitmap(bitmap, imagePath, true);
                        if (!success){
                            throw new DataFormatException("图片保存失败！");
                        }
                        AppFiles files = new AppFiles();
                        files.setCreateTime(new Date());
                        files.setTitle(String.valueOf(paintType));
                        files.setFilePath(imagePath);
                        files.setFileType("JPEG");
                        File file = new File(imagePath);
                        files.setMD5(MD5Utils.calculateMD5(file));
                        files.setFileSize(String.valueOf(file.length()));
                        files.setFileType(String.valueOf(paintType));
                        SamplingApplication.getDaoSession().getAppFilesDao().save(files);
                        SamplingApplication.getDaoSession().getAppFilesDao().refresh(files);
                        intent.putExtra(EXTRA_FILE_PATH, imagePath);
                        return String.valueOf(files.getID());
                    }
                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new CommonSubscriber<String>(){

                            @Override
                            public void onNext(String o) {
                                super.onNext(o);
                                showToast("图片保存成功！");
                                intent.putExtra(EXTRA_FILE_ID, o);
                                setResult(RESULT_OK, intent);
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
        return super.onOptionsItemSelected(item);
    }
}
