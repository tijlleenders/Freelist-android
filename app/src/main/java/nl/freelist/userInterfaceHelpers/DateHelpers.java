package nl.freelist.userInterfaceHelpers;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateHelpers {
  public static Date parseDateString(String stringToParse){
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
}
