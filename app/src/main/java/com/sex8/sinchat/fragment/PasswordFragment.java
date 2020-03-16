package com.sex8.sinchat.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import com.sex8.sinchat.Constant;
import com.sex8.sinchat.R;
import com.sex8.sinchat.activity.MainActivity;
import com.sex8.sinchat.event.RoomListUpdateEvent;
import com.sex8.sinchat.event.SocketConnectionEvent;
import com.sex8.sinchat.event.UserLoginCompleteEvent;
import com.sex8.sinchat.model.IMData;
import com.sex8.sinchat.model.dataBase.Entity.Area_List;
import com.sex8.sinchat.model.dataBase.IMRepository;
import com.sex8.sinchat.model.server.pojo.roomlist.GroupRoom;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class PasswordFragment extends Fragment implements View.OnClickListener, TextWatcher {
    @BindView(R.id.loginText)
    TextView loginText;
    @BindView(R.id.registerText)
    TextView registerText;
    Unbinder unbinder;
    @BindView(R.id.mainLayout)
    ConstraintLayout mainLayout;
    @BindView(R.id.account_id2)
    EditText accountId2;
    @BindView(R.id.account_password)
    EditText accountPassword;
    @BindView(R.id.deleteall)
    ImageView deleteall;
    @BindView(R.id.visible)
    ImageView visible;

    private Timer mTimer = new Timer();
    public ProgressDialog progressDialog;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_password_launch, container, false);
        unbinder = ButterKnife.bind(this, view);
        init();
        setListener();
        return view;
    }


    private void init() {
        EventBus.getDefault().register(this);
        int newversion=0;
        try {
            PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            newversion = Integer.valueOf(pInfo.versionName.substring(pInfo.versionName.lastIndexOf(".")+1));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    private void setListener() {
        accountId2.addTextChangedListener(this);
        accountPassword.addTextChangedListener(this);
        deleteall.setOnClickListener(this);
        visible.setOnClickListener(this);
        registerText.setOnClickListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loginText:
                progressDialog = ProgressDialog.show(getContext(),null, getString(R.string.dialog_refreshing), true, false);
                IMData.getInstance().userLogin(null);
                mTimer.schedule(new FailTask(),10000);
                break;

            case R.id.deleteall:
                accountPassword.setText("");

                break;

            case R.id.visible:

                break;


        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    synchronized public void onUserLoginComplete(UserLoginCompleteEvent event) {


        if (event.getStatus() == Constant.RESPONSE_SUCCESS_V4) {
            IMData.getInstance().updateRoomList();
            mTimer.cancel();
            goMain();
            EventBus.getDefault().removeStickyEvent(event);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    synchronized public void onRoomListUpdate(RoomListUpdateEvent event) {
        if (event.getResponse().body().getError().getCode() != 0 && event.getResponse().body().getError().getMessage().length() > 5) {
            Toast.makeText(getActivity(), event.getResponse().message(), Toast.LENGTH_LONG).show();
        } else {
            List<GroupRoom> groupRooms = event.getResponse().body().getData().get(1);
            ArrayList<Area_List> list = new ArrayList<>();

            for (int i = 0; i < groupRooms.size(); i++) {
                int groupId = groupRooms.get(i).getGroupId();
                String groupName = groupRooms.get(i).getGroupName();
                for(int roomId: groupRooms.get(i).getChatroomIds()){
                    Area_List area_list = new Area_List();
                    area_list.setGroupId(groupId);
                    area_list.setGroupName(groupName);
                    area_list.setChatRoomId(roomId);
                    list.add(area_list);

                }
            }

            IMRepository.getInstance().insertAreaLists(list);

        }


    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    synchronized public void onSocketConnection(SocketConnectionEvent event) {
        if (event.getStatus() == Constant.RESPONSE_SUCCESS_V4) {
            goMain();
        } else {
            IMData.getInstance().terminate();
            Toast.makeText(getActivity(), event.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void goMain() {
        if (progressDialog!=null){
            progressDialog.dismiss();
        }

        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        getActivity().finish();
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        if (accountId2.getText().toString().length() > 0 && accountPassword.getText().toString().length() > 0) {
            loginText.setEnabled(true);
            loginText.setOnClickListener(this);
            loginText.setBackground(getActivity().getResources().getDrawable(R.drawable.btn_positive_bg));
        } else {
//            loginText.setEnabled(false);
            loginText.setEnabled(true);
            loginText.setOnClickListener(null);
            loginText.setBackground(getActivity().getResources().getDrawable(R.drawable.btn_negative_ripple));
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    private class FailTask extends TimerTask {
        @Override
        public void run() {
            EventBus.getDefault().post(new UserLoginCompleteEvent(-2, "網路連線失敗，請稍後重試"));
        }
    }
}
