package com.cdv.sampling.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;
import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class UserBean implements Serializable {

    public static final String ROLE_ADMIN = "0";

    public static final long serialVersionUID = 141298189292L;

    @Id
    private String ID;
    private String Account;
    private String UserName;
    private String Telephone;
    private String Role;
    private Date CreateTime;
    private int IsInValidate;
    private String Password;

    @Generated(hash = 1180466924)
    public UserBean(String ID, String Account, String UserName, String Telephone,
            String Role, Date CreateTime, int IsInValidate, String Password) {
        this.ID = ID;
        this.Account = Account;
        this.UserName = UserName;
        this.Telephone = Telephone;
        this.Role = Role;
        this.CreateTime = CreateTime;
        this.IsInValidate = IsInValidate;
        this.Password = Password;
    }

    @Generated(hash = 1203313951)
    public UserBean() {
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getAccount() {
        return Account;
    }

    public void setAccount(String account) {
        Account = account;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getTelephone() {
        return Telephone;
    }

    public void setTelephone(String telephone) {
        Telephone = telephone;
    }

    public String getRole() {
        return Role;
    }

    public void setRole(String role) {
        Role = role;
    }

    public Date getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(Date createTime) {
        CreateTime = createTime;
    }

    public int getIsInValidate() {
        return IsInValidate;
    }

    public void setIsInValidate(int isInValidate) {
        IsInValidate = isInValidate;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }
}
