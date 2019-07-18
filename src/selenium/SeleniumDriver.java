package selenium;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.swing.JOptionPane;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.support.ui.Select;

import database.DBProcess;
import database.TestMachineConfig;

public class SeleniumDriver {
	public Logger log;
	public DBProcess dbProcess = null;
	private String defaultBorder;
	private String defaultBorderTemp;
	private List<String> defaultBorders;
	public WebDriver webDriver;
	private String strWindowHandler;
	private String testcaseName;
	private String testcaseDir;
	private String moduleName;
	private TestMachineConfig dbConfig = new TestMachineConfig();
	private WebElement element;
	private List<WebElement> elements;
	public Map<String, String> variables = new HashMap<String, String>();
	public Map<String, String> encryptedVariables = new HashMap<String, String>();
	private String paramVal;
	private String elementString;
	public SeleniumDriver selDriver;
	public String browser;

	public SeleniumDriver() {
		
	}

	public WebDriver openWebDriver(String Browser) {
	
		String appPath = new File("").getAbsolutePath();
		WebDriver webDriver = null;
		String userAgent = "";
		if(dbConfig.getConfigValue("CUSTOM_AGENT").contentEquals("Y")) {
			userAgent = dbConfig.getConfigValue("USER_AGENT");
		} else {
			userAgent = dbConfig.getUserAgent();
		}
		try {
			if (Browser.equalsIgnoreCase("firefox")) {
				System.setProperty("webdriver.gecko.driver", appPath + "\\webdriver\\geckodriver.exe");
				FirefoxProfile profile = new FirefoxProfile();
				if(!userAgent.contentEquals("")) {
					profile.setPreference("general.useragent.override", userAgent);
				}
				FirefoxOptions firefoxOptions = new FirefoxOptions().setProfile(profile);
				firefoxOptions.setCapability("marionette", true);
			    firefoxOptions.setCapability("acceptInsecureCerts", true);
				//DesiredCapabilities capabilities = new FirefoxOptions().setProfile(profile).addTo(DesiredCapabilities.firefox());
				//capabilities.setCapability("acceptInsecureCerts", true);
				//capabilities.setCapability("marionette", true);
				//webDriver = new FirefoxDriver(capabilities);
				webDriver = new FirefoxDriver(firefoxOptions);

			}else if (Browser.equalsIgnoreCase("internet explorer")) {
				System.setProperty("webdriver.ie.driver", appPath + "\\webdriver\\IEDriverServer.exe");
				InternetExplorerOptions options = new InternetExplorerOptions();
				webDriver = new InternetExplorerDriver(options);
				//DesiredCapabilities capabilities = new DesiredCapabilities();
				//capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
				//webDriver = new InternetExplorerDriver(capabilities);
				
			}else if (Browser.equalsIgnoreCase("chrome")) {
				System.setProperty("webdriver.chrome.driver", appPath + "\\webdriver\\chromedriver.exe");
				ChromeOptions options = new ChromeOptions();
				options.setAcceptInsecureCerts(true);
			    options.addArguments("ignore-certificate-errors");
				//DesiredCapabilities capabilities = DesiredCapabilities.chrome();
				//capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
				if(!userAgent.contentEquals("")) {
					//ChromeOptions options = new ChromeOptions();
					options.addArguments("--user-agent=" + userAgent);
					//capabilities.setCapability(ChromeOptions.CAPABILITY, options);
				}
				webDriver = new ChromeDriver(options);
			} else {
				log.info("Browser not supported!.");
				return null;
			}
			
			log.info("Start " + Browser + " webdriver");
			if(!userAgent.contentEquals("")) {
				String userAgentSet = (String) ((JavascriptExecutor) webDriver).executeScript("return navigator.userAgent;");
				log.info("User Agent: " + userAgentSet);
			}
			// save main window handle
			strWindowHandler = webDriver.getWindowHandle();
			log.info("New browser session created : " + strWindowHandler);
			webDriver.manage().window().maximize();
			webDriver.manage().timeouts().pageLoadTimeout(Integer.parseInt(dbConfig.getConfigValue("PAGELOAD_TIMEOUT")),TimeUnit.SECONDS);
		} catch (Exception e) {
			log.error("Failed to open WebDriver:" + e.getMessage());
		}
		return webDriver;
	}

	public String[] seleniumAction(String[] step, String fileDir, String testCase, String mod) throws IOException {

		String[] result = new String[2];
		testcaseName = testCase;
		moduleName = mod;
		testcaseDir = fileDir;
		element = null;
		elements = null;
		defaultBorderTemp = "";
		try {
			Thread.sleep(Integer.parseInt(dbConfig.getConfigValue("ACTION_DELAY")) * 1000);
			step = getParameterValue(step);
			Method selAction = selDriver.getClass().getDeclaredMethod(step[5], step.getClass());
			result = (String[]) selAction.invoke(selDriver, (Object) step);
		} catch (Exception e) {
			result[0] = "Failed";
			result[1] = e.getMessage();
			log.error("Failed to create action:" + result[1]);
		}

		if ((step[8].equalsIgnoreCase("Yes") || result[0].equalsIgnoreCase("Failed")) && currentPageExist()) {
			result[1] = result[1] + "|" + screenCapture(step, fileDir, testCase, result, "", mod);
		}

		if (dbConfig.getConfigValue("HIGHLIGHT_ACTION").contentEquals("Y") && currentPageExist() && element != null) {
			checkPageIsReady();
			unHighlight(element);
		}
		if (dbConfig.getConfigValue("HIGHLIGHT_ACTION").contentEquals("Y") && currentPageExist() && elements != null) {
			checkPageIsReady();
			unHighlights(elements);
		}

		return result;
	}

	public String[] seleniumModularAction(String[] step, String fileDir, String testCase, String mod)
			throws IOException {
		String[] result = new String[2];
		element = null;
		elements = null;
		defaultBorderTemp = "";
		try {
			
			Thread.sleep(2000);
			step = getParameterValue(step);
			Method selAction = selDriver.getClass().getDeclaredMethod(step[5], step.getClass());
			result = (String[]) selAction.invoke(selDriver, (Object) step);
			
		} catch (Exception e) {
			result[0] = "Failed";
			result[1] = e.getMessage();
			log.error("Failed to create action:" + result[1]);
		}
		if ((step[8].equalsIgnoreCase("yes") || result[0].equalsIgnoreCase("Failed")) && currentPageExist()) {
			result[1] = result[1] + "|" + screenCapture(step, fileDir, testCase, result, "Mod", mod);
		}

		if (dbConfig.getConfigValue("HIGHLIGHT_ACTION").contentEquals("Y") && currentPageExist() && element != null) {
			checkPageIsReady();
			unHighlight(element);
		}
		if (dbConfig.getConfigValue("HIGHLIGHT_ACTION").contentEquals("Y") && currentPageExist() && elements != null) {
			checkPageIsReady();
			unHighlights(elements);
		}

		return result;
	}
	public String[] getParameterValue(String[] step) {
		
		try {
			Pattern pattern = Pattern.compile("\\(%(.*?)%\\)");
			paramVal = step[7];
			try {
				Matcher matcher = pattern.matcher(step[7]);
				while (matcher.find()) {
					String match = matcher.group(1);
					step[7] = step[7].replaceAll("\\(%" + match + "%\\)", escapeMetaCharacters(variables.get(match)));

					if(encryptedVariables.get(match) != null && encryptedVariables.get(match).contentEquals("1")) {
						paramVal = paramVal.replaceAll("\\(%" + match + "%\\)", "(encrypted)");
					}else {
						paramVal = paramVal.replaceAll("\\(%" + match + "%\\)", escapeMetaCharacters(variables.get(match)));
					}
				}
			} catch (Exception e) {
				// do nothing
			}
			log.info("Parameter Value:" + paramVal);
			elementString = step[4];
			try {
				Matcher matcherElem = pattern.matcher(step[4]);
				while (matcherElem.find()) {
					String match = matcherElem.group(1);
					step[4] = step[4].replaceAll("\\(%" + match + "%\\)", escapeMetaCharacters(variables.get(match)));
					if(encryptedVariables.get(match) != null && encryptedVariables.get(match).contentEquals("1")) {
						elementString = elementString.replaceAll("\\(%" + match + "%\\)", "(encrypted)");
					}else {
						elementString = elementString.replaceAll("\\(%" + match + "%\\)", escapeMetaCharacters(variables.get(match)));
					}
				}
			} catch (Exception e) {
				// do nothing
			}
			log.info("Element String:" + elementString);
			if (step[6] != null && step[6].length() > 0) {
				variables.put(step[6], step[7]);
			}

		} catch (Exception e) {
			log.error("Error generating parameter value:" + e.getMessage());
		}
		return step;
	}
	public String escapeMetaCharacters(String inputString) {
		final String[] metaCharacters = { "\\", "^", "$", "{", "}", "[", "]", "(", ")", ".", "*", "+", "?", "|", "<",
				">", "-", "&" };
		String outputString = "";
		for (int i = 0; i < metaCharacters.length; i++) {
			outputString = inputString.replace(metaCharacters[i], "\\" + metaCharacters[i]);
			inputString = outputString;
		}
		return outputString;
	}

	public boolean currentPageExist() {
		String handle = "";
		try {
			handle = webDriver.getWindowHandle();
		} catch (Exception e) {
			return false;
		}

		for (String winHandle : webDriver.getWindowHandles()) {
			if (handle.contentEquals(winHandle)) {
				return true;
			}
		}
		log.info("Current page closed.");
		return false;
	}

	public WebElement elementLocator(String selector, String param, Integer tOut) {
		// WebElement element = null;
		if (selector.isEmpty()) {
			return null;
		}
		if (tOut > 0) {
			webDriver.manage().timeouts().implicitlyWait(tOut, TimeUnit.SECONDS);
		} else {
			webDriver.manage().timeouts().implicitlyWait(Integer.parseInt(dbConfig.getConfigValue("IMPLICITLY_WAIT")),
					TimeUnit.SECONDS);
		}

		// checkPageIsReady();
		log.info("Finding web element: " + selector + "=" + elementString);
		try {
			// WebDriverWait wait = new WebDriverWait(webDriver, tOut);
			checkPageIsReady();
			switch (selector) {
			case "xpath":
				// wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(param)));
				element = webDriver.findElement(By.xpath(param));
				break;
			case "css":
				// wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(param)));
				element = webDriver.findElement(By.cssSelector(param));
				break;
			case "id":
				// wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(param)));
				element = webDriver.findElement(By.id(param));
				break;
			case "name":
				// wait.until(ExpectedConditions.visibilityOfElementLocated(By.name(param)));
				element = webDriver.findElement(By.name(param));
				break;
			case "linktext":
				// wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText(param)));
				element = webDriver.findElement(By.linkText(param));
				break;
			}

		} catch (Exception e) {
			log.error("Web element not found: " + selector + "=" + elementString);
			return element;
		}

		if (dbConfig.getConfigValue("HIGHLIGHT_ACTION").contentEquals("Y")) {
			String scroll = "scrollIntoView(true)";
			if(browser.equalsIgnoreCase("chrome")) {
				scroll = "scrollIntoViewIfNeeded()";
			}

			// Highlight the element
			String w = element.getCssValue("border-width");
			String s = element.getCssValue("border-style");
			String c = element.getCssValue("border-color");
			defaultBorder = "try{arguments[0].style.borderStyle='" + s + "';arguments[0].style.borderWidth='" + w
					+ "';arguments[0].style.borderColor='" + c + "';return true;}catch(err){return false;}";
			((JavascriptExecutor) webDriver).executeScript(
					"arguments[0]."+scroll+";arguments[0].style.borderStyle='solid';arguments[0].style.borderWidth='5px';arguments[0].style.borderColor='"
							+ dbConfig.getConfigValue("HIGHLIGHT_COLOR") + "'",element);
		}
		return element;

	}

	public List<WebElement> elementsLocator(String selector, String param) {
		// WebElement element = null;
		if (selector.isEmpty()) {
			return null;
		}
		webDriver.manage().timeouts().implicitlyWait(Integer.parseInt(dbConfig.getConfigValue("IMPLICITLY_WAIT")),
				TimeUnit.SECONDS);
		checkPageIsReady();
		log.info("Finding web elements: " + selector + "=" + elementString);
		try {
			checkPageIsReady();
			switch (selector) {
			case "xpath":
				elements = webDriver.findElements(By.xpath(param));
				break;
			case "css":
				elements = webDriver.findElements(By.cssSelector(param));
				break;
			case "id":
				elements = webDriver.findElements(By.id(param));
				break;
			case "name":
				elements = webDriver.findElements(By.name(param));
				break;
			case "linktext":
				elements = webDriver.findElements(By.linkText(param));
				break;
			}

		} catch (Exception e) {
			log.error("Web element not found: " + selector + "=" + elementString + ":" + e.getMessage());
			return elements;
		}

		try {
			if (dbConfig.getConfigValue("HIGHLIGHT_ACTION").contentEquals("Y")) {
				String scroll = "scrollIntoView(true)";
				if(browser.equalsIgnoreCase("chrome")) {
					scroll = "scrollIntoViewIfNeeded()";
				}

				// Highlight the element
				for (WebElement el : elements) {
					String w = el.getCssValue("border-width");
					String s = el.getCssValue("border-style");
					String c = el.getCssValue("border-color");
					defaultBorders.add("try{arguments[0].style.borderStyle='" + s + "';arguments[0].style.borderWidth='"
							+ w + "';arguments[0].style.borderColor='" + c
							+ "';return true;}catch(err){return false;}");
					((JavascriptExecutor) webDriver).executeScript(
							"arguments[0]."+scroll+";arguments[0].style.borderStyle='solid';arguments[0].style.borderWidth='5px';arguments[0].style.borderColor='"
									+ dbConfig.getConfigValue("HIGHLIGHT_COLOR") + "'",
							el);
				}
			}
		} catch (Exception e) {
			// do nothing
		}
		return elements;
	}

	public void exitWebDriver() {
		// webDriver.close();
		webDriver.quit();
		log.info("Web driver is now closed.");
	};

	public String getDateTime() {
		SimpleDateFormat df = new SimpleDateFormat("MMM-dd-yyyy HH.mm.ss");
		return df.format(new Date());
	}
	public String getDT(String format) {
		String date = "";
		try {
			if(format != null && format.length()>0) {
				String[] param = format.split("\\|");
				if(param.length > 1) {
					SimpleDateFormat df = new SimpleDateFormat(param[0]);
					Date dateToday = df.parse(df.format(new Date()).toString());
					long dateTodayInMilliseconds = dateToday.getTime();
					long seconds = Long.parseLong(param[2]);
					long total = 0;
					if(param[1].contentEquals("-")) {
						total = dateTodayInMilliseconds - (seconds * 1000);
					}else if(param[1].contentEquals("+")) {
						total = dateTodayInMilliseconds + (seconds * 1000);
					}
					date = df.format(total);
				} else {
					SimpleDateFormat df = new SimpleDateFormat(format);
					date = df.format(new Date());
				}
			} else {
				date = new Date().toString();
			}
		} catch (Exception e) {
			// do nothing
		}
		return date;
	}
	public String getDatePlusMinus(String format) {
		String date = "";
		try {
			if(format != null && format.length()>0) {
				String[] param = format.split("\\|");
				if(param.length > 3) {
					SimpleDateFormat df = new SimpleDateFormat(param[0]);
					Date dateToday = df.parse(param[1]);
					long dateTodayInMilliseconds = dateToday.getTime();
					long seconds = Long.parseLong(param[3]);
					long total = 0;
					if(param[2].contentEquals("-")) {
						total = dateTodayInMilliseconds - (seconds * 1000);
					}else if(param[2].contentEquals("+")) {
						total = dateTodayInMilliseconds + (seconds * 1000);
					}
					if(param.length > 4) {
						SimpleDateFormat df2 = new SimpleDateFormat(param[4]);
						date = df2.format(total);
					} else {
						date = df.format(total);	
					}
				}
			}
		} catch (Exception e) {
			// do nothing
		}
		return date;
	}
	private String getTimeDiff(String param) {
		try {
			String[] par = param.split("\\|");
			DateFormat df = new SimpleDateFormat(par[0]);
			Date d1 = df.parse(par[1]);
			Date d2 = df.parse(par[2]);
			long diffInSeconds = Math.abs(d1.getTime() - d2.getTime()) / 1000;

			return String.valueOf(diffInSeconds);

		} catch (Exception e) {
			// nothing to do
		}

		return null;
	}

	public void checkPageIsReady() {
		JavascriptExecutor js = (JavascriptExecutor) webDriver;
		for (int i = 0; i < Integer.parseInt(dbConfig.getConfigValue("PAGELOAD_TIMEOUT")); i++) {
			try {
				log.info("check if ready state: " + i);
				// To check page ready state.
				if (js.executeScript("return document.readyState").toString().equals("complete")) {
					log.info("document load complete.");
					return;
				}
				Thread.sleep(1000);
			} catch (Exception e) {
				log.info("check if ready state: Exception");
				return;
			}
		}
	}

	// function for screen capture
	public String screenCapture(String[] step, String path, String testCase, String[] status, String flg,
			String mod) throws IOException {
		try {

			String strSSImage = path + "/" + mod + "/" + testCase + "/Screenshot/" + flg + step[0] + "_" + getDateTime()
					+ ".jpeg";
			File scrFile = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.FILE);
			FileUtils.copyFile(scrFile, new File(strSSImage));

			// for saving of screenshot to word
			// check first if file exist else create word
			String strSSDocName = path + "/" + mod + "/" + testCase + "/" + testCase + ".docx";
			File f = new File(strSSDocName);
			if (f.exists() == false && !f.isDirectory()) {
				createWordDocument(strSSDocName);
				log.info("Word Document created: " + strSSDocName);
			}
			// write screenshot to word
			writeSStoDocu(strSSDocName, strSSImage, step[0], step[2], status);

			// return screenshot filename
			return strSSImage;
		} catch (Exception e) {
			// log.error("Failed to capture screenshot: "+e.getMessage().toString());
			return "Failed to capture screen: " + e.getMessage();
		}
	}

	public void createWordDocument(String strFileNamePath) {

		try {
			// Blank Document
			XWPFDocument objDocument = new XWPFDocument();
			// Write the Document in file system
			FileOutputStream out = new FileOutputStream(strFileNamePath);
			objDocument.write(out);
			objDocument.close();
			out.close();
			log.info("MS Word created: " + strFileNamePath);
		} catch (FileNotFoundException e) {
			log.info("File not found! [" + strFileNamePath + "]");
		} catch (IOException e) {
			log.info("IOException: " + e.getMessage());
		}
	}

	public void writeSStoDocu(String strFileNamePath, String strSSImage, String strStepID,
			String strStepDescription, String[] arrStepStatus) {

		try {
			FileInputStream in = new FileInputStream(strFileNamePath);
			XWPFDocument doc = new XWPFDocument(in);
			XWPFParagraph title = doc.createParagraph();
			XWPFRun run = title.createRun();

			run.setBold(false);
			run.setColor("000000");

			// run.setText("StepID: " + strStepID + " - " + strStepDescription + "\n\n");
			run.setText("Step Description : " + strStepDescription + "\n\n");
			run.addCarriageReturn();
			run.setText("Step Status: ");
			if (arrStepStatus[0].contains("Failed")) {
				run.setBold(true);
				run.setColor("ff0000");
			}
			run.setText(arrStepStatus[0]);
			run.addCarriageReturn();

			FileInputStream is = new FileInputStream(strSSImage);
			run.addBreak();
			run.addPicture(is, XWPFDocument.PICTURE_TYPE_JPEG, strSSImage, Units.toEMU(480), Units.toEMU(250)); // 200x200
																												// pixels
			is.close();

			// run.addCarriageReturn();
			run.addCarriageReturn();
			in.close();

			log.info("Done writing to document.");

			FileOutputStream out = new FileOutputStream(strFileNamePath);
			doc.write(out);
			out.close();
			doc.close();
			log.info("Done save and exit.");
		} catch (FileNotFoundException e) {
			log.info("File not found: " + e.getMessage());
		} catch (IOException e) {
			log.info("IOException: " + e.getMessage());
		} catch (InvalidFormatException e) {
			log.info("InvalidFormat: " + e.getMessage());
		}
	}

	private void unHighlight(WebElement element) {

		if (element != null) {
			try {
				// if there already is a highlighted element, unhighlight it
				((JavascriptExecutor) webDriver).executeScript(defaultBorder, element);
			} catch (Exception ignored) {
				// the page got reloaded, the element isn't there
			}
		}
	}

	private void unHighlights(List<WebElement> elements) {
		if (elements != null) {
			try {
				// if there already is a highlighted element, unhighlight it
				int i = 0;
				for (WebElement w : elements) {
					((JavascriptExecutor) webDriver).executeScript(defaultBorders.get(i), w);
					i = i + 1;
				}
				defaultBorders.clear();
			} catch (Exception ignored) {
				// the page got reloaded, the element isn't there
			}
		}
	}

	public int compareNumber(Number x, Number y) {
		if (isSpecial(x) || isSpecial(y))
			return Double.compare(x.doubleValue(), y.doubleValue());
		else
			return toBigDecimal(x).compareTo(toBigDecimal(y));
	}

	private boolean isSpecial(Number x) {
		boolean specialDouble = x instanceof Double && (Double.isNaN((Double) x) || Double.isInfinite((Double) x));
		boolean specialFloat = x instanceof Float && (Float.isNaN((Float) x) || Float.isInfinite((Float) x));
		return specialDouble || specialFloat;
	}

	private BigDecimal toBigDecimal(Number number) {
		if (number instanceof BigDecimal)
			return (BigDecimal) number;
		if (number instanceof BigInteger)
			return new BigDecimal((BigInteger) number);
		if (number instanceof Byte || number instanceof Short || number instanceof Integer || number instanceof Long)
			return new BigDecimal(number.longValue());
		if (number instanceof Float || number instanceof Double)
			return new BigDecimal(number.doubleValue());

		try {
			return new BigDecimal(number.toString());
		} catch (final NumberFormatException e) {
			throw new RuntimeException("The given number (\"" + number + "\" of class " + number.getClass().getName()
					+ ") does not have a parsable string representation", e);
		}
	}

	/*
	 * ============================
	 * ========| ACTIONS |=========
	 * ============================
	 */
	// Load URL to browser
	public String[] openWebPage(String[] step) {
		String[] arrStepStatus = new String[2];
		try {
			log.info("web driver: " + webDriver);
			webDriver.get(step[7]);
			arrStepStatus[0] = "Passed";
			arrStepStatus[1] = "URL navigated to " + paramVal;
		} catch (Exception e) {
			arrStepStatus[0] = "Failed";
			arrStepStatus[1] = e.getMessage().toString();
		}
		return arrStepStatus;
	}

	// click element on the browser
	public String[] click(String[] step) {
		String[] arrStepStatus = new String[2];
		try {
			element = elementLocator(step[3], step[4], 0);

			if (!element.isEnabled()) {
				Thread.sleep(3000);
			}
			if (step[7] != null && step[7].length() > 0) {
				log.info("click with param.");
				element.findElement(By.xpath(".//*[text()='" + step[7] + "']")).click();
			} else {
				element.click();
			}

			arrStepStatus[0] = "Passed";
			arrStepStatus[1] = "Element clicked";
			Thread.sleep(3000);
		} catch (Exception e) {
			arrStepStatus[0] = "Failed";
			arrStepStatus[1] = e.getMessage().toString();
		}
		return arrStepStatus;
	}

	// click element on the browser
	public String[] clickAll(String[] step) {
		String[] arrStepStatus = new String[2];
		try {
			elements = elementsLocator(step[3], step[4]);
			int i = 0;
			for (@SuppressWarnings("unused")
			WebElement e : elements) {
				i = i + 1;

				element = elementLocator(step[3], "(" + step[4] + ")[" + i + "]", 10);
				// if(!element.isEnabled()){
				Thread.sleep(2000);
				// }
				if (step[7] != null && step[7].length() > 0) {
					element.findElement(By.xpath(".//*[text()='" + step[7] + "']")).click();
					log.info("Click with param.");
					log.info(" Clicked: Element location: " + "(" + elementString + ")[" + i + "]");
				} else {
					element.click();
					log.info("Clicked: Element location: " + "(" + elementString + ")[" + i + "]");
				}
			}
			arrStepStatus[0] = "Passed";
			arrStepStatus[1] = "Elements clicked";

		} catch (Exception e) {
			arrStepStatus[0] = "Failed";
			arrStepStatus[1] = e.getMessage().toString();
		}
		return arrStepStatus;
	}
	// click element on the browser
	public String[] clickAllv2(String[] step) {
		String[] arrStepStatus = new String[2];
		try {
			elements = elementsLocator(step[3], step[4]);
			for (@SuppressWarnings("unused")
			WebElement e : elements) {

				element = elementLocator(step[3], "(" + step[4] + ")", 10);
				// if(!element.isEnabled()){
				Thread.sleep(2000);
				// }
				if (step[7] != null && step[7].length() > 0) {
					element.findElement(By.xpath(".//*[text()='" + step[7] + "']")).click();
					log.info("Click with param.");
					log.info(" Clicked: Element location: " + "(" + elementString + ")");
				} else {
					element.click();
					log.info("Clicked: Element location: " + "(" + elementString + ")");
				}
			}
			arrStepStatus[0] = "Passed";
			arrStepStatus[1] = "Elements clicked";

		} catch (Exception e) {
			arrStepStatus[0] = "Failed";
			arrStepStatus[1] = e.getMessage().toString();
		}
		return arrStepStatus;
	}


	// click element on the browser
	public String[] clickAllWithAlert(String[] step) {
		String[] arrStepStatus = new String[2];
		try {
			elements = elementsLocator(step[3], step[4]);
			for (@SuppressWarnings("unused")
			WebElement e : elements) {

				element = elementLocator(step[3], "(" + step[4] + ")", 10);
				// if(!element.isEnabled()){
				Thread.sleep(2000);
				// }
				if (step[7] != null && step[7].length() > 0) {
					element.findElement(By.xpath(".//*[text()='" + step[7] + "']")).click();
					webDriver.switchTo().alert().accept();
					log.info("Click with param.");
					log.info(" Clicked: Element location: " + "(" + elementString + ")");
				} else {
					element.click();
					webDriver.switchTo().alert().accept();
					log.info("Clicked: Element location: " + "(" + elementString + ")");
				}

			}
			arrStepStatus[0] = "Passed";
			arrStepStatus[1] = "Elements clicked";

		} catch (Exception e) {
			arrStepStatus[0] = "Failed";
			arrStepStatus[1] = e.getMessage().toString();
		}
		return arrStepStatus;
	}

	// click element on the browser
	public String[] doubleClick(String[] step) {
		String[] arrStepStatus = new String[2];
		try {
			element = elementLocator(step[3], step[4], 0);

			String mouseOverScript = " triggerMouseEvent(arguments[0], 'dblclick');"
					+ " triggerMouseEvent(arguments[0], 'focus');" + " function triggerMouseEvent (node, eventType) {"
					+ "  var eventObj = document.createEvent ('MouseEvents');"
					+ " eventObj.initEvent (eventType, true, true);" + " node.dispatchEvent (eventObj);" + " }";
			((JavascriptExecutor) webDriver).executeScript(mouseOverScript, element);

			log.info("Doubled clicked.");
			arrStepStatus[0] = "Passed";
			arrStepStatus[1] = "Element double clicked";
		} catch (Exception e) {
			arrStepStatus[0] = "Failed";
			arrStepStatus[1] = e.getMessage().toString();
		}
		return arrStepStatus;
	}

	// type or set text
	public String[] typeOnKeyboard(String[] step) {
		String[] arrStepStatus = new String[2];
		try {
			element = elementLocator(step[3], step[4], 0);
			element.clear();
			element.sendKeys(step[7]);

			arrStepStatus[0] = "Passed";
			arrStepStatus[1] = "Element input: " + paramVal;
		} catch (Exception e) {
			arrStepStatus[0] = "Failed";
			arrStepStatus[1] = e.getMessage().toString();
		}
		return arrStepStatus;
	}

	// press Enter Key
	public String[] pressEnterKey(String[] step) {
		String[] arrStepStatus = new String[2];
		try {
			element = elementLocator(step[3], step[4], 0);
			if (step[7] != null && step[7].length() > 0) {
				for(int i = 0; i < Integer.parseInt(step[7]); i++) {
					element.sendKeys(Keys.ENTER);
					log.info("Enter Key pressed.");
				}
			}else {
				element.sendKeys(Keys.ENTER);
				log.info("Enter Key pressed.");
			}
			arrStepStatus[0] = "Passed";
			arrStepStatus[1] = "Enter Key pressed.";
		} catch (Exception e) {
			arrStepStatus[0] = "Failed";
			arrStepStatus[1] = e.getMessage().toString();
		}

		return arrStepStatus;
	}

	// press arrow up key
	public String[] pressArrowUpKey(String[] step) {
		String[] arrStepStatus = new String[2];
		try {
			element = elementLocator(step[3], step[4], 0);
			if (step[7] != null && step[7].length() > 0) {
				for(int i = 0; i < Integer.parseInt(step[7]); i++) {
					element.sendKeys(Keys.ARROW_UP);
					log.info("ArrowUp Key pressed.");
				}
			}else {
				element.sendKeys(Keys.ARROW_UP);
				log.info("ArrowUp Key pressed.");
			}

			arrStepStatus[0] = "Passed";
			arrStepStatus[1] = "ArrowUp Key pressed.";
		} catch (Exception e) {
			arrStepStatus[0] = "Failed";
			arrStepStatus[1] = e.getMessage().toString();
		}

		return arrStepStatus;
	}

	// press arrow left key
	public String[] pressArrowLeftKey(String[] step) {
		String[] arrStepStatus = new String[2];
		try {
			element = elementLocator(step[3], step[4], 0);
			if (step[7] != null && step[7].length() > 0) {
				for(int i = 0; i < Integer.parseInt(step[7]); i++) {
					element.sendKeys(Keys.ARROW_LEFT);
					log.info("ArrowLeft Key pressed.");
				}
			}else {
				element.sendKeys(Keys.ARROW_LEFT);
				log.info("ArrowLeft Key pressed.");
			}

			arrStepStatus[0] = "Passed";
			arrStepStatus[1] = "ArrowLeft Key pressed.";
		} catch (Exception e) {
			arrStepStatus[0] = "Failed";
			arrStepStatus[1] = e.getMessage().toString();
		}

		return arrStepStatus;
	}

	// press arrow Page Down key
	public String[] pressPageDownKey(String[] step) {
		String[] arrStepStatus = new String[2];
		try {
			element = elementLocator(step[3], step[4], 0);
			if (step[7] != null && step[7].length() > 0) {
				for(int i = 0; i < Integer.parseInt(step[7]); i++) {
					element.sendKeys(Keys.PAGE_DOWN);
					log.info("Page Down Key pressed.");
				}
			}else {
				element.sendKeys(Keys.PAGE_DOWN);
				log.info("Page Down Key pressed.");
			}

			arrStepStatus[0] = "Passed";
			arrStepStatus[1] = "Page Down Key pressed.";
		} catch (Exception e) {
			arrStepStatus[0] = "Failed";
			arrStepStatus[1] = e.getMessage().toString();
		}

		return arrStepStatus;
	}

	// press arrow Page Up key
	public String[] pressPageUpKey(String[] step) {
		String[] arrStepStatus = new String[2];
		try {
			element = elementLocator(step[3], step[4], 0);
			if (step[7] != null && step[7].length() > 0) {
				for(int i = 0; i < Integer.parseInt(step[7]); i++) {
					element.sendKeys(Keys.PAGE_UP);
					log.info("Page Up Key pressed.");
				}
			}else {
				element.sendKeys(Keys.PAGE_UP);
				log.info("Page Up Key pressed.");
			}

			arrStepStatus[0] = "Passed";
			arrStepStatus[1] = "Page Up Key pressed.";
		} catch (Exception e) {
			arrStepStatus[0] = "Failed";
			arrStepStatus[1] = e.getMessage().toString();
		}

		return arrStepStatus;
	}

	// press arrow down key
	public String[] pressArrowDownKey(String[] step) {
		String[] arrStepStatus = new String[2];
		try {
			
			element = elementLocator(step[3], step[4], 0);
			if (step[7] != null && step[7].length() > 0) {
				for(int i = 0; i < Integer.parseInt(step[7]); i++) {
					element.sendKeys(Keys.ARROW_DOWN);
					log.info("ArrowDown Key pressed.");
				}
			}else {
				element.sendKeys(Keys.ARROW_DOWN);
				log.info("ArrowDown Key pressed.");
			}

			arrStepStatus[0] = "Passed";
			arrStepStatus[1] = "ArrowDown Key pressed.";
		} catch (Exception e) {
			arrStepStatus[0] = "Failed";
			arrStepStatus[1] = e.getMessage().toString();
		}

		return arrStepStatus;
	}

	// press arrow right key
	public String[] pressArrowRightKey(String[] step) {
		String[] arrStepStatus = new String[2];
		try {
			element = elementLocator(step[3], step[4], 0);
			if (step[7] != null && step[7].length() > 0) {
				for(int i = 0; i < Integer.parseInt(step[7]); i++) {
					element.sendKeys(Keys.ARROW_RIGHT);
					log.info("ArrowRight Key pressed.");
				}
			}else {
				element.sendKeys(Keys.ARROW_RIGHT);
				log.info("ArrowRight Key pressed.");
			}
			
			arrStepStatus[0] = "Passed";
			arrStepStatus[1] = "ArrowRight Key pressed.";
		} catch (Exception e) {
			arrStepStatus[0] = "Failed";
			arrStepStatus[1] = e.getMessage().toString();
		}
		return arrStepStatus;
	}
	// press Esc Key
	public String[] pressEscKey(String[] step) {
		String[] arrStepStatus = new String[2];
		try {
			element = elementLocator(step[3], step[4], 0);
			if (step[7] != null && step[7].length() > 0) {
				for(int i = 0; i < Integer.parseInt(step[7]); i++) {
					element.sendKeys(Keys.ESCAPE);
					log.info("ESC Key pressed.");
				}
			}else {
				element.sendKeys(Keys.ENTER);
				log.info("ESC Key pressed.");
			}
			arrStepStatus[0] = "Passed";
			arrStepStatus[1] = "ESC Key pressed.";
		} catch (Exception e) {
			arrStepStatus[0] = "Failed";
			arrStepStatus[1] = e.getMessage().toString();
		}

		return arrStepStatus;
	}
	// press arrow right key
	public String[] openNewWindow(String[] step) {
		String[] arrStepStatus = new String[2];
		try {
			log.info("Open new window.");

			String winOpen = "window.open('','_blank','toolbar=yes,location=yes,directories=yes,status=yes,menubar=yes,scrollbars=yes,copyhistory=yes,resizable=yes')";
			((JavascriptExecutor) webDriver).executeScript(winOpen);

			arrStepStatus[0] = "Passed";
			arrStepStatus[1] = "New window opened.";
		} catch (Exception e) {
			arrStepStatus[0] = "Failed";
			arrStepStatus[1] = e.getMessage().toString();
		}
		return arrStepStatus;
	}

	// move mouse over the element
	public String[] mouseHover(String[] step) {
		String[] arrStepStatus = new String[2];
		try {
			element = elementLocator(step[3], step[4], 0);

			String mouseOverScript = " triggerMouseEvent(arguments[0], 'mouseover');"
					+ " triggerMouseEvent(arguments[0], 'mousemove');"
					+ " function triggerMouseEvent (node, eventType) {"
					+ "  var eventObj = document.createEvent ('MouseEvents');"
					+ " eventObj.initEvent (eventType, true, true);" + " node.dispatchEvent (eventObj);" + " }";
			((JavascriptExecutor) webDriver).executeScript(mouseOverScript, element);

			log.info("Mouse hovered.");

			arrStepStatus[0] = "Passed";
			arrStepStatus[1] = "Mouse over web element.";
		} catch (Exception e) {
			arrStepStatus[0] = "Failed";
			arrStepStatus[1] = e.getMessage().toString();
		}

		return arrStepStatus;
	}
	// execute javascript
	public String[] executeJavascript(String[] step) {
		String[] arrStepStatus = new String[2];
		try {
				log.info("Execute Javascript : " + step[7]);
				element = elementLocator(step[3], step[4], 0);
				String rtrn = (String) ((JavascriptExecutor) webDriver).executeScript(step[7]);
	
				log.info("Javascript return: " + rtrn);
				variables.put(step[6], rtrn);
				
				arrStepStatus[0] = "Passed";
				arrStepStatus[1] = "Javascript executed.: " + rtrn;
				
		} catch (Exception e) {
			arrStepStatus[0] = "Failed";
			arrStepStatus[1] = e.getMessage().toString();
		}

		return arrStepStatus;
	}

	// clear text from the text box
	public String[] clearTextField(String[] step) {
		String[] arrStepStatus = new String[2];
		try {
			log.info("Text field cleared.");
			element = elementLocator(step[3], step[4], 0);
			element.clear();

			arrStepStatus[0] = "Passed";
			arrStepStatus[1] = "Text field cleared.";
		} catch (Exception e) {
			arrStepStatus[0] = "Failed";
			arrStepStatus[1] = e.getMessage().toString();
		}

		return arrStepStatus;
	}

	// validate displayed text of the specific element
	public String[] validateText(String[] step) {
		String[] arrStepStatus = new String[2];
		try {
			element = elementLocator(step[3], step[4], 0);
			String strElemText = element.getText();
			strElemText = strElemText.replaceAll("\\r\\n|\\r|\\n", " ");
			strElemText = strElemText.trim();

			if (strElemText.contentEquals(step[7])) {
				arrStepStatus[0] = "Passed";
				arrStepStatus[1] = "Actual: " + strElemText + " = Expected: " + paramVal;
				log.info("Actual: " + strElemText);
				log.info("Actual and Expected are the same.");
			} else {
				arrStepStatus[0] = "Failed";
				arrStepStatus[1] = "[Not Equal] Actual: " + strElemText + " <> Expected: " + paramVal;
				log.info("[Not Equal] Actual: " + strElemText + " <> Expected: " + paramVal);
			}

		} catch (Exception e) {
			arrStepStatus[0] = "Failed";
			arrStepStatus[1] = e.getMessage().toString();
		}

		return arrStepStatus;
	}

	// validate displayed text of the specific element
	public String[] validateTextContains(String[] step) {
		String[] arrStepStatus = new String[2];
		try {
			element = elementLocator(step[3], step[4], 0);
			String strElemText = element.getText();
			strElemText = strElemText.replaceAll("\\r\\n|\\r|\\n", " ");
			strElemText = strElemText.trim();

			Pattern pattern = Pattern.compile("(" + step[7] + ")");
			Matcher matcher = pattern.matcher(strElemText);
			if (matcher.find()) {
				arrStepStatus[0] = "Passed";
				arrStepStatus[1] = "Actual: " + strElemText + " = Expected: " + paramVal;
				log.info("Actual and Expected are Matched. Text Found :" + strElemText + "Matched to:" + paramVal);
			} else {
				arrStepStatus[0] = "Failed";
				arrStepStatus[1] = "[Not Equal] Actual: " + strElemText + " <> Expected: " + paramVal;
				log.info("[Not Equal] Actual: " + strElemText + " <> Expected: " + paramVal);
			}
		} catch (Exception e) {
			arrStepStatus[0] = "Failed";
			arrStepStatus[1] = e.getMessage().toString();
		}

		return arrStepStatus;
	}
	// validate displayed text of the specific element
	public String[] valParamContains(String[] step) {
		String[] arrStepStatus = new String[2];
		try {
			String[] param = step[7].split("\\|");
			Pattern pattern = Pattern.compile("(" + param[0] + ")");
			Matcher matcher = pattern.matcher(param[1]);
			if (matcher.find()) {
				arrStepStatus[0] = "Passed";
				arrStepStatus[1] = "Regex found Matched: " + paramVal;
				log.info("Regex found Matched: " + paramVal);
			} else {
				arrStepStatus[0] = "Failed";
				arrStepStatus[1] = "Regex NOT found Matched: " + paramVal;
				log.info("Regex found Matched: " + paramVal);
			}
		} catch (Exception e) {
			arrStepStatus[0] = "Failed";
			arrStepStatus[1] = e.getMessage().toString();
		}

		return arrStepStatus;
	}

	// validate displayed text of table row
	public String[] validateTableRowTextTd(String[] step) {
		String[] arrStepStatus = new String[2];
		try {
			element = elementLocator(step[3], step[4], 0);
			List<WebElement> columns = element.findElements(By.tagName("td"));
			Iterator<WebElement> iter = columns.iterator();
			String strElemText = "";
			while (iter.hasNext()) {
				WebElement we = iter.next();
				String elemText = we.getText();
				elemText = elemText.trim();
				strElemText = strElemText + "|" + elemText.replaceAll("\\r\\n|\\r|\\n", " ");
			}
			if (strElemText != null && strElemText.length() > 0) {
				strElemText = strElemText.substring(1);
			}

			if (strElemText.contentEquals(step[7])) {
				arrStepStatus[0] = "Passed";
				arrStepStatus[1] = "Actual: " + strElemText + " = Expected: " + paramVal;
				log.info("Actual: " + strElemText + " = Expected: " + paramVal);
			} else {
				arrStepStatus[0] = "Failed";
				arrStepStatus[1] = "[Not Equal] Actual: " + strElemText + " <> Expected: " + paramVal;
				log.info("[Not Equal] Actual: " + strElemText + " <> Expected: " + paramVal);
			}

		} catch (Exception e) {
			arrStepStatus[0] = "Failed";
			arrStepStatus[1] = e.getMessage().toString();
		}

		return arrStepStatus;
	}

	// validate displayed text of table row
	public String[] validateTableRowTextTh(String[] step) {
		String[] arrStepStatus = new String[2];
		try {
			element = elementLocator(step[3], step[4], 0);
			List<WebElement> columns = element.findElements(By.tagName("th"));
			Iterator<WebElement> iter = columns.iterator();
			String strElemText = "";
			while (iter.hasNext()) {
				WebElement we = iter.next();
				String elemText = we.getText();
				elemText = elemText.trim();
				strElemText = strElemText + "|" + elemText.replaceAll("\\r\\n|\\r|\\n", " ");
			}
			if (strElemText != null && strElemText.length() > 0) {
				strElemText = strElemText.substring(1);
			}

			if (strElemText.contentEquals(step[7])) {
				arrStepStatus[0] = "Passed";
				arrStepStatus[1] = "Actual: " + strElemText + " = Expected: " + paramVal;
				log.info("Actual: " + strElemText + " = Expected: " + paramVal);
			} else {
				arrStepStatus[0] = "Failed";
				arrStepStatus[1] = "[Not Equal] Actual: " + strElemText + " <> Expected: " + paramVal;
				log.info("[Not Equal] Actual: " + strElemText + " <> Expected: " + paramVal);
			}

		} catch (Exception e) {
			arrStepStatus[0] = "Failed";
			arrStepStatus[1] = e.getMessage().toString();
		}

		return arrStepStatus;
	}

	// validate current loaded URL
	public String[] validateURL(String[] step) {
		String[] arrStepStatus = new String[2];
		try {
			String strURL = webDriver.getCurrentUrl();
			if (strURL == step[7]) {
				arrStepStatus[0] = "Passed";
				arrStepStatus[1] = "Actual: " + strURL + " = Expected: " + paramVal;
				log.info("Actual: " + strURL + " = Expected: " + paramVal);
			} else {
				arrStepStatus[0] = "Failed";
				arrStepStatus[1] = "[Not Equal] Actual: " + strURL + " <> Expected: " + paramVal;
				arrStepStatus[1] = "[Not Equal] Actual: " + strURL + " <> Expected: " + paramVal;
				log.info("[Not Equal] Actual: " + strURL + " <> Expected: " + paramVal);
			}
		} catch (Exception e) {
			arrStepStatus[0] = "Failed";
			arrStepStatus[1] = e.getMessage().toString();
		}

		return arrStepStatus;
	}

	// validate current loaded URL
	public String[] validateTitle(String[] step) {
		String[] arrStepStatus = new String[2];
		try {
			element = elementLocator(step[3], step[4], 0);
			String strElemText = element.getAttribute("title");
			strElemText = strElemText.replaceAll("\\r\\n|\\r|\\n", " ");
			strElemText = strElemText.trim();

			if (strElemText.contentEquals(step[7])) {
				arrStepStatus[0] = "Passed";
				arrStepStatus[1] = "Actual: " + strElemText + " = Expected: " + paramVal;
				log.info("Actual: " + strElemText + " = Expected: " + paramVal);
			} else {
				arrStepStatus[0] = "Failed";
				arrStepStatus[1] = "[NotEqual] Actual: " + strElemText + " <> Expected: " + paramVal;
				log.info("[NotEqual] Actual: " + strElemText + " <> Expected: " + paramVal);
			}

		} catch (Exception e) {
			arrStepStatus[0] = "Failed";
			arrStepStatus[1] = e.getMessage().toString();
		}

		return arrStepStatus;
	}

	// validate attribute value
	public String[] valAttribValue(String[] step) {
		String[] arrStepStatus = new String[2];
		try {
			String[] paramArr = step[7].split("\\|");
			element = elementLocator(step[3], step[4], 0);
			String strElemText = element.getAttribute(paramArr[0]);
			strElemText = strElemText.replaceAll("\\r\\n|\\r|\\n", " ");
			strElemText = strElemText.trim();
			String paramVal1 = "";
			if (paramArr.length > 1) {
				paramVal1 = paramArr[1];
			}
			if (strElemText.contentEquals(paramVal1)) {
				arrStepStatus[0] = "Passed";
				arrStepStatus[1] = "Actual: " + strElemText + " = Expected: " + paramVal;
				log.info("Actual: " + strElemText + "= Expected: " + paramVal);
			} else {
				arrStepStatus[0] = "Failed";
				arrStepStatus[1] = "[Not Equal] Actual: " + strElemText + " <> Expected: " + paramVal;
				log.info("[Not Equal] Actual: " + strElemText + " <> Expected: " + paramVal);
			}

		} catch (Exception e) {
			arrStepStatus[0] = "Failed";
			arrStepStatus[1] = e.getMessage().toString();
		}

		return arrStepStatus;
	}

	// validate attribute value
	public String[] valAttribContains(String[] step) {
		String[] arrStepStatus = new String[2];
		try {
			String[] paramArr = step[7].split("\\|");
			element = elementLocator(step[3], step[4], 0);
			String strElemText = element.getAttribute(paramArr[0]);
			strElemText = strElemText.replaceAll("\\r\\n|\\r|\\n", " ");
			strElemText = strElemText.trim();
			String paramVal1 = "";
			if (paramArr.length > 1) {
				paramVal1 = paramArr[1];
			}

			Pattern pattern = Pattern.compile(paramVal1);
			Matcher matcher = pattern.matcher(strElemText);
			if (matcher.find()) {
				arrStepStatus[0] = "Passed";
				arrStepStatus[1] = "Actual: " + strElemText + " = Expected: " + paramVal;
				log.info("Actual: " + strElemText + " = Expected: " + paramVal);
			} else {
				arrStepStatus[0] = "Failed";
				arrStepStatus[1] = "[Not Equal] Actual: " + strElemText + " <> Expected: " + paramVal;
				log.info("[Not Equal] Actual: " + strElemText + " <> Expected: " + paramVal);
			}

		} catch (Exception e) {
			arrStepStatus[0] = "Failed";
			arrStepStatus[1] = e.getMessage().toString();
		}

		return arrStepStatus;
	}

	// validate text from the drop down list
	public String[] validateDropDownText(String[] step) {
		String[] arrStepStatus = new String[2];
		try {
			element = elementLocator(step[3], step[4], 0);
			String strDDText = new Select(element).getFirstSelectedOption().getText();

			if (strDDText.contentEquals(step[7])) {
				arrStepStatus[0] = "Passed";
				arrStepStatus[1] = "Actual: " + strDDText + " = Expected: " + paramVal;
				log.info("Actual: " + strDDText + " = Expected: " + paramVal);
			} else {
				arrStepStatus[0] = "Failed";
				arrStepStatus[1] = "[Not Equal] Actual: " + strDDText + " <> Expected: " + paramVal;
				log.info("[Not Equal] Actual: " + strDDText + " <> Expected: " + paramVal);
			}

		} catch (Exception e) {
			arrStepStatus[0] = "Failed";
			arrStepStatus[1] = e.getMessage().toString();
		}

		return arrStepStatus;
	}

	// validate text from the drop down list
	public String[] getDropDownToVar(String[] step) {
		String[] arrStepStatus = new String[2];
		try {
			element = elementLocator(step[3], step[4], 0);
			String strDDText = new Select(element).getFirstSelectedOption().getText();

			strDDText = strDDText.replaceAll("\\r\\n|\\r|\\n", " ");
			strDDText = strDDText.trim();
			log.info("Get Element text:  " + strDDText);
			if (step[6] != null && step[6].length() > 0) {
				if (step[7] != null && step[7].length() > 0) {
					Pattern pattern = Pattern.compile(step[7]);
					Matcher matcher = pattern.matcher(strDDText);
					if (matcher.find()) {
						log.info("RegEx Matched:  " + matcher.group(1));
						variables.put(step[6], matcher.group(1));
					} else {
						log.info("RegEx Not Found.");
						variables.put(step[6], "");
					}

				} else {
					variables.put(step[6], strDDText);
				}

				arrStepStatus[0] = "Passed";
				arrStepStatus[1] = "Element text stored to variable " + step[6] + ":" + variables.get(step[6]);
			} else {
				arrStepStatus[0] = "Failed";
				arrStepStatus[1] = "Parameter name is required";
			}

		} catch (Exception e) {
			arrStepStatus[0] = "Failed";
			arrStepStatus[1] = e.getMessage().toString();
		}

		return arrStepStatus;
	}

	// check if element is present
	public String[] elementIsPresent(String[] step) {
		String[] arrStepStatus = new String[2];
		try {
			element = elementLocator(step[3], step[4], 0);
			if (element != null) {
				arrStepStatus[0] = "Passed";
				arrStepStatus[1] = "Element is existing.";
				log.info("Element is existing.");
			} else {
				arrStepStatus[0] = "Failed";
				arrStepStatus[1] = "Element is does not exist.";
				log.info("Element is does not exist.");
			}

		} catch (Exception e) {
			arrStepStatus[0] = "Failed";
			arrStepStatus[1] = e.getMessage().toString();
		}

		return arrStepStatus;
	}

	// check if element is not present
	public String[] elementIsNotPresent(String[] step) {
		String[] arrStepStatus = new String[2];
		try {
			element = elementLocator(step[3], step[4], 0);
			if (element == null) {
				arrStepStatus[0] = "Passed";
				arrStepStatus[1] = "Element is does not exist.";
				log.info("Element does not exist.");
			} else {
				arrStepStatus[0] = "Failed";
				arrStepStatus[1] = "Element is existing.";
				log.info("Element is existing.");
			}

		} catch (Exception e) {
			arrStepStatus[0] = "Failed";
			arrStepStatus[1] = e.getMessage().toString();
		}

		return arrStepStatus;
	}

	// check if check box is checked
	public String[] isChecked(String[] step) {
		String[] arrStepStatus = new String[2];
		try {
			element = elementLocator(step[3], step[4], 0);
			if (element.isSelected()) {
				arrStepStatus[0] = "Passed";
				arrStepStatus[1] = "Web Element is selected.";
				log.info("Web Element is selected.");
			} else {
				arrStepStatus[0] = "Failed";
				arrStepStatus[1] = "Web Element is not selected.";
				log.info("Web Element is not selected.");
			}

		} catch (Exception e) {
			arrStepStatus[0] = "Failed";
			arrStepStatus[1] = e.getMessage().toString();
		}

		return arrStepStatus;
	}

	// check if check box is not checked
	public String[] isUnchecked(String[] step) {
		String[] arrStepStatus = new String[2];
		try {
			element = elementLocator(step[3], step[4], 0);
			if (element.isSelected()) {
				arrStepStatus[0] = "Failed";
				arrStepStatus[1] = "Web Element is not selected.";
				log.info("Web Element is not selected.");
			} else {
				arrStepStatus[0] = "Passed";
				arrStepStatus[1] = "Web Element is selected.";
				log.info("Web Element is selected.");
			}

		} catch (Exception e) {
			arrStepStatus[0] = "Failed";
			arrStepStatus[1] = e.getMessage().toString();
		}

		return arrStepStatus;
	}

	public String[] check(String[] step) {
		String[] arrStepStatus = new String[2];
		try {
			element = elementLocator(step[3], step[4], 0);
			if (element.isSelected()) {
				arrStepStatus[0] = "Passed";
				arrStepStatus[1] = "Web Element is already checked.";
				log.info("Web Element is already checked.");
			} else {
				if (!element.isEnabled()) {
					Thread.sleep(3000);
				}
				arrStepStatus[0] = "Passed";
				element.click();
				arrStepStatus[1] = "Web Element is checked.";
				log.info("Web Element is checked.");
			}

		} catch (Exception e) {
			arrStepStatus[0] = "Failed";
			arrStepStatus[1] = e.getMessage().toString();
		}

		return arrStepStatus;
	}

	public String[] unCheck(String[] step) {
		String[] arrStepStatus = new String[2];
		try {
			element = elementLocator(step[3], step[4], 0);
			if (element.isSelected()) {
				if (!element.isEnabled()) {
					Thread.sleep(3000);
				}
				element.click();
				arrStepStatus[0] = "Passed";
				arrStepStatus[1] = "Web Element is unchecked.";
				log.info("Web Element is already checked.");
			} else {
				arrStepStatus[0] = "Passed";
				arrStepStatus[1] = "Web Element is already unchecked.";
				log.info("Web Element is already unchecked.");
			}

		} catch (Exception e) {
			arrStepStatus[0] = "Failed";
			arrStepStatus[1] = e.getMessage().toString();
		}

		return arrStepStatus;
	}

	// check if web element is displayed
	public String[] isDisplayed(String[] step) {
		String[] arrStepStatus = new String[2];
		try {
			element = elementLocator(step[3], step[4], 0);
			if (element.isDisplayed()) {
				arrStepStatus[0] = "Passed";
				arrStepStatus[1] = "Web Element is displayed.";
				log.info("Web Element is displayed.");
			} else {
				arrStepStatus[0] = "Failed";
				arrStepStatus[1] = "Web Element is not displayed.";
				log.info("Web Element is not displayed.");
			}
		} catch (Exception e) {
			arrStepStatus[0] = "Failed";
			arrStepStatus[1] = e.getMessage().toString();
		}

		return arrStepStatus;
	}
	// check if web element is displayed
	public String[] isNotDisplayed(String[] step) {
		String[] arrStepStatus = new String[2];
		try {
			element = elementLocator(step[3], step[4], 0);
			if (element.isDisplayed()) {
				arrStepStatus[0] = "Failed";
				arrStepStatus[1] = "Web Element is displayed.";
				log.info("Web Element is displayed.");
			} else {
				arrStepStatus[0] = "Passed";
				arrStepStatus[1] = "Web Element is not displayed.";
				log.info("Web Element is not displayed.");
			}
		} catch (Exception e) {
			arrStepStatus[0] = "Failed";
			arrStepStatus[1] = e.getMessage().toString();
		}

		return arrStepStatus;
	}

	// back one page
	public String[] pageBack(String[] step) {
		String[] arrStepStatus = new String[2];
		try {
			webDriver.navigate().back();
			arrStepStatus[0] = "Passed";
			arrStepStatus[1] = "Page is navigated back.";
			log.info("Page is navigated back.");
		} catch (Exception e) {
			arrStepStatus[0] = "Failed";
			arrStepStatus[1] = e.getMessage().toString();
		}

		return arrStepStatus;
	}

	// navigate page forward
	public String[] pageForward(String[] step) {
		String[] arrStepStatus = new String[2];
		try {
			webDriver.navigate().forward();
			arrStepStatus[0] = "Passed";
			arrStepStatus[1] = "Page is navigated forward.";
			log.info("Page is navigated forward.");
		} catch (Exception e) {
			arrStepStatus[0] = "Failed";
			arrStepStatus[1] = e.getMessage().toString();
		}
		return arrStepStatus;
	}

	public String[] validatePageTitle(String[] step) {
		String[] arrStepStatus = new String[2];
		try {

			String strTitle = webDriver.getTitle();
			if (strTitle.contentEquals(step[7])) {
				arrStepStatus[0] = "Passed";
				arrStepStatus[1] = "Actual: " + strTitle + " = Expected: " + paramVal;
				log.info("Actual: " + strTitle + " = Expected: " + paramVal);
			} else {
				arrStepStatus[0] = "Failed";
				arrStepStatus[1] = "[Not Equal] Actual: " + strTitle + " <> Expected: " + paramVal;
				log.info("[Not Equal] Actual: " + strTitle + " <> Expected: " + paramVal);
			}
		} catch (Exception e) {
			arrStepStatus[0] = "Failed";
			arrStepStatus[1] = e.getMessage().toString();
		}

		return arrStepStatus;
	}

	// select item from drop down
	public String[] selectFromDropDown(String[] step) {
		String[] arrStepStatus = new String[2];
		try {
			element = elementLocator(step[3], step[4], 0);
			Select selDD = new Select(element);
			selDD.selectByVisibleText(step[7]);
			Thread.sleep(5000);
			defaultBorderTemp = defaultBorder;
			element = elementLocator(step[3], step[4], 0);
			defaultBorder = defaultBorderTemp;
			selDD = new Select(element);
			WebElement option = selDD.getFirstSelectedOption();
			log.info("Selected: " + option.getText());
			arrStepStatus[0] = "Passed";
			arrStepStatus[1] = option.getText() + " is selected";

		} catch (Exception e) {
			log.info("Dropdown value not found. " + paramVal);
			arrStepStatus[0] = "Failed";
			arrStepStatus[1] = e.getMessage().toString();
		}

		return arrStepStatus;
	}

	// select item from drop down
	public String[] selectDropDownIndex(String[] step) {
		String[] arrStepStatus = new String[2];
		try {
			element = elementLocator(step[3], step[4], 0);
			if (step[7] != null && step[7].length() > 0) {
				Select selDD = new Select(element);
				selDD.selectByIndex(Integer.parseInt(step[7]));
				Thread.sleep(5000);
				defaultBorderTemp = defaultBorder;
				element = elementLocator(step[3], step[4], 0);
				defaultBorder = defaultBorderTemp;
				selDD = new Select(element);
				WebElement option = selDD.getFirstSelectedOption();
				log.info("Selected: " + option.getText());
				arrStepStatus[0] = "Passed";
				arrStepStatus[1] = option.getText() + " is selected";
			} else {
				arrStepStatus[0] = "Failed";
				arrStepStatus[1] = "Parameter is required.";
			}
		} catch (Exception e) {
			log.info("Error on parameter(must be integer)/Index not found");
			arrStepStatus[0] = "Failed";
			arrStepStatus[1] = e.getMessage().toString();
		}

		return arrStepStatus;
	}

	// select frame by using selector and string
	public String[] selectFrame(String[] step) {
		String[] arrStepStatus = new String[2];
		try {
			element = elementLocator(step[3], step[4], 0);
			unHighlight(element);
			webDriver.switchTo().frame(element);

			arrStepStatus[0] = "Passed";
			arrStepStatus[1] = "Frame is selected: " + element;
			log.info("Frame is selected: " + element);
		} catch (Exception e) {
			arrStepStatus[0] = "Failed";
			arrStepStatus[1] = e.getMessage().toString();
		}

		return arrStepStatus;
	}

	// unselect frame
	public String[] unSelectFrame(String[] step) {
		String[] arrStepStatus = new String[2];
		try {
			webDriver.switchTo().defaultContent();
			arrStepStatus[0] = "Passed";
			arrStepStatus[1] = "Frame is unselected.";
			log.info("Frame is unselected.");
		} catch (Exception e) {
			arrStepStatus[0] = "Failed";
			arrStepStatus[1] = e.getMessage().toString();
		}

		return arrStepStatus;
	}

	// switch to new window
	public String[] switchToNewWindow(String[] step) {
		String[] arrStepStatus = new String[2];
		try {
			String winHdl = "";
			// switch to new window
			for (String winHandle : webDriver.getWindowHandles()) {
				log.info("Window Handle : " + winHandle);
				winHdl = winHandle;
			}
			if (winHdl != "") {
				webDriver.switchTo().window(winHdl);
				arrStepStatus[0] = "Passed";
				arrStepStatus[1] = "Successful switch to new window.";
				log.info("Successful switch to new window.");
			} else {
				arrStepStatus[0] = "Failed";
				arrStepStatus[1] = "No window found.";
				log.info("No window found.");
			}

		} catch (Exception e) {
			arrStepStatus[0] = "Failed";
			arrStepStatus[1] = e.getMessage().toString();
		}

		return arrStepStatus;
	}

	// switch to main window
	public String[] switchToMainWindow(String[] step) {
		String[] arrStepStatus = new String[2];
		try {
			// switch to main window
			webDriver.switchTo().window(strWindowHandler);
			arrStepStatus[0] = "Passed";
			arrStepStatus[1] = "Successful switch to main window.";
			log.info("Successful switch to main window.");
		} catch (Exception e) {
			arrStepStatus[0] = "Failed";
			arrStepStatus[1] = e.getMessage().toString();
		}

		return arrStepStatus;
	}

	// refresh web page
	public String[] pageRefresh(String[] step) {
		String[] arrStepStatus = new String[2];
		try {
			webDriver.navigate().refresh();
			arrStepStatus[0] = "Passed";
			arrStepStatus[1] = "Web Page refreshed.";
			log.info("Web Page refreshed.");
		} catch (Exception e) {
			arrStepStatus[0] = "Failed";
			arrStepStatus[1] = e.getMessage().toString();
		}
		return arrStepStatus;
	}

	// validate image filename based on src
	public String[] validateImageFilename(String[] step) {
		String[] arrStepStatus = new String[2];
		try {
			element = elementLocator(step[3], step[4], 0);
			String elementSource = element.getAttribute("src");
			String[] split = elementSource.split("/");
			String filename = split[split.length - 1];
			if (filename.contentEquals(step[7])) {
				arrStepStatus[0] = "Passed";
				arrStepStatus[1] = "Filename is correct";
			} else {
				arrStepStatus[0] = "Failed";
				arrStepStatus[1] = "Filename NOT matched";
				log.info("Source: " + elementSource);
				log.info("Filename: " + filename);
			}

		} catch (Exception e) {
			arrStepStatus[0] = "Failed";
			arrStepStatus[1] = e.getMessage();
		}
		return arrStepStatus;
	}

	// upload file
	public String[] uploadFile(String[] step) {
		String[] arrStepStatus = new String[2];
		try {
			element = elementLocator(step[3], step[4], 0);
			element.sendKeys(step[7]);

			arrStepStatus[0] = "Passed";
			arrStepStatus[1] = "File upload complete. (" + paramVal + ")";
		} catch (Exception e) {
			arrStepStatus[0] = "Failed";
			arrStepStatus[1] = e.getMessage();
		}
		return arrStepStatus;
	}

	// get text from alert
	public String[] validateAlertText(String[] step) {
		String[] arrStepStatus = new String[2];
		try {

			String strAlertText = webDriver.switchTo().alert().getText();
			strAlertText = strAlertText.replaceAll("\\r\\n|\\r|\\n", " ");
			strAlertText = strAlertText.trim();

			if (strAlertText.contentEquals(step[7])) {
				arrStepStatus[0] = "Passed";
				arrStepStatus[1] = "Actual: " + strAlertText  + " = Expected: " + paramVal;
				log.info("Actual: " + strAlertText  + " = Expected: " + paramVal);
			} else {
				arrStepStatus[0] = "Failed";
				arrStepStatus[1] = "[Not Equal] Actual: " + strAlertText  + " <> Expected: " + paramVal;
				log.info("[Not Equal] Actual: " + strAlertText  + " <> Expected: " + paramVal);
			}
		} catch (Exception e) {
			arrStepStatus[0] = "Failed";
			arrStepStatus[1] = e.getMessage();
		}
		return arrStepStatus;
	}

	// accept alert
	public String[] acceptAlert(String[] step) {
		String[] arrStepStatus = new String[2];
		try {
			webDriver.switchTo().alert().accept();
			arrStepStatus[0] = "Passed";
			arrStepStatus[1] = "Alert accepted.";
		} catch (Exception e) {
			arrStepStatus[0] = "Failed";
			arrStepStatus[1] = e.getMessage();
		}
		return arrStepStatus;
	}

	// dismiss alert
	public String[] dismissAlert(String[] step) {
		String[] arrStepStatus = new String[2];
		try {
			webDriver.switchTo().alert().dismiss();
			arrStepStatus[0] = "Passed";
			arrStepStatus[1] = "Alert dismissed.";
		} catch (Exception e) {
			arrStepStatus[0] = "Failed";
			arrStepStatus[1] = e.getMessage();
		}
		return arrStepStatus;
	}

	// type on input box
	public String[] typeOnInputBox(String[] step) {
		String[] arrStepStatus = new String[2];
		try {
			webDriver.switchTo().alert().sendKeys(step[7]);
			arrStepStatus[0] = "Passed";
			arrStepStatus[1] = "String entered: " + paramVal;
		} catch (Exception e) {
			arrStepStatus[0] = "Failed";
			arrStepStatus[1] = e.getMessage();
		}
		return arrStepStatus;
	}

	// bypass next step if failed
	public String[] isDisplayedSkipNext(String[] step) {
		String[] arrStepStatus = new String[2];
		try {
			if (step[7] != null && step[7].length() > 0) {
				element = elementLocator(step[3], step[4], Integer.parseInt(step[7]));
			} else {
				element = elementLocator(step[3], step[4], 5);
			}

			if (element == null) {
				arrStepStatus[0] = "Passed";
				arrStepStatus[1] = "Element not Displayed/Not Found.";
			} else if (element.isDisplayed() & element.getCssValue("display") != "none") {
				arrStepStatus[0] = "SkipNext";
				arrStepStatus[1] = "Element Displayed.";
				log.info("Element is displayed: " + element.isDisplayed());
				log.info("Element CSS display: " + element.getCssValue("display"));
			} else {
				arrStepStatus[0] = "Passed";
				arrStepStatus[1] = "Element not Displayed/Not Found. CSS display:" + element.getCssValue("display")
						+ " isDisplayed:" + element.isDisplayed();
				log.info("Element is displayed: " + element.isDisplayed());
				log.info("Element CSS display: " + element.getCssValue("display"));
			}

		} catch (Exception e) {
			arrStepStatus[0] = "Passed";
			arrStepStatus[1] = e.getMessage();
		}
		return arrStepStatus;
	}

	// bypass next step if failed
	public String[] ifTextContainsSkipNext(String[] step) {
		String[] arrStepStatus = new String[2];
		try {
			element = elementLocator(step[3], step[4], 0);

			if (element == null) {
				arrStepStatus[0] = "Failed";
				arrStepStatus[1] = "Element not Displayed/Not Found.";
				return arrStepStatus;
			}
			String strElemText = element.getText();
			strElemText = strElemText.replaceAll("\\r\\n|\\r|\\n", " ");
			strElemText = strElemText.trim();

			log.info("Get Element text:  " + strElemText);
			if (step[7] != null && step[7].length() > 0) {
				Pattern pattern = Pattern.compile("(" + step[7] + ")");
				Matcher matcher = pattern.matcher(strElemText);
				if (matcher.find()) {
					log.info("RegEx Matched:  " + matcher.group(1));
					arrStepStatus[0] = "SkipNext";
					arrStepStatus[1] = "Text Found :" + strElemText + "Matched to: " + paramVal;
				} else {
					arrStepStatus[0] = "Passed";
					arrStepStatus[1] = "Text Found :" + strElemText + "Not Matched to: " + paramVal;
					log.info("RegEx Not Found.");
				}

			} else {
				if (step[7].contentEquals(strElemText)) {
					arrStepStatus[0] = "SkipNext";
					arrStepStatus[1] = "Text Found :" + strElemText + "Matched to: " + paramVal;
				} else {
					arrStepStatus[0] = "Passed";
					arrStepStatus[1] = "Text Found :" + strElemText + "Not Matched to: " + paramVal;
				}
			}

		} catch (Exception e) {
			arrStepStatus[0] = "Passed";
			arrStepStatus[1] = e.getMessage();
		}
		return arrStepStatus;
	}

	// bypass next step if failed
	public String[] skipNextSteps(String[] step) {
		String[] arrStepStatus = new String[2];
		try {
			if (step[7].matches("^\\d+$")) {
				log.info("Skipping next " + paramVal + " steps.");
				arrStepStatus[0] = "Passed";
				arrStepStatus[1] = "Skipping " + paramVal + " steps.";
			} else {
				arrStepStatus[0] = "Failed";
				arrStepStatus[1] = "invalid parameter value, Integer Only.";

			}
		} catch (Exception e) {
			arrStepStatus[0] = "Failed";
			arrStepStatus[1] = e.getMessage();
		}
		return arrStepStatus;
	}

	// get text value and stored in variable
	public String[] getTextToVar(String[] step) {
		String[] arrStepStatus = new String[2];
		try {
			element = elementLocator(step[3], step[4], 0);
			String strElemText = element.getText();
			strElemText = strElemText.replaceAll("\\r\\n|\\r|\\n", " ");
			strElemText = strElemText.trim();
			log.info("Get Element text:  " + strElemText);
			if (step[7] != null && step[7].length() > 0) {
				Pattern pattern = Pattern.compile(step[7]);
				Matcher matcher = pattern.matcher(strElemText);
				String g = "";
				try {
					while (matcher.find()) {
						g = g + matcher.group(1);
					}
					log.info("RegEx Matched:  " + g);
					variables.put(step[6], g);
				} catch (Exception n) {
				}
			} else {
				variables.put(step[6], strElemText);
			}

			arrStepStatus[0] = "Passed";
			arrStepStatus[1] = "Element text stored to variable " + step[6] + ":" + variables.get(step[6]);
		} catch (Exception e) {
			arrStepStatus[0] = "Failed";
			arrStepStatus[1] = e.getMessage().toString();
		}
		return arrStepStatus;
	}

	// declare param name and stored param value in variable
	public String[] declareVar(String[] step) {
		String[] arrStepStatus = new String[2];
		try {

			if (step[6] != null && step[6].length() > 0) {
				variables.put(step[6], step[7]);
				arrStepStatus[0] = "Passed";
				arrStepStatus[1] = "parameter value stored to variable " + step[6] + ":" + variables.get(step[6]);
			} else {
				log.info("Invalid parameter name.");
				arrStepStatus[0] = "Failed";
				arrStepStatus[1] = "Parameter name Empty.";
			}

		} catch (Exception e) {
			arrStepStatus[0] = "Failed";
			arrStepStatus[1] = e.getMessage().toString();
		}
		return arrStepStatus;
	}

	// compare parameter value skip next
	public String[] isParamContainsSkipNext(String[] step) {
		String[] arrStepStatus = new String[2];
		try {
			String[] paramArr = step[7].split("\\|", 2);

			if (paramArr.length > 1) {
				Pattern pattern = Pattern.compile(paramArr[0].trim());
				Matcher matcher = pattern.matcher(paramArr[1].trim());
				if (matcher.find()) {
					arrStepStatus[0] = "SkipNext";
					arrStepStatus[1] = "Parameter Matched.";
					log.info("RegEx Matched:" + matcher.group(1));
				} else {
					arrStepStatus[0] = "Passed";
					arrStepStatus[1] = "Parameter Not Matched.";
					log.info("RegEx Not Found.");
				}

			} else {
				log.info("Invalid parameter. <Regex> | <param>");
				arrStepStatus[0] = "Failed";
				arrStepStatus[1] = "Parameter invalid. <Regex> | <param>";
			}

		} catch (Exception e) {
			arrStepStatus[0] = "Failed";
			arrStepStatus[1] = e.getMessage().toString();
		}
		return arrStepStatus;
	}

	// math eval string value
	public String[] mathEval(String[] step) {
		String[] arrStepStatus = new String[2];
		try {
			if (step[6] != null && step[6].length() > 0) {
				String[] param = step[7].split("\\|", 2);
				if (param.length > 1) {
					ScriptEngineManager mgr = new ScriptEngineManager();
					ScriptEngine engine = mgr.getEngineByName("JavaScript");
					param[1] = param[1].replaceAll(",", "");
					NumberFormat formatter = new DecimalFormat(param[0]);
					String value = formatter.format(engine.eval(param[1]));
					variables.put(step[6], value);
					arrStepStatus[0] = "Passed";
					arrStepStatus[1] = "Math Eval value stored to variable " + step[6] + ":" + value;
				} else {
					log.info("invalid parameter");
					arrStepStatus[0] = "Failed";
					arrStepStatus[1] = "Invalid parameter. <Number Format>|<Equation>";
				}
			} else {
				log.info("Parameter name Empty.");
				arrStepStatus[0] = "Failed";
				arrStepStatus[1] = "Parameter name Empty.";
			}

		} catch (Exception e) {
			arrStepStatus[0] = "Failed";
			arrStepStatus[1] = e.getMessage().toString();
		}
		return arrStepStatus;
	}

	// math eval string value
	public String[] validateNumber(String[] step) {
		String[] arrStepStatus = new String[2];
		try {
			element = elementLocator(step[3], step[4], 0);
			String strElemText = element.getText();
			strElemText = strElemText.replaceAll("\\r\\n|\\r|\\n", " ");
			strElemText = strElemText.trim();

			if (step[6] != null && step[6].length() > 0) {
				step[7] = step[7].replaceAll(",", "");
				strElemText = strElemText.replaceAll(",|\\$", "");
				float a = Float.parseFloat(strElemText);
				float b = Float.parseFloat(step[7]);
				if (compareNumber(a, b) != 0) {
					arrStepStatus[0] = "Failed";
					arrStepStatus[1] = "[Not Equal] Actual: " + strElemText + " <> Expected: " + paramVal;
				} else {
					arrStepStatus[0] = "Passed";
					arrStepStatus[1] = "Actual: " + strElemText + " = Expected: " + paramVal;
				}

			} else {
				log.info("Parameter name Empty.");
				arrStepStatus[0] = "Failed";
				arrStepStatus[1] = "Parameter name Empty.";
			}

		} catch (Exception e) {
			arrStepStatus[0] = "Failed";
			arrStepStatus[1] = e.getMessage().toString();
		}
		return arrStepStatus;
	}
	// get machine timestamp
	public String[] getTimestamp(String[] step) {
		String[] arrStepStatus = new String[2];
		try {
			if (step[6] != null && step[6].length() > 0) {
				String ts = getDT(step[7]);
				if(ts == null || ts.contentEquals("")) {
					arrStepStatus[0] = "Failed";
					arrStepStatus[1] = "Error generating timestamp.";
				} else {
					log.info("Timestamp:  " + ts);
					variables.put(step[6], ts);
					arrStepStatus[0] = "Passed";
					arrStepStatus[1] = "Timestamp:"+ ts;
				}
				
			} else {
				log.info("Parameter name Empty.");
				arrStepStatus[0] = "Failed";
				arrStepStatus[1] = "Parameter name Empty.";
			}
			
		} catch (Exception e) {
			arrStepStatus[0] = "Failed";
			arrStepStatus[1] = e.getMessage().toString();
		}
		return arrStepStatus;
	}
	// get time difference
	public String[] getTimeDifference(String[] step) {
		String[] arrStepStatus = new String[2];
		try {
			if (step[6] != null && step[6].length() > 0) {
				
				String  ts = getTimeDiff(step[7]);
				if(ts == null || ts.contentEquals("")) {
					arrStepStatus[0] = "Failed";
					arrStepStatus[1] = "Error generating timestamp.";
				} else {
					log.info("Time difference in seconds:  " + ts);
					variables.put(step[6], ts);
					arrStepStatus[0] = "Passed";
					arrStepStatus[1] = "Time difference in seconds:"+ ts;
				}
				
			} else {
				log.info("Parameter name Empty.");
				arrStepStatus[0] = "Failed";
				arrStepStatus[1] = "Parameter name Empty.";
			}
			
		} catch (Exception e) {
			arrStepStatus[0] = "Failed";
			arrStepStatus[1] = e.getMessage().toString();
		}
		return arrStepStatus;
	}
	// date eval
	public String[] dateEval(String[] step) {
		String[] arrStepStatus = new String[2];
		try {
			if (step[6] != null && step[6].length() > 0) {
				String ts = getDatePlusMinus(step[7]);
				if(ts == null || ts.contentEquals("")) {
					arrStepStatus[0] = "Failed";
					arrStepStatus[1] = "Error generating timestamp. invalid format/parameters: <format>|<date>|< + or - >|<seconds>|<output format>";
				} else {
					log.info("DateTime:  " + ts);
					variables.put(step[6], ts);
					arrStepStatus[0] = "Passed";
					arrStepStatus[1] = "DateTime:"+ ts;
				}
				
			} else {
				log.info("Parameter name Empty.");
				arrStepStatus[0] = "Failed";
				arrStepStatus[1] = "Parameter name Empty.";
			}
			
		} catch (Exception e) {
			arrStepStatus[0] = "Failed";
			arrStepStatus[1] = e.getMessage().toString();
		}
		return arrStepStatus;
	}
	// get text value and stored in variable
	public String[] getAttValToVar(String[] step) {
		String[] arrStepStatus = new String[2];
		try {
			element = elementLocator(step[3], step[4], 0);
			String[] paramArr = step[7].split("\\|", 2);
			String strElemText = "";

			if (paramArr.length > 1) {
				strElemText = element.getAttribute(paramArr[0]);
				Pattern pattern = Pattern.compile(paramArr[1]);
				Matcher matcher = pattern.matcher(strElemText);
				try {
					strElemText="";
					while (matcher.find()) {
						strElemText = strElemText + matcher.group(1);
					}
					log.info("RegEx Matched:  " + strElemText);
				} catch (Exception n) { }

			} else {
				strElemText = element.getAttribute(step[7]);
			}

			strElemText = strElemText.replaceAll("\\r\\n|\\r|\\n", " ");
			strElemText = strElemText.trim();
			log.info("Get Element text: " + strElemText);
			if (step[7] != null && step[7].length() > 0) {
				variables.put(step[6], strElemText);
				arrStepStatus[0] = "Passed";
				arrStepStatus[1] = "Element text stored to variable " + step[6] + ":" + variables.get(step[6]);
			} else {
				log.info("Element attribute not found.");
				arrStepStatus[0] = "Failed";
				arrStepStatus[1] = "Element attribute not found.";
			}
		} catch (Exception e) {
			arrStepStatus[0] = "Failed";
			arrStepStatus[1] = e.getMessage().toString();
		}
		return arrStepStatus;
	}
	// validate current loaded URL
	public String[] getURLToVar(String[] step) {
		String[] arrStepStatus = new String[2];
		try {
			String strURL = webDriver.getCurrentUrl();
			if (step[6] != null && step[6].length() > 0) {
				variables.put(step[6], strURL);
				arrStepStatus[0] = "Passed";
				arrStepStatus[1] = "URL text stored to variable " + step[6] + ":" + variables.get(step[6]);
			} else {
				arrStepStatus[0] = "Failed";
				arrStepStatus[1] = "Parameter name is required.";
			}
		} catch (Exception e) {
			arrStepStatus[0] = "Failed";
			arrStepStatus[1] = e.getMessage().toString();
		}

		return arrStepStatus;
	}

	// get window handler of current window and stored in variable
	public String[] getWindowHandle(String[] step) {
		String[] arrStepStatus = new String[2];
		try {
			String winHandle = webDriver.getWindowHandle();
			log.info("Current Window Handle:  " + winHandle);

			variables.put(step[6], winHandle);

			arrStepStatus[0] = "Passed";
			arrStepStatus[1] = "Window handle stored to variable " + step[6];
		} catch (Exception e) {
			arrStepStatus[0] = "Failed";
			arrStepStatus[1] = e.getMessage().toString();
		}
		return arrStepStatus;
	}

	// switch to new window
	public String[] switchToWindow(String[] step) {
		String[] arrStepStatus = new String[2];
		try {
			log.info("Switch to Window Handle : " + paramVal);
			webDriver.switchTo().window(step[7]);
			log.info("Successful switch to window handle : " + paramVal);
			arrStepStatus[0] = "Passed";
			arrStepStatus[1] = "Successful switch to window handle :" + paramVal;
		} catch (Exception e) {
			arrStepStatus[0] = "Failed";
			arrStepStatus[1] = e.getMessage().toString();
		}

		return arrStepStatus;
	}

	// wait for specific seconds
	public String[] waitFor(String[] step) {
		String[] arrStepStatus = new String[2];
		try {
			Thread.sleep((Integer.parseInt(step[7])) * 1000);
			arrStepStatus[0] = "Passed";
			arrStepStatus[1] = "Waited for: " + step[7] + " seconds.";
		} catch (Exception e) {
			arrStepStatus[0] = "Failed";
			arrStepStatus[1] = e.getMessage();
		}
		return arrStepStatus;
	}
	// wait for specific seconds
	public String[] userConfirm(String[] step) {
		String[] arrStepStatus = new String[2];
		try {
			 Object[] options = {"Continue"};
			    int n = JOptionPane.showOptionDialog(null,
			    			step[7],"Paused",
			                   JOptionPane.PLAIN_MESSAGE,
			                   JOptionPane.QUESTION_MESSAGE,
			                   null,
			                   options,
			                   options[0]);
			if(n==0) {
				arrStepStatus[0] = "Passed";
				arrStepStatus[1] = "Paused: " + step[7] + ".";
				Thread.sleep(1000);
			} else {
				arrStepStatus[0] = "Failed";
				arrStepStatus[1] = "Confirm dialog is closed";
			}
		} catch (Exception e) {
			arrStepStatus[0] = "Failed";
			arrStepStatus[1] = e.getMessage();
		}
		return arrStepStatus;
	}

	// Close current browser window
	public String[] closeWindow(String[] step) {
		String[] arrStepStatus = new String[2];
		try {
			webDriver.close();
			arrStepStatus[0] = "Passed";
			arrStepStatus[1] = "window is now closed.";
		} catch (Exception e) {
			arrStepStatus[0] = "Failed";
			arrStepStatus[1] = e.getMessage().toString();
		}

		return arrStepStatus;
	}

	// Close current browser window
	public String[] closeWinHandle(String[] step) {
		String[] arrStepStatus = new String[2];
		try {
			webDriver.switchTo().window(step[7]);
			webDriver.close();
			arrStepStatus[0] = "Passed";
			arrStepStatus[1] = "window is now closed.";
		} catch (Exception e) {
			arrStepStatus[0] = "Passed";
			arrStepStatus[1] = e.getMessage().toString();
		}

		return arrStepStatus;
	}
	// Close current browser window
	public String[] maximizeWindow(String[] step) {
		String[] arrStepStatus = new String[2];
		try {
			webDriver.manage().window().maximize();
			arrStepStatus[0] = "Passed";
			arrStepStatus[1] = "window is now maximized.";
		} catch (Exception e) {
			arrStepStatus[0] = "Failed";
			arrStepStatus[1] = e.getMessage().toString();
		}

		return arrStepStatus;
	}

	// validate date range ( date today - date parameter)
	public String[] valDateRange(String[] step) {
		String[] arrStepStatus = new String[2];
		try {
			element = elementLocator(step[3], step[4], 0);
			String strElemText = element.getText();
			strElemText = strElemText.replaceAll("\\r\\n|\\r|\\n", " ");
			strElemText = strElemText.trim();
			log.info("Get Element date:  " + strElemText);

			String[] paramArr = step[7].split("\\|");
			SimpleDateFormat df = new SimpleDateFormat(paramArr[0]);

			Date dateToday = df.parse(strElemText);
			df.applyPattern("yyyy-MM-dd");
			String newDateString = df.format(dateToday);
			LocalDate dateMin = LocalDate.now();
			LocalDate dateMax = dateMin;
			LocalDate elemDate = LocalDate.parse(newDateString);

			if (paramArr[1].contentEquals(">")) {
				if (paramArr[3].contentEquals("D")) {
					dateMax = dateMin.plusDays(Integer.parseInt(paramArr[2]));
				} else if (paramArr[3].contentEquals("M")) {
					dateMax = dateMin.plusMonths(Integer.parseInt(paramArr[2]));
				} else if (paramArr[3].contentEquals("Y")) {
					dateMax = dateMin.plusYears(Integer.parseInt(paramArr[2]));
				} else {
					arrStepStatus[0] = "Failed";
					arrStepStatus[1] = "Parameter is invalid[3].<M or D or Y>";
					return arrStepStatus;
				}
				if (elemDate.compareTo(dateMin) >= 0 && elemDate.compareTo(dateMax) <= 0) {
					arrStepStatus[0] = "Passed";
					arrStepStatus[1] = "Element date is between date range: " + dateMin + " - " + dateMax;
				} else {
					arrStepStatus[0] = "Failed";
					arrStepStatus[1] = "Element date is NOT between date range: " + dateMin + " - " + dateMax;
				}

			}  else if (paramArr[1].contentEquals(">>")) {
				if(elemDate.compareTo(dateMax) < 0) {
					arrStepStatus[0] = "Failed";
					arrStepStatus[1] = "Element date is less than date today";
					return arrStepStatus;
				}
				if (paramArr[3].contentEquals("D")) {
					dateMax = dateMin.plusDays(Integer.parseInt(paramArr[2]));
				} else if (paramArr[3].contentEquals("M")) {
					dateMax = dateMin.plusMonths(Integer.parseInt(paramArr[2]));
				} else if (paramArr[3].contentEquals("Y")) {
					dateMax = dateMin.plusYears(Integer.parseInt(paramArr[2]));
				} else {
					arrStepStatus[0] = "Failed";
					arrStepStatus[1] = "Parameter is invalid[3].<M or D or Y>";
					return arrStepStatus;
				}
				if (elemDate.compareTo(dateMin) >= 0 && elemDate.compareTo(dateMax) <= 0) {
					arrStepStatus[0] = "Failed";
					arrStepStatus[1] = "Element date is between date range: " + dateMin + " - " + dateMax;
				} else {
					arrStepStatus[0] = "Passed";
					arrStepStatus[1] = "Element date is greater than: " + dateMax;
				}

			}else if (paramArr[1].contentEquals("<")) {
				if (paramArr[3].contentEquals("D")) {
					dateMax = dateMin.minusDays(Integer.parseInt(paramArr[2]));
				} else if (paramArr[3].contentEquals("M")) {
					dateMax = dateMin.minusMonths(Integer.parseInt(paramArr[2]));
				} else if (paramArr[3].contentEquals("Y")) {
					dateMax = dateMin.minusYears(Integer.parseInt(paramArr[2]));
				} else {
					arrStepStatus[0] = "Failed";
					arrStepStatus[1] = "Parameter is invalid[3].<M or D or Y>";
					return arrStepStatus;
				}
				if (elemDate.compareTo(dateMax) >= 0 && elemDate.compareTo(dateMin) <= 0) {
					arrStepStatus[0] = "Passed";
					arrStepStatus[1] = "Element date is between date range: " + dateMax + " - " + dateMin;
				} else {
					arrStepStatus[0] = "Failed";
					arrStepStatus[1] = "Element date is NOT between date range: " + dateMax + " - " + dateMin;
				}
			} else if (paramArr[1].contentEquals("<<")) {
				if(elemDate.compareTo(dateMax) > 0) {
					arrStepStatus[0] = "Failed";
					arrStepStatus[1] = "Element date is greater than date today";
					return arrStepStatus;
				}
				if (paramArr[3].contentEquals("D")) {
					dateMax = dateMin.minusDays(Integer.parseInt(paramArr[2]));
				} else if (paramArr[3].contentEquals("M")) {
					dateMax = dateMin.minusMonths(Integer.parseInt(paramArr[2]));
				} else if (paramArr[3].contentEquals("Y")) {
					dateMax = dateMin.minusYears(Integer.parseInt(paramArr[2]));
				} else {
					arrStepStatus[0] = "Failed";
					arrStepStatus[1] = "Parameter is invalid[3].<M or D or Y>";
					return arrStepStatus;
				}
				if (elemDate.compareTo(dateMax) >= 0 && elemDate.compareTo(dateMin) <= 0) {
					arrStepStatus[0] = "Failed";
					arrStepStatus[1] = "Element date is between date range: " + dateMax + " - " + dateMin;
				} else {
					arrStepStatus[0] = "Passed";
					arrStepStatus[1] = "Element date is less than: " + dateMax;	
				}
			} else if (paramArr[1].contentEquals("=")) {
				if (dateToday.toString().contentEquals(strElemText)) {
					arrStepStatus[0] = "Passed";
					arrStepStatus[1] = "Dates are Equal: Actual: " + strElemText + " | Expected: " + dateToday;
				} else {
					arrStepStatus[0] = "Failed";
					arrStepStatus[1] = "Date compare not Equal: Actual: " + strElemText + " | Expected: " + dateToday;
				}
			} else {
				arrStepStatus[0] = "Failed";
				arrStepStatus[1] = "Parameter is invalid[1]. << or > or =>";
			}

		} catch (Exception e) {
			arrStepStatus[0] = "Failed";
			arrStepStatus[1] = e.getMessage();
		}
		return arrStepStatus;
	}
	// Load URL to browser
	public String[] executeModule(String[] step) {
		String[] arrStepStatus = new String[2];
		Map<String, String> parameter = new HashMap<String, String>();
		try {
			String[] paramArr = step[7].split("\\|", 2);
			if (paramArr.length > 1) {
				String[] paramArr2 = paramArr[1].split("\\|");
				for (String p : paramArr2) {
					String[] a = p.split("=", 2);
					if (a.length > 1) {
						parameter.put(a[0], a[1]);
					}
				}
			}

			List<String[]> listSteps = dbProcess.getModularSteps(paramArr[0]);
			if (listSteps.size() < 1) {
				arrStepStatus[0] = "Failed";
				arrStepStatus[1] = "Modular Action Not Found!";
			}
			int skip = 0;
			String stepSkip = "";
			for (String[] testStep : listSteps) {
				
				log.info("------Modular Action : " + paramArr[0] + "-----------");
				log.info("Modular Action : Step ID : " + testStep[0]);
				log.info("Modular Action : Step Description : [" + paramArr[0] + "] " + testStep[2]);
				log.info("Modular Action : Element : " + testStep[3] + ":" + testStep[4]);
				log.info("Modular Action : Action : " + testStep[5]);
				log.info("Modular Action : Param : " + testStep[7]);
				log.info("Modular Action : Screenshot : " + testStep[8]);
				if (parameter.get(testStep[6]) != null) {
					testStep[7] = parameter.get(testStep[6]);
				}
				arrStepStatus[0] = "";
				arrStepStatus[1] = "";
				if (skip > 0) {
					skip = skip - 1;
					log.info("Step Result : Skipped.");
					log.info("Step Remarks : Skipped.");
					arrStepStatus[0] = "Passed";
					continue;
				}
				if (stepSkip.equalsIgnoreCase("Skipped")) {
					log.info("Step Result : " + stepSkip);
					log.info("Step Remarks : Prevoius step is to SkipNext.");
					stepSkip = "";
					arrStepStatus[0] = "Passed";
					continue;
				}

				arrStepStatus = seleniumModularAction(testStep, testcaseDir, testcaseName, moduleName);

				if (testStep[5].equalsIgnoreCase("skipNextSteps") && arrStepStatus[0].equalsIgnoreCase("Passed")) {
					skip = Integer.parseInt(testStep[7]);
				}
				log.info("Modular Action : Step Result : " + arrStepStatus[0]);
				log.info("Modular Action : Step Remarks : " + arrStepStatus[1]);
				if ((testStep[5].equalsIgnoreCase("isDisplayedSkipNext")
						|| testStep[5].equalsIgnoreCase("ifTextContainsSkipNext")
						|| testStep[5].equalsIgnoreCase("isParamContainsSkipNext"))
						&& arrStepStatus[0].equalsIgnoreCase("SkipNext")) {
					stepSkip = "Skipped";
				}
				if (arrStepStatus[0].equalsIgnoreCase("Failed")) {
					arrStepStatus[0] = "Failed";
					arrStepStatus[1] = "Modular step failed: " + testStep[2];
					webDriver.switchTo().window(strWindowHandler);
					break;
				}
			}

		} catch (Exception e) {
			arrStepStatus[0] = "Failed";
			arrStepStatus[1] = e.getMessage().toString();
		}
		webDriver.switchTo().window(strWindowHandler);
		return arrStepStatus;
	}
	
	//verify action - will not stop execution of testcase if return failed
	// check if element is present
	public String[] verifyElementIsPresent(String[] step) {
		String[] arrStepStatus = new String[2];
		try {
			element = elementLocator(step[3], step[4], 0);
			if (element != null) {
				arrStepStatus[0] = "Passed";
				arrStepStatus[1] = "Element is existing.";
				log.info("Element is existing.");
			} else {
				arrStepStatus[0] = "Failed";
				arrStepStatus[1] = "Element is does not exist.";
				log.info("Element is does not exist.");
			}

		} catch (Exception e) {
			arrStepStatus[0] = "Failed";
			arrStepStatus[1] = e.getMessage().toString();
		}

		return arrStepStatus;
	}
}
