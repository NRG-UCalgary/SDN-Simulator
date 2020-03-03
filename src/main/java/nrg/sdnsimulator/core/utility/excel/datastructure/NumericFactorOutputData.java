package nrg.sdnsimulator.core.utility.excel.datastructure;

import java.util.LinkedHashMap;
import java.util.TreeMap;

import nrg.sdnsimulator.core.utility.Keywords;
import nrg.sdnsimulator.core.utility.Statistics;

public class NumericFactorOutputData {
	private LinkedHashMap<String, TreeMap<Float, Float>> avgCompletionTimeData;
	private LinkedHashMap<String, TreeMap<Float, Float>> avgFlowThroughputData;
	private LinkedHashMap<String, TreeMap<Float, Float>> avgStartupDelayData;
	private LinkedHashMap<String, TreeMap<Float, Float>> btlUtilizationData;
	private LinkedHashMap<String, TreeMap<Float, Float>> fairnessIndexData;
	private LinkedHashMap<String, TreeMap<Float, Float>> btlMaxQueueLengthData;
	private LinkedHashMap<String, TreeMap<Float, Float>> btlAvgQueueLengthData;
	private String mainFactorName;
	private LinkedHashMap<String, NumericFactorScatterTableData> outputSheets;

	public NumericFactorOutputData(String mainFactorName,
			LinkedHashMap<String, TreeMap<Float, Statistics>> result) {
		this.mainFactorName = mainFactorName;
		outputSheets = new LinkedHashMap<String, NumericFactorScatterTableData>();

		avgCompletionTimeData = new LinkedHashMap<String, TreeMap<Float, Float>>();
		avgStartupDelayData = new LinkedHashMap<String, TreeMap<Float, Float>>();
		avgFlowThroughputData = new LinkedHashMap<String, TreeMap<Float, Float>>();
		fairnessIndexData = new LinkedHashMap<String, TreeMap<Float, Float>>();
		btlUtilizationData = new LinkedHashMap<String, TreeMap<Float, Float>>();
		btlMaxQueueLengthData = new LinkedHashMap<String, TreeMap<Float, Float>>();
		btlAvgQueueLengthData = new LinkedHashMap<String, TreeMap<Float, Float>>();

		for (String seriesName : result.keySet()) {
			initializedSeriesForAllMetrics(seriesName);
			for (Float mainFactorValue : result.get(seriesName).keySet()) {
				insertValuesForAllMetrics(seriesName, mainFactorValue,
						result.get(seriesName).get(mainFactorValue));
			}
		}
		prepareOutputSheets();
	}

	/** ================================================================ **/
	private void addAvgCompletionTimeData() {
		String sheetName = "AvgCompletionTime";
		String xAxisCaption = mainFactorName;
		String yAxisCaption = Keywords.Metrics.Names.AvgFlowCompletionTime;
		NumericFactorScatterTableData table = new NumericFactorScatterTableData(xAxisCaption,
				yAxisCaption);
		for (String seriesName : avgCompletionTimeData.keySet()) {
			table.addSeriesToTable(seriesName, avgCompletionTimeData.get(seriesName));
		}
		outputSheets.put(sheetName, table);
	}

	private void addAvgFlowThroughputData() {
		String sheetName = "AvgFlowThroughput";
		String xAxisCaption = mainFactorName;
		String yAxisCaption = Keywords.Metrics.Names.AvgFlowThroughput;
		NumericFactorScatterTableData table = new NumericFactorScatterTableData(xAxisCaption,
				yAxisCaption);
		for (String seriesName : avgFlowThroughputData.keySet()) {
			table.addSeriesToTable(seriesName, avgFlowThroughputData.get(seriesName));
		}
		outputSheets.put(sheetName, table);
	}

	private void addAvgStartupDelayData() {
		String sheetName = "AvgStartupDelay";
		String xAxisCaption = mainFactorName;
		String yAxisCaption = Keywords.Metrics.Names.AvgFlowStartupDelay;
		NumericFactorScatterTableData table = new NumericFactorScatterTableData(xAxisCaption,
				yAxisCaption);
		for (String seriesName : avgStartupDelayData.keySet()) {
			table.addSeriesToTable(seriesName, avgStartupDelayData.get(seriesName));
		}
		outputSheets.put(sheetName, table);
	}

	private void addFairnessIndexData() {
		String sheetName = "FairnessIndex";
		String xAxisCaption = mainFactorName;
		String yAxisCaption = Keywords.Metrics.Names.FairnessIndex;
		NumericFactorScatterTableData table = new NumericFactorScatterTableData(xAxisCaption,
				yAxisCaption);
		for (String seriesName : fairnessIndexData.keySet()) {
			table.addSeriesToTable(seriesName, fairnessIndexData.get(seriesName));
		}
		outputSheets.put(sheetName, table);
	}

	private void addBtlUtilizationData() {
		String sheetName = "BtlUtil";
		String xAxisCaption = mainFactorName;
		String yAxisCaption = Keywords.Metrics.Names.BtlUtilization;
		NumericFactorScatterTableData table = new NumericFactorScatterTableData(xAxisCaption,
				yAxisCaption);
		for (String seriesName : btlUtilizationData.keySet()) {
			table.addSeriesToTable(seriesName, btlUtilizationData.get(seriesName));
		}
		outputSheets.put(sheetName, table);
	}

	private void addBtlMaxQueueLengthData() {
		String sheetName = "MaxBtlBufferOccupancy";
		String xAxisCaption = mainFactorName;
		String yAxisCaption = Keywords.Metrics.Names.MaxBtlQueueLength;
		NumericFactorScatterTableData table = new NumericFactorScatterTableData(xAxisCaption,
				yAxisCaption);
		for (String seriesName : btlMaxQueueLengthData.keySet()) {
			table.addSeriesToTable(seriesName, btlMaxQueueLengthData.get(seriesName));
		}
		outputSheets.put(sheetName, table);
	}

	private void addBtlAvgQueueLengthData() {
		String sheetName = "AvgBtlBufferOccupancy";
		String xAxisCaption = mainFactorName;
		String yAxisCaption = Keywords.Metrics.Names.AvgBtlQueueLength;
		NumericFactorScatterTableData table = new NumericFactorScatterTableData(xAxisCaption,
				yAxisCaption);
		for (String seriesName : btlAvgQueueLengthData.keySet()) {
			table.addSeriesToTable(seriesName, btlAvgQueueLengthData.get(seriesName));
		}
		outputSheets.put(sheetName, table);
	}

	private void initializedSeriesForAllMetrics(String seriesName) {
		// All metric data structures must be mentioned here
		avgCompletionTimeData.put(seriesName, new TreeMap<Float, Float>());
		avgStartupDelayData.put(seriesName, new TreeMap<Float, Float>());
		avgFlowThroughputData.put(seriesName, new TreeMap<Float, Float>());
		fairnessIndexData.put(seriesName, new TreeMap<Float, Float>());
		btlUtilizationData.put(seriesName, new TreeMap<Float, Float>());
		btlMaxQueueLengthData.put(seriesName, new TreeMap<Float, Float>());
		btlAvgQueueLengthData.put(seriesName, new TreeMap<Float, Float>());

	}

	private void insertValuesForAllMetrics(String seriesName, Float metricValue, Statistics stat) {
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
		addFairnessIndexData();
		addBtlUtilizationData();
		addBtlMaxQueueLengthData();
		addBtlAvgQueueLengthData();
	}

	public LinkedHashMap<String, TreeMap<Float, Float>> getAvgCompletionTimeData() {
		return avgCompletionTimeData;
	}

	public void setAvgCompletionTimeData(
			LinkedHashMap<String, TreeMap<Float, Float>> avgCompletionTimeData) {
		this.avgCompletionTimeData = avgCompletionTimeData;
	}

	public LinkedHashMap<String, TreeMap<Float, Float>> getAvgFlowThroughputData() {
		return avgFlowThroughputData;
	}

	public void setAvgFlowThroughputData(
			LinkedHashMap<String, TreeMap<Float, Float>> avgFlowThroughputData) {
		this.avgFlowThroughputData = avgFlowThroughputData;
	}

	public LinkedHashMap<String, TreeMap<Float, Float>> getAvgStartupDelayData() {
		return avgStartupDelayData;
	}

	public void setAvgStartupDelayData(
			LinkedHashMap<String, TreeMap<Float, Float>> avgStartupDelayData) {
		this.avgStartupDelayData = avgStartupDelayData;
	}

	public LinkedHashMap<String, TreeMap<Float, Float>> getBtlUtilizationData() {
		return btlUtilizationData;
	}

	public void setBtlUtilizationData(
			LinkedHashMap<String, TreeMap<Float, Float>> btlUtilizationData) {
		this.btlUtilizationData = btlUtilizationData;
	}

	public LinkedHashMap<String, TreeMap<Float, Float>> getFairnessIndexData() {
		return fairnessIndexData;
	}

	public void setFairnessIndexData(
			LinkedHashMap<String, TreeMap<Float, Float>> fairnessIndexData) {
		this.fairnessIndexData = fairnessIndexData;
	}

	public LinkedHashMap<String, TreeMap<Float, Float>> getBtlMaxQueueLengthData() {
		return btlMaxQueueLengthData;
	}

	public void setBtlMaxQueueLengthData(
			LinkedHashMap<String, TreeMap<Float, Float>> btlMaxQueueLengthData) {
		this.btlMaxQueueLengthData = btlMaxQueueLengthData;
	}

	public LinkedHashMap<String, TreeMap<Float, Float>> getBtlAvgQueueLengthData() {
		return btlAvgQueueLengthData;
	}

	public void setBtlAvgQueueLengthData(
			LinkedHashMap<String, TreeMap<Float, Float>> btlAvgQueueLengthData) {
		this.btlAvgQueueLengthData = btlAvgQueueLengthData;
	}

	public String getMainFactorName() {
		return mainFactorName;
	}

	public void setMainFactorName(String mainFactorName) {
		this.mainFactorName = mainFactorName;
	}

	public LinkedHashMap<String, NumericFactorScatterTableData> getOutputSheets() {
		return outputSheets;
	}

	public void setOutputSheets(LinkedHashMap<String, NumericFactorScatterTableData> outputSheets) {
		this.outputSheets = outputSheets;
	}

}
