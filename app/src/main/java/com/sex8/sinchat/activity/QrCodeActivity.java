package com.sex8.sinchat.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.sex8.sinchat.IMMessageApplication;
import com.sex8.sinchat.R;
import com.sex8.sinchat.model.IMData;
import com.sex8.sinchat.tools.OnClickLimitListener;
import com.sex8.sinchat.utils.PhotoUtuls;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.EnumMap;
import java.util.Locale;

public class QrCodeActivity extends BaseActivity {
    private DisplayMetrics displayMetrics;
    private ImageView iconImage;
    private ImageView qrCodeImage;
    private Bitmap iconBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code);

        findViews();
        init();
        setListeners();
    }

    @Override
    public void onBackPressed() {
        backActivity();
    }

    @Override
    public void findViews() {
        iconImage = findViewById(R.id.iconImage);
        qrCodeImage = findViewById(R.id.qrCodeImage);
    }

    @Override
    public void init() {
        ((TextView) findViewById(R.id.titleText)).setText(R.string.my_qr_code);
        ((TextView) findViewById(R.id.nameText)).setText(IMData.getInstance().getUserInfo().getNickName());
        ((TextView) findViewById(R.id.vipLevelText)).setText("LV20");
        Glide.with(this)
                .asBitmap()
                .load(IMData.getInstance().getUserInfo().getImageUrl())
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        iconBitmap = resource;
                        setQrCodeImage();
                        return false;
                    }
                })
                .circleCrop()
                .into(iconImage);

        //取得螢幕寬度設置QrCode大小
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        ViewGroup.LayoutParams params = qrCodeImage.getLayoutParams();
        params.width = (int) (displayMetrics.widthPixels / 1.5f);
        params.height = (int) (displayMetrics.widthPixels / 1.5f);
        qrCodeImage.setLayoutParams(params);
    }

    @Override
    public void setListeners() {
        findViewById(R.id.back).setOnClickListener(btnClick);
    }

    private OnClickLimitListener btnClick = new OnClickLimitListener() {
        @Override
        public void onClickLimit(View v) {
            switch (v.getId()){
                case R.id.back:
                    backActivity();
                    break;
            }
        }
    };

    private void setQrCodeImage(){
        Glide.with(this)
                .load(mergeBitmaps(iconBitmap, qrCodeGenerated("dsaddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd")))
                .fitCenter()
                .into(qrCodeImage);
    }

    /**
     * 生成QrCode
     */
    private Bitmap qrCodeGenerated(String str){
        EnumMap hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
        hints.put(EncodeHintType.MARGIN, 0);
        BitMatrix result;
        try {
            result = new MultiFormatWriter().encode(
                    new String(str.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8),
                    BarcodeFormat.QR_CODE,
                    qrCodeImage.getWidth(),
                    qrCodeImage.getHeight(),
                    hints);
            int w = result.getWidth();
            int h = result.getHeight();
            int[] pixels = new int[w * h];
            for (int y = 0; y < h; y++) {
                int offset = y * w;
                for (int x = 0; x < w; x++) {
                    pixels[offset + x] = result.get(x, y) ? Color.BLACK : Color.WHITE;
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, w, 0, 0, w, h);
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 合併logo和qrCode
     */
    public Bitmap mergeBitmaps(Bitmap overlay, Bitmap bitmap) {
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();

        Bitmap combined = Bitmap.createBitmap(width, height, bitmap.getConfig());
        Canvas canvas = new Canvas(combined);
        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();

        canvas.drawBitmap(bitmap, new Matrix(), null);

        int centreX = (canvasWidth  - overlay.getWidth()) /2;
        int centreY = (canvasHeight - overlay.getHeight()) /2 ;
        canvas.drawBitmap(overlay, centreX, centreY, null);
        IMMessageApplication.tmpFile = new File(IMMessageApplication.cacheDir, String.format(Locale.getDefault(), "%d.jpg", System.currentTimeMillis()));
        PhotoUtuls.saveBitmap(combined, IMMessageApplication.tmpFile);
        return combined;
    }

    private void backActivity(){
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        finish();
    }
}
