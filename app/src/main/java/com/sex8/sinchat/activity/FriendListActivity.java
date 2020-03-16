package com.sex8.sinchat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.sex8.sinchat.IMMessageApplication;
import com.sex8.sinchat.R;
import com.sex8.sinchat.event.AddFollowEvent;
import com.sex8.sinchat.event.CreateEvent;
import com.sex8.sinchat.event.DeleteEvent;
import com.sex8.sinchat.event.DeleteFollowEvent;
import com.sex8.sinchat.model.IMData;
import com.sex8.sinchat.model.dataBase.Entity.Chatroom_Info;
import com.sex8.sinchat.model.dataBase.Entity.Chatroom_Info_Local;
import com.sex8.sinchat.model.dataBase.Entity.Member;
import com.sex8.sinchat.model.dataBase.Entity.Myroom_List;
import com.sex8.sinchat.model.dataBase.IMRepository;
import com.sex8.sinchat.model.server.ff_list.CreateResponse;
import com.sex8.sinchat.tools.OnClickLimitListener;
import com.sex8.sinchat.viewmodel.ChatRoomInfoViewModel;
import com.sex8.sinchat.viewmodel.SinchatMemberViewModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import in.myinnos.alphabetsindexfastscrollrecycler.IndexFastScrollRecyclerView;
import retrofit2.http.POST;

public class FriendListActivity extends BaseActivity {
    private IndexFastScrollRecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerViewAdapter recyclerViewAdapter;
    private TextView titleText;
    private List<Member> memberList=new ArrayList<>();
    private List<Chatroom_Info> chatRoomList=new ArrayList<>();
    private SinchatMemberViewModel viewModel;
    private ChatRoomInfoViewModel chatviewModel;
    private List<Member> friendData;
    private List<Chatroom_Info_Local> privateRoomInfoLocal;
    private boolean isRoomCreating=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);

        findViews();
        init();
        setListeners();
    }

    @Override
    public void onBackPressed() {
        backMain();
    }

    @Override
    public void findViews() {
        titleText = findViewById(R.id.titleText);
        recyclerView = findViewById(R.id.fast_scroller_recycler);
    }

    @Override
    public void init() {
        switch (getIntent().getIntExtra("fromId", 0)){
            case R.id.friendText:
                titleText.setText(String.format(Locale.getDefault(), "%s%s", getString(R.string.friend), getString(R.string.list)));
                IMData.getInstance().updateFriendList();
                break;
            case R.id.attentionText:
                titleText.setText(String.format(Locale.getDefault(), "%s%s", getString(R.string.attention), getString(R.string.list)));
                IMData.getInstance().updateFollows();
                break;
            case R.id.fanText:
                titleText.setText(String.format(Locale.getDefault(), "%s%s", getString(R.string.fan), getString(R.string.list)));
                IMData.getInstance().updateFanList();
                break;
            case R.id.groupText:
                titleText.setText(String.format(Locale.getDefault(), "%s%s", getString(R.string.groups), getString(R.string.list)));
                IMData.getInstance().updateGroupList(0);
                break;
        }

        recyclerView.setIndexTextSize(14);
        recyclerView.setIndexBarTextColor(android.R.color.black);
        recyclerView.setIndexBarTransparentValue(0f);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewAdapter = new RecyclerViewAdapter();
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(recyclerViewAdapter);

        viewModel = new ViewModelProvider(this).get(SinchatMemberViewModel.class);
        chatviewModel = new ViewModelProvider(this).get(ChatRoomInfoViewModel.class);



        switch (getIntent().getIntExtra("fromId", 0))
        {
            case R.id.friendText:
                viewModel.getFriendData().observe(this, new Observer<List<Member>>() {
                    @Override
                    public void onChanged(List<Member> member) {
                        memberList=member;
                        recyclerViewAdapter.notifyDataSetChanged();
                    }
                });
                break;
            case R.id.attentionText:
                viewModel.getFollowData().observe(this, new Observer<List<Member>>() {
                    @Override
                    public void onChanged(List<Member> member) {
                        memberList=member;
                        recyclerViewAdapter.notifyDataSetChanged();
                    }
                });
                break;
            case R.id.fanText:
                viewModel.getFanData().observe(this, new Observer<List<Member>>() {
                    @Override
                    public void onChanged(List<Member> member) {
                        memberList=member;
                        viewModel.getFriendData().observe(FriendListActivity.this, new Observer<List<Member>>() {
                            @Override
                            public void onChanged(List<Member> member) {
                                friendData = member;
                                recyclerViewAdapter.notifyDataSetChanged();
                            }
                        });

                    }
                });

                break;


            case R.id.groupText:

                chatviewModel.getMyGroupList().observe(this,chatroom_infos -> {

                    this.chatRoomList=chatroom_infos;
                    recyclerViewAdapter.notifyDataSetChanged();
                });
                break;
        }



    }



    @Override
    public void setListeners() {
        findViewById(R.id.back).setOnClickListener(new OnClickLimitListener() {
            @Override
            public void onClickLimit(View v) {
                switch (v.getId()){
                    case R.id.back:
                        backMain();
                        break;
                }
            }
        });


    }

    private void backMain(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        finish();
    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    synchronized public void onDeleteEvent(DeleteEvent event) {

        if(event.getStatus()==0)
        {
            if(event.getMessage().equals("addResponse"))
            {
                IMData.getInstance().updateFanList();
                IMData.getInstance().updateFriendList();
                finish();
                startActivity(getIntent());
            }
            else if(event.getMessage().equals("chatResponse"))
            {
                IMData.getInstance().updateFanList();
                IMData.getInstance().updateFriendList();
                IMData.getInstance().updateFollows();
            }
            else
            {

                IMData.getInstance().updateFriendList();
                finish();
                startActivity(getIntent());
            }

        }
        else
        {
            dismiss_Progress_Dialog();
            showToast(getString(R.string.fail_connect));
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    synchronized public void onDeleteFollowComplete(DeleteFollowEvent event) {
        dismiss_Progress_Dialog();
        if(event.getStatus()==0) {

          chatviewModel.getMyPartyList().observe(this, new Observer<List<Chatroom_Info>>() {
              @Override
              public void onChanged(List<Chatroom_Info> chatroom_infos) {

                  for(Chatroom_Info info:chatroom_infos)
                  {
                      if(info.getChatRoomName().equals(event.getMember().getUsername()))
                      {
                          IMRepository.getInstance().deleteChatRoom(info);
                      }
                  }
              }
          });
            viewModel.deleteFollow(event.getMember());//取消關注
            viewModel.deleteFriend(event.getMember());//取消好友

        } else {
            showToast(getString(R.string.fail_connect));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    synchronized public void onAddFollowComplete(AddFollowEvent event) {
        dismiss_Progress_Dialog();
        if(event.getStatus()==0) {
            viewModel.insertFollow(event.getMember());//增加關注
            viewModel.insertFriend(event.getMember());//增加好友
        } else {
            showToast(getString(R.string.fail_connect));
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    synchronized public void onCreateRoom(CreateEvent event) {

        if(event.getStatus()==0) {
//            Log.d("eventData",""+event.getData().getChatroomId());
//            Log.d("eventData",""+event.getAvatar());
//            Log.d("eventData",""+event.getUserName());
            /**1.從event的ROODID裡面取得DB:sinchat_member的資料
             */
            boolean hasRoom=true;

            for(Chatroom_Info_Local local:privateRoomInfoLocal)
            {
                if(local.getChatRoomId()==event.getData().getChatroomId())
                {
                    hasRoom=false;
                    break;
                }
            }

            /** 2.在DB:ChatRoom_Info_local裡面加入私聊房號以及資料**/
            if(hasRoom)
            {
                Chatroom_Info info=new Chatroom_Info();
                isRoomCreating = true;
                viewModel.getMemberSearchDataByName(event.getUserName()).observe(this, new Observer<Member>() {
                    @Override
                    public void onChanged(Member member) {
                        /**
                         * 3-1.直接進房間
                         */
                        if(isRoomCreating) {
                            isRoomCreating = false;
                            viewModel.getMemberSearchDataByName(event.getUserName()).removeObserver(this::onChanged);
                            member.setPrivateroom(event.getData().getChatroomId());
                            IMRepository.getInstance().updateMember(member);
                            Log.d("chatroomInfoLocals__", "" + event.getData().getChatroomId());

                            info.setChatRoomId(event.getData().getChatroomId());
                            info.setChatRoomName(member.getUsername());
                            info.setGroupId(4);
                            info.set_imageDes1Link(member.getAvatar());
                            info.setMemberLimit(2);
                            info.setUids(String.valueOf(member.getUid()));
                            IMRepository.getInstance().insertChatRoom(info);

                            //新增chatRoom 到 myRoom
                            Myroom_List myroom_list = new Myroom_List();
                            myroom_list.setChatroomId(info.getChatRoomId());
                            IMRepository.getInstance().insertMy_Room_List(myroom_list);

                            //新增私聊的ChatRoomLocal
                            Chatroom_Info_Local local = new Chatroom_Info_Local();
                            local.setChatRoomId(info.getChatRoomId());
                            local.setNeedAPISync(true);
                            IMRepository.getInstance().insertChatRoomLocalInfo(local);

                            new Handler().postDelayed((Runnable) () -> {
                                Intent intent = new Intent(FriendListActivity.this, ChatMessageActivity.class);
                                Bundle mBundle = new Bundle();
                                mBundle.putInt("mRoomId", local.getChatRoomId());

                                intent.putExtras(mBundle);
                                startActivity(intent);
                            }, 1000);
                        }

                    }
                });
            }
            else
            {
                /**
                 * 3-2.直接進房間
                 */
                Intent intent = new Intent(FriendListActivity.this, ChatMessageActivity.class);
                Bundle mBundle=new Bundle();
                mBundle.putInt("mRoomId",event.getData().getChatroomId());

                intent.putExtras(mBundle);
                startActivity(intent);
            }




        }
        else
        {
            dismiss_Progress_Dialog();
            showToast(getString(R.string.fail_connect));
        }

    }




    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements SectionIndexer {

        private ArrayList<Integer> sectionPositions;
        private View.OnClickListener itemClick;
        public RecyclerViewAdapter(){


            ArrayList<String> dataArray=new ArrayList<>();

            for(int i=0;i<memberList.size();i++)
            {
                dataArray.add(memberList.get(i).getUsername());
            }


            Collections.sort(dataArray, new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    String o1PinYin = IMMessageApplication.getFirstPingYin(o1).toUpperCase();
                    String o2PinYin = IMMessageApplication.getFirstPingYin(o2).toUpperCase();
                    int o1_ascii = o1PinYin.charAt(0);
                    int o2_ascii = o2PinYin.charAt(0);
                    //排序: 數字 > 英文 > 中文
                    if(o1_ascii < 65 && o2_ascii < 65)
                        return o1_ascii < o2_ascii ? -1 : 1;
                    else if(o1_ascii < 65)
                        return 1;
                    else if(o2_ascii < 65)
                        return -1;
                    else
                        return o1PinYin.compareTo(o2PinYin);
                }
            });
        }

        public void setItemClick(View.OnClickListener itemClick) {
            this.itemClick = itemClick;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View convertView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_ff_list, viewGroup, false);
            return new ViewHolder(convertView);
        }

        @Override
        public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, final int i) {
            ViewHolder vh = (ViewHolder) viewHolder;
            vh.itemView.setOnClickListener(itemClick);

          //  QueryPartyRoomList
            if(getIntent().getIntExtra("fromId", 0)==R.id.groupText) {

                Glide.with(FriendListActivity.this)
                        .load(IMData.getInstance().getWebSocketInfo().getFileHosts().get(0)+
                                chatRoomList.get(viewHolder.getAdapterPosition()).get_imageDes1Link())
                        .circleCrop()
                        .into(vh.iconImage);
            }else {
                Glide.with(FriendListActivity.this)
                        .load(memberList.get(viewHolder.getAdapterPosition()).getAvatar())
                        .circleCrop()
                        .into(vh.iconImage);
            }

            vh.deleteLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                 //   Log.d("delete",DataList.get(i).get("_uid"));

                    switch (getIntent().getIntExtra("fromId", 0)){

                        case R.id.friendText:
                        case R.id.attentionText:
                            show_Progress_Dialog(null, getString(R.string.dialog_refreshing));
                            IMData.getInstance().deleteFollows(memberList.get(viewHolder.getAdapterPosition()));

                            break;
                        case R.id.fanText:

                            break;


                        case R.id.groupText:

                            break;


                    }

                }
            });


            switch (getIntent().getIntExtra("fromId", 0)){
                case R.id.friendText:
                    vh.feature_btn.setText(getResources().getString(R.string.send_message));
                    vh.nameText.setText(memberList.get(viewHolder.getAdapterPosition()).getUsername());

                    break;

                case R.id.attentionText:
                    vh.feature_btn.setVisibility(View.GONE);
                    vh.nameText.setText(memberList.get(viewHolder.getAdapterPosition()).getUsername());
                    break;

                case R.id.fanText:
                    vh.nameText.setText(memberList.get(viewHolder.getAdapterPosition()).getUsername());
                    vh.item.setLockDrag(true);
                    vh.feature_btn.setText(getResources().getString(R.string.attention));
                    vh.feature_btn.setEnabled(true);
                    vh.feature_btn.setBackgroundResource(R.drawable.round_bg_primecolor);
                    if (friendData!=null&& friendData.size()>0){
                        for (Member member : friendData){
                            if (member.getUid() == memberList.get(viewHolder.getAdapterPosition()).getUid()){
                                vh.feature_btn.setText(getResources().getString(R.string.alreadyfriend));
                                vh.feature_btn.setEnabled(false);
                                vh.feature_btn.setBackgroundResource(R.drawable.btn_negative_ripple);
                            }
                        }
                    }
                    break;

                case R.id.groupText:
                    vh.nameText.setText(chatRoomList.get(viewHolder.getAdapterPosition()).getChatRoomName());
                    vh.feature_btn.setVisibility(View.GONE);
                    break;
            }

            vh.feature_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (getIntent().getIntExtra("fromId", 0)){
                        case R.id.friendText:

                            IMRepository.getInstance().getAllRoomLocalInfos().observe(FriendListActivity.this, new Observer<List<Chatroom_Info_Local>>() {
                                @Override
                                public void onChanged(List<Chatroom_Info_Local> chatroom_info_locals) {
                                    IMRepository.getInstance().getAllRoomLocalInfos().removeObserver(this);
                                    privateRoomInfoLocal=chatroom_info_locals;
                                }
                            });

                            show_Progress_Dialog(null, getString(R.string.dialog_refreshing));
                            IMData.getInstance().createRoom(memberList.get(viewHolder.getAdapterPosition()));
                            break;

                        case R.id.attentionText:
                            break;

                        case R.id.fanText:
                            show_Progress_Dialog(null, getString(R.string.dialog_refreshing));
                            IMData.getInstance().addFollows(memberList.get(viewHolder.getAdapterPosition()));


                            break;

                        case R.id.groupText:

                            break;

                    }
                }
            });

        }



        @Override
        public int getItemCount() {
            if(getIntent().getIntExtra("fromId", 0)==R.id.groupText) {
                return chatRoomList.size();
            }else
            {
                return memberList.size();

            }
        }

        @Override
        public Object[] getSections() {
            List<String> sections = new ArrayList<>(26);
            sectionPositions = new ArrayList<>(27);
            for (int i = 0, size = memberList.size(); i < size; i++) {
                String section = String.valueOf(IMMessageApplication.getFirstPingYin(memberList.get(i).getUsername()).toUpperCase().charAt(0));
                int ascii = section.charAt(0);
                if(ascii < 65)
                    section = "#";

                if (!sections.contains(section)) {
                    sections.add(section);
                    sectionPositions.add(i);
                }
            }
            return sections.toArray(new String[0]);
        }

        @Override
        public int getPositionForSection(int sectionIndex) {
            return sectionPositions.get(sectionIndex);
        }

        @Override
        public int getSectionForPosition(int position) {
            return 0;
        }

        class ViewHolder extends RecyclerView.ViewHolder{
            private ImageView iconImage;
            private TextView sendText;
            private TextView nameText;
            private ConstraintLayout deleteLayout;
            private TextView feature_btn;
            private SwipeRevealLayout item;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                iconImage = itemView.findViewById(R.id.iconImage);
                sendText = itemView.findViewById(R.id.sendText);
                nameText = itemView.findViewById(R.id.nameText);
                deleteLayout=itemView.findViewById(R.id.deleteLayout);
                feature_btn = itemView.findViewById(R.id.feature_btn);
                item = itemView.findViewById(R.id.item);
            }
        }
    }


}
