package com.sex8.sinchat.event;

import com.sex8.sinchat.entity.Message;
import com.sex8.sinchat.model.dataBase.Entity.Chatroom_History;

public class ChatReportShowEvent {
    private Chatroom_History chat;
    public ChatReportShowEvent(Chatroom_History chat){
        this.chat = chat;
    }

    public Chatroom_History getChat() {
        return chat;
    }
}
