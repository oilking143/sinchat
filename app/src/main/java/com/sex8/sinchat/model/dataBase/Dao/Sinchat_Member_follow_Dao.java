package com.sex8.sinchat.model.dataBase.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.sex8.sinchat.model.dataBase.Entity.Member;
import com.sex8.sinchat.model.dataBase.Entity.Member_Follow;

import java.util.List;

@Dao
public interface Sinchat_Member_follow_Dao {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  void insert(Member_Follow member);

  @Query("SELECT * From sinchat_member_follow LEFT JOIN sinchat_member ON sinchat_member_follow.uid = sinchat_member.uid ORDER BY username ASC")
  LiveData<Member> query();

  @Query("SELECT * From sinchat_member_follow")
  LiveData<List<Member_Follow>> queryAll();

  @Delete
  void delete(Member_Follow member_follow);

}
