package com.example.mansi.movies;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.R.attr.duration;
import static android.content.Context.CONNECTIVITY_SERVICE;

public class Utility {
    public static final String BASEURL = "http://image.tmdb.org/t/p/";
    final static String PATH = "w185";

    public static Uri getAbsoluteUrlForPoster(String relativeUrl) {
        Uri uri = Uri.parse(BASEURL)
                .buildUpon()
                .appendPath(PATH)
                .appendPath(relativeUrl.substring(1))
                .build();
        return uri;
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
//        java.text.DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(context);
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy");
        return simpleDateFormat1.format(date);
    }

    public static String readPreference(Context context, String key, String defaultValue) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String value = sharedPreferences.getString(key, defaultValue);
        return value;
    }
}
