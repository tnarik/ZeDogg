/*    */ package emssn00p.viewer;
/*    */ 
/*    */ import javax.swing.table.DefaultTableModel;
/*    */ 
/*    */ public class LogTableModel extends DefaultTableModel
/*    */ {
/*    */   private static final long serialVersionUID = 3258125860492817204L;
/*    */ 
/*    */   public LogTableModel(Object[] colNames, int numRows)
/*    */   {
/* 44 */     super(colNames, numRows);
/*    */   }
/*    */ 
/*    */   public boolean isCellEditable(int row, int column)
/*    */   {
/* 52 */     return false;
/*    */   }
/*    */ }

/* Location:           /Users/tnarik/Desktop/emssn00p.jar
 * Qualified Name:     emssn00p.viewer.LogTableModel
 * JD-Core Version:    0.6.1
 */