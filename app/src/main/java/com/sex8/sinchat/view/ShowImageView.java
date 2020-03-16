package com.sex8.sinchat.view;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;

public class ShowImageView extends RelativeLayout implements View.OnTouchListener {
    private ImageView imageView;
    private float[] moveAnimatorParameter;
    private int animatorDuration = 300;
    private boolean isShow = false;
    private boolean isAnim = false;
    private String PathUrl="";

    public ShowImageView(Context context) {
        super(context);
        init();
    }

    public ShowImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ShowImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        setOnTouchListener(this);
        setBackgroundColor(Color.BLACK);
    }

    public void show(String path, final float x, final float y, final float width, final float height){
        if(isAnim || isShow)
            return;

        setAlpha(0f);
        setVisibility(INVISIBLE);
        removeAllViews();

        imageView = new ImageView(getContext());
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        imageView.setLayoutParams(layoutParams);
        addView(imageView);


        Log.d("Pathhh",path);
        Glide.with(getContext()).load(path).fitCenter().into(imageView);
        imageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if(imageView.getWidth() > 0 && imageView.getHeight() > 0)
                {
                    imageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    setVisibility(VISIBLE);

                    float[] matrixValues = new float[9];
                    Matrix mScaleMatrix = new Matrix();
                    RectF rectFSrc = new RectF(imageView.getLeft(),imageView.getTop(),
                            imageView.getLeft() + imageView.getWidth(),imageView.getTop() + imageView.getHeight());
                    float dstX = (getWidth() - width) / 2f;
                    float dstY = (getHeight() - height) / 2f;
                    RectF rectFDst = new RectF(dstX, dstY, dstX + width,dstY + height);
                    mScaleMatrix.setRectToRect(rectFSrc, rectFDst, Matrix.ScaleToFit.FILL);
                    mScaleMatrix.getValues(matrixValues);

                    float sx = imageView.getLeft() * matrixValues[Matrix.MSCALE_X] + 1 * matrixValues[Matrix.MTRANS_X];
                    float sy = imageView.getTop() * matrixValues[Matrix.MSCALE_Y] + 1 * matrixValues[Matrix.MTRANS_Y];
                    imageView.setScaleX(matrixValues[Matrix.MSCALE_X]);
                    imageView.setScaleY(matrixValues[Matrix.MSCALE_Y]);
                    imageView.setTranslationX(x - sx);
                    imageView.setTranslationY(y - sy);

                    moveAnimatorParameter = new float[]{
                            imageView.getTranslationX(),
                            imageView.getTranslationY(),
                            imageView.getScaleX(),
                            imageView.getScaleY()
                    };
                    moveAnim(true);
                }
            }
        });
    }

    public void dismiss(){
        if(isAnim || !isShow)
            return;
        if(moveAnimatorParameter != null){
            moveAnim(false);
        }else{
            setVisibility(GONE);
        }
        moveAnimatorParameter = null;
    }

    public boolean isShow() {
        return isShow;
    }

    private void moveAnim(final boolean open){
        AnimatorSet set = new AnimatorSet();
        if(open) {
            set.playTogether(
                    ObjectAnimator.ofFloat(imageView, "translationX", 0).setDuration(animatorDuration),
                    ObjectAnimator.ofFloat(imageView, "translationY", 0).setDuration(animatorDuration),
                    ObjectAnimator.ofFloat(imageView, "scaleX", 1).setDuration(animatorDuration),
                    ObjectAnimator.ofFloat(imageView, "scaleY", 1).setDuration(animatorDuration),
                    ObjectAnimator.ofFloat(this, "alpha", 1).setDuration(animatorDuration)
            );
        }else{
            set.playTogether(
                    ObjectAnimator.ofFloat(imageView, "translationX", moveAnimatorParameter[0]).setDuration(animatorDuration),
                    ObjectAnimator.ofFloat(imageView, "translationY", moveAnimatorParameter[1]).setDuration(animatorDuration),
                    ObjectAnimator.ofFloat(imageView, "scaleX", moveAnimatorParameter[2]).setDuration(animatorDuration),
                    ObjectAnimator.ofFloat(imageView, "scaleY", moveAnimatorParameter[3]).setDuration(animatorDuration),
                    ObjectAnimator.ofFloat(this, "alpha", 0).setDuration(animatorDuration)
            );
        }
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                isAnim = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isAnim = false;
                if(open){
                    isShow = true;
                }else{
                    isShow = false;
                    setVisibility(GONE);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                isAnim = false;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                isAnim = true;
            }
        });
        set.start();
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()){
            case MotionEvent.ACTION_DOWN:
                dismiss();
                break;
        }
        return true;
    }
}
