package com.sex8.sinchat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.sex8.sinchat.R;
import com.sex8.sinchat.event.ChangeFragmentEvent;
import com.sex8.sinchat.event.MainEvent;
import com.sex8.sinchat.fragment.AccountFragment;
import com.sex8.sinchat.fragment.FindFragment;
import com.sex8.sinchat.fragment.MessageFragment;
import com.sex8.sinchat.fragment.PartyFragment;
import com.sex8.sinchat.model.IMData;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends BaseFragmentActivity {

    private BottomNavigationView nav_view;
    private boolean first=true;
    ArrayList<Map<String, String>> DataList;
    private Handler DataBaseHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setContainerRes(R.id.fragmentFL);

        findViews();
        init();
        setListeners();
        nav_view.setSelectedItemId(R.id.navigation_message);

    }

    @Override
    public void findViews() {
        nav_view = findViewById(R.id.nav_view);
    }

    @Override
    public void init() {
        nav_view.setItemIconTintList(null);
        IMData.getInstance().updateFriendList();
        IMData.getInstance().updateFollows();
        IMData.getInstance().updateFanList();
        IMData.getInstance().updateGroupList(0);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        IMData.getInstance().pauseWebSocket();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    synchronized public void onChangeFragment(ChangeFragmentEvent event) {

        nav_view.setSelectedItemId(R.id.navigation_party);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    synchronized public void onMainEvent(MainEvent event) {
        nav_view.getMenu().findItem(R.id.navigation_message).setIcon(R.drawable.ic_message_p);
    }

    @Override
    public void setListeners() {
        nav_view.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.navigation_message:
                        fragmentChanged(MessageFragment.class, "Message");
                        nav_view.getMenu().findItem(R.id.navigation_message).setIcon(R.drawable.ic_message_n);
                        return true;
                    case R.id.navigation_party:
                        fragmentChanged(PartyFragment.class, "Party");
                        return true;
                    case R.id.navigation_find:
                        fragmentChanged(FindFragment.class, "Find");
                        return true;
                    case R.id.navigation_account:
                        fragmentChanged(AccountFragment.class, "Account");
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

}
