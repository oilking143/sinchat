package com.sex8.sinchat.event;

import com.sex8.sinchat.model.server.pojo.party.GetPartyRegionResponse;

import java.util.List;

public class PartyRegionEvent {

    private int status;
    private String message;
    private List<GetPartyRegionResponse.Data> data;
    public PartyRegionEvent(int status, String message, List<GetPartyRegionResponse.Data> data){
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public List<GetPartyRegionResponse.Data> getData() {
        return data;
    }
}
