/*     */ package emssn00p.viewer;
/*     */ 
/*     */ import emssn00p.util.ems.EMSParameters;
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
/*     */ public class RvSnooperRvCloseListenerDialog extends RvSnooperDialog
/*     */ {
/*     */   private static final long serialVersionUID = 3760566373390300467L;
/*     */   private JTextField _tPassword;
/*     */   private JTextField _tUser;
/*     */   private JTextField _tServer;
/*     */   private JTextField _tTopic;
/*     */ 
/*     */   public RvSnooperRvCloseListenerDialog(JFrame jframe, String title, EMSParameters defaultParameters)
/*     */   {
/*  62 */     super(jframe, title, true);
/*     */ 
/*  64 */     JPanel bottom = new JPanel();
/*  65 */     bottom.setLayout(new FlowLayout());
/*     */ 
/*  67 */     JPanel main = new JPanel();
/*  68 */     main.setLayout(new FlowLayout());
/*  69 */     JPanel pservice = new JPanel();
/*  70 */     pservice.setLayout(new FlowLayout());
/*  71 */     pservice.add(new JLabel("Service"));
/*  72 */     this._tPassword = new JTextField(5);
/*  73 */     this._tPassword.setText(defaultParameters.getServerURL());
/*  74 */     pservice.add(this._tPassword);
/*  75 */     main.add(pservice);
/*     */ 
/*  77 */     JPanel pDaemon = new JPanel();
/*  78 */     pDaemon.add(new JLabel("Daemon"));
/*  79 */     this._tServer = new JTextField(30);
/*     */ 
/*  81 */     this._tServer.setText(defaultParameters.getUserName());
/*  82 */     pDaemon.add(this._tServer);
/*  83 */     main.add(pDaemon);
/*     */ 
/*  86 */     JPanel pSubject = new JPanel();
/*  87 */     pSubject.add(new JLabel("Topic"));
/*  88 */     this._tTopic = new JTextField(20);
/*  89 */     this._tTopic.setText(defaultParameters.getTopic());
/*  90 */     pSubject.add(this._tTopic);
/*  91 */     main.add(pSubject);
/*     */ 
/*  93 */     addKeyListener(new KeyAdapter() {
/*     */       public void keyPressed(KeyEvent e) {
/*  95 */         if (e.getKeyCode() == 10)
/*  96 */           RvSnooperRvCloseListenerDialog.this.setVisible(false);
/*     */       }
/*     */     });
/* 101 */     JButton ok = new JButton("Ok");
/* 102 */     ok.addActionListener(new ActionListener() {
/*     */       public void actionPerformed(ActionEvent e) {
/* 104 */         RvSnooperRvCloseListenerDialog.this.setVisible(false);
/*     */       }
/*     */     });
/* 108 */     JButton cancel = new JButton("Cancel");
/* 109 */     cancel.addActionListener(new ActionListener() {
/*     */       public void actionPerformed(ActionEvent e) {
/* 111 */         RvSnooperRvCloseListenerDialog.this.setVisible(false);
/*     */       }
/*     */     });
/* 118 */     bottom.add(ok);
/* 119 */     bottom.add(cancel);
/* 120 */     getContentPane().add(main, "Center");
/* 121 */     getContentPane().add(bottom, "South");
/* 122 */     minimumSizeDialog(this, 100, 60);
/* 123 */     pack();
/* 124 */     centerWindow(this);
/* 125 */     setVisible(true);
/*     */   }
/*     */ 
/*     */   public EMSParameters getRvParameters()
/*     */   {
/* 133 */     EMSParameters p = new EMSParameters();
/*     */ 
/* 135 */     p.setServerURL(this._tServer.getText());
/* 136 */     p.setUserName(this._tUser.getText());
/* 137 */     p.setPassword(this._tPassword.getText());
/*     */ 
/* 140 */     return p;
/*     */   }
/*     */ }

/* Location:           /Users/tnarik/Desktop/emssn00p.jar
 * Qualified Name:     emssn00p.viewer.RvSnooperRvCloseListenerDialog
 * JD-Core Version:    0.6.1
 */