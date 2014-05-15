package uk.co.lecafeautomatique.zedogg.viewer;

import uk.co.lecafeautomatique.zedogg.util.DateFormatManager;
import uk.co.lecafeautomatique.zedogg.util.ems.IMarshalJMSToString;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

public class LogTable extends JTable
{
  private static final long serialVersionUID = 3544669594364227894L;
  protected int _rowHeight = 30;
  protected JTextArea _detailTextArea;
  protected int _numCols = 15;
  protected TableColumn[] _tableColumns = new TableColumn[this._numCols];
  protected int[] _colWidths = LogTableColumn.getColWidths();
  protected LogTableColumn[] _colNames = LogTableColumn.getLogTableColumnArray();
  protected int _colDate = 0;
  protected int _colMessageNum = 1;
  protected int _colLevel = 2;
  protected int _colSubject = 3;
  protected int _colTrackingID = 4;
  protected int _colEventClass = 5;
  protected int _colEventReason = 6;
  protected int _colServer = 7;
  protected int _colConnHostname = 8;
  protected int _colConnUsername = 9;
  protected int _colTargetObject = 10;
  protected int _colTargetName = 11;
  protected int _colTargetDest_type = 12;
  protected int _colConnType = 13;

  protected StringBuffer _buf = new StringBuffer();

  public LogTable(JTextArea detailTextArea, IMarshalJMSToString impl)
  {
    init();

    this._detailTextArea = detailTextArea;

    setModel(new FilteredLogTableModel());

    Enumeration columns = getColumnModel().getColumns();
    int i = 0;
    while (columns.hasMoreElements()) {
      TableColumn col = (TableColumn)columns.nextElement();
      col.setCellRenderer(new LogTableRowRenderer());
      col.setPreferredWidth(this._colWidths[i]);

      this._tableColumns[i] = col;
      i++;
    }

    ListSelectionModel rowSM = getSelectionModel();
    rowSM.addListSelectionListener(new LogTableListSelectionListener(this, impl));
  }

  public DateFormatManager getDateFormatManager()
  {
    return getFilteredLogTableModel().getDateFormatManager();
  }

  public void setDateFormatManager(DateFormatManager dfm)
  {
    getFilteredLogTableModel().setDateFormatManager(dfm);
  }

  public int getSubjectColumnID() {
    return this._colSubject;
  }

  public int getDateColumnID() {
    return this._colDate;
  }

  public int getTIDColumnID() {
    return this._colTrackingID;
  }

  public int getConnHostnameColumnID() {
    return this._colConnHostname;
  }

  public synchronized void clearLogRecords()
  {
    getFilteredLogTableModel().clear();
  }

  public FilteredLogTableModel getFilteredLogTableModel() {
    return (FilteredLogTableModel)getModel();
  }

  public void setDetailedView()
  {
    TableColumnModel model = getColumnModel();

    for (int f = 0; f < this._numCols; f++) {
      model.removeColumn(this._tableColumns[f]);
    }

    for (int i = 0; i < this._numCols - 1; i++) {
      model.addColumn(this._tableColumns[i]);
    }

    sizeColumnsToFit(-1);
  }

  public void setView(List columns) {
    TableColumnModel model = getColumnModel();

    for (int f = 0; f < this._numCols; f++) {
      model.removeColumn(this._tableColumns[f]);
    }
    Iterator selectedColumns = columns.iterator();
    Vector columnNameAndNumber = getColumnNameAndNumber();
    while (selectedColumns.hasNext())
    {
      model.addColumn(this._tableColumns[columnNameAndNumber.indexOf(selectedColumns.next())]);
    }

    sizeColumnsToFit(-1);
  }

  public void setFont(Font font) {
    super.setFont(font);
    Graphics g = getGraphics();
    if (g != null) {
      FontMetrics fm = g.getFontMetrics(font);
      int height = fm.getHeight();
      this._rowHeight = (height + height / 3);
      setRowHeight(this._rowHeight);
    }
  }

  protected void init()
  {
    setRowHeight(this._rowHeight);
    setSelectionMode(0);
  }

  protected Vector getColumnNameAndNumber()
  {
    Vector columnNameAndNumber = new Vector();
    for (int i = 0; i < this._colNames.length; i++) {
      columnNameAndNumber.add(i, this._colNames[i]);
    }
    return columnNameAndNumber;
  }

  protected int getColumnWidth(String name) {
    try {
      for (int i = 0; i < this._numCols; i++)
        if (this._colNames[i].getLabel().equalsIgnoreCase(name)) {
          TableColumnModel model = getColumnModel();
          return model.getColumn(i).getPreferredWidth();
        }
    }
    catch (Exception ex) {
      return 0;
    }

    return 0;
  }

  void setColumnWidth(String name, int width) {
    try {
      for (int i = 0; i < this._numCols; i++)
        if (this._colNames[i].getLabel().equalsIgnoreCase(name)) {
          TableColumnModel model = getColumnModel();
          model.getColumn(i).setPreferredWidth(width);
        }
    }
    catch (Exception ex)
    {
    }
  }

  public int getMsgColumnID()
  {
    return 999;
  }

  class LogTableListSelectionListener
    implements ListSelectionListener
  {
    protected JTable _table;
    IMarshalJMSToString _impl;

    public LogTableListSelectionListener(JTable table, IMarshalJMSToString impl)
    {
      this._table = table;
      this._impl = impl;
    }

    public void valueChanged(ListSelectionEvent e)
    {
      if (e.getValueIsAdjusting()) {
        return;
      }

      ListSelectionModel lsm = (ListSelectionModel)e.getSource();
      if (!lsm.isSelectionEmpty())
      {
        synchronized (LogTable.this._buf) {
          LogTable.this._buf.setLength(0);
          int selectedRow = lsm.getMinSelectionIndex();

          Object obj = this._table.getModel().getValueAt(selectedRow, ((LogTable)this._table).getMsgColumnID());

          if (obj != null) {
            try {
              LogTable.this._buf.append(this._impl.JMSMsgToString((Message)obj, ""));
            }
            catch (JMSException e1) {
              LogTable.this._buf.append(e1.getMessage());
            }
          }

          LogTable.this._detailTextArea.setText(LogTable.this._buf.toString());

          LogTable.this._detailTextArea.setCaretPosition(0);
        }
      }
    }
  }
}
