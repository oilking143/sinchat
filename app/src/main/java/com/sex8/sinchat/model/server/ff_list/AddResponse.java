package com.sex8.sinchat.model.server.ff_list;

 import androidx.annotation.NonNull;

 import com.google.gson.annotations.SerializedName;
import com.sex8.sinchat.model.server.pojo.ResponseBase;

public class AddResponse extends ResponseBase {

    @NonNull

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


}