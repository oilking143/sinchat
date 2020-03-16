package com.sex8.sinchat.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.sex8.sinchat.IMMessageApplication;
import com.sex8.sinchat.R;
import com.sex8.sinchat.entity.UserInfo;
import com.sex8.sinchat.event.SelectImageEvent;
import com.sex8.sinchat.model.IMData;
import com.sex8.sinchat.tools.OnClickLimitListener;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class EditProfileActivity extends BaseActivity {

    UserInfo mUserInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        findViews();
        init();
        setListeners();
    }

    @Override
    public void onBackPressed() {
        backMain(EditProfileActivity.this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(IMMessageApplication.checkPermissionsResult(this, permissions, grantResults)){
            if(requestCode == IMMessageApplication.Request_Permission_Read){
                goSelectImage();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode){
                case IMMessageApplication.Request_Crop_Image:
                    String path = data.getStringExtra("path");
                    Glide.with(this)
                            .load(path)
                            .circleCrop()
                            .into(((ImageView) findViewById(R.id.avatarImage)));

                    mUserInfo.setImageUrl(path);
                    IMData.getInstance().setmUserInfo(mUserInfo);

                    break;
                case IMMessageApplication.Request_Edit_Nick:
                    String nickName = data.getStringExtra("nickName");
                    ((TextView) findViewById(R.id.nameText)).setText(nickName);
                    break;
            }
        }
    }

    @Override
    public void findViews() {

    }

    @Override
    public void init() {
        mUserInfo=IMData.getInstance().getUserInfo();
        ((TextView) findViewById(R.id.titleText)).setText(R.string.edit_profile);
        ((TextView) findViewById(R.id.nameText)).setText(IMData.getInstance().getUserInfo().getNickName());
        Glide.with(this)
                .load(mUserInfo.getImageUrl())
                .circleCrop()
                .into(((ImageView) findViewById(R.id.avatarImage)));
    }

    @Override
    public void setListeners() {
        findViewById(R.id.back).setOnClickListener(btnClick);
        findViewById(R.id.avatarLayout).setOnClickListener(btnClick);
        findViewById(R.id.nameLayout).setOnClickListener(btnClick);
        findViewById(R.id.qrCodeLayout).setOnClickListener(btnClick);
    }

    private OnClickLimitListener btnClick = new OnClickLimitListener() {
        @Override
        public void onClickLimit(View v) {
            switch(v.getId()){
                case R.id.back:
                    backMain(EditProfileActivity.this);
                    break;
                case R.id.avatarLayout:
                    goSelectImage();
                    break;
                case R.id.nameLayout:
                    goEditNick();
                    break;
                case R.id.qrCodeLayout:
                    goQrCode();
                    break;
            }
        }
    };

    private void goSelectImage(){
        //檢查權限
        if(IMMessageApplication.checkPermission(this, IMMessageApplication.Request_Permission_Read, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Intent intent = new Intent(this, SelectImageActivity.class);
            intent.putExtra("singleSelect", true);
            intent.putExtra("Request_Code", SelectImageActivity.Request_Code_Select_Photo);
            startActivityForResult(intent, intent.getIntExtra("Request_Code", 0));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }

    private void goCropImage(String path){
        Intent intent = new Intent(this, ClipImageActivity.class);
        intent.putExtra("path", path);
        startActivityForResult(intent, IMMessageApplication.Request_Crop_Image);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void goEditNick(){
        Intent intent = new Intent(this, EditNickNameActivity.class);
        startActivityForResult(intent, IMMessageApplication.Request_Edit_Nick);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void goQrCode(){
        Intent intent = new Intent(this, QrCodeActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    synchronized public void onSelectImageEvent(SelectImageEvent event) {
        if(event.getSelectList() != null && event.getSelectList().size() > 0){
            goCropImage(event.getSelectList().get(0).path);
        }
    }
}
