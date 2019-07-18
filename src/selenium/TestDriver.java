package selenium;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;

import database.DBProcess;
import database.PasswordEncryptDecrypt;
import database.TestMachineConfig;
import userInterface.EmailClass;
import userInterface.ExcelClass;

public class TestDriver {

	public TestDriver() {

	}

	private TestMachineConfig dbConfig = new TestMachineConfig();
	private PasswordEncryptDecrypt encrypt = new PasswordEncryptDecrypt();

	public void executeTestRun(String logName, String runId, String browser, DBProcess dbProcess) throws IOException {
		boolean useTestData = dbProcess.getTestRunUseTestData(runId);

		String appPath = new File("").getAbsolutePath();
		String fileDir = appPath + "\\Test Result\\" + logName;
		// generate log file
		String logFileName = fileDir + "\\" + logName + ".log";
		RollingFileAppender fa = new RollingFileAppender();
		fa.setLayout(new PatternLayout("%d{dd MMM yyyy HH:mm:ss} %5p %c{1}:%L %t - %m%n"));
		fa.setFile(logFileName);
		fa.setAppend(false);
		fa.activateOptions();
		fa.setName(logName);
		fa.setThreshold(Level.INFO);
		Logger.getRootLogger().addAppender(fa);
		Logger Log = Logger.getLogger(logName);
		
		Log.setAdditivity(false);
		Log.addAppender(fa);
		
		Log.info("Start Execution : " + runId);
		SeleniumDriver selDriver = new SeleniumDriver();
		selDriver.log = Log;
		selDriver.dbProcess = dbProcess;
		selDriver.webDriver = selDriver.openWebDriver(browser);
		selDriver.selDriver = selDriver;
		selDriver.browser = browser;
		
		if (selDriver.webDriver !=null) {
			try {
				List<String[]> varList = dbProcess.getRunVariables(runId);
				Log.info("====VARIABLES===");
				for (String[] var : varList) {
					if(!var[3].contentEquals("1")) {
						selDriver.variables.put(var[1], var[2]);
						Log.info(var[1] + ":" + var[2]);
					} else {
						selDriver.encryptedVariables.put(var[1], "1");
						selDriver.variables.put(var[1], encrypt.decryptPassword(var[2]));
						Log.info(var[1] + ":(encrypted)");
					}
				}
			} catch (Exception e) {
				Log.error("ERROR Loading variables.");
			}
			dbProcess.updateTestRunResult(runId, "Ongoing");
			dbProcess.updateTestRunResultPath(runId, fileDir);
			dbProcess.updateTestRunStartTime(runId, getDateTime());
	
			List<String[]> modList = dbProcess.getRunModules(runId);
			for (String[] module : modList) {
				Log.info("========================================================");
				Log.info("Executing Module ID : " + module[0]);
				Log.info("Executing Module Name : " + module[1]);
				Log.info("Executing Module Description : " + module[2]);
				List<String[]> tcList = dbProcess.getRunTestcases(module[0], runId);
				for (String[] testCase : tcList) {
					Log.info("===================================================");
					Log.info("TestCase ID : " + testCase[0]);
					Log.info("TestCase Name : " + testCase[1]);
					Log.info("TestCase Description : " + testCase[2]);
					dbProcess.updateRunTestcasesStartTime(testCase[0], getDateTime());
					dbProcess.updateRunTestcasesStatus(testCase[0], "Ongoing");
	
					List<String[]> stepList;
					if (useTestData) {
						stepList = dbProcess.getRunTestStepsWithTestData(testCase[0], "");
					} else {
						stepList = dbProcess.getRunTestSteps(testCase[0], "");
					}
					String[] stepResult = new String[2];
					String stepSkip = "";
					int skip = 0;
					for (String[] step : stepList) {
	
						Log.info("-----------------------------------------------");
						Log.info("Step ID : " + step[0]);
						Log.info("Step Description : " + step[2]);
						Log.info("Element : " + step[3] + ":" + step[4]);
						Log.info("Action : " + step[5]);
						Log.info("Param Name: " + step[6]);
						Log.info("Param Value: " + step[7]);
						Log.info("Screenshot : " + step[8]);
						stepResult[0] = "";
						stepResult[1] = "";
						if (skip > 0) {
							dbProcess.updateRunStepsResult(step[0], "Skipped", "Skipped.");
							Log.info("Step Result : Skipped.");
							Log.info("Step Remarks : Skipped.");
							skip = skip - 1;
							stepResult[0] = "Passed";
							continue;
						}
						if (stepSkip.equalsIgnoreCase("Skipped")) {
	
							dbProcess.updateRunStepsResult(step[0], stepSkip, "Prevoius step is to SkipNext.");
							Log.info("Step Result : " + stepSkip);
							Log.info("Step Remarks : Prevoius step is to SkipNext.");
							stepSkip = "";
							stepResult[0] = "Passed";
							continue;
						}
	
						stepResult = selDriver.seleniumAction(step, fileDir, testCase[0] + "_" + testCase[1],
								module[0] + "_" + module[1]);
	
						dbProcess.updateRunStepsResult(step[0], stepResult[0], stepResult[1]);
	
						if (step[5].equalsIgnoreCase("skipNextSteps") && stepResult[0].equalsIgnoreCase("Passed")) {
							skip = Integer.parseInt(step[7]);
						}
	
						Log.info("Step Result : " + stepResult[0]);
						Log.info("Step Remarks : " + stepResult[1]);
						if ((step[5].equalsIgnoreCase("isDisplayedSkipNext")
								|| step[5].equalsIgnoreCase("ifTextContainsSkipNext")
								|| step[5].equalsIgnoreCase("isParamContainsSkipNext"))
								&& stepResult[0].equalsIgnoreCase("SkipNext")) {
							stepSkip = "Skipped";
						}
						if (stepResult[0].equalsIgnoreCase("Failed") && !step[5].contains("verify")) {
							break;
						}
					}
					dbProcess.updateRunTestcasesStatus(testCase[0], stepResult[0]);
					dbProcess.updateRunTestcasesEndTime(testCase[0], getDateTime());
	
				}
	
			}
			if (dbProcess.testRunHasFailed(runId)) {
				dbProcess.updateTestRunResult(runId, "Failed");
			} else {
				dbProcess.updateTestRunResult(runId, "Passed");
			}
	
			dbProcess.updateTestRunEndTime(runId, getDateTime());
			selDriver.exitWebDriver();
	
			if (dbConfig.getConfigValue("GEN_XLSX_RESULT").contentEquals("Y")) {
				try {
					Log.info("Generating excel result...");
					ExcelClass excel = new ExcelClass();
					excel.createExcelFileReport(new File(fileDir + "\\Test Run Summary.xlsx"), runId, dbProcess, false);
					Log.info("Result File: " + fileDir + "\\Test Run Summary.xlsx");
					Log.info("Generate Result Done!.");
				} catch (Exception e) {
					Log.info("Failed to Generate Result!. : " + e.getMessage());
				}
			}
			if (dbConfig.getConfigValue("EMAIL_SEND").contentEquals("Y")) {
				try {
					EmailClass e = new EmailClass();
					e.sendEmail(dbProcess.getRunBrowser(runId) + ":" + dbProcess.getRunName(runId), runId, dbProcess,
							fileDir + "\\Test Run Summary.xlsx");
				} catch (Exception e1) {
					Log.info("Failed to send Email report!. : " + e1.getMessage());
				}
			}
		}
		Log.info("Execution Finished!");
	}
	public void executeTestRunFailed(String logName, String runId, String browser, DBProcess dbProcess) throws IOException {
		boolean useTestData = dbProcess.getTestRunUseTestData(runId);

		String appPath = new File("").getAbsolutePath();
		String fileDir = appPath + "\\Test Result\\" + logName;
		// generate log file
		String logFileName = fileDir + "\\" + logName + ".log";
		RollingFileAppender fa = new RollingFileAppender();
		fa.setLayout(new PatternLayout("%d{dd MMM yyyy HH:mm:ss} %5p %c{1}:%L %t - %m%n"));
		fa.setFile(logFileName);
		fa.setAppend(false);
		fa.setMaxFileSize("100MB");
		fa.setMaxBackupIndex(10);
		fa.activateOptions();
		fa.setThreshold(Level.INFO);
		Logger.getRootLogger().addAppender(fa);
		Logger Log = Logger.getLogger(logName);
		Log.setAdditivity(false);
		Log.addAppender(fa);
		
		//reset status of failed testcases
		dbProcess.resetTestCaseStatus(runId);
		Log.info("Start Execution : " + runId);
		SeleniumDriver selDriver = new SeleniumDriver();
		selDriver.log = Log;
		selDriver.dbProcess = dbProcess;
		selDriver.webDriver = selDriver.openWebDriver(browser);
		selDriver.selDriver = selDriver;
		
		if (selDriver.webDriver !=null) {
			try {
				List<String[]> varList = dbProcess.getRunVariables(runId);
				Log.info("====VARIABLES===");
				for (String[] var : varList) {
					if(!var[3].contentEquals("1")) {
						selDriver.variables.put(var[1], var[2]);
						Log.info(var[1] + ":" + var[2]);
					} else {
						selDriver.variables.put(var[1], encrypt.decryptPassword(var[2]));
						Log.info(var[1] + ":(Encrypted)");
					}
				}
			} catch (Exception e) {
				Log.error("ERROR Loading variables.");
			}
			dbProcess.updateTestRunResult(runId, "Ongoing");
			dbProcess.updateTestRunResultPath(runId, fileDir);
//			dbProcess.updateTestRunStartTime(runId, getDateTime());
	
			List<String[]> modList = dbProcess.getRunModules(runId);
			for (String[] module : modList) {
				Log.info("========================================================");
				Log.info("Executing Module ID : " + module[0]);
				Log.info("Executing Module Name : " + module[1]);
				Log.info("Executing Module Description : " + module[2]);
				List<String[]> tcList = dbProcess.getRunTestcases(module[0], runId);
				for (String[] testCase : tcList) {
					if(testCase[6].contentEquals("Passed")) {
						Log.info("===================================================");
						Log.info("TestCase ID : " + testCase[0]);
						Log.info("TestCase Name : " + testCase[1]);
						Log.info("TestCase Description : " + testCase[2]);
						Log.info("TestCase Status : " + testCase[6]);
						continue;
					}
					Log.info("===================================================");
					Log.info("TestCase ID : " + testCase[0]);
					Log.info("TestCase Name : " + testCase[1]);
					Log.info("TestCase Description : " + testCase[2]);
					dbProcess.updateRunTestcasesStartTime(testCase[0], getDateTime());
					dbProcess.updateRunTestcasesStatus(testCase[0], "Ongoing");
	
					List<String[]> stepList;
					if (useTestData) {
						stepList = dbProcess.getRunTestStepsWithTestData(testCase[0], "");
					} else {
						stepList = dbProcess.getRunTestSteps(testCase[0], "");
					}
					String[] stepResult = new String[2];
					String stepSkip = "";
					int skip = 0;
					for (String[] step : stepList) {
	
						Log.info("-----------------------------------------------");
						Log.info("Step ID : " + step[0]);
						Log.info("Step Description : " + step[2]);
						Log.info("Element : " + step[3] + ":" + step[4]);
						Log.info("Action : " + step[5]);
						Log.info("Param : " + step[7]);
						Log.info("Screenshot : " + step[8]);
						stepResult[0] = "";
						stepResult[1] = "";
						if (skip > 0) {
							dbProcess.updateRunStepsResult(step[0], "Skipped", "Skipped.");
							Log.info("Step Result : Skipped.");
							Log.info("Step Remarks : Skipped.");
							skip = skip - 1;
							stepResult[0] = "Passed";
							continue;
						}
						if (stepSkip.equalsIgnoreCase("Skipped")) {
	
							dbProcess.updateRunStepsResult(step[0], stepSkip, "Prevoius step is to SkipNext.");
							Log.info("Step Result : " + stepSkip);
							Log.info("Step Remarks : Prevoius step is to SkipNext.");
							stepSkip = "";
							stepResult[0] = "Passed";
							continue;
						}
	
						stepResult = selDriver.seleniumAction(step, fileDir, testCase[0] + "_" + testCase[1],
								module[0] + "_" + module[1]);
	
						dbProcess.updateRunStepsResult(step[0], stepResult[0], stepResult[1]);
	
						if (step[5].equalsIgnoreCase("skipNextSteps") && stepResult[0].equalsIgnoreCase("Passed")) {
							skip = Integer.parseInt(step[7]);
						}
	
						Log.info("Step Result : " + stepResult[0]);
						Log.info("Step Remarks : " + stepResult[1]);
						if ((step[5].equalsIgnoreCase("isDisplayedSkipNext")
								|| step[5].equalsIgnoreCase("ifTextContainsSkipNext")
								|| step[5].equalsIgnoreCase("isParamContainsSkipNext"))
								&& stepResult[0].equalsIgnoreCase("SkipNext")) {
							stepSkip = "Skipped";
						}
						if (stepResult[0].equalsIgnoreCase("Failed") && !step[5].contains("verify")) {
							break;
						}
					}
					dbProcess.updateRunTestcasesStatus(testCase[0], stepResult[0]);
					dbProcess.updateRunTestcasesEndTime(testCase[0], getDateTime());
	
				}
	
			}
			if (dbProcess.testRunHasFailed(runId)) {
				dbProcess.updateTestRunResult(runId, "Failed");
			} else {
				dbProcess.updateTestRunResult(runId, "Passed");
			}
	
			dbProcess.updateTestRunEndTime(runId, getDateTime());
			selDriver.exitWebDriver();
	
			if (dbConfig.getConfigValue("GEN_XLSX_RESULT").contentEquals("Y")) {
				try {
					Log.info("Generating excel result...");
					ExcelClass excel = new ExcelClass();
					excel.createExcelFileReport(new File(fileDir + "\\Test Run Summary.xlsx"), runId, dbProcess, false);
					Log.info("Result File: " + fileDir + "\\Test Run Summary.xlsx");
					Log.info("Generate Result Done!.");
				} catch (Exception e) {
					Log.info("Failed to Generate Result!. : " + e.getMessage());
				}
			}
			if (dbConfig.getConfigValue("EMAIL_SEND").contentEquals("Y")) {
				try {
					EmailClass e = new EmailClass();
					e.sendEmail(dbProcess.getRunBrowser(runId) + ":" + dbProcess.getRunName(runId), runId, dbProcess,
							fileDir + "\\Test Run Summary.xlsx");
				} catch (Exception e1) {
					Log.info("Failed to send Email report!. : " + e1.getMessage());
				}
	
			}
		}
		Log.info("Execution Finished!");
	}

	public String getDateTime() {
		SimpleDateFormat df = new SimpleDateFormat("MMM-dd-yyyy HH.mm.ss");
		return df.format(new Date());
	}

}
