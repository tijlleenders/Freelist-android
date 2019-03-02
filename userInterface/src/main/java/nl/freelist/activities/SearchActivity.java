package nl.freelist.activities;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.bottomappbar.BottomAppBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;
import nl.freelist.androidCrossCuttingConcerns.MySettings;
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

  }

  private void initializeSharedPreferences() {
    MySettings mySettings = new MySettings(this);
    myUuid = mySettings.getUuid();
  }

  private void initializeViews() {
    Log.d(TAG, "initializeViews called.");
    bottomAppBar = findViewById(R.id.bottom_app_bar);
  }


  private void updateView() {
  }

  private void setupActionBars() {
    Log.d(TAG, "setupActionBars called.");

    //TopAppBar
    setTitle("Search");
    //override onCreateOptionsMenu and onOptionsItemSelected for TopAppBar

    bottomAppBar.replaceMenu(R.menu.bottom_app_bar_menu);
    bottomAppBar.setOnMenuItemClickListener(new BottomAppBar.OnMenuItemClickListener() {
      @Override
      public boolean onMenuItemClick(MenuItem menuItem) {
        int id = menuItem.getItemId();
        switch (id) {
          case R.id.bottom_app_bar_freelists:
            Toast.makeText(SearchActivity.this, "Freelists selected",
                Toast.LENGTH_SHORT).show();
            Intent navigateFreelistIntent = new Intent(SearchActivity.this,
                NavigateFreelistActivity.class);
            startActivity(navigateFreelistIntent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            return true;
          case R.id.bottom_app_bar_calendar:
            Toast.makeText(SearchActivity.this, "Calendar selected",
                Toast.LENGTH_SHORT).show();
            Intent calendarIntent = new Intent(SearchActivity.this, CalendarActivity.class);
            startActivity(calendarIntent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            return true;
          case R.id.bottom_app_bar_search:
            Toast.makeText(SearchActivity.this, "Search already selected", Toast.LENGTH_SHORT)
                .show();
            return true;
        }
        return false;
      }
    });
  }


}
