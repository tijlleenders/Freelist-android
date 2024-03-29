package nl.freelist.domain.crossCuttingConcerns;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import nl.freelist.domain.valueObjects.TimeSlot;
import nl.freelist.domain.valueObjects.constraints.Constraint;

public class TimeHelper { // Todo: move to value object in domain model

  public static int getWholeYearsFrom(long duration) {
    return (int) ((duration) / (3600 * 24 * 365 + 3600 * 6));
  }

  public static int getStandaloneWeeksFrom(long duration) {
    return (int) ((duration % (3600 * 24 * 365 + 3600 * 6)) / (3600 * 24 * 7));
  }

  public static int getStandaloneDaysFrom(long duration) {
    return (int) (((duration % (3600 * 24 * 365 + 3600 * 6)) % (3600 * 24 * 7)) / (3600 * 24));
  }

  public static int getStandaloneHoursFrom(long duration) {
    return (int) ((duration % (3600 * 24)) / 3600);
  }

  public static int getStandaloneMinutesFrom(long duration) {
    return (int) ((duration % 3600) / 60);
  }

  public static int getStandaloneSecondsFrom(long duration) {
    return (int) (duration % 60);
  }

  public static String getDurationStringFrom(long duration) {

    int years = getWholeYearsFrom(duration);
    int weeks = getStandaloneWeeksFrom(duration);
    int days = getStandaloneDaysFrom(duration);
    int hours = getStandaloneHoursFrom(duration);
    int minutes = getStandaloneMinutesFrom(duration);
    int seconds = getStandaloneSecondsFrom(duration);

    if (seconds >= 30 && hours > 0) {
      minutes += 1;
      seconds = 0;
    }
    if (minutes >= 30 && days > 0) {
      hours += 1;
      minutes = 0;
    }
    if (hours >= 12 && weeks > 0) {
      days += 1;
      hours = 0;
    }
    if (days > 3 && years > 0) {
      weeks += 1;
      days = 0;
    }

    StringBuilder durationString = new StringBuilder();

    if (years != 0) {
      if (weeks != 0) {
        durationString.append(years).append("y ");
        durationString.append(weeks).append("w");
      } else {
        durationString.append(years).append("y");
      }
      return durationString.toString();
    }

    if (weeks != 0) {
      if (days != 0) {
        durationString.append(weeks).append("w ");
        durationString.append(days).append("d");
      } else {
        durationString.append(weeks).append("w");
      }
      return durationString.toString();
    }

    if (days != 0) {
      if (hours != 0) {
        durationString.append(days).append("d ");
        durationString.append(hours).append("h");
      } else {
        durationString.append(days).append("d");
      }
      return durationString.toString();
    }

    if (hours != 0) {
      if (minutes != 0) {
        durationString.append(hours).append("h ");
        durationString.append(minutes).append("m");
      } else {
        durationString.append(hours).append("h");
      }
      return durationString.toString();
    }

    if (minutes != 0) {
      if (seconds != 0) {
        durationString.append(minutes).append("m ");
        durationString.append(seconds).append("s");
      } else {
        durationString.append(minutes).append("m");
      }
      return durationString.toString();
    }

    if (seconds != 0) {
      durationString.append(seconds).append("s");
      return durationString.toString();
    }

    return "";
  }

  public static OffsetDateTime getDateFromString(String stringToParse) {
    DateFormat formatter =
        new SimpleDateFormat("yyyy-M-d"); // also works with leading zero in month or days field
    try {
      Date convertedDate = formatter.parse(stringToParse);
      return OffsetDateTime.ofInstant(convertedDate.toInstant(), ZoneOffset.UTC);
    } catch (ParseException e) {
      formatter = new SimpleDateFormat("yyyy-M-d H:m");
      try {
        Date convertedDate = formatter.parse(stringToParse);
        return OffsetDateTime.ofInstant(convertedDate.toInstant(), ZoneOffset.UTC);
      } catch (ParseException e2) {
        e2.printStackTrace();
      }
      e.printStackTrace();
    }
    return null; // can never be reached
  }

  public static Result checkIfDayNotPresentInDtr(
      TimeSlot timeSlot, String dayThatShouldntBePresent) {
    Boolean dayNotPresent = true;
    OffsetDateTime testTime = timeSlot.getStartDateTime();

    if (testTime.getDayOfWeek().toString().equals(dayThatShouldntBePresent)) {
      dayNotPresent = false;
    }

    do {
      if (testTime.getDayOfWeek().toString().equals(dayThatShouldntBePresent)) {
        dayNotPresent = false;
      }
      testTime = testTime.plusDays(1);
    } while (testTime.toEpochSecond() < timeSlot.getEndDateTime().toEpochSecond());

    if (timeSlot.getEndDateTime().getDayOfWeek().toString().equals(dayThatShouldntBePresent)) {
      dayNotPresent = false;
    }

    if (dayNotPresent) {
      Result result = Result.Create(true, null, "", "");
      return result;
    } else {
      Result result = Result.Create(false, null, "", "");
      return result;
    }
  }

  public static String formatForDateTime(OffsetDateTime offsetDateTime) {
    // Todo: if within one week before after, format as yesterday/tomorrow at H:m, last/this Tuesday
    // at H:m
    // otherwise format as date something like Tue 8 Oct 2020 at H:m
    long differenceInSecondsFromDisplayDateTime =
        offsetDateTime.toEpochSecond() - OffsetDateTime.now(ZoneOffset.UTC).toEpochSecond();
    if (differenceInSecondsFromDisplayDateTime <= 0) {
      if (differenceInSecondsFromDisplayDateTime > -60) {
        return "Now";
      }
      if (differenceInSecondsFromDisplayDateTime > -3600) {
        return "Minutes ago";
      }
      long hoursAgo = -differenceInSecondsFromDisplayDateTime % 3600;
      if (hoursAgo < 24) {
        return hoursAgo + " hours ago";
      }
      long daysAgo = -differenceInSecondsFromDisplayDateTime % (3600 * 24);
      if (daysAgo < 30) {
        return daysAgo + " days ago";
      }
    }

    if (offsetDateTime.toEpochSecond() == Constants.END_OF_TIME.toEpochSecond()) {
      return "";
    }
    return offsetDateTime.format(DateTimeFormatter.ofPattern("EEE dd MMM yyyy 'at' H':'mm"));
  }

  public static String formatForDays(
      OffsetDateTime offsetDateTime) { //Todo: work out in more detail next x last week ..day etc
    OffsetDateTime today = OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.DAYS);
    if (offsetDateTime.isEqual(today.minusDays(3))) {
      return "Last ...day";
    }
    if (offsetDateTime.isEqual(today.minusDays(2))) {
      return "Two days ago";
    }
    if (offsetDateTime.isEqual(today.minusDays(1))) {
      return "Yesterday";
    }
    if (offsetDateTime.isEqual(today)) {
      return "Today";
    }
    if (offsetDateTime.isEqual(today.plusDays(1))) {
      return "Tomorrow";
    }
    if (offsetDateTime.isAfter(today.plusDays(1)) && offsetDateTime.isBefore(today.plusDays(11))) {
      switch (offsetDateTime.getDayOfWeek()) {
        case MONDAY:
          return "Monday";
        case TUESDAY:
          return "Tuesday";
        case WEDNESDAY:
          return "Wednesday";
        case THURSDAY:
          return "Thursday";
        case FRIDAY:
          return "Friday";
        case SATURDAY:
          return "Saturday";
        case SUNDAY:
          return "Sunday";
      }
      ;
    }
    if (offsetDateTime.isEqual(today.plusDays(11))) {
      switch (today.plusDays(11).getDayOfWeek()) {
        case MONDAY:
          return "Next week Monday";
        case TUESDAY:
          return "Next week Tuesday";
        case WEDNESDAY:
          return "Next week Wednesday";
        case THURSDAY:
          return "Next week Thursday";
        case FRIDAY:
          return "Next week Friday";
        case SATURDAY:
          return "Next week Saturday";
        case SUNDAY:
          return "Next week Sunday";
      }
      ;
    }
    if (offsetDateTime.isEqual(today.plusDays(11))) {
      switch (today.plusDays(11).getDayOfWeek()) {
        case MONDAY:
          return "Monday in two weeks";
        case TUESDAY:
          return "Tuesday in two weeks";
        case WEDNESDAY:
          return "Wednesday in two weeks";
        case THURSDAY:
          return "Thursday in two weeks";
        case FRIDAY:
          return "Friday in two weeks";
        case SATURDAY:
          return "Saturday in two weeks";
        case SUNDAY:
          return "Sunday in two weeks";
      }
      ;
    }
    return offsetDateTime.toString();
  }

  public static Boolean slotListsEqual(List<TimeSlot> a, List<TimeSlot> b) {
    if (a.size() == 0 && b.size() == 0) {
      return true;
    }

    if (a.size() != b.size()) {
      return false;
    }

    for (TimeSlot slot : a) {
      if (!b.contains(slot)) {
        return false;
      }
    }
    for (TimeSlot slot : b) {
      if (!a.contains(slot)) {
        return false;
      }
    }

    return true;
  }

  public static List<TimeSlot> getCompatibleSlots(
      List<TimeSlot> timeSlotList, List<Constraint> constraintList) {

    if (timeSlotList.size() == 0) {
      return new ArrayList<>(timeSlotList);
    }

    List<TimeSlot> timeSlotsToRemove = new ArrayList<>();

    for (TimeSlot timeSlot : timeSlotList) {
      if (constraintList.size() != 0) {
        for (Constraint constraint : constraintList) {
          if (!constraint.validate(timeSlot)) {
            timeSlotsToRemove.add(timeSlot);
          }
        }
      }
    }

    List<TimeSlot> result = new ArrayList<>(timeSlotList);
    result.removeAll(timeSlotsToRemove);
    return result;
  }

  public static String formatForTimeDuration(OffsetDateTime scheduledStartDateTime, long duration) {
    return scheduledStartDateTime.toLocalTime().toString() + "  -  " + getDurationStringFrom(
        duration);
  }
}
