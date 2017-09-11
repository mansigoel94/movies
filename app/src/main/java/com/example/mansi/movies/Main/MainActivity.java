package com.example.mansi.movies.Main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.mansi.movies.R;
import com.example.mansi.movies.SettingsActivity;
import com.example.mansi.movies.Utility;

public class MainActivity extends AppCompatActivity {

    private static final String ASYNC_FRAG_TAG = "mainfrag";
    private static final String CURSOR_FRAG_TAG = "cursorfrag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String preference = Utility.readPreference(MainActivity.this,
                getString(R.string.list_preference_key),
                getString(R.string.list_preference_default_value));

        Log.v("Mansi", "preference is " + preference);

        if (!preference.equals(getString(R.string.favorites_value))) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, new MainFragmentAsyncTask(), ASYNC_FRAG_TAG)
                    .addToBackStack(CURSOR_FRAG_TAG)
                    .commit();

        } else {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, new MainFragmentCursor(), CURSOR_FRAG_TAG)
                    .addToBackStack(ASYNC_FRAG_TAG)
                    .commit();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = new MenuInflater(getBaseContext());
        menuInflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.settings_menu_item:
                Intent openSettingsActivity = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(openSettingsActivity);
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        int count = getSupportFragmentManager().getBackStackEntryCount();
        if (count == 0)
            super.onBackPressed();
        else
            getSupportFragmentManager().popBackStack();
    }
}
