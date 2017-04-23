package com.home.aleksandrnazarenko.yandextranslator;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.home.aleksandrnazarenko.yandextranslator.data.DbHelper;


public class History_Favorite_tabs extends Fragment {

    private View rootView;
    private DbHelper mDbHelper;

    //This is our tablayout
    private TabLayout tabLayout;

    //This is our viewPager
    private ViewPager viewPager;

    Pager_history_favotite adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.historyfavorite_layout, container, false);



        return rootView;//inflater.inflate(R.layout.translate_layout, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);


        //инициализировали наш массив с edittext.aьи
        ///Adding toolbar to the activity
//        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar1);
////        setSupportActionBar(toolbar);
//        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        //Initializing the tablayout
        tabLayout = (TabLayout) view.findViewById(R.id.tabLayout1);
        //Initializing viewPager
        viewPager = (ViewPager)view.findViewById(R.id.pager1);


        // tabLayout.setupWithViewPager(viewPager);


        tabLayout.addTab(tabLayout.newTab().setText("История"));
        tabLayout.addTab(tabLayout.newTab().setText("Избранное"));


        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);



        //Creating our pager adapter
        adapter = new Pager_history_favotite(getActivity().getSupportFragmentManager(), tabLayout.getTabCount());

        //Adding adapter to pager
        viewPager.setAdapter(adapter);


        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition(),true);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }


}
