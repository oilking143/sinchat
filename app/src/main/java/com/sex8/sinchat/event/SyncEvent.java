package com.sex8.sinchat.event;

public class SyncEvent {
    private int RoomID;
    private String MsgId;


    public SyncEvent(int RoomID,String MsgId)
    {
        this.RoomID=RoomID;
        this.MsgId=MsgId;
    }

    public int getRoomID() {
        return RoomID;
    }

    public void setRoomID(int roomID) {
        RoomID = roomID;
    }

    public String getMsgId() {
        return MsgId;
    }

    public void setMsgId(String msgId) {
        MsgId = msgId;
    }
}
