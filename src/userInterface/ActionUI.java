package userInterface;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import database.TestMachineConfig;
import javax.swing.JScrollPane;
import javax.swing.JTable;

@SuppressWarnings("serial")
public class ActionUI extends JDialog {

	private final JPanel contentPanel = new JPanel();
	static TestMachineConfig dbConfig = new TestMachineConfig();
	private JTable table;
	/**
	 * Create the dialog.
	 */
	public ActionUI() {

		setTitle("Actions");
		setResizable(false);
		setModal(true);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 750, 440);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);

		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 11, 724, 350);
		contentPanel.add(scrollPane);

		table = new JTable() {
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
				Component c = super.prepareRenderer(renderer, row, column);
				if (c instanceof JComponent) {
					JComponent jc = (JComponent) c;
					if (column == 2) {
						jc.setToolTipText(getValueAt(row, column).toString());
					} else {
						jc.setToolTipText(null);
					}
				}
				return c;
			}

		};
		scrollPane.setViewportView(table);
		table.setShowGrid(true);
		table.setGridColor(new Color(0x99ccff));
		JTableHeader header = table.getTableHeader();
		header.setDefaultRenderer(new HeaderRenderer(MainUI.tableHeaderBg,MainUI.tableHeaderFg,MainUI.tablHeadereFont,MainUI.tableGridColor));

		List<String[]> actList = dbConfig.getAllActions();
		// convert arraylist to array[][]
		String[][] actValue = new String[actList.size()][];
		for (int i = 0; i < actValue.length; i++) {
			String[] row = actList.get(i);
			actValue[i] = row;
		}

		DefaultTableModel tModel = new DefaultTableModel(actValue,
				new String[] { "ID", "NAME", "DESCRIPTION", "STATUS", "REPORT" }) {
			boolean[] columnEditables = new boolean[] { false, false, false, true, true };

			public boolean isCellEditable(int row, int column) {
				return columnEditables[column];
			}
		};
		table.setModel(tModel);
		table.getColumnModel().getColumn(0).setMinWidth(0);
		table.getColumnModel().getColumn(0).setMaxWidth(0);
		table.getColumnModel().getColumn(0).setPreferredWidth(0);
		//table.getColumnModel().getColumn(1).setMinWidth(120);
		//table.getColumnModel().getColumn(1).setMaxWidth(120);
		table.getColumnModel().getColumn(1).setPreferredWidth(120);
		//table.getColumnModel().getColumn(3).setMinWidth(80);
		//table.getColumnModel().getColumn(3).setMaxWidth(80);
		table.getColumnModel().getColumn(3).setPreferredWidth(80);
		JComboBox<String> cmbActiveDisabled = new JComboBox<String>();
		cmbActiveDisabled.addItem("Active");
		cmbActiveDisabled.addItem("Disabled");
		table.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(cmbActiveDisabled));
		JComboBox<String> cmbYesNo = new JComboBox<String>();
		cmbYesNo.addItem("Yes");
		cmbYesNo.addItem("No");
		table.getColumnModel().getColumn(4).setCellEditor(new DefaultCellEditor(cmbYesNo));
		table.getColumnModel().getColumn(4).setMinWidth(60);
		table.getColumnModel().getColumn(4).setMaxWidth(60);
		table.getColumnModel().getColumn(4).setPreferredWidth(60);

		tModel.addTableModelListener(new TableModelListener() {
			public void tableChanged(TableModelEvent e) {
				int row = e.getFirstRow();
				int column = e.getColumn();
				TableModel model = (TableModel) e.getSource();
				if (e.getType() == TableModelEvent.UPDATE) {
					// update cell value
					String updatedData = (String) model.getValueAt(row, column);
					String actId = (String) model.getValueAt(row, 0);
					dbConfig.updateAction(actId, updatedData, column);
				}
			}
		});

		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("Close");
				okButton.setActionCommand("Close");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dispose();
					}
				});
			}
		}
		int rowCount = table.getRowCount();
		for(int x =0; x < rowCount;x++) {
			table.setRowHeight(x, 24);
		}
	}
}
