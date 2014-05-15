/*     */ package uk.co.lecafeautomatique.zedogg.viewer.categoryexplorer;
/*     */ 
/*     */ import java.util.LinkedList;
/*     */ 
/*     */ public class CategoryPath
/*     */ {
/*  30 */   protected LinkedList<CategoryElement> _categoryElements = new LinkedList();
/*     */ 
/*     */   public CategoryPath()
/*     */   {
/*     */   }
/*     */ 
/*     */   public CategoryPath(String category)
/*     */   {
/*  48 */     String processedCategory = category;
/*     */ 
/*  50 */     if (processedCategory == null) {
/*  51 */       processedCategory = "NullDebug";
/*     */     }
/*     */ 
/*  54 */     processedCategory.replace('/', '.');
/*  55 */     processedCategory = processedCategory.replace('\\', '.');
/*     */ 
/*  57 */     String[] result = processedCategory.split("\\.");
/*  58 */     for (int x = 0; x < result.length; x++)
/*  59 */       addCategoryElement(new CategoryElement(result[x]));
/*     */   }
/*     */ 
/*     */   public int size()
/*     */   {
/*  72 */     int count = this._categoryElements.size();
/*     */ 
/*  74 */     return count;
/*     */   }
/*     */ 
/*     */   public boolean isEmpty() {
/*  78 */     boolean empty = false;
/*     */ 
/*  80 */     if (this._categoryElements.size() == 0) {
/*  81 */       empty = true;
/*     */     }
/*     */ 
/*  84 */     return empty;
/*     */   }
/*     */ 
/*     */   public void removeAllCategoryElements()
/*     */   {
/*  92 */     this._categoryElements.clear();
/*     */   }
/*     */ 
/*     */   public void addCategoryElement(CategoryElement categoryElement)
/*     */   {
/*  99 */     this._categoryElements.addLast(categoryElement);
/*     */   }
/*     */ 
/*     */   public CategoryElement categoryElementAt(int index)
/*     */   {
/* 106 */     return (CategoryElement)this._categoryElements.get(index);
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 111 */     StringBuffer out = new StringBuffer(100);
/*     */ 
/* 113 */     out.append("\n");
/* 114 */     out.append("===========================\n");
/* 115 */     out.append("TopicPath:                   \n");
/* 116 */     out.append("---------------------------\n");
/*     */ 
/* 118 */     out.append("\nTopicPath:\n\t");
/*     */ 
/* 120 */     if (size() > 0)
/* 121 */       for (int i = 0; i < size(); i++) {
/* 122 */         out.append(categoryElementAt(i).toString());
/* 123 */         out.append("\n\t");
/*     */       }
/*     */     else {
/* 126 */       out.append("<<NONE>>");
/*     */     }
/*     */ 
/* 129 */     out.append("\n");
/* 130 */     out.append("===========================\n");
/*     */ 
/* 132 */     return out.toString();
/*     */   }
/*     */ }

/* Location:           /Users/tnarik/Desktop/emssn00p.jar
 * Qualified Name:     emssn00p.viewer.categoryexplorer.CategoryPath
 * JD-Core Version:    0.6.1
 */
