package com.sex8.sinchat.entity;

import android.net.Uri;

public class UserInfo {
    private long uid;
    private String imkey;
    private String nickName;
    private String imageUrl;
    private int isAdmin;
    private String token;
    private String coverURl;

    public UserInfo(int uid, String imkey, String nickName, String imageUrl, int isAdmin) {
        this.uid = uid;
        this.imkey = imkey;
        this.nickName = nickName;
        this.imageUrl = imageUrl;
        this.isAdmin = isAdmin;
        this.token = Uri.encode(imkey);
    }


    public String getImKey() {
        return imkey;
    }

    public String getNickName() {
        return nickName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getCoverUrl(){
        return coverURl;
    }

    public int getIsAdmin() {
        return isAdmin;
    }

    public String getToken(){
        return token;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getImkey() {
        return imkey;
    }

    public void setImkey(String imkey) {
        this.imkey = imkey;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setIsAdmin(int isAdmin) {
        this.isAdmin = isAdmin;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setCoverURl(String coverURl) {
        this.coverURl = coverURl;
    }

    public boolean isGuest() {
        return uid == -1;
    }
}
