package com.cdv.sampling.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cdv.sampling.R;
import com.cdv.sampling.utils.UIUtils;

/**
 * @Description: 左侧为标题，右侧为详情
 */
public class PreferenceRightDetailView extends RelativeLayout {
	
	private TextView titleTv;
	private TextView contentTv;
	private ImageView rightIV;
	private RelativeLayout container;//title content arrow的父组件
	private Context mContext;

	/**
	 * @param context
	 * @param attrs
	 */
	public PreferenceRightDetailView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		initView(attrs);
	}
	
	/**
	 * 
	 */
	public PreferenceRightDetailView(Context context, AttributeSet attrs, int defaultStyle) {
		super(context, attrs, defaultStyle);
		mContext = context;
		initView(attrs);
	}

	private void initView(AttributeSet attrs) {
		LayoutInflater.from(mContext).inflate(R.layout.preference_detail_item, this, true);
		titleTv = (TextView) findViewById(R.id.title);
		contentTv = (TextView) findViewById(R.id.content);
		rightIV = (ImageView) findViewById(R.id.arrow);
		TypedArray type = mContext.getTheme().obtainStyledAttributes(attrs, R.styleable.PreferenceRightDetailView, 0, 0);
		titleTv.setText(type.getString(R.styleable.PreferenceRightDetailView_show_title));
		contentTv.setText(type.getString(R.styleable.PreferenceRightDetailView_show_content));
		if(type.getColorStateList(R.styleable.PreferenceRightDetailView_show_titleColor) != null){
			titleTv.setTextColor(type.getColorStateList(R.styleable.PreferenceRightDetailView_show_titleColor));
		}
		Drawable drawable = type.getDrawable(R.styleable.PreferenceRightDetailView_ptd_background);
		if (drawable == null){
			setBackgroundResource(R.drawable.listitem_basebg);
		} else {
			setBackgroundDrawable(drawable);
		}
		if(type.getColorStateList(R.styleable.PreferenceRightDetailView_show_contentColor) != null){
			contentTv.setTextColor(type.getColorStateList(R.styleable.PreferenceRightDetailView_show_contentColor));
		}
		titleTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, type.getDimensionPixelSize(R.styleable.PreferenceRightDetailView_show_titleSize, dip2px(mContext, 16)));
		contentTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, type.getDimensionPixelSize(R.styleable.PreferenceRightDetailView_show_contentSize, dip2px(mContext, 14)));
		int style = type.getInteger(R.styleable.PreferenceRightDetailView_show_accessStyle, 1);
		rightIV.setImageResource(type.getResourceId(R.styleable.PreferenceRightDetailView_show_accessImage, R.drawable.icon_unfold));
		if(style != 1){
			rightIV.setVisibility(View.INVISIBLE);
		}else{
			rightIV.setVisibility(View.VISIBLE);
		}
		int location = type.getInteger(R.styleable.PreferenceRightDetailView_show_divider_location, 2);
		int dividerHeight = UIUtils.convertDpToPixel(1, getContext());
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, dividerHeight);
		if(location == 0){
			//do nothing
		}else if(location == 1){
			View divider = LayoutInflater.from(mContext).inflate(R.layout.divider_hor, null);
			params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			divider.setLayoutParams(params);
			addView(divider);
		}else if(location == 2){
			View divider = LayoutInflater.from(mContext).inflate(R.layout.divider_hor, null);
			params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			divider.setLayoutParams(params);
			addView(divider);
		}else{
			View divider = LayoutInflater.from(mContext).inflate(R.layout.divider_hor, null);
			params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			divider.setLayoutParams(params);
			addView(divider);
			View divider2 = LayoutInflater.from(mContext).inflate(R.layout.divider_hor, null);
			params = new LayoutParams(LayoutParams.MATCH_PARENT, dividerHeight);
			params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			divider2.setLayoutParams(params);
			addView(divider2);
		}
	}
	
	 public static int dip2px(Context context, float dpValue) {  
	     final float scale = context.getResources().getDisplayMetrics().density;  
	     return (int) (dpValue * scale + 0.5f);  
	 }  
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
	}
	
	public void setContent(String content){
		this.contentTv.setText(content);
	}
	
	public void setTitle(String title){
		this.titleTv.setText(title);
	}
	
	public void setContentSize(int unit, int size){
		this.contentTv.setTextSize(unit, size);
	}
	
	public void setTitleSize(int unit, int size){
		this.titleTv.setTextSize(unit, size);
	}
	
	public void setRightBitmap(Bitmap bitmap){
		this.rightIV.setImageBitmap(bitmap);
	}
	
	public CharSequence getContent(){
		return this.contentTv.getText(); 
	}

	public CharSequence getTitle(){
		return this.titleTv.getText(); 
	}
	
	public void setContentGravity(int gravity){
		this.contentTv.setGravity(gravity);
	}

	public void setTitleGravity(int gravity){
		this.titleTv.setGravity(gravity);
	}
}
