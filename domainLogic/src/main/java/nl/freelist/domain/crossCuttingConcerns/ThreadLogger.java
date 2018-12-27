package nl.freelist.domain.crossCuttingConcerns;

import java.util.logging.Logger;

public class ThreadLogger {

  private final static Logger LOGGER = Logger.getLogger(ThreadLogger.class.getName());

  public static String getThreadSignature() {
    Thread t = Thread.currentThread();
    long l = t.getId();
    String name = t.getName();
    long p = t.getPriority();
    String gname = t.getThreadGroup().getName();
    return (name + ":(id)" + l + ":(priority)" + p
        + ":(group)" + gname);
  }

  public static void logThreadSignature(String tag) {
    LOGGER.info(tag + getThreadSignature());
  }

  public static void sleepForInSecs(int secs) {
    try {
      Thread.sleep(secs * 1000);
    } catch (InterruptedException x) {
      throw new RuntimeException("interrupted", x);
    }
  }


}
