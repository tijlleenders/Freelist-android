package nl.freelist.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import nl.freelist.freelist.R;

public class SettingsActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_settings);
    getSupportFragmentManager()
        .beginTransaction()
        .replace(R.id.settingsContainer, new SettingsFragment())
        .commit();
  }

}
