package com.nano.android.popularmovies;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;


public class DetailActivity extends AppCompatActivity {

    private final String LOG_TAG = DetailActivity.class.getSimpleName();
    private final String DETAIL_FRAGMENT_TAG = "detail_fragment_tag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (savedInstanceState == null) {

            Log.v(LOG_TAG, "Create a new DetailFragment and start a new transaction");

            // Add fragment to the activity
            DetailFragment detailFragment = new DetailFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_detail_container, detailFragment).commit();

        }
    }
}
