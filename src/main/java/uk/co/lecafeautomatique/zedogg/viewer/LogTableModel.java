package uk.co.lecafeautomatique.zedogg.viewer;

import javax.swing.table.DefaultTableModel;

public class LogTableModel extends DefaultTableModel {
  private static final long serialVersionUID = 3258125860492817204L;

  public LogTableModel(Object[] colNames, int numRows) {
    super(colNames, numRows);
  }

  public boolean isCellEditable(int row, int column) {
    return false;
  }
}
