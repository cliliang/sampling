package com.cdv.sampling.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.cdv.sampling.R;
import com.cdv.sampling.SamplingApplication;
import com.cdv.sampling.activity.BaseActivity;
import com.cdv.sampling.activity.ImageShowActivity;
import com.cdv.sampling.bean.AppFiles;
import com.cdv.sampling.constants.Constants;
import com.cdv.sampling.image.ImageLoaderUtils;
import com.cdv.sampling.rxandroid.CommonSubscriber;
import com.cdv.sampling.utils.ListUtils;
import com.cdv.sampling.utils.MD5Utils;
import com.cdv.sampling.utils.io.FileUtils;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.loader.ImageLoader;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.download.ImageDownloader;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class AttachContainerView extends LinearLayout implements View.OnClickListener {

    private static final int IMAGE_PICKER = 0x1001;

    ImageView ivAddAttach;
    LinearLayout panelAttachArea;

    private ArrayList<String> mAttachFileList = new ArrayList<>();

    public AttachContainerView(Context context) {
        super(context);
        initView(context);
    }

    public AttachContainerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public AttachContainerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        ImagePicker imagePicker = ImagePicker.getInstance();
//        imagePicker.setImageLoader(new ImageLoader() {
//
//            @Override
//            public void displayImage(Activity activity, String path, ImageView imageView, int width, int height) {
//
//            }
//
//            @Override
//            public void clearMemoryCache() {
//
//            }
//        });
        imagePicker.setImageLoader(new ImageLoader() {
            @Override
            public void displayImage(Activity activity, String path, ImageView imageView, int width, int height) {
                com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(ImageDownloader.Scheme.FILE.wrap(path), imageView, new ImageSize(width, height));
            }

            @Override
            public void displayImagePreview(Activity activity, String path, ImageView imageView, int width, int height) {

            }

            @Override
            public void clearMemoryCache() {

            }
        });
        //设置图片加载器
        imagePicker.setShowCamera(true);  //显示拍照按钮
        imagePicker.setCrop(false);        //允许裁剪（单选才有效）
        imagePicker.setSaveRectangle(false); //是否按矩形区域保存
        imagePicker.setSelectLimit(9);    //选中数量限制
        imagePicker.setOutPutX(1400);//保存文件的宽度。单位像素
        imagePicker.setOutPutY(1400);//保存文件的高度。单位像素
        this.setOrientation(VERTICAL);
        LayoutInflater.from(context).inflate(R.layout.view_attach_container, this, true);
        ivAddAttach = (ImageView) findViewById(R.id.iv_add_attach);
        panelAttachArea = (LinearLayout) findViewById(R.id.panel_attach_area);
        ivAddAttach.setOnClickListener(this);
    }

    public void setFileIds(String fileIds) {
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
                        if (!FileUtils.isFileExist(appFile.getFilePath())) {
                            return;
                        }
                        mAttachFileList.add(appFile.getFilePath());
                    }

                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                        initImagePanel();
                    }
                });
    }

    @Override
    public void onClick(View view) {
        BaseActivity holdAct = (BaseActivity) getContext();
        Intent intent = new Intent(getContext(), ImageGridActivity.class);
        holdAct.startActivityForResult(intent, IMAGE_PICKER, new PreferenceManager.OnActivityResultListener() {
            @Override
            public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
                if (requestCode == IMAGE_PICKER && resultCode == ImagePicker.RESULT_CODE_ITEMS && data != null) {
                    ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                    if (ListUtils.isEmpty(images)) {
                        return true;
                    }
                    for (ImageItem item : images) {
                        mAttachFileList.add(item.path);
                    }
                    initImagePanel();
                    return true;
                }
                return false;
            }
        });
    }

    private void initImagePanel() {
        panelAttachArea.removeAllViews();
        for (int index = 0; index < mAttachFileList.size(); index ++) {

            View view = LayoutInflater.from(getContext()).inflate(R.layout.item_select_image, null);
            ImageView iv = (ImageView) view.findViewById(R.id.iv_attch);
            ImageView ivDelete = (ImageView) view.findViewById(R.id.iv_delete_attach);
            final int finalIndex = index;
            iv.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    getContext().startActivity(ImageShowActivity.getStartIntent(getContext(), mAttachFileList, finalIndex));
                }
            });
            ivDelete.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    mAttachFileList.remove(finalIndex);
                    initImagePanel();
                }
            });
            panelAttachArea.addView(view);
            ImageLoaderUtils.displayImageForIv(iv, ImageDownloader.Scheme.FILE.wrap(mAttachFileList.get(index)));
        }
    }

    public Observable<String> getSelectedFileIds(final String rootPath, final String sampleNo) {
        return Observable.just(mAttachFileList).map(new Func1<List<String>, String>() {
            @Override
            public String call(List<String> strings) {
                String fileIds = "";
                int i = 1;
                for (String filePath : mAttachFileList) {
                    if (!filePath.contains(rootPath)){
                        String newFilePath = rootPath + File.separator + sampleNo + "-" + i + "." + FileUtils.getFileExtension(filePath);
                        FileUtils.copyFile(filePath, newFilePath);
                        filePath = newFilePath;
                    }
                    AppFiles files = new AppFiles();
                    files.setCreateTime(new Date());
                    files.setFilePath(filePath);
                    files.setFileType(FileUtils.getFileExtension(filePath));
                    File file = new File(filePath);
                    files.setFileLength(String.valueOf(file.length()));
                    files.setMD5(MD5Utils.calculateMD5(file));
                    files.setFileSize(String.valueOf(file.length()));
                    files.setFileType(Constants.TYPE_FORM_IMAGE);
                    SamplingApplication.getDaoSession().getAppFilesDao().save(files);
                    fileIds = fileIds.concat(String.valueOf(files.getID()));
                    fileIds = fileIds.concat(Constants.FILE_ID_SEPARATOR);
                    i ++;
                }
                if (fileIds.length() > 0) {
                    fileIds = fileIds.substring(0, fileIds.length() - 1);
                }
                return fileIds;
            }
        }).subscribeOn(Schedulers.io());
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        ImageLoaderUtils.clearCache();
    }
}
