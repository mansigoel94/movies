package com.example.mansi.movies.Main;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.mansi.movies.Database.MoviesContract;
import com.example.mansi.movies.Detail.DetailActivity;
import com.example.mansi.movies.R;

public class MainFragmentCursor extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int LOADER_ID = 1;
    Context context;
    private FavouriteCollectionAdapter mAdapter;
    private ProgressBar mProgressBar;
    private TextView mEmptyView;
    private GridView mGridview;

    public MainFragmentCursor() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getContext();

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        mGridview = (GridView) rootView.findViewById(R.id.gridview);
        mEmptyView = (TextView) rootView.findViewById(R.id.emptyView);
        mEmptyView.setText(R.string.no_fav_collection);

        mAdapter = new FavouriteCollectionAdapter(getActivity(), null);
        mGridview.setAdapter(mAdapter);

        mGridview.setEmptyView(mEmptyView);

        mGridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Cursor selectedMovie = (Cursor) adapterView.getItemAtPosition(position);
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
