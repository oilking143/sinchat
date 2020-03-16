package com.sex8.sinchat.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Group;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sex8.sinchat.IMMessageApplication;
import com.sex8.sinchat.R;
import com.sex8.sinchat.activity.ChatMessageActivity;
import com.sex8.sinchat.decoration.ItemDecorationAlbumColumns;
import com.sex8.sinchat.event.LocationEvent;
import com.sex8.sinchat.event.ReceivedWebSocketDataEvent;
import com.sex8.sinchat.event.RoomChatListDetailEvent;
import com.sex8.sinchat.event.RoomPartyListDetailEvent;
import com.sex8.sinchat.event.StartRoomInfoLocalCheckEvent;
import com.sex8.sinchat.model.IMData;
import com.sex8.sinchat.model.dataBase.Entity.Chatroom_Info;
import com.sex8.sinchat.model.dataBase.Entity.Chatroom_Info_Local;
import com.sex8.sinchat.model.dataBase.IMRepository;
import com.sex8.sinchat.model.server.pojo.roomlist.GetRoomListDetailResponse;
import com.sex8.sinchat.tools.OnClickLimitListener;
import com.sex8.sinchat.viewmodel.AreaListViewModel;
import com.sex8.sinchat.viewmodel.ChatRoomInfoViewModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class PartyChildFragment extends BaseFragment {
    private boolean first = true;
    private RecyclerView recyclerView;
    private GridLayoutManager layoutManager;
    private PartyChildAdapter partyChildAdapter;
    private boolean isGroup;
    private List<Chatroom_Info> partyList=new ArrayList<>();
    private List<Chatroom_Info> wholePartyList=new ArrayList<>();

    private Group noPartyGroup;
    ChatRoomInfoViewModel viewModel;
    AreaListViewModel areaModel;
    private boolean syncChatroomLocalDB = false;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(first){
            return inflater.inflate(R.layout.fragment_party_chlid, container, false);
        }else{
            return thisView;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(first) {

            findViews();
            init();
            setListeners();


        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }


    private void findViews(){
        recyclerView = thisView.findViewById(R.id.recyclerView);
        noPartyGroup = thisView.findViewById(R.id.noPartyGroup);
    }

    private void init(){
//        radioButton.setText(data.get(i).get("_group_name"));
//        radioButton.setId(Integer.valueOf(data.get(i).get("_group_id")));

        if(getArguments() != null){
            isGroup = getArguments().getBoolean("group", false);
        }

        int padding = getResources().getDimensionPixelSize(R.dimen.party_list_padding);
        layoutManager = new GridLayoutManager(getContext(), 2, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        ///以螢幕寬度計算item大小
        WindowManager windowManager = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        recyclerView.addItemDecoration(new ItemDecorationAlbumColumns(padding, layoutManager.getSpanCount()));

        partyChildAdapter = new PartyChildAdapter();
        recyclerView.setAdapter(partyChildAdapter);

        //textView文本插入圖片
        TextView noPartyHintText = thisView.findViewById(R.id.noPartyHintText);
        int start = noPartyHintText.getText().toString().indexOf('[');
        int end = noPartyHintText.getText().toString().lastIndexOf(']') + 1;
        ImageSpan span = new ImageSpan(activity, R.drawable.ic_location, ImageSpan.ALIGN_BOTTOM);
        SpannableStringBuilder ssb = new SpannableStringBuilder(noPartyHintText.getText());
        ssb.setSpan(span, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        noPartyHintText.setText(ssb);
        viewModel = new ViewModelProvider(this).get(ChatRoomInfoViewModel.class);
        areaModel = new ViewModelProvider(this).get(AreaListViewModel.class);

        viewModel.getMyGroupList().observe(this,chatroom_infos -> {
            if(!isGroup)
            {
                if(chatroom_infos.size()>0)
                {
                    noPartyGroup.setVisibility(View.GONE);
                }
                else
                {
                    noPartyGroup.setVisibility(View.VISIBLE);
                }
            }

        });
    }

    private void setListeners(){
        partyChildAdapter.setItemClick(new OnClickLimitListener() {
            @Override
            public void onClickLimit(View v) {

                Intent intent = new Intent(getActivity(), ChatMessageActivity.class);

                RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) v.getTag();
                int position = viewHolder.getAdapterPosition();
                Bundle mBundle = new Bundle();
                mBundle.putInt("mRoomId", Integer.valueOf(partyList.get(position).getChatRoomId()));
                mBundle.putString("RoomName",partyList.get(position).getChatRoomName());
                mBundle.putInt("sort", 20);
                mBundle.putInt("group_id",  Integer.valueOf(partyList.get(position).getGroupId()));
                mBundle.putBoolean("private", false);
                intent.putExtras(mBundle);
                startActivity(intent);
            }
        });
    }


    public void uploadData(List<Chatroom_Info> chatroom_infos){



        if(isGroup)
        {

            partyList.clear();
            partyList.addAll(chatroom_infos);

        }
        else
        {
            partyList.clear();


            if(chatroom_infos.size()>0)
            {
                noPartyGroup.setVisibility(View.GONE);
                for(int i=0;i<chatroom_infos.size();i++)
                {
                    if(chatroom_infos.get(i).getChatRoomName()!=null)
                    {
                        partyList.add(chatroom_infos.get(i));

                    }
                }

            }
            else
            {
                noPartyGroup.setVisibility(View.VISIBLE);
            }



        }

        partyChildAdapter.notifyDataSetChanged();



    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    synchronized public void onRoomChatListDetail(RoomChatListDetailEvent event) {



    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    synchronized public void onLocation(LocationEvent event) {


      if(isGroup)
      {
          if(event.getMessage().equals("2"))
          {

              IMRepository.getInstance().getPartyInfoData().observe(this,chatroom_infos -> {

                  uploadData(chatroom_infos);
              });
          }
          else
          {
              IMRepository.getInstance().getGroupList(Integer.valueOf(event.getMessage())).observe(this, chatroom_infos -> {

                  uploadData(chatroom_infos);

              });
          }
      }





    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    synchronized public void onStartRoomInfoLocalCheck(StartRoomInfoLocalCheckEvent event) {
        if(!syncChatroomLocalDB) {
            syncChatroomLocalDB=true;
            List<GetRoomListDetailResponse.Data> datas = event.getResponse().getData();
            CopyOnWriteArrayList<GetRoomListDetailResponse.Data> safeList = new CopyOnWriteArrayList<>();
            safeList.addAll(datas);

            //Chatroom_Info_Local
            IMRepository.getInstance().getAllRoomLocalInfos().observe(this, new Observer<List<Chatroom_Info_Local>>() {
                @Override
                public void onChanged(List<Chatroom_Info_Local> chatroom_info_locals) {
                    IMRepository.getInstance().getAllRoomLocalInfos().removeObserver(this);
                    for(GetRoomListDetailResponse.Data data:datas) {
                        for (Chatroom_Info_Local locat : chatroom_info_locals) {
                            if (locat.getChatRoomId() == data.getChatroom_id()) {
                                safeList.remove(data);
                                break;
                            }
                        }
                    }

                    List<Chatroom_Info_Local> locallist = new ArrayList<>();
                    for(GetRoomListDetailResponse.Data data:safeList) {
                        Chatroom_Info_Local local = new Chatroom_Info_Local();
                        local.setChatRoomId(data.getChatroom_id());
                        local.setNeedAPISync(true);
                        locallist.add(local);
                    }
                    IMRepository.getInstance().insertChatRoomLocalInfos(locallist);
                }
            });

        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    synchronized public void onLastEvent(ReceivedWebSocketDataEvent event) {
        try {
            if(event.getResult().getString("content").contains("action")
                    &&event.getResult().getString("content").contains("joinChatroom"))
            {

                for(Chatroom_Info info:wholePartyList)
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
                for(Chatroom_Info info:wholePartyList)
                {
                    if(info.getChatRoomId()==event.getResult().getInt("roomId"))
                    {
                        info.setMembercounts(info.getMembercounts()-1);
                        IMRepository.getInstance().insertChatRoom(info);
                    }
                }
            }
        }catch (JSONException j)
        {
            Log.e(getClass().getSimpleName(), j.getMessage(), j);
        }

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    synchronized public void onRoomPartyListDetail(RoomPartyListDetailEvent event) {

//        Log.d("Stataus",""+event.getStatus());
        if(event.getStatus()==1 && event.getResponse().getData()!=null)
        {
            areaModel.getAreaList().observe(this,area_lists -> {
            for(int j=0;j<event.getResponse().getData().size();j++)
            {
                int counts= event.getResponse().getData().get(j).getMemberIds().size();
//                Log.d("Stataus",""+counts);


                Chatroom_Info info=new Chatroom_Info();
                info.setChatRoomId(event.getResponse().getData().get(j).getChatroom_id());
                info.setChatRoomName(event.getResponse().getData().get(j).getChatroom_name());
                info.setDescrption(event.getResponse().getData().get(j).getDescription());
                info.setMembercounts(counts);
                info.setGroupName(area_lists.get(j).getGroupName());
                info.setGroupId(event.getResponse().getData().get(j).getGroup_id());
                info.setUids(event.getResponse().getData().get(j).getAdminIds().toString());
                info.set_imageDes1Link(event.getResponse().getData().get(j).getImageDes1Link());
                info.set_imageDes2Link(event.getResponse().getData().get(j).getImageDes2Link());
                info.set_imageDes3Link(event.getResponse().getData().get(j).getImageDes3Link());
                info.set_imageLogoLink(event.getResponse().getData().get(j).getImageLogoLink());
                IMRepository.getInstance().insertChatRoom(info);

            }

            });

         if(isGroup)
         {
             viewModel.getPartyList().observe(this,chatroom_infos -> {

                 uploadData(chatroom_infos);
                 if(chatroom_infos.size()>0)
                 {
                     noPartyGroup.setVisibility(View.GONE);
                 }
                 else
                 {
                     noPartyGroup.setVisibility(View.VISIBLE);
                 }
                 wholePartyList=chatroom_infos;
             });

         }
         else
         {
             viewModel.getMyGroupList().observe(this,chatroom_infos -> {

                 uploadData(chatroom_infos);
                 if(chatroom_infos.size()>0)
                 {
                     noPartyGroup.setVisibility(View.GONE);
                 }
                 else
                 {
                     noPartyGroup.setVisibility(View.VISIBLE);
                 }
             });

         }




        }




    }


        private class PartyChildAdapter extends RecyclerView.Adapter{
        private View.OnClickListener itemClick;
        private int[] bgColor = new int[]{
                ContextCompat.getColor(activity, R.color.party_list_item_color_1),
                ContextCompat.getColor(activity, R.color.party_list_item_color_2),
                ContextCompat.getColor(activity, R.color.party_list_item_color_3),
                ContextCompat.getColor(activity, R.color.party_list_item_color_4)
        };

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View convertView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_party_list, viewGroup, false);
            RippleDrawable rippleDrawable = (RippleDrawable) convertView.getBackground();
            ((GradientDrawable) rippleDrawable.getDrawable(1)).setColor(Color.BLACK);
            return new ViewHolder(convertView);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            ViewHolder vh = (ViewHolder) viewHolder;

            ((GradientDrawable)((RippleDrawable) vh.itemView.getBackground()).getDrawable(1)).setColor(bgColor[viewHolder.getAdapterPosition() % bgColor.length]);
            vh.itemView.setOnClickListener(itemClick);

            vh.onlineText.setText("(" + partyList.get(viewHolder.getAdapterPosition()).getMembercounts() + "人)");
            vh.titleText.setText(partyList.get(viewHolder.getAdapterPosition()).getChatRoomName());

            Glide.with(activity)
                    .load(IMData.getInstance().getWebSocketInfo().getFileHosts().get(0)+partyList.get(viewHolder.getAdapterPosition()).get_imageLogoLink())
                    .circleCrop()
                    .into(vh.imageView);
        }

        @Override
        public int getItemCount() {
            return partyList != null ? partyList.size() : 0;
        }

        public void setItemClick(View.OnClickListener itemClick) {
            this.itemClick = itemClick;
        }

        class ViewHolder extends RecyclerView.ViewHolder{
            public ImageView imageView;
            public TextView onlineText;
            public TextView titleText;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                itemView.setTag(this);
                imageView = itemView.findViewById(R.id.imageView);
                onlineText = itemView.findViewById(R.id.onlineText);
                titleText = itemView.findViewById(R.id.titleText);
            }
        }
    }
}
