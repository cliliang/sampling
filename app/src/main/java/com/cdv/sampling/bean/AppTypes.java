package com.cdv.sampling.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;

import java.io.Serializable;

@Entity(indexes = {
        @Index(value = "localId DESC", unique = true)
})
public class AppTypes implements Serializable {

    public static final long serialVersionUID = 147783938912L;

    public static final String TYPE_YANGPIN_LEIXING = "1";
    public static final String TYPE_YANGPIN_MING = "2";
    public static final String TYPE_SHOUYAO_JINHUO_FANGSHI = "3";
    public static final String TYPE_ZHILIANG_JINHUO_FANGSHI = "4";
    public static final String TYPE_SHOUYAO_SAMPLE_TYPE = "5";
    public static final String TYPE_ZHILIANG_SAMPLE_TYPE = "6";


    @Id(autoincrement = true)
    private Long localId;
    private String ID;
    private String ValueType;
    private String ValueName;
    private int Order;
    private String Description;
    private int IsDeleted;
@Generated(hash = 1149869678)
public AppTypes(Long localId, String ID, String ValueType, String ValueName,
        int Order, String Description, int IsDeleted) {
    this.localId = localId;
    this.ID = ID;
    this.ValueType = ValueType;
    this.ValueName = ValueName;
    this.Order = Order;
    this.Description = Description;
    this.IsDeleted = IsDeleted;
}
@Generated(hash = 433350970)
public AppTypes() {
}
public Long getLocalId() {
    return this.localId;
}
public void setLocalId(Long localId) {
    this.localId = localId;
}
public String getID() {
    return this.ID;
}
public void setID(String ID) {
    this.ID = ID;
}
public String getValueType() {
    return this.ValueType;
}
public void setValueType(String ValueType) {
    this.ValueType = ValueType;
}
public String getValueName() {
    return this.ValueName;
}
public void setValueName(String ValueName) {
    this.ValueName = ValueName;
}
public int getOrder() {
    return this.Order;
}
public void setOrder(int Order) {
    this.Order = Order;
}
public String getDescription() {
    return this.Description;
}
public void setDescription(String Description) {
    this.Description = Description;
}
public int getIsDeleted() {
    return this.IsDeleted;
}
public void setIsDeleted(int IsDeleted) {
    this.IsDeleted = IsDeleted;
}
}
