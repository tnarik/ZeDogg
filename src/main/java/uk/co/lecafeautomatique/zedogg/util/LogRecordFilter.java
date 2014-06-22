package uk.co.lecafeautomatique.zedogg.util;

import uk.co.lecafeautomatique.zedogg.jms.LogRecord;

public abstract interface LogRecordFilter {
  public abstract boolean passes(LogRecord paramLogRecord);
}
