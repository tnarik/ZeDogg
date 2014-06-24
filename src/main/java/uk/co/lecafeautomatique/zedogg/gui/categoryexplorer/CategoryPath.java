package uk.co.lecafeautomatique.zedogg.gui.categoryexplorer;

import java.util.LinkedList;

public class CategoryPath {
  protected LinkedList<CategoryElement> _categoryElements = new LinkedList();

  public CategoryPath() {
  }

  public CategoryPath(String category) {
    String processedCategory = category;

    if (processedCategory == null) {
      processedCategory = "NullDebug";
    }

    processedCategory = processedCategory.replace('/', '.');
    processedCategory = processedCategory.replace('\\', '.');

    String[] result = processedCategory.split("\\.");
    for (int x = 0; x < result.length; x++)
      addCategoryElement(new CategoryElement(result[x]));
  }

  public int size() {
    int count = this._categoryElements.size();

    return count;
  }

  public boolean isEmpty() {
    boolean empty = false;

    if (this._categoryElements.size() == 0) {
      empty = true;
    }

    return empty;
  }

  public void removeAllCategoryElements() {
    this._categoryElements.clear();
  }

  public void addCategoryElement(CategoryElement categoryElement) {
    this._categoryElements.addLast(categoryElement);
  }

  public CategoryElement categoryElementAt(int index) {
    return this._categoryElements.get(index);
  }

  public String toString() {
    StringBuffer out = new StringBuffer(100);

    out.append("\n===========================\n");
    out.append("TopicPath:                   \n");
    out.append("---------------------------\n");

    out.append("\nTopicPath:\n\t");

    if (size() > 0) {
      for (int i = 0; i < size(); i++) {
        out.append(categoryElementAt(i).toString());
        out.append("\n\t");
      }
    } else {
      out.append("<<NONE>>");
    }

    out.append("\n===========================\n");

    return out.toString();
  }
}
