package uk.co.lecafeautomatique.zedogg.gui.categoryexplorer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultTreeCellRenderer;

public class CategoryNodeRenderer extends DefaultTreeCellRenderer {
  protected JCheckBox _checkBox = new JCheckBox();
  protected JPanel _panel = new JPanel();

  public CategoryNodeRenderer() {
    this._panel.setBackground(UIManager.getColor("Tree.textBackground"));

    this._checkBox.setOpaque(false);

    this._panel.setLayout(new FlowLayout(0, 0, 0));
    this._panel.add(this._checkBox);
    this._panel.add(this);

    setOpenIcon(null);
    setClosedIcon(null);
    setLeafIcon(null);
  }

  public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
      boolean leaf, int row, boolean hasFocus) {
    CategoryNode node = (CategoryNode) value;

    super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

    if (row == 0) {
      this._checkBox.setVisible(false);
    } else {
      this._checkBox.setVisible(true);
      this._checkBox.setSelected(node.isSelected());
    }
    String toolTip = buildToolTip(node);
    this._panel.setToolTipText(toolTip);

    setForeground(Color.BLACK);

    return this._panel;
  }

  public Dimension getCheckBoxOffset() {
    return new Dimension(0, 0);
  }

  protected String buildToolTip(CategoryNode node) {
    StringBuffer result = new StringBuffer();
    result.append(node.getTotalNumberOfRecords());
    result.append(" records has been registered by this node.");
    result.append(" Right-click for more info.");
    return result.toString();
  }
}
