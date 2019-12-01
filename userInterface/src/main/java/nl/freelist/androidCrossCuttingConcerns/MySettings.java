package nl.freelist.androidCrossCuttingConcerns;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import nl.freelist.domain.crossCuttingConcerns.Constants;

public class MySettings {

  private final SharedPreferences sharedPreferences;

  public MySettings(Context context) {
    sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
  }

  public String getUuid() {
    return sharedPreferences.getString(Constants.SETTINGS_USER_UUID, null);
  }

  public String getResourceUuid() {
    return sharedPreferences.getString(Constants.SETTINGS_RESOURCE_UUID, null);
  }
}
