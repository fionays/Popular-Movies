package com.nano.android.popularmovies;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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

        @Bind(R.id.title)TextView title;
        @Bind(R.id.release_date)TextView release;
        @Bind(R.id.vote_average)TextView vote;
        @Bind(R.id.overview)TextView overView;
        @Bind(R.id.poster)ImageView poster;
        @Bind(R.id.trailer_reviews_container)LinearLayout trailersReviewsContainer;


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

        @Override
        public void onStart() {
            super.onStart();
            FetchDataTask fetchDataTask = new FetchDataTask();
            fetchDataTask.execute(BuildConfig.THE_MOVIE_DB_API_KEY);
        }

        /**
         * An inner class to fetch trailers and reviews for this movie
         */
        public class FetchDataTask extends AsyncTask<String, Void, MovieHolder> {

            private final String TASK_LOG_TAG = FetchDataTask.class.getSimpleName();

            /**
             * Helper method: Start Youtube to play trailer using Intent
             * @param key  The source of the trailer
             */
            private void playTrailerIntent(String key) {

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
