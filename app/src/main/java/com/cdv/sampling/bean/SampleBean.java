package com.cdv.sampling.bean;

import java.io.Serializable;

public class SampleBean implements Serializable{

    public static final String TABLE = "tb_sample_info";

    public static final String ID_ = "ID_";
    public static final String NO_ = "NO_";
    public static final String NAME_ = "NAME_";
    public static final String COUNT_ = "COUNT_";
    public static final String BASE_ = "BASE_";
    public static final String SOURCE_COMPANY = "SOURCE_COMPANY";
    public static final String SOURCE_TEL = "SOURCE_TEL";
    public static final String SOURCE_PERSON = "SOURCE_PERSON";
    public static final String SOURCE_TYPE = "SOURCE_TYPE";
    public static final String QUARANTINE_NO = "QUARANTINE_NO";
    public static final String BOOTH_OWNER = "BOOTH_OWNER";
    public static final String REMARK_ = "REMARK_";

    private Long id;

    private String no;
    private String name;
    private int count;
    private String base;

    private String sourceCompany;
    private String sourceTel;
    private String sourcePerson;
    private String sourceType;

    private String quarantineNo;
    private String boothOwner;
    private String remark;

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public String getQuarantineNo() {
        return quarantineNo;
    }

    public void setQuarantineNo(String quarantineNo) {
        this.quarantineNo = quarantineNo;
    }

    public String getBoothOwner() {
        return boothOwner;
    }

    public void setBoothOwner(String boothOwner) {
        this.boothOwner = boothOwner;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getSourceCompany() {
        return sourceCompany;
    }

    public void setSourceCompany(String sourceCompany) {
        this.sourceCompany = sourceCompany;
    }

    public String getSourceTel() {
        return sourceTel;
    }

    public void setSourceTel(String sourceTel) {
        this.sourceTel = sourceTel;
    }

    public String getSourcePerson() {
        return sourcePerson;
    }

    public void setSourcePerson(String sourcePerson) {
        this.sourcePerson = sourcePerson;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
