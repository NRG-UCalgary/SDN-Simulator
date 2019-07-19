package experiments.traffic;

import java.util.TreeMap;

import system.utility.Keywords;
import system.utility.Mathematics;
import system.utility.RandomVariableGenerator;

public class TrafficGenerator {

	private RandomVariableGenerator interArrivalTimeRVG;
	private RandomVariableGenerator flowSizeRVG;

	public int TotalNumberOfFlows;

	public int MinFlowSize;
	public int MaxFlowSize;
	public double AverageFlowSize;
	public double StandardDeviationFlowSize;

	public double MinFlowInterArrivalTime;
	public double MaxFlowInterArrivalTime;
	public double AverageFlowInterArrivalTime;
	public float FirstFlowArrivalTime;

	public int InterArrivalTimeDistribution;
	public int FlowSizeDistribution;

	public TrafficGenerator(int networkType) {
		interArrivalTimeRVG = new RandomVariableGenerator(Keywords.InterArrivalTimeStartingSeed);
		flowSizeRVG = new RandomVariableGenerator(Keywords.FlowSizeStartingSeed);
		switch (networkType) {
		case Keywords.GeneralTraffic:

			InterArrivalTimeDistribution = Keywords.Exponential;
			FlowSizeDistribution = Keywords.Uniform;

			TotalNumberOfFlows = 10;

			MinFlowSize = (int) Mathematics.kiloToBase(5);
			MaxFlowSize = (int) Mathematics.kiloToBase(30);
			AverageFlowSize = Mathematics.kiloToBase(10);
			StandardDeviationFlowSize = Mathematics.kiloToBase(5);

			MinFlowInterArrivalTime = Mathematics.milliToMicro(100);
			MaxFlowInterArrivalTime = Mathematics.milliToMicro(1000);
			AverageFlowInterArrivalTime = Mathematics.milliToMicro(100);

			FirstFlowArrivalTime = 0;
			break;
		case Keywords.DatacenterTraffic:
			break;
		case Keywords.MultilediaTraffic:
			break;
		default:
			break;
		}
	}

	public Traffic generate() {
		Traffic traffic = new Traffic();
		traffic.ArrivalTimePerFlowID = prepareFlowArrivals(InterArrivalTimeDistribution);
		traffic.FlowSizePerFlowID = prepareFlowSizes(FlowSizeDistribution);
		return traffic;
	}

	private TreeMap<Integer, Float> prepareFlowArrivals(int distribution) {
		TreeMap<Integer, Float> arrivalTimePerFlowID = new TreeMap<Integer, Float>();
		interArrivalTimeRVG.resetRng();
		arrivalTimePerFlowID.put(0, FirstFlowArrivalTime);
		float interArrivalTime = 0;
		float previousArrival = FirstFlowArrivalTime;
		for (int flowIndex = 1; flowIndex < TotalNumberOfFlows; flowIndex++) {
			switch (distribution) {
			case Keywords.Exponential:
				interArrivalTime = (float) interArrivalTimeRVG.getNextExponential(AverageFlowInterArrivalTime);
				break;
			case Keywords.Uniform:
				interArrivalTime = (float) interArrivalTimeRVG.getNextUniform(MinFlowInterArrivalTime,
						MaxFlowInterArrivalTime);
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
		for (int flowIndex = 0; flowIndex < TotalNumberOfFlows; flowIndex++) {
			int flowSize = 0;
			switch (distribution) {
			case Keywords.Exponential:
				flowSize = (int) flowSizeRVG.getNextExponential(AverageFlowSize);
				break;
			case Keywords.Uniform:
				flowSize = flowSizeRVG.getNextUniformInteger(MinFlowSize, MaxFlowSize);
				break;
			case Keywords.Guassian:
				flowSize = flowSizeRVG.getNextGuassianInteger((int) AverageFlowSize, (int) StandardDeviationFlowSize);
				break;
			default:
				break;
			}
			flowSizePerFlowID.put(flowIndex, flowSize);
		}
		return flowSizePerFlowID;
	}

}
