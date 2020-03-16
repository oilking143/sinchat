package com.sex8.sinchat.event;

import com.sex8.sinchat.entity.Message;

public class ChatReportSendEvent {
    private Message message;
    public ChatReportSendEvent(Message message){
        this.message = message;
    }

    public Message getMessage() {
        return message;
    }
}
