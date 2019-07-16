package system.utility.dataStructures;

import java.util.HashMap;
import java.util.TreeMap;
import system.utility.Statistics;

public class NumberOFFlowsOutputData {
	private TreeMap<Float, Float> avgCompletionTimeData;
	private TreeMap<Float, Float> avgStartupDelayData;
	private TreeMap<Float, Float> varianceOfBottleneckUtilizationData;
	private TreeMap<Float, Float> maxBottleneckBufferOccupancy;
	private TreeMap<Float, Float> bottleneckUtilizationData;

	public HashMap<String, ScatterTableData> outputSheets;

	public NumberOFFlowsOutputData() {
		avgCompletionTimeData = new TreeMap<Float, Float>();
		avgStartupDelayData = new TreeMap<Float, Float>();
		varianceOfBottleneckUtilizationData = new TreeMap<Float, Float>();
		outputSheets = new HashMap<String, ScatterTableData>();
		maxBottleneckBufferOccupancy = new TreeMap<Float, Float>();
		bottleneckUtilizationData = new TreeMap<Float, Float>();

	}

	public void prepareOutputMetrics(int numberOfFlows, Statistics stat) {
		Float key = (float) numberOfFlows;
		avgCompletionTimeData.put(key, stat.getAvgFlowCompletionTime());
		avgStartupDelayData.put(key, stat.getAvgStartupDelay());
		varianceOfBottleneckUtilizationData.put(key, stat.getVarianceOfBottleneckUtilizationShare());
		maxBottleneckBufferOccupancy.put(key, stat.getMaxBottleneckBufferOccupancy());

	}

	public void prepareOutputSheets() {
		addAvgCompletionTimeData();
		addAvgStartupDelayData();
		addMaxBottleneckBufferOccupancyData();
		addVarianceOfBottleneckUtilizationData();
		addBottleneckUtilizationData();

	}

	private void addAvgCompletionTimeData() {
		String outputName = "AvgCompletionTime";
		ScatterTableData table = new ScatterTableData("Number of Flows", "Avg Flow Completion Time (ms)");
		table.addSeriesToTable(outputName, avgCompletionTimeData);
		outputSheets.put(outputName, table);
	}

	private void addBottleneckUtilizationData() {
		String outputName = "BottleneckUtilizationPercentage";
		ScatterTableData table = new ScatterTableData("Number of Flows", "Bottleneck Utilization Share (%)");
		table.addSeriesToTable(outputName, bottleneckUtilizationData);
		outputSheets.put(outputName, table);
	}

	private void addAvgStartupDelayData() {
		String outputName = "AvgStartupDelay";
		ScatterTableData table = new ScatterTableData("Number of Flows", "Avg Startup Delay (ms)");
		table.addSeriesToTable(outputName, avgStartupDelayData);
		outputSheets.put(outputName, table);
	}

	private void addMaxBottleneckBufferOccupancyData() {
		String outputName = "highestBottleneckBufferOccupancy";
		ScatterTableData table = new ScatterTableData("Number of Flows",
				"Maximum Bottleneck Buffer Occupancy (Packets)");
		table.addSeriesToTable(outputName, maxBottleneckBufferOccupancy);
		outputSheets.put(outputName, table);
	}

	private void addVarianceOfBottleneckUtilizationData() {
		String outputName = "varianceOfBottleneckUtilization";
		ScatterTableData table = new ScatterTableData("Number of Flows", "Variance of Bottleneck Utilization Share");
		table.addSeriesToTable(outputName, varianceOfBottleneckUtilizationData);
		outputSheets.put(outputName, table);
	}

}
