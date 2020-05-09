package nl.freelist.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import nl.freelist.freelist.R;

public class ConstraintPickerDialog
    extends DialogFragment { // Todo: DialogFragment is deprecated since API28?

  public static final String TAG = "ConstraintPickerDialog";

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

  public static ConstraintPickerDialog Create(Bundle argsBundle) {
    ConstraintPickerDialog frag = new ConstraintPickerDialog();
    frag.setArguments(argsBundle);
    return frag;
  }

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
  public void onStart() { //  Tried putting below in onCreateView but didn't work
    super.onStart();
    checkMondays = getDialog().findViewById(R.id.checkMondays);
    checkTuesdays = getDialog().findViewById(R.id.checkTuesdays);
    checkWednesdays = getDialog().findViewById(R.id.checkWednesdays);
    checkThursdays = getDialog().findViewById(R.id.checkThursdays);
    checkFridays = getDialog().findViewById(R.id.checkFridays);
    checkSaturdays = getDialog().findViewById(R.id.checkSaturdays);
    checkSundays = getDialog().findViewById(R.id.checkSundays);
    checkMornings = getDialog().findViewById(R.id.checkMornings);
    checkAfternoons = getDialog().findViewById(R.id.checkAfternoons);
    checkEvenings = getDialog().findViewById(R.id.checkEvenings);
    checkNights = getDialog().findViewById(R.id.checkNights);

    checkMondays.setChecked(getArguments().getBoolean("allowMondays"));
    checkTuesdays.setChecked(getArguments().getBoolean("allowTuesdays"));
    checkWednesdays.setChecked(getArguments().getBoolean("allowWednesdays"));
    checkThursdays.setChecked(getArguments().getBoolean("allowThursdays"));
    checkFridays.setChecked(getArguments().getBoolean("allowFridays"));
    checkSaturdays.setChecked(getArguments().getBoolean("allowSaturdays"));
    checkSundays.setChecked(getArguments().getBoolean("allowSundays"));
    checkMornings.setChecked(getArguments().getBoolean("allowMornings"));
    checkAfternoons.setChecked(getArguments().getBoolean("allowAfternoons"));
    checkEvenings.setChecked(getArguments().getBoolean("allowEvenings"));
    checkNights.setChecked(getArguments().getBoolean("allowNights"));

    checkMondays.setOnCheckedChangeListener(
        (buttonView, isChecked) -> {
          getArguments().putBoolean("allowMondays", isChecked);
          listener.onPreferredDaysChange(getArguments());
        }
    );
    checkTuesdays.setOnCheckedChangeListener(
        (buttonView, isChecked) -> {
          getArguments().putBoolean("allowTuesdays", isChecked);
          listener.onPreferredDaysChange(getArguments());
        }
    );
    checkWednesdays.setOnCheckedChangeListener(
        (buttonView, isChecked) -> {
          getArguments().putBoolean("allowWednesdays", isChecked);
          listener.onPreferredDaysChange(getArguments());
        }
    );
    checkThursdays.setOnCheckedChangeListener(
        (buttonView, isChecked) -> {
          getArguments().putBoolean("allowThursdays", isChecked);
          listener.onPreferredDaysChange(getArguments());
        }
    );
    checkFridays.setOnCheckedChangeListener(
        (buttonView, isChecked) -> {
          getArguments().putBoolean("allowFridays", isChecked);
          listener.onPreferredDaysChange(getArguments());
        }
    );
    checkSaturdays.setOnCheckedChangeListener(
        (buttonView, isChecked) -> {
          getArguments().putBoolean("allowSaturdays", isChecked);
          listener.onPreferredDaysChange(getArguments());
        }
    );
    checkSundays.setOnCheckedChangeListener(
        (buttonView, isChecked) -> {
          getArguments().putBoolean("allowSundays", isChecked);
          listener.onPreferredDaysChange(getArguments());
        }
    );
    checkMornings.setOnCheckedChangeListener(
        (buttonView, isChecked) -> {
          getArguments().putBoolean("allowMornings", isChecked);
          listener.onPreferredDaysChange(getArguments());
        }
    );
    checkAfternoons.setOnCheckedChangeListener(
        (buttonView, isChecked) -> {
          getArguments().putBoolean("allowAfternoons", isChecked);
          listener.onPreferredDaysChange(getArguments());
        }
    );
    checkEvenings.setOnCheckedChangeListener(
        (buttonView, isChecked) -> {
          getArguments().putBoolean("allowEvenings", isChecked);
          listener.onPreferredDaysChange(getArguments());
        }
    );
    checkNights.setOnCheckedChangeListener(
        (buttonView, isChecked) -> {
          getArguments().putBoolean("allowNights", isChecked);
          listener.onPreferredDaysChange(getArguments());
        }
    );
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
