package com.sex8.sinchat.event;


import com.sex8.sinchat.model.server.ff_list.CreateResponse;

public class CreateEvent {
    private int status;

    private int roomID;
    private String avatar;
    private String userName;
    private CreateResponse.Data data;

   public CreateEvent(int status, String userName,String avatar,CreateResponse.Data data)
   {
       this.status = status;
       this.avatar = avatar;
       this.userName=userName;
       this.data=data;
   }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getRoomID() {
        return roomID;
    }

    public void setRoomID(int roomID) {
        this.roomID = roomID;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public CreateResponse.Data getData() {
        return data;
    }

    public void setData(CreateResponse.Data data) {
        this.data = data;
    }
}
