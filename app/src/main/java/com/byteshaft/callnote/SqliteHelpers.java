package com.byteshaft.callnote;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class SqliteHelpers extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "NotesDatabase.db";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "NotesDatabase";
    public static final String NUMBER_COLUMN = "PHONE_NUMBER";
    public static final String NOTES_COLUMN = "Note";
    public static final String PICTURE_COLUMN = "Picture";
    public static final String ID_COLUMN = "ID";
    public static final String DATE_COLUMN = "DATETIME";
    public static final String DESCRIPTION = "DESCRIPTION";

    public static final String TABLE_CREATE =
            "CREATE TABLE " +
                    TABLE_NAME + "(" +
                    ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    NUMBER_COLUMN + " TEXT , " +
                    NOTES_COLUMN + " TEXT UNIQUE , " +
                    DESCRIPTION + " TEXT , "+
                    PICTURE_COLUMN + " TEXT, " +
                    DATE_COLUMN + " TEXT" + " ) ";

    public SqliteHelpers(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
        Log.i("DBHelpers", "table created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_NAME);
        onCreate(db);

    }
}
