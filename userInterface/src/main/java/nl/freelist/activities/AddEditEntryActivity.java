package nl.freelist.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import io.reactivex.schedulers.Schedulers;
import java.util.UUID;
import nl.freelist.androidCrossCuttingConcerns.MySettings;
import nl.freelist.commands.ChangeEntryDescriptionCommand;
import nl.freelist.commands.ChangeEntryParentCommand;
import nl.freelist.commands.ChangeEntryTitleCommand;
import nl.freelist.commands.CreateEntryCommand;
import nl.freelist.data.Repository;
import nl.freelist.data.dto.ViewModelEntry;
import nl.freelist.domain.crossCuttingConcerns.Constants;
import nl.freelist.freelist.R;
import nl.freelist.viewModelPerActivity.AddEditEntryActivityViewModel;

public class AddEditEntryActivity extends AppCompatActivity {

  private static final String TAG = "AddEditEntryActivity";

  private String uuid; // Todo: why ever store a UUID as a string, if not in data persistence layer?
  private String parentUuid;
  private String defaultUuid;
  private int lastSavedEventSequenceNumber = -1;

  private Repository repository;

  private EditText editTextTitle;
  private EditText editTextDescription;
  private Button scheduleButton;

  // Todo: connect
  private EditText yearPicker;
  private EditText weekPicker;
  private EditText dayPicker;
  private EditText hourPicker;
  private EditText minutePicker;
  private EditText secondPicker;

  private nl.freelist.viewModelPerActivity.AddEditEntryActivityViewModel
      AddEditEntryActivityViewModel;


  @Override
  protected void onResume() {
    Log.d(TAG, "onResume called.");
    //Todo: do something with bundle from ChooseCalendarOptionActivity

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

    attachViewListeners();
  }


  private void initializeForAddNew(Bundle bundle) {
    if (bundle.containsKey(Constants.EXTRA_ENTRY_PARENT_ID)) {
      initializeParentButtonWithUuid(parentUuid);
      uuid = UUID.randomUUID().toString();
    }
    setTitle("Add new Freelist");
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

    setTitle("Edit existing Freelist");
  }

  private void initializeViews() {
    editTextTitle = findViewById(R.id.edit_text_title);
    editTextDescription = findViewById(R.id.edit_text_notes);

    scheduleButton = findViewById(R.id.schedule_button);

    //Todo: initialize editTexts for duration
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
                    .subscribe((result -> {
                      // update View
                      runOnUiThread(
                          new Runnable() {
                            @Override
                            public void run() {
                              if (!result.isSuccess()) {
                                Toast.makeText(AddEditEntryActivity.this,
                                    "Sorry! Title change failed!", Toast.LENGTH_SHORT)
                                    .show();
                              }
                            }
                          });
                    }));
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
                    .subscribe(
                        (result -> {
                          // update View
                          runOnUiThread(
                              new Runnable() {
                                @Override
                                public void run() {
                                  if (!result.isSuccess()) {
                                    Toast.makeText(AddEditEntryActivity.this,
                                        "Sorry! Change description failed!", Toast.LENGTH_SHORT)
                                        .show();
                                  }
                                }
                              });
                        })
                    );
              }
            }
          }
        };
    editTextDescription.setOnFocusChangeListener(editTextDescriptionOnFocusChangeListener);

    attachScheduleButtonListener();
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
            .subscribe(
                (result -> {
                  // update View
                  runOnUiThread(
                      new Runnable() {
                        @Override
                        public void run() {
                          if (!result.isSuccess()) {
                            Toast
                                .makeText(AddEditEntryActivity.this, "Sorry! Change parent failed!",
                                    Toast.LENGTH_SHORT)
                                .show();
                          }
                        }
                      });
                })
            );
        parentUuid = bundle.getString(Constants.EXTRA_ENTRY_ID);
        setParentButtonText();
      }
    }
  }

  private void setParentButtonText() {
  }

  private void initializeParentButtonWithUuid(String parentUuid) {
  }

  private void attachScheduleButtonListener() {
    scheduleButton.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            Log.d(TAG, "scheduleButton clicked for entry ..." + uuid);
            Intent intent = new Intent(AddEditEntryActivity.this,
                ChooseCalendarOptionActivity.class);
            intent.putExtra(
                Constants.EXTRA_ENTRY_ID, uuid);
            intent.putExtra(
                Constants.EXTRA_RESOURCE_ID, defaultUuid);
            startActivityForResult(intent, Constants.CHOOSE_CALENDAR_OPTION_REQUEST);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
          }
        }
    );
    scheduleButton.setEnabled(true);
  }

  private void initializeEditActivityWith(ViewModelEntry viewModelEntry) {
    Log.d(TAG, "initializeEditActivityWith viewModelEntry " + viewModelEntry.getTitle() + "called");
    editTextTitle.setText(viewModelEntry.getTitle());
    editTextDescription.setText(viewModelEntry.getDescription());
    initializeParentButtonWithUuid(viewModelEntry.getParentUuid());
    lastSavedEventSequenceNumber = viewModelEntry.getLastSavedEventSequenceNumber();
    return;
  }

}
