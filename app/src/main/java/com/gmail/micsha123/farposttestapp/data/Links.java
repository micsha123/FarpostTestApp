package com.gmail.micsha123.farposttestapp.data;


import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;

/** Data class keeping list of links and providing loading and saving from/to DB */
public class Links {

    private static Links instance;
    /** Host-activity context */
    private Context context;
    /** list of links */
    private ArrayList<String> links;
    private SQLDatabaseHelper dbHelper;

    private Links(Context context) {
        this.context = context;
        this.dbHelper = new SQLDatabaseHelper(context);
        loadLinksFromDB();
    }

    public static synchronized Links getInstance(Context context) {
        if (instance == null) {
            instance = new Links(context);
        }
        return instance;
    }
    /** Data class keeping list of links and providing loading and saving from/to DB */
    public ArrayList<String> getLinks() {
        return links;
    }
    /** Method provides loading from DB */
    public void loadLinksFromDB(){
        links = new ArrayList<String>();
        Cursor cursor = dbHelper.getLinks();
        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            links.add(cursor.getString(cursor.getColumnIndex(SQLDatabaseHelper.COLUMN_LINK)));
            cursor.moveToNext();
        }
    }
    /** Method provides saving ArrayList to DB*/
    public void saveLinksToDB(ArrayList<String> links){
        deleteDB();
        for(String link : links){
            dbHelper.insertLink(link);
        }
    }
    /** Data class keeping list of links and providing loading and saving from/to DB */
    public void deleteDB(){
        dbHelper.deleteDBs();
    }
}
