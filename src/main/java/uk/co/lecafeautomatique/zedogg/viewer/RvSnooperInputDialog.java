/*     */ package uk.co.lecafeautomatique.zedogg.viewer;
/*     */ 
/*     */ import java.awt.Container;
/*     */ import java.awt.FlowLayout;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.KeyAdapter;
/*     */ import java.awt.event.KeyEvent;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JTextField;
/*     */ 
/*     */ public class RvSnooperInputDialog extends RvSnooperDialog
/*     */ {
/*     */   private static final long serialVersionUID = 3978142132274149429L;
/*     */   public static final int SIZE = 30;
/*     */   private JTextField _textField;
/*     */ 
/*     */   public RvSnooperInputDialog(JFrame jframe, String title, String label)
/*     */   {
/*  57 */     this(jframe, title, label, 30);
/*     */   }
/*     */ 
/*     */   public RvSnooperInputDialog(JFrame jframe, String title, String label, int size)
/*     */   {
/*  69 */     super(jframe, title, true);
/*     */ 
/*  71 */     JPanel bottom = new JPanel();
/*  72 */     bottom.setLayout(new FlowLayout());
/*     */ 
/*  74 */     JPanel main = new JPanel();
/*  75 */     main.setLayout(new FlowLayout());
/*  76 */     main.add(new JLabel(label));
/*  77 */     this._textField = new JTextField(size);
/*  78 */     main.add(this._textField);
/*     */ 
/*  80 */     addKeyListener(new KeyAdapter() {
/*     */       public void keyPressed(KeyEvent e) {
/*  82 */         if (e.getKeyCode() == 10)
/*  83 */           RvSnooperInputDialog.this.setVisible(false);
/*     */       }
/*     */     });
/*  88 */     JButton ok = new JButton("Ok");
/*  89 */     ok.addActionListener(new ActionListener() {
/*     */       public void actionPerformed(ActionEvent e) {
/*  91 */         RvSnooperInputDialog.this.setVisible(false);
/*     */       }
/*     */     });
/*  95 */     JButton cancel = new JButton("Cancel");
/*  96 */     cancel.addActionListener(new ActionListener() {
/*     */       public void actionPerformed(ActionEvent e) {
/*  98 */         RvSnooperInputDialog.this.setVisible(false);
/*     */ 
/* 102 */         RvSnooperInputDialog.this._textField.setText("");
/*     */       }
/*     */     });
/* 106 */     bottom.add(ok);
/* 107 */     bottom.add(cancel);
/* 108 */     getContentPane().add(main, "Center");
/* 109 */     getContentPane().add(bottom, "South");
/* 110 */     pack();
/* 111 */     centerWindow(this);
/* 112 */     setVisible(true);
/*     */   }
/*     */ 
/*     */   public String getText()
/*     */   {
/* 119 */     String s = this._textField.getText();
/*     */ 
/* 121 */     if ((s != null) && (s.trim().length() == 0)) {
/* 122 */       return null;
/*     */     }
/*     */ 
/* 125 */     return s;
/*     */   }
/*     */ 
/*     */   public String setText(String text)
/*     */   {
/* 130 */     this._textField.setText(text);
/* 131 */     return text;
/*     */   }
/*     */ 
/*     */   public String setToolTipText(String text) {
/* 135 */     this._textField.setToolTipText(text);
/* 136 */     return text;
/*     */   }
/*     */ }

/* Location:           /Users/tnarik/Desktop/emssn00p.jar
 * Qualified Name:     emssn00p.viewer.RvSnooperInputDialog
 * JD-Core Version:    0.6.1
 */
