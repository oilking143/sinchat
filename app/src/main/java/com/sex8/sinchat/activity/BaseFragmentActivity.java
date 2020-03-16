package com.sex8.sinchat.activity;

import android.os.Bundle;


import androidx.annotation.IdRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.HashMap;
import java.util.Map;

public abstract class BaseFragmentActivity extends BaseActivity {
    public FragmentManager fragmentManager;
    private int containerRes;
    private Map<String, Fragment> fragmentMap = new HashMap<>();
    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentManager = getSupportFragmentManager();
    }

    public void setContainerRes(@IdRes int containerRes) {
        this.containerRes = containerRes;
    }

    public Fragment fragmentChanged(Class fragmentClass, String tag){
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        if(currentFragment != null && !tag.equals(currentFragment.getTag())){
            transaction.detach(currentFragment);
        }

        currentFragment = fragmentMap.get(tag);
        if(currentFragment != null && tag.equals(currentFragment.getTag())){
            transaction.attach(currentFragment);
        }else{
            currentFragment = Fragment.instantiate(this, fragmentClass.getName());
            fragmentMap.put(tag, currentFragment);
            transaction.add(containerRes, currentFragment, tag);
        }
        transaction.commit();
        return currentFragment;
    }
}
