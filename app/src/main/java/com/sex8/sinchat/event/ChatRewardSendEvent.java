package com.sex8.sinchat.event;

import com.sex8.sinchat.entity.Message;

public class ChatRewardSendEvent {
    private Message message;
    public ChatRewardSendEvent(Message message){
        this.message = message;
    }

    public Message getMessage() {
        return message;
    }
}
