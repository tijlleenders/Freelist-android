package nl.freelist.dialogs;

import android.os.Bundle;

public interface NoticeDialogListener {

  public void onDialogPositiveClick(String input, String inputType);
  public void onPreferredDaysChange(Bundle checkBoxStates);

}
