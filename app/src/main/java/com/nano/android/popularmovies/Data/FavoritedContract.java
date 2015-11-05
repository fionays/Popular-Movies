package com.nano.android.popularmovies.data;

/**
 * Created by YANG on 11/4/2015.
 */

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines table names and column names for favorite database.
 * Three tables in the database:
 * -- favorite table:
 *                   store basic detail info for the movie, (movie_id, poster, title, vote
 *                   , overview, release date)
 * -- trailer table:
 *                  store all trailers for favorite movies, (movie_id, trailer_name, trailer_key)
 * -- review table:
 *                  store all reviews for favorite movies, (movie_id, review_author, review_content)
 */
public class FavoritedContract {

    // Add "Content authority" for the contentProvider
    public static final String CONTENT_AUTHORITY = "com.nano.android.popularmovies";

    // Construct the base URI of all URI's which apps will use to access the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths appended to the base content URI
    public static final String PATH_FAVORITE = "favorite";
    public static final String PATH_TRAILER = "trailer";
    public static final String PATH_REVIEW = "review";

    /**
     * Inner class that defines the table contents of the favorite table.
     */
    public static final class FavoriteEntry implements BaseColumns {

        // Table name
        public static final String TABLE_NAME = "favorite";

        // Table columns
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_POSTER = "poster";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_VOTE = "average_vote";
        public static final String COLUMN_OVERVIEW = "overview";

        // Add ContentProvider to the FavoriteEntry***************************
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITE).build();

        // TODO: Still confused about those type
        // Create a cursor of base type directory for multiple entries
        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITE;
        // Create a cursor of base type of item for single entry
        public static final String CURSOR_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITE;

        // For building URI for a single row with _ID
        public static Uri buildFavoriteUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    /**
     * Inner class that defines the table contents of trailer table.
     */
    public static final class TrailerEntry implements BaseColumns {

        public static final String TABLE_NAME = "trailer";

        // Columns
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_TRAILER_NAME = "name";
        public static final String COLUMN_TRAILER_KEY = "key";

        // Add ContentProvoder to the TrailerEntry**************************
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRAILER).build();

        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRAILER;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" +PATH_TRAILER;

        public static Uri buildTrailerUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        // Help to build content provider query, to get a dataset with a specific movie_id
        // Still a Dir.
        public static Uri buildTrailerWithMovieId(long movie_id) {
            return CONTENT_URI.buildUpon().appendPath(Long.toString(movie_id)).build();
        }

        public static long getMovieIdFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }
    }

    /**
     * Inner class that defines the table contents of review table.
     */
    public static final class ReviewEntry implements BaseColumns {

        public static final String TABLE_NAME = "review";

        // Columns
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_AUTHOR = "author";
        public static final String COLUMN_CONTENT = "content";

        // Add ContentProvider to the TrailerEntry**************************
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEW).build();

        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEW;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" +PATH_REVIEW;

        public static Uri buildReviewUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        // Help to build content provider query, to get a dataset by filtering a specific movie_id
        // Still a Dir.
        public static Uri buildReviewWithMovieId(long movie_id) {
            return CONTENT_URI.buildUpon().appendPath(Long.toString(movie_id)).build();
        }

        public static long getMovieIdFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }
    }
}

