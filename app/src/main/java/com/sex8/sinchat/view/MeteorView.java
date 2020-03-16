package com.sex8.sinchat.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MeteorView extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder surfaceHolder = null;
    private Paint mPaint = new Paint();
    private DrawThread drawThread;
    private boolean start;
    private int maxStarCount = 10;
    private Rect starRect;

    public MeteorView(Context context) {
        this(context, null);
    }

    public MeteorView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MeteorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        this.setKeepScreenOn(true);
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);//事件添加
        setZOrderOnTop(true);
        getHolder().setFormat(PixelFormat.TRANSLUCENT);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(10f);
        mPaint.setColor(Color.WHITE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setAntiAlias(true);

        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);

        starRect = new Rect(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels / 3);
        setMaxStarCount(displayMetrics.widthPixels / 100);
    }

    public void setMaxStarCount(int count){
        this.maxStarCount = count;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        surfaceHolder = holder;
        if(start) {
            start();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if(drawThread != null) {
            drawThread.isRun = false;
            drawThread.interrupt();
        }
    }

    public void start(){
        start = true;
        if( surfaceHolder.getSurface().isValid() && (drawThread == null || !drawThread.isRun)) {
            drawThread = new DrawThread();
            drawThread.start();
        }
    }
    
    public void stop(){
        start = false;
    }

    private class DrawThread extends Thread {
        public boolean isRun;
        private List<ShootingStar> starList = new ArrayList<>();
        private Canvas canvas;
        public int countDownFrame;

        public DrawThread() {
            isRun = true;
        }

        public ShootingStar createStar() {
            double angle = Math.PI / 3;
            double distance = Math.random() * starRect.height();
            Crood initCrood = new Crood((float)(Math.random() * starRect.width()), (float)(Math.random() * starRect.height()));
            Crood finalCrood = new Crood((float)(initCrood.x - distance * Math.cos(angle)), (float)(initCrood.y + distance * Math.sin(angle)));
            float size = (float)(Math.random() * (starRect.width() / 200f));
            float speed = (float)(Math.random() * starRect.height() + starRect.height() / 5f);

            if(starRect.contains((int) finalCrood.x, (int) finalCrood.y)) {
                ShootingStar star = new ShootingStar(
                        initCrood, finalCrood, size, speed);
                return star;
            }else{
                return createStar();
            }
        }

        public ShootingStar createfinalStar() {

            return null;
        }

        public void remove(ShootingStar star){
            starList.remove(star);
        }

        public void update(long delta) {
            if (start && starList.size() < maxStarCount) {
                starList.add(createStar());
            }

            if(starList.size() > 0) {
                for (ShootingStar star : starList.toArray(new ShootingStar[0])) {
                    star.draw(canvas, delta);
                }
            }else if(countDownFrame < 0){
                isRun = false;
            }
        }

        @Override
        public void run() {
            while (isRun) {
                try {
                    synchronized (surfaceHolder) {
                        canvas = surfaceHolder.lockCanvas();
                        long now = new Date().getTime();
                        long last = now;
                        long delta;
                        delta = now - last;
                        delta = delta > 500 ? 30 : (delta < 16? 16 : delta);
                        last = now;
                        if(canvas != null) {
                            countDownFrame--;
                            canvas.drawColor(Color.parseColor("#CC000000"), PorterDuff.Mode.DST_IN);
                            update(delta);
                        }
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();
                } finally {
                    if (canvas != null) {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
            }
        }
    }

    // 坐标
    public class Crood {
        private float x, y;
        public Crood(float x, float y) {
            this.x = x;
            this.y = y;
        }

        public void setCrood(float x, float y) {
            this.x = x;
            this.y = y;
        }

        public Crood copy() {
            return new Crood(this.x, this.y);
        }
    }

    public class ShootingStar{
        private Crood initCrood, finalCrood, prevCrood, nowCrood;
        private float size, speed;
        private float dur, pass;

        public ShootingStar(Crood initCrood, Crood finalCrood, float size, float speed){
            this.initCrood = initCrood; // 初始位置
            this.finalCrood = finalCrood; // 最终位置
            this.size = size; // 大小
            this.speed = speed; // 速度：像素/s

            // 飞行总时间
            this.dur = (float) (Math.sqrt(Math.pow(this.finalCrood.x - this.initCrood.x, 2) +
                    Math.pow(this.finalCrood.y-this.initCrood.y, 2)) * 1000 / this.speed);

            this.pass = 0; // 已过去的时间
            this.prevCrood = this.initCrood.copy(); // 上一帧位置
            this.nowCrood = this.initCrood.copy(); // 当前位置
        }

        public void draw(Canvas canvas, long delta) {
            drawThread.countDownFrame = 50;
            this.pass += delta;
            this.pass = Math.min(this.pass, this.dur);

            float percent = this.pass / this.dur;
            this.nowCrood.setCrood(
                    this.initCrood.x + (this.finalCrood.x - this.initCrood.x) * percent,
                    this.initCrood.y + (this.finalCrood.y - this.initCrood.y) * percent
            );
            this.prevCrood.setCrood(
                    this.initCrood.x + (this.prevCrood.x - this.initCrood.x) * 0.9f,
                    this.initCrood.y + (this.prevCrood.y - this.initCrood.y) * 0.9f
            );
            // canvas
            mPaint.setStrokeWidth(size);
            canvas.drawLine(
                    this.prevCrood.x,
                    this.prevCrood.y,
                    this.nowCrood.x,
                    this.nowCrood.y,
                    mPaint);

            this.prevCrood.setCrood(this.nowCrood.x, this.nowCrood.y);
            if (this.pass == this.dur) {
                destroy();
            }
        }

        public void destroy(){
            if(drawThread != null)
                drawThread.remove(this);
        }
    }
}
