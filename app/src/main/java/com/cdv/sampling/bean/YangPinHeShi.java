package com.cdv.sampling.bean;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.ToOne;

import java.io.Serializable;

@Entity(generateGettersSetters = true)
public class YangPinHeShi implements Serializable{
    public static final long serialVersionUID = 14123123112L;

    @Id(autoincrement = true)
    private Long localId;
    private String ID;
    private String Code;
    private String XukeCode;
    private String GMPCode;
    private String GouMaiType;
    private long GouMaiTypeId;
    private String Number;
    private String JinHuoTime;
    private String Description;
    private Long formId;
    private String PiZhunWenHao;
    private long SampleSourceID;
    @ToOne(joinProperty = "SampleSourceID")
    private ClientUnit sampleSource;
    @ToOne(joinProperty = "GouMaiTypeId")
    private AppTypes gouMaiLeiXing;
    private String SampleFileIDs;
    private String localFileIds;
    private boolean finished;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 973161752)
    private transient YangPinHeShiDao myDao;
    @Generated(hash = 2106588109)
    public YangPinHeShi(Long localId, String ID, String Code, String XukeCode,
            String GMPCode, String GouMaiType, long GouMaiTypeId, String Number,
            String JinHuoTime, String Description, Long formId, String PiZhunWenHao,
            long SampleSourceID, String SampleFileIDs, String localFileIds,
            boolean finished) {
        this.localId = localId;
        this.ID = ID;
        this.Code = Code;
        this.XukeCode = XukeCode;
        this.GMPCode = GMPCode;
        this.GouMaiType = GouMaiType;
        this.GouMaiTypeId = GouMaiTypeId;
        this.Number = Number;
        this.JinHuoTime = JinHuoTime;
        this.Description = Description;
        this.formId = formId;
        this.PiZhunWenHao = PiZhunWenHao;
        this.SampleSourceID = SampleSourceID;
        this.SampleFileIDs = SampleFileIDs;
        this.localFileIds = localFileIds;
        this.finished = finished;
    }
    @Generated(hash = 69151387)
    public YangPinHeShi() {
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
    public String getCode() {
        return this.Code;
    }
    public void setCode(String Code) {
        this.Code = Code;
    }
    public String getXukeCode() {
        return this.XukeCode;
    }
    public void setXukeCode(String XukeCode) {
        this.XukeCode = XukeCode;
    }
    public String getGMPCode() {
        return this.GMPCode;
    }
    public void setGMPCode(String GMPCode) {
        this.GMPCode = GMPCode;
    }
    public String getGouMaiType() {
        return this.GouMaiType;
    }
    public void setGouMaiType(String GouMaiType) {
        this.GouMaiType = GouMaiType;
    }
    public long getGouMaiTypeId() {
        return this.GouMaiTypeId;
    }
    public void setGouMaiTypeId(long GouMaiTypeId) {
        this.GouMaiTypeId = GouMaiTypeId;
    }
    public String getNumber() {
        return this.Number;
    }
    public void setNumber(String Number) {
        this.Number = Number;
    }
    public String getJinHuoTime() {
        return this.JinHuoTime;
    }
    public void setJinHuoTime(String JinHuoTime) {
        this.JinHuoTime = JinHuoTime;
    }
    public String getDescription() {
        return this.Description;
    }
    public void setDescription(String Description) {
        this.Description = Description;
    }
    public Long getFormId() {
        return this.formId;
    }
    public void setFormId(Long formId) {
        this.formId = formId;
    }
    public String getPiZhunWenHao() {
        return this.PiZhunWenHao;
    }
    public void setPiZhunWenHao(String PiZhunWenHao) {
        this.PiZhunWenHao = PiZhunWenHao;
    }
    public long getSampleSourceID() {
        return this.SampleSourceID;
    }
    public void setSampleSourceID(long SampleSourceID) {
        this.SampleSourceID = SampleSourceID;
    }
    public String getSampleFileIDs() {
        return this.SampleFileIDs;
    }
    public void setSampleFileIDs(String SampleFileIDs) {
        this.SampleFileIDs = SampleFileIDs;
    }
    public String getLocalFileIds() {
        return this.localFileIds;
    }
    public void setLocalFileIds(String localFileIds) {
        this.localFileIds = localFileIds;
    }
    public boolean getFinished() {
        return this.finished;
    }
    public void setFinished(boolean finished) {
        this.finished = finished;
    }
    @Generated(hash = 1879792315)
    private transient Long sampleSource__resolvedKey;
    /** To-one relationship, resolved on first access. */
    @Generated(hash = 1716985961)
    public ClientUnit getSampleSource() {
        long __key = this.SampleSourceID;
        if (sampleSource__resolvedKey == null
                || !sampleSource__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ClientUnitDao targetDao = daoSession.getClientUnitDao();
            ClientUnit sampleSourceNew = targetDao.load(__key);
            synchronized (this) {
                sampleSource = sampleSourceNew;
                sampleSource__resolvedKey = __key;
            }
        }
        return sampleSource;
    }
    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 376726358)
    public void setSampleSource(@NotNull ClientUnit sampleSource) {
        if (sampleSource == null) {
            throw new DaoException(
                    "To-one property 'SampleSourceID' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.sampleSource = sampleSource;
            SampleSourceID = sampleSource.getLocalID();
            sampleSource__resolvedKey = SampleSourceID;
        }
    }
    @Generated(hash = 1016830325)
    private transient Long gouMaiLeiXing__resolvedKey;
    /** To-one relationship, resolved on first access. */
    @Generated(hash = 1162921186)
    public AppTypes getGouMaiLeiXing() {
        long __key = this.GouMaiTypeId;
        if (gouMaiLeiXing__resolvedKey == null
                || !gouMaiLeiXing__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            AppTypesDao targetDao = daoSession.getAppTypesDao();
            AppTypes gouMaiLeiXingNew = targetDao.load(__key);
            synchronized (this) {
                gouMaiLeiXing = gouMaiLeiXingNew;
                gouMaiLeiXing__resolvedKey = __key;
            }
        }
        return gouMaiLeiXing;
    }
    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 495961861)
    public void setGouMaiLeiXing(@NotNull AppTypes gouMaiLeiXing) {
        if (gouMaiLeiXing == null) {
            throw new DaoException(
                    "To-one property 'GouMaiTypeId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.gouMaiLeiXing = gouMaiLeiXing;
            GouMaiTypeId = gouMaiLeiXing.getLocalId();
            gouMaiLeiXing__resolvedKey = GouMaiTypeId;
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
    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 372567974)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getYangPinHeShiDao() : null;
    }
}
