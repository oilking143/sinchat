package com.sex8.sinchat.event;

import java.util.ArrayList;

public class FriendEvent {

    private int status;
    private String message;
    private ArrayList<String> data;
    public FriendEvent(int status, String message, ArrayList<String> data){
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

    public ArrayList<String> getData() {
        return data;
    }
}
