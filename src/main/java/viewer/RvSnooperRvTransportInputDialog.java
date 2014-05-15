/*     */ package emssn00p.viewer;
/*     */ 
/*     */ import emssn00p.util.ems.EMSParameters;
/*     */ import java.awt.Container;
/*     */ import java.awt.FlowLayout;
/*     */ import java.awt.GridLayout;
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
/*     */ public class RvSnooperRvTransportInputDialog extends RvSnooperDialog
/*     */ {
/*     */   private static final long serialVersionUID = 3906081235372421427L;
/*  47 */   private JTextField _tServerURL = new JTextField();
/*  48 */   private JTextField _tPassword = new JTextField();
/*  49 */   private JTextField _tUserName = new JTextField();
/*  50 */   private JTextField _tTopic = new JTextField();
/*     */   private boolean isOK;
/*     */ 
/*     */   public RvSnooperRvTransportInputDialog(JFrame jframe, String title, EMSParameters defaultParameters)
/*     */   {
/*  66 */     super(jframe, title, true);
/*     */ 
/*  68 */     JPanel bottom = new JPanel();
/*  69 */     bottom.setLayout(new FlowLayout());
/*  70 */     JPanel main = new JPanel();
/*  71 */     main.setLayout(new GridLayout(8, 0));
/*     */ 
/*  73 */     createInputField("Server:", defaultParameters.getServerURL(), this._tServerURL, main);
/*  74 */     createInputField("User:", defaultParameters.getUserName(), this._tUserName, main);
/*  75 */     createInputField("Topic:", defaultParameters.getTopic(), this._tTopic, main);
/*  76 */     createInputField("Password:", defaultParameters.getPassword(), this._tPassword, main);
/*     */ 
/*  78 */     this._tTopic.setToolTipText("Comma (,) separated list of topics (topic1,topic2)");
/*     */ 
/*  81 */     addKeyListener(new KeyAdapter() {
/*     */       public void keyPressed(KeyEvent e) {
/*  83 */         if (e.getKeyCode() == 10) {
/*  84 */           RvSnooperRvTransportInputDialog.this.setVisible(false);
/*  85 */           RvSnooperRvTransportInputDialog.this.setOK(true);
/*     */         }
/*     */       }
/*     */     });
/*  90 */     JButton ok = new JButton("Ok");
/*  91 */     ok.addActionListener(new ActionListener() {
/*     */       public void actionPerformed(ActionEvent e) {
/*  93 */         RvSnooperRvTransportInputDialog.this.setVisible(false);
/*  94 */         RvSnooperRvTransportInputDialog.this.setOK(true);
/*     */       }
/*     */     });
/*  98 */     JButton cancel = new JButton("Cancel");
/*  99 */     cancel.addActionListener(new ActionListener() {
/*     */       public void actionPerformed(ActionEvent e) {
/* 101 */         RvSnooperRvTransportInputDialog.this.setVisible(false);
/* 102 */         RvSnooperRvTransportInputDialog.this.setOK(false);
/*     */       }
/*     */     });
/* 107 */     bottom.add(ok);
/* 108 */     bottom.add(cancel);
/* 109 */     getContentPane().add(main, "Center");
/* 110 */     getContentPane().add(bottom, "South");
/*     */ 
/* 112 */     pack();
/* 113 */     centerWindow(this);
/* 114 */     setVisible(true);
/*     */   }
/*     */ 
/*     */   public EMSParameters getRvParameters()
/*     */   {
/* 122 */     EMSParameters p = new EMSParameters();
/*     */ 
/* 124 */     p.setUserName(this._tUserName.getText());
/* 125 */     p.setPassword(this._tPassword.getText());
/* 126 */     p.setServerURL(this._tServerURL.getText());
/* 127 */     p.setTopics(this._tTopic.getText());
/*     */ 
/* 129 */     return p;
/*     */   }
/*     */ 
/*     */   public boolean isOK() {
/* 133 */     return this.isOK;
/*     */   }
/*     */ 
/*     */   protected void createInputField(String name, String defaultText, JTextField tf, JPanel addTo)
/*     */   {
/* 145 */     JLabel jl = new JLabel(name);
/*     */ 
/* 147 */     addTo.add(jl);
/* 148 */     tf.setText(defaultText);
/* 149 */     addTo.add(tf);
/* 150 */     jl.setLabelFor(tf);
/*     */   }
/*     */ 
/*     */   protected void setOK(boolean OK)
/*     */   {
/* 155 */     this.isOK = OK;
/*     */   }
/*     */ }

/* Location:           /Users/tnarik/Desktop/emssn00p.jar
 * Qualified Name:     emssn00p.viewer.RvSnooperRvTransportInputDialog
 * JD-Core Version:    0.6.1
 */