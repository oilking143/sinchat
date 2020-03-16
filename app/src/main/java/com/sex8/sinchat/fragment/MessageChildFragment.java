package com.sex8.sinchat.fragment;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Group;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.sex8.sinchat.R;
import com.sex8.sinchat.activity.ChatMessageActivity;
import com.sex8.sinchat.activity.MainActivity;
import com.sex8.sinchat.decoration.LineDividerItemDecoration;
import com.sex8.sinchat.event.DoneEvent;
import com.sex8.sinchat.event.GroupListEvent;
import com.sex8.sinchat.event.MainEvent;
import com.sex8.sinchat.event.ReceivedWebSocketDataEvent;
import com.sex8.sinchat.model.IMData;
import com.sex8.sinchat.model.dataBase.Entity.Chatroom_History;
import com.sex8.sinchat.model.dataBase.Entity.Chatroom_Info;
import com.sex8.sinchat.model.dataBase.Entity.Chatroom_Info_Local;
import com.sex8.sinchat.model.dataBase.IMRepository;
import com.sex8.sinchat.model.server.pojo.roomlist.ChatRoom;
import com.sex8.sinchat.model.server.pojo.roomlist.GroupRoom;
import com.sex8.sinchat.viewmodel.ChatRoomInfoViewModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class MessageChildFragment extends BaseFragment {
    private RecyclerView recyclerView;
    private Group noPartyGroup;
    private LinearLayoutManager linearLayoutManager;
    private MessageChildAdapter messageChildAdapter;
    private boolean isGroup = false;
    private List<GroupRoom> groupRoomList=new ArrayList<>();
    private List<ChatRoom> chatRoomList=new ArrayList<>();
    private boolean deleteFlag=false;
    private ChatRoomInfoViewModel viewModel;
    private List<Chatroom_Info_Local> refresh_Chat_Local;
    private List<Chatroom_Info> localChatRooms;
    private boolean clearPrivateChatProceed=false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if(recyclerView==null){
            return inflater.inflate(R.layout.fragment_party_chlid, container, false);
        }else{
            return thisView;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        findViews();
        init();
        if (!viewModel.getMyPartyList().hasObservers()) {
            viewModel.getMyPartyList().observe(this, chatroom_infos -> {
                localChatRooms=chatroom_infos;
                uploadData(chatroom_infos);
            });
        }
        setListeners();

    }

    @Override
    public void onResume() {
        super.onResume();
        if(!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        if (isGroup) {
            IMRepository.getInstance().getMyGroupRoomLocals().observe(this, new Observer<List<Chatroom_Info_Local>>() {
                @Override
                public void onChanged(List<Chatroom_Info_Local> chatroom_info_locals) {
                    IMRepository.getInstance().getMyGroupRoomLocals().removeObserver(this);
                    if (refresh_Chat_Local == null) {
                        refresh_Chat_Local = chatroom_info_locals;
                        for(GroupRoom groupRoom :groupRoomList)
                        {
                            for(Chatroom_Info_Local local:chatroom_info_locals) {

                                if(groupRoom.getGroupId()==local.getChatRoomId())
                                {
                                    groupRoom.setLastMessage(local.getLastMsgContent());
                                    groupRoom.setLastMessageTimeStamp(local.getLastMsgTimeStamp());
                                }

                            }
                        }
                        Collections.sort(groupRoomList, new Comparator<GroupRoom>() {
                            @Override
                            public int compare(GroupRoom o1, GroupRoom o2) {
                                return (int)(o2.getLastMessageTimeStamp()-o1.getLastMessageTimeStamp());
                            }
                        });
                        messageChildAdapter.notifyItemRangeChanged(0,groupRoomList.size());
                    }

                }
            });
        }else {
            IMRepository.getInstance().getMyPrivateRoomLocals().observe(this, new Observer<List<Chatroom_Info_Local>>() {
                @Override
                public void onChanged(List<Chatroom_Info_Local> chatroom_info_locals) {
                    IMRepository.getInstance().getMyPrivateRoomLocals().removeObserver(this);
                    if (refresh_Chat_Local == null) {
                        refresh_Chat_Local = chatroom_info_locals;
                        for(ChatRoom chatRoom :chatRoomList)
                        {
                            for(Chatroom_Info_Local local:chatroom_info_locals) {

                                if(chatRoom.getRoomId()==local.getChatRoomId())
                                {
                                    chatRoom.setLastMessage(local.getLastMsgContent());
                                    chatRoom.setLastMessageTimeStamp(local.getLastMsgTimeStamp());
                                }

                            }
                        }
                        Collections.sort(chatRoomList, new Comparator<ChatRoom>() {
                            @Override
                            public int compare(ChatRoom o1, ChatRoom o2) {
                                return (int)(o2.getLastMessageTimeStamp()-o1.getLastMessageTimeStamp());
                            }
                        });
                        messageChildAdapter.notifyItemRangeChanged(0,chatRoomList.size());
                    }

                }
            });

        }

    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();

    }

    private void findViews() {
        recyclerView = thisView.findViewById(R.id.recyclerView);
        noPartyGroup = thisView.findViewById(R.id.noPartyGroup);
    }

    private void init() {
        if (getArguments() != null) {
            isGroup = getArguments().getBoolean("group", true);
        }
        messageChildAdapter = new MessageChildAdapter();
        linearLayoutManager = new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(messageChildAdapter);
        recyclerView.addItemDecoration(new LineDividerItemDecoration(activity));
        try {
            PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(getClass().getSimpleName(), e.getMessage(), e);
        }
        //textView文本插入圖片
        TextView noPartyHintText = thisView.findViewById(R.id.noPartyHintText);
        int start = noPartyHintText.getText().toString().indexOf('[');
        int end = noPartyHintText.getText().toString().lastIndexOf(']') + 1;
        ImageSpan span = new ImageSpan(activity, R.drawable.ic_location, ImageSpan.ALIGN_BOTTOM);
        SpannableStringBuilder ssb = new SpannableStringBuilder(noPartyHintText.getText());
        ssb.setSpan(span, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        noPartyHintText.setText(ssb);
        viewModel = new ViewModelProvider(this).get(ChatRoomInfoViewModel.class);

    }

    private void setListeners() {
        messageChildAdapter.setItemClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) v.getTag();
                final int position = viewHolder.getAdapterPosition();

                switch (v.getId()) {
                    case R.id.mainLayout:

//                            Intent intent = new Intent(getActivity(), MessageChatActivity.class);
                            Intent intent = new Intent(getActivity(), ChatMessageActivity.class);
                            Bundle mBundle = new Bundle();

                            if(isGroup)
                            {
                                mBundle.putString("RoomName", groupRoomList.get(position).getGroupName());
                                mBundle.putInt("mRoomId", groupRoomList.get(position).getGroupId());
                                mBundle.putBoolean("private", false);
                                groupRoomList.get(position).setIsnew(false);
                            }
                            else
                            {
                                mBundle.putString("RoomName", chatRoomList.get(position).getName());
                                mBundle.putInt("mRoomId", chatRoomList.get(position).getRoomId());
                                mBundle.putBoolean("private", true);
                                chatRoomList.get(position).setIsnew(false);
                            }
                        messageChildAdapter.notifyItemRangeChanged(position,groupRoomList.size());

                        intent.putExtras(mBundle);
                            startActivity(intent);
                        break;

                    case R.id.deleMessageteLayout:

                       final Dialog mdialog = new Dialog(activity, R.style.ActionSheetDialogStyle);
                        View view = LayoutInflater.from(activity).inflate(
                                R.layout.dialoag_confirm, null);
                        view.setMinimumWidth(10000);
                        mdialog.setContentView(view);
                        Window dialogWindow = mdialog.getWindow();
                        assert dialogWindow != null;
                        dialogWindow.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
                        DisplayMetrics metrics = new DisplayMetrics();
                        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
                        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
                        lp.x = 0;
                        lp.y = 0;
                        lp.height = metrics.heightPixels / 2;
                        lp.width = metrics.widthPixels;
                        dialogWindow.setAttributes(lp);

                        TextView dismiss=(TextView)mdialog.findViewById(R.id.dismiss);
                        TextView confirm=(TextView)mdialog.findViewById(R.id.confirm);
                        TextView msgtxt=(TextView)mdialog.findViewById(R.id.message);
                        LinearLayout frame=(LinearLayout)mdialog.findViewById(R.id.frame);
                        msgtxt.setText(getActivity().getResources().getString(R.string.delete_history));


                        dismiss.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mdialog.dismiss();

                            }
                        });

                        confirm.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(isGroup)
                                {
                                    IMRepository.getInstance().deleteChatHistory(groupRoomList.get(position).getGroupId());

                                    for(Chatroom_Info_Local local:refresh_Chat_Local) {
                                        if(groupRoomList.get(position).getGroupId()==local.getChatRoomId()) {
                                            try {
                                                Log.d("chatRoomListRoomID",""+groupRoomList.get(position).getGroupId());
                                                JSONObject object=new JSONObject();
                                                object.put("data","");
                                                object.put("submitTime",local.getLastMsgTimeStamp());
                                                local.setLastMsgContent(object.toString());
                                                IMRepository.getInstance().insertChatRoomLocalInfo(local);
                                                new Handler().postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        //延遲兩百豪秒之後插入刪除紀錄
                                                        //讓聊天室資料跟SOCKET同步
                                                        if(local.getLastMsgTimeStamp()!=0)
                                                        {
                                                            InsertDeletedHistoryMessage(local.getChatRoomId(), local.getLastMsgTimeStamp());

                                                        }

                                                    }
                                                },200);
                                            }catch (Exception e) {
                                                Log.e("exception",e.getMessage(),e);
                                            }
                                            break;
                                        }

                                    }



                                    groupRoomList.remove(position);
                                    messageChildAdapter.notifyItemRemoved(position);
                                    mdialog.dismiss();
                                }
                                else
                                {
                                    clearPrivateChatProceed=true;
                                    IMRepository.getInstance().deleteChatHistory(chatRoomList.get(position).getRoomId());

                                    for(Chatroom_Info_Local local:refresh_Chat_Local) {
                                        if(chatRoomList.get(position).getRoomId()==local.getChatRoomId()) {
                                          try {
                                                Log.d("chatRoomListRoomID",""+chatRoomList.get(position).getRoomId());
                                                JSONObject object=new JSONObject();
                                                object.put("data","");
                                                object.put("submitTime",local.getLastMsgTimeStamp());
                                                local.setLastMsgContent(object.toString());
                                                IMRepository.getInstance().insertChatRoomLocalInfo(local);
                                              new Handler().postDelayed(new Runnable() {
                                                  @Override
                                                  public void run() {
                                                      //延遲兩百豪秒之後插入刪除紀錄
                                                      //讓聊天室資料跟SOCKET同步
                                                      if(local.getLastMsgTimeStamp()!=0)
                                                      {
                                                          InsertDeletedHistoryMessage(local.getChatRoomId(), local.getLastMsgTimeStamp());

                                                      }

                                                  }
                                              },200);
                                              }catch (Exception e) {
                                                Log.e("exception",e.getMessage(),e);
                                                }
                                                break;
                                              }

                                    }
                                    chatRoomList.remove(position);
                                    messageChildAdapter.notifyItemRemoved(position);
                                    mdialog.dismiss();

                                }

                            }
                        });

                        mdialog.show();

                        break;

                }
            }
        });

        messageChildAdapter.setitemLongClick(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) v.getTag();
                int position = viewHolder.getAdapterPosition();
                MessageChildAdapter.ViewHolder vh = (MessageChildAdapter.ViewHolder) viewHolder;


                switch (v.getId()) {
                    case R.id.mainLayout:
                        deleteFlag=true;
                        messageChildAdapter.notifyDataSetChanged();
                        EventBus.getDefault().post(new DoneEvent(0,"done"));

                        break;
                }

                return true;
            }
        });
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    synchronized public void onDone(DoneEvent event) {

        if(event.getStatus()==1)
        {
            deleteFlag=false;
            messageChildAdapter.notifyDataSetChanged();

        }

    }



    public void uploadData(List<Chatroom_Info> chatRoom_Info){
        if (isGroup) {
                groupRoomList.clear();
            for(int i=0;i<chatRoom_Info.size();i++) {
                if(chatRoom_Info.get(i).getChatRoomName()!=null && chatRoom_Info.get(i).getGroupId()!=4)
                {
                    GroupRoom group=new GroupRoom();
                    group.setGroupId(chatRoom_Info.get(i).getChatRoomId());
                    if (IMData.getInstance().getWebSocketInfo()==null || IMData.getInstance().getWebSocketInfo().getFileHosts()==null) {
                        group.set_imageLogoLink("");
                    }else{
                        group.set_imageLogoLink(IMData.getInstance().getWebSocketInfo().getFileHosts().get(0)+chatRoom_Info.get(i).get_imageLogoLink());
                    }
                    group.setGroupName(chatRoom_Info.get(i).getChatRoomName()
                            +"("+chatRoom_Info.get(i).getMembercounts()+"人)");
                    if (refresh_Chat_Local!=null) {
                        for (Chatroom_Info_Local local: refresh_Chat_Local) {
                            if (group.getGroupId()==local.getChatRoomId()){
                                group.setLastMessageTimeStamp(local.getLastMsgTimeStamp());
                                group.setLastMessage(local.getLastMsgContent());
                                group.setIsnew(local.isUnreadMessage());
                                break;
                            }
                        }
                    }

                    groupRoomList.add(group);

                    Collections.sort(groupRoomList, new Comparator<GroupRoom>() {
                        @Override
                        public int compare(GroupRoom o1, GroupRoom o2) {
                            return (int)(o2.getLastMessageTimeStamp()-o1.getLastMessageTimeStamp());
                        }
                    });
                }
            }

            if(groupRoomList.size()>0)
            {
                noPartyGroup.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
            else
            {
                noPartyGroup.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }

            if(  refresh_Chat_Local!=null)
            {
                if(groupRoomList.size()==refresh_Chat_Local.size())
                {
                    messageChildAdapter.notifyItemRangeChanged(0,groupRoomList.size());

                }
            }
            else
            {
                messageChildAdapter.notifyDataSetChanged();
            }
        } else {
            chatRoomList.clear();
            for(int i=0;i<chatRoom_Info.size();i++) {
                if(chatRoom_Info.get(i).getChatRoomName()!=null && chatRoom_Info.get(i).getGroupId()==4)
                {
                    ChatRoom chatroom=new ChatRoom();
                    chatroom.setmRoomId(chatRoom_Info.get(i).getChatRoomId());
                    chatroom.setmDescription(chatRoom_Info.get(i).get_imageDes1Link());
                    chatroom.setmName(chatRoom_Info.get(i).getChatRoomName());
                    if (refresh_Chat_Local!=null) {
                        for (Chatroom_Info_Local local: refresh_Chat_Local) {
                            if (chatroom.getRoomId()==local.getChatRoomId()){
                                chatroom.setLastMessageTimeStamp(local.getLastMsgTimeStamp());
                                chatroom.setLastMessage(local.getLastMsgContent());
                                chatroom.setIsnew(local.isUnreadMessage());
                                break;
                            }
                        }
                    }
                    chatRoomList.add(chatroom);
                    Collections.sort(chatRoomList, new Comparator<ChatRoom>() {
                        @Override
                        public int compare(ChatRoom o1, ChatRoom o2) {
                            return (int)(o2.getLastMessageTimeStamp()-o1.getLastMessageTimeStamp());
                        }
                    });
                }
            }


            if(  refresh_Chat_Local!=null)
            {
                if(chatRoomList.size()==refresh_Chat_Local.size())
                {
                    messageChildAdapter.notifyItemRangeChanged(0,chatRoomList.size());

                }
            }
            else
            {
                messageChildAdapter.notifyDataSetChanged();
            }
        }

    }




    @Override
    public void onStop() {
        super.onStop();
            refresh_Chat_Local = null;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    synchronized public void onLastEvent(ReceivedWebSocketDataEvent event) {
//            Message msg=new Message();
//            msg.setmMsgId(event.getResult().getString("messageId"));
//            msg.setmNickname(event.getResult().getString("fromUserName"));
//            msg.setmUserImageUrl(event.getResult().getString("fromUserPic"));
//            msg.setRoomId(event.getResult().getInt("roomId"));
//            msg.setType(event.getResult().getString("contentType"));
//            msg.setContent(event.getResult().getString("content"));
//            msg.setmUserId(event.getResult().getInt("fromUserId"));
//            msg.setTimeStamp(Long.valueOf(event.getResult().getString("messageId").substring(0,13)));
//            messageAdapter.updateSending(msg);//

        try {

            if(refresh_Chat_Local!=null)
            {
                for(Chatroom_Info_Local local:refresh_Chat_Local) {

                    if(local.getChatRoomId()==event.getResult().getInt("roomId"))
                    {
                        local.setLastMsgContent(event.getResult().getString("content"));
                        local.setLastMsgTimeStamp(Long.valueOf(event.getResult().getString("messageId").substring(0,13)));
                        local.setUnreadMessage(true);
                        IMRepository.getInstance().insertChatRoomLocalInfo(local);
                        break;
                    }
                }
            }

            if(isGroup) {

                 if(groupRoomList.size()>0)
                 {
                         noPartyGroup.setVisibility(View.GONE);
                         recyclerView.setVisibility(View.VISIBLE);
                     for(int i=0;i<groupRoomList.size();i++) {
                         if(groupRoomList.get(i).getGroupId()==event.getResult().getInt("roomId"))
                         {
                             EventBus.getDefault().post(new MainEvent(0));
                         }
                     }
                 }
                 else
                 {
                     noPartyGroup.setVisibility(View.VISIBLE);
                     recyclerView.setVisibility(View.GONE);
                 }
            }

            /**
             * SOCKET打新消息的時候位置要做切換
             *
             * **/
            if(localChatRooms!=null)
            {

                if(event.getResult().getString("content").contains("action")
                   &&event.getResult().getString("content").contains("joinChatroom"))
                {
                    for(Chatroom_Info info:localChatRooms)
                    {
                        if(info.getChatRoomId()==event.getResult().getInt("roomId"))
                        {
                            info.setMembercounts(info.getMembercounts()+1);
                            IMRepository.getInstance().insertChatRoom(info);
                        }
                    }

                }
                else if(event.getResult().getString("content").contains("action")
                        &&event.getResult().getString("content").contains("leaveChatroom"))
                {
                    for(Chatroom_Info info:localChatRooms)
                    {
                        if(info.getChatRoomId()==event.getResult().getInt("roomId"))
                        {
                            info.setMembercounts(info.getMembercounts()-1);
                            IMRepository.getInstance().insertChatRoom(info);
                        }
                    }
                }

                    uploadData(localChatRooms);

            }

//           for(GroupRoom groupRoom:groupRoomList)
//           {
//               if(groupRoom.getGroupId()==event.getResult().getInt("roomId"))
//               {
//                   Collections.swap(groupRoomList,groupRoomList.indexOf(groupRoom),0);
//               }
//           }
//            for(ChatRoom chatRoom:chatRoomList)
//            {
//                if(chatRoom.getRoomId()==event.getResult().getInt("roomId"))
//                {
//                    Collections.swap(chatRoomList,chatRoomList.indexOf(chatRoom),0);
//                }
//            }

//            } else {
//                for(int i=0;i<chatRoomList.size();i++) {
//                    if(chatRoomList.get(i).getRoomId()==event.getResult().getInt("roomId")) {
//                        chatRoomList.get(i).setLastMessage(event.getResult().getString("content"));
//                        chatRoomList.get(i).setLastmessageType(event.getResult().getString("contentType"));
//                        String timeString=event.getResult().getString("messageId").substring(0,13);
//                        chatRoomList.get(i).setLastMessageTimeStamp(Long.valueOf(timeString));
//                        chatRoomList.get(i).setIsnew(true);
//                        Collections.swap(chatRoomList, i, 0);
//                    }
//                }
//

        }catch (JSONException j)
        {
            Log.e(getClass().getSimpleName(), j.getMessage(), j);
        }

    }
/**
 * {"action":"joinChatroom","userId":"154036708","nickName":"油王"}
 * */

    public void InsertDeletedHistoryMessage(int RoomID,long timestamp)
    {
        Chatroom_History chat = new Chatroom_History();
        chat.setMsgId(timestamp+"-0");
        chat.setAvatar("");
        chat.setChatRoomId(RoomID);
        chat.setChatType("system");
        try
        {
            JSONObject object=new JSONObject();
            object.put("action","deleteMessage");
            chat.setContent(object.toString());
        }catch (JSONException e)
        {
            Log.e("ERROR",e.getMessage(),e);
        }
        chat.setTimestamp(timestamp);
        IMRepository.getInstance().insertChatHistory(chat);

    }



    private class MessageChildAdapter extends RecyclerView.Adapter {
        private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();
        private View.OnClickListener itemClick;
        private View.OnLongClickListener itemLongClick;


        public MessageChildAdapter() {
            viewBinderHelper.setOpenOnlyOne(false);
        }


        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View convertView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_message_list, viewGroup, false);
            return new ViewHolder(convertView);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            ViewHolder vh = (ViewHolder) viewHolder;
            if (vh.mainLayout.getMeasuredHeight() == 0) {
                vh.mainLayout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            }


            if(deleteFlag) {
                vh.deleMessageteLayout.setVisibility(View.VISIBLE);
            }
            else
            {
                vh.deleMessageteLayout.setVisibility(View.GONE);
            }

            if (isGroup) {

                GroupRoom groupRoom = groupRoomList.get(viewHolder.getAdapterPosition());
                if(groupRoom.getGroupName().length()>13)
                {
                    vh.nameText.setText(groupRoom.getGroupName().substring(0,13)+"...");
                }
                else
                {
                    vh.nameText.setText(groupRoom.getGroupName());

                }
                Glide.with(activity)
                        .load(groupRoom.get_imageLogoLink())
                        .circleCrop()
                        .placeholder(R.drawable.loading)
                        .into(vh.iconImage);


                try {

                    String message="";
                    int roomId=-1;
                    long timeStamp;

                    message= groupRoomList.get(viewHolder.getAdapterPosition()).getLastMessage();
                    roomId = Integer.valueOf(groupRoomList.get(viewHolder.getAdapterPosition()).getGroupId());
                    timeStamp=groupRoomList.get(viewHolder.getAdapterPosition()).getLastMessageTimeStamp();
                    vh.unreadView.setVisibility(View.INVISIBLE);

                    if(groupRoom.getGroupId()==roomId && message!=null && message.length()>0)
                    {

                        if(message.toUpperCase().contains(".GIF"))
                        {
                            vh.messageText.setText("[動圖]");
                        }
                        else if(message.contains("md5Token"))
                        {
                            vh.messageText.setText("[圖片]");

                        }
                        else if(message.contains("\"action\":"))
                        {
                            try {
                                JSONObject action=new JSONObject(message);
                                if(action.getString("action").equals("leaveChatroom"))
                                {
                                    vh.messageText.setText(action.getString("nickName")+"離開聊天群");
                                }
                                else if(action.getString("action").equals("joinChatroom"))
                                {
                                    vh.messageText.setText(action.getString("nickName")+"加入聊天群");
                                }
                            } catch (JSONException e) {
                                Log.e(getClass().getSimpleName(), e.getMessage(), e);
                            }

                        }
                        else if(message.contains("{\"data\":"))
                        {
                            JSONObject jsonMsg=new JSONObject(message);

                            if(!vh.messageText.getText().toString().equals(message))
                            {
                                if(message.length()*14>vh.messageText.getWidth())
                                {
                                    vh.messageText.setText(jsonMsg.getString("data").substring(0,12)+"...");
                                }
                                else
                                {
                                    vh.messageText.setText(jsonMsg.getString("data"));

                                }
                            }
                        }

                        else
                        {
                            if(!vh.messageText.getText().toString().equals(message))
                            {
                                if(message.length()>13 && message.length()*14>vh.messageText.getWidth())
                                {
                                    vh.messageText.setText(message.substring(0,12)+"...");
                                }
                                else
                                {
                                    vh.messageText.setText(message);

                                }
                            }

                        }

                        if(vh.messageText.getText().equals("訊息已刪除")
                        ||vh.messageText.getText().equals(""))
                        {
                            vh.item.setVisibility(View.GONE);
                            vh.item.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                        }
                        else
                        {
                            vh.item.setVisibility(View.VISIBLE);
                            vh.item.setLayoutParams(vh.params);

                        }

                        if (groupRoom.isIsnew()) {
                            vh.unreadView.setVisibility(View.VISIBLE);
                        } else {
                            vh.unreadView.setVisibility(View.INVISIBLE);
                        }


                            if(getHours(timeStamp,System.currentTimeMillis())<=1)
                            {
                                vh.timeText.setText("剛剛");
                            }
                            else if(getHours(timeStamp,System.currentTimeMillis())>1
                                    && getHours(timeStamp,System.currentTimeMillis())<=24)
                            {
                                vh.timeText.setText("今天");
                            }
                            else if(getHours(timeStamp,System.currentTimeMillis())>24
                                    && getHours(timeStamp,System.currentTimeMillis())<=48)
                            {
                                vh.timeText.setText("昨天");
                            }
                            else
                            {
                                vh.timeText.setText("");
                            }

                    }

                }
                catch (Exception e)
                {
                    Log.e("ErrorGROUP","Message is Empthy!!!!!!"+e.getMessage(), e);
                }

            } else {

                /**
                 * 因應新的API改變資料讀入
                 *
                 * **/

                ChatRoom chatRoom=chatRoomList.get(viewHolder.getAdapterPosition());
                vh.nameText.setText(chatRoom.getName());

                    try {
                        String message="";
                        int roomId=-1;
                        long timeStamp;
                        message= chatRoomList.get(viewHolder.getAdapterPosition()).getLastMessage();
                        roomId = Integer.valueOf(chatRoomList.get(viewHolder.getAdapterPosition()).getRoomId());
                        timeStamp=chatRoomList.get(viewHolder.getAdapterPosition()).getLastMessageTimeStamp();
                        vh.unreadView.setVisibility(View.INVISIBLE);

                        if(chatRoom.getRoomId()==roomId)
                        {
                            if(message.toUpperCase().contains(".GIF"))
                            {
                                vh.messageText.setText("[動圖]");
                            }
                            else if(message.contains("md5Token"))
                            {
                                vh.messageText.setText("[圖片]");

                            }
                            else if(message.contains("\"action\":"))
                            {

                                    JSONObject action=new JSONObject(message);
                                    if(action.getString("action").equals("leaveChatroom"))
                                    {
                                        vh.messageText.setText(action.getString("nickName")+"離開聊天群");
                                    }
                                    else if(action.getString("action").equals("joinChatroom"))
                                    {
                                        vh.messageText.setText(action.getString("nickName")+"加入聊天群");
                                    }

                            }
                            else if(message.contains("{\"data\":"))
                            {
                                JSONObject jsonMsg=new JSONObject(message);

                                    if(!vh.messageText.getText().toString().equals(message))
                                    {
                                        if(jsonMsg.getString("data").length()>13 && message.length()*14>vh.messageText.getWidth())
                                        {
                                            vh.messageText.setText(jsonMsg.getString("data").substring(0,12)+"...");
                                        }
                                        else
                                        {
                                            vh.messageText.setText(jsonMsg.getString("data"));

                                        }
                                    }

                            }
                            else
                            {
                                if(!vh.messageText.getText().toString().equals(message))
                                {
                                    if(message.length()*14>vh.messageText.getWidth())
                                    {
                                        vh.messageText.setText(message.substring(0,12)+"...");
                                    }
                                    else
                                    {
                                        vh.messageText.setText(message);

                                    }
                                }

                            }

                            if(vh.messageText.getText().equals("訊息已刪除")
                              ||vh.messageText.getText().equals(""))
                            {
                                vh.item.setVisibility(View.GONE);
                                vh.item.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                            }
                            else
                            {
                                vh.item.setVisibility(View.VISIBLE);
                                vh.item.setLayoutParams(vh.params);

                            }

                            if (chatRoom.isIsnew()) {
                                vh.unreadView.setVisibility(View.VISIBLE);
                            } else {
                                vh.unreadView.setVisibility(View.INVISIBLE);
                            }


                                if(getHours(timeStamp,System.currentTimeMillis())<=1)
                                {
                                    vh.timeText.setText("剛剛");
                                }
                                else if(getHours(timeStamp,System.currentTimeMillis())>1
                                        && getHours(timeStamp,System.currentTimeMillis())<=24)
                                {
                                    vh.timeText.setText("今天");
                                }
                                else if(getHours(timeStamp,System.currentTimeMillis())>24
                                        && getHours(timeStamp,System.currentTimeMillis())<=48)
                                {
                                    vh.timeText.setText("昨天");
                                }
                                else
                                {
                                    vh.timeText.setText("");
                                }


                        }
                    }
                    catch (Exception e)
                {
                    Log.e("ErrorPRIVATE","Message is Empthy!!!!!!"+e.getMessage(), e);
                }

                    Glide.with(activity)
                            .load(chatRoom.getmDescription())
                            .circleCrop()
                            .into(vh.iconImage);
            }


            vh.deleMessageteLayout.setOnClickListener(itemClick);
            vh.mainLayout.setOnClickListener(itemClick);
            vh.mainLayout.setOnLongClickListener(itemLongClick);

        }


        @Override
        public int getItemCount() {
            if (isGroup) {
                return groupRoomList.size();
            } else {
                return chatRoomList.size();
            }
        }

        public void setItemClick(View.OnClickListener itemClick) {
            this.itemClick = itemClick;
        }
        public void setitemLongClick(View.OnLongClickListener itemLongClick) {
            this.itemLongClick = itemLongClick;
        }

        public void saveStates(Bundle outState) {
            viewBinderHelper.saveStates(outState);
        }

        public void restoreStates(Bundle inState) {
            viewBinderHelper.restoreStates(inState);
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            public View mainLayout;
            public View item;
            public View unreadView;
            public ImageView iconImage;
            public TextView nameText;
            public TextView timeText;
            public TextView messageText;
            public TextView bonusText;
            public ViewGroup.LayoutParams params;
            public ConstraintLayout deleMessageteLayout;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                item = itemView.findViewById(R.id.item);
                params=item.getLayoutParams();
                mainLayout = itemView.findViewById(R.id.mainLayout);
                unreadView = itemView.findViewById(R.id.unreadView);
                iconImage = itemView.findViewById(R.id.iconImage);
                nameText = itemView.findViewById(R.id.nameText);
                timeText = itemView.findViewById(R.id.timeText);
                messageText = itemView.findViewById(R.id.messageText);
                bonusText = itemView.findViewById(R.id.bonusText);
                deleMessageteLayout=itemView.findViewById(R.id.deleMessageteLayout);
                deleMessageteLayout.setTag(this);
                mainLayout.setTag(this);
            }
        }
    }
}
