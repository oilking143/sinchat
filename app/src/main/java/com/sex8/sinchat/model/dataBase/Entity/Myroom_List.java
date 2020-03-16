package com.sex8.sinchat.model.dataBase.Entity;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "myroom_list",
        indices = @Index(value = {"chatroomId"}, unique = true))
public class Myroom_List {

    @PrimaryKey
    private int chatroomId;


    public int getChatroomId() {
        return chatroomId;
    }

    public void setChatroomId(int chatroomId) {
        this.chatroomId = chatroomId;
    }
}
