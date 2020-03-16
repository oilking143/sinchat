package com.sex8.sinchat.model.server.pojo;

import com.google.gson.annotations.SerializedName;

public class ResponseBase {
    @SerializedName(value = "message", alternate = "msg")
    private String mMessage;

    @SerializedName(value = "status_code", alternate = "status")
    private int mStatusCode;

    public String getMessage() {
        return mMessage;
    }

    public int getStatusCode() {
        return mStatusCode;
    }
}
