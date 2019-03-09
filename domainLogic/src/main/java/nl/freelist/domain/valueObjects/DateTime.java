package nl.freelist.domain.valueObjects;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class DateTime {

  private String dateTime;

  private DateTime() {
  }

  @Override
  public String toString() {
    return dateTime;
  }

  public static DateTime Create(String parameter) {
    DateTime dateTime = new DateTime();
    OffsetDateTime utc = null;
    switch (parameter) {
      case "1 year":
        utc = OffsetDateTime.now(ZoneOffset.UTC);
        OffsetDateTime utcPlusAYear = utc.plusYears(1);
        dateTime.dateTime = utc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return dateTime;
      case "now":
      default:
        utc = OffsetDateTime.now(ZoneOffset.UTC);
        dateTime.dateTime = utc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return dateTime;
    }
  }
}
