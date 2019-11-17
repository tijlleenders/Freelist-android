package nl.freelist.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.NumberPicker.Formatter;
import android.widget.NumberPicker.OnValueChangeListener;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.schedulers.Schedulers;
import java.util.UUID;
import nl.freelist.androidCrossCuttingConcerns.MySettings;
import nl.freelist.commands.ChangeEntryDescriptionCommand;
import nl.freelist.commands.ChangeEntryDurationCommand;
import nl.freelist.commands.ChangeEntryParentCommand;
import nl.freelist.commands.ChangeEntryTitleCommand;
import nl.freelist.commands.CreateEntryCommand;
import nl.freelist.data.Repository;
import nl.freelist.data.dto.ViewModelEntry;
import nl.freelist.domain.crossCuttingConcerns.Constants;
import nl.freelist.freelist.R;
import nl.freelist.viewModelPerActivity.AddEditEntryActivityViewModel;
import nl.freelist.views.NumberPickerDuration;

public class AddEditEntryActivity extends AppCompatActivity {

  private static final String TAG = "AddEditEntryActivity";

  private String uuid; // Todo: why ever store a UUID as a string, if not in data persistence layer?
  private String parentUuid;
  private String defaultUuid;
  private int lastSavedEventSequenceNumber = -1;

  private Repository repository;

  private EditText editTextTitle;
  private EditText editTextDescription;
  private Button parentButton;
  private Button scheduleButton;

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
  protected void onResume() {
    Log.d(TAG, "onResume called.");

    super.onResume();
  }

  @Override
  protected void onPause() {
    Log.d(TAG, "onPause exited without saving!");
    if (editTextTitle.hasFocus()) {
      editTextDescription.requestFocus();
    }
    if (editTextDescription.hasFocus()) {
      editTextTitle.requestFocus();
    }
    super.onPause();
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_add_edit_entry);

    initializeViews();

    repository = new Repository(this.getApplicationContext());

    AddEditEntryActivityViewModel =
        ViewModelProviders.of(this).get(AddEditEntryActivityViewModel.class);

    Bundle bundle = getIntent().getExtras();

    MySettings mySettings = new MySettings(this);
    parentUuid = defaultUuid = mySettings.getUuid();

    if (bundle != null && bundle.containsKey(Constants.EXTRA_ENTRY_PARENT_ID)) {
      parentUuid = bundle.getString(Constants.EXTRA_ENTRY_PARENT_ID);
    }

    if (bundle != null && bundle.containsKey(Constants.EXTRA_REQUEST_TYPE_EDIT)) { // do edit setup
      initializeForEditExisting(bundle);
    } else if (bundle != null && bundle.containsKey(Constants.EXTRA_REQUEST_TYPE_ADD)) { // do add
      // setup
      initializeForAddNew(bundle);

      CreateEntryCommand createEntryCommand =
          new CreateEntryCommand(defaultUuid, parentUuid, uuid, repository);
      lastSavedEventSequenceNumber += 1;
      AddEditEntryActivityViewModel.handle(createEntryCommand)
          .subscribeOn(Schedulers.io())
          .observeOn(Schedulers.io())
          .subscribe();
    }

    attachViewListeners();
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
    scheduleButton = findViewById(R.id.schedule_button);

    initializeDurationPicker();
  }

  private void attachViewListeners() {
    OnFocusChangeListener editTextTitleOnFocusChangeListener =
        new OnFocusChangeListener() {
          private String textOnFocusGained = "";

          @Override
          public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
              textOnFocusGained = editTextTitle.getText().toString();
            } else {
              if (!textOnFocusGained.equals(editTextTitle.getText().toString())) {
                Log.d(
                    TAG,
                    "Title changed from "
                        + textOnFocusGained
                        + " to "
                        + editTextTitle.getText().toString()
                        + " with eventSequenceNumber "
                        + lastSavedEventSequenceNumber);
                ChangeEntryTitleCommand changeEntryTitleCommand =
                    new ChangeEntryTitleCommand(
                        uuid,
                        textOnFocusGained,
                        editTextTitle.getText().toString(),
                        lastSavedEventSequenceNumber,
                        repository);
                lastSavedEventSequenceNumber += 1;
                AddEditEntryActivityViewModel.handle(changeEntryTitleCommand)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .subscribe();
              }
            }
          }
        };
    editTextTitle.setOnFocusChangeListener(editTextTitleOnFocusChangeListener);

    OnFocusChangeListener editTextDescriptionOnFocusChangeListener =
        new OnFocusChangeListener() {
          private String textOnFocusGained = "";

          @Override
          public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
              textOnFocusGained = editTextDescription.getText().toString();
            } else {
              if (!textOnFocusGained.equals(editTextDescription.getText().toString())) {
                Log.d(
                    TAG,
                    "Description changed from "
                        + textOnFocusGained
                        + " to "
                        + editTextTitle.getText().toString()
                        + " with eventSequenceNumber "
                        + lastSavedEventSequenceNumber);
                ChangeEntryDescriptionCommand changeEntryDescriptionCommand =
                    new ChangeEntryDescriptionCommand(
                        uuid,
                        textOnFocusGained,
                        editTextDescription.getText().toString(),
                        lastSavedEventSequenceNumber,
                        repository);
                lastSavedEventSequenceNumber += 1;
                AddEditEntryActivityViewModel.handle(changeEntryDescriptionCommand)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .subscribe();
              }
            }
          }
        };
    editTextDescription.setOnFocusChangeListener(editTextDescriptionOnFocusChangeListener);

    OnValueChangeListener secondPickerOnValueChangeListener =
        new OnValueChangeListener() {
          @Override
          public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
            Log.d(
                TAG,
                "Seconds changed from "
                    + oldVal
                    + " to "
                    + newVal
                    + " with eventSequenceNumber "
                    + lastSavedEventSequenceNumber);
            ChangeEntryDurationCommand changeEntryDurationCommand =
                new ChangeEntryDurationCommand(
                    uuid, oldVal, newVal, "seconds", lastSavedEventSequenceNumber, repository);
            lastSavedEventSequenceNumber += 1;
            AddEditEntryActivityViewModel.handle(changeEntryDurationCommand)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe();
          }
        };
    secondPicker.setOnValueChangedListener(secondPickerOnValueChangeListener);

    OnValueChangeListener minutePickerOnValueChangeListener =
        new OnValueChangeListener() {
          @Override
          public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
            Log.d(
                TAG,
                "Minutes changed from "
                    + oldVal
                    + " to "
                    + newVal
                    + " with eventSequenceNumber "
                    + lastSavedEventSequenceNumber);
            ChangeEntryDurationCommand changeEntryDurationCommand =
                new ChangeEntryDurationCommand(
                    uuid, oldVal, newVal, "minutes", lastSavedEventSequenceNumber, repository);
            lastSavedEventSequenceNumber += 1;
            AddEditEntryActivityViewModel.handle(changeEntryDurationCommand)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe();
          }
        };
    minutePicker.setOnValueChangedListener(minutePickerOnValueChangeListener);

    OnValueChangeListener hourPickerOnValueChangeListener =
        new OnValueChangeListener() {
          @Override
          public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
            Log.d(
                TAG,
                "Hours changed from "
                    + oldVal
                    + " to "
                    + newVal
                    + " with eventSequenceNumber "
                    + lastSavedEventSequenceNumber);
            ChangeEntryDurationCommand changeEntryDurationCommand =
                new ChangeEntryDurationCommand(
                    uuid, oldVal, newVal, "hours", lastSavedEventSequenceNumber, repository);
            lastSavedEventSequenceNumber += 1;
            AddEditEntryActivityViewModel.handle(changeEntryDurationCommand)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe();
          }
        };
    hourPicker.setOnValueChangedListener(hourPickerOnValueChangeListener);

    OnValueChangeListener dayPickerOnValueChangeListener =
        new OnValueChangeListener() {
          @Override
          public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
            Log.d(
                TAG,
                "Days changed from "
                    + oldVal
                    + " to "
                    + newVal
                    + " with eventSequenceNumber "
                    + lastSavedEventSequenceNumber);
            ChangeEntryDurationCommand changeEntryDurationCommand =
                new ChangeEntryDurationCommand(
                    uuid, oldVal, newVal, "days", lastSavedEventSequenceNumber, repository);
            lastSavedEventSequenceNumber += 1;
            AddEditEntryActivityViewModel.handle(changeEntryDurationCommand)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe();
          }
        };
    dayPicker.setOnValueChangedListener(dayPickerOnValueChangeListener);

    OnValueChangeListener weekPickerOnValueChangeListener =
        new OnValueChangeListener() {
          @Override
          public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
            Log.d(
                TAG,
                "Weeks changed from "
                    + oldVal
                    + " to "
                    + newVal
                    + " with eventSequenceNumber "
                    + lastSavedEventSequenceNumber);
            ChangeEntryDurationCommand changeEntryDurationCommand =
                new ChangeEntryDurationCommand(
                    uuid, oldVal, newVal, "weeks", lastSavedEventSequenceNumber, repository);
            lastSavedEventSequenceNumber += 1;
            AddEditEntryActivityViewModel.handle(changeEntryDurationCommand)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe();
          }
        };
    weekPicker.setOnValueChangedListener(weekPickerOnValueChangeListener);

    OnValueChangeListener yearPickerOnValueChangeListener =
        new OnValueChangeListener() {
          @Override
          public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
            Log.d(
                TAG,
                "Years changed from "
                    + oldVal
                    + " to "
                    + newVal
                    + " with eventSequenceNumber "
                    + lastSavedEventSequenceNumber);
            ChangeEntryDurationCommand changeEntryDurationCommand =
                new ChangeEntryDurationCommand(
                    uuid, oldVal, newVal, "years", lastSavedEventSequenceNumber, repository);
            lastSavedEventSequenceNumber += 1;
            AddEditEntryActivityViewModel.handle(changeEntryDurationCommand)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe();
          }
        };
    yearPicker.setOnValueChangedListener(yearPickerOnValueChangeListener);

    attachScheduleButtonListener();
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

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    Log.d(TAG, "onActivityResult called.");

    if (requestCode == Constants.CHOOSE_PARENT_REQUEST && resultCode == RESULT_OK) {
      Bundle bundle = data.getExtras();
      if (bundle != null && !parentUuid.equals(bundle.getString(Constants.EXTRA_ENTRY_ID))) {
        ChangeEntryParentCommand changeEntryParentCommand =
            new ChangeEntryParentCommand(
                uuid,
                parentUuid,
                bundle.getString(Constants.EXTRA_ENTRY_ID),
                lastSavedEventSequenceNumber,
                repository);
        lastSavedEventSequenceNumber += 1;
        AddEditEntryActivityViewModel.handle(changeEntryParentCommand)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe();
        parentUuid = bundle.getString(Constants.EXTRA_ENTRY_ID);
        setParentButtonText();
      }
    }
  }

  private void setParentButtonText() {
    if (parentUuid.equals(defaultUuid)) {
      parentButton.setText("");
      return;
    } else {
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
                Constants.EXTRA_REQUEST_TYPE_CHOOSE_PARENT, Constants.CHOOSE_PARENT_REQUEST);
            chooseParentActivityIntent.putExtra(Constants.EXTRA_ENTRY_ID, uuid);
            startActivityForResult(chooseParentActivityIntent, Constants.CHOOSE_PARENT_REQUEST);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
          }
        });
    setParentButtonText();
  }

  private void attachScheduleButtonListener() {
    scheduleButton.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            Log.d(TAG, "scheduleButton clicked for entry ..." + uuid);
            scheduleButton.setEnabled(false);
            scheduleButton.setText("scheduling...");
            AddEditEntryActivityViewModel.scheduleEntry(uuid, defaultUuid)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new DisposableCompletableObserver() {
                  @Override
                  public void onComplete() {
                    // update View
                    runOnUiThread(
                        new Runnable() {
                          @Override
                          public void run() {
                            scheduleButton.setText("Scheduling completed!");
                          }
                        });
                  }

                  @Override
                  public void onError(Throwable e) {
                    // update View
                    runOnUiThread(
                        new Runnable() {
                          @Override
                          public void run() {
                            scheduleButton.setText("Scheduling failed!");
                          }
                        });
                  }
                });
          }
        }
    );
    scheduleButton.setEnabled(true);
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
    lastSavedEventSequenceNumber = viewModelEntry.getLastSavedEventSequenceNumber();
    return;
  }
}
