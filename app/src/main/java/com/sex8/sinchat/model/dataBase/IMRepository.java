package com.sex8.sinchat.model.dataBase;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.sex8.sinchat.IMMessageApplication;
import com.sex8.sinchat.model.dataBase.Dao.Area_List_Dao;
import com.sex8.sinchat.model.dataBase.Dao.ChatRoom_History_Dao;
import com.sex8.sinchat.model.dataBase.Dao.ChatRoom_Info_Dao;
import com.sex8.sinchat.model.dataBase.Dao.ChatRoom_Info_Local_Dao;
import com.sex8.sinchat.model.dataBase.Dao.Myroom_List_Dao;
import com.sex8.sinchat.model.dataBase.Dao.Sinchat_Member_Dao;
import com.sex8.sinchat.model.dataBase.Dao.Sinchat_Member_fan_Dao;
import com.sex8.sinchat.model.dataBase.Dao.Sinchat_Member_follow_Dao;
import com.sex8.sinchat.model.dataBase.Dao.Sinchat_Member_friend_Dao;
import com.sex8.sinchat.model.dataBase.Entity.Area_List;
import com.sex8.sinchat.model.dataBase.Entity.Chatroom_History;
import com.sex8.sinchat.model.dataBase.Entity.Chatroom_Info;
import com.sex8.sinchat.model.dataBase.Entity.Chatroom_Info_Local;
import com.sex8.sinchat.model.dataBase.Entity.Member;
import com.sex8.sinchat.model.dataBase.Entity.Member_Fan;
import com.sex8.sinchat.model.dataBase.Entity.Member_Follow;
import com.sex8.sinchat.model.dataBase.Entity.Member_Friend;
import com.sex8.sinchat.model.dataBase.Entity.Myroom_List;

import java.util.List;

public class IMRepository {

    private static IMRepository instance;
    private ChatRoom_Info_Dao chatRoom_dao;
    private ChatRoom_Info_Local_Dao chatRoomLocal_dao;
    private Area_List_Dao area_list_dao;
    private ChatRoom_History_Dao history_dao;
    private Myroom_List_Dao my_room_list_dao;
    private Sinchat_Member_Dao sinchat_member_dao;
    private Sinchat_Member_fan_Dao member_fan_dao;
    private Sinchat_Member_friend_Dao member_friend_dao;
    private Sinchat_Member_follow_Dao member_follow_dao;

    IMRepository(Application application)
    {
        instance = this;

        GoogleRoomUtils db = GoogleRoomUtils.getDatabase(application);
        chatRoom_dao = db.info_dao();
        history_dao = db.history_dao();
        sinchat_member_dao = db.member_dao();
        member_fan_dao = db.member_fan_dao();
        member_friend_dao = db.member_friend_dao();
        member_follow_dao = db.member_follow_dao();
        my_room_list_dao=db.my_room_list_dao();
        area_list_dao=db.area_list_dao();
        chatRoomLocal_dao=db.chatRoomLocal_dao();
    }

    public static IMRepository getInstance(){
        if (instance==null){
            return new IMRepository(IMMessageApplication.application);
        }else{
            return instance;
        }
    }

    public void insertMember(final Member member) {
        GoogleRoomUtils.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                sinchat_member_dao.insert(member);
            }
        });
    }

    public void updateMember(final Member member) {
        GoogleRoomUtils.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                sinchat_member_dao.updateMember(member);
            }
        });
    }


    public void insertMember_fan(final Member_Fan member_fan) {
        GoogleRoomUtils.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                member_fan_dao.insert(member_fan);
            }
        });
    }


    public void insertMember_friend(final Member_Friend member_friend) {
        GoogleRoomUtils.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                member_friend_dao.insert(member_friend);
            }
        });
    }

    public void insertMy_Room_List(final Myroom_List myroom_list) {
        GoogleRoomUtils.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                my_room_list_dao.insert(myroom_list);
            }
        });
    }

    public void insertChatRoom(final Chatroom_Info chatroon_Info)
    {
        GoogleRoomUtils.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                chatRoom_dao.addChatRoom(chatroon_Info);
            }
        });
    }

    public void insertChatRooms(final List<Chatroom_Info> chatroon_Infos)
    {
        GoogleRoomUtils.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                chatRoom_dao.addChatRooms(chatroon_Infos);
            }
        });
    }

    public void insertAreaLists( List<Area_List> area_lists)
    {
        GoogleRoomUtils.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                area_list_dao.addChatRooms(area_lists);
            }
        });
    }

    public void updateChatRoom(final Chatroom_Info chatroon_Info)
    {
        GoogleRoomUtils.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                chatRoom_dao.updateChatRoom(chatroon_Info);
            }
        });
    }

    public void deleteChatRoom(final Chatroom_Info chatroon_Info)
    {
        GoogleRoomUtils.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                chatRoom_dao.deleteChatRoom(chatroon_Info);
            }
        });
    }

    public LiveData<List<Area_List>> getAreaList() {
        return area_list_dao.queryAll();
    }

    public List<Area_List> getAreaArrayList() {
        return area_list_dao.queryArraylist();
    }

    public LiveData<List<Member>> getFriendData() {
        return sinchat_member_dao.queryFriend();
    }

    public LiveData<List<Member>> getFanData() {
        return sinchat_member_dao.queryFan();
    }

    public LiveData<List<Member>> getFollowData() {
        return sinchat_member_dao.queryFollow();
    }

    public LiveData<List<Member>> getMemberData() {
        return sinchat_member_dao.query();
    }

    public LiveData<Member> getMemberSearchData(long uid) {
        return sinchat_member_dao.queryByUid(uid);
    }

    public LiveData<Member> getMemberSearchDataByName(String userName) {
        return sinchat_member_dao.queryByName(userName);
    }

    public LiveData<List<Chatroom_Info>> getGroupMsgData() {
        return chatRoom_dao.getLastChatroomInfoListByRoomID();
    }


    public LiveData<List<Chatroom_Info>> getPartyInfoData() {
        return chatRoom_dao.getLastPartyInfoListByRoomID();
    }

    public LiveData<List<Chatroom_Info>> getMyPartyInfoData() {
        return chatRoom_dao.getLastMyPartyInfoListByRoomID();
    }
//    public LiveData<List<Chatroom_Info>> getMyGroupInfoData() {
//        return chatRoom_dao.getMyGroupInfoListByRoomID();
//    }

    public LiveData<List<Chatroom_Info>> getMyGroupInfoData() {
        return chatRoom_dao.getMyGroupInfoListByRoomID();
    }

    public LiveData<Myroom_List> queryMyRoomByRoomID(int chatRoomId) {
        return my_room_list_dao.queryMyRoomByRoomId(chatRoomId);
    }

    public LiveData<List<Myroom_List>> queryAllMyRoomList() {
        return my_room_list_dao.queryAll();
    }

    public LiveData<List<Chatroom_Info>> getPrivateInfoData() {
        return chatRoom_dao.getLastPrivateroomInfoListByRoomID();
    }

    public LiveData<List<Chatroom_Info>> getGroupList(int GroupID) {
        return chatRoom_dao.getChatroomInfoListByGroupID(GroupID);
    }

    public LiveData<Chatroom_Info> getChatroomInfo(int chatroomId) {
        return chatRoom_dao.getChatroomInfoByRoomId(chatroomId);
    }

    public LiveData<List<Chatroom_History>> getChatHistoryLast50(int chatRoomId) {
        return history_dao.queryRoomChatLast50(chatRoomId);
    }

    public LiveData<List<Chatroom_History>> getChatHistory50ByLastMsgid(int chatRoomId, String lastMsgId) {
        return history_dao.queryRoomChatByLastMsgid(chatRoomId, lastMsgId);
    }

    public LiveData<List<Chatroom_History>> getAllPrivateHistoryLastMessage() {
        return history_dao.getAllPrivateHistoryLastMessage();
    }

    public LiveData<List<Chatroom_History>> getChatHistoryByRoom(int chatRoomId) {
        return history_dao.queryRoomChat(chatRoomId);
    }

    public LiveData<List<Chatroom_Info>> getGroupWholeList() {
        return chatRoom_dao.getGroupId();
    }

    public LiveData<List<Chatroom_Info_Local>> getMyGroupRoomLocals() {
        return chatRoomLocal_dao.getMyGroupRoomLocalInfos();
    }

    public LiveData<List<Chatroom_Info_Local>> getMyPrivateRoomLocals() {
        return chatRoomLocal_dao.getMyPrivateRoomLocalInfos();
    }

    public LiveData<List<Chatroom_Info_Local>> getAllPrivateLastMessage() {
        return chatRoomLocal_dao.getAllPrivateLastMessage();
    }

    public LiveData<List<Chatroom_Info_Local>> getAllGroupLastMessage() {
        return chatRoomLocal_dao.getAllGroupLastMessage();
    }
    public LiveData<Chatroom_Info_Local> getChatRoomLocalInfoByRoomId(int chatRoomId) {
        return chatRoomLocal_dao.getChatRoomLocalInfoByRoomId(chatRoomId);
    }

    public LiveData<List<Chatroom_Info_Local>> getAllRoomLocalInfos() {
        return chatRoomLocal_dao.getAllRoomLocalInfos();
    }


    public void insertMember_follow(final Member_Follow member_follow) {
        GoogleRoomUtils.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                member_follow_dao.insert(member_follow);
            }
        });
    }

    public void deleteMyroom(Myroom_List myroom){
        GoogleRoomUtils.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                my_room_list_dao.delete(myroom);
            }
        });
    }


    public void deleteFriend(Member_Friend friend){
        GoogleRoomUtils.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                member_friend_dao.delete(friend);
            }
        });
    }

    public void deleteFollow(Member_Follow follow) {
        GoogleRoomUtils.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                member_follow_dao.delete(follow);
            }
        });
    }

    public void deleteFan(Member_Fan fan) {
        GoogleRoomUtils.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                member_fan_dao.delete(fan);
            }
        });
    }

    public void deleteChatHistory(int chatroomId){
        GoogleRoomUtils.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                history_dao.deleteChatHistoryByRoomId(chatroomId);
            }
        });
    }

    public void deleteChat(Chatroom_History chat){
        GoogleRoomUtils.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                history_dao.deleteChat(chat);
            }
        });
    }

    public void insertFriend(Member_Friend friend){
        GoogleRoomUtils.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                member_friend_dao.insert(friend);
            }
        });
    }

    public void insertFollow(Member_Follow follow) {
        GoogleRoomUtils.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                member_follow_dao.insert(follow);
            }
        });
    }

    public void insertFan(Member_Fan fan) {
        GoogleRoomUtils.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                member_fan_dao.insert(fan);
            }
        });
    }

    public void insertChatHistory(Chatroom_History chat) {
        GoogleRoomUtils.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                history_dao.insert(chat);
            }
        });
    }

    public void insertChatHistoryList(List<Chatroom_History> chatList) {
        GoogleRoomUtils.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                history_dao.insertList(chatList);
            }
        });
    }

    public void insertChatRoomLocalInfos(List<Chatroom_Info_Local> roomLocalList) {
        GoogleRoomUtils.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                chatRoomLocal_dao.insertChatRoomLocalInfos(roomLocalList);
            }
        });
    }

    public void insertChatRoomLocalInfo(Chatroom_Info_Local roomLocal) {
        GoogleRoomUtils.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                chatRoomLocal_dao.insertChatRoomLocalInfo(roomLocal);
            }
        });
    }

}
