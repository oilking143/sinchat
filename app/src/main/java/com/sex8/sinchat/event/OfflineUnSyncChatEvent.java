package com.sex8.sinchat.event;

import org.json.JSONObject;

public class OfflineUnSyncChatEvent {

    private JSONObject result;



    public OfflineUnSyncChatEvent(JSONObject result)
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
