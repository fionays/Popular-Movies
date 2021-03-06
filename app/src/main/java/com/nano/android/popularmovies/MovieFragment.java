package com.nano.android.popularmovies;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.nano.android.popularmovies.data.FavoritedContract;

import java.util.ArrayList;
import java.util.Vector;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A fragment which contains the grid view to display movies fetched form server.
 * TODO: Why parcelable is used in this project? IPC and Intent?
 */
    public class MovieFragment extends Fragment {

        private static final String LOG_TAG = MovieFragment.class.getSimpleName();
        private static final String KEY = "movie_list";
        private ImageAdapter imageAdapter;
        private boolean isRetained = false;

        private ArrayList<MovieHolder> movieList;

        @Bind(R.id.app_bar)Toolbar toolbarMain;
        @Bind(R.id.gridview_movie)GridView gridView;
        /**
        * A callback interface that all activities containing this fragment must implement.
        * This mechanism allows activities to be notified of item selections.
        * Let the MovieFragment talk to the MainActivity.
        */
         public interface CallBack {

             // DetailFragmentCallBack for when an item has been selected.
              void onItemSelected(MovieHolder theMovie);
        }

        public MovieFragment() {}

        @Override
        public void onCreate(Bundle onSavedInstanceStates) {

            Log.v(LOG_TAG, "MovieFragment: Entering onCreate()");

            super.onCreate(onSavedInstanceStates);
            if(onSavedInstanceStates == null || !onSavedInstanceStates.containsKey(KEY)) {
                //Create a new ArrayList based on the array of parcelable MovieHolder object.
               movieList = new ArrayList<MovieHolder>();
            } else {
                //Restore the movie list if it was saved.
                movieList = onSavedInstanceStates.getParcelableArrayList(KEY);
                isRetained = true;
            }
        }

        @Override
        public void onSaveInstanceState(Bundle states) {
             //Save the Parcelable movieList data into the mapping of this bundle
            // if any configuration change, like rotation.
            states.putParcelableArrayList(KEY, movieList);
            super.onSaveInstanceState(states);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_movie, container, false);
            ButterKnife.bind(this, rootView);

            // Set the toolbar to act as ActionBar for DetailActivity window
            ((AppCompatActivity)getActivity()).setSupportActionBar(toolbarMain);
            Log.i(LOG_TAG, (toolbarMain == null) ? "toolbar is null" : "toolbar is not null");
            toolbarMain.setTitle(R.string.app_name);

            // Create the ImageAdapter
            imageAdapter = new ImageAdapter(getActivity(), movieList);
            // Attached the ImageAdapter to the GridView
            gridView.setAdapter(imageAdapter);
            // Set on item click listener and create a intent to sent the clicked MovieHolder
            // object to DetailActivity.
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    MovieHolder clickedMovie = imageAdapter.getItem(position);

                    // Talk to the MainActivity which implements the Callback interface and
                    // invoke the callback method.
                    // Pass the selected movie object to MainActivity and let it decide how to
                    // dispatch the event.
                    // 1) on phone, launch DetailActivity, OR
                    // 2) on tablet, replace DetailFragment
                    ((CallBack)getActivity()).onItemSelected(clickedMovie);

                    Log.v(LOG_TAG, "Select one movie at position " + position);
                }
            });

            return rootView;
        }

        @Override
        public void onStart() {
            Log.v(LOG_TAG, "MovieFragment: Entering onStart()");

            super.onStart();
            if (Utility.isSortFavorite(getActivity())) {
                retrieveFromDatabase();
            } else {
                fetchFromServer();
            }

        }

    private void fetchFromServer() {
        FetchMovieTask fetchMovieTask = new FetchMovieTask(getActivity(), imageAdapter);
        fetchMovieTask.execute(Utility.getPreferredSort(getActivity()));
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
