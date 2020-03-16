package com.sex8.sinchat.view;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
 import android.util.SparseArray;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.sex8.sinchat.IMMessageApplication;
import com.sex8.sinchat.R;
import com.sex8.sinchat.event.ChatDeleteShowEvent;
import com.sex8.sinchat.event.ChatReportShowEvent;
import com.sex8.sinchat.model.IMData;
import com.sex8.sinchat.model.dataBase.Entity.Chatroom_History;
import com.sex8.sinchat.model.dataBase.Entity.Chatroom_Info;
import com.sex8.sinchat.model.dataBase.Entity.Member;
import com.sex8.sinchat.tools.OnClickLimitListener;

import org.greenrobot.eventbus.EventBus;

import static android.content.Context.CLIPBOARD_SERVICE;

public class ChatPopupWindow extends PopupWindow {
    private Activity context;
    private Chatroom_Info roomInfo;
    private LinearLayout linearLayout;
    private int textHeight;
    private SparseArray<TextView> textViewArray;
    private Chatroom_History chat;
    private ClipboardManager clipboard;

    public ChatPopupWindow(final Context context, Chatroom_Info roomInfo) {
        super(context);
        this.context = (Activity) context;
        this.textHeight = context.getResources().getDimensionPixelOffset(R.dimen.popup_chat_more_text_height);
        this.clipboard = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
        this.roomInfo=roomInfo;
        initPopupWindow();
        initMenu();
    }

    private void initPopupWindow(){
        linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        linearLayout.setBackgroundResource(R.drawable.bg_more_popup);
        setContentView(linearLayout);
        setFocusable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    private void initMenu(){
        PopupMenu popupMenu  = new PopupMenu(context, null);
        popupMenu.inflate(R.menu.chat_popup_menu);
        Menu menu = popupMenu.getMenu();
        textViewArray = new SparseArray<>();
        for(int i=0;i<menu.size();i++){
            MenuItem menuItem = menu.getItem(i);
            TextView textView = new TextView(new ContextThemeWrapper(context, R.style.popup_chat_item_text));
            textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, textHeight));
            textView.setText(menuItem.getTitle());
            textView.setId(menuItem.getItemId());
            textView.setOnClickListener(btnClick);
            textViewArray.put(menuItem.getItemId(), textView);
        }
    }

    private void initItem(){
        linearLayout.removeAllViews();
        SparseArray<TextView> tmpArray = textViewArray.clone();
//        if(chat.getSendingStatus()==2){
//            tmpArray.remove(R.id.resend);
//        }

        if(chat.getUid() == IMData.getInstance().getUserInfo().getUid()){
//            tmpArray.remove(R.id.report);
//        }else{
//            tmpArray.remove(R.id.delete);
        }

        for(int i = 0; i < tmpArray.size(); i++) {
            int key = tmpArray.keyAt(i);
            TextView textView = tmpArray.get(key);
            if(i == 0){
                if(tmpArray.size() > 1){
                    textView.setBackgroundResource(R.drawable.chat_popup_item_left_ripple);
                }else{
                    textView.setBackgroundResource(R.drawable.chat_popup_item_single_ripple);
                }
            }else if(i == tmpArray.size() - 1){
                textView.setBackgroundResource(R.drawable.chat_popup_item_right_ripple);
            }else{
                textView.setBackgroundResource(R.drawable.chat_popup_item_normal_ripple);
            }
            linearLayout.addView(textView);
        }
        linearLayout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
    }

    public void show(View anchor, @NonNull Chatroom_History chat){
        this.chat = chat;
        initItem();
        if(linearLayout.getChildCount() > 0) {
            int[] location = new int[2];
            anchor.getLocationOnScreen(location);
            showAtLocation(anchor, 0, (location[0] + anchor.getWidth() / 2) - linearLayout.getMeasuredWidth() / 2, location[1] - linearLayout.getMeasuredHeight());
        }
    }

    private OnClickLimitListener btnClick = new OnClickLimitListener() {
        @Override
        public void onClickLimit(View v) {
            dismiss();
            switch (v.getId()){
//                case R.id.copy:
//                    IMMessageApplication.showToast(context, R.string.replicated, Toast.LENGTH_SHORT);
//                    clipboard.setPrimaryClip(ClipData.newPlainText(null, message.getContent()));
//                    break;
//                case R.id.resend:
//                    //todo
                    //有空再做失敗重傳
//                    ((SendingMessage) message).resend();
//                    break;
//                case R.id.report:
//                    EventBus.getDefault().post(new ChatReportShowEvent(chat));
////                    int chatroomID,String MsgId,String reportType,String ReportCotent,
////                        String ReportedUid,String reportText
//                    IMData.getInstance().Report(roomInfo.getChatRoomId(),chat.getMsgId(),"1",chat.getContent()
//                    ,String.valueOf(chat.getUid()),"testt");
//                    break;
//                case R.id.delete:
//                    EventBus.getDefault().post(new ChatDeleteShowEvent(chat));
//                    break;

                case R.id.set_friend:
                    IMMessageApplication.showToast(context, R.string.set_friend, Toast.LENGTH_SHORT);
                    Member member = new Member();
                    member.setUid(chat.getUid());
                    member.setAvatar(chat.getAvatar());
                    member.setUsername(chat.getUsername());
                    IMData.getInstance().addFollows(member);

//                    IMData.getInstance().addFollows(message.getUserId(),"popwindows");
//                    ContentValues values = new ContentValues();
//                    values.put("_username", message.getUserName());
//                    values.put("_uid", message.getUserId());
//                    values.put("_avatar", message.getUserImageUrl());
//                    SQLiteDBUtils.getInstance(context).UpdateFollowList(values);
                    break;
            }
        }
    };



}
