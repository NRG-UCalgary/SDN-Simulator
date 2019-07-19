package system.charts.datastructures;

import java.util.LinkedHashMap;
import java.util.TreeMap;

import system.utility.Statistics;

public class NumericFactorOutputData {
	private String mainFactorName;
	private LinkedHashMap<String, TreeMap<Float, Float>> avgCompletionTimeData;
	private LinkedHashMap<String, TreeMap<Float, Float>> avgStartupDelayData;
	public LinkedHashMap<String, NumericFactorScatterTableData> outputSheets;

	public NumericFactorOutputData(String mainFactorName, LinkedHashMap<String, TreeMap<Float, Statistics>> result) {
		this.mainFactorName = mainFactorName;
		avgCompletionTimeData = new LinkedHashMap<String, TreeMap<Float, Float>>();
		avgStartupDelayData = new LinkedHashMap<String, TreeMap<Float, Float>>();
		outputSheets = new LinkedHashMap<String, NumericFactorScatterTableData>();

		for (String seriesName : result.keySet()) {
			initializedSeriesForAllMetrics(seriesName);
			for (Float mainFactorValue : result.get(seriesName).keySet()) {
				insertValuesForAllMetrics(seriesName, mainFactorValue, result.get(seriesName).get(mainFactorValue));
			}
		}
		prepareOutputSheets();

	}

	private void initializedSeriesForAllMetrics(String seriesName) {
		// All metric data structures must be mentioned here
		avgCompletionTimeData.put(seriesName, new TreeMap<Float, Float>());
		avgStartupDelayData.put(seriesName, new TreeMap<Float, Float>());
	}

	private void insertValuesForAllMetrics(String seriesName, Float metricValue, Statistics stat) {
		// All metric data structures must be mentioned here
		avgCompletionTimeData.get(seriesName).put(metricValue, stat.getAvgFlowCompletionTime());
		avgStartupDelayData.get(seriesName).put(metricValue, stat.getAvgStartupDelay());
	}

	public void prepareOutputSheets() {
		// All metric data structures must be mentioned here
		addAvgCompletionTimeData();
		addAvgStartupDelayData();

	}

	private void addAvgCompletionTimeData() {
		String sheetName = "AvgCompletionTime";
		String xAxisCaption = mainFactorName;
		String yAxisCaption = "Avg Flow Completion Time (ms)";
		NumericFactorScatterTableData table = new NumericFactorScatterTableData(xAxisCaption, yAxisCaption);
		for (String seriesName : avgCompletionTimeData.keySet()) {
			table.addSeriesToTable(seriesName, avgCompletionTimeData.get(seriesName));
		}
		outputSheets.put(sheetName, table);
	}

	private void addAvgStartupDelayData() {
		String sheetName = "AvgStartupDelay";
		String xAxisCaption = mainFactorName;
		String yAxisCaption = "Avg Startup Delay (ms)";
		NumericFactorScatterTableData table = new NumericFactorScatterTableData(xAxisCaption, yAxisCaption);
		for (String seriesName : avgStartupDelayData.keySet()) {
			table.addSeriesToTable(seriesName, avgStartupDelayData.get(seriesName));
		}
		outputSheets.put(sheetName, table);
	}

}
