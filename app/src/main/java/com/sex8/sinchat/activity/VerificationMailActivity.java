package com.sex8.sinchat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.sex8.sinchat.IMMessageApplication;
import com.sex8.sinchat.R;
import com.sex8.sinchat.tools.OnClickLimitListener;

public class VerificationMailActivity extends BaseActivity {
    private EditText emailEdit;
    private TextView doneText;
    private View rootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_mail);

        findViews();
        init();
        setListeners();
    }

    @Override
    public void onBackPressed() {
        backActivity(false);
    }

    @Override
    public void findViews() {
        emailEdit = findViewById(R.id.emailEdit);
        doneText = findViewById(R.id.doneText);
        rootLayout = findViewById(R.id.rootLayout);
    }

    @Override
    public void init() {
        ((TextView) findViewById(R.id.titleText)).setText(R.string.verification_mail);
        doneText.setText(R.string.done);
        doneText.setVisibility(View.VISIBLE);
        doneText.setEnabled(false);
        emailEdit.requestFocus();

        String redString = getResources().getString(R.string.verification_mail_tip);
        ((TextView) findViewById(R.id.tipText)).setText(Html.fromHtml(redString));
    }

    @Override
    public void setListeners() {
        findViewById(R.id.back).setOnClickListener(btnClick);
        findViewById(R.id.doneText).setOnClickListener(btnClick);
        findViewById(R.id.clearImage).setOnClickListener(btnClick);
        emailEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //檢查Email格式
                doneText.setEnabled(IMMessageApplication.checkEmail(emailEdit.getText().toString()));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private OnClickLimitListener btnClick = new OnClickLimitListener() {
        @Override
        public void onClickLimit(View v) {
            switch (v.getId()){
                case R.id.back:
                    backActivity(false);
                    break;
                case R.id.doneText:
                    backActivity(true);
                    break;
                case R.id.clearImage:
                    emailEdit.setText(null);
                    break;
            }
        }
    };

    private void backActivity(boolean done){
        IMMessageApplication.hideKeyboard(this, rootLayout);
        if(done){
            Intent intent = new Intent();
            emailEdit.getText().toString();
            setResult(RESULT_OK, intent);
        }
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        finish();
    }
}
