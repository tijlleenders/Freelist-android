package nl.freelist.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import io.reactivex.schedulers.Schedulers;
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
  private NumberPickerDuration numberPickerDuration;
  private nl.freelist.viewModelPerActivity.AddEditEntryActivityViewModel AddEditEntryActivityViewModel;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_add_edit_entry);

    editTextTitle = findViewById(R.id.edit_text_title);
    editTextDescription = findViewById(R.id.edit_text_description);
    numberPickerDuration = findViewById(R.id.number_picker_duration);
    parentButton = findViewById(R.id.button_parent_change);

    numberPickerDuration.setMinValue(1);
    numberPickerDuration.setMaxValue(8); // todo: make configurable
    numberPickerDuration.setDisplayedValues(
        new String[]{
            "5m", "15m", "45m", "2h", "4h", "8h", "12h", "24h"
        }); // todo: make configurable, ie make custom subclass with functions to set with string or
    // duration int?

    AddEditEntryActivityViewModel =
        ViewModelProviders.of(this)
            .get(AddEditEntryActivityViewModel.class);

    // Intent.ACTION_* fields are String constant.
    // You cannot use switch with String until JDK 7 android use JDK 6 or 5 to compile. So you can't
    // use that method on Android
    // So using if else if :(
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

  private void saveEntry() {
    if (id == 0) {
      int id = ActivityConstants.VIEWMODEL_ENTRY_ID_NOT_SET;
    }

    //parentId initialized to 0 by default or set by lambda from observable
    String title = editTextTitle.getText().toString();
    String description = editTextDescription.getText().toString();
    String duration = "5m"; //Todo: implement setNumberPicker and getNumberPicker based on string value only (abstract the rest) and get current value from numberPickerDuration

    if (title.trim().isEmpty() || description.trim().isEmpty()) {
      boolean notificationBool =
          PreferenceManager.getDefaultSharedPreferences(this).getBoolean("notifications", false);
      if (notificationBool) {
        Toast.makeText(this, "setting true", Toast.LENGTH_SHORT).show();
      } else {
        Toast.makeText(this, "setting false", Toast.LENGTH_SHORT).show();
      }
      Toast.makeText(this, "Please insert a title and description", Toast.LENGTH_SHORT).show();
      return;
    }

    ViewModelEntry viewModelEntryToSave =
        new ViewModelEntry(
            id, parentId, title, description, duration,
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
    // Intent.ACTION_* fields are String constant.
    // You cannot use switch with String until JDK 7 android use JDK 6 or 5 to compile. So you can't
    // use that method on Android
    // So using if else if :(

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
    numberPickerDuration.setValue(
        numberPickerDuration
            .getNumberPickerPosition(viewModelEntry.getDuration()));
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
