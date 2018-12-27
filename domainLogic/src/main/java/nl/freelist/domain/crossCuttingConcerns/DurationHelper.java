package nl.freelist.domain.crossCuttingConcerns;

public class DurationHelper {

  public static int getDurationIntFromString(String durationString) {
    int durationInt = 1;
    switch (durationString) {
      case "5m":
        durationInt = 1;
        break;
      case "15m":
        durationInt = 2;
        break;
      case "45m":
        durationInt = 3;
        break;
      case "2h":
        durationInt = 4;
        break;
      case "4h":
        durationInt = 5;
        break;
      case "8h":
        durationInt = 6;
        break;
      case "12h":
        durationInt = 7;
        break;
      case "24h":
        durationInt = 8;
        break;
      default:
        durationInt = 1; //Todo: what if user can customize their durations, then available choices on numberpicker will not match values from repository...
        break;
    }
    return durationInt;
  }

}
