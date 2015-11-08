package com.nano.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.List;

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

    /**
     * Start Youtube to play trailer using Intent.
     */
    public static void playTrailerIntent(String key, Context context) {

        final String VALUE = "v";
        final String BASE_YOUTUBE_URI = "http://www.youtube.com/watch?";
        Uri builtUri = Uri.parse(BASE_YOUTUBE_URI).buildUpon()
                .appendQueryParameter(VALUE, key)
                .build();

        // Build the intent
        Intent playIntent = new Intent(Intent.ACTION_VIEW, builtUri);

        // Verify it resolves
        PackageManager packageManager = context.getPackageManager();
        // TODO: Why the example in documentation set flag = 0?
        List<ResolveInfo> activities = packageManager.queryIntentActivities(playIntent,PackageManager.MATCH_DEFAULT_ONLY);
        boolean isIntentSafe = activities.size() > 0;

        // Start an activity if it is safe
        if (isIntentSafe) {
            context.startActivity(playIntent);
        }
    }
}


