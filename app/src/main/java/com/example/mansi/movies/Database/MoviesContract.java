package com.example.mansi.movies.Database;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class MoviesContract {
    public static final String CONTENT_AUTHORITY = "com.example.mansi.movies.app";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static class MoviesEntry implements BaseColumns {
        public static final String TABLE_NAME = "movies_info";
        public static final String COL_NAME = "name";
        public static final String COL_IMAGE = "image";
        public static final String COL_DURATION = "runtime";
        public static final String COL_DATE = "date";
        public static final String COL_RATING = "rating";
        public static final String COL_SUMMARY = "synopsis";
        public static final String COL_REVIEW1 = "review1";
        public static final String COL_REVIEW2 = "review2";
        public static final String COL_REVIEW3 = "review3";
        public static final String COL_ID = "_id";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(TABLE_NAME)
                .build();

        //create cursor of dir type
        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                        CONTENT_AUTHORITY + "/" +
                        TABLE_NAME;

        //create cursor of item type
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" +
                        CONTENT_AUTHORITY + "/" +
                        TABLE_NAME;

        //build movies uri
        public static Uri buildMoviesUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
