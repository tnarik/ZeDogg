/*     */ package emssn00p.viewer;
/*     */ 
/*     */ import emssn00p.EventActionType;
/*     */ import emssn00p.MsgTypeFormatException;
/*     */ import emssn00p.util.DateFormatManager;
/*     */ import emssn00p.util.ems.EMSParameters;
/*     */ import emssn00p.viewer.categoryexplorer.CategoryExplorerModel;
/*     */ import emssn00p.viewer.categoryexplorer.CategoryExplorerTree;
/*     */ import emssn00p.viewer.categoryexplorer.CategoryNode;
/*     */ import emssn00p.viewer.categoryexplorer.CategoryPath;
/*     */ import java.awt.Color;
/*     */ import java.awt.Font;
/*     */ import java.awt.Rectangle;
/*     */ import java.io.File;
/*     */ import java.io.FileWriter;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Enumeration;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import javax.swing.JCheckBoxMenuItem;
/*     */ import javax.swing.tree.TreePath;
/*     */ import javax.xml.parsers.DocumentBuilder;
/*     */ import javax.xml.parsers.DocumentBuilderFactory;
/*     */ import org.w3c.dom.DOMException;
/*     */ import org.w3c.dom.Document;
/*     */ import org.w3c.dom.NamedNodeMap;
/*     */ import org.w3c.dom.Node;
/*     */ import org.w3c.dom.NodeList;
/*     */ 
/*     */ public class ConfigurationManager
/*     */ {
/*     */   private static final String CONFIG_FILE_NAME = "conf.xml";
/*     */   public static final String CONFIG_DIR_NAME = ".emssnoop";
/*     */   private static final String NAME = "name";
/*     */   private static final String PATH = "path";
/*     */   private static final String SELECTED = "selected";
/*     */   private static final String COLUMWIDTH = "columnsize";
/*     */   private static final String EXPANDED = "expanded";
/*     */   private static final String CATEGORY = "subject";
/*     */   private static final String FIRST_CATEGORY_NAME = "Topics";
/*     */   private static final String LEVEL = "type";
/*     */   private static final String COLORLEVEL = "colorlevel";
/*     */   private static final String COLOR = "color";
/*     */   private static final String RED = "red";
/*     */   private static final String GREEN = "green";
/*     */   private static final String BLUE = "blue";
/*     */   private static final String COLUMN = "column";
/*  71 */   private static final String SEP = System.getProperty("file.separator");
/*     */   private static final String FONTINFO = "fontinfo";
/*     */   private static final String FONTNAME = "name";
/*     */   private static final String FONTSIZE = "size";
/*     */   private static final String FONTSTYLE = "style";
/*     */   private static final String DATEFORMAT = "dateformat";
/*     */   private static final String DATEPATTERN = "pattern";
/*     */   private static final String SPLITPANEPOS = "splitpanepos";
/*     */   private static final String SPLITPANEPOSH = "horizontal";
/*     */   private static final String SPLITPANEPOSV = "vertical";
/*     */   private static final String WINDOWPOS = "windowpos";
/*     */   private static final String WINDOWX = "windowsx";
/*     */   private static final String WINDOWY = "windowy";
/*     */   private static final String WINDOWWIDTH = "windowwidth";
/*     */   private static final String WINDOWHEIGHT = "windowheight";
/*     */   private static final String LSNRSUBSCRIPTIONS = "SUBSCRIPTIONS";
/*     */   private static final String LSNRTIBLISTENER = "TIBLISTENER";
/*     */   private static final String LSNRTIBSERVER = "SERVERURL";
/*     */   private static final String LSNRTIBUSER = "USER";
/*     */   private static final String LSNRTIBPASSWORD = "PASSWORD";
/*     */   private static final String LSNRTIBTOPIC = "TOPIC";
/*     */   private static final String LSNRTIBTOPICID = "ID";
/*     */   private static final String RENDERER = "RENDERER";
/* 103 */   private RvSnooperGUI _gui = null;
/* 104 */   private LogTable _table = null;
/*     */   private String _fileName;
/*     */ 
/*     */   public ConfigurationManager(RvSnooperGUI gui, LogTable table)
/*     */   {
/* 113 */     this._gui = gui;
/* 114 */     this._table = table;
/* 115 */     this._fileName = getDefaultFilename();
/* 116 */     load();
/*     */   }
/*     */ 
/*     */   public ConfigurationManager(RvSnooperGUI gui, LogTable table, String fileName)
/*     */   {
/* 121 */     this._gui = gui;
/* 122 */     this._table = table;
/* 123 */     this._fileName = fileName;
/* 124 */     load();
/*     */   }
/*     */ 
/*     */   public void save()
/*     */     throws IOException
/*     */   {
/* 131 */     CategoryExplorerModel model = this._gui.getCategoryExplorerTree().getExplorerModel();
/* 132 */     CategoryNode root = model.getRootCategoryNode();
/*     */ 
/* 134 */     StringBuffer xml = new StringBuffer(2048);
/* 135 */     openXMLDocument(xml);
/* 136 */     openConfigurationXML(xml);
/* 137 */     processMsgTypes(this._gui.getLogLevelMenuItems(), xml);
/* 138 */     processMsgTypesColors(this._gui.getLogLevelMenuItems(), EventActionType.getLogLevelColorMap(), xml);
/*     */ 
/* 141 */     processLogTableColumns(LogTableColumn.getLogTableColumns(), xml);
/*     */ 
/* 144 */     processConfigurationNode(root, xml);
/*     */ 
/* 146 */     processFont(this._table.getFont(), xml);
/*     */ 
/* 148 */     processDateFormat(this._gui, xml);
/*     */ 
/* 150 */     processSplitPanes(this._gui, xml);
/*     */ 
/* 152 */     processWindowPosition(this._gui, xml);
/*     */ 
/* 154 */     processListeners(this._gui.getSubscriptions(), xml);
/*     */ 
/* 156 */     processLastUsedRenderer(this._gui.getLastUsedRenderer(), xml);
/*     */ 
/* 159 */     closeConfigurationXML(xml);
/* 160 */     store(xml.toString());
/*     */   }
/*     */ 
/*     */   private void processLastUsedRenderer(String lastUsedRenderer, StringBuffer xml)
/*     */   {
/* 168 */     if (lastUsedRenderer == null) {
/* 169 */       return;
/*     */     }
/* 171 */     xml.append("\t<").append("RENDERER").append(">").append(lastUsedRenderer);
/* 172 */     xml.append("</").append("RENDERER").append(">\n");
/*     */   }
/*     */ 
/*     */   private void processListeners(Iterator it, StringBuffer xml)
/*     */   {
/* 178 */     if (it == null) {
/* 179 */       return;
/*     */     }
/* 181 */     xml.append("\t<").append("SUBSCRIPTIONS").append(">\n");
/*     */ 
/* 183 */     while (it.hasNext()) {
/* 184 */       exportListener((EMSParameters)it.next(), xml);
/*     */     }
/*     */ 
/* 187 */     xml.append("\t</").append("SUBSCRIPTIONS").append(">\n");
/*     */   }
/*     */ 
/*     */   private void exportListener(EMSParameters rvParam, StringBuffer xml)
/*     */   {
/* 192 */     xml.append("\t\t<").append("TIBLISTENER").append(" ");
/* 193 */     xml.append("SERVERURL").append("=\"").append(rvParam.getServerURL() == null ? "" : rvParam.getServerURL()).append("\" ");
/* 194 */     xml.append("USER").append("=\"").append(rvParam.getUserName() == null ? "" : rvParam.getUserName()).append("\" ");
/* 195 */     xml.append("PASSWORD").append("=\"").append(rvParam.getPassword() == null ? "" : rvParam.getPassword()).append("\" >\n");
/*     */ 
/* 198 */     Set s = rvParam.getTopics();
/* 199 */     if (s != null) {
/* 200 */       Iterator it = s.iterator();
/* 201 */       while (it.hasNext()) {
/* 202 */         xml.append("\t\t\t<").append("TOPIC").append(" ");
/* 203 */         String Ttopic = (String)it.next();
/* 204 */         Ttopic = Ttopic.replaceAll(">", "&gt;");
/* 205 */         xml.append("ID").append("=\"").append(Ttopic).append("\" />\n");
/*     */       }
/*     */     }
/* 208 */     xml.append("\t\t</").append("TIBLISTENER").append(">\n");
/*     */   }
/*     */ 
/*     */   private void processWindowPosition(RvSnooperGUI gui, StringBuffer xml)
/*     */   {
/* 213 */     exportWindowPosition(gui.getWindowBounds(), xml);
/*     */   }
/*     */ 
/*     */   private void exportWindowPosition(Rectangle r, StringBuffer xml) {
/* 217 */     xml.append("\t<").append("windowpos").append(" ");
/* 218 */     xml.append("windowsx").append("=\"").append(String.valueOf((int)r.getX())).append("\"").append(" ");
/* 219 */     xml.append("windowy").append("=\"").append(String.valueOf((int)r.getY())).append("\"").append(" ");
/* 220 */     xml.append("windowwidth").append("=\"").append(String.valueOf((int)r.getWidth())).append("\"").append(" ");
/* 221 */     xml.append("windowheight").append("=\"").append(String.valueOf((int)r.getHeight())).append("\"").append(" ");
/* 222 */     xml.append("/>\n\r");
/*     */   }
/*     */ 
/*     */   private void processSplitPanes(RvSnooperGUI gui, StringBuffer xml) {
/* 226 */     exportSplitPanes(gui.getSplitPaneTableViewerPos(), gui.getSplitPaneVerticalPos(), xml);
/*     */   }
/*     */ 
/*     */   private void exportSplitPanes(int horizontal, int vertical, StringBuffer xml) {
/* 230 */     xml.append("\t<").append("splitpanepos").append(" ");
/* 231 */     xml.append("horizontal").append("=\"").append(String.valueOf(horizontal)).append("\"").append(" ");
/* 232 */     xml.append("vertical").append("=\"").append(String.valueOf(vertical)).append("\"").append(" ");
/* 233 */     xml.append("/>\n\r");
/*     */   }
/*     */ 
/*     */   private void processDateFormat(RvSnooperGUI gui, StringBuffer xml)
/*     */   {
/* 238 */     exportDateFormatPattern(gui.getDateFormat(), xml);
/*     */   }
/*     */ 
/*     */   private void exportDateFormatPattern(String pattern, StringBuffer xml)
/*     */   {
/* 243 */     xml.append("\t<").append("dateformat").append(" ");
/* 244 */     xml.append("pattern").append("=\"").append(pattern).append("\"").append(" ");
/* 245 */     xml.append("/>\n\r");
/*     */   }
/*     */ 
/*     */   protected void processDateFormat(Document doc)
/*     */   {
/*     */     try
/*     */     {
/* 252 */       NodeList nodes = doc.getElementsByTagName("dateformat");
/*     */ 
/* 256 */       Node n = nodes.item(0);
/* 257 */       NamedNodeMap map = n.getAttributes();
/* 258 */       String dateFormatPattern = getValue(map, "pattern");
/*     */ 
/* 260 */       if (dateFormatPattern != null) {
/* 261 */         this._gui.setDateFormat(dateFormatPattern);
/*     */       }
/*     */     }
/*     */     catch (Exception e1)
/*     */     {
/* 266 */       this._gui.setDateFormat("HH:mm:ss.S");
/*     */     }
/*     */   }
/*     */ 
/*     */   public void reset()
/*     */   {
/* 273 */     deleteConfigurationFile();
/* 274 */     collapseTree();
/* 275 */     selectAllNodes();
/*     */   }
/*     */ 
/*     */   public static String treePathToString(TreePath path)
/*     */   {
/* 280 */     StringBuffer sb = new StringBuffer();
/* 281 */     CategoryNode n = null;
/* 282 */     Object[] objects = path.getPath();
/* 283 */     for (int i = 1; i < objects.length; i++) {
/* 284 */       n = (CategoryNode)objects[i];
/* 285 */       if (i > 1) {
/* 286 */         sb.append(".");
/*     */       }
/* 288 */       sb.append(n.getTitle());
/*     */     }
/* 290 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   protected void load()
/*     */   {
/* 297 */     File file = new File(getFilename());
/* 298 */     if (file.exists()) {
/*     */       try {
/* 300 */         DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
/*     */ 
/* 302 */         DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
/* 303 */         Document doc = docBuilder.parse(file);
/* 304 */         doc.normalize();
/* 305 */         processCategories(doc);
/* 306 */         processMsgTypes(doc);
/* 307 */         processLogLevelColors(doc);
/* 308 */         processLogTableColumns(doc);
/* 309 */         processFont(doc);
/* 310 */         processDateFormat(doc);
/* 311 */         processSplitPanes(doc);
/* 312 */         processWindowPosition(doc);
/*     */ 
/* 314 */         processListeners(doc);
/* 315 */         processRenderer(doc);
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/* 321 */         System.err.println("Unable process configuration file at " + getFilename() + ". Error Message=" + e.getMessage());
/*     */       }
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/* 327 */       this._table.setDateFormatManager(new DateFormatManager("HH:mm:ss.S"));
/*     */     }
/*     */   }
/*     */ 
/*     */   private void processRenderer(Document doc)
/*     */   {
/* 336 */     NodeList nodeList = doc.getElementsByTagName("RENDERER");
/*     */ 
/* 338 */     for (int i = 0; i < nodeList.getLength(); i++) {
/* 339 */       Node n = nodeList.item(i);
/*     */ 
/* 342 */       if (n == null) {
/* 343 */         return;
/*     */       }
/*     */ 
/* 346 */       String value = null;
/* 347 */       NodeList children = n.getChildNodes();
/* 348 */       for (int ii = 0; ii < children.getLength(); ii++) {
/* 349 */         Node ci = children.item(ii);
/* 350 */         if (ci.getNodeType() == 3) {
/* 351 */           value = ci.getNodeValue();
/*     */         }
/*     */       }
/*     */ 
/* 355 */       this._gui.setLastUsedRenderer(value);
/*     */     }
/*     */   }
/*     */ 
/*     */   private String getType(Node n)
/*     */   {
/* 365 */     int type = n.getNodeType();
/* 366 */     switch (type) { case 1:
/* 367 */       return "Element";
/*     */     case 2:
/* 368 */       return "Attribute";
/*     */     case 3:
/* 369 */       return "Text";
/*     */     case 4:
/* 370 */       return "CDATA Section";
/*     */     case 5:
/* 371 */       return "Entity Reference";
/*     */     case 6:
/* 372 */       return "Entity";
/*     */     case 7:
/* 373 */       return "Processing Instruction";
/*     */     case 8:
/* 374 */       return "Comment";
/*     */     case 9:
/* 375 */       return "Document";
/*     */     case 10:
/* 376 */       return "Document Type Declaration";
/*     */     case 11:
/* 377 */       return "Document Fragment";
/*     */     case 12:
/* 378 */       return "Notation"; }
/* 379 */     return "Unknown Type";
/*     */   }
/*     */ 
/*     */   private void processListeners(Document doc)
/*     */   {
/*     */     try
/*     */     {
/* 392 */       NodeList tiblisternodes = doc.getElementsByTagName("SUBSCRIPTIONS");
/*     */ 
/* 394 */       if (tiblisternodes == null) {
/* 395 */         return;
/*     */       }
/*     */ 
/* 399 */       NodeList ln = tiblisternodes.item(0).getChildNodes();
/* 400 */       Set setRvParameters = new HashSet();
/* 401 */       int len = ln.getLength();
/* 402 */       for (int i = 0; i < len; i++) {
/* 403 */         EMSParameters p = new EMSParameters();
/* 404 */         Node listener = ln.item(i);
/* 405 */         if (listener.getNodeType() != 3)
/*     */         {
/* 410 */           if (listener.hasAttributes())
/*     */           {
/* 416 */             NamedNodeMap nnm = listener.getAttributes();
/*     */ 
/* 418 */             Node server = nnm.getNamedItem("SERVERURL");
/* 419 */             Node user = nnm.getNamedItem("USER");
/* 420 */             Node password = nnm.getNamedItem("PASSWORD");
/*     */ 
/* 422 */             p.setServerURL(server.getNodeValue());
/* 423 */             p.setUserName(user.getNodeValue());
/* 424 */             p.setPassword(password.getNodeValue());
/*     */           }
/*     */ 
/* 429 */           NodeList subs = listener.getChildNodes();
/* 430 */           int leni = subs.getLength();
/* 431 */           Set setRvListeners = new HashSet();
/* 432 */           for (int iSubscription = 0; iSubscription < leni; iSubscription++) {
/* 433 */             Node subscription = subs.item(iSubscription);
/*     */ 
/* 435 */             if (subscription.getNodeType() != 3)
/*     */             {
/* 440 */               if (subscription.hasAttributes())
/*     */               {
/* 443 */                 NamedNodeMap nnm = subscription.getAttributes();
/* 444 */                 Node id = nnm.getNamedItem("ID");
/* 445 */                 String Ttopic = id.getNodeValue();
/* 446 */                 Ttopic = Ttopic.replaceAll("&gt;", ">");
/*     */ 
/* 448 */                 setRvListeners.add(id.getNodeValue());
/*     */               }
/* 450 */               p.setTopics(setRvListeners);
/*     */             }
/*     */           }
/* 453 */           setRvParameters.add(p);
/*     */         }
/*     */       }
/*     */ 
/* 457 */       this._gui.startListeners(setRvParameters);
/*     */     } catch (DOMException e) {
/* 459 */       System.err.println(e.getLocalizedMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   private void processWindowPosition(Document doc)
/*     */   {
/*     */     try
/*     */     {
/* 468 */       NodeList nodes = doc.getElementsByTagName("windowpos");
/*     */ 
/* 475 */       Node n = nodes.item(0);
/* 476 */       NamedNodeMap map = n.getAttributes();
/* 477 */       String windowHeight = getValue(map, "windowheight");
/* 478 */       String windowWidth = getValue(map, "windowwidth");
/* 479 */       String windowX = getValue(map, "windowsx");
/* 480 */       String windowY = getValue(map, "windowy");
/*     */ 
/* 482 */       if ((windowHeight != null) && (windowWidth != null) && (windowX != null) && (windowY != null))
/*     */       {
/* 484 */         Rectangle r = new Rectangle(Integer.parseInt(windowX), Integer.parseInt(windowY), Integer.parseInt(windowWidth), Integer.parseInt(windowHeight));
/*     */ 
/* 488 */         this._gui.setWindowBounds(r);
/*     */       }
/*     */       else
/*     */       {
/* 492 */         throw new Exception("");
/*     */       }
/*     */     }
/*     */     catch (Exception e1)
/*     */     {
/* 497 */       e1.printStackTrace();
/*     */ 
/* 499 */       this._gui.updateFrameSize();
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void processSplitPanes(Document doc)
/*     */   {
/*     */     try
/*     */     {
/* 507 */       NodeList nodes = doc.getElementsByTagName("splitpanepos");
/*     */ 
/* 512 */       Node n = nodes.item(0);
/* 513 */       NamedNodeMap map = n.getAttributes();
/* 514 */       String sizeHorizontal = getValue(map, "horizontal");
/* 515 */       String sizeVertical = getValue(map, "vertical");
/*     */ 
/* 517 */       if ((sizeHorizontal != null) && (sizeHorizontal != null)) {
/* 518 */         this._gui.setSplitPaneTableViewerPos(Integer.parseInt(sizeHorizontal));
/* 519 */         this._gui.setSplitPaneVerticalPos(Integer.parseInt(sizeVertical));
/*     */       }
/*     */       else {
/* 522 */         throw new Exception("");
/*     */       }
/*     */     }
/*     */     catch (Exception e1)
/*     */     {
/* 527 */       this._gui.setSplitPaneVerticalPos(130);
/* 528 */       this._gui.setSplitPaneTableViewerPos(350);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void processCategories(Document doc)
/*     */   {
/* 535 */     CategoryExplorerTree tree = this._gui.getCategoryExplorerTree();
/* 536 */     CategoryExplorerModel model = tree.getExplorerModel();
/* 537 */     NodeList nodeList = doc.getElementsByTagName("subject");
/*     */ 
/* 540 */     NamedNodeMap map = nodeList.item(0).getAttributes();
/* 541 */     int j = getValue(map, "name").equalsIgnoreCase("Topics") ? 1 : 0;
/*     */ 
/* 544 */     for (int i = nodeList.getLength() - 1; i >= j; i--) {
/* 545 */       Node n = nodeList.item(i);
/* 546 */       map = n.getAttributes();
/* 547 */       CategoryNode chnode = model.addCategory(new CategoryPath(getValue(map, "path")));
/* 548 */       chnode.setSelected(getValue(map, "selected").equalsIgnoreCase("true"));
/* 549 */       if (getValue(map, "expanded").equalsIgnoreCase("true"));
/* 550 */       tree.expandPath(model.getTreePathToRoot(chnode));
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void processMsgTypes(Document doc)
/*     */   {
/* 556 */     NodeList nodeList = doc.getElementsByTagName("type");
/* 557 */     Map menuItems = this._gui.getLogLevelMenuItems();
/*     */ 
/* 559 */     for (int i = 0; i < nodeList.getLength(); i++) {
/* 560 */       Node n = nodeList.item(i);
/* 561 */       NamedNodeMap map = n.getAttributes();
/* 562 */       String name = getValue(map, "name");
/*     */       try {
/* 564 */         JCheckBoxMenuItem item = (JCheckBoxMenuItem)menuItems.get(EventActionType.valueOf(name));
/*     */ 
/* 566 */         item.setSelected(getValue(map, "selected").equalsIgnoreCase("true"));
/*     */       }
/*     */       catch (MsgTypeFormatException e) {
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void processLogLevelColors(Document doc) {
/* 574 */     NodeList nodeList = doc.getElementsByTagName("colorlevel");
/* 575 */     Map logLevelColors = EventActionType.getLogLevelColorMap();
/*     */ 
/* 577 */     for (int i = 0; i < nodeList.getLength(); i++) {
/* 578 */       Node n = nodeList.item(i);
/*     */ 
/* 580 */       if (n == null) {
/* 581 */         return;
/*     */       }
/*     */ 
/* 584 */       NamedNodeMap map = n.getAttributes();
/* 585 */       String name = getValue(map, "name");
/*     */       try {
/* 587 */         EventActionType level = EventActionType.valueOf(name);
/* 588 */         int red = Integer.parseInt(getValue(map, "red"));
/* 589 */         int green = Integer.parseInt(getValue(map, "green"));
/* 590 */         int blue = Integer.parseInt(getValue(map, "blue"));
/* 591 */         Color c = new Color(red, green, blue);
/* 592 */         if (level != null)
/* 593 */           level.setLogLevelColorMap(level, c);
/*     */       }
/*     */       catch (MsgTypeFormatException e)
/*     */       {
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void processFont(Document doc)
/*     */   {
/*     */     try
/*     */     {
/* 606 */       NodeList nodes = doc.getElementsByTagName("fontinfo");
/*     */ 
/* 611 */       String fontSize = null;
/* 612 */       String fontName = null;
/*     */ 
/* 614 */       Node n = nodes.item(0);
/* 615 */       NamedNodeMap map = n.getAttributes();
/* 616 */       fontName = getValue(map, "name");
/* 617 */       fontSize = getValue(map, "size");
/* 618 */       String fontStyle = getValue(map, "style");
/*     */ 
/* 620 */       if ((fontName != null) && (fontSize != null)) {
/* 621 */         this._gui.setFontSize(Integer.parseInt(fontStyle));
/* 622 */         this._gui.setFontName(fontName);
/*     */       }
/*     */     }
/*     */     catch (Exception e1)
/*     */     {
/* 627 */       this._gui.setFontSize(12);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void processFont(Font f, StringBuffer xml) {
/* 632 */     exportFont(f.getFontName(), f.getStyle(), f.getSize(), xml);
/*     */   }
/*     */ 
/*     */   private void exportFont(String fontName, int style, int size, StringBuffer xml)
/*     */   {
/* 637 */     xml.append("\t<").append("fontinfo").append(" ");
/* 638 */     xml.append("name").append("=\"").append(fontName).append("\"").append(" ");
/* 639 */     xml.append("style").append("=\"").append(style).append("\"").append(" ");
/* 640 */     xml.append("size").append("=\"").append(size).append("\"").append(" ");
/* 641 */     xml.append("/>\n\r");
/*     */   }
/*     */ 
/*     */   private void appendStartTag(StringBuffer s, String tag) {
/* 645 */     s.append("\t<");
/* 646 */     s.append(tag);
/* 647 */     s.append(">\r\n");
/*     */   }
/*     */ 
/*     */   private void appendCloseTag(StringBuffer s, String tag) {
/* 651 */     s.append("\n</");
/* 652 */     s.append(tag);
/* 653 */     s.append(">\r\n");
/*     */   }
/*     */ 
/*     */   protected void processLogTableColumns(Document doc) {
/* 657 */     NodeList nodeList = doc.getElementsByTagName("column");
/* 658 */     Map menuItems = this._gui.getLogTableColumnMenuItems();
/* 659 */     List selectedColumns = new ArrayList();
/* 660 */     for (int i = 0; i < nodeList.getLength(); i++) {
/* 661 */       Node n = nodeList.item(i);
/*     */ 
/* 663 */       if (n == null) {
/* 664 */         return;
/*     */       }
/* 666 */       NamedNodeMap map = n.getAttributes();
/* 667 */       String name = getValue(map, "name");
/*     */       try
/*     */       {
/* 670 */         LogTableColumn column = LogTableColumn.valueOf(name);
/* 671 */         JCheckBoxMenuItem item = (JCheckBoxMenuItem)menuItems.get(column);
/*     */ 
/* 673 */         item.setSelected(getValue(map, "selected").equalsIgnoreCase("true"));
/*     */ 
/* 675 */         if (item.isSelected()) {
/* 676 */           selectedColumns.add(column);
/*     */         }
/*     */       }
/*     */       catch (LogTableColumnFormatException e)
/*     */       {
/*     */       }
/*     */     }
/* 683 */     if (selectedColumns.isEmpty())
/* 684 */       this._table.setDetailedView();
/*     */     else {
/* 686 */       this._table.setView(selectedColumns);
/*     */     }
/*     */ 
/* 689 */     for (int i = 0; i < nodeList.getLength(); i++) {
/* 690 */       Node n = nodeList.item(i);
/*     */ 
/* 692 */       if (n == null) {
/* 693 */         return;
/*     */       }
/* 695 */       NamedNodeMap map = n.getAttributes();
/*     */       try
/*     */       {
/* 699 */         String name = getValue(map, "name");
/* 700 */         String width = getValue(map, "columnsize");
/*     */ 
/* 702 */         this._table.setColumnWidth(name, Integer.parseInt(width));
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected String getValue(NamedNodeMap map, String attr)
/*     */   {
/* 712 */     Node n = map.getNamedItem(attr);
/* 713 */     return n.getNodeValue();
/*     */   }
/*     */ 
/*     */   protected void collapseTree()
/*     */   {
/* 718 */     CategoryExplorerTree tree = this._gui.getCategoryExplorerTree();
/* 719 */     for (int i = tree.getRowCount() - 1; i > 0; i--)
/* 720 */       tree.collapseRow(i);
/*     */   }
/*     */ 
/*     */   protected void selectAllNodes()
/*     */   {
/* 725 */     CategoryExplorerModel model = this._gui.getCategoryExplorerTree().getExplorerModel();
/* 726 */     CategoryNode root = model.getRootCategoryNode();
/* 727 */     Enumeration all = root.breadthFirstEnumeration();
/* 728 */     CategoryNode n = null;
/* 729 */     while (all.hasMoreElements()) {
/* 730 */       n = (CategoryNode)all.nextElement();
/* 731 */       n.setSelected(true);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void createConfigurationDirectory() {
/* 736 */     String home = System.getProperty("user.home");
/* 737 */     File f = new File(home + SEP + ".emssnoop");
/* 738 */     if (!f.exists())
/*     */       try {
/* 740 */         f.mkdirs();
/*     */       } catch (SecurityException e) {
/* 742 */         throw e;
/*     */       }
/*     */   }
/*     */ 
/*     */   protected void store(String s)
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/* 751 */       createConfigurationDirectory();
/*     */ 
/* 753 */       File f = new File(getFilename());
/*     */ 
/* 755 */       if (!f.exists()) {
/*     */         try {
/* 757 */           f.createNewFile();
/*     */         } catch (Exception e) {
/*     */         }
/*     */         finally {
/* 761 */           f = null;
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 766 */       PrintWriter writer = new PrintWriter(new FileWriter(getFilename()));
/*     */ 
/* 768 */       writer.print(s);
/* 769 */       writer.close();
/*     */     }
/*     */     catch (IOException e) {
/* 772 */       throw e;
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void deleteConfigurationFile()
/*     */   {
/*     */     try {
/* 779 */       File f = new File(getFilename());
/* 780 */       if (f.exists())
/* 781 */         f.delete();
/*     */     }
/*     */     catch (SecurityException e) {
/* 784 */       System.err.println("Cannot delete " + getFilename() + " because a security violation occured.");
/*     */     }
/*     */   }
/*     */ 
/*     */   protected String getDefaultFilename()
/*     */   {
/* 790 */     String home = System.getProperty("user.home");
/* 791 */     return home + SEP + ".emssnoop" + SEP + "conf.xml";
/*     */   }
/*     */ 
/*     */   public String getFilename() {
/* 795 */     return this._fileName;
/*     */   }
/*     */ 
/*     */   public void setFilename(String fileName) {
/* 799 */     this._fileName = fileName;
/*     */   }
/*     */ 
/*     */   private void processConfigurationNode(CategoryNode node, StringBuffer xml)
/*     */   {
/* 806 */     CategoryExplorerModel model = this._gui.getCategoryExplorerTree().getExplorerModel();
/*     */ 
/* 808 */     Enumeration all = node.breadthFirstEnumeration();
/* 809 */     CategoryNode n = null;
/* 810 */     while (all.hasMoreElements()) {
/* 811 */       n = (CategoryNode)all.nextElement();
/* 812 */       exportXMLElement(n, model.getTreePathToRoot(n), xml);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void processMsgTypes(Map logLevelMenuItems, StringBuffer xml)
/*     */   {
/* 819 */     xml.append("\t<msgtypes>\r\n");
/* 820 */     Iterator it = logLevelMenuItems.keySet().iterator();
/* 821 */     while (it.hasNext()) {
/* 822 */       EventActionType level = (EventActionType)it.next();
/* 823 */       JCheckBoxMenuItem item = (JCheckBoxMenuItem)logLevelMenuItems.get(level);
/* 824 */       exportLogLevelXMLElement(level.getLabel(), item.isSelected(), xml);
/*     */     }
/*     */ 
/* 827 */     xml.append("\t</msgtypes>\r\n");
/*     */   }
/*     */ 
/*     */   private void processMsgTypesColors(Map logLevelMenuItems, Map logLevelColors, StringBuffer xml) {
/* 831 */     xml.append("\t<msgtypescolors>\r\n");
/*     */ 
/* 833 */     Iterator it = logLevelMenuItems.keySet().iterator();
/* 834 */     while (it.hasNext()) {
/* 835 */       EventActionType level = (EventActionType)it.next();
/*     */ 
/* 837 */       Color color = (Color)logLevelColors.get(level);
/* 838 */       exportLogLevelColorXMLElement(level.getLabel(), color, xml);
/*     */     }
/*     */ 
/* 841 */     xml.append("\t</msgtypescolors>\r\n");
/*     */   }
/*     */ 
/*     */   private void processLogTableColumns(List logTableColumnMenuItems, StringBuffer xml)
/*     */   {
/* 846 */     xml.append("\t<logtablecolumns>\r\n");
/* 847 */     Iterator it = logTableColumnMenuItems.iterator();
/* 848 */     while (it.hasNext()) {
/* 849 */       LogTableColumn column = (LogTableColumn)it.next();
/* 850 */       JCheckBoxMenuItem item = this._gui.getTableColumnMenuItem(column);
/* 851 */       int size = this._table.getColumnWidth(column.getLabel());
/* 852 */       exportLogTableColumnXMLElement(column.getLabel(), item.isSelected(), size, xml);
/*     */     }
/*     */ 
/* 855 */     xml.append("\t</logtablecolumns>\r\n");
/*     */   }
/*     */ 
/*     */   private void openXMLDocument(StringBuffer xml)
/*     */   {
/* 860 */     xml.append("<?xml version=\"1.0\" encoding=\"" + System.getProperty("file.encoding") + "\" ?>\r\n");
/*     */   }
/*     */ 
/*     */   private void openConfigurationXML(StringBuffer xml) {
/* 864 */     xml.append("<configuration>\r\n");
/*     */   }
/*     */ 
/*     */   private void closeConfigurationXML(StringBuffer xml) {
/* 868 */     xml.append("</configuration>\r\n");
/*     */   }
/*     */ 
/*     */   private void exportXMLElement(CategoryNode node, TreePath path, StringBuffer xml) {
/* 872 */     CategoryExplorerTree tree = this._gui.getCategoryExplorerTree();
/*     */ 
/* 874 */     xml.append("\t<").append("subject").append(" ");
/* 875 */     xml.append("name").append("=\"").append(node.getTitle()).append("\" ");
/* 876 */     xml.append("path").append("=\"").append(treePathToString(path)).append("\" ");
/* 877 */     xml.append("expanded").append("=\"").append(tree.isExpanded(path)).append("\" ");
/* 878 */     xml.append("selected").append("=\"").append(node.isSelected()).append("\"/>\r\n");
/*     */   }
/*     */ 
/*     */   private void exportLogLevelXMLElement(String label, boolean selected, StringBuffer xml) {
/* 882 */     xml.append("\t\t<").append("type").append(" ").append("name");
/* 883 */     xml.append("=\"").append(label).append("\" ");
/* 884 */     xml.append("selected").append("=\"").append(selected);
/* 885 */     xml.append("\"/>\r\n");
/*     */   }
/*     */ 
/*     */   private void exportLogLevelColorXMLElement(String label, Color color, StringBuffer xml) {
/* 889 */     xml.append("\t\t<").append("colorlevel").append(" ").append("name");
/* 890 */     xml.append("=\"").append(label).append("\" ");
/* 891 */     xml.append("red").append("=\"").append(color.getRed()).append("\" ");
/* 892 */     xml.append("green").append("=\"").append(color.getGreen()).append("\" ");
/* 893 */     xml.append("blue").append("=\"").append(color.getBlue());
/* 894 */     xml.append("\"/>\r\n");
/*     */   }
/*     */ 
/*     */   private void exportLogTableColumnXMLElement(String label, boolean selected, int size, StringBuffer xml) {
/* 898 */     xml.append("\t\t<").append("column").append(" ").append("name");
/* 899 */     xml.append("=\"").append(label).append("\" ");
/* 900 */     xml.append("selected").append("=\"").append(selected).append("\" ");
/* 901 */     xml.append("columnsize").append("=\"").append(String.valueOf(size));
/* 902 */     xml.append("\"/>\r\n");
/*     */   }
/*     */ }

/* Location:           /Users/tnarik/Desktop/emssn00p.jar
 * Qualified Name:     emssn00p.viewer.ConfigurationManager
 * JD-Core Version:    0.6.1
 */