package com.sex8.sinchat.fragment;

import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.Group;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.sex8.sinchat.R;
import com.sex8.sinchat.dialog.LocationDialog;
import com.sex8.sinchat.model.IMData;
import com.sex8.sinchat.tools.OnClickLimitListener;
import com.sex8.sinchat.viewmodel.AreaListViewModel;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;


public class PartyFragment extends BaseFragment {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private PartyPageAdapter pageAdapter;
    private boolean first = true;
    private ImageView locationImage;
    private TextView locationText;
    private LocationDialog locationDialog;
    private Group locationGroup;
    private AreaListViewModel viewModel;
    private JSONArray wholeRoomList=new JSONArray();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(first)
            return inflater.inflate(R.layout.fragment_party, container, false);
        else
            return thisView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(first) {

            findViews();
            setListeners();
            init();
            activity.handler.post(new Runnable(){
                @Override
                public void run() {
                    viewPager.setCurrentItem(0);
                }
            });
        }
    }


    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    public void findViews() {
        tabLayout = thisView.findViewById(R.id.tabLayout);
        viewPager = thisView.findViewById(R.id.viewPager);
        locationImage = thisView.findViewById(R.id.locationImage);
        locationText = thisView.findViewById(R.id.locationText);
        locationGroup = thisView.findViewById(R.id.locationGroup);
    }

    public void init() {
        pageAdapter = new PartyPageAdapter(fragmentManager);
        viewPager.setAdapter(pageAdapter);
        tabLayout.setupWithViewPager(viewPager);
     //   ArrayList<Map<String, String>> data=SQLiteDBUtils.getInstance(getContext()).QueryRoomList_Area();

        viewModel = new ViewModelProvider(this).get(AreaListViewModel.class);

        viewModel.getAreaList().observe(this,area_lists -> {
            locationDialog = new LocationDialog(activity,area_lists);
        });

        viewModel.getAreaList().observe(this,area_lists -> {
            for(int i=0;i<area_lists.size();i++)
            {

                int item=area_lists.get(i).getChatRoomId();
                wholeRoomList.put(item);
            }

            if(first)
            {
                first = false;
                Log.d("PartyWhole",wholeRoomList.toString());
                IMData.getInstance().updatePartyRoomListDetail(wholeRoomList.toString());
            }


        });


    }



    public void setListeners() {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {

                if(i == 0){
                    locationGroup.setVisibility(View.VISIBLE);
                }else{
                    locationGroup.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        locationImage.setOnClickListener(btnClick);
        locationText.setOnClickListener(btnClick);
    }

    private OnClickLimitListener btnClick = new OnClickLimitListener() {
        @Override
        public void onClickLimit(View v) {
            switch (v.getId()){
                case R.id.locationImage:
                case R.id.locationText:
                    locationDialog.show();
                    break;
            }
        }
    };


    private class PartyPageAdapter extends FragmentPagerAdapter {
        private SparseArray<PartyChildFragment> fragmentSparseArray = new SparseArray<>();
        private String[] pagerTitle = new String[]{
                getString(R.string.title_party),
                getString(R.string.title_account)
        };

        public PartyPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            if(fragmentSparseArray.get(i) == null){
                PartyChildFragment fragment = new PartyChildFragment();
                Bundle bundle = new Bundle();
                if(i == 0){
                    bundle.putBoolean("group", true);
                    fragment.setArguments(bundle);
                }
                fragmentSparseArray.put(i, fragment);
            }

            return fragmentSparseArray.get(i);
        }

        @Override
        public int getCount() {
            return pagerTitle.length;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return pagerTitle[position];
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return POSITION_NONE;
        }
    }
}
