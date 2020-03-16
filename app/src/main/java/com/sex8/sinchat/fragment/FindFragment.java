package com.sex8.sinchat.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sex8.sinchat.R;
import com.sex8.sinchat.activity.FriendCircleActivity;
import com.sex8.sinchat.tools.OnClickLimitListener;

public class FindFragment extends BaseFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_find, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        findViews();
        init();
        setListeners();
    }

    private void findViews(){
        thisView.findViewById(R.id.back).setVisibility(View.GONE);
        ((TextView) thisView.findViewById(R.id.titleText)).setText(getString(R.string.title_find));
    }

    private void init(){

    }

    private void setListeners(){
        thisView.findViewById(R.id.friendsLayout).setOnClickListener(btnClick);
        thisView.findViewById(R.id.voiceControlLayout).setOnClickListener(btnClick);
    }

    private OnClickLimitListener btnClick = new OnClickLimitListener() {
        @Override
        public void onClickLimit(View v) {
            switch (v.getId()){
                case R.id.friendsLayout:
                    goFriendCircle();
                    break;
                case R.id.voiceControlLayout:
                    break;
            }
        }
    };

    private void goFriendCircle(){
        Intent intent = new Intent(activity, FriendCircleActivity.class);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}
