package com.sex8.sinchat.viewmodel;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.sex8.sinchat.IMMessageApplication;
import com.sex8.sinchat.model.dataBase.Entity.Chatroom_History;
import com.sex8.sinchat.model.dataBase.Entity.Myroom_List;
import com.sex8.sinchat.model.dataBase.IMRepository;

import java.util.List;

public class SinchatChatRoomViewModel extends AndroidViewModel {

    private IMRepository mRepository;

    public SinchatChatRoomViewModel() {
        super(IMMessageApplication.application);
        mRepository= IMRepository.getInstance();
    }


    public LiveData<List<Chatroom_History>> getLastCharHistory(int chatRoomId) {
        return mRepository.getChatHistoryLast50(chatRoomId);
    }

    public LiveData<List<Chatroom_History>> getChatHistory50ByLastMsgid(int chatRoomId, String lastMsgId) {
        return mRepository.getChatHistory50ByLastMsgid(chatRoomId, lastMsgId);
    }

    public LiveData<List<Chatroom_History>> getChatHistoryByRoom(int chatRoomId) {
        return mRepository.getChatHistoryByRoom(chatRoomId);
    }

    public void insertChatHistory(Chatroom_History chat){
        mRepository.insertChatHistory(chat);
    }

    public void insertChatHistoryList(List<Chatroom_History> chatList){
        mRepository.insertChatHistoryList(chatList);
    }


    public void deleteChatByRoomId(int chatRoomId){
        mRepository.deleteChatHistory(chatRoomId);
    }
}
