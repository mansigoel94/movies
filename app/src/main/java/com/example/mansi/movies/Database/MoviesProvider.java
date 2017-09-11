package com.example.mansi.movies.Database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.mansi.movies.Database.MoviesContract.MoviesEntry;

public class MoviesProvider extends ContentProvider {

    private static final int MOVIES = 100;
    private static final int MOVIES_WITH_ID = 101;
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private static final String LOG_TAG = MoviesProvider.class.getSimpleName();
    private SQLiteOpenHelper dbHelper;


    private static UriMatcher buildUriMatcher() {
        final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        sUriMatcher.addURI(MoviesContract.CONTENT_AUTHORITY, MoviesEntry.TABLE_NAME, MOVIES);
        sUriMatcher.addURI(MoviesContract.CONTENT_AUTHORITY, MoviesEntry.TABLE_NAME + "/#", MOVIES_WITH_ID);

        return sUriMatcher;
    }

    @Override
    public boolean onCreate() {
        dbHelper = new MoviesDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri,
                        @Nullable String[] columns,
                        @Nullable String selection,
                        @Nullable String[] selectionArgs,
                        @Nullable String orderBy) {
        final int match = sUriMatcher.match(uri);
        Cursor cursor;
        switch (match) {
            case MOVIES:
                cursor = dbHelper.getReadableDatabase()
                        .query(MoviesEntry.TABLE_NAME,
                                columns,
                                null,
                                null,
                                null,
                                null,
                                null);
                break;
            case MOVIES_WITH_ID:
                cursor = dbHelper.getReadableDatabase()
                        .query(MoviesEntry.TABLE_NAME,
                                columns,
                                selection,
                                selectionArgs,
                                null,
                                null,
                                orderBy);
                break;
            default:
                // By default, we assume a bad URI
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIES: {
                return MoviesEntry.CONTENT_DIR_TYPE;
            }
            case MOVIES_WITH_ID: {
                return MoviesEntry.CONTENT_ITEM_TYPE;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        Uri returnUri;
        switch (sUriMatcher.match(uri)) {
            case MOVIES: {
                long _id = db.insert(MoviesEntry.TABLE_NAME, null, contentValues);
                // insert unless it is already contained in the database
                if (_id > 0) {
                    returnUri = MoviesEntry.buildMoviesUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into: " + uri);
                }
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int numDeleted;
        switch (match) {
            case MOVIES:
                numDeleted = db.delete(
                        MoviesEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case MOVIES_WITH_ID:
                numDeleted = db.delete(MoviesEntry.TABLE_NAME,
                        MoviesEntry._ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return numDeleted;
    }

    @Override
    public int update(@NonNull Uri uri,
                      @Nullable ContentValues contentValues,
                      @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        return 0;
    }
}

