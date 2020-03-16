package com.sex8.sinchat.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.sex8.sinchat.IMMessageApplication;
import com.sex8.sinchat.R;
import com.sex8.sinchat.decoration.CirclePagerIndicatorDecoration;
import com.sex8.sinchat.entity.Message;
import com.sex8.sinchat.event.ChatCommentSendEvent;
import com.sex8.sinchat.event.ChatReportShowEvent;
import com.sex8.sinchat.event.ChatRewardShowEvent;
import com.sex8.sinchat.model.dataBase.Entity.Chatroom_History;
import com.sex8.sinchat.tools.OnClickLimitListener;

import org.greenrobot.eventbus.EventBus;

public class CommentPopupWindow extends PopupWindow {
    private Activity context;
    private Chatroom_History chat;
    private RecyclerView commentRecyclerView;
    private CommentAdapter commentAdapter;

    public CommentPopupWindow(Context context){
        super(context);
        this.context = (Activity) context;
        initPopupWindow();
    }

    private void initPopupWindow(){
        View contentView = LayoutInflater.from(context).inflate(R.layout.popup_comment, null, false);
        setContentView(contentView);
        setHeight(context.getResources().getDimensionPixelOffset(R.dimen.popup_comment_height));
        setFocusable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

        commentRecyclerView = contentView.findViewById(R.id.commentRecyclerView);
        commentRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(commentRecyclerView);
        commentAdapter = new CommentAdapter();
        commentRecyclerView.setAdapter(commentAdapter);
        CirclePagerIndicatorDecoration circlePagerIndicatorDecoration = new CirclePagerIndicatorDecoration();

        circlePagerIndicatorDecoration.setColorActive(ContextCompat.getColor(context, R.color.comment_indicator_active));
        circlePagerIndicatorDecoration.setColorInactive(ContextCompat.getColor(context, R.color.comment_indicator_inactive));
        circlePagerIndicatorDecoration.setIndicatorHeight(context.getResources().getDimensionPixelOffset(R.dimen.popup_comment_indicator_height));
        circlePagerIndicatorDecoration.setmIndicatorStrokeWidth(context.getResources().getDimensionPixelOffset(R.dimen.popup_comment_stroke_width));
        commentRecyclerView.addItemDecoration(circlePagerIndicatorDecoration);
        contentView.findViewById(R.id.rewardText).setOnClickListener(btnClick);
        contentView.findViewById(R.id.reportText).setOnClickListener(btnClick);
    }

    public void show(View anchor, @NonNull Chatroom_History chat){
        this.chat = chat;
        int[] location = new int[2];
        anchor.getLocationOnScreen(location);
        showAtLocation(anchor, 0,
                (location[0] + anchor.getWidth() / 2) - getContentView().getMeasuredWidth() / 2,
                location[1] - getHeight() - IMMessageApplication.dipToPixels(context, 5f));
    }

    private OnClickLimitListener btnClick = new OnClickLimitListener() {
        @Override
        public void onClickLimit(View v) {
            dismiss();
            switch (v.getId()){
                case R.id.rewardText:
                    EventBus.getDefault().post(new ChatRewardShowEvent(chat));
                    break;
                case R.id.reportText:
                    EventBus.getDefault().post(new ChatReportShowEvent(chat));
                    break;
                default:
                    EventBus.getDefault().post(new ChatCommentSendEvent(chat, v.getId()));
                    break;
            }
        }
    };

    private class CommentAdapter extends RecyclerView.Adapter{
        TypedArray comment_img = context.getResources().obtainTypedArray(R.array.comment_img);
        int[] comment_type = context.getResources().getIntArray(R.array.comment_type);
        int imgSize = context.getResources().getDimensionPixelSize(R.dimen.popup_comment_img_size);
        int imgMargin = (context.getResources().getDimensionPixelSize(R.dimen.popup_comment_width) - imgSize * 4) / 5;

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LinearLayout convertView = new LinearLayout(context);
            convertView.setOrientation(LinearLayout.HORIZONTAL);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            convertView.setLayoutParams(params);
            convertView.setPadding(imgMargin, 0, imgMargin, 0);
            return new ViewHolder(convertView);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            ViewHolder vh = (ViewHolder) viewHolder;
            LinearLayout convertView = (LinearLayout) vh.itemView;
            int pos = viewHolder.getAdapterPosition();
            int maxIndex = (pos * 4) + 4;
            if(maxIndex > comment_img.length())
                maxIndex = comment_img.length();

            for(int index=(pos * 4);index<maxIndex;index++){
                int linearIndex = index % 4;
                ImageView imageView = (ImageView) convertView.getChildAt(linearIndex);
                if(imageView == null){
                    imageView = new ImageView(context);
                    imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(imgSize, imgSize);
                    if(linearIndex != 0){
                        params.leftMargin = imgMargin;
                    }
                    imageView.setLayoutParams(params);
                    imageView.setOnClickListener(btnClick);
                    convertView.addView(imageView);
                }
                imageView.setImageDrawable(comment_img.getDrawable(index));
                imageView.setId(comment_type[index]);
            }
        }

        @Override
        public int getItemCount() {
            return comment_img.length() % 4 == 0 ? comment_img.length() / 4 : comment_img.length() / 4 + 1;
        }

        class ViewHolder extends RecyclerView.ViewHolder{

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
            }
        }
    }
}
