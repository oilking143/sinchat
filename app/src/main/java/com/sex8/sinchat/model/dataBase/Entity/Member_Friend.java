package com.sex8.sinchat.model.dataBase.Entity;


import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "sinchat_member_friend",
        indices = @Index(value = {"uid"}, unique = true))
public class Member_Friend {

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    @PrimaryKey
    private long uid;

}
