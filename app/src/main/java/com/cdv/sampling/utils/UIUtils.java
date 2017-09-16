/**   
 * @Title: UIUtils.java 
 * @Package com.ruijie.anan.util 
 * @Description: TODO(用一句话描述该文件做什么) 
 * @author xuyingjian@ruijie.com.cn   
 * @date 2014年8月22日 上午11:44:02 
 */
package com.cdv.sampling.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.TextView;

/**
 * @Description:关于UI的一些工具类
 * @author xuyingjian@ruijie.com.cn
 * @date 2014年8月22日 上午11:44:02
 * @version 2.30
 */
public class UIUtils {

	/**
	 * 
	 * @Description: 转换dp到pixel
	 * @param dp
	 * @param context
	 * @return
	 */
	public static int convertDpToPixel(float dp, Context context) {
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		int px = (int)(dp * (metrics.densityDpi / 160f));
		return px;
	}

	/**
	 * 
	 * @Description:转换像素到dp
	 * @param px
	 * @param context
	 * @return
	 */
	public static float convertPixelsToDp(float px, Context context) {
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		float dp = px / (metrics.densityDpi / 160f);
		return dp;
	}

	/**
	 * 
	 * @Description: 获取屏幕的宽高
	 * @param activity
	 * @return Point.x为屏幕宽，Point.y为屏幕高
	 */
	public static Point getScreenSize(Activity activity) {
		DisplayMetrics metrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		Point size = new Point();
		size.y = metrics.heightPixels;
		size.x = metrics.widthPixels;
		return size;
	}

	/**
	 * 
	 * @Description: View转换为Bitmap 
	 * @param view
	 * @return
	 */
	public static Bitmap convertViewToBitmap(View view) {
		view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
				MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
		view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
		view.buildDrawingCache();
		Bitmap bitmap = view.getDrawingCache();

		return bitmap;
	}
	
	public static void setTextTruncate(final TextView tv, final int lines, final CharSequence text, final TruncateAt at){
		tv.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			
			@Override
			public void onGlobalLayout() {
				int width = tv.getWidth();
				tv.setText(TextUtils.ellipsize(text, tv.getPaint(), width * lines, at));
				if(Build.VERSION.SDK_INT < 16){
					tv.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				}else{
					removeGlobalOnLayoutListener(tv, this);
				}
			}
		});
	}
	
	@TargetApi(16)
	private static void removeGlobalOnLayoutListener(View view, OnGlobalLayoutListener listener){
		view.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
	}
	
	/**
	 * 
	 * @Description: 判断坐标是否在view内 .x,y的坐标应为相对于屏幕的坐标
	 * @param x 相对于屏幕坐标x
	 * @param y 相对于屏幕坐标y
	 * @param view
	 * @return
	 */
	public static boolean isPointInsideView(float x, float y, View view){
	    int location[] = new int[2];
	    view.getLocationOnScreen(location);
	    int viewX = location[0];
	    int viewY = location[1];

	    //point is inside view bounds
	    if(( x > viewX && x < (viewX + view.getWidth())) &&
	            ( y > viewY && y < (viewY + view.getHeight()))){
	        return true;
	    } else {
	        return false;
	    }
	}
	
	public static Bitmap drawableToBitmap (Drawable drawable) {
	    if (drawable instanceof BitmapDrawable) {
	        return ((BitmapDrawable)drawable).getBitmap();
	    }

	    Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Config.ARGB_8888);
	    Canvas canvas = new Canvas(bitmap);
	    drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
	    drawable.draw(canvas);

	    return bitmap;
	}
	
	public static SpannableStringBuilder convertDrawable2Span(final Context context, int resId, final int dpSize){
		ImageSpan span = new ImageSpan(context, resId){
			
			public Drawable getDrawable() {
				Drawable drawable = super.getDrawable();
				int size = UIUtils.convertDpToPixel(dpSize, context);
				drawable.setBounds(0, 0, size, size);
				return drawable;
			};
		};
		SpannableStringBuilder sb = new SpannableStringBuilder("  ");
		sb.setSpan(span, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return sb;
	}
}
