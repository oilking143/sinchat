package com.sex8.sinchat.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.sex8.sinchat.Constant;
import com.sex8.sinchat.R;
import com.sex8.sinchat.activity.MainActivity;
import com.sex8.sinchat.event.SocketConnectionEvent;
import com.sex8.sinchat.event.UserLoginCompleteEvent;
import com.sex8.sinchat.model.IMData;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ConfirmCodeFragment extends Fragment implements View.OnClickListener, TextWatcher {
    @BindView(R.id.account_id2)
    EditText accountId2;
    @BindView(R.id.account_password)
    EditText accountPassword;
    @BindView(R.id.visible)
    TextView visible;
    @BindView(R.id.loginText)
    TextView loginText;
    @BindView(R.id.registerText)
    TextView registerText;
    Unbinder unbinder;
    @BindView(R.id.progress_view)
    ProgressBar progressView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_confirm_launch, container, false);
        unbinder = ButterKnife.bind(this, view);
        init();
        setListener();
        return view;
    }


    private void init() {
        EventBus.getDefault().register(this);
    }

    private void setListener() {

        accountId2.addTextChangedListener(this);
        accountPassword.addTextChangedListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if(accountId2.getText().toString().length()>0 && accountPassword.getText().toString().length()>0)
        {
            loginText.setEnabled(true);
            loginText.setOnClickListener(this);
            loginText.setBackground(getActivity().getResources().getDrawable(R.drawable.btn_positive_bg));
        }
        else
        {
            loginText.setEnabled(false);
            loginText.setOnClickListener(null);
            loginText.setBackground(getActivity().getResources().getDrawable(R.drawable.btn_negative_ripple));
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loginText:
                progressView.setVisibility(View.VISIBLE);
                IMData.getInstance().userLogin(null);

                break;


        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    synchronized public void onUserLoginComplete(UserLoginCompleteEvent event) {
        if (event.getStatus() == Constant.RESPONSE_SUCCESS_V4) {
//            if(SQLiteDBUtils.getInstance(getActivity()).QueryRoomList_Area().size()<1)
//            {
//                Log.d("ChatRoomList","No SQLData,Start API");
//            }
//            else
//            {
//                IMData.getInstance().updateRoomList();
//            }
        } else {
            progressView.setVisibility(View.VISIBLE);
            Toast.makeText(getActivity(), event.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    synchronized public void onSocketConnection(SocketConnectionEvent event) {
        if (event.getStatus() == Constant.RESPONSE_SUCCESS_V4) {
            progressView.setVisibility(View.GONE);
            goMain();
        } else {
            IMData.getInstance().terminate();
            progressView.setVisibility(View.VISIBLE);
            Toast.makeText(getActivity(), event.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void goMain() {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        getActivity().finish();
    }

}
