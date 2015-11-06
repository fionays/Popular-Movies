package com.nano.android.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * Created by YANG on 11/4/2015.
 */
public class FavoritedProvider extends ContentProvider{

    private static final UriMatcher mUriMatcher = buildUriMatcher();
    private FavoritedDbHelper dbHelper;

    static final int FAVORITE = 100;
    static final int FAVORITE_ID = 101;
    static final int FAVORITE_MOVIE_ID = 102;
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
        matcher.addURI(authority, FavoritedContract.PATH_FAVORITE +
                "/" + FavoritedContract.FavoriteEntry.COLUMN_MOVIE_ID + "/#", FAVORITE_MOVIE_ID);

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
    public boolean onCreate() {
        dbHelper = new FavoritedDbHelper(getContext());
        return true;
    }

    /*
        Use UriMatcher to decide which type of URI it is.
     */
    @Override
    public String getType(Uri uri) {

        final int match = mUriMatcher.match(uri);

        switch (match) {
            case FAVORITE:
                return FavoritedContract.FavoriteEntry.CONTENT_DIR_TYPE;    // dir
            case FAVORITE_ID:
                return FavoritedContract.FavoriteEntry.CONTENT_ITEM_TYPE;   // single item
            case FAVORITE_MOVIE_ID:
                return FavoritedContract.FavoriteEntry.CONTENT_ITEM_TYPE;   // single item
            case TRAILER:
                return FavoritedContract.TrailerEntry.CONTENT_DIR_TYPE;     // dir
            case TRAILER_MOVIE_ID:
                return FavoritedContract.TrailerEntry.CONTENT_DIR_TYPE;     // multiple items,dir
            case TRAILER_ID:
                return FavoritedContract.TrailerEntry.CONTENT_ITEM_TYPE;    // single item
            case REVIEW:
                return FavoritedContract.ReviewEntry.CONTENT_DIR_TYPE;      // dir
            case REVIEW_MOVIE_ID:
                return FavoritedContract.ReviewEntry.CONTENT_DIR_TYPE;      // multiple items, dir
            case REVIEW_ID:
                return FavoritedContract.ReviewEntry.CONTENT_ITEM_TYPE;     // single item
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        Cursor retCursor;
        // Given the URI, determine what kind of request it is and, query database.
        switch(mUriMatcher.match(uri)) {
            // Query the favorite table according to the args
            case FAVORITE: {
                retCursor = dbHelper.getReadableDatabase().query(
                        FavoritedContract.FavoriteEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }

            // Query a single record based on the _ID selected. The "selection" arg is the _ID
            // indicated in the uri, not the "selection" passed in to the method.
            case FAVORITE_ID: {
                retCursor = dbHelper.getReadableDatabase().query(
                        FavoritedContract.FavoriteEntry.TABLE_NAME,
                        projection,
                        FavoritedContract.FavoriteEntry._ID + " =?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // Query a single record based on the movie_ID selected.
            case FAVORITE_MOVIE_ID: {
                long sMovieId = FavoritedContract.FavoriteEntry.getMovieIdFromUri(uri);
                retCursor = dbHelper.getReadableDatabase().query(
                        FavoritedContract.FavoriteEntry.TABLE_NAME,
                        projection,
                        FavoritedContract.FavoriteEntry.COLUMN_MOVIE_ID + " =?",
                        new String[]{String.valueOf(sMovieId)},
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // Query the trailer table according to the args passed in.
            case TRAILER: {
                retCursor = dbHelper.getReadableDatabase().query(
                        FavoritedContract.TrailerEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            // Query the subset of the trailer table based on the movie_id in the uri.
            case TRAILER_MOVIE_ID: {
                long sMovieId = FavoritedContract.TrailerEntry.getMovieIdFromUri(uri);
                retCursor = dbHelper.getReadableDatabase().query(
                        FavoritedContract.TrailerEntry.TABLE_NAME,
                        projection,
                        FavoritedContract.TrailerEntry.COLUMN_MOVIE_ID + " =?",
                        new String[]{String.valueOf(sMovieId)},
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            // Query a single record
            case TRAILER_ID: {
                retCursor = dbHelper.getReadableDatabase().query(
                        FavoritedContract.TrailerEntry.TABLE_NAME,
                        projection,
                        FavoritedContract.FavoriteEntry._ID + " =?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            // Query the review table according to the args passed in.
            case REVIEW: {
                retCursor = dbHelper.getReadableDatabase().query(
                        FavoritedContract.ReviewEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            // Query the subset of the review table based on the movie_id in the uri.
            case REVIEW_MOVIE_ID: {
                long sMovieId = FavoritedContract.ReviewEntry.getMovieIdFromUri(uri);
                retCursor = dbHelper.getReadableDatabase().query(
                        FavoritedContract.ReviewEntry.TABLE_NAME,
                        projection,
                        FavoritedContract.ReviewEntry.COLUMN_MOVIE_ID + " =?",
                        new String[]{String.valueOf(sMovieId)},
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            case REVIEW_ID: {
                retCursor = dbHelper.getReadableDatabase().query(
                        FavoritedContract.ReviewEntry.TABLE_NAME,
                        projection,
                        FavoritedContract.ReviewEntry._ID + " =?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Register the cursor as a observer and keeps watching the uri.
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = mUriMatcher.match(uri);
        Uri returnUri;

        switch(match) {
            // Insert to favorite table
            case FAVORITE: {
                long _id = db.insert(FavoritedContract.FavoriteEntry.TABLE_NAME, null, values);

                // If _id = -1, failed to insert.
                if( _id > 0 )
                    returnUri = FavoritedContract.FavoriteEntry.buildFavoriteUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri );
                break;
            }

            // Insert into trailer table
            case TRAILER: {
                long _id = db.insert(FavoritedContract.TrailerEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = FavoritedContract.TrailerEntry.buildTrailerUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }

            // Insert into review table
            case REVIEW: {
                long _id = db.insert(FavoritedContract.ReviewEntry.TABLE_NAME, null, values);
                if (_id >0)
                    returnUri = FavoritedContract.ReviewEntry.buildReviewUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Notify observers that the content has changed.
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = mUriMatcher.match(uri);
        int rowsDeleted;

        switch (match) {
            case FAVORITE:{
                rowsDeleted = db.delete(
                        FavoritedContract.FavoriteEntry.TABLE_NAME, selection, selectionArgs);
                // Reset _ID
                db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                        FavoritedContract.FavoriteEntry.TABLE_NAME + "'");
                break;
            }
            case FAVORITE_ID: {
                rowsDeleted = db.delete(
                        FavoritedContract.FavoriteEntry.TABLE_NAME,
                        FavoritedContract.FavoriteEntry._ID + " =?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))}
                );
                // Reset _ID
                db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                        FavoritedContract.FavoriteEntry.TABLE_NAME + "'");
                break;
            }
            case TRAILER: {
                rowsDeleted = db.delete(
                        FavoritedContract.TrailerEntry.TABLE_NAME, selection, selectionArgs);
                // Reset _ID
                db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                        FavoritedContract.TrailerEntry.TABLE_NAME + "'");
                break;
            }
            case TRAILER_MOVIE_ID: {
                long sMovieId = FavoritedContract.TrailerEntry.getMovieIdFromUri(uri);
                rowsDeleted = db.delete(
                        FavoritedContract.TrailerEntry.TABLE_NAME,
                        FavoritedContract.TrailerEntry.COLUMN_MOVIE_ID + " =?",
                        new String[]{String.valueOf(sMovieId)}
                );
                // Reset _ID
                db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                        FavoritedContract.TrailerEntry.TABLE_NAME + "'");
                break;
            }
            case TRAILER_ID: {
                rowsDeleted = db.delete(
                        FavoritedContract.TrailerEntry.TABLE_NAME,
                        FavoritedContract.TrailerEntry._ID + " =?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))}
                );
                // Reset _ID
                db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                        FavoritedContract.TrailerEntry.TABLE_NAME + "'");
                break;
            }
            case REVIEW: {
                rowsDeleted = db.delete(
                        FavoritedContract.ReviewEntry.TABLE_NAME, selection, selectionArgs);
                // Reset _ID
                db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                        FavoritedContract.ReviewEntry.TABLE_NAME + "'");
                break;
            }
            case REVIEW_MOVIE_ID: {
                long sMovieId = FavoritedContract.ReviewEntry.getMovieIdFromUri(uri);
                rowsDeleted = db.delete(
                        FavoritedContract.ReviewEntry.TABLE_NAME,
                        FavoritedContract.ReviewEntry.COLUMN_MOVIE_ID + " =?",
                        new String[]{String.valueOf(sMovieId)}
                );
                // Reset _ID
                db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                        FavoritedContract.ReviewEntry.TABLE_NAME + "'");
                break;
            }
            case REVIEW_ID: {
                rowsDeleted = db.delete(
                        FavoritedContract.ReviewEntry.TABLE_NAME,
                        FavoritedContract.ReviewEntry._ID + " =?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))}
                );
                // Reset _ID
                db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                        FavoritedContract.ReviewEntry.TABLE_NAME + "'");
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Notify the content resolver of the database change
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = mUriMatcher.match(uri);
        int rowsUpdated;

        if (values == null) {
            throw new IllegalArgumentException("Can not have null content values to update.");
        }
        switch (match) {
            case FAVORITE:{
                rowsUpdated = db.update(
                        FavoritedContract.FavoriteEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            }
            case FAVORITE_ID: {
                rowsUpdated = db.update(
                        FavoritedContract.FavoriteEntry.TABLE_NAME,
                        values,
                        FavoritedContract.FavoriteEntry._ID + " =?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))});
                break;
            }
            case TRAILER: {
                rowsUpdated = db.update(
                        FavoritedContract.TrailerEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            }
            case TRAILER_MOVIE_ID: {
                long sMovieId = FavoritedContract.TrailerEntry.getMovieIdFromUri(uri);
                rowsUpdated = db.update(
                        FavoritedContract.TrailerEntry.TABLE_NAME,
                        values,
                        FavoritedContract.TrailerEntry.COLUMN_MOVIE_ID + " =?",
                        new String[]{String.valueOf(sMovieId)});
                break;
            }
            case TRAILER_ID:{
                rowsUpdated = db.update(
                        FavoritedContract.TrailerEntry.TABLE_NAME,
                        values,
                        FavoritedContract.TrailerEntry._ID + " =?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))});
                break;
            }
            case REVIEW: {
                rowsUpdated = db.update(
                        FavoritedContract.ReviewEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            }
            case REVIEW_MOVIE_ID: {
                long sMovieId = FavoritedContract.ReviewEntry.getMovieIdFromUri(uri);
                rowsUpdated = db.update(
                        FavoritedContract.ReviewEntry.TABLE_NAME,
                        values,
                        FavoritedContract.ReviewEntry.COLUMN_MOVIE_ID + " =?",
                        new String[]{String.valueOf(sMovieId)});
                break;
            }
            case REVIEW_ID: {
                rowsUpdated = db.update(
                        FavoritedContract.ReviewEntry.TABLE_NAME,
                        values,
                        FavoritedContract.ReviewEntry._ID + " =?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))});
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    /*
        Wrap multiple insertion in one transaction. Only apply bulkInsert to
        trailer table and review table.
     */
    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = mUriMatcher.match(uri);
        int retCount = 0;

        switch (match) {
            case TRAILER: {
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(FavoritedContract.TrailerEntry.TABLE_NAME, null, value);
                        if (_id != 0) {
                            retCount ++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                break;
            }
            case REVIEW: {
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(FavoritedContract.ReviewEntry.TABLE_NAME, null, value);
                        if (_id != 0) {
                            retCount ++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            }
            default:
                //super.bulkInsert is implemented the loop of insert without transaction
                return super.bulkInsert(uri, values);

        }
        getContext().getContentResolver().notifyChange(uri, null);
        return retCount;
    }
}
