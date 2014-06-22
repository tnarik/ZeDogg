package uk.co.lecafeautomatique.zedogg.util;

import uk.co.lecafeautomatique.zedogg.jms.LogRecord;

public class PassingLogRecordFilter implements LogRecordFilter {
  public boolean passes(LogRecord record) {
    return true;
  }

  public void reset() {
  }
}
