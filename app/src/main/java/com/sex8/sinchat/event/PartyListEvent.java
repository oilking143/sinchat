package com.sex8.sinchat.event;

import com.sex8.sinchat.model.server.pojo.party.GetPartyListResponse;

public class PartyListEvent {

    private int status;
    private String message;
    private GetPartyListResponse response;
    public PartyListEvent(int status, String message, GetPartyListResponse response){
        this.status = status;
        this.message = message;
        this.response = response;
    }

    public int getStatus(){
        return status;
    }

    public String getMessage(){
        return message;
    }

    public GetPartyListResponse getResponse() {
        return response;
    }
}
