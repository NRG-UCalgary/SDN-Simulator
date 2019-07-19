package system.utility.excel;

import java.io.*;

import org.apache.commons.math3.util.Pair;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import system.charts.datastructures.CategoryFactorBarTableData;
import system.charts.datastructures.CategoryFactorOutputData;
import system.charts.datastructures.NumericFactorOutputData;
import system.charts.datastructures.NumericFactorScatterTableData;

import org.apache.poi.xssf.usermodel.XSSFCell;

public class ExcelHandler {

	public ExcelHandler() {
	}

	public static void createNumericFactorStudyOutput(String outputPath, String fileName,
			NumericFactorOutputData outputData) throws IOException {
		FileOutputStream outPutStream = new FileOutputStream(outputPath + fileName + ".xlsx");
		XSSFWorkbook workbook = new XSSFWorkbook();
		for (String sheetName : outputData.outputSheets.keySet()) {
			XSSFSheet sheet = workbook.createSheet(sheetName);
			createNumericScatterTableInSheet(sheet, outputData.outputSheets.get(sheetName));
			ChartPlotter.plotNumericalScatterChart(sheet, sheetName, outputData.outputSheets.get(sheetName));
		}
		workbook.write(outPutStream);
		workbook.close();
		outPutStream.close();

	}

	public static void createCategoryFactorStudyOutput(String outputPath, String fileName,
			CategoryFactorOutputData outputData) throws IOException {
		FileOutputStream outPutStream = new FileOutputStream(outputPath + fileName + ".xlsx");
		XSSFWorkbook workbook = new XSSFWorkbook();
		for (String sheetName : outputData.outputSheets.keySet()) {
			XSSFSheet sheet = workbook.createSheet(sheetName);
			createCategoryScatterTableInSheet(sheet, outputData.outputSheets.get(sheetName));
			ChartPlotter.plotCategoryBarChart(sheet, sheetName, outputData.outputSheets.get(sheetName));
		}
		workbook.write(outPutStream);
		workbook.close();
		outPutStream.close();

	}

	private static XSSFSheet createCategoryScatterTableInSheet(XSSFSheet sheet, CategoryFactorBarTableData table) {
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
			for (Pair<String, Float> data : table.data.get(seriesTitle)) {
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

	private static XSSFSheet createNumericScatterTableInSheet(XSSFSheet sheet, NumericFactorScatterTableData table) {
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
