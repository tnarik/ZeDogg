/*     */ package emssn00p.viewer;
/*     */ 
/*     */ import java.awt.Adjustable;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.JScrollPane;
/*     */ import javax.swing.JTable;
/*     */ import javax.swing.ListSelectionModel;
/*     */ import javax.swing.SwingUtilities;
/*     */ import javax.swing.table.TableModel;
/*     */ 
/*     */ public class SwingUtils
/*     */ {
/*     */   public static void selectRow(int row, JTable table, JScrollPane pane)
/*     */   {
/*  53 */     if ((table == null) || (pane == null)) {
/*  54 */       return;
/*     */     }
/*  56 */     if (!contains(row, table.getModel())) {
/*  57 */       return;
/*     */     }
/*  59 */     moveAdjustable(row * table.getRowHeight(), pane.getVerticalScrollBar());
/*  60 */     selectRow(row, table.getSelectionModel());
/*     */ 
/*  64 */     repaintLater(table);
/*     */   }
/*     */ 
/*     */   public static void makeScrollBarTrack(Adjustable scrollBar)
/*     */   {
/*  72 */     if (scrollBar == null) {
/*  73 */       return;
/*     */     }
/*  75 */     scrollBar.addAdjustmentListener(new TrackingAdjustmentListener());
/*     */   }
/*     */ 
/*     */   public static void makeVerticalScrollBarTrack(JScrollPane pane)
/*     */   {
/*  84 */     if (pane == null) {
/*  85 */       return;
/*     */     }
/*  87 */     makeScrollBarTrack(pane.getVerticalScrollBar());
/*     */   }
/*     */ 
/*     */   protected static boolean contains(int row, TableModel model)
/*     */   {
/*  94 */     if (model == null) {
/*  95 */       return false;
/*     */     }
/*  97 */     if (row < 0) {
/*  98 */       return false;
/*     */     }
/* 100 */     if (row >= model.getRowCount()) {
/* 101 */       return false;
/*     */     }
/* 103 */     return true;
/*     */   }
/*     */ 
/*     */   protected static void selectRow(int row, ListSelectionModel model) {
/* 107 */     if (model == null) {
/* 108 */       return;
/*     */     }
/* 110 */     model.setSelectionInterval(row, row);
/*     */   }
/*     */ 
/*     */   protected static void moveAdjustable(int location, Adjustable scrollBar) {
/* 114 */     if (scrollBar == null) {
/* 115 */       return;
/*     */     }
/* 117 */     scrollBar.setValue(location);
/*     */   }
/*     */ 
/*     */   protected static void repaintLater(final JComponent component)
/*     */   {
/* 125 */     SwingUtilities.invokeLater(new Runnable() {
/*     */       public void run() {
/* 127 */         component.repaint();
/*     */       }
/*     */     });
/*     */   }
/*     */ }

/* Location:           /Users/tnarik/Desktop/emssn00p.jar
 * Qualified Name:     emssn00p.viewer.SwingUtils
 * JD-Core Version:    0.6.1
 */