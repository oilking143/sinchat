package com.sex8.sinchat.model.dataBase.Entity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "Chatroom_Info",
        indices = @Index(value = {"chatRoomId"}, unique = true))
public class Chatroom_Info {

    @PrimaryKey(autoGenerate = false)
    @NonNull
    @ColumnInfo(name = "chatRoomId")
    private int chatRoomId;

    @ColumnInfo(name = "chatRoomName")
    private String chatRoomName;

    @ColumnInfo(name = "groupId")
    private int groupId;

    @ColumnInfo(name = "groupName")
    private String groupName;

    @ColumnInfo(name = "descrption")
    private String descrption;

    @ColumnInfo(name = "uids")
    private String uids;

    @Nullable
    @ColumnInfo(name = "memberLimit")
    private int memberLimit ;

    @Nullable
    @ColumnInfo(name = "membercounts")
    private int membercounts=0;

    @ColumnInfo(name = "_imageDes1Link")
    private String _imageDes1Link;

    @ColumnInfo(name = "_imageDes2Link")
    private String _imageDes2Link;

    @ColumnInfo(name = "_imageDes3Link")
    private String _imageDes3Link;

    @ColumnInfo(name = "_imageLogoLink")
    private String _imageLogoLink;

    public int getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(int chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    public String getChatRoomName() {
        return chatRoomName;
    }

    public void setChatRoomName(String chatRoomName) {
        this.chatRoomName = chatRoomName;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getDescrption() {
        return descrption;
    }

    public void setDescrption(String descrption) {
        this.descrption = descrption;
    }

    public String getUids() {
        return uids;
    }

    public void setUids(String uids) {
        this.uids = uids;
    }

    public int getMemberLimit() {
        return memberLimit;
    }

    public void setMemberLimit(int memberLimit) {
        this.memberLimit = memberLimit;
    }

    public int getMembercounts() {
        return membercounts;
    }

    public void setMembercounts(int membercounts) {
        this.membercounts = membercounts;
    }

    public String get_imageDes1Link() {
        return _imageDes1Link;
    }

    public void set_imageDes1Link(String _imageDes1Link) {
        this._imageDes1Link = _imageDes1Link;
    }

    public String get_imageDes2Link() {
        return _imageDes2Link;
    }

    public void set_imageDes2Link(String _imageDes2Link) {
        this._imageDes2Link = _imageDes2Link;
    }

    public String get_imageDes3Link() {
        return _imageDes3Link;
    }

    public void set_imageDes3Link(String _imageDes3Link) {
        this._imageDes3Link = _imageDes3Link;
    }

    public String get_imageLogoLink() {
        return _imageLogoLink;
    }

    public void set_imageLogoLink(String _imageLogoLink) {
        this._imageLogoLink = _imageLogoLink;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
