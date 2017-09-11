package com.example.mansi.movies.Detail;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.mansi.movies.Database.MoviesContract;
import com.example.mansi.movies.Movie;
import com.example.mansi.movies.R;
import com.example.mansi.movies.Utility;


public class DetailActivity extends AppCompatActivity implements DetailFragment.Callback {

    //// TODO: Retains scroll position
    //// TODO: Load more movies and allow user to search for movie of his choice

    private static final String DETFRAGTAG = "DetailFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container, new DetailFragment(), DETFRAGTAG)
                .commit();
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
        Intent shareIntent = new Intent(Intent.ACTION_SEND)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT)
                .setType("text/plain");
        String extra_text = null;
        if (DetailFragment.offlineMode) {
            int movieId = getIntent()
                    .getIntExtra(getString(R.string.cursor_key), 0);
            String projection[] = {MoviesContract.MoviesEntry.COL_NAME,
                    MoviesContract.MoviesEntry.COL_SUMMARY,
                    MoviesContract.MoviesEntry.COL_RATING,
                    MoviesContract.MoviesEntry.COL_DATE};

            Cursor cursor = Utility.getCursorWithId(getBaseContext(), projection, movieId);
            if (cursor.moveToFirst()) {
                extra_text = "Title: " + cursor.getString(0) + "\n" +
                        "Plot: " + cursor.getString(1) + "\n" +
                        "Ratings: " + cursor.getFloat(2) + "\n" +
                        "Release year: " + cursor.getInt(3) + "\n";
            }
        } else {
            Movie selectedMovie = (Movie) getIntent().getParcelableExtra(getString(R.string.parcelable_key));
            extra_text = selectedMovie.toString();
        }
        shareIntent.putExtra(Intent.EXTRA_TEXT, extra_text);

        if (shareIntent.resolveActivity(getPackageManager()) != null) {
            return shareIntent;
        }
        return null;
    }

    @Override
    public void notifyClick(View changedView, String text) {
        if (changedView instanceof TextView) {
            Bundle bundle = new Bundle();
            bundle.putString("text", text);

            // set Fragment class Arguments
            Fragment fragment = new TextViewFragment();
            fragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction()
                    .addToBackStack(DETFRAGTAG)
                    .replace(R.id.container, fragment)
                    .commit();
        }
    }
}
