package com.sex8.sinchat.event;

import com.sex8.sinchat.model.dataBase.Entity.Chatroom_History;

public class SendingMessageStatusUpdateEvent {
    private Chatroom_History chat;
    public SendingMessageStatusUpdateEvent(Chatroom_History chat){
        this.chat = chat;
    }

    public Chatroom_History getChat() {
        return chat;
    }
}
