/**   
* @Title: WhistleHeadDisplayer.java 
* @Package com.ruijie.anan.util.imageloader 
* @Description: TODO(用一句话描述该文件做什么) 
* @author xuyingjian@ruijie.com.cn   
* @date 2015年1月19日 上午10:21:50 
*/
package com.cdv.sampling.image;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;

import com.nostra13.universalimageloader.core.assist.LoadedFrom;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

/**    
 * @Description: 圆角处理
 * @date 2015年1月19日 上午10:21:50
 * @version 3.10  
 */
public class HeadDisplayer implements BitmapDisplayer {
	
	private boolean isOnline = true;
	private int radius;
	private int width, height;
	private String tag;//根据tag类型来进行渲染，默认为Null，为null时根据其它进行渲染
	
	/**
	 * 默认为头像 圆形处理
	 */
	public HeadDisplayer() {
		this.radius = -1;
	}
	
	/**
	 * 仅设置图片的圆角
	 * @param radius
	 */
	public HeadDisplayer(int radius) {
		this.radius = radius;
	}
	
	public HeadDisplayer(boolean isOnline, int radius) {
		this.isOnline = isOnline;
		this.radius = radius;
	}
	
	/**
	 * width和height没有实际意义，仅为逻辑需要存储
	 * @param isOnline
	 * @param radius
	 * @param width
	 * @param height
	 */
	protected HeadDisplayer(boolean isOnline, int radius, int width, int height) {
		this.isOnline = isOnline;
		this.radius = radius;
		this.width = width;
		this.height = height;
	}
	
	/**
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @param width the width to set
	 */
	public void setWidth(int width) {
		this.width = width;
	}

	/**
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * @param height the height to set
	 */
	public void setHeight(int height) {
		this.height = height;
	}

	/**
	 * @return the isOnline
	 */
	public boolean isOnline() {
		return isOnline;
	}

	/**
	 * @param isOnline the isOnline to set
	 */
	public void setOnline(boolean isOnline) {
		this.isOnline = isOnline;
	}

	/**
	 * @return the radius
	 */
	public int getRadius() {
		return radius;
	}

	/**
	 * @param radius =-1时为圆形处理
	 */
	public void setRadius(int radius) {
		this.radius = radius;
	}

	/**
	 * @return the tag
	 */
	public String getTag() {
		return tag;
	}

	/**
	 * @param tag the tag to set
	 */
	public void setTag(String tag) {
		this.tag = tag;
	}

	@Override
	public void display(Bitmap bitmap, ImageAware imageAware,
						LoadedFrom loadedFrom) {
		if (!(imageAware instanceof ImageViewAware)) {
			throw new IllegalArgumentException("ImageAware should wrap ImageView. ImageViewAware is expected.");
		}
		if(tag != null){
			boolean flag = tag != null && (tag.equals(ImageLoaderUtils.IMAGE_CONTENT_PATH) || tag.equals(ImageLoaderUtils.IMAGE_BENEFIT_PATH) || tag.equals(ImageLoaderUtils.IMAGE_GAME_PATH)
					|| tag.equals(ImageLoaderUtils.IMAGE_ZONE_PATH));
			if(!flag){
				imageAware.setImageDrawable(new RoundedDrawable(bitmap, radius == -1 ?  imageAware.getWidth() / 2 : radius, isOnline));
			}else{
				imageAware.setImageBitmap(bitmap);
			}
		}else{
			imageAware.setImageDrawable(new RoundedDrawable(bitmap, radius == -1 ?  imageAware.getWidth() / 2 : radius, isOnline));
		}
	}

	public static class RoundedDrawable extends Drawable {

		protected final float cornerRadius;

		protected final RectF mRect = new RectF(),
				mBitmapRect;
		protected final BitmapShader bitmapShader;
		protected final Paint paint;
		protected final boolean isOnline;

		public RoundedDrawable(Bitmap bitmap, int cornerRadius, boolean isOnline) {
			this.cornerRadius = cornerRadius;
			this.isOnline = isOnline;
			
			bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
			mBitmapRect = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
			
			paint = new Paint();
			paint.setAntiAlias(true);
			paint.setShader(bitmapShader);
			if(!isOnline){
				ColorMatrix cm = new ColorMatrix();
				cm.setSaturation(0);
				ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
				paint.setColorFilter(f);
			}
		}

		@Override
		protected void onBoundsChange(Rect bounds) {
			super.onBoundsChange(bounds);
			mRect.set(0, 0, bounds.width(), bounds.height());
			
			// Resize the original bitmap to fit the new bound
			Matrix shaderMatrix = new Matrix();
			shaderMatrix.setRectToRect(mBitmapRect, mRect, Matrix.ScaleToFit.FILL);
			bitmapShader.setLocalMatrix(shaderMatrix);
			
		}

		@Override
		public void draw(Canvas canvas) {
			canvas.drawRoundRect(mRect, cornerRadius, cornerRadius, paint);
		}

		@Override
		public int getOpacity() {
			return PixelFormat.TRANSLUCENT;
		}

		@Override
		public void setAlpha(int alpha) {
			paint.setAlpha(alpha);
		}

		@Override
		public void setColorFilter(ColorFilter cf) {
			paint.setColorFilter(cf);
		}
	}
}
