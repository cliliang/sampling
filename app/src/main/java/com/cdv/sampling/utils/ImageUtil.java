
package com.cdv.sampling.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.TextUtils;

import com.cdv.sampling.utils.io.FileUtils;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageUtil {
    public static boolean saveBitmap(Bitmap bitmap, String path, boolean recyle) {
        if (bitmap == null || TextUtils.isEmpty(path)) {
            return false;
        }

        BufferedOutputStream bos = null;
        try {
            FileUtils.createNewFile(path);
            FileOutputStream fos = new FileOutputStream(path);
            bos = new BufferedOutputStream(fos);
            bitmap.compress(Bitmap.CompressFormat.PNG, 80, bos);

            return true;
        } catch (FileNotFoundException e) {
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                }
            }
            if (recyle) {
                bitmap.recycle();
            }
        }
    }

    private final static String SHARE_IMAGE_DIR = "image";
    private final static String SHARE_IMAGE_NAME = "share";
    private final static String SHARE_IMAGE_EXTENSION = ".jpg";


    public static Bitmap getBitmapByUri(Context context, Uri uri) {
        if (uri == null) {
            return null;
        }

        Bitmap bitmap = null;
        try {
            InputStream input = context.getContentResolver().openInputStream(uri);
            bitmap = BitmapFactory.decodeStream(input);
            if (input != null) {
                input.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
