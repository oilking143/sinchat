package com.sex8.sinchat.model.dataBase.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.sex8.sinchat.model.dataBase.Entity.Chatroom_Info;

import java.util.List;

@Dao
public interface ChatRoom_Info_Dao {

    @Query("select * FROM Chatroom_Info")
    LiveData<List<Chatroom_Info>> getChatroom_infoList();
   // Chatroom_Info


    @Query("SELECT * From myroom_list LEFT JOIN chatroom_info ON myroom_list.chatroomId = chatroom_info.chatroomId ORDER BY chatroomName ASC")
    LiveData<List<Chatroom_Info>> getLastChatroomInfoListByRoomID();

    @Query("SELECT * From sinchat_member_friend LEFT JOIN chatroom_info ON sinchat_member_friend.uid = chatroom_info.uids WHERE groupId = 4")
    LiveData<List<Chatroom_Info>> getLastPrivateroomInfoListByRoomID();

    @Query("SELECT * From chatroom_info WHERE groupId not IN (1,3,4) ORDER BY chatroomName ASC")
    LiveData<List<Chatroom_Info>> getLastPartyInfoListByRoomID();

    @Query("SELECT * From myroom_list LEFT JOIN chatroom_info ON myroom_list.chatroomId = chatroom_info.chatroomId  WHERE groupId not IN (1,3) ORDER BY chatroomId ASC")
    LiveData<List<Chatroom_Info>> getLastMyPartyInfoListByRoomID();

    @Query("SELECT * From myroom_list LEFT JOIN chatroom_info ON myroom_list.chatroomId = chatroom_info.chatroomId  WHERE groupId not IN (1,3,4) ORDER BY chatroomId ASC")
    LiveData<List<Chatroom_Info>> getMyGroupInfoListByRoomID();

    @Query("select * FROM Chatroom_Info WHERE groupId = :groupID")
    LiveData<List<Chatroom_Info>> getChatroomInfoListByGroupID(int groupID);

    @Query("select * FROM Chatroom_Info WHERE chatRoomId = :chatRoomId")
    LiveData<Chatroom_Info> getChatroomInfoByRoomId(int chatRoomId);

    @Query("select distinct(groupId) as memberLimit,membercounts,chatRoomId,groupId,groupName FROM Chatroom_Info")
    LiveData<List<Chatroom_Info>> getGroupId();





    @Update()
    void updateChatRoom(Chatroom_Info Chatroom_info);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addChatRoom(Chatroom_Info Chatroom_info);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addChatRooms(List<Chatroom_Info> infos);

    @Delete()
    void deleteChatRoom(Chatroom_Info Chatroom_info);

}
