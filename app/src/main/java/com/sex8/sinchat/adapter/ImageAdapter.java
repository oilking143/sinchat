package com.sex8.sinchat.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.sex8.sinchat.R;
import com.sex8.sinchat.entity.PhotoData;

import java.io.File;
import java.util.List;

public class ImageAdapter extends BaseAdapter {

    List<PhotoData> photolist;
    Context mContext;
    private LayoutInflater mLayInf;

    public ImageAdapter(Context c , List<PhotoData> photolist) {
        mContext = c;
        this.photolist=photolist;
        this.mLayInf = LayoutInflater.from(c);
    }

    @Override
    public int getCount() {
        return photolist.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView gridImage;

        if (convertView == null) {  // if it's not recycled, initialize some attributes
            convertView = mLayInf.inflate(R.layout.grid_layout, parent, false);

        }

        gridImage = (ImageView)convertView.findViewById(R.id.grid_image);

        if(photolist.get(position).isLocal)
        {
            gridImage.setImageResource(R.drawable.add_icon);
            gridImage.setScaleType(ImageView.ScaleType.CENTER);
            gridImage.setBackgroundColor(0xffFFE4EC);
        }
        else
        {
            gridImage.setScaleType(ImageView.ScaleType.CENTER_CROP);

            File imgFile = new File(photolist.get(position).path);

            if (imgFile.exists()) {

                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                gridImage.setImageBitmap(myBitmap);
            }

        }

        return convertView;
    }
}
