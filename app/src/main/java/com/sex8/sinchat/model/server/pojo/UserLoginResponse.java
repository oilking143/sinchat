package com.sex8.sinchat.model.server.pojo;

import com.google.gson.annotations.SerializedName;

public class UserLoginResponse {
    @SerializedName("status")
    private int status;

    @SerializedName("msg")
    private String msg;

    @SerializedName("muser")
    private String muser;

    @SerializedName("uid")
    private int uid;

    @SerializedName("mtype")
    private int mtype;

    @SerializedName("mphoto")
    private String mphoto;

    @SerializedName("mapiurl")
    private String mapiurl;

    @SerializedName("imkey")
    public String imkey;

    public int getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }

    public String getMuser() {
        return muser;
    }

    public int getUid() {
        return uid;
    }

    public int getMtype() {
        return mtype;
    }

    public String getMphoto() {
        return mphoto;
    }

    public String getMapiurl() {
        return mapiurl;
    }

    public String getImkey() {
        return imkey;
    }
}
