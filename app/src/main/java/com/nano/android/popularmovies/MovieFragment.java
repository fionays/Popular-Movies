package com.nano.android.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.nano.android.popularmovies.data.FavoritedContract;

import java.util.ArrayList;
import java.util.Vector;


/**
 * A fragment which contains the grid view to display movies fetched form server.
 * TODO: Why parcelable is used in this project? IPC and Intent?
 */
    public class MovieFragment extends Fragment {

        private static final String LOG_TAG = MovieFragment.class.getSimpleName();
        private static final String KEY = "movie";
        private ImageAdapter imageAdapter;

        private ArrayList<MovieHolder> movieList = new ArrayList<MovieHolder>();

        public MovieFragment() {}

        @Override
        public void onCreate(Bundle onSavedInstanceStates) {
            super.onCreate(onSavedInstanceStates);
            //setHasOptionsMenu(true);

             //Restore the movie list if it was saved.
            //if(onSavedInstanceStates == null || !onSavedInstanceStates.containsKey(KEY)) {
              // movieList = new ArrayList<MovieHolder>(Arrays.asList(movieHolders));
            //} else {
                // Create a new ArrayList based on the array of parcelable MovieHolder object.
                //movieList = onSavedInstanceStates.getParcelableArrayList("movie");
           // }
       // }

        //@Override
        //public void onSaveInstanceState(Bundle states) {
            // Save the Parcelable movieList into the mapping of this bundle
            // if any configuration change, like rotation.
            //states.putParcelableArrayList(KEY, movieList);
            //super.onSaveInstanceState(states);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_movie, container, false);

            // Create the ImageAdapter
            imageAdapter = new ImageAdapter(getActivity(), movieList);
            // Get the GridView.
            GridView gridView = (GridView)rootView.findViewById(R.id.gridview_movie);
            // Attached the ImageAdapter to the GridView
            gridView.setAdapter(imageAdapter);
            // Set on item click listener and create a intent to sent the clicked MovieHolder
            // object to DetailActivity.
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    MovieHolder clickedMovie = imageAdapter.getItem(position);
                    Intent myIntent = new Intent(getActivity(), DetailActivity.class);
                    myIntent.putExtra("MovieHolder", clickedMovie);
                    startActivity(myIntent);
                }
            });

            return rootView;
        }

        @Override
        public void onStart() {
            super.onStart();
            String sortPref = Utility.getPreferredSort(getActivity());

            // Retrieve movie from tababase
            if (Utility.isSortFavorite(getActivity(),sortPref)) {
                retrieveFromDatabase();
            } else {
                // Fetch movies from server if sort by popularity and rate
                FetchMovieTask fetchMovieTask = new FetchMovieTask(getActivity(), imageAdapter);
                fetchMovieTask.execute(sortPref);
            }
        }

    /**
     * Create an array of MovieHolder Objects for each ContentValues
     * @param vector An vector of ContentValues
     * @return       An array of MovieHolder
     */
    private MovieHolder[] bindMovieValuesToMovieHolder( Vector<ContentValues> vector) {
        MovieHolder[] movies = new MovieHolder[vector.size()];
        for (int i = 0; i < movies.length; i ++) {
            ContentValues value = vector.get(i);
            String posterPath = value.getAsString(FavoritedContract.FavoriteEntry.COLUMN_POSTER);
            String originalTitle = value.getAsString(FavoritedContract.FavoriteEntry.COLUMN_TITLE);
            String overview = value.getAsString(FavoritedContract.FavoriteEntry.COLUMN_OVERVIEW);
            String releaseDate = value.getAsString(FavoritedContract.FavoriteEntry.COLUMN_RELEASE_DATE);
            int voteAverage = value.getAsByte(FavoritedContract.FavoriteEntry.COLUMN_VOTE);
            long id = value.getAsLong(FavoritedContract.FavoriteEntry.COLUMN_MOVIE_ID);
            MovieHolder movie = new MovieHolder(posterPath,originalTitle,overview,releaseDate,
                    voteAverage, id);
            movie.favorite = true;
            movies[i] = movie;
        }
        return movies;
    }
    /*
        Helper method: Query the favorite table and populate the fragment with posters got from db.
     */
    private void retrieveFromDatabase() {

        // Query favorite table
        Uri favoriteUri = FavoritedContract.FavoriteEntry.CONTENT_URI;
        Cursor cursor = getActivity().getContentResolver().query(
                favoriteUri, null, null, null, null);

        if (!cursor.moveToFirst()) {
            Toast.makeText(getActivity(), "You Don't Have Favorite Movie!", Toast.LENGTH_LONG).show();
            return;
        }

        // Display what have sored in favorite table
        Vector<ContentValues> cVVector = new Vector<ContentValues>(cursor.getCount());
        do {
            ContentValues cv = new ContentValues();
            DatabaseUtils.cursorRowToContentValues(cursor, cv);
            cVVector.add(cv);
        } while (cursor.moveToNext());

        MovieHolder[] movies = bindMovieValuesToMovieHolder(cVVector);
        imageAdapter.clear();
        imageAdapter.addAll(movies);
    }
    }
