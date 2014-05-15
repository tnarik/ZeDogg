/*     */ package emssn00p.viewer.categoryexplorer;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.Component;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.FlowLayout;
/*     */ import javax.swing.JCheckBox;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JTree;
/*     */ import javax.swing.UIManager;
/*     */ import javax.swing.tree.DefaultTreeCellRenderer;
/*     */ 
/*     */ public class CategoryNodeRenderer extends DefaultTreeCellRenderer
/*     */ {
/*     */   private static final long serialVersionUID = 3256438114422371125L;
/*  38 */   protected JCheckBox _checkBox = new JCheckBox();
/*  39 */   protected JPanel _panel = new JPanel();
/*     */ 
/*     */   public CategoryNodeRenderer()
/*     */   {
/*  49 */     this._panel.setBackground(UIManager.getColor("Tree.textBackground"));
/*     */ 
/*  54 */     this._checkBox.setOpaque(false);
/*     */ 
/*  59 */     this._panel.setLayout(new FlowLayout(0, 0, 0));
/*  60 */     this._panel.add(this._checkBox);
/*  61 */     this._panel.add(this);
/*     */ 
/*  64 */     setOpenIcon(null);
/*  65 */     setClosedIcon(null);
/*  66 */     setLeafIcon(null);
/*     */   }
/*     */ 
/*     */   public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus)
/*     */   {
/*  78 */     CategoryNode node = (CategoryNode)value;
/*     */ 
/*  80 */     super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
/*     */ 
/*  84 */     if (row == 0)
/*     */     {
/*  86 */       this._checkBox.setVisible(false);
/*     */     }
/*     */     else
/*     */     {
/*  90 */       this._checkBox.setVisible(true);
/*  91 */       this._checkBox.setSelected(node.isSelected());
/*     */     }
/*  93 */     String toolTip = buildToolTip(node);
/*  94 */     this._panel.setToolTipText(toolTip);
/*     */ 
/*  96 */     setForeground(Color.BLACK);
/*     */ 
/*  99 */     return this._panel;
/*     */   }
/*     */ 
/*     */   public Dimension getCheckBoxOffset() {
/* 103 */     return new Dimension(0, 0);
/*     */   }
/*     */ 
/*     */   protected String buildToolTip(CategoryNode node)
/*     */   {
/* 111 */     StringBuffer result = new StringBuffer();
/* 112 */     result.append(node.getTotalNumberOfRecords());
/* 113 */     result.append(" ");
/* 114 */     result.append(" records has been registered by this node.");
/* 115 */     result.append(" Right-click for more info.");
/* 116 */     return result.toString();
/*     */   }
/*     */ }

/* Location:           /Users/tnarik/Desktop/emssn00p.jar
 * Qualified Name:     emssn00p.viewer.categoryexplorer.CategoryNodeRenderer
 * JD-Core Version:    0.6.1
 */