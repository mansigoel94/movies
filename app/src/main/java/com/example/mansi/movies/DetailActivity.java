package com.example.mansi.movies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import static com.example.mansi.movies.DetailFragment.mTrailer1;


public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, new DetailFragment())
                    .commit();
        }

        Movie movieToDisplay = (Movie) getIntent()
                .getParcelableExtra(getString(R.string.open_detail_intent_key));

        Log.v("Mansi","id is"+movieToDisplay.getId());

        FetchDetailData fetchDetailData = new FetchDetailData(DetailActivity.this,movieToDisplay.getId());
        fetchDetailData.execute("videos", "reviews", "");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);

        // Retrieve the share menu item
        MenuItem menuItem = (MenuItem) menu.findItem(R.id.menu_item_share);

        ShareActionProvider shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
        shareActionProvider.setShareIntent(createShareIntent());
        return true;
    }

    public Intent createShareIntent() {
        Movie selectedMovie = (Movie) getIntent().getParcelableExtra(getString(R.string.open_detail_intent_key));
        Intent shareIntent = new Intent(Intent.ACTION_SEND)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT)
                .putExtra(Intent.EXTRA_TEXT, selectedMovie.toString())
                .setType("text/plain");
        if (shareIntent.resolveActivity(getPackageManager()) != null) {
            return shareIntent;
        }
        return null;
    }
}
