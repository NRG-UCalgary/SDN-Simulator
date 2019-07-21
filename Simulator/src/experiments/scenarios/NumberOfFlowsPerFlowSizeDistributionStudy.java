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
		super(Keywords.Scenarios.Names.NumberOfFlowsStudy, Keywords.Charts.MainFactors.Titles.NumberOfFlowsFactor);
	}

	public void executeTest(ArrayList<Integer> numberOfFlowsValues, ArrayList<Short> flowSizeDistributionValues,
			short networkType, short networkTopology, short trafficType) {
		LinkedHashMap<String, TreeMap<Float, Statistics>> result = new LinkedHashMap<String, TreeMap<Float, Statistics>>();
		for (short flowSizeDistribution : flowSizeDistributionValues) {
			TreeMap<Float, Statistics> StudyStats = new TreeMap<Float, Statistics>();
			TrafficGenerator trafficGen = new TrafficGenerator(trafficType,
					Keywords.DefaultTestValues.FirstFlowArrival);
			trafficGen.setFlowSizeProperties(flowSizeDistribution, Keywords.DefaultTestValues.FlowSize.Mean,
					Keywords.DefaultTestValues.FlowSize.STD);
			Testbed testbed;
			switch (networkTopology) {
			case Keywords.Testbeds.Topologies.Dumbbell:
				testbed = new Dumbbell(networkType);
				break;
			case Keywords.Testbeds.Topologies.DataCenter:
				testbed = new DataCenter(networkType);
				break;
			case Keywords.Testbeds.Topologies.ParkingLot:
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
				trafficGen.setNumberOfFlowsProperties(Keywords.RandomVariableGenerator.Distributions.Constant,
						numberOfFlows, 0);
				StudyStats.put((float) numberOfFlows, testbed.executeSimulation(trafficGen.generateTraffic()));
				Main.simulationDoneMessage();
			}
			String distributionName;
			switch (flowSizeDistribution) {
			case Keywords.RandomVariableGenerator.Distributions.Uniform:
				distributionName = "Uniform";
				break;
			case Keywords.RandomVariableGenerator.Distributions.Exponential:
				distributionName = "Exponential";
				break;
			case Keywords.RandomVariableGenerator.Distributions.Guassian:
				distributionName = "Guassian";
				break;
			default:
				distributionName = "Error";
				break;
			}
			result.put(distributionName + " Flow Sizes", StudyStats);
		}
		generateNumericalFactorOutput(result);
	}

}
