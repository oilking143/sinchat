package com.sex8.sinchat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sex8.sinchat.R;
import com.sex8.sinchat.tools.OnClickLimitListener;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ExchangeActivity extends BaseActivity implements View.OnClickListener{

    int[] iconId = new int[]{R.drawable.video_vip, R.drawable.book_vip,
            R.drawable.radio_vip, R.drawable.show_diamond,};
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.back)
    ImageView back;

    private ChangeAdapter adapter;

    private LinearLayoutManager linearLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange_layout);
        ButterKnife.bind(this);
        init();
        setListeners();
    }

    @Override
    public void findViews() {

    }

    @Override
    public void init() {

        adapter = new ChangeAdapter();
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void setListeners() {
        back.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        backMain();
    }

    private void backMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.back:
                backMain();
                break;
        }
    }

    private class ChangeAdapter extends RecyclerView.Adapter {
        private View.OnClickListener itemClick;

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View convertView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_exchange, viewGroup, false);
            return new ViewHolder(convertView);
        }

        @Override
        public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, final int i) {
            final ViewHolder vh = (ViewHolder) viewHolder;
            vh.daysText.setText("90天");
            vh.shinCoin.setText("20000");
            vh.iconImage.setImageResource(iconId[viewHolder.getAdapterPosition()]);
            vh.layout.setOnClickListener(new OnClickLimitListener() {
                @Override
                public void onClickLimit(View v) {

                    ShowBottomConfirmDialog(getResources().getString(R.string.exchange_success), false, 2);
                }
            });
        }

        @Override
        public int getItemCount() {
            return iconId.length;
        }

        public void setItemClick(View.OnClickListener itemClick) {
            this.itemClick = itemClick;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private ImageView iconImage;
            private TextView daysText;
            private TextView shinCoin;
            private ConstraintLayout layout;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                iconImage = itemView.findViewById(R.id.iconImage);
                daysText = itemView.findViewById(R.id.days_txt);
                shinCoin = itemView.findViewById(R.id.shin_coin);
                layout = itemView.findViewById(R.id.item_layout);

            }
        }

    }


}
