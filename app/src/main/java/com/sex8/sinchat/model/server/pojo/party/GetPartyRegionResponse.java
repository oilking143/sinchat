package com.sex8.sinchat.model.server.pojo.party;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetPartyRegionResponse {

    @SerializedName("error")
    private Error error;

    @SerializedName("data")
    private List<Data> data;

    public Error getError() {
        if(error != null)
            return error;
        else
            return null;
    }

    public List<Data> getData() {
        if(data != null && data.size() > 0)
            return data;
        else
            return null;
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

    public class Data{

        private int group_id;
        private String group_name;

        public int getGroup_id() {
            return group_id;
        }

        public String getGroup_name() {
            return group_name;
        }
    }

}
