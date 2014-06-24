package uk.co.lecafeautomatique.zedogg.gui;

import uk.co.lecafeautomatique.zedogg.Zedogg;
import uk.co.lecafeautomatique.zedogg.gui.categoryexplorer.CategoryExplorerTree;
import uk.co.lecafeautomatique.zedogg.gui.categoryexplorer.CategoryPath;
import uk.co.lecafeautomatique.zedogg.jms.EventActionType;

import uk.co.lecafeautomatique.zedogg.jms.JMSParameters;
import uk.co.lecafeautomatique.zedogg.jms.LogRecord;
import uk.co.lecafeautomatique.zedogg.jms.MarshalJMSMsgToStringProxyImpl;
import uk.co.lecafeautomatique.zedogg.jms.MarshalJMSToString;
import uk.co.lecafeautomatique.zedogg.util.DateFormatManager;
import uk.co.lecafeautomatique.zedogg.util.LogRecordFilter;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

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

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


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
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;


public class GUI implements MessageListener {
  public static final String NAME = "ZeDogg";
  public static final String VERSION = "v0.1.0";
  public static final MarshalJMSMsgToStringProxyImpl _marshalImpl = new MarshalJMSMsgToStringProxyImpl();

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

  protected JMSParameters _lastUsedParameters = new JMSParameters();
  protected ClassLoader _cl = null;

  protected static String _JMSCorrelationIDTextFilter = "";
  protected static String _subjectTextFilter = "";
  protected static boolean useMtrackingInfo = true;
  private String _ConnHostnameTextFilter;
  private JComboBox _rendererCombo;
  protected String _lastUsedRenderer;

  private Zedogg zeDogg;
  
  public GUI(Zedogg z) {
    this(EventActionType.getAllDefaultLevels(), z);
  }

  public GUI(List MsgTypes, Zedogg z) {
    zeDogg = z;
    _levels = MsgTypes;
    _columns = LogTableColumn.getLogTableColumns();
    _columns = LogTableColumn.getLogTableColumns();
    
    initComponents();

    _logMonitorFrame.addWindowListener(new LogBrokerMonitorWindowAdaptor(this));
    updateTitle();
  }

  public void onError(Object tibrvObject, int errorCode, String message, Throwable throwable) {
    new GUIErrorDialog(getBaseFrame(), "A System error occured " + message);
  }

  public void show(final int delay) {
    if (this._logMonitorFrame.isVisible()) {
      return;
    }

    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        Thread.yield();
        GUI.pause(delay);
        GUI.this._logMonitorFrame.setVisible(true);
        GUI.changeStringCombo(GUI.this._fontNameCombo, GUI.this._fontName);
        GUI.changeIntCombo(GUI.this._fontSizeCombo, GUI.this._fontSize);
        GUI.changeStringCombo(GUI.this._rendererCombo, GUI.this.getLastUsedRenderer());
      }
    });
  }

  public void show() {
    show(0);
    updateTitle();
  }

  public void updateTitle() {
    String sBanner = zeDogg.getTransports().toString();
    setTitle(sBanner + " " + NAME + " " + VERSION);
  }

  public void dispose() {
    _logMonitorFrame.dispose();
    _isDisposed = true;
    try {
      zeDogg.shutdown();
    } catch (Exception ex) {
      System.err.println(ex.getMessage());
    }

    if (this._callSystemExitOnClose)
      System.exit(0);
  }

  public void hide() {
    _logMonitorFrame.setVisible(false);
  }

  DateFormatManager getDateFormatManager() {
    return _table.getDateFormatManager();
  }

  void setDateFormatManager(DateFormatManager dfm) {
    _table.setDateFormatManager(dfm);
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

  public boolean getCallSystemExitOnClose() {
    return _callSystemExitOnClose;
  }

  public void setCallSystemExitOnClose(boolean callSystemExitOnClose) {
    _callSystemExitOnClose = callSystemExitOnClose;
  }

  public void addMessage(LogRecord lr) {
    if (_isDisposed) return;

    SwingUtilities.invokeLater(new AddLogRecordRunnable(lr));
  }

  public void setMaxNumberOfLogRecords(int maxNumberOfLogRecords) {
    _table.getFilteredLogTableModel().setMaxNumberOfLogRecords(maxNumberOfLogRecords);
  }

  public JFrame getBaseFrame() {
    return _logMonitorFrame;
  }

  public void setTitle(String title) {
    _logMonitorFrame.setTitle(title);
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

  public void addDisplayedProperty(Object messageLine) {
    this._displayedLogBrokerProperties.add(messageLine);
  }

  public Map getLogLevelMenuItems() {
    return _logLevelMenuItems;
  }

  public Map getLogTableColumnMenuItems() {
    return _logTableColumnMenuItems;
  }

  public JCheckBoxMenuItem getTableColumnMenuItem(LogTableColumn column) {
    return getLogTableColumnMenuItem(column);
  }

  public CategoryExplorerTree getCategoryExplorerTree() {
    return this._subjectExplorerTree;
  }

  protected void pauseListeners() {
    try {
      zeDogg.pause();
    } catch (JMSException e) {
      _statusLabel.setText("Pause all listeners failed " + e.getMessage());
      return;
    }
    _pauseButton.setText("Continue all listeners");
    _pauseButton.setToolTipText("Unpause listeners");
    _statusLabel.setText("All listeners are now paused");

    ImageIcon pbIcon = null;
    URL pbIconURL = _cl.getResource("restart.gif");

    if (pbIconURL != null) {
      pbIcon = new ImageIcon(pbIconURL);
    }

    if (pbIcon != null)
      this._pauseButton.setIcon(pbIcon);
  }

  protected void resumeListeners() {
    try {
      zeDogg.resume();
    } catch (JMSException e) {
      _statusLabel.setText("Resume all listeners failed " + e.getMessage());
      return;
    }
    _pauseButton.setText("Pause all listeners");
    _pauseButton.setToolTipText("Put listeners on hold");
    _statusLabel.setText("All listeners are now active");

    ImageIcon pbIcon = null;
    URL pbIconURL = _cl.getResource("pauseon.gif");

    if (pbIconURL != null) {
      pbIcon = new ImageIcon(pbIconURL);
    }

    if (pbIcon != null)
      _pauseButton.setIcon(pbIcon);
  }

  protected void setSearchText(String text) {
    _searchText = text;
  }

  protected void findSearchText() {
    String text = _searchText;
    if ((text == null) || (text.length() == 0)) {
      return;
    }
    int startRow = getFirstSelectedRow();
    int foundRow = findRecord(startRow, text, _table.getFilteredLogTableModel().getFilteredRecords());

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

  protected int findRecord(int startRow, String searchText, List records) {
    if (startRow < 0) {
      startRow = 0;
    } else {
      startRow++;
    }
    int len = records.size();

    for (int i = startRow; i < len; i++) {
      if (matches((LogRecord) records.get(i), searchText)) {
        return i;
      }
    }

    len = startRow;
    for (int i = 0; i < len; i++) {
      if (matches((LogRecord) records.get(i), searchText)) {
        return i;
      }
    }

    return -1;
  }

  protected static boolean matches(LogRecord record, String text) {
    String message = record.toString(_marshalImpl);
    if (((message == null) && (_JMSCorrelationIDTextFilter == null)) || (text == null)) {
      return false;
    }

    if ((message.toLowerCase().indexOf(text.toLowerCase()) == -1)
        && (_JMSCorrelationIDTextFilter.indexOf(text.toLowerCase()) == -1)) {
      return false;
    }

    return true;
  }

  protected static void refresh(JTextArea textArea) {
    String text = textArea.getText();
    textArea.setText("");
    textArea.setText(text);
  }

  protected void refreshDetailTextArea() {
    refresh(_table._detailTextArea);
  }

  protected void clearDetailTextArea() {
    _table._detailTextArea.setText("");
  }

  public static int changeIntCombo(JComboBox box, int requestedSize) {
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

  public static String changeStringCombo(JComboBox box, String requestedName) {
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

  protected void setFontSizeSilently(int fontSize) {
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
    } catch (InterruptedException e) {
    }
  }

  protected void initComponents() {
    _logMonitorFrame = new JFrame();
    _logMonitorFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

    URL iconURL = getClass().getResource("/eye.gif");

    if (iconURL != null) {
      _logMonitorFrame.setIconImage(new ImageIcon(iconURL).getImage());
    }
    updateFrameSize();

    JTextArea detailTA = createDetailTextArea();
    JScrollPane detailTAScrollPane = new JScrollPane(detailTA);
    _table = new LogTable(detailTA, _marshalImpl);
    setView(_currentView, _table);
    _table.setFont(new Font(getFontName(), 0, getFontSize()));
    _logTableScrollPane = new JScrollPane(_table);

    if (_trackTableScrollPane) {
      _logTableScrollPane.getVerticalScrollBar().addAdjustmentListener(new TrackingAdjustmentListener());
    }

    JSplitPane tableViewerSplitPane = new JSplitPane();
    tableViewerSplitPane.setOneTouchExpandable(true);
    tableViewerSplitPane.setOrientation(0);
    tableViewerSplitPane.setLeftComponent(_logTableScrollPane);
    tableViewerSplitPane.setRightComponent(detailTAScrollPane);

    tableViewerSplitPane.setDividerLocation(350);

    setSplitPaneTableViewer(tableViewerSplitPane);

    _subjectExplorerTree = new CategoryExplorerTree();

    _table.getFilteredLogTableModel().setLogRecordFilter(createLogRecordFilter());

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

    final GUI gui = this;
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        GUI.this.setDateFormat("HH:mm:ss.S");

        GUI.this.resumeListeners();
        String ex = "";
        try {
          GUI.this.updateRenderClass(GUI.this.getLastUsedRenderer());
        } catch (Exception e) {
          e.printStackTrace();
          ex = " " + e.getMessage();
        }

        GUI.this._statusLabel.setText("Started @ " + new Date() + ex);
      }
    });
  }

  protected LogRecordFilter createLogRecordFilter() {
    LogRecordFilter result = new LogRecordFilter() {
      public boolean passes(LogRecord record) {
        CategoryPath path = new CategoryPath(record.getJMSDestination());
        return (GUI.this.getMenuItem(record.getType()).isSelected())
            && (GUI.this._subjectExplorerTree.getExplorerModel().isCategoryPathActive(path));
      }
    };
    return result;
  }

  protected void updateStatusLabel() {
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
        GUI.this.getRecordsDisplayedMessage(sb);
        return sb.toString();
      }
    });
    addDisplayedProperty(new Object() {
      public String toString() {
        return "Maximum number of displayed LogRecords: " + model._maxNumberOfLogRecords;
      }
    });
  }

  protected static void getStatusText(int displayedRows, int totalRows, StringBuffer sb) {
    sb.append("Displaying: ");
    sb.append(displayedRows);
    sb.append(" records out of a total of: ");
    sb.append(totalRows);
    sb.append(" records. ");
    sb.append(totalRows - displayedRows);
    sb.append(" are filtered.");
  }

  protected void makeLogTableListenToCategoryExplorer() {
    ActionListener listener = new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        GUI.this._table.getFilteredLogTableModel().refresh();
        GUI.this.updateStatusLabel();
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

  protected JMenuItem createSaveSelectedAsTextFileMenuItem() {
    JMenuItem result = new JMenuItem("Save selected as txt");
    result.setMnemonic('r');

    result.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        ListSelectionModel lsm = GUI.this._table.getSelectionModel();
        if (lsm.isSelectionEmpty()) {
          GUI.this._statusLabel.setText("No rows are selected.");
        } else {
          int selectedRow = lsm.getMinSelectionIndex();

          FilteredLogTableModel ftm = GUI.this._table.getFilteredLogTableModel();

          final String sMsg = (String) GUI.this._table.getModel().getValueAt(selectedRow,
              GUI.this._table.getMsgColumnID());
          final String sSubject = (String) GUI.this._table.getModel().getValueAt(selectedRow,
              GUI.this._table.getSubjectColumnID());

          SwingUtilities.invokeLater(new Runnable() {
            public void run() {
              GUIFileHandler.saveMsgAsTextFile(sSubject, sMsg,
                  GUI.this.getBaseFrame(), GUI.this._statusLabel);
            }
          });
        }
      }
    });
    result.setEnabled(false);

    return result;
  }

  protected JMenu createMsgTypeMenu() {
    JMenu result = new JMenu("Event Action");
    result.setMnemonic('a');
    Iterator imsgtypes = getMsgTypes();
    while (imsgtypes.hasNext()) {
      result.add(getMenuItem((EventActionType) imsgtypes.next()));
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
        GUI.this.selectAllMsgTypes(true);
        GUI.this._table.getFilteredLogTableModel().refresh();
        GUI.this.updateStatusLabel();
      }
    });
    return result;
  }

  protected JMenuItem createNoMsgTypesMenuItem() {
    JMenuItem result = new JMenuItem("Hide all Event Actions");
    result.setMnemonic('h');
    result.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        GUI.this.selectAllMsgTypes(false);
        GUI.this._table.getFilteredLogTableModel().refresh();
        GUI.this.updateStatusLabel();
      }
    });
    return result;
  }

  protected JMenu createLogLevelColorMenu() {
    JMenu colorMenu = new JMenu("Configure Event Actions Colors");
    colorMenu.setMnemonic('c');
    Iterator levels = getMsgTypes();
    while (levels.hasNext()) {
      colorMenu.add(createSubMenuItem((EventActionType) levels.next()));
    }

    return colorMenu;
  }

  protected JMenuItem createResetLogLevelColorMenuItem() {
    JMenuItem result = new JMenuItem("Reset Event Actions Colors");
    result.setMnemonic('r');
    result.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        EventActionType.resetLogLevelColorMap();

        GUI.this._table.getFilteredLogTableModel().refresh();
      }
    });
    return result;
  }

  protected void selectAllMsgTypes(boolean selected) {
    Iterator levels = getMsgTypes();
    while (levels.hasNext())
      getMenuItem((EventActionType) levels.next()).setSelected(selected);
  }

  protected JCheckBoxMenuItem getMenuItem(EventActionType level) {
    JCheckBoxMenuItem result = this._logLevelMenuItems.get(level);
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
        GUI.this.showLogLevelColorChangeDialog(result, logLevel);
      }
    });
    return result;
  }

  protected void showLogLevelColorChangeDialog(JMenuItem result, EventActionType level) {
    JMenuItem menuItem = result;
    Color newColor = JColorChooser.showDialog(this._logMonitorFrame, "Choose Event Actions Color",
        result.getForeground());

    if (newColor != null) {
      level.setLogLevelColorMap(level, newColor);
      this._table.getFilteredLogTableModel().refresh();
    }
  }

  protected JCheckBoxMenuItem createMenuItem(EventActionType level) {
    JCheckBoxMenuItem result = new JCheckBoxMenuItem(level.toString());
    result.setSelected(true);
    result.setMnemonic(level.toString().charAt(0));
    result.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        GUI.this._table.getFilteredLogTableModel().refresh();
        GUI.this.updateStatusLabel();
      }
    });
    return result;
  }

  protected JMenu createViewMenu() {
    JMenu result = new JMenu("View");
    result.setMnemonic('v');
    Iterator columns = getLogTableColumns();
    while (columns.hasNext()) {
      result.add(getLogTableColumnMenuItem((LogTableColumn) columns.next()));
    }

    result.addSeparator();
    result.add(createAllLogTableColumnsMenuItem());
    result.add(createNoLogTableColumnsMenuItem());
    return result;
  }

  protected JCheckBoxMenuItem getLogTableColumnMenuItem(LogTableColumn column) {
    JCheckBoxMenuItem result = this._logTableColumnMenuItems.get(column);
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
    result.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        List selectedColumns = GUI.this.updateView();
        GUI.this._table.setView(selectedColumns);
      }
    });
    return result;
  }

  protected List<LogTableColumn> updateView() {
    ArrayList updatedList = new ArrayList();
    Iterator columnIterator = this._columns.iterator();
    while (columnIterator.hasNext()) {
      LogTableColumn column = (LogTableColumn) columnIterator.next();
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
        GUI.this.selectAllLogTableColumns(true);

        List selectedColumns = GUI.this.updateView();
        GUI.this._table.setView(selectedColumns);
      }
    });
    return result;
  }

  protected JMenuItem createNoLogTableColumnsMenuItem() {
    JMenuItem result = new JMenuItem("Hide all Columns");
    result.setMnemonic('h');
    result.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        GUI.this.selectAllLogTableColumns(false);

        List selectedColumns = GUI.this.updateView();
        GUI.this._table.setView(selectedColumns);
      }
    });
    return result;
  }

  protected void selectAllLogTableColumns(boolean selected) {
    Iterator columns = getLogTableColumns();
    while (columns.hasNext())
      getLogTableColumnMenuItem((LogTableColumn) columns.next()).setSelected(selected);
  }

  protected JMenu createFileMenu() {
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

    fileMenu.addSeparator();
    fileMenu.add(createExitMI());
    return fileMenu;
  }

  protected JMenuItem createSaveHTML() {
    JMenuItem result = new JMenuItem("Save Table to HTML file");
    result.setMnemonic('h');
    result.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        GUIFileHandler.saveTableToHtml(GUI.this.getBaseFrame(), GUI.this._statusLabel, GUI.this._table);
      }
    });
    return result;
  }

  protected JMenuItem createSaveAsTextMI() {
    JMenuItem result = new JMenuItem("Save Table to text file");
    result.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        GUIErrorDialog error;
        try {
          GUIFileHandler.saveTableToTextFile(GUI.this.getBaseFrame(), GUI.this._statusLabel, GUI.this._table);
        } catch (Exception ex) {
          error = new GUIErrorDialog(GUI.this.getBaseFrame(), ex.getMessage());
        }
      }
    });
    return result;
  }

  protected JMenuItem createOpenMI() {
    JMenuItem result = new JMenuItem("New Listener...");
    result.setMnemonic('n');
    result.setAccelerator(KeyStroke.getKeyStroke("control N"));
    result.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        GUI.this.requestNewListener(null);
      }
    });
    return result;
  }

  protected JMenuItem createSaveConfigMI() {
    JMenuItem result = new JMenuItem("Save Listeners to file");
    result.setMnemonic('s');
    result.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        GUI.this.requestNewListener(null);
      }
    });
    return result;
  }

  protected JMenuItem createOpenConfigMI() {
    JMenuItem result = new JMenuItem("Open Listeners from file");
    result.setMnemonic('o');
    result.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        GUI.this.requestNewListener(null);
      }
    });
    return result;
  }

  protected JMenuItem createCloseListener() {
    JMenuItem result = new JMenuItem("Close All Listeners");
    result.setMnemonic('c');
    result.setAccelerator(KeyStroke.getKeyStroke("control Q"));
    result.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        try {
          zeDogg.shutdown();
        } catch (JMSException e1) {
          e1.printStackTrace();
        }
        GUI.this.updateTitle();
      }
    });
    return result;
  }

  protected JMenuItem createExitMI() {
    JMenuItem result = new JMenuItem("Exit");
    result.setMnemonic('x');
    result.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        GUI.this.requestExit();
      }
    });
    return result;
  }

  protected JMenu createConfigureMenu() {
    JMenu configureMenu = new JMenu("Configure");
    configureMenu.setMnemonic('c');
    configureMenu.add(createConfigureMaxRecords());
    return configureMenu;
  }

  protected JMenuItem createConfigureMaxRecords() {
    JMenuItem result = new JMenuItem("Set Max Number of Records");
    result.setMnemonic('m');
    result.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        GUI.this.setMaxRecordConfiguration();
      }
    });
    result.addKeyListener(new KeyAdapter() {
      public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == 10)
          GUI.this.hide();
      }
    });
    return result;
  }

  protected void setMaxRecordConfiguration() {
    GUIInputDialog inputDialog = new GUIInputDialog(getBaseFrame(), "Set Max Number of Records", "", 10);

    String temp = inputDialog.getText();

    if (temp != null)
      try {
        setMaxNumberOfLogRecords(Integer.parseInt(temp));
      } catch (NumberFormatException e) {
        new GUIErrorDialog(getBaseFrame(), "'" + temp
            + "' is an invalid parameter.\nPlease try again.");

        setMaxRecordConfiguration();
      }
  }

  protected JMenu createHelpMenu() {
    JMenu helpMenu = new JMenu("Help");
    helpMenu.setMnemonic('h');
    helpMenu.add(createHelpAbout());
    helpMenu.add(createHelpProperties());
    StringBuffer a = new StringBuffer();
    a.toString();
    return helpMenu;
  }

  protected JMenuItem createHelpProperties() {
    String title = "Show Properties";
    JMenuItem result = new JMenuItem("Show Properties");
    result.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        GUI.this.showPropertiesDialog("Show Properties");
      }
    });
    return result;
  }

  protected JMenuItem createHelpAbout() {
    JMenuItem result = new JMenuItem("About "+NAME);
    result.setMnemonic('a');
    result.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        GUI.this.showAboutDialog("About "+NAME);
      }
    });
    return result;
  }

  protected void showPropertiesDialog(String title) {
    JOptionPane.showMessageDialog(_logMonitorFrame, _displayedLogBrokerProperties.toArray(), title, -1);
  }

  protected void showAboutDialog(String title) {
    JOptionPane.showMessageDialog(this._logMonitorFrame, new String[] { NAME, " ", VERSION, " ",
        "Developed by Tnarik Innael <tnarik@lecafeautomatique.co.uk>", " ",
        "This product includes software developed by the Apache Software Foundation (http://www.apache.org/). ", " ",
        "Based on Jakarta log4J LogFactor5, Contributed by ThoughtWorks Inc.", " ",
        "Copyright (C) The Apache Software Foundation. All rights reserved.", " ",
        "This software is published under the terms of the Apache Software",
        "License version 1.1, a copy of which has been included with this", "distribution in the LICENSE.txt file. ",
        " " }, title, -1);
  }

  protected JMenu createEditMenu() {
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
        GUI.this.findSearchText();
      }
    });
    return editFindNextMI;
  }

  protected JMenuItem createEditFindMI() {
    JMenuItem editFindMI = new JMenuItem("Find");
    editFindMI.setMnemonic('f');
    editFindMI.setAccelerator(KeyStroke.getKeyStroke("control F"));

    editFindMI.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        String inputValue = JOptionPane.showInputDialog(GUI.this._logMonitorFrame, "Find text: ",
            "Search Record Messages", 3);

        GUI.this.setSearchText(inputValue);
        GUI.this.findSearchText();
      }
    });
    return editFindMI;
  }

  protected JMenuItem createEditFilterTIDMI() {
    JMenuItem editFilterNDCMI = new JMenuItem("Filter by JMSCorrelationID");
    editFilterNDCMI.setMnemonic('t');
    editFilterNDCMI.setAccelerator(KeyStroke.getKeyStroke("control shift T"));
    editFilterNDCMI.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        String inputValue = JOptionPane.showInputDialog(GUI.this._logMonitorFrame,
            "Filter by this JMSCorrelationID: ", "Filter Log Records by tracking id", 3);

        GUI.this.setTIDTextFilter(inputValue);
        GUI.this.filterByTID();
        GUI.this._table.getFilteredLogTableModel().refresh();
        GUI.this.updateStatusLabel();
      }
    });
    return editFilterNDCMI;
  }

  protected JMenuItem createEditFilterBySelectedTIDMI() {
    JMenuItem editFilterTIDMI = new JMenuItem("Filter by Selected JMSCorrelationID");
    editFilterTIDMI.setMnemonic('s');
    editFilterTIDMI.setAccelerator(KeyStroke.getKeyStroke("control T"));
    editFilterTIDMI.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        ListSelectionModel lsm = GUI.this._table.getSelectionModel();

        if (!lsm.isSelectionEmpty()) {
          int selectedRow = lsm.getMinSelectionIndex();

          FilteredLogTableModel ftm = GUI.this._table.getFilteredLogTableModel();

          String sTID = (String) GUI.this._table.getModel().getValueAt(selectedRow, GUI.this._table.getTIDColumnID());
          if (sTID != null) {
            GUI.this.setTIDTextFilter(sTID);
            GUI.this.filterByTID();
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
    editFilterTIDMI.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        ListSelectionModel lsm = GUI.this._table.getSelectionModel();

        if (!lsm.isSelectionEmpty()) {
          int selectedRow = lsm.getMinSelectionIndex();

          FilteredLogTableModel ftm = GUI.this._table.getFilteredLogTableModel();

          String sHNF = (String) GUI.this._table.getModel().getValueAt(selectedRow, GUI.this._table.getConnHostnameColumnID());

          if (sHNF != null) {
            GUI.this.setConnHostnameTextFilter(sHNF);
            GUI.this.filterByConnHostname();
            ftm.refresh();
          }
        }
      }
    });
    return editFilterTIDMI;
  }

  protected JMenuItem createEditFilterBySelectedRevConnHN() {
    JMenuItem editFilterTIDMI = new JMenuItem("Remove Selected Conn Hostname");

    editFilterTIDMI.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        ListSelectionModel lsm = GUI.this._table.getSelectionModel();

        if (!lsm.isSelectionEmpty()) {
          int selectedRow = lsm.getMinSelectionIndex();

          FilteredLogTableModel ftm = GUI.this._table.getFilteredLogTableModel();

          String sHNF = (String) GUI.this._table.getModel().getValueAt(selectedRow, GUI.this._table.getConnHostnameColumnID());

          if (sHNF != null) {
            GUI.this.setConnHostnameTextFilter(sHNF);
            GUI.this.filterRemoveConnHostname();
            ftm.refresh();
          }
        }
      }
    });
    return editFilterTIDMI;
  }

  protected JMenuItem createEditFilterBySelectedSubjectMI() {
    JMenuItem editFilterSubjectMI = new JMenuItem("Filter by Selected Destination");
    editFilterSubjectMI.setMnemonic('y');
    editFilterSubjectMI.setAccelerator(KeyStroke.getKeyStroke("control Y"));
    editFilterSubjectMI.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        ListSelectionModel lsm = GUI.this._table.getSelectionModel();

        if (!lsm.isSelectionEmpty()) {
          int selectedRow = lsm.getMinSelectionIndex();

          FilteredLogTableModel ftm = GUI.this._table.getFilteredLogTableModel();

          String s = (String) GUI.this._table.getModel().getValueAt(selectedRow,
              GUI.this._table.getSubjectColumnID());
          if (s != null) {
            GUI.this.setSubjectTextFilter(s);
            GUI.this.filterBySubject();
            ftm.refresh();
          }
        }
      }
    });
    return editFilterSubjectMI;
  }

  protected void setTIDTextFilter(String text) {
    if (text == null)
      _JMSCorrelationIDTextFilter = "";
    else
      _JMSCorrelationIDTextFilter = text;
  }

  protected void setConnHostnameTextFilter(String text) {
    _ConnHostnameTextFilter = text;
  }

  protected void setSubjectTextFilter(String text) {
    _subjectTextFilter = text;
  }

  protected void filterByTID() {
    if ((_JMSCorrelationIDTextFilter == null) || (_JMSCorrelationIDTextFilter.length() == 0)) return;

    _table.getFilteredLogTableModel().setLogRecordFilter(createTIDLogRecordFilter(_JMSCorrelationIDTextFilter));
    _statusLabel.setText("Filtered by JMSCorrelationiD " + _JMSCorrelationIDTextFilter);
  }

  protected void filterByConnHostname() {
    if ((_ConnHostnameTextFilter == null) || (_ConnHostnameTextFilter.length() == 0)) return;

    _table.getFilteredLogTableModel().setLogRecordFilter(createConnHostNameRecordFilter(_ConnHostnameTextFilter));
    _statusLabel.setText("Filtered by Connnection Hostname " + _ConnHostnameTextFilter);
  }

  protected void filterRemoveConnHostname() {
    if ((_ConnHostnameTextFilter == null) || (_ConnHostnameTextFilter.length() == 0)) return;

    _table.getFilteredLogTableModel().setLogRecordFilter(createConnHostNameReverseRecordFilter(_ConnHostnameTextFilter));
    _statusLabel.setText("Filter Removed Connnection Hostname " + _ConnHostnameTextFilter);
  }

  protected void filterBySubject() {
    if ((_subjectTextFilter == null) || (_subjectTextFilter.length() == 0)) return;

    _table.getFilteredLogTableModel().setLogRecordFilter(createSubjectLogRecordFilter(_subjectTextFilter));
    _statusLabel.setText("Filtered by Destination  " + _subjectTextFilter);
  }

  protected LogRecordFilter createTIDLogRecordFilter(String text) {
    _JMSCorrelationIDTextFilter = text;
    LogRecordFilter result = new LogRecordFilter() {
      public boolean passes(LogRecord record) {
        String correlationID = record.getJMSCorrelationID();

        if ((correlationID == null) || (GUI._JMSCorrelationIDTextFilter == null)) return false;
        if (correlationID.indexOf(GUI._JMSCorrelationIDTextFilter) == -1) return false;
  
        CategoryPath path = new CategoryPath(record.getJMSDestination());
        return (GUI.this.getMenuItem(record.getType()).isSelected())
            && (GUI.this._subjectExplorerTree.getExplorerModel().isCategoryPathActive(path));
      }
    };
    return result;
  }

  protected LogRecordFilter createSubjectLogRecordFilter(String text) {
    _JMSCorrelationIDTextFilter = text;
    LogRecordFilter result = new LogRecordFilter() {
      public boolean passes(LogRecord record) {
        String subject = record.getJMSDestination();
        if ((subject == null) || (GUI._subjectTextFilter == null)) return false;
        if (subject.indexOf(GUI._subjectTextFilter) == -1) return false;
       
        CategoryPath path = new CategoryPath(subject);
        return (GUI.this.getMenuItem(record.getType()).isSelected())
            && (GUI.this._subjectExplorerTree.getExplorerModel().isCategoryPathActive(path));
      }
    };
    return result;
  }

  protected LogRecordFilter createConnHostNameRecordFilter(String text) {
    this._ConnHostnameTextFilter = text;
    LogRecordFilter result = new LogRecordFilter() {
      public boolean passes(LogRecord record) {
        String hostName = record.getConnectionHostName();
        if ((hostName == null) || (GUI.this._ConnHostnameTextFilter == null))
          return false;
        if (hostName.indexOf(GUI.this._ConnHostnameTextFilter) == -1) {
          return false;
        }
        String subject = record.getJMSDestination();
        CategoryPath path = new CategoryPath(subject);
        return (GUI.this.getMenuItem(record.getType()).isSelected())
            && (GUI.this._subjectExplorerTree.getExplorerModel().isCategoryPathActive(path));
      }
    };
    return result;
  }

  protected LogRecordFilter createConnHostNameReverseRecordFilter(String text) {
    _ConnHostnameTextFilter = text;
    LogRecordFilter result = new LogRecordFilter() {
      public boolean passes(LogRecord record) {
        String hostName = record.getConnectionHostName();
        if ((hostName == null) || (GUI.this._ConnHostnameTextFilter == null))
          return true;
        if (hostName.compareTo(GUI.this._ConnHostnameTextFilter) == 0) {
          return false;
        }
        String subject = record.getJMSDestination();
        CategoryPath path = new CategoryPath(subject);
        return (GUI.this.getMenuItem(record.getType()).isSelected())
            && (GUI.this._subjectExplorerTree.getExplorerModel().isCategoryPathActive(path));
      }
    };
    return result;
  }

  protected JMenuItem createEditRemoveAllFiltersTIDMI() {
    JMenuItem editRestoreAllNDCMI = new JMenuItem("Remove all filters");
    editRestoreAllNDCMI.setMnemonic('r');
    editRestoreAllNDCMI.setAccelerator(KeyStroke.getKeyStroke("control R"));
    editRestoreAllNDCMI.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        GUI.this._table.getFilteredLogTableModel().setLogRecordFilter(GUI.this.createLogRecordFilter());

        GUI.this.setTIDTextFilter("");
        GUI.this._table.getFilteredLogTableModel().refresh();
        GUI.this.updateStatusLabel();
      }
    });
    return editRestoreAllNDCMI;
  }

  protected JToolBar createToolBar() {
    JToolBar tb = new JToolBar();
    tb.putClientProperty("JToolBar.isRollover", Boolean.TRUE);
    JComboBox fontCombo = new JComboBox();
    JComboBox fontSizeCombo = new JComboBox();
    this._fontSizeCombo = fontSizeCombo;
    this._fontNameCombo = fontCombo;
    JComboBox rendererCombo = new JComboBox();
    this._rendererCombo = rendererCombo;

    _cl = getClass().getClassLoader();
    if (_cl == null) _cl = ClassLoader.getSystemClassLoader();

    URL newIconURL = _cl.getResource("channelexplorer_new.gif");
    ImageIcon newIcon = null;

    if (newIconURL != null) {
      newIcon = new ImageIcon(newIconURL);
    }

    JButton listenerButton = new JButton("Add Listener");

    if (newIcon != null) {
      listenerButton.setIcon(newIcon);
    }

    listenerButton.setToolTipText("Create new Listener.");

    listenerButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        GUI.this.requestNewListener(null);
      }
    });
    JButton newButton = new JButton("Clear Log Table");

    URL tcIconURL = _cl.getResource("trash.gif");

    ImageIcon tcIcon = null;

    if (tcIconURL != null) {
      tcIcon = new ImageIcon(tcIconURL);
    }

    if (newIcon != null) {
      newButton.setIcon(tcIcon);
    }

    newButton.setToolTipText("Clear Log Table.");

    newButton.addActionListener(getNewButtonActionListener());

    _pauseButton = new JButton("Pause all listeners");
    _pauseButton.addActionListener(getPauseButonActionListener());

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

  private void addRenderersToRendererCombo(JComboBox rendererCombo) {
    rendererCombo.addItem("uk.co.lecafeautomatique.zedogg.jms.MarshalJMSMsgToStringImpl");
    rendererCombo.addItem("uk.co.lecafeautomatique.zedogg.jms.MarshalJMSMsgToStringJMSStreamImpl");

    String env = System.getProperty("RENDERERS");
    try {
      if (env != null) {
        String[] result = env.split(",");
        for (int x = 0; x < result.length; x++)
          rendererCombo.addItem(result[x]);
      }
    } catch (RuntimeException e) {
    }
  }

  String getLastUsedRenderer() {
    if (this._lastUsedRenderer == null) {
      return "uk.co.lecafeautomatique.zedogg.jms.MarshalJMSMsgToStringImpl";
    }
    return this._lastUsedRenderer;
  }

  private void addFontsToFontCombo(JComboBox fontCombo) {
    Toolkit tk = Toolkit.getDefaultToolkit();

    String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();

    for (int j = 0; j < fonts.length; j++)
      fontCombo.addItem(fonts[j]);
  }

  private ActionListener getNewButtonActionListener() {
    return new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        GUI.this._table.clearLogRecords();
        GUI.this._subjectExplorerTree.getExplorerModel().resetAllNodeCounts();
        GUI.this.updateStatusLabel();
        GUI.this.clearDetailTextArea();
        LogRecord.resetSequenceNumber();
      }
    };
  }

  private ActionListener getPauseButonActionListener() {
    return new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (zeDogg.isPaused())
          GUI.this.resumeListeners();
        else
          GUI.this.pauseListeners();
      }
    };
  }

  private ActionListener getFontComboActionListener() {
    return new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        JComboBox box = (JComboBox) e.getSource();
        String font = (String) box.getSelectedItem();

        GUI.this.setFontName(font);
      }
    };
  }

  private void addFontSizesToCombo(JComboBox fontSizeCombo) {
    fontSizeCombo.addItem("8");
    fontSizeCombo.addItem("9");
    fontSizeCombo.addItem("10");
    fontSizeCombo.addItem("12");
    fontSizeCombo.addItem("14");
    fontSizeCombo.addItem("16");
    fontSizeCombo.addItem("18");
    fontSizeCombo.addItem("24");
  }

  private ActionListener getFontSizeComboActionListener() {
    return new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        JComboBox box = (JComboBox) e.getSource();
        String size = (String) box.getSelectedItem();
        int s = Integer.valueOf(size).intValue();

        GUI.this.setFontSizeSilently(s);
        GUI.this.refreshDetailTextArea();
        GUI.this.setFontSize(s);
      }
    };
  }

  private ActionListener getRendererActionListeener() {
    return new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        JComboBox box = (JComboBox) e.getSource();
        String rendererClass = (String) box.getSelectedItem();
        try {
          GUI.this.updateRenderClass(rendererClass);
        } catch (Exception ex) {
          GUI.this._statusLabel.setText("Renderer exception " + ex.getMessage());
          new GUIErrorDialog(GUI.this.getBaseFrame(), "Error creating renderer : " + ex.getLocalizedMessage());

          return;
        }

        GUI.this.setLastUsedRenderer(rendererClass);

        GUI.this._statusLabel.setText("Renderer " + rendererClass + " Selected");
      }
    };
  }

  private void setButtonAlignment(JButton newButton) {
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
        JComboBox box = (JComboBox) e.getSource();
        EventActionType level = (EventActionType) box.getSelectedItem();
        GUI.this.setLeastSevereDisplayedLogLevel(level);
      }
    });
    result.setMaximumSize(result.getPreferredSize());
    return result;
  }

  protected void setLeastSevereDisplayedLogLevel(EventActionType level) {
    if ((level == null) || (this._leastSevereDisplayedMsgType.equals(level))) return;

    this._leastSevereDisplayedMsgType = level;
    this._table.getFilteredLogTableModel().refresh();
    updateStatusLabel();
  }

  protected static void centerFrame(JFrame frame) {
    Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension comp = frame.getSize();

    frame.setLocation((screen.width - comp.width) / 2, (screen.height - comp.height) / 2);
  }

  Rectangle getWindowBounds() {
    return getBaseFrame().getBounds();
  }

  void setWindowBounds(Rectangle r) {
    getBaseFrame().setBounds(r);
  }

  protected void requestNewListener(JMSParameters p) {
    GUIErrorDialog error;
    try {
      GUITransportInputDialog inputDialog = null;
      inputDialog = new GUITransportInputDialog(getBaseFrame(), "Add  Listener", (p != null)? p : this._lastUsedParameters);

      if (inputDialog.isOK()) {
        this._lastUsedParameters = inputDialog.getParameters();

        zeDogg.listen(this._lastUsedParameters);
        updateTitle();
      }

    } catch (JMSException ex) {
      error = new GUIErrorDialog(getBaseFrame(), "Error creating listener : " + ex.getMessage());
    }
  }

  protected void requestCloseListener() {
    updateTitle();
  }

  protected void requestClose() {
    setCallSystemExitOnClose(true);
    closeAfterConfirm();
  }


  protected void requestExit() {
    setCallSystemExitOnClose(true);
    closeAfterConfirm();
  }

  protected void closeAfterConfirm() {
    StringBuffer message = new StringBuffer();

    message.append("Are you sure you want to exit?\n");

    int value = JOptionPane.showConfirmDialog(this._logMonitorFrame, message.toString(),
        "Are you sure you want to exit?", 2, 3, null);

    if (value == 0)
      dispose();
  }

  protected Iterator getMsgTypes() {
    return this._levels.iterator();
  }

  protected Iterator getLogTableColumns() {
    return this._columns.iterator();
  }

  Iterator getSubscriptions() {
    return zeDogg.getTransports().iterator();
  }

  protected static boolean loadLogFile(File file) {
    boolean ok = true;

    return ok;
  }

  protected static boolean loadLogFile(URL url) {
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

  protected void setSplitPaneVertical(JSplitPane _splitPaneVertical) {
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

  void setSplitPaneTableViewer(JSplitPane _splitPaneTableViewer) {
    this._splitPaneTableViewer = _splitPaneTableViewer;
  }

  void setSplitPaneTableViewerPos(int location) {
    this._splitPaneTableViewer.setDividerLocation(location);
  }

  public void onMessage(Message msg) {
    addMessage(LogRecord.create(msg));
  }

  void setLastUsedRenderer(String rendererClass) {
    if (rendererClass != null)
      this._lastUsedRenderer = rendererClass;
  }

  private void updateRenderClass(String rendererClass) throws ClassNotFoundException, InstantiationException,
      IllegalAccessException {
    Class c = Class.forName(rendererClass);
    Object o = c.newInstance();
    if ((o instanceof MarshalJMSToString))
      _marshalImpl.setImpl((MarshalJMSToString) o);
    else
      this._statusLabel.setText("Class " + rendererClass + " does not implement IMarshalJMSToString");
  }

  private class AddLogRecordRunnable implements Runnable {
    private final LogRecord lr;

    public AddLogRecordRunnable(LogRecord r) {
      lr = r;
    }

    public void run() {
      GUI.this._subjectExplorerTree.getExplorerModel().addLogRecord(lr);
      GUI.this._table.getFilteredLogTableModel().addLogRecord(lr);
      GUI.this.updateStatusLabel();
    }
  }

  class LogBrokerMonitorWindowAdaptor extends WindowAdapter {
    protected GUI _monitor;

    public LogBrokerMonitorWindowAdaptor(GUI monitor) {
      this._monitor = monitor;
    }

    public void windowClosing(WindowEvent ev) {
      this._monitor.requestClose();
    }
  }
}
