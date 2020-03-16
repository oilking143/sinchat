package com.sex8.sinchat.view;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.sex8.sinchat.IMMessageApplication;
import com.sex8.sinchat.R;
import com.sex8.sinchat.event.EmojiButtonClickEvent;
import com.sex8.sinchat.tools.SoftKeyBoardListener;

import org.greenrobot.eventbus.EventBus;

public class ChatBottomSheet extends ConstraintLayout implements SoftKeyBoardListener.OnSoftKeyBoardChangeListener
{
    private BottomSheetBehavior bottomSheetBehavior;
    private boolean isShowed = false;
    private boolean isShowing = false;
    private boolean isKeyBoardShowed = false;
    private boolean isKeyBoardShowing = false;
    private boolean isEmoji = false;
    private View rootLayout;
    private Activity activity;
    private View emojiView;
    private ChatBottomAddMoreView addMoreView;
    private AnimatorSet fade_in, fade_out;

    public ChatBottomSheet(Context context) {
        super(context);
    }

    public ChatBottomSheet(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ChatBottomSheet(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ChatBottomAddMoreView getAddMoreView() {
        return addMoreView;
    }

    public void init(EditText editText) {
        activity = (Activity) getContext();
        rootLayout = activity.findViewById(R.id.rootLayout);
        bottomSheetBehavior = BottomSheetBehavior.from(this);
        bottomSheetBehavior.setBottomSheetCallback(bottomSheetCallback);
        SoftKeyBoardListener.setListener(activity, this);

        editText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && isShowed) {
                    hide();
                }
            }
        });

        emojiView = new View(getContext());
        emojiView.setBackgroundColor(Color.BLUE);
        LayoutInflater inflater = LayoutInflater.from(getContext()); // or (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        emojiView = inflater.inflate(R.layout.item_emoji, null);
        setButton(emojiView);


        addMoreView = new ChatBottomAddMoreView(getContext(), this);
        this.addView(emojiView);
        this.addView(addMoreView);

        fade_in = (AnimatorSet) AnimatorInflater.loadAnimator(activity, R.animator.fade_in);
        fade_out = (AnimatorSet) AnimatorInflater.loadAnimator(activity, R.animator.fade_out);
        fade_in.playTogether(fade_out);
    }

    private BottomSheetBehavior.BottomSheetCallback bottomSheetCallback = new BottomSheetBehavior.BottomSheetCallback() {
        @Override
        public void onStateChanged(@NonNull View view, int i) {
            isShowing = false;
            if (i == BottomSheetBehavior.STATE_EXPANDED) {
                isShowed = true;
            } else if (i == BottomSheetBehavior.STATE_HIDDEN) {
                isShowed = false;
            }
        }

        @Override
        public void onSlide(@NonNull View view, float v) {

        }
    };

    public void toggle() {
        switch (bottomSheetBehavior.getState()) {
            case BottomSheetBehavior.STATE_COLLAPSED:
                if (isShowed)
                    hide();
                else
                    show();
                break;
            case BottomSheetBehavior.STATE_HIDDEN:
                show();
                break;
            case BottomSheetBehavior.STATE_EXPANDED:
                hide();
                break;
        }
    }

    public void setButton(View emojiView) {

        RecyclerView recyclerView=(RecyclerView)emojiView.findViewById(R.id.recyclerview);
        GridLayoutManager layoutManage = new GridLayoutManager(getContext(), 6, GridLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManage);
        emojiAdapter adapter=new emojiAdapter();
        recyclerView.setAdapter(adapter);



    }

    public void show() {
        isShowing = true;
        if (isKeyBoardShowed) {
            hideKeyboard();
        } else {
            bottomSheetBehavior.setHideable(false);
            bottomSheetBehavior.setPeekHeight(getHeight());
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    public void hide() {
        bottomSheetBehavior.setHideable(true);
        bottomSheetBehavior.setPeekHeight(0);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    public void emojiToggle() {

        if (isShowed && isEmoji) {
            hide();
        } else {
            isEmoji = true;
            this.removeAllViews();
            this.addView(emojiView);
            if (!isShowed) {
                emojiView.setVisibility(VISIBLE);
                emojiView.setAlpha(1f);
                addMoreView.setVisibility(INVISIBLE);
                addMoreView.setAlpha(0f);
                show();
            } else {
                emojiView.setVisibility(VISIBLE);
                emojiView.setAlpha(1f);
                fade_in.setTarget(emojiView);
                fade_out.setTarget(addMoreView);
                fade_in.start();
            }
        }
    }

    public void addToggle() {

        if (isShowed && !isEmoji) {
            hide();
        } else {
            isEmoji = false;
            this.removeAllViews();
            this.addView(addMoreView);
            if (!isShowed) {
                emojiView.setVisibility(INVISIBLE);
                emojiView.setAlpha(0f);
                addMoreView.setVisibility(VISIBLE);
                addMoreView.setAlpha(1f);
                show();
            } else {
                addMoreView.setVisibility(VISIBLE);
                addMoreView.setAlpha(1f);
                fade_in.setTarget(addMoreView);
                fade_out.setTarget(emojiView);
                fade_in.start();
            }
        }
    }

    public void hideKeyboard() {
        if (rootLayout == null)
            rootLayout = ((View) getParent());
        IMMessageApplication.hideKeyboard(getContext(), rootLayout);
    }

    @Override
    public void keyBoardShow(int height) {
        isKeyBoardShowed = true;
        hide();
    }

    @Override
    public void keyBoardHide(int height) {
        isKeyBoardShowed = false;
        hideKeyboard();
        if (isShowing)
            show();
    }


    private class emojiAdapter extends RecyclerView.Adapter{

        private View.OnClickListener itemClick;
        String[] emojis=getContext().getResources().getStringArray(R.array.emoji);


        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View convertView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_emoji_layout, viewGroup, false);

            return new ViewHolder(convertView);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            final ViewHolder vh = (ViewHolder) viewHolder;

            vh.emojiText.setText(emojis[vh.getAdapterPosition()]);

            vh.emojiText.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    EventBus.getDefault().post(new EmojiButtonClickEvent(vh.getAdapterPosition()
                            ,emojis[vh.getAdapterPosition()]));

                }
            });

        }

        @Override
        public int getItemCount() {
            return emojis.length;
        }

        class ViewHolder extends RecyclerView.ViewHolder{
            public TextView emojiText;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                itemView.setTag(this);
                emojiText = itemView.findViewById(R.id.emojiText);

            }
        }

    }



}
