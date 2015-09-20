package com.gmail.micsha123.farposttestapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLDatabaseHelper {

    private static final String TAG = "SQLDatabaseHelper";
    /** Database name */
    private static final int DATABASE_VERSION = 1;
    /** Database version */
    private static final String DATABASE_NAME = "farpost_testapp.db";

    /** Name of table with requests*/
    private static final String TABLE_LINKS = "links_table";
    /** Column names */
    public static final String COLUMN_LINK = "link";

    /** SQLHelper for managing database */
    private DatabaseOpenHelper openHelper;
    /** SQLite Database */
    private SQLiteDatabase database;

    public SQLDatabaseHelper(Context context) {
        openHelper = new DatabaseOpenHelper(context);
        database = openHelper.getWritableDatabase();
    }

    /** Method provides addin link to database */
    public void insertLink(String link) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_LINK, link);
        database.insert(TABLE_LINKS, null, contentValues);
    }

    /** Method provides erasing databases for debugging */
    public void deleteDBs()
    {
        database.delete(TABLE_LINKS, null, null);
    }

    /** Method returns cursor with all links from database */
    public Cursor getLinks() {
        String buildSQL = "SELECT * FROM " + TABLE_LINKS;
        Log.d(TAG, "getRequests SQL: " + buildSQL);
        return database.rawQuery(buildSQL, null);
    }

    /** openhelper for creating and updating databases */
    private class DatabaseOpenHelper extends SQLiteOpenHelper {

        public DatabaseOpenHelper(Context aContext) {
            super(aContext, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {

            /** request for creating links database */
            String buildRequestsSQL = "CREATE TABLE " + TABLE_LINKS + "( " + COLUMN_LINK + " TEXT )";

            Log.d(TAG, "onCreate SQL: " + buildRequestsSQL);

            sqLiteDatabase.execSQL(buildRequestsSQL);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

            String buildRequestsSQL = "DROP TABLE IF EXISTS " + TABLE_LINKS;

            Log.d(TAG, "onUpgrade SQL: " + buildRequestsSQL);

            sqLiteDatabase.execSQL(buildRequestsSQL);
            onCreate(sqLiteDatabase);
        }
    }

}