package nl.freelist.domain.crossCuttingConcerns;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.util.Date;
import nl.freelist.domain.valueObjects.DateTimeRange;

public class DateHelper {

  public static Date getDateFromString(String stringToParse) {
    DateFormat formatter = new SimpleDateFormat(
        "yyyy-M-d"); //also works with leading zero in month or days field
    try {
      Date convertedDate = formatter.parse(stringToParse);
      return convertedDate;
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return null; //can never be reached
  }

  public static Long getLongFromString(String stringToParse) {
    Date date = getDateFromString(stringToParse);
    long longDate = date.getTime();
    return longDate;
  }

  public static Result checkIfDayNotPresentInDtr(DateTimeRange dateTimeRange,
      String dayThatShouldntBePresent) {
    Boolean dayNotPresent = true;
    OffsetDateTime testTime = dateTimeRange.getStartDateTime();

    if (testTime.getDayOfWeek().toString().equals(dayThatShouldntBePresent)) {
      dayNotPresent = false;
    }

    do {
      if (testTime.getDayOfWeek().toString().equals(dayThatShouldntBePresent)) {
        dayNotPresent = false;
      }
      testTime = testTime.plusDays(1);
    } while (testTime.toEpochSecond() < dateTimeRange.getEndDateTime().toEpochSecond());

    if (dateTimeRange.getEndDateTime().getDayOfWeek().toString().equals(dayThatShouldntBePresent)) {
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

}
