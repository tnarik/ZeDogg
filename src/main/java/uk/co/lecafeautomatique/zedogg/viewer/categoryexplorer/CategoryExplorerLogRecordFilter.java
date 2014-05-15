/*    */ package uk.co.lecafeautomatique.zedogg.viewer.categoryexplorer;
/*    */ 
/*    */ import uk.co.lecafeautomatique.zedogg.LogRecord;
/*    */ import uk.co.lecafeautomatique.zedogg.LogRecordFilter;
/*    */ import java.util.Enumeration;
/*    */ 
/*    */ public class CategoryExplorerLogRecordFilter
/*    */   implements LogRecordFilter
/*    */ {
/*    */   protected CategoryExplorerModel _model;
/*    */ 
/*    */   public CategoryExplorerLogRecordFilter(CategoryExplorerModel model)
/*    */   {
/* 44 */     this._model = model;
/*    */   }
/*    */ 
/*    */   public boolean passes(LogRecord record)
/*    */   {
/* 58 */     CategoryPath path = new CategoryPath(record.getJMSDestination());
/* 59 */     return this._model.isCategoryPathActive(path);
/*    */   }
/*    */ 
/*    */   public void reset()
/*    */   {
/* 66 */     resetAllNodes();
/*    */   }
/*    */ 
/*    */   protected void resetAllNodes()
/*    */   {
/* 74 */     Enumeration nodes = this._model.getRootCategoryNode().depthFirstEnumeration();
/*    */ 
/* 76 */     while (nodes.hasMoreElements()) {
/* 77 */       CategoryNode current = (CategoryNode)nodes.nextElement();
/* 78 */       current.resetNumberOfContainedRecords();
/* 79 */       this._model.nodeChanged(current);
/*    */     }
/*    */   }
/*    */ }

/* Location:           /Users/tnarik/Desktop/emssn00p.jar
 * Qualified Name:     emssn00p.viewer.categoryexplorer.CategoryExplorerLogRecordFilter
 * JD-Core Version:    0.6.1
 */
