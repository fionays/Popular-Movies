package com.nano.android.popularmovies.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.test.AndroidTestCase;

import com.nano.android.popularmovies.Data.FavoritedContract;

import java.util.Map;
import java.util.Set;

/**
 * Created by YANG on 11/4/2015.
 */
public class TestUtilities extends AndroidTestCase {

    /*
       Create some default movie detail value for database test.
     */
    static ContentValues createMadMaxDetailValues() {
        ContentValues testValues = new ContentValues();
        String overview = "An apocalyptic story set in the furthest reaches of our planet, in a " +
                "stark desert landscape where humanity is broken, and most everyone is crazed " +
                "fighting for the necessities of life. Within this world exist two rebels on the " +
                "run who just might be able to restore order. There's Max, a man of action and a " +
                "man of few words, who seeks peace of mind following the loss of his wife and child" +
                " in the aftermath of the chaos. And Furiosa, a woman of action and a woman who " +
                "believes her path to survival may be achieved if she can make it across the " +
                "desert back to her childhood homeland.";

        String poster = "http://image.tmdb.org/t/p/w185//kqjL17yufvn9OVLyXYpvtyrFfak.jpg";

        testValues.put(FavoritedContract.FavoriteEntry.COLUMN_MOVIE_ID, 76341);
        testValues.put(FavoritedContract.FavoriteEntry.COLUMN_POSTER, poster);
        testValues.put(FavoritedContract.FavoriteEntry.COLUMN_TITLE, "Mad Max: Fury Road");
        testValues.put(FavoritedContract.FavoriteEntry.COLUMN_RELEASE_DATE, 2015-05-15);
        testValues.put(FavoritedContract.FavoriteEntry.COLUMN_VOTE, 7);
        testValues.put(FavoritedContract.FavoriteEntry.COLUMN_OVERVIEW, overview);

        return testValues;
    }

    /*
       Create some default movie trailer values for database test.
    */
    static ContentValues createTrailerValues() {
        ContentValues testValues = new ContentValues();
        testValues.put(FavoritedContract.TrailerEntry.COLUMN_MOVIE_ID, 76341);
        testValues.put(FavoritedContract.TrailerEntry.COLUMN_TRAILER_NAME, "Trailers From Hell");
        testValues.put(FavoritedContract.TrailerEntry.COLUMN_TRAILER_KEY, "FRDdRto_3SA");

        return testValues;
    }

    /*
       Create some default review values for database test
     */
    static ContentValues createReviewValues() {
        ContentValues testValues = new ContentValues();
        String content = "Fabulous action movie. Lots of interesting characters. They don't make" +
                " many movies like this. The whole movie from start to finish was entertaining " +
                "I'm looking forward to seeing it again. I definitely recommend seeing it.";
        testValues.put(FavoritedContract.ReviewEntry.COLUMN_MOVIE_ID, 76341);
        testValues.put(FavoritedContract.ReviewEntry.COLUMN_AUTHOR, "Phileas Fogg");
        testValues.put(FavoritedContract.ReviewEntry.COLUMN_CONTENT, content);

        return testValues;
    }

    /*
       Validate data
     */
    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues originalValues) {
        Set<Map.Entry<String, Object>> valueSet = originalValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int id = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, id == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(id));
        }
    }

}
