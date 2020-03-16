package com.sex8.sinchat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.sex8.sinchat.R;
import com.sex8.sinchat.tools.OnClickLimitListener;

public class SettingActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

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

    }

    @Override
    public void init() {
        ((TextView) findViewById(R.id.titleText)).setText(R.string.ic_setting);
    }

    @Override
    public void setListeners() {
        findViewById(R.id.back).setOnClickListener(btnClick);
        findViewById(R.id.editPasswordLayout).setOnClickListener(btnClick);
        findViewById(R.id.verificationMailLayout).setOnClickListener(btnClick);
        findViewById(R.id.bindMobileNumberLayout).setOnClickListener(btnClick);
    }

    private OnClickLimitListener btnClick = new OnClickLimitListener() {
        @Override
        public void onClickLimit(View v) {
            switch (v.getId()){
                case R.id.back:
                    backMain();
                    break;
                case R.id.editPasswordLayout:
                    goEditPassword();
                    break;
                case R.id.verificationMailLayout:
                    goVerificationMail();
                    break;
                case R.id.bindMobileNumberLayout:
                    goBindCellphone();
                    break;
            }
        }
    };

    private void backMain(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        finish();
    }

    private void goEditPassword(){
        Intent intent = new Intent(this, EditPasswordActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void goVerificationMail(){
        Intent intent = new Intent(this, VerificationMailActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void goBindCellphone(){
        Intent intent = new Intent(this, BindCellphoneActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}
