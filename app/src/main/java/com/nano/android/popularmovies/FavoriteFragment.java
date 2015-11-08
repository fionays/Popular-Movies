package com.nano.android.popularmovies;

/**
 * Created by YANG on 11/7/2015.
 */

import android.support.v4.app.Fragment;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Bundle;
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
 * A fragment which contains the graid view to display movies fetched from database.
 */
public class FavoriteFragment extends Fragment {

    private static final String LOG_TAG = FavoriteFragment.class.getSimpleName();
    private  ImageAdapter imageAdapter;

    public FavoriteFragment() {}

    @Override
    public void onCreate(Bundle onSavedInstanceStates) {
        super.onCreate(onSavedInstanceStates);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie, container, false);

        imageAdapter = new ImageAdapter(getActivity(), new ArrayList<MovieHolder>());
        GridView gridView = (GridView)rootView.findViewById(R.id.gridview_movie);
        // Attach imageAdapter to gridView
        gridView.setAdapter(imageAdapter);
        // When clicked, create an intent to sent the MovieHolder object to DetailActivity.
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
        fetchFromDatabase();
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
        Helper method: Query the favorite table and populate the fragment with data got from db.
     */
     private void fetchFromDatabase() {

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
