package nl.freelist.androidCrossCuttingConcerns;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import java.util.UUID;
import nl.freelist.domain.crossCuttingConcerns.Constants;
import nl.freelist.domain.valueObjects.Email;

public class MySettings {

  private final SharedPreferences sharedPreferences;
  private Editor editor;

  public MySettings(Context context) {
    sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    if (!sharedPreferences.contains(Constants.SETTINGS_UUID)) {
      editor = sharedPreferences.edit();
      Email email = new Email("tijl.leenders@gmail.com");
      editor.putString(
          Constants.SETTINGS_UUID, UUID.nameUUIDFromBytes(email.getEmail().getBytes()).toString());
      editor.commit();
    }
    if (!sharedPreferences.contains(Constants.SETTINGS_RESOURCE_UUID)) {
      editor = sharedPreferences.edit();
      Email email = new Email("tijl.leenders@gmail.com");
      editor.putString(
          Constants.SETTINGS_RESOURCE_UUID,
          UUID.nameUUIDFromBytes(email.getEmail().getBytes()).toString());
      editor.commit();
    }
  }

  public String getUuid() {
    return sharedPreferences.getString(Constants.SETTINGS_UUID,
        UUID.nameUUIDFromBytes("unknown@freelist.nl".getBytes()).toString());
  }

  public String getResourceUuid() {
    return sharedPreferences.getString(Constants.SETTINGS_RESOURCE_UUID,
        UUID.nameUUIDFromBytes("unknown@freelist.nl".getBytes()).toString());
  }

}
