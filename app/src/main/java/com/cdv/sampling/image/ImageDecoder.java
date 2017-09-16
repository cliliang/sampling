/**   
* @Title: WSImageDecoder.java 
* @Package com.ruijie.anan.util.imageloader 
* @Description: TODO(用一句话描述该文件做什么) 
* @author xuyingjian@ruijie.com.cn   
* @date 2015年1月27日 下午12:01:14 
*/
package com.cdv.sampling.image;

import android.graphics.Bitmap;
import android.graphics.Matrix;

import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.ViewScaleType;
import com.nostra13.universalimageloader.core.decode.BaseImageDecoder;
import com.nostra13.universalimageloader.core.decode.ImageDecodingInfo;
import com.nostra13.universalimageloader.utils.ImageSizeUtils;
import com.nostra13.universalimageloader.utils.L;

/**    
 * @Description: 因为RoundDrawable不支持centerCrop的原因，所以在ImageDecoder的时候把图片裁切为和ImageView等比例的大小
 * @date 2015年1月27日 下午12:01:14
 */
public class ImageDecoder extends BaseImageDecoder {
	protected static final String IMAGE_CREATE_LOG = "Image: %1$s create with width %2$d and height %3$d";
	
	/**
	 * @param loggingEnabled
	 */
	public ImageDecoder(boolean loggingEnabled) {
		super(loggingEnabled);
	}


	protected Bitmap considerExactScaleAndOrientatiton(Bitmap subsampledBitmap, ImageDecodingInfo decodingInfo,
													   int rotation, boolean flipHorizontal) {
		Matrix m = new Matrix();
		// Scale to exact size if need
		ImageScaleType scaleType = decodingInfo.getImageScaleType();
		if (scaleType == ImageScaleType.EXACTLY || scaleType == ImageScaleType.EXACTLY_STRETCHED) {
			ImageSize srcSize = new ImageSize(subsampledBitmap.getWidth(), subsampledBitmap.getHeight(), rotation);
			float scale = ImageSizeUtils.computeImageScale(srcSize, decodingInfo.getTargetSize(), decodingInfo
					.getViewScaleType(), scaleType == ImageScaleType.EXACTLY_STRETCHED);
			if (Float.compare(scale, 1f) != 0) {
				m.setScale(scale, scale);

				if (loggingEnabled) {
					L.d(LOG_SCALE_IMAGE, srcSize, srcSize.scale(scale), scale, decodingInfo.getImageKey());
				}
			}
		}
		// Flip bitmap if need
		if (flipHorizontal) {
			m.postScale(-1, 1);

			if (loggingEnabled) L.d(LOG_FLIP_IMAGE, decodingInfo.getImageKey());
		}
		// Rotate bitmap if need
		if (rotation != 0) {
			m.postRotate(rotation);

			if (loggingEnabled) L.d(LOG_ROTATE_IMAGE, rotation, decodingInfo.getImageKey());
		}
		Bitmap finalBitmap = null;
		if(decodingInfo.getViewScaleType() == ViewScaleType.CROP){
			float ratio = ((float)decodingInfo.getTargetSize().getWidth()) / decodingInfo.getTargetSize().getHeight();
			if(subsampledBitmap.getWidth() / ((float)subsampledBitmap.getHeight()) > ratio){
				int width = (int)(subsampledBitmap.getHeight() * ratio);
				finalBitmap = Bitmap.createBitmap(subsampledBitmap, (subsampledBitmap.getWidth() - width) / 2, 0, width, subsampledBitmap
						.getHeight(), m, true);
			}else{
				int height = (int)(subsampledBitmap.getWidth() / ratio);
				finalBitmap = Bitmap.createBitmap(subsampledBitmap, 0, (subsampledBitmap.getHeight() - height) / 2,  subsampledBitmap.getWidth(), height, m, true);
			}
			if (loggingEnabled) L.d(IMAGE_CREATE_LOG, decodingInfo.getImageUri(), finalBitmap.getWidth(), finalBitmap.getHeight());
		}else{
			finalBitmap = Bitmap.createBitmap(subsampledBitmap, 0, 0, subsampledBitmap.getWidth(), subsampledBitmap
					.getHeight(), m, true);
		}
		if (finalBitmap != subsampledBitmap) {
			subsampledBitmap.recycle();
		}
		return finalBitmap;
	}
}
