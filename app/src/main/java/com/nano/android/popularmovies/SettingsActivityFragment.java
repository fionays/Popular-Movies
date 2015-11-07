package com.nano.android.popularmovies;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

/**
 * A placeholder fragment containing preference.
 */
public class SettingsActivityFragment extends PreferenceFragment
                                        implements Preference.OnPreferenceChangeListener{

    public SettingsActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        // Attach an OnPreferenceChangListener to update UI summary when preference changes.
        // Find preference according to the key of pref
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_sort_key)));
    }

    private void bindPreferenceSummaryToValue(Preference preference) {
        // Set listener for value changes
        preference.setOnPreferenceChangeListener(this);
        // Sets the default values from an XML preference file by reading the values defined by
        // each Preference item's android:defaultValue attribute.
        // Fasle means it will not re-read the default values if the method has been called
        // in the past.
        PreferenceManager.setDefaultValues(preference.getContext(), R.xml.preferences, false);
    }

    @Override
    public boolean onPreferenceChange(Preference pref, Object value) {
        String stringValue = value.toString();

        if (pref instanceof ListPreference) {
            // For list preferences, look up the correct display value in
            // the preference's 'entries' list (since they have separate labels/values).
            ListPreference listPreference = (ListPreference) pref;
            int prefIndex = listPreference.findIndexOfValue(stringValue);
            if (prefIndex >= 0) {
                pref.setSummary(listPreference.getEntries()[prefIndex]);
            }
        } else {
            // For other preferences, set the summary to the value's simple string representation.
            pref.setSummary(stringValue);
        }
        return true;
    }
}
