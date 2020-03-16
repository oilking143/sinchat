package com.sex8.sinchat.activity;


import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.sex8.sinchat.IMMessageApplication;
import com.sex8.sinchat.R;
import com.sex8.sinchat.fragment.ConfirmCodeFragment;
import com.sex8.sinchat.fragment.PasswordFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Launch_Activity_new extends BaseActivity implements View.OnClickListener {


    @BindView(R.id.viewpager)
    ViewPager viewpager;
    @BindView(R.id.head_bg)
    ImageView headBg;
    @BindView(R.id.switcher)
    LinearLayout switcher;
    @BindView(R.id.password_switcher)
    TextView passwordSwitcher;
    @BindView(R.id.confirm_switcher)
    TextView confirmSwitcher;
    @BindView(R.id.password_light)
    ImageView passwordLight;
    @BindView(R.id.confirm_light)
    ImageView confirmLight;
    @BindView(R.id.account_list)
    Spinner accountList;
    @BindView(R.id.linearLayout2)
    LinearLayout linearLayout2;
    @BindView(R.id.btnGroup)
    LinearLayout btnGroup;
    private LaunchPagerAdapter adapter;
    PasswordFragment passwordFragment;
    ConfirmCodeFragment confirmCodeFragment;
    IMMessageApplication user;
    private ArrayAdapter<CharSequence> nAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_launch_new);
        ButterKnife.bind(this);
        init();
        setListeners();

    }


    @Override
    protected void onResume() {
        super.onResume();
        handler.postDelayed(new Runnable() {
            public void run() {
//                    meteorView.stop();
                ObjectAnimator objectAnimator1 = ObjectAnimator.ofFloat(headBg, "alpha", 1f, 0f);
                ObjectAnimator objectAnimator2 = ObjectAnimator.ofFloat(viewpager, "alpha", 0f, 1f);
                ObjectAnimator objectAnimator3 = ObjectAnimator.ofFloat(switcher, "alpha", 0f, 1f);
                AnimatorSet set = new AnimatorSet();
                set.setTarget(headBg);
                set.setDuration(800);
                set.playTogether(objectAnimator1, objectAnimator2, objectAnimator3);
                set.start();
            }
        }, 2000);
    }

    @Override
    public void findViews() {

    }

    @Override
    public void init() {
        ArrayList<Fragment> FragArray = new ArrayList<>();
        passwordFragment = new PasswordFragment();
        confirmCodeFragment = new ConfirmCodeFragment();

        FragArray.add(passwordFragment);
        FragArray.add(confirmCodeFragment);

        adapter = new LaunchPagerAdapter(getSupportFragmentManager(), FragArray);
        viewpager.setAdapter(adapter);

        user=(IMMessageApplication)IMMessageApplication.getGlobalContext();
        if(user.getImKey().length()<5)
        {
            accountList.setVisibility(View.VISIBLE);
            nAdapter=ArrayAdapter.createFromResource(
                    this, R.array.test_name, android.R.layout.simple_spinner_item );
            accountList.setAdapter(nAdapter);
        }

    }

    @Override
    public void setListeners() {
        passwordSwitcher.setOnClickListener(this);
        confirmSwitcher.setOnClickListener(this);
        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int postiton, float v, int i1) {

            }

            @Override
            public void onPageSelected(int postiton) {
                switch (postiton) {
                    case 0:
                        passwordSwitcher.setTypeface(passwordSwitcher.getTypeface(), Typeface.BOLD);
                        confirmSwitcher.setTypeface(null, Typeface.NORMAL);
                        passwordSwitcher.setTextColor(getResources().getColor(R.color.pure_white));
                        confirmSwitcher.setTextColor(getResources().getColor(R.color.dark_text));
                        passwordSwitcher.setTextSize(TypedValue.COMPLEX_UNIT_SP, (float) 30);
                        confirmSwitcher.setTextSize(TypedValue.COMPLEX_UNIT_SP, (float) 19.04);
                        passwordLight.setVisibility(View.VISIBLE);
                        confirmLight.setVisibility(View.INVISIBLE);
                        break;


                    case 1:
                        passwordSwitcher.setTypeface(null, Typeface.NORMAL);
                        confirmSwitcher.setTypeface(confirmSwitcher.getTypeface(), Typeface.BOLD);
                        passwordSwitcher.setTextColor(getResources().getColor(R.color.dark_text));
                        confirmSwitcher.setTextColor(getResources().getColor(R.color.pure_white));
                        passwordSwitcher.setTextSize(TypedValue.COMPLEX_UNIT_SP, (float) 19.04);
                        confirmSwitcher.setTextSize(TypedValue.COMPLEX_UNIT_SP, (float) 30);
                        passwordLight.setVisibility(View.INVISIBLE);
                        confirmLight.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        accountList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                switch (position)
                {
                    case 0:
                        user.setLoginUid(getResources().getStringArray(R.array.test_account)[0]);
                        user.setImKey(getResources().getStringArray(R.array.test_imkey)[0]);

                        break;
                    case 1:

                        user.setLoginUid(getResources().getStringArray(R.array.test_account)[1]);
                        user.setImKey(getResources().getStringArray(R.array.test_imkey)[1]);
                        break;
                    case 2:
                        user.setLoginUid(getResources().getStringArray(R.array.test_account)[2]);
                        user.setImKey(getResources().getStringArray(R.array.test_imkey)[2]);
                        break;

                    case 3:
                        user.setLoginUid(getResources().getStringArray(R.array.test_account)[3]);
                        user.setImKey(getResources().getStringArray(R.array.test_imkey)[3]);
                        break;

                    case 4:
                        user.setLoginUid(getResources().getStringArray(R.array.test_account)[4]);
                        user.setImKey(getResources().getStringArray(R.array.test_imkey)[4]);
                        break;

                    case 5:
                        user.setLoginUid(getResources().getStringArray(R.array.test_account)[5]);
                        user.setImKey(getResources().getStringArray(R.array.test_imkey)[5]);
                        break;

                    case 6:
                        user.setLoginUid(getResources().getStringArray(R.array.test_account)[6]);
                        user.setImKey(getResources().getStringArray(R.array.test_imkey)[6]);
                        break;

                    case 7:
                        user.setLoginUid(getResources().getStringArray(R.array.test_account)[7]);
                        user.setImKey(getResources().getStringArray(R.array.test_imkey)[7]);
                       break;

                    case 8:
                        user.setLoginUid(getResources().getStringArray(R.array.test_account)[8]);
                        user.setImKey(getResources().getStringArray(R.array.test_imkey)[8]);

                          break;

                    case 9:
                        user.setLoginUid(getResources().getStringArray(R.array.test_account)[9]);
                        user.setImKey(getResources().getStringArray(R.array.test_imkey)[9]);

                         break;

                    case 10:
                        user.setLoginUid(getResources().getStringArray(R.array.test_account)[10]);
                        user.setImKey(getResources().getStringArray(R.array.test_imkey)[10]);

                         break;

                    case 11:
                        user.setLoginUid(getResources().getStringArray(R.array.test_account)[11]);
                        user.setImKey(getResources().getStringArray(R.array.test_imkey)[11]);

                         break;

                    case 12:
                        user.setLoginUid(getResources().getStringArray(R.array.test_account)[12]);
                        user.setImKey(getResources().getStringArray(R.array.test_imkey)[12]);

                        break;

                    case 13:
                        user.setLoginUid(getResources().getStringArray(R.array.test_account)[13]);
                        user.setImKey(getResources().getStringArray(R.array.test_imkey)[13]);

                         break;

                    case 14:
                        user.setLoginUid(getResources().getStringArray(R.array.test_account)[14]);
                        user.setImKey(getResources().getStringArray(R.array.test_imkey)[14]);

                         break;

                    case 15:
                        user.setLoginUid(getResources().getStringArray(R.array.test_account)[15]);
                        user.setImKey(getResources().getStringArray(R.array.test_imkey)[15]);

                         break;

                    case 16:
                        user.setLoginUid(getResources().getStringArray(R.array.test_account)[16]);
                        user.setImKey(getResources().getStringArray(R.array.test_imkey)[16]);

                         break;

                    case 17:
                        user.setLoginUid(getResources().getStringArray(R.array.test_account)[17]);
                        user.setImKey(getResources().getStringArray(R.array.test_imkey)[17]);

                         break;

                    case 18:
                        user.setLoginUid(getResources().getStringArray(R.array.test_account)[18]);
                        user.setImKey(getResources().getStringArray(R.array.test_imkey)[18]);

                        break;

                    case 19:
                        user.setLoginUid(getResources().getStringArray(R.array.test_account)[19]);
                        user.setImKey(getResources().getStringArray(R.array.test_imkey)[19]);

                        break;

                    case 20:
                        user.setLoginUid(getResources().getStringArray(R.array.test_account)[20]);
                        user.setImKey(getResources().getStringArray(R.array.test_imkey)[20]);

                        break;

                    case 21:
                        user.setLoginUid(getResources().getStringArray(R.array.test_account)[21]);
                        user.setImKey(getResources().getStringArray(R.array.test_imkey)[21]);
                        break;

                    case 22:
                        user.setLoginUid(getResources().getStringArray(R.array.test_account)[22]);
                        user.setImKey(getResources().getStringArray(R.array.test_imkey)[22]);

                        break;

                    case 23:
                        user.setLoginUid(getResources().getStringArray(R.array.test_account)[23]);
                        user.setImKey(getResources().getStringArray(R.array.test_imkey)[23]);

                        break;

                    case 24:
                        user.setLoginUid(getResources().getStringArray(R.array.test_account)[24]);
                        user.setImKey(getResources().getStringArray(R.array.test_imkey)[24]);

                        break;




                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.password_switcher:
                viewpager.setCurrentItem(0);
                passwordSwitcher.setTypeface(passwordSwitcher.getTypeface(), Typeface.BOLD);
                confirmSwitcher.setTypeface(null, Typeface.NORMAL);
                passwordSwitcher.setTextColor(getResources().getColor(R.color.pure_white));
                confirmSwitcher.setTextColor(getResources().getColor(R.color.dark_text));
                passwordSwitcher.setTextSize(TypedValue.COMPLEX_UNIT_SP, (float) 30);
                confirmSwitcher.setTextSize(TypedValue.COMPLEX_UNIT_SP, (float) 19.04);
                passwordLight.setVisibility(View.VISIBLE);
                confirmLight.setVisibility(View.INVISIBLE);
                break;


            case R.id.confirm_switcher:
                viewpager.setCurrentItem(1);
                passwordSwitcher.setTypeface(null, Typeface.NORMAL);
                confirmSwitcher.setTypeface(confirmSwitcher.getTypeface(), Typeface.BOLD);
                passwordSwitcher.setTextColor(getResources().getColor(R.color.dark_text));
                confirmSwitcher.setTextColor(getResources().getColor(R.color.pure_white));
                passwordSwitcher.setTextSize(TypedValue.COMPLEX_UNIT_SP, (float) 19.04);
                confirmSwitcher.setTextSize(TypedValue.COMPLEX_UNIT_SP, (float) 30);
                passwordLight.setVisibility(View.INVISIBLE);
                confirmLight.setVisibility(View.VISIBLE);
                break;


        }
    }

    public class LaunchPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> mFragmentList = new ArrayList<>();

        public LaunchPagerAdapter(FragmentManager fm, ArrayList<Fragment> mList) {
            super(fm);
            mFragmentList = mList;
        }


        @Override
        public Fragment getItem(int position) {
            return this.mFragmentList.get(position);
        }


        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }


    }


}
