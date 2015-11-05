package com.nano.android.popularmovies.data;

import android.content.UriMatcher;
import android.net.Uri;
import android.test.AndroidTestCase;

/**
 * Created by YANG on 11/4/2015.
 */
public class TestUriMatcher extends AndroidTestCase {
    private static final long MOVIE_ID_QUERY = 74651;
    private static final long _ID = 10L;

    // content://com.nano.android.popularmovies/favorite
    private static final Uri TEST_FAV_DIR = FavoritedContract.FavoriteEntry.CONTENT_URI;
    private static final Uri TEST_FAV_ITEM = FavoritedContract.FavoriteEntry.buildFavoriteUri(_ID);

    // content://com.nano.android.popularmovies/trailer
    private static final Uri TEST_TR_DIR = FavoritedContract.TrailerEntry.CONTENT_URI;
    private static final Uri TEST_TR_MOVIE_ID_DIR = FavoritedContract.TrailerEntry
            .buildTrailerWithMovieId(MOVIE_ID_QUERY);
    private static final Uri TEST_TR_ITEM = FavoritedContract.TrailerEntry.buildTrailerUri(_ID);

    // content://com.nano.android.popularmovies/review
    private static final Uri TEST_RE_DIR = FavoritedContract.ReviewEntry.CONTENT_URI;
    private static final Uri TEST_RE_MOVIE_ID_DIR = FavoritedContract.ReviewEntry
            .buildReviewWithMovieId(MOVIE_ID_QUERY);
    private static final Uri TEST_RE_ITEM = FavoritedContract.ReviewEntry.buildReviewUri(_ID);

    /*
        Check if the UriMatcher returns the correct integers.
     */
    public void testUriMatcher() {
        UriMatcher testMatcher = FavoritedProvider.buildUriMatcher();

        assertEquals("Error: The FAVOTITE URI was matched incorrectly.",
                testMatcher.match(TEST_FAV_DIR), FavoritedProvider.FAVORITE);
        assertEquals("Error: The FAVOTITE_ID URI was matched incorrectly.",
                testMatcher.match(TEST_FAV_ITEM), FavoritedProvider.FAVORITE_ID);

        assertEquals("Error: The TRAILER URI was matched incorrectly.",
                testMatcher.match(TEST_TR_DIR), FavoritedProvider.TRAILER);
        assertEquals("Error: The TRAILER_MOVIE_ID URI was matched incorrectly.",
                testMatcher.match(TEST_TR_MOVIE_ID_DIR), FavoritedProvider.TRAILER_MOVIE_ID);
        assertEquals("Error: The TRAILER URI was matched incorrectly.",
                testMatcher.match(TEST_TR_ITEM), FavoritedProvider.TRAILER_ID);

        assertEquals("Error: The REVIEW URI was matched incorrectly.",
                testMatcher.match(TEST_RE_DIR), FavoritedProvider.REVIEW);
        assertEquals("Error: The REVIEW_MOVIE_ID URI was matched incorrectly.",
                testMatcher.match(TEST_RE_MOVIE_ID_DIR), FavoritedProvider.REVIEW_MOVIE_ID);
        assertEquals("Error: The REVIEW URI was matched incorrectly.",
                testMatcher.match(TEST_RE_ITEM), FavoritedProvider.REVIEW_ID);
    }


}

