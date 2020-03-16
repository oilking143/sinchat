package com.sex8.sinchat.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Group;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.sex8.sinchat.Constant;
import com.sex8.sinchat.IMMessageApplication;
import com.sex8.sinchat.R;
import com.sex8.sinchat.adapter.ChatMessageAdapter;
import com.sex8.sinchat.dialog.ChatReportDialog;
import com.sex8.sinchat.dialog.ChatRewardDialog;
import com.sex8.sinchat.dialog.LeaveDialog;
import com.sex8.sinchat.entity.PhotoData;
import com.sex8.sinchat.entity.UserInfo;
import com.sex8.sinchat.event.AddFollowEvent;
import com.sex8.sinchat.event.ChatCommentSendEvent;
import com.sex8.sinchat.event.ChatCommentShowEvent;
import com.sex8.sinchat.event.ChatDeleteSendEvent;
import com.sex8.sinchat.event.ChatItemLongClickEvent;
import com.sex8.sinchat.event.ChatSendingFailEvent;
import com.sex8.sinchat.event.DismissEvent;
import com.sex8.sinchat.event.EmojiButtonClickEvent;
import com.sex8.sinchat.event.LeaveEvent;
import com.sex8.sinchat.event.OfflineUnSyncChatEvent;
import com.sex8.sinchat.event.HistoryUpdateFromAPIEvent;
import com.sex8.sinchat.event.ImageSendingToChatEvent;
import com.sex8.sinchat.event.JoinPartyEvent;
import com.sex8.sinchat.event.ReceivedWebSocketDataEvent;
import com.sex8.sinchat.event.MessageItemClickEvent;
import com.sex8.sinchat.event.ReportEvent;
import com.sex8.sinchat.event.SelectImageEvent;
import com.sex8.sinchat.model.IMData;
import com.sex8.sinchat.model.dataBase.Entity.Chatroom_History;
import com.sex8.sinchat.model.dataBase.Entity.Chatroom_Info;
import com.sex8.sinchat.model.dataBase.Entity.Chatroom_Info_Local;
import com.sex8.sinchat.model.dataBase.Entity.Member_Follow;
import com.sex8.sinchat.model.dataBase.Entity.Myroom_List;
import com.sex8.sinchat.model.dataBase.IMRepository;
import com.sex8.sinchat.tools.OnClickLimitListener;
import com.sex8.sinchat.tools.SingleMediaScanner;
import com.sex8.sinchat.utils.MD5Generator;
import com.sex8.sinchat.utils.NameInputFilter;
import com.sex8.sinchat.utils.PhotoUtuls;
import com.sex8.sinchat.view.ChatBottomSheet;
import com.sex8.sinchat.view.ChatPopupWindow;
import com.sex8.sinchat.view.CommentPopupWindow;
import com.sex8.sinchat.view.ShowImageView;
import com.sex8.sinchat.viewmodel.SinchatChatRoomViewModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.sex8.sinchat.IMMessageApplication.getGlobalContext;


public class ChatMessageActivity extends BaseActivity {

    private Chatroom_Info roomInfo;
    private Chatroom_Info_Local localInfo;
    private UserInfo userInfo;
    private LinearLayoutManager linearLayoutManager;
    private ChatMessageAdapter messageAdapter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout mySwipeRefreshLayout;
    private EditText inputEdit;
    private ImageView emojiImage, moreSendImage,scrollBottom;
    private ChatBottomSheet bottomSheetConstraintLayout;
    private ShowImageView showImageView;
    private View rootLayout;
    private ChatPopupWindow chatPopupWindow;
    private TextView enterText;
    private Group inputGroup, enterGroup;
    private ImageView more;
    private LeaveDialog leaveDialog;
    private String TAG ="LogScroll";
    private SinchatChatRoomViewModel viewModel;
    private List<Chatroom_History> chatList = new CopyOnWriteArrayList<>();
    private boolean syncFromRemote = false;//是否已執行過拿50筆資料formAPI或DB的請求
    private boolean syncLatestFromDB = false;//是否已同步過最新資料
    private boolean syncIsMyRoom =false;//比對過是否為myRoom
    private boolean isInit = false;//是否已拿過roomInfo
    private boolean isMyRoom = false;//是否有加入該群


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_chat);
        Intent intent = getIntent();
        findViews();

        //判斷權限
        if (ContextCompat.checkSelfPermission(getGlobalContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);

        }else{

        }

        //檢查該房間是否在我的房間列表中
        IMRepository.getInstance().queryMyRoomByRoomID(intent.getIntExtra("mRoomId",0)).observe(this, new Observer<Myroom_List>() {
            @Override
            public void onChanged(Myroom_List myroom_list) {
                IMRepository.getInstance().queryMyRoomByRoomID(intent.getIntExtra("mRoomId",0)).removeObserver(this);
                if (!syncIsMyRoom) {
                    syncIsMyRoom = true;
                    if (myroom_list == null) {//尚未加入聊天室
                        inputGroup.setVisibility(View.GONE);
                        enterGroup.setVisibility(View.VISIBLE);
                    } else {//已加入聊天室
                        inputGroup.setVisibility(View.VISIBLE);
                        enterGroup.setVisibility(View.GONE);
                        isMyRoom = true;
                    }

                    if(enterGroup.getVisibility()==View.GONE) {
                        mySwipeRefreshLayout.setOnRefreshListener(() -> {
                            get50ChatsFromDB();
                        });
                    } else {
                        mySwipeRefreshLayout.setEnabled(false);
                    }
                }
            }
        });

        //取得房間相關資訊以進行初始化
        IMRepository.getInstance().getChatroomInfo(intent.getIntExtra("mRoomId",0)).observe(this, new Observer<Chatroom_Info>() {
            @Override
            public void onChanged(Chatroom_Info chatroom_info) {
                Log.d(getClass().getSimpleName(), "getChatroomInfo observe");
                IMRepository.getInstance().getChatroomInfo(intent.getIntExtra("mRoomId",0)).removeObserver(this);
                Log.d(getClass().getSimpleName(), "getChatroomInfo removeObserver");
                roomInfo=chatroom_info;
                if (!isInit) {//只會初始化一次
                    isInit = true;
                    init();//初始化元件
                    setListeners();
                    if (roomInfo.getGroupId() == 4) {
                        enterGroup.setVisibility(View.GONE);
                        inputGroup.setVisibility(View.VISIBLE);
                        more.setVisibility(View.GONE);
                    }

                    IMRepository.getInstance().getChatRoomLocalInfoByRoomId(roomInfo.getChatRoomId()).observe(ChatMessageActivity.this, new Observer<Chatroom_Info_Local>() {
                        @Override
                        public void onChanged(Chatroom_Info_Local chatroom_info_local) {
                            IMRepository.getInstance().getChatRoomLocalInfoByRoomId(roomInfo.getChatRoomId()).removeObserver(this);
                            if (chatroom_info_local==null){
                                return;//如果找不到該房間資料則返回
                            }
                            if (!syncFromRemote) {
                                localInfo = chatroom_info_local;
                                syncFromRemote = true;
                                show_Progress_Dialog(null, getString(R.string.dialog_loading));
                                //判斷是否要做API拉取50筆資料
                                if (localInfo.getNeedAPISync()) {//表示第一次進入
                                    //從API抓50筆上次聊天訊息
                                    IMData.getInstance().getRoomHistoryFromApi(roomInfo.getChatRoomId(), 0);
                                    Log.d(getClass().getSimpleName(), "getChatroomInfo getRoomHistoryFromApi()");
                                } else {
                                    Log.d(getClass().getSimpleName(), "getChatroomInfo getLastChatFromDB()");
                                    getLastChatFromDB();//非第一次進入
                                }
                            }
                        }
                    });
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (messageAdapter != null && chatList.size()>0){
            messageAdapter.notifyDataSetChanged();//修改時區後及時刷新對話的時間
        }
    }

    @Override
    public void findViews() {
        rootLayout = findViewById(R.id.rootLayout);
        recyclerView = findViewById(R.id.recyclerView);
        inputEdit = findViewById(R.id.inputEdit);
        emojiImage = findViewById(R.id.emojiImage);
        moreSendImage = findViewById(R.id.addSendImage);
        showImageView = findViewById(R.id.showImageView);
        bottomSheetConstraintLayout = findViewById(R.id.bottomSheet);
        scrollBottom=findViewById(R.id.scroll_bottom);
        enterText = findViewById(R.id.enterText);
        inputGroup = findViewById(R.id.inputGroup);
        enterGroup = findViewById(R.id.enterGroup);
        mySwipeRefreshLayout=findViewById(R.id.mySwipeRefreshLayout);
        more=findViewById(R.id.more);
        more.setVisibility(View.VISIBLE);
    }

    @Override
    public void init() {
        Log.d(getClass().getSimpleName(), "init()");

        userInfo = IMData.getInstance().getUserInfo();
        ((TextView) findViewById(R.id.titleText)).setText(String.valueOf(roomInfo.getChatRoomName()));
        ((TextView) findViewById(R.id.titleText)).setTextColor(ContextCompat.getColor(this, R.color.text_gray_dark));
        viewModel = new ViewModelProvider(this).get(SinchatChatRoomViewModel.class);
        messageAdapter = new ChatMessageAdapter(this, roomInfo, userInfo, chatList);
        linearLayoutManager = new CustomLinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        linearLayoutManager.setReverseLayout(false);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(messageAdapter);
        mySwipeRefreshLayout.setProgressViewOffset(true, 50, 200);
        mySwipeRefreshLayout.setSize(SwipeRefreshLayout.LARGE);
        bottomSheetConstraintLayout.init(inputEdit);
        chatPopupWindow = new ChatPopupWindow(this,roomInfo);
//        chatReportDialog = new ChatReportDialog(this, rootLayout);
//        commentPopupWindow = new CommentPopupWindow(this);
//        chatRewardDialog = new ChatRewardDialog(this, rootLayout);
        ArrayList<Map<String, String>> leave=new ArrayList<>();
        Map<String, String> item = new HashMap<>();
        item.put("_group_name","退出群組");
        item.put("_group_id",""+roomInfo.getChatRoomId());
        leave.add(item);
//        locationDialog = new LocationDialog(this,roomInfo,"leave");
        leaveDialog=new LeaveDialog(this,leave);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                //屏幕中最后一个可见子项的position
                int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
                //当前屏幕所看到的子项个数
                int visibleItemCount = layoutManager.getChildCount();
                //当前RecyclerView的所有子项个数
                int totalItemCount = layoutManager.getItemCount();
                //RecyclerView的滑动状态
                int state = recyclerView.getScrollState();
                if(visibleItemCount > 0 && lastVisibleItemPosition == totalItemCount - 1 && state == recyclerView.SCROLL_STATE_IDLE){
                 scrollBottom.setVisibility(View.GONE);
                }

            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if(messageAdapter.getItemCount()-linearLayoutManager.findFirstVisibleItemPosition()<5){
                    scrollBottom.setVisibility(View.GONE);
                }

            }
        });


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    synchronized public void EmojiButtonClick(EmojiButtonClickEvent event) {
        String tmp=inputEdit.getText().toString();
        inputEdit.setText(tmp+event.getMessage());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    synchronized public void onLeave(LeaveEvent event) {
        if(event.getMessage().equals("popwindows")) {
            Log.d("status",""+event.getStatus());
            Myroom_List myroom = new Myroom_List();
            myroom.setChatroomId(roomInfo.getChatRoomId());
            IMRepository.getInstance().deleteMyroom(myroom);
            finish();
        }

    }


        @Override
    public void setListeners() {
        findViewById(R.id.back).setOnClickListener(btnClick);
        emojiImage.setOnClickListener(btnClick);
        moreSendImage.setOnClickListener(btnClick);
        enterText.setOnClickListener(btnClick);
        more.setOnClickListener(btnClick);
        final NameInputFilter filter=new NameInputFilter();
        inputEdit.setFilters(new InputFilter[]{filter});
        inputEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                moreSendImage.setActivated(!inputEdit.getText().toString().isEmpty());
            }
        });
            scrollBottom.setOnClickListener(btnClick);
    }

    public boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if( !isNum.matches() ){
            return false;
        }  return true;
    }

    public static String fmtMicrometer(String text) {
        DecimalFormat df = null;

            df = new DecimalFormat("#,###");

        double number = 0.0;
        try {
            number = Double.parseDouble(text);
        } catch (Exception e) {
            number = 0.0;
        }
        return df.format(number);
    }

    private OnClickLimitListener btnClick = new OnClickLimitListener() {
        @Override
        public void onClickLimit(View v) {
            switch (v.getId()){
                case R.id.back:
                    backMain();
                    break;
                case R.id.emojiImage:
                    bottomSheetConstraintLayout.emojiToggle();
                    break;
                case R.id.addSendImage:
                    if(moreSendImage.isActivated()){
                        String message = inputEdit.getText().toString();

                        if(isNumeric(message) && message.length()>15) {
                            message+="#@#";
                        }

                        if (message.length() > 0) {
                            messageAdapter.sendMessage(message);
                            inputEdit.setText(null);
                            handler.post(scrollRunnable);
                        }

                        Bundle bundle = new Bundle();
                        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "發送對話");
                        IMMessageApplication.application.mFirebaseAnalytics.logEvent("行為", bundle);
                        scrollBottom.setVisibility(View.GONE);
                    }else{
                        bottomSheetConstraintLayout.addToggle();
                    }
                    break;
                case R.id.enterText:
                    IMData.getInstance().JoinParty(roomInfo.getChatRoomId());

                    break;

                case R.id.more:

                    leaveDialog.show();
                    break;

                case R.id.inputEdit:
                case R.id.scroll_bottom:
                    handler.post(scrollRunnable);
                    scrollBottom.setVisibility(View.GONE);
                    break;

            }
        }
    };

    private void backMain(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        finish();
    }

    public Runnable scrollRunnable = new Runnable() {
        @Override
        public void run() {
            dismiss_Progress_Dialog();

            if(messageAdapter.getItemCount() > 0) {
                if (messageAdapter.getItemCount() - linearLayoutManager.findFirstVisibleItemPosition() > 10) {
                    recyclerView.scrollToPosition(messageAdapter.getItemCount() - 10);
                }
                recyclerView.smoothScrollToPosition(messageAdapter.getItemCount() - 1);
            }

        }
    };

    public Runnable progrossRunnable = new Runnable() {
        @Override
        public void run() {
            dismiss_Progress_Dialog();
        }
    };


    @Override
    public void onBackPressed() {
        if(showImageView.isShow())
            showImageView.dismiss();
        else {
            backMain();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(IMMessageApplication.checkPermissionsResult(this, permissions, grantResults)){
            switch (requestCode){
                case IMMessageApplication.Request_Permission_Read:
                    bottomSheetConstraintLayout.getAddMoreView().goSelectImage();
                    break;
                case IMMessageApplication.Request_Permission_Camera:
                    bottomSheetConstraintLayout.getAddMoreView().takePicture();
                    break;
            }
        }else
            IMMessageApplication.Toast(getString(R.string.permission_denied), Toast.LENGTH_LONG);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK
                && (requestCode == PhotoUtuls.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE
                || requestCode == PhotoUtuls.RECORD_VIDEO_ACTIVITY_REQUEST_CODE)){
            new SingleMediaScanner(this, IMMessageApplication.tmpFile);
            List<PhotoData> selectList = new ArrayList<>();
            selectList.add(new PhotoData(IMMessageApplication.tmpFile));
            EventBus.getDefault().post(new SelectImageEvent(selectList));
        }
    }

    public void get50ChatsFromDB(){//靠List的頭一筆資料往前拉50筆DB資料
        viewModel.getChatHistory50ByLastMsgid(roomInfo.getChatRoomId(), chatList.size()>0?chatList.get(0).getMsgId():"0").observe(this, new Observer<List<Chatroom_History>>() {
            @Override
            public void onChanged(List<Chatroom_History> chatroom_histories) {
                viewModel.getChatHistory50ByLastMsgid(roomInfo.getChatRoomId(), chatList.size()>0?chatList.get(0).getMsgId():"0").removeObserver(this);

                if (mySwipeRefreshLayout.isRefreshing()==false) return;//如果不在更新中則返回

                mySwipeRefreshLayout.setRefreshing(false);

                if (chatList.size()>0 && chatList.get(0).getUsername().equals("System") && chatList.get(0).getMsgId().equals("000000000000")) {
                    mySwipeRefreshLayout.setEnabled(false);
                    return;//檢查頭一筆若為無更多資料, 則返回
                }

                /**
                 * 取出資料逆排序已符合時序
                 * */
                Collections.reverse(chatroom_histories);

                if (chatroom_histories.size() <50) {//若不足50筆, 表示無更多資料, 關閉刷新功能
                    addNoHistoryFakeDataOnListFirst(chatroom_histories);
                    mySwipeRefreshLayout.setEnabled(false);
                }



               Log.d("get50ChatsFromDB",chatroom_histories.get(0).getContent());

                chatList.addAll(0, chatroom_histories);
                messageAdapter.notifyItemRangeInserted(0, chatroom_histories.size());
            }
        });
    }

    public void getLastChatFromDB(){//直接抓最新的50筆DB資料
        viewModel.getLastCharHistory(roomInfo.getChatRoomId()).observe(this, new Observer<List<Chatroom_History>>() {
            @Override
            public void onChanged(List<Chatroom_History> chatroom_histories) {
                viewModel.getLastCharHistory(roomInfo.getChatRoomId()).removeObserver(this);

                if (!syncLatestFromDB) {
                    dismiss_Progress_Dialog();
                    syncLatestFromDB = true;//已同步過則不再同步

                    if (chatroom_histories.size()==0){
                        return; //如果沒有資料, 直接退出
                    }

                    if (chatList.size()>0 && chatList.get(0).getUsername().equals("System") && chatList.get(0).getMsgId().equals("000000000000")) {
                        mySwipeRefreshLayout.setEnabled(false);
                        return;//檢查頭一筆若為無更多資料, 則返回
                    }

                    Log.d(getClass().getSimpleName(), "getLastChatFromDB()");

                    /**
                     * 取出資料逆排序已符合時序
                     * */
                    Collections.reverse(chatroom_histories);

                    if (chatroom_histories.size() >0 && chatroom_histories.size() < 50) {//若不足50筆, 表示無更多資料, 關閉刷新功能
                        addNoHistoryFakeDataOnListFirst(chatroom_histories);
                        mySwipeRefreshLayout.setEnabled(false);
                    }

                    chatList.addAll(0, chatroom_histories);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            messageAdapter.notifyItemRangeInserted(0, chatroom_histories.size());
//                            開始跟WebSocket拉上次進入訊息
                            if (chatList.size() > 0) {
                                String lastMsgid = chatList.get(chatList.size() - 1).getMsgId();
//                                IMData.getInstance().sendSync(roomInfo.getChatRoomId(), localInfo.getLastMsgTimeStamp()+"-0");
                                IMData.getInstance().sendSync(roomInfo.getChatRoomId(), lastMsgid);

//                                show_Progress_Dialog(null, getString(R.string.dialog_loading));
                                new Handler().postDelayed(new Runnable() {//三秒後刷新畫面
                                    @Override
                                    public void run() {
                                        messageAdapter.notifyDataSetChanged();//刷新列表
                                        handler.post(scrollRunnable);

                                        //更新最後一筆資料到ChatRoomLocalInfo
                                        localInfo.setNeedAPISync(false);
                                        localInfo.setLastMsgTimeStamp(chatList.get(chatList.size()-1).getTimestamp());
                                        localInfo.setLastMsgContent(chatList.get(chatList.size()-1).getContent());
                                        localInfo.setUnreadMessage(false);
                                        IMRepository.getInstance().insertChatRoomLocalInfo(localInfo);
                                    }
                                },roomInfo.getGroupId()==4?1500:3000);
                            }
                        }
                    },500);
                }
            }


        });
    }

    private void addNoHistoryFakeDataOnListFirst(List<Chatroom_History> chatroom_histories){
        Chatroom_History chat = new Chatroom_History();
        chat.setMsgId("000000000000");
        chat.setUsername("System");
        chat.setAvatar("");
        chat.setChatRoomId(roomInfo.getChatRoomId());
        chat.setChatType("system");
        chat.setContent("");
        chat.setUid(444444444);
        chat.setTimestamp(000000000000);
        chatroom_histories.add(0, chat);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    synchronized public void onJoinParty(JoinPartyEvent event) {

        if (event.getStatus()==0) {
            inputGroup.setVisibility(View.VISIBLE);
            enterGroup.setVisibility(View.GONE);

            //加入房間成功, 加入DB我的房間列表
            Myroom_List myroom_list = new Myroom_List();
            myroom_list.setChatroomId(roomInfo.getChatRoomId());
            IMRepository.getInstance().insertMy_Room_List(myroom_list);

            isMyRoom = true;
        }else{
            if(event.getMessage().length()>0) {
              enterText.setText(event.getMessage());
            } else {
                enterText.setText("回傳錯誤");
            }

        }
    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    synchronized public void onReport(ReportEvent event) {
        if (event.getStatus()==1) {

                Toast.makeText(this,event.getMessage(),Toast.LENGTH_LONG).show();

        }

    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    synchronized public void onHistoryMessageUpdate(HistoryUpdateFromAPIEvent event) {
//        Log.d("roomID",""+currentRoomInfo.getRoomId());

//        if (currentRoomInfo != null ) {
//            dismiss_Progress_Dialog();
//            messageAdapter.updateHistoryMessage(event.getHistory());
//            handler.post(scrollRunnable);
//
//        }else{
//            Toast.makeText(this, event.getMessage(), Toast.LENGTH_SHORT).show();
//        }


        if (roomInfo != null ) {
            dismiss_Progress_Dialog();
            viewModel.insertChatHistoryList(event.getHistory());//把歷史資料寫入DB

            chatList.addAll(0, event.getHistory());
            messageAdapter.notifyItemRangeInserted(0, event.getHistory().size());//刷新列表
//            messageAdapter.notifyDataSetChanged();

            //寫入DB告知不需再API同步資料
            Chatroom_History chat = event.getHistory().get(event.getHistory().size()-1);
            localInfo.setNeedAPISync(false);
            localInfo.setLastMsgTimeStamp(chat.getTimestamp());
            localInfo.setLastMsgContent(chat.getContent());
            localInfo.setUnreadMessage(false);
            IMRepository.getInstance().insertChatRoomLocalInfo(localInfo);
            Log.d(getClass().getSimpleName(), "HistoryUpdateFromAPIEvent insertChatRoomLocalInfo");
            //列表置底
            handler.post(scrollRunnable);



        }else{
            Toast.makeText(this, event.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    synchronized public void onMessageUpdate(MessageUpdateEvent event) {
//        if (roomInfo != null && event.getChat().getChatRoomId() == roomInfo.getChatRoomId()) {
//            viewModel.insertChatHistory(event.getChat());//寫入DB
//            chatList.add(event.getChat());
////            messageAdapter.notifyDataSetChanged();
//            messageAdapter.notifyItemInserted(chatList.size()-1);
//            if (event.getChat().getUid() == userInfo.getUid()) {
//                handler.post(scrollRunnable);
//            } else {
//                if (linearLayoutManager.findLastVisibleItemPosition() >= messageAdapter.getItemCount() - 2) {
//                    handler.post(scrollRunnable);
//                } else {
//                    //????
//                }
//            }
//        }
//    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    synchronized public void onSelectImageEvent(SelectImageEvent event) {
        for(int i=0;i<event.getSelectList().size();i++) {
            sendImageFileToServer(event.getSelectList().get(i).path);
        }

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "選擇相片");
        IMMessageApplication.application.mFirebaseAnalytics.logEvent("行為", bundle);
    }

    private void sendImageFileToServer(String path){
        File file=new File(path);
        try {
            MessageDigest md5Digest = MessageDigest.getInstance("MD5");
            MD5Generator md5=new MD5Generator(md5Digest, file);
            String checksum=md5.getMd5String();

            Bitmap compressImage10 =MD5Generator.compressImageSize100(path);
            Bitmap compressImage100=MD5Generator.compressImageSize300(path);

            String sourcePath;
            String thumbnailPath;

            if(path.toUpperCase().contains(".GIF")) {
                sourcePath = PhotoUtuls.encodeGifToBase64(path);
                thumbnailPath = PhotoUtuls.encodeGifToBase64(path);
            } else {
                sourcePath="data:image/png;base64,"+MD5Generator.bitmapToBase64(compressImage100);
                thumbnailPath="data:image/png;base64,"+MD5Generator.bitmapToBase64(compressImage10);
            }

            Chatroom_History chat=new Chatroom_History();
            JSONObject data = new JSONObject ();
            JSONObject content = new JSONObject();
            data.put("md5Token",checksum);//會透過API uploadImage 返回正確的路徑
            data.put("sourcePath",sourcePath);//會透過API uploadImage 返回正確的路徑
            data.put("thumbnailPath",thumbnailPath);//會透過API uploadImage返回正確的路徑
            data.put("Local_filename",path);
            content.put("data", data);//封裝一層"data"
            content.put("submitTime", new Date().getTime());//用此方式得以識別返回對話的身分
            chat.setChatType("image");
            chat.setUid(userInfo.getUid());
            chat.setContent(content.toString());
            chat.setAvatar(userInfo.getImageUrl());
            chat.setChatRoomId(roomInfo.getChatRoomId());
            chat.setIsAdmin(userInfo.getIsAdmin());
            chat.setSendingStatus(1);

            IMData.getInstance().uploadImage(checksum, sourcePath, thumbnailPath, path, roomInfo, chat);
            EventBus.getDefault().post(new ImageSendingToChatEvent(Constant.RESPONSE_SUCCESS1, "", chat));//告知河道有一個發送中的圖片

        }catch (JSONException | IOException | NoSuchAlgorithmException e) {
            Log.e(getClass().getSimpleName(), e.getMessage(), e);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    synchronized public void onImageSendingToChat(ImageSendingToChatEvent event) {

        if (event.getCode()== Constant.RESPONSE_SUCCESS1){
            chatList.add(event.getChat());
            messageAdapter.notifyItemRangeChanged(chatList.size()-1, 1);//刷新列表
            recyclerView.scrollToPosition(chatList.size()-1);
        }else {
            if (event.getMsg().length() > 5) {
                showTipDialog(event.getMsg());
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    synchronized public void onMessageItemClickEvent(MessageItemClickEvent event) {
        switch (event.getType()){
            case ShowImage:
                Intent intent = new Intent(this, PhotoViewerActivity.class);
                Bundle mBundle = new Bundle();
                mBundle.putString("file",event.getPath());
                intent.putExtras(mBundle);
                startActivity(intent);
            break;
        }
    }


//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    synchronized public void onSendingMessageUpdate(SendingMessageStatusUpdateEvent event) {
//        messageAdapter.updateMessage(event.getChat());
//
//    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    synchronized public void onChatSendingFail(ChatSendingFailEvent event) {
        try {
            for (Chatroom_History chat: chatList){
                if (event.getChat().getContent().equals(chat.getContent()) && chat.getSendingStatus()==1){
                    Log.d(getClass().getSimpleName(), "onChatSendingFail chat: " + chat.getContent());
                    int removePosition = chatList.indexOf(event.getChat());
                    chatList.remove(event.getChat());
                    messageAdapter.notifyItemRemoved(removePosition);
                    break;
                }


//                long eventSubmitTime = new JSONObject(event.getChat().getContent()).getLong("submitTime");
//                long chatSubmitTime = new JSONObject(chat.getContent()).getLong("submitTime");
//                Log.d(getClass().getSimpleName(), "onChatSendingFail eventSubmitTime: " + eventSubmitTime + " chatSubmitTime: " + chatSubmitTime );
//                if (eventSubmitTime==chatSubmitTime && chat.getSendingStatus()==1){
//                    int modifyPoition = chatList.indexOf(event.getChat());
//                    Chatroom_History item = chatList.get(modifyPoition);
//                    item.setSendingStatus(2/**fail*/);
//                    messageAdapter.notifyItemChanged(modifyPoition);
//                    break;
//                }
            }

        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), e.getMessage(), e);
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    synchronized public void onReceivedWebSocketData(ReceivedWebSocketDataEvent event) {


        try {

        if(event.getResult().getInt("roomId")==roomInfo.getChatRoomId()) {
            Chatroom_History chat = new Chatroom_History();
            chat.setMsgId(event.getResult().getString("messageId"));
            chat.setUsername(event.getResult().getString("fromUserName"));
            chat.setAvatar(event.getResult().getString("fromUserPic"));
            chat.setChatRoomId(event.getResult().getInt("roomId"));
            chat.setChatType(event.getResult().getString("contentType"));
            chat.setContent(event.getResult().getString("content"));
            chat.setUid(event.getResult().getInt("fromUserId"));
            chat.setTimestamp(Long.valueOf(event.getResult().getString("messageId").substring(0,13)));
            if(chat.getUid()==userInfo.getUid()) {
                for (Chatroom_History chatItem : chatList) {//清除列表中的發送中留言
                    if (event.getResult().getString("content").equals(chatItem.getContent()) && chatItem.getSendingStatus() == 1) {
                        int removePosition = chatList.indexOf(chatItem);
                        Log.d("remove", "content: " + chatItem.getContent());
                        chatList.remove(chatItem);
                        messageAdapter.notifyItemRemoved(removePosition);
                        break;
                    }
                }
            }


            viewModel.insertChatHistory(chat);//寫入DB
            chatList.add(chat);
            messageAdapter.notifyItemRangeInserted(chatList.size()-1, 1);//刷新列表

           if(messageAdapter.getItemCount()-linearLayoutManager.findFirstVisibleItemPosition()<8) {
               handler.post(scrollRunnable); }
           else {
             scrollBottom.setVisibility(View.VISIBLE);
           }

           //更新最後一筆資料到ChatRoomLocalInfo
           localInfo.setNeedAPISync(false);
           localInfo.setLastMsgTimeStamp(chat.getTimestamp());
           localInfo.setLastMsgContent(chat.getContent());
           localInfo.setUnreadMessage(false);
           IMRepository.getInstance().insertChatRoomLocalInfo(localInfo);
        }


        }catch (Exception e)
        {
            Log.e(getClass().getSimpleName(), e.getMessage(), e);
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    synchronized public void onDismiss(DismissEvent event) {
        finish();

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    synchronized public void receiveOffileUnSyncChatEvent(OfflineUnSyncChatEvent event) {
        //觸發sync三秒後直接一口氣刷新畫面, 在這邊只做資料填充
        try {
            if(event.getResult().getInt("roomId")==roomInfo.getChatRoomId()) {
                Chatroom_History chat = new Chatroom_History();
                chat.setMsgId(event.getResult().getString("messageId"));
                chat.setUsername(event.getResult().getString("fromUserName"));
                chat.setAvatar(event.getResult().getString("fromUserPic"));
                chat.setChatRoomId(event.getResult().getInt("roomId"));
                chat.setChatType(event.getResult().getString("contentType"));
                chat.setContent(event.getResult().getString("content"));
                chat.setUid(event.getResult().getInt("fromUserId"));
                chat.setTimestamp(Long.valueOf(event.getResult().getString("messageId").substring(0,13)));
                viewModel.insertChatHistory(chat);//寫入DB
                chatList.add(chat);
            }
        }catch (JSONException j)
        {
            Log.e(getClass().getSimpleName(), j.getMessage(), j);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    synchronized public void onChatItemLongClick(ChatItemLongClickEvent event) {
        if (isMyRoom) {
            chatPopupWindow.show(event.getAnchor(), event.getChat());
            handler.postDelayed(timecount, 3000);
        }
    }

    Runnable timecount=new Runnable() {
        @Override
        public void run() {
            chatPopupWindow.dismiss();
        }
    };

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    synchronized public void onChatDeleteSend(ChatDeleteSendEvent event) {
//        messageAdapter.removeMessage(event.getChat());
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    synchronized public void onChatCommentShow(ChatCommentShowEvent event) {
//        commentPopupWindow.show(event.getAnchor(), event.getChat());
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    synchronized public void onChatCommentSend(ChatCommentSendEvent event) {
//        Chatroom_History chat = event.getChat();
//        if(message.commentArray == null)
//            message.commentArray = new SparseIntArray();
//        message.commentArray.put(event.getCommentType(), message.commentArray.get(event.getCommentType(), 0) + 1);
//        messageAdapter.updateMessage(chat);
//    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    synchronized public void onAddFollowComplete(AddFollowEvent event) {
        dismiss_Progress_Dialog();
        if(event.getStatus()==0) {
            Member_Follow follow  = new Member_Follow();
            follow.setUid(event.getMember().getUid());
            IMRepository.getInstance().insertFollow(follow);//增加關注
        } else {
            showToast(getString(R.string.fail_connect));
        }
    }


    public class CustomLinearLayoutManager extends LinearLayoutManager {
        public CustomLinearLayoutManager(Context context) {
            super(context);
        }

        //Generate constructors

        @Override
        public boolean supportsPredictiveItemAnimations() {
            return false;
        }
    }
}
