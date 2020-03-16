package com.sex8.sinchat.activity;

import android.content.Intent;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.github.chrisbanes.photoview.OnMatrixChangedListener;
import com.github.chrisbanes.photoview.OnPhotoTapListener;
import com.github.chrisbanes.photoview.OnSingleFlingListener;
import com.github.chrisbanes.photoview.PhotoView;
import com.github.chrisbanes.photoview.PhotoViewAttacher;
import com.sex8.sinchat.R;
import com.sex8.sinchat.model.IMData;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PhotoViewerActivity extends BaseActivity {
    static final String PHOTO_TAP_TOAST_STRING = "Photo Tap! X: %.2f %% Y:%.2f %% ID: %d";
    static final String SCALE_TOAST_STRING = "Scaled to: %.2ff";
    static final String FLING_LOG_STRING = "Fling velocityX: %.2f, velocityY: %.2f";

    @BindView(R.id.iv_photo)
    PhotoView ivPhoto;
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.more)
    ImageView more;
    @BindView(R.id.titleText)
    TextView titleText;
    private PhotoViewAttacher mAttacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photoview_layout);
        ButterKnife.bind(this);
        init();
        findViews();
        setListeners();
    }

    @Override
    public void findViews() {

        titleText.setText("");
        more.setVisibility(View.GONE);

    }

    @Override
    public void init() {
        Intent intent = getIntent();
        String fileneme = intent.getStringExtra("file");
        mAttacher = new PhotoViewAttacher(ivPhoto);
        Log.d("filename", fileneme);


        if(fileneme.contains(".gif"))
        {
            Glide.with(this).asGif().load(fileneme).fitCenter().into(ivPhoto);
        }
        else {
            Glide.with(this).load(fileneme).fitCenter().into(new DrawableImageViewTarget(ivPhoto) {

                @Override
                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                    super.onResourceReady(resource, transition);
                    mAttacher.update();
                }
            });
        }


        ivPhoto.setOnMatrixChangeListener(new MatrixChangeListener());
        ivPhoto.setOnPhotoTapListener(new PhotoTapListener());
        ivPhoto.setOnSingleFlingListener(new SingleFlingListener());


    }

    @Override
    public void setListeners() {

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

            }
        });
    }


    private class PhotoTapListener implements OnPhotoTapListener {

        @Override
        public void onPhotoTap(ImageView view, float x, float y) {
            float xPercentage = x * 100f;
            float yPercentage = y * 100f;

            showToast(String.format(PHOTO_TAP_TOAST_STRING, xPercentage, yPercentage, view == null ? 0 : view.getId()));
        }
    }

    private class MatrixChangeListener implements OnMatrixChangedListener {

        @Override
        public void onMatrixChanged(RectF rect) {

        }
    }

    private class SingleFlingListener implements OnSingleFlingListener {

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Log.d("PhotoView", String.format(FLING_LOG_STRING, velocityX, velocityY));
            return true;
        }
    }
}
