package database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBProcess {
	public Connection dbConn = null;
	public DBProcess(String dbName) {
		dbConn = connect(dbName.replaceAll("\\\\", "\\\\\\\\"));
		if (dbConn != null) {
			//validate test Suite file, test run variables encrypt column.
			try {
				Statement stmt = dbConn.createStatement();
				//stmt.executeQuery("SELECT ENCRYPT FROM RUN_VARIABLES;");
				stmt.close();
			}catch (Exception e){
				//e.printStackTrace();
				//executeUpdateQuery("ALTER TABLE RUN_VARIABLES ADD COLUMN ENCRYPT TEXT DEFAULT 0;");
			}
		}	
	}
	private PasswordEncryptDecrypt encrypt = new PasswordEncryptDecrypt();
	// connect to database
	public Connection connect(String dbName) {
		if(dbName.contentEquals("")) {return null;}
		try {
			Class.forName("org.sqlite.JDBC");
			// connect to database
			return DriverManager.getConnection("jdbc:sqlite:" + dbName);

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
		return null;
	}

	public void createTestSuite() {
		Statement stmt = null;
		try {
			stmt = dbConn.createStatement();
			String sql = "CREATE TABLE [MODULES] ([ID] INTEGER  PRIMARY KEY AUTOINCREMENT NOT NULL,[NAME] TEXT  NULL,[DESCRIPTION] TEXT  NULL,[FLOW] INTEGER  NULL);"
					+ "CREATE TABLE [RUN_STEPS] ([ID] INTEGER  PRIMARY KEY AUTOINCREMENT NOT NULL,[RUN_TESTCASE_ID] INTEGER  NULL,"
					+ "[TESTCASE_ID] INTEGER  NULL,[STEP_ID] INTEGER  NULL,[RESULT] TEXT  NULL,[REMARKS] TEXT  NULL);"
					+ "CREATE TABLE [RUN_TESTCASES] ([ID] INTEGER  PRIMARY KEY AUTOINCREMENT NOT NULL,[RUN_ID] INTEGER  NULL,[TESTCASE_ID] INTEGER  NULL,"
					+ "[RESULT] TEXT  NULL,[REMARKS] TEXT  NULL,[START_TIME] TEXT  NULL,[END_TIME] TEXT  NULL);"
					+ "CREATE TABLE [TESTCASES] ([ID] INTEGER  PRIMARY KEY AUTOINCREMENT NOT NULL,[NAME] TEXT  NULL,[DESCRIPTION] TEXT  NULL,[FLOW] INTEGER  NULL,"
					+ "[MODULE_ID] INTEGER  NULL);"
					+ "CREATE TABLE [TEST_DATA] ([ID] INTEGER  PRIMARY KEY AUTOINCREMENT NOT NULL,[RUN_ID] INTEGER  NULL,[TESTCASE_ID] INTEGER  NULL,"
					+ "[PARAM_NAME] TEXT  NULL,[PARAM_VALUE] TEXT  NULL);"
					+ "CREATE TABLE [TEST_RUNS] ([ID] INTEGER  PRIMARY KEY AUTOINCREMENT NOT NULL,[NAME] TEXT  NULL,[BROWSER] TEXT  NULL,[START_TIME] TEXT  NULL,"
					+ "[END_TIME] TEXT  NULL,[RESULT] TEXT  NULL,[USE_TEST_DATA] INTEGER NULL,[RESULT_PATH] TEXT NULL);"
					+ "CREATE TABLE [TEST_STEPS] ([ID] INTEGER  PRIMARY KEY AUTOINCREMENT NOT NULL,[TESTCASE_ID] INTEGER  NULL,[FLOW] INTEGER  NULL,"
					+ "[DESCRIPTION] TEXT  NULL,[ELEMENT_SELECTOR] TEXT  NULL,[ELEMENT_STRING] TEXT  NULL,[ACTION] TEXT  NULL,[PARAM_NAME] TEXT  NULL,"
					+ "[PARAM_VALUE] TEXT  NULL,[SCREENCAPTURE] TEXT  NULL);"
					+ "CREATE TABLE [MODULAR_ACTION] ([ID] INTEGER  PRIMARY KEY AUTOINCREMENT NOT NULL,[FLOW] INTEGER  NULL,"
					+ "[DESCRIPTION] TEXT  NULL,[ELEMENT_SELECTOR] TEXT  NULL,[ELEMENT_STRING] TEXT  NULL,[ACTION] TEXT  NULL,[PARAM_NAME] TEXT  NULL,"
					+ "[PARAM_VALUE] TEXT  NULL,[SCREENCAPTURE] TEXT  NULL,[ACTION_ID] TEXT  NULL);"
					+ "CREATE TABLE [VARIABLES]([ID] INTEGER PRIMARY KEY  AUTOINCREMENT , [NAME] TEXT, [VALUE] TEXT);"
					+ "CREATE TABLE [RUN_VARIABLES] ([ID] INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL , [RUN_ID] TEXT, [NAME] TEXT, [VALUE] TEXT, [ENCRYPT] TEXT);";

			stmt.executeUpdate(sql);
			stmt.close();

		} catch (Exception e) {

			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			// System.exit(0);
		}
	}

	// get all modules
	public List<String[]> getModules() {

		return executeSelectQuery("SELECT ID,NAME,DESCRIPTION,FLOW FROM MODULES ORDER BY FLOW ASC");
	}

	public List<String[]> getModule(String id) {

		return executeSelectQuery("SELECT ID,NAME,DESCRIPTION,FLOW FROM MODULES WHERE ID IN (" + id + ") ORDER BY FLOW ASC");
	}

	// get testcases based on module id
	public List<String[]> getTestcases(String modID) {

		return executeSelectQuery("SELECT ID,NAME,DESCRIPTION,FLOW,MODULE_ID FROM TESTCASES WHERE MODULE_ID = '" + modID
				+ "' ORDER BY FLOW ASC");

	}

	public List<String[]> getTestcase(String id) {

		return executeSelectQuery("SELECT ID,NAME,DESCRIPTION,FLOW,MODULE_ID FROM TESTCASES WHERE ID = '" + id + "'");

	}

	// get test steps based on testcase id
	public List<String[]> getSteps(String tcID) {

		return executeSelectQuery(
				"SELECT ID,TESTCASE_ID,FLOW,DESCRIPTION,ELEMENT_SELECTOR,ELEMENT_STRING,ACTION,PARAM_NAME,PARAM_VALUE,SCREENCAPTURE FROM TEST_STEPS "
						+ "WHERE TESTCASE_ID = '" + tcID + "' ORDER BY FLOW ASC");

	}

	public void updateStep(String ID, int col, String value) {

		String dbColumn = null;
		switch (col) {
		case 2:
			dbColumn = "FLOW";
			break;
		case 3:
			dbColumn = "DESCRIPTION";
			break;
		case 4:
			dbColumn = "ELEMENT_SELECTOR";
			break;
		case 5:
			dbColumn = "ELEMENT_STRING";
			break;
		case 6:
			dbColumn = "ACTION";
			break;
		case 7:
			dbColumn = "PARAM_NAME";
			break;
		case 8:
			dbColumn = "PARAM_VALUE";
			break;
		case 9:
			dbColumn = "SCREENCAPTURE";
			break;
		}
		value = value.replaceAll("'", "''");
		executeUpdateQuery("UPDATE TEST_STEPS SET " + dbColumn + "='" + value + "' WHERE ID='" + ID + "'");
	}

	public void updateModule(String ID, int col, String value) {

		String dbColumn = null;
		switch (col) {
		case 1:
			dbColumn = "NAME";
			break;
		case 2:
			dbColumn = "DESCRIPTION";
			break;
		case 3:
			dbColumn = "FLOW";
			break;
		}
		value = value.replaceAll("'", "''");
		executeUpdateQuery("UPDATE MODULES SET " + dbColumn + "='" + value + "' WHERE ID='" + ID + "'");

	}

	public void updateTestcases(String ID, int col, String value) {

		String dbColumn = null;
		switch (col) {
		case 1:
			dbColumn = "NAME";
			break;
		case 2:
			dbColumn = "DESCRIPTION";
			break;
		case 3:
			dbColumn = "FLOW";
			break;
		}
		value = value.replaceAll("'", "''");
		executeUpdateQuery("UPDATE TESTCASES SET " + dbColumn + "='" + value + "' WHERE ID='" + ID + "'");

	}

	public void updateTestDesc(String ID, String desc) {

		desc = desc.replaceAll("'", "''");
		executeUpdateQuery("UPDATE TESTCASES SET DESCRIPTION='" + desc + "' WHERE ID='" + ID + "'");

	}

	public void updateTestName(String ID, String name) {

		name = name.replaceAll("'", "''");
		executeUpdateQuery("UPDATE TESTCASES SET NAME='" + name + "' WHERE ID='" + ID + "'");

	}

	public void updateModName(String ID, String name) {

		name = name.replaceAll("'", "''");
		executeUpdateQuery("UPDATE MODULES SET NAME='" + name + "'  WHERE ID='" + ID + "'");

	}

	public void updateModDesc(String ID, String desc) {

		desc = desc.replaceAll("'", "''");
		executeUpdateQuery("UPDATE MODULES SET DESCRIPTION='" + desc + "' WHERE ID='" + ID + "'");

	}

	public void updateTestDataParamValue(String ID, String value, String paramName) {

		value = value.replaceAll("'", "''");
		executeUpdateQuery("UPDATE TEST_DATA SET PARAM_VALUE='" + value + "' "
				+ "WHERE RUN_ID=(SELECT RUN_ID FROM RUN_TESTCASES WHERE ID=(SELECT RUN_TESTCASE_ID FROM RUN_STEPS WHERE ID='"
				+ ID + "')) " + "AND TESTCASE_ID=(SELECT TESTCASE_ID FROM RUN_STEPS WHERE ID ='" + ID + "') "
				+ "AND PARAM_NAME='" + paramName + "'");

	}

	public void deleteStep(String ID) {
		if (ID.equalsIgnoreCase("")) {
			return;
		}

		List<String[]> steps = executeSelectQuery("SELECT ID,FLOW FROM TEST_STEPS "
				+ "WHERE TESTCASE_ID=(SELECT TESTCASE_ID FROM TEST_STEPS WHERE ID IN (" + ID + ")) " + "AND ID NOT IN ("
				+ ID + ") ORDER BY FLOW ASC");
		String whenQ = "";
		int i = 0;
		String id = "";
		for (String[] step : steps) {
			i = i + 1;
			whenQ = whenQ + " WHEN ID ='" + step[0] + "' THEN '" + i + "' ";
			id = id + step[0] + ",";
		}
		if (whenQ != "") {
			executeUpdateQuery("UPDATE TEST_STEPS  SET FLOW = CASE " + whenQ + " ELSE FLOW END WHERE ID IN ("
					+ id.substring(0, id.length() - 1) + ");");
		}

		executeUpdateQuery("DELETE FROM TEST_STEPS WHERE ID IN (" + ID + ");");

	}

	public void deleteTestcases(String ID,String ModID) {

		executeUpdateQuery("DELETE FROM TESTCASES WHERE ID='" + ID + "';" + "DELETE FROM TEST_STEPS WHERE TESTCASE_ID='"
				+ ID + "';");
		
		List<String[]> tcs = executeSelectQuery("SELECT ID FROM TESTCASES WHERE MODULE_ID ='" + ModID + "' ORDER BY FLOW ASC");
		String whenQ = "";
		int i = 0;
		String id = "";
		for (String[] tc : tcs) {
			i = i + 1;
			whenQ = whenQ + " WHEN ID ='" + tc[0] + "' THEN '" + i + "' ";
			id = id + tc[0] + ",";
		}
		if (whenQ != "") {
			executeUpdateQuery("UPDATE TESTCASES SET FLOW = CASE " + whenQ + " ELSE FLOW END WHERE ID IN ("
					+ id.substring(0, id.length() - 1) + ");");
		}

	}

	public void deleteModule(String ID) {

		executeUpdateQuery("DELETE FROM TEST_STEPS WHERE TESTCASE_ID IN (SELECT ID FROM TESTCASES WHERE MODULE_ID='"
				+ ID + "');" + "DELETE FROM MODULES WHERE ID='" + ID + "';" + "DELETE FROM TESTCASES WHERE MODULE_ID ='"
				+ ID + "';");
		
		List<String[]> mods = executeSelectQuery("SELECT ID FROM MODULES ORDER BY FLOW ASC");
		String whenQ = "";
		int i = 0;
		String id = "";
		for (String[] mod : mods) {
			i = i + 1;
			whenQ = whenQ + " WHEN ID ='" + mod[0] + "' THEN '" + i + "' ";
			id = id + mod[0] + ",";
		}
		if (whenQ != "") {
			executeUpdateQuery("UPDATE MODULES SET FLOW = CASE " + whenQ + " ELSE FLOW END WHERE ID IN ("
					+ id.substring(0, id.length() - 1) + ");");
		}
	}

	public void addStep(String tcID) {

		executeUpdateQuery(
				"INSERT INTO TEST_STEPS(TESTCASE_ID,FLOW,DESCRIPTION,ELEMENT_SELECTOR,ELEMENT_STRING,ACTION,PARAM_NAME,PARAM_VALUE,SCREENCAPTURE) "
						+ "VALUES(" + tcID + ",(SELECT IFNULL(MAX(FLOW),0) FROM TEST_STEPS WHERE TESTCASE_ID=" + tcID
						+ ")+1,'','','','','','','No')");

	}

	public void insertStep(String tcID, String stepId) {

		List<String[]> steps = executeSelectQuery("SELECT ID,FLOW FROM TEST_STEPS "
				+ "WHERE TESTCASE_ID=(SELECT TESTCASE_ID FROM TEST_STEPS WHERE ID = " + stepId + ") "
				+ "AND FLOW >= (SELECT FLOW FROM TEST_STEPS WHERE ID = " + stepId + ") ORDER BY FLOW ASC");
		String whenQ = "";
		String id = "";
		for (String[] step : steps) {
			whenQ = whenQ + " WHEN ID ='" + step[0] + "' THEN '" + (Integer.parseInt(step[1]) + 1) + "' ";
			id = id + step[0] + ",";
		}
		if (whenQ != "") {
			executeUpdateQuery("UPDATE TEST_STEPS  SET FLOW = CASE " + whenQ + " ELSE FLOW END WHERE ID IN ("
					+ id.substring(0, id.length() - 1) + ");");
		}
		executeUpdateQuery(
				"INSERT INTO TEST_STEPS(TESTCASE_ID,FLOW,DESCRIPTION,ELEMENT_SELECTOR,ELEMENT_STRING,ACTION,PARAM_NAME,PARAM_VALUE,SCREENCAPTURE) "
						+ "VALUES(" + tcID + ",(SELECT FLOW FROM TEST_STEPS WHERE ID = " + stepId
						+ ") -1,'','','','','','','No')");

	}

	public void insertTestcase(String moduleID,int rowindex) {
		if(rowindex >= 0) {
			
			List<String[]> tcs = executeSelectQuery(
					"SELECT ID,NAME,DESCRIPTION,FLOW,MODULE_ID FROM TESTCASES "
							+ " WHERE MODULE_ID = '" + moduleID + "' AND  FLOW > " + rowindex + " ORDER BY FLOW ASC;");
			rowindex++;
			executeUpdateQuery("INSERT INTO TESTCASES(NAME,DESCRIPTION,FLOW,MODULE_ID) "
					+ "VALUES('',''," + rowindex + ","
					+ moduleID + ")");
			
			String whenQ = "";
			String id = "";
			for (String[] tc : tcs) {
				rowindex = rowindex + 1;
				whenQ = whenQ + " WHEN ID ='" + tc[0] + "' THEN '" + rowindex + "' ";
				id = id + tc[0] + ",";
			}
			if (whenQ != "") {
				executeUpdateQuery("UPDATE TESTCASES  SET FLOW = CASE " + whenQ + " ELSE FLOW END WHERE ID IN ("
						+ id.substring(0, id.length() - 1) + ");");
			}
		}
	}
	public void upTestcase(String moduleID,String tcId, int rowindex) {

		List<String[]> tcs = executeSelectQuery(
				"SELECT ID FROM TESTCASES WHERE MODULE_ID = '" + moduleID + "' AND  FLOW = " + rowindex + ";");
		
		executeUpdateQuery("UPDATE TESTCASES  SET FLOW = '"+ rowindex +"' WHERE ID = '"+tcId+"';");
		
		String whenQ = "";
		String id = "";
		for (String[] tc : tcs) {
			rowindex = rowindex + 1;
			whenQ = whenQ + " WHEN ID ='" + tc[0] + "' THEN '" + rowindex + "' ";
			id = id + tc[0] + ",";
		}
		if (whenQ != "") {
			executeUpdateQuery("UPDATE TESTCASES  SET FLOW = CASE " + whenQ + " ELSE FLOW END WHERE ID IN ("
					+ id.substring(0, id.length() - 1) + ");");
		}
	}
	public void upStep(String tcId,String stepId, int rowindex) {

		List<String[]> steps = executeSelectQuery(
				"SELECT ID FROM TEST_STEPS WHERE TESTCASE_ID = '" + tcId + "' AND  FLOW = " + rowindex + ";");
		
		executeUpdateQuery("UPDATE TEST_STEPS  SET FLOW = '"+ rowindex +"' WHERE ID = '"+stepId+"';");
		
		String whenQ = "";
		String id = "";
		for (String[] step : steps) {
			rowindex = rowindex + 1;
			whenQ = whenQ + " WHEN ID ='" + step[0] + "' THEN '" + rowindex + "' ";
			id = id + step[0] + ",";
		}
		if (whenQ != "") {
			executeUpdateQuery("UPDATE TEST_STEPS  SET FLOW = CASE " + whenQ + " ELSE FLOW END WHERE ID IN ("
					+ id.substring(0, id.length() - 1) + ");");
		}
	}

	public void upModule(String moduleID, int rowindex) {

		List<String[]> mods = executeSelectQuery(
				"SELECT ID FROM MODULES WHERE FLOW = " + rowindex + ";");
		
		executeUpdateQuery("UPDATE MODULES SET FLOW = '"+ rowindex +"' WHERE ID = '"+moduleID+"';");
		
		String whenQ = "";
		String id = "";
		for (String[] mod : mods) {
			rowindex = rowindex + 1;
			whenQ = whenQ + " WHEN ID ='" + mod[0] + "' THEN '" + rowindex + "' ";
			id = id + mod[0] + ",";
		}
		if (whenQ != "") {
			executeUpdateQuery("UPDATE MODULES  SET FLOW = CASE " + whenQ + " ELSE FLOW END WHERE ID IN ("
					+ id.substring(0, id.length() - 1) + ");");
		}
	}
	public void downModule(String moduleID, int rowindex) {

		List<String[]> mods = executeSelectQuery(
				"SELECT ID FROM MODULES WHERE FLOW = " + (rowindex+2) + ";");
		
		executeUpdateQuery("UPDATE MODULES SET FLOW = '"+ (rowindex+2) +"' WHERE ID = '"+moduleID+"';");
		
		String whenQ = "";
		String id = "";
		for (String[] mod : mods) {
			rowindex++;
			whenQ = whenQ + " WHEN ID ='" + mod[0] + "' THEN '" + rowindex + "' ";
			id = id + mod[0] + ",";
		}
		if (whenQ != "") {
			executeUpdateQuery("UPDATE MODULES  SET FLOW = CASE " + whenQ + " ELSE FLOW END WHERE ID IN ("
					+ id.substring(0, id.length() - 1) + ");");
		}
	}
	public void downTestcase(String moduleID,String tcId, int rowindex) {
			
		List<String[]> tcs = executeSelectQuery(
				"SELECT ID FROM TESTCASES WHERE MODULE_ID = '" + moduleID + "' AND  FLOW = " + (rowindex+2) + ";");
		
		executeUpdateQuery("UPDATE TESTCASES  SET FLOW = '"+ (rowindex+2) +"' WHERE ID = '"+tcId+"';");
		
		String whenQ = "";
		String id = "";
		for (String[] tc : tcs) {
			rowindex++;
			whenQ = whenQ + " WHEN ID ='" + tc[0] + "' THEN '" + rowindex + "' ";
			id = id + tc[0] + ",";
		}
		if (whenQ != "") {
			executeUpdateQuery("UPDATE TESTCASES  SET FLOW = CASE " + whenQ + " ELSE FLOW END WHERE ID IN ("
					+ id.substring(0, id.length() - 1) + ");");
		}
	}
	public void downStep(String tcId,String stepId, int rowindex) {

		List<String[]> steps = executeSelectQuery(
				"SELECT ID FROM TEST_STEPS WHERE TESTCASE_ID = '" + tcId + "' AND  FLOW = " + (rowindex+2) + ";");
		
		executeUpdateQuery("UPDATE TEST_STEPS  SET FLOW = '"+ (rowindex+2) +"' WHERE ID = '"+stepId+"';");
		
		String whenQ = "";
		String id = "";
		for (String[] step : steps) {
			rowindex++;
			whenQ = whenQ + " WHEN ID ='" + step[0] + "' THEN '" + rowindex + "' ";
			id = id + step[0] + ",";
		}
		if (whenQ != "") {
			executeUpdateQuery("UPDATE TEST_STEPS  SET FLOW = CASE " + whenQ + " ELSE FLOW END WHERE ID IN ("
					+ id.substring(0, id.length() - 1) + ");");
		}
	}
	public void addTestcase(String moduleID) {
		
		executeUpdateQuery("INSERT INTO TESTCASES(NAME,DESCRIPTION,FLOW,MODULE_ID) "
				+ "VALUES('','',(SELECT IFNULL(MAX(FLOW),0) FROM TESTCASES WHERE MODULE_ID=" + moduleID + ")+1,"
				+ moduleID + ")");

	}

	public void addModule(String name, String desc) {

		executeUpdateQuery("INSERT INTO MODULES(NAME,DESCRIPTION,FLOW) VALUES('" + name.replaceAll("'", "''") + "','"
				+ desc.replaceAll("'", "''") + "',(SELECT IFNULL(MAX(FLOW),0) FROM MODULES)+1)");

	}
	public void insertModule(String name, String desc, int rowindex) {

		if(rowindex >= 0) {
			
			List<String[]> mods = executeSelectQuery(
					"SELECT ID FROM MODULES  WHERE  FLOW > " + rowindex + " ORDER BY FLOW ASC;");
			rowindex++;

			executeUpdateQuery("INSERT INTO MODULES(NAME,DESCRIPTION,FLOW) VALUES('" + name.replaceAll("'", "''") + "','"
					+ desc.replaceAll("'", "''") + "',"+rowindex+")");
			
			String whenQ = "";
			String id = "";
			for (String[] mod : mods) {
				rowindex = rowindex + 1;
				whenQ = whenQ + " WHEN ID ='" + mod[0] + "' THEN '" + rowindex + "' ";
				id = id + mod[0] + ",";
			}
			if (whenQ != "") {
				executeUpdateQuery("UPDATE MODULES  SET FLOW = CASE " + whenQ + " ELSE FLOW END WHERE ID IN ("
						+ id.substring(0, id.length() - 1) + ");");
			}
		}

	}


	public List<String[]> getTestRun() {

		return executeSelectQuery(
				"SELECT ID, NAME, BROWSER,START_TIME,END_TIME,'' AS DURATION,RESULT,USE_TEST_DATA FROM TEST_RUNS ORDER BY ID DESC");

	}

	public List<String[]> getRunModules(String rId) {

		return executeSelectQuery(
				"SELECT MOD.ID, MOD.NAME, MOD.DESCRIPTION,'' AS START_TIME,'' AS END_TIME,'' AS DURATION, RES.RESULT FROM MODULES MOD, RUN_TESTCASES RES, TESTCASES TC "
						+ "WHERE MOD.ID =  TC.MODULE_ID " + "AND TC.ID = RES.TESTCASE_ID " + "AND RES.RUN_ID ='" + rId
						+ "' " + "GROUP BY MOD.ID " + "ORDER BY MOD.FLOW ASC;");

	}

	public List<String[]> getRunTestcases(String modID, String runId) {

		return executeSelectQuery(
				"SELECT RES.ID, TC.NAME, TC.DESCRIPTION, RES.START_TIME, RES.END_TIME,'' AS DURATION, RES.RESULT "
						+ "FROM RUN_TESTCASES RES, TESTCASES TC, TEST_RUNS TR " + "WHERE RES.TESTCASE_ID=TC.ID "
						+ "AND  RES.RUN_ID = TR.ID " + "AND TC.MODULE_ID = '" + modID + "' " + "AND TR.ID = '" + runId
						+ "' " + "ORDER BY TC.FLOW ASC;");

	}

	public List<String[]> getRunTestcasesPassed(String modID, String runId) {

		return executeSelectQuery("SELECT RES.ID, TC.NAME, TC.DESCRIPTION, RES.START_TIME, RES.END_TIME, RES.RESULT "
				+ "FROM RUN_TESTCASES RES, TESTCASES TC, TEST_RUNS TR " + "WHERE RES.TESTCASE_ID=TC.ID "
				+ "AND  RES.RUN_ID = TR.ID " + "AND TC.MODULE_ID = '" + modID + "' " + "AND TR.ID = '" + runId + "' "
				+ "AND RES.RESULT='Passed';");

	}

	public List<String[]> getRunTestcasesFailed(String modID, String runId) {

		return executeSelectQuery("SELECT RES.ID, TC.NAME, TC.DESCRIPTION, RES.START_TIME, RES.END_TIME, RES.RESULT "
				+ "FROM RUN_TESTCASES RES, TESTCASES TC, TEST_RUNS TR " + "WHERE RES.TESTCASE_ID=TC.ID "
				+ "AND  RES.RUN_ID = TR.ID " + "AND TC.MODULE_ID = '" + modID + "' " + "AND TR.ID = '" + runId + "' "
				+ "AND RES.RESULT='Failed';");

	}

	public List<String[]> getRunTestSteps(String tcID, String opt) {

		return executeSelectQuery(
				"SELECT RS.ID, TS.FLOW, TS.DESCRIPTION, TS.ELEMENT_SELECTOR, TS.ELEMENT_STRING, TS.ACTION, TS.PARAM_NAME, "
						+ "TS.PARAM_VALUE,TS.SCREENCAPTURE, RS.RESULT, RS.REMARKS "
						+ "FROM TEST_STEPS TS, RUN_TESTCASES RTC, RUN_STEPS RS " + "WHERE RTC.ID = RS.RUN_TESTCASE_ID "
						+ "AND RS.STEP_ID = TS.ID " + "AND RTC.ID = '" + tcID + "' " + opt + "ORDER BY TS.FLOW ASC;");

	}

	public List<String[]> getRunTestStepsWithTestData(String tcID, String opt) {

		return executeSelectQuery(
				"SELECT RS.ID, TS.FLOW, TS.DESCRIPTION, TS.ELEMENT_SELECTOR, TS.ELEMENT_STRING, TS.ACTION, TS.PARAM_NAME,"
						+ "(SELECT PARAM_VALUE FROM TEST_DATA WHERE TESTCASE_ID = RS.TESTCASE_ID AND RUN_ID = RTC.RUN_ID AND PARAM_NAME = TS.PARAM_NAME) AS PARAM_VALUE,"
						+ "TS.SCREENCAPTURE, RS.RESULT, RS.REMARKS "
						+ "FROM TEST_STEPS TS, RUN_TESTCASES RTC, RUN_STEPS RS " + "WHERE RTC.ID = RS.RUN_TESTCASE_ID "
						+ "AND RS.STEP_ID = TS.ID " + "AND RTC.ID = '" + tcID + "' " + opt + "ORDER BY TS.FLOW ASC;");

	}

	public void deleteRunTestcases(String ID) {

		executeUpdateQuery("DELETE FROM TEST_DATA WHERE RUN_ID = (SELECT RUN_ID FROM RUN_TESTCASES WHERE ID ='" + ID
				+ "') AND TESTCASE_ID=(SELECT TESTCASE_ID FROM RUN_TESTCASES WHERE ID ='" + ID + "');"
				+ "DELETE FROM RUN_STEPS WHERE RUN_TESTCASE_ID = '" + ID + "';" + "DELETE FROM RUN_TESTCASES WHERE ID='"
				+ ID + "';");

	}

	public String getStepIdFromRun(String rsId) {
		List<String[]> id = executeSelectQuery("SELECT STEP_ID FROM RUN_STEPS WHERE ID ='" + rsId + "'");
		for (String[] array : id) {
			return array[0];
		}
		return "";
	}

	public void deleteRunModule(String ID, String modId) {

		executeUpdateQuery("DELETE FROM TEST_DATA WHERE RUN_ID= '" + ID
				+ "' AND TESTCASE_ID IN (SELECT TESTCASE_ID FROM RUN_TESTCASES WHERE RUN_ID='" + ID + "' "
				+ "AND TESTCASE_ID IN (SELECT ID FROM TESTCASES WHERE MODULE_ID = '" + modId + "'));"
				+ "DELETE FROM RUN_STEPS WHERE RUN_TESTCASE_ID IN(SELECT ID FROM RUN_TESTCASES WHERE RUN_ID='" + ID
				+ "' " + "AND TESTCASE_ID IN (SELECT ID FROM TESTCASES WHERE MODULE_ID = '" + modId + "'));"
				+ "DELETE FROM RUN_TESTCASES WHERE RUN_ID = '" + ID + "'"
				+ " AND TESTCASE_ID IN (SELECT ID FROM TESTCASES WHERE MODULE_ID = '" + modId + "')");

	}

	public void deleteRun(String ID) {

		executeUpdateQuery("DELETE FROM TEST_DATA WHERE RUN_ID= '" + ID
				+ "' AND TESTCASE_ID IN (SELECT TESTCASE_ID FROM RUN_TESTCASES WHERE RUN_ID='" + ID + "');"
				+ "DELETE FROM RUN_STEPS WHERE RUN_TESTCASE_ID IN (SELECT ID FROM RUN_TESTCASES WHERE RUN_ID='" + ID
				+ "');" + "DELETE FROM TEST_RUNS WHERE ID='" + ID + "';" + "DELETE FROM RUN_TESTCASES WHERE RUN_ID = '"
				+ ID + "';" + "DELETE FROM RUN_VARIABLES WHERE RUN_ID = '" + ID + "';");
	}

	public void resetTestRunStatus(String ID) {

		executeUpdateQuery("UPDATE TEST_RUNS SET START_TIME='', END_TIME='',RESULT='Not Started',RESULT_PATH='' WHERE ID='" + ID
						+ "';"
						+ "UPDATE RUN_TESTCASES SET START_TIME='', END_TIME='',RESULT='Not Started',REMARKS='' WHERE RUN_ID='"
						+ ID + "';"
						+ "UPDATE RUN_STEPS SET RESULT='Not Started',REMARKS='' WHERE RUN_TESTCASE_ID IN (SELECT ID FROM RUN_TESTCASES WHERE RUN_ID='"
						+ ID + "')");

	}
	public void resetTestCaseStatus(String ID) {

		executeUpdateQuery("UPDATE RUN_STEPS SET RESULT='Not Started',REMARKS='' "
				+ "WHERE RUN_TESTCASE_ID IN (SELECT ID FROM RUN_TESTCASES WHERE RUN_ID='" + ID + "'AND RESULT <> 'Passed');"
				 + "UPDATE RUN_TESTCASES SET START_TIME='', END_TIME='',RESULT='Not Started',REMARKS='' WHERE RUN_ID='"
						+ ID + "' AND RESULT <> 'Passed';");
	}

	public void addRunModule(String modId, String runId) {

		try {
			List<String[]> tcs = getTestcases(modId);
			for (String[] rowTc : tcs) {
				addRunTescase(rowTc[0], runId);
			}
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			// System.exit(0);
		}

	}

	public void addRunTescase(String tcId, String runId) {
		if (!isTestcaseExist(tcId, runId)) {

			String sql = "INSERT INTO RUN_TESTCASES(RUN_ID,TESTCASE_ID,RESULT,REMARKS,START_TIME,END_TIME) "
					+ "VALUES ('" + runId + "','" + tcId + "','Not Started','','',''); ";
			String stepSql = "";
			String testData = "";
			List<String[]> steps = getSteps(tcId);
			for (String[] rowSteps : steps) {
				// add test case value
				stepSql = stepSql + "((SELECT ID FROM RUN_TESTCASES WHERE RUN_ID='" + runId + "' AND TESTCASE_ID = '"
						+ tcId + "'),'" + tcId + "','" + rowSteps[0] + "','Not Started',''),";
				// create testdata value
				if (rowSteps[7] != null && !rowSteps[7].isEmpty()) {
					testData = testData + "('" + runId + "','" + tcId + "','" + rowSteps[7].replaceAll("'", "''")
							+ "','" + rowSteps[8].replaceAll("'", "''") + "'),";
				}
			}
			if (stepSql != "") {
				stepSql = "INSERT INTO RUN_STEPS(RUN_TESTCASE_ID,TESTCASE_ID,STEP_ID,RESULT,REMARKS) VALUES "
						+ stepSql.substring(0, stepSql.length() - 1) + ";";
				if (testData != "") {
					testData = "INSERT INTO TEST_DATA(RUN_ID,TESTCASE_ID,PARAM_NAME,PARAM_VALUE) VALUES "
							+ testData.substring(0, testData.length() - 1) + ";";
				}
			}
			executeUpdateQuery(sql + stepSql + testData);
		} else {
			System.out.println("RUN: " + runId + " - TC: " + tcId + " is already exist");
		}

	}

	public void pasteTestcase(String sourceId, String targetId) {
		List<String[]> sourceSteps = getSteps(sourceId);
		String stepSql = "";

		List<String[]> countStep = executeSelectQuery(
				"SELECT COUNT(ID) FROM TEST_STEPS WHERE TESTCASE_ID='" + targetId + "'");
		int flow = Integer.parseInt(countStep.get(0)[0]);
		;

		for (String[] rowSteps : sourceSteps) {
			flow = flow + 1;
			stepSql = stepSql + "(" + targetId + "," + flow + "," + "'" + rowSteps[3].replaceAll("'", "''") + "'," + "'"
					+ rowSteps[4].replaceAll("'", "''") + "'," + "'" + rowSteps[5].replaceAll("'", "''") + "'," + "'"
					+ rowSteps[6].replaceAll("'", "''") + "'," + "'" + rowSteps[7].replaceAll("'", "''") + "'," + "'"
					+ rowSteps[8].replaceAll("'", "''") + "'," + "'" + rowSteps[9].replaceAll("'", "''") + "'),";
		}

		if (stepSql != "") {
			executeUpdateQuery(
					"INSERT INTO TEST_STEPS(TESTCASE_ID,FLOW,DESCRIPTION,ELEMENT_SELECTOR,ELEMENT_STRING,ACTION,PARAM_NAME,PARAM_VALUE,SCREENCAPTURE) VALUES "
							+ stepSql.substring(0, stepSql.length() - 1) + ";");
		}

	}

	public void pasteSteps(String sourceId, String targetId, int rowindex) {
		List<String[]> sourceSteps = getStepsIN(sourceId);
		String stepSql = "";

		List<String[]> steps = executeSelectQuery(
				"SELECT ID,TESTCASE_ID,FLOW,DESCRIPTION,ELEMENT_SELECTOR,ELEMENT_STRING,ACTION,PARAM_NAME,PARAM_VALUE,SCREENCAPTURE FROM TEST_STEPS "
						+ " WHERE TESTCASE_ID='" + targetId + "' AND FLOW > " + rowindex + " ORDER BY FLOW ASC;");

		for (String[] rowSteps : sourceSteps) {
			rowindex = rowindex + 1;
			stepSql = stepSql + "(" + targetId + "," + rowindex + "," + "'" + rowSteps[3].replaceAll("'", "''") + "',"
					+ "'" + rowSteps[4].replaceAll("'", "''") + "'," + "'" + rowSteps[5].replaceAll("'", "''") + "',"
					+ "'" + rowSteps[6].replaceAll("'", "''") + "'," + "'" + rowSteps[7].replaceAll("'", "''") + "',"
					+ "'" + rowSteps[8].replaceAll("'", "''") + "'," + "'" + rowSteps[9].replaceAll("'", "''") + "'),";
		}

		if (stepSql != "") {
			executeUpdateQuery(
					"INSERT INTO TEST_STEPS(TESTCASE_ID,FLOW,DESCRIPTION,ELEMENT_SELECTOR,ELEMENT_STRING,ACTION,PARAM_NAME,PARAM_VALUE,SCREENCAPTURE) VALUES "
							+ stepSql.substring(0, stepSql.length() - 1) + ";");
		}

		String whenQ = "";
		String id = "";
		for (String[] step : steps) {
			rowindex = rowindex + 1;
			whenQ = whenQ + " WHEN ID ='" + step[0] + "' THEN '" + rowindex + "' ";
			id = id + step[0] + ",";
		}
		if (whenQ != "") {
			executeUpdateQuery("UPDATE TEST_STEPS  SET FLOW = CASE " + whenQ + " ELSE FLOW END WHERE ID IN ("
					+ id.substring(0, id.length() - 1) + ");");
		}

	}

	// get test steps based on step id
	public List<String[]> getStepsIN(String stepId) {

		return executeSelectQuery(
				"SELECT ID,TESTCASE_ID,FLOW,DESCRIPTION,ELEMENT_SELECTOR,ELEMENT_STRING,ACTION,PARAM_NAME,PARAM_VALUE,SCREENCAPTURE FROM TEST_STEPS "
						+ "WHERE ID IN (" + stepId + ") ORDER BY FLOW ASC");

	}
	// get test steps based on step id
	public List<String[]> getTcIN(String tcId) {

		return executeSelectQuery(
				"SELECT ID,NAME,DESCRIPTION,FLOW,MODULE_ID FROM TESTCASES "
						+ "WHERE ID IN (" + tcId + ") ORDER BY FLOW ASC");

	}

	public void updateTestRun(String runId, int col, String value) {

		String dbColumn = null;
		switch (col) {
		case 1:
			dbColumn = "NAME";
			break;
		case 2:
			dbColumn = "BROWSER";
			break;
		case 7:
			dbColumn = "USE_TEST_DATA";
			break;
		}
		value = value.replaceAll("'", "''");
		executeUpdateQuery("UPDATE TEST_RUNS SET " + dbColumn + "='" + value + "' WHERE ID='" + runId + "'");

	}

	public void updateTestRunStartTime(String runId, String value) {

		value = value.replaceAll("'", "''");
		executeUpdateQuery("UPDATE TEST_RUNS SET START_TIME='" + value + "' WHERE ID='" + runId + "'");

	}

	public void updateTestRunEndTime(String runId, String value) {

		value = value.replaceAll("'", "''");
		executeUpdateQuery("UPDATE TEST_RUNS SET END_TIME='" + value + "' WHERE ID='" + runId + "'");

	}

	public void updateTestRunResultPath(String runId, String value) {

		value = value.replaceAll("'", "''");
		executeUpdateQuery("UPDATE TEST_RUNS SET RESULT_PATH='" + value + "' WHERE ID='" + runId + "'");

	}

	public void updateTestRunResult(String runId, String value) {

		value = value.replaceAll("'", "''");
		executeUpdateQuery("UPDATE TEST_RUNS SET RESULT='" + value + "' WHERE ID='" + runId + "'");

	}

	public void updateRunTestcasesStatus(String ID, String value) {

		value = value.replaceAll("'", "''");
		executeUpdateQuery("UPDATE RUN_TESTCASES SET RESULT='" + value + "' WHERE ID='" + ID + "'");

	}

	public void updateRunTestcasesRemarks(String ID, String value) {

		value = value.replaceAll("'", "''");
		executeUpdateQuery("UPDATE RUN_TESTCASES SET REMARKS='" + value + "' WHERE ID='" + ID + "'");

	}

	public void updateRunTestcasesStartTime(String ID, String value) {

		value = value.replaceAll("'", "''");
		executeUpdateQuery("UPDATE RUN_TESTCASES SET START_TIME='" + value + "' WHERE ID='" + ID + "'");

	}

	public void updateRunTestcasesEndTime(String ID, String value) {

		value = value.replaceAll("'", "''");
		executeUpdateQuery("UPDATE RUN_TESTCASES SET END_TIME='" + value + "' WHERE ID='" + ID + "'");

	}

	public void updateRunStepsResult(String ID, String value, String remarks) {

		value = value.replaceAll("'", "''");
		remarks = remarks.replaceAll("'", "''");
		executeUpdateQuery(
				"UPDATE RUN_STEPS SET RESULT='" + value + "', REMARKS='" + remarks + "' WHERE ID='" + ID + "'");

	}

	public long insertTestRun(String name) {

		long id = executeUpdateQuery(
				"INSERT INTO TEST_RUNS(NAME,BROWSER,RESULT,START_TIME,END_TIME,USE_TEST_DATA,RESULT_PATH) " + "VALUES('"
						+ name.replaceAll("'", "''") + "','Chrome','Not Started','','','Yes','');");
		return id;
	}

	public void insertRunVarSelected(String id, long rId) {
		List<String[]> varList = getVariablesIN(id);
		String val = "";
		for (String[] var : varList) {
			val = val + "(" + rId + "," + "'" + var[1].replaceAll("'", "''") + "'," + "'" + var[2].replaceAll("'", "''")
					+ "','0'),";
		}

		if (val != "") {
			executeUpdateQuery(
					"INSERT INTO RUN_VARIABLES(RUN_ID,NAME,VALUE,ENCRYPT) VALUES " + val.substring(0, val.length() - 1) + ";");
		}
	}

	public String getRunStatus(String id) {
		List<String[]> status = executeSelectQuery("SELECT RESULT FROM TEST_RUNS WHERE ID ='" + id + "'");
		for (String[] array : status) {
			return array[0];
		}
		return "";
	}

	public String getRunName(String id) {
		List<String[]> status = executeSelectQuery("SELECT NAME FROM TEST_RUNS WHERE ID ='" + id + "'");
		for (String[] array : status) {
			return array[0];
		}
		return "";
	}

	public String getRunBrowser(String id) {
		List<String[]> status = executeSelectQuery("SELECT BROWSER FROM TEST_RUNS WHERE ID ='" + id + "'");
		for (String[] array : status) {
			return array[0];
		}
		return "";
	}

	public String getStartTime(String id) {
		List<String[]> status = executeSelectQuery("SELECT START_TIME FROM TEST_RUNS WHERE ID ='" + id + "'");
		for (String[] array : status) {
			return array[0];
		}
		return "";
	}

	public String getEndTime(String id) {
		List<String[]> status = executeSelectQuery("SELECT END_TIME FROM TEST_RUNS WHERE ID ='" + id + "'");
		for (String[] array : status) {
			return array[0];
		}
		return "";
	}

	public boolean getRunUseTestData(String id) {
		List<String[]> status = executeSelectQuery(
				"SELECT USE_TEST_DATA FROM TEST_RUNS WHERE ID = (SELECT RUN_ID FROM RUN_TESTCASES WHERE ID = '" + id
						+ "')");

		for (String[] array : status) {
			if (array[0].equalsIgnoreCase("Yes")) {
				return true;
			}
		}
		return false;
	}

	public boolean testRunHasFailed(String id) {
		List<String[]> status = executeSelectQuery(
				"SELECT RESULT FROM RUN_TESTCASES WHERE RUN_ID = '" + id + "' AND RESULT ='Failed'");

		for (String[] array : status) {
			if (array[0].equalsIgnoreCase("Failed")) {
				return true;
			}
		}
		return false;
	}

	public boolean getTestRunUseTestData(String id) {
		List<String[]> status = executeSelectQuery("SELECT USE_TEST_DATA FROM TEST_RUNS WHERE ID = '" + id + "'");

		for (String[] array : status) {
			if (array[0].equalsIgnoreCase("Yes")) {
				return true;
			}
		}
		return false;
	}

	public String getTcIdFronRunId(String id) {
		List<String[]> status = executeSelectQuery(
				"SELECT TESTCASE_ID FROM RUN_STEPS WHERE RUN_TESTCASE_ID ='" + id + "'");
		for (String[] array : status) {
			return array[0];
		}
		return "";
	}

	public boolean isTestcaseExist(String tcId, String runId) {

		Statement stmt = null;
		try {
			stmt = dbConn.createStatement();
			String sql = "SELECT COUNT(ID) FROM RUN_TESTCASES WHERE RUN_ID='" + runId + "' AND TESTCASE_ID='" + tcId
					+ "' ORDER BY ID DESC";
			ResultSet rs = stmt.executeQuery(sql);
			int tcCount = Integer.parseInt(rs.getString(1));
			stmt.close();
			if (tcCount > 0) {
				return true;
			}
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
		return false;

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

	public long executeUpdateQuery(String sql) {
		long id = 0;
		try {
			Statement stmt = dbConn.createStatement();
			stmt.executeUpdate(sql);
			id = stmt.getGeneratedKeys().getLong(1);
			stmt.close();

		} catch (Exception e) {
			System.out.println(e.getClass().getName() + ": " + e.getMessage());
		}
		return id;
	}

	public List<String[]> getModularActions() {
		return executeSelectQuery("SELECT DISTINCT(ACTION_ID) FROM MODULAR_ACTION ORDER BY ACTION_ID ASC;");
	}

	public List<String[]> getModularSteps(String actionId) {
		return executeSelectQuery(
				"SELECT ID,FLOW,DESCRIPTION,ELEMENT_SELECTOR,ELEMENT_STRING,ACTION,PARAM_NAME,PARAM_VALUE,SCREENCAPTURE FROM MODULAR_ACTION "
						+ "WHERE ACTION_ID = '" + actionId + "' ORDER BY FLOW ASC");
	}
	public List<String[]> getModularStepsByActionId(String actionId) {
		return executeSelectQuery(
				"SELECT ID,FLOW,DESCRIPTION,ELEMENT_SELECTOR,ELEMENT_STRING,ACTION,PARAM_NAME,PARAM_VALUE,SCREENCAPTURE,ACTION_ID FROM MODULAR_ACTION "
						+ "WHERE ACTION_ID IN (" +  actionId.substring(0, actionId.length() - 1) + ") ORDER BY FLOW ASC");
	}

	public List<String[]> getModularStepsIN(String id) {
		return executeSelectQuery(
				"SELECT ID,FLOW,DESCRIPTION,ELEMENT_SELECTOR,ELEMENT_STRING,ACTION,PARAM_NAME,PARAM_VALUE,SCREENCAPTURE FROM MODULAR_ACTION "
						+ "WHERE ID IN(" + id + ")  ORDER BY FLOW ASC");
	}

	public List<String[]> getModularStepsNotIn(String actionId, String id) {
		return executeSelectQuery(
				"SELECT ID,FLOW,DESCRIPTION,ELEMENT_SELECTOR,ELEMENT_STRING,ACTION,PARAM_NAME,PARAM_VALUE,SCREENCAPTURE FROM MODULAR_ACTION "
						+ "WHERE ACTION_ID = '" + actionId + "' AND ID NOT IN(" + id + ") ORDER BY FLOW ASC");
	}

	public void updateModActStep(String ID, int col, String value) {

		String dbColumn = null;
		switch (col) {
		case 1:
			dbColumn = "FLOW";
			break;
		case 2:
			dbColumn = "DESCRIPTION";
			break;
		case 3:
			dbColumn = "ELEMENT_SELECTOR";
			break;
		case 4:
			dbColumn = "ELEMENT_STRING";
			break;
		case 5:
			dbColumn = "ACTION";
			break;
		case 6:
			dbColumn = "PARAM_NAME";
			break;
		case 7:
			dbColumn = "PARAM_VALUE";
			break;
		case 8:
			dbColumn = "SCREENCAPTURE";
			break;
		}
		value = value.replaceAll("'", "''");
		executeUpdateQuery("UPDATE MODULAR_ACTION SET " + dbColumn + "='" + value + "' WHERE ID='" + ID + "'");
	}

	public void updateModularActionName(String oldName, String newName) {
		newName = newName.replaceAll("'", "''");
		executeUpdateQuery("UPDATE MODULAR_ACTION SET ACTION_ID='" + newName + "' WHERE ACTION_ID='" + oldName + "'");
	}

	public void createModularActionTable() {
		executeUpdateQuery(
				"CREATE TABLE IF NOT EXISTS MODULAR_ACTION ([ID] INTEGER  PRIMARY KEY AUTOINCREMENT NOT NULL,[FLOW] INTEGER  NULL,"
						+ "[DESCRIPTION] TEXT  NULL,[ELEMENT_SELECTOR] TEXT  NULL,[ELEMENT_STRING] TEXT  NULL,[ACTION] TEXT  NULL,[PARAM_NAME] TEXT  NULL,"
						+ "[PARAM_VALUE] TEXT  NULL,[SCREENCAPTURE] TEXT  NULL,[ACTION_ID] TEXT  NULL)");
	}

	public void addModularActionName(String name) {
		name = name.replaceAll("'", "''");
		executeUpdateQuery(
				"INSERT INTO MODULAR_ACTION ('FLOW','DESCRIPTION','ELEMENT_SELECTOR','ELEMENT_STRING','ACTION','PARAM_NAME','PARAM_VALUE','SCREENCAPTURE','ACTION_ID')"
						+ " VALUES ('1','','','','','','','No','" + name + "');");
	}

	public void addModularActionStep(String name) {
		name = name.replaceAll("'", "''");
		executeUpdateQuery(
				"INSERT INTO MODULAR_ACTION ('FLOW','DESCRIPTION','ELEMENT_SELECTOR','ELEMENT_STRING','ACTION','PARAM_NAME','PARAM_VALUE','SCREENCAPTURE','ACTION_ID')"
						+ " VALUES ((SELECT MAX(FLOW) FROM MODULAR_ACTION WHERE ACTION_ID='" + name
						+ "') + 1,'','','','','','','No','" + name + "');");
	}
	public void addModularActions(List<String[]> mods) {
		String stepSql = "";

		for (String[] rowSteps : mods) {
			stepSql = stepSql + "(" + rowSteps[1] + "," + "'" + rowSteps[2].replaceAll("'", "''") + "'," + "'"
					+ rowSteps[3].replaceAll("'", "''") + "'," + "'" + rowSteps[4].replaceAll("'", "''") + "'," + "'"
					+ rowSteps[5].replaceAll("'", "''") + "'," + "'" + rowSteps[6].replaceAll("'", "''") + "'," + "'"
					+ rowSteps[7].replaceAll("'", "''") + "'," + "'" + rowSteps[8].replaceAll("'", "''") + "'," + "'"
					+ rowSteps[9].replaceAll("'", "''") + "'),";
		}

		if (stepSql != "") {
			executeUpdateQuery(
					"INSERT INTO MODULAR_ACTION(FLOW,DESCRIPTION,ELEMENT_SELECTOR,ELEMENT_STRING,ACTION,PARAM_NAME,PARAM_VALUE,SCREENCAPTURE,ACTION_ID) VALUES "
							+ stepSql.substring(0, stepSql.length() - 1) + ";");
		}
	}

	public void deleteModularActionName(String name) {
		name = name.replaceAll("'", "''");
		executeUpdateQuery("DELETE FROM MODULAR_ACTION WHERE ACTION_ID = '" + name + "';");
	}

	public void deleteModularActionSteps(String ids, String modName) {

		if (ids.equalsIgnoreCase("")) {
			return;
		}
		executeUpdateQuery("DELETE FROM MODULAR_ACTION WHERE ID IN (" + ids + ");");

		List<String[]> steps = executeSelectQuery(
				"SELECT ID,FLOW FROM MODULAR_ACTION WHERE ACTION_ID='" + modName + "' ORDER BY FLOW ASC");
		String whenQ = "";
		int i = 0;
		String id = "";
		for (String[] step : steps) {
			i = i + 1;
			whenQ = whenQ + " WHEN ID ='" + step[0] + "' THEN '" + i + "' ";
			id = id + step[0] + ",";
		}
		if (whenQ != "") {
			executeUpdateQuery("UPDATE MODULAR_ACTION  SET FLOW = CASE " + whenQ + " ELSE FLOW END WHERE ID IN ("
					+ id.substring(0, id.length() - 1) + ");");
		}

	}

	public void insertModularActionStep(String sId, String name) {
		List<String[]> steps = executeSelectQuery("SELECT ID,FLOW FROM MODULAR_ACTION " + "WHERE ACTION_ID='" + name
				+ "' AND FLOW >= (SELECT FLOW FROM MODULAR_ACTION WHERE ID='" + sId + "') ORDER BY FLOW ASC");

		executeUpdateQuery(
				"INSERT INTO MODULAR_ACTION ('FLOW','DESCRIPTION','ELEMENT_SELECTOR','ELEMENT_STRING','ACTION','PARAM_NAME','PARAM_VALUE','SCREENCAPTURE','ACTION_ID')"
						+ " VALUES ((SELECT FLOW FROM MODULAR_ACTION WHERE ID='" + sId + "'),'','','','','','','No','"
						+ name + "');");

		String whenQ = "";
		String id = "";
		for (String[] step : steps) {
			whenQ = whenQ + " WHEN ID ='" + step[0] + "' THEN '" + (Integer.parseInt(step[1]) + 1) + "' ";
			id = id + step[0] + ",";
		}
		if (whenQ != "") {
			executeUpdateQuery("UPDATE MODULAR_ACTION  SET FLOW = CASE " + whenQ + " ELSE FLOW END WHERE ID IN ("
					+ id.substring(0, id.length() - 1) + ");");
		}
	}

	public void pasteModularActionStep(String sourceId, String targetId, int rowindex) {
		List<String[]> sourceSteps = getModularStepsIN(sourceId);
		String stepSql = "";

		List<String[]> steps = executeSelectQuery(
				"SELECT ID,FLOW,DESCRIPTION,ELEMENT_SELECTOR,ELEMENT_STRING,ACTION,PARAM_NAME,PARAM_VALUE,SCREENCAPTURE FROM MODULAR_ACTION "
						+ " WHERE ACTION_ID='" + targetId + "' AND FLOW > " + rowindex + " ORDER BY FLOW ASC;");

		for (String[] rowSteps : sourceSteps) {
			rowindex = rowindex + 1;
			stepSql = stepSql + "(" + rowindex + "," + "'" + rowSteps[2].replaceAll("'", "''") + "'," + "'"
					+ rowSteps[3].replaceAll("'", "''") + "'," + "'" + rowSteps[4].replaceAll("'", "''") + "'," + "'"
					+ rowSteps[5].replaceAll("'", "''") + "'," + "'" + rowSteps[6].replaceAll("'", "''") + "'," + "'"
					+ rowSteps[7].replaceAll("'", "''") + "'," + "'" + rowSteps[8].replaceAll("'", "''") + "'," + "'"
					+ targetId.replaceAll("'", "''") + "'),";
		}

		if (stepSql != "") {
			executeUpdateQuery(
					"INSERT INTO MODULAR_ACTION(FLOW,DESCRIPTION,ELEMENT_SELECTOR,ELEMENT_STRING,ACTION,PARAM_NAME,PARAM_VALUE,SCREENCAPTURE,ACTION_ID) VALUES "
							+ stepSql.substring(0, stepSql.length() - 1) + ";");
		}

		String whenQ = "";
		String id = "";
		for (String[] step : steps) {
			rowindex = rowindex + 1;
			whenQ = whenQ + " WHEN ID ='" + step[0] + "' THEN '" + rowindex + "' ";
			id = id + step[0] + ",";
		}
		if (whenQ != "") {
			executeUpdateQuery("UPDATE MODULAR_ACTION  SET FLOW = CASE " + whenQ + " ELSE FLOW END WHERE ID IN ("
					+ id.substring(0, id.length() - 1) + ");");
		}

	}

	public void createVariableTable() {
		executeUpdateQuery(
				"CREATE TABLE IF NOT EXISTS VARIABLES ([ID] INTEGER PRIMARY KEY  AUTOINCREMENT, [NAME] TEXT, [VALUE] TEXT);"
				+ "CREATE  TABLE IF NOT EXISTS RUN_VARIABLES ([ID] INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL , [RUN_ID] TEXT, [NAME] TEXT, [VALUE] TEXT, [ENCRYPT] TEXT);");
	}

	public List<String[]> getVariables(String id) {
		String name = "";
		String query = "";
		List<String[]> rVar = getRunVariables(id);
		for (String[] var : rVar) {
			name = name + "'" + var[1] + "',";
		}
		if (name.length() > 0) {
			query = "SELECT ID,NAME,VALUE FROM VARIABLES WHERE NAME NOT IN(" + name.substring(0, name.length() - 1)
					+ ") ORDER BY NAME ASC";
		} else {
			query = "SELECT ID,NAME,VALUE FROM VARIABLES ORDER BY NAME ASC";
		}
		return executeSelectQuery(query);
	}

	public List<String[]> getVariablesIN(String id) {
		return executeSelectQuery("SELECT ID,NAME,VALUE FROM VARIABLES WHERE ID IN (" + id + ") ORDER BY NAME ASC");
	}

	public List<String[]> getRunVariables(String rId) {
		return executeSelectQuery("SELECT ID,NAME,VALUE,ENCRYPT FROM RUN_VARIABLES WHERE RUN_ID='" + rId + "' ORDER BY NAME ASC");
	}
	public List<String[]> getRunVariablesIN(String id) {
		return executeSelectQuery("SELECT ID,NAME,VALUE,ENCRYPT FROM RUN_VARIABLES WHERE ID IN (" + id + ") ORDER BY NAME ASC");
	}

	public void addVariableName(String name) {
		name = name.replaceAll("'", "''");
		executeUpdateQuery("INSERT INTO VARIABLES(NAME,VALUE) VALUES('" + name + "','');");
	}

	public List<String[]> getVariableNotIn(String name, String id) {
		return executeSelectQuery("SELECT ID,NAME,VALUE FROM VARIABLES WHERE NAME='" + name + "' AND ID NOT IN(" + id
				+ ") ORDER BY NAME ASC");
	}

	public void updateVariableValue(String value, String id) {
		value = value.replaceAll("'", "''");
		executeUpdateQuery("UPDATE VARIABLES SET VALUE='" + value + "' WHERE ID = '" + id + "';");
	}

	public void updateRunVariableValue(String value, String id) {
		value = value.replaceAll("'", "''");
		executeUpdateQuery("UPDATE RUN_VARIABLES SET VALUE='" + value + "' WHERE ID = '" + id + "';");
	}

	public void updateVariableName(String name, String id) {
		name = name.replaceAll("'", "''");
		executeUpdateQuery("UPDATE VARIABLES SET NAME='" + name + "' WHERE ID = '" + id + "';");
	}

	public void deleteVariables(String id) {

		executeUpdateQuery("DELETE FROM VARIABLES WHERE ID IN (" + id + ");");

	}

	public void deleteRunVariables(String id) {

		executeUpdateQuery("DELETE FROM RUN_VARIABLES WHERE ID IN (" + id + ");");

	}
	public void encryptRunVariables(String id) {
		List<String[]> vars = getRunVariablesIN(id);
		String whenQ = "";
		String ids = "";
		for(String[] var : vars) {
			if(var[3]==null || !var[3].contentEquals("1")) {
				whenQ = whenQ + " WHEN ID ='" + var[0] + "' THEN '" + encrypt.createEncryptedPassword(var[2]) + "' ";
				ids = ids + var[0] + ",";
			}
		}

		if (whenQ != "") {
			executeUpdateQuery("UPDATE RUN_VARIABLES  SET VALUE = CASE " + whenQ + " ELSE VALUE END WHERE ID IN ("
					+ ids.substring(0, ids.length() - 1) + ");");
		}
		
		executeUpdateQuery("UPDATE RUN_VARIABLES SET ENCRYPT='1' WHERE ID IN (" + id + ");");

	}
	public void importSteps(List<String[]> stepsSource, String tcId,int rowindex) {
		if(rowindex < 0) {
			List<String[]> count = executeSelectQuery("SELECT ID FROM TEST_STEPS WHERE TESTCASE_ID='" + tcId + "'");
			rowindex = count.size();
		}
		List<String[]> steps = executeSelectQuery(
				"SELECT ID,TESTCASE_ID,FLOW,DESCRIPTION,ELEMENT_SELECTOR,ELEMENT_STRING,ACTION,PARAM_NAME,PARAM_VALUE,SCREENCAPTURE FROM TEST_STEPS "
						+ " WHERE TESTCASE_ID='" + tcId + "' AND FLOW > " + rowindex + " ORDER BY FLOW ASC;");
		
		String stepSql = "";
		for (String[] rowSteps : stepsSource) {
			rowindex = rowindex + 1;
			stepSql = stepSql + "(" + tcId + "," + rowindex + "," + "'" + rowSteps[3].replaceAll("'", "''") + "',"
					+ "'" + rowSteps[4].replaceAll("'", "''") + "'," + "'" + rowSteps[5].replaceAll("'", "''") + "',"
					+ "'" + rowSteps[6].replaceAll("'", "''") + "'," + "'" + rowSteps[7].replaceAll("'", "''") + "',"
					+ "'" + rowSteps[8].replaceAll("'", "''") + "'," + "'" + rowSteps[9].replaceAll("'", "''") + "'),";
		}
		if (stepSql != "") {
			executeUpdateQuery(
					"INSERT INTO TEST_STEPS(TESTCASE_ID,FLOW,DESCRIPTION,ELEMENT_SELECTOR,ELEMENT_STRING,ACTION,PARAM_NAME,PARAM_VALUE,SCREENCAPTURE) VALUES "
							+ stepSql.substring(0, stepSql.length() - 1) + ";");
		}

		String whenQ = "";
		String id = "";
		for (String[] step : steps) {
			rowindex = rowindex + 1;
			whenQ = whenQ + " WHEN ID ='" + step[0] + "' THEN '" + rowindex + "' ";
			id = id + step[0] + ",";
		}
		if (whenQ != "") {
			executeUpdateQuery("UPDATE TEST_STEPS  SET FLOW = CASE " + whenQ + " ELSE FLOW END WHERE ID IN ("
					+ id.substring(0, id.length() - 1) + ");");
		}
	}
	public long importTestcase(List<String[]> tcSource, String modId,int rowindex) {
		long newId = 0;
		if(rowindex < 0) {
			List<String[]> count = executeSelectQuery("SELECT ID FROM TESTCASES WHERE MODULE_ID = '"+modId+"'");
			rowindex = count.size();
		}
		List<String[]> tcs = executeSelectQuery(
				"SELECT ID,NAME,DESCRIPTION,FLOW,MODULE_ID FROM TESTCASES "
						+ " WHERE MODULE_ID = '" + modId + "' AND  FLOW > " + rowindex + " ORDER BY FLOW ASC;");
		
		String tcSql = "";
		for (String[] rowtc : tcSource) {
			rowindex = rowindex + 1;
			tcSql = tcSql + "('" + rowtc[1].replaceAll("'", "''") + "','"
					+ rowtc[2].replaceAll("'", "''") + "','" + rowindex + "','"
					+ modId + "'),";
		}
		if (tcSql != "") {
			newId = executeUpdateQuery(
					"INSERT INTO TESTCASES(NAME,DESCRIPTION,FLOW,MODULE_ID) VALUES "
							+ tcSql.substring(0, tcSql.length() - 1) + ";");
		}

		String whenQ = "";
		String id = "";
		for (String[] tc : tcs) {
			rowindex = rowindex + 1;
			whenQ = whenQ + " WHEN ID ='" + tc[0] + "' THEN '" + rowindex + "' ";
			id = id + tc[0] + ",";
		}
		if (whenQ != "") {
			executeUpdateQuery("UPDATE TESTCASES  SET FLOW = CASE " + whenQ + " ELSE FLOW END WHERE ID IN ("
					+ id.substring(0, id.length() - 1) + ");");
		}
		return newId;
	}
	public long importModule(List<String[]> modSource, String modId,int rowindex) {
		long newId = 0;
		if(rowindex < 0) {
			List<String[]> count = executeSelectQuery("SELECT ID FROM MODULES;");
			rowindex = count.size();
		}
		List<String[]> mods = executeSelectQuery(
				"SELECT ID,NAME,DESCRIPTION,FLOW FROM MODULES "
						+ " WHERE  FLOW > " + rowindex + " ORDER BY FLOW ASC;");
		
		String modSql = "";
		for (String[] rowMod : modSource) {
			rowindex = rowindex + 1;
			modSql = modSql + "('" + rowMod[1].replaceAll("'", "''") + "','"
					+ rowMod[2].replaceAll("'", "''") + "','" + rowindex + "'),";
		}
		if (modSql != "") {
			newId = executeUpdateQuery(
					"INSERT INTO MODULES(NAME,DESCRIPTION,FLOW) VALUES "
							+ modSql.substring(0, modSql.length() - 1) + ";");
		}

		String whenQ = "";
		String id = "";
		for (String[] mod : mods) {
			rowindex = rowindex + 1;
			whenQ = whenQ + " WHEN ID ='" + mod[0] + "' THEN '" + rowindex + "' ";
			id = id + mod[0] + ",";
		}
		if (whenQ != "") {
			executeUpdateQuery("UPDATE MODULES  SET FLOW = CASE " + whenQ + " ELSE FLOW END WHERE ID IN ("
					+ id.substring(0, id.length() - 1) + ");");
		}
		return newId;
	}
}
