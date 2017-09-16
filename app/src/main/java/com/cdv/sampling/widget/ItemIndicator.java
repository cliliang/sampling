package com.cdv.sampling.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cdv.sampling.R;


public class ItemIndicator extends RelativeLayout {

    private TextView tvShowText;
    private TextView tvShowContent;
    private ImageView ivShowIcon;
    private ImageView ivUnread;
    //private ImageView ivRightArrow;

    private Context mContext;

    private int mDividerType = 2;

    /**
     * @param context
     * @param attrs
     */
    public ItemIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView(attrs);
    }

    /**
     *
     */
    public ItemIndicator(Context context, AttributeSet attrs, int defaultStyle) {
        super(context, attrs, defaultStyle);
        mContext = context;
        initView(attrs);
    }

    private void initView(AttributeSet attrs) {
        //if (isInEditMode()) { return; }
        RelativeLayout layout = (RelativeLayout) LayoutInflater.from(mContext).inflate(R.layout.layout_item_indicator, this, true);
        tvShowText = (TextView) layout.findViewById(R.id.show_text);
        tvShowContent = (TextView) layout.findViewById(R.id.show_content);
        ivShowIcon = (ImageView) layout.findViewById(R.id.show_icon);
        ivUnread = (ImageView) layout.findViewById(R.id.iv_item_unread);
        ImageView ivArrow = (ImageView) layout.findViewById(R.id.arrow);

        View dividerTopView = layout.findViewById(R.id.divider_top);
        View dividerBottomView = layout.findViewById(R.id.divider_bottom);


        TypedArray type = mContext.getTheme().obtainStyledAttributes(attrs, R.styleable.item_indicator, 0, 0);

        tvShowText.setText(type.getString(R.styleable.item_indicator_item_showText));
        ivShowIcon.setImageResource(type.getResourceId(R.styleable.item_indicator_showIcon, 0));
        int arrowRes = type.getResourceId(R.styleable.item_indicator_accessIcon, 0);
        if (arrowRes != 0){
            ivArrow.setImageResource(arrowRes);
        }

        if (type.getColorStateList(R.styleable.item_indicator_showTextColor) != null) {
            tvShowText.setTextColor(type.getColorStateList(R.styleable.item_indicator_showTextColor));
        }

        mDividerType = type.getInteger(R.styleable.item_indicator_dividerType, 2);

        if (mDividerType == 0) {
            dividerTopView.setVisibility(View.GONE);
            dividerBottomView.setVisibility(View.GONE);
        } else if (mDividerType == 1) {
            dividerTopView.setVisibility(View.VISIBLE);
            dividerBottomView.setVisibility(View.GONE);
        } else if (mDividerType == 2) {
            dividerTopView.setVisibility(View.GONE);
            dividerBottomView.setVisibility(View.VISIBLE);
        } else {
            dividerTopView.setVisibility(View.VISIBLE);
            dividerBottomView.setVisibility(View.VISIBLE);
        }

        if (isInEditMode()) {
            return;
        }
    }

    public void setUnreadIconVisible(boolean isVisible) {
        if (isVisible) {
            ivUnread.setVisibility(View.VISIBLE);
        } else {
            ivUnread.setVisibility(View.GONE);
        }
    }

    public void setUnreadIcon(Bitmap bitmap) {
        if (bitmap != null) {
            ivUnread.setImageBitmap(bitmap);
        }
    }

    public void setContent(String content){
        tvShowContent.setText(content);
    }

    public void setShowText(String title) {
        this.tvShowText.setText(title);
    }

    public void setShowTextSize(int unit, int size) {
        this.tvShowText.setTextSize(unit, size);
    }

    public void setShowIcon(Bitmap bitmap) {
        this.ivShowIcon.setImageBitmap(bitmap);
    }

    public CharSequence getShowText() {
        return this.tvShowText.getText();
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


}
