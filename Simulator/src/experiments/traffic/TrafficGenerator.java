package experiments.traffic;

import system.utility.*;

public class TrafficGenerator {

	private RandomVariableGenerator interArrivalTimeRVG;
	private RandomVariableGenerator flowSizeRVG;
	private RandomVariableGenerator numberOfFLowsRVG;

	private float firstFlowArrivalTime;
	private int numberOfFlows;
	private Traffic traffic;

	private short numberOfFlowsDistribution;
	private double numberOfFlowsMean;
	private double numberOfFlowsSTD;

	private short interArrivalTimeDistribution;
	private double interArrivalTimeMean;
	private double interArrivalTimeSTD;

	private short flowSizeDistribution;
	private double flowSizeMean;
	private double flowSizeSTD;

	public TrafficGenerator(short trafficType, float firstFlowArrivalTime) {
		interArrivalTimeRVG = new RandomVariableGenerator(
				Keywords.RandomVariableGenerator.StartingSeeds.InterArrivalTimeStartingSeed);
		flowSizeRVG = new RandomVariableGenerator(Keywords.RandomVariableGenerator.StartingSeeds.FlowSizeStartingSeed);
		numberOfFLowsRVG = new RandomVariableGenerator(
				Keywords.RandomVariableGenerator.StartingSeeds.NumberOfFLowsStartingSeed);
		this.firstFlowArrivalTime = firstFlowArrivalTime;
		numberOfFlows = 0;
		traffic = new Traffic();

		numberOfFlowsDistribution = Keywords.DefaultTestValues.NumberOfFlows.Distribution;
		numberOfFlowsMean = Keywords.DefaultTestValues.NumberOfFlows.Mean;
		numberOfFlowsSTD = Keywords.DefaultTestValues.NumberOfFlows.STD;

		interArrivalTimeDistribution = Keywords.DefaultTestValues.FlowInterArrivalTime.Distribution;
		interArrivalTimeMean = Keywords.DefaultTestValues.FlowInterArrivalTime.Mean;
		interArrivalTimeSTD = Keywords.DefaultTestValues.FlowInterArrivalTime.STD;

		flowSizeDistribution = Keywords.DefaultTestValues.FlowSize.Distribution;
		flowSizeMean = Keywords.DefaultTestValues.FlowSize.Mean;
		flowSizeSTD = Keywords.DefaultTestValues.FlowSize.STD;

	}

	public Traffic generateTraffic() {
		prepareNumberOfFlows();
		prepareFlowArrivals();
		prepareFlowSizes();
		return traffic;
	}

	public void setNumberOfFlowsProperties(short distribution, double mean, double standardDeviation) {
		numberOfFlowsDistribution = distribution;
		numberOfFlowsMean = mean;
		numberOfFlowsSTD = standardDeviation;

	}

	public void setFlowInterArrivalTimeProperties(short distribution, double mean, double standardDeviation) {
		interArrivalTimeDistribution = distribution;
		interArrivalTimeMean = mean;
		interArrivalTimeSTD = standardDeviation;
	}

	public void setFlowSizeProperties(short distribution, double mean, double standardDeviation) {
		flowSizeDistribution = distribution;
		flowSizeMean = mean;
		flowSizeSTD = standardDeviation;

	}

	private void prepareNumberOfFlows() {
		numberOfFlows = (int) numberOfFLowsRVG.getNextValue(numberOfFlowsDistribution, numberOfFlowsMean,
				numberOfFlowsSTD);
		while (numberOfFlows <= 0) {
			numberOfFlows = (int) numberOfFLowsRVG.getNextValue(numberOfFlowsDistribution, numberOfFlowsMean,
					numberOfFlowsSTD);

		}
	}

	private void prepareFlowArrivals() {
		interArrivalTimeRVG.resetRng();
		traffic.arrivalTimePerFlowID.put(0, firstFlowArrivalTime);
		float previousArrival = firstFlowArrivalTime;
		float interArrivalTime = 0;
		for (int flowIndex = 1; flowIndex < numberOfFlows; flowIndex++) {
			interArrivalTime = (float) interArrivalTimeRVG.getNextValue(interArrivalTimeDistribution,
					interArrivalTimeMean, interArrivalTimeSTD);
			while (interArrivalTime <= 0) {
				interArrivalTime = (float) interArrivalTimeRVG.getNextValue(interArrivalTimeDistribution,
						interArrivalTimeMean, interArrivalTimeSTD);
			}
			traffic.arrivalTimePerFlowID.put(flowIndex, interArrivalTime + previousArrival);
			previousArrival += interArrivalTime;
		}
	}

	private void prepareFlowSizes() {
		flowSizeRVG.resetRng();
		int flowSize = 0;
		for (int flowIndex = 0; flowIndex < numberOfFlows; flowIndex++) {
			flowSize = (int) flowSizeRVG.getNextValue(flowSizeDistribution, flowSizeMean, flowSizeSTD);
			while (flowSize <= 0) {
				flowSize = (int) flowSizeRVG.getNextValue(flowSizeDistribution, flowSizeMean, flowSizeSTD);
			}
			traffic.flowSizePerFlowID.put(flowIndex, flowSize);
		}
	}

}
