package com.sex8.sinchat.dialog;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.sex8.sinchat.R;

public class TipDialog extends BaseDialog implements View.OnClickListener {
    private TextView titleText;
    public TipDialog(@NonNull Context context) {
        super(context, R.style.MyDialog);
        setContentView(R.layout.dialog_tip);
        setCancelable(true);
        setCanceledOnTouchOutside(true);
        setFullWidth();

        titleText = findViewById(R.id.titleText);
        findViewById(R.id.positiveText).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }

    public void setTitle(String title){
        titleText.setText(title);
    }

    public void show(String title){
        setTitle(title);
        show();
    }
}
