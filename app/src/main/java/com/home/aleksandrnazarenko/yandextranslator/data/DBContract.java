package com.home.aleksandrnazarenko.yandextranslator.data;

import android.provider.BaseColumns;

/**
 * Created by aleksandrnazarenko on 16.04.17.
 * Контракт для бд избраного и истории
 */

public final class DBContract {

    private DBContract() {
    };

    public static final class FavoriteTbl implements BaseColumns {
        public final static String TABLE_NAME = "Favorite";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_WORD = "Word";
        public final static String COLUMN_TRANSLATE = "Translate";
        public final static String COLUMN_DIRECTION = "Direction";
    }
    public static final class HistoryTbl implements BaseColumns {
        public final static String TABLE_NAME = "History";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_WORD = "Word";
        public final static String COLUMN_TRANSLATE = "Translate";
        public final static String COLUMN_DIRECTION = "Direction";
    }
}
