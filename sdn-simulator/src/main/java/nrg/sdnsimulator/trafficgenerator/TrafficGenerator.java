package nrg.sdnsimulator.trafficgenerator;

import nrg.sdnsimulator.core.utility.Keywords;
import nrg.sdnsimulator.core.utility.RandomVariableGenerator;

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
		flowSizeRVG = new RandomVariableGenerator(
				Keywords.RandomVariableGenerator.StartingSeeds.FlowSizeStartingSeed);
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
			interArrivalTime = (float) interArrivalTimeRVG.getNextValue(
					interArrivalTimeDistribution, interArrivalTimeMean, interArrivalTimeSTD);
			while (interArrivalTime < 0) {
				interArrivalTime = (float) interArrivalTimeRVG.getNextValue(
						interArrivalTimeDistribution, interArrivalTimeMean, interArrivalTimeSTD);
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
			flowSize = (int) flowSizeRVG.getNextValue(flowSizeDistribution, flowSizeMean,
					flowSizeSTD);
			while (flowSize <= 0 || flowSize > 1100000) {
				flowSize = (int) flowSizeRVG.getNextValue(flowSizeDistribution, flowSizeMean,
						flowSizeSTD);
			}
			traffic.getFlowSizePerFlowID().put(flowIndex, flowSize);
		}
	}

	private void prepareNumberOfFlows() {
		numberOfFlows = (int) numberOfFLowsRVG.getNextValue(numberOfFlowsDistribution,
				numberOfFlowsMean, numberOfFlowsSTD);
		while (numberOfFlows <= 0) {
			numberOfFlows = (int) numberOfFLowsRVG.getNextValue(numberOfFlowsDistribution,
					numberOfFlowsMean, numberOfFlowsSTD);

		}
	}

	public void setFlowInterArrivalTimeProperties(short distribution, double mean,
			double standardDeviation) {
		interArrivalTimeDistribution = distribution;
		interArrivalTimeMean = mean;
		interArrivalTimeSTD = standardDeviation;
	}

	public void setFlowSizeProperties(short distribution, double mean, double standardDeviation) {
		flowSizeDistribution = distribution;
		flowSizeMean = mean;
		flowSizeSTD = standardDeviation;

	}

	public void setNumberOfFlowsProperties(short distribution, double mean,
			double standardDeviation) {
		numberOfFlowsDistribution = distribution;
		numberOfFlowsMean = mean;
		numberOfFlowsSTD = standardDeviation;

	}

	public float getFirstFlowArrivalTime() {
		return firstFlowArrivalTime;
	}

	public void setFirstFlowArrivalTime(float firstFlowArrivalTime) {
		this.firstFlowArrivalTime = firstFlowArrivalTime;
	}

	public short getFlowSizeDistribution() {
		return flowSizeDistribution;
	}

	public void setFlowSizeDistribution(short flowSizeDistribution) {
		this.flowSizeDistribution = flowSizeDistribution;
	}

	public double getFlowSizeMean() {
		return flowSizeMean;
	}

	public void setFlowSizeMean(double flowSizeMean) {
		this.flowSizeMean = flowSizeMean;
	}

	public RandomVariableGenerator getFlowSizeRVG() {
		return flowSizeRVG;
	}

	public void setFlowSizeRVG(RandomVariableGenerator flowSizeRVG) {
		this.flowSizeRVG = flowSizeRVG;
	}

	public double getFlowSizeSTD() {
		return flowSizeSTD;
	}

	public void setFlowSizeSTD(double flowSizeSTD) {
		this.flowSizeSTD = flowSizeSTD;
	}

	public short getInterArrivalTimeDistribution() {
		return interArrivalTimeDistribution;
	}

	public void setInterArrivalTimeDistribution(short interArrivalTimeDistribution) {
		this.interArrivalTimeDistribution = interArrivalTimeDistribution;
	}

	public double getInterArrivalTimeMean() {
		return interArrivalTimeMean;
	}

	public void setInterArrivalTimeMean(double interArrivalTimeMean) {
		this.interArrivalTimeMean = interArrivalTimeMean;
	}

	public RandomVariableGenerator getInterArrivalTimeRVG() {
		return interArrivalTimeRVG;
	}

	public void setInterArrivalTimeRVG(RandomVariableGenerator interArrivalTimeRVG) {
		this.interArrivalTimeRVG = interArrivalTimeRVG;
	}

	public double getInterArrivalTimeSTD() {
		return interArrivalTimeSTD;
	}

	public void setInterArrivalTimeSTD(double interArrivalTimeSTD) {
		this.interArrivalTimeSTD = interArrivalTimeSTD;
	}

	public int getNumberOfFlows() {
		return numberOfFlows;
	}

	public void setNumberOfFlows(int numberOfFlows) {
		this.numberOfFlows = numberOfFlows;
	}

	public short getNumberOfFlowsDistribution() {
		return numberOfFlowsDistribution;
	}

	public void setNumberOfFlowsDistribution(short numberOfFlowsDistribution) {
		this.numberOfFlowsDistribution = numberOfFlowsDistribution;
	}

	public double getNumberOfFlowsMean() {
		return numberOfFlowsMean;
	}

	public void setNumberOfFlowsMean(double numberOfFlowsMean) {
		this.numberOfFlowsMean = numberOfFlowsMean;
	}

	public RandomVariableGenerator getNumberOfFLowsRVG() {
		return numberOfFLowsRVG;
	}

	public void setNumberOfFLowsRVG(RandomVariableGenerator numberOfFLowsRVG) {
		this.numberOfFLowsRVG = numberOfFLowsRVG;
	}

	public double getNumberOfFlowsSTD() {
		return numberOfFlowsSTD;
	}

	public void setNumberOfFlowsSTD(double numberOfFlowsSTD) {
		this.numberOfFlowsSTD = numberOfFlowsSTD;
	}

	public Traffic getTraffic() {
		return traffic;
	}

	public void setTraffic(Traffic traffic) {
		this.traffic = traffic;
	}

}
