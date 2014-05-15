/*      */ package emssn00p.viewer;
/*      */ 
/*      */ import emssn00p.EventActionType;
/*      */ import emssn00p.LogRecord;
/*      */ import emssn00p.LogRecordFilter;
/*      */ import emssn00p.util.BrowserLauncher;
/*      */ import emssn00p.util.DateFormatManager;
/*      */ import emssn00p.util.ems.EMSController;
/*      */ import emssn00p.util.ems.EMSParameters;
/*      */ import emssn00p.util.ems.IMarshalJMSToString;
/*      */ import emssn00p.util.ems.LogRecordFactory;
/*      */ import emssn00p.util.ems.MarshalJMSMsgToStringProxyImpl;
/*      */ import emssn00p.viewer.categoryexplorer.CategoryExplorerModel;
/*      */ import emssn00p.viewer.categoryexplorer.CategoryExplorerTree;
/*      */ import emssn00p.viewer.categoryexplorer.CategoryPath;
/*      */ import emssn00p.viewer.configure.MRUListnerManager;
/*      */ import java.awt.Color;
/*      */ import java.awt.Component;
/*      */ import java.awt.Container;
/*      */ import java.awt.Dimension;
/*      */ import java.awt.FileDialog;
/*      */ import java.awt.FlowLayout;
/*      */ import java.awt.Font;
/*      */ import java.awt.GraphicsEnvironment;
/*      */ import java.awt.Rectangle;
/*      */ import java.awt.Toolkit;
/*      */ import java.awt.event.ActionEvent;
/*      */ import java.awt.event.ActionListener;
/*      */ import java.awt.event.KeyAdapter;
/*      */ import java.awt.event.KeyEvent;
/*      */ import java.awt.event.WindowAdapter;
/*      */ import java.awt.event.WindowEvent;
/*      */ import java.io.File;
/*      */ import java.io.InputStream;
/*      */ import java.io.PrintStream;
/*      */ import java.net.URL;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Date;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import java.util.StringTokenizer;
/*      */ import java.util.Vector;
/*      */ import javax.jms.JMSException;
/*      */ import javax.jms.Message;
/*      */ import javax.jms.MessageListener;
/*      */ import javax.swing.BorderFactory;
/*      */ import javax.swing.ImageIcon;
/*      */ import javax.swing.JButton;
/*      */ import javax.swing.JCheckBoxMenuItem;
/*      */ import javax.swing.JColorChooser;
/*      */ import javax.swing.JComboBox;
/*      */ import javax.swing.JFrame;
/*      */ import javax.swing.JLabel;
/*      */ import javax.swing.JMenu;
/*      */ import javax.swing.JMenuBar;
/*      */ import javax.swing.JMenuItem;
/*      */ import javax.swing.JOptionPane;
/*      */ import javax.swing.JPanel;
/*      */ import javax.swing.JRootPane;
/*      */ import javax.swing.JScrollBar;
/*      */ import javax.swing.JScrollPane;
/*      */ import javax.swing.JSplitPane;
/*      */ import javax.swing.JTextArea;
/*      */ import javax.swing.JToolBar;
/*      */ import javax.swing.KeyStroke;
/*      */ import javax.swing.ListSelectionModel;
/*      */ import javax.swing.SwingUtilities;
/*      */ import javax.swing.table.TableModel;
/*      */ 
/*      */ public class RvSnooperGUI
/*      */   implements MessageListener
/*      */ {
/*      */   public static final String DETAILED_VIEW = "Detailed";
/*      */   public static final String VERSION = "EMSSn00p v2.0.3";
/*      */   public static final String URL = "http://emsn00p.sf.net";
/*  102 */   public static final MarshalJMSMsgToStringProxyImpl _marshalImpl = new MarshalJMSMsgToStringProxyImpl();
/*      */ 
/*  107 */   protected String _name = null;
/*      */   protected JFrame _logMonitorFrame;
/*  109 */   protected int _logMonitorFrameWidth = 550;
/*  110 */   protected int _logMonitorFrameHeight = 500;
/*      */   protected LogTable _table;
/*      */   protected CategoryExplorerTree _subjectExplorerTree;
/*      */   protected String _searchText;
/*  114 */   protected EventActionType _leastSevereDisplayedMsgType = EventActionType.UNKNOWN;
/*      */   protected JScrollPane _logTableScrollPane;
/*      */   protected JLabel _statusLabel;
/*      */   protected JComboBox _fontSizeCombo;
/*      */   protected JComboBox _fontNameCombo;
/*  120 */   protected JButton _pauseButton = null;
/*      */ 
/*  122 */   private int _fontSize = 10;
/*  123 */   private String _fontName = "Dialog";
/*  124 */   protected String _currentView = "Detailed";
/*      */ 
/*  126 */   protected boolean _loadSystemFonts = false;
/*  127 */   protected boolean _trackTableScrollPane = true;
/*  128 */   protected boolean _callSystemExitOnClose = true;
/*  129 */   protected List<Object> _displayedLogBrokerProperties = new Vector();
/*      */ 
/*  131 */   protected Map<EventActionType, JCheckBoxMenuItem> _logLevelMenuItems = new HashMap();
/*  132 */   protected Map<LogTableColumn, JCheckBoxMenuItem> _logTableColumnMenuItems = new HashMap();
/*      */   protected JSplitPane _splitPaneVertical;
/*      */   protected JSplitPane _splitPaneTableViewer;
/*  137 */   protected List _levels = null;
/*  138 */   protected List _columns = null;
/*  139 */   protected boolean _isDisposed = false;
/*      */ 
/*  141 */   protected ConfigurationManager _configurationManager = null;
/*  142 */   protected MRUListnerManager _mruListnerManager = null;
/*      */ 
/*  144 */   protected boolean _displaySystemMsgs = true;
/*  145 */   protected boolean _displayIMMsgs = true;
/*  146 */   protected EMSParameters _lastUsedRvParameters = new EMSParameters();
/*  147 */   protected boolean _isPaused = false;
/*  148 */   protected ClassLoader _cl = null;
/*      */ 
/*  150 */   protected static String _JMSCorrelationIDTextFilter = "";
/*  151 */   protected static String _subjectTextFilter = "";
/*  152 */   protected static boolean useMtrackingInfo = true;
/*      */   private String _ConnHostnameTextFilter;
/*      */   private JComboBox _rendererCombo;
/*      */   protected String _lastUsedRenderer;
/*      */ 
/*      */   public RvSnooperGUI(List MsgTypes, Set listeners, String name)
/*      */   {
/*  166 */     this._levels = MsgTypes;
/*  167 */     this._columns = LogTableColumn.getLogTableColumns();
/*  168 */     this._columns = LogTableColumn.getLogTableColumns();
/*  169 */     this._name = name;
/*      */ 
/*  171 */     initComponents();
/*      */ 
/*  173 */     this._logMonitorFrame.addWindowListener(new LogBrokerMonitorWindowAdaptor(this));
/*      */ 
/*  176 */     startListeners(listeners);
/*      */   }
/*      */ 
/*      */   protected void startListeners(Set listeners)
/*      */   {
/*  187 */     Iterator itrl = listeners.iterator();
/*      */     RvSnooperErrorDialog error;
/*  189 */     while (itrl.hasNext()) {
/*      */       try
/*      */       {
/*  192 */         EMSParameters p = (EMSParameters)itrl.next();
/*      */ 
/*  194 */         p.setDescription(" <a href=\"http://emsn00p.sf.net\">EMSSn00p v2.0.3</a> ");
/*      */ 
/*  197 */         EMSController.startListener(p, this);
/*      */ 
/*  199 */         this._lastUsedRvParameters = p;
/*      */       }
/*      */       catch (ClassCastException ex) {
/*  202 */         error = new RvSnooperErrorDialog(getBaseFrame(), ex.getMessage());
/*      */       }
/*      */       catch (JMSException ex)
/*      */       {
/*      */         RvSnooperErrorDialog error1;
/*  206 */         error1 = new RvSnooperErrorDialog(getBaseFrame(), "EMS Exception" + ex.getMessage());
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  212 */     updateBanner();
/*      */   }
/*      */ 
/*      */   public void onError(Object tibrvObject, int errorCode, String message, Throwable throwable)
/*      */   {
/*  232 */     RvSnooperErrorDialog error = new RvSnooperErrorDialog(getBaseFrame(), "A System error occured " + message);
/*      */   }
/*      */ 
/*      */   public void show(final int delay)
/*      */   {
/*  245 */     if (this._logMonitorFrame.isVisible()) {
/*  246 */       return;
/*      */     }
/*      */ 
/*  249 */     SwingUtilities.invokeLater(new Runnable() {
/*      */       public void run() {
/*  251 */         Thread.yield();
/*  252 */         RvSnooperGUI.pause(delay);
/*  253 */         RvSnooperGUI.this._logMonitorFrame.setVisible(true);
/*  254 */         RvSnooperGUI.changeStringCombo(RvSnooperGUI.this._fontNameCombo, RvSnooperGUI.this._fontName);
/*  255 */         RvSnooperGUI.changeIntCombo(RvSnooperGUI.this._fontSizeCombo, RvSnooperGUI.this._fontSize);
/*  256 */         RvSnooperGUI.changeStringCombo(RvSnooperGUI.this._rendererCombo, RvSnooperGUI.this.getLastUsedRenderer());
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void show() {
/*  262 */     show(0);
/*  263 */     updateBanner();
/*      */   }
/*      */ 
/*      */   public void updateBanner()
/*      */   {
	           String sBanner = null;
/*  270 */     if (this._name != null) {
/*  271 */       sBanner = this._name;
/*  272 */       sBanner = sBanner + " ";
/*  273 */       sBanner = sBanner + EMSController.getTransports().toString();
/*      */     } else {
/*  275 */       sBanner = EMSController.getTransports().toString();
/*      */     }
/*      */ 
/*  278 */     sBanner = sBanner + " ";
/*  279 */     sBanner = sBanner + "EMSSn00p v2.0.3";
/*      */ 
/*  281 */     setTitle(sBanner);
/*      */   }
/*      */ 
/*      */   public void dispose()
/*      */   {
/*  289 */     this._logMonitorFrame.dispose();
/*  290 */     this._isDisposed = true;
/*      */     try
/*      */     {
/*  294 */       EMSController.shutdownAll();
/*      */     }
/*      */     catch (Exception ex) {
/*  297 */       System.err.println(ex.getMessage());
/*      */     }
/*      */ 
/*  301 */     if (this._callSystemExitOnClose == true)
/*  302 */       System.exit(0);
/*      */   }
/*      */ 
/*      */   public void hide()
/*      */   {
/*  310 */     this._logMonitorFrame.setVisible(false);
/*      */   }
/*      */ 
/*      */   DateFormatManager getDateFormatManager()
/*      */   {
/*  317 */     return this._table.getDateFormatManager();
/*      */   }
/*      */ 
/*      */   void setDateFormatManager(DateFormatManager dfm)
/*      */   {
/*  324 */     this._table.setDateFormatManager(dfm);
/*      */   }
/*      */ 
/*      */   public String getDateFormat() {
/*  328 */     DateFormatManager dfm = getDateFormatManager();
/*  329 */     return dfm.getPattern();
/*      */   }
/*      */ 
/*      */   public String setDateFormat(String pattern) {
/*  333 */     DateFormatManager dfm = new DateFormatManager(pattern);
/*  334 */     setDateFormatManager(dfm);
/*  335 */     return pattern;
/*      */   }
/*      */ 
/*      */   public boolean getCallSystemExitOnClose()
/*      */   {
/*  343 */     return this._callSystemExitOnClose;
/*      */   }
/*      */ 
/*      */   public void setCallSystemExitOnClose(boolean callSystemExitOnClose)
/*      */   {
/*  351 */     this._callSystemExitOnClose = callSystemExitOnClose;
/*      */   }
/*      */ 
/*      */   public void addMessage(LogRecord lr)
/*      */   {
/*  360 */     if (this._isDisposed == true)
/*      */     {
/*  363 */       return;
/*      */     }
/*      */ 
/*  366 */     SwingUtilities.invokeLater(new AddLogRecordRunnable(lr));
/*      */   }
/*      */ 
/*      */   public void setMaxNumberOfLogRecords(int maxNumberOfLogRecords) {
/*  370 */     this._table.getFilteredLogTableModel().setMaxNumberOfLogRecords(maxNumberOfLogRecords);
/*      */   }
/*      */ 
/*      */   public JFrame getBaseFrame() {
/*  374 */     return this._logMonitorFrame;
/*      */   }
/*      */ 
/*      */   public void setTitle(String title) {
/*  378 */     this._logMonitorFrame.setTitle(title);
/*      */   }
/*      */ 
/*      */   public void setFrameSize(int width, int height) {
/*  382 */     Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
/*  383 */     if ((0 < width) && (width < screen.width)) {
/*  384 */       this._logMonitorFrameWidth = width;
/*      */     }
/*  386 */     if ((0 < height) && (height < screen.height)) {
/*  387 */       this._logMonitorFrameHeight = height;
/*      */     }
/*  389 */     updateFrameSize();
/*      */   }
/*      */ 
/*      */   void setFontSize(int fontSize) {
/*  393 */     changeIntCombo(this._fontSizeCombo, fontSize);
/*      */   }
/*      */ 
/*      */   public void addDisplayedProperty(Object messageLine)
/*      */   {
/*  399 */     this._displayedLogBrokerProperties.add(messageLine);
/*      */   }
/*      */ 
/*      */   public Map getLogLevelMenuItems() {
/*  403 */     return this._logLevelMenuItems;
/*      */   }
/*      */ 
/*      */   public Map getLogTableColumnMenuItems() {
/*  407 */     return this._logTableColumnMenuItems;
/*      */   }
/*      */ 
/*      */   public JCheckBoxMenuItem getTableColumnMenuItem(LogTableColumn column) {
/*  411 */     return getLogTableColumnMenuItem(column);
/*      */   }
/*      */ 
/*      */   public CategoryExplorerTree getCategoryExplorerTree() {
/*  415 */     return this._subjectExplorerTree;
/*      */   }
/*      */ 
/*      */   protected boolean isPaused()
/*      */   {
/*  424 */     return this._isPaused;
/*      */   }
/*      */ 
/*      */   protected void pauseListeners()
/*      */   {
/*      */     try {
/*  430 */       EMSController.pauseAll();
/*      */     } catch (JMSException e) {
/*  432 */       this._statusLabel.setText("Pause all listeners failed " + e.getMessage());
/*  433 */       return;
/*      */     }
/*  435 */     this._isPaused = true;
/*  436 */     this._pauseButton.setText("Continue all listeners");
/*  437 */     this._pauseButton.setToolTipText("Unpause listeners");
/*  438 */     this._statusLabel.setText("All listeners are now paused");
/*      */ 
/*  440 */     ImageIcon pbIcon = null;
/*  441 */     URL pbIconURL = this._cl.getResource("rvsn00p/viewer/images/restart.gif");
/*      */ 
/*  443 */     if (pbIconURL != null) {
/*  444 */       pbIcon = new ImageIcon(pbIconURL);
/*      */     }
/*      */ 
/*  447 */     if (pbIcon != null)
/*  448 */       this._pauseButton.setIcon(pbIcon);
/*      */   }
/*      */ 
/*      */   protected void unPauseListeners()
/*      */   {
/*      */     try
/*      */     {
/*  456 */       EMSController.resumeAll();
/*      */     } catch (JMSException e) {
/*  458 */       this._statusLabel.setText("Resume all listeners failed " + e.getMessage());
/*  459 */       return;
/*      */     }
/*  461 */     this._isPaused = false;
/*      */ 
/*  463 */     this._pauseButton.setText("Pause all listeners");
/*  464 */     this._pauseButton.setToolTipText("Put listeners on hold");
/*  465 */     this._statusLabel.setText("All listeners are now active");
/*      */ 
/*  467 */     ImageIcon pbIcon = null;
/*  468 */     URL pbIconURL = this._cl.getResource("rvsn00p/viewer/images/pauseon.gif");
/*      */ 
/*  470 */     if (pbIconURL != null) {
/*  471 */       pbIcon = new ImageIcon(pbIconURL);
/*      */     }
/*      */ 
/*  474 */     if (pbIcon != null)
/*  475 */       this._pauseButton.setIcon(pbIcon);
/*      */   }
/*      */ 
/*      */   protected void setSearchText(String text)
/*      */   {
/*  483 */     this._searchText = text;
/*      */   }
/*      */ 
/*      */   protected void findSearchText()
/*      */   {
/*  488 */     String text = this._searchText;
/*  489 */     if ((text == null) || (text.length() == 0)) {
/*  490 */       return;
/*      */     }
/*  492 */     int startRow = getFirstSelectedRow();
/*  493 */     int foundRow = findRecord(startRow, text, this._table.getFilteredLogTableModel().getFilteredRecords());
/*      */ 
/*  498 */     selectRow(foundRow);
/*      */   }
/*      */ 
/*      */   protected int getFirstSelectedRow() {
/*  502 */     return this._table.getSelectionModel().getMinSelectionIndex();
/*      */   }
/*      */ 
/*      */   protected void selectRow(int foundRow) {
/*  506 */     if (foundRow == -1) {
/*  507 */       String message = this._searchText + " not found.";
/*  508 */       JOptionPane.showMessageDialog(this._logMonitorFrame, message, "Text not found", 1);
/*      */ 
/*  514 */       return;
/*      */     }
/*  516 */     SwingUtils.selectRow(foundRow, this._table, this._logTableScrollPane);
/*      */   }
/*      */ 
/*      */   protected int findRecord(int startRow, String searchText, List records)
/*      */   {
/*  524 */     if (startRow < 0)
/*  525 */       startRow = 0;
/*      */     else {
/*  527 */       startRow++;
/*      */     }
/*  529 */     int len = records.size();
/*      */ 
/*  531 */     for (int i = startRow; i < len; i++) {
/*  532 */       if (matches((LogRecord)records.get(i), searchText)) {
/*  533 */         return i;
/*      */       }
/*      */     }
/*      */ 
/*  537 */     len = startRow;
/*  538 */     for (int i = 0; i < len; i++) {
/*  539 */       if (matches((LogRecord)records.get(i), searchText)) {
/*  540 */         return i;
/*      */       }
/*      */     }
/*      */ 
/*  544 */     return -1;
/*      */   }
/*      */ 
/*      */   protected static boolean matches(LogRecord record, String text)
/*      */   {
/*  551 */     String message = record.toString(_marshalImpl);
/*  552 */     if (((message == null) && (_JMSCorrelationIDTextFilter == null)) || (text == null)) {
/*  553 */       return false;
/*      */     }
/*      */ 
/*  556 */     if ((message.toLowerCase().indexOf(text.toLowerCase()) == -1) && (_JMSCorrelationIDTextFilter.indexOf(text.toLowerCase()) == -1))
/*      */     {
/*  558 */       return false;
/*      */     }
/*      */ 
/*  561 */     return true;
/*      */   }
/*      */ 
/*      */   protected static void refresh(JTextArea textArea)
/*      */   {
/*  570 */     String text = textArea.getText();
/*  571 */     textArea.setText("");
/*  572 */     textArea.setText(text);
/*      */   }
/*      */ 
/*      */   protected void refreshDetailTextArea() {
/*  576 */     refresh(this._table._detailTextArea);
/*      */   }
/*      */ 
/*      */   protected void clearDetailTextArea() {
/*  580 */     this._table._detailTextArea.setText("");
/*      */   }
/*      */ 
/*      */   public static int changeIntCombo(JComboBox box, int requestedSize)
/*      */   {
/*  589 */     int len = box.getItemCount();
/*      */ 
/*  592 */     Object selectedObject = box.getItemAt(0);
/*  593 */     int selectedValue = Integer.parseInt(String.valueOf(selectedObject));
/*  594 */     for (int i = 0; i < len; i++) {
/*  595 */       Object currentObject = box.getItemAt(i);
/*  596 */       int currentValue = Integer.parseInt(String.valueOf(currentObject));
/*  597 */       if ((selectedValue < currentValue) && (currentValue <= requestedSize)) {
/*  598 */         selectedValue = currentValue;
/*  599 */         selectedObject = currentObject;
/*      */       }
/*      */     }
/*  602 */     box.setSelectedItem(selectedObject);
/*  603 */     return selectedValue;
/*      */   }
/*      */ 
/*      */   public static String changeStringCombo(JComboBox box, String requestedName)
/*      */   {
/*  612 */     int len = box.getItemCount();
/*      */ 
/*  614 */     String currentValue = null;
/*      */ 
/*  616 */     Object selectedObject = box.getItemAt(0);
/*  617 */     for (int i = 0; i < len; i++) {
/*  618 */       Object currentObject = box.getItemAt(i);
/*  619 */       currentValue = String.valueOf(currentObject);
/*  620 */       if (currentValue.compareToIgnoreCase(requestedName) == 0) {
/*  621 */         selectedObject = currentObject;
/*      */       }
/*      */     }
/*  624 */     box.setSelectedItem(selectedObject);
/*  625 */     return currentValue;
/*      */   }
/*      */ 
/*      */   protected void setFontSizeSilently(int fontSize)
/*      */   {
/*  632 */     setFontSize(fontSize);
/*  633 */     setFontSize(this._table._detailTextArea, fontSize);
/*  634 */     selectRow(0);
/*  635 */     setFontSize(this._table, fontSize);
/*      */   }
/*      */ 
/*      */   protected static void setFontSize(Component component, int fontSize) {
/*  639 */     Font oldFont = component.getFont();
/*  640 */     Font newFont = new Font(oldFont.getFontName(), oldFont.getStyle(), fontSize);
/*      */ 
/*  642 */     component.setFont(newFont);
/*      */   }
/*      */ 
/*      */   protected void updateFrameSize() {
/*  646 */     this._logMonitorFrame.setSize(this._logMonitorFrameWidth, this._logMonitorFrameHeight);
/*  647 */     centerFrame(this._logMonitorFrame);
/*      */   }
/*      */ 
/*      */   protected static void pause(int millis) {
/*      */     try {
/*  652 */       Thread.sleep(millis);
/*      */     }
/*      */     catch (InterruptedException e)
/*      */     {
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void initComponents()
/*      */   {
/*  662 */     this._logMonitorFrame = new JFrame(this._name);
/*      */ 
/*  664 */     this._logMonitorFrame.setDefaultCloseOperation(0);
/*      */ 
/*  666 */     String resource = "/emssn00p/viewer/images/eye.gif";
/*      */ 
/*  668 */     URL iconURL = getClass().getResource("/emssn00p/viewer/images/eye.gif");
/*      */ 
/*  670 */     if (iconURL != null) {
/*  671 */       this._logMonitorFrame.setIconImage(new ImageIcon(iconURL).getImage());
/*      */     }
/*  673 */     updateFrameSize();
/*      */ 
/*  678 */     JTextArea detailTA = createDetailTextArea();
/*  679 */     JScrollPane detailTAScrollPane = new JScrollPane(detailTA);
/*  680 */     this._table = new LogTable(detailTA, _marshalImpl);
/*  681 */     setView(this._currentView, this._table);
/*  682 */     this._table.setFont(new Font(getFontName(), 0, getFontSize()));
/*  683 */     this._logTableScrollPane = new JScrollPane(this._table);
/*      */ 
/*  685 */     if (this._trackTableScrollPane) {
/*  686 */       this._logTableScrollPane.getVerticalScrollBar().addAdjustmentListener(new TrackingAdjustmentListener());
/*      */     }
/*      */ 
/*  695 */     JSplitPane tableViewerSplitPane = new JSplitPane();
/*  696 */     tableViewerSplitPane.setOneTouchExpandable(true);
/*  697 */     tableViewerSplitPane.setOrientation(0);
/*  698 */     tableViewerSplitPane.setLeftComponent(this._logTableScrollPane);
/*  699 */     tableViewerSplitPane.setRightComponent(detailTAScrollPane);
/*      */ 
/*  707 */     tableViewerSplitPane.setDividerLocation(350);
/*      */ 
/*  710 */     setSplitPaneTableViewer(tableViewerSplitPane);
/*      */ 
/*  716 */     this._subjectExplorerTree = new CategoryExplorerTree();
/*      */ 
/*  718 */     this._table.getFilteredLogTableModel().setLogRecordFilter(createLogRecordFilter());
/*      */ 
/*  720 */     JScrollPane categoryExplorerTreeScrollPane = new JScrollPane(this._subjectExplorerTree);
/*      */ 
/*  722 */     categoryExplorerTreeScrollPane.setPreferredSize(new Dimension(130, 400));
/*      */ 
/*  731 */     JSplitPane splitPane = new JSplitPane();
/*  732 */     splitPane.setOneTouchExpandable(true);
/*  733 */     splitPane.setRightComponent(tableViewerSplitPane);
/*  734 */     splitPane.setLeftComponent(categoryExplorerTreeScrollPane);
/*      */ 
/*  736 */     splitPane.setDividerLocation(130);
/*  737 */     setSplitPaneVertical(splitPane);
/*      */ 
/*  742 */     this._logMonitorFrame.getRootPane().setJMenuBar(createMenuBar());
/*  743 */     this._logMonitorFrame.getContentPane().add(splitPane, "Center");
/*  744 */     this._logMonitorFrame.getContentPane().add(createToolBar(), "North");
/*  745 */     this._logMonitorFrame.getContentPane().add(createStatusArea(), "South");
/*      */ 
/*  747 */     makeLogTableListenToCategoryExplorer();
/*  748 */     addTableModelProperties();
/*      */ 
/*  753 */     final RvSnooperGUI gui = this;
/*  754 */     SwingUtilities.invokeLater(new Runnable() {
/*      */       public void run() {
/*  756 */         RvSnooperGUI.this._configurationManager = new ConfigurationManager(gui, RvSnooperGUI.this._table);
/*  757 */         RvSnooperGUI.this.unPauseListeners();
/*  758 */         String ex = "";
/*      */         try {
/*  760 */           RvSnooperGUI.this.updateRenderClass(RvSnooperGUI.this.getLastUsedRenderer());
/*      */         } catch (Exception e) {
/*  762 */           e.printStackTrace();
/*  763 */           ex = " " + e.getMessage();
/*      */         }
/*      */ 
/*  766 */         RvSnooperGUI.this._statusLabel.setText("Started @ " + new Date() + ex);
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   protected LogRecordFilter createLogRecordFilter() {
/*  772 */     LogRecordFilter result = new LogRecordFilter() {
/*      */       public boolean passes(LogRecord record) {
/*  774 */         CategoryPath path = new CategoryPath(record.getJMSDestination());
/*  775 */         return (RvSnooperGUI.this.getMenuItem(record.getType()).isSelected()) && (RvSnooperGUI.this._subjectExplorerTree.getExplorerModel().isCategoryPathActive(path));
/*      */       }
/*      */     };
/*  780 */     return result;
/*      */   }
/*      */ 
/*      */   protected void updateStatusLabel()
/*      */   {
/*  786 */     StringBuffer sb = new StringBuffer(100);
/*  787 */     getRecordsDisplayedMessage(sb);
/*  788 */     getFileEncodingMessage(sb);
/*      */ 
/*  790 */     this._statusLabel.setText(sb.toString());
/*      */   }
/*      */ 
/*      */   protected void getRecordsDisplayedMessage(StringBuffer sb) {
/*  794 */     FilteredLogTableModel model = this._table.getFilteredLogTableModel();
/*  795 */     getStatusText(model.getRowCount(), model.getTotalRowCount(), sb);
/*      */   }
/*      */ 
/*      */   protected void getFileEncodingMessage(StringBuffer sb) {
/*  799 */     sb.append("  Encoding:");
/*  800 */     sb.append(System.getProperty("file.encoding"));
/*      */   }
/*      */ 
/*      */   protected void addTableModelProperties() {
/*  804 */     final FilteredLogTableModel model = this._table.getFilteredLogTableModel();
/*      */ 
/*  806 */     addDisplayedProperty(new Object() {
/*      */       public String toString() {
/*  808 */         StringBuffer sb = new StringBuffer(40);
/*  809 */         RvSnooperGUI.this.getRecordsDisplayedMessage(sb);
/*  810 */         return sb.toString();
/*      */       }
/*      */     });
/*  813 */     addDisplayedProperty(new Object() {
/*      */       public String toString() {
/*  815 */         return "Maximum number of displayed LogRecords: " + model._maxNumberOfLogRecords;
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   protected static void getStatusText(int displayedRows, int totalRows, StringBuffer sb)
/*      */   {
/*  822 */     sb.append("Displaying: ");
/*  823 */     sb.append(displayedRows);
/*  824 */     sb.append(" records out of a total of: ");
/*  825 */     sb.append(totalRows);
/*  826 */     sb.append(" records. ");
/*  827 */     sb.append(totalRows - displayedRows);
/*  828 */     sb.append(" are filtered.");
/*      */   }
/*      */ 
/*      */   protected void makeLogTableListenToCategoryExplorer()
/*      */   {
/*  833 */     ActionListener listener = new ActionListener() {
/*      */       public void actionPerformed(ActionEvent e) {
/*  835 */         RvSnooperGUI.this._table.getFilteredLogTableModel().refresh();
/*  836 */         RvSnooperGUI.this.updateStatusLabel();
/*      */       }
/*      */     };
/*  839 */     this._subjectExplorerTree.getExplorerModel().addActionListener(listener);
/*      */   }
/*      */ 
/*      */   protected JPanel createStatusArea() {
/*  843 */     JPanel statusArea = new JPanel();
/*  844 */     JLabel status = new JLabel("No records to display.");
/*      */ 
/*  846 */     this._statusLabel = status;
/*  847 */     status.setHorizontalAlignment(2);
/*      */ 
/*  849 */     statusArea.setBorder(BorderFactory.createEtchedBorder());
/*  850 */     statusArea.setLayout(new FlowLayout(0, 0, 0));
/*  851 */     statusArea.add(status);
/*      */ 
/*  853 */     return statusArea;
/*      */   }
/*      */ 
/*      */   protected static JTextArea createDetailTextArea() {
/*  857 */     JTextArea detailTA = new JTextArea();
/*  858 */     detailTA.setFont(new Font("Monospaced", 0, 14));
/*  859 */     detailTA.setTabSize(3);
/*  860 */     detailTA.setLineWrap(true);
/*  861 */     detailTA.setWrapStyleWord(false);
/*  862 */     return detailTA;
/*      */   }
/*      */ 
/*      */   protected JMenuBar createMenuBar() {
/*  866 */     JMenuBar menuBar = new JMenuBar();
/*  867 */     menuBar.add(createFileMenu());
/*  868 */     menuBar.add(createEditMenu());
/*      */ 
/*  872 */     menuBar.add(createMsgTypeMenu());
/*  873 */     menuBar.add(createViewMenu());
/*  874 */     menuBar.add(createConfigureMenu());
/*  875 */     menuBar.add(createHelpMenu());
/*      */ 
/*  877 */     return menuBar;
/*      */   }
/*      */ 
/*      */   protected JMenuItem createSaveSelectedAsTextFileMenuItem()
/*      */   {
/*  882 */     JMenuItem result = new JMenuItem("Save selected as txt");
/*  883 */     result.setMnemonic('r');
/*      */ 
/*  885 */     result.addActionListener(new ActionListener()
/*      */     {
/*      */       public void actionPerformed(ActionEvent e) {
/*  888 */         ListSelectionModel lsm = RvSnooperGUI.this._table.getSelectionModel();
/*  889 */         if (lsm.isSelectionEmpty()) {
/*  890 */           RvSnooperGUI.this._statusLabel.setText("No rows are selected.");
/*      */         } else {
/*  892 */           int selectedRow = lsm.getMinSelectionIndex();
/*      */ 
/*  895 */           FilteredLogTableModel ftm = RvSnooperGUI.this._table.getFilteredLogTableModel();
/*      */ 
/*  897 */           final String sMsg = (String)RvSnooperGUI.this._table.getModel().getValueAt(selectedRow, RvSnooperGUI.this._table.getMsgColumnID());
/*  898 */           final String sSubject = (String)RvSnooperGUI.this._table.getModel().getValueAt(selectedRow, RvSnooperGUI.this._table.getSubjectColumnID());
/*      */ 
/*  901 */           SwingUtilities.invokeLater(new Runnable() {
/*      */             public void run() {
/*  903 */               RvSnooperFileHandler.saveMsgAsTextFile(sSubject, sMsg, "EMSSn00p v2.0.3 http://emsn00p.sf.net", RvSnooperGUI.this.getBaseFrame(), RvSnooperGUI.this._statusLabel);
/*      */             }
/*      */           });
/*      */         }
/*      */       }
/*      */     });
/*  912 */     result.setEnabled(false);
/*      */ 
/*  914 */     return result;
/*      */   }
/*      */ 
/*      */   protected JMenu createMsgTypeMenu()
/*      */   {
/*  919 */     JMenu result = new JMenu("Event Action");
/*  920 */     result.setMnemonic('a');
/*  921 */     Iterator imsgtypes = getMsgTypes();
/*  922 */     while (imsgtypes.hasNext()) {
/*  923 */       result.add(getMenuItem((EventActionType)imsgtypes.next()));
/*      */     }
/*      */ 
/*  926 */     result.addSeparator();
/*  927 */     result.add(createAllMsgTypesMenuItem());
/*  928 */     result.add(createNoMsgTypesMenuItem());
/*  929 */     result.addSeparator();
/*  930 */     result.add(createLogLevelColorMenu());
/*  931 */     result.add(createResetLogLevelColorMenuItem());
/*      */ 
/*  933 */     return result;
/*      */   }
/*      */ 
/*      */   protected JMenuItem createAllMsgTypesMenuItem() {
/*  937 */     JMenuItem result = new JMenuItem("Show all Event Actions");
/*  938 */     result.setMnemonic('a');
/*  939 */     result.addActionListener(new ActionListener() {
/*      */       public void actionPerformed(ActionEvent e) {
/*  941 */         RvSnooperGUI.this.selectAllMsgTypes(true);
/*  942 */         RvSnooperGUI.this._table.getFilteredLogTableModel().refresh();
/*  943 */         RvSnooperGUI.this.updateStatusLabel();
/*      */       }
/*      */     });
/*  946 */     return result;
/*      */   }
/*      */ 
/*      */   protected JMenuItem createNoMsgTypesMenuItem() {
/*  950 */     JMenuItem result = new JMenuItem("Hide all Event Actions");
/*  951 */     result.setMnemonic('h');
/*  952 */     result.addActionListener(new ActionListener() {
/*      */       public void actionPerformed(ActionEvent e) {
/*  954 */         RvSnooperGUI.this.selectAllMsgTypes(false);
/*  955 */         RvSnooperGUI.this._table.getFilteredLogTableModel().refresh();
/*  956 */         RvSnooperGUI.this.updateStatusLabel();
/*      */       }
/*      */     });
/*  959 */     return result;
/*      */   }
/*      */ 
/*      */   protected JMenu createLogLevelColorMenu() {
/*  963 */     JMenu colorMenu = new JMenu("Configure Event Actions Colors");
/*  964 */     colorMenu.setMnemonic('c');
/*  965 */     Iterator levels = getMsgTypes();
/*  966 */     while (levels.hasNext()) {
/*  967 */       colorMenu.add(createSubMenuItem((EventActionType)levels.next()));
/*      */     }
/*      */ 
/*  970 */     return colorMenu;
/*      */   }
/*      */ 
/*      */   protected JMenuItem createResetLogLevelColorMenuItem() {
/*  974 */     JMenuItem result = new JMenuItem("Reset Event Actions Colors");
/*  975 */     result.setMnemonic('r');
/*  976 */     result.addActionListener(new ActionListener()
/*      */     {
/*      */       public void actionPerformed(ActionEvent e) {
/*  979 */         EventActionType.resetLogLevelColorMap();
/*      */ 
/*  982 */         RvSnooperGUI.this._table.getFilteredLogTableModel().refresh();
/*      */       }
/*      */     });
/*  985 */     return result;
/*      */   }
/*      */ 
/*      */   protected void selectAllMsgTypes(boolean selected) {
/*  989 */     Iterator levels = getMsgTypes();
/*  990 */     while (levels.hasNext())
/*  991 */       getMenuItem((EventActionType)levels.next()).setSelected(selected);
/*      */   }
/*      */ 
/*      */   protected JCheckBoxMenuItem getMenuItem(EventActionType level)
/*      */   {
/*  996 */     JCheckBoxMenuItem result = (JCheckBoxMenuItem)this._logLevelMenuItems.get(level);
/*  997 */     if (result == null) {
/*  998 */       result = createMenuItem(level);
/*  999 */       this._logLevelMenuItems.put(level, result);
/*      */     }
/*      */ 
/* 1002 */     return result;
/*      */   }
/*      */ 
/*      */   protected JMenuItem createSubMenuItem(EventActionType level) {
/* 1006 */     final JMenuItem result = new JMenuItem(level.toString());
/* 1007 */     final EventActionType logLevel = level;
/* 1008 */     result.setMnemonic(level.toString().charAt(0));
/* 1009 */     result.addActionListener(new ActionListener() {
/*      */       public void actionPerformed(ActionEvent e) {
/* 1011 */         RvSnooperGUI.this.showLogLevelColorChangeDialog(result, logLevel);
/*      */       }
/*      */     });
/* 1015 */     return result;
/*      */   }
/*      */ 
/*      */   protected void showLogLevelColorChangeDialog(JMenuItem result, EventActionType level)
/*      */   {
/* 1020 */     JMenuItem menuItem = result;
/* 1021 */     Color newColor = JColorChooser.showDialog(this._logMonitorFrame, "Choose Event Actions Color", result.getForeground());
/*      */ 
/* 1026 */     if (newColor != null)
/*      */     {
/* 1028 */       level.setLogLevelColorMap(level, newColor);
/* 1029 */       this._table.getFilteredLogTableModel().refresh();
/*      */     }
/*      */   }
/*      */ 
/*      */   protected JCheckBoxMenuItem createMenuItem(EventActionType level)
/*      */   {
/* 1035 */     JCheckBoxMenuItem result = new JCheckBoxMenuItem(level.toString());
/* 1036 */     result.setSelected(true);
/* 1037 */     result.setMnemonic(level.toString().charAt(0));
/* 1038 */     result.addActionListener(new ActionListener() {
/*      */       public void actionPerformed(ActionEvent e) {
/* 1040 */         RvSnooperGUI.this._table.getFilteredLogTableModel().refresh();
/* 1041 */         RvSnooperGUI.this.updateStatusLabel();
/*      */       }
/*      */     });
/* 1044 */     return result;
/*      */   }
/*      */ 
/*      */   protected JMenu createViewMenu()
/*      */   {
/* 1049 */     JMenu result = new JMenu("View");
/* 1050 */     result.setMnemonic('v');
/* 1051 */     Iterator columns = getLogTableColumns();
/* 1052 */     while (columns.hasNext()) {
/* 1053 */       result.add(getLogTableColumnMenuItem((LogTableColumn)columns.next()));
/*      */     }
/*      */ 
/* 1056 */     result.addSeparator();
/* 1057 */     result.add(createAllLogTableColumnsMenuItem());
/* 1058 */     result.add(createNoLogTableColumnsMenuItem());
/* 1059 */     return result;
/*      */   }
/*      */ 
/*      */   protected JCheckBoxMenuItem getLogTableColumnMenuItem(LogTableColumn column) {
/* 1063 */     JCheckBoxMenuItem result = (JCheckBoxMenuItem)this._logTableColumnMenuItems.get(column);
/* 1064 */     if (result == null) {
/* 1065 */       result = createLogTableColumnMenuItem(column);
/* 1066 */       this._logTableColumnMenuItems.put(column, result);
/*      */     }
/*      */ 
/* 1069 */     return result;
/*      */   }
/*      */ 
/*      */   protected JCheckBoxMenuItem createLogTableColumnMenuItem(LogTableColumn column) {
/* 1073 */     JCheckBoxMenuItem result = new JCheckBoxMenuItem(column.toString());
/*      */ 
/* 1075 */     result.setSelected(true);
/* 1076 */     result.setMnemonic(column.toString().charAt(0));
/* 1077 */     result.addActionListener(new ActionListener()
/*      */     {
/*      */       public void actionPerformed(ActionEvent e) {
/* 1080 */         List selectedColumns = RvSnooperGUI.this.updateView();
/* 1081 */         RvSnooperGUI.this._table.setView(selectedColumns);
/*      */       }
/*      */     });
/* 1084 */     return result;
/*      */   }
/*      */ 
/*      */   protected List<LogTableColumn> updateView() {
/* 1088 */     ArrayList updatedList = new ArrayList();
/* 1089 */     Iterator columnIterator = this._columns.iterator();
/* 1090 */     while (columnIterator.hasNext()) {
/* 1091 */       LogTableColumn column = (LogTableColumn)columnIterator.next();
/* 1092 */       JCheckBoxMenuItem result = getLogTableColumnMenuItem(column);
/*      */ 
/* 1094 */       if (result.isSelected()) {
/* 1095 */         updatedList.add(column);
/*      */       }
/*      */     }
/*      */ 
/* 1099 */     return updatedList;
/*      */   }
/*      */ 
/*      */   protected JMenuItem createAllLogTableColumnsMenuItem() {
/* 1103 */     JMenuItem result = new JMenuItem("Show all Columns");
/* 1104 */     result.setMnemonic('s');
/* 1105 */     result.addActionListener(new ActionListener() {
/*      */       public void actionPerformed(ActionEvent e) {
/* 1107 */         RvSnooperGUI.this.selectAllLogTableColumns(true);
/*      */ 
/* 1109 */         List selectedColumns = RvSnooperGUI.this.updateView();
/* 1110 */         RvSnooperGUI.this._table.setView(selectedColumns);
/*      */       }
/*      */     });
/* 1113 */     return result;
/*      */   }
/*      */ 
/*      */   protected JMenuItem createNoLogTableColumnsMenuItem() {
/* 1117 */     JMenuItem result = new JMenuItem("Hide all Columns");
/* 1118 */     result.setMnemonic('h');
/* 1119 */     result.addActionListener(new ActionListener() {
/*      */       public void actionPerformed(ActionEvent e) {
/* 1121 */         RvSnooperGUI.this.selectAllLogTableColumns(false);
/*      */ 
/* 1123 */         List selectedColumns = RvSnooperGUI.this.updateView();
/* 1124 */         RvSnooperGUI.this._table.setView(selectedColumns);
/*      */       }
/*      */     });
/* 1127 */     return result;
/*      */   }
/*      */ 
/*      */   protected void selectAllLogTableColumns(boolean selected) {
/* 1131 */     Iterator columns = getLogTableColumns();
/* 1132 */     while (columns.hasNext())
/* 1133 */       getLogTableColumnMenuItem((LogTableColumn)columns.next()).setSelected(selected);
/*      */   }
/*      */ 
/*      */   protected JMenu createFileMenu()
/*      */   {
/* 1138 */     JMenu fileMenu = new JMenu("File");
/* 1139 */     fileMenu.setMnemonic('f');
/*      */ 
/* 1141 */     fileMenu.add(createOpenMI());
/* 1142 */     fileMenu.addSeparator();
/* 1143 */     fileMenu.add(createSaveHTML());
/* 1144 */     fileMenu.add(createSaveSelectedAsTextFileMenuItem());
/* 1145 */     fileMenu.add(createSaveAsTextMI());
/* 1146 */     fileMenu.addSeparator();
/* 1147 */     fileMenu.add(createCloseListener());
/* 1148 */     fileMenu.addSeparator();
/* 1149 */     fileMenu.add(createFileSaveConfigMI());
/* 1150 */     fileMenu.add(createFileLoadConfigMI());
/*      */ 
/* 1152 */     fileMenu.addSeparator();
/* 1153 */     fileMenu.add(createExitMI());
/* 1154 */     return fileMenu;
/*      */   }
/*      */ 
/*      */   protected JMenuItem createSaveHTML()
/*      */   {
/* 1163 */     JMenuItem result = new JMenuItem("Save Table to HTML file");
/* 1164 */     result.setMnemonic('h');
/* 1165 */     result.addActionListener(new ActionListener() {
/*      */       public void actionPerformed(ActionEvent e) {
/* 1167 */         RvSnooperFileHandler.saveTableToHtml("EMSSn00p v2.0.3", "http://emsn00p.sf.net", RvSnooperGUI.this.getBaseFrame(), RvSnooperGUI.this._statusLabel, RvSnooperGUI.this._table);
/*      */       }
/*      */     });
/* 1170 */     return result;
/*      */   }
/*      */ 
/*      */   protected JMenuItem createFileSaveConfigMI()
/*      */   {
/* 1179 */     JMenuItem result = new JMenuItem("Save configuration to file");
/* 1180 */     result.setMnemonic('c');
/* 1181 */     result.addActionListener(new ActionListener() {
/*      */       public void actionPerformed(ActionEvent e) {
/*      */         RvSnooperErrorDialog error;
/*      */         try { FileDialog fd = new FileDialog(RvSnooperGUI.this.getBaseFrame(), "Save config File", 1);
/* 1185 */           fd.setDirectory(RvSnooperGUI.this._configurationManager.getFilename());
/* 1186 */           fd.setFile("*.rs0");
/* 1187 */           fd.setVisible(true);
/*      */ 
/* 1189 */           String fileName = fd.getDirectory() + fd.getFile();
/* 1190 */           RvSnooperGUI.this._configurationManager.setFilename(fileName);
/* 1191 */           RvSnooperGUI.this._configurationManager.save();
/* 1192 */           RvSnooperGUI.this._statusLabel.setText("Saved configuration in " + fileName);
/*      */         } catch (Exception ex) {
/* 1194 */           error = new RvSnooperErrorDialog(RvSnooperGUI.this.getBaseFrame(), ex.getMessage());
/*      */         }
/*      */       }
/*      */     });
/* 1200 */     return result;
/*      */   }
/*      */ 
/*      */   protected JMenuItem createFileLoadConfigMI()
/*      */   {
/* 1208 */     JMenuItem result = new JMenuItem("Load configuration from file");
/* 1209 */     result.setMnemonic('c');
/* 1210 */     result.addActionListener(new ActionListener() {
/*      */       public void actionPerformed(ActionEvent e) {
/*      */         RvSnooperErrorDialog error;
/*      */         try { FileDialog fd = new FileDialog(RvSnooperGUI.this.getBaseFrame(), "Open config File", 0);
/* 1214 */           fd.setDirectory(RvSnooperGUI.this._configurationManager.getFilename());
/* 1215 */           fd.setFile("*.rs0");
/* 1216 */           fd.setVisible(true);
/*      */ 
/* 1218 */           String fileName = fd.getDirectory() + fd.getFile();
/* 1219 */           RvSnooperGUI.this._configurationManager.setFilename(fileName);
/* 1220 */           RvSnooperGUI.this._configurationManager.load();
/* 1221 */           RvSnooperGUI.this._statusLabel.setText("Loaded configuration from " + fileName);
/*      */         } catch (Exception ex) {
/* 1223 */           error = new RvSnooperErrorDialog(RvSnooperGUI.this.getBaseFrame(), ex.getMessage());
/*      */         }
/*      */       }
/*      */     });
/* 1229 */     return result;
/*      */   }
/*      */ 
/*      */   protected JMenuItem createSaveAsTextMI()
/*      */   {
/* 1237 */     JMenuItem result = new JMenuItem("Save Table to text file");
/* 1238 */     result.addActionListener(new ActionListener() {
/*      */       public void actionPerformed(ActionEvent e) {
/*      */         RvSnooperErrorDialog error;
/*      */         try { RvSnooperFileHandler.saveTableToTextFile("EMSSn00p v2.0.3 http://emsn00p.sf.net", RvSnooperGUI.this.getBaseFrame(), RvSnooperGUI.this._statusLabel, RvSnooperGUI.this._table);
/*      */         } catch (Exception ex) {
/* 1243 */           error = new RvSnooperErrorDialog(RvSnooperGUI.this.getBaseFrame(), ex.getMessage());
/*      */         }
/*      */       }
/*      */     });
/* 1249 */     return result;
/*      */   }
/*      */ 
/*      */   protected JMenuItem createOpenMI()
/*      */   {
/* 1258 */     JMenuItem result = new JMenuItem("New Listener...");
/* 1259 */     result.setMnemonic('n');
/* 1260 */     result.setAccelerator(KeyStroke.getKeyStroke("control N"));
/* 1261 */     result.addActionListener(new ActionListener() {
/*      */       public void actionPerformed(ActionEvent e) {
/* 1263 */         RvSnooperGUI.this.requestNewRvListener(null);
/*      */       }
/*      */     });
/* 1266 */     return result;
/*      */   }
/*      */ 
/*      */   protected JMenuItem createSaveConfigMI()
/*      */   {
/* 1271 */     JMenuItem result = new JMenuItem("Save Listeners to file");
/* 1272 */     result.setMnemonic('s');
/* 1273 */     result.addActionListener(new ActionListener() {
/*      */       public void actionPerformed(ActionEvent e) {
/* 1275 */         RvSnooperGUI.this.requestNewRvListener(null);
/*      */       }
/*      */     });
/* 1278 */     return result;
/*      */   }
/*      */ 
/*      */   protected JMenuItem createOpenConfigMI() {
/* 1282 */     JMenuItem result = new JMenuItem("Open Listeners from file");
/* 1283 */     result.setMnemonic('o');
/* 1284 */     result.addActionListener(new ActionListener() {
/*      */       public void actionPerformed(ActionEvent e) {
/* 1286 */         RvSnooperGUI.this.requestNewRvListener(null);
/*      */       }
/*      */     });
/* 1289 */     return result;
/*      */   }
/*      */ 
/*      */   protected JMenuItem createCloseListener()
/*      */   {
/* 1294 */     JMenuItem result = new JMenuItem("Close All Listeners");
/* 1295 */     result.setMnemonic('c');
/* 1296 */     result.setAccelerator(KeyStroke.getKeyStroke("control Q"));
/* 1297 */     result.addActionListener(new ActionListener() {
/*      */       public void actionPerformed(ActionEvent e) {
/*      */         try {
/* 1300 */           EMSController.shutdownAll();
/*      */         } catch (JMSException e1) {
/* 1302 */           e1.printStackTrace();
/*      */         }
/* 1304 */         RvSnooperGUI.this.updateBanner();
/*      */       }
/*      */     });
/* 1307 */     return result;
/*      */   }
/*      */ 
/*      */   protected void createMRUListnerListMI(JMenu menu)
/*      */   {
/* 1316 */     String[] parameters = this._mruListnerManager.getMRUFileList();
/*      */ 
/* 1318 */     if (parameters != null) {
/* 1319 */       menu.addSeparator();
/* 1320 */       for (int i = 0; i < parameters.length; i++)
/*      */       {
/* 1322 */         JMenuItem result = new JMenuItem(i + 1 + " " + parameters[i]);
/* 1323 */         result.setMnemonic(i + 1);
/* 1324 */         result.addActionListener(new ActionListener() {
/*      */           public void actionPerformed(ActionEvent e) {
/* 1326 */             RvSnooperGUI.this.requestOpenMRU(e);
/*      */           }
/*      */         });
/* 1329 */         menu.add(result);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected JMenuItem createExitMI() {
/* 1335 */     JMenuItem result = new JMenuItem("Exit");
/* 1336 */     result.setMnemonic('x');
/* 1337 */     result.addActionListener(new ActionListener() {
/*      */       public void actionPerformed(ActionEvent e) {
/* 1339 */         RvSnooperGUI.this.requestExit();
/*      */       }
/*      */     });
/* 1342 */     return result;
/*      */   }
/*      */ 
/*      */   protected JMenu createConfigureMenu() {
/* 1346 */     JMenu configureMenu = new JMenu("Configure");
/* 1347 */     configureMenu.setMnemonic('c');
/* 1348 */     configureMenu.add(createConfigureSave());
/* 1349 */     configureMenu.add(createConfigureReset());
/* 1350 */     configureMenu.add(createConfigureMaxRecords());
/* 1351 */     configureMenu.add(createConfigureDateFormat());
/*      */ 
/* 1353 */     return configureMenu;
/*      */   }
/*      */ 
/*      */   protected JMenuItem createConfigureSave() {
/* 1357 */     JMenuItem result = new JMenuItem("Save");
/* 1358 */     result.setMnemonic('s');
/* 1359 */     result.addActionListener(new ActionListener()
/*      */     {
/*      */       public void actionPerformed(ActionEvent e) {
/* 1362 */         RvSnooperGUI.this.saveConfiguration();
/*      */       }
/*      */     });
/* 1366 */     return result;
/*      */   }
/*      */ 
/*      */   protected JMenuItem createConfigureReset() {
/* 1370 */     JMenuItem result = new JMenuItem("Reset");
/* 1371 */     result.setMnemonic('r');
/* 1372 */     result.addActionListener(new ActionListener() {
/*      */       public void actionPerformed(ActionEvent e) {
/* 1374 */         RvSnooperGUI.this.resetConfiguration();
/*      */       }
/*      */     });
/* 1378 */     return result;
/*      */   }
/*      */ 
/*      */   protected JMenuItem createConfigureMaxRecords()
/*      */   {
/* 1383 */     JMenuItem result = new JMenuItem("Set Max Number of Records");
/* 1384 */     result.setMnemonic('m');
/* 1385 */     result.addActionListener(new ActionListener() {
/*      */       public void actionPerformed(ActionEvent e) {
/* 1387 */         RvSnooperGUI.this.setMaxRecordConfiguration();
/*      */       }
/*      */     });
/* 1391 */     result.addKeyListener(new KeyAdapter() {
/*      */       public void keyPressed(KeyEvent e) {
/* 1393 */         if (e.getKeyCode() == 10)
/* 1394 */           RvSnooperGUI.this.hide();
/*      */       }
/*      */     });
/* 1399 */     return result;
/*      */   }
/*      */ 
/*      */   protected JMenuItem createConfigureDateFormat()
/*      */   {
/* 1404 */     JMenuItem result = new JMenuItem("Configure Date Format");
/* 1405 */     result.setMnemonic('d');
/*      */ 
/* 1407 */     result.addActionListener(new ActionListener() {
/*      */       public void actionPerformed(ActionEvent e) {
/* 1409 */         RvSnooperGUI.this.setDateConfiguration();
/*      */       }
/*      */     });
/* 1413 */     return result;
/*      */   }
/*      */   protected void saveConfiguration() {
/*      */     RvSnooperErrorDialog error;
/*      */     try {
/* 1418 */       this._configurationManager.save();
/*      */     } catch (Exception ex) {
/* 1420 */       ex.printStackTrace();
/* 1421 */       error = new RvSnooperErrorDialog(getBaseFrame(), ex.getMessage());
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void resetConfiguration()
/*      */   {
/* 1428 */     this._configurationManager.reset();
/*      */   }
/*      */ 
/*      */   protected void setMaxRecordConfiguration() {
/* 1432 */     RvSnooperInputDialog inputDialog = new RvSnooperInputDialog(getBaseFrame(), "Set Max Number of Records", "", 10);
/*      */ 
/* 1435 */     String temp = inputDialog.getText();
/*      */ 
/* 1437 */     if (temp != null)
/*      */       try {
/* 1439 */         setMaxNumberOfLogRecords(Integer.parseInt(temp));
/*      */       } catch (NumberFormatException e) {
/* 1441 */         RvSnooperErrorDialog error = new RvSnooperErrorDialog(getBaseFrame(), "'" + temp + "' is an invalid parameter.\nPlease try again.");
/*      */ 
/* 1444 */         setMaxRecordConfiguration();
/*      */       }
/*      */   }
/*      */ 
/*      */   protected void setDateConfiguration()
/*      */   {
/* 1450 */     RvSnooperInputDialog inputDialog = new RvSnooperInputDialog(getBaseFrame(), "Set DateFormat", "", 10);
/*      */ 
/* 1453 */     inputDialog.addKeyListener(new KeyAdapter() {
/*      */       public void keyPressed(KeyEvent e) {
/* 1455 */         if (e.getKeyCode() == 10)
/* 1456 */           RvSnooperGUI.this.hide();
/*      */       }
/*      */     });
/* 1461 */     String temp = inputDialog.getText();
/*      */ 
/* 1463 */     if (temp != null)
/*      */       try {
/* 1465 */         setDateFormat(temp);
/*      */       } catch (NumberFormatException e) {
/* 1467 */         RvSnooperErrorDialog error = new RvSnooperErrorDialog(getBaseFrame(), "'" + temp + "' is an invalid parameter.\nPlease try again.");
/*      */ 
/* 1470 */         setMaxRecordConfiguration();
/*      */       }
/*      */   }
/*      */ 
/*      */   protected JMenu createHelpMenu()
/*      */   {
/* 1477 */     JMenu helpMenu = new JMenu("Help");
/* 1478 */     helpMenu.setMnemonic('h');
/* 1479 */     helpMenu.add(createHelpAbout());
/*      */ 
/* 1481 */     helpMenu.add(createHelpGotoHomepage());
/*      */ 
/* 1483 */     helpMenu.add(createHelpProperties());
/* 1484 */     helpMenu.add(createHelpLICENSE());
/* 1485 */     StringBuffer a = new StringBuffer();
/* 1486 */     a.toString();
/* 1487 */     return helpMenu;
/*      */   }
/*      */ 
/*      */   protected JMenuItem createHelpProperties()
/*      */   {
/* 1492 */     String title = "Show Properties";
/* 1493 */     JMenuItem result = new JMenuItem("Show Properties");
/* 1494 */     result.addActionListener(new ActionListener() {
/*      */       public void actionPerformed(ActionEvent e) {
/* 1496 */         RvSnooperGUI.this.showPropertiesDialog("Show Properties");
/*      */       }
/*      */     });
/* 1499 */     return result;
/*      */   }
/*      */ 
/*      */   protected JMenuItem createHelpLICENSE() {
/* 1503 */     String title = "License information";
/* 1504 */     JMenuItem result = new JMenuItem("License information");
/* 1505 */     result.setMnemonic('l');
/* 1506 */     result.addActionListener(new ActionListener() {
/*      */       public void actionPerformed(ActionEvent e) {
/*      */         RvSnooperErrorDialog error;
/*      */         try { BrowserLauncher.openURL("http://www.apache.org/licenses/LICENSE");
/*      */         } catch (Exception ex)
/*      */         {
/* 1512 */           error = new RvSnooperErrorDialog(RvSnooperGUI.this.getBaseFrame(), "Could not open browser : " + ex.getMessage());
/*      */         }
/*      */       }
/*      */     });
/* 1517 */     return result;
/*      */   }
/*      */ 
/*      */   protected JMenuItem createHelpGotoHomepage()
/*      */   {
/* 1526 */     String title = "Help topics";
/* 1527 */     JMenuItem result = new JMenuItem("Help topics");
/* 1528 */     result.setMnemonic('t');
/* 1529 */     result.setAccelerator(KeyStroke.getKeyStroke("F1"));
/* 1530 */     result.addActionListener(new ActionListener() {
/*      */       public void actionPerformed(ActionEvent e) {
/*      */         RvSnooperErrorDialog error;
/*      */         try { BrowserLauncher.openURL("http://rvsn00p.sf.net");
/*      */         } catch (Exception ex)
/*      */         {
/* 1536 */           error = new RvSnooperErrorDialog(RvSnooperGUI.this.getBaseFrame(), "Could not open browser : " + ex.getMessage());
/*      */         }
/*      */       }
/*      */     });
/* 1541 */     return result;
/*      */   }
/*      */ 
/*      */   protected JMenuItem createHelpAbout()
/*      */   {
/* 1549 */     String title = "About EMSSn00per";
/* 1550 */     JMenuItem result = new JMenuItem("About EMSSn00per");
/* 1551 */     result.setMnemonic('a');
/* 1552 */     result.addActionListener(new ActionListener() {
/*      */       public void actionPerformed(ActionEvent e) {
/* 1554 */         RvSnooperGUI.this.showAboutDialog("About EMSSn00per");
/*      */       }
/*      */     });
/* 1557 */     return result;
/*      */   }
/*      */ 
/*      */   protected void showPropertiesDialog(String title)
/*      */   {
/* 1562 */     JOptionPane.showMessageDialog(this._logMonitorFrame, this._displayedLogBrokerProperties.toArray(), title, -1);
/*      */   }
/*      */ 
/*      */   protected void showAboutDialog(String title)
/*      */   {
/* 1571 */     JOptionPane.showMessageDialog(this._logMonitorFrame, new String[] { "EMSSn00p v2.0.3", " ", "Constructed by Orjan Lundberg <lundberg@home.se>", " ", "This product includes software developed by the Apache Software Foundation (http://www.apache.org/). ", " ", "Thanks goes to (in no special order):", " ", "Julian Lo, Dan McLean ", " ", "Based on Jakarta log4J LogFactor5, Contributed by ThoughtWorks Inc.", " ", "Copyright (C) The Apache Software Foundation. All rights reserved.", " ", "This software is published under the terms of the Apache Software", "License version 1.1, a copy of which has been included with this", "distribution in the LICENSE.txt file. ", " " }, title, -1);
/*      */   }
/*      */ 
/*      */   protected JMenu createEditMenu()
/*      */   {
/* 1598 */     JMenu editMenu = new JMenu("Edit");
/* 1599 */     editMenu.setMnemonic('e');
/* 1600 */     editMenu.add(createEditFindMI());
/* 1601 */     editMenu.add(createEditFindNextMI());
/* 1602 */     editMenu.addSeparator();
/* 1603 */     editMenu.add(createEditFilterTIDMI());
/* 1604 */     editMenu.add(createEditFilterBySelectedTIDMI());
/* 1605 */     editMenu.add(createEditFilterBySelectedSubjectMI());
/* 1606 */     editMenu.add(createEditFilterBySelectedConnHN());
/* 1607 */     editMenu.add(createEditFilterBySelectedRevConnHN());
/* 1608 */     editMenu.add(createEditRemoveAllFiltersTIDMI());
/* 1609 */     return editMenu;
/*      */   }
/*      */ 
/*      */   protected JMenuItem createEditFindNextMI() {
/* 1613 */     JMenuItem editFindNextMI = new JMenuItem("Find Next");
/* 1614 */     editFindNextMI.setMnemonic('n');
/* 1615 */     editFindNextMI.setAccelerator(KeyStroke.getKeyStroke("F3"));
/* 1616 */     editFindNextMI.addActionListener(new ActionListener() {
/*      */       public void actionPerformed(ActionEvent e) {
/* 1618 */         RvSnooperGUI.this.findSearchText();
/*      */       }
/*      */     });
/* 1621 */     return editFindNextMI;
/*      */   }
/*      */ 
/*      */   protected JMenuItem createEditFindMI() {
/* 1625 */     JMenuItem editFindMI = new JMenuItem("Find");
/* 1626 */     editFindMI.setMnemonic('f');
/* 1627 */     editFindMI.setAccelerator(KeyStroke.getKeyStroke("control F"));
/*      */ 
/* 1629 */     editFindMI.addActionListener(new ActionListener()
/*      */     {
/*      */       public void actionPerformed(ActionEvent e) {
/* 1632 */         String inputValue = JOptionPane.showInputDialog(RvSnooperGUI.this._logMonitorFrame, "Find text: ", "Search Record Messages", 3);
/*      */ 
/* 1639 */         RvSnooperGUI.this.setSearchText(inputValue);
/* 1640 */         RvSnooperGUI.this.findSearchText();
/*      */       }
/*      */     });
/* 1645 */     return editFindMI;
/*      */   }
/*      */ 
/*      */   protected JMenuItem createEditFilterTIDMI() {
/* 1649 */     JMenuItem editFilterNDCMI = new JMenuItem("Filter by JMSCorrelationID");
/* 1650 */     editFilterNDCMI.setMnemonic('t');
/* 1651 */     editFilterNDCMI.setAccelerator(KeyStroke.getKeyStroke("control shift T"));
/* 1652 */     editFilterNDCMI.addActionListener(new ActionListener()
/*      */     {
/*      */       public void actionPerformed(ActionEvent e) {
/* 1655 */         String inputValue = JOptionPane.showInputDialog(RvSnooperGUI.this._logMonitorFrame, "Filter by this JMSCorrelationID: ", "Filter Log Records by tracking id", 3);
/*      */ 
/* 1662 */         RvSnooperGUI.this.setTIDTextFilter(inputValue);
/* 1663 */         RvSnooperGUI.this.filterByTID();
/* 1664 */         RvSnooperGUI.this._table.getFilteredLogTableModel().refresh();
/* 1665 */         RvSnooperGUI.this.updateStatusLabel();
/*      */       }
/*      */     });
/* 1670 */     return editFilterNDCMI;
/*      */   }
/*      */ 
/*      */   protected JMenuItem createEditFilterBySelectedTIDMI()
/*      */   {
/* 1677 */     JMenuItem editFilterTIDMI = new JMenuItem("Filter by Selected JMSCorrelationID");
/* 1678 */     editFilterTIDMI.setMnemonic('s');
/* 1679 */     editFilterTIDMI.setAccelerator(KeyStroke.getKeyStroke("control T"));
/* 1680 */     editFilterTIDMI.addActionListener(new ActionListener()
/*      */     {
/*      */       public void actionPerformed(ActionEvent e)
/*      */       {
/* 1684 */         ListSelectionModel lsm = RvSnooperGUI.this._table.getSelectionModel();
/*      */ 
/* 1686 */         if (!lsm.isSelectionEmpty())
/*      */         {
/* 1689 */           int selectedRow = lsm.getMinSelectionIndex();
/*      */ 
/* 1692 */           FilteredLogTableModel ftm = RvSnooperGUI.this._table.getFilteredLogTableModel();
/*      */ 
/* 1695 */           String sTID = (String)RvSnooperGUI.this._table.getModel().getValueAt(selectedRow, RvSnooperGUI.this._table.getTIDColumnID());
/* 1696 */           if (sTID != null) {
/* 1697 */             RvSnooperGUI.this.setTIDTextFilter(sTID);
/* 1698 */             RvSnooperGUI.this.filterByTID();
/* 1699 */             ftm.refresh();
/*      */           }
/*      */         }
/*      */       }
/*      */     });
/* 1708 */     return editFilterTIDMI;
/*      */   }
/*      */ 
/*      */   protected JMenuItem createEditFilterBySelectedConnHN() {
/* 1712 */     JMenuItem editFilterTIDMI = new JMenuItem("Filter by Selected Conn Hostname");
/* 1713 */     editFilterTIDMI.setMnemonic('H');
/* 1714 */     editFilterTIDMI.setAccelerator(KeyStroke.getKeyStroke("control H"));
/* 1715 */     editFilterTIDMI.addActionListener(new ActionListener()
/*      */     {
/*      */       public void actionPerformed(ActionEvent e) {
/* 1718 */         ListSelectionModel lsm = RvSnooperGUI.this._table.getSelectionModel();
/*      */ 
/* 1720 */         if (!lsm.isSelectionEmpty())
/*      */         {
/* 1723 */           int selectedRow = lsm.getMinSelectionIndex();
/*      */ 
/* 1726 */           FilteredLogTableModel ftm = RvSnooperGUI.this._table.getFilteredLogTableModel();
/*      */ 
/* 1729 */           String sHNF = (String)RvSnooperGUI.this._table.getModel().getValueAt(selectedRow, RvSnooperGUI.this._table.getConnHostnameColumnID());
/*      */ 
/* 1731 */           if (sHNF != null) {
/* 1732 */             RvSnooperGUI.this.setConnHostnameTextFilter(sHNF);
/* 1733 */             RvSnooperGUI.this.filterByConnHostname();
/* 1734 */             ftm.refresh();
/*      */           }
/*      */         }
/*      */       }
/*      */     });
/* 1743 */     return editFilterTIDMI;
/*      */   }
/*      */ 
/*      */   protected JMenuItem createEditFilterBySelectedRevConnHN() {
/* 1747 */     JMenuItem editFilterTIDMI = new JMenuItem("Remove Selected Conn Hostname");
/*      */ 
/* 1750 */     editFilterTIDMI.addActionListener(new ActionListener()
/*      */     {
/*      */       public void actionPerformed(ActionEvent e) {
/* 1753 */         ListSelectionModel lsm = RvSnooperGUI.this._table.getSelectionModel();
/*      */ 
/* 1755 */         if (!lsm.isSelectionEmpty())
/*      */         {
/* 1758 */           int selectedRow = lsm.getMinSelectionIndex();
/*      */ 
/* 1761 */           FilteredLogTableModel ftm = RvSnooperGUI.this._table.getFilteredLogTableModel();
/*      */ 
/* 1764 */           String sHNF = (String)RvSnooperGUI.this._table.getModel().getValueAt(selectedRow, RvSnooperGUI.this._table.getConnHostnameColumnID());
/*      */ 
/* 1766 */           if (sHNF != null) {
/* 1767 */             RvSnooperGUI.this.setConnHostnameTextFilter(sHNF);
/* 1768 */             RvSnooperGUI.this.filterRemoveConnHostname();
/* 1769 */             ftm.refresh();
/*      */           }
/*      */         }
/*      */       }
/*      */     });
/* 1778 */     return editFilterTIDMI;
/*      */   }
/*      */ 
/*      */   protected JMenuItem createEditFilterBySelectedSubjectMI()
/*      */   {
/* 1783 */     JMenuItem editFilterSubjectMI = new JMenuItem("Filter by Selected Destination");
/* 1784 */     editFilterSubjectMI.setMnemonic('y');
/* 1785 */     editFilterSubjectMI.setAccelerator(KeyStroke.getKeyStroke("control Y"));
/* 1786 */     editFilterSubjectMI.addActionListener(new ActionListener()
/*      */     {
/*      */       public void actionPerformed(ActionEvent e) {
/* 1789 */         ListSelectionModel lsm = RvSnooperGUI.this._table.getSelectionModel();
/*      */ 
/* 1791 */         if (!lsm.isSelectionEmpty())
/*      */         {
/* 1794 */           int selectedRow = lsm.getMinSelectionIndex();
/*      */ 
/* 1797 */           FilteredLogTableModel ftm = RvSnooperGUI.this._table.getFilteredLogTableModel();
/*      */ 
/* 1800 */           String s = (String)RvSnooperGUI.this._table.getModel().getValueAt(selectedRow, RvSnooperGUI.this._table.getSubjectColumnID());
/* 1801 */           if (s != null) {
/* 1802 */             RvSnooperGUI.this.setSubjectTextFilter(s);
/* 1803 */             RvSnooperGUI.this.filterBySubject();
/* 1804 */             ftm.refresh();
/*      */           }
/*      */         }
/*      */       }
/*      */     });
/* 1813 */     return editFilterSubjectMI;
/*      */   }
/*      */ 
/*      */   protected void setTIDTextFilter(String text)
/*      */   {
/* 1819 */     if (text == null)
/* 1820 */       _JMSCorrelationIDTextFilter = "";
/*      */     else
/* 1822 */       _JMSCorrelationIDTextFilter = text;
/*      */   }
/*      */ 
/*      */   protected void setConnHostnameTextFilter(String text)
/*      */   {
/* 1829 */     if (text == null)
/* 1830 */       this._ConnHostnameTextFilter = "";
/*      */     else
/* 1832 */       this._ConnHostnameTextFilter = text;
/*      */   }
/*      */ 
/*      */   protected void setSubjectTextFilter(String text)
/*      */   {
/* 1839 */     if (text == null)
/* 1840 */       _subjectTextFilter = "";
/*      */     else
/* 1842 */       _subjectTextFilter = text;
/*      */   }
/*      */ 
/*      */   protected void filterByTID()
/*      */   {
/* 1847 */     String text = _JMSCorrelationIDTextFilter;
/* 1848 */     if ((text == null) || (text.length() == 0)) {
/* 1849 */       return;
/*      */     }
/*      */ 
/* 1853 */     this._table.getFilteredLogTableModel().setLogRecordFilter(createTIDLogRecordFilter(text));
/*      */ 
/* 1855 */     this._statusLabel.setText("Filtered by JMSCorrelationiD " + text);
/*      */   }
/*      */ 
/*      */   protected void filterByConnHostname() {
/* 1859 */     String text = this._ConnHostnameTextFilter;
/* 1860 */     if ((text == null) || (text.length() == 0)) {
/* 1861 */       return;
/*      */     }
/*      */ 
/* 1865 */     this._table.getFilteredLogTableModel().setLogRecordFilter(createConnHostNameRecordFilter(text));
/*      */ 
/* 1867 */     this._statusLabel.setText("Filtered by Connnection Hostname " + text);
/*      */   }
/*      */ 
/*      */   protected void filterRemoveConnHostname()
/*      */   {
/* 1872 */     String text = this._ConnHostnameTextFilter;
/* 1873 */     if ((text == null) || (text.length() == 0)) {
/* 1874 */       return;
/*      */     }
/*      */ 
/* 1878 */     this._table.getFilteredLogTableModel().setLogRecordFilter(createConnHostNameReverseRecordFilter(text));
/*      */ 
/* 1880 */     this._statusLabel.setText("Filter Removed Connnection Hostname " + text);
/*      */   }
/*      */ 
/*      */   protected void filterBySubject()
/*      */   {
/* 1886 */     String text = _subjectTextFilter;
/* 1887 */     if ((text == null) || (text.length() == 0)) {
/* 1888 */       return;
/*      */     }
/*      */ 
/* 1892 */     this._table.getFilteredLogTableModel().setLogRecordFilter(createSubjectLogRecordFilter(text));
/*      */ 
/* 1894 */     this._statusLabel.setText("Filtered by Destination  " + text);
/*      */   }
/*      */ 
/*      */   protected LogRecordFilter createTIDLogRecordFilter(String text) {
/* 1898 */     _JMSCorrelationIDTextFilter = text;
/* 1899 */     LogRecordFilter result = new LogRecordFilter() {
/*      */       public boolean passes(LogRecord record) {
/* 1901 */         String correlationID = record.getJMSCorrelationID();
/*      */ 
/* 1903 */         if ((correlationID == null) || (RvSnooperGUI._JMSCorrelationIDTextFilter == null))
/* 1904 */           return false;
/* 1905 */         if (correlationID.indexOf(RvSnooperGUI._JMSCorrelationIDTextFilter) == -1) {
/* 1906 */           return false;
/*      */         }
/* 1908 */         CategoryPath path = new CategoryPath(record.getJMSDestination());
/* 1909 */         return (RvSnooperGUI.this.getMenuItem(record.getType()).isSelected()) && (RvSnooperGUI.this._subjectExplorerTree.getExplorerModel().isCategoryPathActive(path));
/*      */       }
/*      */     };
/* 1915 */     return result;
/*      */   }
/*      */ 
/*      */   protected LogRecordFilter createSubjectLogRecordFilter(String text) {
/* 1919 */     _JMSCorrelationIDTextFilter = text;
/* 1920 */     LogRecordFilter result = new LogRecordFilter() {
/*      */       public boolean passes(LogRecord record) {
/* 1922 */         String subject = record.getJMSDestination();
/* 1923 */         if ((subject == null) || (RvSnooperGUI._subjectTextFilter == null))
/* 1924 */           return false;
/* 1925 */         if (subject.indexOf(RvSnooperGUI._subjectTextFilter) == -1) {
/* 1926 */           return false;
/*      */         }
/* 1928 */         CategoryPath path = new CategoryPath(subject);
/* 1929 */         return (RvSnooperGUI.this.getMenuItem(record.getType()).isSelected()) && (RvSnooperGUI.this._subjectExplorerTree.getExplorerModel().isCategoryPathActive(path));
/*      */       }
/*      */     };
/* 1935 */     return result;
/*      */   }
/*      */ 
/*      */   protected LogRecordFilter createConnHostNameRecordFilter(String text) {
/* 1939 */     this._ConnHostnameTextFilter = text;
/* 1940 */     LogRecordFilter result = new LogRecordFilter()
/*      */     {
/*      */       public boolean passes(LogRecord record) {
/* 1943 */         String hostName = record.getConnHostName();
/* 1944 */         if ((hostName == null) || (RvSnooperGUI.this._ConnHostnameTextFilter == null))
/* 1945 */           return false;
/* 1946 */         if (hostName.indexOf(RvSnooperGUI.this._ConnHostnameTextFilter) == -1) {
/* 1947 */           return false;
/*      */         }
/* 1949 */         String subject = record.getJMSDestination();
/* 1950 */         CategoryPath path = new CategoryPath(subject);
/* 1951 */         return (RvSnooperGUI.this.getMenuItem(record.getType()).isSelected()) && (RvSnooperGUI.this._subjectExplorerTree.getExplorerModel().isCategoryPathActive(path));
/*      */       }
/*      */     };
/* 1957 */     return result;
/*      */   }
/*      */ 
/*      */   protected LogRecordFilter createConnHostNameReverseRecordFilter(String text) {
/* 1961 */     this._ConnHostnameTextFilter = text;
/* 1962 */     LogRecordFilter result = new LogRecordFilter()
/*      */     {
/*      */       public boolean passes(LogRecord record) {
/* 1965 */         String hostName = record.getConnHostName();
/* 1966 */         if ((hostName == null) || (RvSnooperGUI.this._ConnHostnameTextFilter == null))
/* 1967 */           return true;
/* 1968 */         if (hostName.compareTo(RvSnooperGUI.this._ConnHostnameTextFilter) == 0) {
/* 1969 */           return false;
/*      */         }
/* 1971 */         String subject = record.getJMSDestination();
/* 1972 */         CategoryPath path = new CategoryPath(subject);
/* 1973 */         return (RvSnooperGUI.this.getMenuItem(record.getType()).isSelected()) && (RvSnooperGUI.this._subjectExplorerTree.getExplorerModel().isCategoryPathActive(path));
/*      */       }
/*      */     };
/* 1979 */     return result;
/*      */   }
/*      */ 
/*      */   protected JMenuItem createEditRemoveAllFiltersTIDMI() {
/* 1983 */     JMenuItem editRestoreAllNDCMI = new JMenuItem("Remove all filters");
/* 1984 */     editRestoreAllNDCMI.setMnemonic('r');
/* 1985 */     editRestoreAllNDCMI.setAccelerator(KeyStroke.getKeyStroke("control R"));
/* 1986 */     editRestoreAllNDCMI.addActionListener(new ActionListener()
/*      */     {
/*      */       public void actionPerformed(ActionEvent e) {
/* 1989 */         RvSnooperGUI.this._table.getFilteredLogTableModel().setLogRecordFilter(RvSnooperGUI.this.createLogRecordFilter());
/*      */ 
/* 1991 */         RvSnooperGUI.this.setTIDTextFilter("");
/* 1992 */         RvSnooperGUI.this._table.getFilteredLogTableModel().refresh();
/* 1993 */         RvSnooperGUI.this.updateStatusLabel();
/*      */       }
/*      */     });
/* 1997 */     return editRestoreAllNDCMI;
/*      */   }
/*      */ 
/*      */   protected JToolBar createToolBar()
/*      */   {
/* 2002 */     JToolBar tb = new JToolBar();
/* 2003 */     tb.putClientProperty("JToolBar.isRollover", Boolean.TRUE);
/* 2004 */     JComboBox fontCombo = new JComboBox();
/* 2005 */     JComboBox fontSizeCombo = new JComboBox();
/* 2006 */     this._fontSizeCombo = fontSizeCombo;
/* 2007 */     this._fontNameCombo = fontCombo;
/* 2008 */     JComboBox rendererCombo = new JComboBox();
/* 2009 */     this._rendererCombo = rendererCombo;
/*      */ 
/* 2011 */     this._cl = getClass().getClassLoader();
/* 2012 */     if (this._cl == null) {
/* 2013 */       this._cl = ClassLoader.getSystemClassLoader();
/*      */     }
/* 2015 */     URL newIconURL = this._cl.getResource("emssn00p/viewer/images/channelexplorer_new.gif");
/*      */ 
/* 2017 */     ImageIcon newIcon = null;
/*      */ 
/* 2019 */     if (newIconURL != null) {
/* 2020 */       newIcon = new ImageIcon(newIconURL);
/*      */     }
/*      */ 
/* 2023 */     JButton listenerButton = new JButton("Add Listener");
/*      */ 
/* 2025 */     if (newIcon != null) {
/* 2026 */       listenerButton.setIcon(newIcon);
/*      */     }
/*      */ 
/* 2029 */     listenerButton.setToolTipText("Create new Rv Listener.");
/*      */ 
/* 2031 */     listenerButton.addActionListener(new ActionListener()
/*      */     {
/*      */       public void actionPerformed(ActionEvent e) {
/* 2034 */         RvSnooperGUI.this.requestNewRvListener(null);
/*      */       }
/*      */     });
/* 2040 */     JButton newButton = new JButton("Clear Log Table");
/*      */ 
/* 2043 */     URL tcIconURL = this._cl.getResource("emssn00p/viewer/images/trash.gif");
/*      */ 
/* 2045 */     ImageIcon tcIcon = null;
/*      */ 
/* 2047 */     if (tcIconURL != null) {
/* 2048 */       tcIcon = new ImageIcon(tcIconURL);
/*      */     }
/*      */ 
/* 2051 */     if (newIcon != null) {
/* 2052 */       newButton.setIcon(tcIcon);
/*      */     }
/*      */ 
/* 2055 */     newButton.setToolTipText("Clear Log Table.");
/*      */ 
/* 2057 */     newButton.addActionListener(getNewButtonActionListener());
/*      */ 
/* 2060 */     this._pauseButton = new JButton("Pause all listeners");
/*      */ 
/* 2063 */     this._pauseButton.addActionListener(getPauseButonActionListener());
/*      */ 
/* 2065 */     addFontsToFontCombo(fontCombo);
/*      */ 
/* 2067 */     fontCombo.setSelectedItem(getFontName());
/*      */ 
/* 2069 */     fontCombo.addActionListener(getFontComboActionListener());
/*      */ 
/* 2071 */     addFontSizesToCombo(fontSizeCombo);
/*      */ 
/* 2073 */     fontSizeCombo.setSelectedItem(String.valueOf(getFontSize()));
/* 2074 */     fontSizeCombo.addActionListener(getFontSizeComboActionListener());
/*      */ 
/* 2076 */     addRenderersToRendererCombo(rendererCombo);
/* 2077 */     rendererCombo.setSelectedItem(getLastUsedRenderer());
/*      */ 
/* 2079 */     rendererCombo.addActionListener(getRendererActionListeener());
/*      */ 
/* 2082 */     tb.add(new JLabel(" Font: "));
/* 2083 */     tb.add(fontCombo);
/* 2084 */     tb.add(fontSizeCombo);
/* 2085 */     tb.addSeparator();
/* 2086 */     tb.add(new JLabel(" Renderer: "));
/* 2087 */     tb.add(rendererCombo);
/* 2088 */     tb.addSeparator();
/* 2089 */     tb.add(listenerButton);
/* 2090 */     tb.addSeparator();
/* 2091 */     tb.add(newButton);
/* 2092 */     tb.addSeparator();
/* 2093 */     tb.add(this._pauseButton);
/*      */ 
/* 2095 */     setButtonAlignment(newButton);
/* 2096 */     setButtonAlignment(listenerButton);
/* 2097 */     setButtonAlignment(this._pauseButton);
/*      */ 
/* 2100 */     fontCombo.setMaximumSize(fontCombo.getPreferredSize());
/* 2101 */     fontSizeCombo.setMaximumSize(fontSizeCombo.getPreferredSize());
/*      */ 
/* 2104 */     rendererCombo.setMaximumSize(rendererCombo.getPreferredSize());
/*      */ 
/* 2106 */     return tb;
/*      */   }
/*      */ 
/*      */   private void addRenderersToRendererCombo(JComboBox rendererCombo)
/*      */   {
/* 2114 */     rendererCombo.addItem("emssn00p.util.ems.MarshalJMSMsgToStringImpl");
/* 2115 */     rendererCombo.addItem("emssn00p.util.ems.MarshalJMSMsgToStringJMSStreamImpl");
/*      */ 
/* 2117 */     String env = System.getenv("EMSSNOOP_RENDERERS");
/*      */     try
/*      */     {
/* 2120 */       if (env != null) {
/* 2121 */         String[] result = env.split(";");
/* 2122 */         for (int x = 0; x < result.length; x++)
/* 2123 */           rendererCombo.addItem(result[x]);
/*      */       }
/*      */     }
/*      */     catch (RuntimeException e)
/*      */     {
/*      */     }
/*      */   }
/*      */ 
/*      */   String getLastUsedRenderer()
/*      */   {
/* 2136 */     if (this._lastUsedRenderer == null) {
/* 2137 */       return "emssn00p.util.ems.MarshalJMSMsgToStringImpl";
/*      */     }
/* 2139 */     return this._lastUsedRenderer;
/*      */   }
/*      */ 
/*      */   private void addFontsToFontCombo(JComboBox fontCombo)
/*      */   {
/* 2147 */     Toolkit tk = Toolkit.getDefaultToolkit();
/*      */ 
/* 2150 */     String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
/*      */ 
/* 2152 */     for (int j = 0; j < fonts.length; j++)
/* 2153 */       fontCombo.addItem(fonts[j]);
/*      */   }
/*      */ 
/*      */   private ActionListener getNewButtonActionListener()
/*      */   {
/* 2161 */     return new ActionListener() {
/*      */       public void actionPerformed(ActionEvent e) {
/* 2163 */         RvSnooperGUI.this._table.clearLogRecords();
/* 2164 */         RvSnooperGUI.this._subjectExplorerTree.getExplorerModel().resetAllNodeCounts();
/* 2165 */         RvSnooperGUI.this.updateStatusLabel();
/* 2166 */         RvSnooperGUI.this.clearDetailTextArea();
/* 2167 */         LogRecord.resetSequenceNumber();
/*      */       }
/*      */     };
/*      */   }
/*      */ 
/*      */   private ActionListener getPauseButonActionListener()
/*      */   {
/* 2176 */     return new ActionListener() {
/*      */       public void actionPerformed(ActionEvent e) {
/* 2178 */         if (RvSnooperGUI.this.isPaused())
/* 2179 */           RvSnooperGUI.this.unPauseListeners();
/*      */         else
/* 2181 */           RvSnooperGUI.this.pauseListeners();
/*      */       }
/*      */     };
/*      */   }
/*      */ 
/*      */   private ActionListener getFontComboActionListener()
/*      */   {
/* 2191 */     return new ActionListener() {
/*      */       public void actionPerformed(ActionEvent e) {
/* 2193 */         JComboBox box = (JComboBox)e.getSource();
/* 2194 */         String font = (String)box.getSelectedItem();
/*      */ 
/* 2196 */         RvSnooperGUI.this.setFontName(font);
/*      */       }
/*      */     };
/*      */   }
/*      */ 
/*      */   private void addFontSizesToCombo(JComboBox fontSizeCombo)
/*      */   {
/* 2205 */     fontSizeCombo.addItem("8");
/* 2206 */     fontSizeCombo.addItem("9");
/* 2207 */     fontSizeCombo.addItem("10");
/* 2208 */     fontSizeCombo.addItem("12");
/* 2209 */     fontSizeCombo.addItem("14");
/* 2210 */     fontSizeCombo.addItem("16");
/* 2211 */     fontSizeCombo.addItem("18");
/* 2212 */     fontSizeCombo.addItem("24");
/*      */   }
/*      */ 
/*      */   private ActionListener getFontSizeComboActionListener()
/*      */   {
/* 2219 */     return new ActionListener() {
/*      */       public void actionPerformed(ActionEvent e) {
/* 2221 */         JComboBox box = (JComboBox)e.getSource();
/* 2222 */         String size = (String)box.getSelectedItem();
/* 2223 */         int s = Integer.valueOf(size).intValue();
/*      */ 
/* 2225 */         RvSnooperGUI.this.setFontSizeSilently(s);
/* 2226 */         RvSnooperGUI.this.refreshDetailTextArea();
/* 2227 */         RvSnooperGUI.this.setFontSize(s);
/*      */       }
/*      */     };
/*      */   }
/*      */ 
/*      */   private ActionListener getRendererActionListeener()
/*      */   {
/* 2237 */     return new ActionListener() {
/*      */       public void actionPerformed(ActionEvent e) {
/* 2239 */         JComboBox box = (JComboBox)e.getSource();
/* 2240 */         String rendererClass = (String)box.getSelectedItem();
/*      */         try
/*      */         {
/* 2243 */           RvSnooperGUI.this.updateRenderClass(rendererClass);
/*      */         }
/*      */         catch (Exception ex) {
/* 2246 */           RvSnooperGUI.this._statusLabel.setText("Renderer exception " + ex.getMessage());
/* 2247 */           String str1 = ex.getLocalizedMessage();
/* 2248 */           RvSnooperErrorDialog error = new RvSnooperErrorDialog(RvSnooperGUI.this.getBaseFrame(), "Error creating renderer : " + ex.getMessage());
/*      */ 
/* 2250 */           return;
/*      */         }
/*      */ 
/* 2253 */         RvSnooperGUI.this.setLastUsedRenderer(rendererClass);
/*      */ 
/* 2255 */         RvSnooperGUI.this._statusLabel.setText("Renderer " + rendererClass + " Selected");
/*      */       }
/*      */     };
/*      */   }
/*      */ 
/*      */   private void setButtonAlignment(JButton newButton)
/*      */   {
/* 2264 */     newButton.setAlignmentY(0.5F);
/* 2265 */     newButton.setAlignmentX(0.5F);
/*      */   }
/*      */ 
/*      */   protected void setView(String viewString, LogTable table) {
/* 2269 */     if ("Detailed".equals(viewString)) {
/* 2270 */       table.setDetailedView();
/*      */     } else {
/* 2272 */       String message = viewString + "does not match a supported view.";
/* 2273 */       throw new IllegalArgumentException(message);
/*      */     }
/* 2275 */     this._currentView = viewString;
/*      */   }
/*      */ 
/*      */   protected JComboBox createLogLevelCombo() {
/* 2279 */     JComboBox result = new JComboBox();
/* 2280 */     Iterator levels = getMsgTypes();
/* 2281 */     while (levels.hasNext()) {
/* 2282 */       result.addItem(levels.next());
/*      */     }
/* 2284 */     result.setSelectedItem(this._leastSevereDisplayedMsgType);
/*      */ 
/* 2286 */     result.addActionListener(new ActionListener() {
/*      */       public void actionPerformed(ActionEvent e) {
/* 2288 */         JComboBox box = (JComboBox)e.getSource();
/* 2289 */         EventActionType level = (EventActionType)box.getSelectedItem();
/* 2290 */         RvSnooperGUI.this.setLeastSevereDisplayedLogLevel(level);
/*      */       }
/*      */     });
/* 2293 */     result.setMaximumSize(result.getPreferredSize());
/* 2294 */     return result;
/*      */   }
/*      */ 
/*      */   protected void setLeastSevereDisplayedLogLevel(EventActionType level) {
/* 2298 */     if ((level == null) || (this._leastSevereDisplayedMsgType == level)) {
/* 2299 */       return;
/*      */     }
/* 2301 */     this._leastSevereDisplayedMsgType = level;
/* 2302 */     this._table.getFilteredLogTableModel().refresh();
/* 2303 */     updateStatusLabel();
/*      */   }
/*      */ 
/*      */   protected static void centerFrame(JFrame frame)
/*      */   {
/* 2308 */     Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
/* 2309 */     Dimension comp = frame.getSize();
/*      */ 
/* 2311 */     frame.setLocation((screen.width - comp.width) / 2, (screen.height - comp.height) / 2);
/*      */   }
/*      */ 
/*      */   Rectangle getWindowBounds()
/*      */   {
/* 2317 */     return getBaseFrame().getBounds();
/*      */   }
/*      */ 
/*      */   void setWindowBounds(Rectangle r)
/*      */   {
/* 2322 */     getBaseFrame().setBounds(r);
/*      */   }
/*      */ 
/*      */   protected void requestNewRvListener(EMSParameters p)
/*      */   {
/*      */     RvSnooperErrorDialog error;
/*      */     try
/*      */     {
/* 2334 */       RvSnooperRvTransportInputDialog inputDialog = null;
/* 2335 */       if (p != null) {
/* 2336 */         inputDialog = new RvSnooperRvTransportInputDialog(getBaseFrame(), "Add  Listener", p);
/*      */       }
/*      */       else {
/* 2339 */         inputDialog = new RvSnooperRvTransportInputDialog(getBaseFrame(), "Add  Listener", this._lastUsedRvParameters);
/*      */       }
/*      */ 
/* 2344 */       if (inputDialog.isOK()) {
/* 2345 */         this._lastUsedRvParameters = inputDialog.getRvParameters();
/*      */ 
/* 2347 */         this._lastUsedRvParameters.setDescription("<a href=\"http://emsn00p.sf.net\">EMSSn00p v2.0.3</a> ");
/* 2348 */         EMSController.startListener(this._lastUsedRvParameters, this);
/* 2349 */         updateBanner();
/*      */       }
/*      */ 
/*      */     }
/*      */     catch (JMSException ex)
/*      */     {
/* 2356 */       error = new RvSnooperErrorDialog(getBaseFrame(), "Error creating listener : " + ex.getMessage());
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void updateMRUList()
/*      */   {
/* 2367 */     JMenu menu = this._logMonitorFrame.getJMenuBar().getMenu(0);
/* 2368 */     menu.removeAll();
/* 2369 */     menu.add(createOpenMI());
/* 2370 */     menu.addSeparator();
/* 2371 */     menu.add(createSaveConfigMI());
/* 2372 */     menu.add(createOpenConfigMI());
/* 2373 */     menu.add(createCloseListener());
/* 2374 */     createMRUListnerListMI(menu);
/* 2375 */     menu.addSeparator();
/* 2376 */     menu.add(createExitMI());
/*      */   }
/*      */ 
/*      */   protected void requestCloseListener() {
/* 2380 */     updateBanner();
/*      */   }
/*      */ 
/*      */   protected void requestClose() {
/* 2384 */     setCallSystemExitOnClose(true);
/* 2385 */     closeAfterConfirm();
/*      */   }
/*      */ 
/*      */   protected void requestOpenMRU(ActionEvent e)
/*      */   {
/* 2394 */     String file = e.getActionCommand();
/* 2395 */     StringTokenizer st = new StringTokenizer(file);
/* 2396 */     String num = st.nextToken().trim();
/* 2397 */     file = st.nextToken("\n");
/*      */     RvSnooperErrorDialog error;
/*      */     try {
/* 2400 */       int index = Integer.parseInt(num) - 1;
/*      */ 
/* 2402 */       InputStream in = this._mruListnerManager.getInputStream(index);
/*      */ 
/* 2406 */       this._mruListnerManager.moveToTop(index);
/* 2407 */       updateMRUList();
/*      */     }
/*      */     catch (Exception me) {
/* 2410 */       error = new RvSnooperErrorDialog(getBaseFrame(), "Unable to load file " + file);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void requestExit()
/*      */   {
/* 2418 */     setCallSystemExitOnClose(true);
/* 2419 */     closeAfterConfirm();
/*      */   }
/*      */ 
/*      */   protected void closeAfterConfirm() {
/* 2423 */     StringBuffer message = new StringBuffer();
/*      */ 
/* 2425 */     message.append("Are you sure you want to exit?\n");
/*      */ 
/* 2427 */     String title = "Are you sure you want to exit?";
/*      */ 
/* 2429 */     int value = JOptionPane.showConfirmDialog(this._logMonitorFrame, message.toString(), "Are you sure you want to exit?", 2, 3, null);
/*      */ 
/* 2438 */     if (value == 0)
/* 2439 */       dispose();
/*      */   }
/*      */ 
/*      */   protected Iterator getMsgTypes()
/*      */   {
/* 2444 */     return this._levels.iterator();
/*      */   }
/*      */ 
/*      */   protected Iterator getLogTableColumns() {
/* 2448 */     return this._columns.iterator();
/*      */   }
/*      */ 
/*      */   Iterator getSubscriptions() {
/* 2452 */     return EMSController.getTransports().iterator();
/*      */   }
/*      */ 
/*      */   protected static boolean loadLogFile(File file)
/*      */   {
/* 2463 */     boolean ok = true;
/*      */ 
/* 2466 */     return ok;
/*      */   }
/*      */ 
/*      */   protected static boolean loadLogFile(URL url)
/*      */   {
/* 2477 */     boolean ok = true;
/*      */ 
/* 2479 */     return ok;
/*      */   }
/*      */ 
/*      */   int getFontSize() {
/* 2483 */     return this._fontSize;
/*      */   }
/*      */ 
/*      */   String getFontName() {
/* 2487 */     return this._fontName;
/*      */   }
/*      */ 
/*      */   void setFontName(String fontName) {
/* 2491 */     this._fontName = fontName;
/* 2492 */     this._table.setFont(new Font(fontName, 0, getFontSize()));
/* 2493 */     changeStringCombo(this._fontNameCombo, fontName);
/*      */   }
/*      */ 
/*      */   protected void setSplitPaneVertical(JSplitPane _splitPaneVertical)
/*      */   {
/* 2498 */     this._splitPaneVertical = _splitPaneVertical;
/*      */   }
/*      */ 
/*      */   int getSplitPaneVerticalPos() {
/* 2502 */     return this._splitPaneVertical.getDividerLocation();
/*      */   }
/*      */ 
/*      */   void setSplitPaneVerticalPos(int location) {
/* 2506 */     this._splitPaneVertical.setDividerLocation(location);
/*      */   }
/*      */ 
/*      */   int getSplitPaneTableViewerPos() {
/* 2510 */     return this._splitPaneTableViewer.getDividerLocation();
/*      */   }
/*      */ 
/*      */   void setSplitPaneTableViewer(JSplitPane _splitPaneTableViewer)
/*      */   {
/* 2515 */     this._splitPaneTableViewer = _splitPaneTableViewer;
/*      */   }
/*      */ 
/*      */   void setSplitPaneTableViewerPos(int location)
/*      */   {
/* 2520 */     this._splitPaneTableViewer.setDividerLocation(location);
/*      */   }
/*      */ 
/*      */   public void onMessage(Message msg)
/*      */   {
/* 2564 */     if (isPaused()) {
/* 2565 */       return;
/*      */     }
/*      */ 
/* 2568 */     LogRecord r = LogRecordFactory.createLogRecordFromJMSMessage(this._statusLabel, msg);
/*      */ 
/* 2573 */     addMessage(r);
/*      */   }
/*      */ 
/*      */   void setLastUsedRenderer(String rendererClass)
/*      */   {
/* 2582 */     if (rendererClass != null)
/* 2583 */       this._lastUsedRenderer = rendererClass;
/*      */   }
/*      */ 
/*      */   private void updateRenderClass(String rendererClass)
/*      */     throws ClassNotFoundException, InstantiationException, IllegalAccessException
/*      */   {
/* 2594 */     Class c = Class.forName(rendererClass);
/* 2595 */     Object o = c.newInstance();
/* 2596 */     if ((o instanceof IMarshalJMSToString))
/* 2597 */       _marshalImpl.setImpl((IMarshalJMSToString)o);
/*      */     else
/* 2599 */       this._statusLabel.setText("Class " + rendererClass + " does not implement IMarshalJMSToString");
/*      */   }
/*      */ 
/*      */   private class AddLogRecordRunnable
/*      */     implements Runnable
/*      */   {
/*      */     private final LogRecord lr;
/*      */ 
/*      */     public AddLogRecordRunnable(LogRecord lr)
/*      */     {
/* 2549 */       this.lr = lr;
/*      */     }
/*      */ 
/*      */     public void run() {
/* 2553 */       RvSnooperGUI.this._subjectExplorerTree.getExplorerModel().addLogRecord(this.lr);
/* 2554 */       RvSnooperGUI.this._table.getFilteredLogTableModel().addLogRecord(this.lr);
/* 2555 */       RvSnooperGUI.this.updateStatusLabel();
/*      */     }
/*      */   }
/*      */ 
/*      */   class LogBrokerMonitorWindowAdaptor extends WindowAdapter
/*      */   {
/*      */     protected RvSnooperGUI _monitor;
/*      */ 
/*      */     public LogBrokerMonitorWindowAdaptor(RvSnooperGUI monitor)
/*      */     {
/* 2537 */       this._monitor = monitor;
/*      */     }
/*      */ 
/*      */     public void windowClosing(WindowEvent ev) {
/* 2541 */       this._monitor.requestClose();
/*      */     }
/*      */   }
/*      */ }

/* Location:           /Users/tnarik/Desktop/emssn00p.jar
 * Qualified Name:     emssn00p.viewer.RvSnooperGUI
 * JD-Core Version:    0.6.1
 */