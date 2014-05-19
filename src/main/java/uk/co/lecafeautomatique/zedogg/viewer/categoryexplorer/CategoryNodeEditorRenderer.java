package uk.co.lecafeautomatique.zedogg.viewer.categoryexplorer;

import java.awt.Component;
import javax.swing.JCheckBox;
import javax.swing.JTree;

public class CategoryNodeEditorRenderer extends CategoryNodeRenderer {
  private static final long serialVersionUID = 3256445819543369269L;

  public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
      boolean leaf, int row, boolean hasFocus) {
    Component c = super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

    return c;
  }

  public JCheckBox getCheckBox() {
    return this._checkBox;
  }
}
