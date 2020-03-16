package com.sex8.sinchat.tools.glide;

 import android.util.Base64;

 import androidx.annotation.NonNull;

 import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.data.DataFetcher;

import java.io.IOException;
import java.nio.ByteBuffer;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Base64DataFetcher implements DataFetcher<ByteBuffer> {
    private final String model;
    private OkHttpClient client = new OkHttpClient();
    private Call call;
    public Base64DataFetcher(String model){
        this.model = model;
    }

    @Override
    public void loadData(@NonNull Priority priority, @NonNull final DataCallback<? super ByteBuffer> callback) {
        Request request = new Request.Builder()
                .url(model)//请求接口。如果需要传参拼接到接口后面。
                .build();
        call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onLoadFailed(e);
            }

            @Override
            public void onResponse(Call call, Response response){
                try {
                    String base64 = response.body().string();
                    int startOfBase64Section = base64.indexOf(',');
                    base64 = base64.substring(startOfBase64Section + 1);
                    byte[] data = Base64.decode(base64, Base64.DEFAULT);
                    ByteBuffer byteBuffer = ByteBuffer.wrap(data);
                    callback.onDataReady(byteBuffer);
                }catch (Exception e){
                    callback.onLoadFailed(e);
                }
            }
        });
    }

    @Override
    public void cleanup() {

    }

    @Override
    public void cancel() {
        call.cancel();
    }

    @NonNull
    @Override
    public Class<ByteBuffer> getDataClass() {
        return ByteBuffer.class;
    }

    @NonNull
    @Override
    public DataSource getDataSource() {
        return DataSource.REMOTE;
    }
}
