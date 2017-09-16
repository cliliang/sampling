package com.cdv.sampling.bean;

import android.text.TextUtils;

import com.cdv.sampling.utils.TimeUtils;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.annotation.Transient;

import java.io.Serializable;
import java.util.Date;

@Entity(indexes = {
        @Index(value = "CreateTime DESC", unique = true)
})
public class JianCeDan implements Serializable {

    public static final long serialVersionUID = 112314365312L;


    public static final int STATUS_ARCHIVED = 2;
    public static final int STATUS_PRINTED = 1;
    public static final int STATUS_INIT = 1;

    public static final String DAN_TYPE_SAMPLING_DRUG = "0";
    public static final String DAN_TYPE_SAMPLING_QUALITY = "1";
    public static final String DAN_TYPE_SAMPLING_VERIFICATION = "2";

    @Id(autoincrement = true)
    private Long ID;
    private long ClientID;
    @ToOne(joinProperty = "ClientID")
    private ClientUnit clientUnit;

    private long TestingID;
    private String ClientUser;
    private String TestUser;
    private String EDanType;
    private String FileIDs;
    private String CreateUser;
    private Date CreateTime;
    private String ClientUserFileID;
    private String TestUserFileID;
    private String JianCeDanFileID;
    @Transient
    private String showCreateTime;
    private int status;
    private String formImageUrl;
    private String formRemoteId;


    private String SampleType;
    @ToOne(joinProperty = "sampleTypeId")
    private AppTypes sampleHuanjie;
    private long sampleTypeId;


/** Used to resolve relations */
@Generated(hash = 2040040024)
private transient DaoSession daoSession;


/** Used for active entity operations. */
@Generated(hash = 116202375)
private transient JianCeDanDao myDao;
@Generated(hash = 1631375920)
public JianCeDan(Long ID, long ClientID, long TestingID, String ClientUser,
        String TestUser, String EDanType, String FileIDs, String CreateUser,
        Date CreateTime, String ClientUserFileID, String TestUserFileID,
        String JianCeDanFileID, int status, String formImageUrl,
        String formRemoteId, String SampleType, long sampleTypeId) {
    this.ID = ID;
    this.ClientID = ClientID;
    this.TestingID = TestingID;
    this.ClientUser = ClientUser;
    this.TestUser = TestUser;
    this.EDanType = EDanType;
    this.FileIDs = FileIDs;
    this.CreateUser = CreateUser;
    this.CreateTime = CreateTime;
    this.ClientUserFileID = ClientUserFileID;
    this.TestUserFileID = TestUserFileID;
    this.JianCeDanFileID = JianCeDanFileID;
    this.status = status;
    this.formImageUrl = formImageUrl;
    this.formRemoteId = formRemoteId;
    this.SampleType = SampleType;
    this.sampleTypeId = sampleTypeId;
}
@Generated(hash = 1722947415)
public JianCeDan() {
}
public Long getID() {
    return this.ID;
}
public void setID(Long ID) {
    this.ID = ID;
}
public long getClientID() {
    return this.ClientID;
}
public void setClientID(long ClientID) {
    this.ClientID = ClientID;
}
public long getTestingID() {
    return this.TestingID;
}
public void setTestingID(long TestingID) {
    this.TestingID = TestingID;
}
public String getClientUser() {
    return this.ClientUser;
}
public void setClientUser(String ClientUser) {
    this.ClientUser = ClientUser;
}
public String getTestUser() {
    return this.TestUser;
}
public void setTestUser(String TestUser) {
    this.TestUser = TestUser;
}
public String getEDanType() {
    return this.EDanType;
}
public void setEDanType(String EDanType) {
    this.EDanType = EDanType;
}
public String getFileIDs() {
    return this.FileIDs;
}
public void setFileIDs(String FileIDs) {
    this.FileIDs = FileIDs;
}
public String getCreateUser() {
    return this.CreateUser;
}
public void setCreateUser(String CreateUser) {
    this.CreateUser = CreateUser;
}
public Date getCreateTime() {
    return this.CreateTime;
}
public void setCreateTime(Date CreateTime) {
    this.CreateTime = CreateTime;
}
public String getClientUserFileID() {
    return this.ClientUserFileID;
}
public void setClientUserFileID(String ClientUserFileID) {
    this.ClientUserFileID = ClientUserFileID;
}
public String getTestUserFileID() {
    return this.TestUserFileID;
}
public void setTestUserFileID(String TestUserFileID) {
    this.TestUserFileID = TestUserFileID;
}
public String getJianCeDanFileID() {
    return this.JianCeDanFileID;
}
public void setJianCeDanFileID(String JianCeDanFileID) {
    this.JianCeDanFileID = JianCeDanFileID;
}
public int getStatus() {
    return this.status;
}
public void setStatus(int status) {
    this.status = status;
}
public String getFormImageUrl() {
    return this.formImageUrl;
}
public void setFormImageUrl(String formImageUrl) {
    this.formImageUrl = formImageUrl;
}
public String getFormRemoteId() {
    return this.formRemoteId;
}
public void setFormRemoteId(String formRemoteId) {
    this.formRemoteId = formRemoteId;
}
public String getSampleType() {
    return this.SampleType;
}
public void setSampleType(String SampleType) {
    this.SampleType = SampleType;
}
public long getSampleTypeId() {
    return this.sampleTypeId;
}
public void setSampleTypeId(long sampleTypeId) {
    this.sampleTypeId = sampleTypeId;
}
@Generated(hash = 1832526614)
private transient Long clientUnit__resolvedKey;
/** To-one relationship, resolved on first access. */
@Generated(hash = 1627189701)
public ClientUnit getClientUnit() {
    long __key = this.ClientID;
    if (clientUnit__resolvedKey == null
            || !clientUnit__resolvedKey.equals(__key)) {
        final DaoSession daoSession = this.daoSession;
        if (daoSession == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        ClientUnitDao targetDao = daoSession.getClientUnitDao();
        ClientUnit clientUnitNew = targetDao.load(__key);
        synchronized (this) {
            clientUnit = clientUnitNew;
            clientUnit__resolvedKey = __key;
        }
    }
    return clientUnit;
}
/** called by internal mechanisms, do not call yourself. */
@Generated(hash = 1667365823)
public void setClientUnit(@NotNull ClientUnit clientUnit) {
    if (clientUnit == null) {
        throw new DaoException(
                "To-one property 'ClientID' has not-null constraint; cannot set to-one to null");
    }
    synchronized (this) {
        this.clientUnit = clientUnit;
        ClientID = clientUnit.getLocalID();
        clientUnit__resolvedKey = ClientID;
    }
}
@Generated(hash = 1950175225)
private transient Long sampleHuanjie__resolvedKey;
/** To-one relationship, resolved on first access. */
@Generated(hash = 1823972415)
public AppTypes getSampleHuanjie() {
    long __key = this.sampleTypeId;
    if (sampleHuanjie__resolvedKey == null
            || !sampleHuanjie__resolvedKey.equals(__key)) {
        final DaoSession daoSession = this.daoSession;
        if (daoSession == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        AppTypesDao targetDao = daoSession.getAppTypesDao();
        AppTypes sampleHuanjieNew = targetDao.load(__key);
        synchronized (this) {
            sampleHuanjie = sampleHuanjieNew;
            sampleHuanjie__resolvedKey = __key;
        }
    }
    return sampleHuanjie;
}
/** called by internal mechanisms, do not call yourself. */
@Generated(hash = 1731558521)
public void setSampleHuanjie(@NotNull AppTypes sampleHuanjie) {
    if (sampleHuanjie == null) {
        throw new DaoException(
                "To-one property 'sampleTypeId' has not-null constraint; cannot set to-one to null");
    }
    synchronized (this) {
        this.sampleHuanjie = sampleHuanjie;
        sampleTypeId = sampleHuanjie.getLocalId();
        sampleHuanjie__resolvedKey = sampleTypeId;
    }
}
/**
 * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
 * Entity must attached to an entity context.
 */
@Generated(hash = 128553479)
public void delete() {
    if (myDao == null) {
        throw new DaoException("Entity is detached from DAO context");
    }
    myDao.delete(this);
}
/**
 * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
 * Entity must attached to an entity context.
 */
@Generated(hash = 1942392019)
public void refresh() {
    if (myDao == null) {
        throw new DaoException("Entity is detached from DAO context");
    }
    myDao.refresh(this);
}
/**
 * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
 * Entity must attached to an entity context.
 */
@Generated(hash = 713229351)
public void update() {
    if (myDao == null) {
        throw new DaoException("Entity is detached from DAO context");
    }
    myDao.update(this);
}
public String getShowCreateTime() {
        if (TextUtils.isEmpty(showCreateTime)) {
            showCreateTime = TimeUtils.getNormalTimeFormat(this.getCreateTime());
        }
        return showCreateTime;
    }

    public void setShowCreateTime(String showCreateTime) {
        this.showCreateTime = showCreateTime;
    }
    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 460736327)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getJianCeDanDao() : null;
    }
}