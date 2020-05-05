package nl.freelist.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import java.util.ArrayList;
import java.util.List;
import nl.freelist.domain.valueObjects.DtrConstraint;
import nl.freelist.freelist.R;

public class ConstraintPickerDialog
    extends DialogFragment { // Todo: DialogFragment is deprecated since API28?

  public static final String TAG = "ConstraintPickerDialog";
  private String inputType = "constraint";

  private List<DtrConstraint> dtrConstraints = new ArrayList<>();
  private List<DtrConstraint> dayConstraints = new ArrayList<>();
  private List<DtrConstraint> timeOfDayConstraints = new ArrayList<>();

  private CheckBox checkMondays;
  private CheckBox checkTuesdays;
  private CheckBox checkWednesdays;
  private CheckBox checkThursdays;
  private CheckBox checkFridays;
  private CheckBox checkSaturdays;
  private CheckBox checkSundays;
  private CheckBox checkMornings;
  private CheckBox checkAfternoons;
  private CheckBox checkEvenings;
  private CheckBox checkNights;

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
      throw new ClassCastException(
          getActivity().toString() + " must implement NoticeDialogListener");
    }
  }

  @Override
  public void onStart() {
    super.onStart();
    checkMondays = getDialog().findViewById(R.id.checkMondays);
    checkMondays.setOnClickListener(v -> {
      if (((CheckBox) v).isChecked()) {
      listener.onDialogPositiveClick("NOMONDAYS", "preferredDaysConstraintsRemoved");
      } else {
        listener.onDialogPositiveClick("NOMONDAYS", "preferredDaysConstraintsAdded");
      }
    });

    checkTuesdays = getDialog().findViewById(R.id.checkTuesdays);
    checkTuesdays.setOnClickListener(v -> {
      if (((CheckBox) v).isChecked()) {
        listener.onDialogPositiveClick("NOTUESDAYS", "preferredDaysConstraintsRemoved");
      } else {
        listener.onDialogPositiveClick("NOTUESDAYS", "preferredDaysConstraintsAdded");
      }
    });

    checkWednesdays = getDialog().findViewById(R.id.checkWednesdays);
    checkWednesdays.setOnClickListener(v -> {
      if (((CheckBox) v).isChecked()) {
        listener.onDialogPositiveClick("NOWEDNESDAYS", "preferredDaysConstraintsRemoved");
      } else {
        listener.onDialogPositiveClick("NOWEDNESDAYS", "preferredDaysConstraintsAdded");
      }
    });

    checkThursdays = getDialog().findViewById(R.id.checkThursdays);
    checkThursdays.setOnClickListener(v -> {
      if (((CheckBox) v).isChecked()) {
        listener.onDialogPositiveClick("NOTHURSDAYS", "preferredDaysConstraintsRemoved");
      } else {
        listener.onDialogPositiveClick("NOTHURSDAYS", "preferredDaysConstraintsAdded");
      }
    });

    checkFridays = getDialog().findViewById(R.id.checkFridays);
    checkFridays.setOnClickListener(v -> {
      if (((CheckBox) v).isChecked()) {
        listener.onDialogPositiveClick("NOFRIDAYS", "preferredDaysConstraintsRemoved");
      } else {
        listener.onDialogPositiveClick("NOFRIDAYS", "preferredDaysConstraintsAdded");
      }
    });

    checkSaturdays = getDialog().findViewById(R.id.checkSaturdays);
    checkSaturdays.setOnClickListener(v -> {
      if (((CheckBox) v).isChecked()) {
        listener.onDialogPositiveClick("NOSATURDAYS", "preferredDaysConstraintsRemoved");
      } else {
        listener.onDialogPositiveClick("NOSATURDAYS", "preferredDaysConstraintsAdded");
      }
    });

    checkSundays = getDialog().findViewById(R.id.checkSundays);
    checkSundays.setOnClickListener(v -> {
      if (((CheckBox) v).isChecked()) {
        listener.onDialogPositiveClick("NOSUNDAYS", "preferredDaysConstraintsRemoved");
      } else {
        listener.onDialogPositiveClick("NOSUNDAYS", "preferredDaysConstraintsAdded");
      }
    });

    checkMornings = getDialog().findViewById(R.id.checkMornings);
    checkMornings.setOnClickListener(v -> {
      if (((CheckBox) v).isChecked()) {
        listener.onDialogPositiveClick("NOMORNINGS", "preferredDaysConstraintsRemoved");
      } else {
        listener.onDialogPositiveClick("NOMORNINGS", "preferredDaysConstraintsAdded");
      }
    });

    checkAfternoons = getDialog().findViewById(R.id.checkAfternoons);
    checkAfternoons.setOnClickListener(v -> {
      if (((CheckBox) v).isChecked()) {
        listener.onDialogPositiveClick("NOAFTERNOONS", "preferredDaysConstraintsRemoved");
      } else {
        listener.onDialogPositiveClick("NOAFTERNOONS", "preferredDaysConstraintsAdded");
      }
    });

    checkEvenings = getDialog().findViewById(R.id.checkEvenings);
    checkEvenings.setOnClickListener(v -> {
      if (((CheckBox) v).isChecked()) {
        listener.onDialogPositiveClick("NOEVENINGS", "preferredDaysConstraintsRemoved");
      } else {
        listener.onDialogPositiveClick("NOEVENINGS", "preferredDaysConstraintsAdded");
      }
    });

    checkNights = getDialog().findViewById(R.id.checkNights);
    checkNights.setOnClickListener(v -> {
      if (((CheckBox) v).isChecked()) {
        listener.onDialogPositiveClick("NONIGHTS", "preferredDaysConstraintsRemoved");
      } else {
        listener.onDialogPositiveClick("NONIGHTS", "preferredDaysConstraintsAdded");
      }
    });
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    // According to https://developer.android.com/reference/android/app/DialogFragment
    // This is the place to do final initialization once these pieces are in place,
    // such as retrieving views or restoring state.
    // Todo: when retrieving view here I get null so I put it in onStart.
    //  Seems to work for now but probably not good.
  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    LayoutInflater inflater = requireActivity().getLayoutInflater();
    builder
        .setView(inflater.inflate(R.layout.picker_constraints, null))
        .setMessage("Schedule preference for");
    return builder.create();
  }
}
