package com.sex8.sinchat.model.dataBase.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.sex8.sinchat.model.dataBase.Entity.Member_Follow;
import com.sex8.sinchat.model.dataBase.Entity.Member_Friend;

import java.util.List;

@Dao
public interface Sinchat_Member_friend_Dao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Member_Friend member);

    @Query("SELECT * From sinchat_member_friend LEFT JOIN sinchat_member ON sinchat_member_friend.uid = sinchat_member.uid ORDER BY username ASC")
    LiveData<List<Member_Friend>> query();

    @Query("SELECT * From sinchat_member_friend WHERE uid=:uid")
    LiveData<Member_Friend> querybyuid(long uid);

    @Query("SELECT * From sinchat_member_friend")
    LiveData<List<Member_Friend>> queryAll();

    @Delete
    void delete(Member_Friend member_friend);

}
