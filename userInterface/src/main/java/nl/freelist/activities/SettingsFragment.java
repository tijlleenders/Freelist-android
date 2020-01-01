package nl.freelist.activities;

import android.os.Bundle;
import androidx.preference.PreferenceFragmentCompat;
import nl.freelist.freelist.R;

public class SettingsFragment extends PreferenceFragmentCompat {

  @Override
  public void onCreatePreferences(Bundle bundle, String s) {
    // Load the Preferences from the XML file
    addPreferencesFromResource(R.xml.preferences);
  }
}
