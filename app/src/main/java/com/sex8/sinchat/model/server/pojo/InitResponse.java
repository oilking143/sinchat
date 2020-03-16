package com.sex8.sinchat.model.server.pojo;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class InitResponse extends ResponseBase {

    @SerializedName("error")
    private Error error;

    @SerializedName("data")
    private List<Data> data;


    public Error getError() {
        if(error != null)
            return error;
        else
            return null;
    }

    public Data getData() {
        if(data != null && data.size() > 0)
            return data.get(0);
        else
            return null;
    }

    public class Error
    {
        int code;
        String message;

        public int getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }
    }

    public class Data{
        private String groupAdminId;
        private String groupWelfareId;
        private String groupWolfId;
        private String workermanServer;
        private String zimgshowserver;
        private ArrayList<String> fileHosts = new ArrayList <String> ();
        private version VersionObject;
        private userVersion UserVersionObject;


        public String getGroupAdminId() {
            return groupAdminId;
        }

        public String getGroupWelfareId() {
            return groupWelfareId;
        }

        public String getGroupWolfId() {
            return groupWolfId;
        }

        public String getWorkermanServer() {
            return workermanServer;
        }

        public String getZimgshowserver() {
            return zimgshowserver;
        }

        public ArrayList<String> getFileHosts() {
            return fileHosts;
        }

        public version getVersionObject() {
            return VersionObject;
        }

        public userVersion getUserVersionObject() {
            return UserVersionObject;
        }

        public class version{
          private int chatroomList;

          public int getChatroomList() {
              return chatroomList;
          }
      }


      public class userVersion{
          private int publicRoom;
          private int fans;
          private int follows;
          private int friends;

          public int getPublicRoom() {
              return publicRoom;
          }

          public int getFans() {
              return fans;
          }

          public int getFollows() {
              return follows;
          }

          public int getFriends() {
              return friends;
          }
      }

    }

    public class ApiEncrypt{
        private int value;
        public String k, v;
        public boolean isEncrypt(){
            return value == 1;
        }
    }
}
