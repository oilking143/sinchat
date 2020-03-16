package com.sex8.sinchat.event;

import com.sex8.sinchat.model.dataBase.Entity.Member;

public class AddFollowEvent {
    private int status;
    private String message;

    private Member member;

    public AddFollowEvent(int status, String message, Member member){
        this.status = status;
        this.message = message;
        this.member = member;
    }


    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public Member getMember() {
        return member;
    }
}
