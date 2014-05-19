package uk.co.lecafeautomatique.zedogg.viewer;

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class RvSnooperLoadingDialog extends RvSnooperDialog {
  private static final long serialVersionUID = 3690471424787952436L;

  public RvSnooperLoadingDialog(JFrame jframe, String message) {
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
