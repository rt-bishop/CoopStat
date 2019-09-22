package priv.rtbishop.coopstat.ui;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import priv.rtbishop.coopstat.R;

public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preference, rootKey);
    }
}