package com.sex8.sinchat.dialog;

import android.content.Context;
 import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;

import com.sex8.sinchat.IMMessageApplication;
import com.sex8.sinchat.R;
import com.sex8.sinchat.entity.Message;
import com.sex8.sinchat.event.ChatReportSendEvent;
import com.sex8.sinchat.tools.OnClickLimitListener;

import org.greenrobot.eventbus.EventBus;

public class ChatReportDialog extends BaseDialog {
    private Message message;
    private RadioGroup reportRadioGroup;
    private RadioGroup disposalRadioGroup;
    private EditText inputEdit;
    private View rootLayout;
    private View positiveText;

    public ChatReportDialog(@NonNull Context context, @NonNull View rootLayout) {
        super(context, R.style.MyDialog);
        setContentView(R.layout.dialog_report_chat);
        setCancelable(true);
        setCanceledOnTouchOutside(true);
        setFullWidth();

        findViews();
        init();
        setListeners();
    }

    private void findViews(){
        rootLayout = findViewById(R.id.rootLayout);
        reportRadioGroup = findViewById(R.id.reportRadioGroup);
        disposalRadioGroup = findViewById(R.id.disposalRadioGroup);
        inputEdit = findViewById(R.id.inputEdit);
        positiveText = findViewById(R.id.positiveText);
    }

    private void init(){
        //將兩個RadioGroup寬度同步
        reportRadioGroup.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        disposalRadioGroup.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        ViewGroup.LayoutParams params = reportRadioGroup.getLayoutParams();
        params.width = reportRadioGroup.getMeasuredWidth() > disposalRadioGroup.getMeasuredWidth() ? reportRadioGroup.getMeasuredWidth() : disposalRadioGroup.getMeasuredWidth();
        reportRadioGroup.setLayoutParams(params);
        disposalRadioGroup.setLayoutParams(params);
    }

    private void setListeners(){
        reportRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.radioButton_6){
                    inputEdit.setVisibility(View.VISIBLE);
                }else{
                    inputEdit.setVisibility(View.GONE);
                    IMMessageApplication.hideKeyboard(getContext(), rootLayout);
                }
            }
        });
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
                    EventBus.getDefault().post(new ChatReportSendEvent(message));
                    break;
                case R.id.negativeText:
                    break;
            }
        }
    };
}
