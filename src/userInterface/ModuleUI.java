package userInterface;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import java.awt.GridBagLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;
import database.DBProcess;
import database.TestMachineConfig;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JButton;

@SuppressWarnings("serial")
public class ModuleUI extends JDialog {
	private JTable tableName;
	private JTable tableSteps;
	private int[] stepsColWidth = new int[] { 50, 200, 60, 100, 100, 100, 100, 50 };
	private TestMachineConfig dbConfig = new TestMachineConfig();
	private String selectedName = "";
	private String copySteps = null;
	/**
	 * Create the dialog.
	 */
	public ModuleUI(DBProcess dbp) {

		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setModal(true);
		setTitle("Modules");
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
		tableName.addMouseListener(tableNameMouseListener(dbp));
		if (dbp != null) {
			displayName(dbp);
		}
		JScrollPane scrollPane = new JScrollPane();
		splitPane.setRightComponent(scrollPane);

		tableSteps = new JTable();
		tableSteps.addMouseListener(tableStepsMouseListener(dbp));
		scrollPane.setViewportView(tableSteps);
		tableSteps.setShowGrid(true);
		tableSteps.setGridColor(new Color(0x99ccff));
		JTableHeader header2 = tableSteps.getTableHeader();
		header2.setDefaultRenderer(new HeaderRenderer(MainUI.tableHeaderBg,MainUI.tableHeaderFg,MainUI.tablHeadereFont,MainUI.tableGridColor));
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JButton btnAdd = new JButton("Add");
		menuBar.add(btnAdd);
		for (ActionListener act : btnAdd.getActionListeners()) {
			btnAdd.removeActionListener(act);
		}
		btnAdd.addActionListener(btnAddActionListener(dbp));
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
			boolean[] columnEditables = new boolean[] { true };

			public boolean isCellEditable(int row, int column) {
				return columnEditables[column];
			}
		};
		tableName.setModel(tModel);
		tableName.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		ListSelectionModel cellSelectionModel = tableName.getSelectionModel();
		cellSelectionModel.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				String selectedData = null;
				int selectedRow = tableName.getSelectedRow();
				if (selectedRow >= 0) {
					selectedData = (String) tableName.getValueAt(selectedRow, 0);
					if (!e.getValueIsAdjusting()) {
						selectedName = selectedData;
						displaySteps(selectedData,dbp);
					}
				}
			}
		});
		tModel.addTableModelListener(new TableModelListener() {
			public void tableChanged(TableModelEvent e) {
				int row = e.getFirstRow();
				TableModel model = (TableModel) e.getSource();
				if (e.getType() == TableModelEvent.UPDATE) {
					// update cell value
					String stepId = (String) model.getValueAt(row, 0);
					if (stepId.contentEquals("") || stepId.length() < 1) {
						model.setValueAt(selectedName, row, 0);
						JOptionPane.showMessageDialog(tableSteps.getRootPane(), "Module name empty!", "Error",
								JOptionPane.ERROR_MESSAGE);
					} else {
						if (!selectedName.contentEquals(stepId)) {
							// dbProcess.updateModActName(stepId, column, updatedData);
							if (isEditNameExist(stepId,dbp)) {
								model.setValueAt(selectedName, row, 0);
								JOptionPane.showMessageDialog(tableSteps.getRootPane(),
										"Module name " + stepId + " is already exist!", "Error",
										JOptionPane.ERROR_MESSAGE);
							} else {
								dbp.updateModularActionName(selectedName, stepId);
							}
						}
					}

				}
			}
		});
		int rowCount = tableName.getRowCount();
		for(int x =0; x < rowCount;x++) {
			tableName.setRowHeight(x, 24);
		}
	}

	public boolean isEditNameExist(String newId,DBProcess dbp) {
		String stepId = "";
		for (int i = 0; i < tableSteps.getModel().getRowCount(); i++) {
			stepId = stepId + (String) tableSteps.getModel().getValueAt(i, 0) + ",";
		}
		if (stepId.length() > 0) {
			List<String[]> steps = dbp.getModularStepsNotIn(newId, stepId.substring(0, stepId.length() - 1));
			if (steps.size() > 0) {
				return true;
			}
		}
		return false;
	}

	public boolean isAddNameExist(String newId,DBProcess dbp) {

		List<String[]> steps = dbp.getModularStepsNotIn(newId, "");
		if (steps.size() > 0) {
			return true;
		}

		return false;
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
			boolean[] columnEditables = new boolean[] { false, false, true, true, true, true, true, true, true };

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

		tableSteps.getColumnModel().getColumn(3)
				.setCellEditor(new DefaultCellEditor(createDropdownList(dbConfig.getSelector())));
		tableSteps.getColumnModel().getColumn(5)
				.setCellEditor(new DefaultCellEditor(createDropdownList(dbConfig.getActions())));

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

	public JComboBox<String> createDropdownList(List<String[]> arrayList) {

		JComboBox<String> cmbBox = new JComboBox<String>();
		for (String[] array : arrayList) {
			cmbBox.addItem(array[1]);
		}
		return cmbBox;
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

	public ActionListener btnAddActionListener(DBProcess dbp) {
		ActionListener action = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean inputAccepted = false;
				while (!inputAccepted) {
					String modName = JOptionPane.showInputDialog(tableSteps.getRootPane(), "Module name: ",
							"Add Module", JOptionPane.PLAIN_MESSAGE);
					if (modName == null) {
						inputAccepted = true;
					} else if (modName.equalsIgnoreCase("")) {
						JOptionPane.showMessageDialog(tableSteps.getRootPane(), "Please enter Module name", "Error",
								JOptionPane.ERROR_MESSAGE);
					} else if (isAddNameExist(modName,dbp)) {
						JOptionPane.showMessageDialog(tableSteps.getRootPane(), "Module Name is already exist!",
								"Error", JOptionPane.ERROR_MESSAGE);
					} else {
						inputAccepted = true;
						dbp.addModularActionName(modName);
						displayName(dbp);
						tableName.getSelectionModel().setSelectionInterval(0, getTableRowIndex(tableName, modName));
					}
				}
			}
		};
		return action;
	}

	public int getTableRowIndex(JTable table, String value) {
		for (int i = 0; i < table.getModel().getRowCount(); i++) {
			String name = (String) tableName.getModel().getValueAt(i, 0);
			if (name.contentEquals(value)) {
				return i;
			}
		}
		return 0;
	}

	public void popupMenuNameAction(MouseEvent mEvent, int rowindex,DBProcess dbp) {
		DefaultTableModel tModel = (DefaultTableModel) tableName.getModel();
		if (mEvent.isPopupTrigger() && mEvent.getComponent() instanceof JTable) {
			JPopupMenu popup = new JPopupMenu();
			JMenuItem item = new JMenuItem("Delete");
			popup.add(item);
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					String name = (String) tModel.getValueAt(rowindex, 0);
					int opt = JOptionPane.showConfirmDialog(tableName.getRootPane(),
							"Are you sure you want to delete " + name + "?", "Confirm Delete",
							JOptionPane.YES_NO_OPTION);
					if (opt == JOptionPane.YES_OPTION) {
						dbp.deleteModularActionName(name);
						displayName(dbp);
						displaySteps("",dbp);
					}
				}
			});
			popup.show(mEvent.getComponent(), mEvent.getX(), mEvent.getY());
		}
	}

	public MouseListener tableNameMouseListener(DBProcess dbp) {
		MouseListener mListener = new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {

				int rowindex = tableName.getSelectedRow();
				if (rowindex < 0)
					return;
				popupMenuNameAction(e, rowindex,dbp);
			}

		};
		return mListener;
	}

	public void popupMenuStepsAction(MouseEvent mEvent, int rowindex,DBProcess dbp) {
		if (mEvent.isPopupTrigger() && mEvent.getComponent() instanceof JTable) {
			JPopupMenu popup = new JPopupMenu();
			JMenuItem addItem = new JMenuItem("Add");
			popup.add(addItem);
			addItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					dbp.addModularActionStep(selectedName);
					displaySteps(selectedName,dbp);
				}
			});

			JMenuItem insertItem = new JMenuItem("Insert");
			popup.add(insertItem);
			insertItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int row = tableSteps.getSelectedRow();
					dbp.insertModularActionStep((String) tableSteps.getModel().getValueAt(row, 0), selectedName);
					displaySteps(selectedName,dbp);
				}
			});

			JMenuItem copyItem = new JMenuItem("Copy");
			popup.add(copyItem);
			copyItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int[] row = tableSteps.getSelectedRows();
					String stepId = "";
					for (int r : row) {
						stepId = stepId + (String) tableSteps.getModel().getValueAt(r, 0) + ",";
					}
					copySteps = stepId.substring(0, stepId.length() - 1);
				}
			});

			if (copySteps != null) {
				JMenuItem pasteItem = new JMenuItem("Paste");
				popup.add(pasteItem);
				pasteItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						int row = tableSteps.getSelectedRow();
						dbp.pasteModularActionStep(copySteps, selectedName, row);
						copySteps = null;
						displaySteps(selectedName,dbp);
					}
				});
			}

			JMenuItem item = new JMenuItem("Delete");
			popup.add(item);
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int opt = JOptionPane.showConfirmDialog(tableSteps.getRootPane(),
							"Are you sure you want to delete?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
					if (opt == JOptionPane.YES_OPTION) {
						int[] row = tableSteps.getSelectedRows();
						String stepId = "";
						for (int r : row) {
							stepId = stepId + (String) tableSteps.getModel().getValueAt(r, 0) + ",";
						}
						dbp.deleteModularActionSteps(stepId.substring(0, stepId.length() - 1), selectedName);
						displaySteps(selectedName,dbp);
						if (tableSteps.getRowCount() < 1) {
							displayName(dbp);
						}
					}
				}
			});
			popup.show(mEvent.getComponent(), mEvent.getX(), mEvent.getY());
		}
	}

	public MouseListener tableStepsMouseListener(DBProcess dbp) {
		MouseListener mListener = new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {

				int rowindex = tableSteps.getSelectedRow();
				if (rowindex < 0)
					return;
				popupMenuStepsAction(e, rowindex,dbp);
			}

		};
		return mListener;
	}
}
