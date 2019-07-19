package experiments.scenarios;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.TreeMap;

import experiments.testbeds.*;
import experiments.traffic.*;
import system.Main;
import system.utility.*;

public class NumberOfFlowsPerFlowSizeDistributionStudy extends Scenario {

	public NumberOfFlowsPerFlowSizeDistributionStudy() {
		super(Keywords.NumberOfFlowsStudy, Keywords.NumberOfFlowsFactor);
	}

	public void executeTest(ArrayList<Integer> numberOfFlowsValues, ArrayList<Integer> flowSizeDistributionValues,
			int networkType, int networkTopology, int trafficType) {
		LinkedHashMap<String, TreeMap<Float, Statistics>> result = new LinkedHashMap<String, TreeMap<Float, Statistics>>();
		for (int flowSizeDistribution : flowSizeDistributionValues) {
			TreeMap<Float, Statistics> StudyStats = new TreeMap<Float, Statistics>();
			Testbed testbed;
			TrafficGenerator trafficGen = new TrafficGenerator(trafficType);
			trafficGen.FlowSizeDistribution = flowSizeDistribution;
			switch (networkTopology) {
			case Keywords.Dumbbell:
				testbed = new Dumbbell(networkType);
				break;
			case Keywords.DataCenter:
				testbed = new DataCenter(networkType);
				break;
			case Keywords.ParkingLot:
				testbed = new ParkingLot(networkType);
				break;
			default:
				Main.error(this.getClass().getName(), "executeTest", "Invalid networkTopology");
				testbed = null;
				break;
			}
			// An outer loop can be created for replication
			for (int numberOfFlows : numberOfFlowsValues) {
				Main.singleFactoractorMessage("Number Of Flows", Integer.toString(numberOfFlows));
				trafficGen.TotalNumberOfFlows = (int) numberOfFlows;
				StudyStats.put((float) numberOfFlows, testbed.executeSimulation(trafficGen.generate()));
				Main.simulationDoneMessage();
			}
			String distributionName;
			switch (flowSizeDistribution) {
			case Keywords.Uniform:
				distributionName = "Uniform";
				break;
			case Keywords.Exponential:
				distributionName = "Exponential";
				break;
			case Keywords.Guassian:
				distributionName = "Guassian";
				break;
			default:
				distributionName = "Error";
				break;
			}
			result.put(distributionName+" Flow Sizes", StudyStats);
		}
		generateNumericalFactorOutput(result);
	}

}
