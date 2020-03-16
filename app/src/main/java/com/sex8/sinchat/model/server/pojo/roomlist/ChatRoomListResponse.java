package com.sex8.sinchat.model.server.pojo.roomlist;

 import androidx.annotation.NonNull;

 import com.google.gson.annotations.SerializedName;
import com.sex8.sinchat.model.server.pojo.ResponseBase;

import java.util.List;

public class ChatRoomListResponse extends ResponseBase {

    @NonNull
    @SerializedName("data")
    private List<List<GroupRoom>> data;

    public List<List<GroupRoom>> getData() {
        return data;
    }

    @SerializedName("error")
    private Error error;

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


    public class Data {


        @SerializedName("groupRoom")
        List<GroupRoom> groupRoomList;

        public List<GroupRoom> getGroupRoomList() {
            return groupRoomList;
        }


    }
}

