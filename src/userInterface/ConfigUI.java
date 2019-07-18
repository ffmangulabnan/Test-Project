package userInterface;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import database.PasswordEncryptDecrypt;
import database.TestMachineConfig;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.JPasswordField;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.border.TitledBorder;
import javax.swing.border.EtchedBorder;
import java.awt.Color;
import javax.swing.JSpinner;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

@SuppressWarnings("serial")
public class ConfigUI extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField txtEmail;
	private JPasswordField passwordEmail;
	private JTextField txtEmailRecipient;
	private JTextField txtEmailHost;
	private JTextField txtEmailPort;

	static TestMachineConfig dbConfig = new TestMachineConfig();
	private JTextField textUserAgent;
	private PasswordEncryptDecrypt encrypt = new PasswordEncryptDecrypt();
	private JTextField txtBackupLoc;
	/**
	 * Create the dialog.
	 */
	public ConfigUI() {
		setTitle("Configuration");
		setResizable(false);
		setModal(true);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 506, 572);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[]{5, 450, 5};
		gbl_contentPanel.rowHeights = new int[]{5, 161, 56, 0, 129, 0, 45, 0, 0};
		gbl_contentPanel.columnWeights = new double[]{0.0, 0.0, 0.0};
		gbl_contentPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		contentPanel.setLayout(gbl_contentPanel);
		
		SpinnerModel smodel = new SpinnerNumberModel(0, 0, 999, 1);
		SpinnerModel smodel2 = new SpinnerNumberModel(0, 0, 999, 1);
		SpinnerModel smodel3 = new SpinnerNumberModel(0, 0, 999, 1);
		
		JPanel wdPanel = new JPanel();
		GridBagConstraints gbc_wdPanel = new GridBagConstraints();
		gbc_wdPanel.fill = GridBagConstraints.BOTH;
		gbc_wdPanel.insets = new Insets(0, 0, 5, 5);
		gbc_wdPanel.gridx = 1;
		gbc_wdPanel.gridy = 1;
		contentPanel.add(wdPanel, gbc_wdPanel);
		GridBagLayout gbl_wdPanel = new GridBagLayout();
		gbl_wdPanel.columnWidths = new int[]{116, 59, 154, 102, 0};
		gbl_wdPanel.rowHeights = new int[]{29, 28, 28, 22, 4, 0};
		gbl_wdPanel.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_wdPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		wdPanel.setLayout(gbl_wdPanel);
		JLabel lblPLoadTO = new JLabel("Page Load Timeout:");
		GridBagConstraints gbc_lblPLoadTO = new GridBagConstraints();
		gbc_lblPLoadTO.anchor = GridBagConstraints.WEST;
		gbc_lblPLoadTO.insets = new Insets(0, 0, 5, 5);
		gbc_lblPLoadTO.gridwidth = 2;
		gbc_lblPLoadTO.gridx = 0;
		gbc_lblPLoadTO.gridy = 0;
		wdPanel.add(lblPLoadTO, gbc_lblPLoadTO);
		JSpinner spinnerTO = new JSpinner(smodel);
		GridBagConstraints gbc_spinnerTO = new GridBagConstraints();
		gbc_spinnerTO.fill = GridBagConstraints.BOTH;
		gbc_spinnerTO.insets = new Insets(0, 0, 5, 5);
		gbc_spinnerTO.gridx = 1;
		gbc_spinnerTO.gridy = 0;
		wdPanel.add(spinnerTO, gbc_spinnerTO);
		spinnerTO.setValue(Integer.parseInt(dbConfig.getConfigValue("PAGELOAD_TIMEOUT")));
				
		JCheckBox chkHighLight = new JCheckBox("Highlight driver action");
		GridBagConstraints gbc_chkHighLight = new GridBagConstraints();
		gbc_chkHighLight.fill = GridBagConstraints.HORIZONTAL;
		gbc_chkHighLight.insets = new Insets(0, 0, 5, 5);
		gbc_chkHighLight.gridx = 2;
		gbc_chkHighLight.gridy = 0;
		wdPanel.add(chkHighLight, gbc_chkHighLight);
						
		JComboBox<String> cmbHLColor = new JComboBox<String>();
		cmbHLColor.setModel(new DefaultComboBoxModel<String>(new String[] { "Red", "Green", "Blue", "Yellow" }));
		cmbHLColor.setSelectedItem(dbConfig.getConfigValue("HIGHLIGHT_COLOR"));
		GridBagConstraints gbc_cmbHLColor = new GridBagConstraints();
		gbc_cmbHLColor.fill = GridBagConstraints.BOTH;
		gbc_cmbHLColor.insets = new Insets(0, 0, 5, 0);
		gbc_cmbHLColor.gridx = 3;
		gbc_cmbHLColor.gridy = 0;
		wdPanel.add(cmbHLColor, gbc_cmbHLColor);
								
		JLabel lblImpWaitTO = new JLabel("Implicitly Wait:");
		GridBagConstraints gbc_lblImpWaitTO = new GridBagConstraints();
		gbc_lblImpWaitTO.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblImpWaitTO.insets = new Insets(0, 0, 5, 5);
		gbc_lblImpWaitTO.gridx = 0;
		gbc_lblImpWaitTO.gridy = 1;
		wdPanel.add(lblImpWaitTO, gbc_lblImpWaitTO);
		JSpinner spinnerIW = new JSpinner(smodel2);
		GridBagConstraints gbc_spinnerIW = new GridBagConstraints();
		gbc_spinnerIW.fill = GridBagConstraints.BOTH;
		gbc_spinnerIW.insets = new Insets(0, 0, 5, 5);
		gbc_spinnerIW.gridx = 1;
		gbc_spinnerIW.gridy = 1;
		wdPanel.add(spinnerIW, gbc_spinnerIW);
		spinnerIW.setValue(Integer.parseInt(dbConfig.getConfigValue("IMPLICITLY_WAIT")));
										
		JCheckBox chckbxGenerateXlsReport = new JCheckBox("Generate xls report after execution.");
		GridBagConstraints gbc_chckbxGenerateXlsReport = new GridBagConstraints();
		gbc_chckbxGenerateXlsReport.anchor = GridBagConstraints.SOUTH;
		gbc_chckbxGenerateXlsReport.fill = GridBagConstraints.HORIZONTAL;
		gbc_chckbxGenerateXlsReport.insets = new Insets(0, 0, 5, 0);
		gbc_chckbxGenerateXlsReport.gridwidth = 2;
		gbc_chckbxGenerateXlsReport.gridx = 2;
		gbc_chckbxGenerateXlsReport.gridy = 1;
		wdPanel.add(chckbxGenerateXlsReport, gbc_chckbxGenerateXlsReport);
												
		JLabel lblActionDelay = new JLabel("Action Delay: ");
		GridBagConstraints gbc_lblActionDelay = new GridBagConstraints();
		gbc_lblActionDelay.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblActionDelay.insets = new Insets(0, 0, 5, 5);
		gbc_lblActionDelay.gridx = 0;
		gbc_lblActionDelay.gridy = 2;
		wdPanel.add(lblActionDelay, gbc_lblActionDelay);
		JSpinner spinnerAD = new JSpinner(smodel3);
		GridBagConstraints gbc_spinnerAD = new GridBagConstraints();
		gbc_spinnerAD.fill = GridBagConstraints.BOTH;
		gbc_spinnerAD.insets = new Insets(0, 0, 5, 5);
		gbc_spinnerAD.gridx = 1;
		gbc_spinnerAD.gridy = 2;
		wdPanel.add(spinnerAD, gbc_spinnerAD);
		spinnerAD.setValue(Integer.parseInt(dbConfig.getConfigValue("ACTION_DELAY")));
		wdPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Web Driver"));
		
		JCheckBox chckbxDisplayLogConsole = new JCheckBox("Log console");
		GridBagConstraints gbc_chckbxDisplayLogConsole = new GridBagConstraints();
		gbc_chckbxDisplayLogConsole.anchor = GridBagConstraints.SOUTH;
		gbc_chckbxDisplayLogConsole.fill = GridBagConstraints.HORIZONTAL;
		gbc_chckbxDisplayLogConsole.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxDisplayLogConsole.gridx = 2;
		gbc_chckbxDisplayLogConsole.gridy = 2;
		wdPanel.add(chckbxDisplayLogConsole, gbc_chckbxDisplayLogConsole);
																
		JLabel lblUserAgentString = new JLabel("User Agent:");
		GridBagConstraints gbc_lblUserAgentString = new GridBagConstraints();
		gbc_lblUserAgentString.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblUserAgentString.insets = new Insets(0, 0, 5, 5);
		gbc_lblUserAgentString.gridx = 0;
		gbc_lblUserAgentString.gridy = 3;
		wdPanel.add(lblUserAgentString, gbc_lblUserAgentString);
		JLabel labelAString = new JLabel("User Agent String:");	
		JComboBox<String> comboBoxUserAgent = new JComboBox<String>();
		comboBoxUserAgent.addItem("(Default)");
		comboBoxUserAgent.addItem("Custom");
		comboBoxUserAgent.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                String agent = (String)comboBoxUserAgent.getSelectedItem();
                if(agent.contentEquals("Custom")) {
                	textUserAgent.setVisible(true);
                	labelAString.setVisible(true);
                } else {
                	textUserAgent.setVisible(false);
                	labelAString.setVisible(false);
                }
            }
        }   );
		GridBagConstraints gbc_comboBoxUserAgent = new GridBagConstraints();
		gbc_comboBoxUserAgent.fill = GridBagConstraints.BOTH;
		gbc_comboBoxUserAgent.insets = new Insets(0, 0, 5, 0);
		gbc_comboBoxUserAgent.gridwidth = 3;
		gbc_comboBoxUserAgent.gridx = 1;
		gbc_comboBoxUserAgent.gridy = 3;
		wdPanel.add(comboBoxUserAgent, gbc_comboBoxUserAgent);
																
																	
																
		labelAString.setVisible(false);
		GridBagConstraints gbc_labelAString = new GridBagConstraints();
		gbc_labelAString.fill = GridBagConstraints.HORIZONTAL;
		gbc_labelAString.insets = new Insets(0, 0, 0, 5);
		gbc_labelAString.gridx = 0;
		gbc_labelAString.gridy = 4;
		wdPanel.add(labelAString, gbc_labelAString);
		textUserAgent = new JTextField(dbConfig.getConfigValue("USER_AGENT"));
		GridBagConstraints gbc_textUserAgent = new GridBagConstraints();
		gbc_textUserAgent.fill = GridBagConstraints.BOTH;
		gbc_textUserAgent.gridwidth = 3;
		gbc_textUserAgent.gridx = 1;
		gbc_textUserAgent.gridy = 4;
		wdPanel.add(textUserAgent, gbc_textUserAgent);
		textUserAgent.setColumns(10);
		textUserAgent.setVisible(false);
		
		JCheckBox chkSendEmail = new JCheckBox("Send Email Notification.");
		//chkSendEmail.setOpaque(true);
		GridBagConstraints gbc_chkSendEmail = new GridBagConstraints();
		gbc_chkSendEmail.fill = GridBagConstraints.HORIZONTAL;
		gbc_chkSendEmail.insets = new Insets(0, 0, 5, 5);
		gbc_chkSendEmail.gridx = 1;
		gbc_chkSendEmail.gridy = 3;
		contentPanel.add(chkSendEmail, gbc_chkSendEmail);
		JPanel emailPanel = new JPanel();
		chkSendEmail.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				enableComponents(emailPanel, chkSendEmail.isSelected());
			}
		});
		JPanel uiPanel = new JPanel();
		uiPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "User Interface",
				TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		GridBagConstraints gbc_uiPanel = new GridBagConstraints();
		gbc_uiPanel.fill = GridBagConstraints.BOTH;
		gbc_uiPanel.insets = new Insets(0, 0, 5, 5);
		gbc_uiPanel.gridx = 1;
		gbc_uiPanel.gridy = 2;
		contentPanel.add(uiPanel, gbc_uiPanel);
		GridBagLayout gbl_uiPanel = new GridBagLayout();
		gbl_uiPanel.columnWidths = new int[]{56, 63, 328, 0};
		gbl_uiPanel.rowHeights = new int[]{31, 0};
		gbl_uiPanel.columnWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_uiPanel.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		uiPanel.setLayout(gbl_uiPanel);
		
		JLabel lblTheme = new JLabel("Theme: ");
		GridBagConstraints gbc_lblTheme = new GridBagConstraints();
		gbc_lblTheme.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblTheme.insets = new Insets(0, 0, 0, 5);
		gbc_lblTheme.gridx = 0;
		gbc_lblTheme.gridy = 0;
		uiPanel.add(lblTheme, gbc_lblTheme);
		
		JComboBox<String> comboBox = new JComboBox<String>();
		comboBox.setModel(new DefaultComboBoxModel<String>(new String[] { "Default","Windows Classic",
																			"Nimbus","Web","NimROD","JTattoo McWin" }));
		comboBox.setSelectedItem(dbConfig.getConfigValue("UI_THEME"));
		GridBagConstraints gbc_comboBox = new GridBagConstraints();
		gbc_comboBox.fill = GridBagConstraints.BOTH;
		gbc_comboBox.gridx = 2;
		gbc_comboBox.gridy = 0;
		
		JPanel backupPanel = new JPanel();
		uiPanel.add(comboBox, gbc_comboBox);
		
				
						
		emailPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), ""));
		GridBagConstraints gbc_emailPanel = new GridBagConstraints();
		gbc_emailPanel.fill = GridBagConstraints.BOTH;
		gbc_emailPanel.insets = new Insets(0, 0, 5, 5);
		gbc_emailPanel.gridx = 1;
		gbc_emailPanel.gridy = 4;
		contentPanel.add(emailPanel, gbc_emailPanel);
		GridBagLayout gbl_emailPanel = new GridBagLayout();
		gbl_emailPanel.columnWidths = new int[]{67, 237, 52, 88, 0};
		gbl_emailPanel.rowHeights = new int[]{28, 28, 29, 28, 0};
		gbl_emailPanel.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_emailPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		emailPanel.setLayout(gbl_emailPanel);
						
		JLabel lblHost = new JLabel("Host:");
		GridBagConstraints gbc_lblHost = new GridBagConstraints();
		gbc_lblHost.fill = GridBagConstraints.BOTH;
		gbc_lblHost.insets = new Insets(0, 0, 5, 5);
		gbc_lblHost.gridx = 0;
		gbc_lblHost.gridy = 0;
		emailPanel.add(lblHost, gbc_lblHost);
								
		txtEmailHost = new JTextField(dbConfig.getConfigValue("EMAIL_HOST"));
		GridBagConstraints gbc_txtEmailHost = new GridBagConstraints();
		gbc_txtEmailHost.fill = GridBagConstraints.BOTH;
		gbc_txtEmailHost.insets = new Insets(0, 0, 5, 5);
		gbc_txtEmailHost.gridx = 1;
		gbc_txtEmailHost.gridy = 0;
		emailPanel.add(txtEmailHost, gbc_txtEmailHost);
		txtEmailHost.setColumns(10);
										
		txtEmailPort = new JTextField(dbConfig.getConfigValue("EMAIL_PORT"));
		GridBagConstraints gbc_txtEmailPort = new GridBagConstraints();
		gbc_txtEmailPort.anchor = GridBagConstraints.EAST;
		gbc_txtEmailPort.fill = GridBagConstraints.VERTICAL;
		gbc_txtEmailPort.insets = new Insets(0, 0, 5, 0);
		gbc_txtEmailPort.gridwidth = 2;
		gbc_txtEmailPort.gridx = 2;
		gbc_txtEmailPort.gridy = 0;
		emailPanel.add(txtEmailPort, gbc_txtEmailPort);
		txtEmailPort.setColumns(10);
												
		JLabel lblPort = new JLabel("Port:");
		GridBagConstraints gbc_lblPort = new GridBagConstraints();
		gbc_lblPort.fill = GridBagConstraints.BOTH;
		gbc_lblPort.insets = new Insets(0, 0, 5, 5);
		gbc_lblPort.gridx = 2;
		gbc_lblPort.gridy = 0;
		emailPanel.add(lblPort, gbc_lblPort);
														

		JLabel lblEmailUser = new JLabel("Email:");
		GridBagConstraints gbc_lblEmailUser = new GridBagConstraints();
		gbc_lblEmailUser.anchor = GridBagConstraints.NORTH;
		gbc_lblEmailUser.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblEmailUser.insets = new Insets(0, 0, 5, 5);
		gbc_lblEmailUser.gridx = 0;
		gbc_lblEmailUser.gridy = 1;
		emailPanel.add(lblEmailUser, gbc_lblEmailUser);
														
		txtEmail = new JTextField(dbConfig.getConfigValue("EMAIL_USER"));
		GridBagConstraints gbc_txtEmail = new GridBagConstraints();
		gbc_txtEmail.fill = GridBagConstraints.BOTH;
		gbc_txtEmail.insets = new Insets(0, 0, 5, 5);
		gbc_txtEmail.gridx = 1;
		gbc_txtEmail.gridy = 1;
		emailPanel.add(txtEmail, gbc_txtEmail);
		txtEmail.setColumns(10);
																
		JLabel lblPassword = new JLabel("Password:");
		GridBagConstraints gbc_lblPassword = new GridBagConstraints();
		gbc_lblPassword.anchor = GridBagConstraints.NORTH;
		gbc_lblPassword.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblPassword.insets = new Insets(0, 0, 5, 5);
		gbc_lblPassword.gridx = 0;
		gbc_lblPassword.gridy = 2;
		emailPanel.add(lblPassword, gbc_lblPassword);
																		
		passwordEmail = new JPasswordField(encrypt.decryptPassword(dbConfig.getConfigValue("EMAIL_PASS")));
		GridBagConstraints gbc_passwordEmail = new GridBagConstraints();
		gbc_passwordEmail.fill = GridBagConstraints.BOTH;
		gbc_passwordEmail.insets = new Insets(0, 0, 5, 5);
		gbc_passwordEmail.gridx = 1;
		gbc_passwordEmail.gridy = 2;
		emailPanel.add(passwordEmail, gbc_passwordEmail);
																				
		JCheckBox chckbxSendToMe = new JCheckBox("Send to me.");
		GridBagConstraints gbc_chckbxSendToMe = new GridBagConstraints();
		gbc_chckbxSendToMe.fill = GridBagConstraints.HORIZONTAL;
		gbc_chckbxSendToMe.insets = new Insets(0, 0, 5, 0);
		gbc_chckbxSendToMe.gridx = 3;
		gbc_chckbxSendToMe.gridy = 2;
		emailPanel.add(chckbxSendToMe, gbc_chckbxSendToMe);
																						
		JLabel lblRecipient = new JLabel("Recipient:");
		GridBagConstraints gbc_lblRecipient = new GridBagConstraints();
		gbc_lblRecipient.fill = GridBagConstraints.BOTH;
		gbc_lblRecipient.insets = new Insets(0, 0, 0, 5);
		gbc_lblRecipient.gridx = 0;
		gbc_lblRecipient.gridy = 3;
		emailPanel.add(lblRecipient, gbc_lblRecipient);
																								
		txtEmailRecipient = new JTextField(dbConfig.getConfigValue("EMAIL_TO"));
		GridBagConstraints gbc_txtEmailRecipient = new GridBagConstraints();
		gbc_txtEmailRecipient.fill = GridBagConstraints.BOTH;
		gbc_txtEmailRecipient.gridwidth = 3;
		gbc_txtEmailRecipient.gridx = 1;
		gbc_txtEmailRecipient.gridy = 3;
		emailPanel.add(txtEmailRecipient, gbc_txtEmailRecipient);
		txtEmailRecipient.setColumns(10);
		JCheckBox chckbxNewCheckBox = new JCheckBox("Auto Backup");	
		//chckbxNewCheckBox.setOpaque(true);
		GridBagConstraints gbc_chckbxNewCheckBox = new GridBagConstraints();
		gbc_chckbxNewCheckBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_chckbxNewCheckBox.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxNewCheckBox.anchor = GridBagConstraints.NORTH;
		gbc_chckbxNewCheckBox.gridx = 1;
		gbc_chckbxNewCheckBox.gridy = 5;
		contentPanel.add(chckbxNewCheckBox, gbc_chckbxNewCheckBox);
		chckbxNewCheckBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				enableComponents(backupPanel, chckbxNewCheckBox.isSelected());
			}
		});
		
				
		backupPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "",
						TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		GridBagConstraints gbc_backupPanel = new GridBagConstraints();
		gbc_backupPanel.insets = new Insets(0, 0, 5, 5);
		gbc_backupPanel.fill = GridBagConstraints.BOTH;
		gbc_backupPanel.gridx = 1;
		gbc_backupPanel.gridy = 6;
		contentPanel.add(backupPanel, gbc_backupPanel);
		GridBagLayout gbl_backupPanel = new GridBagLayout();
		gbl_backupPanel.columnWidths = new int[]{46, 78, 65, 208, 29, 0};
		gbl_backupPanel.rowHeights = new int[]{34, 0};
		gbl_backupPanel.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_backupPanel.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		backupPanel.setLayout(gbl_backupPanel);
				
				
		JLabel lblInterval = new JLabel("Interval:");
		GridBagConstraints gbc_lblInterval = new GridBagConstraints();
		gbc_lblInterval.fill = GridBagConstraints.BOTH;
		gbc_lblInterval.insets = new Insets(0, 0, 0, 5);
		gbc_lblInterval.gridx = 0;
		gbc_lblInterval.gridy = 0;
		backupPanel.add(lblInterval, gbc_lblInterval);
		
		JSpinner spinnerBackupInterval = new JSpinner();
		GridBagConstraints gbc_spinnerBackupInterval = new GridBagConstraints();
		gbc_spinnerBackupInterval.fill = GridBagConstraints.BOTH;
		gbc_spinnerBackupInterval.insets = new Insets(0, 0, 0, 5);
		gbc_spinnerBackupInterval.gridx = 1;
		gbc_spinnerBackupInterval.gridy = 0;
		backupPanel.add(spinnerBackupInterval, gbc_spinnerBackupInterval);
		spinnerBackupInterval.setValue(Integer.parseInt(dbConfig.getConfigValue("BACKUP_INTERVAL")));
		JLabel lblLocation = new JLabel("Location:");
		GridBagConstraints gbc_lblLocation = new GridBagConstraints();
		gbc_lblLocation.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblLocation.insets = new Insets(0, 0, 0, 5);
		gbc_lblLocation.gridx = 2;
		gbc_lblLocation.gridy = 0;
		backupPanel.add(lblLocation, gbc_lblLocation);
		
		txtBackupLoc = new JTextField(dbConfig.getConfigValue("BACKUP_LOC"));
		txtBackupLoc.setEditable(false);
		GridBagConstraints gbc_txtBackupLoc = new GridBagConstraints();
		gbc_txtBackupLoc.fill = GridBagConstraints.BOTH;
		gbc_txtBackupLoc.insets = new Insets(0, 0, 0, 5);
		gbc_txtBackupLoc.gridx = 3;
		gbc_txtBackupLoc.gridy = 0;
		backupPanel.add(txtBackupLoc, gbc_txtBackupLoc);
		txtBackupLoc.setColumns(10);
		
		
		JButton btnBrowseBackupLoc = new JButton("...");
		GridBagConstraints gbc_btnBrowseBackupLoc = new GridBagConstraints();
		gbc_btnBrowseBackupLoc.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnBrowseBackupLoc.gridx = 4;
		gbc_btnBrowseBackupLoc.gridy = 0;
		backupPanel.add(btnBrowseBackupLoc, gbc_btnBrowseBackupLoc);
		btnBrowseBackupLoc.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String appPath = new File("").getAbsolutePath();
				JFileChooser fileChooser = new JFileChooser();
				String cPath = dbConfig.getConfigValue("BACKUP_LOC");
				File backupLoc = new File(cPath);
				if (backupLoc.exists()) {
					fileChooser.setCurrentDirectory(backupLoc);
				} else {
					fileChooser.setCurrentDirectory(new File(appPath + "\\Test Suite\\Backup"));
				}

				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int result = fileChooser.showOpenDialog(fileChooser);
				if (result == JFileChooser.APPROVE_OPTION) {
					File selectedFile = fileChooser.getSelectedFile();
					String path = selectedFile.getAbsolutePath();
					txtBackupLoc.setText(path);
				}
				
			}
		});
		enableComponents(emailPanel, chkSendEmail.isSelected());
		enableComponents(backupPanel, chckbxNewCheckBox.isSelected());
		
		if (dbConfig.getConfigValue("EMAIL_SEND_ME").contentEquals("Y")) {
			chckbxSendToMe.setSelected(true);
		} else {
			chckbxSendToMe.setSelected(false);
		}
		

		if (dbConfig.getConfigValue("HIGHLIGHT_ACTION").contentEquals("Y")) {
			chkHighLight.setSelected(true);
		} else {
			chkHighLight.setSelected(false);
		}
		if (dbConfig.getConfigValue("LOG_CONSOLE").contentEquals("Y")) {
			chckbxDisplayLogConsole.setSelected(true);
		} else {
			chckbxDisplayLogConsole.setSelected(false);
		}

		List<String[]> arrayList = dbConfig.getUserAgents();
		for (String[] array : arrayList) {
			comboBoxUserAgent.addItem(array[1]);
		}
		String agent = dbConfig.getUserAgent();
		if(dbConfig.getConfigValue("CUSTOM_AGENT").contentEquals("Y")) {
			comboBoxUserAgent.setSelectedItem("Custom");
			textUserAgent.setVisible(true);
			labelAString.setVisible(true);
		} else {
			if(agent.contentEquals("")) {
				comboBoxUserAgent.setSelectedItem("(Default)");
			} else {
				comboBoxUserAgent.setSelectedItem(agent);
			}
		}
		if (dbConfig.getConfigValue("GEN_XLSX_RESULT").contentEquals("Y")) {
			chckbxGenerateXlsReport.setSelected(true);
			labelAString.setVisible(true);
		} else {
			chckbxGenerateXlsReport.setSelected(false);
			labelAString.setVisible(false);
		}
		if (dbConfig.getConfigValue("EMAIL_SEND").contentEquals("Y")) {
			chkSendEmail.setSelected(true);
		} else {
			chkSendEmail.setSelected(false);
		}
		if (dbConfig.getConfigValue("BACKUP").contentEquals("Y")) {
			chckbxNewCheckBox.setSelected(true);
		} else {
			chckbxNewCheckBox.setSelected(false);
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

						String emailSendMe = "N";
						if (chckbxSendToMe.isSelected()) {
							emailSendMe = "Y";
						}
						String xlsxResult = "N";
						if (chckbxGenerateXlsReport.isSelected()) {
							xlsxResult = "Y";
						}
						String hlAction = "N";
						if (chkHighLight.isSelected()) {
							hlAction = "Y";
						}
						String logConsole = "N";
						if (chckbxDisplayLogConsole.isSelected()) {
							logConsole = "Y";
						}
						String custAgent = "N";
						if (comboBoxUserAgent.getSelectedItem().toString().contentEquals("Custom")) {
							custAgent = "Y";
						}
						String backup = "N";
						if(chckbxNewCheckBox.isSelected()) {
							backup = "Y";
						}
						
						
						String strEmail = txtEmail.getText();
						String strPassword = String.valueOf(passwordEmail.getPassword());
						String strEmailPort = txtEmailPort.getText();
						String strEmailHost = txtEmailHost.getText();
						String strRecipient = txtEmailRecipient.getText();

						if (chkSendEmail.isSelected()) {
							if (strEmail.isEmpty()) {
								JOptionPane.showMessageDialog(contentPanel, "Email is required!", "Error",
										JOptionPane.ERROR_MESSAGE);
							} else if (strPassword.isEmpty()) {
								JOptionPane.showMessageDialog(contentPanel, "Password is required!", "Error",
										JOptionPane.ERROR_MESSAGE);
							} else if (strEmailHost.isEmpty()) {
								JOptionPane.showMessageDialog(contentPanel, "Host is required!", "Error",
										JOptionPane.ERROR_MESSAGE);
							} else if (strEmailPort.isEmpty()) {
								JOptionPane.showMessageDialog(contentPanel, "Port is required!", "Error",
										JOptionPane.ERROR_MESSAGE);
							} else if (!chckbxSendToMe.isSelected() & strRecipient.isEmpty()) {
								JOptionPane.showMessageDialog(contentPanel, "Recipient is required!", "Error",
										JOptionPane.ERROR_MESSAGE);
							} else {
								dbConfig.updateConfig(spinnerTO.getValue().toString(), spinnerIW.getValue().toString(),
										txtEmailHost.getText(), txtEmailPort.getText(), "Y", txtEmail.getText(),
										String.valueOf(passwordEmail.getPassword()), txtEmailRecipient.getText(),
										emailSendMe, xlsxResult, spinnerAD.getValue().toString(), hlAction,
										cmbHLColor.getSelectedItem().toString(), logConsole,textUserAgent.getText(),custAgent,
										backup,spinnerBackupInterval.getValue().toString(),txtBackupLoc.getText(),
										comboBox.getSelectedItem().toString());
								dbConfig.updateUserAgent(comboBoxUserAgent.getSelectedItem().toString());
								dispose();
							}

						} else {
							dbConfig.updateConfig(spinnerTO.getValue().toString(), spinnerIW.getValue().toString(),
									txtEmailHost.getText(), txtEmailPort.getText(), "N", txtEmail.getText(),
									String.valueOf(passwordEmail.getPassword()), txtEmailRecipient.getText(),
									emailSendMe, xlsxResult, spinnerAD.getValue().toString(), hlAction,
									cmbHLColor.getSelectedItem().toString(), logConsole,textUserAgent.getText(),custAgent,
									backup,spinnerBackupInterval.getValue().toString(),txtBackupLoc.getText(),
									comboBox.getSelectedItem().toString());
							dbConfig.updateUserAgent(comboBoxUserAgent.getSelectedItem().toString());
							dispose();
						}

					}
				});
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dispose();
					}
				});
			}
		}
	}

	public void enableComponents(Container container, boolean enable) {
		Component[] components = container.getComponents();
		for (Component component : components) {
			component.setEnabled(enable);
			if (component instanceof Container) {
				enableComponents((Container) component, enable);
			}
		}
	}
}
