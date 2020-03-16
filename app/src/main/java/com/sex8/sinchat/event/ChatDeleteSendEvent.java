package com.sex8.sinchat.event;

import com.sex8.sinchat.entity.Message;
import com.sex8.sinchat.model.dataBase.Entity.Chatroom_History;

public class ChatDeleteSendEvent {
    private Chatroom_History chat;
    public ChatDeleteSendEvent(Chatroom_History chat){
        this.chat = chat;
    }

    public Chatroom_History getChat() {
        return chat;
    }
}
