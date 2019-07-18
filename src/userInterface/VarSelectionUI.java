package userInterface;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import database.DBProcess;

import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JScrollPane;
import java.awt.GridBagConstraints;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

@SuppressWarnings("serial")
public class VarSelectionUI extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private static JTable table;
	private static DBProcess dbProcess = null;
	/**
	 * Launch the application.
	 */
	public void openVarSelectionUI(DBProcess dbP, long runId) {
		try {
			dbProcess = dbP;
			VarSelectionUI dialog = new VarSelectionUI(runId);
			dialog.setLocationRelativeTo(null);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public VarSelectionUI(long runId) {
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setModal(true);
		setTitle("Select Test Data");
		setBounds(100, 100, 530, 403);

		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[] { 0, 0 };
		gbl_contentPanel.rowHeights = new int[] { 0, 0 };
		gbl_contentPanel.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_contentPanel.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		contentPanel.setLayout(gbl_contentPanel);
		{
			JScrollPane scrollPane = new JScrollPane();
			GridBagConstraints gbc_scrollPane = new GridBagConstraints();
			gbc_scrollPane.fill = GridBagConstraints.BOTH;
			gbc_scrollPane.gridx = 0;
			gbc_scrollPane.gridy = 0;
			contentPanel.add(scrollPane, gbc_scrollPane);
			{
				table = new JTable();
				table.setShowGrid(true);
				table.setGridColor(new Color(0x99ccff));
				JTableHeader header = table.getTableHeader();
				
				header.setDefaultRenderer(new HeaderRenderer(MainUI.tableHeaderBg,MainUI.tableHeaderFg,MainUI.tablHeadereFont,MainUI.tableGridColor));
				scrollPane.setViewportView(table);
				if (dbProcess != null) {
					displayVar(runId);
				}
			}
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						int[] row = table.getSelectedRows();
						String id = "";
						for (int r : row) {
							id = id + (String) table.getModel().getValueAt(r, 0) + ",";
						}
						if (row.length > 0) {
							dbProcess.insertRunVarSelected(id.substring(0, id.length() - 1), runId);
						}
						dispose();
					}
				});
			}
		}
	}

	public static void displayVar(long rId) {
		List<String[]> actList = dbProcess.getVariables(String.valueOf(rId));

		// convert arraylist to array[][]
		String[][] actValue = new String[actList.size()][];
		for (int i = 0; i < actValue.length; i++) {
			String[] row = actList.get(i);
			actValue[i] = row;
		}
		DefaultTableModel tModel = new DefaultTableModel(actValue, new String[] { "ID", "NAME", "VALUE" }) {
			boolean[] columnEditables = new boolean[] { false, false, false };

			public boolean isCellEditable(int row, int column) {
				return columnEditables[column];
			}
		};

		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		table.setModel(tModel);
		table.getColumnModel().getColumn(0).setMinWidth(0);
		table.getColumnModel().getColumn(0).setMaxWidth(0);
		table.getColumnModel().getColumn(0).setPreferredWidth(0);
		
		int rowCount = table.getRowCount();
		for(int x =0; x < rowCount;x++) {
			table.setRowHeight(x, 24);
		}
	}
}
