package com.sex8.sinchat.model.server.ff_list;

 import androidx.annotation.NonNull;

 import com.google.gson.annotations.SerializedName;
import com.sex8.sinchat.model.server.pojo.ResponseBase;

public class GroupListResponse extends ResponseBase {


    @NonNull
    @SerializedName("data")
    private Data data;

    public Data getData() {
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
        userVersion version;

        public int getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }

        public userVersion getVersion() {
            return version;
        }
    }


    public class Data
    {

        String publicRoom;
        String privateRoom;


        public String getpublicRoom() {
            return publicRoom;
        }

        public String getprivateRoom() {
            return privateRoom;
        }


    }


    public class userVersion
    {
        int publicRoom;
        int privateRoom;

        public int getPublicRoom() {
            return publicRoom;
        }

        public int getPrivateRoom() {
            return privateRoom;
        }
    }

}
