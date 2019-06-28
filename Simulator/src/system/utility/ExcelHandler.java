package system.utility;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.math3.util.Pair;
import org.apache.poi.ss.usermodel.charts.AxisCrosses;
import org.apache.poi.ss.usermodel.charts.AxisPosition;
import org.apache.poi.ss.usermodel.charts.ChartAxis;
import org.apache.poi.ss.usermodel.charts.ChartDataSource;
import org.apache.poi.ss.usermodel.charts.DataSources;
import org.apache.poi.ss.usermodel.charts.ScatterChartData;
import org.apache.poi.ss.usermodel.charts.ValueAxis;
import org.apache.poi.xssf.usermodel.charts.XSSFChartLegend;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFChart;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import system.utility.dataStructures.SeqNumData;

@SuppressWarnings({ "deprecation" })
public class ExcelHandler {

	public final String outputFolderPath;

	public ExcelHandler() {
		outputFolderPath = "output/";
	}

	/* add column to a sheet */
	public void createSeqNumOutput(String workbookName, ArrayList<SeqNumData> flows) throws IOException {
		int seqNumColNum = 0;
		int seqTimeColNum = 1;
		int ackNumColNum = 2;
		int ackTimeColNum = 3;
		XSSFWorkbook workBook = new XSSFWorkbook();

		FileOutputStream outPutStream = new FileOutputStream(outputFolderPath + workbookName + ".xlsx");

		for (SeqNumData flowData : flows) {
			HashMap<Pair<Integer, Integer>, Pair<Integer, Integer>> seriesCoordinates = new HashMap<Pair<Integer, Integer>, Pair<Integer, Integer>>();
			XSSFSheet sheet = workBook.createSheet(flowData.flowName);
			int seqRowIndex = 0;
			int ackRowIndex = 0;

			// Creating the headers
			XSSFRow headers = sheet.createRow(0);
			XSSFCell seqNumbers = headers.createCell(seqNumColNum);
			seqNumbers.setCellValue("SeqNumbers");
			XSSFCell seqTimes = headers.createCell(seqTimeColNum);
			seqTimes.setCellValue("SeqTimes");
			XSSFCell ackNumbers = headers.createCell(ackNumColNum);
			ackNumbers.setCellValue("ACKNumbers");
			XSSFCell ackTimes = headers.createCell(ackTimeColNum);
			ackTimes.setCellValue("ACKTimes");

			// seqNumbers and Times
			for (Integer seqNum : flowData.seqNumbers.keySet()) {
				seqRowIndex++;
				XSSFRow r = sheet.createRow(seqRowIndex);
				XSSFCell c0 = r.createCell(seqNumColNum);
				c0.setCellValue(seqNum);
				XSSFCell c1 = r.createCell(seqTimeColNum);
				c1.setCellValue(flowData.seqNumbers.get(seqNum));
			}
			Pair<Integer, Integer> seqTimeData = new Pair<Integer, Integer>(seqRowIndex, seqTimeColNum);
			Pair<Integer, Integer> seqNumberData = new Pair<Integer, Integer>(seqRowIndex, seqNumColNum);
			seriesCoordinates.put(seqTimeData, seqNumberData);

			// ackNumbers and Times
			for (Integer ackNum : flowData.ackNumbers.keySet()) {
				ackRowIndex++;
				XSSFRow r = sheet.getRow(ackRowIndex);
				XSSFCell c2 = r.createCell(ackNumColNum);
				c2.setCellValue(ackNum);
				XSSFCell c3 = r.createCell(ackTimeColNum);
				c3.setCellValue(flowData.ackNumbers.get(ackNum));
			}
			Pair<Integer, Integer> ackTimeData = new Pair<Integer, Integer>(ackRowIndex, ackTimeColNum);
			Pair<Integer, Integer> ackNumberData = new Pair<Integer, Integer>(ackRowIndex, ackNumColNum);
			seriesCoordinates.put(ackTimeData, ackNumberData);
			sheet = plotScatterChart(sheet, 6, 2, 200, 200, seriesCoordinates);

		}
		workBook.write(outPutStream);
		workBook.close();
		outPutStream.close();

	}

	private XSSFSheet plotScatterChart(XSSFSheet sheet, int topLeftX, int topLeftY, int length, int height,
			HashMap<Pair<Integer, Integer>, Pair<Integer, Integer>> dataSeries) {
		// Drawing the Graph
		XSSFDrawing drawing = sheet.createDrawingPatriarch();
		XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, topLeftX, topLeftY, topLeftX + length,
				topLeftY + height);
		XSSFChart chart = drawing.createChart(anchor);
		XSSFChartLegend legend = chart.getOrCreateLegend();
		legend.setPosition(org.apache.poi.ss.usermodel.charts.LegendPosition.BOTTOM);
		ScatterChartData data = chart.getChartDataFactory().createScatterChartData();
		ChartAxis xAxis = chart.getChartAxisFactory().createCategoryAxis(AxisPosition.BOTTOM);

		ValueAxis yAxis = chart.getChartAxisFactory().createValueAxis(AxisPosition.LEFT);
		yAxis.setCrosses(AxisCrosses.AUTO_ZERO);

		for (Pair<Integer, Integer> serie : dataSeries.keySet()) {
			ChartDataSource<Number> xData = DataSources.fromNumericCellRange(sheet,
					new CellRangeAddress(1, serie.getFirst(), serie.getSecond(), serie.getSecond()));
			ChartDataSource<Number> yData = DataSources.fromNumericCellRange(sheet,
					new CellRangeAddress(1, dataSeries.get(serie).getFirst(), dataSeries.get(serie).getSecond(),
							dataSeries.get(serie).getSecond()));
			data.addSerie(xData, yData);
		}

		/* Plot the chart with the inputs from data and chart axis */
		chart.plot(data, new ChartAxis[] { xAxis, yAxis });

		return sheet;
	}

}
