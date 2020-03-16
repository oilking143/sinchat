package com.sex8.sinchat.event;

public class DoneEvent {

    private int status;
    private String message;
    public DoneEvent(int status,String message)
    {
        this.message=message;
        this.status=status;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
