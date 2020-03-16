package com.sex8.sinchat.utils;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.sex8.sinchat.model.dataBase.Entity.Chatroom_History;
import com.sex8.sinchat.model.dataBase.Entity.Chatroom_Info;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.sex8.sinchat.IMMessageApplication.Show_Log;

public class SocketCmdUtils {
    private static final String TAG = "SocketCmdUtils";

    private static final String KEY_TYPE = "type";
    private static final String KEY_UID = "uid";
    private static final String KEY_IMKEY = "imkey";
    private static final String KEY_CONTENT = "content";
    private static final String KEY_STATUS = "status";
    private static final String KEY_FROM_ROOM_ID = "from_roomid";
    private static final String KEY_ROOM_ID = "roomid";
    private static final String KEY_NICK_NAME = "nickname";
    private static final String KEY_TIME = "time";
    private static final String KEY_USER_IMAGE = "userimg";
    private static final String KEY_CHAT_TYPE = "chattype";
    private static final String KEY_IS_ADMIN = "isadmin";
    private static final String KEY_MSGID = "msgid";
    private static final String KEY_CMD = "cmd";

    private static final String KEY_ROOM_ONLINE = "room_id";

    public static final String TYPE_PING = "ping";
    public static final String TYPE_ONLINE = "online";
    public static final String TYPE_ROOM_LIST = "roomlist"; //成員列表的最後一句
    public static final String TYPE_JOIN_ROOM = "joinroom";
    public static final String TYPE_SAY = "say";
    public static final String TYPE_IMAGE = "image";
    public static final String TYPE_DIRTY = "dirty";
    public static final String TYPE_WRONG_SAY = "wrong_say";
    public static final String TYPE_BLOCK_SAY = "blocksay";
    public static final String TYPE_BLOCK_SAY1 = "block_say";
    public static final String TYPE_CLOSE = "close";
    public static final String TYPE_DATE_STAMP = "date_stamp";
    public static final String TYPE_SYSTEM = "system";
    public static final String TYPE_CHATROOM_ADD = "addchatroom";
    public static final String TYPE_CHATROOM_UPD = "updchatroom";
    public static final String TYPE_CHATROOM_DEL = "delchatroom";
    public static final String TYPE_GROUP_UPD = "updgroup";
    public static final String TYPE_MASTER_GROUP_UPD = "updmastergroup";

    private static final String TYPE_PONG = "pong";

    public static final String TYPE_AUTH = "auth";

    private static final String VALUE_EMPTY = "";
    private static final String VALUE_GROUP = "group";
    public static final String VALUE_FAIL = "fail";
    public static final String VALUE_ROOM_FULL = "RoomFull";

    public static final String CHATTYPE_GROUP = "group";

    public static String cmdPing() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(KEY_TYPE, TYPE_PING);
        if(Show_Log) Log.d(TAG, "ping cmd = " + jsonObject.toString());
        return jsonObject.toString();
    }

    public static String cmdSync(int chacRoomid,String LastMsg) {
        try {
            JSONObject  jsonObject = new JSONObject ();
            jsonObject.put(KEY_CMD,"chat.sync");
            JSONObject data = new JSONObject ();
            data.put(String.valueOf(chacRoomid),LastMsg);
            jsonObject.put("data",data);
            if(Show_Log) Log.d(TAG, "sync cmd = " + jsonObject.toString());
            return jsonObject.toString();
        }
        catch (JSONException j)
        {
            Log.e("ERROR", j.getMessage(), j);
            return "ERROR";
        }
    }



    public static String cmdSay(Chatroom_History chat, Chatroom_Info info) {

        try {
            JSONObject  jsonObject = new JSONObject ();
            jsonObject.put(KEY_CMD,"chat.say");
            JSONObject data = new JSONObject ();
            data.put("roomId", String.valueOf(info.getChatRoomId()));
            data.put("contentType",chat.getChatType());
            switch (info.getGroupId()) {
                case 4:
                    data.put("roomType","private");
                    break;

               default:
                    data.put("roomType","public");
                    break;
            }
            data.put("content",chat.getContent());
            jsonObject.put("data",data);
            return jsonObject.toString();
        }
        catch (JSONException j)
        {
            Log.e("ERROR", j.getMessage(), j);
            return "ERROR";
        }

    }


    static public String getSocketType(Map<String, Object> socket) {
        if (socket.containsKey(KEY_TYPE)) {
            return (String) socket.get(KEY_TYPE);
        }
        return VALUE_EMPTY;
    }

    static public Map<Integer, Integer> getRoomOnlineMap(JSONObject socket) {
        if (!socket.isNull(KEY_ROOM_ONLINE)) {
            try {
                return new Gson().fromJson((String) socket.get(KEY_ROOM_ONLINE),
                        new TypeToken<HashMap<Integer, Integer>>() {
                        }.getType());
            }catch (JSONException j)
            {
                Log.e("ERROR", j.getMessage(), j);
            }

        }
        return null;
    }

    static public String getContentMessage(Map<String, Object> socket) {
        if (socket.containsKey(KEY_CONTENT)) {
            return (String) socket.get(KEY_CONTENT);
        }
        return null;
    }

    static public int getStatus(Map<String, Object> socket) {
        if (socket.containsKey(KEY_STATUS)) {
            return ((Double) socket.get(KEY_STATUS)).intValue();
        }
        return -1;
    }
}
