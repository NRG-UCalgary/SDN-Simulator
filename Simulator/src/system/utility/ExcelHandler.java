package system.utility;

import java.io.*;
import java.util.TreeMap;

import org.apache.commons.math3.util.Pair;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;

import system.utility.dataStructures.ScatterTableData;

public class ExcelHandler {

	public final String outputFolderPath;

	private ChartPlotter plotter;

	public ExcelHandler() {
		outputFolderPath = "output/";
		plotter = new ChartPlotter();
	}

	public void createSegmentArrivalToBottleneckOutput(String workbookName, ScatterTableData table) throws IOException {
		FileOutputStream outPutStream = new FileOutputStream(outputFolderPath + workbookName + ".xlsx");
		XSSFWorkbook workBook = new XSSFWorkbook();
		XSSFSheet sheet = workBook.createSheet();
		sheet = createScatterTableInSheet(sheet, table);
		sheet = plotter.plotScatterChart(sheet, Keywords.ArrtivalToBottleneckLink, table);
		workBook.write(outPutStream);
		workBook.close();
		outPutStream.close();
	}

	public void createSeqNumOutput(String workbookName, TreeMap<Integer, ScatterTableData> tables) throws IOException {
		FileOutputStream outPutStream = new FileOutputStream(outputFolderPath + workbookName + ".xlsx");
		XSSFWorkbook workBook = new XSSFWorkbook();
		for (int flowID : tables.keySet()) {
			XSSFSheet sheet = workBook.createSheet("Flow " + flowID);
			sheet = createScatterTableInSheet(sheet, tables.get(flowID));
			sheet = plotter.plotScatterChart(sheet, Keywords.SeqNumPlot, tables.get(flowID));
		}
		workBook.write(outPutStream);
		workBook.close();
		outPutStream.close();
	}

	private XSSFSheet createScatterTableInSheet(XSSFSheet sheet, ScatterTableData table) {
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
			// Merging two title cells and adding the title
			//sheet = mergeCells(sheet, SeriesTitleRowIndex, SeriesTitleRowIndex, seriesColIndex, seriesColIndex + 1,
				//	true);
			//XSSFCell seriesTitleCell = seriesTitleRow.createCell(seriesColIndex);
			//seriesTitleCell.setCellValue(seriesTitle);
			seriesColIndex++;

			// Going for series column headers
			XSSFCell xAxisColHeaderCell = colHeaderRow.createCell(xColIndex);
			xAxisColHeaderCell.setCellValue(table.xAxisColTitle);
			XSSFCell yAxisColHeaderCell = colHeaderRow.createCell(yColIndex);
			yAxisColHeaderCell.setCellValue(table.yAxisColTitle);

			// Putting actual data
			int dataRowIndex = FirstDataRowIndex;
			for (Pair<Double, Integer> data : table.data.get(seriesTitle)) {
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
			xColIndex += 2;
		}
		return sheet;

	}

	private XSSFSheet mergeCells(XSSFSheet sheet, int numRow, int untilRow, int numCol, int untilCol, boolean border) {
		CellRangeAddress cellMerge = new CellRangeAddress(numRow, untilRow, numCol, untilCol);
		sheet.addMergedRegion(cellMerge);
		if (border) {
			setBordersToMergedCells(sheet, cellMerge);
		}
		return sheet;

	}

	private XSSFSheet setBordersToMergedCells(XSSFSheet sheet, CellRangeAddress rangeAddress) {
		RegionUtil.setBorderTop(BorderStyle.MEDIUM, rangeAddress, sheet);
		RegionUtil.setBorderLeft(BorderStyle.MEDIUM, rangeAddress, sheet);
		RegionUtil.setBorderRight(BorderStyle.MEDIUM, rangeAddress, sheet);
		RegionUtil.setBorderBottom(BorderStyle.MEDIUM, rangeAddress, sheet);
		return sheet;
	}

}
