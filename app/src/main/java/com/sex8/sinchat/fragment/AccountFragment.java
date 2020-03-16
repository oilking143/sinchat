package com.sex8.sinchat.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.IdRes;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.sex8.sinchat.R;
import com.sex8.sinchat.activity.CardActivity;
import com.sex8.sinchat.activity.EditProfileActivity;
import com.sex8.sinchat.activity.ExchangeActivity;
import com.sex8.sinchat.activity.FriendListActivity;
import com.sex8.sinchat.activity.GroupControlActivity;
import com.sex8.sinchat.activity.SettingActivity;
import com.sex8.sinchat.model.IMData;
import com.sex8.sinchat.model.dataBase.Entity.Chatroom_Info;
import com.sex8.sinchat.model.dataBase.Entity.Member;
import com.sex8.sinchat.tools.OnClickLimitListener;
import com.sex8.sinchat.viewmodel.ChatRoomInfoViewModel;
import com.sex8.sinchat.viewmodel.SinchatMemberViewModel;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AccountFragment extends BaseFragment {
    private TextView friendText;
    private TextView attentionText;
    private TextView fanText;
    private TextView groupText;
    private ArrayList<JSONObject> data=new ArrayList<>();
    private SinchatMemberViewModel viewModel;
    private ChatRoomInfoViewModel chatviewModel;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

            findViews();
            init();
            setListeners();

    }

    @Override
    public void onResume() {
        super.onResume();


        //Group
//        viewModel.getFriendData().observe(this, new Observer<List<Member_Friend>>() {
//            @Override
//            public void onChanged(List<Member_Friend> member_friends) {
//                setSpanText(groupText, ""+member_friends.size(),"", getString(R.string.friend));
//            }
//        });
    }

    private void findViews(){
        friendText = thisView.findViewById(R.id.friendText);
        attentionText = thisView.findViewById(R.id.attentionText);
        fanText = thisView.findViewById(R.id.fanText);
        groupText = thisView.findViewById(R.id.groupText);
    }

    private void init(){

        thisView.findViewById(R.id.back).setVisibility(View.GONE);
        ((TextView) thisView.findViewById(R.id.titleText)).setText(getString(R.string.title_account));
        ((TextView) thisView.findViewById(R.id.vipLevelText)).setText(getString(R.string.vip_level, 20));
        ((TextView) thisView.findViewById(R.id.nameText)).setText(""+IMData.getInstance().getUserInfo().getUid());
        ((TextView) thisView.findViewById(R.id.moneyText)).setText("999,999,999");
        Glide.with(this)
                .load(R.mipmap.ic_launcher)
                .circleCrop()
                .into(((ImageView) thisView.findViewById(R.id.iconImage)));
        viewModel = new ViewModelProvider(this).get(SinchatMemberViewModel.class);
        chatviewModel =  new ViewModelProvider(this).get(ChatRoomInfoViewModel.class);
        viewModel.getFriendData().observe(this, new Observer<List<Member>>() {
            @Override
            public void onChanged(List<Member> member) {
                setSpanText(friendText, ""+member.size(),"", getString(R.string.friend));
            }
        });

        viewModel.getFanData().observe(this, new Observer<List<Member>>() {
            @Override
            public void onChanged(List<Member> member) {
                setSpanText(fanText, ""+member.size(), "", getString(R.string.fan));

            }
        });

        viewModel.getFollowData().observe(this, new Observer<List<Member>>() {
            @Override
            public void onChanged(List<Member> member) {
                setSpanText(attentionText, ""+member.size(), "", getString(R.string.attention));
            }
        });

        chatviewModel.getMyPartyList().observe(this, new Observer<List<Chatroom_Info>>() {
            @Override
            public void onChanged(List<Chatroom_Info> chatroom_infos) {

                int count = 0;

                for(int i=0;i<chatroom_infos.size();i++)
                {
                    if(chatroom_infos.get(i).getChatRoomName()!=null)
                    {
                        count++;
                    }
                }
                setSpanText(groupText, ""+count, "", getString(R.string.group));
            }
        });

    }

    private void setListeners(){
        thisView.findViewById(R.id.infoLayout).setOnClickListener(btnClick);
        thisView.findViewById(R.id.payLayout).setOnClickListener(btnClick);
        thisView.findViewById(R.id.cardLayout).setOnClickListener(btnClick);
        thisView.findViewById(R.id.adminLayout).setOnClickListener(btnClick);
        thisView.findViewById(R.id.settingLayout).setOnClickListener(btnClick);
        friendText.setOnClickListener(btnClick);
        attentionText.setOnClickListener(btnClick);
        fanText.setOnClickListener(btnClick);
        groupText.setOnClickListener(btnClick);
    }

    private OnClickLimitListener btnClick = new OnClickLimitListener() {
        @Override
        public void onClickLimit(View v) {
            switch (v.getId()){
                case R.id.infoLayout:
                    goEditProfile();
                    break;
                case R.id.payLayout:
                    goExChange();
                    break;
                case R.id.cardLayout:
                    goCardLayout();
                    break;
                case R.id.adminLayout:
                    goAdminSetting();
                    break;
                case R.id.settingLayout:
                    goSetting();
                    break;
                case R.id.friendText:
                    goFriendList(v.getId());
                    break;
                case R.id.attentionText:
                    goFriendList(v.getId());
                    break;
                case R.id.fanText:
                    goFriendList(v.getId());
                    break;
                case R.id.groupText:
                    goFriendList(v.getId());
                    break;
            }
        }
    };

    private void setSpanText(TextView textView, String... text){
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<text.length;i++) {
            if(i == text.length - 1){
                text[i] = "\n" + text[i];
            }
            sb.append(text[i]);
        }
        SpannableString spannableString = new SpannableString(sb.toString());
        int len = 0;
        for(int i=0;i<text.length;i++){
            if(i == 0){
                spannableString.setSpan(new TextAppearanceSpan(activity, R.style.account_info_1), len, len + text[i].length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }else if(i == 1 && text.length > 2){
                spannableString.setSpan(new TextAppearanceSpan(activity, R.style.account_info_2), len, len + text[i].length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }else{
                spannableString.setSpan(new TextAppearanceSpan(activity, R.style.account_info_3), len, len + text[i].length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            len += text[i].length();
        }
        textView.setText(spannableString);
    }

    private void goEditProfile(){
        Intent intent = new Intent(activity, EditProfileActivity.class);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void goAdminSetting()
    {
        Intent intent = new Intent(activity, GroupControlActivity.class);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void goExChange(){
        Intent intent = new Intent(activity, ExchangeActivity.class);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void goSetting(){
        Intent intent = new Intent(activity, SettingActivity.class);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void goFriendList(@IdRes int fromId){
        Intent intent = new Intent(activity, FriendListActivity.class);
        intent.putExtra("fromId", fromId);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void goCardLayout()
    {
        Intent intent = new Intent(activity, CardActivity.class);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }


}