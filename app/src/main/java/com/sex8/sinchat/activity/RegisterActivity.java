package com.sex8.sinchat.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sex8.sinchat.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RegisterActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.loginText)
    TextView loginText;
    @BindView(R.id.spinner_btn)
    ImageView spinnerBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        findViews();
        init();
        setListeners();

    }

    @Override
    public void findViews() {

    }

    @Override
    public void init() {

    }

    @Override
    public void setListeners() {
        loginText.setOnClickListener(this);
        spinnerBtn.setOnClickListener(this);
    }




    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.spinner_btn:
                ShowBottomRegisterDialog();
                break;
        }

    }
}
