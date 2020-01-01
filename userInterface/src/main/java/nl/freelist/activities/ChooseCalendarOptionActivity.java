package nl.freelist.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.schedulers.Schedulers;
import nl.freelist.androidCrossCuttingConcerns.MySettings;
import nl.freelist.commands.ScheduleEntryCommand;
import nl.freelist.data.Repository;
import nl.freelist.domain.crossCuttingConcerns.Constants;
import nl.freelist.domain.entities.Calendar;
import nl.freelist.freelist.R;
import nl.freelist.recyclerviewHelpers.ChooseCalendarOptionAdapter;
import nl.freelist.recyclerviewHelpers.ItemClickListener;
import nl.freelist.viewModelPerActivity.ChooseCalendarOptionViewModel;

public class ChooseCalendarOptionActivity extends AppCompatActivity implements ItemClickListener {

  private ChooseCalendarOptionViewModel chooseCalendarOptionViewModel;
  private ChooseCalendarOptionAdapter adapter;
  private RecyclerView recyclerView;
  private static final String TAG = "ChooseCalendarOptionActivity";
  private String entryUuid;
  private String resourceUuid;
  private String myUuid;
  private int calendarOptionSelected;
  private Repository repository;

  public ChooseCalendarOptionActivity() {
  }

  @Override
  protected void onResume() {
    Log.d(TAG, "onResume called.");
    super.onResume();
    updateRecyclerView();
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    Log.d(TAG, "onCreate called.");
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_choose_entry);

    chooseCalendarOptionViewModel = ViewModelProviders.of(this)
        .get(ChooseCalendarOptionViewModel.class);

    initializeSharedPreferences();
    repository = new Repository(this.getApplicationContext());

    Bundle bundle = getIntent().getExtras();
    if (bundle != null) {
      entryUuid = bundle.getString(Constants.EXTRA_ENTRY_ID);
      resourceUuid = bundle.getString(Constants.EXTRA_RESOURCE_ID);
    }
    chooseCalendarOptionViewModel.updateEntryAndResource(entryUuid, resourceUuid);

    recyclerView = findViewById(R.id.recycler_view);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    recyclerView.setHasFixedSize(true);

    adapter = new ChooseCalendarOptionAdapter(this);

    recyclerView.setAdapter(adapter);

    ActionBar actionbar = getSupportActionBar();
//    actionbar.setDisplayHomeAsUpEnabled(true);
    actionbar.setTitle("Choose a scheduling option");
  }

  private void initializeSharedPreferences() {
    MySettings mySettings = new MySettings(this);
    myUuid = mySettings.getUuid();
  }

  private void updateRecyclerView() {
    Log.d(TAG, "updateRecyclerView called.");
    chooseCalendarOptionViewModel
        .getAllPrioOptions(entryUuid, resourceUuid)
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.io())
        .subscribe(
            prioEntries -> {
              // update RecyclerView
              runOnUiThread(
                  new Runnable() {
                    @Override
                    public void run() {
                      //update adapter
                      adapter.setPrioEntries(prioEntries);
                      Toast.makeText(
                          ChooseCalendarOptionActivity.this,
                          "ChooseCalendarOptionViewModel refreshed!",
                          Toast.LENGTH_SHORT)
                          .show();
                    }
                  });
            });
  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    Log.d(TAG, "onCreateOptionsMenu called.");
    MenuInflater menuInflater = getMenuInflater();
    menuInflater.inflate(R.menu.choose_entry_menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    Log.d(TAG, "onOptionsItemSelected called.");
    switch (item.getItemId()) {
      case R.id.save_entry:
        Intent data = new Intent();
        //calendarOptionSelected = adapter.getCurrentUuid();
        data.putExtra(Constants.EXTRA_PRIO_CHOSEN, calendarOptionSelected);
        //Todo: handle multi-select calendarOptions ... if at all reasonable...??? ie recurrent scheduling entry
        if (calendarOptionSelected > -1) {
          int lastSavedEventSequenceNumber;
          lastSavedEventSequenceNumber = adapter.getLastSavedEventSequenceNumberFor(
              calendarOptionSelected);
          int lastSavedResourceSequenceNumber;
          lastSavedResourceSequenceNumber = adapter.lastSavedResourceSequenceNumberFor(
              calendarOptionSelected);
          Calendar calendar;
          calendar = adapter.getCalendarFor(calendarOptionSelected);
          Log.d(
              TAG,
              "calendar option "
                  + calendarOptionSelected
                  + " selected with following specs "
                  + " lastSavedEventSequenceNumber " + lastSavedEventSequenceNumber
                  + " lastSavedResourceSequenceNumber" + lastSavedResourceSequenceNumber
          );
          ScheduleEntryCommand scheduleEntryCommand =
              new ScheduleEntryCommand(entryUuid, resourceUuid, lastSavedEventSequenceNumber,
                  lastSavedResourceSequenceNumber, repository, calendar);
          //Todo: check if the whole creation + asyncScheduling of command can't be executed by viewModel
          ChooseCalendarOptionViewModel.handle(scheduleEntryCommand)
              .subscribeOn(Schedulers.io())
              .observeOn(Schedulers.io())
              .subscribe(
                  (result -> {
                    // update View
                    runOnUiThread(
                        new Runnable() {
                          @Override
                          public void run() {
                            if (result.isSuccess()) {
                              updateRecyclerView();
                            }
                          }
                        });
                  }));
        }
        setResult(RESULT_OK, data);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
        return true;
      case android.R.id.home:
        this.finish();
        return true;
      default:
        return true;
    }
  }

  @Override
  public void onItemClick(View view, int position) {
    Log.d(TAG, "onItemClick called.");
    calendarOptionSelected = position;

  }


}
