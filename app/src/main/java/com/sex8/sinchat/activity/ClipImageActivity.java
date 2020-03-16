package com.sex8.sinchat.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.sex8.sinchat.IMMessageApplication;
import com.sex8.sinchat.R;
import com.sex8.sinchat.utils.PhotoUtuls;
import com.sex8.sinchat.view.ClipImageLayout;

import java.io.File;
import java.util.Locale;

public class ClipImageActivity extends BaseActivity {
    private TextView doneText;
    private ClipImageLayout clipImageLayout = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clip);

        findViews();
        init();
        setListeners();
    }

    @Override
    public void onBackPressed() {
        backActivity(false);
    }

    @Override
    public void findViews() {
        doneText = findViewById(R.id.doneText);
        clipImageLayout = findViewById(R.id.clipImageLayout);
    }

    @Override
    public void init() {
        doneText.setVisibility(View.VISIBLE);
        doneText.setText(R.string.done);
        ((TextView) findViewById(R.id.titleText)).setText(R.string.move_image_title);
        String path = getIntent().getStringExtra("path");
// 有的系统返回的图片是旋转了，有的没有旋转，所以处理
        int degreee = PhotoUtuls.getOrientationDegrees(path);
        Bitmap bitmap = PhotoUtuls.createBitmap(path);
        if (bitmap != null) {
            if (degreee == 0) {
                clipImageLayout.setImageBitmap(bitmap);
            } else {
                clipImageLayout.setImageBitmap(PhotoUtuls.rotated(bitmap, degreee));
            }
        } else {
            backActivity(false);
        }
    }

    @Override
    public void setListeners() {
        findViewById(R.id.back).setOnClickListener(btnClick);
        doneText.setOnClickListener(btnClick);
    }

    private View.OnClickListener btnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.back:
                    backActivity(false);
                    break;
                case R.id.doneText:
                    doneText.setOnClickListener(null);
                    backActivity(true);
                    break;
            }
        }
    };

    private void backActivity(boolean done){
        if(done){
            Bitmap bitmap = clipImageLayout.clip();
            if (!IMMessageApplication.cacheDir.exists())
                IMMessageApplication.cacheDir.mkdirs();
            IMMessageApplication.tmpFile = new File(IMMessageApplication.cacheDir, String.format(Locale.getDefault(), "%d.jpg", System.currentTimeMillis()));
            PhotoUtuls.saveBitmap(bitmap, IMMessageApplication.tmpFile);
            Intent intent = new Intent();
            intent.putExtra("path", IMMessageApplication.tmpFile.getPath());
            setResult(RESULT_OK, intent);
        }
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        finish();
    }
}
