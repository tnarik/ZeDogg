package uk.co.lecafeautomatique.zedogg.gui;


import uk.co.lecafeautomatique.zedogg.jms.LogRecord;
import uk.co.lecafeautomatique.zedogg.util.DateFormatManager;
import uk.co.lecafeautomatique.zedogg.util.HTMLEncoder;
import uk.co.lecafeautomatique.zedogg.util.LogRecordFilter;
import uk.co.lecafeautomatique.zedogg.util.PassingLogRecordFilter;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.swing.table.AbstractTableModel;

public class FilteredLogTableModel extends AbstractTableModel {
  protected LogRecordFilter _filter = new PassingLogRecordFilter();
  protected List<LogRecord> _allRecords = new ArrayList();
  protected List<LogRecord> _filteredRecords;
  protected int _maxNumberOfLogRecords = 5000;
  protected String[] _colNames = LogTableColumn.getColNames();

  protected DateFormatManager _dfm = null;
  static int _lastHTMLBufLength = 1000;
  protected final Date _conversionDate = new Date();
  protected final StringBuffer _conversionStrBuf = new StringBuffer(15);
  protected final StringBuffer _outStrBuf = new StringBuffer(15);

  public void setDateFormatManager(DateFormatManager dfm) {
    if (dfm != null)
      this._dfm = dfm;
  }

  public DateFormatManager getDateFormatManager() {
    return this._dfm;
  }

  public void setLogRecordFilter(LogRecordFilter filter) {
    this._filter = filter;
  }

  public LogRecordFilter getLogRecordFilter() {
    return this._filter;
  }

  public String getColumnName(int i) {
    return this._colNames[i];
  }

  public int getColumnCount() {
    return this._colNames.length;
  }

  public int getRowCount() {
    return getFilteredRecords().size();
  }

  public int getTotalRowCount() {
    return this._allRecords.size();
  }

  public Object getValueAt(int row, int col) {
    LogRecord record = getFilteredRecord(row);
    return getColumn(col, record);
  }

  public void setMaxNumberOfLogRecords(int maxNumRecords) {
    if (maxNumRecords > 0)
      this._maxNumberOfLogRecords = maxNumRecords;
  }

  public synchronized boolean addLogRecord(LogRecord record) {
    this._allRecords.add(record);

    if (!this._filter.passes(record)) {
      return false;
    }
    getFilteredRecords().add(record);
    fireTableRowsInserted(getRowCount(), getRowCount());
    trimRecords();
    return true;
  }

  public synchronized void refresh() {
    this._filteredRecords = createFilteredRecordsList();
    fireTableDataChanged();
  }

  public synchronized void fastRefresh() {
    this._filteredRecords.remove(0);
    fireTableRowsDeleted(0, 0);
  }

  public synchronized void clear() {
    this._allRecords.clear();
    this._filteredRecords.clear();
    fireTableDataChanged();
  }

  protected List<LogRecord> getFilteredRecords() {
    if (this._filteredRecords == null) {
      refresh();
    }
    return this._filteredRecords;
  }

  protected List<LogRecord> createFilteredRecordsList() {
    List result = new ArrayList();
    Iterator records = this._allRecords.iterator();

    while (records.hasNext()) {
      LogRecord current = (LogRecord) records.next();
      if (this._filter.passes(current)) {
        result.add(current);
      }
    }
    return result;
  }

  public StringBuffer createFilteredHTMLTable(DateFormatManager dfMgr) {
    StringBuffer strbuf = new StringBuffer(_lastHTMLBufLength);
    Iterator records = this._filteredRecords.iterator();
    StringBuffer buffer = new StringBuffer();

    addHtmlTableHeaderString(strbuf, dfMgr);
    while (records.hasNext()) {
      LogRecord current = (LogRecord) records.next();

      strbuf.append("<tr>\n\t");
      addHTMLTDString(current, strbuf, dfMgr, buffer);
      strbuf.append("\n</tr>\n");
    }
    strbuf.append("</table>");

    _lastHTMLBufLength = strbuf.length() + 2;

    return strbuf;
  }

  public StringBuffer createFilteredTextFromMsg() {
    StringBuffer strbuf = new StringBuffer();
    Iterator records = this._filteredRecords.iterator();

    while (records.hasNext()) {
      LogRecord current = (LogRecord) records.next();
      strbuf.append('\n');
      strbuf.append(current.getMessage());
    }
    strbuf.append('\n');

    return strbuf;
  }

  protected void addHTMLTDString(LogRecord lr, StringBuffer buf, DateFormatManager dfMgr, StringBuffer Tbuffer) {
    if (lr != null) {
      for (int i = 0; i < getColumnCount(); i++) {
        buf.append("<td>");

        addColumnToStringBuffer(buf, i, lr, dfMgr);
        buf.append("</td>");
      }

      buf.append("<td><code>");
      Tbuffer.setLength(0);
      addColumnToStringBuffer(Tbuffer, 999, lr, dfMgr);
      HTMLEncoder.encodeStringBuffer(Tbuffer);
      buf.append(Tbuffer);
      buf.append("</code></td>");
    } else {
      buf.append("<td></td>");
    }
  }

  protected void addHtmlTableHeaderString(StringBuffer buf, DateFormatManager dfMgr) {
    buf.append("<table border=\"1\" width=\"100%\">\n");
    buf.append("<tr>\n");

    for (int i = 0; i < getColumnCount(); i++) {
      buf.append("\t<th align=\"left\" bgcolor=\"#C0C0C0\" bordercolor=\"#FFFFFF\">");

      buf.append(getColumnName(i));

      if (i == 0) {
        buf.append("<br>(");
        buf.append(dfMgr.getPattern());
        buf.append(')');
      }
      buf.append("</th>\n");
    }
    buf.append("\t<th align=\"left\" bgcolor=\"#C0C0C0\" bordercolor=\"#FFFFFF\">");
    buf.append("Message</th>\n");
    buf.append("</tr>\n");
  }

  protected LogRecord getFilteredRecord(int row) {
    List records = getFilteredRecords();
    int size = records.size();
    if (row < size) {
      return (LogRecord) records.get(row);
    }

    return (LogRecord) records.get(size - 1);
  }

  protected Object getColumn(int col, LogRecord lr, DateFormatManager dfm) {
    if (lr == null) {
      return "NULL";
    }

    switch (col) {
    case 0:
      synchronized (this._conversionDate) {
        this._conversionDate.setTime(lr.getMillis());
        this._conversionStrBuf.setLength(0);
        this._outStrBuf.setLength(0);
        this._outStrBuf.append(dfm.format(this._conversionDate, this._conversionStrBuf));
        return this._outStrBuf.toString();
      }
    case 1:
      return String.valueOf(lr.getSequenceNumber());
    case 2:
      return lr.getType();
    case 3:
      return lr.getJMSDestination();
    case 4:
      return lr.getJMSCorrelationID();
    case 5:
      return lr.getEventClass();
    case 6:
      return lr.getEventReason();
    case 7:
      return lr.getServer();
    case 8:
      return lr.getConnectionHostName();
    case 9:
      return lr.getConnectionUserName();
    case 10:
      return lr.getTargetObject();
    case 11:
      return lr.getTargetName();
    case 12:
      return lr.getTargetDestType();
    case 13:
      return lr.getConnectionType();
    case 999:
      return lr.getMessage();
     default:
    }
    String message = "The column number " + col + " must be between 0 and 13";
    throw new IllegalArgumentException(message);
  }

  protected void addColumnToStringBuffer(StringBuffer sb, int col, LogRecord lr, DateFormatManager dfm) {
    if (lr == null) {
      sb.append("NULL Column");
    }

    switch (col) {
    case 0:
      synchronized (this._conversionDate) {
        this._conversionDate.setTime(lr.getMillis());
        this._conversionStrBuf.setLength(0);
        sb.append(dfm.format(this._conversionDate, this._conversionStrBuf));
      }
      break;
    case 1:
      sb.append(lr.getSequenceNumber());
      break;
    case 2:
      sb.append(lr.getType().toString());
      break;
    case 3:
      sb.append(lr.getJMSDestinationAsStringBuffer());
      break;
    case 4:
      sb.append(lr.getJMSCorrelationID());
      break;
    case 5:
      sb.append(lr.getEventClass());
      break;
    case 6:
      sb.append(lr.getEventReason());
      break;
    case 7:
      sb.append(lr.getServer());
      break;
    case 8:
      sb.append(lr.getConnectionHostName());
      break;
    case 9:
      sb.append(lr.getConnectionUserName());
      break;
    case 10:
      sb.append(lr.getTargetObject());
      break;
    case 11:
      sb.append(lr.getTargetName());
      break;
    case 12:
      sb.append(lr.getTargetDestType());
      break;
    case 13:
      sb.append(lr.getConnectionType());
      break;
    case 999:
      sb.append(lr.getMessageAsStringBuffer());
      break;
    default:
      String message = "The column number " + col + " must be between 0 and 4";
      throw new IllegalArgumentException(message);
    }
  }

  protected void addColumnToStringBuffer(StringBuffer sb, int col, LogRecord lr) {
    addColumnToStringBuffer(sb, col, lr, this._dfm);
  }

  protected Object getColumn(int col, LogRecord lr) {
    return getColumn(col, lr, this._dfm);
  }

  protected void trimRecords() {
    if (_allRecords.size() > _maxNumberOfLogRecords) {
      synchronized (_allRecords) {
        int trim = (_allRecords.size() - _maxNumberOfLogRecords);
        if (trim > 1) {
          List oldRecords = _allRecords.subList(0, trim);

          /*Iterator records = oldRecords.iterator();
          while (records.hasNext()) {
            LogRecord.freeInstance((LogRecord) records.next());
          }*/
          oldRecords.clear();
          refresh();
        } else {
          _allRecords.remove(0);
          fastRefresh();
        }
      }
    }
  }

}
