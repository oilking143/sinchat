package com.sex8.sinchat.view;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sex8.sinchat.BuildConfig;
import com.sex8.sinchat.IMMessageApplication;
import com.sex8.sinchat.R;
import com.sex8.sinchat.activity.SelectImageActivity;
import com.sex8.sinchat.tools.OnClickLimitListener;
import com.sex8.sinchat.utils.PhotoUtuls;

import java.io.File;
import java.util.Locale;

public class ChatBottomAddMoreView extends RecyclerView {
    private ChatBottomSheet chatBottomSheet;
    private Activity activity;
    private int[] icResIds = new int[]{
            R.drawable.ic_photo,
            R.drawable.ic_take_a_photo,
            R.drawable.ic_qa,
            R.drawable.ic_exchange,
            R.drawable.ic_minutes,
            R.drawable.ic_share_task,
            R.drawable.ic_talk_task,
            R.drawable.ic_send_photo
    };
    private int[] textResIds = new int[]{
            R.string.ic_photo,
            R.string.ic_take_a_photo,
            R.string.ic_qa,
            R.string.ic_exchange,
            R.string.ic_minutes,
            R.string.ic_share_task,
            R.string.ic_talk_task,
            R.string.ic_send_photo
    };

    public ChatBottomAddMoreView(@NonNull Context context, ChatBottomSheet chatBottomSheet) {
        super(context);
        this.chatBottomSheet = chatBottomSheet;
        init();
    }

    private void init(){
        activity = (Activity) getContext();
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        setLayoutParams(params);
        setPadding(
                IMMessageApplication.dipToPixels(getContext(), 22.4f),
                IMMessageApplication.dipToPixels(getContext(), 16.8f),
                IMMessageApplication.dipToPixels(getContext(), 22.4f),
                IMMessageApplication.dipToPixels(getContext(), 16.8f)
        );
        setClipToPadding(false);
        setClipChildren(false);
        setLayoutManager(new GridLayoutManager(getContext(), 4, LinearLayoutManager.VERTICAL, false));
        setAdapter(new RecyclerViewAdapter());
    }

    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View convertView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_add_more, viewGroup, false);
            return new ViewHolder(convertView);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            ViewHolder vh = (ViewHolder) viewHolder;
            vh.imageView.setImageResource(icResIds[viewHolder.getAdapterPosition()]);
            vh.textView.setText(textResIds[viewHolder.getAdapterPosition()]);
            vh.itemView.setTag(icResIds[viewHolder.getAdapterPosition()]);
            vh.itemView.setOnClickListener(btnClick);
        }

        @Override
        public int getItemCount() {
            return icResIds.length;
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView imageView;
        public TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            textView = itemView.findViewById(R.id.textView);
        }
    }

    public OnClickLimitListener btnClick = new OnClickLimitListener() {
        @Override
        public void onClickLimit(View v) {
            chatBottomSheet.hide();
            switch((int) v.getTag()){
                case R.drawable.ic_photo:
                    //檢查讀取權限
                    if(IMMessageApplication.checkPermission(activity, IMMessageApplication.Request_Permission_Read, Manifest.permission.READ_EXTERNAL_STORAGE)){
                        goSelectImage();
                    }
                    break;
                case R.drawable.ic_take_a_photo:
                    if(IMMessageApplication.checkPermission(activity, IMMessageApplication.Request_Permission_Camera,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)){
                        takePicture();
                    }
                    break;
                case R.drawable.ic_qa:
                    Log.d("Asiz", "ic_qa");
                    break;
                case R.drawable.ic_exchange:
                    Log.d("Asiz", "ic_exchange");
                    break;
                case R.drawable.ic_minutes:
                    Log.d("Asiz", "ic_minutes");
                    break;
                case R.drawable.ic_share_task:
                    Log.d("Asiz", "ic_share_task");
                    break;
                case R.drawable.ic_talk_task:
                    Log.d("Asiz", "ic_talk_task");
                    break;
                case R.drawable.ic_send_photo:
                    Log.d("Asiz", "ic_send_photo");
                    break;
            }
        }
    };

    public void goSelectImage(){
        Intent it = new Intent(activity, SelectImageActivity.class);
        it.putExtra("singleSelect", true);
        it.putExtra("canSelectCount", 1);
        it.putExtra("Request_Code", SelectImageActivity.Request_Code_Select_Photo);
        activity.startActivityForResult(it, it.getIntExtra("Request_Code", 0));
        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void takePicture(){
        if (!IMMessageApplication.cacheDir.exists())
            IMMessageApplication.cacheDir.mkdirs();
        Intent intent = new Intent (MediaStore.ACTION_IMAGE_CAPTURE);
        IMMessageApplication.tmpFile = new File(IMMessageApplication.cacheDir, String.format(Locale.getDefault(), "%d.jpg", System.currentTimeMillis()));
        intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(activity, BuildConfig.APPLICATION_ID + ".fileprovider", IMMessageApplication.tmpFile));
        activity.startActivityForResult(intent, PhotoUtuls.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }
}
