package com.sex8.sinchat.model.server.pojo.roomlist;

import com.sex8.sinchat.IMMessageApplication;
import com.sex8.sinchat.R;
import com.sex8.sinchat.entity.Message;
import com.sex8.sinchat.utils.SocketCmdUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class RoomMessages {
    private ChatRoom mRoomInfo;
    private List<Message> mMessages = new ArrayList<>();
    private String lastDate;

    public RoomMessages(ChatRoom roomInfo) {
        mRoomInfo = roomInfo;
    }

    public int getRoomId() {
        return mRoomInfo.getRoomId();
    }

    public String getLastDate() {
        return lastDate;
    }

    public void renewHistMessages(List<Message> messages) {
        mMessages.clear();
        lastDate = "";
        String todayDate = Message.utcFormat.format(Calendar.getInstance(Locale.getDefault()).getTime()).split(",")[0];

        for(Message message: messages){
            message.renew();
            if(!lastDate.equals(message.getDateStamp())){
                Message dateStamp = new Message();
                dateStamp.setRoomId(message.getRoomId());
                dateStamp.setType(SocketCmdUtils.TYPE_DATE_STAMP);
                if(todayDate.equals(message.getDateStamp()))
                    dateStamp.setContent(IMMessageApplication.getGlobalContext().getString(R.string.today));
                else
                    dateStamp.setContent(message.getDateStamp());
                lastDate = message.getDateStamp();
                mMessages.add(dateStamp);
            }

//            if(message.getContent().contains("加入"))
//            {
//                message.setType(SocketCmdUtils.TYPE_DATE_STAMP);
//            }



            mMessages.add(message);
        }
    }

    public List<Message> getMessages() {
        return mMessages;
    }
}
