package nl.freelist.domain.crossCuttingConcerns;

import java.util.Comparator;

public class DurationStartDateTimeKeyComparator implements Comparator<Long> {

  @Override
  public int compare(Long aLong, Long t1) {
    Long leftBitsA = aLong >>> 32;
    Long leftBitsT1 = t1 >>> 32;
    if (leftBitsA == leftBitsT1) {
      Long rightBitsA = aLong << 32 >>> 32;
      Long rightBitsT1 = t1 << 32 >>> 32;
      return (int) (rightBitsA - rightBitsT1);
    }
    return (int) (leftBitsA - leftBitsT1);
  }
}
