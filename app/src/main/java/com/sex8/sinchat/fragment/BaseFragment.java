package com.sex8.sinchat.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.sex8.sinchat.IMMessageApplication;
import com.sex8.sinchat.R;
import com.sex8.sinchat.activity.BaseFragmentActivity;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BaseFragment extends Fragment {
    public BaseFragmentActivity activity;
    public FragmentManager fragmentManager;
    public View thisView;
    public ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return new TextView(getContext());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        thisView = getView();
        activity = (BaseFragmentActivity) getActivity();
        fragmentManager = getChildFragmentManager();

        if(thisView instanceof TextView)
            ((TextView) thisView).setText(getTag());

        IMMessageApplication.application.mFirebaseAnalytics.setCurrentScreen(this.activity,
                this.getClass().getSimpleName(),null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    public String fortMatTimeStr(Long TimeMillis)
    {
        long yourmilliseconds = TimeMillis;
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Date resultdate = new Date(yourmilliseconds);

        return sdf.format(resultdate);
    }


    public void showToast(String msg)
    {
        Toast.makeText(getActivity(),msg,Toast.LENGTH_LONG).show();

    }

    public int getHours(long from,long to)
    {
        int hours = (int) ((to - from)/(100 * 60 * 60));
        return hours;

    }

    public void ShowBottomConfirmDialog(String msg, boolean single, final int type) {

        final Dialog mdialog = new Dialog(getActivity(), R.style.ActionSheetDialogStyle);
        View view = LayoutInflater.from(getActivity()).inflate(
                R.layout.dialoag_confirm, null);
        view.setMinimumWidth(10000);
        mdialog.setContentView(view);
        Window dialogWindow = mdialog.getWindow();
        assert dialogWindow != null;
        dialogWindow.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.x = 0;
        lp.y = 0;
        lp.height = metrics.heightPixels / 2;
        lp.width = metrics.widthPixels;
        dialogWindow.setAttributes(lp);

        TextView dismiss=(TextView)mdialog.findViewById(R.id.dismiss);
        TextView confirm=(TextView)mdialog.findViewById(R.id.confirm);
        TextView msgtxt=(TextView)mdialog.findViewById(R.id.message);

        if(single)
        {
            dismiss.setVisibility(View.GONE);
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

                switch (type)
                {
                    case 0:

                        break;

                    case 1:
                        showToast("資料已送出");
                        break;

                    case 2:
                        showToast("兌換成功!");
                        break;
                }


                mdialog.dismiss();


            }
        });


        mdialog.show();

    }

    public void show_Progress_Dialog(String title, String message){
        if(progressDialog != null && progressDialog.isShowing()){
            progressDialog.setTitle(title);
            progressDialog.setMessage(message);
        }else {
            dismiss_Progress_Dialog();
            progressDialog = ProgressDialog.show(getActivity(), title, message, true, false);
        }
    }

    public void dismiss_Progress_Dialog(){
        if(progressDialog != null) progressDialog.dismiss();
    }


}
