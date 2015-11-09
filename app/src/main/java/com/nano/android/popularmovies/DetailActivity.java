package com.nano.android.popularmovies;


import android.content.Intent;

import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;


public class DetailActivity extends AppCompatActivity {

    private final String DETAIL_FRAGMENT_TAG = "detail_fragment_tag";
    private DetailFragment detailFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Look up the instance already exist by tag.
        detailFragment = (DetailFragment)getSupportFragmentManager()
                .findFragmentByTag(DETAIL_FRAGMENT_TAG);

        if (detailFragment == null) {
            // only create fragment if they haven't been instantiated already
            detailFragment = new DetailFragment();
            Log.v("DetailActivity", "create a new fragment!");
            // Add fragment to the activity
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.detail_container, detailFragment, DETAIL_FRAGMENT_TAG).commit();

            // Get the intent and pass the data to Fragment by initializing them with arguments.
            Intent intent = getIntent();
            MovieHolder theMovie = intent.getParcelableExtra("MovieHolder");
            Bundle bundle = new Bundle();
            bundle.putParcelable("MovieHolder", theMovie);
            detailFragment.setArguments(bundle);
        }

//            // Get the intent and pass the data to Fragment by initializing them with arguments.
//            Intent intent = getIntent();
//            MovieHolder theMovie = intent.getParcelableExtra("MovieHolder");
//            Bundle bundle = new Bundle();
//            bundle.putParcelable("MovieHolder", theMovie);
//            detailFragment.setArguments(bundle);
    }
}
