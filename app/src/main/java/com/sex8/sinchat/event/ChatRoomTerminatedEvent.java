package com.sex8.sinchat.event;

import androidx.annotation.StringRes;

public class ChatRoomTerminatedEvent {
    @StringRes
    private int mTitleResId;

    public ChatRoomTerminatedEvent(@StringRes int resId) {
        mTitleResId = resId;
    }

    @StringRes
    public int getTitleResId() {
        return mTitleResId;
    }
}
