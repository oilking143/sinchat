package com.sex8.sinchat.viewmodel;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.sex8.sinchat.IMMessageApplication;
import com.sex8.sinchat.model.dataBase.Entity.Chatroom_Info;
import com.sex8.sinchat.model.dataBase.Entity.Member;
import com.sex8.sinchat.model.dataBase.Entity.Member_Fan;
import com.sex8.sinchat.model.dataBase.Entity.Member_Follow;
import com.sex8.sinchat.model.dataBase.Entity.Member_Friend;
import com.sex8.sinchat.model.dataBase.IMRepository;

import java.util.List;

public class ChatRoomInfoViewModel extends AndroidViewModel {

    private IMRepository mRepository;
    private LiveData<List<Chatroom_Info>> partyList;
    private LiveData<List<Chatroom_Info>> groupWholeList;
    private LiveData<List<Chatroom_Info>> myPartyList;
    private LiveData<List<Chatroom_Info>> privateList;
    private LiveData<List<Chatroom_Info>> myGroupList;




    public ChatRoomInfoViewModel()
    {
        super(IMMessageApplication.application);
        mRepository=IMRepository.getInstance();
        partyList=mRepository.getPartyInfoData();
        groupWholeList=mRepository.getGroupWholeList();
        myPartyList=mRepository.getMyPartyInfoData();
        privateList = mRepository.getPrivateInfoData();
        myGroupList= mRepository.getMyGroupInfoData();

    }

    public LiveData<List<Chatroom_Info>> getPartyList() {
        return partyList;
    }

    public LiveData<List<Chatroom_Info>> getGroupWholeList() {
        return groupWholeList;
    }


    public LiveData<List<Chatroom_Info>> getMyPartyList() {
        return myPartyList;
    }

    public LiveData<List<Chatroom_Info>> getPrivateList() {
        return privateList;
    }

    public LiveData<List<Chatroom_Info>> getMyGroupList() {
        return myGroupList;
    }
}
