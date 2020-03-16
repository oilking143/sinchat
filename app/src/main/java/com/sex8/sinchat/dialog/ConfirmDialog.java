package com.sex8.sinchat.dialog;

import android.content.Context;
 import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.sex8.sinchat.R;

import org.greenrobot.eventbus.EventBus;

public class ConfirmDialog extends BaseDialog implements View.OnClickListener {
    private TextView titleText;
    private Object event;
    public ConfirmDialog(@NonNull Context context) {
        super(context, R.style.MyDialog);
        setContentView(R.layout.dialog_confirm);
        setCancelable(true);
        setCanceledOnTouchOutside(true);
        setFullWidth();

        titleText = findViewById(R.id.titleText);
        findViewById(R.id.positiveText).setOnClickListener(this);
        findViewById(R.id.negativeText).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        dismiss();
        if(v.getId() == R.id.positiveText){
            EventBus.getDefault().post(event);
        }
    }

    public void setTitle(String title){
        titleText.setText(title);
    }

    public void show(String title, Object event){
        this.event = event;
        setTitle(title);
        show();
    }
}
