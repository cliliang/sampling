package com.cdv.sampling.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class TestUnit implements Serializable{
    public static final long serialVersionUID = 141123435392L;

    @Id
    private Long id;
    private String Code;
    private String Name;
    private String ShortName;
    private String BusinessCode;
    private String Address;
    private String Email;
    private String Fax;
    private String Zip;
    private String Telephone;
    private String ContactUser;
    private String Description;

    @Generated(hash = 616435213)
    public TestUnit(Long id, String Code, String Name, String ShortName,
            String BusinessCode, String Address, String Email, String Fax,
            String Zip, String Telephone, String ContactUser, String Description) {
        this.id = id;
        this.Code = Code;
        this.Name = Name;
        this.ShortName = ShortName;
        this.BusinessCode = BusinessCode;
        this.Address = Address;
        this.Email = Email;
        this.Fax = Fax;
        this.Zip = Zip;
        this.Telephone = Telephone;
        this.ContactUser = ContactUser;
        this.Description = Description;
    }

    @Generated(hash = 1908246429)
    public TestUnit() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String Code) {
        this.Code = Code;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getShortName() {
        return ShortName;
    }

    public void setShortName(String ShortName) {
        this.ShortName = ShortName;
    }

    public String getBusinessCode() {
        return BusinessCode;
    }

    public void setBusinessCode(String BusinessCode) {
        this.BusinessCode = BusinessCode;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String Address) {
        this.Address = Address;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String Email) {
        this.Email = Email;
    }

    public String getFax() {
        return Fax;
    }

    public void setFax(String Fax) {
        this.Fax = Fax;
    }

    public String getZip() {
        return Zip;
    }

    public void setZip(String Zip) {
        this.Zip = Zip;
    }

    public String getTelephone() {
        return Telephone;
    }

    public void setTelephone(String Telephone) {
        this.Telephone = Telephone;
    }

    public String getContactUser() {
        return ContactUser;
    }

    public void setContactUser(String ContactUser) {
        this.ContactUser = ContactUser;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String Description) {
        this.Description = Description;
    }
}
