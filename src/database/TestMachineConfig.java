package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TestMachineConfig {
	private Connection dbConn = null;
	
	public TestMachineConfig() {
		dbConn = connect();
	}
	private PasswordEncryptDecrypt encrypt = new PasswordEncryptDecrypt();
	// connect to database
	public Connection connect() {

		Connection c = null;
		try {
			Class.forName("org.sqlite.JDBC");
			// connect to database
			c = DriverManager.getConnection("jdbc:sqlite:cfg\\config.db");
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			// System.exit(0);
		}
		return c;

	}

	public String getConfigValue(String cfgName) {
		List<String[]> recent = executeSelectQuery("SELECT VALUE FROM DRIVER_CONFIG WHERE NAME='" + cfgName + "'");
		for (String[] array : recent) {
			return array[0];
		}
		return "";
	}

	public List<String[]> getBrowsers() {

		return executeSelectQuery("SELECT ID,NAME,DESCRIPTION,VERSION,STATUS FROM SEL_DRIVER_BROWSER WHERE STATUS='Active' ORDER BY NAME ASC");
	}

	public List<String[]> getActions() {

		return executeSelectQuery("SELECT ID,NAME,DESCRIPTION,STATUS FROM SEL_DRIVER_ACTIONS WHERE STATUS='Active' ORDER BY NAME ASC");
	}
	
	public List<String[]> getUserAgents() {
		
		return executeSelectQuery("SELECT ID,NAME,VALUE,STATUS FROM USER_AGENT ORDER BY NAME ASC");
	}
	public String getUserAgent() {
		List<String[]> recent = executeSelectQuery("SELECT NAME FROM USER_AGENT WHERE STATUS='Y';");
		for (String[] array : recent) {
			return array[0];
		}
		return "";
	}

	public List<String[]> getAllActions() {

		return executeSelectQuery("SELECT ID,NAME,DESCRIPTION,STATUS,REPORT FROM SEL_DRIVER_ACTIONS ORDER BY NAME ASC");
	}

	public List<String[]> getActionReports(String val) {

		return executeSelectQuery("SELECT NAME FROM SEL_DRIVER_ACTIONS WHERE REPORT='" + val + "' ORDER BY NAME ASC");
	}

	public List<String[]> getSelector() {

		return executeSelectQuery("SELECT ID,NAME,DESCRIPTION,STATUS FROM SEL_DRIVER_ELEM_LOC WHERE STATUS='Active' ORDER BY NAME ASC");
	}

	public String getRecentFile() {
		List<String[]> recent = executeSelectQuery("SELECT TS_NAME FROM RECENT_TRXN ORDER BY LAST_OPEN DESC");
		for (String[] array : recent) {
			return array[0];
		}
		return "";
	}

	public List<String[]> getRecentFiles() {
		return executeSelectQuery("SELECT TS_NAME FROM RECENT_TRXN ORDER BY LAST_OPEN DESC LIMIT 10");
	}

	public void updateLastOpen(String name) {

		executeUpdateQuery("INSERT OR REPLACE INTO RECENT_TRXN (TS_NAME,LAST_OPEN) VALUES ('" + name
				+ "',datetime(CURRENT_TIMESTAMP,'localtime'))");

	}
	
	public void updateUserAgent(String name) {
		
		executeUpdateQuery("UPDATE USER_AGENT SET STATUS='N';");
		
		if(!name.contentEquals("Custom") && !name.contentEquals("(Default)")) {
			executeUpdateQuery("UPDATE USER_AGENT SET STATUS='Y' WHERE NAME ='"+name+"';");
		}
	}

	public void updateUser(String user) {

		executeUpdateQuery("UPDATE DRIVER_CONFIG SET VALUE='" + user + "' WHERE NAME='USER';");

	}

	public void updateAction(String id, String value, int col) {

		String dbColumn = null;
		switch (col) {
		case 3:
			dbColumn = "STATUS";
			break;
		case 4:
			dbColumn = "REPORT";
			break;
		}

		executeUpdateQuery("UPDATE SEL_DRIVER_ACTIONS SET " + dbColumn + "='" + value + "' WHERE ID='" + id + "';");

	}

	public void resetEmailCredential() {

		executeUpdateQuery("UPDATE DRIVER_CONFIG SET VALUE='' WHERE NAME='EMAIL_USER';"
				+ "UPDATE DRIVER_CONFIG SET VALUE='N' WHERE NAME='EMAIL_SEND';"
				+ "UPDATE DRIVER_CONFIG SET VALUE='' WHERE NAME='EMAIL_SEND_ME';" + "UPDATE DRIVER_CONFIG SET VALUE='"
				+ encrypt.createEncryptedPassword("") + "' WHERE NAME='EMAIL_PASS';");

	}

	public void deleteRecent() {

		executeUpdateQuery("DELETE FROM RECENT_TRXN;");

	}

	public void updateConfig(String pageLoadTO, String impWaitTO, String emailHost, String emailPort, String emailSend,
			String emailUser, String emailPass, String email_to, String emailSendMe, String xlsxResult, String delay,
			String hlAction, String hlColor, String logConsole, String userAgent,String custAgent,
			String backup, String backup_int, String backup_loc,String theme) {
		executeUpdateQuery("UPDATE DRIVER_CONFIG " + "SET VALUE = CASE " + " WHEN NAME = 'PAGELOAD_TIMEOUT' THEN '" + pageLoadTO + "'" 
				+ " WHEN NAME = 'IMPLICITLY_WAIT' THEN '" + impWaitTO + "'"
				+ " WHEN NAME = 'EMAIL_HOST' THEN '" + emailHost + "'" 
				+ " WHEN NAME = 'EMAIL_PORT' THEN '" + emailPort + "'" 
				+ " WHEN NAME = 'EMAIL_SEND' THEN '" + emailSend + "'" 
				+ " WHEN NAME = 'EMAIL_USER' THEN '"+ emailUser + "'" 
				+ " WHEN NAME = 'EMAIL_PASS' THEN '" + encrypt.createEncryptedPassword(emailPass) + "'"
				+ " WHEN NAME = 'EMAIL_TO' THEN '" + email_to + "'" 
				+ " WHEN NAME = 'EMAIL_SEND_ME' THEN '"+ emailSendMe + "'" 
				+ " WHEN NAME = 'GEN_XLSX_RESULT' THEN '" + xlsxResult + "'"
				+ " WHEN NAME = 'ACTION_DELAY' THEN '" + delay + "'" 
				+ " WHEN NAME = 'HIGHLIGHT_ACTION' THEN '"+ hlAction + "'" 
				+ " WHEN NAME = 'HIGHLIGHT_COLOR' THEN '" + hlColor + "'"
				+ " WHEN NAME = 'LOG_CONSOLE' THEN '" + logConsole + "'" 
				+ " WHEN NAME = 'USER_AGENT' THEN '" + userAgent.replaceAll("'", "''") + "'" 
				+ " WHEN NAME = 'CUSTOM_AGENT' THEN '" + custAgent + "'" 
				+ " WHEN NAME = 'BACKUP' THEN '" + backup + "'" 
				+ " WHEN NAME = 'BACKUP_INTERVAL' THEN '" + backup_int + "'" 
				+ " WHEN NAME = 'BACKUP_LOC' THEN '" + backup_loc.replaceAll("'", "''") + "'" 
				+ " WHEN NAME = 'UI_THEME' THEN '" + theme.replaceAll("'", "''") + "'" 
				+ " ELSE VALUE END"
				+ " WHERE NAME IN ('PAGELOAD_TIMEOUT','IMPLICITLY_WAIT','EMAIL_HOST','EMAIL_PORT','EMAIL_SEND',"
				+ " 'EMAIL_USER','EMAIL_PASS','EMAIL_TO','EMAIL_SEND_ME','GEN_XLSX_RESULT',"
				+ " 'ACTION_DELAY','HIGHLIGHT_ACTION','HIGHLIGHT_COLOR','LOG_CONSOLE','USER_AGENT','CUSTOM_AGENT',"
				+ "'BACKUP','BACKUP_INTERVAL','BACKUP_LOC','UI_THEME');");
	}

	public List<String[]> getModularSteps(String actionId) {

		return executeSelectQuery("SELECT ID,FLOW,DESCRIPTION,SELECTOR,ELEMENT,ACTION,PARAM_NAME,PARAM_VAL,CAPTURE"
				+ " FROM MODULAR_ACTION WHERE ACTION_ID='" + actionId + "' ORDER BY FLOW ASC");
	}

	public List<String[]> executeSelectQuery(String sql) {

		Statement stmt = null;
		try {
			stmt = dbConn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);

			ResultSetMetaData rsmd = rs.getMetaData();
			int col = rsmd.getColumnCount();
			List<String[]> modules = new ArrayList<String[]>();

			while (rs.next()) {
				String[] row = new String[col];
				for (int i = 0; i < col; i++) {
					row[i] = rs.getString(i + 1);
				}
				modules.add(row);
			}
			stmt.close();
			return modules;
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
		return null;
	}

	public void executeUpdateQuery(String sql) {
		try {

			Statement stmt = dbConn.createStatement();
			stmt.executeUpdate(sql);
			stmt.close();

		} catch (Exception e) {
			System.out.println(e.getClass().getName() + ": " + e.getMessage());
		}
	}

}
