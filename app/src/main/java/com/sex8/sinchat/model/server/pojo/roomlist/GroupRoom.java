package com.sex8.sinchat.model.server.pojo.roomlist;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class GroupRoom {

    @SerializedName("groupId")
    private int groupId;

    @SerializedName("groupName")
    private String groupName;

    @SerializedName("chatroomIds")
    private ArrayList<Integer> chatroomIds;

    private String LastMessage;

    private long LastMessageTimeStamp;

    private String LastmessageType;



    private String _imageLogoLink;

    private boolean isnew;

    public void setLastMessage(String lastMessage) {
        LastMessage = lastMessage;
    }

    public int getGroupId() {
        return groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public ArrayList<Integer> getChatroomIds() {
        return chatroomIds;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }



    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void setChatroomIds(ArrayList<Integer> chatroomIds) {
        this.chatroomIds = chatroomIds;
    }

    public boolean isIsnew() {
        return isnew;
    }

    public void setIsnew(boolean isnew) {
        this.isnew = isnew;
    }

    public Long getLastMessageTimeStamp() {
        return LastMessageTimeStamp;
    }

    public void setLastMessageTimeStamp(Long lastMessageTimeStamp) {
        LastMessageTimeStamp = lastMessageTimeStamp;
    }

    public String getLastMessage() {
        return LastMessage;
    }

    public String get_imageLogoLink() {
        return _imageLogoLink;
    }

    public void set_imageLogoLink(String _imageLogoLink) {
        this._imageLogoLink = _imageLogoLink;
    }

    public String getLastmessageType() {
        return LastmessageType;
    }

    public void setLastmessageType(String lastmessageType) {
        LastmessageType = lastmessageType;
    }
}
