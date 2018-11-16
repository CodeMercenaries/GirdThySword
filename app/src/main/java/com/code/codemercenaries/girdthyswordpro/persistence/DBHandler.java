package com.code.codemercenaries.girdthyswordpro.persistence;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.code.codemercenaries.girdthyswordpro.beans.local.Version;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Joel Kingsley on 31-10-2018.
 */

public class DBHandler extends SQLiteAssetHelper{

    private static final String DATABASE_NAME = "girdthysword.db";
    private static final int DATABASE_VERSION = 1;

    public DBHandler(Context context, String name, String storageDirectory,
                     SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, storageDirectory, factory, version);
    }

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public ArrayList<Version> getAllVersions() {
        Log.d("getAllVersions:", "Entered");
        ArrayList<Version> versions = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        String query = DBQueries.GET_ALL_VERSIONS;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                versions.add(new Version(cursor.getString(0),
                        cursor.getString(1), cursor.getString(2)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        Log.d("getAllVersions:", "Left");
        return versions;
    }

    public ArrayList<String> getBookNames(String version) {
        Log.d("getBookNames:", "Entered");
        SQLiteDatabase db = this.getWritableDatabase();
        String query = String.format(DBQueries.GET_ALL_BOOK_NAMES, version);
        Cursor cursor = db.rawQuery(query, null);
        ArrayList<String> bookNames = new ArrayList<String>();
        if (cursor.moveToFirst()) {
            do {
                bookNames.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        Log.d("getBookNames:", "Left");
        return bookNames;
    }

    public int getNumOfChap(String version, String bookName) {
        Log.d("getNumOfChap:", "Entered");
        SQLiteDatabase db = getWritableDatabase();
        String selectQuery = String.format(DBQueries.GET_CHAP_NUMS, version, bookName);
        Cursor cursor = db.rawQuery(selectQuery, null);
        int count = cursor.getCount();
        cursor.close();
        Log.d("getNumOfChap:", "Left");
        return count-1;
    }

    public int getNumOfVerse(String version, String bookName, int chapNum) {
        Log.d("getNumOfVerse:", "Entered");
        SQLiteDatabase db = this.getWritableDatabase();
        String query = String.format(Locale.getDefault(),DBQueries.GET_VERSE_NUMS, version, bookName, chapNum);
        Cursor cursor = db.rawQuery(query, null);
        int count = cursor.getCount();
        Log.d("getNumOfVerse:", "Left");
        cursor.close();
        return count - 1;
    }

    public ArrayList<String> getAllVersesOfChap(String version, String bookName, int chapNum) {
        Log.d("getAllVersesOfChap:", "Entered");
        ArrayList<String> verses = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        String query = String.format(Locale.getDefault(),DBQueries.GET_VERSE_TEXT_OF_CHAP, version, bookName, chapNum);
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            cursor.moveToNext();

            do {
                verses.add(cursor.getString(0));

            } while (cursor.moveToNext());
        }
        cursor.close();
        Log.d("getAllVersesOfChap:", "Left");
        return verses;
    }

    public String getVerse(String version, String bookName, int chapNum, int verseNum) {
        Log.d("getVerse:", "Entered");
        SQLiteDatabase db = this.getWritableDatabase();
        String query = String.format(Locale.getDefault(),DBQueries.GET_VERSE_TEXT, version, bookName, chapNum, verseNum);
        Cursor cursor = db.rawQuery(query, null);
        String string = "NA";
        if (cursor.moveToFirst()) {
            string = cursor.getString(0);
        }
        cursor.close();
        Log.d("getVerse:", "Left");
        return string;
    }
}
