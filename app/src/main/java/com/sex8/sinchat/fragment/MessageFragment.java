package com.sex8.sinchat.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.sex8.sinchat.IMMessageApplication;
import com.sex8.sinchat.R;
import com.sex8.sinchat.activity.ChatMessageActivity;
import com.sex8.sinchat.event.ChangeFragmentEvent;
import com.sex8.sinchat.event.DoneEvent;
import com.sex8.sinchat.event.EnterRoomResultEvent;
import com.sex8.sinchat.event.RoomCategoryClickEvent;
import com.sex8.sinchat.viewmodel.ChatRoomInfoViewModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.security.acl.Owner;

public class MessageFragment extends BaseFragment {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private PageAdapter pageAdapter;
    private boolean first = true;
    private TextView doneText;
    private ChatRoomInfoViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(first)
            return inflater.inflate(R.layout.fragment_message, container, false);
        else
            return thisView;
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


    public void findViews() {
        tabLayout = thisView.findViewById(R.id.tabLayout);
        viewPager = thisView.findViewById(R.id.viewPager);
        doneText=thisView.findViewById(R.id.doneText);
    }

    public void init() {
        tabLayout.setupWithViewPager(viewPager);
        pageAdapter = new PageAdapter(fragmentManager);
        viewPager.setAdapter(pageAdapter);
        viewModel = new ViewModelProvider(this).get(ChatRoomInfoViewModel.class);

    }

    public void setListeners() {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {

//                Log.d("select",""+i);

                if(i == 1){
                    try {
                        viewModel.getMyPartyList().observe(MessageFragment.this, chatroom_infos -> {
                            ((MessageChildFragment) pageAdapter.getItem(i)).uploadData(chatroom_infos);
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        doneText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doneText.setVisibility(View.INVISIBLE);
                EventBus.getDefault().post(new DoneEvent(1,"done"));
                doneText.setVisibility(View.GONE);
            }
        });


    }

    private class PageAdapter extends FragmentPagerAdapter {
        private SparseArray<MessageChildFragment> fragmentSparseArray = new SparseArray<>();
        private String[] pagerTitle = new String[]{
            getString(R.string.group),
            getString(R.string.friend)
        };

        public PageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            if(fragmentSparseArray.get(i) == null){
                MessageChildFragment fragment = new MessageChildFragment();
                Bundle bundle = new Bundle();

                if(i == 0){
                    bundle.putBoolean("group", true);
                    fragment.setArguments(bundle);
                }
                fragmentSparseArray.put(i, fragment);
            }

            /**發生了無法gewtItem的問題，只好用eventBus硬轉**/
            if(first)
            {
                first = false;
                EventBus.getDefault().post(new ChangeFragmentEvent());

            }

            return fragmentSparseArray.get(i);

        }

        @Override
        public int getCount() {
            return pagerTitle.length;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return pagerTitle[position];
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return POSITION_NONE;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    synchronized public void onRoomCategoryClick(RoomCategoryClickEvent event) {
        viewPager.setCurrentItem(1, true);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    synchronized public void onJoinRoomComplete(EnterRoomResultEvent event) {
        if(event.getStatus() != 1 && event.getStatus() != 2)
            activity.dismiss_Progress_Dialog();

        switch (event.getStatus()){
            case 0:
//                Intent intent = new Intent(activity, MessageChatActivity.class);
                Intent intent = new Intent(activity, ChatMessageActivity.class);
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            case 3:
                IMMessageApplication.showDialog(getContext(), R.string.dialog_title_join_room_not_found, null);
                break;
            case 4:
                activity.dismiss_Progress_Dialog();
                IMMessageApplication.showDialog(getContext(), R.string.dialog_title_join_room_fail, null);
                break;
            case 5:
                activity.dismiss_Progress_Dialog();
                IMMessageApplication.showDialog(getContext(), R.string.dialog_title_join_room_full, null);
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    synchronized public void onDone(DoneEvent event) {

        doneText.setVisibility(View.VISIBLE);
    }
}
