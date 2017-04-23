package com.home.aleksandrnazarenko.yandextranslator.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.home.aleksandrnazarenko.yandextranslator.FavoriteTab;
import com.home.aleksandrnazarenko.yandextranslator.FragmentDataSync;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by aleksandrnazarenko on 22.04.17.
 */

public class DbMethods {


    //удаляем из избраного
    public static void deleteFavorite(String Word,String Translate, String Direction,DbHelper mDbHelper) {
        // Получаем базу для записи
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.delete(DBContract.FavoriteTbl.TABLE_NAME,
                DBContract.FavoriteTbl.COLUMN_WORD+" = ? and "+DBContract.FavoriteTbl.COLUMN_TRANSLATE+" = ? " +
                        "and "+DBContract.FavoriteTbl.COLUMN_DIRECTION+"= ?",
                new String[] {Word, Translate,Direction.toUpperCase()});
        db.close();
    }


    //Вставляем слово в таблицу избранного
    public static void insertFavorite(String Word,String Translate, String Direction,DbHelper mDbHelper) {
        // Получаем базу для записи
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        // Создаем объект ContentValues, где имена столбцов ключи,
        ContentValues values = new ContentValues();
        values.put(DBContract.FavoriteTbl.COLUMN_WORD,Word);
        values.put(DBContract.FavoriteTbl.COLUMN_TRANSLATE,Translate);
        values.put(DBContract.FavoriteTbl.COLUMN_DIRECTION,Direction.toUpperCase());
        long newRowId = db.insert(DBContract.FavoriteTbl.TABLE_NAME, null, values);

        db.close();
    }

    //Вставляем слово в базу истории
    public static void insertHistory(String Word,String Translate, String Direction,DbHelper mDbHelper) {
        // Получаем базу для записи
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        Direction=Direction.toUpperCase();
        // Создаем объект ContentValues, где имена столбцов ключи,
        ContentValues values = new ContentValues();
        values.put(DBContract.HistoryTbl.COLUMN_WORD,Word);
        values.put(DBContract.HistoryTbl.COLUMN_TRANSLATE,Translate);
        values.put(DBContract.HistoryTbl.COLUMN_DIRECTION,Direction);
        long newRowId = db.insert(DBContract.HistoryTbl.TABLE_NAME, null, values);
        EventBus.getDefault().post(new FragmentDataSync("HistoryChange",-1,Word,Translate,Direction,0));
//        db.setTransactionSuccessful();
        db.close();
    }

    //Проверсям, есть ли в базе избраного слово
    public static Boolean getExistFavorite(String Word,String Translate, String Direction,DbHelper mDbHelper)
    {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String DATABASE_COMPARE = "select * from "+DBContract.FavoriteTbl.TABLE_NAME+" where "+
                DBContract.FavoriteTbl.COLUMN_WORD+"='"+Word+"' and "+
                DBContract.FavoriteTbl.COLUMN_TRANSLATE+"='"+Translate+"' and "+
                DBContract.FavoriteTbl.COLUMN_DIRECTION+"='"+Direction.toUpperCase()+"'";
        Cursor cursor = db.rawQuery(DATABASE_COMPARE, null);
        int cnt = cursor.getCount();

        db.close();
        if (cnt==1){
            return true;
        }
        return false;
    }

    //заполняем массив избранного для апатера recyclreview
    public static List<FavoriteTab.MemoryWords> getExistFavorite(DbHelper mDbHelper,List<FavoriteTab.MemoryWords> TranslateWord) {
        TranslateWord.clear();
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        // Делаем запрос в бд
        Cursor cursor = db.query(DBContract.FavoriteTbl.TABLE_NAME, null, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow("_id"));
                String word = cursor.getString(cursor.getColumnIndexOrThrow(DBContract.FavoriteTbl.COLUMN_WORD));
                String translate = cursor.getString(cursor.getColumnIndexOrThrow(DBContract.FavoriteTbl.COLUMN_TRANSLATE));
                String direction = cursor.getString(cursor.getColumnIndexOrThrow(DBContract.FavoriteTbl.COLUMN_DIRECTION));
                TranslateWord.add(new FavoriteTab.MemoryWords(word,translate,direction.toUpperCase(),1));
            } while (cursor.moveToNext());
        }
        db.close();
        return TranslateWord;
    }

    //заполняем массив избранного для апатера recyclreview
    public static List<FavoriteTab.MemoryWords> getHistory(DbHelper mDbHelper,List<FavoriteTab.MemoryWords> TranslateWord) {
        TranslateWord.clear();
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        // Делаем запрос в бд
        Cursor cursor = db.query(DBContract.HistoryTbl.TABLE_NAME, null, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow("_id"));
                String word = cursor.getString(cursor.getColumnIndexOrThrow(DBContract.HistoryTbl.COLUMN_WORD));
                String translate = cursor.getString(cursor.getColumnIndexOrThrow(DBContract.HistoryTbl.COLUMN_TRANSLATE));
                String direction = cursor.getString(cursor.getColumnIndexOrThrow(DBContract.HistoryTbl.COLUMN_DIRECTION));
                Boolean fav = DbMethods.getExistFavorite(word,translate,direction.toUpperCase(),mDbHelper);
                int favorite =0;
                if (fav)
                {
                    favorite = 1;
                }
                TranslateWord.add(new FavoriteTab.MemoryWords(word,translate,direction.toUpperCase(),favorite));
            } while (cursor.moveToNext());
        }
        db.close();
        return TranslateWord;
    }


}
