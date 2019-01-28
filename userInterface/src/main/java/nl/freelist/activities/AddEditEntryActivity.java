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
import android.widget.NumberPicker.Formatter;
import android.widget.Switch;
import io.reactivex.schedulers.Schedulers;
import java.util.UUID;
import nl.freelist.androidCrossCuttingConcerns.MySettings;
import nl.freelist.data.EntryRepository;
import nl.freelist.data.dto.ViewModelEntry;
import nl.freelist.domain.commands.ChangeEntryTitleCommand;
import nl.freelist.domain.commands.CreateEntryCommand;
import nl.freelist.domain.crossCuttingConcerns.Constants;
import nl.freelist.domain.entities.Entry;
import nl.freelist.domain.interfaces.Repository;
import nl.freelist.freelist.R;
import nl.freelist.viewModelPerActivity.AddEditEntryActivityViewModel;
import nl.freelist.views.NumberPickerDuration;

public class AddEditEntryActivity extends AppCompatActivity {

  private static final String TAG = "AddEditEntryActivity";

  private String uuid; //Todo: why ever store a UUID as a string, if not in data persistence layer?
  private String parentUuid;
  private String defaultUuid;
  private int lastSavedEventSequenceNumber = -1;

  private Repository<Entry> entryRepository;

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

    entryRepository = new EntryRepository(this.getApplicationContext());

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

      CreateEntryCommand createEntryCommand =
          new CreateEntryCommand(defaultUuid, parentUuid, uuid, entryRepository);
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
    copyMoveSwitch = findViewById(R.id.copy_move_switch);

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
                    new ChangeEntryTitleCommand(uuid, textOnFocusGained,
                        editTextTitle.getText().toString(), lastSavedEventSequenceNumber,
                        entryRepository);
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
      if (!parentUuid.equals(bundle.getString(Constants.EXTRA_ENTRY_ID))) {
        parentUuid = bundle.getString(Constants.EXTRA_ENTRY_ID);
        copyMoveSwitch.setVisibility(View.VISIBLE);
      }

      if (parentUuid.equals(
          UUID.nameUUIDFromBytes("tijl.leenders@gmail.com".getBytes()).toString())) {
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

    if (parentUuid.equals(
        UUID.nameUUIDFromBytes("tijl.leenders@gmail.com".getBytes()).toString())) {
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
    lastSavedEventSequenceNumber = viewModelEntry.getLastSavedEventSequenceNumber();
    return;
  }
}
