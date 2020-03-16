package com.sex8.sinchat.model.dataBase.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.sex8.sinchat.model.dataBase.Entity.Chatroom_History;
import com.sex8.sinchat.model.dataBase.Entity.Member_Fan;
import com.sex8.sinchat.model.dataBase.Entity.Member_Follow;

import java.util.List;

@Dao
public interface ChatRoom_History_Dao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Chatroom_History chat);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertList(List<Chatroom_History> chatList);

    @Query("SELECT * From chatroom_history WHERE chatRoomId=:chatRoomId and msgid<:lastMsgid ORDER BY msgid DESC LIMIT 0,50")
    LiveData<List<Chatroom_History>> queryRoomChatByLastMsgid(int chatRoomId, String lastMsgid);

    @Query("SELECT * From chatroom_history WHERE chatRoomId=:chatRoomId ORDER BY msgid DESC LIMIT 0,50")
    LiveData<List<Chatroom_History>> queryRoomChatLast50(int chatRoomId);

    @Query("SELECT * From chatroom_history WHERE chatRoomId=:chatRoomId ORDER BY msgid ASC")
    LiveData<List<Chatroom_History>> queryRoomChat(int chatRoomId);

    @Query("DELETE from chatroom_history where chatRoomId=:chatRoomId")
    void deleteChatHistoryByRoomId(int chatRoomId);

    @Query("SELECT * FROM chatroom_history " +
            "LEFT JOIN chatroom_Info ON chatroom_history.chatroomId = chatroom_Info.chatroomId " +
            "WHERE uids IN (SELECT uid FROM sinchat_member_friend) " +
            " GROUP BY chatroom_history.chatroomId ORDER BY chatroom_history.timestamp DESC")
    LiveData<List<Chatroom_History>> getAllPrivateHistoryLastMessage();

    @Query("SELECT * FROM chatroom_history " +
            "LEFT JOIN chatroom_Info ON chatroom_history.chatroomId = chatroom_Info.chatroomId " +
            "WHERE chatroom_history.chatRoomId IN (SELECT chatroomId FROM myroom_list) AND chatroom_Info.groupId != 4 " +
            "GROUP BY chatroom_history.chatroomId ORDER BY chatroom_history.timestamp DESC")
    LiveData<List<Chatroom_History>> getAllGroupHistoryLastMessage();

    @Delete
    void deleteChat(Chatroom_History chat);

}
