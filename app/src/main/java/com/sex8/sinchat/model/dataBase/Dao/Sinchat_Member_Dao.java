package com.sex8.sinchat.model.dataBase.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.sex8.sinchat.model.dataBase.Entity.Chatroom_Info;
import com.sex8.sinchat.model.dataBase.Entity.Member;

import java.util.List;

@Dao
public interface Sinchat_Member_Dao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Member member);

    @Query("SELECT * From sinchat_member")
    LiveData<List<Member>> query();

    @Query("SELECT * From sinchat_member WHERE username=:userName")
    LiveData<Member> queryByName(String userName);

    @Query("SELECT * From sinchat_member WHERE uid=:uid")
    LiveData<Member> queryByUid(long uid);

    @Query("SELECT * From sinchat_member_friend LEFT JOIN sinchat_member ON sinchat_member_friend.uid = sinchat_member.uid ORDER BY username ASC")
    LiveData<List<Member>> queryFriend();

    @Query("SELECT * From sinchat_member_follow LEFT JOIN sinchat_member ON sinchat_member_follow.uid = sinchat_member.uid " +
            "WHERE sinchat_member_follow.uid NOT IN (SELECT uid From sinchat_member_friend)" +
            "ORDER BY username ASC")
    LiveData<List<Member>> queryFollow();

    @Query("SELECT * From sinchat_member_fan LEFT JOIN sinchat_member ON sinchat_member_fan.uid = sinchat_member.uid " +
            "WHERE sinchat_member_fan.uid NOT IN (SELECT uid From sinchat_member_friend)" +
            "ORDER BY username ASC")
    LiveData<List<Member>> queryFan();

    @Update
    void updateMember(Member member);

    @Delete
    void delete(Member member);
}
