package com.nano.android.popularmovies;

/**
 * Created by YANG on 11/8/2015.
 */

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Class to fetch trailers and reviews for this movie
 */
public class FetchTrailerReview extends AsyncTask<String, Void, MovieHolder> {

    private final String TASK_LOG_TAG = FetchTrailerReview.class.getSimpleName();
    private final Context mContext;
    private final LinearLayout trailersReviewsContainer;
    private final MovieHolder theMovie;

    public FetchTrailerReview(Context context, LinearLayout container, MovieHolder movie) {
        mContext = context;
        theMovie = movie;
        trailersReviewsContainer = container;
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

        // Add trailers
        // Clear all existing views in it
        if (results.trailers.size() != 0) {
            // display trailers
            for (final MovieHolder.Trailer trailer : results.trailers) {
                View trailerItem = LayoutInflater.from(mContext).inflate(R.layout.trailer_item, null);
                // Set the trailer title
                TextView trailerTitle = (TextView)trailerItem.findViewById(R.id.movie_trailer_name);
                trailerTitle.setText(trailer.trailerName);

                // Add OnClickListener to the view
                trailerItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Start an Intent to open the trailer on Youtube
                        Utility.playTrailerIntent(trailer.key, mContext);
                    }
                });

                // Add this trailer to the end of container
                trailersReviewsContainer.addView(trailerItem);
            }
        }

        // Add reviews
        if (results.reviews.size() != 0) {
            for (final MovieHolder.Review review : results.reviews) {
                View reviewItem = LayoutInflater.from(mContext).inflate(R.layout.review_item, null);

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
