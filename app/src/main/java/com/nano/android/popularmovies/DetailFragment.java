package com.nano.android.popularmovies;


import android.support.v4.app.Fragment;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nano.android.popularmovies.data.FavoritedContract;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A placeholder fragment containing a simple view.
 */

public class DetailFragment extends Fragment {

    private final String LOG_CAT = DetailFragment.class.getSimpleName();
    private final String KEY = "movie";

    private MovieHolder theMovie;

    private boolean checked = false;

    @Bind(R.id.title)TextView title;
    @Bind(R.id.release_date)TextView release;
    @Bind(R.id.vote_average)TextView vote;
    @Bind(R.id.overview)TextView overView;
    @Bind(R.id.poster)ImageView poster;
    @Bind(R.id.trailer_reviews_container)LinearLayout trailersReviewsContainer;
    @Bind(R.id.favorite_button)CheckBox favCheckBox;


    public DetailFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle onSavedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        // Receive the bundle sent by the activity
        Bundle bundle = getArguments();

        if (bundle != null) {
            theMovie = bundle.getParcelable("MovieHolder");
            ButterKnife.bind(this, rootView);
            title.setText(theMovie.originalTitle);
            release.setText(theMovie.releaseDate);
            vote.setText(Integer.toString(theMovie.voteAverage));
            overView.setText(theMovie.overview);
            Picasso.with(getActivity()).load(theMovie.posterPath)
                    .placeholder(R.drawable.image_holder)
                    .error(R.drawable.image_holder)
                    .into(poster);

            // If the "favorite" field is true, means right now we are viewing the favorite lists.
            // Then mark the checkbox.
            // Otherwise, the movie is downloaded from server. Need to query fav table to check
            // if it has been added to fav table.
            if (theMovie.favorite || isMovieAdded()) {
                checked = true;
            }
            favCheckBox.setChecked(checked);
        }

        favCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // If new state is checked, insert the movie into database.
                // Otherwise, delete the record from favorite table
                if (isChecked) {
                    addMovie();
                    Toast.makeText(getActivity(), "Added to favorites!", Toast.LENGTH_LONG)
                            .show();
                } else {
                    removeMovie();
                    Toast.makeText(getActivity(), "Removed from favorites", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });


        return rootView;
    }

    @Override
    public void onStart() {

        Log.v(LOG_CAT, "DetailFragment entering onStart");

        super.onStart();

        // If entering DetailActivity from favorite movie list
        if (theMovie.favorite) {
            fetchFromDatabase(theMovie);
        } else {
            fetchOnline();
        }

    }

    private void fetchOnline() {
        FetchTrailerReview fetchDataTask = new FetchTrailerReview(
                getActivity(),trailersReviewsContainer, theMovie);
        fetchDataTask.execute(BuildConfig.THE_MOVIE_DB_API_KEY);
    }

    private void fetchFromDatabase(MovieHolder theMovie) {
        Log.v(LOG_CAT, "Entering fetchFromDatabase");
        queryTrailerTable();
        queryReviewTable();
    }

    private void queryTrailerTable() {
        Log.v(LOG_CAT, "Entering queryTrailerTable");
        Uri uri = FavoritedContract.TrailerEntry.buildTrailerWithMovieId(theMovie.movieId);

        Cursor cur = getActivity().getContentResolver().query(uri, null, null, null, null);

        // If the cursor is not empty
        if (cur.moveToFirst()) {
            do {
                ContentValues values = new ContentValues();
                DatabaseUtils.cursorRowToContentValues(cur, values);

                String name = values.getAsString(FavoritedContract.TrailerEntry.COLUMN_TRAILER_NAME);
                String key = values.getAsString(FavoritedContract.TrailerEntry.COLUMN_TRAILER_KEY);

                MovieHolder.Trailer trailer = new MovieHolder.Trailer(theMovie.movieId, name, key);
                theMovie.trailers.add(trailer);
            } while (cur.moveToNext());

            // display trailers
            for (final MovieHolder.Trailer trailer : theMovie.trailers) {
                View trailerItem = LayoutInflater.from(getActivity()).inflate(R.layout.trailer_item, null);
                // Set the trailer title
                TextView trailerTitle = (TextView)trailerItem.findViewById(R.id.movie_trailer_name);
                trailerTitle.setText(trailer.trailerName);

                // Add OnClickListener to the view
                trailerItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Start an Intent to open the trailer on Youtube
                        Utility.playTrailerIntent(trailer.key, getActivity());
                    }
                });

                // Add this trailer to the end of container
                trailersReviewsContainer.addView(trailerItem);
            }
        }
    }

    private void queryReviewTable() {
        Uri uri = FavoritedContract.ReviewEntry.buildReviewWithMovieId(theMovie.movieId);

        Cursor cur = getActivity().getContentResolver().query(uri, null, null, null, null);

        // If the cursor is not empty
        if (cur.moveToFirst()) {
            do {
                ContentValues values = new ContentValues();
                DatabaseUtils.cursorRowToContentValues(cur, values);

                String name = values.getAsString(FavoritedContract.ReviewEntry.COLUMN_AUTHOR);
                String content = values.getAsString(FavoritedContract.ReviewEntry.COLUMN_CONTENT);

                MovieHolder.Review review = new MovieHolder.Review(theMovie.movieId, name, content);
                theMovie.reviews.add(review);
            } while (cur.moveToNext());

            // Display those reviews
            for (final MovieHolder.Review review : theMovie.reviews) {
                View reviewItem = LayoutInflater.from(getActivity()).inflate(R.layout.review_item, null);

                TextView reviewAuthor = (TextView)reviewItem.findViewById(R.id.review_author);
                TextView reviewContent = (TextView)reviewItem.findViewById(R.id.review_content);
                reviewAuthor.setText(review.author);
                reviewContent.setText(review.content);

                // Add this review to the end of container
                trailersReviewsContainer.addView(reviewItem);
            }

        }
    }
    /**
     * Query the fav table based on the movie id. Check if the movie has been added to the fav.
     * @return       True if it is existing in the fav table.
     */
    private boolean isMovieAdded() {
        Uri uriWithMovieId = FavoritedContract.FavoriteEntry.buildFavoriteWithMovieId(theMovie.movieId);
        Cursor cur = getActivity().getContentResolver().query(
                uriWithMovieId, null, null, null, null);
        if (!cur.moveToFirst()) {
            return false;
        }
        return true;
    }

    private void addMovie() {
        addDetails();
        addTrailers();
        addReviews();
    }

    private void removeMovie() {
        removeDetails();
        removeTrailers();
        removeReviews();
    }

    private void addDetails() {
        ContentValues insertValues = new ContentValues();
        insertValues.put(FavoritedContract.FavoriteEntry.COLUMN_POSTER, theMovie.posterPath);
        insertValues.put(FavoritedContract.FavoriteEntry.COLUMN_MOVIE_ID, theMovie.movieId);
        insertValues.put(FavoritedContract.FavoriteEntry.COLUMN_TITLE, theMovie.originalTitle);
        insertValues.put(FavoritedContract.FavoriteEntry.COLUMN_RELEASE_DATE, theMovie.releaseDate);
        insertValues.put(FavoritedContract.FavoriteEntry.COLUMN_VOTE, theMovie.voteAverage);
        insertValues.put(FavoritedContract.FavoriteEntry.COLUMN_OVERVIEW, theMovie.overview);

        Uri tableUri = FavoritedContract.FavoriteEntry.CONTENT_URI;

        getActivity().getContentResolver().insert(tableUri, insertValues);
    }

    private void addTrailers() {
        int count = theMovie.trailers.size();
        if (count > 0) {
            Uri tableUri = FavoritedContract.TrailerEntry.CONTENT_URI;

            ContentValues[] valuesArray = new ContentValues[count];
            for (int i = 0; i < count; i++) {
                MovieHolder.Trailer trailer = theMovie.trailers.get(i);
                ContentValues values = new ContentValues();

                values.put(FavoritedContract.TrailerEntry.COLUMN_MOVIE_ID, trailer.movieId);
                values.put(FavoritedContract.TrailerEntry.COLUMN_TRAILER_NAME, trailer.trailerName);
                values.put(FavoritedContract.TrailerEntry.COLUMN_TRAILER_KEY, trailer.key);

                valuesArray[i] = values;
            }

            getActivity().getContentResolver().bulkInsert(tableUri, valuesArray);
        }
    }

    private void addReviews() {
        int count = theMovie.reviews.size();
        if (count > 0) {
            Uri tableUri = FavoritedContract.ReviewEntry.CONTENT_URI;

            ContentValues[] valuesArray = new ContentValues[count];
            for (int i = 0; i < count; i++) {
                MovieHolder.Review review = theMovie.reviews.get(i);
                ContentValues values = new ContentValues();

                values.put(FavoritedContract.ReviewEntry.COLUMN_MOVIE_ID, review.movieId);
                values.put(FavoritedContract.ReviewEntry.COLUMN_AUTHOR, review.author);
                values.put(FavoritedContract.ReviewEntry.COLUMN_CONTENT, review.content);

                valuesArray[i] = values;
            }
            getActivity().getContentResolver().bulkInsert(tableUri, valuesArray);
        }
    }

    private void removeDetails() {
        Uri uri = FavoritedContract.FavoriteEntry.buildFavoriteWithMovieId(theMovie.movieId);
        getActivity().getContentResolver().delete(uri, null, null);
    }

    private void removeTrailers() {
        if (theMovie.trailers.size() > 0) {
            Uri uri = FavoritedContract.TrailerEntry.buildTrailerWithMovieId(theMovie.movieId);
            getActivity().getContentResolver().delete(uri, null, null);
        }
    }

    private void removeReviews() {
        if (theMovie.reviews.size() > 0) {
            Uri uri = FavoritedContract.ReviewEntry.buildReviewWithMovieId(theMovie.movieId);
            getActivity().getContentResolver().delete(uri, null, null);
        }
    }
}