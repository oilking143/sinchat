package com.sex8.sinchat.adapter;

import android.content.Context;
import android.content.res.TypedArray;
 import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sex8.sinchat.R;

import java.util.ArrayList;
import java.util.List;

public class MessageCommentAdapter extends RecyclerView.Adapter {
    private Context context;
    private SparseIntArray commentArray;
    private TypedArray comment_img;
    private List<Integer> comment_type;
    public MessageCommentAdapter(Context context){
        this.context = context;
        comment_img = context.getResources().obtainTypedArray(R.array.comment_img);
        comment_type = new ArrayList<>();
        for(int i: context.getResources().getIntArray(R.array.comment_type)){
            comment_type.add(i);
        }
    }

    public void updateComment(SparseIntArray commentArray){
        this.commentArray = commentArray;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View convertView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_message_comment, viewGroup, false);
        return new ViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ViewHolder vh = (ViewHolder) viewHolder;
        int type = commentArray.keyAt(viewHolder.getAdapterPosition());
        int count = commentArray.get(type, 0);
        vh.commentImage.setImageDrawable(comment_img.getDrawable(comment_type.indexOf(type)));
        if(count > 1){
            vh.commentNumText.setVisibility(View.VISIBLE);
            vh.commentNumText.setText(String.valueOf(count));
        }else{
            vh.commentNumText.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return commentArray != null ? commentArray.size() : 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView commentImage;
        TextView commentNumText;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            commentImage = itemView.findViewById(R.id.commentImage);
            commentNumText = itemView.findViewById(R.id.commentNumText);
        }
    }
}
