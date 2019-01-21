package nl.freelist.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker.Formatter;
import android.widget.Switch;
import android.widget.Toast;
import io.reactivex.schedulers.Schedulers;
import java.util.UUID;
import nl.freelist.androidCrossCuttingConcerns.MySettings;
import nl.freelist.data.dto.ViewModelEntry;
import nl.freelist.domain.crossCuttingConcerns.Constants;
import nl.freelist.domain.crossCuttingConcerns.DurationHelper;
import nl.freelist.freelist.R;
import nl.freelist.viewModelPerActivity.AddEditEntryActivityViewModel;
import nl.freelist.views.NumberPickerDuration;

public class AddEditEntryActivity extends AppCompatActivity {

  private static final String TAG = "AddEditEntryActivity";

  private String uuid;
  private String parentUuid;
  private String defaultUuid;

  private EditText editTextTitle;
  private EditText editTextDescription;
  private Button parentButton;
  private Switch copyMoveSwitch;

  // Todo: move to fragment invoked by tapping the (readable) duration display button
  private NumberPickerDuration yearPicker;
  private NumberPickerDuration weekPicker;
  private NumberPickerDuration dayPicker;
  private NumberPickerDuration hourPicker;
  private NumberPickerDuration minutePicker;
  private NumberPickerDuration secondPicker;

  private nl.freelist.viewModelPerActivity.AddEditEntryActivityViewModel
      AddEditEntryActivityViewModel;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_add_edit_entry);

    initializeViews();

    AddEditEntryActivityViewModel =
        ViewModelProviders.of(this).get(AddEditEntryActivityViewModel.class);

    Bundle bundle = getIntent().getExtras();

    MySettings mySettings = new MySettings(this);
    parentUuid = defaultUuid = mySettings.getUuid();

    if (bundle.containsKey(Constants.EXTRA_ENTRY_PARENT_ID)) {
      parentUuid = bundle.getString(Constants.EXTRA_ENTRY_PARENT_ID);
    }

    if (bundle.containsKey(Constants.EXTRA_REQUEST_TYPE_EDIT)) { // do edit setup
      initializeForEditExisting(bundle);
    } else if (bundle.containsKey(Constants.EXTRA_REQUEST_TYPE_ADD)) { // do add setup
      initializeForAddNew(bundle);
    }
  }

  private void initializeForAddNew(Bundle bundle) {
    if (bundle.containsKey(Constants.EXTRA_ENTRY_PARENT_ID)) {
      initializeParentButtonWithUuid(parentUuid);
      uuid = UUID.randomUUID().toString();
    }
    setTitle("Add new");
  }

  private void initializeForEditExisting(Bundle bundle) {
    uuid = bundle.getString(Constants.EXTRA_ENTRY_ID);

    AddEditEntryActivityViewModel.getViewModelEntry(uuid)
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.io())
        .subscribe(
            (viewModelEntry -> {
              // update View
              runOnUiThread(
                  new Runnable() {
                    @Override
                    public void run() {
                      initializeEditActivityWith(viewModelEntry);
                    }
                  });
            }));

    setTitle("Edit existing");
  }

  private void initializeViews() {
    editTextTitle = findViewById(R.id.edit_text_title);
    editTextDescription = findViewById(R.id.edit_text_description);
    parentButton = findViewById(R.id.button_parent_change);
    copyMoveSwitch = findViewById(R.id.copy_move_switch);

    initializeDurationPicker();

    getSupportActionBar()
        .setHomeAsUpIndicator(R.drawable.ic_close);
  }

  private void initializeDurationPicker() {
    yearPicker = findViewById(R.id.year_picker);
    yearPicker.setMaxValue(99);
    yearPicker.setFormatter(
        new Formatter() {
          @Override
          public String format(int value) {
            return Integer.toString(value) + "y";
          }
        });

    weekPicker = findViewById(R.id.week_picker);
    weekPicker.setMaxValue(51);
    weekPicker.setFormatter(
        new Formatter() {
          @Override
          public String format(int value) {
            return Integer.toString(value) + "w";
          }
        });

    dayPicker = findViewById(R.id.day_picker);
    dayPicker.setMaxValue(6);
    dayPicker.setFormatter(
        new Formatter() {
          @Override
          public String format(int value) {
            return Integer.toString(value) + "d";
          }
        });

    hourPicker = findViewById(R.id.hour_picker);
    hourPicker.setMaxValue(23);
    hourPicker.setFormatter(
        new Formatter() {
          @Override
          public String format(int value) {
            return Integer.toString(value) + "h";
          }
        });

    minutePicker = findViewById(R.id.minute_picker);
    minutePicker.setMinValue(0);
    minutePicker.setMaxValue(59);
    minutePicker.setFormatter(
        new Formatter() {
          @Override
          public String format(int value) {
            return Integer.toString(value) + "m";
          }
        });

    secondPicker = findViewById(R.id.second_picker);
    secondPicker.setMinValue(0);
    secondPicker.setMaxValue(59);
    secondPicker.setFormatter(
        new Formatter() {
          @Override
          public String format(int value) {
            return Integer.toString(value) + "s";
          }
        });
  }

  private void saveEntry() {
    Log.d(TAG, "saveEntry called");
    if (!isValidInput()) {
      return;
    }

    ViewModelEntry viewModelEntryToSave = getViewModelFromScreen();

    AddEditEntryActivityViewModel.saveViewModelEntry(viewModelEntryToSave)
        .subscribe(
            (resultObject -> {
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

    if (bundle.containsKey(Constants.EXTRA_REQUEST_TYPE_EDIT)) {
      Toast.makeText(this, "Existing entry updated!", Toast.LENGTH_LONG).show();
    } else if (bundle.containsKey(Constants.EXTRA_REQUEST_TYPE_ADD)) {
      Toast.makeText(this, "New entry saved!", Toast.LENGTH_LONG).show();
    }

    Intent data = new Intent();
    data.putExtra(Constants.EXTRA_TITLE, viewModelEntryToSave.getTitle());
    setResult(RESULT_OK, data);
    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    finish();
  }

  private ViewModelEntry getViewModelFromScreen() {
    String ownerUuid = defaultUuid;
    if (parentButton.getText().toString() == "") {
      parentUuid = defaultUuid;
    }
    String title = editTextTitle.getText().toString();
    String description = editTextDescription.getText().toString();
    int years = yearPicker.getValue();
    int weeks = weekPicker.getValue();
    int days = dayPicker.getValue();
    int hours = hourPicker.getValue();
    int minutes = minutePicker.getValue();
    int seconds = secondPicker.getValue();
    int duration =
        DurationHelper.getDurationIntFromInts(years, weeks, days, hours, minutes, seconds);
    String durationString = DurationHelper.getDurationStringFromInt(duration);

    ViewModelEntry viewModelEntryToSave =
        new ViewModelEntry(
            ownerUuid,
            parentUuid,
            uuid,
            title,
            description,
            duration,
            Constants.UNKNOWN_ENTRY_VIEW_TYPE,
            Constants.UNKNOWN_CHILDRENCOUNT,
            Constants.UNKNOWN_CHILDRENDURATION
        );
    return viewModelEntryToSave;
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
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    Log.d(TAG, "onActivityResult called.");

    if (requestCode == Constants.CHOOSE_PARENT_REQUEST && resultCode == RESULT_OK) {
      Bundle bundle = data.getExtras();
      if (!parentUuid.equals(bundle.getString(Constants.EXTRA_ENTRY_ID))) {
        parentUuid = bundle.getString(Constants.EXTRA_ENTRY_ID);
        copyMoveSwitch.setVisibility(View.VISIBLE);
      }

      if (parentUuid
          .equals(UUID.nameUUIDFromBytes("tijl.leenders@gmail.com".getBytes()).toString())) {
        parentButton.setText("");
      } else {

        AddEditEntryActivityViewModel.getViewModelEntry(parentUuid)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe(
                (viewModelEntry -> {
                  // update View
                  runOnUiThread(
                      new Runnable() {
                        @Override
                        public void run() {
                          parentButton.setText(viewModelEntry.getTitle());
                        }
                      });
                }));
      }
    }
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

  private void initializeParentButtonWithUuid(String parentUuid) {
    this.parentUuid = parentUuid;
    parentButton.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            Intent chooseParentActivityIntent =
                new Intent(AddEditEntryActivity.this, ChooseEntryActivity.class);
            chooseParentActivityIntent.putExtra(
                Constants.EXTRA_REQUEST_TYPE_CHOOSE_PARENT,
                Constants.CHOOSE_PARENT_REQUEST);
            chooseParentActivityIntent.putExtra(
                Constants.EXTRA_ENTRY_ID, uuid);
            startActivityForResult(
                chooseParentActivityIntent, Constants.CHOOSE_PARENT_REQUEST);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
          }
        });

    if (parentUuid
        .equals(UUID.nameUUIDFromBytes("tijl.leenders@gmail.com".getBytes()).toString())) {
      parentButton.setText("");
      return;
    }

    AddEditEntryActivityViewModel.getViewModelEntry(parentUuid)
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.io())
        .subscribe(
            (viewModelEntryParent -> {
              // update View
              runOnUiThread(
                  new Runnable() {
                    @Override
                    public void run() {
                      parentButton.setText(viewModelEntryParent.getTitle());
                    }
                  });
            }));
  }

  private void initializeEditActivityWith(ViewModelEntry viewModelEntry) {
    Log.d(TAG, "initializeEditActivityWith viewModelEntry " + viewModelEntry.getTitle() + "called");
    editTextTitle.setText(viewModelEntry.getTitle());
    editTextDescription.setText(viewModelEntry.getDescription());
    // Todo: fix bug in NumberPicker that doesn't display formatting on first rendering
    yearPicker.setValue(viewModelEntry.getYears());
    weekPicker.setValue(viewModelEntry.getWeeks());
    dayPicker.setValue(viewModelEntry.getDays());
    hourPicker.setValue(viewModelEntry.getHours());
    minutePicker.setValue(viewModelEntry.getMinutes());
    secondPicker.setValue(viewModelEntry.getSeconds());
    initializeParentButtonWithUuid(viewModelEntry.getParentUuid());
    return;
  }
}
