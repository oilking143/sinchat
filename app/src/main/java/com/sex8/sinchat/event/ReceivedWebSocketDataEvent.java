package com.sex8.sinchat.event;

import org.json.JSONObject;

public class ReceivedWebSocketDataEvent {

    private JSONObject result;



    public ReceivedWebSocketDataEvent(JSONObject result)
    {
       this.result=result;
    }

    public JSONObject getResult() {
        return result;
    }

    public void setResult(JSONObject result) {
        this.result = result;
    }
}
