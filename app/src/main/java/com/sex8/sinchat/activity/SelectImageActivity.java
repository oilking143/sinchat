package com.sex8.sinchat.activity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sex8.sinchat.BuildConfig;
import com.sex8.sinchat.IMMessageApplication;
import com.sex8.sinchat.R;
import com.sex8.sinchat.decoration.ItemDecorationAlbumColumns;
import com.sex8.sinchat.dialog.TipDialog;
import com.sex8.sinchat.entity.PhotoData;
import com.sex8.sinchat.event.SelectImageEvent;
import com.sex8.sinchat.tools.SingleMediaScanner;
import com.sex8.sinchat.utils.PhotoUtuls;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SelectImageActivity extends AppCompatActivity {
    public static final int Request_Code_Select_Photo = 501;
    private PhotoUtuls photoUtuls;
    private boolean isPhoto = true;
    private boolean singleSelect = false;
    private int canSelectCount;
    private RecyclerView recyclerView;
    private RecyclerAdapter recyclerAdapter;
    private ArrayList<PhotoData> photoList;
    private TextView doneText;
    //選取照片暫存
    private List<PhotoData> selectList = new ArrayList<>();
    //拍攝影片或照片
    private File tmpFile;
    private Handler handler = new Handler();
    private boolean showCamera = false;
    private final int spanCount = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_image);

        findViews();
        init();
        setListener();

        //檢查讀取權限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    IMMessageApplication.Request_Permission_Read);
        }else{
            initUI();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(permissions.length > 0 && grantResults.length > 0
                && permissions[0].equals(Manifest.permission.READ_EXTERNAL_STORAGE)
                && grantResults[0] == 0){
            handler.post(new Runnable() {
                @Override
                public void run() {
                    initUI();
                }
            });
        }else if(permissions.length > 0 && grantResults.length > 0
                && permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                && grantResults[0] == 0
                && permissions[1].equals(Manifest.permission.CAMERA)
                && grantResults[1] == 0){
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if(isPhoto)
                        takePicture();
                    else
                        recordVideo();
                }
            });
        }else{
            IMMessageApplication.Toast(getString(R.string.permission_read_external_storage), Toast.LENGTH_LONG);
            onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK
                && (requestCode == PhotoUtuls.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE
                        || requestCode == PhotoUtuls.RECORD_VIDEO_ACTIVITY_REQUEST_CODE)){
            new SingleMediaScanner(this, tmpFile);
            selectList.clear();
            selectList.add(new PhotoData(tmpFile));
            backActivity(true);
        }
    }

    @Override
    public void onBackPressed() {
        backActivity(false);
    }

    private void findViews(){
        recyclerView = findViewById(R.id.recyclerView);
        doneText = findViewById(R.id.doneText);
    }

    private void init(){
        new SingleMediaScanner(this, IMMessageApplication.cacheDir);
        singleSelect = getIntent().getBooleanExtra("singleSelect", false);
        if(singleSelect){
            canSelectCount = 1;
        }else {
            canSelectCount = getIntent().getIntExtra("canSelectCount", 9);
        }
        showCamera = getIntent().getBooleanExtra("showCamera", false);
        isPhoto = getIntent().getIntExtra("Request_Code", Request_Code_Select_Photo) == Request_Code_Select_Photo;
        photoUtuls = new PhotoUtuls(this);
        doneText.setText(R.string.next_step);
        ((TextView) findViewById(R.id.titleText)).setText(getString(R.string.album));

        recyclerView.setLayoutManager(new GridLayoutManager(this, spanCount, LinearLayoutManager.VERTICAL, false));
        //以螢幕寬度計算item大小
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        int itemWidth = displayMetrics.widthPixels / spanCount;
        int spacing = itemWidth / 30;
        recyclerView.setPadding(
                spacing,
                spacing,
                spacing,
                spacing
        );

        recyclerView.addItemDecoration(new ItemDecorationAlbumColumns(spacing, spanCount));
        recyclerAdapter = new RecyclerAdapter();
        recyclerView.setAdapter(recyclerAdapter);
    }

    private void setListener(){
        findViewById(R.id.back).setOnClickListener(btnClick);
        doneText.setOnClickListener(btnClick);
    }

    private View.OnClickListener itemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ViewHolder viewHolder = (ViewHolder) v.getTag();
            int position = viewHolder.getAdapterPosition();
            if(showCamera)
                position -= 1;
            if(position == -1){
                if(isPhoto)
                    takePicture();
                else
                    recordVideo();
            }else {
                if (recyclerAdapter.itemSelectList.contains(position)) {
                    int lastIndex = recyclerAdapter.itemSelectList.indexOf(position);
                    int lastPost = recyclerAdapter.itemSelectList.get(lastIndex);
                    selectList.remove(photoList.get(position));
                    recyclerAdapter.itemSelectList.remove(lastIndex);

                    recyclerAdapter.notifyItemChanged(lastPost);
                    for(int n: recyclerAdapter.itemSelectList){
                        recyclerAdapter.notifyItemChanged(n);
                    }
                } else {
                    if (singleSelect) {
                        recyclerAdapter.itemSelectList.clear();
                        selectList.clear();
                    }

                    if(selectList.size() < canSelectCount) {
                        selectList.add(photoList.get(position));
                        recyclerAdapter.itemSelectList.add(position);
                    }else
                        IMMessageApplication.Toast(getString(R.string.select_max_photo, canSelectCount), Toast.LENGTH_SHORT);

                    recyclerAdapter.notifyItemChanged(position);
                    if(singleSelect)
                        backActivity(true);
                }
                if (selectList.size() > 0) {
                    doneText.setVisibility(View.VISIBLE);
                } else
                    doneText.setVisibility(View.GONE);
            }
        }
    };

    private View.OnClickListener btnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.back:
                    onBackPressed();
                    break;
                case R.id.doneText:
                    doneText.setOnClickListener(null);
                    backActivity(true);
                    break;
            }
        }
    };

    private void initUI(){
        if(isPhoto)
            photoUtuls.queryAll(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        else
            photoUtuls.queryAll(MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        photoList = photoUtuls.getAllPhoto();
        recyclerAdapter.initSelect();
        recyclerAdapter.notifyDataSetChanged();
    }

    public void takePicture(){
        //檢查權限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                    IMMessageApplication.Request_Permission_Read);
            return;
        }
        if (!IMMessageApplication.cacheDir.exists())
            IMMessageApplication.cacheDir.mkdirs();
        Intent intent = new Intent (MediaStore.ACTION_IMAGE_CAPTURE);
        tmpFile = new File(IMMessageApplication.cacheDir, String.format(Locale.getDefault(), "%d.jpg", System.currentTimeMillis()));
        intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileprovider", tmpFile));
        startActivityForResult(intent, PhotoUtuls.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    public void recordVideo(){
        //檢查權限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                    IMMessageApplication.Request_Permission_Read);
            return;
        }

        if (!IMMessageApplication.cacheDir.exists())
            IMMessageApplication.cacheDir.mkdirs();
        Intent intent = new Intent (MediaStore.ACTION_VIDEO_CAPTURE);
        tmpFile = new File(IMMessageApplication.cacheDir, String.format(Locale.getDefault(), "%d.mp4", System.currentTimeMillis()));
        intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileprovider", tmpFile));
        startActivityForResult(intent, PhotoUtuls.RECORD_VIDEO_ACTIVITY_REQUEST_CODE);
    }

    class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        public List<Integer> itemSelectList;
        public RecyclerAdapter(){

        }

        public void initSelect(){
            if(itemSelectList == null)
                itemSelectList = new ArrayList<Integer>();
            else
                itemSelectList.clear();
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View convertView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_select_photo, viewGroup, false);
            return new ViewHolder(convertView);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
            ViewHolder vh = (ViewHolder) viewHolder;
            if(showCamera)
                position -= 1;

            if(singleSelect){
                vh.numText.setVisibility(View.GONE);
            }

            if(position == -1){
                vh.cameraView.setVisibility(View.VISIBLE);
            }else {
                vh.cameraView.setVisibility(View.GONE);
                String path = photoList.get(position).path;
                Glide.with(SelectImageActivity.this)
                        .load(path)
                        .centerCrop()
                        .error(android.R.drawable.ic_menu_report_image)
                        .into(vh.photoImage);

                if (itemSelectList.contains(position)) {
                    vh.mask.setVisibility(View.VISIBLE);
                    vh.numText.setActivated(true);
                    String num = String.valueOf(itemSelectList.indexOf(position) + 1);
                    if (num.length() > 2)
                        num = "*";
                    vh.numText.setText(num);
                } else {
                    vh.mask.setVisibility(View.GONE);
                    vh.numText.setActivated(false);
                    vh.numText.setText(null);
                }
            }
        }

        @Override
        public int getItemCount() {
            if(showCamera){
                return photoList != null ? photoList.size() + 1 : 1;
            }else{
                return photoList != null ? photoList.size() : 0;
            }
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        View mask;
        TextView numText;
        ImageView cameraView;
        ImageView photoImage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mask = itemView.findViewById(R.id.mask);
            numText = itemView.findViewById(R.id.numText);
            cameraView = itemView.findViewById(R.id.cameraView);
            photoImage = itemView.findViewById(R.id.photoImage);
            itemView.setTag(this);
            itemView.setOnClickListener(itemClickListener);
        }
    }

    private void backActivity(boolean done){

        boolean oversize=false;
        int postiton=0;
        if(done){
            for(int i=0;i<selectList.size();i++)
            {
                Log.d("list",selectList.get(i).fileName);
                File f= new File(selectList.get(i).path);
                Log.d("filesize",""+f.length());

                if(f.length()>2000000)
                {
                    oversize=true;
                    postiton=i;
                    break;
                }
            }



       if(oversize && selectList.get(postiton).path.contains(".gif"))
       {

           LayoutInflater inflater = getLayoutInflater();
           View layout = inflater.inflate(R.layout.custom_toast_layout,
                   null);
           Toast toast = new Toast(this);
           toast.setGravity(Gravity.TOP, 0, 0);
           toast.setDuration(Toast.LENGTH_LONG);
           toast.setView(layout);
           TextView toasttxt=(TextView)layout.findViewById(R.id.toast_content);

           toasttxt.setText(R.string.file_size_warn_gif);

           toast.show();
       }
       else
       {
           EventBus.getDefault().post(new SelectImageEvent(selectList));

       }
        }
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        finish();
    }


    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

}
