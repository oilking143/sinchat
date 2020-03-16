package com.sex8.sinchat.model.dataBase.Entity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "Chatroom_Info_Local")
public class Chatroom_Info_Local {
    @PrimaryKey(autoGenerate = false)
    @NonNull
    @ColumnInfo(name = "chatRoomId")
    private int chatRoomId;

    @ColumnInfo(name = "apiSync")
    private boolean needAPISync=true;

    @ColumnInfo(name = "lastMsgTimeStamp")
    private long lastMsgTimeStamp;

    @ColumnInfo(name = "lastMsgContent")
    private String lastMsgContent;

    @ColumnInfo(name = "unreadMessage")
    private boolean unreadMessage = false ;

    @ColumnInfo(name = "redEvnTime")
    private Long redEvnTime;


    public int getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(int chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    public boolean getNeedAPISync() {
        return needAPISync;
    }

    public void setNeedAPISync(boolean needAPISync) {
        this.needAPISync = needAPISync;
    }

    public long getLastMsgTimeStamp() {
        return lastMsgTimeStamp;
    }

    public void setLastMsgTimeStamp(long lastMsgTimeStamp) {
        this.lastMsgTimeStamp = lastMsgTimeStamp;
    }

    public String getLastMsgContent() {
        return lastMsgContent;
    }

    public void setLastMsgContent(String lastMsgContent) {
        this.lastMsgContent = lastMsgContent;
    }

    public boolean isUnreadMessage() {
        return unreadMessage;
    }

    public void setUnreadMessage(boolean unreadMessage) {
        this.unreadMessage = unreadMessage;
    }

    public Long getRedEvnTime() {
        return redEvnTime;
    }

    public void setRedEvnTime(Long redEvnTime) {
        this.redEvnTime = redEvnTime;
    }
}
