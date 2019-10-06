package priv.rtbishop.coopstat.ui

import android.os.Bundle

import androidx.preference.PreferenceFragmentCompat
import priv.rtbishop.coopstat.R

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preference, rootKey)
    }
}