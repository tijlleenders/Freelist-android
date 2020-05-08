package nl.freelist.dialogs;

import android.os.Bundle;

public interface NoticeDialogListener {

  public void onDialogFeedback(String input, String inputType);
  public void onPreferredDaysChange(Bundle checkBoxStates);

}
