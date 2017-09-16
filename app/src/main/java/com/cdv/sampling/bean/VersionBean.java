package com.cdv.sampling.bean;

import java.io.Serializable;

public class VersionBean implements Serializable{

    private String ID;
    private int Version;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public int getVersion() {
        return Version;
    }

    public void setVersion(int version) {
        Version = version;
    }
}
