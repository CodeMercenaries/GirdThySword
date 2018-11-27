package com.code.codemercenaries.girdthyswordpro.persistence;

import android.content.ContentValues;
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

    public void setReadChapter(String version, String bookName, int chapNum) {
        Log.d("setReadChapter:", "book " + bookName + " chap" + chapNum + " entered");
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();

        String selectQuery = String.format(Locale.getDefault(),DBQueries.GET_CHAP_REP_ID,version,bookName,chapNum);
        Cursor cursor = db.rawQuery(selectQuery, null);

        cursor.moveToFirst();
        String id = cursor.getString(0);

        cv.put(DBConstants.B_KEY_READ, DBConstants.CODE_READ);
        db.update(version, cv, "id=" + id, null);

        cursor.close();
        Log.d("setReadChapter:", "book " + bookName + " chap" + chapNum + " left");
    }

    public void setNotReadChapter(String version, String bookName, int chapNum) {
        Log.d("setNotReadChapter:", "book " + bookName + " chap" + chapNum + " entered");
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();

        String selectQuery = String.format(Locale.getDefault(),DBQueries.GET_CHAP_REP_ID,version,bookName,chapNum);
        Cursor cursor = db.rawQuery(selectQuery, null);

        cursor.moveToFirst();
        String id = cursor.getString(0);

        cv.put(DBConstants.B_KEY_READ, DBConstants.CODE_NOT_READ);
        db.update(version, cv, "id=" + id, null);

        cursor.close();
        Log.d("setNotReadChapter:", "book " + bookName + " chap" + chapNum + " left");
    }

    public void setNotReadVersion(String version) {
        Log.d("setNotReadVersion:", "version " + version + " entered");
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(DBConstants.B_KEY_READ, DBConstants.CODE_NOT_READ);
        db.update(version, cv, null, null);

        Log.d("setNotReadVersion:", "version " + version + " left");
    }

    public int getTotalChaptersReadInVersion(String version) {
        Log.d("getTChaptersReadInV:", "version " + version + " entered");

        SQLiteDatabase db = getWritableDatabase();

        String selectQuery = String.format(Locale.getDefault(),DBQueries.GET_TOTAL_CHAPTERS_READ_IN_VERSION,version);
        Cursor cursor = db.rawQuery(selectQuery, null);

        int count = cursor.getCount();

        cursor.close();
        Log.d("getTChaptersReadInV:", "version " + version + " left");
        Log.d("getTChaptersReadInV:", Integer.toString(count));
        return count;
    }

    public int getTotalChaptersReadInBook(String version, String bookName) {
        Log.d("getTChaptersReadInB:", "version " + version + "bookName " + bookName + " entered");

        SQLiteDatabase db = getWritableDatabase();

        String selectQuery = String.format(Locale.getDefault(),DBQueries.GET_TOTAL_CHAPTERS_READ_IN_BOOK,version,bookName);
        Cursor cursor = db.rawQuery(selectQuery, null);

        int count = cursor.getCount();

        cursor.close();
        Log.d("getTChaptersReadInB:", "version " + version + "bookName " + bookName + " left");
        Log.d("getTChaptersReadInB:", Integer.toString(count));
        return count;
    }

    public boolean isReadChapter(String version, String bookName, int chapNum) {
        Log.d("isReadChapter:", "version " + version + "bookName " + bookName + "chapNum " + chapNum + " entered");

        SQLiteDatabase db = getWritableDatabase();

        String selectQuery = String.format(Locale.getDefault(),DBQueries.GET_CHAP_READ_STATUS,version,bookName,chapNum);
        Cursor cursor = db.rawQuery(selectQuery, null);

        cursor.moveToFirst();

        int readStatus = cursor.getInt(0);

        cursor.close();
        Log.d("isReadChapter:", "version " + version + "bookName " + bookName + "chapNum " + chapNum +  " left");
        Log.d("isReadChapter:", Boolean.toString(readStatus == DBConstants.CODE_READ));
        return readStatus == DBConstants.CODE_READ;
    }
}
