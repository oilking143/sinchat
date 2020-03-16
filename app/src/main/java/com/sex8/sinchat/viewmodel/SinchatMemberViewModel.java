package com.sex8.sinchat.viewmodel;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.sex8.sinchat.IMMessageApplication;
import com.sex8.sinchat.model.dataBase.Entity.Member;
import com.sex8.sinchat.model.dataBase.Entity.Member_Fan;
import com.sex8.sinchat.model.dataBase.Entity.Member_Follow;
import com.sex8.sinchat.model.dataBase.Entity.Member_Friend;
import com.sex8.sinchat.model.dataBase.IMRepository;

import java.util.List;

public class SinchatMemberViewModel extends AndroidViewModel {

    private IMRepository mRepository;
    private LiveData<List<Member>> friendData;
    private LiveData<List<Member>> fanData;
    private LiveData<List<Member>> followData;
    private LiveData<List<Member>> memberData;

    public SinchatMemberViewModel()
    {
        super(IMMessageApplication.application);
        mRepository= IMRepository.getInstance();
        friendData=mRepository.getFriendData();
        fanData=mRepository.getFanData();
        followData=mRepository.getFollowData();
        memberData=mRepository.getMemberData();


    }


    public LiveData<List<Member>> getFriendData() {
        return friendData;
    }

    public LiveData<List<Member>> getFanData() {
        return fanData;
    }

    public LiveData<List<Member>> getFollowData() {
        return followData;
    }

    public LiveData<List<Member>> getMemberData() {
        return memberData;
    }

    public LiveData<Member> getMemberSearchData(long uid) {
        return mRepository.getMemberSearchData(uid);
    }

    public LiveData<Member> getMemberSearchDataByName(String name) {
        return mRepository.getMemberSearchDataByName(name);
    }

    public void insertFollow(Member member){
        Member_Follow follow  = new Member_Follow();
        follow.setUid(member.getUid());
        mRepository.insertFollow(follow);
    }

    public void insertFriend(Member member){
        Member_Friend friend  = new Member_Friend();
        friend.setUid(member.getUid());
        mRepository.insertFriend(friend);
    }

    public void insertFan(Member member){
        Member_Fan fan  = new Member_Fan();
        fan.setUid(member.getUid());
        mRepository.insertFan(fan);
    }

    public void deleteFollow(Member member){
        Member_Follow follow  = new Member_Follow();
        follow.setUid(member.getUid());
        mRepository.deleteFollow(follow);
    }

    public void deleteFriend(Member member){
        Member_Friend friend  = new Member_Friend();
        friend.setUid(member.getUid());
        mRepository.deleteFriend(friend);
    }

    public void deleteFan(Member member){
        Member_Fan fan  = new Member_Fan();
        fan.setUid(member.getUid());
        mRepository.deleteFan(fan);
    }
}
