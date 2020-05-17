package nl.freelist.domain.crossCuttingConcerns;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.DayOfWeek;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import nl.freelist.domain.valueObjects.TimeSlot;
import nl.freelist.domain.valueObjects.constraints.Constraint;
import nl.freelist.domain.valueObjects.constraints.ImpossibleDaysConstraint;

public class TimeHelperTest {

  @org.junit.Test
  public void getDurationStringFromInt() {
    assertEquals("", TimeHelper.getDurationStringFrom(0));

    assertEquals("1y", TimeHelper.getDurationStringFrom(24 * 3600 * 365 + 3600 * 6));
    assertEquals(
        "1y 1w", TimeHelper.getDurationStringFrom(24 * 3600 * 365 + 3600 * 6 + 24 * 3600 * 7));
    assertEquals(
        "1y 1w",
        TimeHelper.getDurationStringFrom(
            24 * 3600 * 365 + 3600 * 6 + 24 * 3600 * 7 + 24 * 3600 * 3));
    assertEquals(
        "1y 2w",
        TimeHelper.getDurationStringFrom(
            24 * 3600 * 365 + 3600 * 6 + 24 * 3600 * 7 + 24 * 3600 * 4));

    assertEquals("1w", TimeHelper.getDurationStringFrom(24 * 3600 * 7));
    assertEquals("1w 5d", TimeHelper.getDurationStringFrom(24 * 3600 * 7 + 24 * 3600 * 5));
    assertEquals(
        "1w 6d", TimeHelper.getDurationStringFrom(24 * 3600 * 7 + 24 * 3600 * 5 + 13 * 3600));

    assertEquals("1d 5h", TimeHelper.getDurationStringFrom(24 * 3600 + 5 * 3600));
    assertEquals("1d 6h", TimeHelper.getDurationStringFrom(24 * 3600 + 5 * 3600 + 1800));

    assertEquals("1h", TimeHelper.getDurationStringFrom(3600));
    assertEquals("1h 30m", TimeHelper.getDurationStringFrom(3600 + 29 * 60 + 30));
    assertEquals("1h 5m", TimeHelper.getDurationStringFrom(3600 + 5 * 60));

    assertEquals("5m", TimeHelper.getDurationStringFrom(300));
    assertEquals("29m 30s", TimeHelper.getDurationStringFrom(29 * 60 + 30));
    assertEquals("1m 5s", TimeHelper.getDurationStringFrom(65));

    assertEquals("5s", TimeHelper.getDurationStringFrom(5));
  }

  @org.junit.Test
  public void timeSlotAdditionAStartsBeforeAEndsBefore() {
    TimeSlot a =
        TimeSlot.Create(
            OffsetDateTime.of(2019, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC),
            OffsetDateTime.of(2021, 1, 2, 0, 0, 0, 0, ZoneOffset.UTC));
    TimeSlot b =
        TimeSlot.Create(
            OffsetDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC),
            OffsetDateTime.of(2022, 1, 2, 0, 0, 0, 0, ZoneOffset.UTC));
    TimeSlot c = a.plus(b);
    assertEquals(
        c,
        TimeSlot.Create(
            OffsetDateTime.of(2019, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC),
            OffsetDateTime.of(2022, 1, 2, 0, 0, 0, 0, ZoneOffset.UTC)));
  }

  @org.junit.Test
  public void timeSlotAdditionAStartsBeforeAEndIsEqual() {
    TimeSlot a =
        TimeSlot.Create(
            OffsetDateTime.of(2019, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC),
            OffsetDateTime.of(2021, 1, 2, 0, 0, 0, 0, ZoneOffset.UTC));
    TimeSlot b =
        TimeSlot.Create(
            OffsetDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC),
            OffsetDateTime.of(2021, 1, 2, 0, 0, 0, 0, ZoneOffset.UTC));
    TimeSlot c = a.plus(b);
    assertEquals(
        c,
        TimeSlot.Create(
            OffsetDateTime.of(2019, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC),
            OffsetDateTime.of(2021, 1, 2, 0, 0, 0, 0, ZoneOffset.UTC)));
  }

  @org.junit.Test
  public void timeSlotAdditionAStartsBeforeAEndsAfter() {
    TimeSlot a =
        TimeSlot.Create(
            OffsetDateTime.of(2019, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC),
            OffsetDateTime.of(2022, 1, 2, 0, 0, 0, 0, ZoneOffset.UTC));
    TimeSlot b =
        TimeSlot.Create(
            OffsetDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC),
            OffsetDateTime.of(2021, 1, 2, 0, 0, 0, 0, ZoneOffset.UTC));
    TimeSlot c = a.plus(b);
    assertEquals(
        c,
        TimeSlot.Create(
            OffsetDateTime.of(2019, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC),
            OffsetDateTime.of(2022, 1, 2, 0, 0, 0, 0, ZoneOffset.UTC)));
  }

  @org.junit.Test
  public void timeSlotAdditionAStartIsEqualAEndsBefore() {
    TimeSlot a =
        TimeSlot.Create(
            OffsetDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC),
            OffsetDateTime.of(2021, 1, 2, 0, 0, 0, 0, ZoneOffset.UTC));
    TimeSlot b =
        TimeSlot.Create(
            OffsetDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC),
            OffsetDateTime.of(2022, 1, 2, 0, 0, 0, 0, ZoneOffset.UTC));
    TimeSlot c = a.plus(b);
    assertEquals(
        c,
        TimeSlot.Create(
            OffsetDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC),
            OffsetDateTime.of(2022, 1, 2, 0, 0, 0, 0, ZoneOffset.UTC)));
  }

  @org.junit.Test
  public void timeSlotAdditionAStartIsEqualAEndIsEqual() {
    TimeSlot a =
        TimeSlot.Create(
            OffsetDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC),
            OffsetDateTime.of(2021, 1, 2, 0, 0, 0, 0, ZoneOffset.UTC));
    TimeSlot b =
        TimeSlot.Create(
            OffsetDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC),
            OffsetDateTime.of(2021, 1, 2, 0, 0, 0, 0, ZoneOffset.UTC));
    TimeSlot c = a.plus(b);
    assertEquals(
        c,
        TimeSlot.Create(
            OffsetDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC),
            OffsetDateTime.of(2021, 1, 2, 0, 0, 0, 0, ZoneOffset.UTC)));
  }

  @org.junit.Test
  public void timeSlotAdditionAStartIsEqualAEndsAfter() {
    TimeSlot a =
        TimeSlot.Create(
            OffsetDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC),
            OffsetDateTime.of(2022, 1, 2, 0, 0, 0, 0, ZoneOffset.UTC));
    TimeSlot b =
        TimeSlot.Create(
            OffsetDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC),
            OffsetDateTime.of(2021, 1, 2, 0, 0, 0, 0, ZoneOffset.UTC));
    TimeSlot c = a.plus(b);
    assertEquals(
        c,
        TimeSlot.Create(
            OffsetDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC),
            OffsetDateTime.of(2022, 1, 2, 0, 0, 0, 0, ZoneOffset.UTC)));
  }

  @org.junit.Test
  public void timeSlotAdditionAStartsAfterAEndsBefore() {
    TimeSlot a =
        TimeSlot.Create(
            OffsetDateTime.of(2021, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC),
            OffsetDateTime.of(2022, 1, 2, 0, 0, 0, 0, ZoneOffset.UTC));
    TimeSlot b =
        TimeSlot.Create(
            OffsetDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC),
            OffsetDateTime.of(2023, 1, 2, 0, 0, 0, 0, ZoneOffset.UTC));
    TimeSlot c = a.plus(b);
    assertEquals(
        c,
        TimeSlot.Create(
            OffsetDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC),
            OffsetDateTime.of(2023, 1, 2, 0, 0, 0, 0, ZoneOffset.UTC)));
  }

  @org.junit.Test
  public void timeSlotAdditionAStartsAfterAEndIsEqual() {
    TimeSlot a =
        TimeSlot.Create(
            OffsetDateTime.of(2021, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC),
            OffsetDateTime.of(2022, 1, 2, 0, 0, 0, 0, ZoneOffset.UTC));
    TimeSlot b =
        TimeSlot.Create(
            OffsetDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC),
            OffsetDateTime.of(2022, 1, 2, 0, 0, 0, 0, ZoneOffset.UTC));
    TimeSlot c = a.plus(b);
    assertEquals(
        c,
        TimeSlot.Create(
            OffsetDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC),
            OffsetDateTime.of(2022, 1, 2, 0, 0, 0, 0, ZoneOffset.UTC)));
  }

  @org.junit.Test
  public void timeSlotAdditionAStartsAfterAEndsAfter() {
    TimeSlot a =
        TimeSlot.Create(
            OffsetDateTime.of(2021, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC),
            OffsetDateTime.of(2023, 1, 2, 0, 0, 0, 0, ZoneOffset.UTC));
    TimeSlot b =
        TimeSlot.Create(
            OffsetDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC),
            OffsetDateTime.of(2022, 1, 2, 0, 0, 0, 0, ZoneOffset.UTC));
    TimeSlot c = a.plus(b);
    assertEquals(
        c,
        TimeSlot.Create(
            OffsetDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC),
            OffsetDateTime.of(2023, 1, 2, 0, 0, 0, 0, ZoneOffset.UTC)));
  }

  // minus cases start here (only three as the to subtract TimeSlot should fall inside
  @org.junit.Test
  public void timeSlotMinusAStartsBeforeAEndsAfter() {
    TimeSlot a =
        TimeSlot.Create(
            OffsetDateTime.of(2019, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC),
            OffsetDateTime.of(2022, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC));
    TimeSlot b =
        TimeSlot.Create(
            OffsetDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC),
            OffsetDateTime.of(2021, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC));
    List<TimeSlot> c = a.minus(b);
    List<TimeSlot> expectedSlots = new ArrayList<>();
    expectedSlots.add(
        TimeSlot.Create(
            OffsetDateTime.of(2019, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC),
            OffsetDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC)));
    expectedSlots.add(
        TimeSlot.Create(
            OffsetDateTime.of(2021, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC),
            OffsetDateTime.of(2022, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC)));
    assertTrue(TimeHelper.slotListsEqual(c, expectedSlots));
  }

  @org.junit.Test
  public void timeSlotMinusAStartIsEqualAEndIsAfter() {
    TimeSlot a =
        TimeSlot.Create(
            OffsetDateTime.of(2019, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC),
            OffsetDateTime.of(2021, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC));
    TimeSlot b =
        TimeSlot.Create(
            OffsetDateTime.of(2019, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC),
            OffsetDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC));
    List<TimeSlot> c = a.minus(b);
    List<TimeSlot> expectedSlots = new ArrayList<>();
    expectedSlots.add(
        TimeSlot.Create(
            OffsetDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC),
            OffsetDateTime.of(2021, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC)));
    assertTrue(TimeHelper.slotListsEqual(c, expectedSlots));
  }

  @org.junit.Test
  public void timeSlotMinusAStartsBeforeAEndIsEqual() {
    TimeSlot a =
        TimeSlot.Create(
            OffsetDateTime.of(2019, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC),
            OffsetDateTime.of(2021, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC));
    TimeSlot b =
        TimeSlot.Create(
            OffsetDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC),
            OffsetDateTime.of(2021, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC));
    List<TimeSlot> c = a.minus(b);
    List<TimeSlot> expectedSlots = new ArrayList<>();
    expectedSlots.add(
        TimeSlot.Create(
            OffsetDateTime.of(2019, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC),
            OffsetDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC)));
    assertTrue(TimeHelper.slotListsEqual(c, expectedSlots));
  }

  @org.junit.Test
  public void compareSlotLists() {
    TimeSlot a =
        TimeSlot.Create(
            OffsetDateTime.of(2019, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC),
            OffsetDateTime.of(2021, 1, 2, 0, 0, 0, 0, ZoneOffset.UTC));
    TimeSlot b =
        TimeSlot.Create(
            OffsetDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC),
            OffsetDateTime.of(2021, 1, 2, 0, 0, 0, 0, ZoneOffset.UTC));
    List<TimeSlot> firstList = new ArrayList<>();
    List<TimeSlot> secondList = new ArrayList<>();
    assertTrue(TimeHelper.slotListsEqual(firstList, secondList));
    firstList.add(a);
    assertFalse(TimeHelper.slotListsEqual(firstList, secondList));
    secondList.add(a);
    assertTrue(TimeHelper.slotListsEqual(firstList, secondList));
    firstList.add(a);
    assertFalse(TimeHelper.slotListsEqual(firstList, secondList));
    secondList.add(a);
    assertTrue(TimeHelper.slotListsEqual(firstList, secondList));
  }

  @org.junit.Test
  public void findCompatibleTimeSlots() {
    List<TimeSlot> timeSlotList = new ArrayList<>();

    List<Constraint> constraintList = new ArrayList<>();
    constraintList.add(ImpossibleDaysConstraint.Create(DayOfWeek.MONDAY));
    constraintList.add(ImpossibleDaysConstraint.Create(DayOfWeek.TUESDAY));
//    constraintList.add(ImpossibleDaysConstraint.Create(DayOfWeek.WEDNESDAY));
    TimeSlot a =
        TimeSlot.Create(
            OffsetDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC),
            OffsetDateTime.of(2020, 1, 6, 0, 0, 0, 0, ZoneOffset.UTC));
    TimeSlot b =
        TimeSlot.Create(
            OffsetDateTime.of(2020, 1, 6, 0, 0, 0, 0, ZoneOffset.UTC),
            OffsetDateTime.of(2020, 1, 14, 0, 0, 0, 0, ZoneOffset.UTC));
    timeSlotList.add(a);
    timeSlotList.add(b);
    List<TimeSlot> result = TimeHelper.getCompatibleSlots(timeSlotList, constraintList);
    assertTrue(result.size() == 1);
  }
}
