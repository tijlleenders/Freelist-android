package nl.freelist.data.comparators;

import java.util.Comparator;
import nl.freelist.data.dto.CalendarEntry;

public class CalendarEntryComparator implements Comparator<CalendarEntry> {

  @Override
  public int compare(CalendarEntry calendarEntry1, CalendarEntry calendarEntry2) {
    //sorts on date, then time
    int dateComp = calendarEntry1.getDate().compareTo(calendarEntry2.getDate());
    if (dateComp != 0) {
      return dateComp;
    }
    int timeComp = calendarEntry1.getTime().compareTo(calendarEntry2.getTime());
    return timeComp;
  }
}
