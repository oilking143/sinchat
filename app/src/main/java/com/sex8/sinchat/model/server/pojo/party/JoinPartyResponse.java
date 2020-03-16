package com.sex8.sinchat.model.server.pojo.party;

import com.google.gson.annotations.SerializedName;
import com.sex8.sinchat.model.server.pojo.ResponseBase;

public class JoinPartyResponse extends ResponseBase {

    @SerializedName("error")
    private Error error;

    public Error getError() {
        return error;
    }

    public class Error
    {
        int code;
        String message;

        public int getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }
    }

}
