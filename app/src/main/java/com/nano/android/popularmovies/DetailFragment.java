package com.nano.android.popularmovies;


import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubePlayer;
import com.nano.android.popularmovies.data.FavoritedContract;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A placeholder fragment containing a simple view.
 */

public class DetailFragment extends Fragment
        implements CompoundButton.OnCheckedChangeListener{

    private final String LOG_CAT = DetailFragment.class.getSimpleName();
    private final String SCROLL_POSITION = "position";
    static final String MOVIE = "movie";

    private MovieHolder theMovie;
    private YouTubePlayer YPlayer;

    private boolean checked = false;

    @Bind(R.id.app_bar_layout) AppBarLayout appBarLayout;
    @Bind(R.id.collapsing_toolbar_layout)CollapsingToolbarLayout cToolbarLayout;
    @Bind(R.id.app_bar_detail)Toolbar toolbar;
    @Bind(R.id.youtube_fragment)ImageView youtubeView;
    @Bind(R.id.nestedScrollView)NestedScrollView nestedScrollView;
    @Bind(R.id.release_date)TextView release;
    @Bind(R.id.vote_average)TextView vote;
    @Bind(R.id.overview)TextView overView;
    @Bind(R.id.poster)ImageView poster;
    @Bind(R.id.trailer_reviews_container)LinearLayout trailersReviewsContainer;
    @Bind(R.id.favorite_button)CheckBox favCheckBox;


    public DetailFragment() {}

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save the trailers and reviews so that it will not

        // Save the scroll position
        outState.putIntArray(SCROLL_POSITION,
                new int[]{nestedScrollView.getScrollX(), nestedScrollView.getScrollY()});
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle onSaveInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        setHasOptionsMenu(true);

        // Retrieve arguments from bundle
        Bundle bundle = getArguments();
        if (bundle != null) {
            theMovie = bundle.getParcelable(MOVIE);

            ButterKnife.bind(this, rootView);
            // Set the toolbar to act as ActionBar for DetailActivity window
            ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
            ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            cToolbarLayout.setTitle(theMovie.originalTitle);
            // Show toolbar title only when the CollapsingToolbarLayout is at the actionbar height,
            // set the expanded color for the title to transparent
            cToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));

            Picasso.with(getActivity()).load(theMovie.posterPath)
                    .placeholder(R.drawable.image_holder)
                    .error(R.drawable.image_holder)
                    .into(youtubeView);

            release.setText(theMovie.releaseDate);
            vote.setText(Integer.toString(theMovie.voteAverage));
            overView.setText(theMovie.overview);
            Picasso.with(getActivity()).load(theMovie.posterPath)
                    .placeholder(R.drawable.image_holder)
                    .error(R.drawable.image_holder)
                    .into(poster);
            favCheckBox.setVisibility(View.VISIBLE);
            // If the "favorite" field is true, means right now we are viewing the favorite lists.
            // Then mark the checkbox.
            // Otherwise, the movie is downloaded from server. Need to query fav table to check
            // if it has been added to fav table.
            if (theMovie.favorite || isMovieAdded()) {
                checked = true;
            }
            favCheckBox.setChecked(checked);
            favCheckBox.setOnCheckedChangeListener(this);

//            // YoutubePlayerView
//            YouTubePlayerSupportFragment youTubePlayerFragment =
//                    YouTubePlayerSupportFragment.newInstance();
//            getChildFragmentManager().beginTransaction().add(R.id.youtube_fragment, youTubePlayerFragment).commit();
//            youTubePlayerFragment.initialize(BuildConfig.YOUTUBE_API_KEY, new YouTubePlayer.OnInitializedListener() {
//                @Override
//                public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
//                    if (!wasRestored) {
//                        YPlayer = player;
//                        YPlayer.setFullscreen(true);
//                        YPlayer.loadVideo(theMovie.trailers.get(0).key);
//                        YPlayer.play();
//                    }
//                }
//
//                @Override
//                public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
//                    // TODO Auto-generated method
//                }
//            });
        }

//        // Restore the scroll view position.
//        if (onSaveInstanceState != null ) {
//            final int[] position = onSaveInstanceState.getIntArray(SCROLL_POSITION);
//            if (position != null ) {
//                scrollView.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        Log.v(LOG_CAT,"Start Runnable");
//                        scrollView.scrollTo(position[0], position[1]);
//                    }
//                });
//            }
//        }
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Restore the scroll view position.
        if (savedInstanceState != null ) {
            final int[] position = savedInstanceState.getIntArray(SCROLL_POSITION);
            if (position != null ) {
                nestedScrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.v(LOG_CAT,"Start Runnable");
                        nestedScrollView.scrollTo(position[0], position[1]);
                    }
                });
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        Log.v(LOG_CAT, "Detail Fragment: Create option menu.");

        // Inflate the menu, adds items to the action bar if it is present
        inflater.inflate(R.menu.detail_fragment_menu, menu);

        // Get the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Fetch and store hareActionProvider
        ShareActionProvider shareActionProvider =
                (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        // Attach an itent to this shareActionProvider. Update any time when users
        // select new data
        if (shareActionProvider != null) {
            shareActionProvider.setShareIntent(createShareTrailer());
        } else {
            Log.d(LOG_CAT, "Share Action Privider is null.");
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // For two-pane model, DetailFragment will not receive any intent at the first time, because
        // no movie is selected. The theMovie would be an null object.
        if (theMovie == null) {return;}

        // If entering DetailActivity from favorite movie list
        if (theMovie.favorite) {
            fetchFromDatabase(theMovie);
        } else {
            fetchOnline();
        }

    }

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

    private Intent createShareTrailer() {

        // Handle the IndexOutOfBoundsException when trailers ArrayList is still empty.
        // It happens when it will take a longer time to fetch trailer from server.
        if (theMovie.trailers.size() == 0) {return null;}

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        String trailerURL = "https://www.youtube.com/watch?v=" + theMovie.trailers.get(0).key;
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, trailerURL);
        return shareIntent;
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
            trailersReviewsContainer.removeAllViews();
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