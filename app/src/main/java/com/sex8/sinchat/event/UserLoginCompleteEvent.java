package com.sex8.sinchat.event;

public class UserLoginCompleteEvent {
    private int mStatus;
    private String mMessage;

    static public UserLoginCompleteEvent createSuccessEvent() {
        return new UserLoginCompleteEvent(0, "OK");
    }
    public UserLoginCompleteEvent(int status, String message) {
        mStatus = status;
        mMessage = message;
    }

    public int getStatus() {
        return mStatus;
    }

    public String getMessage() {
        return mMessage;
    }
}
