package com.nano.android.popularmovies;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;


public class DetailActivity extends AppCompatActivity {

    private final String LOG_TAG = DetailActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (savedInstanceState == null) {

            Log.v(LOG_TAG, "Create a new DetailFragment and start a new transaction");

            // Receive intent from MovieFragment, and pass it to DetailFragment as Bundle
            MovieHolder theMovie = getIntent().getParcelableExtra(DetailFragment.MOVIE);
            Bundle bundle = new Bundle();
            bundle.putParcelable(DetailFragment.MOVIE, theMovie);

            DetailFragment detailFragment = new DetailFragment();
            detailFragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_detail_container, detailFragment).commit();
        }
    }
}
