package com.sex8.sinchat.utils;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.sex8.sinchat.entity.PhotoData;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PhotoUtuls {
    private final static String TAG = "PhotoHelp";
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 9001;
    public static final int RECORD_VIDEO_ACTIVITY_REQUEST_CODE = 9002;
    private Context context;
    private ContentResolver contentResolver;
    private ArrayList<PhotoData> photoList = new ArrayList<>();
    private Map<String,ArrayList<PhotoData>> albumMap = new HashMap<>();
    //圖像屬性
    private final String[] projection = new String[]{
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media.DATE_TAKEN,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATA
    };

    public PhotoUtuls(Context context){
        this.context = context;
        this.contentResolver = context.getContentResolver();
    }

    public void queryAll(Uri uri){
        photoList.clear();
        albumMap.clear();
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            return;

        Log.d("Uri = ",uri.toString());
//        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
//        uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        Cursor cur = contentResolver.query(uri,
                projection, // 要返回的列
                null,       // 要返回 （所有行） 哪些行
                null,       // 選擇參數 （無）
                MediaStore.Images.Media.DATE_TAKEN + " DESC"        // 排序
        );
        if (cur.moveToFirst()) {
            String bucket;
            String date;
            String name;
            String data;
            String extension;

            do {
                // 取得該欄位值
                bucket = cur.getString(cur.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
                name = cur.getString(cur.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
                data = cur.getString(cur.getColumnIndex(MediaStore.Images.Media.DATA));
                if(name!=null)
                {
                    extension = getExtension(name);                //過濾 .webp
                    if(!extension.equals("webp")) {
                        PhotoData pd = new PhotoData();
                        pd.path = data;
                        pd.fileName = name;

                        photoList.add(pd);

                        ArrayList<PhotoData> list = albumMap.get(bucket);
                        if (list == null) {
                            list = new ArrayList<>();
                            albumMap.put(bucket, list);
                        }
                        list.add(pd);
                    }
                }

            } while (cur.moveToNext());
        }
        cur.close();
    }

    public ArrayList<PhotoData> getAllPhoto(){
        return photoList;
    }

    public Map<String,ArrayList<PhotoData>> getAllAlbums(){
        return albumMap;
    }

    public ArrayList<PhotoData> getAlbum(String bucket) {
        return albumMap.get(bucket);
    }

    //取得縮圖
    public static Bitmap getThumbnail(File file, float sampleSize){
        return getThumbnail(file.getPath(), sampleSize);
    }
    //取得縮圖
    public static Bitmap getThumbnail(String imgPath, float sampleSize)
    {
        Bitmap bmp = null;
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        bmp = BitmapFactory.decodeFile(imgPath, opts);

        opts.inSampleSize = getinSampleSize(opts, sampleSize);
        opts.inJustDecodeBounds = false;
        try {
            bmp = BitmapFactory.decodeFile(imgPath, opts);
        } catch (OutOfMemoryError e) {

        }
        return bmp;
    }

    //切成正方形
    public static Bitmap cropSquare(Bitmap bmp){
        if(bmp == null)
            return null;
        int w = bmp.getWidth(); // 得到图片的宽，高
        int h = bmp.getHeight();
        int wh = w > h ? h : w;// 裁切后所取的正方形区域边长
        int retX = w > h ? (w - h) / 2 : 0;//基于原图，取正方形左上角x坐标
        int retY = w > h ? 0 : (h - w) / 2;
        bmp = Bitmap.createBitmap(bmp, retX, retY, wh, wh, null, false);

        return bmp;
    }

    public static int getinSampleSize(BitmapFactory.Options options, float sampleSize) {
        final int height = options.outHeight;
        final int width = options.outWidth;

        int inSampleSize = 1;

        int min = height > width ? width : height;
        inSampleSize = (int) Math.ceil(min / sampleSize);
//        Log.d("Asiz","压缩前分辨率：" + width + "*" + height + "     压缩倍数："
//                + inSampleSize);
        return inSampleSize;
    }

    public static Bitmap getSampleSizeBitmap(String path, float sampleSize){
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(path, opts);
        opts.inSampleSize = getinSampleSize(opts, sampleSize);
        opts.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, opts);
    }

    //旋轉圖片
    public static Bitmap rotated(Bitmap bmp, float degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
    }

    public static boolean haveCameraApp (Context context)
    {
        PackageManager pm = context.getPackageManager();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> availableActivities = pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return availableActivities != null && availableActivities.size() > 0;
    }

    public static void saveBitmap(Bitmap bmp, File file){
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //取得照片方向角度
    public static int getOrientationDegrees(String path){
        int degrees = 0;
        try {
            ExifInterface exif = new ExifInterface(path);
            int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (rotation){
                case  ExifInterface.ORIENTATION_ROTATE_90:
                    degrees = 90;
                    break;
                case  ExifInterface.ORIENTATION_ROTATE_180:
                    degrees = 180;
                    break;
                case  ExifInterface.ORIENTATION_ROTATE_270:
                    degrees = 270;
                    break;
            }
        } catch (IOException ioe){
        }
        return degrees;
    }

    public static String encodeToBase64(@NonNull Bitmap bitmap, int quality) {
        return encodeToBase64(bitmap, quality, false);
    }

    //将图片转换成 bitmap 并编码 Base 64
    public static String encodeToBase64(@NonNull Bitmap bitmap, int quality, boolean front) {
        //conver-t to byte array
        byte[] bytes;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
            bytes = baos.toByteArray();
            baos.close();
        }catch (IOException ioe){
            return null;
        }
        //base64 encode
        byte[] encode = Base64.encode(bytes,Base64.DEFAULT);

        String base64 = new String(encode);
        if(front)
            base64 = "data:image/jpg;base64," + base64;
        return base64;
    }

    public static String encodeGifToBase64(String path){
        File file = new File(path);
        byte[] bytes = new byte[(int) file.length()];
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(new File(path));
            BufferedInputStream buf = new BufferedInputStream(fis);
            buf.read(bytes, 0, bytes.length);
            buf.close();
            byte[] encode = Base64.encode(bytes,Base64.DEFAULT);
            String base64 = new String(encode);
            base64 = "data:image/gif;base64," + base64;
            return base64;
        }catch (IOException ioe){
            ioe.printStackTrace();
        } finally {
            try {
                fis.close();
            } catch (IOException e) {

            }
        }
        return null;
    }

    public static Bitmap decodeFromBase64(String encodeString) {
        byte[] decode = Base64.decode(encodeString, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decode, 0, decode.length);
    }

    public static Bitmap getBitmapFromURL(String urlPath){
        try {
            URL url = new URL(urlPath);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();

//            BitmapFactory.Options opts = new BitmapFactory.Options();
//            opts.inJustDecodeBounds = true;
//            Bitmap bmp = BitmapFactory.decodeStream(input, null, opts);
//            opts.inSampleSize = getinSampleSize(opts, 700);
//            opts.inJustDecodeBounds = false;
//            bmp = BitmapFactory.decodeStream(input, null, opts);
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            Log.e(TAG, "getBitmapFromURL Error : " + e.getMessage());
            return null;
        }
    }

    /*
    * 計算自適應最大尺寸
    * */
    public static int[] getAdaptiveSize(int targetWidth, int targetHeight, int maxWidth, int maxHeight){
        float max = (float) maxWidth / (float) maxHeight;
        float target = (float) targetWidth / (float) targetHeight;
        float scale = target < max ? (float) maxHeight / targetHeight : (float) maxWidth / targetWidth;
        return new int[]{(int) (targetWidth * scale), (int) (targetHeight * scale)};
    }

    public static int[] getLocalImageSize(String imgPath){
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imgPath, opts);
        return new int[]{opts.outWidth, opts.outHeight};
    }

    public static int[] getUrlImageSize(String urlPath){
        try {
            URL url = new URL(urlPath);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();

            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            Bitmap bmp = BitmapFactory.decodeStream(input, null, opts);
            return new int[]{opts.outWidth, opts.outHeight};


        } catch (IOException e) {
            Log.e(TAG, "getBitmapFromURL Error : " + e.getMessage());
            return null;
        }
    }


    public static String getExtension(String fileName){
        int startIndex = fileName.lastIndexOf(46) + 1;
        int endIndex = fileName.length();
        return  fileName.substring(startIndex, endIndex).toLowerCase();
    }

    /**
     * 创建图片
     *
     * @param path
     * @return
     */
    public static Bitmap createBitmap(String path) {
        if (path == null) {
            return null;
        }

        BitmapFactory.Options opts = new BitmapFactory.Options();
        //不在内存中读取图片的宽高
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, opts);
        int width = opts.outWidth;

        opts.inSampleSize = width > 1080 ? (int)(width / 1080) : 1 ;//注意此处为了解决1080p手机拍摄图片过大所以做了一定压缩，否则bitmap会不显示

        opts.inJustDecodeBounds = false;// 这里一定要将其设置回false，因为之前我们将其设置成了true
        opts.inPurgeable = true;
        opts.inInputShareable = true;
        opts.inDither = false;
        FileInputStream is = null;
        Bitmap bitmap = null;
        try {
            is = new FileInputStream(path);
            bitmap = BitmapFactory.decodeFileDescriptor(is.getFD(), null, opts);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                    is = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return bitmap;
    }
}
