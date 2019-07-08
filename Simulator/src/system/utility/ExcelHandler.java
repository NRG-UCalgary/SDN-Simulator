package system.utility;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.math3.util.Pair;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xddf.usermodel.chart.AxisCrosses;
import org.apache.poi.xddf.usermodel.chart.AxisPosition;
import org.apache.poi.xddf.usermodel.chart.ChartTypes;
import org.apache.poi.xddf.usermodel.chart.LegendPosition;
import org.apache.poi.xddf.usermodel.chart.MarkerStyle;
import org.apache.poi.xddf.usermodel.chart.ScatterStyle;
import org.apache.poi.xddf.usermodel.chart.XDDFChart;
import org.apache.poi.xddf.usermodel.chart.XDDFChartLegend;
import org.apache.poi.xddf.usermodel.chart.XDDFDataSourcesFactory;
import org.apache.poi.xddf.usermodel.chart.XDDFNumericalDataSource;
import org.apache.poi.xddf.usermodel.chart.XDDFScatterChartData;
import org.apache.poi.xddf.usermodel.chart.XDDFValueAxis;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.apache.commons.math3.util.Pair;
import org.apache.poi.ss.usermodel.charts.ChartAxis;
import org.apache.poi.ss.usermodel.charts.ChartDataSource;
import org.apache.poi.ss.usermodel.charts.DataSources;
import org.apache.poi.ss.usermodel.charts.LineChartData;
import org.apache.poi.ss.usermodel.charts.ScatterChartData;
import org.apache.poi.ss.usermodel.charts.ValueAxis;
import org.apache.poi.xssf.usermodel.charts.XSSFChartLegend;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xddf.usermodel.chart.ChartTypes;
import org.apache.poi.xddf.usermodel.chart.LegendPosition;
import org.apache.poi.xddf.usermodel.chart.XDDFChart;
import org.apache.poi.xddf.usermodel.chart.XDDFChartLegend;
import org.apache.poi.xddf.usermodel.chart.XDDFDataSourcesFactory;
import org.apache.poi.xddf.usermodel.chart.XDDFNumericalDataSource;
import org.apache.poi.xddf.usermodel.chart.XDDFScatterChartData;
import org.apache.poi.xddf.usermodel.chart.XDDFValueAxis;
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

	int FirstRowOfData = 1;
	public final String outputFolderPath;

	public ExcelHandler() {
		outputFolderPath = "output/";
	}

	public void createBottleneckUtilizationGraph(String workbookName, Map<Double, Integer> data) throws IOException {
		int arrivalTimeColNum = 0;
		int flowIDColNum = 1;
		XSSFWorkbook workBook = new XSSFWorkbook();
		FileOutputStream outPutStream = new FileOutputStream(outputFolderPath + workbookName + ".xlsx");

		HashMap<Pair<Integer, Integer>, Pair<Integer, Integer>> seriesCoordinates = new HashMap<Pair<Integer, Integer>, Pair<Integer, Integer>>();
		XSSFSheet sheet = workBook.createSheet();
		int rowIndex = 0;
		// Creating the headers
		XSSFRow headers = sheet.createRow(0);
		XSSFCell arrivalTimes = headers.createCell(arrivalTimeColNum);
		arrivalTimes.setCellValue("arrivalTimes");
		XSSFCell flowIDs = headers.createCell(flowIDColNum);
		flowIDs.setCellValue("FlowIDs");

		// ArrivalTimes and FlowIDs
		for (Double arrivalTime : data.keySet()) {
			rowIndex++;
			XSSFRow r = sheet.createRow(rowIndex);
			XSSFCell c0 = r.createCell(arrivalTimeColNum);
			c0.setCellValue(arrivalTime);
			XSSFCell c1 = r.createCell(flowIDColNum);
			c1.setCellValue(data.get(arrivalTime));
		}
		Pair<Integer, Integer> flowIDsData = new Pair<Integer, Integer>(rowIndex, flowIDColNum);
		Pair<Integer, Integer> arrivalsTimeData = new Pair<Integer, Integer>(rowIndex, arrivalTimeColNum);
		seriesCoordinates.put(arrivalsTimeData, flowIDsData);
		// sheet = plotScatterChart(sheet, 6, 2, 200, 200, seriesCoordinates);
		workBook.write(outPutStream);
		workBook.close();
		outPutStream.close();
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
			HashMap<String, Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> seriesCoordinates = new HashMap<String, Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>>();
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
			seriesCoordinates.put("SeqNumbs",
					new Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>(seqTimeData, seqNumberData));

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
			seriesCoordinates.put("Acks",
					new Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>(ackTimeData, ackNumberData));
			sheet = plotScatterChart(sheet, "SeqNumPlot", "Time", "SeqNumber", new Pair<Integer, Integer>(6, 6), 200,
					200, seriesCoordinates);

		}
		workBook.write(outPutStream);
		workBook.close();
		outPutStream.close();

	}

	public XSSFSheet plotScatterChart(XSSFSheet sheet, String chartTitle, String xAxisTitle, String yAxisTitle,
			Pair<Integer, Integer> topLeftPos, int width, int height,
			HashMap<String, Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> allSeries) {

		XSSFDrawing drawer = sheet.createDrawingPatriarch();
		XSSFClientAnchor anchor = drawer.createAnchor(0, 0, 0, 0, topLeftPos.getFirst(), topLeftPos.getSecond(),
				topLeftPos.getFirst() + width, topLeftPos.getSecond() + height);
		XDDFChart chart = drawer.createChart(anchor);
		chart.setTitleText(chartTitle);
		chart.setTitleOverlay(false);
		XDDFChartLegend legend = chart.getOrAddLegend();
		legend.setPosition(LegendPosition.TOP_RIGHT);

		XDDFValueAxis xAxis = chart.createValueAxis(AxisPosition.BOTTOM);
		xAxis.setTitle(xAxisTitle);
		xAxis.setCrosses(AxisCrosses.AUTO_ZERO);
		XDDFValueAxis yAxis = chart.createValueAxis(AxisPosition.LEFT);
		yAxis.setTitle(yAxisTitle);
		yAxis.setCrosses(AxisCrosses.AUTO_ZERO);
		// TODO Axis formatting based on data
		XDDFScatterChartData data = (XDDFScatterChartData) chart.createData(ChartTypes.SCATTER, xAxis, yAxis);
		data.setStyle(ScatterStyle.MARKER);

		for (String seriesTitle : allSeries.keySet()) {
			XDDFNumericalDataSource<Double> xData = XDDFDataSourcesFactory.fromNumericCellRange(sheet,
					new CellRangeAddress(FirstRowOfData, allSeries.get(seriesTitle).getFirst().getFirst(),
							allSeries.get(seriesTitle).getFirst().getSecond(),
							allSeries.get(seriesTitle).getFirst().getSecond()));
			XDDFNumericalDataSource<Double> yData = XDDFDataSourcesFactory.fromNumericCellRange(sheet,
					new CellRangeAddress(FirstRowOfData, allSeries.get(seriesTitle).getSecond().getFirst(),
							allSeries.get(seriesTitle).getSecond().getSecond(),
							allSeries.get(seriesTitle).getSecond().getSecond()));
			XDDFScatterChartData.Series series = (XDDFScatterChartData.Series) data.addSeries(xData, yData);
			series.setTitle(seriesTitle, null);
			series.setSmooth(false);
			if (seriesTitle == "SeqNumbs") {
				series.setMarkerStyle(MarkerStyle.CIRCLE);
			} else if (seriesTitle == "Acks") {
				series.setMarkerStyle(MarkerStyle.X);
			}
		}
		chart.plot(data);
		return sheet;
	}

}
