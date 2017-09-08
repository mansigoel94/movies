package com.example.mansi.movies;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

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

import static com.example.mansi.movies.DetailFragment.mDuration;
import static com.example.mansi.movies.DetailFragment.mReview1;
import static com.example.mansi.movies.DetailFragment.mReview2;
import static com.example.mansi.movies.DetailFragment.mReview3;
import static com.example.mansi.movies.DetailFragment.mReviewLabel1;
import static com.example.mansi.movies.DetailFragment.mReviewLabel2;
import static com.example.mansi.movies.DetailFragment.mReviewLabel3;
import static com.example.mansi.movies.DetailFragment.mTrailer1;
import static com.example.mansi.movies.DetailFragment.mTrailer1Layout;
import static com.example.mansi.movies.DetailFragment.mTrailer2;
import static com.example.mansi.movies.DetailFragment.mTrailer2Layout;
import static com.example.mansi.movies.DetailFragment.mTrailer3;
import static com.example.mansi.movies.DetailFragment.mTrailer3Layout;
import static com.example.mansi.movies.DetailFragment.mTrailerLabel;

public class FetchDetailData extends AsyncTask<String, Void, String[]> {

    private static final String BASEURL = "http://api.themoviedb.org/3/movie/";
    private static final String API_KEY = "api_key";
    Context mContext;
    DetailFragment.Callback mCallback;
    private int mId;
    private int runTimeDuration;
    private String[] reviews;
    private String[] trailer;
    private String[] trailerSetter = new String[3];
    private String[] reviewsSetter = new String[3];

    public FetchDetailData(Context context, int id) {
        mContext = context;
        mId = id;
        mCallback = (DetailFragment.Callback) context;
    }

    @Override
    protected String[] doInBackground(String... strings) {
        String jsonArray[] = new String[3];
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        String json;
        for (int i = 0; i < 3; i++) {
            Uri.Builder uriBuilder = Uri.parse(BASEURL)
                    .buildUpon()
                    .appendPath(String.valueOf(mId));
            if (!TextUtils.isEmpty(strings[i])) {
                uriBuilder.appendPath(strings[i]);
            }
            Uri uri = uriBuilder.appendQueryParameter(API_KEY, BuildConfig.OPEN_MOVIE_DB_API_KEY)
                    .build();
            URL url = null;
            try {
                Log.v("Mansi", "url " + uri.toString());
                url = new URL(uri.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                int responseCode = urlConnection.getResponseCode();
                if (responseCode != 200) {
                    throw new Exception("Response code " + responseCode + " is not valid ");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                inputStream = new BufferedInputStream(urlConnection.getInputStream());
                json = readStream(inputStream);
                jsonArray[i] = json;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                urlConnection.disconnect();
            }
        }
        return jsonArray;
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

    @Override
    protected void onPostExecute(String[] strings) {
        super.onPostExecute(strings);

        parseJsonResponse(strings);

        //checking keys within trailer array
        int counterTrailer = 0;
        for (final String string : trailer) {
            if (!TextUtils.isEmpty(string)) {
                if (counterTrailer == 3)
                    break;
                trailerSetter[counterTrailer++] = string;
            }
        }

        View.OnClickListener clickListenerTrailer1 = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                watchYoutubeVideo(trailerSetter[0]);
            }
        };
        View.OnClickListener clickListenerTrailer2 = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                watchYoutubeVideo(trailerSetter[1]);
            }
        };
        View.OnClickListener clickListenerTrailer3 = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                watchYoutubeVideo(trailerSetter[2]);
            }
        };

        //check number of trailers for selected movie
        switch (counterTrailer) {
            case 0:
                mTrailerLabel.setText(R.string.no_trailers);
            case 1:
                //one trailer
                mTrailer1Layout.setVisibility(View.VISIBLE);
                mTrailer1.setOnClickListener(clickListenerTrailer1);
                break;
            case 2:
                //two trailers
                mTrailer1Layout.setVisibility(View.VISIBLE);
                mTrailer1.setOnClickListener(clickListenerTrailer1);

                mTrailer2Layout.setVisibility(View.VISIBLE);
                mTrailer2.setOnClickListener(clickListenerTrailer2);
                break;
            case 3:
                //movies has max 3 trailers
                mTrailer1Layout.setVisibility(View.VISIBLE);
                mTrailer1.setOnClickListener(clickListenerTrailer1);

                mTrailer2Layout.setVisibility(View.VISIBLE);
                mTrailer2.setOnClickListener(clickListenerTrailer2);

                mTrailer3Layout.setVisibility(View.VISIBLE);
                mTrailer3.setOnClickListener(clickListenerTrailer3);
            default:
                break;
        }

        //TODO Implement reviews in layout
        //checking reviews withing review array
        int counterReviews = 0;
        for (String string : reviews) {
            if (!TextUtils.isEmpty(string)) {
                //Made a assumption that there can be max 3 reviews
                if (counterReviews == 3)
                    break;
                Log.v("Mansi", "Reviews " + string);
                reviewsSetter[counterReviews++] = string;
            }
        }

        if (counterReviews >= 1) {
            mReviewLabel1.setVisibility(View.VISIBLE);
            mReview1.setVisibility(View.VISIBLE);
            mReview1.setText(reviewsSetter[0]);
            mReview1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mCallback.notifyChange(mReview1, reviewsSetter[0]);
                }
            });
        }
        if (counterReviews >= 2) {
            mReviewLabel2.setVisibility(View.VISIBLE);
            mReview2.setVisibility(View.VISIBLE);
            mReview2.setText(reviewsSetter[1]);
            mReview2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mCallback.notifyChange(mReview2, reviewsSetter[1]);
                }
            });
        }
        if (counterReviews >= 3) {
            mReviewLabel3.setVisibility(View.VISIBLE);
            mReview3.setVisibility(View.VISIBLE);
            mReview3.setText(reviewsSetter[2]);
            mReview3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mCallback.notifyChange(mReview3, reviewsSetter[2]);
                }
            });
        }

        //display duration
        if (runTimeDuration != -1)
            mDuration.setText(String.valueOf(runTimeDuration) + " min");
        else {
            mDuration.setVisibility(View.INVISIBLE);
        }
    }

    public void parseJsonResponse(String jsonArray[]) {
        final String duration = "runtime";
        final String arrayResults = "results";
        final String contentReviews = "content";
        final String keyTrailer = "key";
        final String typeTrailer = "type";

        for (int i = 0; i < 3; i++) {
            try {
                JSONObject root = new JSONObject(jsonArray[i]);

                //i=0 means its json response for trailers
                if (i == 0) {
                    if (root.has(arrayResults)) {
                        JSONArray array = root.getJSONArray(arrayResults);
                        trailer = new String[array.length()];
                        for (int j = 0; j < array.length(); j++) {
                            if (array.getJSONObject(j).getString(typeTrailer).equalsIgnoreCase("Trailer")) {
                                if (array.getJSONObject(j).has(keyTrailer)) {
                                    trailer[j] = array.getJSONObject(j).getString(keyTrailer);
                                }
                            } else {
                                trailer[j] = "";
                            }
                        }
                    }
                }

                //i=1 means its json response for reviews
                if (i == 1) {
                    if (root.has(arrayResults)) {
                        JSONArray array = root.getJSONArray(arrayResults);
                        reviews = new String[array.length()];
                        for (int j = 0; j < array.length(); j++) {
                            if (array.getJSONObject(j).has(contentReviews)) {
                                reviews[j] = array.getJSONObject(j).getString(contentReviews);
                            } else {
                                reviews[j] = "";
                            }
                        }
                    }
                }

                //i=2 means its json response for duration
                if (i == 2) {
                    if (root.has(duration)) {
                        runTimeDuration = root.getInt(duration);
                    } else {
                        runTimeDuration = -1;
                    }
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void watchYoutubeVideo(String id) {
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://www.youtube.com/watch?v=" + id));
        try {
            mContext.startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            mContext.startActivity(webIntent);
        }
    }
}
