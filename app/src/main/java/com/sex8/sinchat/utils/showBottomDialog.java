package com.sex8.sinchat.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.sex8.sinchat.R;
import com.sex8.sinchat.activity.MainActivity;

public class showBottomDialog {

    private static showBottomDialog instance;
    private Activity activity;
    Dialog mdialog;

    public static  showBottomDialog getInstance(Activity activity) {
        if (instance == null) {
            instance = new showBottomDialog(activity);
        }
        return instance;
    }


    public showBottomDialog(@Nullable Activity activity) {
        this.activity = activity;
        mdialog = new Dialog(activity, R.style.ActionSheetDialogStyle);

    }

    public void ShowBottomConfirmDialog(String msg, boolean single) {
        View view = LayoutInflater.from(activity).inflate(
                R.layout.dialoag_confirm, null);
        view.setMinimumWidth(10000);
        mdialog.setContentView(view);
        Window dialogWindow = mdialog.getWindow();
        assert dialogWindow != null;
        dialogWindow.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.x = 0;
        lp.y = 0;
        lp.height = metrics.heightPixels / 2;
        lp.width = metrics.widthPixels;
        dialogWindow.setAttributes(lp);

        TextView dismiss=(TextView)mdialog.findViewById(R.id.dismiss);
        TextView confirm=(TextView)mdialog.findViewById(R.id.confirm);
        TextView msgtxt=(TextView)mdialog.findViewById(R.id.message);
        LinearLayout frame=(LinearLayout)mdialog.findViewById(R.id.frame);

        if(single)
        {
            dismiss.setVisibility(View.GONE);
            frame.setVisibility(View.GONE);
        }

        msgtxt.setText(msg);

        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mdialog.dismiss();

            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(activity, MainActivity.class);
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                activity.finish();


            }
        });


        mdialog.show();
    }

}
