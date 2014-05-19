package uk.co.lecafeautomatique.zedogg.viewer;

import uk.co.lecafeautomatique.zedogg.util.ems.EMSParameters;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class RvSnooperRvTransportInputDialog extends RvSnooperDialog {
  private static final long serialVersionUID = 3906081235372421427L;
  private JTextField _tServerURL = new JTextField();
  private JTextField _tPassword = new JTextField();
  private JTextField _tUserName = new JTextField();
  private JTextField _tTopic = new JTextField();
  private boolean isOK;

  public RvSnooperRvTransportInputDialog(JFrame jframe, String title, EMSParameters defaultParameters) {
    super(jframe, title, true);

    JPanel bottom = new JPanel();
    bottom.setLayout(new FlowLayout());
    JPanel main = new JPanel();
    main.setLayout(new GridLayout(8, 0));

    createInputField("Server:", defaultParameters.getServerURL(), this._tServerURL, main);
    createInputField("User:", defaultParameters.getUserName(), this._tUserName, main);
    createInputField("Topic:", defaultParameters.getTopic(), this._tTopic, main);
    createInputField("Password:", defaultParameters.getPassword(), this._tPassword, main);

    this._tTopic.setToolTipText("Comma (,) separated list of topics (topic1,topic2)");

    addKeyListener(new KeyAdapter() {
      public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == 10) {
          RvSnooperRvTransportInputDialog.this.setVisible(false);
          RvSnooperRvTransportInputDialog.this.setOK(true);
        }
      }
    });
    JButton ok = new JButton("Ok");
    ok.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        RvSnooperRvTransportInputDialog.this.setVisible(false);
        RvSnooperRvTransportInputDialog.this.setOK(true);
      }
    });
    JButton cancel = new JButton("Cancel");
    cancel.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        RvSnooperRvTransportInputDialog.this.setVisible(false);
        RvSnooperRvTransportInputDialog.this.setOK(false);
      }
    });
    bottom.add(ok);
    bottom.add(cancel);
    getContentPane().add(main, "Center");
    getContentPane().add(bottom, "South");

    pack();
    centerWindow(this);
    setVisible(true);
  }

  public EMSParameters getRvParameters() {
    EMSParameters p = new EMSParameters();

    p.setUserName(this._tUserName.getText());
    p.setPassword(this._tPassword.getText());
    p.setServerURL(this._tServerURL.getText());
    p.setTopics(this._tTopic.getText());

    return p;
  }

  public boolean isOK() {
    return this.isOK;
  }

  protected void createInputField(String name, String defaultText, JTextField tf, JPanel addTo) {
    JLabel jl = new JLabel(name);

    addTo.add(jl);
    tf.setText(defaultText);
    addTo.add(tf);
    jl.setLabelFor(tf);
  }

  protected void setOK(boolean OK) {
    this.isOK = OK;
  }
}
