package com.nano.android.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

/**
 * Created by YANG on 11/4/2015.
 */
public class FavoritedProvider extends ContentProvider{

    private static final UriMatcher mUriMatcher = buildUriMatcher();
    private FavoritedDbHelper dbHelper;

    static final int FAVORITE = 100;
    static final int FAVORITE_ID = 101;
    static final int TRAILER = 200;
    static final int TRAILER_ID = 201;
    static final int TRAILER_MOVIE_ID = 202;
    static final int REVIEW = 300;
    static final int REVIEW_ID = 301;
    static final int REVIEW_MOVIE_ID = 302;

    /*
       Build UriMatcher. It will match each URI to the FAVORITE, FAVORITE_ID, TRAILER, TRAILER_ID,
       TRAILER_MOVIE_ID, REVIEW, REVIEW_ID, REVIEW_MOVIE_ID integer constants.
     */
    static UriMatcher buildUriMatcher() {
        // It's common to use NO_MATCH as the code for this case.
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        String authority = FavoritedContract.CONTENT_AUTHORITY;

        // Use addURI to match each of types.
        matcher.addURI(authority, FavoritedContract.PATH_FAVORITE, FAVORITE);
        matcher.addURI(authority, FavoritedContract.PATH_FAVORITE + "/#", FAVORITE_ID);

        matcher.addURI(authority, FavoritedContract.PATH_TRAILER, TRAILER);
        matcher.addURI(authority, FavoritedContract.PATH_TRAILER + "/#", TRAILER_ID);
        matcher.addURI(authority,FavoritedContract.PATH_TRAILER +
                "/" + FavoritedContract.TrailerEntry.COLUMN_MOVIE_ID + "/#", TRAILER_MOVIE_ID);

        matcher.addURI(authority, FavoritedContract.PATH_REVIEW, REVIEW);
        matcher.addURI(authority, FavoritedContract.PATH_REVIEW + "/#", REVIEW_ID);
        matcher.addURI(authority,FavoritedContract.PATH_REVIEW +
                "/" + FavoritedContract.ReviewEntry.COLUMN_MOVIE_ID + "/#", REVIEW_MOVIE_ID);

        return matcher;
    }
    @Override
    public boolean onCreate() {return false;}

    @Override
    public String getType(Uri uri) {return null;}

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {return null;}

    @Override
    public Uri insert(Uri uri, ContentValues values) {return null;}

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {return 0;}
    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {return 0;}
}
