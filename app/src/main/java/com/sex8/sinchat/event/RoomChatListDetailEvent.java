package com.sex8.sinchat.event;

import com.sex8.sinchat.model.server.pojo.roomlist.GetRoomListDetailResponse;

public class RoomChatListDetailEvent {

    private int status;
    private String message;
    private GetRoomListDetailResponse response;
    public RoomChatListDetailEvent(int status, String message, GetRoomListDetailResponse response){
        this.status = status;
        this.message = message;
        this.response=response;
    }

    public int getStatus(){
        return status;
    }

    public String getMessage(){
        return message;
    }

    public GetRoomListDetailResponse getResponse() {
        return response;
    }
}
