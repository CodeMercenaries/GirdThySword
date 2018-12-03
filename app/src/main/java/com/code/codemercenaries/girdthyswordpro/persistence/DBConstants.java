package com.code.codemercenaries.girdthyswordpro.persistence;

/**
 * Created by Joel Kingsley on 02-11-2018.
 */

public class DBConstants {

    //Local Database
    public static final String TABLE_VERSION = "version";
    public static final String TABLE_EN_KJV = "en_kjv";
    public static final String TABLE_TAM_ORG = "tam_org";
    public static final String TABLE_TELUGU = "telugu";
    public static final String TABLE_ORIYA = "oriya";

    public static final String V_KEY_ID = "id";
    public static final String V_KEY_NAME = "name";
    public static final String V_KEY_LANG = "lang";

    public static final String B_KEY_ID = "id";
    public static final String B_KEY_BOOK_NAME = "book_name";
    public static final String B_KEY_CHAP_NUM = "chapter_num";
    public static final String B_KEY_VERSE_NUM = "verse_num";
    public static final String B_KEY_VERSE_TEXT = "verse_text";
    public static final String B_KEY_MEMORY = "memory";
    public static final String B_KEY_READ = "read";

    public static final int CODE_NOT_ADDED = 0;
    public static final int CODE_ADDED = 1;
    public static final int CODE_MEMORIZED = 2;

    public static final int CODE_NOT_READ = 0;
    public static final int CODE_READ = 1;

    //Remote Database
    public static final String FIREBASE_TABLE_USERS = "users";
    public static final String FIREBASE_TABLE_CHUNKS = "chunks";
    public static final String FIREBASE_TABLE_SECTIONS = "sections";
    public static final String FIREBASE_TABLE_USER_BIBLE = "user_bible";
    public static final String FIREBASE_TABLE_EN_KJV = "en_kjv";

    public static final String FIREBASE_C_KEY_NEXT_DATE_OF_REVIEW = "nextDateOfReview";
    public static final String FIREBASE_UB_KEY_MEMORY = "memory";
    public static final String FIREBASE_U_KEY_EQUIPPED_SWORD = "equippedSword";
    public static final String FIREBASE_U_KEY_VERSES_MEMORIZED = "versesMemorized";
    public static final String FIREBASE_U_KEY_VERSES_ADDED = "versesAdded";
    public static final String FIREBASE_U_KEY_LAST_UPDATED_BY = "lastUpdatedBy";
    public static final String FIREBASE_S_VERSION_ID = "versionID";

    public static final String DATE_FORMAT = "yyyy/MM/dd";

    //SharedPreferences
    public final static String SYSTEM_PREF = "system";
    public final static String SETTINGS_PREF = "settings";
    public final static String SET_CHUNK_SIZE = "chunk_size";
}
