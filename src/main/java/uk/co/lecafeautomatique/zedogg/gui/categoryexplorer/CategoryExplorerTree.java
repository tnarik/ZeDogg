package uk.co.lecafeautomatique.zedogg.gui.categoryexplorer;


import javax.swing.JTree;
import javax.swing.event.TreeModelEvent;
import javax.swing.tree.TreePath;

public class CategoryExplorerTree extends JTree {
  protected CategoryExplorerModel _model;
  protected boolean _rootAlreadyExpanded = false;

  public CategoryExplorerTree(CategoryExplorerModel model) {
    super(model);

    this._model = model;
    init();
  }

  public CategoryExplorerTree() {
    CategoryNode rootNode = new CategoryNode("Topics");

    this._model = new CategoryExplorerModel(rootNode);

    setModel(this._model);

    init();
  }

  public CategoryExplorerModel getExplorerModel() {
    return this._model;
  }
/* REMOVE
  public String getToolTipText(MouseEvent e) {
    try {
      return super.getToolTipText(e);
    } catch (Exception ex) {
    }
    return "";
  }
  
  */

  protected void init() {
    putClientProperty("JTree.lineStyle", "Angled");

    CategoryNodeRenderer renderer = new CategoryNodeRenderer();
    setEditable(true);
    setCellRenderer(renderer);

    CategoryNodeEditor editor = new CategoryNodeEditor(this._model);

    setCellEditor(new CategoryImmediateEditor(this, new CategoryNodeRenderer(), editor));

    setShowsRootHandles(true);

    setToolTipText("");

    ensureRootExpansion();

    this._model.updateAllNodes();
  }

  protected void expandRootNode() {
    if (this._rootAlreadyExpanded) {
      return;
    }
    this._rootAlreadyExpanded = true;
    TreePath path = new TreePath(this._model.getRootCategoryNode().getPath());
    expandPath(path);
  }

  protected void ensureRootExpansion() {
    this._model.addTreeModelListener(new TreeModelAdapter() {
      public void treeNodesInserted(TreeModelEvent e) {
        CategoryExplorerTree.this.expandRootNode();
      }
    });
  }
}
