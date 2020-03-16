package com.sex8.sinchat.event;

import com.sex8.sinchat.model.server.pojo.roomlist.ChatRoomListResponse;

import retrofit2.Response;

public class RoomListUpdateEvent {


    Response<ChatRoomListResponse> response;

    public RoomListUpdateEvent(Response<ChatRoomListResponse> response){

        this.response=response;
    }

    public Response<ChatRoomListResponse> getResponse() {
        return response;
    }

    public void setResponse(Response<ChatRoomListResponse> response) {
        this.response = response;
    }

}
