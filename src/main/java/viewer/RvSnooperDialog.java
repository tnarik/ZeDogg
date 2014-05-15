/*     */ package emssn00p.viewer;
/*     */ 
/*     */ import java.awt.Component;
/*     */ import java.awt.Container;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Font;
/*     */ import java.awt.GridBagConstraints;
/*     */ import java.awt.GridBagLayout;
/*     */ import java.awt.Insets;
/*     */ import java.awt.Label;
/*     */ import java.awt.Toolkit;
/*     */ import java.awt.Window;
/*     */ import javax.swing.JDialog;
/*     */ import javax.swing.JFrame;
/*     */ 
/*     */ public abstract class RvSnooperDialog extends JDialog
/*     */ {
/*  26 */   protected static final Font DISPLAY_FONT = new Font("Arial", 1, 12);
/*     */ 
/*     */   protected RvSnooperDialog(JFrame jframe, String message, boolean modal)
/*     */   {
/*  39 */     super(jframe, message, modal);
/*     */   }
/*     */ 
/*     */   public void setVisible(boolean b)
/*     */   {
/*  46 */     pack();
/*  47 */     minimumSizeDialog(this, 200, 100);
/*  48 */     centerWindow(this);
/*  49 */     super.setVisible(b);
/*     */   }
/*     */ 
/*     */   protected void centerWindow(Window win)
/*     */   {
/*  60 */     Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
/*     */ 
/*  63 */     if (screenDim.width < win.getSize().width) {
/*  64 */       win.setSize(screenDim.width, win.getSize().height);
/*     */     }
/*     */ 
/*  67 */     if (screenDim.height < win.getSize().height) {
/*  68 */       win.setSize(win.getSize().width, screenDim.height);
/*     */     }
/*     */ 
/*  72 */     int x = (screenDim.width - win.getSize().width) / 2;
/*  73 */     int y = (screenDim.height - win.getSize().height) / 2;
/*  74 */     win.setLocation(x, y);
/*     */   }
/*     */ 
/*     */   protected void wrapStringOnPanel(String message, Container container)
/*     */   {
/*  79 */     GridBagConstraints c = getDefaultConstraints();
/*  80 */     c.gridwidth = 0;
/*     */ 
/*  82 */     c.insets = new Insets(0, 0, 0, 0);
/*  83 */     GridBagLayout gbLayout = (GridBagLayout)container.getLayout();
/*     */ 
/*  85 */     if (message != null) {
/*  86 */       while (message.length() > 0) {
/*  87 */         int newLineIndex = message.indexOf('\n');
/*     */         String line;
/*  89 */         if (newLineIndex >= 0) {
/*  90 */           line = message.substring(0, newLineIndex);
/*  91 */           message = message.substring(newLineIndex + 1);
/*     */         } else {
/*  93 */           line = message;
/*  94 */           message = "";
/*     */         }
/*  96 */         Label label = new Label(line);
/*  97 */         label.setFont(DISPLAY_FONT);
/*  98 */         gbLayout.setConstraints(label, c);
/*  99 */         container.add(label);
/*     */       }
/*     */     }
/* 102 */     Label label = new Label("Empty error message");
/* 103 */     label.setFont(DISPLAY_FONT);
/* 104 */     gbLayout.setConstraints(label, c);
/* 105 */     container.add(label);
/*     */   }
/*     */ 
/*     */   protected GridBagConstraints getDefaultConstraints()
/*     */   {
/* 110 */     GridBagConstraints constraints = new GridBagConstraints();
/* 111 */     constraints.weightx = 1.0D;
/* 112 */     constraints.weighty = 1.0D;
/* 113 */     constraints.gridheight = 1;
/*     */ 
/* 115 */     constraints.insets = new Insets(4, 4, 4, 4);
/*     */ 
/* 117 */     constraints.fill = 0;
/*     */ 
/* 119 */     constraints.anchor = 17;
/*     */ 
/* 121 */     return constraints;
/*     */   }
/*     */ 
/*     */   protected void minimumSizeDialog(Component component, int minWidth, int minHeight)
/*     */   {
/* 128 */     if (component.getSize().width < minWidth) {
/* 129 */       component.setSize(minWidth, component.getSize().height);
/*     */     }
/* 131 */     if (component.getSize().height < minHeight)
/* 132 */       component.setSize(component.getSize().width, minHeight);
/*     */   }
/*     */ }

/* Location:           /Users/tnarik/Desktop/emssn00p.jar
 * Qualified Name:     emssn00p.viewer.RvSnooperDialog
 * JD-Core Version:    0.6.1
 */