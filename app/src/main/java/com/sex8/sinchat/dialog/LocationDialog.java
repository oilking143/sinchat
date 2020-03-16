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

public class LocationDialog extends BaseDialog {
    private RadioGroup locationRadioGroup;
    private TextView titleText;
    private List<Area_List> data;
    private String choice="";
    public LocationDialog(@NonNull Context context, List data) {
        super(context, R.style.MyDialog);
        this.data=data;
        setContentView(R.layout.dialog_location);
        setCancelable(true);
        setCanceledOnTouchOutside(true);
        setFullWidth();
        findViews();
        init();
        setListeners();
    }

    private void findViews(){
        locationRadioGroup = findViewById(R.id.locationRadioGroup);
        titleText=findViewById(R.id.titleText);
    }

    private void init(){

        ArrayList<String> Names=new ArrayList<>();
        ArrayList<Integer> Ids=new ArrayList<>();

        /**因為ROOM必須要QUERY ROOMID，但是我只要不重複的GROUPID 所以乾脆用ARRAYLIST**/
        for(int i=0;i<data.size();i++){
            Names.add(data.get(i).getGroupName());
            Ids.add(data.get(i).getGroupId());
        }
        Names.add(0,"全部");
        Ids.add(0,2);
        LinkedHashSet<Integer> hashSet = new LinkedHashSet<>(Ids);
        LinkedHashSet<String> hashSet2 = new LinkedHashSet<>(Names);

        ArrayList<Integer> DuplicateIds = new ArrayList<>(hashSet);
        ArrayList<String> DuplicateNames = new ArrayList<>(hashSet2);

            for(int i=0;i<DuplicateNames.size();i++){
                RadioButton radioButton = getRadioButton();
                radioButton.setText(DuplicateNames.get(i));
                radioButton.setId(DuplicateIds.get(i));
                radioButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        choice=""+v.getId();
                    }
                });

                locationRadioGroup.addView(radioButton);
            }


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
                    Log.d("Choice",choice);
                    IMData.getInstance().changeGroup(choice);
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
