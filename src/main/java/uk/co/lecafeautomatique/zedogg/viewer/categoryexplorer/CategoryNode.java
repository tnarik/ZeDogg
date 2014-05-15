/*     */ package uk.co.lecafeautomatique.zedogg.viewer.categoryexplorer;
/*     */ 
/*     */ import java.util.Enumeration;
/*     */ import javax.swing.tree.DefaultMutableTreeNode;
/*     */ import javax.swing.tree.TreeNode;
/*     */ 
/*     */ public class CategoryNode extends DefaultMutableTreeNode
/*     */ {
/*     */   private static final long serialVersionUID = 3256723991824513331L;
/*  35 */   protected boolean _selected = true;
/*  36 */   protected int _numberOfContainedRecords = 0;
/*  37 */   protected int _numberOfRecordsFromChildren = 0;
/*     */ 
/*     */   public CategoryNode(String title)
/*     */   {
/*  52 */     setUserObject(title);
/*     */   }
/*     */ 
/*     */   public String getTitle()
/*     */   {
/*  59 */     return (String)getUserObject();
/*     */   }
/*     */ 
/*     */   public void setSelected(boolean s) {
/*  63 */     if (s != this._selected)
/*  64 */       this._selected = s;
/*     */   }
/*     */ 
/*     */   public boolean isSelected()
/*     */   {
/*  69 */     return this._selected;
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public void setAllDescendantsSelected()
/*     */   {
/*  76 */     Enumeration children = children();
/*  77 */     while (children.hasMoreElements()) {
/*  78 */       CategoryNode node = (CategoryNode)children.nextElement();
/*  79 */       node.setSelected(true);
/*  80 */       node.setAllDescendantsSelected();
/*     */     }
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public void setAllDescendantsDeSelected()
/*     */   {
/*  88 */     Enumeration children = children();
/*  89 */     while (children.hasMoreElements()) {
/*  90 */       CategoryNode node = (CategoryNode)children.nextElement();
/*  91 */       node.setSelected(false);
/*  92 */       node.setAllDescendantsDeSelected();
/*     */     }
/*     */   }
/*     */ 
/*     */   public String toString() {
/*  97 */     return getTitle();
/*     */   }
/*     */ 
/*     */   public boolean equals(Object obj) {
/* 101 */     if ((obj instanceof CategoryNode)) {
/* 102 */       CategoryNode node = (CategoryNode)obj;
/* 103 */       String tit1 = getTitle().toLowerCase();
/* 104 */       String tit2 = node.getTitle().toLowerCase();
/*     */ 
/* 106 */       if (tit1.equals(tit2)) {
/* 107 */         return true;
/*     */       }
/*     */     }
/* 110 */     return false;
/*     */   }
/*     */ 
/*     */   public int hashCode() {
/* 114 */     return getTitle().hashCode();
/*     */   }
/*     */ 
/*     */   public void addRecord() {
/* 118 */     this._numberOfContainedRecords += 1;
/* 119 */     addRecordToParent();
/*     */   }
/*     */ 
/*     */   public int getNumberOfContainedRecords() {
/* 123 */     return this._numberOfContainedRecords;
/*     */   }
/*     */ 
/*     */   public void resetNumberOfContainedRecords() {
/* 127 */     this._numberOfContainedRecords = 0;
/* 128 */     this._numberOfRecordsFromChildren = 0;
/*     */   }
/*     */ 
/*     */   protected int getTotalNumberOfRecords()
/*     */   {
/* 139 */     return getNumberOfRecordsFromChildren() + getNumberOfContainedRecords();
/*     */   }
/*     */ 
/*     */   protected void addRecordFromChild()
/*     */   {
/* 146 */     this._numberOfRecordsFromChildren += 1;
/* 147 */     addRecordToParent();
/*     */   }
/*     */ 
/*     */   protected int getNumberOfRecordsFromChildren() {
/* 151 */     return this._numberOfRecordsFromChildren;
/*     */   }
/*     */ 
/*     */   protected void addRecordToParent() {
/* 155 */     TreeNode parent = getParent();
/* 156 */     if (parent == null) {
/* 157 */       return;
/*     */     }
/* 159 */     ((CategoryNode)parent).addRecordFromChild();
/*     */   }
/*     */ }

/* Location:           /Users/tnarik/Desktop/emssn00p.jar
 * Qualified Name:     emssn00p.viewer.categoryexplorer.CategoryNode
 * JD-Core Version:    0.6.1
 */
