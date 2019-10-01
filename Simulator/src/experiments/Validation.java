package experiments;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.apache.commons.math3.util.Pair;

import simulator.Simulator;
import simulator.entities.traffic.Flow;
import utility.Keywords;
import utility.Mathematics;
import utility.Statistics;
import utility.excel.ExcelHandler;
import utility.excel.charts.datastructures.NumericFactorScatterTableData;

public class Validation {
	public int sWnd = 5;

	public Validation() {

	}

	public void validate() {
		generateOutput("Single Flow Validation", singleFlowTest());
		// generateOutput("Single Flow Validation", doubleFlowTest());
	}

	private Statistics singleFlowTest() {
		Simulator sim = new Simulator();
		// Creating Controller
		sim.createController("Controller", Keywords.Entities.Controllers.Types.Default, 1, 1);

		// Creating Switches
		sim.createSwitch("Sw_0");
		sim.createSwitch("Sw_1");
		sim.createSwitch("Sw_2");
		sim.createSwitch("Sw_3");

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
		sim.createLink("CL_3", "Sw_3", "Controller", (float) Mathematics.microToBase(1),
				(float) Mathematics.gigaToBase(8), 10, false);

		// Creating Access Links
		sim.createLink("SL_0", "S_0", "Sw_0", (float) Mathematics.microToBase(1), (float) Mathematics.gigaToBase(8), 10,
				false);
		sim.createLink("RL_0", "R_0", "Sw_3", (float) Mathematics.microToBase(1), (float) Mathematics.gigaToBase(8), 10,
				false);

		// Creating Network Links
		sim.createLink("NL_0", "Sw_0", "Sw_1", (float) Mathematics.microToBase(1), (float) Mathematics.gigaToBase(8),
				10, true);
		sim.createLink("NL_1", "Sw_1", "Sw_3", (float) Mathematics.microToBase(1), (float) Mathematics.gigaToBase(8),
				10, false);
		sim.createLink("NL_2", "Sw_0", "Sw_2", (float) Mathematics.microToBase(1), (float) Mathematics.gigaToBase(4),
				10, false);
		sim.createLink("NL_3", "Sw_2", "Sw_3", (float) Mathematics.microToBase(1), (float) Mathematics.gigaToBase(4),
				10, false);

		// Generating the Flows
		sim.generateFlow("Flow_0", "S_0", "R_0", 10, 0, Keywords.Entities.Agents.Types.Default, sWnd);

		// Running the Simulation
		return sim.run(0, Float.MAX_VALUE);

	}

	private Statistics doubleFlowTest() {
		Simulator sim = new Simulator();
		// Creating Controller
		sim.createController("Controller");

		// Creating Switches
		sim.createSwitch("Sw_0");
		sim.createSwitch("Sw_1");
		sim.createSwitch("Sw_2");
		sim.createSwitch("Sw_3");

		// Creating Hosts
		sim.createHost("S_0");
		sim.createHost("S_1");
		sim.createHost("R_0");
		sim.createHost("R_1");

		// Creating Control Links
		sim.createLink("CL_0", "Sw_0", "Controller", (float) Mathematics.microToBase(1),
				(float) Mathematics.gigaToBase(8), 10, false);
		sim.createLink("CL_1", "Sw_1", "Controller", (float) Mathematics.microToBase(1),
				(float) Mathematics.gigaToBase(8), 10, false);
		sim.createLink("CL_2", "Sw_2", "Controller", (float) Mathematics.microToBase(1),
				(float) Mathematics.gigaToBase(8), 10, false);
		sim.createLink("CL_3", "Sw_3", "Controller", (float) Mathematics.microToBase(1),
				(float) Mathematics.gigaToBase(8), 10, false);

		// Creating Access Links
		sim.createLink("SL_0", "S_0", "Sw_0", (float) Mathematics.microToBase(1), (float) Mathematics.gigaToBase(8), 10,
				false);
		sim.createLink("SL_1", "S_1", "Sw_0", (float) Mathematics.microToBase(1), (float) Mathematics.gigaToBase(8), 10,
				false);
		sim.createLink("RL_0", "R_0", "Sw_3", (float) Mathematics.microToBase(1), (float) Mathematics.gigaToBase(8), 10,
				false);
		sim.createLink("RL_1", "R_1", "Sw_3", (float) Mathematics.microToBase(1), (float) Mathematics.gigaToBase(8), 10,
				false);

		// Creating Network Links
		sim.createLink("NL_0", "Sw_0", "Sw_1", (float) Mathematics.microToBase(1), (float) Mathematics.gigaToBase(8),
				10, true);
		sim.createLink("NL_1", "Sw_1", "Sw_3", (float) Mathematics.microToBase(1), (float) Mathematics.gigaToBase(8),
				10, false);
		sim.createLink("NL_2", "Sw_0", "Sw_2", (float) Mathematics.microToBase(1), (float) Mathematics.gigaToBase(4),
				10, false);
		sim.createLink("NL_3", "Sw_2", "Sw_3", (float) Mathematics.microToBase(1), (float) Mathematics.gigaToBase(4),
				10, false);

		// Generating the Flows
		sim.generateFlow("Flow_0", "S_0", "R_0", 10, 0, Keywords.Entities.Agents.Types.Default);
		sim.generateFlow("Flow_1", "S_1", "R_1", 10, 0, Keywords.Entities.Agents.Types.Default);

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
