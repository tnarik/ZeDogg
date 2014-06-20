package uk.co.lecafeautomatique.zedogg.gui;

import uk.co.lecafeautomatique.zedogg.EventActionType;
import uk.co.lecafeautomatique.zedogg.LogRecord;
import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class LogTableRowRenderer extends DefaultTableCellRenderer {
  protected boolean _highlightFatal = true;
  protected Color _color = new Color(230, 230, 230);

  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
      int row, int col) {
    if (row % 2 == 0)
      setBackground(this._color);
    else {
      setBackground(Color.white);
    }

    FilteredLogTableModel model = (FilteredLogTableModel) table.getModel();
    LogRecord record = model.getFilteredRecord(row);

    setForeground(getLogLevelColor(record.getType()));

    return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
  }

  protected Color getLogLevelColor(EventActionType level) {
    return (Color) EventActionType.getLogLevelColorMap().get(level);
  }
}
