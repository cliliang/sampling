package com.cdv.sampling.utils.io;

import android.app.Activity;
import android.widget.ImageView;

import com.lzy.imagepicker.loader.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.download.ImageDownloader;

/**
 * desc:
 * Created by:chenliliang
 * Created on:2017/9/19.
 */

public class PicImageLoader implements ImageLoader {
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
}
