package uk.co.lecafeautomatique.zedogg.gui;

import uk.co.lecafeautomatique.zedogg.EventActionType;
import uk.co.lecafeautomatique.zedogg.MsgTypeFormatException;
import uk.co.lecafeautomatique.zedogg.gui.categoryexplorer.CategoryExplorerModel;
import uk.co.lecafeautomatique.zedogg.gui.categoryexplorer.CategoryExplorerTree;
import uk.co.lecafeautomatique.zedogg.gui.categoryexplorer.CategoryNode;
import uk.co.lecafeautomatique.zedogg.gui.categoryexplorer.CategoryPath;
import uk.co.lecafeautomatique.zedogg.util.DateFormatManager;
import uk.co.lecafeautomatique.zedogg.util.ems.EMSParameters;

import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.tree.TreePath;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ConfigurationManager {
  private static final String CONFIG_FILE_NAME = "conf.xml";
  public static final String CONFIG_DIR_NAME = ".zedogg";
  private static final String NAME = "name";
  private static final String PATH = "path";
  private static final String SELECTED = "selected";
  private static final String COLUMWIDTH = "columnsize";
  private static final String EXPANDED = "expanded";
  private static final String CATEGORY = "subject";
  private static final String FIRST_CATEGORY_NAME = "Topics";
  private static final String LEVEL = "type";
  private static final String COLORLEVEL = "colorlevel";
  private static final String COLOR = "color";
  private static final String RED = "red";
  private static final String GREEN = "green";
  private static final String BLUE = "blue";
  private static final String COLUMN = "column";
  private static final String SEP = System.getProperty("file.separator");
  private static final String FONTINFO = "fontinfo";
  private static final String FONTNAME = "name";
  private static final String FONTSIZE = "size";
  private static final String FONTSTYLE = "style";
  private static final String DATEFORMAT = "dateformat";
  private static final String DATEPATTERN = "pattern";
  private static final String SPLITPANEPOS = "splitpanepos";
  private static final String SPLITPANEPOSH = "horizontal";
  private static final String SPLITPANEPOSV = "vertical";
  private static final String WINDOWPOS = "windowpos";
  private static final String WINDOWX = "windowsx";
  private static final String WINDOWY = "windowy";
  private static final String WINDOWWIDTH = "windowwidth";
  private static final String WINDOWHEIGHT = "windowheight";
  private static final String LSNRSUBSCRIPTIONS = "SUBSCRIPTIONS";
  private static final String LSNRTIBLISTENER = "TIBLISTENER";
  private static final String LSNRTIBSERVER = "SERVERURL";
  private static final String LSNRTIBUSER = "USER";
  private static final String LSNRTIBPASSWORD = "PASSWORD";
  private static final String LSNRTIBTOPIC = "TOPIC";
  private static final String LSNRTIBTOPICID = "ID";
  private static final String RENDERER = "RENDERER";
  private GUI _gui = null;
  private LogTable _table = null;
  private String _fileName;

  public ConfigurationManager(GUI gui, LogTable table) {
    this._gui = gui;
    this._table = table;
    this._fileName = getDefaultFilename();
    load();
  }

  public ConfigurationManager(GUI gui, LogTable table, String fileName) {
    this._gui = gui;
    this._table = table;
    this._fileName = fileName;
    load();
  }

  public void save() throws IOException {
    CategoryExplorerModel model = this._gui.getCategoryExplorerTree().getExplorerModel();
    CategoryNode root = model.getRootCategoryNode();

    StringBuffer xml = new StringBuffer(2048);
    openXMLDocument(xml);
    openConfigurationXML(xml);
    processMsgTypes(this._gui.getLogLevelMenuItems(), xml);
    processMsgTypesColors(this._gui.getLogLevelMenuItems(), EventActionType.getLogLevelColorMap(), xml);

    processLogTableColumns(LogTableColumn.getLogTableColumns(), xml);

    processConfigurationNode(root, xml);

    processFont(this._table.getFont(), xml);

    processDateFormat(this._gui, xml);

    processSplitPanes(this._gui, xml);

    processWindowPosition(this._gui, xml);

    processListeners(this._gui.getSubscriptions(), xml);

    processLastUsedRenderer(this._gui.getLastUsedRenderer(), xml);

    closeConfigurationXML(xml);
    store(xml.toString());
  }

  private void processLastUsedRenderer(String lastUsedRenderer, StringBuffer xml) {
    if (lastUsedRenderer == null) {
      return;
    }
    xml.append("\t<RENDERER>").append(lastUsedRenderer);
    xml.append("</RENDERER>\n");
  }

  private void processListeners(Iterator it, StringBuffer xml) {
    if (it == null) {
      return;
    }
    xml.append("\t<SUBSCRIPTIONS>\n");

    while (it.hasNext()) {
      exportListener((EMSParameters) it.next(), xml);
    }

    xml.append("\t</SUBSCRIPTIONS>\n");
  }

  private void exportListener(EMSParameters rvParam, StringBuffer xml) {
    xml.append("\t\t<TIBLISTENER ");
    xml.append("SERVERURL=\"").append(rvParam.getServerURL() == null ? "" : rvParam.getServerURL())
        .append("\" ");
    xml.append("USER=\"").append(rvParam.getUserName() == null ? "" : rvParam.getUserName()).append("\" ");
    xml.append("PASSWORD=\"").append(rvParam.getPassword() == null ? "" : rvParam.getPassword())
        .append("\" >\n");

    Set s = rvParam.getTopics();
    if (s != null) {
      Iterator it = s.iterator();
      while (it.hasNext()) {
        xml.append("\t\t\t<TOPIC ");
        String Ttopic = (String) it.next();
        Ttopic = Ttopic.replaceAll(">", "&gt;");
        xml.append("ID=\"").append(Ttopic).append("\" />\n");
      }
    }
    xml.append("\t\t</TIBLISTENER>\n");
  }

  private void processWindowPosition(GUI gui, StringBuffer xml) {
    exportWindowPosition(gui.getWindowBounds(), xml);
  }

  private void exportWindowPosition(Rectangle r, StringBuffer xml) {
    xml.append("\t<windowpos ");
    xml.append("windowsx=\"").append(String.valueOf((int) r.getX())).append("\" ");
    xml.append("windowy=\"").append(String.valueOf((int) r.getY())).append("\" ");
    xml.append("windowwidth=\"").append(String.valueOf((int) r.getWidth())).append("\" ");
    xml.append("windowheight=\"").append(String.valueOf((int) r.getHeight())).append("\" ");
    xml.append("/>\n\r");
  }

  private void processSplitPanes(GUI gui, StringBuffer xml) {
    exportSplitPanes(gui.getSplitPaneTableViewerPos(), gui.getSplitPaneVerticalPos(), xml);
  }

  private void exportSplitPanes(int horizontal, int vertical, StringBuffer xml) {
    xml.append("\t<splitpanepos ");
    xml.append("horizontal=\"").append(String.valueOf(horizontal)).append("\" ");
    xml.append("vertical=\"").append(String.valueOf(vertical)).append("\" ");
    xml.append("/>\n\r");
  }

  private void processDateFormat(GUI gui, StringBuffer xml) {
    exportDateFormatPattern(gui.getDateFormat(), xml);
  }

  private void exportDateFormatPattern(String pattern, StringBuffer xml) {
    xml.append("\t<dateformat ");
    xml.append("pattern=\"").append(pattern).append("\" ");
    xml.append("/>\n\r");
  }

  protected void processDateFormat(Document doc) {
    try {
      NodeList nodes = doc.getElementsByTagName("dateformat");

      Node n = nodes.item(0);
      NamedNodeMap map = n.getAttributes();
      String dateFormatPattern = getValue(map, "pattern");

      if (dateFormatPattern != null) {
        this._gui.setDateFormat(dateFormatPattern);
      }
    } catch (Exception e1) {
      this._gui.setDateFormat("HH:mm:ss.S");
    }
  }

  public void reset() {
    deleteConfigurationFile();
    collapseTree();
    selectAllNodes();
  }

  public static String treePathToString(TreePath path) {
    StringBuffer sb = new StringBuffer();
    CategoryNode n = null;
    Object[] objects = path.getPath();
    for (int i = 1; i < objects.length; i++) {
      n = (CategoryNode) objects[i];
      if (i > 1) {
        sb.append('.');
      }
      sb.append(n.getTitle());
    }
    return sb.toString();
  }

  protected void load() {
    File file = new File(getFilename());
    if (file.exists()) {
      try {
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();

        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        Document doc = docBuilder.parse(file);
        doc.normalize();
        processCategories(doc);
        processMsgTypes(doc);
        processLogLevelColors(doc);
        processLogTableColumns(doc);
        processFont(doc);
        processDateFormat(doc);
        processSplitPanes(doc);
        processWindowPosition(doc);

        processListeners(doc);
        processRenderer(doc);
      } catch (Exception e) {
        System.err.println("Unable process configuration file at " + getFilename() + ". Error Message="
            + e.getMessage());
      }

    } else {
      this._table.setDateFormatManager(new DateFormatManager("HH:mm:ss.S"));
    }
  }

  private void processRenderer(Document doc) {
    NodeList nodeList = doc.getElementsByTagName("RENDERER");

    for (int i = 0; i < nodeList.getLength(); i++) {
      Node n = nodeList.item(i);

      if (n == null) {
        return;
      }

      String value = null;
      NodeList children = n.getChildNodes();
      for (int ii = 0; ii < children.getLength(); ii++) {
        Node ci = children.item(ii);
        if (ci.getNodeType() == 3) {
          value = ci.getNodeValue();
        }
      }

      this._gui.setLastUsedRenderer(value);
    }
  }

  private String getType(Node n) {
    int type = n.getNodeType();
    switch (type) {
    case 1:
      return "Element";
    case 2:
      return "Attribute";
    case 3:
      return "Text";
    case 4:
      return "CDATA Section";
    case 5:
      return "Entity Reference";
    case 6:
      return "Entity";
    case 7:
      return "Processing Instruction";
    case 8:
      return "Comment";
    case 9:
      return "Document";
    case 10:
      return "Document Type Declaration";
    case 11:
      return "Document Fragment";
    case 12:
      return "Notation";
    }
    return "Unknown Type";
  }

  private void processListeners(Document doc) {
    try {
      NodeList tiblisternodes = doc.getElementsByTagName("SUBSCRIPTIONS");

      if (tiblisternodes == null) {
        return;
      }

      NodeList ln = tiblisternodes.item(0).getChildNodes();
      Set setParameters = new HashSet();
      int len = ln.getLength();
      for (int i = 0; i < len; i++) {
        EMSParameters p = new EMSParameters();
        Node listener = ln.item(i);
        if (listener.getNodeType() != 3) {
          if (listener.hasAttributes()) {
            NamedNodeMap nnm = listener.getAttributes();

            Node server = nnm.getNamedItem("SERVERURL");
            Node user = nnm.getNamedItem("USER");
            Node password = nnm.getNamedItem("PASSWORD");

            p.setServerURL(server.getNodeValue());
            p.setUserName(user.getNodeValue());
            p.setPassword(password.getNodeValue());
          }

          NodeList subs = listener.getChildNodes();
          int leni = subs.getLength();
          Set setListeners = new HashSet();
          for (int iSubscription = 0; iSubscription < leni; iSubscription++) {
            Node subscription = subs.item(iSubscription);

            if (subscription.getNodeType() != 3) {
              if (subscription.hasAttributes()) {
                NamedNodeMap nnm = subscription.getAttributes();
                Node id = nnm.getNamedItem("ID");
                String Ttopic = id.getNodeValue();
                Ttopic = Ttopic.replaceAll("&gt;", ">");

                setListeners.add(id.getNodeValue());
              }
              p.setTopics(setListeners);
            }
          }
          setParameters.add(p);
        }
      }

      this._gui.startListeners(setParameters);
    } catch (DOMException e) {
      System.err.println(e.getLocalizedMessage());
    }
  }

  private void processWindowPosition(Document doc) {
    try {
      NodeList nodes = doc.getElementsByTagName("windowpos");

      Node n = nodes.item(0);
      NamedNodeMap map = n.getAttributes();
      String windowHeight = getValue(map, "windowheight");
      String windowWidth = getValue(map, "windowwidth");
      String windowX = getValue(map, "windowsx");
      String windowY = getValue(map, "windowy");

      if ((windowHeight != null) && (windowWidth != null) && (windowX != null) && (windowY != null)) {
        Rectangle r = new Rectangle(Integer.parseInt(windowX), Integer.parseInt(windowY),
            Integer.parseInt(windowWidth), Integer.parseInt(windowHeight));

        this._gui.setWindowBounds(r);
      } else {
        throw new Exception("");
      }
    } catch (Exception e1) {
      e1.printStackTrace();

      this._gui.updateFrameSize();
    }
  }

  protected void processSplitPanes(Document doc) {
    try {
      NodeList nodes = doc.getElementsByTagName("splitpanepos");

      Node n = nodes.item(0);
      NamedNodeMap map = n.getAttributes();
      String sizeHorizontal = getValue(map, "horizontal");
      String sizeVertical = getValue(map, "vertical");

      if (sizeHorizontal != null) {
        this._gui.setSplitPaneTableViewerPos(Integer.parseInt(sizeHorizontal));
        this._gui.setSplitPaneVerticalPos(Integer.parseInt(sizeVertical));
      } else {
        throw new Exception("");
      }
    } catch (Exception e1) {
      this._gui.setSplitPaneVerticalPos(130);
      this._gui.setSplitPaneTableViewerPos(350);
    }
  }

  protected void processCategories(Document doc) {
    CategoryExplorerTree tree = this._gui.getCategoryExplorerTree();
    CategoryExplorerModel model = tree.getExplorerModel();
    NodeList nodeList = doc.getElementsByTagName("subject");

    NamedNodeMap map = nodeList.item(0).getAttributes();
    int j = getValue(map, "name").equalsIgnoreCase("Topics") ? 1 : 0;

    for (int i = nodeList.getLength() - 1; i >= j; i--) {
      Node n = nodeList.item(i);
      map = n.getAttributes();
      CategoryNode chnode = model.addCategory(new CategoryPath(getValue(map, "path")));
      chnode.setSelected(getValue(map, "selected").equalsIgnoreCase("true"));
      /* REMOVE
      if (getValue(map, "expanded").equalsIgnoreCase("true"))
        ;
      */
      tree.expandPath(model.getTreePathToRoot(chnode));
    }
  }

  protected void processMsgTypes(Document doc) {
    NodeList nodeList = doc.getElementsByTagName("type");
    Map menuItems = this._gui.getLogLevelMenuItems();

    for (int i = 0; i < nodeList.getLength(); i++) {
      Node n = nodeList.item(i);
      NamedNodeMap map = n.getAttributes();
      String name = getValue(map, "name");
      try {
        JCheckBoxMenuItem item = (JCheckBoxMenuItem) menuItems.get(EventActionType.valueOf(name));

        item.setSelected(getValue(map, "selected").equalsIgnoreCase("true"));
      } catch (MsgTypeFormatException e) {
      }
    }
  }

  protected void processLogLevelColors(Document doc) {
    NodeList nodeList = doc.getElementsByTagName("colorlevel");
    Map logLevelColors = EventActionType.getLogLevelColorMap();

    for (int i = 0; i < nodeList.getLength(); i++) {
      Node n = nodeList.item(i);

      if (n == null) {
        return;
      }

      NamedNodeMap map = n.getAttributes();
      String name = getValue(map, "name");
      try {
        EventActionType level = EventActionType.valueOf(name);
        int red = Integer.parseInt(getValue(map, "red"));
        int green = Integer.parseInt(getValue(map, "green"));
        int blue = Integer.parseInt(getValue(map, "blue"));
        Color c = new Color(red, green, blue);
        if (level != null)
          level.setLogLevelColorMap(level, c);
      } catch (MsgTypeFormatException e) {
      }
    }
  }

  protected void processFont(Document doc) {
    try {
      NodeList nodes = doc.getElementsByTagName("fontinfo");

      String fontSize = null;
      String fontName = null;

      Node n = nodes.item(0);
      NamedNodeMap map = n.getAttributes();
      fontName = getValue(map, "name");
      fontSize = getValue(map, "size");
      String fontStyle = getValue(map, "style");

      if ((fontName != null) && (fontSize != null)) {
        this._gui.setFontSize(Integer.parseInt(fontStyle));
        this._gui.setFontName(fontName);
      }
    } catch (Exception e1) {
      this._gui.setFontSize(12);
    }
  }

  private void processFont(Font f, StringBuffer xml) {
    exportFont(f.getFontName(), f.getStyle(), f.getSize(), xml);
  }

  private void exportFont(String fontName, int style, int size, StringBuffer xml) {
    xml.append("\t<fontinfo ");
    xml.append("name=\"").append(fontName).append("\" ");
    xml.append("style=\"").append(style).append("\" ");
    xml.append("size=\"").append(size).append("\" ");
    xml.append("/>\n\r");
  }

  private void appendStartTag(StringBuffer s, String tag) {
    s.append("\t<");
    s.append(tag);
    s.append(">\r\n");
  }

  private void appendCloseTag(StringBuffer s, String tag) {
    s.append("\n</");
    s.append(tag);
    s.append(">\r\n");
  }

  protected void processLogTableColumns(Document doc) {
    NodeList nodeList = doc.getElementsByTagName("column");
    Map menuItems = this._gui.getLogTableColumnMenuItems();
    List selectedColumns = new ArrayList();
    for (int i = 0; i < nodeList.getLength(); i++) {
      Node n = nodeList.item(i);

      if (n == null) {
        return;
      }
      NamedNodeMap map = n.getAttributes();
      String name = getValue(map, "name");
      try {
        LogTableColumn column = LogTableColumn.valueOf(name);
        JCheckBoxMenuItem item = (JCheckBoxMenuItem) menuItems.get(column);

        item.setSelected(getValue(map, "selected").equalsIgnoreCase("true"));

        if (item.isSelected()) {
          selectedColumns.add(column);
        }
      } catch (LogTableColumnFormatException e) {
      }
    }
    if (selectedColumns.isEmpty())
      this._table.setDetailedView();
    else {
      this._table.setView(selectedColumns);
    }

    for (int i = 0; i < nodeList.getLength(); i++) {
      Node n = nodeList.item(i);

      if (n == null) {
        return;
      }
      NamedNodeMap map = n.getAttributes();
      try {
        String name = getValue(map, "name");
        String width = getValue(map, "columnsize");

        this._table.setColumnWidth(name, Integer.parseInt(width));
      } catch (Exception e) {
      }
    }
  }

  protected String getValue(NamedNodeMap map, String attr) {
    Node n = map.getNamedItem(attr);
    return n.getNodeValue();
  }

  protected void collapseTree() {
    CategoryExplorerTree tree = this._gui.getCategoryExplorerTree();
    for (int i = tree.getRowCount() - 1; i > 0; i--)
      tree.collapseRow(i);
  }

  protected void selectAllNodes() {
    CategoryExplorerModel model = this._gui.getCategoryExplorerTree().getExplorerModel();
    CategoryNode root = model.getRootCategoryNode();
    Enumeration all = root.breadthFirstEnumeration();
    CategoryNode n = null;
    while (all.hasMoreElements()) {
      n = (CategoryNode) all.nextElement();
      n.setSelected(true);
    }
  }

  public static void createConfigurationDirectory() {
    String home = System.getProperty("user.home");
    File f = new File(home + SEP + CONFIG_DIR_NAME);
    if (!f.exists())
      try {
        f.mkdirs();
      } catch (SecurityException e) {
        throw e;
      }
  }

  protected void store(String s) throws IOException {
    try {
      createConfigurationDirectory();

      File f = new File(getFilename());

      if (!f.exists()) {
        try {
          f.createNewFile();
        } catch (Exception e) {
        } finally {
          f = null;
        }

      }

      PrintWriter writer = new PrintWriter(new FileWriter(getFilename()));

      writer.print(s);
      writer.close();
    } catch (IOException e) {
      throw e;
    }
  }

  protected void deleteConfigurationFile() {
    try {
      File f = new File(getFilename());
      if (f.exists())
        f.delete();
    } catch (SecurityException e) {
      System.err.println("Cannot delete " + getFilename() + " because a security violation occured.");
    }
  }

  protected String getDefaultFilename() {
    String home = System.getProperty("user.home");
    return home + SEP + CONFIG_DIR_NAME + SEP + CONFIG_FILE_NAME;
  }

  public String getFilename() {
    return this._fileName;
  }

  public void setFilename(String fileName) {
    this._fileName = fileName;
  }

  private void processConfigurationNode(CategoryNode node, StringBuffer xml) {
    CategoryExplorerModel model = this._gui.getCategoryExplorerTree().getExplorerModel();

    Enumeration all = node.breadthFirstEnumeration();
    CategoryNode n = null;
    while (all.hasMoreElements()) {
      n = (CategoryNode) all.nextElement();
      exportXMLElement(n, model.getTreePathToRoot(n), xml);
    }
  }

  private void processMsgTypes(Map logLevelMenuItems, StringBuffer xml) {
    xml.append("\t<msgtypes>\r\n");
    Iterator it = logLevelMenuItems.keySet().iterator();
    while (it.hasNext()) {
      EventActionType level = (EventActionType) it.next();
      JCheckBoxMenuItem item = (JCheckBoxMenuItem) logLevelMenuItems.get(level);
      exportLogLevelXMLElement(level.getLabel(), item.isSelected(), xml);
    }

    xml.append("\t</msgtypes>\r\n");
  }

  private void processMsgTypesColors(Map logLevelMenuItems, Map logLevelColors, StringBuffer xml) {
    xml.append("\t<msgtypescolors>\r\n");

    Iterator it = logLevelMenuItems.keySet().iterator();
    while (it.hasNext()) {
      EventActionType level = (EventActionType) it.next();

      Color color = (Color) logLevelColors.get(level);
      exportLogLevelColorXMLElement(level.getLabel(), color, xml);
    }

    xml.append("\t</msgtypescolors>\r\n");
  }

  private void processLogTableColumns(List logTableColumnMenuItems, StringBuffer xml) {
    xml.append("\t<logtablecolumns>\r\n");
    Iterator it = logTableColumnMenuItems.iterator();
    while (it.hasNext()) {
      LogTableColumn column = (LogTableColumn) it.next();
      JCheckBoxMenuItem item = this._gui.getTableColumnMenuItem(column);
      int size = this._table.getColumnWidth(column.getLabel());
      exportLogTableColumnXMLElement(column.getLabel(), item.isSelected(), size, xml);
    }

    xml.append("\t</logtablecolumns>\r\n");
  }

  private void openXMLDocument(StringBuffer xml) {
    xml.append("<?xml version=\"1.0\" encoding=\"" + System.getProperty("file.encoding") + "\" ?>\r\n");
  }

  private void openConfigurationXML(StringBuffer xml) {
    xml.append("<configuration>\r\n");
  }

  private void closeConfigurationXML(StringBuffer xml) {
    xml.append("</configuration>\r\n");
  }

  private void exportXMLElement(CategoryNode node, TreePath path, StringBuffer xml) {
    CategoryExplorerTree tree = this._gui.getCategoryExplorerTree();

    xml.append("\t<subject ");
    xml.append("name=\"").append(node.getTitle()).append("\" ");
    xml.append("path=\"").append(treePathToString(path)).append("\" ");
    xml.append("expanded=\"").append(tree.isExpanded(path)).append("\" ");
    xml.append("selected=\"").append(node.isSelected()).append("\"/>\r\n");
  }

  private void exportLogLevelXMLElement(String label, boolean selected, StringBuffer xml) {
    xml.append("\t\t<type name");
    xml.append("=\"").append(label).append("\" ");
    xml.append("selected=\"").append(selected);
    xml.append("\"/>\r\n");
  }

  private void exportLogLevelColorXMLElement(String label, Color color, StringBuffer xml) {
    xml.append("\t\t<colorlevel name");
    xml.append("=\"").append(label).append("\" ");
    xml.append("red=\"").append(color.getRed()).append("\" ");
    xml.append("green=\"").append(color.getGreen()).append("\" ");
    xml.append("blue=\"").append(color.getBlue());
    xml.append("\"/>\r\n");
  }

  private void exportLogTableColumnXMLElement(String label, boolean selected, int size, StringBuffer xml) {
    xml.append("\t\t<column name");
    xml.append("=\"").append(label).append("\" ");
    xml.append("selected=\"").append(selected).append("\" ");
    xml.append("columnsize=\"").append(String.valueOf(size));
    xml.append("\"/>\r\n");
  }
}
