package com.home.aleksandrnazarenko.yandextranslator;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SearchView;

import com.home.aleksandrnazarenko.yandextranslator.data.DbHelper;
import com.home.aleksandrnazarenko.yandextranslator.data.DbMethods;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;


public class FavoriteTab extends Fragment {

    private View rootView;
    private DbHelper mDbHelper;
    RecyclerAdapter Adapter;

    //Создаем список вьюх которые будут создаваться
    private List<MemoryWords> TranslateWord;

    private LinearLayout linear;
    private RecyclerView mRecyclerView;
    private SearchView SearchFieldFavorite;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.favorite_layout, container, false);
        mDbHelper = new DbHelper(rootView.getContext());
        SearchFieldFavorite=(SearchView) rootView.findViewById(R.id.searchView);
        //инициализировали наш массив с edittext.aьи

        //инициализируем массив слов переведенных
        TranslateWord =new ArrayList<MemoryWords>();
        TranslateWord=DbMethods.getExistFavorite(mDbHelper,TranslateWord);
                /*Строим в обратоном порядке, что бы новое было в верху списка*/
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        Adapter = new RecyclerAdapter(TranslateWord, R.layout.favorite_row_layout);
        mRecyclerView.setAdapter(Adapter);

        SearchFieldFavorite.setQueryHint("Найти в избранном");

        //Вешаем обработку введеных символов в поиск и сразу ищем
        SearchFieldFavorite.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
        public boolean onQueryTextSubmit(String query) {
            SearchFieldFavorite.clearFocus();
            return true;
        }
        public boolean onQueryTextChange(String newText) {
        Adapter.filter(newText);
            return true;
        }
        });

        return rootView;
    }


    //метод обновляет экран избранного, вызывается когда приходит сообщение о изменение БД
    private void RefreshFavorite()
    {
        //стираем строку поиска, иначе не красив
        SearchFieldFavorite.setQuery("", false);
        Adapter.RefreshData();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(FragmentDataSync event){
        TranslateWord=DbMethods.getExistFavorite(mDbHelper,TranslateWord);
        RefreshFavorite();
    }


    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }


    public void onResume()
    {
        super.onResume();
        EventBus.getDefault().register(this);

    }





    //класс для массива элеметонов recyclervuew
    public static class MemoryWords {

        private String Source;
        private String Translate;
        private String Direction;
        private int Favorite;

        public MemoryWords(String Source,String Translate,String Direction,int Favorite){
            this.Source = Source;
            this.Translate = Translate;
            this.Direction = Direction;
            this.Favorite = Favorite;
        }


        public String getSource()
        {
            return Source;
        }

        public String getTranslate()
        {
            return Translate;
        }

        public String getDirection()
        {
            return Direction;
        }

        public int getFavorite()
        {
            return Favorite;
        }

        public void setSource(String Source) {
            this.Source = Source;
        }
        public void setTranslate(String Translate) {
            this.Translate = Translate;
        }
        public void setDirection(String Direction) {
            this.Direction = Direction;
        }
        public void setFavorite(int Favorite) {
            this.Favorite = Favorite;
        }
    }



    }