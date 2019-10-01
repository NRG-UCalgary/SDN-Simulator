package experiments;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.apache.commons.math3.util.Pair;

import simulator.Simulator;
import simulator.entities.traffic.Flow;
import utility.Debugger;
import utility.Keywords;
import utility.Mathematics;
import utility.Statistics;
import utility.excel.ExcelHandler;
import utility.excel.charts.datastructures.NumericFactorScatterTableData;

public class Validationv2 {
	float alpha = 1;
	float beta = 1;

	public void validate() {
		 generateOutput("Single Flow Validation", singleFlowTest());
		// generateOutput("Double Flow Validation", doubleFlowTest());
		//generateOutput("Triple Flow Validation", tripleFlowTest());
		Debugger.debugOutPut();
	}

	private Statistics tripleFlowTest() {
		Simulator sim = new Simulator();
		// Creating Controller
		sim.createController("Controller", Keywords.Entities.Controllers.Types.Controller_2, alpha, beta);

		// Creating Switches
		sim.createSwitch("Sw_0");
		sim.createSwitch("Sw_1");
		sim.createSwitch("Sw_2");

		// Creating Hosts
		sim.createHost("S_0");
		sim.createHost("R_0");
		sim.createHost("S_1");
		sim.createHost("R_1");
		sim.createHost("S_2");
		sim.createHost("R_2");

		// Creating Control Links
		sim.createLink("CL_0", "Sw_0", "Controller", (float) Mathematics.microToBase(1),
				(float) Mathematics.gigaToBase(8), 1000, false);
		sim.createLink("CL_1", "Sw_1", "Controller", (float) Mathematics.microToBase(1),
				(float) Mathematics.gigaToBase(8), 1000, false);
		sim.createLink("CL_2", "Sw_2", "Controller", (float) Mathematics.microToBase(1),
				(float) Mathematics.gigaToBase(8), 1000, false);

		// Creating Access Links
		sim.createLink("SL_0", "S_0", "Sw_0", (float) Mathematics.microToBase(1), (float) Mathematics.gigaToBase(8),
				1000, true);
		sim.createLink("RL_0", "R_0", "Sw_2", (float) Mathematics.microToBase(1), (float) Mathematics.gigaToBase(8),
				1000, false);
		sim.createLink("SL_1", "S_1", "Sw_0", (float) Mathematics.microToBase(1), (float) Mathematics.gigaToBase(8),
				1000, true);
		sim.createLink("RL_1", "R_1", "Sw_2", (float) Mathematics.microToBase(1), (float) Mathematics.gigaToBase(8),
				1000, false);
		sim.createLink("SL_2", "S_2", "Sw_0", (float) Mathematics.microToBase(1), (float) Mathematics.gigaToBase(8),
				1000, false);
		sim.createLink("RL_2", "R_2", "Sw_2", (float) Mathematics.microToBase(1), (float) Mathematics.gigaToBase(8),
				1000, false);

		// Creating Network Links
		sim.createLink("NL_0", "Sw_0", "Sw_1", (float) Mathematics.microToBase(1), (float) Mathematics.gigaToBase(8),
				1000, true); // This is the SharedBottleneckLink
		sim.createLink("NL_1", "Sw_1", "Sw_2", (float) Mathematics.microToBase(1), (float) Mathematics.gigaToBase(8),
				1000, false);

		// Generating the Flows
		sim.generateFlow("Flow_0", "S_0", "R_0", 50, 0, Keywords.Entities.Agents.Types.v2);
		sim.generateFlow("Flow_1", "S_1", "R_1", 20, 10, Keywords.Entities.Agents.Types.v2);
		sim.generateFlow("Flow_2", "S_2", "R_2", 10, 30, Keywords.Entities.Agents.Types.v2);

		// Running the Simulation
		return sim.run(0, Float.MAX_VALUE);

	}

	private Statistics doubleFlowTest() {
		Simulator sim = new Simulator();
		// Creating Controller
		sim.createController("Controller", Keywords.Entities.Controllers.Types.Controller_2, alpha, beta);

		// Creating Switches
		sim.createSwitch("Sw_0");
		sim.createSwitch("Sw_1");
		sim.createSwitch("Sw_2");

		// Creating Hosts
		sim.createHost("S_0");
		sim.createHost("R_0");
		sim.createHost("S_1");
		sim.createHost("R_1");

		// Creating Control Links
		sim.createLink("CL_0", "Sw_0", "Controller", (float) Mathematics.microToBase(1),
				(float) Mathematics.gigaToBase(8), 1000, false);
		sim.createLink("CL_1", "Sw_1", "Controller", (float) Mathematics.microToBase(1),
				(float) Mathematics.gigaToBase(8), 1000, false);
		sim.createLink("CL_2", "Sw_2", "Controller", (float) Mathematics.microToBase(1),
				(float) Mathematics.gigaToBase(8), 1000, false);

		// Creating Access Links
		sim.createLink("SL_0", "S_0", "Sw_0", (float) Mathematics.microToBase(1), (float) Mathematics.gigaToBase(8),
				1000, true);
		sim.createLink("RL_0", "R_0", "Sw_2", (float) Mathematics.microToBase(1), (float) Mathematics.gigaToBase(8),
				1000, false);
		sim.createLink("SL_1", "S_1", "Sw_0", (float) Mathematics.microToBase(1), (float) Mathematics.gigaToBase(8),
				1000, true);
		sim.createLink("RL_1", "R_1", "Sw_2", (float) Mathematics.microToBase(1), (float) Mathematics.gigaToBase(8),
				1000, false);

		// Creating Network Links
		sim.createLink("NL_0", "Sw_0", "Sw_1", (float) Mathematics.microToBase(1), (float) Mathematics.gigaToBase(8),
				1000, false); // This is the SharedBottleneckLink
		sim.createLink("NL_1", "Sw_1", "Sw_2", (float) Mathematics.microToBase(1), (float) Mathematics.gigaToBase(8),
				1000, false);

		// Generating the Flows
		sim.generateFlow("Flow_0", "S_0", "R_0", 100, 0, Keywords.Entities.Agents.Types.v2);
		sim.generateFlow("Flow_1", "S_1", "R_1", 40, 30, Keywords.Entities.Agents.Types.v2);

		// Running the Simulation
		return sim.run(0, Float.MAX_VALUE);

	}

	private Statistics singleFlowTest() {
		Simulator sim = new Simulator();
		// Creating Controller
		sim.createController("Controller", Keywords.Entities.Controllers.Types.Controller_2, alpha, beta);

		// Creating Switches
		sim.createSwitch("Sw_0");
		sim.createSwitch("Sw_1");
		sim.createSwitch("Sw_2");

		// Creating Hosts
		sim.createHost("S_0");
		sim.createHost("R_0");

		// Creating Control Links
		sim.createLink("CL_0", "Sw_0", "Controller", (float) Mathematics.microToBase(1),
				(float) Mathematics.gigaToBase(8), 10, false);
		sim.createLink("CL_1", "Sw_1", "Controller", (float) Mathematics.microToBase(1),
				(float) Mathematics.gigaToBase(8), 10, false);
		sim.createLink("CL_2", "Sw_2", "Controller", (float) Mathematics.microToBase(1),
				(float) Mathematics.gigaToBase(8), 10, false);

		// Creating Access Links
		sim.createLink("SL_0", "S_0", "Sw_0", (float) Mathematics.microToBase(1), (float) Mathematics.gigaToBase(8), 10,
				true);
		sim.createLink("RL_0", "R_0", "Sw_2", (float) Mathematics.microToBase(1), (float) Mathematics.gigaToBase(8), 10,
				false);

		// Creating Network Links
		sim.createLink("NL_0", "Sw_0", "Sw_1", (float) Mathematics.microToBase(1), (float) Mathematics.gigaToBase(8),
				10, false);
		sim.createLink("NL_1", "Sw_1", "Sw_2", (float) Mathematics.microToBase(1), (float) Mathematics.gigaToBase(8),
				10, false);

		// Generating the Flows
		sim.generateFlow("Flow_0", "S_0", "R_0", 50, 0, Keywords.Entities.Agents.Types.v2);

		// Running the Simulation
		return sim.run(0, Float.MAX_VALUE);

	}

	private void generateOutput(String testName, Statistics stat) {
		String studyOutputPath = "validation/" + testName + "/";
		new File(studyOutputPath).mkdirs();
		LinkedHashMap<String, NumericFactorScatterTableData> outputData = new LinkedHashMap<String, NumericFactorScatterTableData>();
		for (Flow flow : stat.flows.values()) {
			NumericFactorScatterTableData flowSeqNumData = new NumericFactorScatterTableData("Time (us)",
					"Sequence Number");
			ArrayList<Pair<Float, Float>> dataSerie = new ArrayList<Pair<Float, Float>>();
			for (float seqNum : flow.dataSeqNumSendingTimes.keySet()) {
				Pair<Float, Float> singleEntry = new Pair<Float, Float>(flow.dataSeqNumSendingTimes.get(seqNum),
						seqNum);
				dataSerie.add(singleEntry);
			}
			flowSeqNumData.data.put("Data Segments", dataSerie);

			ArrayList<Pair<Float, Float>> ackSerie = new ArrayList<Pair<Float, Float>>();
			for (float seqNum : flow.ackSeqNumArrivalTimes.keySet()) {
				Pair<Float, Float> singleEntry = new Pair<Float, Float>(flow.ackSeqNumArrivalTimes.get(seqNum), seqNum);
				ackSerie.add(singleEntry);
			}
			flowSeqNumData.data.put("ACKs", ackSerie);
			outputData.put("Flow_" + Integer.toString((int) flow.getID()), flowSeqNumData);
		}
		try {
			ExcelHandler.createValidationOutput(studyOutputPath, "SeqNumPlots", outputData);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
