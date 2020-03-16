package com.sex8.sinchat.model.dataBase.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.sex8.sinchat.model.dataBase.Entity.Chatroom_History;
import com.sex8.sinchat.model.dataBase.Entity.Chatroom_Info;
import com.sex8.sinchat.model.dataBase.Entity.Chatroom_Info_Local;

import java.util.List;

@Dao
public interface ChatRoom_Info_Local_Dao {

    @Query("SELECT * From myroom_list " +
            "LEFT JOIN chatroom_info ON myroom_list.chatroomId = chatroom_info.chatroomId " +
            "LEFT JOIN chatroom_info_local ON myroom_list.chatroomId = chatroom_info_local.chatroomId " +
            "WHERE groupId = 4 ORDER BY chatroomId DESC")
    LiveData<List<Chatroom_Info_Local>> getMyPrivateRoomLocalInfos();

    @Query("SELECT * From myroom_list " +
            "LEFT JOIN chatroom_info ON myroom_list.chatroomId = chatroom_info.chatroomId LEFT JOIN chatroom_info_local ON myroom_list.chatroomId = chatroom_info_local.chatroomId WHERE groupId != 4 ORDER BY chatroomId DESC")
    LiveData<List<Chatroom_Info_Local>> getMyGroupRoomLocalInfos();

    @Query("SELECT * FROM chatroom_info_local LEFT JOIN chatroom_Info ON chatroom_info_local.chatroomId = chatroom_Info.chatroomId WHERE uids IN (SELECT uid FROM sinchat_member_friend)")
    LiveData<List<Chatroom_Info_Local>> getAllPrivateLastMessage();

    @Query("SELECT * FROM chatroom_info_local LEFT JOIN chatroom_Info ON chatroom_info_local.chatroomId = chatroom_Info.chatroomId WHERE chatroom_info_local.chatRoomId IN (SELECT chatroomId FROM myroom_list) AND chatroom_Info.groupId != 4")
    LiveData<List<Chatroom_Info_Local>> getAllGroupLastMessage();

    @Query("SELECT * From chatroom_info_local")
    LiveData<List<Chatroom_Info_Local>> getAllRoomLocalInfos();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertChatRoomLocalInfos(List<Chatroom_Info_Local> room_locals);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertChatRoomLocalInfo(Chatroom_Info_Local room_local);

    @Query("SELECT * From chatroom_info_local WHERE chatRoomId=:chatRoomId")
    LiveData<Chatroom_Info_Local> getChatRoomLocalInfoByRoomId(int chatRoomId);

}
