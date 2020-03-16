package com.sex8.sinchat.model.server.pojo.history;

import com.google.gson.annotations.SerializedName;
import com.sex8.sinchat.entity.Message;

import java.util.List;

public class HistoryMessage {
    @SerializedName("room_history")
    private List<Message> mMessages;

    public List<Message> getMessages() {
        return mMessages;
    }
}
