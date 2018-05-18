package com.cdv.sampling.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;

import java.io.Serializable;
import java.util.Date;

import org.greenrobot.greendao.annotation.Generated;

@Entity
public class ClientUnit implements Serializable {

    public static final long serialVersionUID = 141128938912L;

    @Id(autoincrement = true)
    private Long LocalID;
    private String Code;
    private String ID;
    @Unique
    private String Name;
    private String ShortName;
    private String BusinessCode;
    private String Address;
    private String Fax;
    private String Zip;
    private String Telephone;
    private String ContactUser;
    private String Description;
    private int IsInvalidate;
    private String Email;
    private Date LastModifyTime;
    private String ErrInfo;
    private Date CreateTime;
    private int Version;

    @Generated(hash = 336740253)
    public ClientUnit(Long LocalID, String Code, String ID, String Name,
                      String ShortName, String BusinessCode, String Address, String Fax,
                      String Zip, String Telephone, String ContactUser, String Description,
                      int IsInvalidate, String Email, Date LastModifyTime, String ErrInfo,
                      Date CreateTime, int Version) {
        this.LocalID = LocalID;
        this.Code = Code;
        this.ID = ID;
        this.Name = Name;
        this.ShortName = ShortName;
        this.BusinessCode = BusinessCode;
        this.Address = Address;
        this.Fax = Fax;
        this.Zip = Zip;
        this.Telephone = Telephone;
        this.ContactUser = ContactUser;
        this.Description = Description;
        this.IsInvalidate = IsInvalidate;
        this.Email = Email;
        this.LastModifyTime = LastModifyTime;
        this.ErrInfo = ErrInfo;
        this.CreateTime = CreateTime;
        this.Version = Version;
    }

    @Generated(hash = 1898687408)
    public ClientUnit() {
    }

    public Long getLocalID() {
        return this.LocalID;
    }

    public void setLocalID(Long LocalID) {
        this.LocalID = LocalID;
    }

    public String getCode() {
        return this.Code;
    }

    public void setCode(String Code) {
        this.Code = Code;
    }

    public String getID() {
        return this.ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return this.Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getShortName() {
        return this.ShortName;
    }

    public void setShortName(String ShortName) {
        this.ShortName = ShortName;
    }

    public String getBusinessCode() {
        return this.BusinessCode;
    }

    public void setBusinessCode(String BusinessCode) {
        this.BusinessCode = BusinessCode;
    }

    public String getAddress() {
        return this.Address;
    }

    public void setAddress(String Address) {
        this.Address = Address;
    }

    public String getFax() {
        return this.Fax;
    }

    public void setFax(String Fax) {
        this.Fax = Fax;
    }

    public String getZip() {
        return this.Zip;
    }

    public void setZip(String Zip) {
        this.Zip = Zip;
    }

    public String getTelephone() {
        return this.Telephone;
    }

    public void setTelephone(String Telephone) {
        this.Telephone = Telephone;
    }

    public String getContactUser() {
        return this.ContactUser;
    }

    public void setContactUser(String ContactUser) {
        this.ContactUser = ContactUser;
    }

    public String getDescription() {
        return this.Description;
    }

    public void setDescription(String Description) {
        this.Description = Description;
    }

    public int getIsInvalidate() {
        return this.IsInvalidate;
    }

    public void setIsInvalidate(int IsInvalidate) {
        this.IsInvalidate = IsInvalidate;
    }

    public String getEmail() {
        return this.Email;
    }

    public void setEmail(String Email) {
        this.Email = Email;
    }

    public Date getLastModifyTime() {
        return this.LastModifyTime;
    }

    public void setLastModifyTime(Date LastModifyTime) {
        this.LastModifyTime = LastModifyTime;
    }

    public String getErrInfo() {
        return this.ErrInfo;
    }

    public void setErrInfo(String ErrInfo) {
        this.ErrInfo = ErrInfo;
    }

    public Date getCreateTime() {
        return this.CreateTime;
    }

    public void setCreateTime(Date CreateTime) {
        this.CreateTime = CreateTime;
    }

    public int getVersion() {
        return this.Version;
    }

    public void setVersion(int Version) {
        this.Version = Version;
    }

}
