package uk.co.lecafeautomatique.zedogg.gui;

import javax.swing.table.DefaultTableModel;

public class LogTableModel extends DefaultTableModel {

  public LogTableModel(Object[] colNames, int numRows) {
    super(colNames, numRows);
  }

  public boolean isCellEditable(int row, int column) {
    return false;
  }
}
