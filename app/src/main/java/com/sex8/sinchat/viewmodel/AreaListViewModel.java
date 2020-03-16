package com.sex8.sinchat.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.sex8.sinchat.IMMessageApplication;
import com.sex8.sinchat.model.dataBase.Entity.Area_List;
import com.sex8.sinchat.model.dataBase.IMRepository;

import java.util.List;

public class AreaListViewModel  extends AndroidViewModel {

    private IMRepository mRepository;
    private LiveData<List<Area_List>> areaList;

    public AreaListViewModel() {
        super(IMMessageApplication.application);
        mRepository=IMRepository.getInstance();
        areaList=mRepository.getAreaList();
    }

    public LiveData<List<Area_List>> getAreaList() {
        return areaList;
    }

    public void setAreaList(LiveData<List<Area_List>> areaList) {
        this.areaList = areaList;
    }


}
