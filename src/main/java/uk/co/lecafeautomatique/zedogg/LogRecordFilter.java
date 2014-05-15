package uk.co.lecafeautomatique.zedogg;

public abstract interface LogRecordFilter
{
  public abstract boolean passes(LogRecord paramLogRecord);
}
