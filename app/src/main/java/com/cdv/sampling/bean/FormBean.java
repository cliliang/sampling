package com.cdv.sampling.bean;

import java.io.Serializable;
import java.util.List;

public class FormBean<T> implements Serializable{
    private String ClientID;
    private String ClientUser;
    private String TestUser;
    private String ClientUserFileID;
    private String TestUserFileID;
    private String JianCeDanFileID;
    private String JianCeDanUrl;
    private String JianCeDanID;
    private String SampleType;
    private String CreateTime;
    private List<T> Items;

    public String getClientID() {
        return ClientID;
    }

    public void setClientID(String clientID) {
        ClientID = clientID;
    }

    public String getClientUserFileID() {
        return ClientUserFileID;
    }

    public void setClientUserFileID(String clientUserFileID) {
        ClientUserFileID = clientUserFileID;
    }

    public String getTestUserFileID() {
        return TestUserFileID;
    }

    public void setTestUserFileID(String testUserFileID) {
        TestUserFileID = testUserFileID;
    }

    public String getJianCeDanFileID() {
        return JianCeDanFileID;
    }

    public void setJianCeDanFileID(String jianCeDanFileID) {
        JianCeDanFileID = jianCeDanFileID;
    }

    public List<T> getItems() {
        return Items;
    }

    public void setItems(List<T> items) {
        Items = items;
    }

    public String getJianCeDanUrl() {
        return JianCeDanUrl;
    }

    public void setJianCeDanUrl(String jianCeDanUrl) {
        JianCeDanUrl = jianCeDanUrl;
    }

    public String getJianCeDanID() {
        return JianCeDanID;
    }

    public void setJianCeDanID(String jianCeDanID) {
        this.JianCeDanID = jianCeDanID;
    }

    public String getClientUser() {
        return ClientUser;
    }

    public void setClientUser(String clientUser) {
        ClientUser = clientUser;
    }

    public String getSampleType() {
        return SampleType;
    }

    public void setSampleType(String sampleType) {
        SampleType = sampleType;
    }

    public String getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(String createTime) {
        CreateTime = createTime;
    }

    public String getTestUser() {
        return TestUser;
    }

    public void setTestUser(String testUser) {
        TestUser = testUser;
    }
}
