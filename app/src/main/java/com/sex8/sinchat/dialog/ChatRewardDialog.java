package com.sex8.sinchat.dialog;

import android.content.Context;
 import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.sex8.sinchat.R;
import com.sex8.sinchat.entity.Message;
import com.sex8.sinchat.event.ChatRewardSendEvent;
import com.sex8.sinchat.tools.OnClickLimitListener;

import org.greenrobot.eventbus.EventBus;

public class ChatRewardDialog extends BaseDialog {
    private Message message;
    private EditText inputEdit;
    private View rootLayout;
    private View positiveText;

    public ChatRewardDialog(@NonNull Context context, @NonNull View rootLayout) {
        super(context, R.style.MyDialog);
        setContentView(R.layout.dialog_reward);
        setCancelable(true);
        setCanceledOnTouchOutside(true);
        setFullWidth();

        findViews();
        init();
        setListeners();
    }

    private void findViews(){
        rootLayout = findViewById(R.id.rootLayout);
        inputEdit = findViewById(R.id.inputEdit);
        positiveText = findViewById(R.id.positiveText);
    }

    private void init(){

    }

    private void setListeners(){
        positiveText.setOnClickListener(btnClick);
        findViewById(R.id.negativeText).setOnClickListener(btnClick);
    }

    public void show(Message message){
        this.message = message;
        show();
    }

    private OnClickLimitListener btnClick = new OnClickLimitListener(){
        @Override
        public void onClickLimit(View v) {
            dismiss();
            switch (v.getId()){
                case R.id.positiveText:
                    inputEdit.setText(null);
                    EventBus.getDefault().post(new ChatRewardSendEvent(message));
                    break;
                case R.id.negativeText:
                    break;
            }
        }
    };
}
