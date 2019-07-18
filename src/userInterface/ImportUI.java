package userInterface;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import database.DBProcess;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JSplitPane;
import java.awt.GridBagConstraints;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.JTable;

@SuppressWarnings("serial")
public class ImportUI extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private static JTable cTable;
	public static JButton btnImport = new JButton("Import");
	private static int[] modColWidth = new int[] { 200, 300, 100 };
	private static int[] tcColWidth = new int[] { 200, 300, 100 };
	private static int[] stepsColWidth = new int[] { 50, 200, 60, 100, 100, 100, 100, 50 };
	private int rowindex = 0;
	private String tcId = "";
	public ImportUI(DBProcess dbp,DBProcess tDbp,String pathName,String imp,String tci,int rowi) {
		rowindex = rowi;
		tcId = tci;

		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setModal(true);
		setTitle("Import " + imp + " - " + pathName);
		setBounds(100, 100, 900, 500);
		
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[]{0, 0};
		gbl_contentPanel.rowHeights = new int[]{0, 0};
		gbl_contentPanel.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_contentPanel.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		contentPanel.setLayout(gbl_contentPanel);
		{
			JSplitPane splitPane = new JSplitPane();
			GridBagConstraints gbc_splitPane = new GridBagConstraints();
			gbc_splitPane.fill = GridBagConstraints.BOTH;
			gbc_splitPane.gridx = 0;
			gbc_splitPane.gridy = 0;
			contentPanel.add(splitPane, gbc_splitPane);
			{
				JScrollPane scrollPane = new JScrollPane();
				splitPane.setLeftComponent(scrollPane);
				{
					JTree tree = new JTree();
					tree.setRootVisible(false);
					tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
					tree.setModel(new DefaultTreeModel(null));
					scrollPane.setViewportView(tree);
					createRootNode(tree,dbp,imp);
					
				}
			}
			{
				JScrollPane scrollPane = new JScrollPane();
				splitPane.setRightComponent(scrollPane);
				{
					cTable = new JTable();
					cTable.setShowGrid(true);
					cTable.setGridColor(new Color(0x99ccff));
					JTableHeader header = cTable.getTableHeader();
					header.setDefaultRenderer(new HeaderRenderer(MainUI.tableHeaderBg,MainUI.tableHeaderFg,MainUI.tablHeadereFont,MainUI.tableGridColor));
					scrollPane.setViewportView(cTable);
				}
			}
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				buttonPane.add(btnImport);
				getRootPane().setDefaultButton(btnImport);
				btnImport.setEnabled(false);
				for (ActionListener act : btnImport.getActionListeners()) {
					btnImport.removeActionListener(act);
				}
				btnImport.addActionListener(btnImportActionListener(dbp,tDbp));
			}
		}
	}
	public static void createRootNode(JTree navigationTree,DBProcess dbProcess,String imp) {
		CustomUserObject uObjTC = new CustomUserObject("ROOT", "ROOT");
		navigationTree.setModel(new DefaultTreeModel(new DefaultMutableTreeNode(uObjTC) {
			private static final long serialVersionUID = 1L;
			{
				DefaultMutableTreeNode tcNode;
				CustomUserObject uObjTc = new CustomUserObject("TC", "TEST PLAN");
				tcNode = new DefaultMutableTreeNode(uObjTc);
				add(tcNode);
				createTestcaseNode(tcNode,dbProcess);
			}
		}));
		navigationTree.expandRow(0);
		navigationTree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent se) {
				DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) navigationTree
						.getLastSelectedPathComponent();
				if (selectedNode != null) {
					CustomUserObject tNode = (CustomUserObject) selectedNode.getUserObject();
					int nodeLvl = selectedNode.getLevel();
					if (nodeLvl == 1) {
						displayModules(dbProcess);
						if(imp.contentEquals("Modules")) {
							btnImport.setEnabled(true);
						} else {
							btnImport.setEnabled(false);
						}
					} else if (nodeLvl == 2) {
						displayTestcases(tNode.getId(),dbProcess);
						if(imp.contentEquals("Testcase")) {
							btnImport.setEnabled(true);
						} else {
							btnImport.setEnabled(false);
						}
					}else if (nodeLvl == 3) {
						displaySteps(tNode.getId(),dbProcess);
						if(imp.contentEquals("Test Steps")) {
							btnImport.setEnabled(true);
						} else {
							btnImport.setEnabled(false);
						}
					} else {
						btnImport.setEnabled(false);
					}
				} else {
					btnImport.setEnabled(false);
				}
			}
		});
	}
	public static void createTestcaseNode(DefaultMutableTreeNode tcNode,DBProcess dbProcess) {
		List<String[]> modList = dbProcess.getModules();
		for (String[] rowTcs : modList) {
			// create parent folder
			CustomUserObject objRun = new CustomUserObject(rowTcs[0], rowTcs[1]);
			DefaultMutableTreeNode tcNodeRun = new DefaultMutableTreeNode(objRun);
			List<String[]> tc = dbProcess.getTestcases(rowTcs[0]);
			// create tree node
			for (String[] rowTc : tc) {
				CustomUserObject uObjTcs = new CustomUserObject(rowTc[0], rowTc[1]);
				tcNodeRun.add(new DefaultMutableTreeNode(uObjTcs));
			}
			tcNode.add(tcNodeRun);
		}
	}
	public static void displayModules(DBProcess dbProcess) {
		cTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		List<String[]> mod = dbProcess.getModules();
		String[][] modValue = new String[mod.size()][];
		for (int i = 0; i < modValue.length; i++) {
			String[] row = mod.get(i);
			modValue[i] = row;
		}
		DefaultTableModel tModel = new DefaultTableModel(modValue,
				new String[] { "TCMODULES", "NAME", "DESCRIPTION", "FLOW" }) {
			boolean[] columnEditables = new boolean[] { false, false, false, false };

			public boolean isCellEditable(int row, int column) {
				return columnEditables[column];
			}
		};
		CustomRendererEditable cRenderer = new CustomRendererEditable(MainUI.tableCellBg,MainUI.tableCellFg,MainUI.tableHighlightSelBg,MainUI.tableHighlightSelFg);

		cTable.setModel(tModel);
		cTable.getColumnModel().getColumn(0).setMinWidth(0);
		cTable.getColumnModel().getColumn(0).setMaxWidth(0);
		cTable.getColumnModel().getColumn(0).setPreferredWidth(0);
		cTable.getColumnModel().getColumn(1).setPreferredWidth(modColWidth[0]);
		cTable.getColumnModel().getColumn(2).setPreferredWidth(modColWidth[1]);
		cTable.getColumnModel().getColumn(3).setPreferredWidth(modColWidth[2]);
		cTable.getColumnModel().getColumn(1).setCellRenderer(cRenderer);
		cTable.getColumnModel().getColumn(2).setCellRenderer(cRenderer);
		cTable.getColumnModel().getColumn(3).setCellRenderer(cRenderer);
		cTable.getColumnModel().getColumn(1).setCellRenderer(cRenderer);
		cTable.getColumnModel().getColumn(2).setCellRenderer(cRenderer);
		cTable.getColumnModel().getColumn(3).setCellRenderer(cRenderer);
		setTableRowHeight(cTable);

	}
	public static void setTableRowHeight(JTable table) {
		int rowCount = table.getRowCount();
		for(int x =0; x < rowCount;x++) {
			table.setRowHeight(x, 23);
		}
	}
	public static void displayTestcases(String moduleId, DBProcess dbProcess) {
		cTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		List<String[]> mod = dbProcess.getTestcases(moduleId);
		String[][] tcValue = new String[mod.size()][];
		for (int i = 0; i < tcValue.length; i++) {
			String[] row = mod.get(i);
			tcValue[i] = row;
		}

		DefaultTableModel tModel = new DefaultTableModel(tcValue,
				new String[] { "TCS", "NAME", "DESCRIPTION", "FLOW" }) {
			boolean[] columnEditables = new boolean[] { false, false, false, false };

			public boolean isCellEditable(int row, int column) {
				return columnEditables[column];
			}
		};
		CustomRendererEditable cRenderer = new CustomRendererEditable(MainUI.tableCellBg,MainUI.tableCellFg,MainUI.tableHighlightSelBg,MainUI.tableHighlightSelFg);
		cTable.setModel(tModel);
		cTable.getColumnModel().getColumn(0).setMinWidth(0);
		cTable.getColumnModel().getColumn(0).setMaxWidth(0);
		cTable.getColumnModel().getColumn(0).setPreferredWidth(0);
		cTable.getColumnModel().getColumn(1).setPreferredWidth(tcColWidth[0]);
		cTable.getColumnModel().getColumn(2).setPreferredWidth(tcColWidth[1]);
		cTable.getColumnModel().getColumn(3).setPreferredWidth(tcColWidth[2]);
		cTable.getColumnModel().getColumn(1).setCellRenderer(cRenderer);
		cTable.getColumnModel().getColumn(2).setCellRenderer(cRenderer);
		cTable.getColumnModel().getColumn(3).setCellRenderer(cRenderer);
		setTableRowHeight(cTable);

	}
	public static void displaySteps(String tcID,DBProcess dbProcess) {
		cTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		List<String[]> steps = dbProcess.getSteps(tcID);
		// convert arraylist to array[][]
		String[][] stepsValue = new String[steps.size()][];
		for (int i = 0; i < stepsValue.length; i++) {
			String[] row = steps.get(i);
			stepsValue[i] = row;
		}

		DefaultTableModel tModel = new DefaultTableModel(stepsValue,
				new String[] { "TCSTEPS", tcID, "FLOW", "DESCRIPTION", "SELECTOR", "ELEMENT", "ACTION",
						"PARAMETER NAME", "PARAMETER VALUE", "SCREEN CAPTURE" }) {
			boolean[] columnEditables = new boolean[] { false, false, false, true, true, true, true, true, true, true };

			public boolean isCellEditable(int row, int column) {
				return columnEditables[column];
			}
		};
		CustomRendererEditable cRenderer = new CustomRendererEditable(MainUI.tableCellBg,MainUI.tableCellFg,MainUI.tableHighlightSelBg,MainUI.tableHighlightSelFg);
		CustomRendererReadOnly cRendererRead = new CustomRendererReadOnly(MainUI.tableCellBg,MainUI.tableCellFg,MainUI.tableHighlightSelBg,MainUI.tableHighlightSelFg);
		cTable.setModel(tModel);
		// hide first and second column
		cTable.getColumnModel().getColumn(0).setMinWidth(0);
		cTable.getColumnModel().getColumn(0).setMaxWidth(0);
		cTable.getColumnModel().getColumn(0).setPreferredWidth(0);
		cTable.getColumnModel().getColumn(1).setMinWidth(0);
		cTable.getColumnModel().getColumn(1).setMaxWidth(0);
		cTable.getColumnModel().getColumn(1).setPreferredWidth(0);
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
		setTableRowHeight(cTable);
	}
	public ActionListener btnImportActionListener(DBProcess dbp,DBProcess tDbp) {
		ActionListener action = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					btnImport.setEnabled(false);
					DefaultTableModel tNewModel = (DefaultTableModel) cTable.getModel();
					String tableName = tNewModel.getColumnName(0);
					if (tableName.contentEquals("TCSTEPS")) {
						importSteps(dbp,tDbp);
					} else if (tableName.contentEquals("TCS")) {
						importTC(dbp,tDbp);
					} else if (tableName.contentEquals("TCMODULES")) {
						importModule(dbp,tDbp);
					}
					
				} catch (Exception err) {
					JOptionPane.showMessageDialog(null, "Import Error", "Error", JOptionPane.ERROR_MESSAGE);
					dispose();
				}

			}
		};
		return action;
	}
	public void importSteps(DBProcess dbp,DBProcess tDbp) {
		int row = cTable.getSelectedRow();
		if (row == -1) {
			JOptionPane.showMessageDialog(cTable.getRootPane(), "Please select Test Steps to Import.", "Import",
					JOptionPane.ERROR_MESSAGE);
		} else {
			
			DefaultTableModel tModel = (DefaultTableModel) cTable.getModel();
			String stepId = "";
			int[] rows = cTable.getSelectedRows();
			for (int r : rows) {
				stepId = stepId + (String) tModel.getValueAt(r, 0) + ",";
			}
			stepId = stepId.substring(0, stepId.length() - 1);
			tDbp.importSteps(dbp.getStepsIN(stepId), tcId,rowindex);
			dispose();
		}
	}
	public void importTC(DBProcess dbp,DBProcess tDbp) {
		int row = cTable.getSelectedRow();
		if (row == -1) {
			JOptionPane.showMessageDialog(cTable.getRootPane(), "Please select Testcases to Import.", "Import",
					JOptionPane.ERROR_MESSAGE);
		} else {
			
			DefaultTableModel tModel = (DefaultTableModel) cTable.getModel();
			String tc = "";
			int[] rows = cTable.getSelectedRows();
			for (int r : rows) {
				tc = (String) tModel.getValueAt(r, 0);
				long tId = tDbp.importTestcase(dbp.getTcIN(tc), tcId,rowindex);
				if (tId > 0) {
					String str_tId = String.valueOf(tId);
					tDbp.importSteps(dbp.getSteps(tc), str_tId,0);
				}
				rowindex++;
			}
			dispose();
		}
	}
	public void importModule(DBProcess dbp,DBProcess tDbp) {
		int row = cTable.getSelectedRow();
		if (row == -1) {
			JOptionPane.showMessageDialog(cTable.getRootPane(), "Please select Modules to Import.", "Import",
					JOptionPane.ERROR_MESSAGE);
		} else {
			DefaultTableModel tModel = (DefaultTableModel) cTable.getModel();
			String mod = "";
			int[] rows = cTable.getSelectedRows();
			for (int r : rows) {
				mod = (String) tModel.getValueAt(r, 0);
				long modId= tDbp.importModule(dbp.getModule(mod), tcId,rowindex);
				if (modId > 0) {
					List<String[]> tcList = dbp.getTestcases(mod);
					for (String[] tcArr : tcList) {
						String str_mId = String.valueOf(modId);
						long tId = tDbp.importTestcase(dbp.getTcIN(tcArr[0]), str_mId,-1);
						if(tId > 0) {
							String str_tId = String.valueOf(tId);
							tDbp.importSteps(dbp.getSteps(tcArr[0]), str_tId,-1);
						}
					}
				}
				rowindex++;
			}
			dispose();
		}
	}

}
