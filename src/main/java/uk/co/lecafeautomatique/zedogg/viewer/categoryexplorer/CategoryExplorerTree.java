/*     */ package uk.co.lecafeautomatique.zedogg.viewer.categoryexplorer;
/*     */ 
/*     */ import java.awt.event.MouseEvent;
/*     */ import javax.swing.JTree;
/*     */ import javax.swing.event.TreeModelEvent;
/*     */ import javax.swing.tree.TreePath;
/*     */ 
/*     */ public class CategoryExplorerTree extends JTree
/*     */ {
/*     */   private static final long serialVersionUID = 3834871377667961138L;
/*     */   protected CategoryExplorerModel _model;
/*  40 */   protected boolean _rootAlreadyExpanded = false;
/*     */ 
/*     */   public CategoryExplorerTree(CategoryExplorerModel model)
/*     */   {
/*  54 */     super(model);
/*     */ 
/*  56 */     this._model = model;
/*  57 */     init();
/*     */   }
/*     */ 
/*     */   public CategoryExplorerTree()
/*     */   {
/*  66 */     CategoryNode rootNode = new CategoryNode("Topics");
/*     */ 
/*  68 */     this._model = new CategoryExplorerModel(rootNode);
/*     */ 
/*  70 */     setModel(this._model);
/*     */ 
/*  72 */     init();
/*     */   }
/*     */ 
/*     */   public CategoryExplorerModel getExplorerModel()
/*     */   {
/*  80 */     return this._model;
/*     */   }
/*     */ 
/*     */   public String getToolTipText(MouseEvent e)
/*     */   {
/*     */     try {
/*  86 */       return super.getToolTipText(e);
/*     */     } catch (Exception ex) {
/*     */     }
/*  89 */     return "";
/*     */   }
/*     */ 
/*     */   protected void init()
/*     */   {
/* 100 */     putClientProperty("JTree.lineStyle", "Angled");
/*     */ 
/* 104 */     CategoryNodeRenderer renderer = new CategoryNodeRenderer();
/* 105 */     setEditable(true);
/* 106 */     setCellRenderer(renderer);
/*     */ 
/* 108 */     CategoryNodeEditor editor = new CategoryNodeEditor(this._model);
/*     */ 
/* 110 */     setCellEditor(new CategoryImmediateEditor(this, new CategoryNodeRenderer(), editor));
/*     */ 
/* 113 */     setShowsRootHandles(true);
/*     */ 
/* 115 */     setToolTipText("");
/*     */ 
/* 118 */     ensureRootExpansion();
/*     */ 
/* 121 */     this._model.updateAllNodes();
/*     */   }
/*     */ 
/*     */   protected void expandRootNode()
/*     */   {
/* 127 */     if (this._rootAlreadyExpanded) {
/* 128 */       return;
/*     */     }
/* 130 */     this._rootAlreadyExpanded = true;
/* 131 */     TreePath path = new TreePath(this._model.getRootCategoryNode().getPath());
/* 132 */     expandPath(path);
/*     */   }
/*     */ 
/*     */   protected void ensureRootExpansion()
/*     */   {
/* 137 */     this._model.addTreeModelListener(new TreeModelAdapter() {
/*     */       public void treeNodesInserted(TreeModelEvent e) {
/* 139 */         CategoryExplorerTree.this.expandRootNode();
/*     */       }
/*     */     });
/*     */   }
/*     */ }

/* Location:           /Users/tnarik/Desktop/emssn00p.jar
 * Qualified Name:     emssn00p.viewer.categoryexplorer.CategoryExplorerTree
 * JD-Core Version:    0.6.1
 */
