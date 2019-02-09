package com.code.codemercenaries.girdthyswordpro.persistence;

/**
 * Created by Joel Kingsley on 02-11-2018.
 */

public class DBQueries {

    static final String GET_ALL_VERSIONS = "SELECT * FROM " + DBConstants.TABLE_VERSION;
    static final String GET_ALL_BOOK_NAMES = "SELECT DISTINCT " + DBConstants.B_KEY_BOOK_NAME
            + " FROM " + "%s";
    static final String GET_CHAP_NUMS = "SELECT DISTINCT " + DBConstants.B_KEY_CHAP_NUM
            + " FROM " + "%s" + " WHERE " + DBConstants.B_KEY_BOOK_NAME + " LIKE " + '"' + "%s" + '"';
    static final String GET_VERSE_NUMS = "SELECT * FROM " + "%s" + " WHERE "
            + DBConstants.B_KEY_BOOK_NAME + " LIKE " + '"' + "%s" + '"' + " AND "
            + DBConstants.B_KEY_CHAP_NUM + " = " + "%d";
    static final String GET_VERSE_TEXT = "SELECT " + DBConstants.B_KEY_VERSE_TEXT + " FROM "
            + "%s" + " WHERE " + DBConstants.B_KEY_BOOK_NAME + " LIKE " + '"' + "%s" + '"' + " AND "
            + DBConstants.B_KEY_CHAP_NUM + " = " + "%d" + " AND " + DBConstants.B_KEY_VERSE_NUM
            + " = " + "%d";
    static final String GET_VERSE_TEXT_OF_CHAP = "SELECT " + DBConstants.B_KEY_VERSE_TEXT + " FROM "
            + "%s" + " WHERE " + DBConstants.B_KEY_BOOK_NAME + " LIKE " + '"' + "%s" + '"' + " AND "
            + DBConstants.B_KEY_CHAP_NUM + " = " + "%d" + " ORDER BY " + DBConstants.B_KEY_VERSE_NUM;
    static final String GET_CHAP_REP_ID = "SELECT " + DBConstants.B_KEY_ID + " FROM %s WHERE "
            + DBConstants.B_KEY_BOOK_NAME + " LIKE " + '"' + "%s" + '"' + " AND "
            + DBConstants.B_KEY_CHAP_NUM + "=" + "%d" + " AND " + DBConstants.B_KEY_VERSE_NUM + "=" + 0;
    static final String GET_TOTAL_CHAPTERS_READ_IN_VERSION = "SELECT " + DBConstants.B_KEY_ID + " FROM %s WHERE "
            + DBConstants.B_KEY_VERSE_NUM + "=" + 0 + " AND "
            + DBConstants.B_KEY_READ + "=" + DBConstants.CODE_READ;
    static final String GET_TOTAL_CHAPTERS_READ_IN_BOOK = "SELECT " + DBConstants.B_KEY_ID + " FROM %s WHERE "
            + DBConstants.B_KEY_BOOK_NAME + " LIKE " + '"' + "%s" + '"' + " AND "
            + DBConstants.B_KEY_VERSE_NUM + "=" + 0 + " AND "
            + DBConstants.B_KEY_READ + "=" + DBConstants.CODE_READ;
    static final String GET_CHAP_READ_STATUS = "SELECT " + DBConstants.B_KEY_READ + " FROM %s WHERE "
            + DBConstants.B_KEY_BOOK_NAME + " LIKE " + '"' + "%s" + '"' + " AND "
            + DBConstants.B_KEY_CHAP_NUM + "=" + "%d" + " AND " + DBConstants.B_KEY_VERSE_NUM + "=" + "0";
}
