package nl.freelist.freelist;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AddEntryActivity extends AppCompatActivity
    implements DatePickerDialog.OnDateSetListener {

  public static final String EXTRA_TITLE =
      "nl.freelist.EXTRA_TITLE";

  private EditText editTextTitle;
  private EditText editTextDescription;
  private static EditText editTextDueDate;
  private NumberPicker numberPickerDuration;

  private CalendarViewModel addEntryViewModel;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_add_entry);

    editTextTitle = findViewById(R.id.edit_text_title);
    editTextDescription = findViewById(R.id.edit_text_description);
    numberPickerDuration = findViewById(R.id.number_picker_duration);
    editTextDueDate = findViewById(R.id.edit_due_date);

    numberPickerDuration.setMinValue(1);
    numberPickerDuration.setMaxValue(8);
    numberPickerDuration
        .setDisplayedValues(new String[]{"5m", "15m", "45m", "2h", "4h", "8h", "12h", "24h"});

    //get current date as default for due date
    final Calendar c = Calendar.getInstance();

    String currentDate = String.valueOf(c.get(Calendar.YEAR)) +
        "-" +
        (c.get(Calendar.MONTH) + 1) + //Android SDK says months are indexed starting at zero
        "-" +
        c.get(Calendar.DAY_OF_MONTH);
    editTextDueDate.setText(currentDate);
    editTextDueDate.setFocusable(false); // setting android:inputType="none" in XML is not enough

    getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
    setTitle("Add something");

    addEntryViewModel = ViewModelProviders.of(this).get(CalendarViewModel.class);
    addEntryViewModel.getAllEntries().observe(this, new Observer<List<Entry>>() {
      @Override
      public void onChanged(@Nullable List<Entry> entries) {
        // do nothing; just necessary for addEntryViewModel to load data
      }
    });
  }

  private int getNumberPicker(int pickSelected) {
    int seconds;
    switch (pickSelected) {
      case 1:
        seconds = 300;
        break;
      case 2:
        seconds = 900;
        break;
      case 3:
        seconds = 2700;
        break;
      case 4:
        seconds = 7200;
        break;
      case 5:
        seconds = 14400;
        break;
      case 6:
        seconds = 28800;
        break;
      case 7:
        seconds = 43200;
        break;
      case 8:
        seconds = 86400;
        break;
      default:
        seconds = 540;
        break;
    }
    return seconds;
  }

  private void saveEntry() {
    String title = editTextTitle.getText().toString();
    String description = editTextDescription.getText().toString();
    int duration = getNumberPicker(numberPickerDuration.getValue());
    long date = 1539456867;
    boolean isCompletedStatus = false;

    if (title.trim().isEmpty() || description.trim().isEmpty()) {
      Toast.makeText(this, "Please insert a title and description", Toast.LENGTH_SHORT).show();
      return;
    }

    Entry entry = new Entry(title, description, duration, date, isCompletedStatus);
    addEntryViewModel.insert(entry);
    Toast.makeText(this, "Entry saved!", Toast.LENGTH_SHORT).show();

    Intent data = new Intent();
    data.putExtra(EXTRA_TITLE, title);
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
    month += 1; //Android SDK says months are indexed starting at zero
    String toShow = String.valueOf(year) + "-" + String.valueOf(month) + "-" + String.valueOf(day);
    editTextDueDate.setText(toShow);
  }

  public static class DatePickerFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
      // Use the current date as the default date in the picker
      Calendar c = Calendar.getInstance();
      int year = c.get(Calendar.YEAR);
      int month = c.get(Calendar.MONTH);
      int day = c.get(Calendar.DAY_OF_MONTH);

      // Parse content of EditText to start datePicker with the date in the EditText - if possible
      String editTextDateString = editTextDueDate.getText().toString();
      DateFormat formatter = new SimpleDateFormat(
          "yyyy-M-d"); //also works with leading zero in month or days field
      try {
        Date convertedDate = formatter.parse(editTextDateString);
        c.setTime(convertedDate);
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
      } catch (ParseException e) {
        e.printStackTrace();
      }

      // Create a new instance of DatePickerDialog and return it
      return new DatePickerDialog(getActivity(), (DatePickerDialog.OnDateSetListener) getActivity(),
          year, month, day);
    }

  }

}
