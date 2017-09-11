package com.example.mansi.movies.Main;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.mansi.movies.Database.MoviesContract;
import com.example.mansi.movies.Detail.DetailActivity;
import com.example.mansi.movies.Movie;
import com.example.mansi.movies.R;

import java.util.ArrayList;

public class MainFragmentCursor extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int LOADER_ID = 1;
    private static final String LOG_TAG = MainFragmentCursor.class.getSimpleName();
    private static final String SCROLL_POSITION_KEY = "scroll";
    Context context;
    private FavouriteCollectionAdapter mAdapter;
    private ProgressBar mProgressBar;
    private TextView mEmptyView;
    private GridView mGridview;
    private int mPosition = 0;

    public MainFragmentCursor() {
        // Required empty public constructor
        Log.v("Mansi", "Cursor fragment started");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mPosition = savedInstanceState.getInt(SCROLL_POSITION_KEY);
        }

        ArrayList<Movie> movieArrayList = new ArrayList<>();

        context = getContext();

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        mGridview = (GridView) rootView.findViewById(R.id.gridview);
        mEmptyView = (TextView) rootView.findViewById(R.id.emptyView);
//        mEmptyView.setVisibility(View.VISIBLE);
        mEmptyView.setText(R.string.no_fav_collection);

        mAdapter = new FavouriteCollectionAdapter(getActivity(), null);
        mGridview.setAdapter(mAdapter);

        mGridview.setEmptyView(mEmptyView);

        //since smoothScrollToPosition was not working directly therefore I used delay thread
        //I understand its not the optimal solution so please can you tell me the optimal way to do the scrolling
        //of gridview without delaying the operation
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mGridview.smoothScrollToPosition(mPosition);
            }
        }, 200);

        mGridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Cursor selectedMovie = (Cursor) adapterView.getItemAtPosition(position);
                Log.v("Mansi", "Cursor returned");
                Intent openDetailActivity = new Intent(getActivity(), DetailActivity.class);
                openDetailActivity.putExtra(getString(R.string.cursor_key),
                        selectedMovie.getInt(selectedMovie.getColumnIndex(MoviesContract.MoviesEntry.COL_ID)));
                startActivity(openDetailActivity);
            }
        });
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //get first visible position
        int index = mGridview.getFirstVisiblePosition();
        outState.putInt(SCROLL_POSITION_KEY, index);
    }

    @Override
    public void onResume() {
        super.onResume();
        mProgressBar.setVisibility(View.VISIBLE);
        //Restarting loader instead of initLoader to refresh data everytime after preference changes in SettingsActivity
        getLoaderManager().restartLoader(LOADER_ID, null, this);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = MoviesContract.MoviesEntry.CONTENT_URI;
        return new CursorLoader(
                getContext(),
                uri,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

}
