package com.sex8.sinchat.utils;

import android.text.InputFilter;
import android.text.Spanned;

public class NameInputFilter implements InputFilter {
    private int mMaxLen = 80;

    public NameInputFilter() {
    }

    public NameInputFilter(int maxLen) {
        this.mMaxLen = maxLen;
    }

    @Override
    public CharSequence filter(CharSequence charSequence, int start, int end, Spanned dest, int dstart, int dend) {
        int dindex = 0;
        int count = 0; // 判斷是否到達最大長度
        while (count <= mMaxLen && dindex < dest.length()) {
            char c = dest.charAt(dindex++);
            if (c < 128) {// 按ASCII碼錶0-127算
                count = count + 1;
            } else {
                count = count + 2;
            }
        }
        if (count > mMaxLen) {
            return dest.subSequence(0, dindex - 1);
        }

        int sindex = 0;
        while (count <= mMaxLen && sindex < charSequence.length()) {
            char c = charSequence.charAt(sindex++);
            if (c < 128) {
                count = count + 1;
            } else {
                count = count + 2;
            }
        }

        if (count > mMaxLen) {
            sindex--;
        }
        return charSequence.subSequence(0, sindex);
    }
}
