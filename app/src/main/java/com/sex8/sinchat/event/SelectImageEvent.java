package com.sex8.sinchat.event;

import com.sex8.sinchat.entity.PhotoData;

import java.util.List;

public class SelectImageEvent {
    private List<PhotoData> selectList;
    public SelectImageEvent(List<PhotoData> selectList){
        setSelectList(selectList);
    }

    public void setSelectList(List<PhotoData> selectList){
        this.selectList = selectList;
    }

    public List<PhotoData> getSelectList(){
        return selectList;
    }
}
