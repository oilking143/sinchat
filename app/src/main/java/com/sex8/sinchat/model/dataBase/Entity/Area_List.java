package com.sex8.sinchat.model.dataBase.Entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "area_list")
public class Area_List {

    @ColumnInfo(name = "chatRoomId")
    @PrimaryKey(autoGenerate = false)
    private int chatRoomId;

    @ColumnInfo(name = "groupId")
     private int groupId;

    @ColumnInfo(name = "groupName")
    private String groupName;

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(int chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
