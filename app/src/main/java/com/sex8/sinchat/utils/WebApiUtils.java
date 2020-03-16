package com.sex8.sinchat.utils;

import android.util.Log;

import com.google.gson.JsonObject;
import com.sex8.sinchat.Constant;
import com.sex8.sinchat.IMMessageApplication;
import com.sex8.sinchat.model.server.pojo.ResponseBase;

import retrofit2.Response;

public class WebApiUtils {
    private static final String TAG = "WebApiUtils";

    private static final String KEY_V = "v";
    private static final String KEY_NA = "na";
    private static final String KEY_PS = "ps";
    private static final String KEY_VERIFY = "verify";
    private static final String KEY_IDC = "idc";

    private static final String KEY_UID = "uid";
    private static final String KEY_MTYPE = "mtype";
    private static final String KEY_MUSER = "muser";
    private static final String KEY_MPHOTO = "mphoto";
    private static final String KEY_MSERVER = "mserver";
    private static final String KEY_MAPIURL = "mapiurl";
    private static final String KEY_MCHECK = "mcheck";

    private static final String VALUE_EMPTY = "";
    private static final String VALUE_IM_LOGIN = "im_login";

    static public boolean checkResponse(Response<? extends ResponseBase> response) {
        return response != null && checkResponse(response.body());
    }

    static public boolean checkResponse(ResponseBase response) {

        return response != null && (response.getStatusCode() == Constant.RESPONSE_SUCCESS_V4 );
    }

    static public String cmdLogin(String account, String pwd, String verify) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(KEY_V, VALUE_IM_LOGIN);
        jsonObject.addProperty(KEY_NA, account);
        jsonObject.addProperty(KEY_PS, pwd);
        jsonObject.addProperty(KEY_VERIFY, verify);
        jsonObject.addProperty(KEY_IDC, VALUE_EMPTY);
        if(IMMessageApplication.Show_Log) Log.d(TAG, "login cmd = " + jsonObject.toString());
        return jsonObject.toString();
    }

    static public String cmdIMKey(String uid, String nickName, String imageUrl) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(KEY_UID, uid);
        jsonObject.addProperty(KEY_MTYPE, "1");
        jsonObject.addProperty(KEY_MUSER, nickName);
        jsonObject.addProperty(KEY_MPHOTO, imageUrl);
        jsonObject.addProperty(KEY_MSERVER, "123.12.123.12");
        jsonObject.addProperty(KEY_MAPIURL, "123.123.123.123");
        jsonObject.addProperty(KEY_MCHECK, "1");
        if(IMMessageApplication.Show_Log) Log.d(TAG, "imkey cmd = " + jsonObject.toString());
        return jsonObject.toString();
    }
}
