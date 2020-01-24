package nl.freelist.domain.crossCuttingConcerns;

import static org.junit.Assert.assertEquals;

public class TimeHelperTest {

  @org.junit.Test
  public void getDurationStringFromInt() {
    assertEquals("", TimeHelper.getDurationStringFrom(0));

    assertEquals("1y", TimeHelper.getDurationStringFrom(24 * 3600 * 365 + 3600 * 6));
    assertEquals("1y1w",
        TimeHelper.getDurationStringFrom(24 * 3600 * 365 + 3600 * 6 + 24 * 3600 * 7));
    assertEquals("1y1w", TimeHelper
        .getDurationStringFrom(24 * 3600 * 365 + 3600 * 6 + 24 * 3600 * 7 + 24 * 3600 * 3));
    assertEquals("1y2w", TimeHelper
        .getDurationStringFrom(24 * 3600 * 365 + 3600 * 6 + 24 * 3600 * 7 + 24 * 3600 * 4));

    assertEquals("1w5d", TimeHelper.getDurationStringFrom(24 * 3600 * 7 + 24 * 3600 * 5));
    assertEquals("1w6d",
        TimeHelper.getDurationStringFrom(24 * 3600 * 7 + 24 * 3600 * 5 + 13 * 3600));

    assertEquals("1d5h", TimeHelper.getDurationStringFrom(24 * 3600 + 5 * 3600));
    assertEquals("1d6h", TimeHelper.getDurationStringFrom(24 * 3600 + 5 * 3600 + 1800));

    assertEquals("1h", TimeHelper.getDurationStringFrom(3600));
    assertEquals("1h30m", TimeHelper.getDurationStringFrom(3600 + 29 * 60 + 30));
    assertEquals("1h5m", TimeHelper.getDurationStringFrom(3600 + 5 * 60));

    assertEquals("5m", TimeHelper.getDurationStringFrom(300));
    assertEquals("29m30s", TimeHelper.getDurationStringFrom(29 * 60 + 30));
    assertEquals("1m5s", TimeHelper.getDurationStringFrom(65));

    assertEquals("5s", TimeHelper.getDurationStringFrom(5));
  }
}