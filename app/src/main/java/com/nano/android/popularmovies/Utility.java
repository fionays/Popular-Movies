package com.nano.android.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by YANG on 11/7/2015.
 */
public class Utility {

    private static final String LOG_TAG = Utility.class.getSimpleName();

    public static String getPreferredSort(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String sortPref = sharedPref.getString(context.getString(R.string.pref_sort_key),
                context.getString(R.string.pref_sort_default));
        Log.v(LOG_TAG, "sort prefs: " + sortPref);

        return sortPref;
    }

    public static boolean isSortFavorite(Context context) {
        String sortPref = getPreferredSort(context);
        return sortPref.equals(context.getString(R.string.pref_sort_favorites));
    }
}


