package com.sex8.sinchat.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Group;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.sex8.sinchat.IMMessageApplication;
import com.sex8.sinchat.R;
import com.sex8.sinchat.entity.UserInfo;
import com.sex8.sinchat.event.ChatCommentShowEvent;
import com.sex8.sinchat.event.ChatItemLongClickEvent;
import com.sex8.sinchat.event.DismissEvent;
import com.sex8.sinchat.event.MessageItemClickEvent;
import com.sex8.sinchat.model.IMData;
import com.sex8.sinchat.model.dataBase.Entity.Chatroom_History;
import com.sex8.sinchat.model.dataBase.Entity.Chatroom_Info;
import com.sex8.sinchat.utils.DensityUtil;
import com.sex8.sinchat.utils.PhotoUtuls;
import com.sex8.sinchat.utils.SocketCmdUtils;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import static com.sex8.sinchat.IMMessageApplication.getGlobalContext;
import static com.sex8.sinchat.utils.PhotoUtuls.getBitmapFromURL;

public class ChatMessageAdapter extends RecyclerView.Adapter {
    private static final int TYPE_ME = 0;
    private static final int TYPE_OTHER = 1;
    private static final int TYPE_DATE_STAMP = 2;
    private static final int TYPE_SYSTEM = 3;

    private Context context;
    private Chatroom_Info roomInfo;
    private UserInfo userInfo;
    private List<Chatroom_History> chatList;
    private File file;
    public ChatMessageAdapter(Context context, Chatroom_Info roomInfo, UserInfo userInfo, List<Chatroom_History> chatList){
        this.context = context;
        this.roomInfo = roomInfo;
        this.userInfo = userInfo;
        this.chatList = chatList;
    }

    public void removeMessage(Chatroom_History chat){
        int index = chatList.indexOf(chat);
        if(chat.getSendingStatus()==3){
            chatList.remove(chat);
        }
        notifyItemRemoved(index);
    }

    public void sendMessage(String content) {
        JSONObject contentWithTimeStamp = new JSONObject();
        try {
            contentWithTimeStamp.put("data",content);
            contentWithTimeStamp.put("submitTime", new Date().getTime());//用此方式得以識別返回對話的身分
        } catch (JSONException e) {
            Log.e(getClass().getSimpleName(), e.getMessage(), e);
        }
        Chatroom_History chat=new Chatroom_History();
        chat.setContent(contentWithTimeStamp.toString());
        chat.setUid(userInfo.getUid());
        chat.setUsername(userInfo.getNickName());
        chat.setAvatar(userInfo.getImageUrl());
        chat.setChatRoomId(roomInfo.getChatRoomId());
        chat.setIsAdmin(userInfo.getIsAdmin());
        chat.setTimestamp(new Date().getTime());
        chat.setChatType("text");
        chat.setSendingStatus(1);
        chatList.add(chat);
        IMData.getInstance().sendMessage(chat,roomInfo);
        notifyItemInserted(chatList.size() - 1);
    }


    @Override
    public int getItemViewType(int position) {

        if(chatList.get(position).getChatType().equals(SocketCmdUtils.TYPE_DATE_STAMP))
            return TYPE_DATE_STAMP;
        else if(chatList.get(position).getChatType().equals(SocketCmdUtils.TYPE_SYSTEM) )
            return TYPE_SYSTEM;
        else
            return chatList.get(position).getUid() == userInfo.getUid() ? TYPE_ME : TYPE_OTHER;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View convertView = LayoutInflater.from(viewGroup.getContext()).inflate(
                viewType == TYPE_DATE_STAMP ? R.layout.item_message_date :
                        viewType == TYPE_SYSTEM ? R.layout.item_message_date :
                        viewType == TYPE_ME ? R.layout.item_message_me : R.layout.item_message_other, viewGroup, false);
        return new ViewHolder(convertView);
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
       final ViewHolder vh = (ViewHolder) viewHolder;
       Chatroom_History chat = chatList.get(viewHolder.getAdapterPosition());

        if(viewHolder.getItemViewType() == TYPE_DATE_STAMP){
            vh.contentText.setText(chat.getContent());
        } else if(viewHolder.getItemViewType() == TYPE_SYSTEM) {

            if(chat.getMsgId().contains("000000000000")) {
                vh.contentText.setText(getGlobalContext().getResources().getString(R.string.no_history));
                return;
            }

            try {
                Gson gson = new Gson();
                String json = gson.toJson(chat);
                Log.d("Systemmessage chat",json);//打印所有參數

                Log.d("Systemmessage",chat.getMsgId());

                JSONObject obj = new JSONObject(chat.getContent());
                Log.d("Systemmessage",obj.getString("action"));


                if(obj.getString("action").contains("joinChatroom")) {
                    if(roomInfo.getGroupId()==4) {//私聊就隱藏加入訊息
                        viewHolder.itemView.setVisibility(View.GONE);
                        viewHolder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                    } else {
                        vh.contentText.setText(obj.getString("nickName")+"加入派對群");
                    }
                } else if(obj.getString("action").contains("leaveChatroom")) {
                    if(roomInfo.getGroupId()==4) {
                        vh.contentText.setText(obj.getString("nickName")+"已解除您的好友關係");
                    } else {
                        vh.contentText.setText(obj.getString("nickName")+"離開派對群");
                    }

                    if(obj.getString("nickName").equals("admin")) {
                        EventBus.getDefault().post(new DismissEvent());
                    }
                } else if(obj.getString("action").contains("ban2Hours")) {
                    vh.contentText.setText(obj.getString("nickName")+"受到禁言");
                } else if (obj.getString("action").contains("deleteMessage")){
                    vh.contentText.setText("資料已刪除");
                }
            } catch (JSONException e) {
                Log.e(getClass().getSimpleName(), e.getMessage(), e);

            }

        } else if(viewHolder.getItemViewType() == TYPE_OTHER) {
            vh.contentRL.setOnLongClickListener(vh.itemClick);

            if(roomInfo.getGroupId()==4) {
                vh.username.setText(roomInfo.getChatRoomName());
            } else {
                vh.username.setText(chat.getUsername());
            }

            if(chat.getTimestamp()!=0) {
                SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                format.setTimeZone(TimeZone.getDefault());
                String strDate = format.format(new Date(chat.getTimestamp()));
//                vh.timeText.setText(strDate + " " + chat.getTimestamp());
                vh.timeText.setText(strDate);
            }

            try {
                Glide.with(vh.iconImage.getContext())
                        .load(chat.getAvatar())
                        .centerCrop()
                        .placeholder(R.drawable.loading)
                        .into(vh.iconImage);
            }catch (Exception e) {
                Log.e(getClass().getSimpleName(), e.getMessage(), e);
                vh.iconImage.setImageResource(R.drawable.hameame);
            }

            vh.iconImage.setOnClickListener(vh.itemClick);

        } else {
            if(chat.getTimestamp()!=0) {
                SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                format.setTimeZone(TimeZone.getDefault());
                String strDate = format.format(new Date(chat.getTimestamp()));
//                vh.timeText.setText(strDate + " " + chat.getTimestamp());
                vh.timeText.setText(strDate);
            }
            switch (chat.getSendingStatus()) {
                case 0:
                    vh.sendErrorImage.setVisibility(View.GONE);
                    vh.sendCloseImage.setVisibility(View.GONE);
                    vh.sendLoadingImage.clearAnimation();
                    vh.sendLoadingImage.setVisibility(View.GONE);
                    break;

                case 1:
                    if (chat.getChatType().equals("image")) {
                        vh.sendErrorImage.setVisibility(View.GONE);
                        vh.sendCloseImage.setVisibility(View.VISIBLE);
                        vh.sendLoadingImage.setVisibility(View.VISIBLE);
                        vh.sendLoadingImage.startAnimation(AnimationUtils.loadAnimation(vh.sendLoadingImage.getContext(), R.anim.chat_sending));
                    }else{
                        vh.sendErrorImage.setVisibility(View.GONE);
                        vh.sendCloseImage.setVisibility(View.GONE);
                        vh.sendLoadingImage.clearAnimation();
                        vh.sendLoadingImage.setVisibility(View.GONE);
                    }
                    break;
                case 2:
                    vh.sendErrorImage.setVisibility(View.VISIBLE);
                    vh.sendCloseImage.setVisibility(View.GONE);
                    vh.sendLoadingImage.clearAnimation();
                    vh.sendLoadingImage.setVisibility(View.GONE);
                    break;
                case 3:
                    vh.sendErrorImage.setVisibility(View.GONE);
                    vh.sendCloseImage.setVisibility(View.GONE);
                    vh.sendLoadingImage.clearAnimation();
                    vh.sendLoadingImage.setVisibility(View.GONE);
                    break;
            }
        }

        if (chat.getChatType().equals("image") || chat.getContent().contains("md5Token")) {
            vh.contentText.setVisibility(View.GONE);
            vh.contentText.setText("");
            vh.imageView.setVisibility(View.VISIBLE);
            if (!chat.getContent().equals(vh.imageContent)) {
                vh.imageView.layout(0, 0, 0, 0);
                ViewGroup.LayoutParams params = vh.imageView.getLayoutParams();
                params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                Log.d("params.height",""+ViewGroup.LayoutParams.WRAP_CONTENT);
                Log.d("params.width",""+ViewGroup.LayoutParams.WRAP_CONTENT);
                //圖片切圓角

                String fileName;
                Log.d("Content_local",chat.getContent());
                File issendfile=new File(chat.getContent());

                try {

                    final JSONObject jsonObject = new JSONObject(chat.getContent());
                    if (jsonObject!=null && jsonObject.optJSONObject("data")!=null){
                        Log.d("IMGFilePresant",jsonObject.optJSONObject("data").optString("Local_filename"));
                    }else{
                        Log.e(getClass().getSimpleName(), "IMGFilePresant data = null");
                        return;
                    }
                    if(chat.getUid()==userInfo.getUid()) {
                        fileName=jsonObject.getJSONObject("data").getString("Local_filename");

                        file = new File(fileName);
//                                Log.d("IMGFilePresant",file.getAbsolutePath());
                    } else {
                        Log.d("ContentImage_net",chat.getContent());

                        fileName=jsonObject.getJSONObject("data").getString("md5Token");
                        file = new File(Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_PICTURES),fileName);
                    }

                    Log.d("Filename_exist", "fileName:" + file.getAbsolutePath());



                    if(chat.getContent().toLowerCase().contains(".gif")) {
                        RequestOptions requestOptions = new RequestOptions();
                        requestOptions.override(DensityUtil.dip2px(200), DensityUtil.dip2px(200));
//                        requestOptions.transform(new RoundedCorners(DensityUtil.dip2px(15)));
                        if (file.exists()) {
                            Log.d("Filename_exist", chat.getContent());
                            Glide.with(IMMessageApplication.getGlobalContext())
                                    .asGif()
                                    .load(file)
                                    .placeholder(R.drawable.loading)
                                    .apply(requestOptions)
                                    .error(android.R.drawable.ic_menu_report_image).into(vh.imageView);
                        } else {
                            Log.d(getClass().getSimpleName(),"showImage: "+ IMData.getInstance().getWebSocketInfo().getFileHosts().get(0) + jsonObject.getJSONObject("data").getString("sourcePath"));
                            Glide.with(IMMessageApplication.getGlobalContext())
                                    .asGif()
                                    .load(IMData.getInstance().getWebSocketInfo().getFileHosts().get(0) + jsonObject.getJSONObject("data").getString("sourcePath"))
                                    .placeholder(R.drawable.loading)
                                    .apply(requestOptions)
                                    .error(android.R.drawable.ic_menu_report_image).into(vh.imageView);
                        }
                    }else{
                        RequestOptions requestOptions = new RequestOptions();
                        requestOptions.override(DensityUtil.dip2px(200), DensityUtil.dip2px(200));
                        requestOptions.transform(new RoundedCorners(DensityUtil.dip2px(15)));
                        if (file.exists()) {
                            Log.d("Filename_exist", chat.getContent());
                            Glide.with(IMMessageApplication.getGlobalContext())
                                    .load(file)
                                    .placeholder(R.drawable.loading)
                                    .apply(requestOptions)
                                    .error(android.R.drawable.ic_menu_report_image).into(vh.imageView);
                        } else {
                            Glide.with(IMMessageApplication.getGlobalContext())
                                    .load(Uri.parse(IMData.getInstance().getWebSocketInfo().getFileHosts().get(0) + jsonObject.getJSONObject("data").getString("sourcePath")))
                                    .placeholder(R.drawable.loading)
                                    .apply(requestOptions)
                                    .error(android.R.drawable.ic_menu_report_image).into(vh.imageView);
                        }
                    }


                }catch (JSONException e){
                    Log.d(getClass().getSimpleName(), e.getMessage(), e);
                }
            }
                vh.imageContent = chat.getContent();
                vh.contentRL.setOnClickListener(vh.itemClick);
        } else{//處理文字
            if(viewHolder.getItemViewType() != TYPE_SYSTEM) {
                String chatText = chat.getContent();
                if(chat.getUid()!=1/*非機器人*/) {//因為機器人的訊息沒有做JSON包裝,是純文字, 要跳過該段處理
                    try {
                        chatText = new JSONObject(chatText).optString("data");
                    } catch (JSONException e) {
                        Log.e(getClass().getSimpleName(), e.getMessage(), e);
                    }
                }
                vh.contentText.setVisibility(View.VISIBLE);
                if (chatText.contains("#@#")) {
                    vh.contentText.setText(chatText.replace("#@#", ""));
                } else {
                    vh.contentText.setText(chatText.trim());
                }
            }

            if(vh.imageView!=null) {
                vh.imageView.setVisibility(View.GONE);
                vh.contentRL.setOnClickListener(null);
            }

        }
    }



    @Override
    public int getItemCount() {
        return chatList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView contentText;
        ImageView iconImage;
        TextView timeText;
        TextView username;
        ImageView imageView;
        String imageContent;
        ImageView sendCloseImage;
        ImageView sendLoadingImage;
        ImageView sendErrorImage;
        View contentRL;
        MessageItemClick itemClick;
        Group reportGroup;
        ImageView reportImage;
        TextView reportText;
        RecyclerView commentRecyclerView;
        MessageCommentAdapter messageCommentAdapter;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            contentText = itemView.findViewById(R.id.contentText);
            iconImage = itemView.findViewById(R.id.iconImage);
            timeText = itemView.findViewById(R.id.timeText);
            username=itemView.findViewById(R.id.username);
            imageView = itemView.findViewById(R.id.imageView);
            sendCloseImage = itemView.findViewById(R.id.sendCloseImage);
            sendLoadingImage = itemView.findViewById(R.id.sendLoadingImage);
            sendErrorImage = itemView.findViewById(R.id.sendErrorImage);
            contentRL = itemView.findViewById(R.id.contentRL);
            itemClick = new MessageItemClick(this);
            reportGroup = itemView.findViewById(R.id.reportGroup);
            reportImage = itemView.findViewById(R.id.reportImage);
            reportText = itemView.findViewById(R.id.reportText);
            commentRecyclerView = itemView.findViewById(R.id.commentRecyclerView);
        }

        public void updateComment(SparseIntArray commentArray){
            if(commentArray != null && commentArray.size() > 0){
                messageCommentAdapter = new MessageCommentAdapter(context);
            }

            if(messageCommentAdapter != null){
                if(commentRecyclerView.getAdapter() == null) {
                    commentRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                }
                commentRecyclerView.setAdapter(messageCommentAdapter);
                messageCommentAdapter.updateComment(commentArray);
            }
        }
    }


    class MessageItemClick implements View.OnClickListener, View.OnLongClickListener {
        private ViewHolder viewHolder;
        public MessageItemClick(ViewHolder viewHolder){
            this.viewHolder = viewHolder;
        }

        @Override
        public void onClick(View v) {
            int position = viewHolder.getAdapterPosition();
            Chatroom_History chat = chatList.get(position);
            switch (v.getId()){
                case R.id.contentRL:
                        int[] locationOnScreen = new int[2];
                        viewHolder.imageView.getLocationOnScreen(locationOnScreen);
                        try {
                           JSONObject jsonObject = new JSONObject(chatList.get(position).getContent());
                           if(jsonObject.getJSONObject("data").getString("Local_filename").contains(".gif")) {
                               File tmpfile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), jsonObject.getJSONObject("data").getString("Local_filename"));
                               String tmp="";
                              if(tmpfile.exists()) {
                                  tmp=jsonObject.getJSONObject("data").getString("Local_filename");
                              }
                              else {
                                  tmp=IMData.getInstance().getWebSocketInfo().getFileHosts().get(0)+jsonObject.getJSONObject("data").getString("sourcePath");
                              }
                               EventBus.getDefault().post(new MessageItemClickEvent(tmp));
                           }
                           else {
                               String fileName=jsonObject.getJSONObject("data").getString("sourcePath");
                               File tmpfile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), fileName.substring(0,fileName.indexOf("?")));
                               String tmp="";
                               if(tmpfile.exists()) {
                                   tmp=tmpfile.getAbsolutePath();
                               }
                               else {
                                   tmp=IMData.getInstance().getWebSocketInfo().getFileHosts().get(0)+jsonObject.getJSONObject("data").getString("sourcePath");
                               }


                               EventBus.getDefault().post(new MessageItemClickEvent(tmp));
                           }

                       }catch (JSONException e) {
                           Log.e(getClass().getSimpleName(), e.getMessage(), e);
                           EventBus.getDefault().post(new MessageItemClickEvent(chatList.get(position).getContent()));
                       }


                    break;


                case R.id.iconImage:
                    EventBus.getDefault().post(new ChatItemLongClickEvent(chatList.get(viewHolder.getAdapterPosition()), v));
                    break;

            }
        }

        @Override
        public boolean onLongClick(View v) {
            EventBus.getDefault().post(new ChatItemLongClickEvent(chatList.get(viewHolder.getAdapterPosition()), v));
            return true;
        }
    }
}
