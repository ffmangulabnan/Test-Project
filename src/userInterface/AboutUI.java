package userInterface;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;

@SuppressWarnings("serial")
public class AboutUI extends JDialog {

	private final JPanel contentPanel = new JPanel();

	/**
	 * Create the dialog.
	 */
	public AboutUI() {
		setTitle("About Test Machine");
		setAlwaysOnTop(true);
		setResizable(false);
		setModal(true);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 268, 164);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		{
			JLabel lblVersion = new JLabel("Version:");
			lblVersion.setBounds(10, 32, 46, 14);
			contentPanel.add(lblVersion);
		}
		{
			JLabel lblAuthor = new JLabel("Author:");
			lblAuthor.setBounds(10, 57, 46, 14);
			contentPanel.add(lblAuthor);
		}
		{
			JLabel lblFrancisMangulabnan = new JLabel("Francis Mangulabnan");
			lblFrancisMangulabnan.setBounds(66, 57, 186, 14);
			contentPanel.add(lblFrancisMangulabnan);
		}
		{
			JLabel label = new JLabel("2.0");
			label.setBounds(66, 32, 186, 14);
			contentPanel.add(label);
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
						dispose();
					}
				});
			}

		}
	}
}
