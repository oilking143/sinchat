package com.sex8.sinchat.model.dataBase;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

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
import com.sex8.sinchat.model.dataBase.Entity.Chatroom_Info;
import com.sex8.sinchat.model.dataBase.Entity.Chatroom_History;
import com.sex8.sinchat.model.dataBase.Entity.Chatroom_Info_Local;
import com.sex8.sinchat.model.dataBase.Entity.Member;
import com.sex8.sinchat.model.dataBase.Entity.Member_Follow;
import com.sex8.sinchat.model.dataBase.Entity.Member_Friend;
import com.sex8.sinchat.model.dataBase.Entity.Member_Fan;
import com.sex8.sinchat.model.dataBase.Entity.Myroom_List;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**1.2020-2-24  v1.0 初次建立
 * 2.2020-3-10  v2.0
 *      建立 ChatRoom_Info
 *      建立 ChatRoom_History
 *      建立 Sinchat_Member
 *      建立 Myroom_List
 *      建立 Sinchat_Member_fan
 *      建立 Sinchat_Member_friend
 *      建立 Sinchat_Member_follow
 *      建立 Area_List
 *      建立 ChatRoom_Info_Local
 *
 *
 * **/
@Database(entities = {Chatroom_Info.class, Chatroom_Info_Local.class, Chatroom_History.class, Member.class, Member_Friend.class,
        Member_Follow.class, Member_Fan.class, Myroom_List.class, Area_List.class}, version = 2, exportSchema = false)
    public abstract class GoogleRoomUtils extends RoomDatabase {
    abstract ChatRoom_Info_Dao info_dao();
    abstract ChatRoom_History_Dao history_dao();
    abstract Sinchat_Member_Dao member_dao();
    abstract Myroom_List_Dao my_room_list_dao();
    abstract Sinchat_Member_fan_Dao member_fan_dao();
    abstract Sinchat_Member_friend_Dao member_friend_dao();
    abstract Sinchat_Member_follow_Dao member_follow_dao();
    abstract Area_List_Dao area_list_dao();
    abstract ChatRoom_Info_Local_Dao chatRoomLocal_dao();
    private static GoogleRoomUtils sInstance;
    /**不知道銃三小的參數*/
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);


    public static GoogleRoomUtils getDatabase(Context context) {
        if (sInstance == null) {
            sInstance = Room.databaseBuilder(context.getApplicationContext(), GoogleRoomUtils.class,
                    "IMRoomDB.db")
                    .build();
        }
        return sInstance;
    }



    public static void onDestroy() {
        sInstance = null;
    }

//    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
//
//        @Override
//        public void onOpen(@NonNull SupportSQLiteDatabase db) {
//            super.onOpen(db);
//            ChatRoom_Info_Dao dao = sInstance.dao();
//            Chatroom_Info chatroominfo=new Chatroom_Info(1234567890,"TestRoom");
//            dao.addUser(chatroominfo);
//        }
//    };


}
