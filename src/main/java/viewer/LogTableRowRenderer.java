/*    */ package emssn00p.viewer;
/*    */ 
/*    */ import emssn00p.EventActionType;
/*    */ import emssn00p.LogRecord;
/*    */ import java.awt.Color;
/*    */ import java.awt.Component;
/*    */ import java.util.Map;
/*    */ import javax.swing.JTable;
/*    */ import javax.swing.table.DefaultTableCellRenderer;
/*    */ 
/*    */ public class LogTableRowRenderer extends DefaultTableCellRenderer
/*    */ {
/*    */   private static final long serialVersionUID = 3978420330240750641L;
/* 41 */   protected boolean _highlightFatal = true;
/* 42 */   protected Color _color = new Color(230, 230, 230);
/*    */ 
/*    */   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col)
/*    */   {
/* 63 */     if (row % 2 == 0)
/* 64 */       setBackground(this._color);
/*    */     else {
/* 66 */       setBackground(Color.white);
/*    */     }
/*    */ 
/* 69 */     FilteredLogTableModel model = (FilteredLogTableModel)table.getModel();
/* 70 */     LogRecord record = model.getFilteredRecord(row);
/*    */ 
/* 72 */     setForeground(getLogLevelColor(record.getType()));
/*    */ 
/* 74 */     return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
/*    */   }
/*    */ 
/*    */   protected Color getLogLevelColor(EventActionType level)
/*    */   {
/* 86 */     return (Color)EventActionType.getLogLevelColorMap().get(level);
/*    */   }
/*    */ }

/* Location:           /Users/tnarik/Desktop/emssn00p.jar
 * Qualified Name:     emssn00p.viewer.LogTableRowRenderer
 * JD-Core Version:    0.6.1
 */