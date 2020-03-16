package com.sex8.sinchat.event;

public class ChatRoomUpdateEvent {
    private int roomId;

    public ChatRoomUpdateEvent(int roomId) {
        this.roomId = roomId;
    }

    public int getRoomId() {
        return roomId;
    }
}
