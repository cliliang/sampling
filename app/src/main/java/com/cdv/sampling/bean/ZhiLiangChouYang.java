package com.cdv.sampling.bean;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.ToOne;

import java.io.Serializable;

@Entity(generateGettersSetters = true)
public class ZhiLiangChouYang implements Serializable{
    public static final long serialVersionUID = 1123165312L;

    @Id(autoincrement = true)
    private Long localId;
    private String ID;
    private long formId;
    private String Code;
    private String SampleName;
    private String Name;
    private String PiHao;
    private String PiZhunWenHao;
    private long GouMaiTypeId;
    private String GouMaiType;
    private String SampleGuiGe;
    private String Number;
    private String KuCun;
    private long SampleSourceID;
    private long ShengChanUnitID;
    private String Description;
    private String ShengChanRiQi;
    private String SampleFileIDs;
    private String localFileIds;
    private boolean finished;

    private String JinHuoTime;
    private String JinHuoNumber;
    private String GMPCode;
    private String XukeCode;

    private boolean haveHeshi;

    @ToOne(joinProperty = "ShengChanUnitID")
    private ClientUnit ShengChanUnit;
    @ToOne(joinProperty = "SampleSourceID")
    private ClientUnit sampleSource;
    @ToOne(joinProperty = "GouMaiTypeId")
    private AppTypes gouMaiLeiXing;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 36809175)
    private transient ZhiLiangChouYangDao myDao;
    @Generated(hash = 1758425704)
    public ZhiLiangChouYang(Long localId, String ID, long formId, String Code,
            String SampleName, String Name, String PiHao, String PiZhunWenHao,
            long GouMaiTypeId, String GouMaiType, String SampleGuiGe, String Number,
            String KuCun, long SampleSourceID, long ShengChanUnitID,
            String Description, String ShengChanRiQi, String SampleFileIDs,
            String localFileIds, boolean finished, String JinHuoTime,
            String JinHuoNumber, String GMPCode, String XukeCode,
            boolean haveHeshi) {
        this.localId = localId;
        this.ID = ID;
        this.formId = formId;
        this.Code = Code;
        this.SampleName = SampleName;
        this.Name = Name;
        this.PiHao = PiHao;
        this.PiZhunWenHao = PiZhunWenHao;
        this.GouMaiTypeId = GouMaiTypeId;
        this.GouMaiType = GouMaiType;
        this.SampleGuiGe = SampleGuiGe;
        this.Number = Number;
        this.KuCun = KuCun;
        this.SampleSourceID = SampleSourceID;
        this.ShengChanUnitID = ShengChanUnitID;
        this.Description = Description;
        this.ShengChanRiQi = ShengChanRiQi;
        this.SampleFileIDs = SampleFileIDs;
        this.localFileIds = localFileIds;
        this.finished = finished;
        this.JinHuoTime = JinHuoTime;
        this.JinHuoNumber = JinHuoNumber;
        this.GMPCode = GMPCode;
        this.XukeCode = XukeCode;
        this.haveHeshi = haveHeshi;
    }
    @Generated(hash = 1853664261)
    public ZhiLiangChouYang() {
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
    public String getCode() {
        return this.Code;
    }
    public void setCode(String Code) {
        this.Code = Code;
    }
    public String getSampleName() {
        return this.SampleName;
    }
    public void setSampleName(String SampleName) {
        this.SampleName = SampleName;
    }
    public String getName() {
        return this.Name;
    }
    public void setName(String Name) {
        this.Name = Name;
    }
    public String getPiHao() {
        return this.PiHao;
    }
    public void setPiHao(String PiHao) {
        this.PiHao = PiHao;
    }
    public String getPiZhunWenHao() {
        return this.PiZhunWenHao;
    }
    public void setPiZhunWenHao(String PiZhunWenHao) {
        this.PiZhunWenHao = PiZhunWenHao;
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
    public String getSampleGuiGe() {
        return this.SampleGuiGe;
    }
    public void setSampleGuiGe(String SampleGuiGe) {
        this.SampleGuiGe = SampleGuiGe;
    }
    public String getNumber() {
        return this.Number;
    }
    public void setNumber(String Number) {
        this.Number = Number;
    }
    public String getKuCun() {
        return this.KuCun;
    }
    public void setKuCun(String KuCun) {
        this.KuCun = KuCun;
    }
    public long getSampleSourceID() {
        return this.SampleSourceID;
    }
    public void setSampleSourceID(long SampleSourceID) {
        this.SampleSourceID = SampleSourceID;
    }
    public long getShengChanUnitID() {
        return this.ShengChanUnitID;
    }
    public void setShengChanUnitID(long ShengChanUnitID) {
        this.ShengChanUnitID = ShengChanUnitID;
    }
    public String getDescription() {
        return this.Description;
    }
    public void setDescription(String Description) {
        this.Description = Description;
    }
    public String getShengChanRiQi() {
        return this.ShengChanRiQi;
    }
    public void setShengChanRiQi(String ShengChanRiQi) {
        this.ShengChanRiQi = ShengChanRiQi;
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
    public String getJinHuoTime() {
        return this.JinHuoTime;
    }
    public void setJinHuoTime(String JinHuoTime) {
        this.JinHuoTime = JinHuoTime;
    }
    public String getJinHuoNumber() {
        return this.JinHuoNumber;
    }
    public void setJinHuoNumber(String JinHuoNumber) {
        this.JinHuoNumber = JinHuoNumber;
    }
    public String getGMPCode() {
        return this.GMPCode;
    }
    public void setGMPCode(String GMPCode) {
        this.GMPCode = GMPCode;
    }
    public String getXukeCode() {
        return this.XukeCode;
    }
    public void setXukeCode(String XukeCode) {
        this.XukeCode = XukeCode;
    }
    public boolean getHaveHeshi() {
        return this.haveHeshi;
    }
    public void setHaveHeshi(boolean haveHeshi) {
        this.haveHeshi = haveHeshi;
    }
    @Generated(hash = 1982858892)
    private transient Long ShengChanUnit__resolvedKey;
    /** To-one relationship, resolved on first access. */
    @Generated(hash = 360518869)
    public ClientUnit getShengChanUnit() {
        long __key = this.ShengChanUnitID;
        if (ShengChanUnit__resolvedKey == null
                || !ShengChanUnit__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ClientUnitDao targetDao = daoSession.getClientUnitDao();
            ClientUnit ShengChanUnitNew = targetDao.load(__key);
            synchronized (this) {
                ShengChanUnit = ShengChanUnitNew;
                ShengChanUnit__resolvedKey = __key;
            }
        }
        return ShengChanUnit;
    }
    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 597511398)
    public void setShengChanUnit(@NotNull ClientUnit ShengChanUnit) {
        if (ShengChanUnit == null) {
            throw new DaoException(
                    "To-one property 'ShengChanUnitID' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.ShengChanUnit = ShengChanUnit;
            ShengChanUnitID = ShengChanUnit.getLocalID();
            ShengChanUnit__resolvedKey = ShengChanUnitID;
        }
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
    @Generated(hash = 151323095)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getZhiLiangChouYangDao() : null;
    }

}
