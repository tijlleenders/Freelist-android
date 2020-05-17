package nl.freelist;

import static org.junit.Assert.assertEquals;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.time.DayOfWeek;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import nl.freelist.data.dto.ViewModelEntry;
import nl.freelist.data.gson.Converters;
import nl.freelist.domain.valueObjects.Id;
import nl.freelist.domain.valueObjects.constraints.ImpossibleDaysConstraint;

public class GsonConstraintConverterTest {

  private static final String TAG = "nl.freelist.GsonConstraintConverterTest";
  private Gson gson;
  private ViewModelEntry viewModelEntry;
  private ViewModelEntry viewModelEntry2;

  @org.junit.Before
  public void setup() {
    gson = Converters.registerAll(new GsonBuilder()).create();
    List<ImpossibleDaysConstraint> constraintList = new ArrayList<>();
    ImpossibleDaysConstraint impossibleDaysConstraint = ImpossibleDaysConstraint
        .Create(DayOfWeek.MONDAY);
    constraintList.add(impossibleDaysConstraint);
    viewModelEntry =
        new ViewModelEntry(
            Id.fromString("550e8400-e29b-41d4-a716-446655440000"),
            Id.fromString("550e8400-e29b-41d4-a716-446655440000"),
            Id.fromString("550e8400-e29b-41d4-a716-446655440000"),
            "title",
            OffsetDateTime.now(ZoneOffset.UTC),
            10L,
            OffsetDateTime.now(ZoneOffset.UTC).plusHours(1L),
            constraintList,
            "notes",
            0L,
            0L,
            -1);
  }

  @org.junit.Test
  public void gsonTest() {
    String jsonString = gson.toJson(viewModelEntry);
    viewModelEntry2 = gson.fromJson(jsonString, ViewModelEntry.class);
    assertEquals(viewModelEntry.getImpossibleDaysConstraints(),
        viewModelEntry2.getImpossibleDaysConstraints());
  }
}
