package nl.freelist.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomappbar.BottomAppBar;
import io.reactivex.schedulers.Schedulers;
import nl.freelist.androidCrossCuttingConcerns.MySettings;
import nl.freelist.freelist.R;
import nl.freelist.recyclerviewHelpers.CalendarEntryAdapter;
import nl.freelist.recyclerviewHelpers.ItemClickListener;
import nl.freelist.viewModelPerActivity.CalendarViewModel;

public class CalendarActivity extends AppCompatActivity implements ItemClickListener {

  private static final String TAG = "CalendarActivity";
  private String resourceUuid;

  private CalendarEntryAdapter adapter;
  private RecyclerView recyclerView;

  private CalendarViewModel calendarViewModel;
  private BottomAppBar bottomAppBar;

  public CalendarActivity() {
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
    setContentView(R.layout.activity_calendar);

    initializeSharedPreferences();

    calendarViewModel = ViewModelProviders.of(this)
        .get(CalendarViewModel.class);
    calendarViewModel.setOwnerUuid(resourceUuid);

    initializeViews();

    setupActionBars();

  }

  private void initializeSharedPreferences() {
    MySettings mySettings = new MySettings(this);
    resourceUuid = mySettings.getUuid();
  }

  private void initializeViews() {
    Log.d(TAG, "initializeViews called.");
    recyclerView = findViewById(R.id.recycler_view);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    recyclerView.setHasFixedSize(true);
    adapter = new CalendarEntryAdapter(this);
    recyclerView.setAdapter(adapter);
    bottomAppBar = findViewById(R.id.bottom_app_bar);
  }


  private void updateView() {
    updateRecyclerView();
  }

  private void updateRecyclerView() {
    Log.d(TAG, "updateRecyclerView called.");
    calendarViewModel
        .getAllCalendarEntries()
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.io())
        .subscribe(
            calendarEntries -> {
              // update RecyclerView
              runOnUiThread(
                  new Runnable() {
                    @Override
                    public void run() {
                      adapter.setEntries(calendarEntries);
                      Toast.makeText(
                          CalendarActivity.this,
                          "calendarEntriesViewModel recyclerView refreshed!",
                          Toast.LENGTH_SHORT)
                          .show();
                    }
                  });
            });
  }

  private void setupActionBars() {
    Log.d(TAG, "setupActionBars called.");

    //TopAppBar
    setTitle("My Calendar");
    //override onCreateOptionsMenu and onOptionsItemSelected for TopAppBar

    bottomAppBar.replaceMenu(R.menu.bottom_app_bar_menu);
    bottomAppBar.getMenu().findItem(R.id.bottom_app_bar_calendar).getIcon().setColorFilter(
        Color.BLACK,
        PorterDuff.Mode.SRC_IN);
    bottomAppBar.setOnMenuItemClickListener(new BottomAppBar.OnMenuItemClickListener() {
      @Override
      public boolean onMenuItemClick(MenuItem menuItem) {
        int id = menuItem.getItemId();
        switch (id) {
          case R.id.bottom_app_bar_freelists:
            Toast.makeText(CalendarActivity.this, "Freelists selected",
                Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(CalendarActivity.this, NavigateFreelistActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            return true;
          case R.id.bottom_app_bar_calendar:
            Toast.makeText(CalendarActivity.this, "Calendar already selected",
                Toast.LENGTH_SHORT).show();
            return true;
          case R.id.bottom_app_bar_search:
            Toast.makeText(CalendarActivity.this, "Search selected", Toast.LENGTH_SHORT).show();
            Intent searchIntent = new Intent(CalendarActivity.this, SearchActivity.class);
            startActivity(searchIntent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            return true;
        }
        return false;
      }
    });
  }


  @Override
  public void onItemClick(View view, int position) {
    Log.d(TAG, "onItemClick at position " + position + "called.");
//    adapter.getEntryAt(position)
  }
}
