package com.sex8.sinchat.model.server.pojo.party;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetPartyListResponse {

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

        private int chatroom_id;
        private String chatroom_name;
        private int group_id;
        private String description;
        private int member_limit;
        private String imageDes1Link = null;
        private String imageDes2Link = null;
        private String imageDes3Link = null;
        private String imageLogoLink;


        // Getter Methods

        public int getChatroom_id() {
            return chatroom_id;
        }

        public String getChatroom_name() {
            return chatroom_name;
        }

        public int getGroup_id() {
            return group_id;
        }

        public String getDescription() {
            return description;
        }

        public int getMember_limit() {
            return member_limit;
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
            this.chatroom_id = chatroom_id;
        }

        public void setChatroom_name(String chatroom_name) {
            this.chatroom_name = chatroom_name;
        }

        public void setGroup_id(int group_id) {
            this.group_id = group_id;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public void setMember_limit(int member_limit) {
            this.member_limit = member_limit;
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



    }

}
