package com.nano.android.popularmovies;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

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
                Picasso.with(getActivity()).load(theMovie.posterPath).into(poster);

                // If the "favorite" field is true, means right now we are viewing the favorite lists.
                // Then mark the checkbox.
                // Otherwise, the movie is downloaded from server. Need to query fav table to check
                // if it has been added to fav table.
                if (theMovie.favorite || isAdded(theMovie)) {
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
                        addMovie(theMovie);
                        Toast.makeText(getActivity(), "Added to favorites!", Toast.LENGTH_LONG)
                                .show();
                    } else {
                        removeMovie(theMovie);
                        Toast.makeText(getActivity(), "Removed from favorites", Toast.LENGTH_LONG)
                                .show();
                    }
                }
            });


            return rootView;
        }

        @Override
        public void onStart() {
            super.onStart();
            // If entering DetailActivity from favorite movie list
            if (theMovie.favorite) {
                fetchFromDatabase(theMovie);
                Log.v(LOG_CAT, "Fetch trailers and reviews from database.");
            } else {
                fetchOnline();
            }
        }

        /**
         * Start Youtube to play trailer using Intent
         * @param key  The source of the trailer
         */
         void playTrailerIntent(String key) {

            final String VALUE = "v";
            final String BASE_YOUTUBE_URI = "http://www.youtube.com/watch?";
            Uri builtUri = Uri.parse(BASE_YOUTUBE_URI).buildUpon()
                    .appendQueryParameter(VALUE, key)
                    .build();

            // Build the intent
            Intent playIntent = new Intent(Intent.ACTION_VIEW, builtUri);

            // Verify it resolves
            PackageManager packageManager = getActivity().getPackageManager();
            // TODO: Why the example in documentation set flag = 0?
            List<ResolveInfo> activities = packageManager.queryIntentActivities(playIntent,PackageManager.MATCH_DEFAULT_ONLY);
            boolean isIntentSafe = activities.size() > 0;

            // Start an activity if it is safe
            if (isIntentSafe) {
                startActivity(playIntent);
            }
        }

        private void fetchOnline() {
            FetchDataTask fetchDataTask = new FetchDataTask();
            fetchDataTask.execute(BuildConfig.THE_MOVIE_DB_API_KEY);
        }

        private void fetchFromDatabase(MovieHolder theMovie) {
            Log.v(LOG_CAT, "Entering fetchFromDatabase");
            queryTrailerTable(theMovie);
            queryReviewTable(theMovie);
        }

        private void queryTrailerTable(MovieHolder theMovie) {

            Log.v(LOG_CAT, "Entering queryTrailerTable");
            long movieId = theMovie.movieId;
            Uri uri = FavoritedContract.TrailerEntry.buildTrailerWithMovieId(movieId);

            Cursor cur = getActivity().getContentResolver().query(uri, null, null, null, null);

            // If the cursor is not empty
            if (cur.moveToFirst()) {
                do {
                    ContentValues values = new ContentValues();
                    DatabaseUtils.cursorRowToContentValues(cur, values);

                    String name = values.getAsString(FavoritedContract.TrailerEntry.COLUMN_TRAILER_NAME);
                    String key = values.getAsString(FavoritedContract.TrailerEntry.COLUMN_TRAILER_KEY);

                    MovieHolder.Trailer trailer = new MovieHolder.Trailer(movieId, name, key);
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
                            playTrailerIntent(trailer.key);
                        }
                    });

                    // Add this trailer to the end of container
                    trailersReviewsContainer.addView(trailerItem);
                }
            }
        }

        private void queryReviewTable(MovieHolder theMovie) {
            long movieId = theMovie.movieId;
            Uri uri = FavoritedContract.ReviewEntry.buildReviewWithMovieId(movieId);

            Cursor cur = getActivity().getContentResolver().query(uri, null, null, null, null);

            // If the cursor is not empty
            if (cur.moveToFirst()) {
                do {
                    ContentValues values = new ContentValues();
                    DatabaseUtils.cursorRowToContentValues(cur, values);

                    String name = values.getAsString(FavoritedContract.ReviewEntry.COLUMN_AUTHOR);
                    String content = values.getAsString(FavoritedContract.ReviewEntry.COLUMN_CONTENT);

                    MovieHolder.Review review = new MovieHolder.Review(movieId, name, content);
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
         * @param movie  Movie that will be checked.
         * @return       True if it is existing in the fav table.
         */
        private boolean isAdded(MovieHolder movie) {
            long movieId = movie.movieId;
            Uri uriWithMovieId = FavoritedContract.FavoriteEntry.buildFavoriteWithMovieId(movieId);
            Cursor cur = getActivity().getContentResolver().query(
                    uriWithMovieId, null, null, null, null);
            if (!cur.moveToFirst()) {
                return false;
            }
            return true;
        }

        private void addMovie(MovieHolder theMovie) {
            addDetails(theMovie);
            addTrailers(theMovie);
            addReviews(theMovie);
        }

        private void removeMovie(MovieHolder theMovie) {
            removeDetails(theMovie);
            removeTrailers(theMovie);
            removeReviews(theMovie);
        }

        private void addDetails(MovieHolder movie) {
            ContentValues insertValues = new ContentValues();
            insertValues.put(FavoritedContract.FavoriteEntry.COLUMN_POSTER, movie.posterPath);
            insertValues.put(FavoritedContract.FavoriteEntry.COLUMN_MOVIE_ID, movie.movieId);
            insertValues.put(FavoritedContract.FavoriteEntry.COLUMN_TITLE, movie.originalTitle);
            insertValues.put(FavoritedContract.FavoriteEntry.COLUMN_RELEASE_DATE, movie.releaseDate);
            insertValues.put(FavoritedContract.FavoriteEntry.COLUMN_VOTE, movie.voteAverage);
            insertValues.put(FavoritedContract.FavoriteEntry.COLUMN_OVERVIEW, movie.overview);

            Uri tableUri = FavoritedContract.FavoriteEntry.CONTENT_URI;

            getActivity().getContentResolver().insert(tableUri, insertValues);
        }

        private void addTrailers(MovieHolder theMovie) {
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

        private void addReviews(MovieHolder theMovie) {
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

        private void removeDetails(MovieHolder theMovie) {
            Uri uri = FavoritedContract.FavoriteEntry.buildFavoriteWithMovieId(theMovie.movieId);
            getActivity().getContentResolver().delete(uri, null, null);
        }
        private void removeTrailers(MovieHolder theMovie) {
            if (theMovie.trailers.size() > 0) {
                Uri uri = FavoritedContract.TrailerEntry.buildTrailerWithMovieId(theMovie.movieId);
                getActivity().getContentResolver().delete(uri, null, null);
            }
        }
        private void removeReviews(MovieHolder theMovie) {
            if (theMovie.reviews.size() > 0) {
                Uri uri = FavoritedContract.ReviewEntry.buildReviewWithMovieId(theMovie.movieId);
                getActivity().getContentResolver().delete(uri, null, null);
            }
        }

        /**
         * An inner class to fetch trailers and reviews for this movie
         */
        public class FetchDataTask extends AsyncTask<String, Void, MovieHolder> {

            private final String TASK_LOG_TAG = FetchDataTask.class.getSimpleName();

            /**
             * Extract trailers fields: "movie_id", "trailer_name", "key". Add them to the movie.
             * @param trailerJsonStr String representation of JSON.
             * @return               A MovieHolder Object with zero or more trailers
             * @throws JSONException
             */
            private void getTrailersFromJson(String trailerJsonStr) throws JSONException {

                final String MOVIE_ID = "id";
                final String TRAILER_NAME = "name";
                final String KEY = "key";
                final String RESULTS = "results";
                // Convert JSON string to JSON object
                JSONObject jsonObject = new JSONObject(trailerJsonStr);
                JSONArray resultArray = jsonObject.getJSONArray(RESULTS);

                long movieId = jsonObject.getLong(MOVIE_ID);

                int count = resultArray.length();
                for ( int i = 0; i < count; i ++) {
                    // Get JSON object for each trailer
                    JSONObject trailerJson = resultArray.getJSONObject(i);

                    String name = trailerJson.getString(TRAILER_NAME);
                    String key = trailerJson.getString(KEY);

                    MovieHolder.Trailer trailer = new MovieHolder.Trailer(movieId, name, key);
                    // Add each trailer to the trailers List.
                    theMovie.trailers.add(trailer);
                }

                // Test
                Log.v(TASK_LOG_TAG, "The number of trailers: " + count);
                for (MovieHolder.Trailer t : theMovie.trailers) {
                    Log.v(TASK_LOG_TAG, "Trailer infor: " + t);
                }
            }

            /**
             * Extract review fields: "movie_id", "author", "content". Add them to the movie.
             * @param reviewsJsonStr String format of JSON
             * @throws JSONException
             */
            private void getReviewsFromJson(String reviewsJsonStr) throws JSONException {

                final String MOVIE_ID = "id";
                final String RESULTS = "results";
                final String AUTHOR = "author";
                final String CONTENT = "content";

                // Convert JSON String to JSON
                JSONObject jsonObject = new JSONObject(reviewsJsonStr);

                long movieID = jsonObject.getLong(MOVIE_ID);
                JSONArray resultArray = jsonObject.getJSONArray(RESULTS);
                int count = resultArray.length();

                for (int i = 0; i < count; i ++) {
                    // Get JSON object for each review
                    JSONObject reviewJson = resultArray.getJSONObject(i);

                    String author = reviewJson.getString(AUTHOR);
                    String content = reviewJson.getString(CONTENT);

                    MovieHolder.Review review = new MovieHolder.Review(movieID, author, content);

                    theMovie.reviews.add(review);
                }

                // Test
                Log.v(TASK_LOG_TAG, "The number of reviews: " + count);
                for (MovieHolder.Review r : theMovie.reviews) {
                    Log.v(TASK_LOG_TAG, "Reviews infor: " + r);
                }
            }

            /**
             * Fetch trailers as well as reviews in a single AsyncTask
             * @param params  The API_KEY
             * @return        The MovieHolder object with trailers and reviews
             */
            @Override
            protected MovieHolder doInBackground(String... params) {
                // Return null if there is no API_KEY
                if (params.length == 0) {return null;}

                final String BASE_URL = "http://api.themoviedb.org/3/movie";
                final String API_KEY = "api_key";
                final String VIDEOS = "videos";
                final String REVIEWS = "reviews";
                final long MOVIE_ID = theMovie.movieId;
                String trailersJsonStr = null;
                String reviewsJsonStr = null;

                // Step1: Make HTTP request.
                HttpURLConnection urlConnection = null;
                BufferedReader bufferedReader = null;

                /*******************HTTP request for trailers**********************/
                try {
                    // Construct URI for trailer query
                    final String TRAILER_URL = BASE_URL + "/" + MOVIE_ID + "/" + VIDEOS;
                    Uri builtTrailerUri = Uri.parse(TRAILER_URL).buildUpon()
                            .appendQueryParameter(API_KEY, params[0])
                            .build();

                    Log.v(TASK_LOG_TAG, "Built trailer URL " + builtTrailerUri.toString());

                    // Construct URL
                    URL trailerURL = new URL(builtTrailerUri.toString());

                    // Open the connection
                    urlConnection = (HttpURLConnection)trailerURL.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();

                    // Step 2; Read response from input stream(string of JSON)
                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuffer buffer = new StringBuffer();

                    if(inputStream == null) {return  null;}
                    bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;

                    // Read input stream into string
                    while ((line = bufferedReader.readLine() ) != null) {
                        buffer.append(line + "\n");
                    }

                    if(buffer == null) {return null;}

                    trailersJsonStr = buffer.toString();
                    Log.v(TASK_LOG_TAG, "Trailer JSON String: " + trailersJsonStr);
                } catch(IOException e) {
                    Log.e(TASK_LOG_TAG, e.getMessage(), e);
                    e.printStackTrace();
                    return null;
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }

                    if (bufferedReader != null) {
                        try {
                            bufferedReader.close();
                        } catch (IOException e) {
                            Log.e(TASK_LOG_TAG, e.getMessage(), e);
                            e.printStackTrace();
                        }
                    }
                }
                /*******************HTTP request for Reviews**********************/

                try {
                    // Construct URI for reviews query.
                    final String REVIEW_URL = BASE_URL + "/" + MOVIE_ID + "/" + REVIEWS;
                    Uri builtReviewUri = Uri.parse(REVIEW_URL).buildUpon()
                            .appendQueryParameter(API_KEY, params[0])
                            .build();

                    Log.v(TASK_LOG_TAG, "Build review URI: " + builtReviewUri.toString());

                    // Construct URL
                    URL reviewURL = new URL(builtReviewUri.toString());

                    // Open connection
                    urlConnection = (HttpURLConnection)reviewURL.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();

                    // Step 2: Read response from input stream (String of JSON)
                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuffer buffer = new StringBuffer();

                    if (inputStream == null) {return  null;}

                    bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;

                    // Read input stream into string;
                    while((line = bufferedReader.readLine()) != null) {
                        buffer.append(line + "\n");
                    }

                    if (buffer == null) {return null;}

                    // Get JSON String out of buffer
                    reviewsJsonStr = buffer.toString();

                    Log.v(TASK_LOG_TAG, "Reviews JSON string: " + reviewsJsonStr);
                } catch (IOException e) {
                    Log.e(TASK_LOG_TAG, e.getMessage(), e);
                    e.printStackTrace();
                    return  null;
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                    if (bufferedReader != null) {
                        try {
                            bufferedReader.close();
                        } catch (IOException e) {
                            Log.e(TASK_LOG_TAG, e.getMessage(), e);
                            e.printStackTrace();
                        }
                    }
                }


                // Extract trailers and reviews from JSON string and add them to the movie
                try {
                    getTrailersFromJson(trailersJsonStr);
                    getReviewsFromJson(reviewsJsonStr);
                    return theMovie;

                } catch (JSONException e) {
                    Log.e(TASK_LOG_TAG, e.getMessage(), e);
                    e.printStackTrace();
                }

                return null;
            }
            @Override
            protected void onPostExecute(MovieHolder results) {
                // Adding trailers and reviews programmatically
                // Clear all existing views in it
                trailersReviewsContainer.removeAllViews();

                // Add trailers
                if (results.trailers.size() != 0) {
                    for (final MovieHolder.Trailer video : theMovie.trailers) {
                        View trailerItem = LayoutInflater.from(getActivity()).inflate(R.layout.trailer_item, null);
                        // Set the trailer title
                        TextView trailerTitle = (TextView)trailerItem.findViewById(R.id.movie_trailer_name);
                        trailerTitle.setText(video.trailerName);

                        // Add OnClickListener to the view
                        trailerItem.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Start an Intent to open the trailer on Youtube
                                playTrailerIntent(video.key);
                            }
                        });

                        // Add this trailer to the end of container
                        trailersReviewsContainer.addView(trailerItem);
                    }
                }

                // Add reviews
                if (results.reviews.size() != 0) {
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
        }
    }
}
