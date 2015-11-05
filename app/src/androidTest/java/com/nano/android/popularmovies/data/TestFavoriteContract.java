package com.nano.android.popularmovies.data;

/**
 * Created by YANG on 11/4/2015.
 */

import android.net.Uri;
import android.test.AndroidTestCase;

import com.nano.android.popularmovies.Data.FavoritedContract;

/**
 * Test 2 URI building functions in contract
 */
public class TestFavoriteContract extends AndroidTestCase{

    private static long TEST_MOVIE_ID = 76541;

    public void testBuildTrailerWithMovieId() {
        Uri datasetUri = FavoritedContract.TrailerEntry.buildTrailerWithMovieId(TEST_MOVIE_ID);
        assertNotNull("Error: Null Uri returned. Should fill in movie_id", datasetUri);
        assertEquals("Error: path appending is wrong.", Long.toString(TEST_MOVIE_ID), datasetUri.getLastPathSegment());
    }

    public void testBuildReviewWithMovieId() {
        Uri datasetUri = FavoritedContract.ReviewEntry.buildReviewWithMovieId(TEST_MOVIE_ID);
        assertNotNull("Error: Null Uri returned. Should fill in movie_id", datasetUri);
        assertEquals("Error: path appending is wrong.", Long.toString(TEST_MOVIE_ID), datasetUri.getLastPathSegment());
    }
}
