package utility.excel.charts.datastructures;

import java.util.LinkedHashMap;
import java.util.TreeMap;

import utility.Keywords;
import utility.Statistics;

public class NumericFactorOutputData {
	private LinkedHashMap<String, TreeMap<Float, Float>> avgCompletionTimeData;
	private LinkedHashMap<String, TreeMap<Float, Float>> avgFlowThroughputData;
	private LinkedHashMap<String, TreeMap<Float, Float>> avgStartupDelayData;
	private LinkedHashMap<String, TreeMap<Float, Float>> btlUtilizationData;
	private LinkedHashMap<String, TreeMap<Float, Float>> flowRejectionPercentageData;
	private String mainFactorName;
	private LinkedHashMap<String, TreeMap<Float, Float>> maxBtlBufferOccupancyData;
	public LinkedHashMap<String, NumericFactorScatterTableData> outputSheets;
	private LinkedHashMap<String, TreeMap<Float, Float>> varianceOfBtlUtilizationSharePerFlowSizeData;
	
	private LinkedHashMap<String, TreeMap<Float, Float>> varianceOfFlowCompletionTimePerFlowSizeData;

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

	/** ================================================================ **/
	private void addAvgCompletionTimeData() {
		String sheetName = "AvgCompletionTime";
		String xAxisCaption = mainFactorName;
		String yAxisCaption = Keywords.Metrics.Names.AvgFlowCompletionTime;
		NumericFactorScatterTableData table = new NumericFactorScatterTableData(xAxisCaption, yAxisCaption);
		for (String seriesName : avgCompletionTimeData.keySet()) {
			table.addSeriesToTable(seriesName, avgCompletionTimeData.get(seriesName));
		}
		outputSheets.put(sheetName, table);
	}

	private void addAvgFlowThroughputData() {
		String sheetName = "AvgFlowThroughput";
		String xAxisCaption = mainFactorName;
		String yAxisCaption = Keywords.Metrics.Names.AvgFlowThroughput;
		NumericFactorScatterTableData table = new NumericFactorScatterTableData(xAxisCaption, yAxisCaption);
		for (String seriesName : avgFlowThroughputData.keySet()) {
			table.addSeriesToTable(seriesName, avgFlowThroughputData.get(seriesName));
		}
		outputSheets.put(sheetName, table);
	}

	private void addAvgStartupDelayData() {
		String sheetName = "AvgStartupDelay";
		String xAxisCaption = mainFactorName;
		String yAxisCaption = Keywords.Metrics.Names.AvgFlowStartupDelay;
		NumericFactorScatterTableData table = new NumericFactorScatterTableData(xAxisCaption, yAxisCaption);
		for (String seriesName : avgStartupDelayData.keySet()) {
			table.addSeriesToTable(seriesName, avgStartupDelayData.get(seriesName));
		}
		outputSheets.put(sheetName, table);
	}

	private void addBottleneckUtilizationData() {
		String sheetName = "BtlUtil";
		String xAxisCaption = mainFactorName;
		String yAxisCaption = Keywords.Metrics.Names.BtlUtilization;
		NumericFactorScatterTableData table = new NumericFactorScatterTableData(xAxisCaption, yAxisCaption);
		for (String seriesName : btlUtilizationData.keySet()) {
			table.addSeriesToTable(seriesName, btlUtilizationData.get(seriesName));
		}
		outputSheets.put(sheetName, table);
	}

	private void addFlowRejectionPercentageData() {
		String sheetName = "flowRejectionRate";
		String xAxisCaption = mainFactorName;
		String yAxisCaption = Keywords.Metrics.Names.FlowRejectionRate;
		NumericFactorScatterTableData table = new NumericFactorScatterTableData(xAxisCaption, yAxisCaption);
		for (String seriesName : flowRejectionPercentageData.keySet()) {
			table.addSeriesToTable(seriesName, flowRejectionPercentageData.get(seriesName));
		}
		outputSheets.put(sheetName, table);
	}

	private void addMaxBottlebeckBufferOccupancyData() {
		String sheetName = "MaxBtlBufferOccupancy";
		String xAxisCaption = mainFactorName;
		String yAxisCaption = Keywords.Metrics.Names.MaxBtlBufferOccupancy;
		NumericFactorScatterTableData table = new NumericFactorScatterTableData(xAxisCaption, yAxisCaption);
		for (String seriesName : maxBtlBufferOccupancyData.keySet()) {
			table.addSeriesToTable(seriesName, maxBtlBufferOccupancyData.get(seriesName));
		}
		outputSheets.put(sheetName, table);
	}

	private void addVarianceOfBottleneckUtilizationSharePerFlowSizeData() {
		String sheetName = "VarianceOfBtlUtilSharePerFlowSize";
		String xAxisCaption = mainFactorName;
		String yAxisCaption = Keywords.Metrics.Names.VarBtlUtilizationShareOverFlowSize;
		NumericFactorScatterTableData table = new NumericFactorScatterTableData(xAxisCaption, yAxisCaption);
		for (String seriesName : varianceOfBtlUtilizationSharePerFlowSizeData.keySet()) {
			table.addSeriesToTable(seriesName, varianceOfBtlUtilizationSharePerFlowSizeData.get(seriesName));
		}
		outputSheets.put(sheetName, table);
	}

	private void addVarianceOfFlowCompletionTimePerFlowSizeData() {
		String sheetName = "varianceOfFlowCompletionTime";
		String xAxisCaption = mainFactorName;
		String yAxisCaption = Keywords.Metrics.Names.VarFlowCompletionTimeOverFlowSize;
		NumericFactorScatterTableData table = new NumericFactorScatterTableData(xAxisCaption, yAxisCaption);
		for (String seriesName : varianceOfFlowCompletionTimePerFlowSizeData.keySet()) {
			table.addSeriesToTable(seriesName, varianceOfFlowCompletionTimePerFlowSizeData.get(seriesName));
		}
		outputSheets.put(sheetName, table);
	}

	private void initializedSeriesForAllMetrics(String seriesName) {
		// All metric data structures must be mentioned here
		avgCompletionTimeData.put(seriesName, new TreeMap<Float, Float>());
		avgStartupDelayData.put(seriesName, new TreeMap<Float, Float>());
		avgFlowThroughputData.put(seriesName, new TreeMap<Float, Float>());
		btlUtilizationData.put(seriesName, new TreeMap<Float, Float>());
		varianceOfBtlUtilizationSharePerFlowSizeData.put(seriesName, new TreeMap<Float, Float>());
		varianceOfFlowCompletionTimePerFlowSizeData.put(seriesName, new TreeMap<Float, Float>());
		maxBtlBufferOccupancyData.put(seriesName, new TreeMap<Float, Float>());
		flowRejectionPercentageData.put(seriesName, new TreeMap<Float, Float>());
	}

	private void insertValuesForAllMetrics(String seriesName, Float metricValue, Statistics stat) {
		// All metric data structures must be mentioned here
		avgCompletionTimeData.get(seriesName).put(metricValue, stat.getAvgFlowCompletionTime());
		avgStartupDelayData.get(seriesName).put(metricValue, stat.getAvgStartupDelay());
		avgFlowThroughputData.get(seriesName).put(metricValue, stat.getAvgFlowThroughput());
		btlUtilizationData.get(seriesName).put(metricValue, stat.getBottleneckUtilization());
		varianceOfBtlUtilizationSharePerFlowSizeData.get(seriesName).put(metricValue,
				stat.getVarianceOfBottleneckUtilizationSharePerFlowSize());
		varianceOfFlowCompletionTimePerFlowSizeData.get(seriesName).put(metricValue,
				stat.getVarianceOfFlowCompletionTimePerFlowSize());
		maxBtlBufferOccupancyData.get(seriesName).put(metricValue, stat.getMaxBottleneckBufferOccupancy());
		flowRejectionPercentageData.get(seriesName).put(metricValue, stat.getFlowRejectionPercentage());

	}

	public void prepareOutputSheets() {
		// All metric data structures must be mentioned here
		addAvgCompletionTimeData();
		addAvgStartupDelayData();
		addAvgFlowThroughputData();
		addBottleneckUtilizationData();
		addVarianceOfBottleneckUtilizationSharePerFlowSizeData();
		addVarianceOfFlowCompletionTimePerFlowSizeData();
		addMaxBottlebeckBufferOccupancyData();
		addFlowRejectionPercentageData();

	}

}
