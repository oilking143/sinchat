package com.sex8.sinchat.model.dataBase.Entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "chatroom_history", primaryKeys = {"uid","msgId"})
public class Chatroom_History {

    public int getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(int chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    public String getChatType() {
        return chatType;
    }

    public void setChatType(String chatType) {
        this.chatType = chatType;
    }

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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public int getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(int isAdmin) {
        this.isAdmin = isAdmin;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getSendingStatus() {
        return sendingStatus;
    }

    public void setSendingStatus(int sendingStatus) {
        this.sendingStatus = sendingStatus;
    }

    public String getSystemType() {
        return systemType;
    }

    public void setSystemType(String systemType) {
        this.systemType = systemType;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    @ColumnInfo(name = "chatRoomId")
    private int chatRoomId;

    @ColumnInfo(name = "chatType")
    private String chatType;

    @ColumnInfo(name = "systemType")
    private String systemType;

    @ColumnInfo(name = "uid")
    private long uid;

    @ColumnInfo(name = "username")
    private String username;

    @ColumnInfo(name = "avatar")
    private String avatar;

    @ColumnInfo(name = "content")
    private String content;

    @NonNull
    @ColumnInfo(name = "msgId")
    private String msgId;

    @ColumnInfo(name = "isAdmin")
    private int isAdmin;

    @ColumnInfo(name = "timestamp")
    private long timestamp;

    /**
     *
     *  0:socketReturn(socket回傳)
     *  1:sending
     *  2:fail
     *  3:sucess
     *
     * */
    @ColumnInfo(name = "sendingStatus")
    private int sendingStatus;

    @ColumnInfo(name = "imageWidth")
    private int imageWidth;

    @ColumnInfo(name = "imageHeight")
    private int imageHeight;

    @ColumnInfo(name = "msgType")
    private int msgType;







}
