package com.home.aleksandrnazarenko.yandextranslator;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class Pager extends FragmentStatePagerAdapter {

    int tabCount;

    public Pager(FragmentManager fm, int tabCount) {
        super(fm);
        this.tabCount= tabCount;
    }

    @Override
    public Fragment getItem(int position) {
        //Returning the current tabs
        switch (position) {
            case 0:
                TranslateTab tab1 = new TranslateTab();
                return tab1;
            case 1:
                History_Favorite_tabs tab2 = new History_Favorite_tabs();
                return tab2;
            case 2:
                SettingsTab tab3 = new SettingsTab();
                return tab3;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }


}