package experiments.scenarios.onefactor;

import java.util.TreeMap;

import experiments.testbeds.DataCenter;
import experiments.testbeds.Dumbbell;
import experiments.testbeds.ParkingLot;
import experiments.testbeds.Testbed;
import experiments.traffic.TrafficGenerator;
import system.Main;
import system.utility.Keywords;
import system.utility.OutputHandler;
import system.utility.Statistics;

public class FlowSizeStudy extends OneFactorScenario {

	public FlowSizeStudy() {
		// TODO Auto-generated constructor stub
	}

	public void executeTest(int flowSizeDistribution, double averageFlowSize, double minFlowSize, double maxFlowSize,
			double hops, int networkType, int networkTopology, int trafficType) {
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

		OutputHandler.outStatsforNumberOfFlows(StudyStats, StudyStats);
	}

}
