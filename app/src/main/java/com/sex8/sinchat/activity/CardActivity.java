package com.sex8.sinchat.activity;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sex8.sinchat.R;
import com.sex8.sinchat.tools.OnClickLimitListener;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CardActivity extends BaseActivity implements View.OnClickListener {


    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    CardAdapter adapter=new CardAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_cardlayout);
        ButterKnife.bind(this);
        setListeners();
        init();

    }

    @Override
    public void findViews() {

    }

    @Override
    public void init() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void setListeners() {
        back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.back:
                backMain(this);
                break;
        }

    }

    private class CardAdapter extends RecyclerView.Adapter {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View convertView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_recycle_layout, viewGroup, false);
            return new ViewHolder(convertView);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            final ViewHolder vh = (ViewHolder) viewHolder;

            vh.btn_change.setOnClickListener(new OnClickLimitListener() {
                @Override
                public void onClickLimit(View v) {
                    vh.datetxt.setTextColor(getResources().getColor(R.color.text_gray));
                    vh.datetxt.setText(getResources().getString(R.string.exchange_overtime));
                    vh.textView12.setText("");
                    vh.textView3.setText("");
                    vh.layout.setBackgroundColor(getResources().getColor(R.color.text_hint_color));
                    vh.textView5.setTextColor(getResources().getColor(R.color.pure_white));
                    vh.textView10.setTextColor(getResources().getColor(R.color.pure_white));
                    vh.textView12.setTextColor(getResources().getColor(R.color.text_gray));
                }
            });
        }

        @Override
        public int getItemCount() {
            return 3;
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            ImageView imageView2;
            TextView textView5;
            TextView textView10;
            TextView textView3;
            TextView datetxt;
            TextView textView12;
            TextView btn_change;
            ConstraintLayout layout;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                 imageView2=itemView.findViewById(R.id.imageView2);
                 textView5=itemView.findViewById(R.id.textView5);
                 textView10=itemView.findViewById(R.id.textView10);
                textView3=itemView.findViewById(R.id.textView3);
                datetxt=itemView.findViewById(R.id.date_txt);
                 textView12=itemView.findViewById(R.id.textView12);
                btn_change=itemView.findViewById(R.id.btn_change);
                layout=itemView.findViewById(R.id.constraintLayout);

            }
        }
    }

}
