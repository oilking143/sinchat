package com.sex8.sinchat.event;

import android.view.View;

import com.sex8.sinchat.entity.Message;
import com.sex8.sinchat.model.dataBase.Entity.Chatroom_History;

public class ChatCommentShowEvent {
    private Chatroom_History chat;
    private View anchor;
    public ChatCommentShowEvent(Chatroom_History chat, View anchor){
        this.chat = chat;
        this.anchor = anchor;
    }

    public Chatroom_History getChat() {
        return chat;
    }

    public View getAnchor() {
        return anchor;
    }
}
