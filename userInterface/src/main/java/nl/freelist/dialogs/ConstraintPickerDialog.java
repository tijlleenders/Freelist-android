package nl.freelist.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.WindowManager.LayoutParams;
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

    checkMondays.setOnClickListener(
        v -> {
          getArguments().putBoolean("allowMondays", ((CheckBox) v).isChecked());
          listener.onPreferredDaysChange(getArguments());
        });
    checkTuesdays.setOnClickListener(
        v -> {
          getArguments().putBoolean("allowTuesdays", ((CheckBox) v).isChecked());
          listener.onPreferredDaysChange(getArguments());
        });
    checkWednesdays.setOnClickListener(
        v -> {
          getArguments().putBoolean("allowWednesdays", ((CheckBox) v).isChecked());
          listener.onPreferredDaysChange(getArguments());
        });
    checkThursdays.setOnClickListener(
        v -> {
          getArguments().putBoolean("allowThursdays", ((CheckBox) v).isChecked());
          listener.onPreferredDaysChange(getArguments());
        });
    checkFridays.setOnClickListener(
        v -> {
          getArguments().putBoolean("allowFridays", ((CheckBox) v).isChecked());
          listener.onPreferredDaysChange(getArguments());
        });
    checkSaturdays.setOnClickListener(
        v -> {
          getArguments().putBoolean("allowSaturdays", ((CheckBox) v).isChecked());
          listener.onPreferredDaysChange(getArguments());
        });
    checkSundays.setOnClickListener(
        v -> {
          getArguments().putBoolean("allowSundays", ((CheckBox) v).isChecked());
          listener.onPreferredDaysChange(getArguments());
        });
    checkMornings.setOnClickListener(
        v -> {
          getArguments().putBoolean("allowMornings", ((CheckBox) v).isChecked());
          listener.onPreferredDaysChange(getArguments());
        });
    checkAfternoons.setOnClickListener(
        v -> {
          getArguments().putBoolean("allowAfternoons", ((CheckBox) v).isChecked());
          listener.onPreferredDaysChange(getArguments());
        });
    checkEvenings.setOnClickListener(
        v -> {
          getArguments().putBoolean("allowEvenings", ((CheckBox) v).isChecked());
          listener.onPreferredDaysChange(getArguments());
        });
    checkNights.setOnClickListener(
        v -> {
          getArguments().putBoolean("allowNights", ((CheckBox) v).isChecked());
          listener.onPreferredDaysChange(getArguments());
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

    // To keep sofinput from disappearing the second time the Dialog is invoked
    getDialog()
        .getWindow()
        .setSoftInputMode(
            LayoutParams.SOFT_INPUT_STATE_VISIBLE);

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
