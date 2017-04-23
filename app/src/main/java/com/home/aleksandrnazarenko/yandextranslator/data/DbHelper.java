package com.home.aleksandrnazarenko.yandextranslator.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by aleksandrnazarenko on 16.04.17.
 */

public class DbHelper extends SQLiteOpenHelper {

    /**
     * Имя файла базы данных
     */
    private static final String DATABASE_NAME = "data.db";

    /**
     * Версия базы данных. При изменении схемы увеличить на единицу
     */
    private static final int DATABASE_VERSION = 1;



    public DbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /*Создаем */
    @Override
    public void onCreate(SQLiteDatabase db) {

        String SQL_CREATE_FAVORITE_TABLE = "CREATE TABLE " + DBContract.FavoriteTbl.TABLE_NAME + " ("
                + DBContract.FavoriteTbl._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + DBContract.FavoriteTbl.COLUMN_WORD + " TEXT NOT NULL, "
                + DBContract.FavoriteTbl.COLUMN_TRANSLATE + " TEXT NOT NULL, "
                + DBContract.FavoriteTbl.COLUMN_DIRECTION + " TEXT NOT NULL); ";


        String SQL_CREATE_HISTORY_TABLE = "CREATE TABLE " + DBContract.HistoryTbl.TABLE_NAME + " ("
                + DBContract.HistoryTbl._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + DBContract.HistoryTbl.COLUMN_WORD + " TEXT NOT NULL, "
                + DBContract.HistoryTbl.COLUMN_TRANSLATE + " TEXT NOT NULL, "
                + DBContract.HistoryTbl.COLUMN_DIRECTION + " TEXT NOT NULL); ";

        // Запускаем создание таблицы
        db.execSQL(SQL_CREATE_FAVORITE_TABLE);
        db.execSQL(SQL_CREATE_HISTORY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
