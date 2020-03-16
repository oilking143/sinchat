package com.sex8.sinchat.model.server;

import com.google.gson.Gson;
import com.sex8.sinchat.BuildConfig;
import com.sex8.sinchat.IMMessageApplication;
import com.sex8.sinchat.model.server.ff_list.AddResponse;
import com.sex8.sinchat.model.server.ff_list.CreateResponse;
import com.sex8.sinchat.model.server.ff_list.DeleteResponse;
import com.sex8.sinchat.model.server.ff_list.FanListResponse;
import com.sex8.sinchat.model.server.ff_list.FollowsResponse;
import com.sex8.sinchat.model.server.ff_list.FriendListResponse;
import com.sex8.sinchat.model.server.ff_list.GroupListResponse;
import com.sex8.sinchat.model.server.ff_list.ReportResponse;
import com.sex8.sinchat.model.server.pojo.InitResponse;
import com.sex8.sinchat.model.server.pojo.history.HistoryMessageResponse;
import com.sex8.sinchat.model.server.pojo.party.GetPartyListResponse;
import com.sex8.sinchat.model.server.pojo.party.JoinPartyResponse;
import com.sex8.sinchat.model.server.pojo.roomlist.ChatRoomListResponse;
import com.sex8.sinchat.model.server.pojo.roomlist.GetRoomListDetailResponse;
import com.sex8.sinchat.model.server.pojo.upload_image.UploadImageResponse;

import java.io.IOException;

import io.reactivex.Single;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.POST;
import retrofit2.http.Query;

public class ApiClient {
    private static ApiClient sApiClient = null;
    private OkHttpClient mHttpClient;
    private OkHttpClient mStevenHttpClient;
    private Api mApi;
    private Gson gson;
    private String uid;
    private String imKey;

    public static synchronized ApiClient getInstance() {
        if (sApiClient == null) {
            sApiClient = new ApiClient();
        }
        return sApiClient;
    }

    //10.2.116.24  -- 3.112.66.20
    //10.2.116.46 :7272  —  3.112.40.27  :7272
    //10.2.116.64  - 13.230.32.16
    //10.1.101.129  (3.112.63.99) steven api

    private ApiClient() {
        IMMessageApplication user=(IMMessageApplication)IMMessageApplication.getGlobalContext();

        this.uid=user.getUid();
        this.imKey=user.getImKey();
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        mHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        Request original = chain.request();
                        Request request = original.newBuilder()
                                .header("uid", uid)
                                .header("imkey", imKey)
                                .method(original.method(), original.body())
                                .build();

                        return chain.proceed(request);
                    }
                })
                .addInterceptor(interceptor).build();
        mStevenHttpClient = new OkHttpClient.Builder().addInterceptor(interceptor).build();
        mApi = new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(mHttpClient)
                .build()
                .create(Api.class);

        gson = new Gson();



    }



    private Api getApi() {
        return mApi;
    }


    public static Single<Response<ChatRoomListResponse>> getGuestRoomListRx() {
        return ApiClient.getInstance().getApi().getGuestRoomList();
    }

    public static Single<Response<ChatRoomListResponse>> getChatRoomList() {
        return ApiClient.getInstance().getApi().getChatRoomList();
    }

    public static Single<Response<FriendListResponse>> getFriendList() {
        return ApiClient.getInstance().getApi().getFriendList();
    }

    public static Single<Response<FollowsResponse>> getFollowsList() {
        return ApiClient.getInstance().getApi().getFollows();
    }

    public static Single<Response<FanListResponse>> getFans( ) {
        return ApiClient.getInstance().getApi().getFans();
    }

    public static Single<Response<DeleteResponse>> deleteFollows(long uid) {
        return ApiClient.getInstance().getApi().deleteFollows(uid);
    }

    public static Single<Response<AddResponse>> addFollows(long uid) {
        return ApiClient.getInstance().getApi().addFollows(uid);
    }
    public static Single<Response<ReportResponse>> reports(int chatroomID,String MsgId,String reportType,String ReportCotent,
                                                        String ReportedUid,String reportText) {
        return ApiClient.getInstance().getApi().reports(chatroomID,MsgId,reportType,ReportCotent,ReportedUid,reportText);
    }

    public static Single<Response<DeleteResponse>> deleteGroup(int uid) {
        return ApiClient.getInstance().getApi().deleteGroup(uid);
    }

    public static Single<Response<CreateResponse>> createRoom(String name,long friendUid) {
        return ApiClient.getInstance().getApi().createRoom(name,4,"","",""
                ,"","","","",4,50,friendUid);
    }

    public static Single<Response<GroupListResponse>> getGroupList( ) {
        return ApiClient.getInstance().getApi().getGroup();
    }

    public static Single<Response<InitResponse>> initWebSocket( ) {
        return ApiClient.getInstance().getApi().initWebSocket();
    }

    public static Single<Response<HistoryMessageResponse>> getRoomHistoryMsg(int roomId,int msgId ) {
        return ApiClient.getInstance().getApi().getRoomHistoryMsg(roomId,msgId);
    }



    public static Single<Response<UploadImageResponse>> uploadImage(String md5Token){
        return ApiClient.getInstance().getApi().uploadImage(md5Token);
    }

    public static Single<Response<UploadImageResponse>> uploadImage(String md5Token,String source,String thumbnail){
        return ApiClient.getInstance().getApi().uploadImage(md5Token,source,thumbnail);
    }

    public static Single<Response<GetPartyListResponse>> getPartyList(String region ){
        return ApiClient.getInstance().getApi().getpartylist(region);
    }


    public static Single<Response<GetRoomListDetailResponse>> getRoomListDetail(String region ){
        return ApiClient.getInstance().getApi().getroomDetail(region);
    }

    public static Single<Response<JoinPartyResponse>> JoinParty(int uid){
        return ApiClient.getInstance().getApi().JoinParty(uid);
    }


    public interface Api {

        //現在還妹有登入，有登入後要記得實裝sharedpreference

        @GET("api/chatroom/init")
        Single<Response<InitResponse>> initWebSocket();

        @POST("api/chatRooms/getGuestRooomList")
        Single<Response<ChatRoomListResponse>> getGuestRoomList();

        @GET("api/chatroom/chatroom_list")
        Single<Response<ChatRoomListResponse>> getChatRoomList();


        @GET("api/user/friend/get_friends")
        Single<Response<FriendListResponse>> getFriendList();


        @GET("api/user/friend/get_follows")
        Single<Response<FollowsResponse>> getFollows();


        @FormUrlEncoded
        @HTTP(method = "DELETE", path = "api/user/friend/dismiss_follow", hasBody = true)
        Single<Response<DeleteResponse>> deleteFollows(@Field("followedUid") long uid);

        @FormUrlEncoded
        @HTTP(method = "POST", path = "api/user/friend/add_follow", hasBody = true)
        Single<Response<AddResponse>> addFollows(@Field("followedUid") long uid);

        @FormUrlEncoded
        @HTTP(method = "POST", path = "api/report/report/chatroom_msg", hasBody = true)
        Single<Response<ReportResponse>> reports(@Field("chatroomId") int uid,
                                                 @Field("msgId") String msgId,
                                                 @Field("reportType") String reportType,
                                                 @Field("reportedContent") String reportedContent,
                                                 @Field("reportedUid") String reportedUid,
                                                 @Field("reportText") String reportText);

        @FormUrlEncoded
        @HTTP(method = "POST", path = "api/chatroom/group/create_chatroom", hasBody = true)
        Single<Response<CreateResponse>> createRoom(@Field("chatroomName") String name,
                                                    @Field("groupId") int uid,
                                                    @Field("adId") String adId,
                                                    @Field("welfareType") String welfareType,
                                                    @Field("description") String description,
                                                    @Field("imageDes1Link") String imageDes1Link,
                                                    @Field("imageDes2Link") String imageDes2Link,
                                                    @Field("imageDes3Link") String imageDes3Link,
                                                    @Field("imageLogoLink") String imageLogoLink,
                                                    @Field("inviteUserType") int inviteUserType,
                                                     @Field("memberLimit") int memberLimit, @Field("friendUid") long friendUid);

        @FormUrlEncoded
        @HTTP(method = "DELETE", path = "api/user/room/del_relation", hasBody = true)
        Single<Response<DeleteResponse>> deleteGroup(@Field("chatroomId") int uid);


        @GET("api/user/friend/get_fans")
        Single<Response<FanListResponse>> getFans();


        @GET("api/user/room/get_room_by_uid")
        Single<Response<GroupListResponse>> getGroup();



        @FormUrlEncoded
        @HTTP(method = "POST", path = "api/user/room/set_room_by_uid", hasBody = true)
        Single<Response<JoinPartyResponse>> JoinParty(@Field("chatroomId") int uid);


        @GET("api/history/roomhistory")
        Single<Response<HistoryMessageResponse>> getRoomHistoryMsg(@Query("roomId") int roomId,@Query("msgId") int msgId);


        @GET("api/chatroom/group/get_party")
        Single<Response<GetPartyListResponse>> getpartylist(@Query("region") String region);

        @GET("api/chatroom/chatroom_info")
        Single<Response<GetRoomListDetailResponse>> getroomDetail(@Query("chatroomIds") String region);

        @FormUrlEncoded
        @POST("api/history/uploadimage")
        Single<Response<UploadImageResponse>> uploadImage(@Field("md5Token") String md5Token,
                                                          @Field("source") String source,
                                                          @Field("thumbnail") String thumbnail);

        @FormUrlEncoded
        @POST("api/history/uploadimage")
        Single<Response<UploadImageResponse>> uploadImage(@Field("md5Token") String md5Token);

        @FormUrlEncoded
        @POST("api/userlog/userlinkerrorlog")
        Single<Response<UploadImageResponse>> userLinkErrorLog(@Field("fingerPrint") String fingerPrint,
                                                               @Field("userAgent") String userAgent,
                                                               @Field("referer") String referer,
                                                               @Field("type") String type,
                                                               @Field("message") String message);
    }

}
