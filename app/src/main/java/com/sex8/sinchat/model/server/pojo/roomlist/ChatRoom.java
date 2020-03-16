package com.sex8.sinchat.model.server.pojo.roomlist;

import com.google.gson.annotations.SerializedName;
import com.sex8.sinchat.entity.Message;

import java.util.List;

public class ChatRoom {
//
    @SerializedName("chatroom_id")
    private int mRoomId;

//
    @SerializedName("chatroom_name")
    private String mName;
//
    @SerializedName("sort")
    private int sort;
//
    @SerializedName("group_id")
    private int group_id;

    //
    @SerializedName("mastergroupid")
    private int mastergroupid;

    @SerializedName("lastmsg")
    private List<Message> mLastMessage;

    //
    @SerializedName("member_limit")
    private int mMaxUserCount;

    private String LastMessage="";

    private long LastMessageTimeStamp;

    private String mDescription;

    private int mOnlineMember;

    private String mRoomtype="";

    private boolean isnew;

    private String LastmessageType;

    public int getRoomId() {
        return mRoomId;
    }

    public String getName() {
        return mName;
    }

    public void setLastMessage(String lastMessage) {
        LastMessage = lastMessage;
    }



    public String getLastMessage() {

            return LastMessage;
    }

    public String getmRoomtype() {
        return mRoomtype;
    }

    public void setmRoomtype(String mRoomtype) {
        this.mRoomtype = mRoomtype;
    }


    public void setmRoomId(int mRoomId) {
        this.mRoomId = mRoomId;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public void setMastergroupid(int mastergroupid) {
        this.mastergroupid = mastergroupid;
    }

    public long getLastMessageTimeStamp() {
        return LastMessageTimeStamp;
    }

    public void setLastMessageTimeStamp(Long lastMessageTimeStamp) {
        LastMessageTimeStamp = lastMessageTimeStamp;
    }

    public boolean isIsnew() {
        return isnew;
    }

    public void setIsnew(boolean isnew) {
        this.isnew = isnew;
    }

    public String getmDescription() {
        return mDescription;
    }

    public void setmDescription(String mDescription) {
        this.mDescription = mDescription;
    }


    public String getLastmessageType() {
        return LastmessageType;
    }

    public void setLastmessageType(String lastmessageType) {
        LastmessageType = lastmessageType;
    }
}
