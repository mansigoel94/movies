package com.example.mansi.movies.Main;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.mansi.movies.Detail.DetailActivity;
import com.example.mansi.movies.Movie;
import com.example.mansi.movies.MovieAdapter;
import com.example.mansi.movies.R;
import com.example.mansi.movies.Utility;

import java.util.ArrayList;

public class MainFragmentAsyncTask extends Fragment implements LoaderManager.LoaderCallbacks<ArrayList<Movie>> {

    private static final int LOADER_ID = 1;
    Context context;
    private MovieAdapter mAdapter;
    private ProgressBar mProgressBar;
    private TextView mEmptyView;
    private GridView mGridview;

    public MainFragmentAsyncTask() {
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
        mEmptyView.setText(getString(R.string.no_internet_connection));

        mAdapter = new MovieAdapter(getActivity(), new ArrayList<Movie>());
        mGridview.setAdapter(mAdapter);

        mGridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Movie selectedMovie = (Movie) adapterView.getItemAtPosition(position);
                Intent openDetailActivity = new Intent(getActivity(), DetailActivity.class);
                openDetailActivity.putExtra(getString(R.string.parcelable_key), selectedMovie);
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
        if (!Utility.isNetworkConnected(getContext())) {
            mGridview.setEmptyView(mEmptyView);
            return;
        }
        mProgressBar.setVisibility(View.VISIBLE);
        //Restarting loader instead of initLoader to refresh data everytime after preference changes in SettingsActivity
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    public Loader<ArrayList<Movie>> onCreateLoader(int i, Bundle bundle) {
        mAdapter.clear();
        return new FetchMoviesData(getContext());
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Movie>> loader, ArrayList<Movie> movies) {
        mProgressBar.setVisibility(View.GONE);
        mAdapter.clear();
        mAdapter.addAll(movies);
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Movie>> loader) {
        mAdapter.clear();
    }
}
