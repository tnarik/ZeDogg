package uk.co.lecafeautomatique.zedogg.gui;

import uk.co.lecafeautomatique.zedogg.util.ems.EMSParameters;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class GUICloseListenerDialog extends GUIDialog {
  private JTextField _tPassword;
  private JTextField _tUser;
  private JTextField _tServer;
  private JTextField _tTopic;

  public GUICloseListenerDialog(JFrame jframe, String title, EMSParameters defaultParameters) {
    super(jframe, title, true);

    JPanel bottom = new JPanel();
    bottom.setLayout(new FlowLayout());

    JPanel main = new JPanel();
    main.setLayout(new FlowLayout());
    JPanel pservice = new JPanel();
    pservice.setLayout(new FlowLayout());
    pservice.add(new JLabel("Service"));
    this._tPassword = new JTextField(5);
    this._tPassword.setText(defaultParameters.getServerURL());
    pservice.add(this._tPassword);
    main.add(pservice);

    JPanel pDaemon = new JPanel();
    pDaemon.add(new JLabel("Daemon"));
    this._tServer = new JTextField(30);

    this._tServer.setText(defaultParameters.getUserName());
    pDaemon.add(this._tServer);
    main.add(pDaemon);

    JPanel pSubject = new JPanel();
    pSubject.add(new JLabel("Topic"));
    this._tTopic = new JTextField(20);
    this._tTopic.setText(defaultParameters.getTopic());
    pSubject.add(this._tTopic);
    main.add(pSubject);

    addKeyListener(new KeyAdapter() {
      public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == 10)
          GUICloseListenerDialog.this.setVisible(false);
      }
    });
    JButton ok = new JButton("Ok");
    ok.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        GUICloseListenerDialog.this.setVisible(false);
      }
    });
    JButton cancel = new JButton("Cancel");
    cancel.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        GUICloseListenerDialog.this.setVisible(false);
      }
    });
    bottom.add(ok);
    bottom.add(cancel);
    getContentPane().add(main, "Center");
    getContentPane().add(bottom, "South");
    minimumSizeDialog(this, 100, 60);
    pack();
    centerWindow(this);
    setVisible(true);
  }

  public EMSParameters getParameters() {
    EMSParameters p = new EMSParameters();

    p.setServerURL(this._tServer.getText());
    p.setUserName(this._tUser.getText());
    p.setPassword(this._tPassword.getText());

    return p;
  }
}
