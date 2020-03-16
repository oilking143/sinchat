package com.sex8.sinchat.tools.glide;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.signature.ObjectKey;

import java.nio.ByteBuffer;

public class Base64ModelLoader implements ModelLoader<String, ByteBuffer> {
    @Nullable
    @Override
    public LoadData<ByteBuffer> buildLoadData(@NonNull String model, int width, int height, @NonNull Options options) {
        return new LoadData<>(new ObjectKey(model), new Base64DataFetcher(model));
    }

    @Override
    public boolean handles(@NonNull String url) {
//        if(url.contains(IMData.getInstance().getWebSocketInfo().getFileHost())) {
//            return true;
//        }else{
//            return false;
//        }
        return false;
    }


}
