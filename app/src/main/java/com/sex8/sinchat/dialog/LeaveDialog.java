package com.sex8.sinchat.dialog;

import android.content.Context;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.sex8.sinchat.R;
import com.sex8.sinchat.model.IMData;
import com.sex8.sinchat.model.dataBase.Entity.Area_List;
import com.sex8.sinchat.tools.OnClickLimitListener;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public class LeaveDialog extends BaseDialog {
    private TextView titleText;
    private  ArrayList<Map<String, String>> data;
    private int choice=0;
    public LeaveDialog(@NonNull Context context, ArrayList<Map<String, String>> data) {
        super(context, R.style.MyDialog);
        this.data=data;
        setContentView(R.layout.dialog_leave);
        setCancelable(true);
        setCanceledOnTouchOutside(true);
        setFullWidth();
        findViews();
        init();
        setListeners();
    }

    private void findViews(){
        titleText=findViewById(R.id.titleText);
        titleText.setText(data.get(0).get("_group_name"));
    }

    private void init(){



    }

    private void setListeners(){
        findViewById(R.id.positiveText).setOnClickListener(btnClick);
        findViewById(R.id.negativeText).setOnClickListener(btnClick);
    }

    private OnClickLimitListener btnClick = new OnClickLimitListener(){
        @Override
        public void onClickLimit(View v) {
            dismiss();
            switch (v.getId()){
                case R.id.positiveText:

                    IMData.getInstance().leaveGroup(Integer.valueOf(data.get(0).get("_group_id")));
                    break;
                case R.id.negativeText:
                    break;
            }
        }
    };

    private RadioButton getRadioButton(){
        RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, getContext().getResources().getDimensionPixelOffset(R.dimen.dialog_radio_marginTop), 0, 0);
        RadioButton radioButton = new RadioButton(getContext());
        radioButton.setGravity(Gravity.CENTER_VERTICAL);
        radioButton.setLayoutParams(params);
        radioButton.setPadding(getContext().getResources().getDimensionPixelOffset(R.dimen.dialog_padding), 0, 0, 0);
        radioButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, getContext().getResources().getDimension(R.dimen.dialog_radio_textSize));
        radioButton.setTextColor(ContextCompat.getColor(getContext(), R.color.text_black));
        radioButton.setButtonTintList(ContextCompat.getColorStateList(getContext(), R.color.main_color_dark));
        return radioButton;
    }
}
