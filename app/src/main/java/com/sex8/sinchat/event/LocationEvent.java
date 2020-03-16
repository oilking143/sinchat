package com.sex8.sinchat.event;

public class LocationEvent {

    private int status;


    private String message;

    public LocationEvent(int status, String message){
        this.status = status;
        this.message = message;

    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
