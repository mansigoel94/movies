package com.example.mansi.movies;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class FetchMoviesData extends AsyncTaskLoader<ArrayList<Movie>> {
    private static final String BASEURL = "http://api.themoviedb.org/3/movie/";
    private static final String API_KEY = "api_key";
    private static final String LOG_TAG = FetchMoviesData.class.getSimpleName();

    public FetchMoviesData(Context context) {
        super(context);
        forceLoad();
    }

    @Override
    public ArrayList<Movie> loadInBackground() {
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        String json;
        Uri uri = Uri.parse(BASEURL)
                .buildUpon()
                .appendPath(Utility.readPreference(
                        getContext(),
                        getContext().getString(R.string.list_preference_key),
                        getContext().getString(R.string.list_preference_default_value)))
                .appendQueryParameter(API_KEY, BuildConfig.OPEN_MOVIE_DB_API_KEY)
                .build();
        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            inputStream = new BufferedInputStream(urlConnection.getInputStream());
            json = readStream(inputStream);
            return (getMovieDataFromJson(json));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }
        return null;
    }

    private String readStream(InputStream inputStream) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader br = new BufferedReader(inputStreamReader);
        StringBuilder builder = new StringBuilder();
        String temp = br.readLine();
        while (temp != null) {
            builder.append(temp);
            temp = br.readLine();
        }
        return builder.toString();
    }

    public ArrayList<Movie> getMovieDataFromJson(String json) {

        ArrayList<Movie> movieArrayList = new ArrayList<>();

        final String idLabel = "id";
        final String title = "original_title";
        final String poster_path = "poster_path";
        final String plot = "overview";
        final String ratings = "vote_average";
        final String date = "release_date";
        final String results = "results";

        try {
            JSONObject root = new JSONObject(json);
            JSONArray rootArray = root.getJSONArray(results);
            for (int i = 0; i < rootArray.length(); i++) {
                JSONObject currentMovie = rootArray.getJSONObject(i);
                int id = currentMovie.getInt(idLabel);
                String movieTitle = currentMovie.getString(title);
                String moviePoster = currentMovie.getString(poster_path);
                String moviePlotSynopsis = currentMovie.getString(plot);
                double voteAverage = currentMovie.getDouble(ratings);
                String movieReleaseDate = currentMovie.getString(date);
                //adding new movie to moviesArrayList
                movieArrayList.add(i, new Movie(
                        id,
                        movieTitle,
                        moviePoster,
                        moviePlotSynopsis,
                        voteAverage,
                        movieReleaseDate));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return movieArrayList;
    }
}
