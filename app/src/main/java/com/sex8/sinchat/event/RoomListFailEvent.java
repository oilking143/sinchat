package com.sex8.sinchat.event;

public class RoomListFailEvent {
    private Throwable mError;

    public RoomListFailEvent(Throwable error) {
        mError = error;
    }
}
