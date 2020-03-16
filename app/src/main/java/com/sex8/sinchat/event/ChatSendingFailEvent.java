package com.sex8.sinchat.event;

import com.sex8.sinchat.model.dataBase.Entity.Chatroom_History;

public class ChatSendingFailEvent {

    private Chatroom_History chat;

    public ChatSendingFailEvent(Chatroom_History chat)
    {
        this.chat=chat;
    }


    public Chatroom_History getChat() {
        return chat;
    }

    public void setChat(Chatroom_History chat) {
        this.chat = chat;
    }
}
