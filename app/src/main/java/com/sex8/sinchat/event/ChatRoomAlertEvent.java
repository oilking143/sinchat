package com.sex8.sinchat.event;

import androidx.annotation.StringRes;

public class ChatRoomAlertEvent {
    @StringRes
    private int mTitleResId;

    public ChatRoomAlertEvent(@StringRes int resId) {
        mTitleResId = resId;
    }

    @StringRes
    public int getTitleResId() {
        return mTitleResId;
    }
}
