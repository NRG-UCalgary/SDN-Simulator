package utility.excel.charts.datastructures;

import java.util.LinkedHashMap;

import utility.Keywords;
import utility.Statistics;

public class CategoryFactorOutputData {
	private LinkedHashMap<String, LinkedHashMap<String, Float>> avgCompletionTimeData;
	private LinkedHashMap<String, LinkedHashMap<String, Float>> avgFlowThroughputData;
	private LinkedHashMap<String, LinkedHashMap<String, Float>> avgStartupDelayData;
	private LinkedHashMap<String, LinkedHashMap<String, Float>> fairnessIndexData;
	private LinkedHashMap<String, LinkedHashMap<String, Float>> btlUtilizationData;
	private LinkedHashMap<String, LinkedHashMap<String, Float>> btlMaxQueueLengthData;
	private LinkedHashMap<String, LinkedHashMap<String, Float>> btlAvgQueueLengthData;

	private String mainFactorName;
	public LinkedHashMap<String, CategoryFactorBarTableData> outputSheets;

	public CategoryFactorOutputData(String mainFactorName,
			LinkedHashMap<String, LinkedHashMap<String, Statistics>> result) {
		this.mainFactorName = mainFactorName;
		outputSheets = new LinkedHashMap<String, CategoryFactorBarTableData>();

		// All metric data structures must be initialized here
		avgCompletionTimeData = new LinkedHashMap<String, LinkedHashMap<String, Float>>();
		avgStartupDelayData = new LinkedHashMap<String, LinkedHashMap<String, Float>>();
		avgFlowThroughputData = new LinkedHashMap<String, LinkedHashMap<String, Float>>();
		fairnessIndexData = new LinkedHashMap<String, LinkedHashMap<String, Float>>();
		btlUtilizationData = new LinkedHashMap<String, LinkedHashMap<String, Float>>();
		btlMaxQueueLengthData = new LinkedHashMap<String, LinkedHashMap<String, Float>>();
		btlAvgQueueLengthData = new LinkedHashMap<String, LinkedHashMap<String, Float>>();

		for (String seriesName : result.keySet()) {
			initializedSeriesForAllMetrics(seriesName);
			for (String mainFactorValue : result.get(seriesName).keySet()) {
				insertValuesForAllMetrics(seriesName, mainFactorValue, result.get(seriesName).get(mainFactorValue));
			}
		}
		prepareOutputSheets();

	}

	private void addAvgCompletionTimeData() {
		String sheetName = "AvgCompletionTime";
		String xAxisCaption = mainFactorName;
		String yAxisCaption = Keywords.Metrics.Names.AvgFlowCompletionTime;
		CategoryFactorBarTableData table = new CategoryFactorBarTableData(xAxisCaption, yAxisCaption);
		for (String seriesName : avgCompletionTimeData.keySet()) {
			table.addSeriesToTable(seriesName, avgCompletionTimeData.get(seriesName));
		}
		outputSheets.put(sheetName, table);
	}

	private void addAvgFlowThroughputData() {
		String sheetName = "AvgFlowThroughput";
		String xAxisCaption = mainFactorName;
		String yAxisCaption = Keywords.Metrics.Names.AvgFlowThroughput;
		CategoryFactorBarTableData table = new CategoryFactorBarTableData(xAxisCaption, yAxisCaption);
		for (String seriesName : avgFlowThroughputData.keySet()) {
			table.addSeriesToTable(seriesName, avgFlowThroughputData.get(seriesName));
		}
		outputSheets.put(sheetName, table);
	}

	private void addAvgStartupDelayData() {
		String sheetName = "AvgStartupDelay";
		String xAxisCaption = mainFactorName;
		String yAxisCaption = Keywords.Metrics.Names.AvgFlowStartupDelay;
		CategoryFactorBarTableData table = new CategoryFactorBarTableData(xAxisCaption, yAxisCaption);
		for (String seriesName : avgStartupDelayData.keySet()) {
			table.addSeriesToTable(seriesName, avgStartupDelayData.get(seriesName));
		}
		outputSheets.put(sheetName, table);
	}

	private void addFairnessIndexData() {
		String sheetName = "FairnessIndex";
		String xAxisCaption = mainFactorName;
		String yAxisCaption = Keywords.Metrics.Names.FairnessIndex;
		CategoryFactorBarTableData table = new CategoryFactorBarTableData(xAxisCaption, yAxisCaption);
		for (String seriesName : fairnessIndexData.keySet()) {
			table.addSeriesToTable(seriesName, fairnessIndexData.get(seriesName));
		}
		outputSheets.put(sheetName, table);
	}

	private void addBottleneckUtilizationData() {
		String sheetName = "BtlUtil";
		String xAxisCaption = mainFactorName;
		String yAxisCaption = Keywords.Metrics.Names.BtlUtilization;
		CategoryFactorBarTableData table = new CategoryFactorBarTableData(xAxisCaption, yAxisCaption);
		for (String seriesName : btlUtilizationData.keySet()) {
			table.addSeriesToTable(seriesName, btlUtilizationData.get(seriesName));
		}
		outputSheets.put(sheetName, table);
	}

	private void addBtlMaxQueueLengthData() {
		String sheetName = "BtlMaxQueueLengthData";
		String xAxisCaption = mainFactorName;
		String yAxisCaption = Keywords.Metrics.Names.MaxBtlQueueLength;
		CategoryFactorBarTableData table = new CategoryFactorBarTableData(xAxisCaption, yAxisCaption);
		for (String seriesName : btlMaxQueueLengthData.keySet()) {
			table.addSeriesToTable(seriesName, btlMaxQueueLengthData.get(seriesName));
		}
		outputSheets.put(sheetName, table);
	}

	private void addBtlAvgQueueLengthData() {
		String sheetName = "BtlAvgQueueLengthData";
		String xAxisCaption = mainFactorName;
		String yAxisCaption = Keywords.Metrics.Names.AvgBtlQueueLength;
		CategoryFactorBarTableData table = new CategoryFactorBarTableData(xAxisCaption, yAxisCaption);
		for (String seriesName : btlAvgQueueLengthData.keySet()) {
			table.addSeriesToTable(seriesName, btlAvgQueueLengthData.get(seriesName));
		}
		outputSheets.put(sheetName, table);
	}

	private void initializedSeriesForAllMetrics(String seriesName) {
		// All metric data structures must be mentioned here
		avgCompletionTimeData.put(seriesName, new LinkedHashMap<String, Float>());
		avgStartupDelayData.put(seriesName, new LinkedHashMap<String, Float>());
		avgFlowThroughputData.put(seriesName, new LinkedHashMap<String, Float>());
		fairnessIndexData.put(seriesName, new LinkedHashMap<String, Float>());
		btlUtilizationData.put(seriesName, new LinkedHashMap<String, Float>());
		btlMaxQueueLengthData.put(seriesName, new LinkedHashMap<String, Float>());
		btlAvgQueueLengthData.put(seriesName, new LinkedHashMap<String, Float>());

	}

	private void insertValuesForAllMetrics(String seriesName, String metricValue, Statistics stat) {
		// All metric data structures must be mentioned here
		avgCompletionTimeData.get(seriesName).put(metricValue, stat.getAvgFlowCompletionTime());
		avgStartupDelayData.get(seriesName).put(metricValue, stat.getAvgStartupDelay());
		avgFlowThroughputData.get(seriesName).put(metricValue, stat.getAvgFlowThroughput());
		fairnessIndexData.get(seriesName).put(metricValue, stat.getFairnessIndex());
		btlUtilizationData.get(seriesName).put(metricValue, stat.getBottleneckUtilization());
		btlMaxQueueLengthData.get(seriesName).put(metricValue, stat.getBtlMaxQueueLength());
		btlAvgQueueLengthData.get(seriesName).put(metricValue, stat.getBtlAvgQueueLength());

	}

	public void prepareOutputSheets() {
		// All metric data structures must be mentioned here
		addAvgCompletionTimeData();
		addAvgStartupDelayData();
		addAvgFlowThroughputData();
		addBottleneckUtilizationData();
		addFairnessIndexData();
		addBtlMaxQueueLengthData();
		addBtlAvgQueueLengthData();

	}
}
