package com.nano.android.popularmovies.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import com.nano.android.popularmovies.Data.FavoritedContract;
import com.nano.android.popularmovies.Data.FavoritedDbHelper;

import java.util.HashSet;

/**
 * Created by YANG on 11/4/2015.
 */
public class TestDB extends AndroidTestCase {

    public static final String LOG_TAG = TestDB.class.getSimpleName();

    // Make sure each test starts with clean slate
    void deleteDatabase() { mContext.deleteDatabase(FavoritedDbHelper.DATABASE_NAME);}

    /*
        It is called before each test is executed.
     */
    public void setUp() { deleteDatabase(); }

    /*
        Test that three tables in database has been created correctly with correct columns.
     */
    public void testCreateDb() throws Throwable {
        // Create HashSet of all the table names.
        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(FavoritedContract.FavoriteEntry.TABLE_NAME);
        tableNameHashSet.add(FavoritedContract.ReviewEntry.TABLE_NAME);
        tableNameHashSet.add(FavoritedContract.ReviewEntry.TABLE_NAME);

        mContext.deleteDatabase(FavoritedDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new FavoritedDbHelper(this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        // List all tables names contained in the SQLite database
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        // If the cursor is empty then you are wrong
        assertTrue("Error: This means the database has not been created correclty", c.moveToFirst());

        // verify that the tables have been created
        do {
            tableNameHashSet.remove(c.getString(0));
        } while( c.moveToNext() );

        // if this fails, it means that your database doesn't contain favorite table,
        // trailer table or review table.
        assertTrue("Error: Your database was created without both the location entry and weather entry tables",
                tableNameHashSet.isEmpty());

        // If favarite table contain the correct columns********************************************
        c = db.rawQuery("PRAGMA table_info(" + FavoritedContract.FavoriteEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> favColumnHashSet = new HashSet<String>();
        favColumnHashSet.add(FavoritedContract.FavoriteEntry._ID);
        favColumnHashSet.add(FavoritedContract.FavoriteEntry.COLUMN_MOVIE_ID);
        favColumnHashSet.add(FavoritedContract.FavoriteEntry.COLUMN_POSTER);
        favColumnHashSet.add(FavoritedContract.FavoriteEntry.COLUMN_TITLE);
        favColumnHashSet.add(FavoritedContract.FavoriteEntry.COLUMN_RELEASE_DATE);
        favColumnHashSet.add(FavoritedContract.FavoriteEntry.COLUMN_VOTE);
        favColumnHashSet.add(FavoritedContract.FavoriteEntry.COLUMN_OVERVIEW);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            favColumnHashSet.remove(columnName);
        } while(c.moveToNext());

        assertTrue("Error: The database doesn't contain all of the required favorite entry columns",
                favColumnHashSet.isEmpty());

        // If trailer table contain the correct columns********************************************
        c = db.rawQuery("PRAGMA table_info(" + FavoritedContract.TrailerEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> trailerColumnHashSet = new HashSet<String>();
        trailerColumnHashSet.add(FavoritedContract.TrailerEntry._ID);
        trailerColumnHashSet.add(FavoritedContract.TrailerEntry.COLUMN_MOVIE_ID);
        trailerColumnHashSet.add(FavoritedContract.TrailerEntry.COLUMN_TRAILER_NAME);
        trailerColumnHashSet.add(FavoritedContract.TrailerEntry.COLUMN_TRAILER_KEY);

        columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            favColumnHashSet.remove(columnName);
        } while(c.moveToNext());

        assertTrue("Error: The database doesn't contain all of the required TRAILER entry columns",
                favColumnHashSet.isEmpty());

        // If review table contain the correct columns********************************************
        c = db.rawQuery("PRAGMA table_info(" + FavoritedContract.FavoriteEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> revColumnHashSet = new HashSet<String>();
        revColumnHashSet.add(FavoritedContract.ReviewEntry._ID);
        revColumnHashSet.add(FavoritedContract.ReviewEntry.COLUMN_MOVIE_ID);
        revColumnHashSet.add(FavoritedContract.ReviewEntry.COLUMN_AUTHOR);
        revColumnHashSet.add(FavoritedContract.ReviewEntry.COLUMN_CONTENT);

        columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            favColumnHashSet.remove(columnName);
        } while(c.moveToNext());

        assertTrue("Error: The database doesn't contain all of the required review entry columns",
                favColumnHashSet.isEmpty());
    }
}
