/*    */ package emssn00p.viewer.categoryexplorer;
/*    */ 
/*    */ import java.awt.Component;
/*    */ import javax.swing.JCheckBox;
/*    */ import javax.swing.JTree;
/*    */ 
/*    */ public class CategoryNodeEditorRenderer extends CategoryNodeRenderer
/*    */ {
/*    */   private static final long serialVersionUID = 3256445819543369269L;
/*    */ 
/*    */   public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus)
/*    */   {
/* 53 */     Component c = super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
/*    */ 
/* 57 */     return c;
/*    */   }
/*    */ 
/*    */   public JCheckBox getCheckBox() {
/* 61 */     return this._checkBox;
/*    */   }
/*    */ }

/* Location:           /Users/tnarik/Desktop/emssn00p.jar
 * Qualified Name:     emssn00p.viewer.categoryexplorer.CategoryNodeEditorRenderer
 * JD-Core Version:    0.6.1
 */