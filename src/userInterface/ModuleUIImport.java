package userInterface;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;

import database.DBProcess;

@SuppressWarnings("serial")
public class ModuleUIImport extends JDialog {
	private JTable tableName;
	private JTable tableSteps;
	private int[] stepsColWidth = new int[] { 50, 200, 60, 100, 100, 100, 100, 50 };

	/**
	 * Create the dialog.
	 */
	public ModuleUIImport(DBProcess dbp,DBProcess tDbp,String pathName) {

		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setModal(true);
		setTitle("Import Modules - " + pathName);
		setBounds(100, 100, 900, 500);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		getContentPane().setLayout(gridBagLayout);

		JSplitPane splitPane = new JSplitPane();
		GridBagConstraints gbc_splitPane = new GridBagConstraints();
		gbc_splitPane.fill = GridBagConstraints.BOTH;
		gbc_splitPane.gridx = 0;
		gbc_splitPane.gridy = 0;
		getContentPane().add(splitPane, gbc_splitPane);

		JScrollPane scrollPane_1 = new JScrollPane();
		splitPane.setLeftComponent(scrollPane_1);
		splitPane.setDividerLocation(100);
		tableName = new JTable();
		tableName.setShowGrid(true);
		tableName.setGridColor(new Color(0x99ccff));
		JTableHeader header = tableName.getTableHeader();
		header.setDefaultRenderer(new HeaderRenderer(MainUI.tableHeaderBg,MainUI.tableHeaderFg,MainUI.tablHeadereFont,MainUI.tableGridColor));
		scrollPane_1.setViewportView(tableName);
		//tableName.addMouseListener(tableNameMouseListener(dbp));
		if (dbp != null) {
			displayName(dbp);
		}
		JScrollPane scrollPane = new JScrollPane();
		splitPane.setRightComponent(scrollPane);

		tableSteps = new JTable();
		tableSteps.setShowGrid(true);
		tableSteps.setGridColor(new Color(0x99ccff));
		JTableHeader header2 = tableSteps.getTableHeader();
		header2.setDefaultRenderer(new HeaderRenderer(MainUI.tableHeaderBg,MainUI.tableHeaderFg,MainUI.tablHeadereFont,MainUI.tableGridColor));
	//	tableSteps.addMouseListener(tableStepsMouseListener(dbp));
		scrollPane.setViewportView(tableSteps);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JButton btnAdd = new JButton("Import selected modules");
		menuBar.add(btnAdd);
		for (ActionListener act : btnAdd.getActionListeners()) {
			btnAdd.removeActionListener(act);
		}
		btnAdd.addActionListener(btnActionListener(dbp,tDbp));
		tableSteps.getTableHeader().addMouseListener(tableHeaderMouseListener());

	}
	public void displayName(DBProcess dbp) {
		
		List<String[]> actList = dbp.getModularActions();
		// convert arraylist to array[][]
		String[][] actValue = new String[actList.size()][];
		for (int i = 0; i < actValue.length; i++) {
			String[] row = actList.get(i);
			actValue[i] = row;
		}
		DefaultTableModel tModel = new DefaultTableModel(actValue, new String[] { "MODULE NAME" }) {
			boolean[] columnEditables = new boolean[] { false };

			public boolean isCellEditable(int row, int column) {
				return columnEditables[column];
			}
		};
		tableName.setModel(tModel);
		tableName.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		
		ListSelectionModel cellSelectionModel = tableName.getSelectionModel();
		cellSelectionModel.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				String selectedData = null;
				int selectedRow = tableName.getSelectedRow();
				if (selectedRow >= 0) {
					selectedData = (String) tableName.getValueAt(selectedRow, 0);
					if (!e.getValueIsAdjusting()) {
						displaySteps(selectedData,dbp);
					}
				}
			}
		});
		int rowCount = tableName.getRowCount();
		for(int x =0; x < rowCount;x++) {
			tableName.setRowHeight(x, 24);
		}
		
		
	}
	public void displaySteps(String actId,DBProcess dbp) {
		tableSteps.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		List<String[]> steps = dbp.getModularSteps(actId);

		String[][] stepsValue = new String[steps.size()][];
		for (int i = 0; i < stepsValue.length; i++) {
			String[] row = steps.get(i);
			stepsValue[i] = row;
		}
		DefaultTableModel tModel = new DefaultTableModel(stepsValue, new String[] { actId, "FLOW", "DESCRIPTION",
				"SELECTOR", "ELEMENT", "ACTION", "PARAM NAME", "PARAM VALUE", "CAPTURE" }) {
			boolean[] columnEditables = new boolean[] { false, false, false, false, false, false, false, false, false };

			public boolean isCellEditable(int row, int column) {
				return columnEditables[column];
			}

		};
		CustomRendererEditable cRenderer = new CustomRendererEditable(MainUI.tableCellBg,MainUI.tableCellFg,MainUI.tableHighlightSelBg,MainUI.tableHighlightSelFg);
		CustomRendererReadOnly cRendererRead = new CustomRendererReadOnly(MainUI.tableCellBg,MainUI.tableCellFg,MainUI.tableHighlightSelBg,MainUI.tableHighlightSelFg);
		tableSteps.setModel(tModel);
		// hide first and second column
		tableSteps.getColumnModel().getColumn(0).setMinWidth(0);
		tableSteps.getColumnModel().getColumn(0).setMaxWidth(0);
		tableSteps.getColumnModel().getColumn(0).setPreferredWidth(0);
		tableSteps.getColumnModel().getColumn(1).setCellRenderer(cRendererRead);
		tableSteps.getColumnModel().getColumn(2).setCellRenderer(cRenderer);
		tableSteps.getColumnModel().getColumn(3).setCellRenderer(cRenderer);
		tableSteps.getColumnModel().getColumn(4).setCellRenderer(cRenderer);
		tableSteps.getColumnModel().getColumn(5).setCellRenderer(cRenderer);
		tableSteps.getColumnModel().getColumn(6).setCellRenderer(cRenderer);
		tableSteps.getColumnModel().getColumn(7).setCellRenderer(cRenderer);
		tableSteps.getColumnModel().getColumn(8).setCellRenderer(cRenderer);
		tableSteps.getColumnModel().getColumn(1).setPreferredWidth(stepsColWidth[0]);
		tableSteps.getColumnModel().getColumn(2).setPreferredWidth(stepsColWidth[1]);
		tableSteps.getColumnModel().getColumn(3).setPreferredWidth(stepsColWidth[2]);
		tableSteps.getColumnModel().getColumn(4).setPreferredWidth(stepsColWidth[3]);
		tableSteps.getColumnModel().getColumn(5).setPreferredWidth(stepsColWidth[4]);
		tableSteps.getColumnModel().getColumn(6).setPreferredWidth(stepsColWidth[5]);
		tableSteps.getColumnModel().getColumn(7).setPreferredWidth(stepsColWidth[6]);
		tableSteps.getColumnModel().getColumn(8).setPreferredWidth(stepsColWidth[7]);

		JComboBox<String> cmbYesNo = new JComboBox<String>();
		cmbYesNo.addItem("Yes");
		cmbYesNo.addItem("No");
		tableSteps.getColumnModel().getColumn(8).setCellEditor(new DefaultCellEditor(cmbYesNo));

		tModel.addTableModelListener(new TableModelListener() {
			public void tableChanged(TableModelEvent e) {
				int row = e.getFirstRow();
				int column = e.getColumn();
				TableModel model = (TableModel) e.getSource();
				if (e.getType() == TableModelEvent.UPDATE) {
					// update cell value
					String updatedData = (String) model.getValueAt(row, column);
					String stepId = (String) model.getValueAt(row, 0);
					dbp.updateModActStep(stepId, column, updatedData);
				}
			}
		});
		int rowCount = tableSteps.getRowCount();
		for(int x =0; x < rowCount;x++) {
			tableSteps.setRowHeight(x, 24);
		}
	}
	
	public MouseListener tableHeaderMouseListener() {
		MouseListener mListener = new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				stepsColWidth[0] = tableSteps.getColumnModel().getColumn(1).getWidth();
				stepsColWidth[1] = tableSteps.getColumnModel().getColumn(2).getWidth();
				stepsColWidth[2] = tableSteps.getColumnModel().getColumn(3).getWidth();
				stepsColWidth[3] = tableSteps.getColumnModel().getColumn(4).getWidth();
				stepsColWidth[4] = tableSteps.getColumnModel().getColumn(5).getWidth();
				stepsColWidth[5] = tableSteps.getColumnModel().getColumn(6).getWidth();
				stepsColWidth[6] = tableSteps.getColumnModel().getColumn(7).getWidth();
				stepsColWidth[7] = tableSteps.getColumnModel().getColumn(8).getWidth();
			}
		};
		return mListener;
	}
	public ActionListener btnActionListener(DBProcess dbp,DBProcess tDbp) {
		ActionListener action = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				List<String[]> targets = tDbp.getModularActions();
				int[] row = tableName.getSelectedRows();
				String names = "";
				for (int r : row) {
					String val = (String) tableName.getModel().getValueAt(r, 0); 
					names = names + "'" + val.replaceAll("'", "''") + "',";
				}
				if(row.length < 1) {
					JOptionPane.showMessageDialog(tableSteps.getRootPane(),
							"Please select module to import.","Error",JOptionPane.ERROR_MESSAGE);
				} else {
					String dup = "";
					for (String[] target : targets) {
						for (int r : row) {
							String nme = (String) tableName.getModel().getValueAt(r, 0);
							if(target[0].contentEquals(nme)) {
								dup =dup + nme + "\n";
							}
						}
					}
					if(!dup.contentEquals("")) {
						JTextArea msg = new JTextArea("Module name is already exist : " + dup);
						msg.setLineWrap(true);
						msg.setWrapStyleWord(true);
						JScrollPane scrollPane = new JScrollPane( msg);
						scrollPane.setPreferredSize(new Dimension(600, 200));
						JOptionPane.showMessageDialog(tableSteps.getRootPane(), scrollPane,"Error",JOptionPane.ERROR_MESSAGE);
					} else {
						try {
							tDbp.addModularActions(dbp.getModularStepsByActionId(names));
							JOptionPane.showMessageDialog(tableSteps.getRootPane(), "Import successful.","Success",JOptionPane.INFORMATION_MESSAGE);
						}catch(Exception err) {
							JOptionPane.showMessageDialog(tableSteps.getRootPane(), "Error importing modules: " + err.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
						}
						dispose();	
					}
				}
			}
		};
		return action;
	}
	public boolean isNameExist(String newId,DBProcess dbp) {

		List<String[]> steps = dbp.getModularStepsNotIn(newId, "");
		if (steps.size() > 0) {
			return true;
		}

		return false;
	}
}
