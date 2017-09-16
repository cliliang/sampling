package com.cdv.sampling.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.TextView;

import com.cdv.sampling.image.ImageLoaderUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.download.ImageDownloader;

import java.util.List;

/**
 * Created by apple on 16/3/11.
 */
public class CommonAdapter extends BaseAdapter {

    private static final int DEFAULT_VIEW_TYPE_COUNT = 3;//ListView默认最多展示View的种类数。

    private int[] mLayoutArray;
    private List<CommonDataItem> mDataSource;
    private ViewHooker mViewHooker;
    private LayoutInflater mInflater;
    private Context mContext;
    private final DisplayImageOptions options;

    private int viewTypeCount = DEFAULT_VIEW_TYPE_COUNT;

    public CommonAdapter(Context context, List<CommonDataItem> mDataSource) {
        this(context, mDataSource, DEFAULT_VIEW_TYPE_COUNT, null, null);
    }

    public CommonAdapter(Context context, List<CommonDataItem> mDataSource, int... layoutIds) {
        this(context, mDataSource, DEFAULT_VIEW_TYPE_COUNT, null, layoutIds);
    }

    public CommonAdapter(Context context, List<CommonDataItem> mDataSource, DisplayImageOptions opts, int... layoutIds) {
        this(context, mDataSource, DEFAULT_VIEW_TYPE_COUNT, opts, layoutIds);
    }

    public CommonAdapter(Context context, List<CommonDataItem> mDataSource, int maxViewTypeCount, DisplayImageOptions opts, int... layoutIds) {
        if (layoutIds == null || layoutIds.length == 0) {
            mLayoutArray = new int[0];
            this.viewTypeCount = maxViewTypeCount;
        } else {
            this.mLayoutArray = layoutIds;
            this.viewTypeCount = mLayoutArray.length;
        }
        this.mDataSource = mDataSource;
        this.mContext = context;
        if (opts == null) {
            this.options = ImageLoaderUtils.defaultDisplayOpts;
        } else {
            this.options = opts;
        }
        this.mInflater = LayoutInflater.from(this.mContext);
    }

    @Override
    public int getCount() {
        return mDataSource == null ? 0 : mDataSource.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataSource.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return viewTypeCount;
    }

    @Override
    public int getItemViewType(int position) {
        if (mLayoutArray == null) {
            return 0;
        }
        CommonDataItem dataItem = mDataSource.get(position);
        if (dataItem.getViewType() == CommonDataItem.VIEW_TYPE_NONE) {
            for (int i = 0; i < mLayoutArray.length; i++) {
                if (dataItem.getLayoutId() == mLayoutArray[i]) {
                    dataItem.setViewType(i);
                    return dataItem.getViewType();
                }
            }
            int[] tempArr = mLayoutArray;
            mLayoutArray = new int[tempArr.length + 1];
            System.arraycopy(tempArr, 0, mLayoutArray, 0, tempArr.length);
            mLayoutArray[mLayoutArray.length - 1] = dataItem.getLayoutId();
            return mLayoutArray.length - 1;
        } else {
            return dataItem.getViewType();
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CommonDataItem dataItem = mDataSource.get(position);
        if (convertView == null) {
            convertView = mInflater.inflate(dataItem.getLayoutId(), parent, false);
        }
        CommonViewHolder viewHolder = (CommonViewHolder) convertView.getTag();
        if (viewHolder == null) {
            viewHolder = new CommonViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        SparseArray<Object[]> dataArray = dataItem.getDataArray();
        for (int i = 0; i < dataArray.size(); i++) {
            int viewId = dataArray.keyAt(i);
            Object[] data = dataArray.get(viewId);
            View view = viewHolder.findViewById(viewId);
            bindView(position, view, data);
        }
        return convertView;
    }

    private void bindView(int position, View view, Object[] dataArray) {
        if (dataArray == null || dataArray.length == 0) {
            if (mViewHooker == null || !mViewHooker.isHookSuccess(position, view, null)) {
                clearViewBind(view);
            }
            return;
        }
        for (Object object : dataArray) {
            if (mViewHooker != null && mViewHooker.isHookSuccess(position, view, object)) {
                continue;
            }
            if (object != null) {
                if (object instanceof View.OnTouchListener) {
                    view.setOnTouchListener((View.OnTouchListener) object);
                    continue;
                }
                if (object instanceof View.OnClickListener) {
                    view.setOnClickListener((View.OnClickListener) object);
                    continue;
                }
                if (object instanceof View.OnLongClickListener) {
                    view.setOnLongClickListener((View.OnLongClickListener) object);
                    continue;
                }
                if (object instanceof Boolean) {
                    if (view instanceof Checkable) {
                        ((Checkable) view).setChecked((Boolean) object);
                        continue;
                    }
                    Boolean isVisible = (Boolean) object;
                    if (isVisible) {
                        view.setVisibility(View.VISIBLE);
                    } else {
                        view.setVisibility(View.GONE);
                    }
                    continue;
                }
            }
            if (view instanceof Checkable) {
                throw new IllegalStateException(object.getClass().getName() +
                        " should be bind to a Boolean, not a " + object.getClass());
            } else if (view instanceof TextView) {
                if (object == null) {
                    ((TextView) view).setText("");
                } else if (object instanceof CharSequence) {
                    ((TextView) view).setText((CharSequence) object);
                } else if (object instanceof Integer) {
                    int resId = (Integer) object;
                    try {
                        String type = mContext.getResources().getResourceTypeName(resId);
                        if (type.equals("drawable")) {
                            view.setBackgroundResource(resId);
                        } else if (type.equals("color")) {
                            ((TextView) view).setTextColor(mContext.getResources().getColor(resId));
                        } else if (type.equals("string")) {
                            ((TextView) view).setText(resId);
                        } else {
                            ((TextView) view).setText(object.toString());
                        }
                    } catch (Resources.NotFoundException e) {
                        ((TextView) view).setText(object.toString());
                    }
                } else {
                    ((TextView) view).setText(object.toString());
                }
            } else if (view instanceof ImageView) {
                bindImageView((ImageView) view, object);
            } else {
                throw new IllegalStateException(view.getClass().getName() + " can not bind with value(" + object + ") to " + view + " by " + CommonAdapter.this.getClass().getName());
            }
        }
    }

    private void bindImageView(ImageView iv, Object data) {
        if (data == null) {
            iv.setImageDrawable(null);
            return;
        }
        if (data instanceof Integer) {
            int d = (Integer) data;
            iv.setImageResource(d);
        } else if (data instanceof Bitmap) {
            iv.setImageBitmap((Bitmap) data);
        } else if (data instanceof String) {
            String str = (String) data;
            if (str.startsWith("http://") || str.startsWith("https://")) {
                ImageLoaderUtils.displayImageForIv(iv, str, options);
            } else {
                ImageLoaderUtils.displayImageForIv(iv, ImageDownloader.Scheme.FILE.wrap(str), options);
            }

        } else if (data instanceof Uri) {
            ImageLoaderUtils.displayImageForIv(iv, data.toString(), options);
        } else {
            return;
        }
    }

    private void clearViewBind(View view) {
        if (view instanceof TextView) {
            ((TextView) view).setText(null);
        } else if (view instanceof ImageView) {
            ImageView iv = (ImageView) view;
            iv.setImageBitmap(null);
        }
    }

    @Override
    public void notifyDataSetChanged() {

        super.notifyDataSetChanged();

    }

    public void setViewHooker(ViewHooker hooker) {
        this.mViewHooker = hooker;
    }

    public interface ViewHooker {

        boolean isHookSuccess(int position, View view, Object viewData);
    }
}
