package com.sex8.sinchat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.sex8.sinchat.IMMessageApplication;
import com.sex8.sinchat.R;
import com.sex8.sinchat.model.IMData;
import com.sex8.sinchat.tools.OnClickLimitListener;

public class EditNickNameActivity extends BaseActivity {
    private View rootLayout;
    private EditText nickEdit;
    private TextView doneText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_nick_name);

        findViews();
        init();
        setListeners();
        checkNick();
    }

    @Override
    public void onBackPressed() {
        backActivity(false);
    }

    @Override
    public void findViews() {
        rootLayout = findViewById(R.id.rootLayout);
        nickEdit = findViewById(R.id.nickEdit);
        doneText = findViewById(R.id.doneText);
    }

    @Override
    public void init() {
        ((TextView) findViewById(R.id.titleText)).setText(R.string.edit_nick_name);
        nickEdit.setText(IMData.getInstance().getUserInfo().getNickName());
        nickEdit.requestFocus();
        doneText.setText(R.string.done);
        doneText.setVisibility(View.VISIBLE);
    }

    @Override
    public void setListeners() {
        findViewById(R.id.back).setOnClickListener(btnClick);
        findViewById(R.id.clearImage).setOnClickListener(btnClick);
        doneText.setOnClickListener(btnClick);
        nickEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkNick();
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
                case R.id.clearImage:
                    nickEdit.setText(IMData.getInstance().getUserInfo().getNickName());
                    break;
                case R.id.doneText:
                    backActivity(true);
                    break;
            }
        }
    };

    private void checkNick(){
        String nickName = nickEdit.getText().toString();
        if(!nickName.equals(IMData.getInstance().getUserInfo().getNickName())){
            doneText.setEnabled(true);
        }else{
            doneText.setEnabled(false);
        }
    }

    private void backActivity(boolean done){
        IMMessageApplication.hideKeyboard(this, rootLayout);
        if(done){
            Intent intent = new Intent();
            intent.putExtra("nickName", nickEdit.getText().toString());
            setResult(RESULT_OK, intent);
        }
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        finish();
    }
}
