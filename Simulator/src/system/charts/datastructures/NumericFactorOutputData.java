package system.charts.datastructures;

import java.util.LinkedHashMap;
import java.util.TreeMap;

import system.utility.Statistics;

public class NumericFactorOutputData {
	private String mainFactorName;
	private LinkedHashMap<String, TreeMap<Float, Float>> avgCompletionTimeData;
	private LinkedHashMap<String, TreeMap<Float, Float>> avgStartupDelayData;
	private LinkedHashMap<String, TreeMap<Float, Float>> maxBtlBufferOccupancyData;
	private LinkedHashMap<String, TreeMap<Float, Float>> varianceOfBtlUtilizationSharePerFlowSizeData;
	private LinkedHashMap<String, TreeMap<Float, Float>> btlUtilizationData;
	private LinkedHashMap<String, TreeMap<Float, Float>> avgFlowThroughputData;
	private LinkedHashMap<String, TreeMap<Float, Float>> flowRejectionPercentageData;
	private LinkedHashMap<String, TreeMap<Float, Float>> varianceOfFlowCompletionTimePerFlowSizeData;

	public LinkedHashMap<String, NumericFactorScatterTableData> outputSheets;

	public NumericFactorOutputData(String mainFactorName, LinkedHashMap<String, TreeMap<Float, Statistics>> result) {
		this.mainFactorName = mainFactorName;
		outputSheets = new LinkedHashMap<String, NumericFactorScatterTableData>();

		avgCompletionTimeData = new LinkedHashMap<String, TreeMap<Float, Float>>();
		avgStartupDelayData = new LinkedHashMap<String, TreeMap<Float, Float>>();
		maxBtlBufferOccupancyData = new LinkedHashMap<String, TreeMap<Float, Float>>();
		varianceOfBtlUtilizationSharePerFlowSizeData = new LinkedHashMap<String, TreeMap<Float, Float>>();
		btlUtilizationData = new LinkedHashMap<String, TreeMap<Float, Float>>();
		avgFlowThroughputData = new LinkedHashMap<String, TreeMap<Float, Float>>();
		flowRejectionPercentageData = new LinkedHashMap<String, TreeMap<Float, Float>>();
		varianceOfFlowCompletionTimePerFlowSizeData = new LinkedHashMap<String, TreeMap<Float, Float>>();

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
		maxBtlBufferOccupancyData.put(seriesName, new TreeMap<Float, Float>());
		varianceOfBtlUtilizationSharePerFlowSizeData.put(seriesName, new TreeMap<Float, Float>());
		btlUtilizationData.put(seriesName, new TreeMap<Float, Float>());
		avgFlowThroughputData.put(seriesName, new TreeMap<Float, Float>());
		flowRejectionPercentageData.put(seriesName, new TreeMap<Float, Float>());
		varianceOfFlowCompletionTimePerFlowSizeData.put(seriesName, new TreeMap<Float, Float>());
	}

	private void insertValuesForAllMetrics(String seriesName, Float metricValue, Statistics stat) {
		// All metric data structures must be mentioned here
		avgCompletionTimeData.get(seriesName).put(metricValue, stat.getAvgFlowCompletionTime());
		avgStartupDelayData.get(seriesName).put(metricValue, stat.getAvgStartupDelay());
		maxBtlBufferOccupancyData.get(seriesName).put(metricValue, stat.getMaxBottleneckBufferOccupancy());
		varianceOfBtlUtilizationSharePerFlowSizeData.get(seriesName).put(metricValue,
				stat.getVarianceOfBottleneckUtilizationSharePerFlowSize());
		btlUtilizationData.get(seriesName).put(metricValue, stat.getBottleneckUtilization());
		avgFlowThroughputData.get(seriesName).put(metricValue, stat.getAvgFlowThroughput());
		flowRejectionPercentageData.get(seriesName).put(metricValue, stat.getFlowRejectionPercentage());
		varianceOfFlowCompletionTimePerFlowSizeData.get(seriesName).put(metricValue,
				stat.getVarianceOfFlowCompletionTimePerFlowSize());
	}

	public void prepareOutputSheets() {
		// All metric data structures must be mentioned here
		addAvgCompletionTimeData();
		addAvgStartupDelayData();
		addAvgFlowThroughputData();
		addBottleneckUtilizationData();
		addFlowRejectionPercentageData();
		addMaxBottlebeckBufferOccupancyData();
		addVarianceOfBottleneckUtilizationSharePerFlowSizeData();
		addVarianceOfFlowCompletionTimePerFlowSizeData();

	}

	/** ================================================================ **/
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

	private void addMaxBottlebeckBufferOccupancyData() {
		String sheetName = "MaxBtlBufferOccupancy";
		String xAxisCaption = mainFactorName;
		String yAxisCaption = "Max Bottleneck Buffer Occupancy (Packets)";
		NumericFactorScatterTableData table = new NumericFactorScatterTableData(xAxisCaption, yAxisCaption);
		for (String seriesName : maxBtlBufferOccupancyData.keySet()) {
			table.addSeriesToTable(seriesName, maxBtlBufferOccupancyData.get(seriesName));
		}
		outputSheets.put(sheetName, table);
	}

	private void addVarianceOfBottleneckUtilizationSharePerFlowSizeData() {
		String sheetName = "VarianceOfBtlUtilSharePerFlowSize";
		String xAxisCaption = mainFactorName;
		String yAxisCaption = "Variance of Bottleneck Utilization Share Per Flow Size (%)";
		NumericFactorScatterTableData table = new NumericFactorScatterTableData(xAxisCaption, yAxisCaption);
		for (String seriesName : varianceOfBtlUtilizationSharePerFlowSizeData.keySet()) {
			table.addSeriesToTable(seriesName, varianceOfBtlUtilizationSharePerFlowSizeData.get(seriesName));
		}
		outputSheets.put(sheetName, table);
	}

	private void addBottleneckUtilizationData() {
		String sheetName = "BtlUtil";
		String xAxisCaption = mainFactorName;
		String yAxisCaption = "Bttleneck Utilization (%?)";
		NumericFactorScatterTableData table = new NumericFactorScatterTableData(xAxisCaption, yAxisCaption);
		for (String seriesName : btlUtilizationData.keySet()) {
			table.addSeriesToTable(seriesName, btlUtilizationData.get(seriesName));
		}
		outputSheets.put(sheetName, table);
	}

	private void addAvgFlowThroughputData() {
		String sheetName = "AvgFlowThroughput";
		String xAxisCaption = mainFactorName;
		String yAxisCaption = "Avg Flow Throughput (%?)";
		NumericFactorScatterTableData table = new NumericFactorScatterTableData(xAxisCaption, yAxisCaption);
		for (String seriesName : avgFlowThroughputData.keySet()) {
			table.addSeriesToTable(seriesName, avgFlowThroughputData.get(seriesName));
		}
		outputSheets.put(sheetName, table);
	}

	private void addFlowRejectionPercentageData() {
		String sheetName = "flowRejectionRate";
		String xAxisCaption = mainFactorName;
		String yAxisCaption = "Flow Rejection Rate (%)";
		NumericFactorScatterTableData table = new NumericFactorScatterTableData(xAxisCaption, yAxisCaption);
		for (String seriesName : flowRejectionPercentageData.keySet()) {
			table.addSeriesToTable(seriesName, flowRejectionPercentageData.get(seriesName));
		}
		outputSheets.put(sheetName, table);
	}

	private void addVarianceOfFlowCompletionTimePerFlowSizeData() {
		String sheetName = "varianceOfFlowCompletionTime";
		String xAxisCaption = mainFactorName;
		String yAxisCaption = "Variance of Flow Completion Time Per Flow Size (%?)";
		NumericFactorScatterTableData table = new NumericFactorScatterTableData(xAxisCaption, yAxisCaption);
		for (String seriesName : varianceOfFlowCompletionTimePerFlowSizeData.keySet()) {
			table.addSeriesToTable(seriesName, varianceOfFlowCompletionTimePerFlowSizeData.get(seriesName));
		}
		outputSheets.put(sheetName, table);
	}

}
