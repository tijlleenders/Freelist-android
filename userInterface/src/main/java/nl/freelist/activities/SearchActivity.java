package nl.freelist.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import nl.freelist.androidCrossCuttingConcerns.MySettings;
import nl.freelist.domain.crossCuttingConcerns.Constants;
import nl.freelist.freelist.R;
import nl.freelist.viewModelPerActivity.NavigateEntriesViewModel;

public class SearchActivity extends AppCompatActivity {

  private static final String TAG = "SearchActivity";
  private String myUuid;

  private ViewModel searchViewModel;
  private BottomAppBar bottomAppBar;

  public SearchActivity() {
  }

  @Override
  protected void onResume() {
    Log.d(TAG, "onResume called.");
    super.onResume();
    setupActionBars();
    updateView();
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    Log.d(TAG, "onCreate called.");
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_search);

    searchViewModel = ViewModelProviders.of(this)
        .get(NavigateEntriesViewModel.class);

    initializeViews();
    setupActionBars();
    setupFloatingActionButton();

  }

  private void initializeSharedPreferences() {
    MySettings mySettings = new MySettings(this);
    myUuid = mySettings.getId();
  }

  private void initializeViews() {
    Log.d(TAG, "initializeViews called.");
    bottomAppBar = findViewById(R.id.bottom_app_bar);
  }


  private void setupFloatingActionButton() {
    Log.d(TAG, "setupFloatingActionButton called.");
    FloatingActionButton buttonAddEntry = findViewById(R.id.button_add_entry);
    buttonAddEntry.setColorFilter(Color.parseColor("#15b790"));
    buttonAddEntry.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) { //Todo: fix
            Intent intent = new Intent(SearchActivity.this, AddEditEntryActivity.class);
            intent.putExtra(Constants.EXTRA_REQUEST_TYPE_ADD, Constants.ADD_ENTRY_REQUEST);
            intent.putExtra(Constants.EXTRA_ENTRY_PARENT_ID, "");
            intent.putExtra(
                Constants.EXTRA_SCHEDULER_EVENT_SEQUENCE_NUMBER,
                -1);
            startActivityForResult(intent, Constants.ADD_ENTRY_REQUEST);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
          }
        });
  }

  private void updateView() {
  }

  private void setupActionBars() {
    Log.d(TAG, "setupActionBars called.");

    //TopAppBar
    setTitle("Search");
    //override onCreateOptionsMenu and onOptionsItemSelected for TopAppBar

    bottomAppBar.replaceMenu(R.menu.bottom_app_bar_menu);
    bottomAppBar.getMenu().findItem(R.id.bottom_app_bar_freelists).getIcon().setAlpha(120);

    bottomAppBar.getMenu().findItem(R.id.bottom_app_bar_calendar).getIcon().setAlpha(120);

    bottomAppBar.getMenu().findItem(R.id.bottom_app_bar_search).getIcon().setAlpha(220);
    bottomAppBar.setOnMenuItemClickListener(new BottomAppBar.OnMenuItemClickListener() {
      @Override
      public boolean onMenuItemClick(MenuItem menuItem) {
        int id = menuItem.getItemId();
        switch (id) {
          case R.id.bottom_app_bar_freelists:
            Intent navigateFreelistIntent = new Intent(SearchActivity.this,
                NavigateFreelistActivity.class);
            startActivity(navigateFreelistIntent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            return true;
          case R.id.bottom_app_bar_calendar:
            Intent calendarIntent = new Intent(SearchActivity.this, CalendarActivity.class);
            startActivity(calendarIntent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            return true;
          case R.id.bottom_app_bar_search:
            return true;
        }
        return false;
      }
    });
  }


}
