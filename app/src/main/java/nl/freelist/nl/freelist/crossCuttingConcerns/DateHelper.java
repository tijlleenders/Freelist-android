package nl.freelist.nl.freelist.crossCuttingConcerns;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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

}
