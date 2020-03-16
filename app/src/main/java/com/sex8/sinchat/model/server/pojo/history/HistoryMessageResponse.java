package com.sex8.sinchat.model.server.pojo.history;

 import androidx.annotation.NonNull;

 import com.google.gson.annotations.SerializedName;
import com.sex8.sinchat.model.server.pojo.ResponseBase;

import java.util.List;

public class HistoryMessageResponse extends ResponseBase {

    @NonNull

    @SerializedName("data")
    private List<Data> mMessages;

    @SerializedName("error")
    private Error error;

    public Error getError()
    {
        return error;
    }

    public List<Data> getData()
    {
        return mMessages;
    }

    public class Data {

        String id;
        String msgtype;
        String chattype;
        int uid;
        int roomid;
        String nickname;
        String userimg;
        String content;
        String msgid;
        String created_at;
        long timestamp;

        public String getId() {
            return id;
        }

        public String getMsgtype() {
            return msgtype;
        }

        public String getChattype() {
            return chattype;
        }

        public int getUid() {
            return uid;
        }



        public String getNickname() {
            return nickname;
        }

        public String getUserimg() {
            return userimg;
        }

        public String getContent() {
            return content;
        }

        public String getMsgid() {
            return msgid;
        }

        public String getCreated_at() {
            return created_at;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public int getRoomid() {
            return roomid;
        }
    }


    public class Error{

        String code;
        String message;

        public String getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }
    }

}
