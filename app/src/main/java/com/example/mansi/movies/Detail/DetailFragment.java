package com.example.mansi.movies.Detail;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mansi.movies.Database.MoviesContract;
import com.example.mansi.movies.Movie;
import com.example.mansi.movies.R;
import com.example.mansi.movies.Utility;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DetailFragment extends Fragment {

    static TextView mDuration;
    static ImageView mTrailer1;
    static ImageView mTrailer2;
    static ImageView mTrailer3;
    static TextView mTrailerLabel;
    static LinearLayout mTrailer1Layout;
    static LinearLayout mTrailer2Layout;
    static LinearLayout mTrailer3Layout;
    static LinearLayout mReviewsLayout;
    static TextView mReviewLabel1;
    static TextView mReview1;
    static TextView mReviewLabel2;
    static TextView mReview2;
    static TextView mReviewLabel3;
    static TextView mReview3;
    static Button mFavourite;
    static boolean offlineMode;
    public final String projection[] = {MoviesContract.MoviesEntry._ID,
            MoviesContract.MoviesEntry.COL_NAME,
            MoviesContract.MoviesEntry.COL_IMAGE,
            MoviesContract.MoviesEntry.COL_RATING,
            MoviesContract.MoviesEntry.COL_DURATION,
            MoviesContract.MoviesEntry.COL_SUMMARY,
            MoviesContract.MoviesEntry.COL_REVIEW1,
            MoviesContract.MoviesEntry.COL_REVIEW2,
            MoviesContract.MoviesEntry.COL_REVIEW3,
            MoviesContract.MoviesEntry.COL_DATE};
    public final int COL_NAME = 1;
    public final int COL_IMAGE = 2;
    public final int COL_RATING = 3;
    public final int COL_DURATION = 4;
    public final int COL_SUMMARY = 5;
    public final int COL_REVIEW1 = 6;
    public final int COL_REVIEW2 = 7;
    public final int COL_REVIEW3 = 8;
    public final int COL_DATE = 9;
    TextView mTitle;
    TextView mSynopsis;
    ImageView mImageView;
    TextView mRatings;
    TextView mReleaseDate;
    int movieId;
    Callback mCallback;
    private String title;
    private String relativeUrl;
    private String releaseDate;
    private float ratings;
    private String summary;
    private Bitmap mBitmap;
    private Movie movieToDisplay;

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    public static byte[] convertBitmapToByte(Bitmap bitmap) {
        //convert image to byte array
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] array = byteArrayOutputStream.toByteArray();
        return array;
    }

    //    // Override onAttach to make sure that the container activity has implemented the callback
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the host activity has implemented the callback interface
        // If not, it throws an exception
        try {
            mCallback = (Callback) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement Callback Interface");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        mTitle = (TextView) rootView.findViewById(R.id.movie_title);
        mImageView = (ImageView) rootView.findViewById(R.id.detail_poster);
        mSynopsis = (TextView) rootView.findViewById(R.id.detail_synopsis);
        mDuration = (TextView) rootView.findViewById(R.id.detail_duration);
        mRatings = (TextView) rootView.findViewById(R.id.detail_ratings);
        mTrailerLabel = (TextView) rootView.findViewById(R.id.trailer_label);
        mReleaseDate = (TextView) rootView.findViewById(R.id.detail_release_date);
        mFavourite = (Button) rootView.findViewById(R.id.detail_favourite);
        mTrailer1 = (ImageView) rootView.findViewById(R.id.trailer_1_play_button);
        mTrailer2 = (ImageView) rootView.findViewById(R.id.trailer_2_play_button);
        mTrailer3 = (ImageView) rootView.findViewById(R.id.trailer_3_play_button);
        mTrailer1Layout = (LinearLayout) rootView.findViewById(R.id.trailer_1_layout);
        mTrailer2Layout = (LinearLayout) rootView.findViewById(R.id.trailer_2_layout);
        mTrailer3Layout = (LinearLayout) rootView.findViewById(R.id.trailer_3_layout);
        mReviewsLayout = (LinearLayout) rootView.findViewById(R.id.reviews_layout);
        mReview1 = (TextView) rootView.findViewById(R.id.review_1);
        mReview2 = (TextView) rootView.findViewById(R.id.review_2);
        mReview3 = (TextView) rootView.findViewById(R.id.review_3);
        mReviewLabel1 = (TextView) rootView.findViewById(R.id.reviews_label_1);
        mReviewLabel2 = (TextView) rootView.findViewById(R.id.reviews_label_2);
        mReviewLabel3 = (TextView) rootView.findViewById(R.id.reviews_label_3);

        mSynopsis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.notifyClick(mSynopsis, mSynopsis.getText().toString());
            }
        });

        mFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mFavourite.getText().toString().equals(getString(R.string.mark_as_fav))) {
                    saveMovie();
                } else {
                    deleteMovie();
                }
            }
        });

        mReview1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.notifyClick(mReview1, mReview1.getText().toString());
            }
        });

        mReview2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.notifyClick(mReview2, mReview2.getText().toString());
            }
        });

        mReview3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.notifyClick(mReview3, mReview3.getText().toString());
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getActivity().getIntent().hasExtra(getString(R.string.parcelable_key))) {
            movieToDisplay = (Movie) getActivity().getIntent()
                    .getParcelableExtra(getString(R.string.parcelable_key));
            if (Utility.isMoviePresentInDatabase(getContext(), movieToDisplay)) {
                mFavourite.setText(getString(R.string.delete_database));
            } else {
                mFavourite.setText(getString(R.string.mark_as_fav));
            }
            movieId = movieToDisplay.getId();
            offlineMode = false;
            updateUIWithSync();
        }

        if (getActivity().getIntent().hasExtra(getString(R.string.cursor_key))) {
            movieId = getActivity().getIntent()
                    .getIntExtra(getString(R.string.cursor_key), 0);
            offlineMode = true;
            fetchMovieFromDatabase();
        }
    }

    public void fetchMovieFromDatabase() {
        Cursor cursor = Utility.getCursorWithId(getContext(), projection, movieId);

        if (cursor.moveToFirst()) {
            //grabbing all values from returned cursor
            String title = cursor.getString(COL_NAME);
            int duration = cursor.getInt(COL_DURATION);
            int date = cursor.getInt(COL_DATE);
            String summary = cursor.getString(COL_SUMMARY);
            byte[] image = cursor.getBlob(COL_IMAGE);
            String rating = String.format(getString(R.string.format_ratings), cursor.getFloat(COL_RATING));
            String review1 = cursor.getString(COL_REVIEW1);
            String review2 = cursor.getString(COL_REVIEW2);
            String review3 = cursor.getString(COL_REVIEW3);
            Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);

            //setting all values to their respective views
            mTitle.setText(title);
            mDuration.setText(duration + " min");
            mReleaseDate.setText(String.valueOf(date));
            mSynopsis.setText(summary);
            mImageView.setImageBitmap(bitmap);
            mRatings.setText(rating);
            if (review1 != null && !TextUtils.isEmpty(review1))
                mReview1.setText(review1);
            else {
                mReview1.setVisibility(View.GONE);
                mReviewLabel1.setVisibility(View.GONE);
            }
            if (review2 != null && !TextUtils.isEmpty(review2))
                mReview2.setText(review2);
            else {
                mReview2.setVisibility(View.GONE);
                mReviewLabel2.setVisibility(View.GONE);
            }
            if (review3 != null && !TextUtils.isEmpty(review3))
                mReview3.setText(review3);
            else {
                mReview3.setVisibility(View.GONE);
                mReviewLabel3.setVisibility(View.GONE);
            }
            mFavourite.setText(getString(R.string.delete_database));
            mFavourite.setVisibility(View.VISIBLE);
        }
        cursor.close();
    }

    public void updateUIWithSync() {
        //starting FetchDetailData AsyncTask to grab trailer's url, reviews and duration from 3 different urls
        FetchDetailData fetchDetailData = new FetchDetailData(getActivity(), movieToDisplay.getId());
        fetchDetailData.execute("videos", "reviews", "");

        //starting FetchLogoBitmap AsyncTask
        FetchLogoBitmap fetchLogoBitmap = new FetchLogoBitmap();
        fetchLogoBitmap.execute(Utility.getAbsoluteUrlForPoster(movieToDisplay.getPoster()));

        //displaying title
        title = movieToDisplay.getTitle();
        mTitle.setText(title);

        //displaying poster
        relativeUrl = movieToDisplay.getPoster();
        if (relativeUrl != null && !TextUtils.isEmpty(relativeUrl))

            Picasso.with(getContext())
                    .load(Utility.getAbsoluteUrlForPoster(relativeUrl))
                    .into(mImageView);

        //display date
        releaseDate = movieToDisplay.getReleaseDate();
        if (releaseDate != null && !TextUtils.isEmpty(releaseDate)) {
            mReleaseDate.setText(Utility.formatDate(releaseDate));
        }

        //displaying ratings
        ratings = movieToDisplay.getRatings();
        if (ratings != -1.0f) {
            String ratings = String.format(getString(R.string.format_ratings), movieToDisplay.getRatings());
            mRatings.setText(ratings);
        }

        //displaying synopsis
        summary = movieToDisplay.getSynopsis();
        if (summary != null && !TextUtils.isEmpty(summary))
            mSynopsis.setText(summary);

    }

    private void saveMovie() {
        //means we are inserting movie into database
        ContentValues contentValues = new ContentValues();
        contentValues.put(MoviesContract.MoviesEntry.COL_ID, movieId);
        contentValues.put(MoviesContract.MoviesEntry.COL_NAME, title);
        contentValues.put(MoviesContract.MoviesEntry.COL_IMAGE, convertBitmapToByte(mBitmap));
        contentValues.put(MoviesContract.MoviesEntry.COL_RATING, ratings);
        contentValues.put(MoviesContract.MoviesEntry.COL_DATE, releaseDate);
        contentValues.put(MoviesContract.MoviesEntry.COL_DURATION, FetchDetailData.runTimeDuration);
        contentValues.put(MoviesContract.MoviesEntry.COL_SUMMARY, summary);

        if (mReview1.getVisibility() == View.VISIBLE) {
            contentValues.put(MoviesContract.MoviesEntry.COL_REVIEW1, mReview1.getText().toString());
        }
        if (mReview2.getVisibility() == View.VISIBLE) {
            contentValues.put(MoviesContract.MoviesEntry.COL_REVIEW2, mReview2.getText().toString());
        }
        if (mReview3.getVisibility() == View.VISIBLE) {
            contentValues.put(MoviesContract.MoviesEntry.COL_REVIEW3, mReview3.getText().toString());
        }
        Uri insertUri = getActivity().getContentResolver()
                .insert(MoviesContract.MoviesEntry.CONTENT_URI, contentValues);

        if (insertUri == null) {
            try {
                throw new Exception("Error in insertion");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getContext(), R.string.movie_added, Toast.LENGTH_SHORT).show();
            mFavourite.setText(R.string.delete_database);
        }
    }

    private void deleteMovie() {
        Uri uri = MoviesContract.MoviesEntry.buildMoviesUri(movieId);
        String selection = MoviesContract.MoviesEntry.COL_ID + "=?";
        String selectionArgs[] = new String[]{String.valueOf(movieId)};
        int rowsDeleted = getActivity().getContentResolver().delete(
                uri,
                selection,
                selectionArgs);

        if (rowsDeleted == 0) {
            try {
                throw new Exception("Error in deletion");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getContext(), getString(R.string.movie_deleted), Toast.LENGTH_SHORT).show();
            if (offlineMode)
                getActivity().finish();
            else
                mFavourite.setText(getString(R.string.mark_as_fav));
        }
    }

    public interface Callback {
        void notifyClick(View view, String text);
    }

    public class FetchLogoBitmap extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... strings) {
            URL url = null;
            Bitmap bitmap = null;
            InputStream inputStream = null;
            try {
                url = new URL(strings[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();
                inputStream = urlConnection.getInputStream();
                BitmapFactory.Options options = new BitmapFactory.Options();
                bitmap = BitmapFactory.decodeStream(inputStream, null, options);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            mBitmap = bitmap;
        }
    }
}
