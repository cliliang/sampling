package com.cdv.sampling.bean;

import java.io.Serializable;

public class BaseBean<T> implements Serializable{
    private int ErrCode;
    private String ErrMsg;

    private T Data;

    public int getErrCode() {
        return ErrCode;
    }

    public void setErrCode(int errCode) {
        ErrCode = errCode;
    }

    public String getErrMsg() {
        return ErrMsg;
    }

    public void setErrMsg(String errMsg) {
        ErrMsg = errMsg;
    }

    public T getData() {
        return Data;
    }

    public void setData(T data) {
        Data = data;
    }
}
