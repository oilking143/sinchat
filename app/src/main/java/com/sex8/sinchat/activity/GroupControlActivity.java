package com.sex8.sinchat.activity;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sex8.sinchat.R;
import com.sex8.sinchat.decoration.ItemDecorationAlbumColumns;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GroupControlActivity extends BaseActivity {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private GridLayoutManager layoutManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_groupecontrol);
        ButterKnife.bind(this);
        init();
    }

    @Override
    public void findViews() {

    }

    @Override
    public void init() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        int padding = getResources().getDimensionPixelSize(R.dimen.party_list_padding);
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        layoutManager = new GridLayoutManager(this, 2, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new ItemDecorationAlbumColumns(padding, layoutManager.getSpanCount()));

        GroupControlAdapter adapter=new GroupControlAdapter();
        recyclerView.setAdapter(adapter);
    }


    @Override
    public void setListeners() {

    }


    private class GroupControlAdapter extends RecyclerView.Adapter{

        private View.OnClickListener itemClick;
        private int[] bgColor = new int[]{
                ContextCompat.getColor(GroupControlActivity.this, R.color.party_list_item_color_1),
                ContextCompat.getColor(GroupControlActivity.this, R.color.party_list_item_color_2),
                ContextCompat.getColor(GroupControlActivity.this, R.color.party_list_item_color_3),
                ContextCompat.getColor(GroupControlActivity.this, R.color.party_list_item_color_4)
        };

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View convertView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_party_list, viewGroup, false);
            RippleDrawable rippleDrawable = (RippleDrawable) convertView.getBackground();
            ((GradientDrawable) rippleDrawable.getDrawable(1)).setColor(Color.BLACK);
            return new ViewHolder(convertView);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            ViewHolder vh = (ViewHolder) viewHolder;

            ((GradientDrawable)((RippleDrawable) vh.itemView.getBackground()).getDrawable(1)).setColor(bgColor[viewHolder.getAdapterPosition() % bgColor.length]);
            vh.itemView.setOnClickListener(itemClick);

            vh.onlineText.setText("(" + "99" + "人)");
            vh.titleText.setText("testtt");

            Glide.with(GroupControlActivity.this)
                    .load(R.mipmap.ic_launcher)
                    .circleCrop()
                    .into(vh.imageView);
        }

        @Override
        public int getItemCount() {
            return 30;
        }

        class ViewHolder extends RecyclerView.ViewHolder{
            public ImageView imageView;
            public TextView onlineText;
            public TextView titleText;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                itemView.setTag(this);
                imageView = itemView.findViewById(R.id.imageView);
                onlineText = itemView.findViewById(R.id.onlineText);
                titleText = itemView.findViewById(R.id.titleText);
            }
        }

    }

}
