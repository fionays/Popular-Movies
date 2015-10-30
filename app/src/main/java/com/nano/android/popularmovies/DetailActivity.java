package com.nano.android.popularmovies;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (savedInstanceState == null) {

            DetailFragment detailFragment = new DetailFragment();
            // Get the intent and pass the data to Fragment by initializing them with arguments.
            Intent intent = getIntent();
            MovieHolder theMovie = intent.getParcelableExtra("MovieHolder");
            Bundle bundle = new Bundle();
            bundle.putParcelable("MovieHolder", theMovie);
            detailFragment.setArguments(bundle);

            // Add fragment to the activity
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.detail_container, detailFragment).commit();
        }

        }



    public static class DetailFragment extends Fragment {

        private final String LOG_CAT = DetailFragment.class.getSimpleName();
        private MovieHolder theMovie;
        @Bind(R.id.title)TextView title;
        @Bind(R.id.release_date)TextView release;
        @Bind(R.id.vote_average)TextView vote;
        @Bind(R.id.overview)TextView overView;
        @Bind(R.id.poster)ImageView poster;

        public DetailFragment() {}

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle onSavedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

            // Receive the bundle sent by the activity
            Bundle bundle = getArguments();
            if (bundle != null) {
                theMovie = bundle.getParcelable("MovieHolder");

                //TextView title = (TextView)rootView.findViewById(R.id.title);
                //ImageView poster = (ImageView)rootView.findViewById(R.id.poster);
                //TextView release = (TextView)rootView.findViewById(R.id.release_date);
                //TextView vote = (TextView)rootView.findViewById(R.id.vote_average);
                //TextView overView = (TextView)rootView.findViewById(R.id.overview);
                ButterKnife.bind(this, rootView);
                title.setText(theMovie.originalTitle);
                release.setText(theMovie.releaseDate);
                vote.setText(Integer.toString(theMovie.voteAverage));
                overView.setText(theMovie.overview);
                Picasso.with(getActivity()).load(theMovie.posterPath).into(poster);

            }

            return rootView;
        }

    }
}
