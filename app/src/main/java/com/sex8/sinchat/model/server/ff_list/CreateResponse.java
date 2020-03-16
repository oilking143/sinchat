package com.sex8.sinchat.model.server.ff_list;

import androidx.annotation.NonNull;
import com.google.gson.annotations.SerializedName;
import com.sex8.sinchat.model.server.pojo.ResponseBase;

public class CreateResponse extends ResponseBase {

    @NonNull

    @SerializedName("error")
    private Error error;

    @SerializedName("data")
    private Data data;

    public Data getData() {
        return data;
    }

    public Error getError()
    {
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

    public class Data
    {
        int chatroomId;

        public int getChatroomId() {
            return chatroomId;
        }

        public void setChatroomId(int chatroomId) {
            this.chatroomId = chatroomId;
        }
    }


}
