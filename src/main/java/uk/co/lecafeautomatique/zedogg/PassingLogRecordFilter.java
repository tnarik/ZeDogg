package uk.co.lecafeautomatique.zedogg;

public class PassingLogRecordFilter
  implements LogRecordFilter
{
  public boolean passes(LogRecord record)
  {
    return true;
  }

  public void reset()
  {
  }
}
