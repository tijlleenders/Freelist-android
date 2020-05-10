package nl.freelist.androidCrossCuttingConcerns;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import java.util.UUID;
import nl.freelist.domain.crossCuttingConcerns.Constants;

public class MySettings {

  private final SharedPreferences sharedPreferences;

  public MySettings(Context context) {
    sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
  }

  public String getId() {
    return sharedPreferences.getString(Constants.SETTINGS_USER_UUID, UUID
        .nameUUIDFromBytes("anonymous@freelist.nl".getBytes()).toString());
  }

  public String getPersonUuid() {
    return sharedPreferences.getString(Constants.SETTINGS_PERSON_UUID, UUID
        .nameUUIDFromBytes("anonymous@freelist.nl".getBytes()).toString());
  }
}
