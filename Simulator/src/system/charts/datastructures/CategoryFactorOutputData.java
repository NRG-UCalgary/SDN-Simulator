package system.charts.datastructures;

import java.util.LinkedHashMap;

import system.utility.Statistics;

public class CategoryFactorOutputData {
	private String mainFactorName;
	private LinkedHashMap<String, LinkedHashMap<String, Float>> avgCompletionTimeData;
	private LinkedHashMap<String, LinkedHashMap<String, Float>> avgStartupDelayData;
	private LinkedHashMap<String, LinkedHashMap<String, Float>> maxBtlBufferOccupancyData;
	private LinkedHashMap<String, LinkedHashMap<String, Float>> varianceOfBtlUtilizationSharePerFlowSizeData;
	private LinkedHashMap<String, LinkedHashMap<String, Float>> btlUtilizationData;
	private LinkedHashMap<String, LinkedHashMap<String, Float>> avgFlowThroughputData;
	private LinkedHashMap<String, LinkedHashMap<String, Float>> flowRejectionPercentageData;
	private LinkedHashMap<String, LinkedHashMap<String, Float>> varianceOfFlowCompletionTimePerFlowSizeData;

	public LinkedHashMap<String, CategoryFactorBarTableData> outputSheets;

	public CategoryFactorOutputData(String mainFactorName,
			LinkedHashMap<String, LinkedHashMap<String, Statistics>> result) {
		this.mainFactorName = mainFactorName;
		outputSheets = new LinkedHashMap<String, CategoryFactorBarTableData>();

		// All metric data structures must be initialized here
		avgCompletionTimeData = new LinkedHashMap<String, LinkedHashMap<String, Float>>();
		avgStartupDelayData = new LinkedHashMap<String, LinkedHashMap<String, Float>>();
		maxBtlBufferOccupancyData = new LinkedHashMap<String, LinkedHashMap<String, Float>>();
		varianceOfBtlUtilizationSharePerFlowSizeData = new LinkedHashMap<String, LinkedHashMap<String, Float>>();
		btlUtilizationData = new LinkedHashMap<String, LinkedHashMap<String, Float>>();
		avgFlowThroughputData = new LinkedHashMap<String, LinkedHashMap<String, Float>>();
		flowRejectionPercentageData = new LinkedHashMap<String, LinkedHashMap<String, Float>>();
		varianceOfFlowCompletionTimePerFlowSizeData = new LinkedHashMap<String, LinkedHashMap<String, Float>>();

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
		maxBtlBufferOccupancyData.put(seriesName, new LinkedHashMap<String, Float>());
		varianceOfBtlUtilizationSharePerFlowSizeData.put(seriesName, new LinkedHashMap<String, Float>());
		btlUtilizationData.put(seriesName, new LinkedHashMap<String, Float>());
		avgFlowThroughputData.put(seriesName, new LinkedHashMap<String, Float>());
		flowRejectionPercentageData.put(seriesName, new LinkedHashMap<String, Float>());
		varianceOfFlowCompletionTimePerFlowSizeData.put(seriesName, new LinkedHashMap<String, Float>());
	}

	private void insertValuesForAllMetrics(String seriesName, String metricValue, Statistics stat) {
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

	private void addMaxBottlebeckBufferOccupancyData() {
		String sheetName = "MaxBtlBufferOccupancy";
		String xAxisCaption = mainFactorName;
		String yAxisCaption = "Max Bottleneck Buffer Occupancy (Packets)";
		CategoryFactorBarTableData table = new CategoryFactorBarTableData(xAxisCaption, yAxisCaption);
		for (String seriesName : maxBtlBufferOccupancyData.keySet()) {
			table.addSeriesToTable(seriesName, maxBtlBufferOccupancyData.get(seriesName));
		}
		outputSheets.put(sheetName, table);
	}

	private void addVarianceOfBottleneckUtilizationSharePerFlowSizeData() {
		String sheetName = "VarianceOfBtlUtilSharePerFlowSize";
		String xAxisCaption = mainFactorName;
		String yAxisCaption = "Variance of Bottleneck Utilization Share Per Flow Size (%)";
		CategoryFactorBarTableData table = new CategoryFactorBarTableData(xAxisCaption, yAxisCaption);
		for (String seriesName : varianceOfBtlUtilizationSharePerFlowSizeData.keySet()) {
			table.addSeriesToTable(seriesName, varianceOfBtlUtilizationSharePerFlowSizeData.get(seriesName));
		}
		outputSheets.put(sheetName, table);
	}

	private void addBottleneckUtilizationData() {
		String sheetName = "BtlUtil";
		String xAxisCaption = mainFactorName;
		String yAxisCaption = "Bttleneck Utilization (%?)";
		CategoryFactorBarTableData table = new CategoryFactorBarTableData(xAxisCaption, yAxisCaption);
		for (String seriesName : btlUtilizationData.keySet()) {
			table.addSeriesToTable(seriesName, btlUtilizationData.get(seriesName));
		}
		outputSheets.put(sheetName, table);
	}

	private void addAvgFlowThroughputData() {
		String sheetName = "AvgFlowThroughput";
		String xAxisCaption = mainFactorName;
		String yAxisCaption = "Avg Flow Throughput (%?)";
		CategoryFactorBarTableData table = new CategoryFactorBarTableData(xAxisCaption, yAxisCaption);
		for (String seriesName : avgFlowThroughputData.keySet()) {
			table.addSeriesToTable(seriesName, avgFlowThroughputData.get(seriesName));
		}
		outputSheets.put(sheetName, table);
	}

	private void addFlowRejectionPercentageData() {
		String sheetName = "flowRejectionRate";
		String xAxisCaption = mainFactorName;
		String yAxisCaption = "Flow Rejection Rate (%)";
		CategoryFactorBarTableData table = new CategoryFactorBarTableData(xAxisCaption, yAxisCaption);
		for (String seriesName : flowRejectionPercentageData.keySet()) {
			table.addSeriesToTable(seriesName, flowRejectionPercentageData.get(seriesName));
		}
		outputSheets.put(sheetName, table);
	}

	private void addVarianceOfFlowCompletionTimePerFlowSizeData() {
		String sheetName = "varianceOfFlowCompletionTime";
		String xAxisCaption = mainFactorName;
		String yAxisCaption = "Variance of Flow Completion Time Per Flow Size (%?)";
		CategoryFactorBarTableData table = new CategoryFactorBarTableData(xAxisCaption, yAxisCaption);
		for (String seriesName : varianceOfFlowCompletionTimePerFlowSizeData.keySet()) {
			table.addSeriesToTable(seriesName, varianceOfFlowCompletionTimePerFlowSizeData.get(seriesName));
		}
		outputSheets.put(sheetName, table);
	}
}
