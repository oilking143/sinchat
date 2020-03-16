package com.sex8.sinchat.tools.glide;

import android.graphics.drawable.Drawable;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.sex8.sinchat.utils.PhotoUtuls;

public class AdaptiveDrawableImageViewTarget extends DrawableImageViewTarget {
    public AdaptiveDrawableImageViewTarget(ImageView view) {
        super(view);
    }

    @Override
    protected void setResource(@Nullable Drawable resource) {
        if(resource != null) {
            ViewGroup.LayoutParams params = view.getLayoutParams();
            int[] adaptiveSize = PhotoUtuls.getAdaptiveSize(
                    resource.getIntrinsicWidth(), resource.getIntrinsicHeight(), view.getMaxWidth(), view.getMaxHeight());
            params.width = adaptiveSize[0];
            params.height = adaptiveSize[1];
            view.setLayoutParams(params);
        }
        view.setImageDrawable(resource);
    }
}
