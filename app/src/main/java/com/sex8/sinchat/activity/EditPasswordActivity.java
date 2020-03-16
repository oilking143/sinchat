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
import com.sex8.sinchat.tools.OnClickLimitListener;

public class EditPasswordActivity extends BaseActivity {
    private EditText oldEdit;
    private EditText newEdit;
    private EditText confirmEdit;
    private TextView doneText;
    private View rootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_password);

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
        oldEdit = findViewById(R.id.oldEdit);
        newEdit = findViewById(R.id.newEdit);
        confirmEdit = findViewById(R.id.confirmEdit);
        doneText = findViewById(R.id.doneText);
        rootLayout = findViewById(R.id.rootLayout);
    }

    @Override
    public void init() {
        ((TextView) findViewById(R.id.titleText)).setText(R.string.edit_password);
        doneText.setText(R.string.done);
        doneText.setVisibility(View.VISIBLE);
        doneText.setEnabled(false);
        oldEdit.requestFocus();
    }

    @Override
    public void setListeners() {
        findViewById(R.id.back).setOnClickListener(btnClick);
        findViewById(R.id.doneText).setOnClickListener(btnClick);
        findViewById(R.id.forgetText).setOnClickListener(btnClick);
        oldEdit.addTextChangedListener(passwordTextWatcher);
        newEdit.addTextChangedListener(passwordTextWatcher);
        confirmEdit.addTextChangedListener(passwordTextWatcher);
    }

    private TextWatcher passwordTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            checkPassword();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

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
                case R.id.forgetText:
                    break;
            }
        }
    };

    private void checkPassword(){
        String oldPw = oldEdit.getText().toString();
        String newPw = newEdit.getText().toString();
        String confirmPw = confirmEdit.getText().toString();

        //密碼不可為空
        if(!oldPw.isEmpty() && !newPw.isEmpty() && !confirmPw.isEmpty()){
            if(newPw.equals(confirmPw)){
                doneText.setEnabled(true);
                return;
            }
        }
        doneText.setEnabled(false);
    }

    private void backActivity(boolean done){
        IMMessageApplication.hideKeyboard(this, rootLayout);
        if(done){
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
        }
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        finish();
    }
}
