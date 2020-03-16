package com.sex8.sinchat.model.server;

import android.util.Log;

import androidx.annotation.StringRes;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.sex8.sinchat.IMMessageApplication;
import com.sex8.sinchat.R;
import com.sex8.sinchat.event.ChatRoomAlertEvent;
import com.sex8.sinchat.event.ChatRoomTerminatedEvent;
import com.sex8.sinchat.event.ChatSendingFailEvent;
import com.sex8.sinchat.event.DisconnectEvent;
import com.sex8.sinchat.event.ReloginEvent;
import com.sex8.sinchat.model.IMData;
import com.sex8.sinchat.model.dataBase.Entity.Chatroom_History;
import com.sex8.sinchat.model.dataBase.Entity.Chatroom_Info;
import com.sex8.sinchat.model.dataBase.Entity.Member;
import com.sex8.sinchat.model.dataBase.Entity.Member_Fan;
import com.sex8.sinchat.model.dataBase.Entity.Member_Follow;
import com.sex8.sinchat.model.dataBase.Entity.Member_Friend;
import com.sex8.sinchat.model.dataBase.IMRepository;
import com.sex8.sinchat.model.server.WebSocket.ChatStatus;
import com.sex8.sinchat.utils.SocketCmdUtils;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

import tech.gusavila92.websocketclient.WebSocketClient;

import static com.sex8.sinchat.IMMessageApplication.Show_Log;

public class IMSocketClient extends WebSocketClient {
    private final String TAG = "SocketClient";
    private final int RECONNECTION_LIMIT = 3;
    private boolean isReady = false;
    private List<ChatStatus> sendingQueue = new CopyOnWriteArrayList<>();

    public IMSocketClient(URI uri) {
        super(uri);
//        Log.d(TAG, "Socketurl = " + uri.toString());
    }


    @Override
    public void onOpen() {
        if(IMMessageApplication.Show_Log) Log.d(TAG, "onOpen " + this);

        EventBus.getDefault().post(new DisconnectEvent(0, "onOpen"));


    }

    @Override
    public void send(String message) {
        if(IMMessageApplication.Show_Log) Log.d(TAG, "send : " + message);
        super.send(message);
    }



    @Override
    public void onTextReceived(String message) {
       if(IMMessageApplication.Show_Log) Log.d(TAG, "onTextReceived : " + message);
        Map<String, Object> result = new HashMap<>();
        if(message.contains("Opened"))
        {
            //type
            result.put("type","online");
        }
        else
        {

            try {
              //  Log.d("OnDataReturn", "Succeed");
                JSONObject obj = new JSONObject(message);


            //    Log.d("OnDataReturn_cmd", obj.getString("cmd"));


                    switch (obj.getString("cmd"))
                    {
                        case "chat.sync":
                            JSONArray syncAry=obj.getJSONObject("data").getJSONArray("result");
                            for(int i=0;i<syncAry.length();i++)
                            {
                                Log.d("OnDataReturn_sync", syncAry.getString(i));
                                JSONObject tmp=new JSONObject(syncAry.getString(i));
                                updateMessage(tmp.getJSONObject("data").getJSONObject("result"));
//                                EventBus.getDefault().post(new SyncEvent(tmp.getJSONObject("data").getJSONObject("result").getInt("roomId")
//                                        , tmp.getJSONObject("data").getJSONObject("result").getString("messageId")));
                            }

                            break;

                        /**別人取消你的粉絲，是要刪除你的關注*/
                        /**別人取消你的關注，是要刪除你的粉絲
                         * 兩者顛倒*/

                        case "user.addFan":
                            IMData.getInstance().updateFollows();
                            break;
                        case "user.deleteFan":

                            Member_Follow follows = new Member_Follow();
                            follows.setUid(obj.getJSONObject("data").getJSONObject("result").optLong("fromUserId"));
                            IMRepository.getInstance().deleteFollow(follows);
                            break;
                        case "user.addFollow":
                            IMData.getInstance().updateFanList();
                            break;
                        case "user.deleteFollow":

                            Member_Fan fans = new Member_Fan();
                            fans.setUid(obj.getJSONObject("data").getJSONObject("result").optLong("fromUserId"));
                            IMRepository.getInstance().deleteFan(fans);
                            break;
                        case "user.addFriend":
                            IMData.getInstance().updateFriendList();
                            break;
                        case "user.deleteFriend":
                            Member_Friend friends = new Member_Friend();
                            friends.setUid(obj.optLong("fromUserId"));
                            IMRepository.getInstance().deleteFriend(friends);
                            break;


                        case "chat.createChatroom":
                            updateLastMessageFromWebSocket(obj);
                        case "chat.deleteChatroom":
                            updateLastMessageFromWebSocket(obj);
                            break;

                        case "chat.say":

                            updateRoomOnlineNumber(obj.getJSONObject("data").getJSONObject("result"));

//                            判斷是自己發送就中斷計時器, 表示成功送出
                            for (ChatStatus status: sendingQueue){
                                if (obj.getJSONObject("data").getJSONObject("result").getInt("fromUserId")!=1) {//非機器人
                                    int uid = obj.getJSONObject("data").getJSONObject("result").getInt("fromUserId");
                                    long submitTime = new JSONObject(status.getSubmittedChat().getContent()).getLong("submitTime");//發送時間
                                    JSONObject content = new JSONObject(obj.getJSONObject("data").getJSONObject("result").getString("content"));
                                    long responseTime = content.getLong("submitTime");//返回之發送時間
                                    Log.d("SendingChecker", "response uid: " + uid + " submitTime: " + submitTime + " responseTime: " + responseTime);
                                    //同一個user, 且發送時間相同, 則刪除狀態確認
                                    if (status.getSubmittedChat().getUid()==obj.getJSONObject("data").getJSONObject("result").getInt("fromUserId") && submitTime == responseTime){
                                        status.getTimer().cancel();
                                        sendingQueue.remove(status);
                                        Log.d("SendingChecker", "response remove queue uid: " + uid + " submitTime: " + submitTime + " responseTime: " + responseTime);
                                    }
                                }
                            }

                            break;
                    }
            } catch (JSONException j) {
                Log.e("OnDataReturn", j.getMessage(), j);

            }

        }
    }

    @Override
    public void onBinaryReceived(byte[] data) {
        if(IMMessageApplication.Show_Log) Log.d(TAG, "onBinaryReceived");
    }

    @Override
    public void onPingReceived(byte[] data) {
        if(IMMessageApplication.Show_Log) Log.d(TAG, "onPingReceived");
    }

    @Override
    public void onPongReceived(byte[] data) {
        if(IMMessageApplication.Show_Log) Log.d(TAG, "onPongReceived");
    }

    @Override
    public void onException(Exception e) {
        Log.e(TAG, "onException = " + e.getMessage());

        EventBus.getDefault().post(new DisconnectEvent(1, e.getMessage()));

    }

    @Override
    public void onCloseReceived() {
//        mTimer.cancel();
        Log.e("Timmy", "onCloseReceived");
    }

    @Override
    public void close() {
        isReady = false;
        super.close();
    }

    //----------------operator
    public void sendSync(int RoomId, String LastMsg) {

        send(SocketCmdUtils.cmdSync(RoomId,LastMsg));
    }

    private void sendPing() {

        try {
            JSONObject  jsonObject = new JSONObject ();
            jsonObject.put("cmd","chat.say");
            JSONObject data = new JSONObject ();
            data.put("roomId","-1");
            data.put("contentType","text");
            data.put("roomType","system");
            data.put("content","Ping_message");
            jsonObject.put("data",data);
            if(Show_Log) Log.d(TAG, "say cmd = " + jsonObject.toString());
          send(jsonObject.toString());
        }
        catch (JSONException j)
        {
            j.printStackTrace();
        }
    }

    private void updateLastMessageFromWebSocket(JSONObject result) throws JSONException {
        if(result.getString("cmd").contains("chat.createChatroom"))
        {

            try
            {
                String roomIds= result.getJSONObject("data").getJSONObject("result").getString("roomId");
              IMData.getInstance().updatePartyRoomListDetail("["+roomIds+"]");

            }
            catch (Exception e)
            {
                Log.d("FromWebSocket",e.getMessage());
            }


        }
        else
        {
            int roomIds= result.getJSONObject("data").getJSONObject("result").getInt("roomId");
            Chatroom_Info info = new Chatroom_Info();
            info.setChatRoomId(roomIds);
            IMRepository.getInstance().deleteChatRoom(info);
        }

        IMData.getInstance().updateRoomOnline(result);

    }

    private void updateRoomOnlineNumber(JSONObject result) throws JSONException {



        if(result.getString("contentType").contains("system"))
        {
            //joinChatroom
            String content_type=result.getJSONObject("content").getString("action");


            if(content_type.contains("action")
                    &&content_type.contains("joinChatroom"))
            {


            }
            else if(content_type.contains("action")
                    &&content_type.contains("leaveChatroom"))
            {

            }

        }

            IMData.getInstance().updateRoomOnline(result);

    }

    private void updateMessage(JSONObject result) {


        IMData.getInstance().updateRoomOffline(result);
    }

    private void processChatTerminated(@StringRes int resId) {
        EventBus.getDefault().post(new ChatRoomTerminatedEvent(resId));
    }

    private void processChatAlert(@StringRes int resId) {
        EventBus.getDefault().post(new ChatRoomAlertEvent(resId));
    }

    private void processJoinRoom(int status, String message) {
        IMData.getInstance().notifyJoinRoom(status, message);
    }

    private void processClose() {
        IMData.getInstance().terminate();
        EventBus.getDefault().post(new ReloginEvent());
    }

    private void processChatUpdate(int roomId) {
        if(IMData.getInstance().getCurrentChatRoom().getRoomId() == roomId){
            IMData.getInstance().updateRoomList();
        }
    }

    public void sendMessage(Chatroom_History chat, Chatroom_Info info) {
        send(SocketCmdUtils.cmdSay(chat,info));

        ChatStatus status = new ChatStatus();
        Timer chatStatusChecker = new Timer();
        chatStatusChecker.schedule(new SendingCheckerTask(chat), 30000);
//        chatStatusChecker.schedule(new SendingCheckerTask(chat), 1000);
        status.setTimer(chatStatusChecker);
        status.setSubmittedChat(chat);
        sendingQueue.add(status);
    }

    private class SendingCheckerTask extends TimerTask {

        Chatroom_History chat;

        public SendingCheckerTask(Chatroom_History chat)
        {
            this.chat=chat;
        }

        @Override
        public void run() {
            Log.d(getClass().getSimpleName(), "SendingCheckerTask execute chat: " + chat.getContent());

            //若執行到run, 表示對話傳送未完成, 移除失敗對話
            //若執行成功則會在Websocket將TimerTask取消
            IMRepository.getInstance().deleteChat(chat);
            EventBus.getDefault().post(new ChatSendingFailEvent(chat));

            for (ChatStatus status: sendingQueue){
                try {
                    long chatTime = new JSONObject(chat.getContent()).getLong("submitTime");//發送時間
                    long statusTime = new JSONObject(status.getSubmittedChat().getContent()).getLong("submitTime");//發送時間
                    Log.d("SendingChecker", "Timer submitTime: " + chatTime + " responseTime: " + statusTime);
                    if (chatTime==statusTime){
                        sendingQueue.remove(status);
                        Log.d("SendingChecker", "Timer submitTime: " + chatTime + " responseTime: " + statusTime);
                    }
                    break;
                } catch (JSONException e) {
                    Log.e("SendingChecker", e.getMessage(),e);
                }
            }
        }
    }
}
