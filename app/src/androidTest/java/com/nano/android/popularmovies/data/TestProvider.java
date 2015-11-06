package com.nano.android.popularmovies.data;

/**
 * Created by YANG on 11/5/2015.
 */

import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import com.nano.android.popularmovies.data.FavoritedContract.FavoriteEntry;
import com.nano.android.popularmovies.data.FavoritedContract.ReviewEntry;
import com.nano.android.popularmovies.data.FavoritedContract.TrailerEntry;
/**
 * Test the basic functionality in provider
 */
public class TestProvider extends AndroidTestCase{
    private static final String LOG_TAG = TestProvider.class.getSimpleName();

    /*
        Helper method: Delete all records from database, reset the state of data base
     */
    public  void deleteAllRecordsFromDB() {
        FavoritedDbHelper dbHelper = new FavoritedDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.delete(FavoriteEntry.TABLE_NAME, null, null);
        db.delete(TrailerEntry.TABLE_NAME,null,null);
        db.delete(ReviewEntry.TABLE_NAME,null,null);
        db.close();
    }

    public void deleteAllRecords() {deleteAllRecordsFromDB();}

    /*
        Make sure each test start with a clean slate.
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecords();
    }
    /*
        Verify that the content provider return the correct type of URI that it can handle.
        It does not touch the database.
     */
    public void testGetType() {
        final long _ID = 10L;
        final long MOVIE_ID = 56241;

        // content://authority/favorite/
        String type = mContext.getContentResolver().getType(FavoriteEntry.CONTENT_URI);
        assertEquals("Error: Should return FavoriteEntry.CONTENT_DIR_TYPE",
                FavoriteEntry.CONTENT_DIR_TYPE, type);
        // content://authority/favorite/10L
        type = mContext.getContentResolver().getType(FavoriteEntry.buildFavoriteUri(_ID));
        assertEquals("Error: Should return FavoriteEntry.CONTENT_ITEM_TYPE",
                FavoriteEntry.CONTENT_ITEM_TYPE, type);

        // content://authority/trailer
        type = mContext.getContentResolver().getType(TrailerEntry.CONTENT_URI);
        assertEquals("Error: Should return TrailerEntry.CONTENT_DIR_TYPE",
                TrailerEntry.CONTENT_DIR_TYPE, type);
        // content://authority/trailer/movie_id/56241
        type = mContext.getContentResolver().getType(TrailerEntry.buildTrailerWithMovieId(MOVIE_ID));
        assertEquals("Error: Should return TrailerEntry.CONTENT_DIR_TYPE",
                TrailerEntry.CONTENT_DIR_TYPE, type);
         content://authority/trailer/10L
        type = mContext.getContentResolver().getType(TrailerEntry.buildTrailerUri(_ID));
        assertEquals("Error: Should return TrailerEntry.CONTENT_ITEM_TYPE",
                TrailerEntry.CONTENT_ITEM_TYPE, type);

        // content://authority/review
        type = mContext.getContentResolver().getType(ReviewEntry.CONTENT_URI);
        assertEquals("Error: Should return ReviewEntry.CONTENT_DIR_TYPE",
                ReviewEntry.CONTENT_DIR_TYPE, type);
        // content://authority/review/movie_id/56241
        type = mContext.getContentResolver().getType(ReviewEntry.buildReviewWithMovieId(MOVIE_ID));
        assertEquals("Error: Should return ReviewEntry.CONTENT_DIR_TYPE",
                ReviewEntry.CONTENT_DIR_TYPE, type);
        // content://authority/review/10L
        type = mContext.getContentResolver().getType(ReviewEntry.buildReviewUri(_ID));
        assertEquals("Error: Should return ReviewEntry.CONTENT_ITEM_TYPE",
                ReviewEntry.CONTENT_ITEM_TYPE, type);
    }
}
