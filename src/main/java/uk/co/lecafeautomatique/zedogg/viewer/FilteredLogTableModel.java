/*     */ package uk.co.lecafeautomatique.zedogg.viewer;
/*     */ 
/*     */ import uk.co.lecafeautomatique.zedogg.EventActionType;
/*     */ import uk.co.lecafeautomatique.zedogg.LogRecord;
/*     */ import uk.co.lecafeautomatique.zedogg.LogRecordFilter;
/*     */ import uk.co.lecafeautomatique.zedogg.PassingLogRecordFilter;
/*     */ import uk.co.lecafeautomatique.zedogg.util.DateFormatManager;
/*     */ import uk.co.lecafeautomatique.zedogg.util.HTMLEncoder;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Date;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import javax.swing.table.AbstractTableModel;
/*     */ 
/*     */ public class FilteredLogTableModel extends AbstractTableModel
/*     */ {
/*     */   private static final long serialVersionUID = 3258408417834841649L;
/*  49 */   protected LogRecordFilter _filter = new PassingLogRecordFilter();
/*  50 */   protected List<LogRecord> _allRecords = new ArrayList();
/*     */   protected List<LogRecord> _filteredRecords;
/*  52 */   protected int _maxNumberOfLogRecords = 5000;
/*  53 */   protected String[] _colNames = LogTableColumn.getColNames();
/*     */ 
/*  55 */   protected DateFormatManager _dfm = null;
/*  56 */   static int _lastHTMLBufLength = 1000;
/*  57 */   protected final Date _conversionDate = new Date();
/*  58 */   protected final StringBuffer _conversionStrBuf = new StringBuffer(15);
/*  59 */   protected final StringBuffer _outStrBuf = new StringBuffer(15);
/*     */ 
/*     */   public void setDateFormatManager(DateFormatManager dfm)
/*     */   {
/*  78 */     if (dfm != null)
/*  79 */       this._dfm = dfm;
/*     */   }
/*     */ 
/*     */   public DateFormatManager getDateFormatManager() {
/*  83 */     return this._dfm;
/*     */   }
/*     */ 
/*     */   public void setLogRecordFilter(LogRecordFilter filter) {
/*  87 */     this._filter = filter;
/*     */   }
/*     */ 
/*     */   public LogRecordFilter getLogRecordFilter() {
/*  91 */     return this._filter;
/*     */   }
/*     */ 
/*     */   public String getColumnName(int i) {
/*  95 */     return this._colNames[i];
/*     */   }
/*     */ 
/*     */   public int getColumnCount() {
/*  99 */     return this._colNames.length;
/*     */   }
/*     */ 
/*     */   public int getRowCount() {
/* 103 */     return getFilteredRecords().size();
/*     */   }
/*     */ 
/*     */   public int getTotalRowCount() {
/* 107 */     return this._allRecords.size();
/*     */   }
/*     */ 
/*     */   public Object getValueAt(int row, int col) {
/* 111 */     LogRecord record = getFilteredRecord(row);
/* 112 */     return getColumn(col, record);
/*     */   }
/*     */ 
/*     */   public void setMaxNumberOfLogRecords(int maxNumRecords) {
/* 116 */     if (maxNumRecords > 0)
/* 117 */       this._maxNumberOfLogRecords = maxNumRecords;
/*     */   }
/*     */ 
/*     */   public synchronized boolean addLogRecord(LogRecord record)
/*     */   {
/* 124 */     this._allRecords.add(record);
/*     */ 
/* 126 */     if (!this._filter.passes(record)) {
/* 127 */       return false;
/*     */     }
/* 129 */     getFilteredRecords().add(record);
/* 130 */     fireTableRowsInserted(getRowCount(), getRowCount());
/* 131 */     trimRecords();
/* 132 */     return true;
/*     */   }
/*     */ 
/*     */   public synchronized void refresh()
/*     */   {
/* 140 */     this._filteredRecords = createFilteredRecordsList();
/* 141 */     fireTableDataChanged();
/*     */   }
/*     */ 
/*     */   public synchronized void fastRefresh() {
/* 145 */     this._filteredRecords.remove(0);
/* 146 */     fireTableRowsDeleted(0, 0);
/*     */   }
/*     */ 
/*     */   public synchronized void clear()
/*     */   {
/* 154 */     this._allRecords.clear();
/* 155 */     this._filteredRecords.clear();
/* 156 */     fireTableDataChanged();
/*     */   }
/*     */ 
/*     */   protected List<LogRecord> getFilteredRecords()
/*     */   {
/* 164 */     if (this._filteredRecords == null) {
/* 165 */       refresh();
/*     */     }
/* 167 */     return this._filteredRecords;
/*     */   }
/*     */ 
/*     */   protected List<LogRecord> createFilteredRecordsList() {
/* 171 */     List result = new ArrayList();
/* 172 */     Iterator records = this._allRecords.iterator();
/*     */ 
/* 174 */     while (records.hasNext()) {
/* 175 */       LogRecord current = (LogRecord)records.next();
/* 176 */       if (this._filter.passes(current)) {
/* 177 */         result.add(current);
/*     */       }
/*     */     }
/* 180 */     return result;
/*     */   }
/*     */ 
/*     */   public StringBuffer createFilteredHTMLTable(DateFormatManager dfMgr)
/*     */   {
/* 189 */     StringBuffer strbuf = new StringBuffer(_lastHTMLBufLength);
/* 190 */     Iterator records = this._filteredRecords.iterator();
/* 191 */     StringBuffer buffer = new StringBuffer();
/*     */ 
/* 193 */     addHtmlTableHeaderString(strbuf, dfMgr);
/* 194 */     while (records.hasNext()) {
/* 195 */       LogRecord current = (LogRecord)records.next();
/*     */ 
/* 197 */       strbuf.append("<tr>\n\t");
/* 198 */       addHTMLTDString(current, strbuf, dfMgr, buffer);
/* 199 */       strbuf.append("\n</tr>\n");
/*     */     }
/* 201 */     strbuf.append("</table>");
/*     */ 
/* 204 */     _lastHTMLBufLength = strbuf.length() + 2;
/*     */ 
/* 206 */     return strbuf;
/*     */   }
/*     */ 
/*     */   public StringBuffer createFilteredTextFromMsg()
/*     */   {
/* 214 */     StringBuffer strbuf = new StringBuffer();
/* 215 */     Iterator records = this._filteredRecords.iterator();
/*     */ 
/* 217 */     while (records.hasNext()) {
/* 218 */       LogRecord current = (LogRecord)records.next();
/* 219 */       strbuf.append("\n");
/* 220 */       strbuf.append(current.getMessage());
/*     */     }
/* 222 */     strbuf.append("\n");
/*     */ 
/* 224 */     return strbuf;
/*     */   }
/*     */ 
/*     */   protected void addHTMLTDString(LogRecord lr, StringBuffer buf, DateFormatManager dfMgr, StringBuffer Tbuffer)
/*     */   {
/* 234 */     if (lr != null) {
/* 235 */       for (int i = 0; i < getColumnCount(); i++)
/*     */       {
/* 237 */         buf.append("<td>");
/*     */ 
/* 239 */         addColumnToStringBuffer(buf, i, lr, dfMgr);
/* 240 */         buf.append("</td>");
/*     */       }
/*     */ 
/* 246 */       StringBuffer sb = new StringBuffer();
/* 247 */       buf.append("<td><code>");
/* 248 */       Tbuffer.setLength(0);
/* 249 */       addColumnToStringBuffer(Tbuffer, 999, lr, dfMgr);
/* 250 */       HTMLEncoder.encodeStringBuffer(Tbuffer);
/* 251 */       buf.append(Tbuffer);
/* 252 */       buf.append("</code></td>");
/*     */     }
/*     */     else {
/* 255 */       buf.append("<td></td>");
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void addHtmlTableHeaderString(StringBuffer buf, DateFormatManager dfMgr)
/*     */   {
/* 262 */     buf.append("<table border=\"1\" width=\"100%\">\n");
/* 263 */     buf.append("<tr>\n");
/*     */ 
/* 266 */     for (int i = 0; i < getColumnCount(); i++) {
/* 267 */       buf.append("\t<th align=\"left\" bgcolor=\"#C0C0C0\" bordercolor=\"#FFFFFF\">");
/*     */ 
/* 269 */       buf.append(getColumnName(i));
/*     */ 
/* 272 */       if (i == 0) {
/* 273 */         buf.append("<br>(");
/* 274 */         buf.append(dfMgr.getPattern());
/* 275 */         buf.append(")");
/*     */       }
/* 277 */       buf.append("</th>\n");
/*     */     }
/* 279 */     buf.append("\t<th align=\"left\" bgcolor=\"#C0C0C0\" bordercolor=\"#FFFFFF\">");
/* 280 */     buf.append("Message</th>\n");
/* 281 */     buf.append("</tr>\n");
/*     */   }
/*     */ 
/*     */   protected LogRecord getFilteredRecord(int row) {
/* 285 */     List records = getFilteredRecords();
/* 286 */     int size = records.size();
/* 287 */     if (row < size) {
/* 288 */       return (LogRecord)records.get(row);
/*     */     }
/*     */ 
/* 294 */     return (LogRecord)records.get(size - 1);
/*     */   }
/*     */ 
/*     */   protected Object getColumn(int col, LogRecord lr, DateFormatManager dfm)
/*     */   {
/* 299 */     if (lr == null) {
/* 300 */       return "NULL";
/*     */     }
/*     */ 
/* 303 */     switch (col) {
/*     */     case 0:
/* 305 */       synchronized (this._conversionDate) {
/* 306 */         this._conversionDate.setTime(lr.getMillis());
/* 307 */         this._conversionStrBuf.setLength(0);
/* 308 */         this._outStrBuf.setLength(0);
/* 309 */         this._outStrBuf.append(dfm.format(this._conversionDate, this._conversionStrBuf));
/* 310 */         return this._outStrBuf.toString();
/*     */       }
/*     */     case 1:
/* 313 */       return String.valueOf(lr.getSequenceNumber());
/*     */     case 2:
/* 315 */       return lr.getType();
/*     */     case 3:
/* 317 */       return lr.getJMSDestination();
/*     */     case 4:
/* 319 */       return lr.getJMSCorrelationID();
/*     */     case 5:
/* 321 */       return lr.getEventClass();
/*     */     case 6:
/* 323 */       return lr.getEventReason();
/*     */     case 7:
/* 325 */       return lr.getServer();
/*     */     case 8:
/* 327 */       return lr.getConnHostName();
/*     */     case 9:
/* 329 */       return lr.getConnUserName();
/*     */     case 10:
/* 331 */       return lr.getTargetObject();
/*     */     case 11:
/* 333 */       return lr.getTargetName();
/*     */     case 12:
/* 335 */       return lr.getTargetDestType();
/*     */     case 13:
/* 337 */       return lr.getConnType();
/*     */     case 999:
/* 339 */       return lr.getMessage();
/*     */     }
/* 341 */     String message = "The column number " + col + " must be between 0 and 13";
/* 342 */     throw new IllegalArgumentException(message);
/*     */   }
/*     */ 
/*     */   protected void addColumnToStringBuffer(StringBuffer sb, int col, LogRecord lr, DateFormatManager dfm)
/*     */   {
/* 347 */     if (lr == null) {
/* 348 */       sb.append("NULL Column");
/*     */     }
/*     */ 
/* 351 */     switch (col) {
/*     */     case 0:
/* 353 */       synchronized (this._conversionDate) {
/* 354 */         this._conversionDate.setTime(lr.getMillis());
/* 355 */         this._conversionStrBuf.setLength(0);
/* 356 */         sb.append(dfm.format(this._conversionDate, this._conversionStrBuf));
/*     */       }
/* 358 */       break;
/*     */     case 1:
/* 360 */       sb.append(lr.getSequenceNumber());
/* 361 */       break;
/*     */     case 2:
/* 363 */       sb.append(lr.getType().toString());
/* 364 */       break;
/*     */     case 3:
/* 366 */       sb.append(lr.getJMSDestinationAsStringBuffer());
/* 367 */       break;
/*     */     case 4:
/* 369 */       sb.append(lr.getJMSCorrelationID());
/* 370 */       break;
/*     */     case 5:
/* 372 */       sb.append(lr.getEventClass());
/* 373 */       break;
/*     */     case 6:
/* 375 */       sb.append(lr.getEventReason());
/* 376 */       break;
/*     */     case 7:
/* 378 */       sb.append(lr.getServer());
/* 379 */       break;
/*     */     case 8:
/* 381 */       sb.append(lr.getConnHostName());
/* 382 */       break;
/*     */     case 9:
/* 384 */       sb.append(lr.getConnUserName());
/* 385 */       break;
/*     */     case 10:
/* 387 */       sb.append(lr.getTargetObject());
/* 388 */       break;
/*     */     case 11:
/* 390 */       sb.append(lr.getTargetName());
/* 391 */       break;
/*     */     case 12:
/* 393 */       sb.append(lr.getTargetDestType());
/* 394 */       break;
/*     */     case 13:
/* 396 */       sb.append(lr.getConnType());
/* 397 */       break;
/*     */     case 999:
/* 399 */       sb.append(lr.getMessageAsStringBuffer());
/* 400 */       break;
/*     */     default:
/* 402 */       String message = "The column number " + col + " must be between 0 and 4";
/* 403 */       throw new IllegalArgumentException(message);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void addColumnToStringBuffer(StringBuffer sb, int col, LogRecord lr) {
/* 408 */     addColumnToStringBuffer(sb, col, lr, this._dfm);
/*     */   }
/*     */ 
/*     */   protected Object getColumn(int col, LogRecord lr) {
/* 412 */     return getColumn(col, lr, this._dfm);
/*     */   }
/*     */ 
/*     */   protected void trimRecords()
/*     */   {
/* 424 */     if (needsTrimming())
/* 425 */       trimOldestRecords();
/*     */   }
/*     */ 
/*     */   protected boolean needsTrimming()
/*     */   {
/* 430 */     return this._allRecords.size() > this._maxNumberOfLogRecords;
/*     */   }
/*     */ 
/*     */   protected void trimOldestRecords() {
/* 434 */     synchronized (this._allRecords) {
/* 435 */       int trim = numberOfRecordsToTrim();
/* 436 */       if (trim > 1) {
/* 437 */         List oldRecords = this._allRecords.subList(0, trim);
/*     */ 
/* 439 */         Iterator records = oldRecords.iterator();
/* 440 */         while (records.hasNext()) {
/* 441 */           LogRecord.freeInstance((LogRecord)records.next());
/*     */         }
/* 443 */         oldRecords.clear();
/* 444 */         refresh();
/*     */       } else {
/* 446 */         this._allRecords.remove(0);
/* 447 */         fastRefresh();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private int numberOfRecordsToTrim()
/*     */   {
/* 457 */     return this._allRecords.size() - this._maxNumberOfLogRecords;
/*     */   }
/*     */ }

/* Location:           /Users/tnarik/Desktop/emssn00p.jar
 * Qualified Name:     emssn00p.viewer.FilteredLogTableModel
 * JD-Core Version:    0.6.1
 */
