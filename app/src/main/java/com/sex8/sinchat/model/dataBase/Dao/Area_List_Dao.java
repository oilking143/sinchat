package com.sex8.sinchat.model.dataBase.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;


import com.sex8.sinchat.model.dataBase.Entity.Area_List;

import java.util.List;

@Dao
public interface Area_List_Dao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void addChatRooms(List<Area_List> lists);

    @Query("SELECT * From area_list")
    LiveData<List<Area_List>> queryAll();

    @Query("SELECT * From area_list")
    List<Area_List> queryArraylist();

}
