package system.utility;

import java.io.*;
import java.util.TreeMap;

import org.apache.commons.math3.util.Pair;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;

import system.utility.dataStructures.NumberOFFlowsOutputData;
import system.utility.dataStructures.ScatterTableData;

public class ExcelHandler {

	public ExcelHandler() {
	}

	public static void createNumberOfFlowsStudyOutput(String outputPath, NumberOFFlowsOutputData outputData)
			throws IOException {
		FileOutputStream outPutStream = new FileOutputStream(outputPath + "NumberOfFlowsStudy.xlsx");
		XSSFWorkbook workbook = new XSSFWorkbook();
		for (String sheetName : outputData.outputSheets.keySet()) {
			XSSFSheet sheet = workbook.createSheet(sheetName);
			createScatterTableInSheet(sheet, outputData.outputSheets.get(sheetName));
			// ChartPlotter.plotScatterChart(sheet, Keywords.ArrtivalToBottleneckLink,
			// outputData.outputSheets.get(sheetName));
		}
		workbook.write(outPutStream);
		workbook.close();
		outPutStream.close();
	}

	public static void createSegmentArrivalToBottleneckOutput(String outputPath, ScatterTableData table)
			throws IOException {
		FileOutputStream outPutStream = new FileOutputStream(outputPath + "SegmentArrivalToBottleneck.xlsx");
		XSSFWorkbook workBook = new XSSFWorkbook();
		XSSFSheet sheet = workBook.createSheet();
		createScatterTableInSheet(sheet, table);
		ChartPlotter.plotScatterChart(sheet, Keywords.ArrtivalToBottleneckLink, table);
		workBook.write(outPutStream);
		workBook.close();
		outPutStream.close();
	}

	public static void createSeqNumOutput(String outputPath, TreeMap<Integer, ScatterTableData> tables)
			throws IOException {
		FileOutputStream outPutStream = new FileOutputStream(outputPath + "SequenceNumberPerFlowID.xlsx");
		XSSFWorkbook workBook = new XSSFWorkbook();
		for (int flowID : tables.keySet()) {
			XSSFSheet sheet = workBook.createSheet("Flow " + flowID);
			createScatterTableInSheet(sheet, tables.get(flowID));
			ChartPlotter.plotScatterChart(sheet, Keywords.SeqNumPlot, tables.get(flowID));
		}
		workBook.write(outPutStream);
		workBook.close();
		outPutStream.close();
	}

	private static XSSFSheet createScatterTableInSheet(XSSFSheet sheet, ScatterTableData table) {
		int SeriesTitleRowIndex = table.SeriesTitleRowIndex;
		int ColumnHeaderRowIndex = table.ColumnHeaderRowIndex;
		int FirstDataRowIndex = table.FirstDataRowIndex;
		XSSFRow seriesTitleRow;
		if ((seriesTitleRow = sheet.getRow(SeriesTitleRowIndex)) == null) {
			seriesTitleRow = sheet.createRow(SeriesTitleRowIndex);
		}
		XSSFRow colHeaderRow;
		if ((colHeaderRow = sheet.getRow(ColumnHeaderRowIndex)) == null) {
			colHeaderRow = sheet.createRow(ColumnHeaderRowIndex);
		}
		int seriesColIndex = 0;
		int xColIndex = 0;
		for (String seriesTitle : table.data.keySet()) {
			int yColIndex = xColIndex + 1;
			sheet = mergeCells(sheet, SeriesTitleRowIndex, SeriesTitleRowIndex, seriesColIndex, seriesColIndex + 1);
			XSSFCell seriesTitleCell = seriesTitleRow.createCell(seriesColIndex);
			CellUtil.setAlignment(seriesTitleCell, HorizontalAlignment.CENTER);
			seriesTitleCell.setCellValue(seriesTitle);
			seriesColIndex += 2;

			// Going for series column headers
			XSSFCell xAxisColHeaderCell = colHeaderRow.createCell(xColIndex);
			xAxisColHeaderCell.setCellValue(table.xAxisColTitle);
			XSSFCell yAxisColHeaderCell = colHeaderRow.createCell(yColIndex);
			yAxisColHeaderCell.setCellValue(table.yAxisColTitle);

			// Putting actual data
			int dataRowIndex = FirstDataRowIndex;
			for (Pair<Float, Float> data : table.data.get(seriesTitle)) {
				XSSFRow dataRow;
				if ((dataRow = sheet.getRow(dataRowIndex)) == null) {
					dataRow = sheet.createRow(dataRowIndex);
				}
				XSSFCell xAxisDataCell = dataRow.createCell(xColIndex);
				xAxisDataCell.setCellValue(data.getFirst());
				XSSFCell yAxisDataCell = dataRow.createCell(yColIndex);
				yAxisDataCell.setCellValue(data.getSecond());
				dataRowIndex++;
			}
			sheet.autoSizeColumn(xColIndex);
			sheet.autoSizeColumn(yColIndex);
			sheet.autoSizeColumn(seriesColIndex);
			xColIndex += 2;
		}
		return sheet;
	}

	private static XSSFSheet mergeCells(XSSFSheet sheet, int numRow, int untilRow, int numCol, int untilCol) {
		CellRangeAddress cellMerge = new CellRangeAddress(numRow, untilRow, numCol, untilCol);
		sheet.addMergedRegion(cellMerge);
		return sheet;
	}

}
