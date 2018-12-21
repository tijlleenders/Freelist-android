package nl.freelist.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.NumberPicker;

public class NumberPickerDuration extends NumberPicker {

  public NumberPickerDuration(Context context) {
    super(context);
  }

  public NumberPickerDuration(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public NumberPickerDuration(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  public NumberPickerDuration(Context context, AttributeSet attrs, int defStyleAttr,
      int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
  }

  public int getNumberPickerPosition(
      String formattedDuration) {
    int pickerPosition = 1;
    switch (formattedDuration) {
      case "5m":
        pickerPosition = 1;
        break;
      case "15m":
        pickerPosition = 2;
        break;
      case "45m":
        pickerPosition = 3;
        break;
      case "2h":
        pickerPosition = 4;
        break;
      case "4h":
        pickerPosition = 5;
        break;
      case "8h":
        pickerPosition = 6;
        break;
      case "12h":
        pickerPosition = 7;
        break;
      case "24h":
        pickerPosition = 8;
        break;
      default:
        pickerPosition = 1; //Todo: what if user can customize their durations, then available choices on numberpicker will not match values from repository...
        break;
    }
    return pickerPosition;
  }


  public int getNumberPicker(
      int pickSelected) {
    int seconds;
    switch (pickSelected) {
      case 1:
        seconds = 300;
        break;
      case 2:
        seconds = 900;
        break;
      case 3:
        seconds = 2700;
        break;
      case 4:
        seconds = 7200;
        break;
      case 5:
        seconds = 14400;
        break;
      case 6:
        seconds = 28800;
        break;
      case 7:
        seconds = 43200;
        break;
      case 8:
        seconds = 86400;
        break;
      default:
        seconds = 540;
        break;
    }
    return seconds;
  }

}
