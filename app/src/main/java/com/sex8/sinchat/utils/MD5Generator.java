package com.sex8.sinchat.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;

public class MD5Generator {

    private String Md5String="";


    public MD5Generator(MessageDigest digest, File file)throws IOException
    {
        //Get file input stream for reading the file content
        FileInputStream fis = new FileInputStream(file);

        //Create byte array to read data in chunks
        byte[] byteArray = new byte[1024];
        int bytesCount = 0;

        //Read file data and update in message digest
        while ((bytesCount = fis.read(byteArray)) != -1) {
            digest.update(byteArray, 0, bytesCount);
        };

        //close the stream; We don't need it now.
        fis.close();

        //Get the hash's bytes
        byte[] bytes = digest.digest();

        //This bytes[] has bytes in decimal format;
        //Convert it to hexadecimal format
        StringBuilder sb = new StringBuilder();
        for(int i=0; i< bytes.length ;i++)
        {
            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }

        //return complete hash
        Md5String= sb.toString();
    }


    public String getMd5String() {
        return Md5String;
    }

    public static Bitmap zoomImg(Bitmap bm, int newWidth ,int newHeight){
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return newbm;
    }

    public static Bitmap compressImage(Bitmap image,int size) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 質量壓縮方法，這裡100表示不壓縮，把壓縮後的資料存放到baos中
        int options = 90;
        while (baos.toByteArray().length / 1024 > size) { // 迴圈判斷如果壓縮後圖片是否大於100kb,大於繼續壓縮
            baos.reset(); // 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 這裡壓縮options%，把壓縮後的資料存放到baos中
            options -= 10;// 每次都減少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把壓縮後的資料baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream資料生成圖片
        return bitmap;
    }

    public static Bitmap compressImageSize300(String ImageSrc) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(ImageSrc, newOpts);// 此時返回bm為空
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        float hh = 800f;// 這裡設定高度為800f
        float ww = 600f;// 這裡設定寬度為6000f
        int be = 1;// be=1表示不縮放
        if (w > h && w > ww) {// 如果寬度大的話根據寬度固定大小縮放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {// 如果高度高的話根據寬度固定大小縮放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;// 設定縮放比例
        bitmap = BitmapFactory.decodeFile(ImageSrc, newOpts);
        return compressImage(bitmap,300);// 壓縮好比例大小後再進行質量壓縮

    }

    public static Bitmap compressImageSize100(String ImageSrc) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(ImageSrc, newOpts);// 此時返回bm為空
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        float hh = 640f;// 這裡設定高度為640f
        float ww = 480f;// 這裡設定寬度為480f
        int be = 1;// be=1表示不縮放
        if (w > h && w > ww) {// 如果寬度大的話根據寬度固定大小縮放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {// 如果高度高的話根據寬度固定大小縮放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;// 設定縮放比例
        bitmap = BitmapFactory.decodeFile(ImageSrc, newOpts);
        return compressImage(bitmap,100);// 壓縮好比例大小後再進行質量壓縮
    }



    public Bitmap getResizedBitmap(Bitmap image, int bitmapWidth, int bitmapHeight) {
        return Bitmap.createScaledBitmap(image, bitmapWidth, bitmapHeight, true);
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

    public static Bitmap resizeBtimap(Bitmap oldBit,float scale)
    {

        // 计算缩放比例
        float scaleWidth =scale;
        float scaleHeight = scale;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        Bitmap newbm = Bitmap.createBitmap(oldBit, 0, 0, oldBit.getWidth(), oldBit.getHeight(), matrix,
                true);

        return newbm;
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
            Log.e("ERROR", e.getMessage(), e);
        }
        return bmp;
    }

    public static String bitmapToBase64(Bitmap bitmap) {

        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                baos.flush();
                baos.close();

                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
            }
        } catch (IOException e) {
            Log.e("ERROR", e.getMessage(), e);
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                Log.e("ERROR", e.getMessage(), e);
            }
        }
        return result;
    }

}
