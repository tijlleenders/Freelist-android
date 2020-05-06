package nl.freelist.dialogs;

import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CheckBox;
import java.util.List;
import nl.freelist.domain.valueObjects.DtrConstraint;

public interface NoticeDialogListener {

  public void onDialogPositiveClick(String input, String inputType);
  public void onPreferredDaysChange(Bundle checkBoxStates);

}
