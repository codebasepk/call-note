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

    void createNewEntry(String[] value, String note,String desc, String image, String date) {
        mDbHelper = mSqliteHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        for (String val : value) {
            values.put(SqliteHelpers.NUMBER_COLUMN, val.trim());
            values.put(SqliteHelpers.NOTES_COLUMN, note.trim());
            values.put(SqliteHelpers.PICTURE_COLUMN, image);
            values.put(SqliteHelpers.DATE_COLUMN,date);
            values.put(SqliteHelpers.DESCRIPTION,desc);
            mDbHelper.insert(SqliteHelpers.TABLE_NAME, null, values);
            Log.i(Helpers.LOG_TAG, "created New Entry");
        }
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
        Log.i(Helpers.LOG_TAG, "close database");
    }

    ArrayList<String> retrieveByNotesOrNumber(String value) {
        Cursor cursor;
        mDbHelper = mSqliteHelper.getReadableDatabase();
        String whereClause = SqliteHelpers.NUMBER_COLUMN + " = ?";
        String[] whereArgs = new String[]{value};
        cursor = mDbHelper.query(SqliteHelpers.TABLE_NAME, null, whereClause, whereArgs,
                null, null, null);
        ArrayList<String> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            list.add(cursor.getString(cursor.getColumnIndex(SqliteHelpers.NOTES_COLUMN)));
            Log.i(Helpers.LOG_TAG, " Data retrieved ,,,,,,");
        }
        return list;
    }

    List<String> getLastNoteForContact(String value) {
        Cursor cursor;
        mDbHelper = mSqliteHelper.getWritableDatabase();
        String whereClause = SqliteHelpers.NUMBER_COLUMN + " = ?";
        String[] whereArgs = new String[]{value};
        cursor = mDbHelper.query(SqliteHelpers.TABLE_NAME, null, whereClause, whereArgs,
                null, null, SqliteHelpers.NOTES_COLUMN + " DESC ", "1");
        List<String> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            list.add(cursor.getString(cursor.getColumnIndex(SqliteHelpers.NUMBER_COLUMN)));
            list.add(cursor.getString(cursor.getColumnIndex(SqliteHelpers.NOTES_COLUMN)));
            list.add(cursor.getString(cursor.getColumnIndex(SqliteHelpers.PICTURE_COLUMN)));
            System.out.println(cursor.getString(cursor.getColumnIndex(SqliteHelpers.NUMBER_COLUMN)));
            System.out.println(cursor.getString(cursor.getColumnIndex(SqliteHelpers.NOTES_COLUMN)));
            Log.i(Helpers.LOG_TAG, " Data retrieved");
        }
        return list;
    }

    ArrayList<String> getAllPresentNotes() {
                    Cursor cursor;
                    mDbHelper = mSqliteHelper.getWritableDatabase();
                    String query = "SELECT * FROM " + SqliteHelpers.TABLE_NAME;
                    cursor = mDbHelper.rawQuery(query, null);
                    ArrayList<String> arrayList = new ArrayList<>();
                    while (cursor.moveToNext()) {
                            String itemname = cursor.getString(cursor.getColumnIndex(SqliteHelpers.NOTES_COLUMN));
                            if (itemname != null) {
                                    arrayList.add(itemname);
                                }
                        }
                    return arrayList;
    }

    ArrayList<String> getDescriptionForNote(String note) {
        Cursor cursor;
        mDbHelper = mSqliteHelper.getReadableDatabase();
        String whereClause = SqliteHelpers.NOTES_COLUMN + " = ?";
        String[] whereArgs = new String[]{note};
        cursor = mDbHelper.query(SqliteHelpers.TABLE_NAME, null, whereClause, whereArgs,
                null, null, null);
        ArrayList<String> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            list.add(cursor.getString(cursor.getColumnIndex(SqliteHelpers.DESCRIPTION)));
            System.out.println(cursor.getString(cursor.getColumnIndex(SqliteHelpers.DESCRIPTION)));
            Log.i(Helpers.LOG_TAG, " Data retrieved ,,,,,,");
        }
        return list;
    }
}
