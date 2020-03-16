package com.sex8.sinchat.model.server.pojo.roomlist;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class GetRoomListDetailResponse {

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

    public List<Data> getData() {
        if(data != null && data.size() > 0)
            return data;
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

        private int chatroomId;
        private String chatroomName;
        private int groupId;
        private String description;
        private String memberLimit;
        private String imageDes1Link;
        private String imageDes2Link;
        private String imageDes3Link;
        private String imageLogoLink;
        private long redEventTimes;
        private ArrayList<String> adminIds;
        private ArrayList<String> memberIds;


        // Getter Methods

        public int getChatroom_id() {
            return chatroomId;
        }

        public String getChatroom_name() {
            return chatroomName;
        }

        public int getGroup_id() {
            return groupId;
        }

        public String getDescription() {
            return description;
        }

        public String getMember_limit() {
            return memberLimit;
        }

        public String getImageDes1Link() {
            return imageDes1Link;
        }

        public String getImageDes2Link() {
            return imageDes2Link;
        }

        public String getImageDes3Link() {
            return imageDes3Link;
        }

        public String getImageLogoLink() {
            return imageLogoLink;
        }

        // Setter Methods

        public void setChatroom_id(int chatroom_id) {
            this.chatroomId = chatroom_id;
        }

        public void setChatroom_name(String chatroom_name) {
            this.chatroomName = chatroom_name;
        }

        public void setGroup_id(int group_id) {
            this.groupId = group_id;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public void setMember_limit(String member_limit) {
            this.memberLimit = member_limit;
        }

        public void setImageDes1Link(String imageDes1Link) {
            this.imageDes1Link = imageDes1Link;
        }

        public void setImageDes2Link(String imageDes2Link) {
            this.imageDes2Link = imageDes2Link;
        }

        public void setImageDes3Link(String imageDes3Link) {
            this.imageDes3Link = imageDes3Link;
        }

        public void setImageLogoLink(String imageLogoLink) {
            this.imageLogoLink = imageLogoLink;
        }

        public ArrayList<String> getAdminIds() {
            return adminIds;
        }

        public void setAdminIds(ArrayList<String> adminIds) {
            this.adminIds = adminIds;
        }

        public ArrayList<String> getMemberIds() {
            return memberIds;
        }

        public void setMemberIds(ArrayList<String> memberIds) {
            this.memberIds = memberIds;
        }

        public long getRedEventTimes() {
            return redEventTimes;
        }

        public void setRedEventTimes(long redEventTimes) {
            this.redEventTimes = redEventTimes;
        }
    }




}
