package com.cdv.sampling.adapter;

import android.support.v4.util.ArrayMap;
import android.util.SparseArray;

/**
 * Created by apple on 16/3/11.
 */
public class CommonDataItem {

    public static final int VIEW_TYPE_NONE = -1;
    private SparseArray<Object[]> dataArray;
    private final int layoutId;
    private int position;
    private Object tag;
    private ArrayMap<Object, Object> tagMap;
    private int viewType = VIEW_TYPE_NONE;
    public CommonDataItem(int layoutId){
        this.layoutId = layoutId;
        dataArray = new SparseArray<>();
    }

    public void bindView(int id, Object...objectArray){
        dataArray.put(id, objectArray);
    }

    public void addBind(int id, Object...objectArray){
        Object[] oldArr = dataArray.get(id);
        if (oldArr == null || oldArr.length == 0){
            dataArray.put(id, objectArray);
            return;
        }
        if(objectArray != null && objectArray.length > 0){
            Object[] newArray = new Object[objectArray.length + oldArr.length];
            System.arraycopy(oldArr, 0, newArray, 0, oldArr.length);
            System.arraycopy(objectArray, 0, newArray, oldArr.length, objectArray.length);
            dataArray.put(id, newArray);
        }
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public Object getTag() {
        return tag;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }

    public int getViewType() {
        return viewType;
    }

    public Object getTag(Object key){
        if (this.tagMap != null){
            return this.tagMap.get(key);
        }
        return null;
    }

    public void putTag(Object key, Object tag){
        if (this.tagMap == null){
            this.tagMap = new ArrayMap<>();
        }
        this.tagMap.put(key, tag);
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public SparseArray<Object[]> getDataArray() {
        return dataArray;
    }

    public int getLayoutId() {
        return layoutId;
    }
}
