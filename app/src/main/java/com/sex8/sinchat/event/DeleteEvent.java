package com.sex8.sinchat.event;

public class DeleteEvent {
    private int status;
    private String message;

    public DeleteEvent(int status, String message){
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
