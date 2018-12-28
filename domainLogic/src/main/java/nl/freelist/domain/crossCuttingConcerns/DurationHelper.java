package nl.freelist.domain.crossCuttingConcerns;

public class DurationHelper { //Todo: move to value object in domain model

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

  public static String getDurationStringFromInt(int duration) {

    int years = (duration) / (3600 * 24 * 365 + 3600 * 6);
    int weeks = (duration % (3600 * 24 * 365 + 3600 * 6)) / (3600 * 24 * 7);
    int days = ((duration % (3600 * 24 * 365 + 3600 * 6)) % (3600 * 24 * 7)) / (3600 * 24);
    int hours = (duration % (3600 * 24)) / 3600;
    int minutes = (duration % 3600) / 60;
    int seconds = (duration % 60);

    StringBuilder durationString = new StringBuilder();
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

    if (years != 0) {
      durationString.append(years).append("y");
      if (weeks != 0) {
        durationString.append(weeks).append("w");
      }
      return durationString.toString();
    }

    if (weeks != 0) {
      durationString.append(weeks).append("w");
      if (days != 0) {
        durationString.append(days).append("d");
      }
      return durationString.toString();
    }

    if (days != 0) {
      durationString.append(days).append("d");
      if (hours != 0) {
        durationString.append(hours).append("h");
      }
      return durationString.toString();
    }

    if (hours != 0) {
      durationString.append(hours).append("h");
      if (minutes != 0) {
        durationString.append(minutes).append("m");
      }
      return durationString.toString();
    }

    if (minutes != 0) {
      durationString.append(minutes).append("m");
      if (seconds != 0) {
        durationString.append(seconds).append("s");
      }
      return durationString.toString();
    }

    if (seconds != 0) {
      durationString.append(seconds).append("s");
      return durationString.toString();
    }

    return "error";
  }

}