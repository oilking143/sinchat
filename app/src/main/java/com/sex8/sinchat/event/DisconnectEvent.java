package com.sex8.sinchat.event;

public class DisconnectEvent {
    private int status;
    private String message;
    public DisconnectEvent(int status,String message)
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
