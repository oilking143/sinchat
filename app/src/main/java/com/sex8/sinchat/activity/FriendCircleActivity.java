package com.sex8.sinchat.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
 import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sex8.sinchat.R;
import com.sex8.sinchat.decoration.LineDividerItemDecoration;
import com.sex8.sinchat.entity.FriendCirle;
import com.sex8.sinchat.tools.OnClickLimitListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FriendCircleActivity extends BaseActivity {
    @BindView(R.id.post_news)
    Button postNews;
    private int stateBarHeight;
    private TextView titleText;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private FriendAdapter friendAdapter;
    private List<FriendCirle> dateList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_circle);
        ButterKnife.bind(this);

        findViews();
        init();
        setListeners();

        dateList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            FriendCirle friendCirle = new FriendCirle();
            friendCirle.iconUrl = R.mipmap.ic_launcher;
            friendCirle.name = "測試" + i;
            friendCirle.content = "好無聊好無聊好無聊好無聊好無聊好無聊好無聊好無聊好無聊好無聊好無聊好無聊好無聊好無聊好無聊好無聊好無聊好無聊好無聊" + i;
            friendCirle.time = "剛剛";
            dateList.add(friendCirle);
        }
        friendAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        backMain();
    }

    @Override
    public void findViews() {
        recyclerView = findViewById(R.id.recyclerView);
        titleText = findViewById(R.id.titleText);
    }

    @Override
    public void init() {
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        int resourceId = getApplicationContext().getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            stateBarHeight = getApplicationContext().getResources().getDimensionPixelSize(resourceId);
        }

        friendAdapter = new FriendAdapter();
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setPadding(0, 0, 0, 0);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(friendAdapter);
        recyclerView.addItemDecoration(new LineDividerItemDecoration(this));
        postNews.setOnClickListener(btnClick);
    }

    @Override
    public void setListeners() {
        findViewById(R.id.back).setOnClickListener(btnClick);


        friendAdapter.setItemClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.moreImage) {

                } else {

                }
            }
        });
    }

    private OnClickLimitListener btnClick = new OnClickLimitListener() {
        @Override
        public void onClickLimit(View v) {
            switch (v.getId()) {
                case R.id.back:
                    backMain();
                    break;
                case R.id.post_news:
                    goPost();
                    break;
            }
        }
    };

    private class FriendAdapter extends RecyclerView.Adapter {
        private View.OnClickListener itemClick;

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View convertView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_friend_circle, viewGroup, false);
            return new ViewHolder(convertView);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            ViewHolder vh = (ViewHolder) viewHolder;
            FriendCirle friendCirle = dateList.get(viewHolder.getAdapterPosition());
            Glide.with(FriendCircleActivity.this)
                    .load(friendCirle.iconUrl)
                    .circleCrop()
                    .into(vh.iconImage);
            vh.nameText.setText(friendCirle.name);
            vh.contentText.setText(friendCirle.content);
            vh.timeText.setText(friendCirle.time);
            vh.moreImage.setOnClickListener(itemClick);
            vh.itemView.setOnClickListener(itemClick);
        }

        @Override
        public int getItemCount() {
            return dateList != null ? dateList.size() : 0;
        }

        public void setItemClick(View.OnClickListener itemClick) {
            this.itemClick = itemClick;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private ImageView iconImage;
            private ImageView moreImage;
            private TextView nameText;
            private TextView contentText;
            private TextView timeText;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                iconImage = itemView.findViewById(R.id.iconImage);
                nameText = itemView.findViewById(R.id.nameText);
                contentText = itemView.findViewById(R.id.contentText);
                timeText = itemView.findViewById(R.id.timeText);
                moreImage = itemView.findViewById(R.id.moreImage);
            }
        }
    }

    private void backMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        finish();
    }

    private void goPost() {
        Intent intent = new Intent(this, PostNewActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }
}
