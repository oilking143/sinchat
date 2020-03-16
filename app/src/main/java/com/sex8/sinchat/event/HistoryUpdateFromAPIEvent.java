package com.sex8.sinchat.event;

import com.sex8.sinchat.entity.Message;
import com.sex8.sinchat.model.dataBase.Entity.Chatroom_History;

import java.util.List;

public class HistoryUpdateFromAPIEvent {
    private int mRoomId;
    private String message;
    private List<Chatroom_History> history;

    public HistoryUpdateFromAPIEvent(int roomId, List<Chatroom_History> history) {
        mRoomId = roomId;
        this.history=history;
    }

    public HistoryUpdateFromAPIEvent(String msg){
        this.message = msg;
    }

    public int getRoomId() {
        return mRoomId;
    }

    public String getMessage(){
        return message;
    }

    public List<Chatroom_History> getHistory() {
        return history;
    }

    public void setHistory(List<Chatroom_History> history) {
        this.history = history;
    }
}
