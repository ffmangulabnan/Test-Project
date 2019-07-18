package userInterface;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

import javax.swing.AbstractCellEditor;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableModel;

import database.DBProcess;
import database.PasswordEncryptDecrypt;

import java.awt.GridBagLayout;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import java.awt.GridBagConstraints;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;

@SuppressWarnings("serial")
public class VariablesUI extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private static DBProcess dbProcess = null;
	private static JTable varTable;
	private static int[] colWidth = new int[] { 50, 200 };
	private static String selectedName = "";
	private static boolean edit = false;
	private static JButton btnAdd = new JButton("Add");
	private static String varRunId = "";
	private static boolean adminPass = false;
	private static PasswordEncryptDecrypt encrypt = new PasswordEncryptDecrypt();
	public ActionListener openVariableWindow(DBProcess dbP, String runId, boolean admin) {
		adminPass = admin;
		dbProcess = dbP;

		ActionListener action = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					VariablesUI varUI = new VariablesUI(runId);
					// displayVariablesUI(runId);
					varUI.setLocationRelativeTo(null);
					varUI.setVisible(true);
				} catch (Exception err) {
					JOptionPane.showMessageDialog(null, "Variables not found", "Error", JOptionPane.ERROR_MESSAGE);
					try {
						dbProcess.createVariableTable();
						JOptionPane.showMessageDialog(null, "Variables table created", "Variables",
								JOptionPane.INFORMATION_MESSAGE);
					} catch (Exception er) {
						JOptionPane.showMessageDialog(null, er.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
					}
				}

			}
		};
		return action;
	}

	public void displayVariableWindow(DBProcess dbP, String runId) {

		dbProcess = dbP;
		try {
			VariablesUI varUI = new VariablesUI(runId);
			// displayVariablesUI(runId);
			varUI.setLocationRelativeTo(null);
			varUI.setVisible(true);
		} catch (Exception err) {
			JOptionPane.showMessageDialog(null, "Variables not found", "Error", JOptionPane.ERROR_MESSAGE);
			try {
				dbProcess.createVariableTable();
				JOptionPane.showMessageDialog(null, "Variables table created", "Variables",
						JOptionPane.INFORMATION_MESSAGE);
			} catch (Exception er) {
				JOptionPane.showMessageDialog(null, er.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		}

	}

	/**
	 * Create the
	 * 
	 * @return
	 */
	public VariablesUI(String runId) {
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setModal(true);
		setTitle("Test Data");
		setBounds(100, 100, 530, 403);
		varRunId = runId;
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[] { 0, 0 };
		gbl_contentPanel.rowHeights = new int[] { 0, 0 };
		gbl_contentPanel.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_contentPanel.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		contentPanel.setLayout(gbl_contentPanel);

		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 0;
		contentPanel.add(scrollPane, gbc_scrollPane);

		varTable = new JTable();
		varTable.setShowGrid(true);
		varTable.setGridColor(new Color(0x99ccff));
		JTableHeader header = varTable.getTableHeader();
		
		scrollPane.setViewportView(varTable);
		header.setDefaultRenderer(new HeaderRenderer(MainUI.tableHeaderBg,MainUI.tableHeaderFg,MainUI.tablHeadereFont,MainUI.tableGridColor));
		
		varTable.getTableHeader().addMouseListener(tableHeaderMouseListener());
		varTable.addMouseListener(tableMouseListener());
		if (dbProcess != null) {
			displayVar(runId);
		}
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		menuBar.add(btnAdd);
		for (ActionListener act : btnAdd.getActionListeners()) {
			btnAdd.removeActionListener(act);
		}
		btnAdd.addActionListener(btnAddActionListener());

	}

	public static void displayVar(String rId) {
		List<String[]> actList;
		if (rId.contentEquals("")) {
			actList = dbProcess.getVariables(rId);
			edit = true;
			btnAdd.setVisible(true);
		} else {
			actList = dbProcess.getRunVariables(rId);
			edit = false;
			btnAdd.setVisible(false);
		}
		// convert arraylist to array[][]
		String[][] actValue = new String[actList.size()][];
		for (int i = 0; i < actValue.length; i++) {
			String[] row = actList.get(i);
			actValue[i] = row;
			if (!rId.contentEquals("") && actValue[i][3] != null && actValue[i][3].contentEquals("1")) {
				String val = encrypt.decryptPassword(actValue[i][2]);
				actValue[i][2] = val;
			}
		}
		DefaultTableModel tModel = new DefaultTableModel(actValue, new String[] { "ID", "NAME", "VALUE","ENCRYPT"}) {
			boolean[] columnEditables = new boolean[] { false, edit, true, false };

			public boolean isCellEditable(int row, int column) {
				return columnEditables[column];
			}
		};

		varTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		ListSelectionModel cellSelectionModel = varTable.getSelectionModel();
		cellSelectionModel.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				String selectedData = null;
				int selectedRow = varTable.getSelectedRow();
				if (selectedRow > 0) {
					selectedData = (String) varTable.getValueAt(selectedRow, 1);
					if (!e.getValueIsAdjusting()) {
						selectedName = selectedData;
					}
				}
			}
		});

		tModel.addTableModelListener(new TableModelListener() {
			public void tableChanged(TableModelEvent e) {
				int row = e.getFirstRow();
				int column = e.getColumn();
				TableModel model = (TableModel) e.getSource();
				if (e.getType() == TableModelEvent.UPDATE) {
					String updatedData = (String) model.getValueAt(row, column);
					String id = (String) model.getValueAt(row, 0);
					// update cell value
					if (column == 1) {
						if (updatedData.contentEquals("") || updatedData.length() < 1) {
							model.setValueAt(selectedName, row, 1);
							JOptionPane.showMessageDialog(varTable.getRootPane(), "Variable name empty!", "Error",
									JOptionPane.ERROR_MESSAGE);
						} else {
							if (!selectedName.contentEquals(updatedData)) {
								if (isAddNameExist(updatedData, id)) {
									model.setValueAt(selectedName, row, 1);
									JOptionPane.showMessageDialog(varTable.getRootPane(),
											"Variable name " + updatedData + " is already exist!", "Error",
											JOptionPane.ERROR_MESSAGE);
								} else {
									dbProcess.updateVariableName(updatedData, id);
								}
							}
						}
					} else if (column == 2) {
						if (rId.contentEquals("")) {
							dbProcess.updateVariableValue(updatedData, id);
						} else {
							String en = (String) model.getValueAt(row, 3);
							if(en.contentEquals("1")) {
								dbProcess.updateRunVariableValue(encrypt.createEncryptedPassword(updatedData), id);
							} else {
								dbProcess.updateRunVariableValue(updatedData, id);
							}
						}

					}

				}
			}
		});
		varTable.setModel(tModel);
		varTable.getColumnModel().getColumn(0).setMinWidth(0);
		varTable.getColumnModel().getColumn(0).setMaxWidth(0);
		varTable.getColumnModel().getColumn(0).setPreferredWidth(0);
		varTable.getColumnModel().getColumn(1).setPreferredWidth(colWidth[0]);
		varTable.getColumnModel().getColumn(2).setPreferredWidth(colWidth[1]);
		varTable.getColumnModel().getColumn(3).setMinWidth(0);
		varTable.getColumnModel().getColumn(3).setMaxWidth(0);
		varTable.getColumnModel().getColumn(3).setPreferredWidth(0);
		if (!rId.contentEquals("")) {
			varTable.getColumnModel().getColumn(2).setCellRenderer(new masked());
			varTable.getColumnModel().getColumn(2).setCellEditor(new CustomTableCellEditor());	
		}
		setTableRowHeight();
	}

	public static ActionListener btnAddActionListener() {
		ActionListener action = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean inputAccepted = false;
				while (!inputAccepted) {
					String name = JOptionPane.showInputDialog(varTable.getRootPane(), "Variable name: ", "Add Variable",
							JOptionPane.PLAIN_MESSAGE);
					if (name == null) {
						inputAccepted = true;
					} else if (name.equalsIgnoreCase("")) {
						JOptionPane.showMessageDialog(varTable.getRootPane(), "Please enter Module name", "Error",
								JOptionPane.ERROR_MESSAGE);
					} else if (isAddNameExist(name, "")) {
						JOptionPane.showMessageDialog(varTable.getRootPane(), "Variable name is already exist!",
								"Error", JOptionPane.ERROR_MESSAGE);
					} else {
						inputAccepted = true;
						dbProcess.addVariableName(name);
						displayVar("");
						int r = getTableRowIndex(varTable, name);
						varTable.getSelectionModel().setSelectionInterval(r, r);
					}
				}
			}
		};
		return action;
	}

	public static int getTableRowIndex(JTable table, String value) {
		for (int i = 0; i < table.getModel().getRowCount(); i++) {
			String name = (String) varTable.getModel().getValueAt(i, 1);

			if (name.contentEquals(value)) {
				return i;
			}
		}
		return 0;
	}

	public static boolean isAddNameExist(String name, String notInId) {

		List<String[]> var = dbProcess.getVariableNotIn(name, notInId);
		if (var.size() > 0) {
			return true;
		}

		return false;
	}

	public static MouseListener tableHeaderMouseListener() {
		MouseListener mListener = new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				colWidth[0] = varTable.getColumnModel().getColumn(1).getWidth();
				colWidth[1] = varTable.getColumnModel().getColumn(2).getWidth();
			}

		};
		return mListener;
	}

	public static void popupMenuVar(MouseEvent mEvent, int rowindex) {
		if (mEvent.isPopupTrigger() && mEvent.getComponent() instanceof JTable && adminPass) {
			JPopupMenu popup = new JPopupMenu();
			if(!varRunId.contentEquals("")) {
				JMenuItem itemEncript = new JMenuItem("Encrypt");
				popup.add(itemEncript);
				itemEncript.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						int[] row = varTable.getSelectedRows();
						String varId = "";
						for (int r : row) {
							varId = varId + (String) varTable.getModel().getValueAt(r, 0) + ",";
						}
						dbProcess.encryptRunVariables(varId.substring(0, varId.length() - 1));
						displayVar(varRunId);
					}
				});
			}
			JMenuItem item = new JMenuItem("Delete");
			popup.add(item);
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int opt = JOptionPane.showConfirmDialog(varTable.getRootPane(),
							"Are you sure you want to delete selected data?", "Confirm Delete",
							JOptionPane.YES_NO_OPTION);
					if (opt == JOptionPane.YES_OPTION) {
						int[] row = varTable.getSelectedRows();
						String varId = "";
						for (int r : row) {
							varId = varId + (String) varTable.getModel().getValueAt(r, 0) + ",";
						}
						if (edit) {
							dbProcess.deleteVariables(varId.substring(0, varId.length() - 1));
						} else {
							dbProcess.deleteRunVariables(varId.substring(0, varId.length() - 1));
						}
						displayVar(varRunId);
					}
				}
			});
			popup.show(mEvent.getComponent(), mEvent.getX(), mEvent.getY());
		}
	}

	public static MouseListener tableMouseListener() {
		MouseListener mListener = new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {

				int rowindex = varTable.getSelectedRow();
				if (rowindex < 0)
					return;
				popupMenuVar(e, rowindex);
			}

		};
		return mListener;
	}
	public static void setTableRowHeight() {
		int rowCount = varTable.getRowCount();
		for(int x =0; x < rowCount;x++) {
			varTable.setRowHeight(x, 24);
		}
	}

}
class masked extends DefaultTableCellRenderer {
	 private static final long serialVersionUID = 1L;
     public Component getTableCellRendererComponent(JTable arg0, Object arg1, boolean arg2, boolean arg3, int arg4, int arg5) {
    	 int length =0;
         if (arg1 instanceof String) {
             length =  ((String) arg1).length();
         } else if (arg1 instanceof char[]) {
             length = ((char[])arg1).length;
         }
 		String cellVal = arg0.getModel().getValueAt(arg4, 3).toString();
    	 if(cellVal.contentEquals("1")) {
	         setText(mask(length));
    	 } else {
    		 setText(arg1.toString());
    	 }
    	
         return this;
     }

     private String mask(int length) {
    	    StringBuilder sb = new StringBuilder(length);
    	    for (int i = 0; i < length; i++) {
    	        sb.append('\u25CF');
    	    }
    	    return new String(sb);
    	}

}
@SuppressWarnings("serial")
class CustomTableCellEditor extends AbstractCellEditor implements TableCellEditor {
    private TableCellEditor editor;
    @Override
    public Object getCellEditorValue() {
        if (editor != null) {
            return editor.getCellEditorValue();
        }
        return null;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
    	String cellVal = table.getModel().getValueAt(row, 3).toString();
    	if(cellVal.contentEquals("1")) {
            editor = new DefaultCellEditor(new JPasswordField());
        } else {
        	 editor = new DefaultCellEditor(new JTextField());
        }

        return editor.getTableCellEditorComponent(table, value, isSelected, row, column);
    }
}

