package com.sex8.sinchat.model.dataBase.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.sex8.sinchat.model.dataBase.Entity.Myroom_List;

import java.util.List;

@Dao
public interface Myroom_List_Dao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Myroom_List member);


    @Query("SELECT * From myroom_list")
    LiveData<List<Myroom_List>> queryAll();

    @Query("SELECT * From myroom_list where chatroomId=:chatroomId")
    LiveData<Myroom_List> queryMyRoomByRoomId(int chatroomId);

    @Delete
    void delete(Myroom_List member_follow);
}
