package com.sex8.sinchat.model.server.WebSocket;

import com.sex8.sinchat.model.dataBase.Entity.Chatroom_History;

import java.util.Timer;

public class ChatStatus {

    private Timer timer;
    private Chatroom_History submittedChat;

    public Timer getTimer() {
        return timer;
    }

    public void setTimer(Timer timer) {
        this.timer = timer;
    }

    public Chatroom_History getSubmittedChat() {
        return submittedChat;
    }

    public void setSubmittedChat(Chatroom_History submittedChat) {
        this.submittedChat = submittedChat;
    }
}
