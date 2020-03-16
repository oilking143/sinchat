package com.sex8.sinchat.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.sex8.sinchat.BuildConfig;
import com.sex8.sinchat.IMMessageApplication;
import com.sex8.sinchat.R;
import com.sex8.sinchat.dialog.ConfirmDialog;
import com.sex8.sinchat.dialog.TipDialog;
import com.sex8.sinchat.event.DisconnectEvent;
import com.sex8.sinchat.event.ReloginEvent;
import com.sex8.sinchat.model.IMData;
import com.sex8.sinchat.utils.PhotoUtuls;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.Locale;

public abstract class BaseActivity extends AppCompatActivity {
    public Handler handler;
    public TipDialog tipDialog;
    public ConfirmDialog confirmDialog;
    public ProgressDialog progressDialog;
    public abstract void findViews();
    public abstract void init();
    public abstract void setListeners();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        handler = new Handler();
        setStatusBarColor();
        //防截屏
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        EventBus.getDefault().register(this);
        IMMessageApplication.application.mFirebaseAnalytics.setCurrentScreen(this, this.getClass().getSimpleName(),null);
        netChecker();
    }

    @Override
    protected void onResume() {
        super.onResume();
        IMMessageApplication.activityResumed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        IMMessageApplication.activityPaused();
    }

    @Override
    protected void onDestroy() {
        dismissTipDialog();
        dismissConfirmDialog();
        dismiss_Progress_Dialog();
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }




    public void showTipDialog(String title){
        if(tipDialog == null){
            tipDialog = new TipDialog(this);
        }
        tipDialog.show(title);
    }

    public void dismissTipDialog(){
        if(tipDialog != null)
            tipDialog.dismiss();
    }

    public void showConfirmDialog(String title, Object event){
        if(confirmDialog == null){
            confirmDialog = new ConfirmDialog(this);
        }
        confirmDialog.show(title, event);
    }

    public void dismissConfirmDialog(){
        if(confirmDialog != null)
            confirmDialog.dismiss();
    }

    public void show_Progress_Dialog(String title, String message){
        if(progressDialog != null && progressDialog.isShowing()){
            progressDialog.setTitle(title);
            progressDialog.setMessage(message);
        }else {
            dismiss_Progress_Dialog();
            progressDialog = ProgressDialog.show(this, title, message, true, false);
        }
    }

    public void dismiss_Progress_Dialog(){
        if(progressDialog != null) progressDialog.dismiss();
    }

    public void setStatusBarColor(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getColor(R.color.colorPrimaryDark));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    public void setNavigationBarColor(int color){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //設置虛擬建背景色 (Api 21 5.0)
            getWindow().setNavigationBarColor(color);
        }
    }

    public void ShowBottomRegisterDialog() {

        Dialog mdialog = new Dialog(this, R.style.ActionSheetDialogStyle);
        View view = LayoutInflater.from(BaseActivity.this).inflate(
                R.layout.dialog_sheet, null);
        view.setMinimumWidth(10000);
        mdialog.setContentView(view);
        Window dialogWindow = mdialog.getWindow();
        assert dialogWindow != null;
        dialogWindow.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.x = 0;
        lp.y = 0;
        lp.height = metrics.heightPixels / 2;
        lp.width = metrics.widthPixels;
        dialogWindow.setAttributes(lp);
        mdialog.show();

    }

    public void ShowBottomConfirmDialog(String msg, boolean single, final int type) {

        final Dialog mdialog = new Dialog(this, R.style.ActionSheetDialogStyle);
        View view = LayoutInflater.from(BaseActivity.this).inflate(
                R.layout.dialoag_confirm, null);
        view.setMinimumWidth(10000);
        mdialog.setContentView(view);
        Window dialogWindow = mdialog.getWindow();
        assert dialogWindow != null;
        dialogWindow.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
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
                finish();

            }
        });


        mdialog.show();

    }

    public void ShowBottomPhotoPickerDialog(final int minus) {

        final Dialog mdialog = new Dialog(this, R.style.ActionSheetDialogStyle);
        View view = LayoutInflater.from(BaseActivity.this).inflate(
                R.layout.dialog_photo_picker, null);
        view.setMinimumWidth(10000);
        mdialog.setContentView(view);
        Window dialogWindow = mdialog.getWindow();
        assert dialogWindow != null;
        dialogWindow.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.x = 0;
        lp.y = 0;
        lp.height = metrics.heightPixels / 2;
        lp.width = metrics.widthPixels;
        dialogWindow.setAttributes(lp);

        ImageView takePic=(ImageView)mdialog.findViewById(R.id.take_picture);
        ImageView takePhoto=(ImageView)mdialog.findViewById(R.id.take_photo);
        takePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(BaseActivity.this, SelectImageActivity.class);
                it.putExtra("singleSelect", false);
                it.putExtra("canSelectCount", minus);
                it.putExtra("Request_Code", SelectImageActivity.Request_Code_Select_Photo);
                startActivityForResult(it, it.getIntExtra("Request_Code", 0));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                mdialog.dismiss();
            }
        });

        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!IMMessageApplication.cacheDir.exists())
                    IMMessageApplication.cacheDir.mkdirs();
                Intent intent = new Intent (MediaStore.ACTION_IMAGE_CAPTURE);
                IMMessageApplication.tmpFile = new File(IMMessageApplication.cacheDir, String.format(Locale.getDefault(), "%d.jpg", System.currentTimeMillis()));
                intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(BaseActivity.this, BuildConfig.APPLICATION_ID + ".fileprovider", IMMessageApplication.tmpFile));
                startActivityForResult(intent, PhotoUtuls.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                mdialog.dismiss();
            }
        });


        mdialog.show();

    }

    public void showToast(String msg)
    {
        Toast.makeText(BaseActivity.this,msg,Toast.LENGTH_LONG).show();

    }

    protected void backResultMain(Context context,int Fragpos) {
        Intent intent = new Intent(context, MainActivity.class);
        startActivityForResult(intent, intent.getIntExtra("Request_Code", Fragpos));
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        finish();
    }

    protected void backMain(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        finish();
    }

    protected void netChecker() {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();

        //如果未連線的話，mNetworkInfo會等於null
        if (mNetworkInfo != null) {
            //網路是否已連線(true or false)
            mNetworkInfo.isConnected();
            //網路連線方式名稱(WIFI or mobile)
            mNetworkInfo.getTypeName();
            //網路連線狀態
            mNetworkInfo.getState();
            //網路是否可使用
            mNetworkInfo.isAvailable();
            //網路是否已連接or連線中
            mNetworkInfo.isConnectedOrConnecting();
            //網路是否故障有問題
            mNetworkInfo.isFailover();
            //網路是否在漫遊模式
            mNetworkInfo.isRoaming();
        }

        if (mNetworkInfo == null) {
            EventBus.getDefault().post(new DisconnectEvent(1, getResources().getString(R.string.net_problem)));
        } else if (!mNetworkInfo.isConnected()) {
            EventBus.getDefault().post(new DisconnectEvent(1, getResources().getString(R.string.net_problem)));
        } else if (mNetworkInfo != null && !mNetworkInfo.isConnectedOrConnecting()) {
            EventBus.getDefault().post(new DisconnectEvent(1, getResources().getString(R.string.net_problem)));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    synchronized public void onReloginEvent(ReloginEvent event) {
        IMMessageApplication.showDialog(this, R.string.dialog_title_relogin, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(BaseActivity.this, Launch_Activity_new.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    synchronized public void onDisconnect(DisconnectEvent event) {

        Log.d("IMMessageApplication",""+IMMessageApplication.isActivityVisible());

        if (IMMessageApplication.isActivityVisible()) {
            if (event.getStatus() == 1) {
                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.custom_toast_layout,
                        null);
                layout.setLayoutParams(new LinearLayout.LayoutParams(
                        500, 50));
                Toast toast = new Toast(getApplicationContext());
                toast.setGravity(Gravity.TOP, 0, 0);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(layout);
                toast.show();
                show_Progress_Dialog(null, getString(R.string.dialog_refreshing));
            } else if(event.getStatus()==0) {
                dismiss_Progress_Dialog();
            }
        }

    }
}
