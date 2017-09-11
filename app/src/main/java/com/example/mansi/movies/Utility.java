package com.example.mansi.movies;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;

import com.example.mansi.movies.Database.MoviesContract;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class Utility {
    public static final String BASEURL = "http://image.tmdb.org/t/p/";
    final static String PATH_IMAGE_FORMAT = "w185";

    public static String getAbsoluteUrlForPoster(String relativeUrl) {
        Uri uri = Uri.parse(BASEURL)
                .buildUpon()
                .appendPath(PATH_IMAGE_FORMAT)
                .appendPath(relativeUrl.substring(1))
                .build();
        return uri.toString();
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public static String formatDate(String releaseDate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = (Date) simpleDateFormat.parse(releaseDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy");
        return simpleDateFormat1.format(date);
    }

    public static String readPreference(Context context, String key, String defaultValue) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String value = sharedPreferences.getString(key, defaultValue);
        return value;
    }

    public static boolean isMoviePresentInDatabase(Context context, Movie movie) {
        int id = movie.getId();
        Uri uri = MoviesContract.MoviesEntry.buildMoviesUri(id);
        Cursor cursor = context.getContentResolver()
                .query(uri,
                        null,
                        MoviesContract.MoviesEntry._ID + "=?",
                        new String[]{String.valueOf(id)},
                        null);
        if (cursor.getCount() != 0) {
            return true;
        } else {
            return false;
        }
    }

    public static Cursor getCursorWithId(Context context, String projection[], int movieId) {
        Uri uri = MoviesContract.MoviesEntry.buildMoviesUri(movieId);
        return context.getContentResolver()
                .query(uri,
                        projection,
                        MoviesContract.MoviesEntry._ID + "=?",
                        new String[]{String.valueOf(movieId)},
                        null);
    }
}
