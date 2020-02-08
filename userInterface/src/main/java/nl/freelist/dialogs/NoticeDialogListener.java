package nl.freelist.dialogs;

import java.util.List;
import nl.freelist.domain.valueObjects.DtrConstraint;

public interface NoticeDialogListener {

  public void onDialogPositiveClick(String input, String inputType);

  public void onConstraintsAdded(List<DtrConstraint> dtrConstraints);
}
