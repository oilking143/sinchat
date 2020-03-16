package com.sex8.sinchat.event;

public class RoomListCompleteEvent {
    private String message;
    private Throwable error;
    public RoomListCompleteEvent(){

    }

    public RoomListCompleteEvent(Throwable error){
        this.error = error;
    }

    public RoomListCompleteEvent(String message){
        this.message = message;
    }

    public String getMessage(){
        return message;
    }

    public boolean isSuccess(){
        return error == null;
    }
}
