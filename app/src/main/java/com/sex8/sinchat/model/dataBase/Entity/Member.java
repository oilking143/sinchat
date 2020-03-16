package com.sex8.sinchat.model.dataBase.Entity;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "sinchat_member",
        indices = @Index(value = {"uid"}, unique = true))
public class Member {

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getPrivateroom() {
        return privateroom;
    }

    public void setPrivateroom(int privateroom) {
        this.privateroom = privateroom;
    }

    @PrimaryKey
    private long uid;

    @ColumnInfo(name = "username")
    String username;

    @ColumnInfo(name = "level")
    int level;

    @ColumnInfo(name = "privateroom")
    int privateroom;

    @ColumnInfo(name = "birthday")
    String birthday;

    @ColumnInfo(name = "avatar")
    String avatar;

    @ColumnInfo(name = "timestamp")
    long timestamp;

}
