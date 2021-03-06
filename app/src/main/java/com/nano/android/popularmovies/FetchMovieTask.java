package com.nano.android.popularmovies;

/**
 * Created by YANG on 11/8/2015.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

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
 * An inner class to fetch the movie data from themoviedb.
 */
public class FetchMovieTask extends AsyncTask<String, Void, MovieHolder[]> {

    private final String TASK_LOG_TAG = FetchMovieTask.class.getSimpleName();
    private ProgressDialog pDialog;
    private ImageAdapter imageAdapter;

    private final Context mContext;

    public FetchMovieTask(Context mContext, ImageAdapter imageAdapter) {
        this.mContext = mContext;
        this.imageAdapter = imageAdapter;
    }
    /**
     * Show a progress dialog bar in the UI
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pDialog = new ProgressDialog(mContext);
        pDialog.setMessage("Loading");
        pDialog.setCancelable(false);
        pDialog.show();
    }

    /**
     * Extract data we need:
     *                     "original_title", "poster_path", "overview",
     *                      "vote_average", "release_date", "id", "Trailer"
     * @param movieJsonStr  String representation of complete movie in JSON format
     * @return              An array of MovieHolder which is parcelable.
     */
    private MovieHolder[] getMovieDataFromJson(String movieJsonStr) throws JSONException {
        // Return an array of MovieHolder
        // JSON objects which need to be pulled out.
        final String RESULTS = "results";
        final String POSTER_PATH = "poster_path";
        final String ORIGINAL_TITLE = "original_title";
        final String OVERVIEW = "overview";
        final String RELEASE_DATE = "release_date";
        final String VOTE_AVERAGE = "vote_average";
        final String ID = "id";
        final String BASE_URL = "http://image.tmdb.org/t/p/";
        final String IMAGE_SIZE = "w185/";

        // Convert JSON string to JSON object
        JSONObject resultJson = new JSONObject(movieJsonStr);
        JSONArray resultArray = resultJson.getJSONArray(RESULTS);

        // Count the number of movies in the resultArray.
        int numbers = resultArray.length();
        // TODO: May have error when creating array!!!!!!!!!!!!!
        MovieHolder[] movieHolders = new MovieHolder[numbers];

        // Construct MovieHolder Object for each object in resultArray
        for (int i = 0; i < numbers; i++) {
            // Get JSON object for each movie.
            JSONObject movieJson = resultArray.getJSONObject(i);

            String posterPath = BASE_URL + IMAGE_SIZE + movieJson.getString(POSTER_PATH);
            String originalTitle = movieJson.getString(ORIGINAL_TITLE);
            String overview = movieJson.getString(OVERVIEW);
            String releaseDate = movieJson.getString(RELEASE_DATE);
            int voteAverage = movieJson.getInt(VOTE_AVERAGE);
            long id = movieJson.getInt(ID);

            MovieHolder movieHolder = new MovieHolder(posterPath, originalTitle, overview
                    , releaseDate, voteAverage, id);

            movieHolders[i] = movieHolder;
        }

        return movieHolders;
    }

    /**
     *  Make a request to the server and get information. The default value of key "page" is 1.
     * @param params The api_KEY which is needed to use the API from themoviedb.org.
     * @return       The array of MovieHolder object got from form server.
     */
    @Override
    protected MovieHolder[] doInBackground(String...params) {

        // If there is no sort_by value, nothing to look up, return null.
        if(params.length == 0) {return null;}
        /***************************HTTP request for movie.*************************/
        //STEP 1: Make HTTP request.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        // To hold raw JSON response as a string.
        String movieJsonStr = null;
        // Discover sort_by parameter. Retrieve from sharedPreference.
        String sortPref = params[0];

        try {
            // Construct URI for themoviedb movie query.
            final String MOVIE_BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
            final String SORT_BY = "sort_by";
            final String API_KEY = "api_key";

            // parse() returns a Uri; buildUpon() construct a new Uri.Builder;
            // append() returns a Uri.Builder; build() returns a Uri.
            Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                    .appendQueryParameter(SORT_BY, sortPref)
                    .appendQueryParameter(API_KEY, BuildConfig.THE_MOVIE_DB_API_KEY)
                    .build();

            // Construct the URL
            URL myUrl = new URL(builtUri.toString());

            // Create a request to the server and open the connection.
            urlConnection = (HttpURLConnection)myUrl.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // STEP 2: Read response from input stream (String of JSON).
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();

            // Check if input stream exists.
            if(inputStream == null) {return null;}

            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;

            // Read input stream into string.
            while((line = reader.readLine() )!= null) {
                // Add a newline at the end of each line of JSON data.
                // And put each line into the String Buffer.
                buffer.append(line + "\n");
            }

            // Check if the buffer is empty.
            if(buffer == null) {return null;}

            // Get the JSON data out of String Buffer (as string).
            movieJsonStr = buffer.toString();


        } catch(IOException e) {
            // Log any errors if the movie data does not get successfully.
            // And return null.
            Log.e(TASK_LOG_TAG,e.getMessage(),e);
            e.printStackTrace();
            return null;
        } finally {
            // STEP 3: Clear up the disconnected connections and log any errors.
            if(urlConnection != null) {
                urlConnection.disconnect();
            }
            if(reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(TASK_LOG_TAG, e.getMessage(), e);
                    e.printStackTrace();
                }
            }
        }

        // Extract data from JSON String and return it as the array of MovieHolder.
        try {
            return getMovieDataFromJson(movieJsonStr);
        } catch (JSONException e) {
            Log.e(TASK_LOG_TAG,e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(MovieHolder[] results) {
        // Update the ImageAdapter (put the the array of MovieHolder which is got from
        // remote server into Adapter)
        if (results != null) {
            imageAdapter.clear();
            imageAdapter.addAll(results);
        }
        pDialog.dismiss();
    }
}
