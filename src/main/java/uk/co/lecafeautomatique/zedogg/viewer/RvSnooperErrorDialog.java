/*    */ package uk.co.lecafeautomatique.zedogg.viewer;
/*    */ 
/*    */ import java.awt.Container;
/*    */ import java.awt.FlowLayout;
/*    */ import java.awt.GridBagLayout;
/*    */ import java.awt.event.ActionEvent;
/*    */ import java.awt.event.ActionListener;
/*    */ import javax.swing.JButton;
/*    */ import javax.swing.JFrame;
/*    */ import javax.swing.JPanel;
/*    */ 
/*    */ public class RvSnooperErrorDialog extends RvSnooperDialog
/*    */ {
/*    */   private static final long serialVersionUID = 3258407326829851443L;
/*    */ 
/*    */   public RvSnooperErrorDialog(JFrame jframe, String message)
/*    */   {
/* 46 */     super(jframe, "Error", true);
/*    */ 
/* 48 */     JButton ok = new JButton("Ok");
/* 49 */     ok.addActionListener(new ActionListener() {
/*    */       public void actionPerformed(ActionEvent e) {
/* 51 */         RvSnooperErrorDialog.this.setVisible(false);
/*    */       }
/*    */     });
/* 55 */     JPanel bottom = new JPanel();
/* 56 */     bottom.setLayout(new FlowLayout());
/* 57 */     bottom.add(ok);
/*    */ 
/* 59 */     JPanel main = new JPanel();
/* 60 */     main.setLayout(new GridBagLayout());
/* 61 */     wrapStringOnPanel(message, main);
/*    */ 
/* 63 */     getContentPane().add(main, "Center");
/* 64 */     getContentPane().add(bottom, "South");
/* 65 */     setVisible(true);
/*    */   }
/*    */ }

/* Location:           /Users/tnarik/Desktop/emssn00p.jar
 * Qualified Name:     emssn00p.viewer.RvSnooperErrorDialog
 * JD-Core Version:    0.6.1
 */
