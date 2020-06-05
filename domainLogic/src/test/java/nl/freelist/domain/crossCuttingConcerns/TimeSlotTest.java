package nl.freelist.domain.crossCuttingConcerns;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.time.DayOfWeek;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import nl.freelist.domain.aggregates.scheduler.Calendar;
import nl.freelist.domain.aggregates.scheduler.Scheduler;
import nl.freelist.domain.valueObjects.Id;
import nl.freelist.domain.valueObjects.constraints.ImpossibleDaysConstraint;
import org.junit.Test;

public
class TimeSlotTest { // Use TreeMap instead of Skiplist as concurrent access is not possible within
  // bounded context

  @Test
  public void calendarLogic() {
    Id personId = Id.Create();
    Id entryId = Id.Create();
    Id entryId2 = Id.Create();
    List<ImpossibleDaysConstraint> impossibleDaysConstraintList = new ArrayList<>();
    impossibleDaysConstraintList.add(ImpossibleDaysConstraint.Create(DayOfWeek.FRIDAY));
    Scheduler scheduler = Scheduler.Create(personId);
    Calendar calendar = scheduler.getCalendar();

    scheduler.upsert(
        entryId,
        personId,
        "testTitle",
        OffsetDateTime.of(2021, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC),
        3600L,
        OffsetDateTime.of(2022, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC),
        "notesNew",
        impossibleDaysConstraintList,
        -1
    );
    calendar.printFreeTimeSlots();
    calendar.printScheduledTimeSlots();
    String a = "";
    //Check scheduled start
    assertEquals(OffsetDateTime.of(2021, 1, 2, 0, 0, 0, 0, ZoneOffset.UTC)
        , calendar.getScheduledTimeSlot(entryId).getStartDateTime());
    //Check sheduled end
    assertEquals(OffsetDateTime.of(2021, 1, 2, 1, 0, 0, 0, ZoneOffset.UTC)
        , calendar.getScheduledTimeSlot(entryId).getEndDateTime());

    //Check first free left over start
    assertEquals(OffsetDateTime.of(2021, 1, 2, 1, 0, 0, 0, ZoneOffset.UTC)
        , calendar.getFreeTimeSlot(-136203725140925296L).getStartDateTime());
    //Check first free left over end
    assertEquals(Constants.END_OF_TIME
        , calendar.getFreeTimeSlot(-136203725140925296L).getEndDateTime());
    //Check first free left over Id
    assertNull(calendar.getFreeTimeSlot(-136203725140925296L).getEntryId());

    //Check second free left over start
    assertEquals(OffsetDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC)
        , calendar.getFreeTimeSlot(136188258995404800L).getStartDateTime());
    //Check second free left over end
    assertEquals(OffsetDateTime.of(2021, 1, 2, 0, 0, 0, 0, ZoneOffset.UTC)
        , calendar.getFreeTimeSlot(136188258995404800L).getEndDateTime());
    //Check second free left over Id
    assertNull(calendar.getFreeTimeSlot(136188258995404800L).getEntryId());

    String b = "";
    scheduler.upsert(
        entryId2,
        personId,
        "testTitle2",
        OffsetDateTime.of(2021, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC),
        3600L,
        OffsetDateTime.of(2022, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC),
        "notesNew",
        impossibleDaysConstraintList,
        7
    );

    calendar.printFreeTimeSlots();
    calendar.printScheduledTimeSlots();

    //Check first free left over start
    assertEquals(Constants.START_OF_TIME
        , calendar.getFreeTimeSlot(136188258995404800L).getStartDateTime());
    //Check first free left over end
    assertEquals(OffsetDateTime.of(2021, 1, 2, 0, 0, 0, 0, ZoneOffset.UTC)
        , calendar.getFreeTimeSlot(136188258995404800L).getEndDateTime());
    //Check first free left over Id
    assertNull(calendar.getFreeTimeSlot(136188258995404800L).getEntryId());

    //Check second free left over start
    assertEquals(OffsetDateTime.of(2021, 1, 2, 2, 0, 0, 0, ZoneOffset.UTC)
        , calendar.getFreeTimeSlot(-136219187023187296L).getStartDateTime());
    //Check second free left over end
    assertEquals(Constants.END_OF_TIME
        , calendar.getFreeTimeSlot(-136219187023187296L).getEndDateTime());
    //Check second free left over Id
    assertNull(calendar.getFreeTimeSlot(-136219187023187296L).getEntryId());

    //Check scheduled first
    assertEquals(OffsetDateTime.of(2021, 1, 2, 0, 0, 0, 0, ZoneOffset.UTC)
        , calendar.getScheduledTimeSlot(entryId).getStartDateTime());
    //Check sheduled end
    assertEquals(OffsetDateTime.of(2021, 1, 2, 1, 0, 0, 0, ZoneOffset.UTC)
        , calendar.getScheduledTimeSlot(entryId).getEndDateTime());

    //Check scheduled second
    assertEquals(OffsetDateTime.of(2021, 1, 2, 1, 0, 0, 0, ZoneOffset.UTC)
        , calendar.getScheduledTimeSlot(entryId2).getStartDateTime());
    //Check sheduled end
    assertEquals(OffsetDateTime.of(2021, 1, 2, 2, 0, 0, 0, ZoneOffset.UTC)
        , calendar.getScheduledTimeSlot(entryId2).getEndDateTime());

    String c = "";
  }


}
