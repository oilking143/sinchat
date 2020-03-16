package com.sex8.sinchat.event;

import com.sex8.sinchat.model.server.ff_list.GroupListResponse;

public class GroupListEvent {

    private int status;
    private String message;
    private GroupListResponse.Data data;
    public GroupListEvent(int status, String message, GroupListResponse.Data data){
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public GroupListResponse.Data getData() {
        return data;
    }
}
