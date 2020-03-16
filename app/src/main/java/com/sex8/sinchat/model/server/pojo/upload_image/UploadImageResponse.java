package com.sex8.sinchat.model.server.pojo.upload_image;

import com.sex8.sinchat.model.server.pojo.ResponseBase;

public class UploadImageResponse extends ResponseBase {

    /**
     * error : {"code":0,"message":"ok","cache":1}
     * data : {"md5Token":"5f87b154e564ddab5751d6f3c19c53fa","sourcePath":"5f87b154e564ddab5751d6f3c19c53fa?p=0","thumbnailPath":"5f87b154e564ddab5751d6f3c19c53fa?p=0"}
     */

    private ErrorBean error;
    private DataBean data;

    public ErrorBean getError() {
        return error;
    }

    public void setError(ErrorBean error) {
        this.error = error;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class ErrorBean {
        /**
         * code : 0
         * message : ok
         * cache : 1
         */

        private int code;
        private String message;
        private int cache;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public int getCache() {
            return cache;
        }

        public void setCache(int cache) {
            this.cache = cache;
        }
    }

    public static class DataBean {
        /**
         * md5Token : 5f87b154e564ddab5751d6f3c19c53fa
         * sourcePath : 5f87b154e564ddab5751d6f3c19c53fa?p=0
         * thumbnailPath : 5f87b154e564ddab5751d6f3c19c53fa?p=0
         */

        private String md5Token;
        private String sourcePath;
        private String thumbnailPath;

        public String getMd5Token() {
            return md5Token;
        }

        public void setMd5Token(String md5Token) {
            this.md5Token = md5Token;
        }

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
    }
}
