package nl.freelist.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker.Formatter;
import android.widget.Toast;
import io.reactivex.schedulers.Schedulers;
import nl.freelist.domain.crossCuttingConcerns.DurationHelper;
import nl.freelist.freelist.R;
import nl.freelist.presentationConstants.ActivityConstants;
import nl.freelist.viewModelPerActivity.AddEditEntryActivityViewModel;
import nl.freelist.viewModelPerEntity.ViewModelEntry;
import nl.freelist.views.NumberPickerDuration;

public class AddEditEntryActivity extends AppCompatActivity {

  private int id;
  private int parentId;
  private int parentOfParentId;

  private EditText editTextTitle;
  private EditText editTextDescription;
  private Button parentButton;

  //Todo: move to fragment invoked by tapping the (readable) duration display button
  private NumberPickerDuration yearPicker;
  private NumberPickerDuration weekPicker;
  private NumberPickerDuration dayPicker;
  private NumberPickerDuration hourPicker;
  private NumberPickerDuration minutePicker;
  private NumberPickerDuration secondPicker;

  private nl.freelist.viewModelPerActivity.AddEditEntryActivityViewModel AddEditEntryActivityViewModel;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_add_edit_entry);

    initializeViews();

    AddEditEntryActivityViewModel =
        ViewModelProviders.of(this)
            .get(AddEditEntryActivityViewModel.class);

    Bundle bundle = getIntent().getExtras();

    if (bundle.containsKey(ActivityConstants.EXTRA_REQUEST_TYPE_EDIT)) { // do edit setup

      id = Integer.valueOf(bundle.getString(ActivityConstants.EXTRA_ENTRY_ID));

      AddEditEntryActivityViewModel
          .getViewModelEntry(id)
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
                            updateEditActivityWith(viewModelEntry);
                          }
                        });

                  }));

      getSupportActionBar()
          .setHomeAsUpIndicator(R.drawable.ic_close); // todo: move outside of if else if?
      setTitle("Edit existing");
    } else if (bundle.containsKey(ActivityConstants.EXTRA_REQUEST_TYPE_ADD)) { // do add setup
      getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
      setTitle("Add new");
    } // Todo: Add else if (bundle.containsKey(ActivityConstants.EXTRA_PARENT_CHANGED_KEY)) {}
  }

  private void initializeViews() {
    editTextTitle = findViewById(R.id.edit_text_title);
    editTextDescription = findViewById(R.id.edit_text_description);
    parentButton = findViewById(R.id.button_parent_change);

    yearPicker = findViewById(R.id.year_picker);
    yearPicker.setMaxValue(99);
    yearPicker.setFormatter(new Formatter() {
      @Override
      public String format(int value) {
        return Integer.toString(value) + "y";
      }
    });

    weekPicker = findViewById(R.id.week_picker);
    weekPicker.setMaxValue(51);
    weekPicker.setFormatter(new Formatter() {
      @Override
      public String format(int value) {
        return Integer.toString(value) + "w";
      }
    });

    dayPicker = findViewById(R.id.day_picker);
    dayPicker.setMaxValue(6);
    dayPicker.setFormatter(new Formatter() {
      @Override
      public String format(int value) {
        return Integer.toString(value) + "d";
      }
    });

    hourPicker = findViewById(R.id.hour_picker);
    hourPicker.setMaxValue(23);
    hourPicker.setFormatter(new Formatter() {
      @Override
      public String format(int value) {
        return Integer.toString(value) + "h";
      }
    });

    minutePicker = findViewById(R.id.minute_picker);
    minutePicker.setMinValue(0);
    minutePicker.setMaxValue(59);
    minutePicker.setFormatter(new Formatter() {
      @Override
      public String format(int value) {
        return Integer.toString(value) + "m";
      }
    });

    secondPicker = findViewById(R.id.second_picker);
    secondPicker.setMinValue(0);
    secondPicker.setMaxValue(59);
    secondPicker.setFormatter(new Formatter() {
      @Override
      public String format(int value) {
        return Integer.toString(value) + "s";
      }
    });

  }

  private void saveEntry() {
    if (!isValidInput()) {
      return;
    }

    String title = editTextTitle.getText().toString();
    String description = editTextDescription.getText().toString();
    int years = yearPicker.getValue();
    int weeks = weekPicker.getValue();
    int days = dayPicker.getValue();
    int hours = hourPicker.getValue();
    int minutes = minutePicker.getValue();
    int seconds = secondPicker.getValue();
    int duration = DurationHelper
        .getDurationIntFromInts(years, weeks, days, hours, minutes, seconds);
    String durationString = DurationHelper.getDurationStringFromInt(duration);
    ViewModelEntry viewModelEntryToSave =
        new ViewModelEntry(
            id, parentId, title, description, durationString, duration, years, weeks, days, hours,
            minutes, seconds,
            ActivityConstants.UNKNOWN_ENTRY_VIEW_TYPE);

    AddEditEntryActivityViewModel
        .saveViewModelEntry(viewModelEntryToSave)
        .subscribe(
            (
                resultObject -> {
                  // update View
                  runOnUiThread(
                      new Runnable() {
                        @Override
                        public void run() {
                          // Do stufff
                        }
                      });

                }));


    Bundle bundle = getIntent().getExtras();

    if (bundle.containsKey(ActivityConstants.EXTRA_REQUEST_TYPE_EDIT)) {
      Toast.makeText(this, "Existing entry updated!", Toast.LENGTH_LONG).show();
    } else if (bundle.containsKey(ActivityConstants.EXTRA_REQUEST_TYPE_ADD)) {
      Toast.makeText(this, "New entry saved!", Toast.LENGTH_LONG).show();
    }

    Intent data = new Intent();
    // todo: make distinction between add or update entry?
    data.putExtra(ActivityConstants.EXTRA_TITLE, title);
    setResult(RESULT_OK, data);
    finish();
  }

  private boolean isValidInput() {
    String title = editTextTitle.getText().toString();
    String description = editTextDescription.getText().toString();
    if (title.trim().isEmpty()) {
      Toast.makeText(this, "Please insert a title", Toast.LENGTH_SHORT).show();
      return false;
    }
    return true;
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

  private void updateEditActivityWith(ViewModelEntry viewModelEntry) {
    editTextTitle.setText(viewModelEntry.getTitle());
    editTextDescription.setText(viewModelEntry.getDescription());
    minutePicker.setValue(viewModelEntry.getMinutes());
    parentId = viewModelEntry.getParentId();

    if (parentId == 0) {
      parentButton.setText("");
      return;
    }
    AddEditEntryActivityViewModel
        .getViewModelEntry(parentId)
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.io())
        .subscribe(
            (
                viewModelEntryParent -> {
                  // update View
                  runOnUiThread(
                      new Runnable() {
                        @Override
                        public void run() {
                          parentOfParentId = viewModelEntryParent.getParentId();
                          parentButton.setText(viewModelEntryParent.getTitle());
                        }
                      });

                }));
    return;
  }

}
