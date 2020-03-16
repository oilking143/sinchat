package com.sex8.sinchat.model.server.pojo.upload_image;

import com.google.gson.annotations.SerializedName;

public class UploadImageData {

    @SerializedName("sourcePath")
    private String sourcePath;

    @SerializedName("thumbnailPath")
    private String thumbnailPath;

    @SerializedName("md5Token")
    private String md5Token;


    public String getSourcePath() {
        return sourcePath;
    }

    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }

    public String getThumbnailPath() {
        return thumbnailPath;
    }

    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }

    public String getMd5Token() {
        return md5Token;
    }

    public void setMd5Token(String md5Token) {
        this.md5Token = md5Token;
    }
}
