/*    */ package uk.co.lecafeautomatique.zedogg.viewer;
/*    */ 
/*    */ import java.awt.Container;
/*    */ import java.awt.FlowLayout;
/*    */ import java.awt.GridBagLayout;
/*    */ import javax.swing.JFrame;
/*    */ import javax.swing.JPanel;
/*    */ 
/*    */ public class RvSnooperLoadingDialog extends RvSnooperDialog
/*    */ {
/*    */   private static final long serialVersionUID = 3690471424787952436L;
/*    */ 
/*    */   public RvSnooperLoadingDialog(JFrame jframe, String message)
/*    */   {
/* 45 */     super(jframe, "EMSSn00p", false);
/*    */ 
/* 47 */     JPanel bottom = new JPanel();
/* 48 */     bottom.setLayout(new FlowLayout());
/*    */ 
/* 50 */     JPanel main = new JPanel();
/* 51 */     main.setLayout(new GridBagLayout());
/* 52 */     wrapStringOnPanel(message, main);
/*    */ 
/* 54 */     getContentPane().add(main, "Center");
/* 55 */     getContentPane().add(bottom, "South");
/* 56 */     setVisible(true);
/*    */   }
/*    */ }

/* Location:           /Users/tnarik/Desktop/emssn00p.jar
 * Qualified Name:     emssn00p.viewer.RvSnooperLoadingDialog
 * JD-Core Version:    0.6.1
 */
