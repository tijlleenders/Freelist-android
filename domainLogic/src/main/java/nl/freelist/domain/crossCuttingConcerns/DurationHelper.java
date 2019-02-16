package nl.freelist.domain.crossCuttingConcerns;

import nl.freelist.domain.events.EntryDurationChangedEvent;

public class DurationHelper { //Todo: move to value object in domain model

  public static int getDurationIntFromInts(int years, int weeks, int days, int hours, int minutes,
      int seconds) {
    int result = 0;
    result += years * (3600 * 24 * 365 + 3600 * 6);
    result += weeks * (3600 * 24 * 7);
    result += days * (3600 * 24);
    result += hours * 3600;
    result += minutes * 60;
    result += seconds;
    return result;
  }

  public static int getDurationSecondsDeltaFromDurationChangedEvent(
      EntryDurationChangedEvent entryDurationChangedEvent) {
    int multiplier = 0;
    switch (entryDurationChangedEvent.getUnitOfMeasure()) {
      case "seconds":
        break;
      case "minutes":
        multiplier = 60;
        break;
      case "hours":
        multiplier = 3600;
        break;
      case "days":
        multiplier = 3600 * 24;
        break;
      case "weeks":
        multiplier = 3600 * 24 * 7;
        break;
      case "years":
        multiplier = (int) (3600 * 24 * 7 * 365.25);
      default:
        break;
    }
    return multiplier * (entryDurationChangedEvent.getDurationAfter() - entryDurationChangedEvent
        .getDurationBefore());
  }

  public static int getYearsIntFrom(int duration) {
    return (duration) / (3600 * 24 * 365 + 3600 * 6);
  }

  public static int getWeeksIntFrom(int duration) {
    return (duration % (3600 * 24 * 365 + 3600 * 6)) / (3600 * 24 * 7);
  }

  public static int getDaysIntFrom(int duration) {
    return ((duration % (3600 * 24 * 365 + 3600 * 6)) % (3600 * 24 * 7)) / (3600 * 24);
  }

  public static int getHoursIntFrom(int duration) {
    return (duration % (3600 * 24)) / 3600;
  }

  public static int getMinutesIntFrom(int duration) {
    return (duration % 3600) / 60;
  }

  public static int getSecondsIntFrom(int duration) {
    return (duration % 60);
  }

  public static String getDurationStringFromInt(int duration) {

    int years = getYearsIntFrom(duration);
    int weeks = getWeeksIntFrom(duration);
    int days = getDaysIntFrom(duration);
    int hours = getHoursIntFrom(duration);
    int minutes = getMinutesIntFrom(duration);
    int seconds = getSecondsIntFrom(duration);

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

    return "";
  }

}