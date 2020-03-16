package com.sex8.sinchat.entity;

import android.util.Log;
import android.util.SparseIntArray;

import com.google.gson.annotations.SerializedName;
import com.sex8.sinchat.utils.SocketCmdUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Message {
    public static SimpleDateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd (EEEE),HH:mm,a");

    @SerializedName("type")
    protected String mType;

    @SerializedName("chattype")
    protected String mRoomType;

    @SerializedName("msgid")
    protected String mMsgId;

    @SerializedName("uid")
    protected long mUserId;


    @SerializedName("roomid")
    protected int mRoomId;

    @SerializedName("isadmin")
    protected int mIsAdmin;

    @SerializedName("nickname")
    protected String mNickname;

    @SerializedName("userimg")
    protected String mUserImageUrl;

    @SerializedName("content")
    protected Object mContent;

    @SerializedName("timestamp")
    protected long mTimeStamp;

    @SerializedName("time")
    protected String mFormatTime;

    @SerializedName("ampm")
    protected String mAmPm;

    @SerializedName("width")
    protected String width;

    @SerializedName("height")
    protected String height;

    @SerializedName("commentArray")
    public SparseIntArray commentArray;

    private String dateStamp;

    private String JsonDateData="";

    public void setmType(String mType) {
        this.mType = mType;
    }

    public String getmRoomType() {
        return mRoomType;
    }

    public void setmRoomType(String mRoomType) {
        this.mRoomType = mRoomType;
    }

    public void setmMsgId(String mMsgId) {
        this.mMsgId = mMsgId;
    }

    public void setmNickname(String mNickname) {
        this.mNickname = mNickname;
    }


    public void setmRoomId(int mRoomId) {
        this.mRoomId = mRoomId;
    }

    public void setmUserImageUrl(String mUserImageUrl) {
        this.mUserImageUrl = mUserImageUrl;
    }

    public void setmContent(Object mContent) {
        this.mContent = mContent;
    }

    public String getUserName() {
        return mNickname == null ? "" : mNickname;
    }

    public long getUserId() {
        return mUserId;
    }

    public void setUserId(long mUserId) {
        this.mUserId = mUserId;
    }


    public String getUserImageUrl() {
        return mUserImageUrl == null ? "" : mUserImageUrl;
    }

    public String getContent() {
        return mContent == null ? "" : String.valueOf(mContent);
    }

    public void setContent(String content) {
        this.mContent = content;
    }

    public long getTimeStamp() {
        return mTimeStamp;
    }

    public void setTimeStamp(long mTimeStamp) {
        this.mTimeStamp = mTimeStamp;
    }

    public int getRoomId() {
        return mRoomId;
    }

    public void setRoomId(int mRoomId) {
        this.mRoomId = mRoomId;
    }

    public boolean isAdmin() {
        return mIsAdmin == 1;
    }

    public String getMsgId() {
        return mMsgId == null ? "" : mMsgId;
    }

    public String getType() {
        return mType == null ? "" : mType;
    }

    public void setType(String mType) {
        this.mType = mType;
    }

    public String getJsonDateData() {

        if(JsonDateData.length()>10)
        {
            String inputPattern = "HH:mm:ss";
            String outputPattern = "HH:mm";
            SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
            SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);
            inputFormat.setTimeZone(TimeZone.getTimeZone("GMT+8"));
            outputFormat.setTimeZone(TimeZone.getDefault());
            Log.d("timeZone",TimeZone.getDefault().getDisplayName(false, TimeZone.SHORT));
            Date date = null;
            String str = null;

            String time=JsonDateData.substring(11);
            try {
                date = inputFormat.parse(time);
                outputFormat.setTimeZone(TimeZone.getDefault());
                str = outputFormat.format(date);
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
            return str;



        }
        else
        {
           return null;
        }

    }

    public void setJsonDateData(String jsonDateData) {
        JsonDateData = jsonDateData;
    }

    public String getFormatTime() {
        return mFormatTime == null ? "" : mFormatTime;
    }

    public void setFormatTime(String mFormatTime) {
        this.mFormatTime = mFormatTime;
    }

    public String getAmPm() {
        return mAmPm == null ? "" : mAmPm;
    }

    public void setAmPm(String mAmPm) {
        this.mAmPm = mAmPm;
    }

    public boolean isImage() {
        return getType().endsWith(SocketCmdUtils.TYPE_IMAGE);
    }

    public int getWidth() {
        if(width != null){
            try {
                return Integer.parseInt(width);
            }catch (NumberFormatException e){

            }
        }
        return 0;
    }

    public void setWidth(int width) {
        this.width = String.valueOf(width);
    }

    public void setHeight(int height) {
        this.height = String.valueOf(height);
    }

    public int getHeight() {
        if(width != null){
            try {
                return Integer.parseInt(height);
            }catch (NumberFormatException e){

            }
        }
        return 0;
    }

    public String getDateStamp() {
        return dateStamp;
    }

    public void setDateStamp(String dateStamp) {
        this.dateStamp = dateStamp;
    }

    public void renew() {
        if(String.valueOf(getTimeStamp()).length() == 10) {
            setTimeStamp(getTimeStamp() * 1000);
        }

        String[] timeArray = utcFormat.format(new Date(getTimeStamp())).split(",");
        setDateStamp(timeArray[0]);
        setFormatTime(timeArray[1]);
        setAmPm(timeArray[2]);

        mContent = String.valueOf(mContent).replaceAll("<br \\/>", "");
        if (isImage()) {
//            String host = IMData.getInstance().getWebSocketInfo().getFileHost();
//            if(host.charAt(host.length() -1) != '/')
//                host += '/';
//            setContent(IMData.getInstance().getWebSocketInfo().getZimgshowServer() + "/" + getContent());
//            setContent(host + getContent());
        }
    }
}
