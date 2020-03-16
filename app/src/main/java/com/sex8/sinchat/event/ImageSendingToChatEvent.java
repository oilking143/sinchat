package com.sex8.sinchat.event;

import com.sex8.sinchat.entity.PhotoData;
import com.sex8.sinchat.model.dataBase.Entity.Chatroom_History;

import java.util.List;

public class ImageSendingToChatEvent {
    private int code;
    private String msg;
    private Chatroom_History chat;

    public ImageSendingToChatEvent(int code, String msg, Chatroom_History chat){
        this.code = code;
        this.msg = msg;
        this.chat = chat;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Chatroom_History getChat() {
        return chat;
    }

    public void setChat(Chatroom_History chat) {
        this.chat = chat;
    }



}
