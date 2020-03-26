package nrg.sdnsimulator.core.utility.excel;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;

import org.apache.commons.math3.util.Pair;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import lombok.Getter;
import lombok.Setter;
import nrg.sdnsimulator.core.utility.excel.datastructure.CategoryFactorBarTableData;
import nrg.sdnsimulator.core.utility.excel.datastructure.CategoryFactorOutputData;
import nrg.sdnsimulator.core.utility.excel.datastructure.NumericFactorOutputData;
import nrg.sdnsimulator.core.utility.excel.datastructure.NumericFactorScatterTableData;

@Getter
@Setter
public class ExcelHandler {

	private static DataFormat format;
	private static CellStyle xAxisStyle;
	private static CellStyle yAxisStyle;

	public static void createCategoryFactorStudyOutput(String outputPath, String fileName,
			CategoryFactorOutputData outputData) throws IOException {
		FileOutputStream outPutStream = new FileOutputStream(outputPath + fileName + ".xlsx");
		XSSFWorkbook workbook = new XSSFWorkbook();
		format = workbook.createDataFormat();
		xAxisStyle = workbook.createCellStyle();
		xAxisStyle.setDataFormat(format.getFormat("#.####"));
		yAxisStyle = workbook.createCellStyle();
		yAxisStyle.setDataFormat(format.getFormat("#"));
		for (String sheetName : outputData.getOutputSheets().keySet()) {
			XSSFSheet sheet = workbook.createSheet(sheetName);
			createCategoryScatterTableInSheet(sheet, outputData.getOutputSheets().get(sheetName));
			ChartPlotter.plotCategoryBarChart(sheet, sheetName, outputData.getOutputSheets().get(sheetName));
		}
		workbook.write(outPutStream);
		workbook.close();
		outPutStream.close();

	}

	private static XSSFSheet createCategoryScatterTableInSheet(XSSFSheet sheet, CategoryFactorBarTableData table) {
		int SeriesTitleRowIndex = table.getSeriesTitleRowIndex();
		int ColumnHeaderRowIndex = table.getColumnHeaderRowIndex();
		int FirstDataRowIndex = table.getFirstDataRowIndex();
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
		for (String seriesTitle : table.getData().keySet()) {
			int yColIndex = xColIndex + 1;
			sheet = mergeCells(sheet, SeriesTitleRowIndex, SeriesTitleRowIndex, seriesColIndex, seriesColIndex + 1);
			XSSFCell seriesTitleCell = seriesTitleRow.createCell(seriesColIndex);
			CellUtil.setAlignment(seriesTitleCell, HorizontalAlignment.CENTER);
			seriesTitleCell.setCellValue(seriesTitle);
			seriesColIndex += 2;

			// Going for series column headers
			XSSFCell xAxisColHeaderCell = colHeaderRow.createCell(xColIndex);
			xAxisColHeaderCell.setCellValue(table.getXAxisColTitle());
			XSSFCell yAxisColHeaderCell = colHeaderRow.createCell(yColIndex);
			yAxisColHeaderCell.setCellValue(table.getYAxisColTitle());

			// Putting actual data
			int dataRowIndex = FirstDataRowIndex;
			for (Pair<String, Float> data : table.getData().get(seriesTitle)) {
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

	public static void createNumericFactorStudyOutput(String outputPath, String fileName,
			NumericFactorOutputData outputData) throws IOException {
		FileOutputStream outPutStream = new FileOutputStream(outputPath + fileName + ".xlsx");
		XSSFWorkbook workbook = new XSSFWorkbook();
		format = workbook.createDataFormat();
		xAxisStyle = workbook.createCellStyle();
		xAxisStyle.setDataFormat(format.getFormat("#.####"));
		yAxisStyle = workbook.createCellStyle();
		yAxisStyle.setDataFormat(format.getFormat("#.####"));
		for (String sheetName : outputData.getOutputSheets().keySet()) {
			XSSFSheet sheet = workbook.createSheet(sheetName);
			createNumericScatterTableInSheet(sheet, outputData.getOutputSheets().get(sheetName));
			ChartPlotter.plotNumericalScatterChart(sheet, sheetName, outputData.getOutputSheets().get(sheetName));
		}
		workbook.write(outPutStream);
		workbook.close();
		outPutStream.close();

	}

	private static XSSFSheet createNumericScatterTableInSheet(XSSFSheet sheet, NumericFactorScatterTableData table) {
		int SeriesTitleRowIndex = table.getSeriesTitleRowIndex();
		int ColumnHeaderRowIndex = table.getColumnHeaderRowIndex();
		int FirstDataRowIndex = table.getFirstDataRowIndex();
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
		for (String seriesTitle : table.getData().keySet()) {
			int yColIndex = xColIndex + 1;
			sheet = mergeCells(sheet, SeriesTitleRowIndex, SeriesTitleRowIndex, seriesColIndex, seriesColIndex + 1);
			XSSFCell seriesTitleCell = seriesTitleRow.createCell(seriesColIndex);
			CellUtil.setAlignment(seriesTitleCell, HorizontalAlignment.CENTER);
			seriesTitleCell.setCellValue(seriesTitle);
			seriesColIndex += 2;

			// Going for series column headers
			XSSFCell xAxisColHeaderCell = colHeaderRow.createCell(xColIndex);
			xAxisColHeaderCell.setCellValue(table.getXAxisColTitle());
			XSSFCell yAxisColHeaderCell = colHeaderRow.createCell(yColIndex);
			yAxisColHeaderCell.setCellValue(table.getYAxisColTitle());

			// Putting actual data
			int dataRowIndex = FirstDataRowIndex;
			for (Pair<Float, Float> data : table.getData().get(seriesTitle)) {
				XSSFRow dataRow;
				if ((dataRow = sheet.getRow(dataRowIndex)) == null) {
					dataRow = sheet.createRow(dataRowIndex);
				}
				XSSFCell xAxisDataCell = dataRow.createCell(xColIndex);
				xAxisDataCell.setCellValue(data.getFirst());
				xAxisDataCell.setCellStyle(xAxisStyle);
				XSSFCell yAxisDataCell = dataRow.createCell(yColIndex);
				yAxisDataCell.setCellValue(data.getSecond());
				yAxisDataCell.setCellStyle(yAxisStyle);
				dataRowIndex++;
			}
			sheet.autoSizeColumn(xColIndex);
			sheet.autoSizeColumn(yColIndex);
			sheet.autoSizeColumn(seriesColIndex);
			xColIndex += 2;
		}
		return sheet;
	}

	public static void createValidationOutput(String outputPath, String fileName,
			LinkedHashMap<String, NumericFactorScatterTableData> outputData) throws IOException {
		FileOutputStream outPutStream = new FileOutputStream(outputPath + fileName + ".xlsx");
		XSSFWorkbook workbook = new XSSFWorkbook();
		format = workbook.createDataFormat();
		xAxisStyle = workbook.createCellStyle();
		xAxisStyle.setDataFormat(format.getFormat("#.###"));
		yAxisStyle = workbook.createCellStyle();
		yAxisStyle.setDataFormat(format.getFormat("#"));
		for (String sheetName : outputData.keySet()) {
			XSSFSheet sheet = workbook.createSheet(sheetName);
			createNumericScatterTableInSheet(sheet, outputData.get(sheetName));
			ChartPlotter.plotNumericalScatterChart(sheet, sheetName, outputData.get(sheetName));
		}
		workbook.write(outPutStream);
		workbook.close();
		outPutStream.close();
	}

	private static XSSFSheet mergeCells(XSSFSheet sheet, int numRow, int untilRow, int numCol, int untilCol) {
		CellRangeAddress cellMerge = new CellRangeAddress(numRow, untilRow, numCol, untilCol);
		sheet.addMergedRegion(cellMerge);
		return sheet;
	}

}
