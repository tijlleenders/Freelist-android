package nl.freelist.data.comparators;

import java.util.Comparator;
import nl.freelist.data.dto.ViewModelAppointment;

public class CalendarEntryComparator implements Comparator<ViewModelAppointment> {

  @Override
  public int compare(
      ViewModelAppointment viewModelAppointment1, ViewModelAppointment viewModelAppointment2) {
    //sorts on date, then time
    int dateComp = viewModelAppointment1.getDate().compareTo(viewModelAppointment2.getDate());
    if (dateComp != 0) {
      return dateComp;
    }
    int timeComp = viewModelAppointment1.getTime().compareTo(viewModelAppointment2.getTime());
    return timeComp;
  }
}
