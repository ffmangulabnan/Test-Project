package userInterface;

import java.io.File;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;

import org.apache.log4j.Logger;

import database.DBProcess;
import database.PasswordEncryptDecrypt;
import database.TestMachineConfig;

public class EmailClass {
	public EmailClass() {

	}
	private PasswordEncryptDecrypt encrypt = new PasswordEncryptDecrypt();
	private static final Logger log = Logger.getLogger(EmailClass.class);
	static TestMachineConfig dbConfig = new TestMachineConfig();
	private static String SMTP_HOST_NAME = dbConfig.getConfigValue("EMAIL_HOST");
	private static String SMTP_AUTH_USER = dbConfig.getConfigValue("EMAIL_USER");
	private static String SMTP_AUTH_PWD = dbConfig.getConfigValue("EMAIL_PASS");
	private static String SMTP_EMAIL_TO = dbConfig.getConfigValue("EMAIL_TO");

	public void sendEmail(String subjct, String runId, DBProcess dbProcess, String attachementPath) throws Exception {
		log.info("Sending Email report...");
		Properties props = new Properties();
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.host", SMTP_HOST_NAME);
		props.put("mail.smtp.port", dbConfig.getConfigValue("EMAIL_PORT"));
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		try {
			Authenticator auth = new SMTPAuthenticator();
			Session mailSession = Session.getDefaultInstance(props, auth);
			// uncomment for debugging infos to stdout
			// mailSession.setDebug(true);
			Transport transport = mailSession.getTransport();

			MimeMessage message = new MimeMessage(mailSession);

			Multipart multipart = new MimeMultipart();

			BodyPart body = new MimeBodyPart();
			body.setContent(emailReportBody(runId, dbProcess), "text/html");
			multipart.addBodyPart(body);

			// if xlsx report generated, will attach to email report
			File f = new File(attachementPath);
			if (f.exists()) {
				MimeBodyPart attachmentBodyPart = new MimeBodyPart();
				DataSource source = new FileDataSource(attachementPath);
				attachmentBodyPart.setDataHandler(new DataHandler(source));
				attachmentBodyPart.setFileName(f.getName());
				multipart.addBodyPart(attachmentBodyPart);
			} else {
				log.info("Test Run Summary.xlsx report not exist.");
			}

			message.setContent(multipart);
			message.setFrom(new InternetAddress(SMTP_AUTH_USER));
			message.setSubject("Overall Automation Test Result: " + subjct);

			String[] recipient = SMTP_EMAIL_TO.split(";");
			for (String rec : recipient) {
				try {
					log.info("Sending report to : " + rec);
					message.addRecipient(Message.RecipientType.TO, new InternetAddress(rec));
				} catch (Exception e) {
					/* do nothing */}
			}

			if (dbConfig.getConfigValue("EMAIL_SEND_ME").contentEquals("Y")) {
				try {
					message.addRecipient(Message.RecipientType.CC, new InternetAddress(SMTP_AUTH_USER));
				} catch (Exception e) {
					/* do nothing */ }
			}

			transport.connect();
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
			log.info("Email report Sent!.");
		} catch (Exception mex) {
			log.info("Sent message Failed!: " + mex.getMessage());
		}
	}

	public String emailReportBody(String runId, DBProcess dbProcess) {
		// public String emailReportBody(String[][] tableData){
		String startTime = dbProcess.getStartTime(runId);
		String endTime = dbProcess.getEndTime(runId);
		String body = "<html xmlns:v='urn:schemas-microsoft-com:vml'"
				+ "xmlns:o='urn:schemas-microsoft-com:office:office'"
				+ "xmlns:w='urn:schemas-microsoft-com:office:word'" + "xmlns:x='urn:schemas-microsoft-com:office:excel'"
				+ "xmlns:m='http://schemas.microsoft.com/office/2004/12/omml'"
				+ "xmlns='http://www.w3.org/TR/REC-html40'>" + "<head>" + "<style>" + "<!--" + " /* Font Definitions */"
				+ " @font-face" + "	{font-family:Calibri;" + "	panose-1:2 15 5 2 2 2 4 3 2 4;" + "	mso-font-charset:0;"
				+ "	mso-generic-font-family:swiss;" + "	mso-font-pitch:variable;"
				+ "	mso-font-signature:-536870145 1073786111 1 0 415 0;}" + " /* Style Definitions */"
				+ " p.MsoNormal, li.MsoNormal, div.MsoNormal" + "	{mso-style-unhide:no;" + "	mso-style-qformat:yes;"
				+ "	mso-style-parent:'';" + "	margin:0in;" + "	margin-bottom:.0001pt;"
				+ "	mso-pagination:widow-orphan;" + "	font-size:11.0pt;" + "	font-family:'Calibri',sans-serif;"
				+ "	mso-ascii-font-family:Calibri;" + "	mso-ascii-theme-font:minor-latin;"
				+ "	mso-fareast-font-family:Calibri;" + "	mso-fareast-theme-font:minor-latin;"
				+ "	mso-hansi-font-family:Calibri;" + "	mso-hansi-theme-font:minor-latin;"
				+ "	mso-bidi-font-family:\"Times New Roman\";" + "	mso-bidi-theme-font:minor-bidi;}"
				+ "a:link, span.MsoHyperlink" + "	{mso-style-noshow:yes;" + "	mso-style-priority:99;"
				+ "	color:#0563C1;" + "	mso-themecolor:hyperlink;" + "	text-decoration:underline;"
				+ "	text-underline:single;}" + "a:visited, span.MsoHyperlinkFollowed" + "	{mso-style-noshow:yes;"
				+ "	mso-style-priority:99;" + "	color:#954F72;" + "	mso-themecolor:followedhyperlink;"
				+ "	text-decoration:underline;" + "	text-underline:single;}" + "span.EmailStyle17"
				+ "	{mso-style-type:personal-compose;" + "	mso-style-noshow:yes;" + "	mso-style-unhide:no;"
				+ "	mso-ansi-font-size:11.0pt;" + "	mso-bidi-font-size:11.0pt;" + "	font-family:'Calibri',sans-serif;"
				+ "	mso-ascii-font-family:Calibri;" + "	mso-ascii-theme-font:minor-latin;"
				+ "	mso-fareast-font-family:Calibri;" + "	mso-fareast-theme-font:minor-latin;"
				+ "	mso-hansi-font-family:Calibri;" + "	mso-hansi-theme-font:minor-latin;"
				+ "	mso-bidi-font-family:\"Times New Roman\";" + "	mso-bidi-theme-font:minor-bidi;"
				+ "	color:windowtext;}" + ".MsoChpDefault" + "	{mso-style-type:export-only;"
				+ "	mso-default-props:yes;" + "	font-family:'Calibri',sans-serif;" + "	mso-ascii-font-family:Calibri;"
				+ "	mso-ascii-theme-font:minor-latin;" + "	mso-fareast-font-family:Calibri;"
				+ "	mso-fareast-theme-font:minor-latin;" + "	mso-hansi-font-family:Calibri;"
				+ "	mso-hansi-theme-font:minor-latin;" + "	mso-bidi-font-family:\"Times New Roman\";"
				+ "	mso-bidi-theme-font:minor-bidi;}" + "@page WordSection1" + "	{size:8.5in 11.0in;"
				+ "	margin:1.0in 1.0in 1.0in 1.0in;" + "	mso-header-margin:.5in;" + "	mso-footer-margin:.5in;"
				+ "	mso-paper-source:0;}" + "div.WordSection1" + "	{page:WordSection1;}" + "-->" + "</style>"
				+ "<style>" + " /* Style Definitions */" + " table.MsoNormalTable" + "	{mso-style-name:'Table Normal';"
				+ "	mso-tstyle-rowband-size:0;" + "	mso-tstyle-colband-size:0;" + "	mso-style-noshow:yes;"
				+ "	mso-style-priority:99;" + "	mso-style-parent:'';" + "	mso-padding-alt:0in 5.4pt 0in 5.4pt;"
				+ "	mso-para-margin:0in;" + "	mso-para-margin-bottom:.0001pt;" + "	mso-pagination:widow-orphan;"
				+ "	font-size:11.0pt;" + "	font-family:'Calibri',sans-serif;" + "	mso-ascii-font-family:Calibri;"
				+ "	mso-ascii-theme-font:minor-latin;" + "	mso-hansi-font-family:Calibri;"
				+ "	mso-hansi-theme-font:minor-latin;}" + "</style>" + "</head>"
				+ "<body lang=EN-US link='#0563C1' vlink='#954F72' style='tab-interval:.5in'>"
				+ "<div class=WordSection1>"

				+ "<table class=MsoNormalTable border=0 cellspacing=0 cellpadding=0 width=309"
				+ " style='width:232.0pt;margin-left:-.15pt;border-collapse:collapse;mso-yfti-tbllook:"
				+ "1184;mso-padding-alt:0in 5.4pt 0in 5.4pt'>"
				+ "<tr style='mso-yfti-irow:0;mso-yfti-firstrow:yes;height:15.0pt'>"
				+ "<td width=116 nowrap valign=bottom style='width:87.0pt;border:solid windowtext 1.0pt;"
				+ "  mso-border-alt:solid windowtext .5pt;background:#002060;padding:0in 5.4pt 0in 5.4pt;"
				+ "height:15.0pt'>"
				+ "<p class=MsoNormal><a name=\"_MailOriginal\"><b><span style='mso-ascii-font-family:"
				+ "Calibri;mso-fareast-font-family:\"Times New Roman\";mso-hansi-font-family:Calibri;"
				+ "  mso-bidi-font-family:\"Times New Roman\";color:white'>Browser Used<o:p></o:p></span></b></a></p>"
				+ "</td>" + "<span style='mso-bookmark:_MailOriginal'></span>"
				+ "<td width=193 nowrap valign=bottom style='width:145.0pt;border:solid windowtext 1.0pt;"
				+ "  border-left:none;mso-border-top-alt:solid windowtext .5pt;mso-border-bottom-alt:"
				+ "solid windowtext .5pt;mso-border-right-alt:solid windowtext .5pt;padding:"
				+ "0in 5.4pt 0in 5.4pt;height:15.0pt'>"
				+ "<p class=MsoNormal><span style='mso-bookmark:_MailOriginal'><span "
				+ "  style='mso-ascii-font-family:Calibri;mso-fareast-font-family:\"Times New Roman\";"
				+ "mso-hansi-font-family:Calibri;mso-bidi-font-family:\"Times New Roman\";" + "color:black'>"
				+ dbProcess.getRunBrowser(runId) + "<o:p></o:p></span></span></p>" + "</td>"
				+ "  <span style='mso-bookmark:_MailOriginal'></span>" + "</tr>"
				+ "<tr style='mso-yfti-irow:1;height:15.0pt'>"
				+ "<td width=116 nowrap valign=bottom style='width:87.0pt;border:solid windowtext 1.0pt;"
				+ "  border-top:none;mso-border-left-alt:solid windowtext .5pt;mso-border-bottom-alt:"
				+ "solid windowtext .5pt;mso-border-right-alt:solid windowtext .5pt;background:"
				+ "#002060;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>"
				+ "<p class=MsoNormal><span style='mso-bookmark:_MailOriginal'><b><span "
				+ "  style='mso-ascii-font-family:Calibri;mso-fareast-font-family:\"Times New Roman\";"
				+ "mso-hansi-font-family:Calibri;mso-bidi-font-family:\"Times New Roman\";"
				+ "color:white'>Start Time<o:p></o:p></span></b></span></p>" + "</td>"
				+ "  <span style='mso-bookmark:_MailOriginal'></span>"
				+ "<td width=193 nowrap valign=bottom style='width:145.0pt;border-top:none;"
				+ "border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;"
				+ "mso-border-bottom-alt:solid windowtext .5pt;mso-border-right-alt:solid windowtext .5pt;"
				+ "  padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>"
				+ "<p class=MsoNormal><span style='mso-bookmark:_MailOriginal'><span "
				+ "style='mso-ascii-font-family:Calibri;mso-fareast-font-family:\"Times New Roman\";"
				+ "mso-hansi-font-family:Calibri;mso-bidi-font-family:\"Times New Roman\";" + "  color:black'>"
				+ startTime + "<o:p></o:p></span></span></p>" + "</td>"
				+ "<span style='mso-bookmark:_MailOriginal'></span>" + "</tr>"
				+ " <tr style='mso-yfti-irow:2;mso-yfti-lastrow:yes;height:15.0pt'>"
				+ "<td width=116 nowrap valign=bottom style='width:87.0pt;border:solid windowtext 1.0pt;"
				+ "border-top:none;mso-border-left-alt:solid windowtext .5pt;mso-border-bottom-alt:"
				+ "solid windowtext .5pt;mso-border-right-alt:solid windowtext .5pt;background:"
				+ "  #002060;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>"
				+ "<p class=MsoNormal><span style='mso-bookmark:_MailOriginal'><b><span "
				+ "style='mso-ascii-font-family:Calibri;mso-fareast-font-family:\"Times New Roman\";"
				+ "mso-hansi-font-family:Calibri;mso-bidi-font-family:\"Times New Roman\";"
				+ "  color:white'>End Time<o:p></o:p></span></b></span></p>" + "</td>"
				+ "<span style='mso-bookmark:_MailOriginal'></span>"
				+ "<td width=193 nowrap valign=bottom style='width:145.0pt;border-top:none;"
				+ "  border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;"
				+ "mso-border-bottom-alt:solid windowtext .5pt;mso-border-right-alt:solid windowtext .5pt;"
				+ "padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>"
				+ "<p class=MsoNormal><span style='mso-bookmark:_MailOriginal'><span "
				+ "  style='mso-ascii-font-family:Calibri;mso-fareast-font-family:\"Times New Roman\";"
				+ "mso-hansi-font-family:Calibri;mso-bidi-font-family:\"Times New Roman\";" + "color:black'>" + endTime
				+ "<o:p></o:p></span></span></p>" + "</td>" + "  <span style='mso-bookmark:_MailOriginal'></span>"
				+ "</tr>" + " <tr style='mso-yfti-irow:2;mso-yfti-lastrow:yes;height:15.0pt'>"
				+ "<td width=116 nowrap valign=bottom style='width:87.0pt;border:solid windowtext 1.0pt;"
				+ "border-top:none;mso-border-left-alt:solid windowtext .5pt;mso-border-bottom-alt:"
				+ "solid windowtext .5pt;mso-border-right-alt:solid windowtext .5pt;background:"
				+ "  #002060;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>"
				+ "<p class=MsoNormal><span style='mso-bookmark:_MailOriginal'><b><span "
				+ "style='mso-ascii-font-family:Calibri;mso-fareast-font-family:\"Times New Roman\";"
				+ "mso-hansi-font-family:Calibri;mso-bidi-font-family:\"Times New Roman\";"
				+ "  color:white'>Duration<o:p></o:p></span></b></span></p>" + "</td>"
				+ "<span style='mso-bookmark:_MailOriginal'></span>"
				+ "<td width=193 nowrap valign=bottom style='width:145.0pt;border-top:none;"
				+ "  border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;"
				+ "mso-border-bottom-alt:solid windowtext .5pt;mso-border-right-alt:solid windowtext .5pt;"
				+ "padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>"
				+ "<p class=MsoNormal><span style='mso-bookmark:_MailOriginal'><span "
				+ "  style='mso-ascii-font-family:Calibri;mso-fareast-font-family:\"Times New Roman\";"
				+ "mso-hansi-font-family:Calibri;mso-bidi-font-family:\"Times New Roman\";" + "color:black'>"
				+ getTimeDifference(startTime, endTime) + "<o:p></o:p></span></span></p>" + "</td>"
				+ "  <span style='mso-bookmark:_MailOriginal'></span>" + "</tr>" + "</table>"

				+ "<p class=MsoNormal><o:p>&nbsp;</o:p></p>"
				+ "<table class=MsoNormalTable border=0 cellspacing=0 cellpadding=0 width=850"
				+ " style='width:562.0pt;margin-left:.1pt;border-collapse:collapse;mso-yfti-tbllook:"
				+ " 1184;mso-padding-alt:0in 5.4pt 0in 5.4pt'>"
				+ " <tr style='mso-yfti-irow:0;mso-yfti-firstrow:yes;height:15.0pt'>"
				+ "  <td width=850 nowrap colspan=6 valign=bottom style='width:562.0pt;border:"
				+ "  solid windowtext 1.0pt;background:navy;padding:0in 5.4pt 0in 5.4pt;" + "  height:15.0pt'>"
				+ "  <p class=MsoNormal align=center style='text-align:center'><b><span "
				+ "  style='mso-ascii-font-family:Calibri;mso-fareast-font-family:\"Times New Roman\";"
				+ "  mso-hansi-font-family:Calibri;mso-bidi-font-family:\"Times New Roman\";"
				+ "  color:white'>Overall Progress Summary<o:p></o:p></span></b></p>" + "  </td>" + " </tr>"
				+ " <tr style='mso-yfti-irow:1;height:15.0pt'>"
				+ "  <td width=241 nowrap valign=bottom style='width:180.85pt;border:solid windowtext 1.0pt;"
				+ "  border-top:none;background:#3366FF;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>"
				+ "  <p class=MsoNormal align=center style='text-align:center'><b><span "
				+ "  style='mso-ascii-font-family:Calibri;mso-fareast-font-family:\"Times New Roman\";"
				+ "  mso-hansi-font-family:Calibri;mso-bidi-font-family:\"Times New Roman\";"
				+ "  color:white'>Modules<o:p></o:p></span></b></p>" + "  </td>"
				+ "  <td width=76 nowrap valign=bottom style='width:85.4pt;border-top:none;"
				+ "  border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;"
				+ "  background:#3366FF;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>"
				+ "  <p class=MsoNormal align=center style='text-align:center'><b><span "
				+ "  style='mso-ascii-font-family:Calibri;mso-fareast-font-family:\"Times New Roman\";"
				+ "  mso-hansi-font-family:Calibri;mso-bidi-font-family:\"Times New Roman\";"
				+ "  color:white'>Test Case<o:p></o:p></span></b></p>" + "  </td>"
				+ "  <td width=72 nowrap valign=bottom style='width:83.85pt;border-top:none;"
				+ "  border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;"
				+ "  background:#3366FF;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>"
				+ "  <p class=MsoNormal align=center style='text-align:center'><b><span "
				+ "  style='mso-ascii-font-family:Calibri;mso-fareast-font-family:\"Times New Roman\";"
				+ "  mso-hansi-font-family:Calibri;mso-bidi-font-family:\"Times New Roman\";"
				+ "  color:white'>Untested<o:p></o:p></span></b></p>" + "  </td>"
				+ "  <td width=66 nowrap valign=bottom style='width:77.05pt;border-top:none;"
				+ "  border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;"
				+ "  background:#3366FF;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>"
				+ "  <p class=MsoNormal align=center style='text-align:center'><b><span "
				+ "  style='mso-ascii-font-family:Calibri;mso-fareast-font-family:\"Times New Roman\";"
				+ "  mso-hansi-font-family:Calibri;mso-bidi-font-family:\"Times New Roman\";"
				+ "  color:white'>Passed<o:p></o:p></span></b></p>" + "  </td>"
				+ "  <td width=66 nowrap valign=bottom style='width:55.6pt;border-top:none;"
				+ "  border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;"
				+ "  background:#3366FF;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>"
				+ "  <p class=MsoNormal align=center style='text-align:center'><b><span "
				+ "  style='mso-ascii-font-family:Calibri;mso-fareast-font-family:\"Times New Roman\";"
				+ "  mso-hansi-font-family:Calibri;mso-bidi-font-family:\"Times New Roman\";"
				+ "  color:white'>Failed<o:p></o:p></span></b></p>" + "  </td>"
				+ "  <td width=330 nowrap valign=bottom style='width:79.25pt;border-top:none;"
				+ "  border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;"
				+ "  background:#3366FF;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>"
				+ "  <p class=MsoNormal align=center style='text-align:center'><b><span "
				+ "  style='mso-ascii-font-family:Calibri;mso-fareast-font-family:\"Times New Roman\";"
				+ "  mso-hansi-font-family:Calibri;mso-bidi-font-family:\"Times New Roman\";"
				+ "  color:white'>Remarks<o:p></o:p></span></b></p>" + "  </td>" + " </tr>";

		List<String[]> mod = dbProcess.getRunModules(runId);
		int totalTC = 0;
		int totalUntested = 0;
		int totalPassed = 0;
		int totalFailed = 0;

		for (String[] module : mod) {
			List<String[]> modTc = dbProcess.getRunTestcases(module[0], runId);
			List<String[]> modTcPassed = dbProcess.getRunTestcasesPassed(module[0], runId);
			List<String[]> modTcFailed = dbProcess.getRunTestcasesFailed(module[0], runId);
			totalTC = totalTC + modTc.size();
			totalUntested = totalUntested + (modTc.size() - modTcPassed.size() - modTcFailed.size());
			totalPassed = totalPassed + modTcPassed.size();
			totalFailed = totalFailed + modTcFailed.size();
			body = body + " <tr style='mso-yfti-irow:2;height:15.0pt'>"
					+ "  <td width=241 nowrap valign=bottom style='width:180.85pt;border:solid windowtext 1.0pt;"
					+ "  border-top:none;mso-border-top-alt:solid windowtext .5pt;mso-border-alt:solid windowtext .5pt;"
					+ "  padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>"
					+ "  <p class=MsoNormal><span style='mso-ascii-font-family:Calibri;mso-fareast-font-family:"
					+ "  \"Times New Roman\";mso-hansi-font-family:Calibri;mso-bidi-font-family:\"Times New Roman\";"
					+ "  color:black'>" + module[1] + "<o:p></o:p></span></p>" + "  </td>"
					+ "  <td width=76 nowrap valign=bottom style='width:85.4pt;border-top:none;"
					+ "  border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;"
					+ "  mso-border-top-alt:solid windowtext .5pt;mso-border-top-alt:solid windowtext .5pt;"
					+ "  mso-border-bottom-alt:solid windowtext .5pt;mso-border-right-alt:solid windowtext .5pt;"
					+ "  padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>"
					+ "  <p class=MsoNormal align=center style='text-align:center'><span "
					+ "  style='mso-ascii-font-family:Calibri;mso-fareast-font-family:\"Times New Roman\";"
					+ "  mso-hansi-font-family:Calibri;mso-bidi-font-family:\"Times New Roman\";" + "  color:black'>"
					+ modTc.size() + "<o:p></o:p></span></p>" + "  </td>"
					+ "  <td width=72 nowrap valign=bottom style='width:83.85pt;border-top:none;"
					+ "  border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;"
					+ "  mso-border-top-alt:solid windowtext .5pt;mso-border-top-alt:solid windowtext .5pt;"
					+ "  mso-border-bottom-alt:solid windowtext .5pt;mso-border-right-alt:solid windowtext .5pt;"
					+ "  padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>"
					+ "  <p class=MsoNormal align=center style='text-align:center'><span "
					+ "  style='mso-ascii-font-family:Calibri;mso-fareast-font-family:\"Times New Roman\";"
					+ "  mso-hansi-font-family:Calibri;mso-bidi-font-family:\"Times New Roman\";" + "  color:black'>"
					+ (modTc.size() - modTcPassed.size() - modTcFailed.size()) + "<o:p></o:p></span></p>" + "  </td>"
					+ "  <td width=66 nowrap valign=bottom style='width:77.05pt;border-top:none;"
					+ "  border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;"
					+ "  mso-border-top-alt:solid windowtext .5pt;mso-border-top-alt:solid windowtext .5pt;"
					+ "  mso-border-bottom-alt:solid windowtext .5pt;mso-border-right-alt:solid windowtext .5pt;"
					+ "  padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>"
					+ "  <p class=MsoNormal align=center style='text-align:center'><span "
					+ "  style='mso-ascii-font-family:Calibri;mso-fareast-font-family:\"Times New Roman\";"
					+ "  mso-hansi-font-family:Calibri;mso-bidi-font-family:\"Times New Roman\";" + "  color:black'>"
					+ modTcPassed.size() + "<o:p></o:p></span></p>" + "  </td>"
					+ "  <td width=66 nowrap valign=bottom style='width:55.6pt;border-top:none;"
					+ "  border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;"
					+ "  mso-border-top-alt:solid windowtext .5pt;mso-border-top-alt:solid windowtext .5pt;"
					+ "  mso-border-bottom-alt:solid windowtext .5pt;mso-border-right-alt:solid windowtext .5pt;"
					+ "  padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>"
					+ "  <p class=MsoNormal align=center style='text-align:center'><span "
					+ "  style='mso-ascii-font-family:Calibri;mso-fareast-font-family:\"Times New Roman\";"
					+ "  mso-hansi-font-family:Calibri;mso-bidi-font-family:\"Times New Roman\";" + "  color:black'>"
					+ modTcFailed.size() + "<o:p></o:p></span></p>" + "  </td>"
					+ "  <td width=330 nowrap valign=bottom style='width:79.25pt;border-top:none;"
					+ "  border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;"
					+ "  mso-border-top-alt:solid windowtext .5pt;mso-border-top-alt:solid windowtext .5pt;"
					+ "  mso-border-bottom-alt:solid windowtext .5pt;mso-border-right-alt:solid windowtext .5pt;"
					+ "  padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>"
					+ "  <p class=MsoNormal><span style='mso-ascii-font-family:Calibri;mso-fareast-font-family:"
					+ "  \"Times New Roman\";mso-hansi-font-family:Calibri;mso-bidi-font-family:\"Times New Roman\";"
					+ "  color:black'>&nbsp;<o:p></o:p></span></p>" + "  </td>" + " </tr>";
		}

		DecimalFormat decimalPct = new DecimalFormat("#0.00%");
		double pctUntested = (double) totalUntested / totalTC;
		double pctPassed = (double) totalPassed / totalTC;
		double pctFailed = (double) totalFailed / totalTC;

		body = body + " <tr style='mso-yfti-irow:6;height:15.0pt'>"
				+ "  <td width=241 nowrap valign=bottom style='width:180.85pt;border:solid windowtext 1.0pt;"
				+ "  border-top:none;mso-border-top-alt:solid windowtext 1.0pt;padding:0in 5.4pt 0in 5.4pt;"
				+ "  height:15.0pt'>" + "  <p class=MsoNormal align=right style='text-align:right'><b><span "
				+ "  style='mso-ascii-font-family:Calibri;mso-fareast-font-family:\"Times New Roman\";"
				+ "  mso-hansi-font-family:Calibri;mso-bidi-font-family:\"Times New Roman\"'>TOTAL"
				+ "  PROGRESS<o:p></o:p></span></b></p>" + "  </td>"
				+ "  <td width=76 nowrap valign=bottom style='width:85.4pt;border-top:none;"
				+ "  border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;"
				+ "  mso-border-top-alt:solid windowtext 1.0pt;padding:0in 5.4pt 0in 5.4pt;" + "  height:15.0pt'>"
				+ "  <p class=MsoNormal align=center style='text-align:center'><b><span "
				+ "  style='mso-ascii-font-family:Calibri;mso-fareast-font-family:\"Times New Roman\";"
				+ "  mso-hansi-font-family:Calibri;mso-bidi-font-family:\"Times New Roman\"'>" + totalTC
				+ "<o:p></o:p></span></b></p>" + "  </td>"
				+ "  <td width=72 nowrap valign=bottom style='width:83.85pt;border-top:none;"
				+ "  border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;"
				+ "  mso-border-top-alt:solid windowtext 1.0pt;padding:0in 5.4pt 0in 5.4pt;" + "  height:15.0pt'>"
				+ "  <p class=MsoNormal align=center style='text-align:center'><b><span "
				+ "  style='mso-ascii-font-family:Calibri;mso-fareast-font-family:\"Times New Roman\";"
				+ "  mso-hansi-font-family:Calibri;mso-bidi-font-family:\"Times New Roman\"'>" + totalUntested
				+ "<o:p></o:p></span></b></p>" + "  </td>"
				+ "  <td width=66 nowrap valign=bottom style='width:77.05pt;border-top:none;"
				+ "  border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;"
				+ "  mso-border-top-alt:solid windowtext 1.0pt;padding:0in 5.4pt 0in 5.4pt;" + "  height:15.0pt'>"
				+ "  <p class=MsoNormal align=center style='text-align:center'><b><span "
				+ "  style='mso-ascii-font-family:Calibri;mso-fareast-font-family:\"Times New Roman\";"
				+ "  mso-hansi-font-family:Calibri;mso-bidi-font-family:\"Times New Roman\"'>" + totalPassed
				+ "<o:p></o:p></span></b></p>" + "  </td>"
				+ "  <td width=66 nowrap valign=bottom style='width:55.6pt;border-top:none;"
				+ "  border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;"
				+ "  mso-border-top-alt:solid windowtext 1.0pt;padding:0in 5.4pt 0in 5.4pt;" + "  height:15.0pt'>"
				+ "  <p class=MsoNormal align=center style='text-align:center'><b><span "
				+ "  style='mso-ascii-font-family:Calibri;mso-fareast-font-family:\"Times New Roman\";"
				+ "  mso-hansi-font-family:Calibri;mso-bidi-font-family:\"Times New Roman\"'>" + totalFailed
				+ "<o:p></o:p></span></b></p>" + "  </td>"
				+ "  <td width=330 nowrap valign=bottom style='width:79.25pt;border-top:none;"
				+ "  border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;"
				+ "  mso-border-top-alt:solid windowtext 1.0pt;padding:0in 5.4pt 0in 5.4pt;" + "  height:15.0pt'>"
				+ "  <p class=MsoNormal align=center style='text-align:center'><b><span "
				+ "  style='mso-ascii-font-family:Calibri;mso-fareast-font-family:\"Times New Roman\";"
				+ "  mso-hansi-font-family:Calibri;mso-bidi-font-family:\"Times New Roman\"'>&nbsp;<o:p></o:p></span></b></p>"
				+ "  </td>" + " </tr>" + " <tr style='mso-yfti-irow:7;mso-yfti-lastrow:yes;height:15.0pt'>"
				+ "  <td width=241 nowrap valign=bottom style='width:180.85pt;border:solid windowtext 1.0pt;"
				+ "  border-top:none;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>"
				+ "  <p class=MsoNormal align=right style='text-align:right'><b><span "
				+ "  style='mso-ascii-font-family:Calibri;mso-fareast-font-family:\"Times New Roman\";"
				+ "  mso-hansi-font-family:Calibri;mso-bidi-font-family:\"Times New Roman\"'>TOTAL"
				+ "  PERCENTAGE<o:p></o:p></span></b></p>" + "  </td>"
				+ "  <td width=76 nowrap valign=bottom style='width:85.4pt;border-top:none;"
				+ "  border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;"
				+ "  padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>"
				+ "  <p class=MsoNormal align=center style='text-align:center'><b><span "
				+ "  style='mso-ascii-font-family:Calibri;mso-fareast-font-family:\"Times New Roman\";"
				+ "  mso-hansi-font-family:Calibri;mso-bidi-font-family:\"Times New Roman\"'>&nbsp;<o:p></o:p></span></b></p>"
				+ "  </td>" + "  <td width=72 nowrap valign=bottom style='width:83.85pt;border-top:none;"
				+ "  border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;"
				+ "  padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>"
				+ "  <p class=MsoNormal align=center style='text-align:center'><span "
				+ "  style='mso-ascii-font-family:Calibri;mso-fareast-font-family:\"Times New Roman\";"
				+ "  mso-hansi-font-family:Calibri;mso-bidi-font-family:\"Times New Roman\";" + "  color:black'>"
				+ decimalPct.format(pctUntested) + "<o:p></o:p></span></p>" + "  </td>"
				+ "  <td width=66 nowrap valign=bottom style='width:77.05pt;border-top:none;"
				+ "  border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;"
				+ "  padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>"
				+ "  <p class=MsoNormal align=center style='text-align:center'><span "
				+ "  style='mso-ascii-font-family:Calibri;mso-fareast-font-family:\"Times New Roman\";"
				+ "  mso-hansi-font-family:Calibri;mso-bidi-font-family:\"Times New Roman\";" + "  color:black'>"
				+ decimalPct.format(pctPassed) + "<o:p></o:p></span></p>" + "  </td>"
				+ "  <td width=66 nowrap valign=bottom style='width:55.6pt;border-top:none;"
				+ "  border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;"
				+ "  padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>"
				+ "  <p class=MsoNormal align=center style='text-align:center'><span "
				+ "  style='mso-ascii-font-family:Calibri;mso-fareast-font-family:\"Times New Roman\";"
				+ "  mso-hansi-font-family:Calibri;mso-bidi-font-family:\"Times New Roman\";" + "  color:black'>"
				+ decimalPct.format(pctFailed) + "<o:p></o:p></span></p>" + "  </td>"
				+ "  <td width=330 nowrap valign=bottom style='width:79.25pt;border-top:none;"
				+ "  border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;"
				+ "  padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>"
				+ "  <p class=MsoNormal align=center style='text-align:center'><b><span "
				+ "  style='mso-ascii-font-family:Calibri;mso-fareast-font-family:\"Times New Roman\";"
				+ "  mso-hansi-font-family:Calibri;mso-bidi-font-family:\"Times New Roman\"'>&nbsp;<o:p></o:p></span></b></p>"
				+ "  </td>" + "  </tr>" + "  </table>" + "</div></body></html>";

		return body;
	}

	private class SMTPAuthenticator extends javax.mail.Authenticator {
		public PasswordAuthentication getPasswordAuthentication() {
			String username = SMTP_AUTH_USER;
			String password;
			try {
				password = encrypt.decryptPassword(SMTP_AUTH_PWD);
				return new PasswordAuthentication(username, password);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	private static String getTimeDifference(String start, String end) {
		try {
			DateFormat df = new SimpleDateFormat("MMM-dd-yyyy HH.mm.ss");
			Date d1 = df.parse(start);
			Date d2 = df.parse(end);
			long diffInMilliseconds = Math.abs(d1.getTime() - d2.getTime());
			int seconds = (int) diffInMilliseconds / 1000;
			// calculate hours minutes and seconds
			int hours = seconds / 3600;
			int minutes = (seconds % 3600) / 60;
			seconds = (seconds % 3600) % 60;
			DecimalFormat nf = new DecimalFormat("00");
			return nf.format(hours) + ":" + nf.format(minutes) + ":" + nf.format(seconds);
		} catch (Exception e) {
			// nothing to do
		}

		return "";
	}

}
