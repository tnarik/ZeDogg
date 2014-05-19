package uk.co.lecafeautomatique.zedogg.gui;

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class GUILoadingDialog extends GUIDialog {

  public GUILoadingDialog(JFrame jframe, String message) {
    super(jframe, "EMSSn00p", false);

    JPanel bottom = new JPanel();
    bottom.setLayout(new FlowLayout());

    JPanel main = new JPanel();
    main.setLayout(new GridBagLayout());
    wrapStringOnPanel(message, main);

    getContentPane().add(main, "Center");
    getContentPane().add(bottom, "South");
    setVisible(true);
  }
}
