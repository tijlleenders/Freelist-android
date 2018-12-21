package nl.freelist.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;
import io.reactivex.schedulers.Schedulers;
import java.util.Calendar;
import java.util.Date;
import nl.freelist.constants.ActivityConstants;
import nl.freelist.domain.crossCuttingConcerns.DateHelper;
import nl.freelist.views.NumberPickerDuration;
import nl.freelist.freelist.R;
import nl.freelist.viewModels.ViewModelEntry;
import nl.freelist.viewModels.AddEditEntryActivityViewModel;

public class AddEditEntryActivity extends AppCompatActivity
    implements DatePickerDialog.OnDateSetListener {

  private EditText editTextTitle;
  private EditText editTextDescription;
  private EditText editTextDueDate;
  private Button parentButton;
  private NumberPickerDuration numberPickerDuration;
  private nl.freelist.viewModels.AddEditEntryActivityViewModel AddEditEntryActivityViewModel;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_add_edit_entry);

    editTextTitle = findViewById(R.id.edit_text_title);
    editTextDescription = findViewById(R.id.edit_text_description);
    numberPickerDuration = findViewById(R.id.number_picker_duration);
    editTextDueDate = findViewById(R.id.edit_due_date);
    parentButton = findViewById(R.id.button_parent_change);

    numberPickerDuration.setMinValue(1);
    numberPickerDuration.setMaxValue(8); // todo: make configurable
    numberPickerDuration.setDisplayedValues(
        new String[]{
            "5m", "15m", "45m", "2h", "4h", "8h", "12h", "24h"
        }); // todo: make configurable, ie make custom subclass with functions to set with string or
    // duration int?

    // Intent.ACTION_* fields are String constant.
    // You cannot use switch with String until JDK 7 android use JDK 6 or 5 to compile. So you can't
    // use that method on Android
    // So using if else if :(
    Bundle bundle = getIntent().getExtras();

    if (bundle.containsKey(ActivityConstants.EXTRA_REQUEST_TYPE_EDIT)) { // do edit setup
      AddEditEntryActivityViewModel =
          ViewModelProviders.of(this)
              .get(AddEditEntryActivityViewModel.class);
      String id = bundle.getString(ActivityConstants.EXTRA_ENTRY_ID);

      AddEditEntryActivityViewModel
          .getViewModelEntry(Integer.valueOf(id))
          .subscribeOn(Schedulers.io())
          .observeOn(Schedulers.io())
          .subscribe(
              (
                  viewModelEntry -> {
                    // update View
                    runOnUiThread(
                        new Runnable() {
                          @Override
                          public void run() {
                            editTextTitle.setText(viewModelEntry.getTitle());
                            editTextDescription.setText(viewModelEntry.getDescription());
                            numberPickerDuration.setValue(
                                numberPickerDuration
                                    .getNumberPickerPosition(viewModelEntry.getDuration()));
                            editTextDueDate.setText(viewModelEntry.getDate());
                            parentButton.setText(viewModelEntry.getParentTitle());
                          }
                        });

                  }));


      getSupportActionBar()
          .setHomeAsUpIndicator(R.drawable.ic_close); // todo: move outside of if else if?
      setTitle("Edit existing");
    } else if (bundle.containsKey(ActivityConstants.EXTRA_REQUEST_TYPE_ADD)) { // do add setup
      // get current date as default for due date todo: move to DateHelper class
      final Calendar c = Calendar.getInstance();

      String currentDate =
          String.valueOf(c.get(Calendar.YEAR))
              + "-"
              + (c.get(Calendar.MONTH) + 1)
              + // Android SDK says months are indexed starting at zero
              "-"
              + c.get(Calendar.DAY_OF_MONTH);
      editTextDueDate.setText(currentDate);

      getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
      setTitle("Add new");
    } // Todo: Add else if (bundle.containsKey(ActivityConstants.EXTRA_PARENT_CHANGED_KEY)) {}

    editTextDueDate.setFocusable(false); // setting android:inputType="none" in XML is not enough
  }

  private void saveEntry() {
    int id = ActivityConstants.VIEWMODEL_ENTRY_ID_NOT_SET;
    int parentId = 0; //Todo: replace with current ID from parent button value
    String parentTitle = "parentTitle"; //Todo: replace with current ID from parent button value
    String title = editTextTitle.getText().toString();
    String description = editTextDescription.getText().toString();
    String duration = "5m"; //Todo: implement setNumberPicker and getNumberPicker based on string value only (abstract the rest) and get current value from numberPickerDuration
    String date = editTextDueDate.getText().toString();
    boolean isCompletedStatus = false;

    if (title.trim().isEmpty() || description.trim().isEmpty()) {
      boolean notificationBool =
          PreferenceManager.getDefaultSharedPreferences(this).getBoolean("notifications", false);
      if (notificationBool) {
        Toast.makeText(this, "setting true", Toast.LENGTH_SHORT).show();
      } else {
        Toast.makeText(this, "setting false", Toast.LENGTH_SHORT).show();
      }
      // Toast.makeText(this, "Please insert a title and description", Toast.LENGTH_SHORT).show();
      return;
    }

    ViewModelEntry entryToSave =
        new ViewModelEntry(
            id, parentId, parentTitle, title, description, duration, date, isCompletedStatus);

    Bundle bundle = getIntent().getExtras();
    // Intent.ACTION_* fields are String constant.
    // You cannot use switch with String until JDK 7 android use JDK 6 or 5 to compile. So you can't
    // use that method on Android
    // So using if else if :(

    if (bundle.containsKey(ActivityConstants.EXTRA_REQUEST_TYPE_EDIT)) {
      entryToSave.setId((Integer) bundle.get(ActivityConstants.EXTRA_ENTRY_ID));
      //      AddEditEntryActivityViewModel.update(entryToSave);
      Toast.makeText(this, "Existing entry updated!", Toast.LENGTH_LONG).show();
    } else if (bundle.containsKey(ActivityConstants.EXTRA_REQUEST_TYPE_ADD)) {
      //      AddEditEntryActivityViewModel.insert(entryToSave);
      Toast.makeText(this, "New entry saved!", Toast.LENGTH_LONG).show();
    }

    Intent data = new Intent();
    // todo: make distinction between add or update entry?
    data.putExtra(ActivityConstants.EXTRA_TITLE, title);
    setResult(RESULT_OK, data);
    finish();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater menuInflater = getMenuInflater();
    menuInflater.inflate(R.menu.add_entry_menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.save_entry:
        saveEntry();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  public void showDatePickerDialog(View v) {
    DialogFragment newFragment = new DatePickerFragment();
    newFragment.show(getSupportFragmentManager(), "datePicker");
  }

  @Override
  public void onDateSet(DatePicker view, int year, int month, int day) {
    // Do something with the date chosen by the user
    month += 1; // Android SDK says months are indexed starting at zero
    String toShow = String.valueOf(year) + "-" + String.valueOf(month) + "-" + String.valueOf(day);
    editTextDueDate.setText(toShow);
  }

  public static class DatePickerFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
      // Use the current date as the default date in the picker
      Calendar c = Calendar.getInstance();

      // Parse content of EditText to start datePicker with the date in the EditText - if possible
      String editTextDateString =
          ((AddEditEntryActivity) getActivity()).editTextDueDate.getText().toString();
      Date convertedDate = DateHelper.getDateFromString(editTextDateString);
      c.setTime(convertedDate);
      int year = c.get(Calendar.YEAR);
      int month = c.get(Calendar.MONTH);
      int day = c.get(Calendar.DAY_OF_MONTH);

      // Create a new instance of DatePickerDialog and return it
      return new DatePickerDialog(
          getActivity(), (DatePickerDialog.OnDateSetListener) getActivity(), year, month, day);
    }
  }
}
