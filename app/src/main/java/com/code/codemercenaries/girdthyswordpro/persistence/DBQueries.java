package com.code.codemercenaries.girdthyswordpro.persistence;

/**
 * Created by Joel Kingsley on 02-11-2018.
 */

public class DBQueries {

    public static final String GET_ALL_VERSIONS = "SELECT * FROM " + DBConstants.TABLE_VERSION;
    public static final String GET_ALL_BOOK_NAMES = "SELECT DISTINCT " + DBConstants.B_KEY_BOOK_NAME + " FROM " + "%s";
    public static final String GET_CHAP_NUMS = "SELECT DISTINCT " + DBConstants.B_KEY_CHAP_NUM + " FROM " + "%s" + " WHERE " + DBConstants.B_KEY_BOOK_NAME + " LIKE " + '"' + "%s" + '"';
}
