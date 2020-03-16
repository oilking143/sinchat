package com.sex8.sinchat.model;

import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.google.gson.Gson;
import com.sex8.sinchat.Constant;
import com.sex8.sinchat.IMMessageApplication;
import com.sex8.sinchat.entity.UserInfo;
import com.sex8.sinchat.event.AccountFragmentEvent;
import com.sex8.sinchat.event.AddFollowEvent;
import com.sex8.sinchat.event.CreateChatListDetailEvent;
import com.sex8.sinchat.event.CreateEvent;
import com.sex8.sinchat.event.DeleteEvent;
import com.sex8.sinchat.event.DeleteFollowEvent;
import com.sex8.sinchat.event.EnterRoomResultEvent;
import com.sex8.sinchat.event.OfflineUnSyncChatEvent;
import com.sex8.sinchat.event.FollowEvent;
import com.sex8.sinchat.event.FriendEvent;
import com.sex8.sinchat.event.GroupListEvent;
import com.sex8.sinchat.event.HistoryUpdateFromAPIEvent;
import com.sex8.sinchat.event.ImageSendingToChatEvent;
import com.sex8.sinchat.event.JoinPartyEvent;
import com.sex8.sinchat.event.ReceivedWebSocketDataEvent;
import com.sex8.sinchat.event.LeaveEvent;
import com.sex8.sinchat.event.LocationEvent;
import com.sex8.sinchat.event.MainEvent;
import com.sex8.sinchat.event.PartyListEvent;
import com.sex8.sinchat.event.ReportEvent;
import com.sex8.sinchat.event.RoomListCompleteEvent;
import com.sex8.sinchat.event.RoomChatListDetailEvent;
import com.sex8.sinchat.event.RoomListFailEvent;
import com.sex8.sinchat.event.RoomListUpdateEvent;
import com.sex8.sinchat.event.RoomPartyListDetailEvent;
import com.sex8.sinchat.event.StartRoomInfoLocalCheckEvent;
import com.sex8.sinchat.event.UserLoginCompleteEvent;
import com.sex8.sinchat.model.dataBase.Entity.Chatroom_History;
import com.sex8.sinchat.model.dataBase.Entity.Chatroom_Info;
import com.sex8.sinchat.model.dataBase.Entity.Chatroom_Info_Local;
import com.sex8.sinchat.model.dataBase.Entity.Member;
import com.sex8.sinchat.model.dataBase.Entity.Member_Fan;
import com.sex8.sinchat.model.dataBase.Entity.Myroom_List;
import com.sex8.sinchat.model.server.ApiClient;
import com.sex8.sinchat.model.dataBase.Entity.Member_Follow;
import com.sex8.sinchat.model.dataBase.Entity.Member_Friend;
import com.sex8.sinchat.model.dataBase.IMRepository;
import com.sex8.sinchat.model.server.IMSocketClient;
import com.sex8.sinchat.model.server.ff_list.AddResponse;
import com.sex8.sinchat.model.server.ff_list.CreateResponse;
import com.sex8.sinchat.model.server.ff_list.DeleteResponse;
import com.sex8.sinchat.model.server.ff_list.FanListResponse;
import com.sex8.sinchat.model.server.ff_list.FollowsResponse;
import com.sex8.sinchat.model.server.ff_list.FriendListResponse;
import com.sex8.sinchat.model.server.ff_list.GroupListResponse;
import com.sex8.sinchat.model.server.ff_list.ReportResponse;
import com.sex8.sinchat.model.server.pojo.InitResponse;
import com.sex8.sinchat.model.server.pojo.UserLoginResponse;
import com.sex8.sinchat.model.server.pojo.history.HistoryMessageResponse;
import com.sex8.sinchat.model.server.pojo.party.GetPartyListResponse;
import com.sex8.sinchat.model.server.pojo.party.JoinPartyResponse;
import com.sex8.sinchat.model.server.pojo.roomlist.ChatRoom;
import com.sex8.sinchat.model.server.pojo.roomlist.ChatRoomListResponse;
import com.sex8.sinchat.model.server.pojo.roomlist.GetRoomListDetailResponse;
import com.sex8.sinchat.model.server.pojo.roomlist.GroupRoom;
import com.sex8.sinchat.model.server.pojo.roomlist.RoomMessages;
import com.sex8.sinchat.model.server.pojo.upload_image.UploadImageResponse;
import com.sex8.sinchat.utils.AESCrypt;
import com.sex8.sinchat.utils.WebApiUtils;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Response;

import static com.sex8.sinchat.IMMessageApplication.Show_Log;

public class IMData {

    private static final String TAG = "IMData";

    private CompositeDisposable mDisposables = new CompositeDisposable();
    private static final int WS_CONNECT_TIMEOUT = 3000;
    private static final int WS_AUTO_RECONNECT = 10000;

    private IMSocketClient mWebSocketClient;



    private IMData() {

    }

    private static IMData sInstance;

    public static synchronized IMData getInstance() {
        if (sInstance == null) {
            sInstance = new IMData();
        }
        return sInstance;
    }

    //--------------Data
    private UserInfo mUserInfo;
    private InitResponse.Data mWebSocketInfo;


    private ChatRoomListResponse.Data chatRoomListData;
    private GroupRoom mCurrentGroupRoom;
    private ChatRoom mCurrentChatRoom;
    private SparseArray<RoomMessages> mRoomMessages = new SparseArray<>();

    //-------------- getter
    public GroupRoom getCurrentGroupRoom() {
        return mCurrentGroupRoom;
    }


    public UserInfo getUserInfo() {
        return mUserInfo;
    }

    public InitResponse.Data getWebSocketInfo() {
        return mWebSocketInfo;
    }

    public ChatRoom getCurrentChatRoom() {
        return mCurrentChatRoom;
    }

    public List<GroupRoom> getGroupRoomList(){
        return chatRoomListData.getGroupRoomList();
    }

//    public RoomMessages getRoomMessagesByRoomId(int roomId) {
//        return mRoomMessages.get(roomId);
//    }

    //------------ setter
    public void setCurrentRoomCategory(GroupRoom roomCategory) {
        mCurrentGroupRoom = roomCategory;
    }

    public void setmUserInfo(UserInfo mUserInfo) {
        this.mUserInfo = mUserInfo;
    }

    //------------ operator
    public void sendMessage(Chatroom_History chat, Chatroom_Info info) {
        mWebSocketClient.sendMessage(chat,info);
    }

    public void sendSync(int RoomID, String LastMsg) {
        Log.d(getClass().getSimpleName(), "sendSync LastMsg : " + LastMsg);
        mWebSocketClient.sendSync(RoomID,LastMsg);
    }

//    public void sendImage(Chatroom_History chat, Chatroom_Info info) {
//        mWebSocketClient.sendMessage(message);
//    }

//    public void joinRoom(ChatRoom currentRoomInfo){
//        mCurrentChatRoom = currentRoomInfo;
//
//    }

    public void notifyJoinRoom(int status, String message) {
        if(status != 0 && status != 1) {
            mCurrentChatRoom = null;
        }
        EventBus.getDefault().post(new EnterRoomResultEvent(status, message));
    }

    public void getRoomHistoryFromApi(int RoomId, int MsgId) {
        mDisposables.add(ApiClient.getRoomHistoryMsg(RoomId,MsgId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<Response<HistoryMessageResponse>>() {
                    @Override
                    public void onSuccess(Response<HistoryMessageResponse> historyMessageResponseResponse) {
                        if (WebApiUtils.checkResponse(historyMessageResponseResponse)) {
                            HistoryMessageResponse response = historyMessageResponseResponse.body();
                            updateHistoryMessage(response);
                        }else{
                            EventBus.getDefault().post(new HistoryUpdateFromAPIEvent(historyMessageResponseResponse.message()));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "get room history error", e);
                        EventBus.getDefault().post(new HistoryUpdateFromAPIEvent(e.getMessage()));
                    }
                }));
    }

    //--------------- update from socket
//    public void updateMessage(Chatroom_History chat) {
//        EventBus.getDefault().post(new MessageUpdateEvent(chat));
//    }

    public void updateRoomOnline(JSONObject result) {


            try {
                EventBus.getDefault().post(new ReceivedWebSocketDataEvent(result));
//                Log.e("roomID",result.getString("roomId"));
            }
            catch (Exception e)
            {
                Log.e("ErrorIM","Message is Empthy!!!!!!"+e.getMessage());
            }
    }

    public void updateRoomOffline(JSONObject result) {

        try {
            EventBus.getDefault().post(new OfflineUnSyncChatEvent(result));
//                Log.e("roomID",result.getString("roomId"));
        }
        catch (Exception e)
        {
            Log.e("ErrorIM","Message is Empthy!!!!!!"+e.getMessage());
        }
    }

    //--------------- api
    public void updateRoomList() {
        if (mUserInfo.isGuest()) {
            updateGuestRoomList();
        } else {
            updateUserRoomList();
        }
    }

    public void userLogin(final String encrypt) {
        if (encrypt == null) {
//            mUserInfo = UserInfo.createGuestInfo();
//            EventBus.getDefault().post(UserLoginCompleteEvent.createSuccessEvent());
//            mUserInfo = new UserInfo(51, "gFx1N9R/P1kfaocO82xkLq5oWXK8knpungmqSx0K8QcVqynBd4obADFiSNjbdP6C62xb1cGSccrfBZZPRam7VvzRlUPaYVMG/buadWUd3gAhRrayYhShk/TZhRGxLWhcbOxyoQXjqpg/sPTOUX5uKJAj7d8yHXhnNwn4vRNKAFAqgwwg6KKpFK0Ivo81G0DcAmLvyDs69gn3Pctbp1YC9cbVGw3tJ7ifqCihY+ZNx1ks2W4eraTwsKgZV2BTOywut84sPyEvuZH/78OM3QeLrodcvGfv9RZPBc4qFfwlp7ndUlUeoW4ld0YS3utWxzjRJiKwJgDJAYai4rSafrMZLQ==",
//                    "真島吾郎", "https://i.imgur.com/Al3udj6.jpg", 1);

            IMMessageApplication user=(IMMessageApplication)IMMessageApplication.getGlobalContext();


            String uid=user.getUid();
            String imKey=user.getImKey();

            Log.d("LOGIN__",uid);
            Log.d("LOGIN__",imKey);

            mUserInfo = new UserInfo(Integer.valueOf(uid), imKey,
                    "", "", 1);
//            mUserInfo = new UserInfo(1, "GyQkqC+urjJ8Ha6DNhRlM53ApRHvQ4V+yysXmS+wQ65j2dsEgXrtmPrvyH7/Do/lhyLZR1ACRLUHCAKYXN0sZcu+kKEBvQXR+QdiAFIT/7cGRsQHMTtZeILjk9C+QVAJL6nvCNlbL0OPaTfQw+wf7Im586xK3Zk+zDcFwqEv+weUvwCNnhktRqsAexQ7j6ffo7ySfOa2D94/WF6wkfJ2zOF9UoQsej6VX4ymURr/E2fgmpjUkuV2vwB8lBM2Q1xSezmhfb5Vyj2VT2vM2iWL40RXh2KXqZ0I0oF6ZIXs+vXH4xNN+YJar1nZfuudJBqo",
//                    "Ed", "", 1);
            IMData.getInstance().setmUserInfo(mUserInfo);

            getWebSocketUrl();
        }
    }

    public void getWebSocketUrl() {
        mDisposables.add(ApiClient.initWebSocket()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<Response<InitResponse>>() {
                    @Override
                    public void onSuccess(Response<InitResponse> initResponse) {
                        if (WebApiUtils.checkResponse(initResponse)) {
                            InitResponse response = initResponse.body();
                            initWebSocket(response);
                            EventBus.getDefault().post(new UserLoginCompleteEvent(response.getError().getCode(), response.getError().getMessage()));
                        } else {
                            String message = initResponse.body() == null ? "" : initResponse.body().getMessage();
                            EventBus.getDefault().post(new UserLoginCompleteEvent(-1, message));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        EventBus.getDefault().post(new UserLoginCompleteEvent(-1, e.getMessage()));
                    }
                }));
    }

    public void uploadImage(String md5Token,String source,String thumbnail,final String Path, Chatroom_Info roomInfo, Chatroom_History chat){
        mDisposables.add(ApiClient.uploadImage(md5Token)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(new DisposableSingleObserver<Response<UploadImageResponse>>() {
                @Override
                public void onSuccess(Response<UploadImageResponse> uploadImageResponse) {
                    UploadImageResponse response = uploadImageResponse.body();
                    if (response != null) {
                        if (response.getError().getCode() == 0 && response.getError().getCache() == 0) {
                            //需要上傳
                            mDisposables.add(ApiClient.uploadImage(md5Token, source, thumbnail)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribeWith(new DisposableSingleObserver<Response<UploadImageResponse>>() {
                                        @Override
                                        public void onSuccess(Response<UploadImageResponse> uploadImageResponseResponse) {
                                            UploadImageResponse resendResponse = uploadImageResponseResponse.body();
                                            if (resendResponse != null) {
                                                if (resendResponse.getError().getCode() == 0 && resendResponse.getError().getCache() == 1) {
                                                    try {
                                                        JSONObject data = new JSONObject();
                                                        data.put("md5Token", resendResponse.getData().getMd5Token());
                                                        data.put("sourcePath", resendResponse.getData().getSourcePath());
                                                        data.put("thumbnailPath", resendResponse.getData().getThumbnailPath());
                                                        data.put("Local_filename", Path);
                                                        JSONObject content = new JSONObject();
                                                        content.put("data", data);//封裝一層"data"
                                                        content.put("submitTime", new Date().getTime());//用此方式得以識別返回對話的身分
                                                        chat.setContent(content.toString());
                                                        sendMessage(chat, roomInfo);
                                                    } catch (JSONException e) {
                                                        Log.d(getClass().getSimpleName(), e.getMessage(), e);
                                                    }
                                                } else {
                                                    EventBus.getDefault().post(new ImageSendingToChatEvent(-1, resendResponse.getError().getMessage(), null));
                                                }
                                            }else{
                                                EventBus.getDefault().post(new ImageSendingToChatEvent(-1, uploadImageResponseResponse.raw().message(), null));
                                            }
                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            EventBus.getDefault().post(new ImageSendingToChatEvent(-1, "圖片格式有誤或是包含MP4", null));
                                        }
                                    }));
                        } else if (response.getError().getCode() == 0 && response.getError().getCache() == 1) {
                            //server上已有資訊
                            try {
                                JSONObject data = new JSONObject();
                                data.put("md5Token", response.getData().getMd5Token());
                                data.put("sourcePath", response.getData().getSourcePath());
                                data.put("thumbnailPath", response.getData().getThumbnailPath());
                                data.put("Local_filename", Path);
                                JSONObject content = new JSONObject();
                                content.put("data", data);//封裝一層"data"
                                content.put("submitTime", new Date().getTime());//用此方式得以識別返回對話的身分
                                chat.setContent(content.toString());
                                sendMessage(chat, roomInfo);
                            }catch (JSONException e) {
                                Log.d(getClass().getSimpleName(), e.getMessage(),e);
                            }
                        }else{
                            Log.e("ERROR", uploadImageResponse.raw().message());
                            EventBus.getDefault().post(new ImageSendingToChatEvent(-1, response.getError().getMessage(), null));
                        }
                    } else {
                        EventBus.getDefault().post(new ImageSendingToChatEvent(-1, uploadImageResponse.raw().message(), null));
                    }
                }

                @Override
                public void onError(Throwable e) {
                    Log.e(TAG, "user uploadImage error", e);
                    EventBus.getDefault().post(new ImageSendingToChatEvent(-1, "圖片格式有誤或是包含MP4", null));
                }
            }));

    }


    //-------------------private
    private void updateGuestRoomList() {
        if (mUserInfo == null || (!mUserInfo.isGuest())) {
            return;
        }
        if(Show_Log) Log.d(TAG, "update Guest RoomList");
        mDisposables.add(ApiClient.getGuestRoomListRx( )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<Response<ChatRoomListResponse>>() {
                    @Override
                    public void onSuccess(Response<ChatRoomListResponse> roomListResponseResponse) {


                        updateCurrentRoomList(roomListResponseResponse);
                        startWebSocket();
                        EventBus.getDefault().post(new RoomListCompleteEvent());
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "update guest room error", e);
                        EventBus.getDefault().post(new RoomListCompleteEvent(e.getMessage()));
                    }
                }));

    }

    private void updateUserRoomList() {
        if (mUserInfo == null || mUserInfo.isGuest()) {
            return;
        }

        if(Show_Log) Log.d(TAG, "get user room list ==> " + mUserInfo.getUid() + " " + mUserInfo.getImKey());
        mDisposables.add(ApiClient.getChatRoomList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<Response<ChatRoomListResponse>>() {
                    @Override
                    public void onSuccess(Response<ChatRoomListResponse> roomListResponseResponse) {
                        updateCurrentRoomList(roomListResponseResponse);
                        startWebSocket();
                        EventBus.getDefault().post(new RoomListCompleteEvent());
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "get user room error", e);
                        Toast.makeText(IMMessageApplication.getGlobalContext(),
                                "Get Room List fail : " + e.toString(), Toast.LENGTH_LONG).show();
                        EventBus.getDefault().post(new RoomListFailEvent(e));
                    }
                }));

    }


    public void updateFollows()
    {
        mDisposables.add(ApiClient.getFollowsList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<Response<FollowsResponse>>() {
                    @Override
                    public void onSuccess(Response<FollowsResponse> getFollowsResponse) {
                        FollowsResponse response=getFollowsResponse.body();
                        for(int i=0;i<response.getData().size();i++)
                        {
                            try {
                                JSONObject jsonObj = new JSONObject(response.getData().get(i));

                                Member_Follow follows = new Member_Follow();
                                follows.setUid(jsonObj.optLong("uid"));
                                IMRepository.getInstance().insertMember_follow(follows);

                                Member member = new Member();
                                member.setUid(jsonObj.optLong("uid"));
                                member.setAvatar(jsonObj.optString("avatar"));
                                member.setUsername(jsonObj.optString("username"));
                                IMRepository.getInstance().insertMember(member);

                            }catch (JSONException e)
                            {
                               e.printStackTrace();
                            }


                        }
                        EventBus.getDefault().post(new FollowEvent(0, null,null));
                        EventBus.getDefault().post(new AccountFragmentEvent(0, null,null));
                    }

                    @Override
                    public void onError(Throwable e) {
                        EventBus.getDefault().post(new FollowEvent(-1, null,null));
                    }
                }));

    }

    public void updateFriendList()
    {
        mDisposables.add(ApiClient.getFriendList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<Response<FriendListResponse>>() {
                    @Override
                    public void onSuccess(Response<FriendListResponse> getFriendListResponse) {
                        FriendListResponse response=getFriendListResponse.body();
                        for(int i=0;i<response.getData().size();i++)
                        {
                            try {
                                JSONObject jsonObj = new JSONObject(response.getData().get(i));
                                Member_Friend friend = new Member_Friend();
                                friend.setUid(jsonObj.optLong("uid"));
                                IMRepository.getInstance().insertMember_friend(friend);

                                Member member = new Member();
                                member.setUid(jsonObj.optLong("uid"));
                                member.setAvatar(jsonObj.optString("avatar"));
                                member.setUsername(jsonObj.optString("username"));
                                IMRepository.getInstance().insertMember(member);
                            }catch (JSONException e)
                            {
                                e.printStackTrace();
                            }


                        }
                        EventBus.getDefault().post(new FriendEvent(0, null,null));
                    }

                    @Override
                    public void onError(Throwable e) {
                        EventBus.getDefault().post(new FriendEvent(-1, null,null));
                    }
                }));

    }

    public void updateFanList()
    {
        mDisposables.add(ApiClient.getFans()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<Response<FanListResponse>>() {
                    @Override
                    public void onSuccess(Response<FanListResponse> getFanListResponse) {
                        FanListResponse response=getFanListResponse.body();
                        for(int i=0;i<response.getData().size();i++)
                        {
                            try {
                                JSONObject jsonObj = new JSONObject(response.getData().get(i));
                                Member_Fan fans = new Member_Fan();
                                fans.setUid(jsonObj.optLong("uid"));
                                IMRepository.getInstance().insertMember_fan(fans);

                                Member member = new Member();
                                member.setUid(jsonObj.optLong("uid"));
                                member.setAvatar(jsonObj.optString("avatar"));
                                member.setUsername(jsonObj.optString("username"));
                                IMRepository.getInstance().insertMember(member);

                            }catch (JSONException e)
                            {
                                e.printStackTrace();
                            }


                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }
                }));

    }



    public void createRoom(Member member)
    {
        mDisposables.add(ApiClient.createRoom(member.getUsername(),member.getUid())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<Response<CreateResponse>>() {
                    @Override
                    public void onSuccess(Response<CreateResponse> getCreateResponse) {
                        CreateResponse response=getCreateResponse.body();
                        EventBus.getDefault().post(new CreateEvent(0, member.getUsername(),member.getAvatar(),response.getData()));
                    }

                    @Override
                    public void onError(Throwable e) {
                        EventBus.getDefault().post(new CreateEvent(-1, null,null,null));
                    }
                }));

    }

    public void updateGroupList(final int status)
    {
        mDisposables.add(ApiClient.getGroupList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<Response<GroupListResponse>>() {
                    @Override
                    public void onSuccess(Response<GroupListResponse> getFanListResponse) {
                        GroupListResponse response=getFanListResponse.body();



                        try {
                            JSONArray publicArray = new JSONArray(response.getData().getpublicRoom());
                            JSONArray privateArray = new JSONArray(response.getData().getprivateRoom());

                          for (int i=0;i<publicArray.length();i++)
                          {
                              Myroom_List myroom=new Myroom_List();
                              myroom.setChatroomId(publicArray.getInt(i));
                              IMRepository.getInstance().insertMy_Room_List(myroom);
                          }


                          for (int i=0;i<privateArray.length();i++)
                          {
                              Myroom_List myroom=new Myroom_List();
                              myroom.setChatroomId(privateArray.getInt(i));
                              IMRepository.getInstance().insertMy_Room_List(myroom);
                          }


                        } catch (Exception e) {
                            e.printStackTrace();
                        }


//                        Member_Fan fans = new Member_Fan();
//                        fans.setUid(jsonObj.optLong("uid"));
//                        IMRepository.getInstance().insertMember_fan(fans);


                    }

                    @Override
                    public void onError(Throwable e) {
                        EventBus.getDefault().post(new GroupListEvent(-1, null,null));
                    }
                }));

    }

    public void addFollows(Member member) {
        mDisposables.add(ApiClient.addFollows(member.getUid())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<Response<AddResponse>>() {
                    @Override
                    public void onSuccess(Response<AddResponse> getDeleteResponse) {
                        AddResponse response=getDeleteResponse.body();
                        EventBus.getDefault().post(new AddFollowEvent(response.getError().getCode(),response.getError().getMessage(), member));
                    }

                    @Override
                    public void onError(Throwable e) {
                        EventBus.getDefault().post(new AddFollowEvent(-1, null, null));
                    }
                }));

    }

    public void Report(int chatroomID,String MsgId,String reportType,String ReportCotent,
                           String ReportedUid,String reportText)
    {
        mDisposables.add(ApiClient.reports(chatroomID,MsgId,reportType,ReportCotent,ReportedUid,reportText)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<Response<ReportResponse>>() {
                    @Override
                    public void onSuccess(Response<ReportResponse> getReportResponse) {
                        ReportResponse response=getReportResponse.body();
                        EventBus.getDefault().post(new ReportEvent(getReportResponse.body().getError().getCode()
                                , getReportResponse.body().getError().getMessage()));
                    }

                    @Override
                    public void onError(Throwable e) {
                        EventBus.getDefault().post(new ReportEvent(-1, null));
                    }
                }));

    }


    public void deleteFollows(Member member)
    {
        mDisposables.add(ApiClient.deleteFollows(member.getUid())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<Response<DeleteResponse>>() {
                    @Override
                    public void onSuccess(Response<DeleteResponse> getDeleteResponse) {
                        DeleteResponse response=getDeleteResponse.body();
                        EventBus.getDefault().post(new DeleteFollowEvent(response.getError().getCode()
                                , response.getMessage(), member));
                    }

                    @Override
                    public void onError(Throwable e) {
                        EventBus.getDefault().post(new DeleteFollowEvent(-1, null, null));
                    }
                }));

    }

    public void leaveGroup(final int roomId)
    {
        mDisposables.add(ApiClient.deleteGroup(roomId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<Response<DeleteResponse>>() {
                    @Override
                    public void onSuccess(Response<DeleteResponse> getDeleteResponse) {
                        DeleteResponse response=getDeleteResponse.body();
                        EventBus.getDefault().post(new LeaveEvent(roomId
                                , "popwindows"));
                    }

                    @Override
                    public void onError(Throwable e) {
                        EventBus.getDefault().post(new DeleteEvent(-1, null));
                    }
                }));

    }

    public void deleteGroup(int uid)
    {
        mDisposables.add(ApiClient.deleteGroup(uid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<Response<DeleteResponse>>() {
                    @Override
                    public void onSuccess(Response<DeleteResponse> getDeleteResponse) {
                        DeleteResponse response=getDeleteResponse.body();
                        EventBus.getDefault().post(new DeleteEvent(getDeleteResponse.body().getError().getCode()
                                , getDeleteResponse.body().getError().getMessage()));
                    }

                    @Override
                    public void onError(Throwable e) {
                        EventBus.getDefault().post(new DeleteEvent(-1, null));
                    }
                }));

    }


    public void JoinParty(int uid)
    {
        mDisposables.add(ApiClient.JoinParty(uid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<Response<JoinPartyResponse>>() {
                    @Override
                    public void onSuccess(Response<JoinPartyResponse> getJoinPartyResponse) {

                        JoinPartyResponse response=getJoinPartyResponse.body();
                        Log.d("MESSAGEFAI",response.getError().getMessage());
                        Log.d("MESSAGEFAI",""+response.getError().getCode());
                        EventBus.getDefault().post(new JoinPartyEvent(response.getError().getCode(), response.getError().getMessage()));

                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                }));

    }

    public void updatePartyList(String region)
    {
        mDisposables.add(ApiClient.getPartyList(region)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<Response<GetPartyListResponse>>() {
                    @Override
                    public void onSuccess(Response<GetPartyListResponse> getPartyRegionResponseResponse) {

                        GetPartyListResponse response=getPartyRegionResponseResponse.body();
                        EventBus.getDefault().post(new PartyListEvent(0, null,response));

                    }

                    @Override
                    public void onError(Throwable e) {

                        Log.d("RESPONSE","FAIL");
                        EventBus.getDefault().post(new PartyListEvent(-1, null,null));
                    }
                }));

    }



    public void updateChatRoomListDetail(String region)
    {
        mDisposables.add(ApiClient.getRoomListDetail(region)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<Response<GetRoomListDetailResponse>>() {
                    @Override
                    public void onSuccess(Response<GetRoomListDetailResponse> getRoomListRespons) {

                        GetRoomListDetailResponse response=getRoomListRespons.body();
                        EventBus.getDefault().post(new RoomChatListDetailEvent(0, null,response));

                    }

                    @Override
                    public void onError(Throwable e) {

                        Log.d("RESPONSE",e.getMessage());
                        EventBus.getDefault().post(new RoomChatListDetailEvent(-1, null,null));
                    }
                }));

    }

    public void changeGroup(String GroupId)
    {
        EventBus.getDefault().post(new LocationEvent(0, GroupId));
    }


    public void CheckChatRoomListDetail(String region, final int status)
    {
        mDisposables.add(ApiClient.getRoomListDetail(region)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<Response<GetRoomListDetailResponse>>() {
                    @Override
                    public void onSuccess(Response<GetRoomListDetailResponse> getRoomListRespons) {

                        GetRoomListDetailResponse response=getRoomListRespons.body();
                        EventBus.getDefault().post(new CreateChatListDetailEvent(status, null,response));

                    }

                    @Override
                    public void onError(Throwable e) {

                        Log.d("RESPONSE",e.getMessage());
                        EventBus.getDefault().post(new CreateChatListDetailEvent(-1, null,null));
                    }
                }));

    }



    public void updatePartyRoomListDetail(String region)
    {
        mDisposables.add(ApiClient.getRoomListDetail(region)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<Response<GetRoomListDetailResponse>>() {
                    @Override
                    public void onSuccess(Response<GetRoomListDetailResponse> getRoomListRespons) {

                        GetRoomListDetailResponse response=getRoomListRespons.body();
                        EventBus.getDefault().post(new RoomPartyListDetailEvent(1, null,response));
                        EventBus.getDefault().post(new StartRoomInfoLocalCheckEvent(1, null,response));




                    }

                    @Override
                    public void onError(Throwable e) {

                        Log.d("RESPONSE","FAIL");
                        EventBus.getDefault().post(new RoomPartyListDetailEvent(-1, null,null));
                    }
                }));

    }

    private void updateCurrentRoomList(@Nullable Response<ChatRoomListResponse> response) {
        if (WebApiUtils.checkResponse(response) && response.body().getData() != null) {
            if(Show_Log) Log.d(TAG, "update current room list");

            updateCurrentGroupRoom();
            EventBus.getDefault().post(new RoomListUpdateEvent(response));
        } else {
            Log.w(TAG, "response fail " + response);
            Toast.makeText(IMMessageApplication.getGlobalContext(),
                    "Get Room List fail : " + response.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private void updateCurrentGroupRoom() {

        //更新聊天室
//        EventBus.getDefault().post(new ChatRoomUpdateEvent(mCurrentChatRoom.getRoomId()));
//
    }

    private void initUserInfo(UserLoginResponse response) {
        mUserInfo = new UserInfo(
                response.getUid(),
                response.getImkey(),
                response.getMuser(),
                response.getMphoto(),
                response.getMtype()
        );
    }

    private void initWebSocket(@NonNull InitResponse response) {
        if(Show_Log) Log.d(TAG, "init web socket"+response.getData().getFileHosts().size());
        mWebSocketInfo = response.getData();

    }

    public void startWebSocket() {
        if (mWebSocketClient == null) {
            if(Show_Log) Log.d(TAG, "start web socket");
            URI uri;
            try {
                Log.d("token",mUserInfo.getToken());
                String url =IMData.getInstance().mWebSocketInfo.getWorkermanServer()+"/chat?token="+mUserInfo.getToken();
                Log.d("websocket","url: "  + url);
                uri = new URI(url);
            } catch (URISyntaxException e) {
                e.printStackTrace();
                return;
            }

            mWebSocketClient = new IMSocketClient(uri);
            mWebSocketClient.setConnectTimeout(WS_CONNECT_TIMEOUT);
            mWebSocketClient.enableAutomaticReconnection(WS_AUTO_RECONNECT);
            mWebSocketClient.connect();
        }
    }

    public void pauseWebSocket() {

        if(mWebSocketClient!=null)
        {
            mWebSocketClient.close();
            mWebSocketClient=null;
        }
    }

    private void updateHistoryMessage(HistoryMessageResponse response) {

        List<Chatroom_History> list=new ArrayList<>();


        for(int i=0;i<response.getData().size();i++) {
            Chatroom_History chat = new Chatroom_History();
            chat.setMsgId(response.getData().get(i).getMsgid());
            chat.setUsername(response.getData().get(i).getNickname());
            chat.setAvatar(response.getData().get(i).getUserimg());
            chat.setChatRoomId(response.getData().get(i).getRoomid());
            chat.setChatType(response.getData().get(i).getMsgtype());
            chat.setContent(response.getData().get(i).getContent());
            chat.setUid(response.getData().get(i).getUid());
            chat.setTimestamp(response.getData().get(i).getTimestamp());
            chat.setTimestamp(Long.valueOf(response.getData().get(i).getMsgid().substring(0,13)));

          list.add(chat);
      }

        Collections.reverse(list);

        if (list != null && list.size() > 0) {
            int roomId = list.get(0).getChatRoomId();
            EventBus.getDefault().post(new HistoryUpdateFromAPIEvent(roomId,list));
        }
    }

    private ChatRoom getRoomInfoByRoomId(int roomId) {

        if (mCurrentChatRoom.getRoomId() == roomId) {
            Log.d("Info",mCurrentChatRoom.getName());
            return mCurrentChatRoom;
        }


        Log.w(TAG, "Can not find room info with id = " + roomId);
        return null;
    }

    public void terminate() {
        mDisposables.clear();
        if(mWebSocketClient != null)
            mWebSocketClient.close();
        mWebSocketClient = null;
    }
}
