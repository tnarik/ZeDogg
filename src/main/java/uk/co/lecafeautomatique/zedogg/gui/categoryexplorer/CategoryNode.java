package uk.co.lecafeautomatique.zedogg.gui.categoryexplorer;

import java.util.Enumeration;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

public class CategoryNode extends DefaultMutableTreeNode {
  protected boolean _selected = true;
  protected int _numberOfContainedRecords = 0;
  protected int _numberOfRecordsFromChildren = 0;

  public CategoryNode(String title) {
    setUserObject(title);
  }

  public String getTitle() {
    return (String) getUserObject();
  }

  public void setSelected(boolean s) {
    if (s != this._selected)
      this._selected = s;
  }

  public boolean isSelected() {
    return this._selected;
  }

  public void setAllDescendantsSelected() {
    Enumeration children = children();
    while (children.hasMoreElements()) {
      CategoryNode node = (CategoryNode) children.nextElement();
      node.setSelected(true);
      node.setAllDescendantsDeSelected();
    }
  }

  public void setAllDescendantsDeSelected() {
    Enumeration children = children();
    while (children.hasMoreElements()) {
      CategoryNode node = (CategoryNode) children.nextElement();
      node.setSelected(false);
      node.setAllDescendantsDeSelected();
    }
  }

  public String toString() {
    return getTitle();
  }

  public boolean equals(Object obj) {
    if (this == obj) return true;
    if ((obj instanceof CategoryNode)) {
      CategoryNode node = (CategoryNode) obj;
      String tit1 = getTitle().toLowerCase();
      String tit2 = node.getTitle().toLowerCase();

      if (tit1.equals(tit2)) {
        return true;
      }
    }
    return false;
  }

  public int hashCode() {
    return getTitle().hashCode();
  }

  public void addRecord() {
    this._numberOfContainedRecords += 1;
    addRecordToParent();
  }

  public int getNumberOfContainedRecords() {
    return this._numberOfContainedRecords;
  }

  public void resetNumberOfContainedRecords() {
    this._numberOfContainedRecords = 0;
    this._numberOfRecordsFromChildren = 0;
  }

  protected int getTotalNumberOfRecords() {
    return getNumberOfRecordsFromChildren() + getNumberOfContainedRecords();
  }

  protected void addRecordFromChild() {
    this._numberOfRecordsFromChildren += 1;
    addRecordToParent();
  }

  protected int getNumberOfRecordsFromChildren() {
    return this._numberOfRecordsFromChildren;
  }

  protected void addRecordToParent() {
    TreeNode parent = getParent();
    if (parent == null) {
      return;
    }
    ((CategoryNode) parent).addRecordFromChild();
  }
}
