package nl.freelist.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import androidx.fragment.app.DialogFragment;
import java.util.ArrayList;
import java.util.List;
import nl.freelist.domain.valueObjects.DtrConstraint;
import nl.freelist.freelist.R;

public class ConstraintPickerDialog extends DialogFragment {

  public static final String TAG = "ConstraintPickerDialog";
  private String inputType = "constraint";

  // Use this instance of the interface to deliver action events
  NoticeDialogListener listener;

  // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    // Verify that the host activity implements the callback interface
    try {
      // Instantiate the NoticeDialogListener so we can send events to the host
      listener = (NoticeDialogListener) context;
    } catch (ClassCastException e) {
      // The activity doesn't implement the interface, throw exception
      throw new ClassCastException(getActivity().toString()
          + " must implement NoticeDialogListener");
    }
  }


  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    LayoutInflater inflater = requireActivity().getLayoutInflater();
    builder.setView(inflater.inflate(R.layout.picker_constraints, null))
        .setMessage("Schedule preference for")
        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int id) {
            List<DtrConstraint> dtrConstraints = new ArrayList<>();
            List<DtrConstraint> dayConstraints = new ArrayList<>();
            List<DtrConstraint> timeOfDayConstraints = new ArrayList<>();
            CheckBox checkMondays = ((AlertDialog) dialog)
                .findViewById(R.id.checkMondays); //Todo: simplify ugly mess below

            dayConstraints.add(DtrConstraint.Create("OPENBRACKET", null));

            if (!checkMondays.isChecked()) {
              dayConstraints.add(DtrConstraint.Create("NOMONDAYS", null));
            }
            CheckBox checkTuesdays = ((AlertDialog) dialog).findViewById(R.id.checkTuesdays);
            if (!checkTuesdays.isChecked()) {
              if (dayConstraints.size() > 1) {
                dayConstraints.add(DtrConstraint.Create("AND", null));
              }
              dayConstraints.add(DtrConstraint.Create("NOTUESDAYS", null));
            }
            CheckBox checkWednesdays = ((AlertDialog) dialog).findViewById(R.id.checkWednesdays);
            if (!checkWednesdays.isChecked()) {
              if (dayConstraints.size() > 1) {
                dayConstraints.add(DtrConstraint.Create("AND", null));
              }
              dayConstraints.add(DtrConstraint.Create("NOWEDNESDAYS", null));
            }
            CheckBox checkThursdays = ((AlertDialog) dialog).findViewById(R.id.checkThursdays);
            if (!checkThursdays.isChecked()) {
              if (dayConstraints.size() > 1) {
                dayConstraints.add(DtrConstraint.Create("AND", null));
              }
              dayConstraints.add(DtrConstraint.Create("NOTHURSDAYS", null));
            }
            CheckBox checkFridays = ((AlertDialog) dialog).findViewById(R.id.checkFridays);
            if (!checkFridays.isChecked()) {
              if (dayConstraints.size() > 1) {
                dayConstraints.add(DtrConstraint.Create("AND", null));
              }
              dayConstraints.add(DtrConstraint.Create("NOFRIDAYS", null));
            }
            CheckBox checkSaturdays = ((AlertDialog) dialog).findViewById(R.id.checkSaturdays);
            if (!checkSaturdays.isChecked()) {
              if (dayConstraints.size() > 1) {
                dayConstraints.add(DtrConstraint.Create("AND", null));
              }
              dayConstraints.add(DtrConstraint.Create("NOSATURDAYS", null));
            }
            CheckBox checkSundays = ((AlertDialog) dialog).findViewById(R.id.checkSundays);
            if (!checkSundays.isChecked()) {
              if (dayConstraints.size() > 1) {
                dayConstraints.add(DtrConstraint.Create("AND", null));
              }
              dayConstraints.add(DtrConstraint.Create("NOSUNDAYS", null));
            }

            if (dayConstraints.size() == 1) { //Only OPENBRACKET present
              dayConstraints.clear();
            } else {
              dayConstraints.add(DtrConstraint.Create("CLOSEBRACKET", null));
            }

            timeOfDayConstraints.add(DtrConstraint.Create("OPENBRACKET", null));

            CheckBox checkMornings = ((AlertDialog) dialog).findViewById(R.id.checkMornings);
            if (!checkMornings.isChecked()) {
              timeOfDayConstraints.add(DtrConstraint.Create("NOMORNINGS", null));
            }
            CheckBox checkAfternoons = ((AlertDialog) dialog).findViewById(R.id.checkAfternoons);
            if (!checkAfternoons.isChecked()) {
              if (timeOfDayConstraints.size() > 1) {
                timeOfDayConstraints.add(DtrConstraint.Create("AND", null));
              }
              timeOfDayConstraints.add(DtrConstraint.Create("NOAFTERNOONS", null));
            }
            CheckBox checkEvenings = ((AlertDialog) dialog).findViewById(R.id.checkEvenings);
            if (!checkEvenings.isChecked()) {
              if (timeOfDayConstraints.size() > 1) {
                timeOfDayConstraints.add(DtrConstraint.Create("AND", null));
              }
              timeOfDayConstraints.add(DtrConstraint.Create("NOEVENINGS", null));
            }
            CheckBox checkAtNight = ((AlertDialog) dialog).findViewById(R.id.checkAtNight);
            if (!checkAtNight.isChecked()) {
              if (timeOfDayConstraints.size() > 1) {
                timeOfDayConstraints.add(DtrConstraint.Create("AND", null));
              }
              timeOfDayConstraints.add(DtrConstraint.Create("NONIGHTS", null));
            }

            if (timeOfDayConstraints.size() == 1) { //Only OPENBRACKET present
              timeOfDayConstraints.clear();
            } else {
              timeOfDayConstraints.add(DtrConstraint.Create("CLOSEBRACKET", null));
            }

            dtrConstraints.addAll(dayConstraints);
            if (dayConstraints.size() > 0 && timeOfDayConstraints.size() > 0) {
              dtrConstraints.add(DtrConstraint.Create("AND", null));
            }
            dtrConstraints.addAll(timeOfDayConstraints);
            dtrConstraints = DtrConstraint.Simplify(dtrConstraints);
            listener.onConstraintsAdded(dtrConstraints);
          }
        })
        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int id) {
            // User cancelled the dialog
            listener.onDialogPositiveClick("input cancel", inputType);
          }
        });
    return builder.create();
  }

}
