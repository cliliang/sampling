package com.cdv.sampling.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.util.Date;

@Entity
public class AppFiles {
    public static final long serialVersionUID = 147784522L;

    @Id(autoincrement = true)
    private Long ID;
    private String Name;
    private String FilePath;
    private String Title;
    private String Tags;
    private String Description;
    private String FileType;
    private String MD5;
    private String ThumbPath;
    private String FileSize;
    private String FileLength;
    private String CreateUser;
    private Date CreateTime;
    private Date DeleteTime;
    private String DeleteUser;
    private boolean IsDeleted;

    @Generated(hash = 1874385601)
    public AppFiles(Long ID, String Name, String FilePath, String Title,
            String Tags, String Description, String FileType, String MD5,
            String ThumbPath, String FileSize, String FileLength, String CreateUser,
            Date CreateTime, Date DeleteTime, String DeleteUser,
            boolean IsDeleted) {
        this.ID = ID;
        this.Name = Name;
        this.FilePath = FilePath;
        this.Title = Title;
        this.Tags = Tags;
        this.Description = Description;
        this.FileType = FileType;
        this.MD5 = MD5;
        this.ThumbPath = ThumbPath;
        this.FileSize = FileSize;
        this.FileLength = FileLength;
        this.CreateUser = CreateUser;
        this.CreateTime = CreateTime;
        this.DeleteTime = DeleteTime;
        this.DeleteUser = DeleteUser;
        this.IsDeleted = IsDeleted;
    }

    @Generated(hash = 1993006411)
    public AppFiles() {
    }

    public Long getID() {
        return ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getFilePath() {
        return FilePath;
    }

    public void setFilePath(String filePath) {
        FilePath = filePath;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getTags() {
        return Tags;
    }

    public void setTags(String tags) {
        Tags = tags;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getFileType() {
        return FileType;
    }

    public void setFileType(String fileType) {
        FileType = fileType;
    }

    public String getMD5() {
        return MD5;
    }

    public void setMD5(String MD5) {
        this.MD5 = MD5;
    }

    public String getThumbPath() {
        return ThumbPath;
    }

    public void setThumbPath(String thumbPath) {
        ThumbPath = thumbPath;
    }

    public String getFileSize() {
        return FileSize;
    }

    public void setFileSize(String fileSize) {
        FileSize = fileSize;
    }

    public String getFileLength() {
        return FileLength;
    }

    public void setFileLength(String fileLength) {
        FileLength = fileLength;
    }

    public String getCreateUser() {
        return CreateUser;
    }

    public void setCreateUser(String createUser) {
        CreateUser = createUser;
    }

    public Date getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(Date createTime) {
        CreateTime = createTime;
    }

    public Date getDeleteTime() {
        return DeleteTime;
    }

    public void setDeleteTime(Date deleteTime) {
        DeleteTime = deleteTime;
    }

    public String getDeleteUser() {
        return DeleteUser;
    }

    public void setDeleteUser(String deleteUser) {
        DeleteUser = deleteUser;
    }

    public boolean isDeleted() {
        return IsDeleted;
    }

    public void setDeleted(boolean deleted) {
        IsDeleted = deleted;
    }

    public boolean getIsDeleted() {
        return this.IsDeleted;
    }

    public void setIsDeleted(boolean IsDeleted) {
        this.IsDeleted = IsDeleted;
    }
}
