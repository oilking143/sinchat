package com.sex8.sinchat.model.dataBase.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.sex8.sinchat.model.dataBase.Entity.Member;
import com.sex8.sinchat.model.dataBase.Entity.Member_Fan;

import java.util.List;

@Dao
public interface Sinchat_Member_fan_Dao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Member_Fan member);

    @Query("SELECT * From sinchat_member_fan LEFT JOIN sinchat_member ON sinchat_member_fan.uid = sinchat_member.uid ORDER BY username ASC")
    LiveData<List<Member>> query();

    @Query("SELECT * From sinchat_member_fan WHERE uid=:uid")
    LiveData<Member_Fan> querybyuid(long uid);

    @Delete
    void delete(Member_Fan member_fan);
}
