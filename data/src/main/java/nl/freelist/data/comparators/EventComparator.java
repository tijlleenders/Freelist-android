package nl.freelist.data.comparators;

import java.util.Comparator;
import nl.freelist.domain.events.Event;

public class EventComparator implements Comparator<Event> {

  @Override
  public int compare(Event event1, Event event2) {
    //sorts on date time all at once
    int comparator = event1.getOccurredDateTime().compareTo(event2.getOccurredDateTime());
    return comparator;
  }
}
