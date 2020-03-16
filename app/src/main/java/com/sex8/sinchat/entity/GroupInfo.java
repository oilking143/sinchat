package com.sex8.sinchat.entity;

import com.sex8.sinchat.model.dataBase.Entity.Myroom_List;

import java.util.List;

public class GroupInfo {


    /**
     * groupId : 3
     * groupName : 福利群
     * chatroomIds : []
     */

    private int groupId;
    private String groupName;
    private List<Myroom_List> chatroomIds;

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<?> getChatroomIds() {
        return chatroomIds;
    }

    public void setChatroomIds(List<Myroom_List> chatroomIds) {
        this.chatroomIds = chatroomIds;
    }
}
