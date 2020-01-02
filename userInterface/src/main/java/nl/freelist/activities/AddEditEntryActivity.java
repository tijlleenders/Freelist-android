package nl.freelist.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
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

public class AddEditEntryActivity extends AppCompatActivity implements OnFocusChangeListener {

  private static final String TAG = "AddEditEntryActivity";

  private String uuid; // Todo: why ever store a UUID as a string, if not in data persistence layer?
  private String parentUuid;
  private String defaultUuid;
  private int lastSavedEventSequenceNumber = -1;

  private Repository repository;

  private String initialTitle = "";
  private String initialStart = "";
  private String initialDuration = "";
  private String initialEnd = "";
  private String initialNotes = "";

  private TextInputLayout textInputLayoutTitle;
  private TextInputLayout textInputLayoutStart;
  private TextInputLayout textInputLayoutDuration;
  private TextInputLayout textInputLayoutEnd;
  private TextInputLayout textInputLayoutNotes;

  private TextInputEditText textInputEditTextTitle;
  private TextInputEditText textInputEditTextStart;
  private TextInputEditText textInputEditTextDuration;
  private TextInputEditText textInputEditTextEnd;
  private TextInputEditText textInputEditTextNotes;

  private Button scheduleButton;

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
    if (!textInputEditTextTitle.getText().toString().equals(initialTitle)) {
      {
        Log.d(
            TAG,
            "Title changed from "
                + initialTitle
                + " to "
                + textInputEditTextTitle.getText().toString()
                + " with eventSequenceNumber "
                + lastSavedEventSequenceNumber);
        ChangeEntryTitleCommand changeEntryTitleCommand =
            new ChangeEntryTitleCommand(
                uuid,
                initialTitle,
                textInputEditTextTitle.getText().toString(),
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
    if (!textInputEditTextStart.getText().toString().equals(initialStart)) {
      //Todo: implementation
    }
    if (!textInputEditTextDuration.getText().toString().equals(initialDuration)) {
      //Todo: implementation
    }
    if (!textInputEditTextEnd.getText().toString().equals(initialEnd)) {
      //Todo: implementation
    }
    if (!textInputEditTextNotes.getText().toString().equals(initialNotes)) {
      {
        Log.d(
            TAG,
            "Description changed from "
                + initialNotes
                + " to "
                + textInputEditTextTitle.getText().toString()
                + " with eventSequenceNumber "
                + lastSavedEventSequenceNumber);
        ChangeEntryDescriptionCommand changeEntryDescriptionCommand =
            new ChangeEntryDescriptionCommand(
                uuid,
                initialNotes,
                textInputEditTextNotes.getText().toString(),
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
    }

    attachViewListeners();
  }


  private void initializeForAddNew(Bundle bundle) {
    if (bundle.containsKey(Constants.EXTRA_ENTRY_PARENT_ID)) {
      initializeParentButtonWithUuid(parentUuid);
      uuid = UUID.randomUUID().toString();
    }
    setTitle("Add new Freelist");
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
              //Todo: setup action if fails
            }));

    setTitle("Edit existing Freelist");
  }

  private void initializeViews() {
    textInputLayoutTitle = findViewById(R.id.text_input_layout_title);
    textInputLayoutStart = findViewById(R.id.text_input_layout_start);
    textInputLayoutDuration = findViewById(R.id.text_input_layout_duration);
    textInputLayoutEnd = findViewById(R.id.text_input_layout_end);
    textInputLayoutNotes = findViewById(R.id.text_input_layout_notes);

    //Initialize via layout to pass along appropriate styling from layout
    textInputEditTextTitle = findViewById(R.id.edit_text_title);
    textInputEditTextStart = findViewById(R.id.edit_text_start);
    textInputEditTextDuration = findViewById(R.id.edit_text_duration);
    textInputEditTextEnd = findViewById(R.id.edit_text_end);
    textInputEditTextNotes = findViewById(R.id.edit_text_notes);

    scheduleButton = findViewById(R.id.schedule_button);
  }

  private void attachViewListeners() {
    textInputEditTextTitle.setOnFocusChangeListener(this::onFocusChange);
    textInputEditTextStart.setOnFocusChangeListener(this::onFocusChange);
    textInputEditTextDuration.setOnFocusChangeListener(this::onFocusChange);
    textInputEditTextEnd.setOnFocusChangeListener(this::onFocusChange);
    textInputEditTextNotes.setOnFocusChangeListener(this::onFocusChange);
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
    initialTitle = viewModelEntry.getTitle();
    //Todo: set initial start/duration/end from viewModelEntry
    initialNotes = viewModelEntry.getDescription();

    textInputEditTextTitle.setText(initialTitle);
    textInputEditTextNotes.setText(initialNotes);
    textInputEditTextStart.setText(initialStart);
    textInputEditTextDuration.setText(initialDuration);
    textInputEditTextEnd.setText(initialEnd);

    initializeParentButtonWithUuid(viewModelEntry.getParentUuid());
    lastSavedEventSequenceNumber = viewModelEntry.getLastSavedEventSequenceNumber();
    return;
  }

  @Override
  public void onFocusChange(View view, boolean b) {
    switch (view.getId()) {
      default:
        saveChangedFields();
        break;
    }
  }
}
