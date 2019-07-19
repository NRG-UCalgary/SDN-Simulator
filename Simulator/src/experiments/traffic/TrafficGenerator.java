package experiments.traffic;

import java.util.TreeMap;

import system.utility.*;

public class TrafficGenerator {

	private RandomVariableGenerator interArrivalTimeRVG;
	private RandomVariableGenerator flowSizeRVG;
	private RandomVariableGenerator numberOfFLowsRVG;

	public int numberOfFlowsDistribution;
	public int minNumberOfFlows;
	public int maxNumberOfFlows;
	public double meanNumberOfFlows;
	public double standardDeviationNumberOfFLows;
	public int totalNumberOfFlows;

	public int flowSizeDistribution;
	public int minFlowSize;
	public int maxFlowSize;
	public double meanFlowSize;
	public double standardDeviationFlowSize;

	public int flowInterArrivalTimeDistribution;
	public double minFlowInterArrivalTime;
	public double maxFlowInterArrivalTime;
	public double meanFlowInterArrivalTime;
	public double standardDeviationFlowInterArrivalTime;
	public float firstFlowArrivalTime;

	public TrafficGenerator(int networkType, int numberOfFlows) {
		interArrivalTimeRVG = new RandomVariableGenerator(
				Keywords.Inputs.RandomVariableGenerator.StartingSeeds.InterArrivalTimeStartingSeed);
		flowSizeRVG = new RandomVariableGenerator(
				Keywords.Inputs.RandomVariableGenerator.StartingSeeds.FlowSizeStartingSeed);
		numberOfFLowsRVG = new RandomVariableGenerator(
				Keywords.Inputs.RandomVariableGenerator.StartingSeeds.NumberOfFLowsStartingSeed);
		switch (networkType) {
		case Keywords.Inputs.Traffics.Types.GeneralTraffic:
			setFlowInterArrivalProperties(Keywords.Inputs.RandomVariableGenerator.Distributions.Exponential,
					Mathematics.milliToMicro(100), Mathematics.milliToMicro(1000), Mathematics.milliToMicro(100),
					Mathematics.milliToMicro(10));
			setFlowSizeProperties(Keywords.Inputs.RandomVariableGenerator.Distributions.Uniform,
					(int) Mathematics.kiloToBase(30), (int) Mathematics.kiloToBase(100), Mathematics.kiloToBase(50),
					Mathematics.kiloToBase(30));
			firstFlowArrivalTime = 0;
			break;
		case Keywords.Inputs.Traffics.Types.DatacenterTraffic:
			break;
		case Keywords.Inputs.Traffics.Types.MultilediaTraffic:
			break;
		default:
			break;
		}
		totalNumberOfFlows = numberOfFlows;
	}

	public Traffic generate() {
		Traffic traffic = new Traffic();
		traffic.ArrivalTimePerFlowID = prepareFlowArrivals(flowInterArrivalTimeDistribution);
		traffic.FlowSizePerFlowID = prepareFlowSizes(flowSizeDistribution);
		return traffic;
	}

	public void setNumberOfFlowsProperties(int distribution, int min, int max, double mean, double standardDeviation) {
		numberOfFlowsDistribution = distribution;
		minNumberOfFlows = min;
		maxNumberOfFlows = max;
		meanNumberOfFlows = mean;
		standardDeviationNumberOfFLows = standardDeviation;

	}

	public void setFlowInterArrivalProperties(int distribution, double min, double max, double mean,
			double standardDeviation) {
		flowInterArrivalTimeDistribution = distribution;
		minFlowInterArrivalTime = min;
		maxFlowInterArrivalTime = max;
		meanFlowInterArrivalTime = mean;
		standardDeviationFlowInterArrivalTime = standardDeviation;

	}

	public void setFlowSizeProperties(int distribution, int min, int max, double mean, double standardDeviation) {
		flowSizeDistribution = distribution;
		minFlowSize = min;
		maxFlowSize = max;
		meanFlowSize = mean;
		standardDeviationFlowSize = standardDeviation;
	}

	public void prepareNumberOfFlows() {
		switch (numberOfFlowsDistribution) {
		case Keywords.Inputs.RandomVariableGenerator.Distributions.Uniform:
			totalNumberOfFlows = numberOfFLowsRVG.getNextUniformInteger(minNumberOfFlows, maxNumberOfFlows);
		case Keywords.Inputs.RandomVariableGenerator.Distributions.Exponential:
			totalNumberOfFlows = (int) numberOfFLowsRVG.getNextExponential(meanNumberOfFlows);
		case Keywords.Inputs.RandomVariableGenerator.Distributions.Guassian:
			totalNumberOfFlows = (int) numberOfFLowsRVG.getNextGuassianInteger((int) meanNumberOfFlows,
					(int) standardDeviationNumberOfFLows);

		default:
			totalNumberOfFlows = -1;
		}
	}

	private TreeMap<Integer, Float> prepareFlowArrivals(int distribution) {
		TreeMap<Integer, Float> arrivalTimePerFlowID = new TreeMap<Integer, Float>();
		interArrivalTimeRVG.resetRng();
		arrivalTimePerFlowID.put(0, firstFlowArrivalTime);
		float interArrivalTime = 0;
		float previousArrival = firstFlowArrivalTime;
		for (int flowIndex = 1; flowIndex < totalNumberOfFlows; flowIndex++) {
			switch (distribution) {
			case Keywords.Inputs.RandomVariableGenerator.Distributions.Exponential:
				interArrivalTime = (float) interArrivalTimeRVG.getNextExponential(meanFlowInterArrivalTime);
				break;
			case Keywords.Inputs.RandomVariableGenerator.Distributions.Uniform:
				interArrivalTime = (float) interArrivalTimeRVG.getNextUniform(minFlowInterArrivalTime,
						maxFlowInterArrivalTime);
				break;
			default:
				break;
			}
			arrivalTimePerFlowID.put(flowIndex, interArrivalTime + previousArrival);
			previousArrival += interArrivalTime;
		}
		return arrivalTimePerFlowID;
	}

	private TreeMap<Integer, Integer> prepareFlowSizes(int distribution) {
		TreeMap<Integer, Integer> flowSizePerFlowID = new TreeMap<Integer, Integer>();
		flowSizeRVG.resetRng();
		for (int flowIndex = 0; flowIndex < totalNumberOfFlows; flowIndex++) {
			int flowSize = 0;
			switch (distribution) {
			case Keywords.Inputs.RandomVariableGenerator.Distributions.Exponential:
				flowSize = (int) flowSizeRVG.getNextExponential(meanFlowSize);
				break;
			case Keywords.Inputs.RandomVariableGenerator.Distributions.Uniform:
				flowSize = flowSizeRVG.getNextUniformInteger(minFlowSize, maxFlowSize);
				break;
			case Keywords.Inputs.RandomVariableGenerator.Distributions.Guassian:
				flowSize = flowSizeRVG.getNextGuassianInteger((int) meanFlowSize, (int) standardDeviationFlowSize);
				break;
			default:
				break;
			}
			flowSizePerFlowID.put(flowIndex, flowSize);
		}
		return flowSizePerFlowID;
	}

}
