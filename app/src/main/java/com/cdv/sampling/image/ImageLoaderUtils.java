/**   
 * @Title: ImageLoaderUtils.java 
 * @Package com.ruijie.anan.util 
 * @Description: TODO(用一句话描述该文件做什么) 
 * @author xuyingjian@ruijie.com.cn   
 * @date 2015年1月12日 下午3:33:52 
 */
package com.cdv.sampling.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.support.v4.BuildConfig;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;

import com.cdv.sampling.R;
import com.cdv.sampling.SamplingApplication;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.DisplayImageOptions.Builder;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.ImageDownloader.Scheme;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import java.io.File;


public class ImageLoaderUtils {

	public static final int BITMAP_VENDOR_MEMORY_CACHE_SIZE = (int) (Runtime.getRuntime().maxMemory() / 1024) / 10;

	public static final String URI_AND_SIZE_SEPARATOR = "_";

	public static final String IMAGE_HEAD_PATH = "IMAGE_HEAD_PATH";
	public static final String IMAGE_CONTENT_PATH = "IMAGE_CONTENT_PATH";
	public static final String IMAGE_GAME_PATH = "IMAGE_GAME_PATH";
	public static final String IMAGE_BENEFIT_PATH = "IMAGE_BENEFIT_PATH";
	public static final String IMAGE_ZONE_PATH = "IMAGE_ZONE_PATH";

	public static final String PRESENCE = "presence";
	public static final String PRESENCE_OL = "ol";
	public static final String PRESENCE_OFF = "off";

	private static final int OPTIONS_NONE = 0x0;
	private static final int OPTIONS_SCALE_UP = 0x1;

	public static void clearCache() {
		ImageLoader.getInstance().clearMemoryCache();
		ImageLoader.getInstance().clearDiskCache();
	}

	public enum ImageType {
		ImageTypeDefault
	}

	public static void initImageLoader(Context context) {
		if (!ImageLoader.getInstance().isInited()) {
			ImageLoaderConfiguration config = null;
			if (BuildConfig.DEBUG) {
				config = new ImageLoaderConfiguration.Builder(context)
						.threadPriority(Thread.NORM_PRIORITY - 2)
						.diskCacheSize(500 * 1024 * 1024).writeDebugLogs()
						.imageDecoder(new ImageDecoder(true))
						.diskCacheFileNameGenerator(new Md5FileNameGenerator())
						.tasksProcessingOrder(QueueProcessingType.LIFO).build();
			} else {
				config = new ImageLoaderConfiguration.Builder(context)
						.threadPriority(Thread.NORM_PRIORITY - 2)
						.diskCacheSize(500 * 1024 * 1024)
						.memoryCache(new WeakMemoryCache())
						.memoryCacheSize(BITMAP_VENDOR_MEMORY_CACHE_SIZE)
						.imageDecoder(new ImageDecoder(false))
						.diskCacheFileNameGenerator(new Md5FileNameGenerator())
						.tasksProcessingOrder(QueueProcessingType.LIFO).build();
			}
			ImageLoader.getInstance().init(config);
		}
	}

	public static void resume() {
		initImageLoader(SamplingApplication.getInstance());
		ImageLoader.getInstance().resume();
	}

	public static void pause() {
		initImageLoader(SamplingApplication.getInstance());
		ImageLoader.getInstance().pause();
	}

	public static void stop() {
		if (ImageLoader.getInstance().isInited()) {
			ImageLoader.getInstance().stop();
		}
	}

	public static void destroy() {
		if (ImageLoader.getInstance().isInited()) {
			ImageLoader.getInstance().destroy();
		}
	}

	/**
	 * 
	 * @Description: ListView滚动的时候停止加载图片
	 * @param listView
	 * @param listener
	 *            可为null,不为null时，将会回调该listener
	 */
	public static void pauseOnScrollForLv(AbsListView listView,
			OnScrollListener listener) {
		if (listView == null) {
			return;
		}
		listView.setOnScrollListener(new PauseOnScrollListener(ImageLoader
				.getInstance(), true, true, listener));
	}

	private static final HeadDisplayer circleDisplayer = new HeadDisplayer(
			-1);

	public static void displayImageForIv(ImageView iv, String url) {
		ImageLoader.getInstance().displayImage(url, iv, defaultDisplayOpts);
	}

	public static void displayImageForIv(ImageView iv, String url,
										 DisplayImageOptions opts) {
		displayImageForIv(iv, url, opts, null);
	}

	public static void displayImageForIv(ImageView iv, String url,
										 DisplayImageOptions opts, ImageLoadingListener listener) {
		ImageLoader.getInstance().displayImage(url, iv, opts, listener);
	}

	public static void displayImageForIv(ImageView iv, String url,
										 ImageDisplayOpts opts) {
		DisplayImageOptions options = opts.toImageLoaderOpts();
		ImageLoader.getInstance().displayImage(url, iv, options);
	}

	/**
	 * 
	 * @Description: 根据不同类型展示图片
	 * @param iv
	 * @param url
	 * @param type
	 */
	public static void displayImageForIv(ImageView iv, String url,
										 ImageType type, ImageLoadingListener listener) {
		DisplayImageOptions opts = null;
		switch (type) {
		case ImageTypeDefault:
			opts = defaultDisplayOpts;
			break;
		default:
			opts = defaultDisplayOpts;
			break;
		}
		ImageLoader.getInstance().displayImage(url, iv, opts, listener);
	}

	/**
	 * 
	 * @Description: 根据不同类型展示图片
	 * @param iv
	 * @param url
	 * @param type
	 */
	public static void displayImageForIv(ImageView iv, String url,
										 ImageType type) {
		displayImageForIv(iv, url, type, null);
	}

	public static File getImageInDiskCache(String imageUrl){
		return ImageLoader.getInstance().getDiskCache().get(imageUrl);
	}

	public static BitmapDrawable changeDrawableToExactSize(int resId, int size,
														   int radius) {
		Bitmap bmp = loadDrawableToExactSize(resId, size, size, radius, radius);
		if (bmp != null) {
			return new BitmapDrawable(SamplingApplication.getInstance().getResources(), bmp);
		}
		return null;
	}

	public static BitmapDrawable changeDrawableToExactSize(int resId,
														   int width, int height, int radiusX, int radiusY) {
		Bitmap bmp = loadDrawableToExactSize(resId, width, height, radiusX,
				radiusY);
		if (bmp != null) {
			return new BitmapDrawable(SamplingApplication.getInstance().getResources(), bmp);
		}
		return null;
	}

	/**
	 * 
	 * @Description: 转换Drawable为指定大小和圆角的Bitmap
	 * @param resId
	 * @param size
	 *            宽高均为size
	 * @param radius
	 * @return
	 */
	public static Bitmap loadDrawableToExactSize(int resId, int size, int radius) {
		return loadDrawableToExactSize(resId, size, size, radius, radius);
	}
	/**
	 * @Description:加载图片
	 * @param url 
	 * @param l
	 */
	public static void loadImage(String url, int width, int height, ImageLoadingListener l) {
		ImageLoader.getInstance().loadImage(url, new ImageSize(width, height), l);
	}

	/**
	 * 
	 * @Description: 转换Drawable为指定大小和圆角的Bitmap
	 * @param resId
	 * @param width
	 * @param height
	 * @param radiusX
	 * @param radiusY
	 * @return
	 */
	public static Bitmap loadDrawableToExactSize(int resId, int width,
												 int height, int radiusX, int radiusY) {
		String key = Scheme.DRAWABLE.wrap(String.valueOf(resId)
				+ URI_AND_SIZE_SEPARATOR + width + URI_AND_SIZE_SEPARATOR
				+ height + URI_AND_SIZE_SEPARATOR + radiusX
				+ URI_AND_SIZE_SEPARATOR + radiusY);
		Bitmap bmp = ImageLoader.getInstance().getMemoryCache().get(key);
		if (bmp == null) {
			bmp = BitmapFactory.decodeResource(SamplingApplication.getInstance()
					.getResources(), resId);
			if (bmp == null) {
				return null;
			}

			bmp = changeBitmapToExactSize(bmp, width, height, radiusX, radiusY);
			ImageLoader.getInstance().getMemoryCache().put(key, bmp);
			return bmp;
		}
		return bmp;
	}

	public static Bitmap changeBitmapToExactSize(Bitmap bmp, int width,
												 int height, int radiusX, int radiusY) {
		return changeBitmapToExactSizeAndGray(bmp, width, height, radiusX,
				radiusY, true);
	}

	public static Bitmap changeBitmapToExactSizeAndGray(Bitmap bmp, int width,
														int height, int radiusX, int radiusY, boolean isGray) {
		if (bmp == null) {
			return null;
		}

		float scale = 1;
		if (width > 0 && height > 0) {
			if (bmp.getWidth() < bmp.getHeight()) {
				scale = width / (float) bmp.getWidth();
			} else {
				scale = height / (float) bmp.getHeight();
			}
		} else {
			width = bmp.getWidth();
			height = bmp.getHeight();
		}
		Matrix matrix = new Matrix();
		matrix.setScale(scale, scale);
		if (radiusX < 0) {
			radiusX = width / 2;
		}
		if (radiusY < 0) {
			radiusX = height / 2;
		}
		Bitmap thumbnail = transform(matrix, bmp, width, height,
				OPTIONS_SCALE_UP | ThumbnailUtils.OPTIONS_RECYCLE_INPUT,
				radiusX, radiusY, isGray);
		return thumbnail;
	}

	/**
	 * Transform source Bitmap to targeted width and height.
	 */
	private static Bitmap transform(Matrix scaler, Bitmap source,
									int targetWidth, int targetHeight, int options, int radiusX,
									int radiusY, boolean isGray) {
		boolean scaleUp = (options & OPTIONS_SCALE_UP) != 0;
		boolean recycle = (options & ThumbnailUtils.OPTIONS_RECYCLE_INPUT) != 0;

		int deltaX = source.getWidth() - targetWidth;
		int deltaY = source.getHeight() - targetHeight;
		if (!scaleUp && (deltaX < 0 || deltaY < 0)) {
			/*
			 * In this case the bitmap is smaller, at least in one dimension,
			 * than the target. Transform it by placing as much of the image as
			 * possible into the target and leaving the top/bottom or left/right
			 * (or both) black.
			 */
			Bitmap b2 = Bitmap.createBitmap(targetWidth, targetHeight,
					Bitmap.Config.ARGB_8888);
			Canvas c = new Canvas(b2);

			int deltaXHalf = Math.max(0, deltaX / 2);
			int deltaYHalf = Math.max(0, deltaY / 2);
			Rect src = new Rect(deltaXHalf, deltaYHalf, deltaXHalf
					+ Math.min(targetWidth, source.getWidth()), deltaYHalf
					+ Math.min(targetHeight, source.getHeight()));
			int dstX = (targetWidth - src.width()) / 2;
			int dstY = (targetHeight - src.height()) / 2;
			Paint paint = new Paint();
			paint.setAntiAlias(true);
			paint.setFilterBitmap(true);
			Rect dst = new Rect(dstX, dstY, targetWidth - dstX, targetHeight
					- dstY);
			RectF rectF = new RectF(dst);
			c.drawRoundRect(rectF, radiusX, radiusY, paint);
			PorterDuff.Mode mode = PorterDuff.Mode.SRC_IN;
			PorterDuffXfermode porterDuffXfermode = new PorterDuffXfermode(mode);
			paint.setXfermode(porterDuffXfermode);
			if (isGray) {
				ColorMatrix cm = new ColorMatrix();
				cm.setSaturation(0);
				ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
				paint.setColorFilter(f);
			}
			c.drawBitmap(source, src, dst, paint);
			if (recycle) {
				source.recycle();
			}
			c.setBitmap(null);
			return b2;
		}
		float bitmapWidthF = source.getWidth();
		float bitmapHeightF = source.getHeight();

		float bitmapAspect = bitmapWidthF / bitmapHeightF;
		float viewAspect = (float) targetWidth / targetHeight;

		if (bitmapAspect > viewAspect) {
			float scale = targetHeight / bitmapHeightF;
			if (scale < .9F || scale > 1F) {
				scaler.setScale(scale, scale);
			} else {
				scaler = null;
			}
		} else {
			float scale = targetWidth / bitmapWidthF;
			if (scale < .9F || scale > 1F) {
				scaler.setScale(scale, scale);
			} else {
				scaler = null;
			}
		}

		Bitmap b1;
		if (scaler != null) {
			// this is used for minithumb and crop, so we want to filter here.
			b1 = Bitmap.createBitmap(source, 0, 0, source.getWidth(),
					source.getHeight(), scaler, true);
		} else {
			b1 = source;
		}

		if (recycle && b1 != source) {
			source.recycle();
		}

		int dx1 = Math.max(0, b1.getWidth() - targetWidth);
		int dy1 = Math.max(0, b1.getHeight() - targetHeight);

		Bitmap b2 = Bitmap.createBitmap(b1, dx1 / 2, dy1 / 2, targetWidth,
				targetHeight);
		Bitmap bitmapRounded = Bitmap.createBitmap(targetWidth, targetHeight,
				b2.getConfig());
		Canvas canvas = new Canvas(bitmapRounded);
		Paint paint = new Paint();
		if (isGray) {
			ColorMatrix cm = new ColorMatrix();
			cm.setSaturation(0);
			ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
			paint.setColorFilter(f);
		}
		paint.setAntiAlias(true);
		paint.setShader(new BitmapShader(b2, Shader.TileMode.CLAMP,
				Shader.TileMode.CLAMP));
		canvas.drawRoundRect(
				(new RectF(0.0f, 0.0f, b2.getWidth(), b2.getHeight())),
				radiusX, radiusY, paint);
		if (b2 != b1) {
			if (recycle || b1 != source) {
				b1.recycle();
			}
		}
		b2.recycle();

		return bitmapRounded;
	}

	public static final DisplayImageOptions defaultDisplayOpts = new Builder()
			.showImageForEmptyUri(R.drawable.pic_page_picture)
			.showImageOnLoading(R.drawable.pic_page_picture)
			.cacheInMemory(true)
			.considerExifParams(true)
			.resetViewBeforeLoading(true)
			.showImageOnFail(R.drawable.pic_page_picture).cacheOnDisk(true)
			.cacheInMemory(true).build();
}
