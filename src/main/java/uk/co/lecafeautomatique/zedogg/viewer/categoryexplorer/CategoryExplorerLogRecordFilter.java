package uk.co.lecafeautomatique.zedogg.viewer.categoryexplorer;

import uk.co.lecafeautomatique.zedogg.LogRecord;
import uk.co.lecafeautomatique.zedogg.LogRecordFilter;
import java.util.Enumeration;

public class CategoryExplorerLogRecordFilter implements LogRecordFilter {
  protected CategoryExplorerModel _model;

  public CategoryExplorerLogRecordFilter(CategoryExplorerModel model) {
    this._model = model;
  }

  public boolean passes(LogRecord record) {
    CategoryPath path = new CategoryPath(record.getJMSDestination());
    return this._model.isCategoryPathActive(path);
  }

  public void reset() {
    resetAllNodes();
  }

  protected void resetAllNodes() {
    Enumeration nodes = this._model.getRootCategoryNode().depthFirstEnumeration();

    while (nodes.hasMoreElements()) {
      CategoryNode current = (CategoryNode) nodes.nextElement();
      current.resetNumberOfContainedRecords();
      this._model.nodeChanged(current);
    }
  }
}
