package com.sex8.sinchat.tools;

import android.view.View;

public abstract class OnClickLimitListener implements View.OnClickListener {
    public static final int VALID_CLICK_LIMIT = 500;
    public static long lastValidClickTime = -1;
    protected long current;

    @Override
    public void onClick(View v) {
        current = System.currentTimeMillis();
        if(current - lastValidClickTime > VALID_CLICK_LIMIT){
            lastValidClickTime = current;
            onClickLimit(v);
        }
    }

    public abstract void onClickLimit(View v);
}
