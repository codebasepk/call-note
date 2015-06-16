package com.byteshaft.callnote;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DataBaseHelpers {

    private SQLiteDatabase mDbHelper;
    private SqliteHelpers mSqliteHelper;

    public DataBaseHelpers(Context context) {
        mSqliteHelper = new SqliteHelpers(context);
    }

    void createNewEntry(String column, String value,String secondColumn, String secValue,
                        String thrdColumn, String image ) {
        mDbHelper = mSqliteHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(column, value);
        values.put(secondColumn,secValue);
        values.put(thrdColumn,image);
        mDbHelper.insert(SqliteHelpers.TABLE_NAME, null, values);
        Log.i("Sqlite", "create entry");
    }

    void deleteItem(String column, String value, boolean val) {
        mDbHelper = mSqliteHelper.getWritableDatabase();
        mDbHelper.delete(SqliteHelpers.TABLE_NAME, column + " = ?", new String[]{value});
        if (val) {
            mDbHelper.execSQL("delete from " + SqliteHelpers.TABLE_NAME + " where " +
                    SqliteHelpers.NUMBER_COLUMN + " ='" + value + "'");
            Log.i("sqlite", "All Entries deleted");
        }
        Log.i("sqlite", "Entry deleted");
    }

    void closeDatabase() {
        mSqliteHelper.close();
        Log.i("sqlite", "close database");
    }

    List<String> retrieveDate(String column, String value) {
        Cursor cursor;
        mDbHelper = mSqliteHelper.getWritableDatabase();
        String whereClause =  column+" = ?";
        String[] whereArgs = new String[] {value};
        cursor = mDbHelper.query(SqliteHelpers.TABLE_NAME, null, whereClause, whereArgs,
                null, null, null);
        List<String> list = new ArrayList<>();
        while(cursor.moveToNext()) {
            list.add(cursor.getString(cursor.getColumnIndex(SqliteHelpers.NUMBER_COLUMN)));
            System.out.println(cursor.getString(cursor.getColumnIndex(SqliteHelpers.NUMBER_COLUMN)));
            System.out.println(cursor.getString(cursor.getColumnIndex(SqliteHelpers.NOTES_COLUMN)));
            System.out.println(cursor.getString(cursor.getColumnIndex(SqliteHelpers.PICTURE_COLUMN)));
            list.add(cursor.getString(cursor.getColumnIndex(SqliteHelpers.NOTES_COLUMN)));
            list.add(cursor.getString(cursor.getColumnIndex(SqliteHelpers.PICTURE_COLUMN)));
            Log.i(Helpers.LOG_TAG," Data retrieved");
        }
        return list;
    }
}
