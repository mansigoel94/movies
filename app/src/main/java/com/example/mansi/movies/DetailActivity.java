package com.example.mansi.movies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


public class DetailActivity extends AppCompatActivity implements DetailFragment.Callback {

    private static final String DETFRAGTAG = "DetailFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, new DetailFragment(), DETFRAGTAG)
                    .commit();
        }

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

    @Override
    public void notifyChange(View changedView, String text) {
        if (changedView instanceof TextView) {
            Log.v("Mansi", "Its textview");
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
