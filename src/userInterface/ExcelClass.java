package userInterface;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.JOptionPane;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Chart;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFChart;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTAxDataSource;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBarChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBarSer;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBoolean;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTCatAx;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTDLblPos;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTLegend;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTNumDataSource;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTNumFmt;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTNumRef;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPieChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPieSer;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPlotArea;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTScaling;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTSerTx;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTStrRef;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTTitle;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTTx;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTValAx;
import org.openxmlformats.schemas.drawingml.x2006.chart.STAxPos;
import org.openxmlformats.schemas.drawingml.x2006.chart.STBarDir;
import org.openxmlformats.schemas.drawingml.x2006.chart.STBarGrouping;
import org.openxmlformats.schemas.drawingml.x2006.chart.STDLblPos;
import org.openxmlformats.schemas.drawingml.x2006.chart.STLegendPos;
import org.openxmlformats.schemas.drawingml.x2006.chart.STOrientation;
import org.openxmlformats.schemas.drawingml.x2006.chart.STTickLblPos;
import org.openxmlformats.schemas.drawingml.x2006.main.CTRegularTextRun;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextBody;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextParagraph;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTDLbls;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTDPt;

import database.DBProcess;
import database.TestMachineConfig;

public class ExcelClass {
	private static XSSFWorkbook workbook;
	static TestMachineConfig dbConfig = new TestMachineConfig();

	public ExcelClass() {

	}

	public void createExcelFileReport(File file, String runId, DBProcess dbProcess, boolean manualGen) {
		try {

			String selectedFile = file.getAbsolutePath();
			String fileName;
			if (selectedFile.endsWith(".xlsx")) {
				fileName = file.getAbsolutePath();
			} else {
				fileName = file.getAbsolutePath() + ".xlsx";
			}

			if (file.exists()) {
				int opt = JOptionPane.showConfirmDialog(null, "The file already exists. Do you want to overwrite it?",
						"Confirm Export", JOptionPane.YES_NO_OPTION);
				if (opt == JOptionPane.YES_OPTION) {
					file.delete();
					workbook = new XSSFWorkbook();
				} else {
					return;
				}

			} else {
				workbook = new XSSFWorkbook();
			}

			XSSFSheet sheetSummary = workbook.createSheet("Test Summary");
			sheetSummary.setDisplayGridlines(false);

			XSSFCellStyle blueBG = workbook.createCellStyle();
			blueBG.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
			blueBG.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			blueBG.setAlignment(HorizontalAlignment.CENTER);
			blueBG.setBorderLeft(BorderStyle.MEDIUM);
			blueBG.setBorderBottom(BorderStyle.MEDIUM);
			blueBG.setBorderTop(BorderStyle.MEDIUM);
			blueBG.setBorderRight(BorderStyle.MEDIUM);
			XSSFFont font = workbook.createFont();
			font.setColor(IndexedColors.WHITE.getIndex());
			font.setBold(true);
			blueBG.setFont(font);

			sheetSummary.addMergedRegion(new CellRangeAddress(1, 1, 1, 6));
			Row row1 = sheetSummary.createRow(1);
			Cell cell1 = row1.createCell(1);
			Cell cell2 = row1.createCell(2);
			Cell cell3 = row1.createCell(3);
			Cell cell4 = row1.createCell(4);
			Cell cell5 = row1.createCell(5);
			Cell cell6 = row1.createCell(6);
			cell1.setCellValue("Overall Progress Summary");
			cell1.setCellStyle(blueBG);
			cell2.setCellStyle(blueBG);
			cell3.setCellStyle(blueBG);
			cell4.setCellStyle(blueBG);
			cell5.setCellStyle(blueBG);
			cell6.setCellStyle(blueBG);

			XSSFCellStyle lightBlueBG = workbook.createCellStyle();
			lightBlueBG.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
			lightBlueBG.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			lightBlueBG.setAlignment(HorizontalAlignment.CENTER);
			lightBlueBG.setBorderLeft(BorderStyle.MEDIUM);
			lightBlueBG.setBorderBottom(BorderStyle.MEDIUM);
			lightBlueBG.setBorderTop(BorderStyle.MEDIUM);
			lightBlueBG.setBorderRight(BorderStyle.MEDIUM);
			lightBlueBG.setFont(font);
			XSSFFont font3 = workbook.createFont();
			font3.setBold(true);
			XSSFCellStyle yellowBg1 = workbook.createCellStyle();
			yellowBg1.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
			yellowBg1.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			yellowBg1.setAlignment(HorizontalAlignment.RIGHT);
			yellowBg1.setBorderLeft(BorderStyle.MEDIUM);
			yellowBg1.setBorderBottom(BorderStyle.MEDIUM);
			yellowBg1.setBorderTop(BorderStyle.MEDIUM);
			yellowBg1.setBorderRight(BorderStyle.MEDIUM);
			yellowBg1.setFont(font3);
			XSSFCellStyle yellowBg2 = workbook.createCellStyle();
			yellowBg2.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
			yellowBg2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			yellowBg2.setAlignment(HorizontalAlignment.CENTER);
			yellowBg2.setBorderLeft(BorderStyle.MEDIUM);
			yellowBg2.setBorderBottom(BorderStyle.MEDIUM);
			yellowBg2.setBorderTop(BorderStyle.MEDIUM);
			yellowBg2.setBorderRight(BorderStyle.MEDIUM);

			Row row2 = sheetSummary.createRow(2);
			row2.createCell(1).setCellValue("Modules");
			row2.createCell(2).setCellValue("Test Case");
			row2.createCell(3).setCellValue("Untested");
			row2.createCell(4).setCellValue("Passed");
			row2.createCell(5).setCellValue("Failed");
			row2.createCell(6).setCellValue("Remarks");
			row2.getCell(1).setCellStyle(lightBlueBG);
			row2.getCell(2).setCellStyle(lightBlueBG);
			row2.getCell(3).setCellStyle(lightBlueBG);
			row2.getCell(4).setCellStyle(lightBlueBG);
			row2.getCell(5).setCellStyle(lightBlueBG);
			row2.getCell(6).setCellStyle(lightBlueBG);

			// create row per
			// module======================================================================///
			int modNum = 3;
			List<String[]> mod = dbProcess.getRunModules(runId);
			for (String[] module : mod) {

				XSSFCellStyle borderLeft = workbook.createCellStyle();
				borderLeft.setBorderLeft(BorderStyle.MEDIUM);
				XSSFCellStyle borderRight = workbook.createCellStyle();
				borderRight.setBorderRight(BorderStyle.MEDIUM);
				XSSFCellStyle thinBorder = workbook.createCellStyle();
				thinBorder.setBorderRight(BorderStyle.THIN);
				thinBorder.setBorderLeft(BorderStyle.THIN);
				thinBorder.setBorderTop(BorderStyle.THIN);
				thinBorder.setBorderBottom(BorderStyle.THIN);
				thinBorder.setAlignment(HorizontalAlignment.CENTER);

				List<String[]> modTc = dbProcess.getRunTestcases(module[0], runId);
				List<String[]> modTcPassed = dbProcess.getRunTestcasesPassed(module[0], runId);
				List<String[]> modTcFailed = dbProcess.getRunTestcasesFailed(module[0], runId);

				Row modrow = sheetSummary.createRow(modNum);
				modrow.createCell(1).setCellValue(module[1]);
				modrow.createCell(2).setCellValue(modTc.size());
				modrow.createCell(3).setCellValue(modTc.size() - modTcPassed.size() - modTcFailed.size());
				modrow.createCell(4).setCellValue(modTcPassed.size());
				modrow.createCell(5).setCellValue(modTcFailed.size());
				modrow.createCell(6).setCellValue("");
				modrow.createCell(0).setCellValue("");
				modrow.createCell(7).setCellValue("");

				modrow.getCell(1).setCellStyle(thinBorder);
				modrow.getCell(2).setCellStyle(thinBorder);
				modrow.getCell(3).setCellStyle(thinBorder);
				modrow.getCell(4).setCellStyle(thinBorder);
				modrow.getCell(5).setCellStyle(thinBorder);

				XSSFCellStyle thinBorderLeft = workbook.createCellStyle();
				thinBorderLeft.setBorderRight(BorderStyle.THIN);
				thinBorderLeft.setBorderLeft(BorderStyle.THIN);
				thinBorderLeft.setBorderTop(BorderStyle.THIN);
				thinBorderLeft.setBorderBottom(BorderStyle.THIN);
				thinBorderLeft.setAlignment(HorizontalAlignment.LEFT);
				modrow.getCell(1).setCellStyle(thinBorderLeft);
				modrow.getCell(6).setCellStyle(thinBorderLeft);

				modrow.getCell(0).setCellStyle(borderRight);
				modrow.getCell(7).setCellStyle(borderLeft);

				String st = modTc.get(0)[3];
				String et = modTc.get(modTc.size() - 1)[4];
				XSSFSheet modSheet = workbook.createSheet(module[1]);
				modSheet.setDisplayGridlines(false);
				Row modRow = modSheet.createRow(0);
				modRow.createCell(1).setCellValue("Start Time:");
				modRow.getCell(1).setCellStyle(yellowBg1);
				modRow.createCell(2).setCellValue(st);
				modRow.getCell(2).setCellStyle(yellowBg2);
				modRow.createCell(3).setCellValue("End Time:");
				modRow.getCell(3).setCellStyle(yellowBg1);
				modRow.createCell(4).setCellValue(et);
				modRow.getCell(4).setCellStyle(yellowBg2);
				modRow.createCell(5).setCellValue("Duration:");
				modRow.getCell(5).setCellStyle(yellowBg1);
				modRow.createCell(6).setCellValue(getTimeDifference(st, et));
				modRow.getCell(6).setCellStyle(yellowBg2);

				createTestcases(modSheet, modTc, dbProcess);

				modNum = modNum + 1;
			}

			// create row per
			// module======================================================================///
			int rowSum = modNum;
			Row row3 = sheetSummary.createRow(modNum);
			row3.createCell(1).setCellValue("TOTAL PROGRESS");
			row3.createCell(2).setCellFormula("SUM(C4:C" + rowSum + ")");
			row3.createCell(3).setCellFormula("SUM(D4:D" + rowSum + ")");
			row3.createCell(4).setCellFormula("SUM(E4:E" + rowSum + ")");
			row3.createCell(5).setCellFormula("SUM(F4:F" + rowSum + ")");
			row3.createCell(6).setCellValue("");

			XSSFCellStyle style3 = workbook.createCellStyle();
			style3.setAlignment(HorizontalAlignment.CENTER);
			style3.setBorderLeft(BorderStyle.MEDIUM);
			style3.setBorderBottom(BorderStyle.MEDIUM);
			style3.setBorderTop(BorderStyle.MEDIUM);
			style3.setBorderRight(BorderStyle.MEDIUM);
			XSSFFont font2 = workbook.createFont();
			font2.setBold(true);
			style3.setFont(font2);

			XSSFCellStyle rightAlign = workbook.createCellStyle();
			rightAlign.setAlignment(HorizontalAlignment.RIGHT);
			rightAlign.setBorderLeft(BorderStyle.MEDIUM);
			rightAlign.setBorderBottom(BorderStyle.MEDIUM);
			rightAlign.setBorderTop(BorderStyle.MEDIUM);
			rightAlign.setBorderRight(BorderStyle.MEDIUM);
			rightAlign.setFont(font2);

			row3.getCell(2).setCellStyle(style3);
			row3.getCell(3).setCellStyle(style3);
			row3.getCell(4).setCellStyle(style3);
			row3.getCell(5).setCellStyle(style3);
			row3.getCell(6).setCellStyle(style3);
			row3.getCell(1).setCellStyle(rightAlign);

			XSSFCellStyle percentStyle = workbook.createCellStyle();
			percentStyle.setDataFormat(workbook.createDataFormat().getFormat("0.00%"));
			percentStyle.setAlignment(HorizontalAlignment.CENTER);
			percentStyle.setBorderLeft(BorderStyle.MEDIUM);
			percentStyle.setBorderBottom(BorderStyle.MEDIUM);
			percentStyle.setBorderTop(BorderStyle.MEDIUM);
			percentStyle.setBorderRight(BorderStyle.MEDIUM);

			int percentRow = modNum + 1;
			Row row4 = sheetSummary.createRow(modNum + 1);
			row4.createCell(1).setCellValue("TOTAL PERCENTAGE");
			row4.createCell(2).setCellValue("");
			row4.createCell(3).setCellFormula("(D" + percentRow + "/C" + percentRow + ")");
			row4.createCell(4).setCellFormula("(E" + percentRow + "/C" + percentRow + ")");
			row4.createCell(5).setCellFormula("(F" + percentRow + "/C" + percentRow + ")");
			row4.createCell(6).setCellValue("");

			row4.getCell(3).setCellStyle(percentStyle);
			row4.getCell(4).setCellStyle(percentStyle);
			row4.getCell(5).setCellStyle(percentStyle);

			row4.getCell(2).setCellStyle(style3);
			row4.getCell(6).setCellStyle(style3);
			row4.getCell(1).setCellStyle(rightAlign);
			sheetSummary.addMergedRegion(new CellRangeAddress(modNum + 3, modNum + 3, 2, 4));
			sheetSummary.addMergedRegion(new CellRangeAddress(modNum + 4, modNum + 4, 2, 4));
			sheetSummary.addMergedRegion(new CellRangeAddress(modNum + 5, modNum + 5, 2, 4));

			XSSFCellStyle centerAlign = workbook.createCellStyle();
			centerAlign.setAlignment(HorizontalAlignment.CENTER);
			centerAlign.setBorderLeft(BorderStyle.MEDIUM);
			centerAlign.setBorderBottom(BorderStyle.MEDIUM);
			centerAlign.setBorderTop(BorderStyle.MEDIUM);
			centerAlign.setBorderRight(BorderStyle.MEDIUM);
			centerAlign.setFont(font2);

			String startTime = dbProcess.getStartTime(runId);
			Row row5 = sheetSummary.createRow(modNum + 3);
			row5.createCell(1).setCellValue("START TIME:");
			row5.createCell(2).setCellValue(startTime);
			row5.getCell(1).setCellStyle(rightAlign);
			row5.getCell(2).setCellStyle(centerAlign);
			row5.createCell(3).setCellStyle(centerAlign);
			row5.createCell(4).setCellStyle(centerAlign);

			String endTime = dbProcess.getEndTime(runId);
			Row row6 = sheetSummary.createRow(modNum + 4);
			row6.createCell(1).setCellValue("END TIME:");
			row6.createCell(2).setCellValue(endTime);
			row6.getCell(1).setCellStyle(rightAlign);
			row6.getCell(2).setCellStyle(centerAlign);
			row6.createCell(3).setCellStyle(centerAlign);
			row6.createCell(4).setCellStyle(centerAlign);

			Row row7 = sheetSummary.createRow(modNum + 5);
			row7.createCell(1).setCellValue("DURATION:");
			row7.createCell(2).setCellValue(getTimeDifference(startTime, endTime));
			row7.getCell(1).setCellStyle(rightAlign);
			row7.getCell(2).setCellStyle(centerAlign);
			row7.createCell(3).setCellStyle(centerAlign);
			row7.createCell(4).setCellStyle(centerAlign);

			sheetSummary.setColumnWidth(0, 1000);
			sheetSummary.setColumnWidth(1, 3000 * 3);
			sheetSummary.setColumnWidth(6, 3000 * 3);

			pieChart(sheetSummary, (rowSum + 1));
			barChart(sheetSummary, (rowSum + 1));

			FileOutputStream outFile = new FileOutputStream(fileName);
			workbook.write(outFile);
			outFile.close();
			workbook.close();

			if (manualGen) {
				JOptionPane.showMessageDialog(null, "Export Complete", "Export", JOptionPane.INFORMATION_MESSAGE);
			}

		} catch (Exception e) {
			System.out.println("Failed write to excel:" + e.getMessage());
			if (manualGen) {
				JOptionPane.showMessageDialog(null, "Export Failed", "Export", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	public static void pieChart(XSSFSheet sheet, int rowSum) throws Exception {
		// rowSum = rowSum - 1;
		Drawing drawing = ((XSSFSheet) sheet).createDrawingPatriarch();
		ClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 8, 1, 16, 16);
		Chart chart = drawing.createChart(anchor);
		// create pie chart
		CTChart ctChart = ((XSSFChart) chart).getCTChart();
		CTPlotArea ctPlotArea = ctChart.getPlotArea();
		CTPieChart ctPieChart = ctPlotArea.addNewPieChart();
		CTBoolean ctBoolean = ctPieChart.addNewVaryColors();
		ctBoolean.setVal(true);
		CTPieSer ctPieSer = ctPieChart.addNewSer();
		ctPieSer.addNewIdx().setVal(0);
		// legend
		CTLegend ctLegend = ctChart.addNewLegend();
		ctLegend.addNewLegendPos().setVal(STLegendPos.B);
		ctLegend.addNewOverlay().setVal(false);

		// Title
		CTTitle title = ctChart.addNewTitle();
		CTTx tx = title.addNewTx();
		CTTextBody rich = tx.addNewRich();
		rich.addNewBodyPr();
		CTTextParagraph para = rich.addNewP();
		CTRegularTextRun r = para.addNewR();
		r.setT("Overall Progress Summary");
		CTBoolean ctTitleBoolean = title.addNewOverlay();
		ctTitleBoolean.setVal(false);

		// graph data source
		CTAxDataSource cttAxDataSource = ctPieSer.addNewCat();
		CTStrRef ctStrRef = cttAxDataSource.addNewStrRef();
		ctStrRef.setF("'Test Summary'!$D$3:$F$3");
		CTNumDataSource ctNumDataSource = ctPieSer.addNewVal();
		CTNumRef ctNumRef = ctNumDataSource.addNewNumRef();
		ctNumRef.setF("'Test Summary'!$D$" + rowSum + ":$F$" + rowSum);

		// data labels
		CTDLbls dtaLbls = ctPieSer.addNewDLbls();
		CTNumFmt numFmt = dtaLbls.addNewNumFmt();
		numFmt.setFormatCode("0%;-0%;\"\"");
		CTDLblPos ctPos = dtaLbls.addNewDLblPos();
		ctPos.setVal(STDLblPos.CTR);
		CTBoolean dtapctBoolean = dtaLbls.addNewShowPercent();
		dtapctBoolean.setVal(true);
		CTBoolean dtallBoolean = dtaLbls.addNewShowLeaderLines();
		dtallBoolean.setVal(false);
		CTBoolean dtacatBoolean = dtaLbls.addNewShowCatName();
		dtacatBoolean.setVal(false);
		CTBoolean dtalkeyBoolean = dtaLbls.addNewShowLegendKey();
		dtalkeyBoolean.setVal(false);
		CTBoolean dtavalBoolean = dtaLbls.addNewShowVal();
		dtavalBoolean.setVal(false);
		CTBoolean dtaserBoolean = dtaLbls.addNewShowSerName();
		dtaserBoolean.setVal(false);

		CTDPt ctDPt = ctPieSer.addNewDPt();
		ctDPt.addNewIdx().setVal(0);
		ctDPt.addNewSpPr().addNewSolidFill().addNewSrgbClr().setVal(new byte[] { 51, 102, (byte) 255 });

		CTDPt ctDPt2 = ctPieSer.addNewDPt();
		ctDPt2.addNewIdx().setVal(1);
		ctDPt2.addNewSpPr().addNewSolidFill().addNewSrgbClr().setVal(new byte[] { (byte) 51, (byte) 204, 51 });

		CTDPt ctDPt3 = ctPieSer.addNewDPt();
		ctDPt3.addNewIdx().setVal(2);
		ctDPt3.addNewSpPr().addNewSolidFill().addNewSrgbClr().setVal(new byte[] { (byte) 255, 0, 0 });

	}

	public static void barChart(XSSFSheet sheet, int rowSum) throws Exception {
		int chartHeight = 25 + (rowSum - 4);
		rowSum = rowSum - 1;

		Drawing drawing = sheet.createDrawingPatriarch();
		ClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 8, 17, 16, chartHeight);

		Chart chart = drawing.createChart(anchor);

		CTChart ctChart = ((XSSFChart) chart).getCTChart();
		CTPlotArea ctPlotArea = ctChart.getPlotArea();
		CTBarChart ctBarChart = ctPlotArea.addNewBarChart();
		CTBoolean ctBoolean = ctBarChart.addNewVaryColors();
		ctBoolean.setVal(true);
		ctBarChart.addNewBarDir().setVal(STBarDir.BAR);
		ctBarChart.addNewGrouping().setVal(STBarGrouping.PERCENT_STACKED);
		ctBarChart.addNewOverlap().setVal((byte) 100);
		;

		/// untested
		CTBarSer ctBarSer = ctBarChart.addNewSer();
		CTSerTx ctSerTx = ctBarSer.addNewTx();
		CTStrRef ctStrRef = ctSerTx.addNewStrRef();
		ctStrRef.setF("'Test Summary'!$D$3");
		ctBarSer.addNewIdx().setVal(0);
		ctBarSer.addNewSpPr().addNewSolidFill().addNewSrgbClr().setVal(new byte[] { 51, 102, (byte) 255 });

		CTAxDataSource cttAxDataSource = ctBarSer.addNewCat();
		ctStrRef = cttAxDataSource.addNewStrRef();
		ctStrRef.setF("'Test Summary'!$B$4:$B$" + rowSum);

		CTNumDataSource ctNumDataSource = ctBarSer.addNewVal();
		CTNumRef ctNumRef = ctNumDataSource.addNewNumRef();
		ctNumRef.setF("'Test Summary'!$D$4:$D$" + rowSum);
		///// passed
		CTBarSer ctBarSer2 = ctBarChart.addNewSer();
		CTSerTx ctSerTx2 = ctBarSer2.addNewTx();
		CTStrRef ctStrRef2 = ctSerTx2.addNewStrRef();
		ctStrRef2.setF("'Test Summary'!$E$3");
		ctBarSer2.addNewIdx().setVal(0);
		ctBarSer2.addNewSpPr().addNewSolidFill().addNewSrgbClr().setVal(new byte[] { (byte) 51, (byte) 204, 51 });

		CTAxDataSource cttAxDataSource2 = ctBarSer2.addNewCat();
		ctStrRef2 = cttAxDataSource2.addNewStrRef();
		ctStrRef2.setF("'Test Summary'!$B$4:$B$" + rowSum);

		CTNumDataSource ctNumDataSource2 = ctBarSer2.addNewVal();
		CTNumRef ctNumRef2 = ctNumDataSource2.addNewNumRef();
		ctNumRef2.setF("'Test Summary'!$E$4:$E$" + rowSum);
		///// failed
		CTBarSer ctBarSer3 = ctBarChart.addNewSer();
		CTSerTx ctSerTx3 = ctBarSer3.addNewTx();
		CTStrRef ctStrRef3 = ctSerTx3.addNewStrRef();
		ctStrRef3.setF("'Test Summary'!$F$3");
		ctBarSer3.addNewIdx().setVal(0);
		ctBarSer3.addNewSpPr().addNewSolidFill().addNewSrgbClr().setVal(new byte[] { (byte) 255, 0, 0 });

		CTAxDataSource cttAxDataSource3 = ctBarSer3.addNewCat();
		ctStrRef3 = cttAxDataSource3.addNewStrRef();
		ctStrRef3.setF("'Test Summary'!$B$4:$B$" + rowSum);

		CTNumDataSource ctNumDataSource3 = ctBarSer3.addNewVal();
		CTNumRef ctNumRef3 = ctNumDataSource3.addNewNumRef();
		ctNumRef3.setF("'Test Summary'!$F$4:$F$" + rowSum);

		// telling the BarChart that it has axes and giving them Ids
		ctBarChart.addNewAxId().setVal(123456);
		ctBarChart.addNewAxId().setVal(123457);
		ctBarChart.addNewAxId().setVal(123458);

		// cat axis
		CTCatAx ctCatAx = ctPlotArea.addNewCatAx();
		ctCatAx.addNewAxId().setVal(123456); // id of the cat axis
		CTScaling ctScaling = ctCatAx.addNewScaling();
		ctScaling.addNewOrientation().setVal(STOrientation.MAX_MIN);
		ctCatAx.addNewDelete().setVal(false);
		ctCatAx.addNewAxPos().setVal(STAxPos.B);
		ctCatAx.addNewCrossAx().setVal(123457); // id of the val axis
		ctCatAx.addNewTickLblPos().setVal(STTickLblPos.NEXT_TO);

		// val axis
		CTValAx ctValAx = ctPlotArea.addNewValAx();
		ctValAx.addNewAxId().setVal(123457); // id of the val axis
		ctScaling = ctValAx.addNewScaling();
		ctScaling.addNewOrientation().setVal(STOrientation.MIN_MAX);
		ctValAx.addNewDelete().setVal(false);
		ctValAx.addNewAxPos().setVal(STAxPos.L);
		ctValAx.addNewCrossAx().setVal(123456); // id of the cat axis
		ctValAx.addNewTickLblPos().setVal(STTickLblPos.NEXT_TO);

		// legend
		CTLegend ctLegend = ctChart.addNewLegend();
		ctLegend.addNewLegendPos().setVal(STLegendPos.B);
		ctLegend.addNewOverlay().setVal(false);

		// Title
		CTTitle title = ctChart.addNewTitle();
		CTTx tx = title.addNewTx();
		CTTextBody rich = tx.addNewRich();
		rich.addNewBodyPr();
		CTTextParagraph para = rich.addNewP();
		CTRegularTextRun r = para.addNewR();
		r.setT("Test Case Summary");
		CTBoolean ctTitleBoolean = title.addNewOverlay();
		ctTitleBoolean.setVal(false);

	}

	public static void createTestcases(XSSFSheet sheet, List<String[]> modTc, DBProcess dbProcess) {
		sheet.setColumnWidth(0, 1000);
		sheet.setColumnWidth(1, 3000);
		sheet.setColumnWidth(2, 3000 * 3);
		sheet.setColumnWidth(3, 4000);
		sheet.setColumnWidth(4, 3000 * 3);
		sheet.setColumnWidth(5, 5000);
		sheet.setColumnWidth(6, 3000 * 2);
		sheet.setColumnWidth(7, 3000 * 2);
		sheet.setColumnWidth(8, 4000);
		sheet.setColumnWidth(9, 3000 * 3);

		XSSFCellStyle blueBG = workbook.createCellStyle();
		blueBG.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
		blueBG.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		blueBG.setBorderLeft(BorderStyle.MEDIUM);
		blueBG.setBorderBottom(BorderStyle.MEDIUM);
		blueBG.setBorderTop(BorderStyle.MEDIUM);
		blueBG.setBorderRight(BorderStyle.MEDIUM);
		XSSFFont font = workbook.createFont();
		font.setColor(IndexedColors.WHITE.getIndex());
		font.setBold(true);
		blueBG.setFont(font);

		XSSFCellStyle thickBorder = workbook.createCellStyle();
		thickBorder.setBorderLeft(BorderStyle.MEDIUM);
		thickBorder.setBorderBottom(BorderStyle.MEDIUM);
		thickBorder.setBorderTop(BorderStyle.MEDIUM);
		thickBorder.setBorderRight(BorderStyle.MEDIUM);
		thickBorder.setWrapText(true);

		int rowNum = 0;
		for (String[] tc : modTc) {
			rowNum = rowNum + 1;
			Row row1 = sheet.createRow(rowNum);
			row1.createCell(1).setCellValue("TestCase ID:");
			row1.getCell(1).setCellStyle(blueBG);
			row1.createCell(2).setCellValue(tc[0]);
			row1.getCell(2).setCellStyle(thickBorder);
			row1.createCell(3).setCellValue("Result:");
			row1.getCell(3).setCellStyle(blueBG);
			row1.createCell(4).setCellValue(tc[6]);
			row1.getCell(4).setCellStyle(thickBorder);
			rowNum = rowNum + 1;

			Row row2 = sheet.createRow(rowNum);
			row2.createCell(1).setCellValue("Name:");
			row2.getCell(1).setCellStyle(blueBG);
			row2.createCell(2).setCellValue(tc[1]);
			row2.getCell(2).setCellStyle(thickBorder);
			row2.createCell(3).setCellValue("Start Time:");
			row2.getCell(3).setCellStyle(blueBG);
			row2.createCell(4).setCellValue(tc[3]);
			row2.getCell(4).setCellStyle(thickBorder);
			rowNum = rowNum + 1;

			Row row3 = sheet.createRow(rowNum);
			row3.createCell(1).setCellValue("Description:");
			row3.getCell(1).setCellStyle(blueBG);
			row3.createCell(2).setCellValue(tc[2]);
			row3.getCell(2).setCellStyle(thickBorder);
			row3.createCell(3).setCellValue("End Time:");
			row3.getCell(3).setCellStyle(blueBG);
			row3.createCell(4).setCellValue(tc[4]);
			row3.getCell(4).setCellStyle(thickBorder);
			rowNum = rowNum + 1;
			rowNum = createSteps(sheet, tc[0], rowNum, dbProcess);

		}

	}

	public static int createSteps(XSSFSheet sheet, String tcId, int rowNum, DBProcess dbProcess) {

		XSSFFont font = workbook.createFont();
		font.setColor(IndexedColors.WHITE.getIndex());
		font.setBold(true);

		XSSFCellStyle lightBlueBG = workbook.createCellStyle();
		lightBlueBG.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
		lightBlueBG.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		lightBlueBG.setBorderLeft(BorderStyle.MEDIUM);
		lightBlueBG.setBorderBottom(BorderStyle.MEDIUM);
		lightBlueBG.setBorderTop(BorderStyle.MEDIUM);
		lightBlueBG.setBorderRight(BorderStyle.MEDIUM);
		lightBlueBG.setFont(font);

		Row row4 = sheet.createRow(rowNum);
		row4.createCell(1).setCellValue("Step No.");
		row4.createCell(2).setCellValue("Description");
		row4.createCell(3).setCellValue("Element Locator");
		row4.createCell(4).setCellValue("Element String");
		row4.createCell(5).setCellValue("Action");
		row4.createCell(6).setCellValue("Param Name");
		row4.createCell(7).setCellValue("Param Value");
		row4.createCell(8).setCellValue("Result");
		row4.createCell(9).setCellValue("Remarks");
		row4.getCell(1).setCellStyle(lightBlueBG);
		row4.getCell(2).setCellStyle(lightBlueBG);
		row4.getCell(3).setCellStyle(lightBlueBG);
		row4.getCell(4).setCellStyle(lightBlueBG);
		row4.getCell(5).setCellStyle(lightBlueBG);
		row4.getCell(6).setCellStyle(lightBlueBG);
		row4.getCell(7).setCellStyle(lightBlueBG);
		row4.getCell(8).setCellStyle(lightBlueBG);
		row4.getCell(9).setCellStyle(lightBlueBG);

		XSSFCellStyle thinBorder = workbook.createCellStyle();
		thinBorder.setBorderRight(BorderStyle.THIN);
		thinBorder.setBorderLeft(BorderStyle.THIN);
		thinBorder.setBorderTop(BorderStyle.THIN);
		thinBorder.setBorderBottom(BorderStyle.THIN);
		thinBorder.setWrapText(true);

		XSSFCellStyle resultStylePassed = workbook.createCellStyle();
		resultStylePassed.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
		resultStylePassed.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		resultStylePassed.setBorderRight(BorderStyle.THIN);
		resultStylePassed.setBorderLeft(BorderStyle.THIN);
		resultStylePassed.setBorderTop(BorderStyle.THIN);
		resultStylePassed.setBorderBottom(BorderStyle.THIN);
		resultStylePassed.setWrapText(true);
		XSSFFont fontPassed = workbook.createFont();
		fontPassed.setColor(IndexedColors.GREEN.getIndex());
		resultStylePassed.setFont(fontPassed);

		byte[] rgb = new byte[3];
		rgb[0] = (byte) 255; // red
		rgb[1] = (byte) 199; // green
		rgb[2] = (byte) 206; // blue

		XSSFColor color = new XSSFColor(rgb);
		XSSFCellStyle resultStyleFailed = workbook.createCellStyle();
		resultStyleFailed.setFillForegroundColor(color);
		resultStyleFailed.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		resultStyleFailed.setBorderRight(BorderStyle.THIN);
		resultStyleFailed.setBorderLeft(BorderStyle.THIN);
		resultStyleFailed.setBorderTop(BorderStyle.THIN);
		resultStyleFailed.setBorderBottom(BorderStyle.THIN);
		resultStyleFailed.setWrapText(true);
		XSSFFont fontFailed = workbook.createFont();
		fontFailed.setColor(IndexedColors.RED.getIndex());
		resultStyleFailed.setFont(fontFailed);
		List<String[]> actionNotIncluded = dbConfig.getActionReports("No");
		String excluded = "";
		for (String[] a : actionNotIncluded) {
			excluded = excluded + "'" + a[0] + "',";
		}
		rowNum = rowNum + 1;
		boolean edit = dbProcess.getRunUseTestData(tcId);
		List<String[]> steps;
		if (edit) {
			steps = dbProcess.getRunTestStepsWithTestData(tcId,
					" AND TS.ACTION NOT IN(" + excluded.substring(0, excluded.length() - 1) + ") ");
		} else {
			steps = dbProcess.getRunTestSteps(tcId,
					" AND TS.ACTION NOT IN(" + excluded.substring(0, excluded.length() - 1) + ") ");
		}

		int stepCount = 1;
		for (String[] step : steps) {
			Row row5 = sheet.createRow(rowNum);
			row5.createCell(1).setCellValue(stepCount);
			row5.createCell(2).setCellValue(step[2]);
			row5.createCell(3).setCellValue(step[3]);
			row5.createCell(4).setCellValue(step[4]);
			row5.createCell(5).setCellValue(step[5]);
			row5.createCell(6).setCellValue(step[6]);
			row5.createCell(7).setCellValue(step[7]);
			row5.createCell(8).setCellValue(step[9]);
			row5.createCell(9).setCellValue(step[10]);

			row5.getCell(1).setCellStyle(thinBorder);
			row5.getCell(2).setCellStyle(thinBorder);
			row5.getCell(3).setCellStyle(thinBorder);
			row5.getCell(4).setCellStyle(thinBorder);
			row5.getCell(5).setCellStyle(thinBorder);
			row5.getCell(6).setCellStyle(thinBorder);
			row5.getCell(7).setCellStyle(thinBorder);
			row5.getCell(9).setCellStyle(thinBorder);

			// result
			if (step[9].contentEquals("Passed")) {
				row5.getCell(8).setCellStyle(resultStylePassed);
			} else if (step[9].contentEquals("Failed")) {
				row5.getCell(8).setCellStyle(resultStyleFailed);
			} else {
				row5.getCell(8).setCellStyle(thinBorder);
			}

			stepCount = stepCount + 1;
			rowNum = rowNum + 1;
		}
		return rowNum;
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
