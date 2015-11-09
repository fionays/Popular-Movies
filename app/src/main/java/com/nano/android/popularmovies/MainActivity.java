package com.nano.android.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private boolean mTwoPane;

    private static final String DETAIL_FRAGMENT_TAG = "DF_TAG";
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.v(LOG_TAG, "MainActivity entering onCreate()");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Don't need to add MovieFragment dynamically, since in both cases it is added by xml.

        Log.v(LOG_TAG, "findViewById(R.id.movie_detail_container): " + findViewById(R.id.movie_detail_container));

        if (findViewById(R.id.movie_detail_container) != null) {

            Log.v(LOG_TAG, "MainActivity: two-pane model");

            // If the View is present, then the activity should be in two-pane mode
            mTwoPane = true;

            // In two-pane mode, show the detail view in this activity by adding or replacing
            // the detail fragment using a fragment transaction
            if (savedInstanceState == null) {

                DetailFragment detailFragment = new DetailFragment();

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, detailFragment, DETAIL_FRAGMENT_TAG)
                        .commit();

            }
        } else {

            Log.v(LOG_TAG, "MainActivity: one-pane model");

            mTwoPane = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            // Launch the settings when the button is pressed
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
