package com.home.aleksandrnazarenko.yandextranslator;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.home.aleksandrnazarenko.yandextranslator.data.DbHelper;
import com.home.aleksandrnazarenko.yandextranslator.data.DbMethods;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;


public class TranslateTab extends Fragment {
    //Поле ввода слов
    private EditText TypeFieldEdTxt;
    //Сюда выводим результат
    private TextView resultTxt;
    //Иконка избраного(добавить/удалить)
    private ImageButton FavoriteBtn;
    //В эту переменную сохраняем, есть ли оно уже в избранном
    private Boolean varWordFavorite = false;

    //адаптеры выкидух языков
    private ArrayAdapter<String> spinnerSourseLangAdapter;
    private ArrayAdapter<String> spinnerTranslateLangAdapter;

    //массивы для названий языков и направлений
    private Map<String, String> Langs;
    private List<String> Dirs;

    private Spinner SoursLang;
    private Spinner TranslateLang;
    //БД враппер
    private DbHelper mDbHelper;

    //URL куда шлем post запросы
    private final String URL = "https://translate.yandex.net";
    //KEY для API Яндекс Переводчик
    private String KEY="trnsl.1.1.20170318T094931Z.a209cf040ae50a00.a551516f891d151bf66a94b48aa57500408eb467";

    private Gson gson = new GsonBuilder().create();
    private Map<String, String> mapJson;//, mapGetResponse;

    private View rootView;
    //запомолняется при совершение перевода и попадет в базу
    private String DirectionTranslate;
    //сохраняем выбранный язык
    private String SelectSrcTranslate="";

    /*Интерфейс для перевода по API*/
    public interface TranslateAPI {
        @FormUrlEncoded
        @POST("/api/v1.5/tr.json/translate")
        Call<TranslateJSON> getTranslate(@FieldMap Map<String, String> map);
    }

    /*Интерфейс для получения направлений перевода по API*/
    public interface DirectionTranslateAPI {
        @FormUrlEncoded
        @POST("api/v1.5/tr.json/getLangs")
        Call<DirectionTranslateJSON> getDirectionTranslate(@FieldMap Map<String, String> map);
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

    /*Для синхронизации кнопки избранного на экране перевода*/
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(FragmentDataSync event){
        String evSrc = event.source;
        String evTrn = event.translate;
        String evDrc = event.direction;

        String Word = TypeFieldEdTxt.getText().toString();
        String  Direction= DirectionTranslate.toUpperCase();
        String Translate = resultTxt.getText().toString();


        int fav = event.favorite;

           if(evSrc.equals(Word) && evDrc.equals(Direction) && evTrn.equals(Translate) && event.position>0)
        {

            if (fav==0){
                varWordFavorite = false;
            }
            else
            {
                varWordFavorite = true;
            }
            DrawFavoriteIco();
        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.translate_layout, container, false);

        TypeFieldEdTxt = (EditText) rootView.findViewById(R.id.TypeFieldEdTxt);
        FavoriteBtn = (ImageButton) rootView.findViewById(R.id.FavoriteBtn);

        resultTxt = (TextView) rootView.findViewById(R.id.resultTxt);
        mDbHelper = new DbHelper(rootView.getContext());


        SoursLang = (Spinner) rootView.findViewById(R.id.SoursLang);
        TranslateLang = (Spinner) rootView.findViewById(R.id.TranslateLang);

        /*Если поставить мультилайн, но не срабатывает событие enter*/
        TypeFieldEdTxt.setHorizontallyScrolling(false);
        TypeFieldEdTxt.setMaxLines(Integer.MAX_VALUE);


        ImageButton BtnClear = (ImageButton) rootView.findViewById(R.id.ClearBtn);

        //при нажатие на крестик в поле ввода, убереме весь текст и иконку избранного
        BtnClear.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                TypeFieldEdTxt.setText("");
                resultTxt.setText("");
                FavoriteBtn.setVisibility(View.INVISIBLE);
            }
        });

        //Вешаем обработку клика по иконке избраного
        FavoriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FavoriteBtnClick();
                int favorite=0;
                if (varWordFavorite){
                    favorite=1;
                }
            }
        });

        //Вешаем на поле ввода слов Listener, что бы сделать перевод по нажатию на Enter
        TypeFieldEdTxt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE && TypeFieldEdTxt.getText().toString().length()>0)
                {
                    Translate();
                }
                return false;
            }
        });


        /*Ловим изменение выкидухи языка с которого переводим*/
        SoursLang.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                SpinnerTranslateLangFill();
                SelectSrcTranslate = SoursLang.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });

        TranslateLang.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (TypeFieldEdTxt.getText().toString().length() > 0) {
                    Translate();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });
        GetDiretionTranslate();
        return rootView;
    }




    //Метод добавляет или убирает слово из избраного
    public void FavoriteBtnClick() {
        /*Необходимо записывать переводимое слово в переменную, так как пользователь может ее стереть до добавления в избранное*/

        String Word = TypeFieldEdTxt.getText().toString();
        String Direction = DirectionTranslate.toUpperCase();
        String Translate = resultTxt.getText().toString();

        String ToastMsg;

        //Если уже есть в базе - удаляем, если нет - добавяляем
        if (varWordFavorite) {
            ToastMsg = "Слово: " + Translate + " удалено из избранного!";
            DbMethods.deleteFavorite(Word, Translate, Direction,mDbHelper);
            //deleteFavorite(Word, Translate, Direction);
            varWordFavorite = false;
        } else {
            ToastMsg = "Слово: " + Translate + " добавлено в избранное!";
            DbMethods.insertFavorite(Word, Translate, Direction,mDbHelper);
            varWordFavorite = true;
        }

        ///Рисуем иконку избраного
        DrawFavoriteIco();

        /*Показываем уведомление о работе с избраным*/
        Toast toast = Toast.makeText(rootView.getContext(), ToastMsg, Toast.LENGTH_SHORT);
        toast.show();
        EventBus.getDefault().post(new FragmentDataSync("FavoriteAdd",-1,null,null,null,0));
    }

    //Метод рисует иконку избраного, в зависимоти от есть оно в избраном или нет
    public void DrawFavoriteIco() {
        FavoriteBtn.setVisibility(View.VISIBLE);
        if (varWordFavorite) {
            FavoriteBtn.setImageResource(R.drawable.ic_favorite_black_24dp);
            //рисуем иконку
        } else {
            FavoriteBtn.setImageResource(R.drawable.ic_favorite_border_black_24dp);
        }
    }

    //Метод получения перевода по API Яндекс Переводчик
    public void Translate() {
        /*Делаем перевод, только если есть значения в двух выкидухах языков*/

        if (SoursLang.getCount()>0 && SoursLang.getCount()>0) {

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();

            DirectionTranslate = getLangKeyByValue(SoursLang.getSelectedItem().toString()) + "-" + getLangKeyByValue(TranslateLang.getSelectedItem().toString());

            mapJson = new HashMap<>();
            mapJson.put("key", KEY);
            mapJson.put("text", TypeFieldEdTxt.getText().toString());
            mapJson.put("lang", DirectionTranslate);
            mapJson.put("format", "html");


            TranslateAPI server = retrofit.create(TranslateAPI.class);
            server.getTranslate(mapJson).enqueue(new Callback<TranslateJSON>() {
                @Override
                public void onResponse(Call<TranslateJSON> call, Response<TranslateJSON> response) {
                    try {
                        TranslateJSON data = response.body();
                        String result_translate = data.getText().toString().replaceAll("\\[(.*?)\\]", "$1");//Убираем из текста перевода [ ] в начале и в конце строки
                        resultTxt.setText(result_translate);
                        //Проверяем есть ли это слово в избраном и пишем в переменную
                        Boolean Fav = DbMethods.getExistFavorite(TypeFieldEdTxt.getText().toString(), result_translate, DirectionTranslate, mDbHelper);
                        if (Fav) {
                            varWordFavorite = true;
                        } else {
                            varWordFavorite = false;
                        }
                        ///Рисуем иконку избраного
                        DrawFavoriteIco();
                        //добавялем в историю
                        DbMethods.insertHistory(TypeFieldEdTxt.getText().toString(), result_translate, DirectionTranslate, mDbHelper);
                    } catch (NullPointerException e) {
                        resultTxt.setText("ОШИБКА ПОЛУЧЕНИЯ ПЕРЕВОДА");
                    }
                }

                @Override
                public void onFailure(Call<TranslateJSON> call, Throwable t) {
                    resultTxt.setText("Ошибка! Возможно проблемы с интернетом=(");

                }
            });
        }
        else{
            resultTxt.setText("Не могу сделать перевод, проверьте API ключ в настройках и соединение с интернетом =(");
        }
    }

    //Метод получения направлений перевода по API Яндекс Переводчик
    public void GetDiretionTranslate() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        mapJson = new HashMap<>();
        mapJson.put("key", KEY);
        mapJson.put("ui", "ru");


        DirectionTranslateAPI server = retrofit.create(DirectionTranslateAPI.class);
        server.getDirectionTranslate(mapJson).enqueue(new Callback<DirectionTranslateJSON>() {
            @Override
            public void onResponse(Call<DirectionTranslateJSON> call, Response<DirectionTranslateJSON> response) {
                try {
                    DirectionTranslateJSON data = response.body();
                    Langs = data.getLangs();
                    Dirs = data.getDirs();
                    SpinnerSourseLangFill();
                }catch (NullPointerException e)
                {
                    resultTxt.setText("ОШИБКА ПОЛУЧЕНИЯ НАПРАВЛЕНИЙ ПЕРЕВОДА: Проверьте ключ API в настройках");
                }
            }
            @Override
            public void onFailure(Call<DirectionTranslateJSON> call, Throwable t) {
                resultTxt.setText("ОШИБКА ПОЛУЧЕНИЯ НАПРАВЛЕНИЙ ПЕРЕВОДА");
            }
        });

    }

    //метод заполняет spinner язык источника
    private void SpinnerSourseLangFill() {
        ArrayList<String> langs=new ArrayList();

        int countLangs = 0;
        for (Map.Entry<String, String> pair : Langs.entrySet()) {

            ArrayList<String> langsDirection = getLangDirection(pair.getKey());
                if (langsDirection.size()>0){
                    langs.add(pair.getValue());
                }
            countLangs++;
        }

        spinnerSourseLangAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_source_lang, langs);
        spinnerSourseLangAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        SoursLang.setAdapter(spinnerSourseLangAdapter);

        if (SelectSrcTranslate.length()>0) {
            int spinnerPosition = spinnerSourseLangAdapter.getPosition(SelectSrcTranslate);
            SoursLang.setSelection(spinnerPosition);

        }
        //SpinnerTranslateLangFill();

    }



    //метод заполняет spinner язык перевода
    private void SpinnerTranslateLangFill() {



        //получаем Key массива язаков
        String LangSourceKey = getLangKeyByValue(SoursLang.getSelectedItem().toString());
        //ищем в массиве key для языка источника

        //строим массив, с языками, доступными для перевода
        ArrayList<String> langs = getLangDirection(LangSourceKey);

        if (langs.size() > 0) {
            spinnerTranslateLangAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_translate_lang, langs);
            spinnerTranslateLangAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
            TranslateLang.setAdapter(spinnerTranslateLangAdapter);
        }
    }

    private ArrayList<String> getLangDirection(String LangKey)
    {
        ArrayList<String> langs = new ArrayList<String>();
        for (int i = 0; i < Dirs.size(); i++) {
            if (Dirs.get(i).contains(LangKey + "-")) {
                langs.add(Langs.get(Dirs.get(i).replace(LangKey + "-", "")));
            }
        }
        return langs;
    }


    private String getLangKeyByValue(String lang_name)
    {
        String LangSourceKey="noExistLangs";
        int counterTEst=0;
        for (Map.Entry<String, String> pair : Langs.entrySet()) {
            if (pair.getValue()==lang_name) {
                LangSourceKey = pair.getKey();
                break;
            }
        }
     return LangSourceKey;
    }



}
