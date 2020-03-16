package com.sex8.sinchat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.sex8.sinchat.IMMessageApplication;
import com.sex8.sinchat.R;
import com.sex8.sinchat.adapter.ImageAdapter;
import com.sex8.sinchat.entity.PhotoData;
import com.sex8.sinchat.event.SelectImageEvent;
import com.sex8.sinchat.tools.SingleMediaScanner;
import com.sex8.sinchat.utils.PhotoUtuls;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PostNewActivity extends BaseActivity implements View.OnClickListener {


    @BindView(R.id.photo_picker)
    ImageButton photoPicker;
    @BindView(R.id.photo_layout)
    LinearLayout photoLayout;
    @BindView(R.id.content_layout)
    RelativeLayout contentLayout;
    @BindView(R.id.grid_view)
    GridView gridView;
    List<PhotoData> photoData = new ArrayList<>();
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.post_out)
    TextView postOut;

    private int minus = 9;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_news);
        ButterKnife.bind(this);
        setListeners();
    }

    @Override
    public void findViews() {

    }

    @Override
    public void init() {


    }

    @Override
    public void setListeners() {
        photoPicker.setOnClickListener(this);
        contentLayout.setOnClickListener(this);
        back.setOnClickListener(this);
        postOut.setOnClickListener(this);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (photoData.size() > 9) {
                    showToast(getResources().getString(R.string.over_limit));
                    return;
                }
                ShowBottomPhotoPickerDialog(minus);

            }
        });

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.photo_picker:
                ShowBottomPhotoPickerDialog(minus);
                break;

            case R.id.back:
                ShowBottomConfirmDialog(getResources().getString(R.string.post_cancel),false,0);
                break;

            case R.id.post_out:
                ShowBottomConfirmDialog(getResources().getString(R.string.post_confirm),false,1);
                break;

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK
                && (requestCode == PhotoUtuls.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE
                || requestCode == PhotoUtuls.RECORD_VIDEO_ACTIVITY_REQUEST_CODE)) {
            new SingleMediaScanner(this, IMMessageApplication.tmpFile);
            List<PhotoData> selectList = new ArrayList<>();
            selectList.add(new PhotoData(IMMessageApplication.tmpFile));
            EventBus.getDefault().post(new SelectImageEvent(selectList));
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    synchronized public void onSelectImageEvent(SelectImageEvent event) {
        if (event.getSelectList() != null && event.getSelectList().size() > 0) {

            addPhoto(event.getSelectList());

        }

    }

    public void addPhoto(List<PhotoData> photoDatalist) {
        photoPicker.setVisibility(View.GONE);
         minus-= photoDatalist.size();


        gridView.setVisibility(View.VISIBLE);

        if (minus < 9) {
            if (photoData.size() > 0)
                photoData.remove(photoData.size()-1);

            for (int i = 0; i < photoDatalist.size(); i++) {
                photoData.add(photoDatalist.get(i));
            }

            if(minus>0)
            {
                PhotoData plus = new PhotoData();
                plus.isLocal = true;
                plus.fileName = "local";
                photoData.add(plus);
            }

        }

        ImageAdapter adapter = new ImageAdapter(getBaseContext(), photoData);
        gridView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

    private int getPixelsFromDp(int size) {

        DisplayMetrics metrics = new DisplayMetrics();

        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        return (size * metrics.densityDpi) / DisplayMetrics.DENSITY_DEFAULT;

    }


}
