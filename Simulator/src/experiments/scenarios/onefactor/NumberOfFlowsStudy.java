package experiments.scenarios.onefactor;

import java.util.TreeMap;

import experiments.testbeds.DataCenter;
import experiments.testbeds.Dumbbell;
import experiments.testbeds.ParkingLot;
import experiments.testbeds.Testbed;
import experiments.traffic.TrafficGenerator;
import system.Main;
import system.utility.*;

public class NumberOfFlowsStudy extends OneFactorScenario {

	public NumberOfFlowsStudy() {
	}

	public void executeTest(int dummyDistribution, double dummyAverage, double minNumberOfFlows,
			double maxNumberOfFlows, double hops, int networkType, int networkTopology, int trafficType) {
		TreeMap<Integer, Statistics> StudyStats = new TreeMap<Integer, Statistics>();
		Testbed testbed;
		TrafficGenerator trafficGen = new TrafficGenerator(trafficType);
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
		for (int numberOfFlows = (int) minNumberOfFlows; numberOfFlows <= (int) maxNumberOfFlows; numberOfFlows += hops) {
			Main.singleFactoractorMessage("Number Of Flows", Integer.toString(numberOfFlows));
			trafficGen.TotalNumberOfFlows = (int) numberOfFlows;
			StudyStats.put(numberOfFlows, testbed.executeSimulation(trafficGen.generate()));
			Main.simulationDoneMessage();
		}
		OutputHandler.outStatsforNumberOfFlows(StudyStats, StudyStats);
	}

}
