package com.sex8.sinchat.event;

public class ReportEvent {
    private int status;
    private String message;

    public ReportEvent(int status, String message){
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
