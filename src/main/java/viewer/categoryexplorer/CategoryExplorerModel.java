/*     */ package emssn00p.viewer.categoryexplorer;
/*     */ 
/*     */ import emssn00p.LogRecord;
/*     */ import java.awt.AWTEventMulticaster;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.util.Enumeration;
/*     */ import javax.swing.SwingUtilities;
/*     */ import javax.swing.tree.DefaultTreeModel;
/*     */ import javax.swing.tree.TreeNode;
/*     */ import javax.swing.tree.TreePath;
/*     */ 
/*     */ public class CategoryExplorerModel extends DefaultTreeModel
/*     */ {
/*     */   private static final long serialVersionUID = 4050480127479461941L;
/*  47 */   protected boolean _renderFatal = true;
/*     */ 
/*  49 */   protected ActionListener _listener = null;
/*     */ 
/*  51 */   protected ActionEvent _event = new ActionEvent(this, 1001, "Nodes Selection changed");
/*     */ 
/*     */   public CategoryExplorerModel(CategoryNode node)
/*     */   {
/*  64 */     super(node);
/*     */   }
/*     */ 
/*     */   public void addLogRecord(LogRecord lr)
/*     */   {
/*  72 */     CategoryPath path = new CategoryPath(lr.getJMSDestination());
/*  73 */     addCategory(path);
/*  74 */     CategoryNode node = getCategoryNode(path);
/*  75 */     node.addRecord();
/*     */   }
/*     */ 
/*     */   public CategoryNode getRootCategoryNode()
/*     */   {
/*  80 */     return (CategoryNode)getRoot();
/*     */   }
/*     */ 
/*     */   public CategoryNode getCategoryNode(String category) {
/*  84 */     CategoryPath path = new CategoryPath(category);
/*  85 */     return getCategoryNode(path);
/*     */   }
/*     */ 
/*     */   public CategoryNode getCategoryNode(CategoryPath path)
/*     */   {
/*  92 */     CategoryNode root = (CategoryNode)getRoot();
/*  93 */     CategoryNode parent = root;
/*     */ 
/*  95 */     for (int i = 0; i < path.size(); i++) {
/*  96 */       CategoryElement element = path.categoryElementAt(i);
/*     */ 
/*  99 */       Enumeration children = parent.children();
/*     */ 
/* 101 */       boolean categoryAlreadyExists = false;
/* 102 */       while (children.hasMoreElements()) {
/* 103 */         CategoryNode node = (CategoryNode)children.nextElement();
/* 104 */         String title = node.getTitle().toLowerCase();
/*     */ 
/* 106 */         String pathLC = element.getTitle().toLowerCase();
/* 107 */         if (title.equals(pathLC)) {
/* 108 */           categoryAlreadyExists = true;
/*     */ 
/* 110 */           parent = node;
/* 111 */           break;
/*     */         }
/*     */       }
/*     */ 
/* 115 */       if (!categoryAlreadyExists) {
/* 116 */         return null;
/*     */       }
/*     */     }
/*     */ 
/* 120 */     return parent;
/*     */   }
/*     */ 
/*     */   public boolean isCategoryPathActive(CategoryPath path)
/*     */   {
/* 127 */     CategoryNode root = (CategoryNode)getRoot();
/* 128 */     CategoryNode parent = root;
/* 129 */     boolean active = false;
/*     */ 
/* 131 */     for (int i = 0; i < path.size(); i++) {
/* 132 */       CategoryElement element = path.categoryElementAt(i);
/*     */ 
/* 135 */       Enumeration children = parent.children();
/*     */ 
/* 137 */       boolean categoryAlreadyExists = false;
/* 138 */       active = false;
/*     */ 
/* 140 */       while (children.hasMoreElements()) {
/* 141 */         CategoryNode node = (CategoryNode)children.nextElement();
/* 142 */         String title = node.getTitle().toLowerCase();
/*     */ 
/* 144 */         String pathLC = element.getTitle().toLowerCase();
/* 145 */         if (title.equals(pathLC)) {
/* 146 */           categoryAlreadyExists = true;
/*     */ 
/* 148 */           parent = node;
/*     */ 
/* 150 */           if (!parent.isSelected()) break;
/* 151 */           active = true;
/* 152 */           break;
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 158 */       if ((!active) || (!categoryAlreadyExists)) {
/* 159 */         return false;
/*     */       }
/*     */     }
/*     */ 
/* 163 */     return active;
/*     */   }
/*     */ 
/*     */   public CategoryNode addCategory(CategoryPath path)
/*     */   {
/* 176 */     CategoryNode root = (CategoryNode)getRoot();
/* 177 */     CategoryNode parent = root;
/* 178 */     boolean addedCategory = false;
/*     */ 
/* 180 */     for (int i = 0; i < path.size(); i++) {
/* 181 */       CategoryElement element = path.categoryElementAt(i);
/*     */ 
/* 184 */       Enumeration children = parent.children();
/*     */ 
/* 186 */       boolean categoryAlreadyExists = false;
/* 187 */       while (children.hasMoreElements()) {
/* 188 */         CategoryNode node = (CategoryNode)children.nextElement();
/* 189 */         String title = node.getTitle().toLowerCase();
/*     */ 
/* 191 */         String pathLC = element.getTitle().toLowerCase();
/* 192 */         if (title.equals(pathLC)) {
/* 193 */           categoryAlreadyExists = true;
/*     */ 
/* 195 */           parent = node;
/* 196 */           break;
/*     */         }
/*     */       }
/*     */ 
/* 200 */       if (!categoryAlreadyExists)
/*     */       {
/* 202 */         CategoryNode newNode = new CategoryNode(element.getTitle());
/*     */ 
/* 205 */         insertNodeInto(newNode, parent, parent.getChildCount());
/* 206 */         addedCategory = true;
/*     */ 
/* 208 */         parent = newNode;
/* 209 */         reload(parent);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 214 */     if (addedCategory == true) {
/* 215 */       for (int i = 0; i < path.size(); i++) {
/* 216 */         CategoryElement element = path.categoryElementAt(i);
/* 217 */         Enumeration children = parent.children();
/*     */ 
/* 219 */         boolean categoryAlreadyExists = false;
/* 220 */         while (children.hasMoreElements()) {
/* 221 */           CategoryNode node = (CategoryNode)children.nextElement();
/* 222 */           refresh(node);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 227 */     return parent;
/*     */   }
/*     */ 
/*     */   public void update(CategoryNode node, boolean selected) {
/* 231 */     if (node.isSelected() == selected) {
/* 232 */       return;
/*     */     }
/*     */ 
/* 235 */     if (selected)
/* 236 */       setParentSelection(node, true);
/*     */     else {
/* 238 */       setDescendantSelection(node, false);
/*     */     }
/* 240 */     refresh(node);
/*     */   }
/*     */ 
/*     */   public void setDescendantSelection(CategoryNode node, boolean selected) {
/* 244 */     Enumeration descendants = node.depthFirstEnumeration();
/*     */ 
/* 246 */     while (descendants.hasMoreElements()) {
/* 247 */       CategoryNode current = (CategoryNode)descendants.nextElement();
/*     */ 
/* 249 */       if (current.isSelected() != selected) {
/* 250 */         current.setSelected(selected);
/* 251 */         nodeChanged(current);
/*     */       }
/*     */     }
/* 254 */     notifyActionListeners();
/*     */   }
/*     */ 
/*     */   public void setParentSelection(CategoryNode node, boolean selected) {
/* 258 */     TreeNode[] nodes = getPathToRoot(node);
/* 259 */     int len = nodes.length;
/*     */ 
/* 264 */     for (int i = 1; i < len; i++) {
/* 265 */       CategoryNode parent = (CategoryNode)nodes[i];
/* 266 */       if (parent.isSelected() != selected) {
/* 267 */         parent.setSelected(selected);
/* 268 */         nodeChanged(parent);
/*     */       }
/*     */     }
/* 271 */     notifyActionListeners();
/*     */   }
/*     */ 
/*     */   public synchronized void addActionListener(ActionListener l) {
/* 275 */     this._listener = AWTEventMulticaster.add(this._listener, l);
/*     */   }
/*     */ 
/*     */   public synchronized void removeActionListener(ActionListener l) {
/* 279 */     this._listener = AWTEventMulticaster.remove(this._listener, l);
/*     */   }
/*     */ 
/*     */   public void resetAllNodeCounts() {
/* 283 */     Enumeration nodes = getRootCategoryNode().depthFirstEnumeration();
/*     */ 
/* 285 */     while (nodes.hasMoreElements()) {
/* 286 */       CategoryNode current = (CategoryNode)nodes.nextElement();
/* 287 */       current.resetNumberOfContainedRecords();
/* 288 */       nodeChanged(current);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void updateAllNodes() {
/* 293 */     Enumeration nodes = getRootCategoryNode().preorderEnumeration();
/*     */ 
/* 295 */     while (nodes.hasMoreElements()) {
/* 296 */       CategoryNode current = (CategoryNode)nodes.nextElement();
/* 297 */       nodeChanged(current);
/*     */     }
/*     */   }
/*     */ 
/*     */   public TreePath getTreePathToRoot(CategoryNode node)
/*     */   {
/* 311 */     if (node == null) {
/* 312 */       return null;
/*     */     }
/* 314 */     return new TreePath(getPathToRoot(node));
/*     */   }
/*     */ 
/*     */   protected void notifyActionListeners()
/*     */   {
/* 321 */     if (this._listener != null)
/* 322 */       this._listener.actionPerformed(this._event);
/*     */   }
/*     */ 
/*     */   protected void refresh(final CategoryNode node)
/*     */   {
/* 332 */     SwingUtilities.invokeLater(new Runnable()
/*     */     {
/*     */       public void run() {
/* 335 */         CategoryExplorerModel.this.nodeChanged(node);
/*     */       }
/*     */     });
/*     */   }
/*     */ }

/* Location:           /Users/tnarik/Desktop/emssn00p.jar
 * Qualified Name:     emssn00p.viewer.categoryexplorer.CategoryExplorerModel
 * JD-Core Version:    0.6.1
 */