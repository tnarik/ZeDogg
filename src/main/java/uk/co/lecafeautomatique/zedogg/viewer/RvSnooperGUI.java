package uk.co.lecafeautomatique.zedogg.viewer;

import uk.co.lecafeautomatique.zedogg.EventActionType;
import uk.co.lecafeautomatique.zedogg.LogRecord;
import uk.co.lecafeautomatique.zedogg.LogRecordFilter;
import uk.co.lecafeautomatique.zedogg.util.BrowserLauncher;
import uk.co.lecafeautomatique.zedogg.util.DateFormatManager;
import uk.co.lecafeautomatique.zedogg.util.ems.EMSController;
import uk.co.lecafeautomatique.zedogg.util.ems.EMSParameters;
import uk.co.lecafeautomatique.zedogg.util.ems.IMarshalJMSToString;
import uk.co.lecafeautomatique.zedogg.util.ems.LogRecordFactory;
import uk.co.lecafeautomatique.zedogg.util.ems.MarshalJMSMsgToStringProxyImpl;
import uk.co.lecafeautomatique.zedogg.viewer.categoryexplorer.CategoryExplorerModel;
import uk.co.lecafeautomatique.zedogg.viewer.categoryexplorer.CategoryExplorerTree;
import uk.co.lecafeautomatique.zedogg.viewer.categoryexplorer.CategoryPath;
import uk.co.lecafeautomatique.zedogg.viewer.configure.MRUListnerManager;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.TableModel;

public class RvSnooperGUI
  implements MessageListener
{
  public static final String DETAILED_VIEW = "Detailed";
  public static final String VERSION = "EMSSn00p v2.0.3";
  public static final String URL = "http://emsn00p.sf.net";
  public static final MarshalJMSMsgToStringProxyImpl _marshalImpl = new MarshalJMSMsgToStringProxyImpl();

  protected String _name = null;
  protected JFrame _logMonitorFrame;
  protected int _logMonitorFrameWidth = 550;
  protected int _logMonitorFrameHeight = 500;
  protected LogTable _table;
  protected CategoryExplorerTree _subjectExplorerTree;
  protected String _searchText;
  protected EventActionType _leastSevereDisplayedMsgType = EventActionType.UNKNOWN;
  protected JScrollPane _logTableScrollPane;
  protected JLabel _statusLabel;
  protected JComboBox _fontSizeCombo;
  protected JComboBox _fontNameCombo;
  protected JButton _pauseButton = null;

  private int _fontSize = 10;
  private String _fontName = "Dialog";
  protected String _currentView = "Detailed";

  protected boolean _loadSystemFonts = false;
  protected boolean _trackTableScrollPane = true;
  protected boolean _callSystemExitOnClose = true;
  protected List<Object> _displayedLogBrokerProperties = new Vector();

  protected Map<EventActionType, JCheckBoxMenuItem> _logLevelMenuItems = new HashMap();
  protected Map<LogTableColumn, JCheckBoxMenuItem> _logTableColumnMenuItems = new HashMap();
  protected JSplitPane _splitPaneVertical;
  protected JSplitPane _splitPaneTableViewer;
  protected List _levels = null;
  protected List _columns = null;
  protected boolean _isDisposed = false;

  protected ConfigurationManager _configurationManager = null;
  protected MRUListnerManager _mruListnerManager = null;

  protected boolean _displaySystemMsgs = true;
  protected boolean _displayIMMsgs = true;
  protected EMSParameters _lastUsedRvParameters = new EMSParameters();
  protected boolean _isPaused = false;
  protected ClassLoader _cl = null;

  protected static String _JMSCorrelationIDTextFilter = "";
  protected static String _subjectTextFilter = "";
  protected static boolean useMtrackingInfo = true;
  private String _ConnHostnameTextFilter;
  private JComboBox _rendererCombo;
  protected String _lastUsedRenderer;

  public RvSnooperGUI(List MsgTypes, Set listeners, String name)
  {
    this._levels = MsgTypes;
    this._columns = LogTableColumn.getLogTableColumns();
    this._columns = LogTableColumn.getLogTableColumns();
    this._name = name;

    initComponents();

    this._logMonitorFrame.addWindowListener(new LogBrokerMonitorWindowAdaptor(this));

    startListeners(listeners);
  }

  protected void startListeners(Set listeners)
  {
    Iterator itrl = listeners.iterator();
    RvSnooperErrorDialog error;
    while (itrl.hasNext()) {
      try
      {
        EMSParameters p = (EMSParameters)itrl.next();

        p.setDescription(" <a href=\""+URL+"\">"+VERSION+"</a> ");

        EMSController.startListener(p, this);

        this._lastUsedRvParameters = p;
      }
      catch (ClassCastException ex) {
        error = new RvSnooperErrorDialog(getBaseFrame(), ex.getMessage());
      }
      catch (JMSException ex)
      {
        RvSnooperErrorDialog error1;
        error1 = new RvSnooperErrorDialog(getBaseFrame(), "EMS Exception" + ex.getMessage());
      }

    }

    updateBanner();
  }

  public void onError(Object tibrvObject, int errorCode, String message, Throwable throwable)
  {
    RvSnooperErrorDialog error = new RvSnooperErrorDialog(getBaseFrame(), "A System error occured " + message);
  }

  public void show(final int delay)
  {
    if (this._logMonitorFrame.isVisible()) {
      return;
    }

    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        Thread.yield();
        RvSnooperGUI.pause(delay);
        RvSnooperGUI.this._logMonitorFrame.setVisible(true);
        RvSnooperGUI.changeStringCombo(RvSnooperGUI.this._fontNameCombo, RvSnooperGUI.this._fontName);
        RvSnooperGUI.changeIntCombo(RvSnooperGUI.this._fontSizeCombo, RvSnooperGUI.this._fontSize);
        RvSnooperGUI.changeStringCombo(RvSnooperGUI.this._rendererCombo, RvSnooperGUI.this.getLastUsedRenderer());
      }
    });
  }

  public void show() {
    show(0);
    updateBanner();
  }

  public void updateBanner()
  {
    String sBanner = null;
    if (this._name != null) {
      sBanner = this._name + " " + EMSController.getTransports().toString();
    } else {
      sBanner = EMSController.getTransports().toString();
    }

    sBanner = sBanner + " " + VERSION;

    setTitle(sBanner);
  }

  public void dispose()
  {
    this._logMonitorFrame.dispose();
    this._isDisposed = true;
    try
    {
      EMSController.shutdownAll();
    }
    catch (Exception ex) {
      System.err.println(ex.getMessage());
    }

    if (this._callSystemExitOnClose == true)
      System.exit(0);
  }

  public void hide()
  {
    this._logMonitorFrame.setVisible(false);
  }

  DateFormatManager getDateFormatManager()
  {
    return this._table.getDateFormatManager();
  }

  void setDateFormatManager(DateFormatManager dfm)
  {
    this._table.setDateFormatManager(dfm);
  }

  public String getDateFormat() {
    DateFormatManager dfm = getDateFormatManager();
    return dfm.getPattern();
  }

  public String setDateFormat(String pattern) {
    DateFormatManager dfm = new DateFormatManager(pattern);
    setDateFormatManager(dfm);
    return pattern;
  }

  public boolean getCallSystemExitOnClose()
  {
    return this._callSystemExitOnClose;
  }

  public void setCallSystemExitOnClose(boolean callSystemExitOnClose)
  {
    this._callSystemExitOnClose = callSystemExitOnClose;
  }

  public void addMessage(LogRecord lr)
  {
    if (this._isDisposed == true)
    {
      return;
    }

    SwingUtilities.invokeLater(new AddLogRecordRunnable(lr));
  }

  public void setMaxNumberOfLogRecords(int maxNumberOfLogRecords) {
    this._table.getFilteredLogTableModel().setMaxNumberOfLogRecords(maxNumberOfLogRecords);
  }

  public JFrame getBaseFrame() {
    return this._logMonitorFrame;
  }

  public void setTitle(String title) {
    this._logMonitorFrame.setTitle(title);
  }

  public void setFrameSize(int width, int height) {
    Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
    if ((0 < width) && (width < screen.width)) {
      this._logMonitorFrameWidth = width;
    }
    if ((0 < height) && (height < screen.height)) {
      this._logMonitorFrameHeight = height;
    }
    updateFrameSize();
  }

  void setFontSize(int fontSize) {
    changeIntCombo(this._fontSizeCombo, fontSize);
  }

  public void addDisplayedProperty(Object messageLine)
  {
    this._displayedLogBrokerProperties.add(messageLine);
  }

  public Map getLogLevelMenuItems() {
    return this._logLevelMenuItems;
  }

  public Map getLogTableColumnMenuItems() {
    return this._logTableColumnMenuItems;
  }

  public JCheckBoxMenuItem getTableColumnMenuItem(LogTableColumn column) {
    return getLogTableColumnMenuItem(column);
  }

  public CategoryExplorerTree getCategoryExplorerTree() {
    return this._subjectExplorerTree;
  }

  protected boolean isPaused()
  {
    return this._isPaused;
  }

  protected void pauseListeners()
  {
    try {
      EMSController.pauseAll();
    } catch (JMSException e) {
      this._statusLabel.setText("Pause all listeners failed " + e.getMessage());
      return;
    }
    this._isPaused = true;
    this._pauseButton.setText("Continue all listeners");
    this._pauseButton.setToolTipText("Unpause listeners");
    this._statusLabel.setText("All listeners are now paused");

    ImageIcon pbIcon = null;
    URL pbIconURL = this._cl.getResource("restart.gif");

    if (pbIconURL != null) {
      pbIcon = new ImageIcon(pbIconURL);
    }

    if (pbIcon != null)
      this._pauseButton.setIcon(pbIcon);
  }

  protected void unPauseListeners()
  {
    try
    {
      EMSController.resumeAll();
    } catch (JMSException e) {
      this._statusLabel.setText("Resume all listeners failed " + e.getMessage());
      return;
    }
    this._isPaused = false;

    this._pauseButton.setText("Pause all listeners");
    this._pauseButton.setToolTipText("Put listeners on hold");
    this._statusLabel.setText("All listeners are now active");

    ImageIcon pbIcon = null;
    URL pbIconURL = this._cl.getResource("pauseon.gif");

    if (pbIconURL != null) {
      pbIcon = new ImageIcon(pbIconURL);
    }

    if (pbIcon != null)
      this._pauseButton.setIcon(pbIcon);
  }

  protected void setSearchText(String text)
  {
    this._searchText = text;
  }

  protected void findSearchText()
  {
    String text = this._searchText;
    if ((text == null) || (text.length() == 0)) {
      return;
    }
    int startRow = getFirstSelectedRow();
    int foundRow = findRecord(startRow, text, this._table.getFilteredLogTableModel().getFilteredRecords());

    selectRow(foundRow);
  }

  protected int getFirstSelectedRow() {
    return this._table.getSelectionModel().getMinSelectionIndex();
  }

  protected void selectRow(int foundRow) {
    if (foundRow == -1) {
      String message = this._searchText + " not found.";
      JOptionPane.showMessageDialog(this._logMonitorFrame, message, "Text not found", 1);

      return;
    }
    SwingUtils.selectRow(foundRow, this._table, this._logTableScrollPane);
  }

  protected int findRecord(int startRow, String searchText, List records)
  {
    if (startRow < 0)
      startRow = 0;
    else {
      startRow++;
    }
    int len = records.size();

    for (int i = startRow; i < len; i++) {
      if (matches((LogRecord)records.get(i), searchText)) {
        return i;
      }
    }

    len = startRow;
    for (int i = 0; i < len; i++) {
      if (matches((LogRecord)records.get(i), searchText)) {
        return i;
      }
    }

    return -1;
  }

  protected static boolean matches(LogRecord record, String text)
  {
    String message = record.toString(_marshalImpl);
    if (((message == null) && (_JMSCorrelationIDTextFilter == null)) || (text == null)) {
      return false;
    }

    if ((message.toLowerCase().indexOf(text.toLowerCase()) == -1) && (_JMSCorrelationIDTextFilter.indexOf(text.toLowerCase()) == -1))
    {
      return false;
    }

    return true;
  }

  protected static void refresh(JTextArea textArea)
  {
    String text = textArea.getText();
    textArea.setText("");
    textArea.setText(text);
  }

  protected void refreshDetailTextArea() {
    refresh(this._table._detailTextArea);
  }

  protected void clearDetailTextArea() {
    this._table._detailTextArea.setText("");
  }

  public static int changeIntCombo(JComboBox box, int requestedSize)
  {
    int len = box.getItemCount();

    Object selectedObject = box.getItemAt(0);
    int selectedValue = Integer.parseInt(String.valueOf(selectedObject));
    for (int i = 0; i < len; i++) {
      Object currentObject = box.getItemAt(i);
      int currentValue = Integer.parseInt(String.valueOf(currentObject));
      if ((selectedValue < currentValue) && (currentValue <= requestedSize)) {
        selectedValue = currentValue;
        selectedObject = currentObject;
      }
    }
    box.setSelectedItem(selectedObject);
    return selectedValue;
  }

  public static String changeStringCombo(JComboBox box, String requestedName)
  {
    int len = box.getItemCount();

    String currentValue = null;

    Object selectedObject = box.getItemAt(0);
    for (int i = 0; i < len; i++) {
      Object currentObject = box.getItemAt(i);
      currentValue = String.valueOf(currentObject);
      if (currentValue.compareToIgnoreCase(requestedName) == 0) {
        selectedObject = currentObject;
      }
    }
    box.setSelectedItem(selectedObject);
    return currentValue;
  }

  protected void setFontSizeSilently(int fontSize)
  {
    setFontSize(fontSize);
    setFontSize(this._table._detailTextArea, fontSize);
    selectRow(0);
    setFontSize(this._table, fontSize);
  }

  protected static void setFontSize(Component component, int fontSize) {
    Font oldFont = component.getFont();
    Font newFont = new Font(oldFont.getFontName(), oldFont.getStyle(), fontSize);

    component.setFont(newFont);
  }

  protected void updateFrameSize() {
    this._logMonitorFrame.setSize(this._logMonitorFrameWidth, this._logMonitorFrameHeight);
    centerFrame(this._logMonitorFrame);
  }

  protected static void pause(int millis) {
    try {
      Thread.sleep(millis);
    }
    catch (InterruptedException e)
    {
    }
  }

  protected void initComponents()
  {
    this._logMonitorFrame = new JFrame(this._name);

    this._logMonitorFrame.setDefaultCloseOperation(0);

    String resource = "/eye.gif";

    URL iconURL = getClass().getResource("/eye.gif");

    if (iconURL != null) {
      this._logMonitorFrame.setIconImage(new ImageIcon(iconURL).getImage());
    }
    updateFrameSize();

    JTextArea detailTA = createDetailTextArea();
    JScrollPane detailTAScrollPane = new JScrollPane(detailTA);
    this._table = new LogTable(detailTA, _marshalImpl);
    setView(this._currentView, this._table);
    this._table.setFont(new Font(getFontName(), 0, getFontSize()));
    this._logTableScrollPane = new JScrollPane(this._table);

    if (this._trackTableScrollPane) {
      this._logTableScrollPane.getVerticalScrollBar().addAdjustmentListener(new TrackingAdjustmentListener());
    }

    JSplitPane tableViewerSplitPane = new JSplitPane();
    tableViewerSplitPane.setOneTouchExpandable(true);
    tableViewerSplitPane.setOrientation(0);
    tableViewerSplitPane.setLeftComponent(this._logTableScrollPane);
    tableViewerSplitPane.setRightComponent(detailTAScrollPane);

    tableViewerSplitPane.setDividerLocation(350);

    setSplitPaneTableViewer(tableViewerSplitPane);

    this._subjectExplorerTree = new CategoryExplorerTree();

    this._table.getFilteredLogTableModel().setLogRecordFilter(createLogRecordFilter());

    JScrollPane categoryExplorerTreeScrollPane = new JScrollPane(this._subjectExplorerTree);

    categoryExplorerTreeScrollPane.setPreferredSize(new Dimension(130, 400));

    JSplitPane splitPane = new JSplitPane();
    splitPane.setOneTouchExpandable(true);
    splitPane.setRightComponent(tableViewerSplitPane);
    splitPane.setLeftComponent(categoryExplorerTreeScrollPane);

    splitPane.setDividerLocation(130);
    setSplitPaneVertical(splitPane);

    this._logMonitorFrame.getRootPane().setJMenuBar(createMenuBar());
    this._logMonitorFrame.getContentPane().add(splitPane, "Center");
    this._logMonitorFrame.getContentPane().add(createToolBar(), "North");
    this._logMonitorFrame.getContentPane().add(createStatusArea(), "South");

    makeLogTableListenToCategoryExplorer();
    addTableModelProperties();

    final RvSnooperGUI gui = this;
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        RvSnooperGUI.this._configurationManager = new ConfigurationManager(gui, RvSnooperGUI.this._table);
        RvSnooperGUI.this.unPauseListeners();
        String ex = "";
        try {
          RvSnooperGUI.this.updateRenderClass(RvSnooperGUI.this.getLastUsedRenderer());
        } catch (Exception e) {
          e.printStackTrace();
          ex = " " + e.getMessage();
        }

        RvSnooperGUI.this._statusLabel.setText("Started @ " + new Date() + ex);
      }
    });
  }

  protected LogRecordFilter createLogRecordFilter() {
    LogRecordFilter result = new LogRecordFilter() {
      public boolean passes(LogRecord record) {
        CategoryPath path = new CategoryPath(record.getJMSDestination());
        return (RvSnooperGUI.this.getMenuItem(record.getType()).isSelected()) && (RvSnooperGUI.this._subjectExplorerTree.getExplorerModel().isCategoryPathActive(path));
      }
    };
    return result;
  }

  protected void updateStatusLabel()
  {
    StringBuffer sb = new StringBuffer(100);
    getRecordsDisplayedMessage(sb);
    getFileEncodingMessage(sb);

    this._statusLabel.setText(sb.toString());
  }

  protected void getRecordsDisplayedMessage(StringBuffer sb) {
    FilteredLogTableModel model = this._table.getFilteredLogTableModel();
    getStatusText(model.getRowCount(), model.getTotalRowCount(), sb);
  }

  protected void getFileEncodingMessage(StringBuffer sb) {
    sb.append("  Encoding:");
    sb.append(System.getProperty("file.encoding"));
  }

  protected void addTableModelProperties() {
    final FilteredLogTableModel model = this._table.getFilteredLogTableModel();

    addDisplayedProperty(new Object() {
      public String toString() {
        StringBuffer sb = new StringBuffer(40);
        RvSnooperGUI.this.getRecordsDisplayedMessage(sb);
        return sb.toString();
      }
    });
    addDisplayedProperty(new Object() {
      public String toString() {
        return "Maximum number of displayed LogRecords: " + model._maxNumberOfLogRecords;
      }
    });
  }

  protected static void getStatusText(int displayedRows, int totalRows, StringBuffer sb)
  {
    sb.append("Displaying: ");
    sb.append(displayedRows);
    sb.append(" records out of a total of: ");
    sb.append(totalRows);
    sb.append(" records. ");
    sb.append(totalRows - displayedRows);
    sb.append(" are filtered.");
  }

  protected void makeLogTableListenToCategoryExplorer()
  {
    ActionListener listener = new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        RvSnooperGUI.this._table.getFilteredLogTableModel().refresh();
        RvSnooperGUI.this.updateStatusLabel();
      }
    };
    this._subjectExplorerTree.getExplorerModel().addActionListener(listener);
  }

  protected JPanel createStatusArea() {
    JPanel statusArea = new JPanel();
    JLabel status = new JLabel("No records to display.");

    this._statusLabel = status;
    status.setHorizontalAlignment(2);

    statusArea.setBorder(BorderFactory.createEtchedBorder());
    statusArea.setLayout(new FlowLayout(0, 0, 0));
    statusArea.add(status);

    return statusArea;
  }

  protected static JTextArea createDetailTextArea() {
    JTextArea detailTA = new JTextArea();
    detailTA.setFont(new Font("Monospaced", 0, 14));
    detailTA.setTabSize(3);
    detailTA.setLineWrap(true);
    detailTA.setWrapStyleWord(false);
    return detailTA;
  }

  protected JMenuBar createMenuBar() {
    JMenuBar menuBar = new JMenuBar();
    menuBar.add(createFileMenu());
    menuBar.add(createEditMenu());

    menuBar.add(createMsgTypeMenu());
    menuBar.add(createViewMenu());
    menuBar.add(createConfigureMenu());
    menuBar.add(createHelpMenu());

    return menuBar;
  }

  protected JMenuItem createSaveSelectedAsTextFileMenuItem()
  {
    JMenuItem result = new JMenuItem("Save selected as txt");
    result.setMnemonic('r');

    result.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e) {
        ListSelectionModel lsm = RvSnooperGUI.this._table.getSelectionModel();
        if (lsm.isSelectionEmpty()) {
          RvSnooperGUI.this._statusLabel.setText("No rows are selected.");
        } else {
          int selectedRow = lsm.getMinSelectionIndex();

          FilteredLogTableModel ftm = RvSnooperGUI.this._table.getFilteredLogTableModel();

          final String sMsg = (String)RvSnooperGUI.this._table.getModel().getValueAt(selectedRow, RvSnooperGUI.this._table.getMsgColumnID());
          final String sSubject = (String)RvSnooperGUI.this._table.getModel().getValueAt(selectedRow, RvSnooperGUI.this._table.getSubjectColumnID());

          SwingUtilities.invokeLater(new Runnable() {
            public void run() {
              RvSnooperFileHandler.saveMsgAsTextFile(sSubject, sMsg, VERSION+" "+URL, RvSnooperGUI.this.getBaseFrame(), RvSnooperGUI.this._statusLabel);
            }
          });
        }
      }
    });
    result.setEnabled(false);

    return result;
  }

  protected JMenu createMsgTypeMenu()
  {
    JMenu result = new JMenu("Event Action");
    result.setMnemonic('a');
    Iterator imsgtypes = getMsgTypes();
    while (imsgtypes.hasNext()) {
      result.add(getMenuItem((EventActionType)imsgtypes.next()));
    }

    result.addSeparator();
    result.add(createAllMsgTypesMenuItem());
    result.add(createNoMsgTypesMenuItem());
    result.addSeparator();
    result.add(createLogLevelColorMenu());
    result.add(createResetLogLevelColorMenuItem());

    return result;
  }

  protected JMenuItem createAllMsgTypesMenuItem() {
    JMenuItem result = new JMenuItem("Show all Event Actions");
    result.setMnemonic('a');
    result.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        RvSnooperGUI.this.selectAllMsgTypes(true);
        RvSnooperGUI.this._table.getFilteredLogTableModel().refresh();
        RvSnooperGUI.this.updateStatusLabel();
      }
    });
    return result;
  }

  protected JMenuItem createNoMsgTypesMenuItem() {
    JMenuItem result = new JMenuItem("Hide all Event Actions");
    result.setMnemonic('h');
    result.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        RvSnooperGUI.this.selectAllMsgTypes(false);
        RvSnooperGUI.this._table.getFilteredLogTableModel().refresh();
        RvSnooperGUI.this.updateStatusLabel();
      }
    });
    return result;
  }

  protected JMenu createLogLevelColorMenu() {
    JMenu colorMenu = new JMenu("Configure Event Actions Colors");
    colorMenu.setMnemonic('c');
    Iterator levels = getMsgTypes();
    while (levels.hasNext()) {
      colorMenu.add(createSubMenuItem((EventActionType)levels.next()));
    }

    return colorMenu;
  }

  protected JMenuItem createResetLogLevelColorMenuItem() {
    JMenuItem result = new JMenuItem("Reset Event Actions Colors");
    result.setMnemonic('r');
    result.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e) {
        EventActionType.resetLogLevelColorMap();

        RvSnooperGUI.this._table.getFilteredLogTableModel().refresh();
      }
    });
    return result;
  }

  protected void selectAllMsgTypes(boolean selected) {
    Iterator levels = getMsgTypes();
    while (levels.hasNext())
      getMenuItem((EventActionType)levels.next()).setSelected(selected);
  }

  protected JCheckBoxMenuItem getMenuItem(EventActionType level)
  {
    JCheckBoxMenuItem result = (JCheckBoxMenuItem)this._logLevelMenuItems.get(level);
    if (result == null) {
      result = createMenuItem(level);
      this._logLevelMenuItems.put(level, result);
    }

    return result;
  }

  protected JMenuItem createSubMenuItem(EventActionType level) {
    final JMenuItem result = new JMenuItem(level.toString());
    final EventActionType logLevel = level;
    result.setMnemonic(level.toString().charAt(0));
    result.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        RvSnooperGUI.this.showLogLevelColorChangeDialog(result, logLevel);
      }
    });
    return result;
  }

  protected void showLogLevelColorChangeDialog(JMenuItem result, EventActionType level)
  {
    JMenuItem menuItem = result;
    Color newColor = JColorChooser.showDialog(this._logMonitorFrame, "Choose Event Actions Color", result.getForeground());

    if (newColor != null)
    {
      level.setLogLevelColorMap(level, newColor);
      this._table.getFilteredLogTableModel().refresh();
    }
  }

  protected JCheckBoxMenuItem createMenuItem(EventActionType level)
  {
    JCheckBoxMenuItem result = new JCheckBoxMenuItem(level.toString());
    result.setSelected(true);
    result.setMnemonic(level.toString().charAt(0));
    result.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        RvSnooperGUI.this._table.getFilteredLogTableModel().refresh();
        RvSnooperGUI.this.updateStatusLabel();
      }
    });
    return result;
  }

  protected JMenu createViewMenu()
  {
    JMenu result = new JMenu("View");
    result.setMnemonic('v');
    Iterator columns = getLogTableColumns();
    while (columns.hasNext()) {
      result.add(getLogTableColumnMenuItem((LogTableColumn)columns.next()));
    }

    result.addSeparator();
    result.add(createAllLogTableColumnsMenuItem());
    result.add(createNoLogTableColumnsMenuItem());
    return result;
  }

  protected JCheckBoxMenuItem getLogTableColumnMenuItem(LogTableColumn column) {
    JCheckBoxMenuItem result = (JCheckBoxMenuItem)this._logTableColumnMenuItems.get(column);
    if (result == null) {
      result = createLogTableColumnMenuItem(column);
      this._logTableColumnMenuItems.put(column, result);
    }

    return result;
  }

  protected JCheckBoxMenuItem createLogTableColumnMenuItem(LogTableColumn column) {
    JCheckBoxMenuItem result = new JCheckBoxMenuItem(column.toString());

    result.setSelected(true);
    result.setMnemonic(column.toString().charAt(0));
    result.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e) {
        List selectedColumns = RvSnooperGUI.this.updateView();
        RvSnooperGUI.this._table.setView(selectedColumns);
      }
    });
    return result;
  }

  protected List<LogTableColumn> updateView() {
    ArrayList updatedList = new ArrayList();
    Iterator columnIterator = this._columns.iterator();
    while (columnIterator.hasNext()) {
      LogTableColumn column = (LogTableColumn)columnIterator.next();
      JCheckBoxMenuItem result = getLogTableColumnMenuItem(column);

      if (result.isSelected()) {
        updatedList.add(column);
      }
    }

    return updatedList;
  }

  protected JMenuItem createAllLogTableColumnsMenuItem() {
    JMenuItem result = new JMenuItem("Show all Columns");
    result.setMnemonic('s');
    result.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        RvSnooperGUI.this.selectAllLogTableColumns(true);

        List selectedColumns = RvSnooperGUI.this.updateView();
        RvSnooperGUI.this._table.setView(selectedColumns);
      }
    });
    return result;
  }

  protected JMenuItem createNoLogTableColumnsMenuItem() {
    JMenuItem result = new JMenuItem("Hide all Columns");
    result.setMnemonic('h');
    result.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        RvSnooperGUI.this.selectAllLogTableColumns(false);

        List selectedColumns = RvSnooperGUI.this.updateView();
        RvSnooperGUI.this._table.setView(selectedColumns);
      }
    });
    return result;
  }

  protected void selectAllLogTableColumns(boolean selected) {
    Iterator columns = getLogTableColumns();
    while (columns.hasNext())
      getLogTableColumnMenuItem((LogTableColumn)columns.next()).setSelected(selected);
  }

  protected JMenu createFileMenu()
  {
    JMenu fileMenu = new JMenu("File");
    fileMenu.setMnemonic('f');

    fileMenu.add(createOpenMI());
    fileMenu.addSeparator();
    fileMenu.add(createSaveHTML());
    fileMenu.add(createSaveSelectedAsTextFileMenuItem());
    fileMenu.add(createSaveAsTextMI());
    fileMenu.addSeparator();
    fileMenu.add(createCloseListener());
    fileMenu.addSeparator();
    fileMenu.add(createFileSaveConfigMI());
    fileMenu.add(createFileLoadConfigMI());

    fileMenu.addSeparator();
    fileMenu.add(createExitMI());
    return fileMenu;
  }

  protected JMenuItem createSaveHTML()
  {
    JMenuItem result = new JMenuItem("Save Table to HTML file");
    result.setMnemonic('h');
    result.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        RvSnooperFileHandler.saveTableToHtml(VERSION, URL, RvSnooperGUI.this.getBaseFrame(), RvSnooperGUI.this._statusLabel, RvSnooperGUI.this._table);
      }
    });
    return result;
  }

  protected JMenuItem createFileSaveConfigMI()
  {
    JMenuItem result = new JMenuItem("Save configuration to file");
    result.setMnemonic('c');
    result.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        RvSnooperErrorDialog error;
        try { FileDialog fd = new FileDialog(RvSnooperGUI.this.getBaseFrame(), "Save config File", 1);
          fd.setDirectory(RvSnooperGUI.this._configurationManager.getFilename());
          fd.setFile("*.rs0");
          fd.setVisible(true);

          String fileName = fd.getDirectory() + fd.getFile();
          RvSnooperGUI.this._configurationManager.setFilename(fileName);
          RvSnooperGUI.this._configurationManager.save();
          RvSnooperGUI.this._statusLabel.setText("Saved configuration in " + fileName);
        } catch (Exception ex) {
          error = new RvSnooperErrorDialog(RvSnooperGUI.this.getBaseFrame(), ex.getMessage());
        }
      }
    });
    return result;
  }

  protected JMenuItem createFileLoadConfigMI()
  {
    JMenuItem result = new JMenuItem("Load configuration from file");
    result.setMnemonic('c');
    result.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        RvSnooperErrorDialog error;
        try { FileDialog fd = new FileDialog(RvSnooperGUI.this.getBaseFrame(), "Open config File", 0);
          fd.setDirectory(RvSnooperGUI.this._configurationManager.getFilename());
          fd.setFile("*.rs0");
          fd.setVisible(true);

          String fileName = fd.getDirectory() + fd.getFile();
          RvSnooperGUI.this._configurationManager.setFilename(fileName);
          RvSnooperGUI.this._configurationManager.load();
          RvSnooperGUI.this._statusLabel.setText("Loaded configuration from " + fileName);
        } catch (Exception ex) {
          error = new RvSnooperErrorDialog(RvSnooperGUI.this.getBaseFrame(), ex.getMessage());
        }
      }
    });
    return result;
  }

  protected JMenuItem createSaveAsTextMI()
  {
    JMenuItem result = new JMenuItem("Save Table to text file");
    result.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        RvSnooperErrorDialog error;
        try { RvSnooperFileHandler.saveTableToTextFile(VERSION+" "+URL, RvSnooperGUI.this.getBaseFrame(), RvSnooperGUI.this._statusLabel, RvSnooperGUI.this._table);
        } catch (Exception ex) {
          error = new RvSnooperErrorDialog(RvSnooperGUI.this.getBaseFrame(), ex.getMessage());
        }
      }
    });
    return result;
  }

  protected JMenuItem createOpenMI()
  {
    JMenuItem result = new JMenuItem("New Listener...");
    result.setMnemonic('n');
    result.setAccelerator(KeyStroke.getKeyStroke("control N"));
    result.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        RvSnooperGUI.this.requestNewRvListener(null);
      }
    });
    return result;
  }

  protected JMenuItem createSaveConfigMI()
  {
    JMenuItem result = new JMenuItem("Save Listeners to file");
    result.setMnemonic('s');
    result.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        RvSnooperGUI.this.requestNewRvListener(null);
      }
    });
    return result;
  }

  protected JMenuItem createOpenConfigMI() {
    JMenuItem result = new JMenuItem("Open Listeners from file");
    result.setMnemonic('o');
    result.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        RvSnooperGUI.this.requestNewRvListener(null);
      }
    });
    return result;
  }

  protected JMenuItem createCloseListener()
  {
    JMenuItem result = new JMenuItem("Close All Listeners");
    result.setMnemonic('c');
    result.setAccelerator(KeyStroke.getKeyStroke("control Q"));
    result.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        try {
          EMSController.shutdownAll();
        } catch (JMSException e1) {
          e1.printStackTrace();
        }
        RvSnooperGUI.this.updateBanner();
      }
    });
    return result;
  }

  protected void createMRUListnerListMI(JMenu menu)
  {
    String[] parameters = this._mruListnerManager.getMRUFileList();

    if (parameters != null) {
      menu.addSeparator();
      for (int i = 0; i < parameters.length; i++)
      {
        JMenuItem result = new JMenuItem(i + 1 + " " + parameters[i]);
        result.setMnemonic(i + 1);
        result.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            RvSnooperGUI.this.requestOpenMRU(e);
          }
        });
        menu.add(result);
      }
    }
  }

  protected JMenuItem createExitMI() {
    JMenuItem result = new JMenuItem("Exit");
    result.setMnemonic('x');
    result.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        RvSnooperGUI.this.requestExit();
      }
    });
    return result;
  }

  protected JMenu createConfigureMenu() {
    JMenu configureMenu = new JMenu("Configure");
    configureMenu.setMnemonic('c');
    configureMenu.add(createConfigureSave());
    configureMenu.add(createConfigureReset());
    configureMenu.add(createConfigureMaxRecords());
    configureMenu.add(createConfigureDateFormat());

    return configureMenu;
  }

  protected JMenuItem createConfigureSave() {
    JMenuItem result = new JMenuItem("Save");
    result.setMnemonic('s');
    result.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e) {
        RvSnooperGUI.this.saveConfiguration();
      }
    });
    return result;
  }

  protected JMenuItem createConfigureReset() {
    JMenuItem result = new JMenuItem("Reset");
    result.setMnemonic('r');
    result.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        RvSnooperGUI.this.resetConfiguration();
      }
    });
    return result;
  }

  protected JMenuItem createConfigureMaxRecords()
  {
    JMenuItem result = new JMenuItem("Set Max Number of Records");
    result.setMnemonic('m');
    result.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        RvSnooperGUI.this.setMaxRecordConfiguration();
      }
    });
    result.addKeyListener(new KeyAdapter() {
      public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == 10)
          RvSnooperGUI.this.hide();
      }
    });
    return result;
  }

  protected JMenuItem createConfigureDateFormat()
  {
    JMenuItem result = new JMenuItem("Configure Date Format");
    result.setMnemonic('d');

    result.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        RvSnooperGUI.this.setDateConfiguration();
      }
    });
    return result;
  }
  protected void saveConfiguration() {
    RvSnooperErrorDialog error;
    try {
      this._configurationManager.save();
    } catch (Exception ex) {
      ex.printStackTrace();
      error = new RvSnooperErrorDialog(getBaseFrame(), ex.getMessage());
    }
  }

  protected void resetConfiguration()
  {
    this._configurationManager.reset();
  }

  protected void setMaxRecordConfiguration() {
    RvSnooperInputDialog inputDialog = new RvSnooperInputDialog(getBaseFrame(), "Set Max Number of Records", "", 10);

    String temp = inputDialog.getText();

    if (temp != null)
      try {
        setMaxNumberOfLogRecords(Integer.parseInt(temp));
      } catch (NumberFormatException e) {
        RvSnooperErrorDialog error = new RvSnooperErrorDialog(getBaseFrame(), "'" + temp + "' is an invalid parameter.\nPlease try again.");

        setMaxRecordConfiguration();
      }
  }

  protected void setDateConfiguration()
  {
    RvSnooperInputDialog inputDialog = new RvSnooperInputDialog(getBaseFrame(), "Set DateFormat", "", 10);

    inputDialog.addKeyListener(new KeyAdapter() {
      public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == 10)
          RvSnooperGUI.this.hide();
      }
    });
    String temp = inputDialog.getText();

    if (temp != null)
      try {
        setDateFormat(temp);
      } catch (NumberFormatException e) {
        RvSnooperErrorDialog error = new RvSnooperErrorDialog(getBaseFrame(), "'" + temp + "' is an invalid parameter.\nPlease try again.");

        setMaxRecordConfiguration();
      }
  }

  protected JMenu createHelpMenu()
  {
    JMenu helpMenu = new JMenu("Help");
    helpMenu.setMnemonic('h');
    helpMenu.add(createHelpAbout());

    helpMenu.add(createHelpGotoHomepage());

    helpMenu.add(createHelpProperties());
    helpMenu.add(createHelpLICENSE());
    StringBuffer a = new StringBuffer();
    a.toString();
    return helpMenu;
  }

  protected JMenuItem createHelpProperties()
  {
    String title = "Show Properties";
    JMenuItem result = new JMenuItem("Show Properties");
    result.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        RvSnooperGUI.this.showPropertiesDialog("Show Properties");
      }
    });
    return result;
  }

  protected JMenuItem createHelpLICENSE() {
    String title = "License information";
    JMenuItem result = new JMenuItem("License information");
    result.setMnemonic('l');
    result.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        RvSnooperErrorDialog error;
        try { BrowserLauncher.openURL("http://www.apache.org/licenses/LICENSE");
        } catch (Exception ex)
        {
          error = new RvSnooperErrorDialog(RvSnooperGUI.this.getBaseFrame(), "Could not open browser : " + ex.getMessage());
        }
      }
    });
    return result;
  }

  protected JMenuItem createHelpGotoHomepage()
  {
    String title = "Help topics";
    JMenuItem result = new JMenuItem("Help topics");
    result.setMnemonic('t');
    result.setAccelerator(KeyStroke.getKeyStroke("F1"));
    result.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        RvSnooperErrorDialog error;
        try { BrowserLauncher.openURL("http://rvsn00p.sf.net");
        } catch (Exception ex)
        {
          error = new RvSnooperErrorDialog(RvSnooperGUI.this.getBaseFrame(), "Could not open browser : " + ex.getMessage());
        }
      }
    });
    return result;
  }

  protected JMenuItem createHelpAbout()
  {
    String title = "About EMSSn00per";
    JMenuItem result = new JMenuItem("About EMSSn00per");
    result.setMnemonic('a');
    result.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        RvSnooperGUI.this.showAboutDialog("About EMSSn00per");
      }
    });
    return result;
  }

  protected void showPropertiesDialog(String title)
  {
    JOptionPane.showMessageDialog(this._logMonitorFrame, this._displayedLogBrokerProperties.toArray(), title, -1);
  }

  protected void showAboutDialog(String title)
  {
    JOptionPane.showMessageDialog(this._logMonitorFrame, new String[] { VERSION, " ", "Constructed by Orjan Lundberg <lundberg@home.se>", " ", "This product includes software developed by the Apache Software Foundation (http://www.apache.org/). ", " ", "Thanks goes to (in no special order):", " ", "Julian Lo, Dan McLean ", " ", "Based on Jakarta log4J LogFactor5, Contributed by ThoughtWorks Inc.", " ", "Copyright (C) The Apache Software Foundation. All rights reserved.", " ", "This software is published under the terms of the Apache Software", "License version 1.1, a copy of which has been included with this", "distribution in the LICENSE.txt file. ", " " }, title, -1);
  }

  protected JMenu createEditMenu()
  {
    JMenu editMenu = new JMenu("Edit");
    editMenu.setMnemonic('e');
    editMenu.add(createEditFindMI());
    editMenu.add(createEditFindNextMI());
    editMenu.addSeparator();
    editMenu.add(createEditFilterTIDMI());
    editMenu.add(createEditFilterBySelectedTIDMI());
    editMenu.add(createEditFilterBySelectedSubjectMI());
    editMenu.add(createEditFilterBySelectedConnHN());
    editMenu.add(createEditFilterBySelectedRevConnHN());
    editMenu.add(createEditRemoveAllFiltersTIDMI());
    return editMenu;
  }

  protected JMenuItem createEditFindNextMI() {
    JMenuItem editFindNextMI = new JMenuItem("Find Next");
    editFindNextMI.setMnemonic('n');
    editFindNextMI.setAccelerator(KeyStroke.getKeyStroke("F3"));
    editFindNextMI.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        RvSnooperGUI.this.findSearchText();
      }
    });
    return editFindNextMI;
  }

  protected JMenuItem createEditFindMI() {
    JMenuItem editFindMI = new JMenuItem("Find");
    editFindMI.setMnemonic('f');
    editFindMI.setAccelerator(KeyStroke.getKeyStroke("control F"));

    editFindMI.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e) {
        String inputValue = JOptionPane.showInputDialog(RvSnooperGUI.this._logMonitorFrame, "Find text: ", "Search Record Messages", 3);

        RvSnooperGUI.this.setSearchText(inputValue);
        RvSnooperGUI.this.findSearchText();
      }
    });
    return editFindMI;
  }

  protected JMenuItem createEditFilterTIDMI() {
    JMenuItem editFilterNDCMI = new JMenuItem("Filter by JMSCorrelationID");
    editFilterNDCMI.setMnemonic('t');
    editFilterNDCMI.setAccelerator(KeyStroke.getKeyStroke("control shift T"));
    editFilterNDCMI.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e) {
        String inputValue = JOptionPane.showInputDialog(RvSnooperGUI.this._logMonitorFrame, "Filter by this JMSCorrelationID: ", "Filter Log Records by tracking id", 3);

        RvSnooperGUI.this.setTIDTextFilter(inputValue);
        RvSnooperGUI.this.filterByTID();
        RvSnooperGUI.this._table.getFilteredLogTableModel().refresh();
        RvSnooperGUI.this.updateStatusLabel();
      }
    });
    return editFilterNDCMI;
  }

  protected JMenuItem createEditFilterBySelectedTIDMI()
  {
    JMenuItem editFilterTIDMI = new JMenuItem("Filter by Selected JMSCorrelationID");
    editFilterTIDMI.setMnemonic('s');
    editFilterTIDMI.setAccelerator(KeyStroke.getKeyStroke("control T"));
    editFilterTIDMI.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        ListSelectionModel lsm = RvSnooperGUI.this._table.getSelectionModel();

        if (!lsm.isSelectionEmpty())
        {
          int selectedRow = lsm.getMinSelectionIndex();

          FilteredLogTableModel ftm = RvSnooperGUI.this._table.getFilteredLogTableModel();

          String sTID = (String)RvSnooperGUI.this._table.getModel().getValueAt(selectedRow, RvSnooperGUI.this._table.getTIDColumnID());
          if (sTID != null) {
            RvSnooperGUI.this.setTIDTextFilter(sTID);
            RvSnooperGUI.this.filterByTID();
            ftm.refresh();
          }
        }
      }
    });
    return editFilterTIDMI;
  }

  protected JMenuItem createEditFilterBySelectedConnHN() {
    JMenuItem editFilterTIDMI = new JMenuItem("Filter by Selected Conn Hostname");
    editFilterTIDMI.setMnemonic('H');
    editFilterTIDMI.setAccelerator(KeyStroke.getKeyStroke("control H"));
    editFilterTIDMI.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e) {
        ListSelectionModel lsm = RvSnooperGUI.this._table.getSelectionModel();

        if (!lsm.isSelectionEmpty())
        {
          int selectedRow = lsm.getMinSelectionIndex();

          FilteredLogTableModel ftm = RvSnooperGUI.this._table.getFilteredLogTableModel();

          String sHNF = (String)RvSnooperGUI.this._table.getModel().getValueAt(selectedRow, RvSnooperGUI.this._table.getConnHostnameColumnID());

          if (sHNF != null) {
            RvSnooperGUI.this.setConnHostnameTextFilter(sHNF);
            RvSnooperGUI.this.filterByConnHostname();
            ftm.refresh();
          }
        }
      }
    });
    return editFilterTIDMI;
  }

  protected JMenuItem createEditFilterBySelectedRevConnHN() {
    JMenuItem editFilterTIDMI = new JMenuItem("Remove Selected Conn Hostname");

    editFilterTIDMI.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e) {
        ListSelectionModel lsm = RvSnooperGUI.this._table.getSelectionModel();

        if (!lsm.isSelectionEmpty())
        {
          int selectedRow = lsm.getMinSelectionIndex();

          FilteredLogTableModel ftm = RvSnooperGUI.this._table.getFilteredLogTableModel();

          String sHNF = (String)RvSnooperGUI.this._table.getModel().getValueAt(selectedRow, RvSnooperGUI.this._table.getConnHostnameColumnID());

          if (sHNF != null) {
            RvSnooperGUI.this.setConnHostnameTextFilter(sHNF);
            RvSnooperGUI.this.filterRemoveConnHostname();
            ftm.refresh();
          }
        }
      }
    });
    return editFilterTIDMI;
  }

  protected JMenuItem createEditFilterBySelectedSubjectMI()
  {
    JMenuItem editFilterSubjectMI = new JMenuItem("Filter by Selected Destination");
    editFilterSubjectMI.setMnemonic('y');
    editFilterSubjectMI.setAccelerator(KeyStroke.getKeyStroke("control Y"));
    editFilterSubjectMI.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e) {
        ListSelectionModel lsm = RvSnooperGUI.this._table.getSelectionModel();

        if (!lsm.isSelectionEmpty())
        {
          int selectedRow = lsm.getMinSelectionIndex();

          FilteredLogTableModel ftm = RvSnooperGUI.this._table.getFilteredLogTableModel();

          String s = (String)RvSnooperGUI.this._table.getModel().getValueAt(selectedRow, RvSnooperGUI.this._table.getSubjectColumnID());
          if (s != null) {
            RvSnooperGUI.this.setSubjectTextFilter(s);
            RvSnooperGUI.this.filterBySubject();
            ftm.refresh();
          }
        }
      }
    });
    return editFilterSubjectMI;
  }

  protected void setTIDTextFilter(String text)
  {
    if (text == null)
      _JMSCorrelationIDTextFilter = "";
    else
      _JMSCorrelationIDTextFilter = text;
  }

  protected void setConnHostnameTextFilter(String text)
  {
    if (text == null)
      this._ConnHostnameTextFilter = "";
    else
      this._ConnHostnameTextFilter = text;
  }

  protected void setSubjectTextFilter(String text)
  {
    if (text == null)
      _subjectTextFilter = "";
    else
      _subjectTextFilter = text;
  }

  protected void filterByTID()
  {
    String text = _JMSCorrelationIDTextFilter;
    if ((text == null) || (text.length() == 0)) {
      return;
    }

    this._table.getFilteredLogTableModel().setLogRecordFilter(createTIDLogRecordFilter(text));

    this._statusLabel.setText("Filtered by JMSCorrelationiD " + text);
  }

  protected void filterByConnHostname() {
    String text = this._ConnHostnameTextFilter;
    if ((text == null) || (text.length() == 0)) {
      return;
    }

    this._table.getFilteredLogTableModel().setLogRecordFilter(createConnHostNameRecordFilter(text));

    this._statusLabel.setText("Filtered by Connnection Hostname " + text);
  }

  protected void filterRemoveConnHostname()
  {
    String text = this._ConnHostnameTextFilter;
    if ((text == null) || (text.length() == 0)) {
      return;
    }

    this._table.getFilteredLogTableModel().setLogRecordFilter(createConnHostNameReverseRecordFilter(text));

    this._statusLabel.setText("Filter Removed Connnection Hostname " + text);
  }

  protected void filterBySubject()
  {
    String text = _subjectTextFilter;
    if ((text == null) || (text.length() == 0)) {
      return;
    }

    this._table.getFilteredLogTableModel().setLogRecordFilter(createSubjectLogRecordFilter(text));

    this._statusLabel.setText("Filtered by Destination  " + text);
  }

  protected LogRecordFilter createTIDLogRecordFilter(String text) {
    _JMSCorrelationIDTextFilter = text;
    LogRecordFilter result = new LogRecordFilter() {
      public boolean passes(LogRecord record) {
        String correlationID = record.getJMSCorrelationID();

        if ((correlationID == null) || (RvSnooperGUI._JMSCorrelationIDTextFilter == null))
          return false;
        if (correlationID.indexOf(RvSnooperGUI._JMSCorrelationIDTextFilter) == -1) {
          return false;
        }
        CategoryPath path = new CategoryPath(record.getJMSDestination());
        return (RvSnooperGUI.this.getMenuItem(record.getType()).isSelected()) && (RvSnooperGUI.this._subjectExplorerTree.getExplorerModel().isCategoryPathActive(path));
      }
    };
    return result;
  }

  protected LogRecordFilter createSubjectLogRecordFilter(String text) {
    _JMSCorrelationIDTextFilter = text;
    LogRecordFilter result = new LogRecordFilter() {
      public boolean passes(LogRecord record) {
        String subject = record.getJMSDestination();
        if ((subject == null) || (RvSnooperGUI._subjectTextFilter == null))
          return false;
        if (subject.indexOf(RvSnooperGUI._subjectTextFilter) == -1) {
          return false;
        }
        CategoryPath path = new CategoryPath(subject);
        return (RvSnooperGUI.this.getMenuItem(record.getType()).isSelected()) && (RvSnooperGUI.this._subjectExplorerTree.getExplorerModel().isCategoryPathActive(path));
      }
    };
    return result;
  }

  protected LogRecordFilter createConnHostNameRecordFilter(String text) {
    this._ConnHostnameTextFilter = text;
    LogRecordFilter result = new LogRecordFilter()
    {
      public boolean passes(LogRecord record) {
        String hostName = record.getConnHostName();
        if ((hostName == null) || (RvSnooperGUI.this._ConnHostnameTextFilter == null))
          return false;
        if (hostName.indexOf(RvSnooperGUI.this._ConnHostnameTextFilter) == -1) {
          return false;
        }
        String subject = record.getJMSDestination();
        CategoryPath path = new CategoryPath(subject);
        return (RvSnooperGUI.this.getMenuItem(record.getType()).isSelected()) && (RvSnooperGUI.this._subjectExplorerTree.getExplorerModel().isCategoryPathActive(path));
      }
    };
    return result;
  }

  protected LogRecordFilter createConnHostNameReverseRecordFilter(String text) {
    this._ConnHostnameTextFilter = text;
    LogRecordFilter result = new LogRecordFilter()
    {
      public boolean passes(LogRecord record) {
        String hostName = record.getConnHostName();
        if ((hostName == null) || (RvSnooperGUI.this._ConnHostnameTextFilter == null))
          return true;
        if (hostName.compareTo(RvSnooperGUI.this._ConnHostnameTextFilter) == 0) {
          return false;
        }
        String subject = record.getJMSDestination();
        CategoryPath path = new CategoryPath(subject);
        return (RvSnooperGUI.this.getMenuItem(record.getType()).isSelected()) && (RvSnooperGUI.this._subjectExplorerTree.getExplorerModel().isCategoryPathActive(path));
      }
    };
    return result;
  }

  protected JMenuItem createEditRemoveAllFiltersTIDMI() {
    JMenuItem editRestoreAllNDCMI = new JMenuItem("Remove all filters");
    editRestoreAllNDCMI.setMnemonic('r');
    editRestoreAllNDCMI.setAccelerator(KeyStroke.getKeyStroke("control R"));
    editRestoreAllNDCMI.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e) {
        RvSnooperGUI.this._table.getFilteredLogTableModel().setLogRecordFilter(RvSnooperGUI.this.createLogRecordFilter());

        RvSnooperGUI.this.setTIDTextFilter("");
        RvSnooperGUI.this._table.getFilteredLogTableModel().refresh();
        RvSnooperGUI.this.updateStatusLabel();
      }
    });
    return editRestoreAllNDCMI;
  }

  protected JToolBar createToolBar()
  {
    JToolBar tb = new JToolBar();
    tb.putClientProperty("JToolBar.isRollover", Boolean.TRUE);
    JComboBox fontCombo = new JComboBox();
    JComboBox fontSizeCombo = new JComboBox();
    this._fontSizeCombo = fontSizeCombo;
    this._fontNameCombo = fontCombo;
    JComboBox rendererCombo = new JComboBox();
    this._rendererCombo = rendererCombo;

    this._cl = getClass().getClassLoader();
    if (this._cl == null) {
      this._cl = ClassLoader.getSystemClassLoader();
    }
    URL newIconURL = this._cl.getResource("channelexplorer_new.gif");

    ImageIcon newIcon = null;

    if (newIconURL != null) {
      newIcon = new ImageIcon(newIconURL);
    }

    JButton listenerButton = new JButton("Add Listener");

    if (newIcon != null) {
      listenerButton.setIcon(newIcon);
    }

    listenerButton.setToolTipText("Create new Rv Listener.");

    listenerButton.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e) {
        RvSnooperGUI.this.requestNewRvListener(null);
      }
    });
    JButton newButton = new JButton("Clear Log Table");

    URL tcIconURL = this._cl.getResource("trash.gif");

    ImageIcon tcIcon = null;

    if (tcIconURL != null) {
      tcIcon = new ImageIcon(tcIconURL);
    }

    if (newIcon != null) {
      newButton.setIcon(tcIcon);
    }

    newButton.setToolTipText("Clear Log Table.");

    newButton.addActionListener(getNewButtonActionListener());

    this._pauseButton = new JButton("Pause all listeners");

    this._pauseButton.addActionListener(getPauseButonActionListener());

    addFontsToFontCombo(fontCombo);

    fontCombo.setSelectedItem(getFontName());

    fontCombo.addActionListener(getFontComboActionListener());

    addFontSizesToCombo(fontSizeCombo);

    fontSizeCombo.setSelectedItem(String.valueOf(getFontSize()));
    fontSizeCombo.addActionListener(getFontSizeComboActionListener());

    addRenderersToRendererCombo(rendererCombo);
    rendererCombo.setSelectedItem(getLastUsedRenderer());

    rendererCombo.addActionListener(getRendererActionListeener());

    tb.add(new JLabel(" Font: "));
    tb.add(fontCombo);
    tb.add(fontSizeCombo);
    tb.addSeparator();
    tb.add(new JLabel(" Renderer: "));
    tb.add(rendererCombo);
    tb.addSeparator();
    tb.add(listenerButton);
    tb.addSeparator();
    tb.add(newButton);
    tb.addSeparator();
    tb.add(this._pauseButton);

    setButtonAlignment(newButton);
    setButtonAlignment(listenerButton);
    setButtonAlignment(this._pauseButton);

    fontCombo.setMaximumSize(fontCombo.getPreferredSize());
    fontSizeCombo.setMaximumSize(fontSizeCombo.getPreferredSize());

    rendererCombo.setMaximumSize(rendererCombo.getPreferredSize());

    return tb;
  }

  private void addRenderersToRendererCombo(JComboBox rendererCombo)
  {
    rendererCombo.addItem("uk.co.lecafeautomatique.zedogg.util.ems.MarshalJMSMsgToStringImpl");
    rendererCombo.addItem("uk.co.lecafeautomatique.zedogg.util.ems.MarshalJMSMsgToStringJMSStreamImpl");

    String env = System.getenv("EMSSNOOP_RENDERERS");
    try
    {
      if (env != null) {
        String[] result = env.split(";");
        for (int x = 0; x < result.length; x++)
          rendererCombo.addItem(result[x]);
      }
    }
    catch (RuntimeException e)
    {
    }
  }

  String getLastUsedRenderer()
  {
    if (this._lastUsedRenderer == null) {
      return "uk.co.lecafeautomatique.zedogg.util.ems.MarshalJMSMsgToStringImpl";
    }
    return this._lastUsedRenderer;
  }

  private void addFontsToFontCombo(JComboBox fontCombo)
  {
    Toolkit tk = Toolkit.getDefaultToolkit();

    String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();

    for (int j = 0; j < fonts.length; j++)
      fontCombo.addItem(fonts[j]);
  }

  private ActionListener getNewButtonActionListener()
  {
    return new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        RvSnooperGUI.this._table.clearLogRecords();
        RvSnooperGUI.this._subjectExplorerTree.getExplorerModel().resetAllNodeCounts();
        RvSnooperGUI.this.updateStatusLabel();
        RvSnooperGUI.this.clearDetailTextArea();
        LogRecord.resetSequenceNumber();
      }
    };
  }

  private ActionListener getPauseButonActionListener()
  {
    return new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (RvSnooperGUI.this.isPaused())
          RvSnooperGUI.this.unPauseListeners();
        else
          RvSnooperGUI.this.pauseListeners();
      }
    };
  }

  private ActionListener getFontComboActionListener()
  {
    return new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        JComboBox box = (JComboBox)e.getSource();
        String font = (String)box.getSelectedItem();

        RvSnooperGUI.this.setFontName(font);
      }
    };
  }

  private void addFontSizesToCombo(JComboBox fontSizeCombo)
  {
    fontSizeCombo.addItem("8");
    fontSizeCombo.addItem("9");
    fontSizeCombo.addItem("10");
    fontSizeCombo.addItem("12");
    fontSizeCombo.addItem("14");
    fontSizeCombo.addItem("16");
    fontSizeCombo.addItem("18");
    fontSizeCombo.addItem("24");
  }

  private ActionListener getFontSizeComboActionListener()
  {
    return new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        JComboBox box = (JComboBox)e.getSource();
        String size = (String)box.getSelectedItem();
        int s = Integer.valueOf(size).intValue();

        RvSnooperGUI.this.setFontSizeSilently(s);
        RvSnooperGUI.this.refreshDetailTextArea();
        RvSnooperGUI.this.setFontSize(s);
      }
    };
  }

  private ActionListener getRendererActionListeener()
  {
    return new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        JComboBox box = (JComboBox)e.getSource();
        String rendererClass = (String)box.getSelectedItem();
        try
        {
          RvSnooperGUI.this.updateRenderClass(rendererClass);
        }
        catch (Exception ex) {
          RvSnooperGUI.this._statusLabel.setText("Renderer exception " + ex.getMessage());
          String str1 = ex.getLocalizedMessage();
          RvSnooperErrorDialog error = new RvSnooperErrorDialog(RvSnooperGUI.this.getBaseFrame(), "Error creating renderer : " + ex.getMessage());

          return;
        }

        RvSnooperGUI.this.setLastUsedRenderer(rendererClass);

        RvSnooperGUI.this._statusLabel.setText("Renderer " + rendererClass + " Selected");
      }
    };
  }

  private void setButtonAlignment(JButton newButton)
  {
    newButton.setAlignmentY(0.5F);
    newButton.setAlignmentX(0.5F);
  }

  protected void setView(String viewString, LogTable table) {
    if ("Detailed".equals(viewString)) {
      table.setDetailedView();
    } else {
      String message = viewString + "does not match a supported view.";
      throw new IllegalArgumentException(message);
    }
    this._currentView = viewString;
  }

  protected JComboBox createLogLevelCombo() {
    JComboBox result = new JComboBox();
    Iterator levels = getMsgTypes();
    while (levels.hasNext()) {
      result.addItem(levels.next());
    }
    result.setSelectedItem(this._leastSevereDisplayedMsgType);

    result.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        JComboBox box = (JComboBox)e.getSource();
        EventActionType level = (EventActionType)box.getSelectedItem();
        RvSnooperGUI.this.setLeastSevereDisplayedLogLevel(level);
      }
    });
    result.setMaximumSize(result.getPreferredSize());
    return result;
  }

  protected void setLeastSevereDisplayedLogLevel(EventActionType level) {
    if ((level == null) || (this._leastSevereDisplayedMsgType == level)) {
      return;
    }
    this._leastSevereDisplayedMsgType = level;
    this._table.getFilteredLogTableModel().refresh();
    updateStatusLabel();
  }

  protected static void centerFrame(JFrame frame)
  {
    Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension comp = frame.getSize();

    frame.setLocation((screen.width - comp.width) / 2, (screen.height - comp.height) / 2);
  }

  Rectangle getWindowBounds()
  {
    return getBaseFrame().getBounds();
  }

  void setWindowBounds(Rectangle r)
  {
    getBaseFrame().setBounds(r);
  }

  protected void requestNewRvListener(EMSParameters p)
  {
    RvSnooperErrorDialog error;
    try
    {
      RvSnooperRvTransportInputDialog inputDialog = null;
      if (p != null) {
        inputDialog = new RvSnooperRvTransportInputDialog(getBaseFrame(), "Add  Listener", p);
      }
      else {
        inputDialog = new RvSnooperRvTransportInputDialog(getBaseFrame(), "Add  Listener", this._lastUsedRvParameters);
      }

      if (inputDialog.isOK()) {
        this._lastUsedRvParameters = inputDialog.getRvParameters();

        this._lastUsedRvParameters.setDescription("<a href=\""+URL+"\">"+VERSION+"</a> ");
        EMSController.startListener(this._lastUsedRvParameters, this);
        updateBanner();
      }

    }
    catch (JMSException ex)
    {
      error = new RvSnooperErrorDialog(getBaseFrame(), "Error creating listener : " + ex.getMessage());
    }
  }

  protected void updateMRUList()
  {
    JMenu menu = this._logMonitorFrame.getJMenuBar().getMenu(0);
    menu.removeAll();
    menu.add(createOpenMI());
    menu.addSeparator();
    menu.add(createSaveConfigMI());
    menu.add(createOpenConfigMI());
    menu.add(createCloseListener());
    createMRUListnerListMI(menu);
    menu.addSeparator();
    menu.add(createExitMI());
  }

  protected void requestCloseListener() {
    updateBanner();
  }

  protected void requestClose() {
    setCallSystemExitOnClose(true);
    closeAfterConfirm();
  }

  protected void requestOpenMRU(ActionEvent e)
  {
    String file = e.getActionCommand();
    StringTokenizer st = new StringTokenizer(file);
    String num = st.nextToken().trim();
    file = st.nextToken("\n");
    RvSnooperErrorDialog error;
    try {
      int index = Integer.parseInt(num) - 1;

      InputStream in = this._mruListnerManager.getInputStream(index);

      this._mruListnerManager.moveToTop(index);
      updateMRUList();
    }
    catch (Exception me) {
      error = new RvSnooperErrorDialog(getBaseFrame(), "Unable to load file " + file);
    }
  }

  protected void requestExit()
  {
    setCallSystemExitOnClose(true);
    closeAfterConfirm();
  }

  protected void closeAfterConfirm() {
    StringBuffer message = new StringBuffer();

    message.append("Are you sure you want to exit?\n");

    String title = "Are you sure you want to exit?";

    int value = JOptionPane.showConfirmDialog(this._logMonitorFrame, message.toString(), "Are you sure you want to exit?", 2, 3, null);

    if (value == 0)
      dispose();
  }

  protected Iterator getMsgTypes()
  {
    return this._levels.iterator();
  }

  protected Iterator getLogTableColumns() {
    return this._columns.iterator();
  }

  Iterator getSubscriptions() {
    return EMSController.getTransports().iterator();
  }

  protected static boolean loadLogFile(File file)
  {
    boolean ok = true;

    return ok;
  }

  protected static boolean loadLogFile(URL url)
  {
    boolean ok = true;

    return ok;
  }

  int getFontSize() {
    return this._fontSize;
  }

  String getFontName() {
    return this._fontName;
  }

  void setFontName(String fontName) {
    this._fontName = fontName;
    this._table.setFont(new Font(fontName, 0, getFontSize()));
    changeStringCombo(this._fontNameCombo, fontName);
  }

  protected void setSplitPaneVertical(JSplitPane _splitPaneVertical)
  {
    this._splitPaneVertical = _splitPaneVertical;
  }

  int getSplitPaneVerticalPos() {
    return this._splitPaneVertical.getDividerLocation();
  }

  void setSplitPaneVerticalPos(int location) {
    this._splitPaneVertical.setDividerLocation(location);
  }

  int getSplitPaneTableViewerPos() {
    return this._splitPaneTableViewer.getDividerLocation();
  }

  void setSplitPaneTableViewer(JSplitPane _splitPaneTableViewer)
  {
    this._splitPaneTableViewer = _splitPaneTableViewer;
  }

  void setSplitPaneTableViewerPos(int location)
  {
    this._splitPaneTableViewer.setDividerLocation(location);
  }

  public void onMessage(Message msg)
  {
    if (isPaused()) {
      return;
    }

    LogRecord r = LogRecordFactory.createLogRecordFromJMSMessage(this._statusLabel, msg);

    addMessage(r);
  }

  void setLastUsedRenderer(String rendererClass)
  {
    if (rendererClass != null)
      this._lastUsedRenderer = rendererClass;
  }

  private void updateRenderClass(String rendererClass)
    throws ClassNotFoundException, InstantiationException, IllegalAccessException
  {
    Class c = Class.forName(rendererClass);
    Object o = c.newInstance();
    if ((o instanceof IMarshalJMSToString))
      _marshalImpl.setImpl((IMarshalJMSToString)o);
    else
      this._statusLabel.setText("Class " + rendererClass + " does not implement IMarshalJMSToString");
  }

  private class AddLogRecordRunnable
    implements Runnable
  {
    private final LogRecord lr;

    public AddLogRecordRunnable(LogRecord lr)
    {
      this.lr = lr;
    }

    public void run() {
      RvSnooperGUI.this._subjectExplorerTree.getExplorerModel().addLogRecord(this.lr);
      RvSnooperGUI.this._table.getFilteredLogTableModel().addLogRecord(this.lr);
      RvSnooperGUI.this.updateStatusLabel();
    }
  }

  class LogBrokerMonitorWindowAdaptor extends WindowAdapter
  {
    protected RvSnooperGUI _monitor;

    public LogBrokerMonitorWindowAdaptor(RvSnooperGUI monitor)
    {
      this._monitor = monitor;
    }

    public void windowClosing(WindowEvent ev) {
      this._monitor.requestClose();
    }
  }
}
