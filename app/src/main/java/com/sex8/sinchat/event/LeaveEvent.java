package com.sex8.sinchat.event;

public class LeaveEvent {

    private int status;
    private String message;
    public LeaveEvent(int status, String message){
        this.status = status;
        this.message = message;

    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

}
