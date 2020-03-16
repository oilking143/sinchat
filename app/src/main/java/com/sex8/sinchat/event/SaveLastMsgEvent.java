package com.sex8.sinchat.event;

import com.sex8.sinchat.model.dataBase.Entity.Chatroom_Info_Local;

import java.util.List;

public class SaveLastMsgEvent {

    private List<Chatroom_Info_Local> local;

    public SaveLastMsgEvent(List<Chatroom_Info_Local> local){

        this.local=local;

    }

    public List<Chatroom_Info_Local> getLocal() {
        return local;
    }

    public void setLocal(List<Chatroom_Info_Local> local) {
        this.local = local;
    }
}
