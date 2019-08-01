package experiments.scenarios;

import java.util.LinkedHashMap;
import java.util.TreeMap;

import experiments.testbeds.Dumbbell;
import experiments.testbeds.Testbed;
import experiments.traffic.TrafficGenerator;
import utility.Debugger;
import utility.Keywords;
import utility.Statistics;

public class FlowSizeMeanAndSTDStudy extends Scenario {

	public FlowSizeMeanAndSTDStudy() {
		super(Keywords.Scenarios.Names.FlowSizeStudy, Keywords.Charts.MainFactors.Titles.FlowSizeFactor);
	}

	public void executeTest() {
		Debugger.debug("=========================================================================");
		Debugger.debug("======== Flow Size Mean and Std =========================================");
		Debugger.debug("=========================================================================");
		LinkedHashMap<String, TreeMap<Float, Statistics>> result = new LinkedHashMap<String, TreeMap<Float, Statistics>>();
		for (double flowSizeSTD = 10; flowSizeSTD <= 1000; flowSizeSTD *= 10) {
			TreeMap<Float, Statistics> StudyStats = new TreeMap<Float, Statistics>();
			TrafficGenerator trafficGen = new TrafficGenerator(Keywords.Traffics.Types.GeneralTraffic, 0);
			Testbed testbed = new Dumbbell(Keywords.Testbeds.Types.LAN);

			for (double flowSizeMean = 1000; flowSizeMean <= 20000; flowSizeMean += 2000) {
				trafficGen.setFlowSizeProperties(Keywords.RandomVariableGenerator.Distributions.LogNormal, flowSizeMean,
						flowSizeSTD);
				StudyStats.put((float) flowSizeMean, testbed.executeSimulation(trafficGen.generateTraffic()));
			}
			result.put("STD = " + (int) flowSizeSTD, StudyStats);
		}
		generateNumericalFactorOutput(result);
	}

}
