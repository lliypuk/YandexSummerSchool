package com.home.aleksandrnazarenko.yandextranslator;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class SettingsTab extends Fragment {

    private View rootView;
    //дефолтное значение ключа API, если не было назначено другое



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.setting_layout, container, false);
        return rootView;
    }






    }