package com.sex8.sinchat.entity;

import java.io.File;
import java.io.Serializable;

public class PhotoData implements Serializable {
    public String path;
    public String fileName;
    public float rotated;
    public boolean isLocal = false;
    public boolean isEdit = false;

    public PhotoData(){

    }

    public PhotoData(File file){
        path = file.getPath();
        fileName = file.getName();
    }
}
