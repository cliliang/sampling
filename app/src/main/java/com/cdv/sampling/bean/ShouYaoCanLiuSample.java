package com.cdv.sampling.bean;

import android.text.TextUtils;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.annotation.Transient;

import java.io.Serializable;

@Entity(generateGettersSetters = true)
public class ShouYaoCanLiuSample implements Serializable {

    public static final long serialVersionUID = 141234265312L;

    @Id(autoincrement = true)
    private Long localId;
    private String ID;
    private long formId;
    private String Name;
    private String Code;
    private String Number;
    private String Base;
    private String CheckCode;
    private String PiHao;
    private String Description;
    private String SampleFileIDs;
    private String TanWeiUserFile;
    private Long TanWeiLocalId;
    private String localFileIds;
    private String ShengChanRiQi;
    private String TanWeiCode;
    private String TanWeiUser;
    @ToOne(joinProperty = "SampleSourceID")
    private ClientUnit sampleSource;
    @ToOne(joinProperty = "GouMaiTypeId")
    private AppTypes gouMaiLeiXing;

    private long SampleSourceID;
    private long GouMaiTypeId;
    private String GouMaiType;
    private boolean finished;
    @Transient
    private String jinHuoDescription;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 1516857599)
    private transient ShouYaoCanLiuSampleDao myDao;

    @Generated(hash = 70010473)
    public ShouYaoCanLiuSample(Long localId, String ID, long formId, String Name, String Code, String Number, String Base, String CheckCode, String PiHao, String Description,
            String SampleFileIDs, String TanWeiUserFile, Long TanWeiLocalId, String localFileIds, String ShengChanRiQi, String TanWeiCode, String TanWeiUser,
            long SampleSourceID, long GouMaiTypeId, String GouMaiType, boolean finished) {
        this.localId = localId;
        this.ID = ID;
        this.formId = formId;
        this.Name = Name;
        this.Code = Code;
        this.Number = Number;
        this.Base = Base;
        this.CheckCode = CheckCode;
        this.PiHao = PiHao;
        this.Description = Description;
        this.SampleFileIDs = SampleFileIDs;
        this.TanWeiUserFile = TanWeiUserFile;
        this.TanWeiLocalId = TanWeiLocalId;
        this.localFileIds = localFileIds;
        this.ShengChanRiQi = ShengChanRiQi;
        this.TanWeiCode = TanWeiCode;
        this.TanWeiUser = TanWeiUser;
        this.SampleSourceID = SampleSourceID;
        this.GouMaiTypeId = GouMaiTypeId;
        this.GouMaiType = GouMaiType;
        this.finished = finished;
    }

    @Generated(hash = 2033872098)
    public ShouYaoCanLiuSample() {
    }

    @Generated(hash = 1879792315)
    private transient Long sampleSource__resolvedKey;

    @Generated(hash = 1016830325)
    private transient Long gouMaiLeiXing__resolvedKey;

    public String getJinHuoDescription() {
        if (!TextUtils.isEmpty(jinHuoDescription)){
            return jinHuoDescription;
        }
        if (sampleSource != null){
            jinHuoDescription = gouMaiLeiXing.getValueName() + "/" + sampleSource.getName() + "/" + sampleSource.getContactUser() + "/" + sampleSource.getTelephone();
        }
        return jinHuoDescription;
    }

    public void setJinHuoDescription(String jinHuoDescription) {
        this.jinHuoDescription = jinHuoDescription;
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

    public long getFormId() {
        return this.formId;
    }

    public void setFormId(long formId) {
        this.formId = formId;
    }

    public String getName() {
        return this.Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getCode() {
        return this.Code;
    }

    public void setCode(String Code) {
        this.Code = Code;
    }

    public String getNumber() {
        return this.Number;
    }

    public void setNumber(String Number) {
        this.Number = Number;
    }

    public String getBase() {
        return this.Base;
    }

    public void setBase(String Base) {
        this.Base = Base;
    }

    public String getCheckCode() {
        return this.CheckCode;
    }

    public void setCheckCode(String CheckCode) {
        this.CheckCode = CheckCode;
    }

    public String getPiHao() {
        return this.PiHao;
    }

    public void setPiHao(String PiHao) {
        this.PiHao = PiHao;
    }

    public String getDescription() {
        return this.Description;
    }

    public void setDescription(String Description) {
        this.Description = Description;
    }

    public String getSampleFileIDs() {
        return this.SampleFileIDs;
    }

    public void setSampleFileIDs(String SampleFileIDs) {
        this.SampleFileIDs = SampleFileIDs;
    }

    public String getTanWeiUserFile() {
        return this.TanWeiUserFile;
    }

    public void setTanWeiUserFile(String TanWeiUserFile) {
        this.TanWeiUserFile = TanWeiUserFile;
    }

    public Long getTanWeiLocalId() {
        return this.TanWeiLocalId;
    }

    public void setTanWeiLocalId(Long TanWeiLocalId) {
        this.TanWeiLocalId = TanWeiLocalId;
    }

    public String getLocalFileIds() {
        return this.localFileIds;
    }

    public void setLocalFileIds(String localFileIds) {
        this.localFileIds = localFileIds;
    }

    public String getShengChanRiQi() {
        return this.ShengChanRiQi;
    }

    public void setShengChanRiQi(String ShengChanRiQi) {
        this.ShengChanRiQi = ShengChanRiQi;
    }

    public String getTanWeiCode() {
        return this.TanWeiCode;
    }

    public void setTanWeiCode(String TanWeiCode) {
        this.TanWeiCode = TanWeiCode;
    }

    public String getTanWeiUser() {
        return this.TanWeiUser;
    }

    public void setTanWeiUser(String TanWeiUser) {
        this.TanWeiUser = TanWeiUser;
    }

    public long getSampleSourceID() {
        return this.SampleSourceID;
    }

    public void setSampleSourceID(long SampleSourceID) {
        this.SampleSourceID = SampleSourceID;
    }

    public long getGouMaiTypeId() {
        return this.GouMaiTypeId;
    }

    public void setGouMaiTypeId(long GouMaiTypeId) {
        this.GouMaiTypeId = GouMaiTypeId;
    }

    public String getGouMaiType() {
        return this.GouMaiType;
    }

    public void setGouMaiType(String GouMaiType) {
        this.GouMaiType = GouMaiType;
    }

    public boolean getFinished() {
        return this.finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 1716985961)
    public ClientUnit getSampleSource() {
        long __key = this.SampleSourceID;
        if (sampleSource__resolvedKey == null || !sampleSource__resolvedKey.equals(__key)) {
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
            throw new DaoException("To-one property 'SampleSourceID' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.sampleSource = sampleSource;
            SampleSourceID = sampleSource.getLocalID();
            sampleSource__resolvedKey = SampleSourceID;
        }
    }

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 1162921186)
    public AppTypes getGouMaiLeiXing() {
        long __key = this.GouMaiTypeId;
        if (gouMaiLeiXing__resolvedKey == null || !gouMaiLeiXing__resolvedKey.equals(__key)) {
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
            throw new DaoException("To-one property 'GouMaiTypeId' has not-null constraint; cannot set to-one to null");
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
    @Generated(hash = 1620136542)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getShouYaoCanLiuSampleDao() : null;
    }
}
