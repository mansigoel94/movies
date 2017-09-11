package com.example.mansi.movies.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.mansi.movies.Database.MoviesContract.MoviesEntry;

public class MoviesDbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "movies.db";
    public static final int DATABASE_VERSION = 1;

    //using movie id from moviedb site as primary key since it will always be unique
    public static final String CREATE_QUERY = "CREATE TABLE " +
            MoviesEntry.TABLE_NAME + "(" +
            MoviesEntry.COL_ID + " INTEGER PRIMARY KEY, " +
            MoviesEntry.COL_NAME + " TEXT NOT NULL, " +
            MoviesEntry.COL_IMAGE + " BLOB, " +
            MoviesEntry.COL_DURATION + " INTEGER DEFAULT 0, " +
            MoviesEntry.COL_DATE + " INTEGER, " +
            MoviesEntry.COL_RATING + " REAL, " +
            MoviesEntry.COL_SUMMARY + " TEXT, " +
            MoviesEntry.COL_REVIEW1 + " TEXT DEFAULT null, " +
            MoviesEntry.COL_REVIEW2 + " TEXT DEFAULT null, " +
            MoviesEntry.COL_REVIEW3 + " TEXT DEFAULT null);";

    public static final String DELETE_QUERY = "DROP TABLE IF EXISTS " + MoviesEntry.TABLE_NAME;

    public MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(DELETE_QUERY);
        onCreate(sqLiteDatabase);
    }
}
