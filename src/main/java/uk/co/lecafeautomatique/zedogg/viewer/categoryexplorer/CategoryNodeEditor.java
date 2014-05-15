/*     */ package uk.co.lecafeautomatique.zedogg.viewer.categoryexplorer;
/*     */ 
/*     */ import java.awt.Component;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.MouseAdapter;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Enumeration;
/*     */ import javax.swing.JCheckBox;
/*     */ import javax.swing.JMenuItem;
/*     */ import javax.swing.JOptionPane;
/*     */ import javax.swing.JPopupMenu;
/*     */ import javax.swing.JTree;
/*     */ import javax.swing.tree.TreePath;
/*     */ 
/*     */ public class CategoryNodeEditor extends CategoryAbstractCellEditor
/*     */ {
/*     */   protected CategoryNodeEditorRenderer _renderer;
/*     */   protected CategoryNode _lastEditedNode;
/*     */   protected JCheckBox _checkBox;
/*     */   protected CategoryExplorerModel _categoryModel;
/*     */   protected JTree _tree;
/*     */ 
/*     */   public CategoryNodeEditor(CategoryExplorerModel model)
/*     */   {
/*  52 */     this._renderer = new CategoryNodeEditorRenderer();
/*  53 */     this._checkBox = this._renderer.getCheckBox();
/*  54 */     this._categoryModel = model;
/*     */ 
/*  56 */     this._checkBox.addActionListener(new ActionListener() {
/*     */       public void actionPerformed(ActionEvent e) {
/*  58 */         CategoryNodeEditor.this._categoryModel.update(CategoryNodeEditor.this._lastEditedNode, CategoryNodeEditor.this._checkBox.isSelected());
/*  59 */         CategoryNodeEditor.this.stopCellEditing();
/*     */       }
/*     */     });
/*  63 */     this._renderer.addMouseListener(new MouseAdapter() {
/*     */       public void mousePressed(MouseEvent e) {
/*  65 */         if ((e.getModifiers() & 0x4) != 0) {
/*  66 */           CategoryNodeEditor.this.showPopup(CategoryNodeEditor.this._lastEditedNode, e.getX(), e.getY());
/*     */         }
/*  68 */         CategoryNodeEditor.this.stopCellEditing();
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   public Component getTreeCellEditorComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row)
/*     */   {
/*  80 */     this._lastEditedNode = ((CategoryNode)value);
/*  81 */     this._tree = tree;
/*     */ 
/*  83 */     return this._renderer.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, true);
/*     */   }
/*     */ 
/*     */   public Object getCellEditorValue()
/*     */   {
/*  90 */     return this._lastEditedNode.getUserObject();
/*     */   }
/*     */ 
/*     */   protected JMenuItem createPropertiesMenuItem(final CategoryNode node)
/*     */   {
/*  97 */     JMenuItem result = new JMenuItem("Properties");
/*  98 */     result.addActionListener(new ActionListener() {
/*     */       public void actionPerformed(ActionEvent e) {
/* 100 */         CategoryNodeEditor.this.showPropertiesDialog(node);
/*     */       }
/*     */     });
/* 103 */     return result;
/*     */   }
/*     */ 
/*     */   protected void showPropertiesDialog(CategoryNode node) {
/* 107 */     JOptionPane.showMessageDialog(this._tree, getDisplayedProperties(node), "Topic Properties: " + node.getTitle(), -1);
/*     */   }
/*     */ 
/*     */   protected Object getDisplayedProperties(CategoryNode node)
/*     */   {
/* 116 */     ArrayList result = new ArrayList();
/* 117 */     result.add("Topic: " + node.getTitle());
/*     */ 
/* 119 */     result.add("LogRecords in this Topic path alone: " + node.getNumberOfContainedRecords());
/*     */ 
/* 121 */     result.add("LogRecords in descendant topic path: " + node.getNumberOfRecordsFromChildren());
/*     */ 
/* 123 */     result.add("LogRecords in this topic path including descendants: " + node.getTotalNumberOfRecords());
/*     */ 
/* 125 */     return result.toArray();
/*     */   }
/*     */ 
/*     */   protected void showPopup(CategoryNode node, int x, int y) {
/* 129 */     JPopupMenu popup = new JPopupMenu();
/* 130 */     popup.setSize(150, 400);
/*     */ 
/* 134 */     if (node.getParent() == null) {
/* 135 */       popup.add(createRemoveMenuItem());
/* 136 */       popup.addSeparator();
/*     */     }
/* 138 */     popup.add(createSelectDescendantsMenuItem(node));
/* 139 */     popup.add(createUnselectDescendantsMenuItem(node));
/* 140 */     popup.addSeparator();
/* 141 */     popup.add(createExpandMenuItem(node));
/* 142 */     popup.add(createCollapseMenuItem(node));
/* 143 */     popup.addSeparator();
/* 144 */     popup.add(createPropertiesMenuItem(node));
/* 145 */     popup.show(this._renderer, x, y);
/*     */   }
/*     */ 
/*     */   protected JMenuItem createSelectDescendantsMenuItem(final CategoryNode node) {
/* 149 */     JMenuItem selectDescendants = new JMenuItem("Select All Descendant Topics");
/*     */ 
/* 151 */     selectDescendants.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e) {
/* 154 */         CategoryNodeEditor.this._categoryModel.setDescendantSelection(node, true);
/*     */       }
/*     */     });
/* 158 */     return selectDescendants;
/*     */   }
/*     */ 
/*     */   protected JMenuItem createUnselectDescendantsMenuItem(final CategoryNode node) {
/* 162 */     JMenuItem unselectDescendants = new JMenuItem("Deselect All Descendant Topics");
/*     */ 
/* 164 */     unselectDescendants.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/* 168 */         CategoryNodeEditor.this._categoryModel.setDescendantSelection(node, false);
/*     */       }
/*     */     });
/* 173 */     return unselectDescendants;
/*     */   }
/*     */ 
/*     */   protected JMenuItem createExpandMenuItem(final CategoryNode node) {
/* 177 */     JMenuItem result = new JMenuItem("Expand All Descendant Subjects");
/* 178 */     result.addActionListener(new ActionListener() {
/*     */       public void actionPerformed(ActionEvent e) {
/* 180 */         CategoryNodeEditor.this.expandDescendants(node);
/*     */       }
/*     */     });
/* 183 */     return result;
/*     */   }
/*     */ 
/*     */   protected JMenuItem createCollapseMenuItem(final CategoryNode node) {
/* 187 */     JMenuItem result = new JMenuItem("Collapse All Descendant Subjects");
/* 188 */     result.addActionListener(new ActionListener() {
/*     */       public void actionPerformed(ActionEvent e) {
/* 190 */         CategoryNodeEditor.this.collapseDescendants(node);
/*     */       }
/*     */     });
/* 193 */     return result;
/*     */   }
/*     */ 
/*     */   protected JMenuItem createRemoveMenuItem()
/*     */   {
/* 206 */     JMenuItem result = new JMenuItem("Remove All Empty Subjects");
/* 207 */     result.addActionListener(new ActionListener() {
/*     */       public void actionPerformed(ActionEvent e) {
/* 209 */         while (CategoryNodeEditor.this.removeUnusedNodes() > 0);
/*     */       }
/*     */     });
/* 212 */     return result;
/*     */   }
/*     */ 
/*     */   protected void expandDescendants(CategoryNode node) {
/* 216 */     Enumeration descendants = node.depthFirstEnumeration();
/*     */ 
/* 219 */     while (descendants.hasMoreElements()) {
/* 220 */       CategoryNode current = (CategoryNode)descendants.nextElement();
/* 221 */       expand(current);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void collapseDescendants(CategoryNode node)
/*     */   {
/* 228 */     Enumeration descendants = node.depthFirstEnumeration();
/*     */ 
/* 230 */     while (descendants.hasMoreElements()) {
/* 231 */       CategoryNode current = (CategoryNode)descendants.nextElement();
/* 232 */       collapse(current);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected int removeUnusedNodes()
/*     */   {
/* 241 */     int count = 0;
/* 242 */     CategoryNode root = this._categoryModel.getRootCategoryNode();
/* 243 */     Enumeration enumNodes = root.depthFirstEnumeration();
/* 244 */     while (enumNodes.hasMoreElements()) {
/* 245 */       CategoryNode node = (CategoryNode)enumNodes.nextElement();
/* 246 */       if ((node.isLeaf()) && (node.getNumberOfContainedRecords() == 0) && (node.getParent() != null))
/*     */       {
/* 248 */         this._categoryModel.removeNodeFromParent(node);
/* 249 */         count++;
/*     */       }
/*     */     }
/*     */ 
/* 253 */     return count;
/*     */   }
/*     */ 
/*     */   protected void expand(CategoryNode node) {
/* 257 */     this._tree.expandPath(getTreePath(node));
/*     */   }
/*     */ 
/*     */   protected TreePath getTreePath(CategoryNode node)
/*     */   {
/* 263 */     return new TreePath(node.getPath());
/*     */   }
/*     */ 
/*     */   protected void collapse(CategoryNode node) {
/* 267 */     this._tree.collapsePath(getTreePath(node));
/*     */   }
/*     */ }

/* Location:           /Users/tnarik/Desktop/emssn00p.jar
 * Qualified Name:     emssn00p.viewer.categoryexplorer.CategoryNodeEditor
 * JD-Core Version:    0.6.1
 */
