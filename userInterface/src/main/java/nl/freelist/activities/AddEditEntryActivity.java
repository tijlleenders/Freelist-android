package nl.freelist.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import io.reactivex.schedulers.Schedulers;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import nl.freelist.androidCrossCuttingConcerns.MySettings;
import nl.freelist.commands.CreateEntryCommand;
import nl.freelist.commands.SaveEntryCommand;
import nl.freelist.data.Repository;
import nl.freelist.data.dto.ViewModelEntry;
import nl.freelist.dialogs.ConstraintPickerDialog;
import nl.freelist.dialogs.DurationPickerDialog;
import nl.freelist.dialogs.FDatePickerDialog;
import nl.freelist.dialogs.FTimePickerDialog;
import nl.freelist.dialogs.NoticeDialogListener;
import nl.freelist.domain.crossCuttingConcerns.Constants;
import nl.freelist.domain.crossCuttingConcerns.TimeHelper;
import nl.freelist.domain.valueObjects.DtrConstraint;
import nl.freelist.freelist.R;
import nl.freelist.viewModelPerActivity.AddEditEntryActivityViewModel;

public class AddEditEntryActivity extends AppCompatActivity
    implements OnFocusChangeListener
    , NoticeDialogListener {

  private static final String TAG = "AddEditEntryActivity";

  private String uuid; // Todo: why ever store a UUID as a string, if not in data persistence layer?
  private String parentUuid;
  private String defaultUuid;
  private int lastSavedEventSequenceNumber;
  private int saveCommandsInProgress = 0;

  private Repository repository;

  SimpleDateFormat simpleDateFormat = new SimpleDateFormat();

  private String title = "";
  private OffsetDateTime startDateTime;
  private long duration = 0;
  private OffsetDateTime endDateTime;
  private String notes = "";

  private TextInputLayout textInputLayoutTitle;
  private TextInputLayout textInputLayoutStartDateTime;
  private TextInputLayout textInputLayoutDuration;
  private TextInputLayout textInputLayoutEndDateTime;
  private TextInputLayout textInputLayoutSchedulePrefs;
  private TextInputLayout textInputLayoutRepeat;
  private TextInputLayout textInputLayoutStartsAfter;
  private TextInputLayout textInputLayoutParent;
  private TextInputLayout textInputLayoutNotes;

  private TextInputEditText textInputEditTextTitle;
  private TextInputEditText textInputEditTextStartDateTime;
  private TextInputEditText textInputEditTextDuration;
  private TextInputEditText textInputEditTextEndDateTime;
  private TextInputEditText textInputEditTextSchedulePrefs;
  private TextInputEditText textInputEditTextRepeat;
  private TextInputEditText textInputEditTextStartsAfter;
  private TextInputEditText textInputEditTextParent;
  private TextInputEditText textInputEditTextNotes;

  private List<DtrConstraint> dtrConstraints;

  private nl.freelist.viewModelPerActivity.AddEditEntryActivityViewModel
      AddEditEntryActivityViewModel;


  @Override
  protected void onResume() {
    Log.d(TAG, "onResume called.");
    //Todo: do something with bundle from ChooseCalendarOptionActivity

    super.onResume();
    textInputLayoutTitle.requestFocus();
  }

  @Override
  protected void onPause() {
    Log.d(TAG, "onPause");
    saveChangedFields();
    super.onPause();
  }

  private void saveChangedFields() {
    if (saveCommandsInProgress > 0) {
      Log.d(TAG, "save Command already in progress! #:" + saveCommandsInProgress);
      return;
    }
    title = textInputEditTextTitle.getText().toString();
    notes = textInputEditTextNotes.getText().toString();
    Log.d(
        TAG,
        "SaveEntryCommand"
            + " with eventSequenceNumber "
            + lastSavedEventSequenceNumber);
    SaveEntryCommand saveEntryCommand = //Todo: add parent
        new SaveEntryCommand(
            uuid,
            title,
            startDateTime,
            duration,
            endDateTime,
            notes,
            lastSavedEventSequenceNumber,
            repository);
    saveCommandsInProgress += 1;
    Toast.makeText(AddEditEntryActivity.this,
        "SaveCommandsInProgress: " + saveCommandsInProgress, Toast.LENGTH_SHORT)
        .show();
    AddEditEntryActivityViewModel.handle(saveEntryCommand)
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.io())
        .subscribe((result -> {
          // update View
          runOnUiThread(
              new Runnable() {
                @Override
                public void run() {
                  saveCommandsInProgress -= 1;
                  if (!result.isSuccess()) {
                    Toast.makeText(AddEditEntryActivity.this,
                        "Sorry! SaveEntry failed!", Toast.LENGTH_SHORT)
                        .show();
                  } else {
                    initializeForEditExisting(uuid);
                  }
                }
              });
        }));
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
      uuid = bundle.getString(Constants.EXTRA_ENTRY_ID);
      initializeForEditExisting(uuid);
    } else if (bundle != null && bundle.containsKey(Constants.EXTRA_REQUEST_TYPE_ADD)) { // do add
      // setup
      initializeForAddNew(bundle);
    }

    attachViewListeners();
  }


  private void initializeForAddNew(Bundle bundle) {
    if (bundle.containsKey(Constants.EXTRA_ENTRY_PARENT_ID)) {
      uuid = UUID.randomUUID().toString();
    }
    setTitle("Add new Freelist");
    CreateEntryCommand createEntryCommand =
        new CreateEntryCommand(defaultUuid, parentUuid, uuid, repository);
    AddEditEntryActivityViewModel.handle(createEntryCommand)
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.io())
        .subscribe(
            (result -> {
              // update View
              runOnUiThread(
                  new Runnable() {
                    @Override
                    public void run() {
                      if (!result.isSuccess()) {
                        Toast.makeText(AddEditEntryActivity.this, "Sorry! Create entry failed!",
                            Toast.LENGTH_SHORT)
                            .show();
                      }
                    }
                  });
            })
        );
  }

  private void initializeForEditExisting(String uuid) {

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
              //Todo: setup action if fails
            }));

    setTitle("Edit existing Freelist");
  }

  private void initializeViews() {
    textInputLayoutTitle = findViewById(R.id.text_input_layout_title);
    textInputLayoutStartDateTime = findViewById(R.id.text_input_layout_start_date_time);
    textInputLayoutDuration = findViewById(R.id.text_input_layout_duration);
    textInputLayoutEndDateTime = findViewById(R.id.text_input_layout_end_date_time);
    textInputLayoutSchedulePrefs = findViewById(R.id.text_input_layout_schedule_prefs);
    textInputLayoutRepeat = findViewById(R.id.text_input_layout_repeat);
    textInputLayoutStartsAfter = findViewById(R.id.text_input_layout_starts_after);
    textInputLayoutParent = findViewById(R.id.text_input_layout_parent);
    textInputLayoutNotes = findViewById(R.id.text_input_layout_notes);

    //Todo: Initialize via layout to pass along appropriate styling from layout
    textInputEditTextTitle = findViewById(R.id.edit_text_title);
    textInputEditTextStartDateTime = findViewById(R.id.edit_text_start_date_time);
    textInputEditTextDuration = findViewById(R.id.edit_text_duration);
    textInputEditTextEndDateTime = findViewById(R.id.edit_text_end_date_time);
    textInputEditTextSchedulePrefs = findViewById(R.id.edit_text_schedule_prefs);
    textInputEditTextRepeat = findViewById(R.id.edit_text_repeat);
    textInputEditTextStartsAfter = findViewById(R.id.edit_text_starts_after);
    textInputEditTextParent = findViewById(R.id.edit_text_parent);
    textInputEditTextNotes = findViewById(R.id.edit_text_notes);

  }

  private void attachViewListeners() {
    textInputEditTextTitle.setOnFocusChangeListener(this::onFocusChange);
    textInputEditTextStartDateTime.setOnFocusChangeListener(this::onFocusChange);
    textInputEditTextDuration.setOnFocusChangeListener(this::onFocusChange);
    textInputEditTextEndDateTime.setOnFocusChangeListener(this::onFocusChange);
    textInputEditTextSchedulePrefs.setOnFocusChangeListener(this::onFocusChange);
    textInputEditTextRepeat.setOnFocusChangeListener(this::onFocusChange);
    textInputEditTextStartsAfter.setOnFocusChangeListener(this::onFocusChange);
    textInputEditTextParent.setOnFocusChangeListener(this::onFocusChange);
    textInputEditTextNotes.setOnFocusChangeListener(this::onFocusChange);

    textInputLayoutTitle.setEndIconOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Log.d(TAG, "endIcon clicked for title");
        title = "";
        textInputEditTextTitle.setText("");
        saveChangedFields();
      }
    });
    textInputLayoutStartDateTime.setEndIconOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Log.d(TAG, "endIcon clicked for startDateTime");
        startDateTime = null;
        textInputEditTextStartDateTime.setText("");
        saveChangedFields();
      }
    });
    textInputLayoutDuration.setEndIconOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Log.d(TAG, "endIcon clicked for duration");
        duration = 0;
        textInputEditTextDuration.setText("");
        saveChangedFields();
      }
    });
    textInputLayoutEndDateTime.setEndIconOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Log.d(TAG, "endIcon clicked for endDateTime");
        endDateTime = null;
        textInputEditTextEndDateTime.setText("");
        saveChangedFields();
      }
    });
    textInputLayoutSchedulePrefs.setEndIconOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Log.d(TAG, "endIcon clicked for schedule prefs");
        //Todo: endDateTime = null;
        textInputEditTextSchedulePrefs.setText("");
        saveChangedFields();
      }
    });
    textInputLayoutRepeat.setEndIconOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Log.d(TAG, "endIcon clicked for repeat");
        //Todo: endDateTime = null;
        textInputEditTextRepeat.setText("");
        saveChangedFields();
      }
    });
    textInputLayoutParent.setEndIconOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Log.d(TAG, "endIcon clicked for parent");
        //Todo: endDateTime = null;
        textInputEditTextParent.setText("");
        saveChangedFields();
      }
    });
    textInputLayoutNotes.setEndIconOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Log.d(TAG, "endIcon clicked for notes");
        notes = "";
        textInputEditTextNotes.setText("");
        saveChangedFields();
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
        parentUuid = bundle.getString(Constants.EXTRA_ENTRY_ID);
        saveChangedFields();
      }
    }
  }


  private void initializeEditActivityWith(ViewModelEntry viewModelEntry) {
    Log.d(TAG, "initializeEditActivityWith viewModelEntry " + viewModelEntry.getTitle() + "called");
    title = viewModelEntry.getTitle();
    duration = viewModelEntry.getDuration();
    startDateTime = viewModelEntry.getStartDateTime();
    endDateTime = viewModelEntry.getEndDateTime();
    notes = viewModelEntry.getNotes();
    //Todo: implement schedule-text in viewModelEntry

    textInputEditTextTitle.setText(title);
    if (startDateTime != null) {
      textInputEditTextStartDateTime.setText(TimeHelper.format(startDateTime));
    }
    textInputEditTextDuration.setText(TimeHelper.getDurationStringFrom(duration));
    if (endDateTime != null) {
      textInputEditTextEndDateTime.setText(TimeHelper.format(endDateTime));
    }
    textInputEditTextNotes.setText(notes);

    lastSavedEventSequenceNumber = viewModelEntry.getLastSavedEventSequenceNumber();
    return;
  }


  @Override
  public void onFocusChange(View view, boolean b) {
    switch (view.getId()) {
      case R.id.edit_text_start_date_time:
        if (textInputEditTextStartDateTime.hasFocus()) {
          Log.d(TAG, "startDateTime clicked");
          hideSoftKeyboard();
          FTimePickerDialog fTimePickerDialog = new FTimePickerDialog("startTime");
          fTimePickerDialog.show(getSupportFragmentManager(), "timePicker");
          FDatePickerDialog fDatePickerDialog = new FDatePickerDialog("startDate");
          fDatePickerDialog.show(getSupportFragmentManager(), "datePicker");
        }
        break;
      case R.id.edit_text_duration:
        if (textInputEditTextDuration.hasFocus()) {
          Log.d(TAG, "duration clicked");
          hideSoftKeyboard();
          DurationPickerDialog durationPickerDialog = new DurationPickerDialog();
          durationPickerDialog.show(getSupportFragmentManager(), "testDialog");
        }
        break;
      case R.id.edit_text_end_date_time:
        if (textInputEditTextEndDateTime.hasFocus()) {
          Log.d(TAG, "endDateTime clicked");
          hideSoftKeyboard();
          FTimePickerDialog fTimePickerDialog = new FTimePickerDialog("endTime");
          fTimePickerDialog.show(getSupportFragmentManager(), "timePicker");
          FDatePickerDialog fDatePickerDialog = new FDatePickerDialog("endDate");
          fDatePickerDialog.show(getSupportFragmentManager(), "datePicker");
        }
        break;
      case R.id.edit_text_schedule_prefs:
        if (textInputEditTextSchedulePrefs.hasFocus()) {
          Log.d(TAG, "schedule clicked");
          hideSoftKeyboard();
          if ((duration > 0 //it only makes sense to add constraints when not already constrained to a fixed date-time
              && startDateTime != null
              && endDateTime != null
              && startDateTime.plusSeconds(duration).toEpochSecond() != endDateTime.toEpochSecond())
              || (
              duration > 0
                  && startDateTime == null
                  && endDateTime != null
                  && OffsetDateTime.now().plusSeconds(duration).toEpochSecond() != endDateTime
                  .toEpochSecond()
          )
          ) {
            ConstraintPickerDialog constraintPickerDialog = new ConstraintPickerDialog();
            constraintPickerDialog.show(getSupportFragmentManager(), "constraintPicker");
          } else {
            Toast.makeText(AddEditEntryActivity.this, "Increase flexibility between start and end date-times.", Toast.LENGTH_LONG).show();
          }
          //Todo: schedule entry
          textInputEditTextSchedulePrefs.clearFocus();
        }
        break;
      default:
        break;
    }
    saveChangedFields();
  }

  @Override
  public void onDialogPositiveClick(String input, String inputType) {
    switch (inputType) {
      case "startDate":
        startDateTime = TimeHelper.getDateFromString(input);
        textInputEditTextStartDateTime.setText(TimeHelper.format(startDateTime));
        hideSoftKeyboard();
        textInputEditTextStartDateTime.clearFocus();
        break;
      case "startTime":
        startDateTime = startDateTime.plusSeconds(Integer.valueOf(input));
        textInputEditTextStartDateTime.setText(TimeHelper.format(startDateTime));
        hideSoftKeyboard();
        saveChangedFields();
        break;
      case "duration":
        duration = Long.valueOf(input);
        textInputEditTextDuration.setText(TimeHelper.getDurationStringFrom(duration));
        if (duration != 0 && startDateTime != null) {
          if (endDateTime == null ||
              (endDateTime != null && duration >
                  (endDateTime.toEpochSecond() - startDateTime.toEpochSecond()))
          ) {
            endDateTime = startDateTime.plusSeconds(duration);
            textInputEditTextEndDateTime.setText(TimeHelper.format(endDateTime));
          }
        }
        textInputLayoutDuration.setEndIconVisible(false);
        textInputEditTextDuration.clearFocus();
        saveChangedFields();
        break;
      case "endDate":
        endDateTime = TimeHelper.getDateFromString(input);
        textInputEditTextEndDateTime.setText(TimeHelper.format(endDateTime));
        hideSoftKeyboard();
        textInputEditTextEndDateTime.clearFocus();
        break;
      case "endTime":
        endDateTime = endDateTime.plusSeconds(Integer.valueOf(input));
        textInputEditTextEndDateTime.setText(TimeHelper.format(endDateTime));
        if (startDateTime != null && duration == 0) {
          duration = endDateTime.toEpochSecond() - startDateTime.toEpochSecond();
          textInputEditTextDuration.setText(TimeHelper.getDurationStringFrom(duration));
        }
        hideSoftKeyboard();
        saveChangedFields(); //Todo: when choosing date + time in two separate dialogs the date gets saved first with the old time => issue?
        break;
      default:
        break;
    }
  }

  @Override
  public void onConstraintsAdded(List<DtrConstraint> dtrConstraints) {
    this.dtrConstraints = dtrConstraints;
    //Todo: try to schedule Entry to Resource given the constraints
  }

  public void hideSoftKeyboard() {
    if (getCurrentFocus() != null) {
      InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(
          INPUT_METHOD_SERVICE);
      inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }
  }

  public void showSoftKeyboard(View view) {
    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(
        INPUT_METHOD_SERVICE);
    view.requestFocus();
    inputMethodManager.showSoftInput(view, 0);
  }

}
