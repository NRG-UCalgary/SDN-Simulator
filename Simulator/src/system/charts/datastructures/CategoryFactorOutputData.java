package system.charts.datastructures;

import java.util.LinkedHashMap;

import system.utility.Statistics;

public class CategoryFactorOutputData {
	private String mainFactorName;
	private LinkedHashMap<String, LinkedHashMap<String, Float>> avgCompletionTimeData;
	private LinkedHashMap<String, LinkedHashMap<String, Float>> avgStartupDelayData;

	public LinkedHashMap<String, CategoryFactorBarTableData> outputSheets;

	public CategoryFactorOutputData(String mainFactorName,
			LinkedHashMap<String, LinkedHashMap<String, Statistics>> result) {
		this.mainFactorName = mainFactorName;
		avgCompletionTimeData = new LinkedHashMap<String, LinkedHashMap<String, Float>>();
		avgStartupDelayData = new LinkedHashMap<String, LinkedHashMap<String, Float>>();
		outputSheets = new LinkedHashMap<String, CategoryFactorBarTableData>();
		for (String seriesName : result.keySet()) {
			initializedSeriesForAllMetrics(seriesName);
			for (String mainFactorValue : result.get(seriesName).keySet()) {
				insertValuesForAllMetrics(seriesName, mainFactorValue, result.get(seriesName).get(mainFactorValue));
			}
		}
		prepareOutputSheets();

	}

	private void initializedSeriesForAllMetrics(String seriesName) {
		// All metric data structures must be mentioned here
		avgCompletionTimeData.put(seriesName, new LinkedHashMap<String, Float>());
		avgStartupDelayData.put(seriesName, new LinkedHashMap<String, Float>());
	}

	private void insertValuesForAllMetrics(String seriesName, String metricValue, Statistics stat) {
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
		CategoryFactorBarTableData table = new CategoryFactorBarTableData(xAxisCaption, yAxisCaption);
		for (String seriesName : avgCompletionTimeData.keySet()) {
			table.addSeriesToTable(seriesName, avgCompletionTimeData.get(seriesName));
		}
		outputSheets.put(sheetName, table);
	}

	private void addAvgStartupDelayData() {
		String sheetName = "AvgStartupDelay";
		String xAxisCaption = mainFactorName;
		String yAxisCaption = "Avg Startup Delay (ms)";
		CategoryFactorBarTableData table = new CategoryFactorBarTableData(xAxisCaption, yAxisCaption);
		for (String seriesName : avgStartupDelayData.keySet()) {
			table.addSeriesToTable(seriesName, avgStartupDelayData.get(seriesName));
		}
		outputSheets.put(sheetName, table);
	}
}
