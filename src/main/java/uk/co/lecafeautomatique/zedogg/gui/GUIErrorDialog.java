package uk.co.lecafeautomatique.zedogg.gui;


import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class GUIErrorDialog extends GUIDialog {

  public GUIErrorDialog(JFrame jframe, String message) {
    super(jframe, "Error", true);

    JButton ok = new JButton("Ok");
    ok.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        GUIErrorDialog.this.setVisible(false);
      }
    });
    JPanel bottom = new JPanel();
    bottom.setLayout(new FlowLayout());
    bottom.add(ok);

    JPanel main = new JPanel();
    main.setLayout(new GridBagLayout());
    wrapStringOnPanel(message, main);

    getContentPane().add(main, "Center");
    getContentPane().add(bottom, "South");
    setVisible(true);
  }
}
