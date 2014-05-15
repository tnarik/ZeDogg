/*     */ package uk.co.lecafeautomatique.zedogg.viewer;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class LogTableColumn
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 3833747702537597495L;
/*  33 */   public static final LogTableColumn DATE = new LogTableColumn("Date");
/*  34 */   public static final LogTableColumn MESSAGE_NUM = new LogTableColumn("Msg#");
/*  35 */   public static final LogTableColumn EVENTACTION = new LogTableColumn("EventAction");
/*  36 */   public static final LogTableColumn SUBJECT = new LogTableColumn("Destination");
/*  37 */   public static final LogTableColumn TID = new LogTableColumn("JMSCorrID");
/*     */ 
/*  39 */   public static final LogTableColumn EC = new LogTableColumn("EventClass");
/*  40 */   public static final LogTableColumn ER = new LogTableColumn("EventReason");
/*  41 */   public static final LogTableColumn SRV = new LogTableColumn("Server");
/*  42 */   public static final LogTableColumn CH = new LogTableColumn("ConnHostName");
/*  43 */   public static final LogTableColumn CU = new LogTableColumn("ConnUserName");
/*  44 */   public static final LogTableColumn TO = new LogTableColumn("TargetObject");
/*  45 */   public static final LogTableColumn TN = new LogTableColumn("TargetName");
/*  46 */   public static final LogTableColumn TDT = new LogTableColumn("TargetDestType");
/*  47 */   public static final LogTableColumn CT = new LogTableColumn("ConnType");
/*     */   protected String _label;
/*     */   private static LogTableColumn[] _emssColumns;
/*     */   private static Map<String, LogTableColumn> _logTableColumnMap;
/*  61 */   protected static int[] _colWidths = { 8, 1, 30, 150, 40, 10, 10, 10, 10, 10, 10, 10, 10, 10 };
/*     */   protected static String[] _colNames;
/*     */ 
/*     */   public static String[] getColNames()
/*     */   {
/*  83 */     return _colNames;
/*     */   }
/*     */ 
/*     */   public static int[] getColWidths() {
/*  87 */     return _colWidths;
/*     */   }
/*     */ 
/*     */   public LogTableColumn(String label) {
/*  91 */     this._label = label;
/*     */   }
/*     */ 
/*     */   public String getLabel()
/*     */   {
/* 102 */     return this._label;
/*     */   }
/*     */ 
/*     */   public static LogTableColumn valueOf(String column)
/*     */     throws LogTableColumnFormatException
/*     */   {
/* 115 */     LogTableColumn tableColumn = null;
/* 116 */     if (column != null) {
/* 117 */       column = column.trim();
/* 118 */       tableColumn = (LogTableColumn)_logTableColumnMap.get(column);
/*     */     }
/*     */ 
/* 121 */     if (tableColumn == null) {
/* 122 */       StringBuffer buf = new StringBuffer();
/* 123 */       buf.append("Error while trying to parse (" + column + ") into");
/* 124 */       buf.append(" a LogTableColumn.");
/* 125 */       throw new LogTableColumnFormatException(buf.toString());
/*     */     }
/* 127 */     return tableColumn;
/*     */   }
/*     */ 
/*     */   public boolean equals(Object o)
/*     */   {
/* 132 */     boolean equals = false;
/*     */ 
/* 134 */     if (((o instanceof LogTableColumn)) && 
/* 135 */       (getLabel() == ((LogTableColumn)o).getLabel()))
/*     */     {
/* 137 */       equals = true;
/*     */     }
/*     */ 
/* 141 */     return equals;
/*     */   }
/*     */ 
/*     */   public int hashCode() {
/* 145 */     return this._label.hashCode();
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 149 */     return this._label;
/*     */   }
/*     */ 
/*     */   public static List getLogTableColumns()
/*     */   {
/* 157 */     return Arrays.asList(_emssColumns);
/*     */   }
/*     */ 
/*     */   public static LogTableColumn[] getLogTableColumnArray() {
/* 161 */     return _emssColumns;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  68 */     _emssColumns = new LogTableColumn[] { DATE, MESSAGE_NUM, EVENTACTION, SUBJECT, TID, EC, ER, SRV, CH, CU, TO, TN, TDT, CT };
/*     */ 
/*  71 */     _logTableColumnMap = new HashMap();
/*  72 */     _colNames = new String[_emssColumns.length];
/*     */ 
/*  75 */     for (int i = 0; i < _emssColumns.length; i++) {
/*  76 */       _logTableColumnMap.put(_emssColumns[i].getLabel(), _emssColumns[i]);
/*  77 */       _colNames[i] = _emssColumns[i].getLabel();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/tnarik/Desktop/emssn00p.jar
 * Qualified Name:     emssn00p.viewer.LogTableColumn
 * JD-Core Version:    0.6.1
 */
