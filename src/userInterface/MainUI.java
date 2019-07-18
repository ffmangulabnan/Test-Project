package userInterface;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import org.apache.commons.io.FileUtils;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.text.Document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JMenuBar;
import database.DBProcess;
import database.PasswordEncryptDecrypt;
import database.TestMachineConfig;
import selenium.TestDriver;

import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.Component;

import javax.swing.border.LineBorder;
import javax.swing.JLabel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

public class MainUI {

	private static JFrame frame;
	private static JButton btnAdd;
	private static JButton btnDelete;
	private static JButton btnReset;
	private static JButton btnExport;
	private static JButton btnRun;
	private static JTable cTable;
	private static JTree navigationTree;

	private static TestMachineConfig dbConfig = new TestMachineConfig();
	private static DBProcess dbProcess = new DBProcess("");
	private static String dbPath = "";
	private static String path = new File("").getAbsolutePath();
	private static JTextField textName;
	private static JTextArea textArea;
	private static JPanel namePanel;
	private static String copyTC = null;
	private static String copySteps = null;
	private static int[] modColWidth = new int[] { 300, 500 };
	private static int[] tcColWidth = new int[] { 300, 500 };
	private static int[] stepsColWidth = new int[] { 50, 200, 60, 100, 100, 100, 100, 50 };
	private static int[] runColWidth = new int[] { 200, 50, 100, 100, 100, 100, 100 };
	private static int[] runmodColWidth = new int[] { 200, 200, 100, 100, 100, 50 };
	private static int[] runtcColWidth = new int[] { 200, 200, 100, 100, 100, 50 };
	private static int[] runstepsColWidth = new int[] { 50, 200, 60, 100, 100, 100, 100, 50, 50, 50 };
	private static JSplitPane splitPane_1;
	private static JButton btnRefresh;
	private static JButton btnVariable;
	private static String adminAccess = null;
	private static boolean adminPass = false;
	private static JMenuItem subMenuNew;
	private static JMenu subToolMenuMod;
	private static JMenuItem subToolMenuModView;
	private static JMenuItem subToolMenuModImport;
	private static JMenuItem subToolMenuVar;
	private static JMenuItem subToolMenuActions;
	private static JMenuItem subMenuExitAdmin;
	private static JMenuItem subMenuAdmin;
	private static JMenu subMenuloadRecent;
	private PasswordEncryptDecrypt encrypt = new PasswordEncryptDecrypt();
	private static JButton btnImport;
	private static Thread autobackup = null;
	private static JButton btnUp;
	private static JButton btnDown;
	private static JButton btnInsert;
	private static DefaultMutableTreeNode runNode;
	private static DefaultMutableTreeNode modNode;
	private JPanel tablePanel;
	private JSplitPane splitPane_2;
	private JPanel panel_1;
	private JPanel panel_2;
	private static JTabbedPane tabbedPane;
	private JPanel buttonsPanel;
	private static String strTheme;

	private static Font buttonsFont;
	
	public static Color tableHeaderBg;
	public static Color tableHeaderFg;
	public static Font tablHeadereFont;
	public static Color tableCellBg;
	public static Color tableCellFg;
	public static Font tableFont;
	public static Color tableGridColor;
	public static Color tableHighlightSelBg;
	public static Color tableHighlightSelFg;
	
	public static Color treeBg;
	public static Color treeFg;
	public static Font treeFont;
	public static Color highlightSelection;
	
	private static Color detailsBg;
	private static Color detailsFg;
	private static Font detailsFont;
	private static Color textDetailsBg;
	private static Color textDetailsFg;
	private static Font textDetailsFont;
	
	private static Color logsBg;
	private static Color logsFg;
	private static Font logsFont;
	public static MainUI main;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					strTheme = dbConfig.getConfigValue("UI_THEME");		
					loadTheme(false);
					UIManager.put("Tree.leafIcon", UIManager.getIcon("FileView.fileIcon"));
					main = new MainUI();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainUI() {

		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		JFrame.setDefaultLookAndFeelDecorated(true);
		JDialog.setDefaultLookAndFeelDecorated(true);

		// reset email credential if run on different machine/user
		String user = System.getProperty("user.name");
		if (!dbConfig.getConfigValue("USER").equalsIgnoreCase(user)) {
			dbConfig.updateUser(user);
			dbConfig.resetEmailCredential();
			dbConfig.deleteRecent();
		}
		String pass = dbConfig.getConfigValue("ADMIN");
		if (!pass.contentEquals("")) {
			adminAccess = encrypt.decryptPassword(dbConfig.getConfigValue("ADMIN"));
		}
		
		frame = new JFrame("Test Machine");
		frame.setBounds(100, 100, 733, 488);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1000, 600);
		frame.setMinimumSize(new Dimension(1000, 600));
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 5, 0, 0, 0, 0, 0, 25, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 10, 0, 15, 20, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, 1.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
				1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0 };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0,
				0.0, 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE };
		frame.getContentPane().setLayout(gridBagLayout);

		JSplitPane splitPane = new JSplitPane();
		splitPane.setContinuousLayout(true);
		splitPane.setOneTouchExpandable(true);
		GridBagConstraints gbc_splitPane = new GridBagConstraints();
		gbc_splitPane.gridwidth = 29;
		gbc_splitPane.gridheight = 19;
		gbc_splitPane.insets = new Insets(0, 0, 5, 5);
		gbc_splitPane.fill = GridBagConstraints.BOTH;
		gbc_splitPane.gridx = 0;
		gbc_splitPane.gridy = 0;
		frame.getContentPane().add(splitPane, gbc_splitPane);
		splitPane.setBorder(null);
		// create navigation tree
		navigationTree = new JTree();
		navigationTree.setFont(treeFont);
		navigationTree.setForeground(treeFg);
		navigationTree.setBackground(treeBg);
		navigationTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		navigationTree.setRootVisible(false);
		navigationTree.setModel(new DefaultTreeModel(null));
		navigationTree.setCellRenderer(new MyCellRenderer(highlightSelection));

		JScrollPane scrollPane_2 = new JScrollPane();
		splitPane.setLeftComponent(scrollPane_2);
		scrollPane_2.setViewportView(navigationTree);

		splitPane_1 = new JSplitPane();
		splitPane_1.setBorder(null);
		splitPane_1.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane.setRightComponent(splitPane_1);
		namePanel = new JPanel();
		namePanel.setBackground(detailsBg);
		splitPane_1.setLeftComponent(namePanel);
		namePanel.setBorder(new LineBorder(detailsBg));
		namePanel.setVisible(false);

		GridBagLayout gbl_namePanel = new GridBagLayout();
		gbl_namePanel.columnWidths = new int[] { 10, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 0, 10 };
		gbl_namePanel.rowHeights = new int[] { 10, 0, 0, 0, 0, 0 };
		gbl_namePanel.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
				0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		gbl_namePanel.rowWeights = new double[] { 0.0, 0.0, 0.0, 1.0, 1.0, Double.MIN_VALUE };
		namePanel.setLayout(gbl_namePanel);

		JLabel lblName = new JLabel("Name :");
		lblName.setForeground(detailsFg);
		lblName.setFont(detailsFont);
		lblName.setVerticalAlignment(SwingConstants.TOP);
		GridBagConstraints gbc_lblName = new GridBagConstraints();
		gbc_lblName.anchor = GridBagConstraints.WEST;
		gbc_lblName.gridwidth = 3;
		gbc_lblName.insets = new Insets(0, 0, 5, 5);
		gbc_lblName.gridx = 1;
		gbc_lblName.gridy = 1;
		namePanel.add(lblName, gbc_lblName);
		textName = new BPosTextField();
		textName.setBackground(textDetailsBg);
		textName.setFont(textDetailsFont);
		GridBagConstraints gbc_textName = new GridBagConstraints();
		gbc_textName.gridwidth = 26;
		gbc_textName.insets = new Insets(0, 0, 5, 5);
		gbc_textName.fill = GridBagConstraints.HORIZONTAL;
		gbc_textName.gridx = 4;
		gbc_textName.gridy = 1;
		namePanel.add(textName, gbc_textName);
		textName.setColumns(10);
		textName.addFocusListener(new FocusListener() {
			public String strOld;
			public String nId;
			public String tableName;

			public void focusGained(FocusEvent e) {
				strOld = textName.getText();
				DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) navigationTree
						.getLastSelectedPathComponent();
				if (selectedNode != null) {
					CustomUserObject tNode = (CustomUserObject) selectedNode.getUserObject();
					nId = tNode.getId();
					DefaultTableModel tNewModel = (DefaultTableModel) cTable.getModel();
					tableName = tNewModel.getColumnName(0);
				}
			};

			public void focusLost(FocusEvent e) {
				DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) navigationTree
						.getLastSelectedPathComponent();
				if (selectedNode != null) {
					CustomUserObject tNode = (CustomUserObject) selectedNode.getUserObject();
					if (nId.contentEquals(tNode.getId()) && tableName.contentEquals(cTable.getModel().getColumnName(0))
							&& !textName.getText().contentEquals(strOld)) {
						updateNode(nId, textName.getText());
						if (tableName.contentEquals("TCSTEPS")) {
							// update tc
							dbProcess.updateTestName(nId, textName.getText());
						} else if (tableName.contentEquals("TCS")) {
							// update module
							dbProcess.updateModName(nId, textName.getText());
						}
					}
				}
			}
		});

		JLabel lblDescription = new JLabel("Description :");
		lblDescription.setForeground(detailsFg);
		lblDescription.setFont(detailsFont);
		lblDescription.setVerticalAlignment(SwingConstants.TOP);
		GridBagConstraints gbc_lblDescription = new GridBagConstraints();
		gbc_lblDescription.gridheight = 2;
		gbc_lblDescription.anchor = GridBagConstraints.WEST;
		gbc_lblDescription.gridwidth = 3;
		gbc_lblDescription.insets = new Insets(0, 0, 5, 5);
		gbc_lblDescription.gridx = 1;
		gbc_lblDescription.gridy = 2;
		namePanel.add(lblDescription, gbc_lblDescription);

		JScrollPane scrollPane_1 = new JScrollPane();
		GridBagConstraints gbc_scrollPane_1 = new GridBagConstraints();
		gbc_scrollPane_1.insets = new Insets(0, 0, 5, 5);
		gbc_scrollPane_1.gridheight = 2;
		gbc_scrollPane_1.gridwidth = 26;
		gbc_scrollPane_1.fill = GridBagConstraints.BOTH;
		gbc_scrollPane_1.gridx = 4;
		gbc_scrollPane_1.gridy = 2;
		namePanel.add(scrollPane_1, gbc_scrollPane_1);

		textArea = new BPosTextArea();
		textArea.setBackground(textDetailsBg);
		textArea.setFont(textDetailsFont);
		textArea.setForeground(textDetailsFg);
		textArea.setLineWrap(true);
		
		//textArea.setBorder(rBorder);
		scrollPane_1.setViewportView(textArea);
		scrollPane_1.setBorder(null);
		scrollPane_1.getViewport().setBackground(textDetailsBg);																				
		tablePanel = new JPanel();
		tablePanel.setBorder(null);
		splitPane_1.setRightComponent(tablePanel);
		GridBagLayout gbl_tablePanel = new GridBagLayout();
		gbl_tablePanel.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 21, 0};
		gbl_tablePanel.rowHeights = new int[]{46, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 31, 0};
		gbl_tablePanel.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_tablePanel.rowWeights = new double[]{0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		tablePanel.setLayout(gbl_tablePanel);
		
		splitPane_2 = new JSplitPane();
		splitPane_2.setOneTouchExpandable(true);
		splitPane_2.setOrientation(JSplitPane.VERTICAL_SPLIT);

		GridBagConstraints gbc_splitPane_2 = new GridBagConstraints();
		gbc_splitPane_2.gridheight = 12;
		gbc_splitPane_2.insets = new Insets(0, 0, 5, 0);
		gbc_splitPane_2.gridwidth = 29;
		gbc_splitPane_2.fill = GridBagConstraints.BOTH;
		gbc_splitPane_2.gridx = 0;
		gbc_splitPane_2.gridy = 0;
		tablePanel.add(splitPane_2, gbc_splitPane_2);
		
		panel_1 = new JPanel();
		splitPane_2.setLeftComponent(panel_1);
		GridBagLayout gbl_panel_1 = new GridBagLayout();
		gbl_panel_1.columnWidths = new int[]{0, 0};
		gbl_panel_1.rowHeights = new int[]{0, 10, 0};
		gbl_panel_1.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_panel_1.rowWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		panel_1.setLayout(gbl_panel_1);
		
		buttonsPanel = new JPanel();
		GridBagConstraints gbc_buttonsPanel = new GridBagConstraints();
		gbc_buttonsPanel.insets = new Insets(0, 0, 5, 0);
		gbc_buttonsPanel.fill = GridBagConstraints.BOTH;
		gbc_buttonsPanel.gridx = 0;
		gbc_buttonsPanel.gridy = 0;
		panel_1.add(buttonsPanel, gbc_buttonsPanel);
		GridBagLayout gbl_buttonsPanel = new GridBagLayout();
		gbl_buttonsPanel.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_buttonsPanel.rowHeights = new int[]{0, 0};
		gbl_buttonsPanel.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_buttonsPanel.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		buttonsPanel.setLayout(gbl_buttonsPanel);
		
		btnRun = new JButton("Run");
		btnRun.setFont(buttonsFont);
		GridBagConstraints gbc_btnRun = new GridBagConstraints();
		gbc_btnRun.insets = new Insets(0, 0, 0, 5);
		gbc_btnRun.gridx = 0;
		gbc_btnRun.gridy = 0;
		buttonsPanel.add(btnRun, gbc_btnRun);
		//btnRun.setUI(new MyJButton());
		btnRun.setMargin(new Insets(4, 4, 4, 4));

		btnRun.addActionListener(btnRunActionListener());
		btnRun.setVisible(false);
		
		btnReset = new JButton("Reset");
		btnReset.setFont(buttonsFont);
		GridBagConstraints gbc_btnReset = new GridBagConstraints();
		gbc_btnReset.insets = new Insets(0, 0, 0, 5);
		gbc_btnReset.gridx = 1;
		gbc_btnReset.gridy = 0;
		buttonsPanel.add(btnReset, gbc_btnReset);
		//btnReset.setUI(new MyJButton());
		btnReset.setMargin(new Insets(4, 4, 4, 4));

		btnReset.addActionListener(btnResetActionListener());
		btnReset.setVisible(false);
		
		btnExport = new JButton("Export");
		btnExport.setFont(buttonsFont);
		GridBagConstraints gbc_btnExport = new GridBagConstraints();
		gbc_btnExport.insets = new Insets(0, 0, 0, 5);
		gbc_btnExport.gridx = 2;
		gbc_btnExport.gridy = 0;
		buttonsPanel.add(btnExport, gbc_btnExport);
		//btnExport.setUI(new MyJButton());
		btnExport.setMargin(new Insets(4, 4, 4, 4));

		btnExport.addActionListener(btnExportActionListener());
		btnExport.setVisible(false);
		
		// button inside toolbar
		btnAdd = new JButton("Add");
		btnAdd.setFont(buttonsFont);
		GridBagConstraints gbc_btnAdd = new GridBagConstraints();
		gbc_btnAdd.insets = new Insets(0, 0, 0, 5);
		gbc_btnAdd.gridx = 3;
		gbc_btnAdd.gridy = 0;
		buttonsPanel.add(btnAdd, gbc_btnAdd);
		//btnAdd.setUI(new MyJButton());
		btnAdd.setMargin(new Insets(4, 4, 4, 4));
		
		btnAdd.addActionListener(btnAddActionListener());
		
		btnAdd.setVisible(false);
		
		btnInsert = new JButton("Insert");
		btnInsert.setFont(buttonsFont);
		GridBagConstraints gbc_btnInsert = new GridBagConstraints();
		gbc_btnInsert.insets = new Insets(0, 0, 0, 5);
		gbc_btnInsert.gridx = 4;
		gbc_btnInsert.gridy = 0;
		buttonsPanel.add(btnInsert, gbc_btnInsert);
		//btnInsert.setUI(new MyJButton());
		btnInsert.setMargin(new Insets(4, 4, 4, 4));

		btnInsert.addActionListener(btnInsertActionListener());
		
		btnDelete = new JButton("Delete");
		btnDelete.setFont(buttonsFont);
		GridBagConstraints gbc_btnDelete = new GridBagConstraints();
		gbc_btnDelete.insets = new Insets(0, 0, 0, 5);
		gbc_btnDelete.gridx = 5;
		gbc_btnDelete.gridy = 0;
		buttonsPanel.add(btnDelete, gbc_btnDelete);

		btnDelete.addActionListener(btnDeleteActionListener());
		btnDelete.setVisible(false);
		//btnDelete.setUI(new MyJButton());
		btnDelete.setMargin(new Insets(4, 4, 4, 4));
		
		btnVariable = new JButton("Test Data");
		btnVariable.setFont(buttonsFont);
		GridBagConstraints gbc_btnVariable = new GridBagConstraints();
		gbc_btnVariable.insets = new Insets(0, 0, 0, 5);
		gbc_btnVariable.gridx = 6;
		gbc_btnVariable.gridy = 0;
		buttonsPanel.add(btnVariable, gbc_btnVariable);

		btnVariable.setVisible(false);
		btnVariable.addActionListener(btnViewVarActionListener());
		//btnVariable.setUI(new MyJButton());
		btnVariable.setMargin(new Insets(4, 4, 4, 4));
		
		btnRefresh = new JButton("Refresh");
		btnRefresh.setFont(buttonsFont);
		GridBagConstraints gbc_btnRefresh = new GridBagConstraints();
		gbc_btnRefresh.insets = new Insets(0, 0, 0, 5);
		gbc_btnRefresh.gridx = 7;
		gbc_btnRefresh.gridy = 0;
		buttonsPanel.add(btnRefresh, gbc_btnRefresh);
		//btnRefresh.setUI(new MyJButton());
		btnRefresh.setMargin(new Insets(4, 4, 4, 4));

		
		btnRefresh.addActionListener(btnRefreshActionListener());
		btnRefresh.setVisible(false);
		
		btnImport = new JButton("Import");
		btnImport.setFont(buttonsFont);
		GridBagConstraints gbc_btnImport = new GridBagConstraints();
		gbc_btnImport.insets = new Insets(0, 0, 0, 5);
		gbc_btnImport.gridx = 8;
		gbc_btnImport.gridy = 0;
		buttonsPanel.add(btnImport, gbc_btnImport);
		//btnImport.setUI(new MyJButton());
		btnImport.setMargin(new Insets(4, 4, 4, 4));

		//addLogsTab("19-testMath-Jan-19-2018 03.18.24");
		
		btnImport.setVisible(false);
		btnImport.addActionListener(btnImportActionListener());
		
		btnUp = new JButton("Up");
		btnUp.setFont(buttonsFont);
		GridBagConstraints gbc_btnUp = new GridBagConstraints();
		gbc_btnUp.insets = new Insets(0, 0, 0, 5);
		gbc_btnUp.gridx = 9;
		gbc_btnUp.gridy = 0;
		buttonsPanel.add(btnUp, gbc_btnUp);
		//btnUp.setUI(new MyJButton());
		btnUp.setMargin(new Insets(4, 4, 4, 4));

		btnUp.addActionListener(btnUpActionListener());
		
		btnDown = new JButton("Down");
		btnDown.setFont(buttonsFont);
		GridBagConstraints gbc_btnDown = new GridBagConstraints();
		gbc_btnDown.gridx = 10;
		gbc_btnDown.gridy = 0;
		buttonsPanel.add(btnDown, gbc_btnDown);
		//btnDown.setUI(new MyJButton());
		btnDown.setMargin(new Insets(4, 4, 4, 4));

		btnDown.addActionListener(btnDownActionListener());

		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 1;
		panel_1.add(scrollPane, gbc_scrollPane);
		scrollPane.setViewportBorder(null);
		scrollPane.setBackground(Color.LIGHT_GRAY);
		// create table
		cTable = new JTable();
		cTable.setBorder(new LineBorder(tableGridColor));
		cTable.setFont(tableFont);
		scrollPane.setViewportView(cTable);
		cTable.addMouseListener(tableMouseListener());
		cTable.getTableHeader().addMouseListener(tableHeaderMouseListener());
		cTable.setGridColor(tableGridColor);
		cTable.setShowGrid(true);
		JTableHeader header = cTable.getTableHeader();
		
		panel_2 = new JPanel();
		panel_2.setBorder(null);
		splitPane_2.setRightComponent(panel_2);
		splitPane_2.setDividerLocation(400);
		GridBagLayout gbl_panel_2 = new GridBagLayout();
		gbl_panel_2.columnWidths = new int[]{0, 0};
		gbl_panel_2.rowHeights = new int[]{64, 0};
		gbl_panel_2.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_panel_2.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		panel_2.setLayout(gbl_panel_2);
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		GridBagConstraints gbc_tabbedPane = new GridBagConstraints();
		gbc_tabbedPane.fill = GridBagConstraints.BOTH;
		gbc_tabbedPane.gridx = 0;
		gbc_tabbedPane.gridy = 0;
		panel_2.add(tabbedPane, gbc_tabbedPane);
		tabbedPane.setTabLayoutPolicy(1);
		tabbedPane.setFocusable(false);
		
		textArea.addFocusListener(new FocusListener() {
			public String strOld;
			public String nId;
			public String tableName;

			public void focusGained(FocusEvent e) {
				strOld = textArea.getText();
				DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) navigationTree
						.getLastSelectedPathComponent();
				if (selectedNode != null) {
					CustomUserObject tNode = (CustomUserObject) selectedNode.getUserObject();
					nId = tNode.getId();
					DefaultTableModel tNewModel = (DefaultTableModel) cTable.getModel();
					tableName = tNewModel.getColumnName(0);
				}
			};

			public void focusLost(FocusEvent e) {
				DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) navigationTree
						.getLastSelectedPathComponent();
				if (selectedNode != null) {
					CustomUserObject tNode = (CustomUserObject) selectedNode.getUserObject();
					if (nId.contentEquals(tNode.getId()) && tableName.contentEquals(cTable.getModel().getColumnName(0))
							&& !textArea.getText().contentEquals(strOld)) {
						if (tableName.contentEquals("TCSTEPS")) {
							// update tc
							dbProcess.updateTestDesc(nId, textArea.getText());
						} else if (tableName.contentEquals("TCS")) {
							// update module
							dbProcess.updateModDesc(nId, textArea.getText());
						}
					}
				}
			}
		});
	    header.setDefaultRenderer(new HeaderRenderer(tableHeaderBg,tableHeaderFg,tablHeadereFont,tableGridColor));
		
		// create Menu bar
		JMenuBar menuBar = new JMenuBar();
		// FILE
		JMenu menuFile = new JMenu("File");
		menuBar.add(menuFile);
		menuFile.setMnemonic('F');
		// FILE Sub Menu
		subMenuNew = new JMenuItem("New");
		subMenuNew.setMnemonic('N');
		menuFile.add(subMenuNew);
		subMenuNew.addActionListener(createTestSuiteActionListener());
		// Open File
		JMenuItem subMenuload = new JMenuItem("Open File...");
		subMenuload.setMnemonic('O');
		menuFile.add(subMenuload);
		subMenuload.addActionListener(loadTestSuiteActionListener());
		// Open Recent File
		subMenuloadRecent = new JMenu("Open Recent File");
		subMenuloadRecent.setMnemonic('R');
		menuFile.add(subMenuloadRecent);
		//subMenuRecent();
		
		subMenuExitAdmin = new JMenuItem("Exit Admin");
		subMenuExitAdmin.setMnemonic('X');
		menuFile.add(subMenuExitAdmin);
		subMenuExitAdmin.addActionListener(adminExitActionListener());

		subMenuAdmin = new JMenuItem("Admin");
		subMenuAdmin.setMnemonic('A');
		menuFile.add(subMenuAdmin);
		subMenuAdmin.addActionListener(adminPassActionListener());
		if (adminPass) {
			subMenuAdmin.setVisible(false);
			subMenuExitAdmin.setVisible(true);
		} else {
			subMenuAdmin.setVisible(true);
			subMenuExitAdmin.setVisible(false);
		}

		// TOOL
		JMenu menuTool = new JMenu("Tools");
		menuTool.setMnemonic('T');
		menuBar.add(menuTool);
		
		// TOOL Sub Menu Option
		JMenuItem subToolMenuNew = new JMenuItem("Option");
		subToolMenuNew.setMnemonic('O');
		menuTool.add(subToolMenuNew);
		subToolMenuNew.addActionListener(openConfigWindow());
		
		// TOOL Sub Menu Actions
		subToolMenuActions = new JMenuItem("Actions");
		subToolMenuActions.setMnemonic('A');
		menuTool.add(subToolMenuActions);
		subToolMenuActions.addActionListener(openActionWindow());
		
		// TOOL Sub Menu Actions
		subToolMenuMod = new JMenu("Modules");
		subToolMenuModView = new JMenuItem("View/Edit");
		subToolMenuModImport = new JMenuItem("Import");
		subToolMenuMod.setMnemonic('M');
		menuTool.add(subToolMenuMod);
		subToolMenuMod.add(subToolMenuModView);
		subToolMenuModView.setMnemonic('V');
		subToolMenuMod.add(subToolMenuModImport);
		subToolMenuModImport.setMnemonic('I');
		
		subToolMenuModView.addActionListener(openModulesWindow());
		subToolMenuModImport.addActionListener(openModulesImportWindow());
		// TOOL Sub Menu Actions
		subToolMenuVar = new JMenuItem("Test Data");
		subToolMenuVar.setMnemonic('V');
		menuTool.add(subToolMenuVar);
		
		// HELP
		JMenu menuHelp = new JMenu("Help");
		menuHelp.setMnemonic('H');
		menuBar.add(menuHelp);
		// HELP Sub Menu
		JMenuItem subHelpMenuNew = new JMenuItem("About");
		subHelpMenuNew.setMnemonic('A');
		menuHelp.add(subHelpMenuNew);
		frame.setJMenuBar(menuBar);
		subHelpMenuNew.addActionListener(aboutWindow());
		try {
			File f = new File(dbConfig.getRecentFile());
			if (f.exists() && !f.isDirectory()) {
				String dbName = dbConfig.getRecentFile();
				//String pathName = dbName.replaceAll("\\\\", "\\\\\\\\");
				DBProcess dbProcessTemp = new DBProcess(dbName);
				if(dbProcessTemp.dbConn!=null) {
					dbProcess = dbProcessTemp;
					dbPath = dbName;
					dbConfig.updateLastOpen(dbName);
					frame.setTitle("Test Machine - " + dbName);
					createRootNode();
					autobackup = autoBackup(dbName);
				}
			}
		}catch(Exception e) {}
		
		if (adminPass) {
			subMenuNew.setVisible(true);
			subToolMenuMod.setVisible(true);
			subToolMenuVar.setVisible(true);
			subToolMenuActions.setVisible(true);
			
		} else {
			subMenuNew.setVisible(false);
			subToolMenuMod.setVisible(false);
			subToolMenuVar.setVisible(false);
			subToolMenuActions.setVisible(false);
		}
	}
	public static void subMenuRecent() {
		subMenuloadRecent.removeAll();
		// recent sub menu
		List<String[]> recent = dbConfig.getRecentFiles();

		for (String[] file : recent) {
			JMenuItem subRecMenu = new JMenuItem(file[0]);
			subRecMenu.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					loadRecentTestSuite(file[0]);
				}
			});
			subMenuloadRecent.add(subRecMenu);
		}
	}
	public static void loadTestSuite() {
		String appPath = new File("").getAbsolutePath();
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File(appPath + "\\Test Suite"));
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Test Suite", "db"));
		fileChooser.setAcceptAllFileFilterUsed(false);

		int result = fileChooser.showOpenDialog(fileChooser);
		if (result == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileChooser.getSelectedFile();
			if (!selectedFile.isFile()) {
				JOptionPane.showMessageDialog(frame, "Test Suite not found: " + selectedFile, "Error",
						JOptionPane.ERROR_MESSAGE);
			} else {
				String dbName = selectedFile.getAbsolutePath();
				//String pathName = dbName.replaceAll("\\\\", "\\\\\\\\");
				DBProcess dbProcessTemp = new DBProcess(dbName);
				if(dbProcessTemp.dbConn!=null) {
					dbProcess = dbProcessTemp;
					dbPath = dbName;
					dbConfig.updateLastOpen(dbName);
					frame.setTitle("Test Machine - " + dbName);
					createRootNode();
					autobackup = autoBackup(dbName);
				}
			}
		}
	}

	public static void loadRecentTestSuite(String file) {
		//String pathName = file.replaceAll("\\\\", "\\\\\\\\");

		DBProcess dbProcessTemp = new DBProcess(file);
		if(dbProcessTemp.dbConn!=null) {
			dbProcess = dbProcessTemp;
			dbPath = file;
			dbConfig.updateLastOpen(file);
			frame.setTitle("Test Machine - " + file);
			createRootNode();
			autobackup = autoBackup(file);
		} else {
			JOptionPane.showMessageDialog(frame, "File not found!", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	public static void createTestSuite(String tsName) {
		String tsPath = new File("").getAbsolutePath() + "\\Test Suite\\" + tsName + ".db";
		File f = new File(tsPath);
		if (f.exists() && !f.isDirectory()) {
			JOptionPane.showMessageDialog(frame, "Test Suite name is already exist!", "Error",
					JOptionPane.ERROR_MESSAGE);
		} else {
			DBProcess dbProcessTemp = new DBProcess(tsPath);
			if(dbProcessTemp.dbConn!=null) {
				dbProcess = dbProcessTemp;
				dbPath = tsPath;
				dbConfig.updateLastOpen(tsPath);
				frame.setTitle("Test Machine - " + tsPath);
				dbProcess.createTestSuite();
				createRootNode();
				autobackup = autoBackup(tsPath);
			}
		}
	}

	public static ActionListener createTestSuiteActionListener() {
		ActionListener action = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean inputAccepted = false;
				while (!inputAccepted) {
					String tsName = JOptionPane.showInputDialog(frame, "Please input name: ", "Create New Test Suite",
							JOptionPane.PLAIN_MESSAGE);
					if (tsName == null) {
						inputAccepted = true;
					} else if (tsName.equalsIgnoreCase("")) {
						JOptionPane.showMessageDialog(frame, "Please enter Test Suite name", "Error",
								JOptionPane.ERROR_MESSAGE);
					} else {
						inputAccepted = true;
						createTestSuite(tsName);
					}
				}
			}
		};
		return action;
	}

	public static ActionListener loadTestSuiteActionListener() {
		ActionListener action = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					loadTestSuite();
				} catch (Exception err) {
					JOptionPane.showMessageDialog(frame, "Invalid Test Suite file!", "Error",
							JOptionPane.ERROR_MESSAGE);
				}

			}
		};
		return action;
	}
	public static void addLogsTab(String title){
		JPanel panel = new JPanel();
		tabbedPane.addTab(title, panel);
		int index = tabbedPane.indexOfTab(title);
		JPanel pnlTab = new JPanel(new GridBagLayout());
		pnlTab.setOpaque(false);
		JLabel lblTitle = new JLabel(title);
		JButton btnClose = new MyCloseButton();

		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
		        tabbedPane.remove(tabbedPane.indexOfTab(title));
		    }
		});
		pnlTab.add(lblTitle);
		pnlTab.add(btnClose);


		
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 19, 0, 0, 0, 0, 0, 0};
		gbl_panel.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_panel.columnWeights = new double[]{1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{0.0, 1.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		JTextField lblLogsPath = new JTextField(path + "\\Test Result\\" + title + "\\" + title + ".log");
		lblLogsPath.setEditable(false);
		GridBagConstraints gbc_lblLogsPath = new GridBagConstraints();
		gbc_lblLogsPath.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblLogsPath.gridwidth = 26;
		gbc_lblLogsPath.insets = new Insets(0, 0, 5, 5);
		gbc_lblLogsPath.gridx = 0;
		gbc_lblLogsPath.gridy = 0;
		panel.add(lblLogsPath, gbc_lblLogsPath);
		
		JTextField txtSearch = new JTextField();
		GridBagConstraints gbc_txtSearch = new GridBagConstraints();
		gbc_txtSearch.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtSearch.gridwidth = 4;
		gbc_txtSearch.insets = new Insets(0, 0, 5, 5);
		gbc_txtSearch.gridx = 26;
		gbc_txtSearch.gridy = 0;
		panel.add(txtSearch, gbc_txtSearch);
		JTextArea txtLogs = new JTextArea();
		txtLogs.setFont(logsFont);
		txtLogs.setEditable(false);
		txtLogs.setWrapStyleWord(true);
		txtLogs.setBackground(logsBg);
		txtLogs.setForeground(logsFg);
		JButton btnFind = new JButton("Find");
		btnFind.setFont(buttonsFont);
		GridBagConstraints gbc_btnFind = new GridBagConstraints();
		gbc_btnFind.insets = new Insets(0, 0, 5, 0);
		gbc_btnFind.gridx = 30;
		gbc_btnFind.gridy = 0;
		panel.add(btnFind, gbc_btnFind);
		btnFind.setFocusable(false);
		btnFind.addActionListener(new ActionListener() {
			private int pos = 0;
			public void actionPerformed(ActionEvent e) {
				 String find = txtSearch.getText().toLowerCase();
		            txtLogs.requestFocusInWindow();
		            if (find != null && find.length() > 0) {
		                Document document = txtLogs.getDocument();
		                int findLength = find.length();
		                try {
		                    boolean found = false;
		                    if (pos + findLength > document.getLength()) {
		                        pos = 0;
		                    }
			                while (pos + findLength <= document.getLength()) {
			                    String match = document.getText(pos, findLength).toLowerCase();
			                    if (match.equals(find)) {
			                        found = true;
			                        break;
			                    }
			                    pos++;
			                }
			                if (found) {
			                    Rectangle viewRect = txtLogs.modelToView(pos);
			                    txtLogs.scrollRectToVisible(viewRect);
			                    txtLogs.setCaretPosition(pos + findLength);
			                    txtLogs.moveCaretPosition(pos);
			                    pos += findLength;
			                }
			            } catch (Exception exp) {
			                exp.printStackTrace();
			            }
		            }
			}
		});
		txtSearch.addKeyListener(new KeyAdapter() {
	          public void keyPressed(KeyEvent e) {
	            int key = e.getKeyCode();
	            if (key == KeyEvent.VK_ENTER) {
	            	btnFind.doClick();
	               }
	            }
	          }
	       );
		
		JScrollPane scrollPane_3 = new JScrollPane();
		GridBagConstraints gbc_scrollPane_3 = new GridBagConstraints();
		gbc_scrollPane_3.gridwidth = 31;
		gbc_scrollPane_3.gridheight = 10;
		gbc_scrollPane_3.fill = GridBagConstraints.BOTH;
		gbc_scrollPane_3.gridx = 0;
		gbc_scrollPane_3.gridy = 1;
		panel.add(scrollPane_3, gbc_scrollPane_3);

		scrollPane_3.setViewportView(txtLogs);
		tabbedPane.setTabComponentAt(index, pnlTab);
		tabbedPane.setSelectedIndex(index);
		
		try {
			String logFileName = path + "\\Test Result\\" + title + "\\" + title + ".log";
			File f = new File(logFileName);
			while (!f.exists()) {
				Thread.sleep(2000);
			}

			readLogs(logFileName, txtLogs);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public static void readLogs(String filename, JTextArea logs)
			throws InterruptedException, IOException {
		boolean canBreak = false;
		String line;
		try {
			FileReader rdr = new FileReader(filename);
			LineNumberReader lnr = new LineNumberReader(rdr);
			while (!canBreak) {
				line = lnr.readLine();
				if (line == null) {
					Thread.sleep(1000);
					continue;
				}
				boolean endOfFile = processLine(line, logs);
				canBreak = endOfFile;
			}
			lnr.close();
			rdr.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	public static MouseListener tableMouseListener() {
		MouseListener mListener = new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {

				int rowindex = cTable.getSelectedRow();
				if (rowindex < 0)
					return;
				popupMenuTableRowAction(e, rowindex);
			}

		};
		return mListener;
	}

	public static MouseListener tableHeaderMouseListener() {
		MouseListener mListener = new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				TableColumnModel tableMod = cTable.getColumnModel();
				if(tableMod.getColumnCount() > 0) {
					if (tableMod.getColumn(0).getHeaderValue().toString().contentEquals("TCMODULES")) {
						modColWidth[0] = cTable.getColumnModel().getColumn(1).getWidth();
						modColWidth[1] = cTable.getColumnModel().getColumn(2).getWidth();
					} else if (tableMod.getColumn(0).getHeaderValue().toString().contentEquals("TCS")) {
						tcColWidth[0] = cTable.getColumnModel().getColumn(1).getWidth();
						tcColWidth[1] = cTable.getColumnModel().getColumn(2).getWidth();
					} else if (tableMod.getColumn(0).getHeaderValue().toString().contentEquals("TCSTEPS")) {
						stepsColWidth[0] = cTable.getColumnModel().getColumn(2).getWidth();
						stepsColWidth[1] = cTable.getColumnModel().getColumn(3).getWidth();
						stepsColWidth[2] = cTable.getColumnModel().getColumn(4).getWidth();
						stepsColWidth[3] = cTable.getColumnModel().getColumn(5).getWidth();
						stepsColWidth[4] = cTable.getColumnModel().getColumn(6).getWidth();
						stepsColWidth[5] = cTable.getColumnModel().getColumn(7).getWidth();
						stepsColWidth[6] = cTable.getColumnModel().getColumn(8).getWidth();
						stepsColWidth[7] = cTable.getColumnModel().getColumn(9).getWidth();
					} else if (tableMod.getColumn(0).getHeaderValue().toString().contentEquals("RUNS")) {
						runColWidth[0] = cTable.getColumnModel().getColumn(1).getWidth();
						runColWidth[1] = cTable.getColumnModel().getColumn(2).getWidth();
						runColWidth[2] = cTable.getColumnModel().getColumn(3).getWidth();
						runColWidth[3] = cTable.getColumnModel().getColumn(4).getWidth();
						runColWidth[4] = cTable.getColumnModel().getColumn(5).getWidth();
						runColWidth[5] = cTable.getColumnModel().getColumn(6).getWidth();
						runColWidth[6] = cTable.getColumnModel().getColumn(7).getWidth();
					} else if (tableMod.getColumn(0).getHeaderValue().toString().contentEquals("RUNMODULES")) {
						runmodColWidth[0] = cTable.getColumnModel().getColumn(1).getWidth();
						runmodColWidth[1] = cTable.getColumnModel().getColumn(2).getWidth();
						runmodColWidth[2] = cTable.getColumnModel().getColumn(3).getWidth();
						runmodColWidth[3] = cTable.getColumnModel().getColumn(4).getWidth();
						runmodColWidth[4] = cTable.getColumnModel().getColumn(5).getWidth();
						runmodColWidth[5] = cTable.getColumnModel().getColumn(6).getWidth();
					} else if (tableMod.getColumn(0).getHeaderValue().toString().contentEquals("RUNTC")) {
						runtcColWidth[0] = cTable.getColumnModel().getColumn(1).getWidth();
						runtcColWidth[1] = cTable.getColumnModel().getColumn(2).getWidth();
						runtcColWidth[2] = cTable.getColumnModel().getColumn(3).getWidth();
						runtcColWidth[3] = cTable.getColumnModel().getColumn(4).getWidth();
						runtcColWidth[4] = cTable.getColumnModel().getColumn(5).getWidth();
						runtcColWidth[5] = cTable.getColumnModel().getColumn(6).getWidth();
					} else if (tableMod.getColumn(0).getHeaderValue().toString().contentEquals("RUNSTEPS")) {
						runstepsColWidth[0] = cTable.getColumnModel().getColumn(1).getWidth();
						runstepsColWidth[1] = cTable.getColumnModel().getColumn(2).getWidth();
						runstepsColWidth[2] = cTable.getColumnModel().getColumn(3).getWidth();
						runstepsColWidth[3] = cTable.getColumnModel().getColumn(4).getWidth();
						runstepsColWidth[4] = cTable.getColumnModel().getColumn(5).getWidth();
						runstepsColWidth[5] = cTable.getColumnModel().getColumn(6).getWidth();
						runstepsColWidth[6] = cTable.getColumnModel().getColumn(7).getWidth();
						runstepsColWidth[7] = cTable.getColumnModel().getColumn(8).getWidth();
						runstepsColWidth[8] = cTable.getColumnModel().getColumn(9).getWidth();
						runstepsColWidth[9] = cTable.getColumnModel().getColumn(10).getWidth();
					}
				}
			}

		};
		return mListener;
	}

	public static ActionListener btnAddActionListener() {
		ActionListener action = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DefaultTableModel tNewModel = (DefaultTableModel) cTable.getModel();
				String tableName = tNewModel.getColumnName(0);
				if (tableName.contentEquals("TCSTEPS")) {
					tNewModel.addRow(new Object[] { "", "", "", "", "", "", "", "", "", "" });
					int row = cTable.getRowCount() - 1;
					cTable.setRowSelectionInterval(row, row);
				} else if (tableName.contentEquals("TCS")) {
					tNewModel.addRow(new Object[] { "", "", "", "" });
					int row = cTable.getSelectedRows().length;
					cTable.setRowSelectionInterval(row, row);
				} else if (tableName.contentEquals("TCMODULES")) {
					inputModule(false);
				} else if (tableName.contentEquals("RUNS")) {
					inputTestRun();
				}

			}
		};
		return action;
	}
	public static ActionListener btnInsertActionListener() {
		ActionListener action = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DefaultTableModel tModel = (DefaultTableModel) cTable.getModel();
				String tableName = tModel.getColumnName(0);
				DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) navigationTree.getLastSelectedPathComponent();
				CustomUserObject cNode = (CustomUserObject) selectedNode.getUserObject();
				int row = cTable.getSelectedRow();
				if(row >= 0) {
					if (tableName.contentEquals("TCSTEPS")) {
						dbProcess.insertStep((String) tModel.getColumnName(1),
								(String) tModel.getValueAt(row, 0));
						displaySteps(tModel.getColumnName(1));
						cTable.setRowSelectionInterval(row, row);
					} else if (tableName.contentEquals("TCS")) {
						dbProcess.insertTestcase(cNode.getId(),row);
						displayTestcases(cNode.getId());
						createTcNode(selectedNode);
						cTable.setRowSelectionInterval(row, row);
					} else if (tableName.contentEquals("TCMODULES")) {
						inputModule(true);
						displayModules();
						createModulesNode(modNode);
						cTable.setRowSelectionInterval(row, row);
					} 
					
				}
			}
		};
		return action;
	}
	public static ActionListener btnUpActionListener() {
		ActionListener action = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DefaultTableModel tModel = (DefaultTableModel) cTable.getModel();
				String tableName = tModel.getColumnName(0);
				DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) navigationTree.getLastSelectedPathComponent();
				CustomUserObject cNode = (CustomUserObject) selectedNode.getUserObject();
				int row = cTable.getSelectedRow();
				int[] rows = cTable.getSelectedRows();
				if(row > 0) {
					if (tableName.contentEquals("TCSTEPS")) {
						if(!(rows.length > 1)) {
							dbProcess.upStep(cNode.getId(),(String) tModel.getValueAt(row, 0),row);
							displaySteps(cNode.getId());
							cTable.setRowSelectionInterval(row - 1, row - 1);
						}
						
					} else if (tableName.contentEquals("TCS")) {
						dbProcess.upTestcase(cNode.getId(),(String) tModel.getValueAt(row, 0),row);
						displayTestcases(cNode.getId());
						createTcNode(selectedNode);	
						createRunNode(runNode);
						cTable.setRowSelectionInterval(row - 1, row - 1);
					} else if (tableName.contentEquals("TCMODULES")) {
						dbProcess.upModule((String) tModel.getValueAt(row, 0),row);
						displayModules();
						createModulesNode(modNode);
						createRunNode(runNode);
						cTable.setRowSelectionInterval(row - 1, row - 1);
					} 
				}
			}
		};
		return action;
	}
	public static ActionListener btnDownActionListener() {
		ActionListener action = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DefaultTableModel tModel = (DefaultTableModel) cTable.getModel();
				String tableName = tModel.getColumnName(0);
				DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) navigationTree.getLastSelectedPathComponent();
				CustomUserObject cNode = (CustomUserObject) selectedNode.getUserObject();
				int row = cTable.getSelectedRow();
				int rowCount = cTable.getRowCount();
				int[] rows = cTable.getSelectedRows();
				if( (row > -1 && row < (rowCount -1))) {
					if (tableName.contentEquals("TCSTEPS")) {
						if(!(rows.length > 1)) {
							dbProcess.downStep(cNode.getId(),(String) tModel.getValueAt(row, 0),row);
							displaySteps(cNode.getId());
							cTable.setRowSelectionInterval(row + 1, row + 1);
						}
					} else if (tableName.contentEquals("TCS")) {
						dbProcess.downTestcase(cNode.getId(),(String) tModel.getValueAt(row, 0),row);
						displayTestcases(cNode.getId());
						cTable.setRowSelectionInterval(row + 1, row + 1);
						createTcNode(selectedNode);
						createRunNode(runNode);
					} else if (tableName.contentEquals("TCMODULES")) {
						dbProcess.downModule((String) tModel.getValueAt(row, 0),row);
						displayModules();
						createModulesNode(modNode);
						createRunNode(runNode);
						cTable.setRowSelectionInterval(row + 1, row + 1);
					} 
				}
			}
		};
		return action;
	}


	public ActionListener openModulesWindow() {
		ActionListener action = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					ModuleUI moduleUI = new ModuleUI(dbProcess);
					moduleUI.setLocationRelativeTo(null);
					moduleUI.setVisible(true);
				} catch (Exception err) {
	
					JOptionPane.showMessageDialog(null, "Modules not found", "Error", JOptionPane.ERROR_MESSAGE);
					try {
						dbProcess.createModularActionTable();
						JOptionPane.showMessageDialog(null, "Modules Action table created", "Modules",
								JOptionPane.INFORMATION_MESSAGE);
					} catch (Exception er) {
						JOptionPane.showMessageDialog(null, er.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
					}
				}

			}
		};
		return action;
	}
	public ActionListener openModulesImportWindow() {
		ActionListener action = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					String appPath = new File("").getAbsolutePath();
					JFileChooser fileChooser = new JFileChooser();
					fileChooser.setCurrentDirectory(new File(appPath + "\\Test Suite"));
					fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
					fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Test Suite", "db"));
					fileChooser.setAcceptAllFileFilterUsed(false);
	
					int result = fileChooser.showOpenDialog(fileChooser);
					if (result == JFileChooser.APPROVE_OPTION) {
						File selectedFile = fileChooser.getSelectedFile();
						if (!selectedFile.isFile()) {
							JOptionPane.showMessageDialog(frame, "Test Suite not found: " + selectedFile, "Error",
									JOptionPane.ERROR_MESSAGE);
						} else {
							String dbName = selectedFile.getAbsolutePath();
							//String pathName = dbName.replaceAll("\\\\", "\\\\\\\\");
							DBProcess dbProcessTemp = new DBProcess(dbName);
							if(dbProcessTemp.dbConn!=null) {
								DBProcess dbp = dbProcessTemp;
								ModuleUIImport moduleImport = new ModuleUIImport(dbp,dbProcess,dbName);
								moduleImport.setLocationRelativeTo(null);
								moduleImport.setVisible(true);
							}
						}
					}
				} catch (Exception err) {
					JOptionPane.showMessageDialog(null, "Modules Import Error", "Error", JOptionPane.ERROR_MESSAGE);
				}

			}
		};
		return action;
	}

	public ActionListener btnImportActionListener() {
		ActionListener action = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					DefaultTableModel tNewModel = (DefaultTableModel) cTable.getModel();
					String tableName = tNewModel.getColumnName(0);
					String imp = "";
					String tcId="";
					DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) navigationTree.getLastSelectedPathComponent();
					CustomUserObject cNode = (CustomUserObject) selectedNode.getUserObject();
					if (tableName.contentEquals("TCSTEPS")) {
						imp = "Test Steps";
						tcId = tNewModel.getColumnName(1);
					} else if (tableName.contentEquals("TCS")) {
						imp = "Testcase";
						tcId = cNode.getId();	
					} else if (tableName.contentEquals("TCMODULES")) {
						imp = "Modules";
					}
					String appPath = new File("").getAbsolutePath();
					JFileChooser fileChooser = new JFileChooser();
					fileChooser.setCurrentDirectory(new File(appPath + "\\Test Suite"));
					fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
					fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Test Suite", "db"));
					fileChooser.setAcceptAllFileFilterUsed(false);
	
					int result = fileChooser.showOpenDialog(fileChooser);
					if (result == JFileChooser.APPROVE_OPTION) {
						File selectedFile = fileChooser.getSelectedFile();
						if (!selectedFile.isFile()) {
							JOptionPane.showMessageDialog(frame, "Test Suite not found: " + selectedFile, "Error",
									JOptionPane.ERROR_MESSAGE);
						} else {
							String dbName = selectedFile.getAbsolutePath();
							//String pathName = dbName.replaceAll("\\\\", "\\\\\\\\");
							DBProcess dbp = new DBProcess(dbName);
							ImportUI testImport = new ImportUI(dbp,dbProcess,dbName,imp,tcId,cTable.getSelectedRow());
							testImport.setLocationRelativeTo(null);
							testImport.setVisible(true);
							if (tableName.contentEquals("TCSTEPS")) {
								displaySteps(tNewModel.getColumnName(1));
							} else if (tableName.contentEquals("TCS")) {
								displayTestcases(tcId);
								createTcNode(selectedNode);
							} else if (tableName.contentEquals("TCMODULES")) {
								createRootNode();
								navigationTree.setSelectionPath(new TreePath(selectedNode.getPath()));
								navigationTree.setSelectionRow(0);
							}
						}
					}
				} catch (Exception err) {
					JOptionPane.showMessageDialog(null, "Import Error", "Error", JOptionPane.ERROR_MESSAGE);
				}

			}
		};
		return action;
	}
	public ActionListener openActionWindow() {

		ActionListener action = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					ActionUI dialog = new ActionUI();
					dialog.setLocationRelativeTo(null);
					dialog.setVisible(true);
					
				} catch (Exception err) {
					JOptionPane.showMessageDialog(null, err.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}

			}
		};
		return action;
	}
	public ActionListener openConfigWindow() {
		ActionListener action = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					ConfigUI dialog = new ConfigUI();
					dialog.setLocationRelativeTo(null);
					dialog.setVisible(true);
					autobackup = autoBackup(dbPath);
					if(!strTheme.contentEquals(dbConfig.getConfigValue("UI_THEME"))) {
						loadTheme(true);
					}
					
				} catch (Exception err) {
					JOptionPane.showMessageDialog(null, err.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}

			}
		};
		return action;

	}
	public ActionListener aboutWindow() {
		ActionListener action = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					AboutUI dialog = new AboutUI();
					dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					dialog.setLocationRelativeTo(null);
					dialog.setVisible(true);
				} catch (Exception err) {
					JOptionPane.showMessageDialog(null, err.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}

			}
		};
		return action;
	}
	public static ActionListener btnRefreshActionListener() {
		ActionListener action = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				DefaultTableModel tNewModel = (DefaultTableModel) cTable.getModel();
				String tableName = tNewModel.getColumnName(0);
				DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) navigationTree
						.getLastSelectedPathComponent();
				CustomUserObject tNode = (CustomUserObject) selectedNode.getUserObject();
				int row = cTable.getSelectedRow();
				if (tableName.contentEquals("RUNS")) {
					displayTestRun();
					
				} else if (tableName.contentEquals("RUNMODULES")) {
					displayRunModules(tNode.getId());
				} else if (tableName.contentEquals("RUNTC")) {
					DefaultMutableTreeNode runTree = (DefaultMutableTreeNode) selectedNode.getParent();
					CustomUserObject cUserObjRun = (CustomUserObject) runTree.getUserObject();
					displayRunTestcases(tNode.getId(), cUserObjRun.getId());
				} else if (tableName.contentEquals("RUNSTEPS")) {
					displayRunTestSteps(tNode.getId());
				}
				if(row > -1 ) {
					cTable.setRowSelectionInterval(row, row);
				}

			}
		};
		return action;
	}

	public static ActionListener btnViewVarActionListener() {

		ActionListener action = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int row = cTable.getSelectedRow();
				DefaultTableModel tModel = (DefaultTableModel) cTable.getModel();
				if (row == -1) {
					JOptionPane.showMessageDialog(frame, "Please select Test Run to View.", "Test Data",
							JOptionPane.ERROR_MESSAGE);
				} else {
					String runId = (String) tModel.getValueAt(row, 0);
					new VariablesUI(runId).displayVariableWindow(dbProcess, runId);
				}
			}
		};

		return action;
	}

	public static ActionListener btnRunActionListener() {
		ActionListener action = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int row = cTable.getSelectedRow();
				if (row == -1) {
					JOptionPane.showMessageDialog(frame, "Please select Test Run to execute.", "Run",
							JOptionPane.ERROR_MESSAGE);
				} else {
					DefaultTableModel tModel = (DefaultTableModel) cTable.getModel();
					String runName = (String) tModel.getValueAt(row, 1);
					String runId = (String) tModel.getValueAt(row, 0);
					String browser = (String) tModel.getValueAt(row, 2);
					String logName = runId + "-" + runName + "-" + getTime();
					int opt = JOptionPane.showConfirmDialog(frame,
							"Are you sure you want to Execute " + tModel.getValueAt(cTable.getSelectedRow(), 1) + "?",
							"Confirm Run", JOptionPane.YES_NO_OPTION);
					if (opt == JOptionPane.YES_OPTION) {
						String runStatus = dbProcess.getRunStatus(runId);
						if(browser.contentEquals("Internet Explorer") && (dbConfig.getConfigValue("CUSTOM_AGENT").contentEquals("Y") || !dbConfig.getUserAgent().contentEquals(""))) {
							JOptionPane.showMessageDialog(frame,"IE is not supported simulating User Agent, Please set User Agent as default.",
									"IE not Supported", JOptionPane.INFORMATION_MESSAGE);
						} else {
							if (runStatus.equalsIgnoreCase("Not Started")) {
								executeTestRun(logName, runId, browser,"");
							} else if(runStatus.equalsIgnoreCase("Failed")) {
								int opt1 =  JOptionPane.showConfirmDialog(frame,
										tModel.getValueAt(cTable.getSelectedRow(), 1)
												+ " is already executed, Yes, will execute only with status 'Failed' or click No then Reset the Test Run before running again.",
										"Confirm Re-run", JOptionPane.YES_NO_OPTION);
								if (opt1 == JOptionPane.YES_OPTION) {
									if(runStatus.equalsIgnoreCase("Passed")){
										JOptionPane.showMessageDialog(frame,
												tModel.getValueAt(cTable.getSelectedRow(), 1)
														+ " is already PASSED ,Reset the Test Run before running again.",
												"Already Passed", JOptionPane.INFORMATION_MESSAGE);
									} else {
										executeTestRun(logName, runId, browser, "Y");
									}
								}
							} else if(runStatus.equalsIgnoreCase("Passed")) {
								JOptionPane.showMessageDialog(frame,
										tModel.getValueAt(cTable.getSelectedRow(), 1)
												+ " is already PASSED ,Reset the Test Run before running again.",
										"Already Passed", JOptionPane.INFORMATION_MESSAGE);
							}else {
								JOptionPane.showMessageDialog(frame,
										tModel.getValueAt(cTable.getSelectedRow(), 1)
												+ " is already ONGOING, Terminate the driver then Reset the Test Run before running again.",
										"Ongoing Run", JOptionPane.INFORMATION_MESSAGE);
							}
						}	
					}
				}
			}
		};
		
		return action;
	}

	public static ActionListener btnDeleteActionListener() {
		ActionListener action = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int row = cTable.getSelectedRow();
				if (row == -1) {
					JOptionPane.showMessageDialog(frame, "Please select item to delete.", "Delete",
							JOptionPane.ERROR_MESSAGE);
				} else {
					DefaultTableModel tModel = (DefaultTableModel) cTable.getModel();
					String tableName = tModel.getColumnName(0);
					
					if (tableName.contentEquals("TCSTEPS")) {
						deleteSteps(tModel);
					} else if (tableName.contentEquals("TCS")) {
						DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) navigationTree.getLastSelectedPathComponent();
						CustomUserObject cNode = (CustomUserObject) selectedNode.getUserObject();
						String tcId = (String) tModel.getValueAt(row, 0);
						deleteTestcase(tModel,tcId,cNode.getId(),row);
					} else if (tableName.contentEquals("TCMODULES")) {
						deleteModule(tModel,row);
					} else if (tableName.contentEquals("RUNS")) {
						int opt = JOptionPane.showConfirmDialog(frame,
								"Are you sure you want to delete " + tModel.getValueAt(cTable.getSelectedRow(), 1) + "?",
								"Confirm Delete", JOptionPane.YES_NO_OPTION);
						if (opt == JOptionPane.YES_OPTION) {
							String runId = (String) tModel.getValueAt(row, 0);
							dbProcess.deleteRun(runId);
							tModel.removeRow(row);
							removeChildNode(runId);
						}
					}
				}
			}
		};
		return action;
	}
	private static void deleteSteps(DefaultTableModel tModel) {
		int opt = JOptionPane.showConfirmDialog(frame, "Are you sure you want to delete?",
				"Confirm Delete", JOptionPane.YES_NO_OPTION);
		if (opt == JOptionPane.YES_OPTION) {
			int[] row = cTable.getSelectedRows();
			String stepId = "";
			for (int r : row) {
				stepId = stepId + (String) tModel.getValueAt(r, 0) + ",";
			}
			dbProcess.deleteStep(stepId.substring(0, stepId.length() - 1));
			displaySteps(tModel.getColumnName(1));
		}
	}
	private static void deleteTestcase(DefaultTableModel tModel,String tcId,String modId, int row) {
		int opt = JOptionPane.showConfirmDialog(
				frame, "Are you sure you want to delete "
						+ tModel.getValueAt(cTable.getSelectedRow(), 1) + "?",
				"Confirm Delete", JOptionPane.YES_NO_OPTION);
		if (opt == JOptionPane.YES_OPTION) {
			dbProcess.deleteTestcases(tcId,modId);
			tModel.removeRow(row);
			removeChildNode(tcId);
			displayTestcases(modId);
		}
	}
	private static void deleteModule(DefaultTableModel tModel, int row) {
		String modId = (String) tModel.getValueAt(row, 0);
		int opt = JOptionPane.showConfirmDialog(
				frame, "Are you sure you want to delete "
						+ tModel.getValueAt(cTable.getSelectedRow(), 1) + "?",
				"Confirm Delete", JOptionPane.YES_NO_OPTION);
		if (opt == JOptionPane.YES_OPTION) {
			dbProcess.deleteModule(modId);
			tModel.removeRow(row);
			removeChildNode(modId);
			displayModules();
		}
	}
	public static ActionListener btnResetActionListener() {
		ActionListener action = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int row = cTable.getSelectedRow();
				if (row == -1) {
					JOptionPane.showMessageDialog(frame, "Please select Test Run to reset.", "Reset",
							JOptionPane.ERROR_MESSAGE);
				} else {
					DefaultTableModel tModel = (DefaultTableModel) cTable.getModel();
					int opt = JOptionPane.showConfirmDialog(frame,
							"Are you sure you want to reset " + tModel.getValueAt(cTable.getSelectedRow(), 1) + "?",
							"Confirm Reset", JOptionPane.YES_NO_OPTION);
					if (opt == JOptionPane.YES_OPTION) {
						String runId = (String) tModel.getValueAt(row, 0);
						dbProcess.resetTestRunStatus(runId);
						displayTestRun();
					}
				}
			}
		};
		return action;
	}

	public static ActionListener btnExportActionListener() {
		ActionListener action = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int row = cTable.getSelectedRow();
				if (row == -1) {
					JOptionPane.showMessageDialog(frame, "Please select Test Run to Export.", "Export",
							JOptionPane.ERROR_MESSAGE);
				} else {
					DefaultTableModel tModel = (DefaultTableModel) cTable.getModel();
					String runName = (String) tModel.getValueAt(row, 1);
					String reportName = runName + "-" + getTime();
					JFileChooser chooser = new JFileChooser();
					FileNameExtensionFilter filter = new FileNameExtensionFilter("Excel files", "xlsx", "excel");
					chooser.addChoosableFileFilter(filter);
					chooser.setFileFilter(filter);
					chooser.setAcceptAllFileFilterUsed(false);
					chooser.setSelectedFile(new File(reportName));

					if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
						File file = chooser.getSelectedFile();
						String runId = (String) tModel.getValueAt(row, 0);
						ExcelClass excel = new ExcelClass();
						excel.createExcelFileReport(file, runId, dbProcess, true);

					}
				}
			}
		};
		return action;
	}

	public static void createRootNode() {
		subMenuRecent();
		CustomUserObject uObjTC = new CustomUserObject("ROOT", "ROOT");

		navigationTree.setModel(new DefaultTreeModel(new DefaultMutableTreeNode(uObjTC) {
			private static final long serialVersionUID = 1L;

			{
				if (adminPass) {
					CustomUserObject uObjTc = new CustomUserObject("TC", "TEST PLAN");
					modNode = new DefaultMutableTreeNode(uObjTc);
					add(modNode);
					createModulesNode(modNode);
				}
				CustomUserObject uObjRun = new CustomUserObject("RUN", "TEST RUN");
				runNode = new DefaultMutableTreeNode(uObjRun);
				add(runNode);
				createRunNode(runNode);

			}
		}));

		navigationTree.expandRow(0);

		navigationTree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent se) {

				DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) navigationTree
						.getLastSelectedPathComponent();
				if (selectedNode != null) {
					CustomUserObject tNode = (CustomUserObject) selectedNode.getUserObject();

					CustomUserObject cUserObj;
					String rootFolder = null;
					int nodeLvl = selectedNode.getLevel();
					if (nodeLvl == 1) {
						cUserObj = (CustomUserObject) selectedNode.getUserObject();
						rootFolder = cUserObj.getId();
						if (rootFolder.contentEquals("TC")) {
							displayModules();

							btnAdd.setVisible(true);
							btnImport.setVisible(true);
							btnUp.setVisible(true);
							btnDown.setVisible(true);
							btnInsert.setVisible(true);

							namePanel.setVisible(false);
							btnDelete.setVisible(true);
							btnReset.setVisible(false);
							btnExport.setVisible(false);
							btnRun.setVisible(false);
							btnVariable.setVisible(false);
							btnRefresh.setVisible(false);
						} else if (rootFolder.contentEquals("RUN")) {
							displayTestRun();
							namePanel.setVisible(false);
							btnReset.setVisible(true);
							btnExport.setVisible(true);
							btnRun.setVisible(true);
							btnVariable.setVisible(true);
							btnRefresh.setVisible(true);
							if (adminPass) {
								btnAdd.setVisible(true);
								btnDelete.setVisible(true);
							} else {
								btnAdd.setVisible(false);
								btnDelete.setVisible(false);
							}
							btnUp.setVisible(false);
							btnDown.setVisible(false);
							btnInsert.setVisible(false);
							btnImport.setVisible(false);
						}
					} else if (nodeLvl == 2) {
						DefaultMutableTreeNode pTree = (DefaultMutableTreeNode) selectedNode.getParent();
						cUserObj = (CustomUserObject) pTree.getUserObject();
						rootFolder = cUserObj.getId();
						if (rootFolder.contentEquals("TC")) {
							displayTestcases(tNode.getId());
							displayModuleDetails(tNode.getId());

							btnAdd.setVisible(true);
							textName.setEditable(true);
							textArea.setEditable(true);
							btnImport.setVisible(true);
							btnUp.setVisible(true);
							btnDown.setVisible(true);
							btnInsert.setVisible(true);

							namePanel.setVisible(true);
							btnDelete.setVisible(true);
							btnReset.setVisible(false);
							btnExport.setVisible(false);
							btnRun.setVisible(false);
							btnVariable.setVisible(false);
							btnRefresh.setVisible(false);
							splitPane_1.setDividerLocation(85);
						} else if (rootFolder.contentEquals("RUN")) {
							displayRunModules(tNode.getId());
							btnAdd.setVisible(false);
							namePanel.setVisible(false);
							btnDelete.setVisible(false);
							btnReset.setVisible(false);
							btnExport.setVisible(false);
							btnRun.setVisible(false);
							btnVariable.setVisible(false);
							btnRefresh.setVisible(true);
							btnImport.setVisible(false);
							btnUp.setVisible(false);
							btnDown.setVisible(false);
							btnInsert.setVisible(false);
						}

					} else if (nodeLvl == 3) {
						DefaultMutableTreeNode pTree = (DefaultMutableTreeNode) selectedNode.getParent().getParent();
						cUserObj = (CustomUserObject) pTree.getUserObject();
						rootFolder = cUserObj.getId();

						if (rootFolder.contentEquals("TC")) {
							displaySteps(tNode.getId());
							displayTestcaseDetails(tNode.getId());

							btnAdd.setVisible(true);
							textName.setEditable(true);
							textArea.setEditable(true);
							btnImport.setVisible(true);
							btnUp.setVisible(true);
							btnDown.setVisible(true);
							btnInsert.setVisible(true);

							namePanel.setVisible(true);
							splitPane_1.setDividerLocation(85);
							btnDelete.setVisible(true);
							btnReset.setVisible(false);
							btnExport.setVisible(false);
							btnRun.setVisible(false);
							btnVariable.setVisible(false);
							btnRefresh.setVisible(false);
						} else if (rootFolder.contentEquals("RUN")) {
							DefaultMutableTreeNode runTree = (DefaultMutableTreeNode) selectedNode.getParent();
							CustomUserObject cUserObjRun = (CustomUserObject) runTree.getUserObject();
							displayModuleDetails(tNode.getId());
							displayRunTestcases(tNode.getId(), cUserObjRun.getId());
							btnAdd.setVisible(false);
							namePanel.setVisible(true);
							textName.setEditable(false);
							textArea.setEditable(false);
							btnDelete.setVisible(false);
							btnReset.setVisible(false);
							btnExport.setVisible(false);
							btnRun.setVisible(false);
							btnVariable.setVisible(false);
							btnRefresh.setVisible(true);
							splitPane_1.setDividerLocation(85);
							btnImport.setVisible(false);
							btnUp.setVisible(false);
							btnDown.setVisible(false);
							btnInsert.setVisible(false);
						}
					} else if (nodeLvl == 4) {
						DefaultMutableTreeNode pTree = (DefaultMutableTreeNode) selectedNode.getParent().getParent()
								.getParent();
						cUserObj = (CustomUserObject) pTree.getUserObject();
						rootFolder = cUserObj.getId();
						if (rootFolder.contentEquals("RUN")) {
							displayRunTestSteps(tNode.getId());
							displayTestcaseDetails(dbProcess.getTcIdFronRunId(tNode.getId()));
							btnAdd.setVisible(false);
							namePanel.setVisible(true);
							textName.setEditable(false);
							textArea.setEditable(false);
							btnDelete.setVisible(false);
							btnReset.setVisible(false);
							btnExport.setVisible(false);
							btnRun.setVisible(false);
							btnVariable.setVisible(false);
							splitPane_1.setDividerLocation(85);
							btnRefresh.setVisible(true);
							btnImport.setVisible(false);
							btnUp.setVisible(false);
							btnDown.setVisible(false);
							btnInsert.setVisible(false);
						}
					} else {
						btnAdd.setVisible(false);
						cTable.setModel(new DefaultTableModel());
						namePanel.setVisible(false);
						btnDelete.setVisible(false);
						btnReset.setVisible(false);
						btnExport.setVisible(false);
						btnRun.setVisible(false);
						btnVariable.setVisible(false);
						btnRefresh.setVisible(false);
						btnImport.setVisible(false);
						btnUp.setVisible(false);
						btnDown.setVisible(false);
						btnInsert.setVisible(false);
					}
				}

			}
		});
		DefaultMutableTreeNode fChild = (DefaultMutableTreeNode) navigationTree.getModel().getRoot();
		navigationTree.setSelectionPath(new TreePath(fChild.getRoot()));
		navigationTree.updateUI();
		
		for (ActionListener act : subToolMenuVar.getActionListeners()) {
			subToolMenuVar.removeActionListener(act);
		}
		subToolMenuVar.addActionListener(new VariablesUI("").openVariableWindow(dbProcess, "", adminPass));
	}

	public static void createModulesNode(DefaultMutableTreeNode runNode) {
		runNode.removeAllChildren();
		List<String[]> runList = dbProcess.getModules();
		DefaultTreeModel model = (DefaultTreeModel) navigationTree.getModel();
		for (String[] rowRun : runList) {
			// create parent folder
			CustomUserObject objRun = new CustomUserObject(rowRun[0], rowRun[1]);
			DefaultMutableTreeNode tcNodeRun = new DefaultMutableTreeNode(objRun);
			List<String[]> tc = dbProcess.getTestcases(rowRun[0]);
			// create tree node
			for (String[] rowTc : tc) {
				CustomUserObject uObjTcs = new CustomUserObject(rowTc[0], rowTc[1]);
				tcNodeRun.add(new DefaultMutableTreeNode(uObjTcs));
			}
			runNode.add(tcNodeRun);
			
		}
		model.reload(runNode);
	}
	public static void createTcNode(DefaultMutableTreeNode selectedNode) {
		selectedNode.removeAllChildren();
		DefaultTreeModel model = (DefaultTreeModel) navigationTree.getModel();
		CustomUserObject cNode = (CustomUserObject) selectedNode.getUserObject();
		List<String[]> tc = dbProcess.getTestcases(cNode.getId());
		// create tree node
		for (String[] rowTc : tc) {
			CustomUserObject uObjTcs = new CustomUserObject(rowTc[0], rowTc[1]);
			selectedNode.add(new DefaultMutableTreeNode(uObjTcs));	
		}
		model.reload(selectedNode);
	}

	public static void createRunNode(DefaultMutableTreeNode rootNode) {
		rootNode.removeAllChildren();
		DefaultMutableTreeNode runNode;
		DefaultMutableTreeNode moduleNode;
		DefaultMutableTreeNode testCaseNode;
		DefaultTreeModel model = (DefaultTreeModel) navigationTree.getModel();
		// get Modules from database
		List<String[]> mod = dbProcess.getTestRun();
		for (String[] rowModules : mod) {
			// create parent folder
			CustomUserObject uObjRun = new CustomUserObject(rowModules[0], rowModules[1]);
			runNode = new DefaultMutableTreeNode(uObjRun);
			rootNode.add(runNode);

			List<String[]> modules = dbProcess.getRunModules(rowModules[0]);
			// create tree node
			for (String[] rowMod : modules) {
				CustomUserObject uObjMods = new CustomUserObject(rowMod[0], rowMod[1]);
				moduleNode = new DefaultMutableTreeNode(uObjMods);
				runNode.add(moduleNode);
				List<String[]> tc = dbProcess.getRunTestcases(rowMod[0], rowModules[0]);
				for (String[] rowTc : tc) {
					CustomUserObject uObjTc = new CustomUserObject(rowTc[0], rowTc[1]);
					testCaseNode = new DefaultMutableTreeNode(uObjTc);
					moduleNode.add(testCaseNode);
				}
			}
		}
		model.reload(rootNode);
	}
	public static void createRNode(DefaultMutableTreeNode selectedNode) {
		selectedNode.removeAllChildren();
		
		DefaultTreeModel model = (DefaultTreeModel) navigationTree.getModel();
		CustomUserObject cNode = (CustomUserObject) selectedNode.getUserObject();
		List<String[]> tc = dbProcess.getTestcases(cNode.getId());
		// create tree node
		for (String[] rowTc : tc) {
			CustomUserObject uObjTcs = new CustomUserObject(rowTc[0], rowTc[1]);
			selectedNode.add(new DefaultMutableTreeNode(uObjTcs));	
		}
		model.reload(selectedNode);
	}

	public static void popupMenuTableRowAction(MouseEvent mEvent, int rowindex) {
		DefaultTableModel tModel = (DefaultTableModel) cTable.getModel();
		String tableName = tModel.getColumnName(0);
		if (mEvent.isPopupTrigger() && mEvent.getComponent() instanceof JTable) {
			if (adminPass || tableName.contentEquals("RUNS")) {
				JPopupMenu popup = new JPopupMenu();

				if (tableName.contentEquals("TCSTEPS")) {

					JMenuItem itemInsert = new JMenuItem("Insert");
					popup.add(itemInsert);
					itemInsert.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							dbProcess.insertStep((String) tModel.getColumnName(1),
									(String) tModel.getValueAt(rowindex, 0));
							displaySteps(tModel.getColumnName(1));
						}
					});

					JMenuItem itemCopy = new JMenuItem("Copy");
					popup.add(itemCopy);
					itemCopy.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							int[] row = cTable.getSelectedRows();
							String stepId = "";
							for (int r : row) {
								stepId = stepId + (String) tModel.getValueAt(r, 0) + ",";
							}
							copySteps = stepId.substring(0, stepId.length() - 1);
						}
					});
					if (copySteps != null) {
						JMenuItem itemPaste = new JMenuItem("Paste");
						popup.add(itemPaste);
						itemPaste.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								dbProcess.pasteSteps(copySteps, (String) tModel.getColumnName(1), rowindex);
								displaySteps(tModel.getColumnName(1));
								copySteps = null;
							}
						});
					}

					JMenuItem item = new JMenuItem("Delete");
					popup.add(item);
					item.addActionListener(btnDeleteActionListener());

				} else if (tableName.contentEquals("TCMODULES")) {
					
					JMenuItem itemInsert = new JMenuItem("Insert");
					popup.add(itemInsert);
					itemInsert.addActionListener(btnInsertActionListener());
					
					JMenuItem item = new JMenuItem("Delete");
					popup.add(item);
					String modId = (String) tModel.getValueAt(rowindex, 0);
					item.addActionListener(btnDeleteActionListener());
					
					// Add to Test Run Menu
					createAddToRunSubMenu(popup, "TCMODULES", modId);

				} else if (tableName.contentEquals("TCS")) {
					String tcId = (String) tModel.getValueAt(rowindex, 0);
				
					JMenuItem itemInsert = new JMenuItem("Insert");
					popup.add(itemInsert);
					itemInsert.addActionListener(btnInsertActionListener());
					
					JMenuItem itemCopy = new JMenuItem("Copy");
					popup.add(itemCopy);
					itemCopy.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							copyTC = tcId;
						}
					});
					JMenuItem item = new JMenuItem("Delete");
					popup.add(item);
					item.addActionListener(btnDeleteActionListener());
					
					
					if (copyTC != null) {
						JMenuItem itemPaste = new JMenuItem("Paste");
						popup.add(itemPaste);
						itemPaste.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								dbProcess.pasteTestcase(copyTC, tcId);
								copyTC = null;
							}
						});

					}
					// Add to Test Run Menu
					createAddToRunSubMenu(popup, "TCS", tcId);

				} else if (tableName.contentEquals("RUNS")) {
					JMenuItem itemExecute = new JMenuItem("Run");
					popup.add(itemExecute);
					itemExecute.addActionListener(btnRunActionListener());
					JMenuItem itemReset = new JMenuItem("Reset");
					popup.add(itemReset);
					itemReset.addActionListener(btnResetActionListener());
					JMenuItem itemExport = new JMenuItem("Export");
					popup.add(itemExport);
					itemExport.addActionListener(btnExportActionListener());
					JMenu itemVar = new JMenu("Test Data");
					String rId = (String) cTable.getModel().getValueAt(cTable.getSelectedRow(), 0);
					if (adminPass) {
						JMenuItem itemAdd = new JMenuItem("Add");
						popup.add(itemAdd);
						itemAdd.addActionListener(btnAddActionListener());

						JMenuItem item = new JMenuItem("Delete");
						popup.add(item);
						item.addActionListener(btnDeleteActionListener());

						JMenuItem itemAddVar = new JMenuItem("Add");
						itemVar.add(itemAddVar);
						itemAddVar.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								new VarSelectionUI(Long.parseLong(rId)).openVarSelectionUI(dbProcess,Long.parseLong(rId));
							}
						});

					}
					popup.add(itemVar);
					JMenuItem itemVarView = new JMenuItem("View");
					itemVar.add(itemVarView);
					itemVarView.addActionListener(new VariablesUI(rId).openVariableWindow(dbProcess, rId, adminPass));

				} else if (tableName.contentEquals("RUNMODULES")) {
					JMenuItem item = new JMenuItem("Delete");
					popup.add(item);

					item.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							int opt = JOptionPane.showConfirmDialog(
									frame, "Are you sure you want to delete "
											+ tModel.getValueAt(cTable.getSelectedRow(), 1) + "?",
									"Confirm Delete", JOptionPane.YES_NO_OPTION);
							if (opt == JOptionPane.YES_OPTION) {
								String modRunId = (String) tModel.getValueAt(rowindex, 0);
								DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) navigationTree
										.getLastSelectedPathComponent();
								CustomUserObject cNode = (CustomUserObject) selectedNode.getUserObject();
								String runId = cNode.getId();
								dbProcess.deleteRunModule(runId, modRunId);
								tModel.removeRow(rowindex);
								removeChildNode(modRunId);
							}
						}
					});

				} else if (tableName.contentEquals("RUNTC")) {
					JMenuItem item = new JMenuItem("Delete");
					popup.add(item);
					item.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							int opt = JOptionPane.showConfirmDialog(
									frame, "Are you sure you want to delete "
											+ tModel.getValueAt(cTable.getSelectedRow(), 1) + "?",
									"Confirm Delete", JOptionPane.YES_NO_OPTION);
							if (opt == JOptionPane.YES_OPTION) {
								String tcId = (String) tModel.getValueAt(rowindex, 0);
								dbProcess.deleteRunTestcases(tcId);
								tModel.removeRow(rowindex);
								removeChildNode(tcId);
							}
						}
					});
				} else if (tableName.contentEquals("RUNSTEPS")) {

				} else {

				}
				popup.show(mEvent.getComponent(), mEvent.getX(), mEvent.getY());
			}
		}
	}

	public static void createAddToRunSubMenu(JPopupMenu popup, String tableName, String id) {
		DefaultTreeModel tModel = (DefaultTreeModel) navigationTree.getModel();
		DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) navigationTree.getLastSelectedPathComponent();
		DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) selectedNode.getRoot();
		DefaultMutableTreeNode runNode = (DefaultMutableTreeNode) rootNode.getChildAt(1);

		JMenu itemAddRun = new JMenu("Add to TEST RUN");
		popup.add(itemAddRun);
		List<String[]> run = dbProcess.getTestRun();
		for (String[] element : run) {
			JMenuItem subMenuRun = new JMenuItem(element[1]);
			itemAddRun.add(subMenuRun);
			subMenuRun.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (tableName.contentEquals("TCMODULES")) {
						dbProcess.addRunModule(id, element[0]);
						tModel.reload(runNode);
						tModel.reload(selectedNode);
						createRunNode(runNode);
					} else if (tableName.contentEquals("TCS")) {
						dbProcess.addRunTescase(id, element[0]);
						tModel.reload(runNode);
						tModel.reload(selectedNode);
						createRunNode(runNode);
					}
				}
			});
		}
	}

	public static void displayModuleDetails(String id) {
		List<String[]> module = dbProcess.getModule(id);
		for (String[] mod : module) {
			textName.setText(mod[1]);
			textArea.setText(mod[2]);
		}
	}

	public static void displayTestcaseDetails(String id) {
		List<String[]> tc = dbProcess.getTestcase(id);
		for (String[] tcd : tc) {

			textName.setText(tcd[1]);
			textArea.setText(tcd[2]);
		}
	}

	public static void displaySteps(String tcID) {
		cTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		List<String[]> steps = dbProcess.getSteps(tcID);
		// convert arraylist to array[][]
		String[][] stepsValue = new String[steps.size()][];
		for (int i = 0; i < stepsValue.length; i++) {
			String[] row = steps.get(i);
			stepsValue[i] = row;
		}

		@SuppressWarnings("serial")
		DefaultTableModel tModel = new DefaultTableModel(stepsValue,
				new String[] { "TCSTEPS", tcID, "FLOW", "DESCRIPTION", "SELECTOR", "ELEMENT", "ACTION",
						"PARAMETER NAME", "PARAMETER VALUE", "SCREENSHOT" }) {
			boolean[] columnEditables = isEditable();

			public boolean isCellEditable(int row, int column) {
				return columnEditables[column];
			}

			private boolean[] isEditable() {
				boolean[] bln = new boolean[] { false, false, false, false, false, false, false, false, false, false };
				if (adminPass) {
					bln = new boolean[] { false, false, false, true, true, true, true, true, true, true };
				}
				return bln;
			}

		};
		CustomRendererEditable cRenderer = new CustomRendererEditable(tableCellBg,tableCellFg,tableHighlightSelBg,tableHighlightSelFg);
		CustomRendererReadOnly cRendererRead = new CustomRendererReadOnly(tableCellBg,tableCellFg,tableHighlightSelBg,tableHighlightSelFg);

		cTable.setModel(tModel);
		// hide first and second column
		cTable.getColumnModel().getColumn(0).setMinWidth(0);
		cTable.getColumnModel().getColumn(0).setMaxWidth(0);
		cTable.getColumnModel().getColumn(0).setPreferredWidth(0);
		cTable.getColumnModel().getColumn(1).setMinWidth(0);
		cTable.getColumnModel().getColumn(1).setMaxWidth(0);
		cTable.getColumnModel().getColumn(1).setPreferredWidth(0);
		cTable.getColumnModel().getColumn(2).setMinWidth(40);
		cTable.getColumnModel().getColumn(2).setMaxWidth(40);
		cTable.getColumnModel().getColumn(2).setPreferredWidth(40);
		cTable.getColumnModel().getColumn(2).setCellRenderer(cRendererRead);
		cTable.getColumnModel().getColumn(3).setCellRenderer(cRenderer);
		cTable.getColumnModel().getColumn(4).setCellRenderer(cRenderer);
		cTable.getColumnModel().getColumn(5).setCellRenderer(cRenderer);
		cTable.getColumnModel().getColumn(6).setCellRenderer(cRenderer);
		cTable.getColumnModel().getColumn(7).setCellRenderer(cRenderer);
		cTable.getColumnModel().getColumn(8).setCellRenderer(cRenderer);
		cTable.getColumnModel().getColumn(9).setCellRenderer(cRenderer);
		cTable.getColumnModel().getColumn(2).setPreferredWidth(stepsColWidth[0]);
		cTable.getColumnModel().getColumn(3).setPreferredWidth(stepsColWidth[1]);
		cTable.getColumnModel().getColumn(4).setPreferredWidth(stepsColWidth[2]);
		cTable.getColumnModel().getColumn(5).setPreferredWidth(stepsColWidth[3]);
		cTable.getColumnModel().getColumn(6).setPreferredWidth(stepsColWidth[4]);
		cTable.getColumnModel().getColumn(7).setPreferredWidth(stepsColWidth[5]);
		cTable.getColumnModel().getColumn(8).setPreferredWidth(stepsColWidth[6]);
		cTable.getColumnModel().getColumn(9).setPreferredWidth(stepsColWidth[7]);

		cTable.getColumnModel().getColumn(4)
				.setCellEditor(new DefaultCellEditor(createDropdownList(dbConfig.getSelector())));
		cTable.getColumnModel().getColumn(6)
				.setCellEditor(new DefaultCellEditor(createDropdownList(dbConfig.getActions())));

		JComboBox<String> cmbYesNo = new JComboBox<String>();
		cmbYesNo.addItem("Yes");
		cmbYesNo.addItem("No");
		cTable.getColumnModel().getColumn(9).setCellEditor(new DefaultCellEditor(cmbYesNo));
		setTableRowHeight();
		tModel.addTableModelListener(new TableModelListener() {
			public void tableChanged(TableModelEvent e) {
				int row = e.getFirstRow();
				int column = e.getColumn();
				TableModel model = (TableModel) e.getSource();
				if (e.getType() == TableModelEvent.INSERT) {
					// insert new steps
					dbProcess.addStep(model.getColumnName(1));
					displaySteps(model.getColumnName(1));
				} else if (e.getType() == TableModelEvent.UPDATE) {
					// update cell value
					String updatedData = (String) model.getValueAt(row, column);
					String stepId = (String) model.getValueAt(row, 0);
					dbProcess.updateStep(stepId, column, updatedData);
				}
			}
		});

	}

	public static void displayModules() {
		cTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		List<String[]> mod = dbProcess.getModules();
		String[][] modValue = new String[mod.size()][];
		for (int i = 0; i < modValue.length; i++) {
			String[] row = mod.get(i);
			modValue[i] = row;
		}
		@SuppressWarnings("serial")
		DefaultTableModel tModel = new DefaultTableModel(modValue,
				new String[] { "TCMODULES", "NAME", "DESCRIPTION"}) {
			boolean[] columnEditables = isEditable();

			public boolean isCellEditable(int row, int column) {
				return columnEditables[column];
			}

			private boolean[] isEditable() {
				boolean[] bln = new boolean[] { false, false, false };
				if (adminPass) {
					bln = new boolean[] { false, true, true };
				}
				return bln;
			}
		};
		CustomRendererEditable cRenderer = new CustomRendererEditable(tableCellBg,tableCellFg,tableHighlightSelBg,tableHighlightSelFg);
		cTable.setModel(tModel);
		cTable.getColumnModel().getColumn(0).setMinWidth(0);
		cTable.getColumnModel().getColumn(0).setMaxWidth(0);
		cTable.getColumnModel().getColumn(0).setPreferredWidth(0);
		cTable.getColumnModel().getColumn(1).setPreferredWidth(modColWidth[0]);
		cTable.getColumnModel().getColumn(2).setPreferredWidth(modColWidth[1]);
		cTable.getColumnModel().getColumn(1).setCellRenderer(cRenderer);
		cTable.getColumnModel().getColumn(2).setCellRenderer(cRenderer);
		setTableRowHeight();


		tModel.addTableModelListener(new TableModelListener() {
			public void tableChanged(TableModelEvent e) {
				int row = e.getFirstRow();
				int column = e.getColumn();
				TableModel model = (TableModel) e.getSource();
				if (e.getType() == TableModelEvent.INSERT) {
					// insert new Module
					String name = (String) model.getValueAt(row, 1);
					String desc = (String) model.getValueAt(row, 2);
					dbProcess.addModule(name, desc);
					displayModules();
					addChildNode(getMaxColumnValue(cTable), name);
				} else if (e.getType() == TableModelEvent.UPDATE) {
					// update cell value
					String updatedData = (String) model.getValueAt(row, column);
					String modId = (String) model.getValueAt(row, 0);
					String updatedName = (String) model.getValueAt(row, 1);
					dbProcess.updateModule(modId, column, updatedData);
					updateChildNode(modId, updatedName);
				}
			}
		});
	}

	public static void displayTestcases(String moduleId) {
		cTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		List<String[]> mod = dbProcess.getTestcases(moduleId);
		String[][] tcValue = new String[mod.size()][];
		for (int i = 0; i < tcValue.length; i++) {
			String[] row = mod.get(i);
			tcValue[i] = row;
		}

		@SuppressWarnings("serial")
		DefaultTableModel tModel = new DefaultTableModel(tcValue,
				new String[] { "TCS", "NAME", "DESCRIPTION" }) {
			boolean[] columnEditables = isEditable();

			public boolean isCellEditable(int row, int column) {
				return columnEditables[column];
			}

			private boolean[] isEditable() {
				boolean[] bln = new boolean[] { false, false, false };
				if (adminPass) {
					bln = new boolean[] { false, true, true };
				}
				return bln;
			}
		};
		CustomRendererEditable cRenderer = new CustomRendererEditable(tableCellBg,tableCellFg,tableHighlightSelBg,tableHighlightSelFg);
		cTable.setModel(tModel);
		cTable.getColumnModel().getColumn(0).setMinWidth(0);
		cTable.getColumnModel().getColumn(0).setMaxWidth(0);
		cTable.getColumnModel().getColumn(0).setPreferredWidth(0);
		cTable.getColumnModel().getColumn(1).setPreferredWidth(tcColWidth[0]);
		cTable.getColumnModel().getColumn(2).setPreferredWidth(tcColWidth[1]);
		cTable.getColumnModel().getColumn(1).setCellRenderer(cRenderer);
		cTable.getColumnModel().getColumn(2).setCellRenderer(cRenderer);
		setTableRowHeight();
		
		tModel.addTableModelListener(new TableModelListener() {
			public void tableChanged(TableModelEvent e) {
				int row = e.getFirstRow();
				int column = e.getColumn();
				TableModel model = (TableModel) e.getSource();
				if (e.getType() == TableModelEvent.INSERT) {
					DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) navigationTree
							.getLastSelectedPathComponent();
					CustomUserObject cNode = (CustomUserObject) selectedNode.getUserObject();
					String modId = cNode.getId();
					// insert new Testcase
					dbProcess.addTestcase(modId);
					displayTestcases(modId);
					addChildNode(getMaxColumnValue(cTable), "");
				} else if (e.getType() == TableModelEvent.UPDATE) {
					// update cell value
					String updatedData = (String) model.getValueAt(row, column);
					String tcId = (String) model.getValueAt(row, 0);
					String tcName = (String) model.getValueAt(row, 1);
					dbProcess.updateTestcases(tcId, column, updatedData);
					updateChildNode(tcId, tcName);
				}
			}
		});
	}

	public static void displayTestRun() {
		cTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		List<String[]> mod = dbProcess.getTestRun();
		String[][] modValue = new String[mod.size()][];
		for (int i = 0; i < modValue.length; i++) {
			String[] row = mod.get(i);
			modValue[i] = row;
			if (modValue[i][3] != null && modValue[i][4].length() > 0 && modValue[i][4] != null
					&& modValue[i][4].length() > 0) {
				modValue[i][5] = getTimeDifference(modValue[i][3], modValue[i][4]);
			}

		}

		String[] columnNames = new String[] { "RUNS", "NAME", "BROWSER", "START TIME", "END TIME", "DURATION", "STATUS",
				"TEST DATA" };
		@SuppressWarnings("serial")
		DefaultTableModel tModel = new DefaultTableModel(modValue, columnNames) {
			boolean[] columnEditables = isEditable();

			public boolean isCellEditable(int row, int column) {
				return columnEditables[column];
			}

			private boolean[] isEditable() {
				boolean[] bln = new boolean[] { false, true, true, false, false, false, false, false };
				if (adminPass) {
					bln = new boolean[] { false, true, true, false, false, false, false, true };
				}
				return bln;
			}
		};
		CustomRendererEditable cRenderer = new CustomRendererEditable(tableCellBg,tableCellFg,tableHighlightSelBg,tableHighlightSelFg);
		CustomRendererReadOnly cRendererRead = new CustomRendererReadOnly(tableCellBg,tableCellFg,tableHighlightSelBg,tableHighlightSelFg);
		cTable.setModel(tModel);
		cTable.getColumnModel().getColumn(0).setMinWidth(0);
		cTable.getColumnModel().getColumn(0).setMaxWidth(0);
		cTable.getColumnModel().getColumn(0).setPreferredWidth(0);
		cTable.getColumnModel().getColumn(6).setCellRenderer(new CustomRenderer());
		cTable.getColumnModel().getColumn(1).setCellRenderer(cRenderer);
		cTable.getColumnModel().getColumn(2).setCellRenderer(cRenderer);
		cTable.getColumnModel().getColumn(3).setCellRenderer(cRendererRead);
		cTable.getColumnModel().getColumn(4).setCellRenderer(cRendererRead);
		cTable.getColumnModel().getColumn(5).setCellRenderer(cRendererRead);
		cTable.getColumnModel().getColumn(7).setCellRenderer(cRenderer);

		cTable.getColumnModel().getColumn(1).setPreferredWidth(runColWidth[0]);
		cTable.getColumnModel().getColumn(2).setPreferredWidth(runColWidth[1]);
		cTable.getColumnModel().getColumn(3).setPreferredWidth(runColWidth[2]);
		cTable.getColumnModel().getColumn(4).setPreferredWidth(runColWidth[3]);
		cTable.getColumnModel().getColumn(5).setPreferredWidth(runColWidth[4]);
		cTable.getColumnModel().getColumn(6).setPreferredWidth(runColWidth[5]);
		cTable.getColumnModel().getColumn(7).setPreferredWidth(runColWidth[6]);

		if (adminPass) {
			cTable.getColumnModel().getColumn(7).setPreferredWidth(runColWidth[6]);
		} else {
			cTable.getColumnModel().getColumn(7).setPreferredWidth(0);
			cTable.getColumnModel().getColumn(7).setMinWidth(0);
			cTable.getColumnModel().getColumn(7).setMaxWidth(0);
		}

		TableColumn browserColumn = cTable.getColumnModel().getColumn(2);
		TableColumn testDataColumn = cTable.getColumnModel().getColumn(7);

		browserColumn.setCellEditor(new DefaultCellEditor(createDropdownList(dbConfig.getBrowsers())));

		JComboBox<String> cmbYesNo = new JComboBox<String>();
		cmbYesNo.addItem("Yes");
		cmbYesNo.addItem("No");
		testDataColumn.setCellEditor(new DefaultCellEditor(cmbYesNo));

		tModel.addTableModelListener(new TableModelListener() {
			public void tableChanged(TableModelEvent e) {
				int row = e.getFirstRow();
				int column = e.getColumn();
				TableModel model = (TableModel) e.getSource();
				if (e.getType() == TableModelEvent.INSERT) {
					DefaultTreeModel treeModel = (DefaultTreeModel) navigationTree.getModel();
					DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) navigationTree
							.getLastSelectedPathComponent();
					DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) selectedNode.getRoot();
					DefaultMutableTreeNode runNode = (DefaultMutableTreeNode) rootNode.getChildAt(1);
					// insert new TestRun
					String name = (String) model.getValueAt(row, 1);
					long id = dbProcess.insertTestRun(name);
					new VarSelectionUI(id).openVarSelectionUI(dbProcess, id);
					displayTestRun();
					treeModel.reload(runNode);
					createRunNode(runNode);
					treeModel.reload(selectedNode);
				} else if (e.getType() == TableModelEvent.UPDATE) {
					// update cell value
					String updatedData = (String) model.getValueAt(row, column);
					String runId = (String) model.getValueAt(row, 0);
					String updatedName = (String) model.getValueAt(row, 1);
					dbProcess.updateTestRun(runId, column, updatedData);
					if (column == 1) {
						updateChildNode(runId, updatedName);
					}
				}
			}
		});
		setTableRowHeight();
	}

	public static void displayRunModules(String rId) {
		cTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		List<String[]> mod = dbProcess.getRunModules(rId);
		String[][] modValue = new String[mod.size()][];
		for (int i = 0; i < modValue.length; i++) {
			String[] row = mod.get(i);
			modValue[i] = row;
			List<String[]> rTc = dbProcess.getRunTestcases(modValue[i][0], rId);
			for (String[] a : rTc) {
				if (a[6].contentEquals("Failed")) {
					modValue[i][6] = "Failed";
				}
				if (a[6].contentEquals("Ongoing")) {
					modValue[i][6] = "Ongoing";
				}
			}

			modValue[i][3] = rTc.get(0)[3];
			modValue[i][4] = rTc.get(rTc.size() - 1)[4];
			if (modValue[i][3] != null && modValue[i][4].length() > 0 && modValue[i][4] != null
					&& modValue[i][4].length() > 0) {
				modValue[i][5] = getTimeDifference(modValue[i][3], modValue[i][4]);
			}
			
		}
		@SuppressWarnings("serial")
		DefaultTableModel tModel = new DefaultTableModel(modValue,
				new String[] { "RUNMODULES", "NAME", "DESCRIPTION", "START TIME", "END TIME", "DURATION", "STATUS" }) {
			boolean[] columnEditables = new boolean[] { false, false, false, false, false, false, false };

			public boolean isCellEditable(int row, int column) {
				return columnEditables[column];
			}

		};
		CustomRendererReadOnly cRendererRead = new CustomRendererReadOnly(tableCellBg,tableCellFg,tableHighlightSelBg,tableHighlightSelFg);
		cTable.setModel(tModel);
		cTable.getColumnModel().getColumn(0).setMinWidth(0);
		cTable.getColumnModel().getColumn(0).setMaxWidth(0);
		cTable.getColumnModel().getColumn(0).setPreferredWidth(0);
		cTable.getColumnModel().getColumn(6).setCellRenderer(new CustomRenderer());
		cTable.getColumnModel().getColumn(1).setCellRenderer(cRendererRead);
		cTable.getColumnModel().getColumn(2).setCellRenderer(cRendererRead);
		cTable.getColumnModel().getColumn(3).setCellRenderer(cRendererRead);
		cTable.getColumnModel().getColumn(4).setCellRenderer(cRendererRead);
		cTable.getColumnModel().getColumn(5).setCellRenderer(cRendererRead);
		
		cTable.getColumnModel().getColumn(1).setPreferredWidth(runmodColWidth[0]);

		cTable.getColumnModel().getColumn(2).setPreferredWidth(runmodColWidth[1]);
		cTable.getColumnModel().getColumn(3).setPreferredWidth(runmodColWidth[2]);
		cTable.getColumnModel().getColumn(4).setPreferredWidth(runmodColWidth[3]);
		cTable.getColumnModel().getColumn(5).setPreferredWidth(runmodColWidth[4]);
		cTable.getColumnModel().getColumn(6).setPreferredWidth(runmodColWidth[5]);
		setTableRowHeight();

	}

	public static void displayRunTestcases(String modId, String runId) {
		cTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		List<String[]> mod = dbProcess.getRunTestcases(modId, runId);
		String[][] modValue = new String[mod.size()][];
		for (int i = 0; i < modValue.length; i++) {
			String[] row = mod.get(i);
			modValue[i] = row;
			if (modValue[i][3] != null && modValue[i][4].length() > 0 && modValue[i][4] != null
					&& modValue[i][4].length() > 0) {
				modValue[i][5] = getTimeDifference(modValue[i][3], modValue[i][4]);
			}
		}
		@SuppressWarnings("serial")
		DefaultTableModel tModel = new DefaultTableModel(modValue,
				new String[] { "RUNTC", "NAME", "DESCRIPTION", "START TIME", "END TIME", "DURATION", "STATUS" }) {
			boolean[] columnEditables = new boolean[] { false, false, false, false, false, false, false };

			public boolean isCellEditable(int row, int column) {
				return columnEditables[column];
			}
		};
		CustomRendererReadOnly cRendererRead = new CustomRendererReadOnly(tableCellBg,tableCellFg,tableHighlightSelBg,tableHighlightSelFg);
		cTable.setModel(tModel);
		cTable.getColumnModel().getColumn(0).setMinWidth(0);
		cTable.getColumnModel().getColumn(0).setMaxWidth(0);
		cTable.getColumnModel().getColumn(0).setPreferredWidth(0);
		cTable.getColumnModel().getColumn(6).setCellRenderer(new CustomRenderer());
		cTable.getColumnModel().getColumn(1).setCellRenderer(cRendererRead);
		cTable.getColumnModel().getColumn(2).setCellRenderer(cRendererRead);
		cTable.getColumnModel().getColumn(3).setCellRenderer(cRendererRead);
		cTable.getColumnModel().getColumn(4).setCellRenderer(cRendererRead);
		cTable.getColumnModel().getColumn(5).setCellRenderer(cRendererRead);
		cTable.getColumnModel().getColumn(1).setPreferredWidth(runtcColWidth[0]);
		cTable.getColumnModel().getColumn(2).setPreferredWidth(runtcColWidth[1]);
		cTable.getColumnModel().getColumn(3).setPreferredWidth(runtcColWidth[2]);
		cTable.getColumnModel().getColumn(4).setPreferredWidth(runtcColWidth[3]);
		cTable.getColumnModel().getColumn(5).setPreferredWidth(runtcColWidth[4]);
		cTable.getColumnModel().getColumn(6).setPreferredWidth(runtcColWidth[5]);
		setTableRowHeight();
	}

	public static void displayRunTestSteps(String tcId) {
		cTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		boolean edit = dbProcess.getRunUseTestData(tcId);
		List<String[]> mod;
		if (edit) {
			mod = dbProcess.getRunTestStepsWithTestData(tcId, "");
		} else {
			mod = dbProcess.getRunTestSteps(tcId, "");
		}

		String[][] modValue = new String[mod.size()][];
		for (int i = 0; i < modValue.length; i++) {
			String[] row = mod.get(i);
			modValue[i] = row;
		}
		@SuppressWarnings("serial")
		DefaultTableModel tModel = new DefaultTableModel(modValue, new String[] { "RUNSTEPS", "FLOW", "DESCRIPTION",
				"SELECTOR", "ELEMENT", "ACTION", "PARAM_NAME", "PARAM_VALUE", "SCREENSHOT", "STATUS", "REMARKS" }) {
			boolean[] columnEditables = new boolean[] { false, false, false, false, false, false, false, edit, true,
					false, false };

			public boolean isCellEditable(int row, int column) {
				return columnEditables[column];
			}
		};
		CustomRendererEditable cRenderer = new CustomRendererEditable(tableCellBg,tableCellFg,tableHighlightSelBg,tableHighlightSelFg);
		CustomRendererReadOnly cRendererRead = new CustomRendererReadOnly(tableCellBg,tableCellFg,tableHighlightSelBg,tableHighlightSelFg);
		cTable.setModel(tModel);
		cTable.getColumnModel().getColumn(0).setMinWidth(0);
		cTable.getColumnModel().getColumn(0).setMaxWidth(0);
		cTable.getColumnModel().getColumn(0).setPreferredWidth(0);
		cTable.getColumnModel().getColumn(1).setMinWidth(40);
		cTable.getColumnModel().getColumn(1).setMaxWidth(40);
		cTable.getColumnModel().getColumn(1).setPreferredWidth(40);
		cTable.getColumnModel().getColumn(9).setCellRenderer(new CustomRenderer());
		cTable.getColumnModel().getColumn(1).setCellRenderer(cRendererRead);
		cTable.getColumnModel().getColumn(2).setCellRenderer(cRendererRead);
		cTable.getColumnModel().getColumn(3).setCellRenderer(cRendererRead);
		cTable.getColumnModel().getColumn(4).setCellRenderer(cRendererRead);
		cTable.getColumnModel().getColumn(5).setCellRenderer(cRendererRead);
		cTable.getColumnModel().getColumn(6).setCellRenderer(cRendererRead);
		if (edit) {
			cTable.getColumnModel().getColumn(7).setCellRenderer(cRenderer);
		} else {
			cTable.getColumnModel().getColumn(7).setCellRenderer(cRendererRead);
		}

		cTable.getColumnModel().getColumn(8).setCellRenderer(cRenderer);
		cTable.getColumnModel().getColumn(10).setCellRenderer(cRendererRead);
		cTable.getColumnModel().getColumn(1).setPreferredWidth(runstepsColWidth[0]);
		cTable.getColumnModel().getColumn(2).setPreferredWidth(runstepsColWidth[1]);
		if (adminPass) {
			cTable.getColumnModel().getColumn(3).setPreferredWidth(runstepsColWidth[2]);
			cTable.getColumnModel().getColumn(4).setPreferredWidth(runstepsColWidth[3]);
			cTable.getColumnModel().getColumn(5).setPreferredWidth(runstepsColWidth[4]);
		} else {
			cTable.getColumnModel().getColumn(3).setPreferredWidth(0);
			cTable.getColumnModel().getColumn(3).setMinWidth(0);
			cTable.getColumnModel().getColumn(3).setMaxWidth(0);
			cTable.getColumnModel().getColumn(4).setPreferredWidth(0);
			cTable.getColumnModel().getColumn(4).setMinWidth(0);
			cTable.getColumnModel().getColumn(4).setMaxWidth(0);
			cTable.getColumnModel().getColumn(5).setPreferredWidth(0);
			cTable.getColumnModel().getColumn(5).setMinWidth(0);
			cTable.getColumnModel().getColumn(5).setMaxWidth(0);
		}

		cTable.getColumnModel().getColumn(6).setPreferredWidth(runstepsColWidth[5]);
		cTable.getColumnModel().getColumn(7).setPreferredWidth(runstepsColWidth[6]);
		cTable.getColumnModel().getColumn(8).setPreferredWidth(runstepsColWidth[7]);
		cTable.getColumnModel().getColumn(9).setPreferredWidth(runstepsColWidth[8]);
		cTable.getColumnModel().getColumn(10).setPreferredWidth(runstepsColWidth[9]);
		setTableRowHeight();
		
		JComboBox<String> cmbYesNo = new JComboBox<String>();
		cmbYesNo.addItem("Yes");
		cmbYesNo.addItem("No");
		cTable.getColumnModel().getColumn(8).setCellEditor(new DefaultCellEditor(cmbYesNo));

		tModel.addTableModelListener(new TableModelListener() {
			public void tableChanged(TableModelEvent e) {
				int row = e.getFirstRow();
				int column = e.getColumn();
				TableModel model = (TableModel) e.getSource();
				if (e.getType() == TableModelEvent.UPDATE) {

					// update cell value
					String updatedData = (String) model.getValueAt(row, column);
					String id = (String) model.getValueAt(row, 0);
					String paramName = (String) model.getValueAt(row, 6);
					if (column == 7) {
						dbProcess.updateTestDataParamValue(id, updatedData, paramName);
					} else if (column == 8) {
						dbProcess.updateStep(dbProcess.getStepIdFromRun(id), 9, updatedData);
					}
				}
			}
		});

	}

	public static void removeChildNode(String nodeId) {
		DefaultTreeModel model = (DefaultTreeModel) navigationTree.getModel();
		DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) navigationTree.getLastSelectedPathComponent();
		// remove tree node
		for (int i = 0; i < selectedNode.getChildCount(); i++) {
			DefaultMutableTreeNode child = (DefaultMutableTreeNode) selectedNode.getChildAt(i);
			CustomUserObject cNode = (CustomUserObject) child.getUserObject();
			if (nodeId.contentEquals(cNode.getId())) {
				child.removeFromParent();
				model.reload(selectedNode);
			}
		}

	}
	public static void updateChildNode(String nodeId, String newName) {
		DefaultTreeModel model = (DefaultTreeModel) navigationTree.getModel();
		DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) navigationTree.getLastSelectedPathComponent();
		// update tree node
		for (int i = 0; i < selectedNode.getChildCount(); i++) {
			DefaultMutableTreeNode child = (DefaultMutableTreeNode) selectedNode.getChildAt(i);
			CustomUserObject cNode = (CustomUserObject) child.getUserObject();

			if (nodeId.contentEquals(cNode.getId())) {
				CustomUserObject updatedNode = new CustomUserObject(cNode.getId(), newName);
				child.setUserObject(updatedNode);
				model.nodeChanged(selectedNode);
				model.reload(selectedNode);
			}
		}

	}

	public static void updateNode(String nodeId, String newName) {
		DefaultTreeModel model = (DefaultTreeModel) navigationTree.getModel();
		DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) navigationTree.getLastSelectedPathComponent();
		CustomUserObject updatedNode = new CustomUserObject(nodeId, newName);
		selectedNode.setUserObject(updatedNode);
		model.nodeChanged(selectedNode);
		model.reload(selectedNode);

	}

	public static void addChildNode(String nodeId, String name) {
		DefaultTreeModel model = (DefaultTreeModel) navigationTree.getModel();
		DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) navigationTree.getLastSelectedPathComponent();
		CustomUserObject uObj = new CustomUserObject(nodeId, name);
		selectedNode.add(new DefaultMutableTreeNode(uObj));
		model.reload(selectedNode);

	}

	public static String getMaxColumnValue(JTable table) {
		ArrayList<Integer> list = new ArrayList<Integer>();
		for (int i = 0; i < table.getRowCount(); i++) {
			list.add(Integer.parseInt(table.getValueAt(i, 0).toString()));
		}
		return (String) Collections.max(list).toString();
	}

	public static Dimension toolBarButtonDimension() {
		Dimension dim = new Dimension(50, 20);
		return dim;
	}

	public static JComboBox<String> createDropdownList(List<String[]> arrayList) {

		JComboBox<String> cmbBox = new JComboBox<String>();
		for (String[] array : arrayList) {
			cmbBox.addItem(array[1]);
		}
		return cmbBox;
	}

	private static void inputModule(boolean insert) {
		JTextField name = new JTextField();
		JTextArea desc = new JTextArea();
		desc.setLineWrap(true);
		desc.setRows(5);

		Object[] msg = { "Module Name:", name, "Description:", desc };
		JOptionPane op = new JOptionPane(msg, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null, null);
		String title="";
		if(insert) {
			title = "Insert Module";
		} else {
			title = "Add Module";
		}
		JDialog dialog = op.createDialog(null, title);
		dialog.setSize(new Dimension(400, 300));
		dialog.setResizable(false);
		dialog.setVisible(true);
		int row = cTable.getSelectedRow();

		if (null == op.getValue()) {
			//System.out.println("User closed dialog");
		} else {
			switch (((Integer) op.getValue()).intValue()) {
			case JOptionPane.OK_OPTION:
				if (name.getText().isEmpty()) {
					JOptionPane.showMessageDialog(null, "Please enter module name", "Error", JOptionPane.ERROR_MESSAGE);
					dialog.setVisible(true);
				} else {
					if(insert) {
						dbProcess.insertModule(name.getText(),desc.getText(),row);
					} else {
						DefaultTableModel tNewModel = (DefaultTableModel) cTable.getModel();
						tNewModel.addRow(new Object[] { "", name.getText(), desc.getText(), "" });
						cTable.setRowSelectionInterval(cTable.getRowCount() -1, cTable.getRowCount() -1);
					}
				}
				break;
			case JOptionPane.CANCEL_OPTION:
				//System.out.println("User selected Cancel");
				break;
			}
		}

	}

	private static void inputTestRun() {
		JTextField name = new JTextField();

		Object[] msg = { "Test Run Name:", name };
		JOptionPane op = new JOptionPane(msg, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null, null);
		JDialog dialog = op.createDialog(null, "Add Test Run");
		dialog.setSize(new Dimension(300, 150));
		dialog.setResizable(false);
		dialog.setVisible(true);

		if (null == op.getValue()) {
			//System.out.println("User closed dialog");
		} else {
			switch (((Integer) op.getValue()).intValue()) {
			case JOptionPane.OK_OPTION:
				if (name.getText().isEmpty()) {
					JOptionPane.showMessageDialog(null, "Please enter Run name", "Error", JOptionPane.ERROR_MESSAGE);
				} else {
					DefaultTableModel tNewModel = (DefaultTableModel) cTable.getModel();
					tNewModel.addRow(new Object[] { "", name.getText(), "", "", "", "" });
					cTable.setRowSelectionInterval(cTable.getRowCount() -1, cTable.getRowCount() -1);
				}
				break;
			case JOptionPane.CANCEL_OPTION:
				//System.out.println("User selected Cancel");
				break;
			}
		}

	}

	private static boolean processLine(String s, JTextArea logs) {
		boolean endOfFile = false;
		if (s.endsWith("Execution Finished!")) {
			endOfFile = true;
		}
		logs.append(s + "\n");
		logs.setCaretPosition(logs.getDocument().getLength());

		return endOfFile;
	}

	// Method for getting date and time
	private static String getTime() {
		// Set format for timestamp
		SimpleDateFormat df = new SimpleDateFormat("MMM-dd-yyyy HH.mm.ss");
		return df.format(new Date());
	}

	private static String getTimeDifference(String start, String end) {
		try {
			DateFormat df = new SimpleDateFormat("MMM-dd-yyyy HH.mm.ss");
			Date d1 = df.parse(start);
			Date d2 = df.parse(end);
			long diffInMilliseconds = Math.abs(d1.getTime() - d2.getTime());
			int seconds = (int) diffInMilliseconds / 1000;
			// calculate hours minutes and seconds
			int hours = seconds / 3600;
			int minutes = (seconds % 3600) / 60;
			seconds = (seconds % 3600) % 60;
			DecimalFormat nf = new DecimalFormat("00");
			return nf.format(hours) + ":" + nf.format(minutes) + ":" + nf.format(seconds);
		} catch (Exception e) {
			// nothing to do
		}

		return "";
	}

	public static void executeTestRun(String logName, String runId, String browser, String reRun) {
		// execute test driver in new thread
		Thread driverThread = new Thread(new Runnable() {
			public void run() {
				try {
					if (dbConfig.getConfigValue("LOG_CONSOLE").contentEquals("Y")
							|| dbConfig.getConfigValue("LOG_CONSOLE").contentEquals("")) {
						Thread logThread = new Thread(new Runnable() {
							public void run() {
								 addLogsTab(logName);
							}
						});
						logThread.start();
					}
					
					TestDriver testDriver = new TestDriver();
					if(reRun.contentEquals("Y")) {
						testDriver.executeTestRunFailed(logName, runId, browser, new DBProcess(dbPath));	
					} else {
						testDriver.executeTestRun(logName, runId, browser, new DBProcess(dbPath));	
					}
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		driverThread.start();
	}

	public static ActionListener adminPassActionListener() {
		ActionListener action = new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				JPanel panel = new JPanel();
				JLabel label = new JLabel("Password:");
				JPasswordField password = new JPasswordField(20);
				panel.add(label);
				panel.add(password);
				String[] options = new String[] { "OK", "Cancel" };
				int option = JOptionPane.showOptionDialog(frame, panel, "Admin Password", JOptionPane.NO_OPTION,
						JOptionPane.PLAIN_MESSAGE, null, options, password);
				submitPassword(option,password);
			}
		};
		return action;
	}
	public static void submitPassword(int option, JPasswordField password) {
		if (option == 0) {
			String pass = new String(password.getPassword());
			if (adminAccess != null) {
				if (!pass.contentEquals(adminAccess)) {
					JOptionPane.showMessageDialog(frame, "Password is invalid!", "Error",
							JOptionPane.ERROR_MESSAGE);
				} else {
					adminPass = true;
					String title = frame.getTitle();
					if (!title.contentEquals("Test Machine")) {
						loadRecentTestSuite(frame.getTitle().replaceFirst("Test Machine - ", ""));
					}

					JOptionPane.showMessageDialog(frame, "Admin access enabled!", "Admin",
							JOptionPane.INFORMATION_MESSAGE);
					subMenuNew.setVisible(true);
					subToolMenuMod.setVisible(true);
					subToolMenuVar.setVisible(true);
					subToolMenuActions.setVisible(true);
					subMenuAdmin.setVisible(false);
					subMenuExitAdmin.setVisible(true);

				}
			} else {
				JOptionPane.showMessageDialog(frame, "Admin access not allowed!", "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		}
		
	}
	public static ActionListener adminExitActionListener() {
		ActionListener action = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				adminPass = false;
				String title = frame.getTitle();
				if (!title.contentEquals("Test Machine")) {
					loadRecentTestSuite(frame.getTitle().replaceFirst("Test Machine - ", ""));
				}
				JOptionPane.showMessageDialog(frame, "Exit success!", "Admin", JOptionPane.INFORMATION_MESSAGE);
				subMenuNew.setVisible(false);
				subToolMenuMod.setVisible(false);
				subToolMenuVar.setVisible(false);
				subToolMenuActions.setVisible(false);
				subMenuAdmin.setVisible(true);
				subMenuExitAdmin.setVisible(false);
			}
		};
		return action;
	}
	public static Thread autoBackup(String sourcePath) {
		String interval = dbConfig.getConfigValue("BACKUP_INTERVAL");
		String destPath = dbConfig.getConfigValue("BACKUP_LOC");
		String backup = dbConfig.getConfigValue("BACKUP");
		if(backup.contentEquals("N") || Integer.parseInt(interval) < 1 || sourcePath.contentEquals("") || sourcePath.isEmpty() || sourcePath == null
				|| destPath.contentEquals("") || destPath.isEmpty() || destPath == null) {
			return null;
		}
		if(autobackup != null) {
			autobackup.interrupt();
		}
		// thread backup interval
		Thread backupThread = new Thread(new Runnable() {
			public void run() {
				try {
					boolean loop = true;
					while(loop) {
						String backup = dbConfig.getConfigValue("BACKUP");
						if(backup.contentEquals("N")) {loop = false;}
						
						String interval = dbConfig.getConfigValue("BACKUP_INTERVAL");
						Thread.sleep((Integer.parseInt(interval) * 1000) * 60);
						
						String destPath = dbConfig.getConfigValue("BACKUP_LOC");
						File source = new File(sourcePath);
						File dest = new File(destPath + "/" + source.getName().replaceFirst("[.][^.]+$", "") + "." + getTime() + ".db");
						try {
						    FileUtils.copyFile(source, dest);
						} catch (IOException e) {
						  //  e.printStackTrace();
						}
					}
				} catch (Exception e) {
					//e.printStackTrace();
				}
			}
		});
		backupThread.start();
		System.out.println("backupThread:" + backupThread.getId());
		return backupThread;
	}
	public static void setTableRowHeight() {
		int rowCount = cTable.getRowCount();
		for(int x =0; x < rowCount;x++) {
			cTable.setRowHeight(x, 23);
		}
	}
	public static void loadTheme(boolean update) {
		try {
			String theme = dbConfig.getConfigValue("UI_THEME");
			if(theme.equalsIgnoreCase("Nimbus")) {
				UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
				buttonsFont = new Font("Calibri Light", Font.PLAIN, 12);
				
				logsBg =  Color.WHITE; 
				logsFg = Color.BLACK;
				logsFont = new Font("Calibri Light", Font.PLAIN, 12);
				
				detailsBg = new Color(0x39698A);
				detailsFg = Color.WHITE;
				detailsFont = new Font("Calibri Light", Font.PLAIN, 12);
				textDetailsBg = Color.WHITE;
				textDetailsFg = Color.BLACK;
				textDetailsFont = new Font("Calibri Light", Font.PLAIN, 12);
				
				treeBg = Color.WHITE;
				treeFg = Color.BLACK;
				treeFont = new Font("Calibri Light", Font.PLAIN, 12);
				highlightSelection = new Color(0x3366ff);
				
				tableHeaderBg =new Color(0x39698A);
				tableHeaderFg = Color.WHITE;
				tableGridColor = new Color(0x99ccff);
				tablHeadereFont = new Font("Calibri", Font.BOLD, 11);
				
				tableCellBg = Color.WHITE;
				tableCellFg = Color.BLACK;
				tableHighlightSelBg = new Color(0x3366ff);
				tableHighlightSelFg = Color.WHITE;
				
			} else if(theme.equalsIgnoreCase("Web")){
				UIManager.setLookAndFeel("com.alee.laf.WebLookAndFeel");
				buttonsFont = new Font("Calibri Light", Font.PLAIN, 12);
				
				logsBg =  Color.WHITE; 
				logsFg = Color.BLACK;
				logsFont = new Font("Calibri Light", Font.PLAIN, 12);
				
				detailsBg = Color.LIGHT_GRAY;
				detailsFg = Color.BLACK;
				detailsFont = new Font("Calibri Light", Font.PLAIN, 12);
				textDetailsBg = Color.WHITE;
				textDetailsFg = Color.BLACK;
				textDetailsFont = new Font("Calibri Light", Font.PLAIN, 12);
				
				treeBg = Color.WHITE;
				treeFg = Color.BLACK;
				treeFont = new Font("Calibri Light", Font.PLAIN, 12);
				highlightSelection = new Color(0x3366ff);
				
				tableHeaderBg =new Color(0x39698A);
				tableHeaderFg = Color.BLACK;
				tableGridColor = new Color(0x99ccff);
				tablHeadereFont = new Font("Calibri", Font.BOLD, 11);
				
				tableCellBg = Color.WHITE;
				tableCellFg = Color.BLACK;
				tableHighlightSelBg = new Color(0x3366ff);
				tableHighlightSelFg = Color.WHITE;

			} else if(theme.equalsIgnoreCase("NimROD")){
				UIManager.setLookAndFeel( "com.nilo.plaf.nimrod.NimRODLookAndFeel");
				buttonsFont = new Font("Calibri Light", Font.PLAIN, 12);
				
				logsBg =  Color.WHITE; 
				logsFg = Color.BLACK;
				logsFont = new Font("Calibri Light", Font.PLAIN, 12);
				
				detailsBg = new Color(0xefc700);
				detailsFg = Color.BLACK;
				detailsFont = new Font("Calibri Light", Font.PLAIN, 12);
				textDetailsBg = Color.WHITE;
				textDetailsFg = Color.BLACK;
				textDetailsFont = new Font("Calibri Light", Font.PLAIN, 12);
				
				treeBg = Color.WHITE;
				treeFg = Color.BLACK;
				treeFont = new Font("Calibri Light", Font.PLAIN, 12);
				highlightSelection = new Color(0xffdd33);
				
				tableHeaderBg =new Color(0xefc700);
				tableHeaderFg = Color.BLACK;
				tableGridColor = new Color(0xffe666);
				tablHeadereFont = new Font("Calibri", Font.BOLD, 11);
				
				tableCellBg = Color.WHITE;
				tableCellFg = Color.BLACK;
				tableHighlightSelBg = new Color(0xefc700);
				tableHighlightSelFg = Color.BLACK;
			} else if(theme.equalsIgnoreCase("Windows Classic")){
				UIManager.setLookAndFeel( "com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel");
				buttonsFont = new Font("Calibri Light", Font.PLAIN, 12);
				
				logsBg =  Color.WHITE; 
				logsFg = Color.BLACK;
				logsFont = new Font("Calibri Light", Font.PLAIN, 12);
				
				detailsBg = new Color(0x39698A);
				detailsFg = Color.WHITE;
				detailsFont = new Font("Calibri Light", Font.PLAIN, 12);
				textDetailsBg = Color.WHITE;
				textDetailsFg = Color.BLACK;
				textDetailsFont = new Font("Calibri Light", Font.PLAIN, 12);
				
				treeBg = Color.WHITE;
				treeFg = Color.BLACK;
				treeFont = new Font("Calibri Light", Font.PLAIN, 12);
				highlightSelection = new Color(0x3366ff);
				
				tableHeaderBg =new Color(0x39698A);
				tableHeaderFg = Color.WHITE;
				tableGridColor = new Color(0x99ccff);
				tablHeadereFont = new Font("Calibri", Font.BOLD, 11);
				
				tableCellBg = Color.WHITE;
				tableCellFg = Color.BLACK;
				tableHighlightSelBg = new Color(0x3366ff);
				tableHighlightSelFg = Color.WHITE;
			} else if(theme.equalsIgnoreCase("JTattoo McWin")){
				UIManager.setLookAndFeel("com.jtattoo.plaf.mcwin.McWinLookAndFeel");
				/*AluminiumLookAndFeel GraphiteLookAndFeel LunaLookAndFeel MintLookAndFeel*/
				buttonsFont = new Font("Calibri Light", Font.PLAIN, 12);
				
				logsBg =  Color.WHITE; 
				logsFg = Color.BLACK;
				logsFont = new Font("Calibri Light", Font.PLAIN, 12);
				
				detailsBg = new Color(0xccddff);
				detailsFg = Color.BLACK;
				detailsFont = new Font("Calibri Light", Font.PLAIN, 12);
				textDetailsBg = Color.WHITE;
				textDetailsFg = Color.BLACK;
				textDetailsFont = new Font("Calibri Light", Font.PLAIN, 12);
				
				treeBg = Color.WHITE;
				treeFg = Color.BLACK;
				treeFont = new Font("Calibri Light", Font.PLAIN, 12);
				highlightSelection = new Color(0xccddff);
				
				tableHeaderBg =new Color(0x80aaff);
				tableHeaderFg = Color.BLACK;
				tableGridColor = new Color(0x99ccff);
				tablHeadereFont = new Font("Calibri", Font.BOLD, 11);
				
				tableCellBg = Color.WHITE;
				tableCellFg = Color.BLACK;
				tableHighlightSelBg = new Color(0xb3ccff);
				tableHighlightSelFg = Color.BLACK;
			}  else {
				UIManager.setLookAndFeel( "com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
				buttonsFont = new Font("Calibri Light", Font.PLAIN, 12);
				
				logsBg =  Color.WHITE; 
				logsFg = Color.BLACK;
				logsFont = new Font("Calibri Light", Font.PLAIN, 12);
				
				detailsBg = new Color(0x0066ff);
				detailsFg = Color.WHITE;
				detailsFont = new Font("Calibri Light", Font.PLAIN, 12);
				textDetailsBg = Color.WHITE;
				textDetailsFg = Color.BLACK;
				textDetailsFont = new Font("Calibri Light", Font.PLAIN, 12);
				
				treeBg = Color.WHITE;
				treeFg = Color.BLACK;
				treeFont = new Font("Calibri Light", Font.PLAIN, 12);
				highlightSelection = new Color(0x0066ff);
				
				tableHeaderBg =new Color(0x0066ff);
				tableHeaderFg = Color.WHITE;
				tableGridColor = new Color(0x80b3ff);
				tablHeadereFont = new Font("Calibri", Font.BOLD, 11);
				
				tableCellBg = Color.WHITE;
				tableCellFg = Color.BLACK;
				tableHighlightSelBg = new Color(0x3385ff);
				tableHighlightSelFg = Color.WHITE;
			}
			UIManager.put("Tree.leafIcon", UIManager.getIcon("FileView.fileIcon"));
			if(update) {		
				/*int h = frame.getHeight();
				int w = frame.getWidth();
				SwingUtilities.updateComponentTreeUI(frame);
				frame.setSize(w, h);*/
				strTheme = theme;
				frame.dispose();
				UIManager.put("Tree.leafIcon", UIManager.getIcon("FileView.fileIcon"));
				new MainUI();
			}
			
			
		} catch (Exception e1) {
			JOptionPane.showMessageDialog(null, e1.getMessage(), "Error loading theme.", JOptionPane.ERROR_MESSAGE);
			e1.printStackTrace();
		}
	}
}

class CustomRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 6703872492730589499L;

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		Component cellComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		String cellVal = table.getModel().getValueAt(row, column).toString();
		if (cellVal.contentEquals("Failed")) {
			cellComponent.setBackground(Color.red);
		} else if (cellVal.contentEquals("Passed")) {
			cellComponent.setBackground(new Color(0x33cc33));
		} else if (cellVal.contentEquals("Ongoing")) {
			cellComponent.setBackground(Color.yellow);
			cellComponent.setForeground(Color.BLACK);
		} else if (cellVal.contentEquals("Not Started")) {
			cellComponent.setBackground(Color.lightGray);
			cellComponent.setForeground(Color.BLACK);
		} else {
			cellComponent.setBackground(new Color(0xe6e6e6));
			cellComponent.setForeground(Color.BLACK);
			if (table.isCellSelected(row, column)) {
				cellComponent.setBackground(Color.DARK_GRAY);
				cellComponent.setForeground(Color.white);
			}
		}
		setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		return cellComponent;
	}
}

class CustomRendererEditable extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 6703872492730589499L;
	private Color bg;
	private Color fg;
	private Color selBg;
	private Color selFg;
	public CustomRendererEditable(Color bg1,Color fg1,Color sel,Color sel1) {
		bg=bg1;
		fg=fg1;
		selBg=sel;
		selFg=sel1;
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		Component cellComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

		cellComponent.setBackground(bg);
		cellComponent.setForeground(fg);

		if (table.isCellSelected(row, column)) {
			cellComponent.setBackground(selBg);
			cellComponent.setForeground(selFg);
		}
		setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		return cellComponent;
	}
}

@SuppressWarnings("serial")
class CustomRendererReadOnly extends DefaultTableCellRenderer {
	private Color bg;
	private Color fg;
	private Color selBg;
	private Color selFg;
	public CustomRendererReadOnly(Color bg1,Color fg1,Color sel,Color sel1) {
		bg=bg1;
		fg=fg1;
		selBg=sel;
		selFg=sel1;
	}
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		Component cellComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

	    int r = bg.getRed();
		int b = bg.getBlue();
		int g = bg.getGreen();
		
		Color darker = new Color((int)(r*.93), (int)(g*.93), (int)(b*.93));
		
		cellComponent.setBackground(darker);
		cellComponent.setForeground(fg);

		if (table.isCellSelected(row, column)) {
			cellComponent.setBackground(selBg);
			cellComponent.setForeground(selFg);
		}
		setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		return cellComponent;
	}
}
@SuppressWarnings("serial")
class HeaderRenderer extends JLabel implements TableCellRenderer {
	 
    public HeaderRenderer(Color bg,Color fg,Font ft,Color grd) {
        setFont(ft);
        setForeground(fg);
        setBackground(bg);
        setOpaque(true);
        setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(grd),BorderFactory.createEmptyBorder(5, 5, 5, 5)));
    }
     
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        setText(value.toString());
        return this;
    }
 
}

@SuppressWarnings("serial")
class MyCellRenderer extends DefaultTreeCellRenderer {

	private Color selColor; 
	public MyCellRenderer(Color clr) {
		selColor = clr;
	}
    @Override
    public Color getBackgroundNonSelectionColor() {
        return (null);
    }

    @Override
    public Color getBackgroundSelectionColor() {
        return selColor;
    }

    @Override
    public Color getBackground() {
        return (null);
    }

    @Override
    public Component getTreeCellRendererComponent(final JTree tree, final Object value, final boolean sel, final boolean expanded, final boolean leaf, final int row, final boolean hasFocus) {
        final Component ret = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

        @SuppressWarnings("unused")
		final DefaultMutableTreeNode node = ((DefaultMutableTreeNode) (value));
        this.setText(value.toString());

        return ret;
    }
}

@SuppressWarnings("serial")
class BPosTextArea extends JTextArea {

    private int radius;

    public BPosTextArea() {
        super(1, 3);
        setOpaque(false);
        setBorder(null);
        setRadius(15);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, getRadius(), getRadius());
        super.paintComponent(g);
    }

    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, getRadius(), getRadius());
    }

    public void setRadius(int radius) {
        this.radius = radius;
        repaint();
    }

    public int getRadius() {
        return radius;
    }

    @Override
    public Insets getInsets() {
        int value = getRadius() / 2;
        return new Insets(value, value, value, value);
    }

}
@SuppressWarnings("serial")
class BPosTextField extends JTextField {

    private int radius;

    public BPosTextField() {
        setOpaque(false);
        setBorder(null);
        setRadius(15);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, getRadius(), getRadius());
        super.paintComponent(g);
    }

    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, getRadius(), getRadius());
    }

    public void setRadius(int radius) {
        this.radius = radius;
        repaint();
    }

    public int getRadius() {
        return radius;
    }

    @Override
    public Insets getInsets() {
        int value = getRadius() / 2;
        return new Insets(value, value, value, value);
    }

}
@SuppressWarnings("serial")
class MyCloseButton extends JButton {
	  public MyCloseButton() {
	    super("x");
	    setBorder(BorderFactory.createEmptyBorder());
	    setFocusPainted(false);
	    setBorderPainted(false);
	    setContentAreaFilled(false);
	    setRolloverEnabled(false);
	  }
	  @Override
	  public Dimension getPreferredSize() {
	    return new Dimension(16, 16);
	  }
}



