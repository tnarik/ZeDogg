/*     */ package emssn00p.viewer;
/*     */ 
/*     */ import emssn00p.util.DateFormatManager;
/*     */ import emssn00p.util.ems.IMarshalJMSToString;
/*     */ import java.awt.Font;
/*     */ import java.awt.FontMetrics;
/*     */ import java.awt.Graphics;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Vector;
/*     */ import javax.jms.JMSException;
/*     */ import javax.jms.Message;
/*     */ import javax.swing.JTable;
/*     */ import javax.swing.JTextArea;
/*     */ import javax.swing.ListSelectionModel;
/*     */ import javax.swing.event.ListSelectionEvent;
/*     */ import javax.swing.event.ListSelectionListener;
/*     */ import javax.swing.table.TableColumn;
/*     */ import javax.swing.table.TableColumnModel;
/*     */ import javax.swing.table.TableModel;
/*     */ 
/*     */ public class LogTable extends JTable
/*     */ {
/*     */   private static final long serialVersionUID = 3544669594364227894L;
/*  56 */   protected int _rowHeight = 30;
/*     */   protected JTextArea _detailTextArea;
/*  60 */   protected int _numCols = 15;
/*  61 */   protected TableColumn[] _tableColumns = new TableColumn[this._numCols];
/*  62 */   protected int[] _colWidths = LogTableColumn.getColWidths();
/*  63 */   protected LogTableColumn[] _colNames = LogTableColumn.getLogTableColumnArray();
/*  64 */   protected int _colDate = 0;
/*  65 */   protected int _colMessageNum = 1;
/*  66 */   protected int _colLevel = 2;
/*  67 */   protected int _colSubject = 3;
/*  68 */   protected int _colTrackingID = 4;
/*  69 */   protected int _colEventClass = 5;
/*  70 */   protected int _colEventReason = 6;
/*  71 */   protected int _colServer = 7;
/*  72 */   protected int _colConnHostname = 8;
/*  73 */   protected int _colConnUsername = 9;
/*  74 */   protected int _colTargetObject = 10;
/*  75 */   protected int _colTargetName = 11;
/*  76 */   protected int _colTargetDest_type = 12;
/*  77 */   protected int _colConnType = 13;
/*     */ 
/*  79 */   protected StringBuffer _buf = new StringBuffer();
/*     */ 
/*     */   public LogTable(JTextArea detailTextArea, IMarshalJMSToString impl)
/*     */   {
/*  93 */     init();
/*     */ 
/*  95 */     this._detailTextArea = detailTextArea;
/*     */ 
/*  97 */     setModel(new FilteredLogTableModel());
/*     */ 
/*  99 */     Enumeration columns = getColumnModel().getColumns();
/* 100 */     int i = 0;
/* 101 */     while (columns.hasMoreElements()) {
/* 102 */       TableColumn col = (TableColumn)columns.nextElement();
/* 103 */       col.setCellRenderer(new LogTableRowRenderer());
/* 104 */       col.setPreferredWidth(this._colWidths[i]);
/*     */ 
/* 106 */       this._tableColumns[i] = col;
/* 107 */       i++;
/*     */     }
/*     */ 
/* 111 */     ListSelectionModel rowSM = getSelectionModel();
/* 112 */     rowSM.addListSelectionListener(new LogTableListSelectionListener(this, impl));
/*     */   }
/*     */ 
/*     */   public DateFormatManager getDateFormatManager()
/*     */   {
/* 125 */     return getFilteredLogTableModel().getDateFormatManager();
/*     */   }
/*     */ 
/*     */   public void setDateFormatManager(DateFormatManager dfm)
/*     */   {
/* 133 */     getFilteredLogTableModel().setDateFormatManager(dfm);
/*     */   }
/*     */ 
/*     */   public int getSubjectColumnID() {
/* 137 */     return this._colSubject;
/*     */   }
/*     */ 
/*     */   public int getDateColumnID() {
/* 141 */     return this._colDate;
/*     */   }
/*     */ 
/*     */   public int getTIDColumnID() {
/* 145 */     return this._colTrackingID;
/*     */   }
/*     */ 
/*     */   public int getConnHostnameColumnID() {
/* 149 */     return this._colConnHostname;
/*     */   }
/*     */ 
/*     */   public synchronized void clearLogRecords()
/*     */   {
/* 157 */     getFilteredLogTableModel().clear();
/*     */   }
/*     */ 
/*     */   public FilteredLogTableModel getFilteredLogTableModel() {
/* 161 */     return (FilteredLogTableModel)getModel();
/*     */   }
/*     */ 
/*     */   public void setDetailedView()
/*     */   {
/* 166 */     TableColumnModel model = getColumnModel();
/*     */ 
/* 168 */     for (int f = 0; f < this._numCols; f++) {
/* 169 */       model.removeColumn(this._tableColumns[f]);
/*     */     }
/*     */ 
/* 172 */     for (int i = 0; i < this._numCols - 1; i++) {
/* 173 */       model.addColumn(this._tableColumns[i]);
/*     */     }
/*     */ 
/* 176 */     sizeColumnsToFit(-1);
/*     */   }
/*     */ 
/*     */   public void setView(List columns) {
/* 180 */     TableColumnModel model = getColumnModel();
/*     */ 
/* 183 */     for (int f = 0; f < this._numCols; f++) {
/* 184 */       model.removeColumn(this._tableColumns[f]);
/*     */     }
/* 186 */     Iterator selectedColumns = columns.iterator();
/* 187 */     Vector columnNameAndNumber = getColumnNameAndNumber();
/* 188 */     while (selectedColumns.hasNext())
/*     */     {
/* 190 */       model.addColumn(this._tableColumns[columnNameAndNumber.indexOf(selectedColumns.next())]);
/*     */     }
/*     */ 
/* 194 */     sizeColumnsToFit(-1);
/*     */   }
/*     */ 
/*     */   public void setFont(Font font) {
/* 198 */     super.setFont(font);
/* 199 */     Graphics g = getGraphics();
/* 200 */     if (g != null) {
/* 201 */       FontMetrics fm = g.getFontMetrics(font);
/* 202 */       int height = fm.getHeight();
/* 203 */       this._rowHeight = (height + height / 3);
/* 204 */       setRowHeight(this._rowHeight);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void init()
/*     */   {
/* 214 */     setRowHeight(this._rowHeight);
/* 215 */     setSelectionMode(0);
/*     */   }
/*     */ 
/*     */   protected Vector getColumnNameAndNumber()
/*     */   {
/* 220 */     Vector columnNameAndNumber = new Vector();
/* 221 */     for (int i = 0; i < this._colNames.length; i++) {
/* 222 */       columnNameAndNumber.add(i, this._colNames[i]);
/*     */     }
/* 224 */     return columnNameAndNumber;
/*     */   }
/*     */ 
/*     */   protected int getColumnWidth(String name) {
/*     */     try {
/* 229 */       for (int i = 0; i < this._numCols; i++)
/* 230 */         if (this._colNames[i].getLabel().equalsIgnoreCase(name)) {
/* 231 */           TableColumnModel model = getColumnModel();
/* 232 */           return model.getColumn(i).getPreferredWidth();
/*     */         }
/*     */     }
/*     */     catch (Exception ex) {
/* 236 */       return 0;
/*     */     }
/*     */ 
/* 239 */     return 0;
/*     */   }
/*     */ 
/*     */   void setColumnWidth(String name, int width) {
/*     */     try {
/* 244 */       for (int i = 0; i < this._numCols; i++)
/* 245 */         if (this._colNames[i].getLabel().equalsIgnoreCase(name)) {
/* 246 */           TableColumnModel model = getColumnModel();
/* 247 */           model.getColumn(i).setPreferredWidth(width);
/*     */         }
/*     */     }
/*     */     catch (Exception ex)
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   public int getMsgColumnID()
/*     */   {
/* 331 */     return 999;
/*     */   }
/*     */ 
/*     */   class LogTableListSelectionListener
/*     */     implements ListSelectionListener
/*     */   {
/*     */     protected JTable _table;
/*     */     IMarshalJMSToString _impl;
/*     */ 
/*     */     public LogTableListSelectionListener(JTable table, IMarshalJMSToString impl)
/*     */     {
/* 269 */       this._table = table;
/* 270 */       this._impl = impl;
/*     */     }
/*     */ 
/*     */     public void valueChanged(ListSelectionEvent e)
/*     */     {
/* 275 */       if (e.getValueIsAdjusting()) {
/* 276 */         return;
/*     */       }
/*     */ 
/* 279 */       ListSelectionModel lsm = (ListSelectionModel)e.getSource();
/* 280 */       if (!lsm.isSelectionEmpty())
/*     */       {
/* 283 */         synchronized (LogTable.this._buf) {
/* 284 */           LogTable.this._buf.setLength(0);
/* 285 */           int selectedRow = lsm.getMinSelectionIndex();
/*     */ 
/* 305 */           Object obj = this._table.getModel().getValueAt(selectedRow, ((LogTable)this._table).getMsgColumnID());
/*     */ 
/* 307 */           if (obj != null) {
/*     */             try {
/* 309 */               LogTable.this._buf.append(this._impl.JMSMsgToString((Message)obj, ""));
/*     */             }
/*     */             catch (JMSException e1) {
/* 312 */               LogTable.this._buf.append(e1.getMessage());
/*     */             }
/*     */           }
/*     */ 
/* 316 */           LogTable.this._detailTextArea.setText(LogTable.this._buf.toString());
/*     */ 
/* 318 */           LogTable.this._detailTextArea.setCaretPosition(0);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/tnarik/Desktop/emssn00p.jar
 * Qualified Name:     emssn00p.viewer.LogTable
 * JD-Core Version:    0.6.1
 */