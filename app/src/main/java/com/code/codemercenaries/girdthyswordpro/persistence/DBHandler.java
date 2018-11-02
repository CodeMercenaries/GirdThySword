package com.code.codemercenaries.girdthyswordpro.persistence;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

/**
 * Created by Joel Kingsley on 31-10-2018.
 */

public class DBHandler extends SQLiteAssetHelper{

    private static final String DATABASE_NAME = "girdthysword-local.db";
    private static final int DATABASE_VERSION = 3;

    public DBHandler(Context context, String name, String storageDirectory, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, storageDirectory, factory, version);
    }
}
