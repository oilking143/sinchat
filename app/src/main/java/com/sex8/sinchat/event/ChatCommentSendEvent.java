package com.sex8.sinchat.event;

import com.sex8.sinchat.entity.Message;
import com.sex8.sinchat.model.dataBase.Entity.Chatroom_History;

public class ChatCommentSendEvent {
    private Chatroom_History chat;
    private int commentType;
    public ChatCommentSendEvent(Chatroom_History chat, int commentType){
        this.chat = chat;
        this.commentType = commentType;
    }

    public Chatroom_History getChat() {
        return chat;
    }

    public int getCommentType() {
        return commentType;
    }
}
