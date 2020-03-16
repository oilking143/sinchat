package com.sex8.sinchat.event;

public class EnterRoomResultEvent {
    private boolean mResult;
    private String mMessage;

    /**
     * 0: 成功
     * 1: 開始加入
     * 2: 加入中
     * 3: 房間不存在
     * 4: 沒有權限
     * 5: 房間已滿
     */
    private int mStatus;

    public EnterRoomResultEvent(int status, String message) {
        mMessage = message;
        mStatus = status;
    }

    public String getMessage() {
        return mMessage;
    }

    public int getStatus() {
        return mStatus;
    }
}
