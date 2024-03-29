package nl.freelist.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import io.reactivex.schedulers.Schedulers;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import nl.freelist.androidCrossCuttingConcerns.MySettings;
import nl.freelist.commands.SaveEntryCommand;
import nl.freelist.data.Repository;
import nl.freelist.data.dto.ViewModelEntry;
import nl.freelist.dialogs.ConstraintPickerDialog;
import nl.freelist.dialogs.DurationPickerDialog;
import nl.freelist.dialogs.FDatePickerDialog;
import nl.freelist.dialogs.FTimePickerDialog;
import nl.freelist.dialogs.NoticeDialogListener;
import nl.freelist.domain.crossCuttingConcerns.Constants;
import nl.freelist.domain.crossCuttingConcerns.ThreadLogger;
import nl.freelist.domain.crossCuttingConcerns.TimeHelper;
import nl.freelist.domain.valueObjects.constraints.Constraint;
import nl.freelist.domain.valueObjects.constraints.ImpossibleDaysConstraint;
import nl.freelist.freelist.R;
import nl.freelist.viewModelPerActivity.AddEditEntryActivityViewModel;

public class AddEditEntryActivity extends AppCompatActivity
    implements OnFocusChangeListener, NoticeDialogListener {

  private static final String TAG = "AddEditEntryActivity";

  private String entryId;
  private String parentId;
  private String personId;
  private int lastSavedEventSequenceNumber = -1;
  private int saveCommandsInProgress = 0;
  private Boolean isBrandNewEntry = true;

  private Repository repository;

  SimpleDateFormat simpleDateFormat = new SimpleDateFormat();

  private String title = "";
  private String scheduledStatus = "";
  private OffsetDateTime startAtOrAfterDateTime;
  private OffsetDateTime scheduledStartDateTime = null;
  private long duration = 0;
  private OffsetDateTime finishAtOrBeforeDateTime;
  private OffsetDateTime scheduledEndDateTime = null;
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
  private TextView textViewScheduledStatus;
  private TextInputEditText textInputEditTextStartDateTime;
  private TextInputEditText textInputEditTextDuration;
  private TextInputEditText textInputEditTextEndDateTime;
  private TextInputEditText textInputEditTextSchedulePrefs;
  private TextInputEditText textInputEditTextRepeat;
  private TextInputEditText textInputEditTextStartsAfter;
  private TextInputEditText textInputEditTextParent;
  private TextInputEditText textInputEditTextNotes;

  private Bundle preferredDaysConstraintsCheckBoxStatesBundle = new Bundle();
  private Bundle durationBundle = new Bundle();
  private List<ImpossibleDaysConstraint> impossibleDaysConstraints = new ArrayList<>();

  private nl.freelist.viewModelPerActivity.AddEditEntryActivityViewModel
      AddEditEntryActivityViewModel;

  @Override
  protected void onResume() {
    Log.d(TAG, "onResume called.");
    // Todo: do something with bundle from ChooseCalendarOptionActivity

    super.onResume();
    if (isBrandNewEntry) {
      textInputLayoutTitle.requestFocus();
    }
  }

  @Override
  protected void onPause() {
    Log.d(TAG, "onPause");
    saveChangedFields();
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
    parentId = personId = mySettings.getId();

    preferredDaysConstraintsCheckBoxStatesBundle.putBoolean("allowMondays", true);
    preferredDaysConstraintsCheckBoxStatesBundle.putBoolean("allowTuesdays", true);
    preferredDaysConstraintsCheckBoxStatesBundle.putBoolean("allowWednesdays", true);
    preferredDaysConstraintsCheckBoxStatesBundle.putBoolean("allowThursdays", true);
    preferredDaysConstraintsCheckBoxStatesBundle.putBoolean("allowFridays", true);
    preferredDaysConstraintsCheckBoxStatesBundle.putBoolean("allowSaturdays", true);
    preferredDaysConstraintsCheckBoxStatesBundle.putBoolean("allowSundays", true);
    preferredDaysConstraintsCheckBoxStatesBundle.putBoolean("allowMornings", true);
    preferredDaysConstraintsCheckBoxStatesBundle.putBoolean("allowAfternoons", true);
    preferredDaysConstraintsCheckBoxStatesBundle.putBoolean("allowEvenings", true);
    preferredDaysConstraintsCheckBoxStatesBundle.putBoolean("allowNights", true);

    durationBundle.putLong("duration", duration);

    if (bundle != null && bundle.containsKey(Constants.EXTRA_ENTRY_PARENT_ID)) {
      parentId = bundle.getString(Constants.EXTRA_ENTRY_PARENT_ID);
    }

    if (bundle != null && bundle.containsKey(Constants.EXTRA_REQUEST_TYPE_EDIT)) { // do edit setup
      entryId = bundle.getString(Constants.EXTRA_ENTRY_ID);
      initializeForEditExisting(entryId);
      isBrandNewEntry = false;
    } else if (bundle != null && bundle.containsKey(Constants.EXTRA_REQUEST_TYPE_ADD)) { // do add
      // setup
      initializeForAddNew(bundle);
    }

    attachViewListeners();
  }

  private void saveChangedFields() {

    title = textInputEditTextTitle.getText().toString();
    notes = textInputEditTextNotes.getText().toString();
    Log.d(
        TAG,
        "SaveEntryCommand"
            + saveCommandsInProgress
            + " with eventSequenceNumber "
            + lastSavedEventSequenceNumber);

    SaveEntryCommand saveEntryCommand = // Todo: add parent
        new SaveEntryCommand(
            entryId,
            parentId,
            personId,
            title,
            startAtOrAfterDateTime,
            duration,
            finishAtOrBeforeDateTime,
            notes,
            impossibleDaysConstraints,
            lastSavedEventSequenceNumber,
            repository);
    saveCommandsInProgress += 1;
    //    Toast.makeText(AddEditEntryActivity.this, "Saving...", Toast.LENGTH_SHORT).show();
    AddEditEntryActivityViewModel.handle(saveEntryCommand)
        .subscribeOn(
            Schedulers.single()) // schedules all save commands sequentially in a single thread
        .observeOn(Schedulers.single())
        .subscribe(
            (result -> {
              // update View
              ThreadLogger.logThreadSignature(
                  "ThreadLogger: Inside saveEntryCommand observe " + lastSavedEventSequenceNumber);
              runOnUiThread(
                  new Runnable() {
                    @Override
                    public void run() {
                      ThreadLogger.logThreadSignature(
                          "ThreadLogger: Inside saveEntryCommand observe runOnUI. "
                              + lastSavedEventSequenceNumber);
                      saveCommandsInProgress -= 1;
                      if (result != null && !result.isSuccess()) {
                        Toast.makeText(
                                AddEditEntryActivity.this,
                                "Sorry! SaveEntry failed!",
                            Toast.LENGTH_LONG)
                            .show();
                      }
                      if (result != null && result.isSuccess()) {
                        initializeForEditExisting(entryId);
                      }
                    }
                  });
            }));
  }

  private void initializeForAddNew(Bundle bundle) {
    if (bundle.containsKey(Constants.EXTRA_ENTRY_PARENT_ID)) {
      entryId = UUID.randomUUID().toString();
      if (bundle.containsKey(Constants.EXTRA_SCHEDULER_EVENT_SEQUENCE_NUMBER)) {
        lastSavedEventSequenceNumber =
            bundle.getInt(Constants.EXTRA_SCHEDULER_EVENT_SEQUENCE_NUMBER);
      }
    }

    setTitle("Add new Freelist");
    // Todo: impossibleDaysConstraints from Settings
    //    impossibleDaysConstraints.add(ImpossibleTimeOfDayConstraint.Create("NOEVENINGS"));
    //    impossibleDaysConstraints.add(ImpossibleTimeOfDayConstraint.Create("NONIGHTS"));
  }

  private void initializeForEditExisting(String uuid) {

    AddEditEntryActivityViewModel.getViewModelEntry(uuid)
        .subscribeOn(Schedulers.single())
        .observeOn(Schedulers.single())
        .subscribe(
            (viewModelEntry -> {
              // update View
              runOnUiThread(
                  new Runnable() {
                    @Override
                    public void run() {
                      ThreadLogger.logThreadSignature(
                          "ThreadLogger: Inside refresh " + lastSavedEventSequenceNumber);
                      updateAddEditEntryActivityWith(viewModelEntry);
                    }
                  });
              // Todo: setup action if fails
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

    // Todo: Initialize via layout to pass along appropriate styling from layout
    textInputEditTextTitle = findViewById(R.id.edit_text_title);
    textViewScheduledStatus = findViewById(R.id.schedule_status);
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

    textInputLayoutTitle.setEndIconOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            Log.d(TAG, "endIcon clicked for title");
            title = "";
            textInputEditTextTitle.setText("");
            saveChangedFields();
          }
        });
    textInputLayoutStartDateTime.setEndIconOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            Log.d(TAG, "endIcon clicked for startDateTime");
            startAtOrAfterDateTime = null;
            textInputEditTextStartDateTime.setText("");
            saveChangedFields();
          }
        });
    textInputLayoutDuration.setEndIconOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            Log.d(TAG, "endIcon clicked for duration");
            duration = 0;
            textInputEditTextDuration.setText("");
            saveChangedFields();
          }
        });
    textInputLayoutEndDateTime.setEndIconOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            Log.d(TAG, "endIcon clicked for endDateTime");
            finishAtOrBeforeDateTime = null;
            textInputEditTextEndDateTime.setText("");
            saveChangedFields();
          }
        });
    textInputLayoutSchedulePrefs.setEndIconOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            Log.d(TAG, "endIcon clicked for schedule prefs");
            // Todo: endDateTime = null;
            textInputEditTextSchedulePrefs.setText("");
            saveChangedFields();
          }
        });
    textInputLayoutRepeat.setEndIconOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            Log.d(TAG, "endIcon clicked for repeat");
            // Todo: endDateTime = null;
            textInputEditTextRepeat.setText("");
            saveChangedFields();
          }
        });
    textInputLayoutParent.setEndIconOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            Log.d(TAG, "endIcon clicked for parent");
            // Todo: endDateTime = null;
            textInputEditTextParent.setText("");
            saveChangedFields();
          }
        });
    textInputLayoutNotes.setEndIconOnClickListener(
        new View.OnClickListener() {
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
      if (bundle != null && !parentId.equals(bundle.getString(Constants.EXTRA_ENTRY_ID))) {
        parentId = bundle.getString(Constants.EXTRA_ENTRY_ID);
        saveChangedFields();
      }
    }
  }

  private void updateAddEditEntryActivityWith(ViewModelEntry viewModelEntry) {
    Log.d(TAG, "updateAddEditEntryActivityWith viewModelEntry " + viewModelEntry.getTitle());
    title = viewModelEntry.getTitle();
    duration = viewModelEntry.getDuration();
    durationBundle.putLong("duration", duration);
    startAtOrAfterDateTime = viewModelEntry.getStartAtOrAfterDateTime();
    scheduledStartDateTime = viewModelEntry.getScheduledStartDateTime();
    finishAtOrBeforeDateTime = viewModelEntry.getFinishAtOrBeforeDateTime();
    scheduledEndDateTime = viewModelEntry.getScheduledEndDateTime();
    notes = viewModelEntry.getNotes();
    //    scheduledStatus = viewModelEntry.getScheduledStatus();
    if (duration == 0) {
      textViewScheduledStatus.setText("Not scheduled yet... Please add a duration.");
    }

    if (duration > 0 && scheduledStartDateTime == null) {
      textViewScheduledStatus.setText("Scheduling...");
    }

    if (duration > 0 && scheduledStartDateTime != null) {
      textViewScheduledStatus.setText("Scheduled to start " + scheduledStartDateTime.toString());
    }

    impossibleDaysConstraints = viewModelEntry.getImpossibleDaysConstraints();

    if (impossibleDaysConstraints != null) {
      Log.d(TAG, "check: viewModel updated: " + impossibleDaysConstraints.toString());
      updateCheckBoxStatesBundleFromPreferredDaysConstraints();
    } else {
      Log.d(TAG, "check: viewModel updated: impossibleDaysConstraints null");
    }

    textInputEditTextTitle.setText(title);
    if (startAtOrAfterDateTime != null) {
      textInputEditTextStartDateTime.setText(TimeHelper.formatForDateTime(startAtOrAfterDateTime));
    }
    textInputEditTextDuration.setText(TimeHelper.getDurationStringFrom(duration));
    if (finishAtOrBeforeDateTime != null) {
      textInputEditTextEndDateTime.setText(TimeHelper.formatForDateTime(finishAtOrBeforeDateTime));
    }
    textInputEditTextNotes.setText(notes);

    lastSavedEventSequenceNumber = viewModelEntry.getLastAppliedSchedulerSequenceNumber();

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
          saveChangedFields();
        }
        break;
      case R.id.edit_text_duration:
        if (textInputEditTextDuration.hasFocus()) {
          Log.d(TAG, "duration clicked");
          hideSoftKeyboard();
          DurationPickerDialog durationPickerDialog = DurationPickerDialog.Create(durationBundle);
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
          saveChangedFields();
        }
        break;
      case R.id.edit_text_schedule_prefs:
        if (textInputEditTextSchedulePrefs.hasFocus()) {
          Log.d(TAG, "schedule clicked");
          hideSoftKeyboard();
          if ((duration
                      > 0 // it only makes sense to add constraints when not already constrained to
                  // a fixed date-time
              && startAtOrAfterDateTime != null
              && finishAtOrBeforeDateTime != null
              && startAtOrAfterDateTime.plusSeconds(duration).toEpochSecond()
              != finishAtOrBeforeDateTime.toEpochSecond())
              || (duration > 0
              && startAtOrAfterDateTime == null
              && finishAtOrBeforeDateTime != null
              && OffsetDateTime.now(ZoneOffset.UTC)
              .truncatedTo(ChronoUnit.SECONDS)
              .plusSeconds(duration)
              .toEpochSecond()
              != finishAtOrBeforeDateTime.toEpochSecond())
              || (duration > 0 && finishAtOrBeforeDateTime == null)) {
            ConstraintPickerDialog constraintPickerDialog =
                ConstraintPickerDialog.Create(preferredDaysConstraintsCheckBoxStatesBundle);
            constraintPickerDialog.show(getSupportFragmentManager(), "constraintPicker");
          } else {
            // Todo: change as Toast is also not the preferred way of showing/telling the user
            // something?
            // Also, it doesn't show as it gets overridden by saveCommandInProgress Toast
            ConstraintPickerDialog constraintPickerDialog =
                ConstraintPickerDialog.Create(preferredDaysConstraintsCheckBoxStatesBundle);
            constraintPickerDialog.show(getSupportFragmentManager(), "constraintPicker");
            //            Toast.makeText(
            //                    AddEditEntryActivity.this.getBaseContext(),
            //                    "Increase flexibility between start and end date-times.",
            //                    Toast.LENGTH_SHORT)
            //                .show();
          }
          // Todo: schedule entry
          textInputEditTextSchedulePrefs.clearFocus();
        }
        break;
      default:
        break;
    }
  }

  private void updatePreferredDaysConstraintsFromCheckBoxStatesBundle() {
    impossibleDaysConstraints.clear();
    for (String key : preferredDaysConstraintsCheckBoxStatesBundle.keySet()) {
      switch (key) {
        case "allowMondays":
          if (preferredDaysConstraintsCheckBoxStatesBundle.getBoolean(key) == false) {
            // == false easier to read than !
            impossibleDaysConstraints.add(ImpossibleDaysConstraint.Create(DayOfWeek.MONDAY));
          }
          break;
        case "allowTuesdays":
          if (preferredDaysConstraintsCheckBoxStatesBundle.getBoolean(key) == false) {
            impossibleDaysConstraints.add(ImpossibleDaysConstraint.Create(DayOfWeek.TUESDAY));
          }
          break;
        case "allowWednesdays":
          if (preferredDaysConstraintsCheckBoxStatesBundle.getBoolean(key) == false) {
            impossibleDaysConstraints.add(ImpossibleDaysConstraint.Create(DayOfWeek.WEDNESDAY));
          }
          break;
        case "allowThursdays":
          if (preferredDaysConstraintsCheckBoxStatesBundle.getBoolean(key) == false) {
            impossibleDaysConstraints.add(ImpossibleDaysConstraint.Create(DayOfWeek.THURSDAY));
          }
          break;
        case "allowFridays":
          if (preferredDaysConstraintsCheckBoxStatesBundle.getBoolean(key) == false) {
            impossibleDaysConstraints.add(ImpossibleDaysConstraint.Create(DayOfWeek.FRIDAY));
          }
          break;
        case "allowSaturdays":
          if (preferredDaysConstraintsCheckBoxStatesBundle.getBoolean(key) == false) {
            impossibleDaysConstraints.add(ImpossibleDaysConstraint.Create(DayOfWeek.SATURDAY));
          }
          break;
        case "allowSundays":
          if (preferredDaysConstraintsCheckBoxStatesBundle.getBoolean(key) == false) {
            impossibleDaysConstraints.add(ImpossibleDaysConstraint.Create(DayOfWeek.SUNDAY));
          }
          break;
        //        case "allowMornings":
        //          if (preferredDaysConstraintsCheckBoxStatesBundle.getBoolean(key) == false) {
        //
        // impossibleDaysConstraints.add(ImpossibleTimeOfDayConstraint.Create("NOMORNINGS"));
        //          }
        //          break;
        //        case "allowAfternoons":
        //          if (preferredDaysConstraintsCheckBoxStatesBundle.getBoolean(key) == false) {
        //
        // impossibleDaysConstraints.add(ImpossibleTimeOfDayConstraint.Create("NOAFTERNOONS"));
        //          }
        //          break;
        //        case "allowEvenings":
        //          if (preferredDaysConstraintsCheckBoxStatesBundle.getBoolean(key) == false) {
        //
        // impossibleDaysConstraints.add(ImpossibleTimeOfDayConstraint.Create("NOEVENINGS"));
        //          }
        //          break;
        //        case "allowNights":
        //          if (preferredDaysConstraintsCheckBoxStatesBundle.getBoolean(key) == false) {
        //
        // impossibleDaysConstraints.add(ImpossibleTimeOfDayConstraint.Create("NONIGHTS"));
        //          }
        //          break;

        default:
          break;
      }
    }
    return;
  }

  private void updateCheckBoxStatesBundleFromPreferredDaysConstraints() {
    for (Constraint impossibleDaysConstraint : impossibleDaysConstraints) {

      switch (impossibleDaysConstraint.toString()) {
        case "NOMONDAYS":
          preferredDaysConstraintsCheckBoxStatesBundle.putBoolean("allowMondays", false);
          break;
        case "NOTUESDAYS":
          preferredDaysConstraintsCheckBoxStatesBundle.putBoolean("allowTuesdays", false);
          break;
        case "NOWEDNESDAYS":
          preferredDaysConstraintsCheckBoxStatesBundle.putBoolean("allowWednesdays", false);
          break;
        case "NOTHURSDAYS":
          preferredDaysConstraintsCheckBoxStatesBundle.putBoolean("allowThursdays", false);
          break;
        case "NOFRIDAYS":
          preferredDaysConstraintsCheckBoxStatesBundle.putBoolean("allowFridays", false);
          break;
        case "NOSATURDAYS":
          preferredDaysConstraintsCheckBoxStatesBundle.putBoolean("allowSaturdays", false);
          break;
        case "NOSUNDAYS":
          preferredDaysConstraintsCheckBoxStatesBundle.putBoolean("allowSundays", false);
          break;
        case "NOMORNINGS":
          preferredDaysConstraintsCheckBoxStatesBundle.putBoolean("allowMornings", false);
          break;
        case "NOAFTERNOONS":
          preferredDaysConstraintsCheckBoxStatesBundle.putBoolean("allowAfternoons", false);
          break;
        case "NOEVENINGS":
          preferredDaysConstraintsCheckBoxStatesBundle.putBoolean("allowEvenings", false);
          break;
        case "NONIGHTS":
          preferredDaysConstraintsCheckBoxStatesBundle.putBoolean("allowNights", false);
          break;
        default:
          break;
      }
    }
    return;
  }

  @Override
  public void onPreferredDaysChange(Bundle checkBoxStates) {
    preferredDaysConstraintsCheckBoxStatesBundle = checkBoxStates;
    updatePreferredDaysConstraintsFromCheckBoxStatesBundle();
    Log.d(TAG, "check: " + impossibleDaysConstraints.toString());
    saveChangedFields();
  }

  @Override
  public void onDialogFeedback(String input, String inputType) {
    switch (inputType) {
      case "startDate":
        startAtOrAfterDateTime = TimeHelper.getDateFromString(input);
        textInputEditTextStartDateTime
            .setText(TimeHelper.formatForDateTime(startAtOrAfterDateTime));
        hideSoftKeyboard();
        textInputEditTextStartDateTime.clearFocus();
        break;
      case "startTime":
        startAtOrAfterDateTime = startAtOrAfterDateTime.plusSeconds(Integer.valueOf(input));
        textInputEditTextStartDateTime
            .setText(TimeHelper.formatForDateTime(startAtOrAfterDateTime));
        hideSoftKeyboard();
        saveChangedFields();
        break;
      case "duration":
        duration = Long.valueOf(input);
        textInputEditTextDuration.setText(TimeHelper.getDurationStringFrom(duration));
        if (duration != 0 && startAtOrAfterDateTime != null) {
          if (finishAtOrBeforeDateTime == null
              || (finishAtOrBeforeDateTime != null
              && duration
              > (finishAtOrBeforeDateTime.toEpochSecond()
              - startAtOrAfterDateTime.toEpochSecond()))) {
            finishAtOrBeforeDateTime = startAtOrAfterDateTime.plusSeconds(duration);
            textInputEditTextEndDateTime
                .setText(TimeHelper.formatForDateTime(finishAtOrBeforeDateTime));
          }
        }
        textInputLayoutDuration.setEndIconVisible(false);
        textInputEditTextDuration.clearFocus();
        saveChangedFields();
        break;
      case "endDate":
        finishAtOrBeforeDateTime = TimeHelper.getDateFromString(input);
        textInputEditTextEndDateTime
            .setText(TimeHelper.formatForDateTime(finishAtOrBeforeDateTime));
        hideSoftKeyboard();
        textInputEditTextEndDateTime.clearFocus();
        break;
      case "endTime":
        finishAtOrBeforeDateTime = finishAtOrBeforeDateTime.plusSeconds(Integer.valueOf(input));
        textInputEditTextEndDateTime
            .setText(TimeHelper.formatForDateTime(finishAtOrBeforeDateTime));
        if (startAtOrAfterDateTime != null && duration == 0) {
          duration =
              finishAtOrBeforeDateTime.toEpochSecond() - startAtOrAfterDateTime.toEpochSecond();
          textInputEditTextDuration.setText(TimeHelper.getDurationStringFrom(duration));
        }
        hideSoftKeyboard();
        saveChangedFields(); // Todo: when choosing date + time in two separate dialogs the date
        // gets saved first with the old time => issue?
        break;
      default:
        break;
    }
  }

  public void hideSoftKeyboard() {
    if (getCurrentFocus() != null) {
      InputMethodManager inputMethodManager =
          (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
      inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }
  }

  public void showSoftKeyboard(View view) {
    InputMethodManager inputMethodManager =
        (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
    view.requestFocus();
    inputMethodManager.showSoftInput(view, 0);
  }
}
