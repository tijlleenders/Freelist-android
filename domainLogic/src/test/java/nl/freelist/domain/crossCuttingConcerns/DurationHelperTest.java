package nl.freelist.domain.crossCuttingConcerns;

import static org.junit.Assert.assertEquals;

public class DurationHelperTest {

  @org.junit.Test
  public void getDurationStringFromInt() {
    assertEquals("", DurationHelper.getDurationStringFromInt(0));

    assertEquals("1y", DurationHelper.getDurationStringFromInt(24 * 3600 * 365 + 3600 * 6));
    assertEquals("1y1w",
        DurationHelper.getDurationStringFromInt(24 * 3600 * 365 + 3600 * 6 + 24 * 3600 * 7));
    assertEquals("1y1w", DurationHelper
        .getDurationStringFromInt(24 * 3600 * 365 + 3600 * 6 + 24 * 3600 * 7 + 24 * 3600 * 3));
    assertEquals("1y2w", DurationHelper
        .getDurationStringFromInt(24 * 3600 * 365 + 3600 * 6 + 24 * 3600 * 7 + 24 * 3600 * 4));

    assertEquals("1w5d", DurationHelper.getDurationStringFromInt(24 * 3600 * 7 + 24 * 3600 * 5));
    assertEquals("1w6d",
        DurationHelper.getDurationStringFromInt(24 * 3600 * 7 + 24 * 3600 * 5 + 13 * 3600));

    assertEquals("1d5h", DurationHelper.getDurationStringFromInt(24 * 3600 + 5 * 3600));
    assertEquals("1d6h", DurationHelper.getDurationStringFromInt(24 * 3600 + 5 * 3600 + 1800));

    assertEquals("1h", DurationHelper.getDurationStringFromInt(3600));
    assertEquals("1h30m", DurationHelper.getDurationStringFromInt(3600 + 29 * 60 + 30));
    assertEquals("1h5m", DurationHelper.getDurationStringFromInt(3600 + 5 * 60));

    assertEquals("5m", DurationHelper.getDurationStringFromInt(300));
    assertEquals("29m30s", DurationHelper.getDurationStringFromInt(29 * 60 + 30));
    assertEquals("1m5s", DurationHelper.getDurationStringFromInt(65));

    assertEquals("5s", DurationHelper.getDurationStringFromInt(5));
  }
}