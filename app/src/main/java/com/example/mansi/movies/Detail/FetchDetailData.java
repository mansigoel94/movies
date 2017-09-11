package com.example.mansi.movies.Detail;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.View;

import com.example.mansi.movies.BuildConfig;
import com.example.mansi.movies.R;

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

public class FetchDetailData extends AsyncTask<String, Void, String[]> {

    private static final String BASEURL = "http://api.themoviedb.org/3/movie/";
    private static final String API_KEY = "api_key";
    static int runTimeDuration;
    static String[] reviewsSetter = new String[3];
    static int counterReviews;
    Context mContext;
    DetailFragment.Callback mCallback;
    private int mId;
    private String[] reviews;
    private String[] trailer;
    private String[] trailerSetter = new String[3];

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
                DetailFragment.mTrailerLabel.setText(R.string.no_trailers);
            case 1:
                //one trailer
                DetailFragment.mTrailer1Layout.setVisibility(View.VISIBLE);
                DetailFragment.mTrailer1.setOnClickListener(clickListenerTrailer1);
                break;
            case 2:
                //two trailers
                DetailFragment.mTrailer1Layout.setVisibility(View.VISIBLE);
                DetailFragment.mTrailer1.setOnClickListener(clickListenerTrailer1);

                DetailFragment.mTrailer2Layout.setVisibility(View.VISIBLE);
                DetailFragment.mTrailer2.setOnClickListener(clickListenerTrailer2);
                break;
            case 3:
                //movies has max 3 trailers
                DetailFragment.mTrailer1Layout.setVisibility(View.VISIBLE);
                DetailFragment.mTrailer1.setOnClickListener(clickListenerTrailer1);

                DetailFragment.mTrailer2Layout.setVisibility(View.VISIBLE);
                DetailFragment.mTrailer2.setOnClickListener(clickListenerTrailer2);

                DetailFragment.mTrailer3Layout.setVisibility(View.VISIBLE);
                DetailFragment.mTrailer3.setOnClickListener(clickListenerTrailer3);
            default:
                break;
        }

        //checking reviews withing review array
        counterReviews = 0;
        for (String string : reviews) {
            if (!TextUtils.isEmpty(string)) {
                if (counterReviews == 3)
                    break;
                reviewsSetter[counterReviews++] = string;
            }
        }

        if (counterReviews == 3) {
            DetailFragment.mReview1.setText(reviewsSetter[0]);
            DetailFragment.mReview2.setText(reviewsSetter[1]);
            DetailFragment.mReview3.setText(reviewsSetter[2]);
        } else if (counterReviews == 2) {
            DetailFragment.mReview1.setText(reviewsSetter[0]);
            DetailFragment.mReview2.setText(reviewsSetter[1]);

            DetailFragment.mReviewLabel3.setVisibility(View.GONE);
            DetailFragment.mReview3.setVisibility(View.GONE);
        } else if (counterReviews == 1) {
            DetailFragment.mReview1.setText(reviewsSetter[0]);

            DetailFragment.mReviewLabel2.setVisibility(View.GONE);
            DetailFragment.mReview2.setVisibility(View.GONE);
            DetailFragment.mReviewLabel3.setVisibility(View.GONE);
            DetailFragment.mReview3.setVisibility(View.GONE);
        } else {
            DetailFragment.mReviewsLayout.setVisibility(View.GONE);
        }

        //display duration
        if (runTimeDuration != -1)
            DetailFragment.mDuration.setText(String.valueOf(runTimeDuration) + " min");
        else {
            DetailFragment.mDuration.setVisibility(View.INVISIBLE);
        }

        //set visibility of mark as favourite button as visible
        DetailFragment.mFavourite.setVisibility(View.VISIBLE);
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
