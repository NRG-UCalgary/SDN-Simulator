package nrg.sdnsimulator.trafficgenerator;

import lombok.Getter;
import lombok.Setter;
import nrg.sdnsimulator.core.utility.Keywords;
import nrg.sdnsimulator.core.utility.RandomVariableGenerator;

@Getter
@Setter
public class TrafficGenerator {

	private float firstFlowArrivalTime;
	private short flowSizeDistribution;
	private double flowSizeMean;
	private RandomVariableGenerator flowSizeRVG;
	private double flowSizeSTD;
	private short interArrivalTimeDistribution;
	private double interArrivalTimeMean;
	private RandomVariableGenerator interArrivalTimeRVG;
	private double interArrivalTimeSTD;
	private int numberOfFlows;
	private short numberOfFlowsDistribution;
	private double numberOfFlowsMean;
	private RandomVariableGenerator numberOfFLowsRVG;
	private double numberOfFlowsSTD;
	private Traffic traffic;

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

	public Traffic generateDCNElephantMouseTraffic() {
		// Create an Elephant flow
		return traffic;
	}

	private void prepareFlowArrivals() {
		interArrivalTimeRVG.resetRng();
		traffic.getArrivalTimePerFlowID().clear();
		traffic.getArrivalTimePerFlowID().put(0, firstFlowArrivalTime);
		float previousArrival = firstFlowArrivalTime;
		float interArrivalTime = 0;
		for (int flowIndex = 1; flowIndex < numberOfFlows; flowIndex++) {
			interArrivalTime = (float) interArrivalTimeRVG.getNextValue(interArrivalTimeDistribution,
					interArrivalTimeMean, interArrivalTimeSTD);
			while (interArrivalTime < 0) {
				interArrivalTime = (float) interArrivalTimeRVG.getNextValue(interArrivalTimeDistribution,
						interArrivalTimeMean, interArrivalTimeSTD);
			}
			traffic.getArrivalTimePerFlowID().put(flowIndex, interArrivalTime + previousArrival);
			previousArrival += interArrivalTime;
		}
	}

	private void prepareFlowSizes() {
		flowSizeRVG.resetRng();
		traffic.getFlowSizePerFlowID().clear();
		int flowSize = 0;
		for (int flowIndex = 0; flowIndex < numberOfFlows; flowIndex++) {
			flowSize = (int) flowSizeRVG.getNextValue(flowSizeDistribution, flowSizeMean, flowSizeSTD);
			while (flowSize <= 0 || flowSize > 1100000) {
				flowSize = (int) flowSizeRVG.getNextValue(flowSizeDistribution, flowSizeMean, flowSizeSTD);
			}
			traffic.getFlowSizePerFlowID().put(flowIndex, flowSize);
		}
	}

	private void prepareNumberOfFlows() {
		numberOfFlows = (int) numberOfFLowsRVG.getNextValue(numberOfFlowsDistribution, numberOfFlowsMean,
				numberOfFlowsSTD);
		while (numberOfFlows <= 0) {
			numberOfFlows = (int) numberOfFLowsRVG.getNextValue(numberOfFlowsDistribution, numberOfFlowsMean,
					numberOfFlowsSTD);

		}
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

	public void setNumberOfFlowsProperties(short distribution, double mean, double standardDeviation) {
		numberOfFlowsDistribution = distribution;
		numberOfFlowsMean = mean;
		numberOfFlowsSTD = standardDeviation;

	}

}
