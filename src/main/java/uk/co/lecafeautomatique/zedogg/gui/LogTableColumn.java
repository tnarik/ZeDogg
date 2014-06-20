package uk.co.lecafeautomatique.zedogg.gui;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LogTableColumn implements Serializable {
  public static final LogTableColumn DATE = new LogTableColumn("Date");
  public static final LogTableColumn MESSAGE_NUM = new LogTableColumn("Msg#");
  public static final LogTableColumn EVENTACTION = new LogTableColumn("EventAction");
  public static final LogTableColumn SUBJECT = new LogTableColumn("Destination");
  public static final LogTableColumn TID = new LogTableColumn("JMSCorrID");

  public static final LogTableColumn EC = new LogTableColumn("EventClass");
  public static final LogTableColumn ER = new LogTableColumn("EventReason");
  public static final LogTableColumn SRV = new LogTableColumn("Server");
  public static final LogTableColumn CH = new LogTableColumn("ConnHostName");
  public static final LogTableColumn CU = new LogTableColumn("ConnUserName");
  public static final LogTableColumn TO = new LogTableColumn("TargetObject");
  public static final LogTableColumn TN = new LogTableColumn("TargetName");
  public static final LogTableColumn TDT = new LogTableColumn("TargetDestType");
  public static final LogTableColumn CT = new LogTableColumn("ConnType");
  protected String _label;
  private static LogTableColumn[] _emssColumns;
  private static Map<String, LogTableColumn> _logTableColumnMap;
  protected static int[] _colWidths = { 8, 1, 30, 150, 40, 10, 10, 10, 10, 10, 10, 10, 10, 10 };
  protected static String[] _colNames;

  public static String[] getColNames() {
    return _colNames;
  }

  public static int[] getColWidths() {
    return _colWidths;
  }

  public LogTableColumn(String label) {
    this._label = label;
  }

  public String getLabel() {
    return this._label;
  }

  public static LogTableColumn valueOf(String column) throws LogTableColumnFormatException {
    LogTableColumn tableColumn = null;
    if (column != null) {
      column = column.trim();
      tableColumn = _logTableColumnMap.get(column);
    }

    if (tableColumn == null) {
      StringBuffer buf = new StringBuffer();
      buf.append("Error while trying to parse (" + column + ") into");
      buf.append(" a LogTableColumn.");
      throw new LogTableColumnFormatException(buf.toString());
    }
    return tableColumn;
  }

  public boolean equals(Object o) {
    if (this == o) return true;
    if ((o instanceof LogTableColumn) && getLabel().equals(((LogTableColumn) o).getLabel())) {
      return true;
    }
    return false;
  }

  public int hashCode() {
    return this._label.hashCode();
  }

  public String toString() {
    return this._label;
  }

  public static List getLogTableColumns() {
    return Arrays.asList(_emssColumns);
  }

  public static LogTableColumn[] getLogTableColumnArray() {
    return _emssColumns;
  }

  static {
    _emssColumns = new LogTableColumn[] { DATE, MESSAGE_NUM, EVENTACTION, SUBJECT, TID, EC, ER, SRV, CH, CU, TO, TN,
        TDT, CT };

    _logTableColumnMap = new HashMap();
    _colNames = new String[_emssColumns.length];

    for (int i = 0; i < _emssColumns.length; i++) {
      _logTableColumnMap.put(_emssColumns[i].getLabel(), _emssColumns[i]);
      _colNames[i] = _emssColumns[i].getLabel();
    }
  }
}
