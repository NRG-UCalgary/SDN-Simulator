package experiments;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.apache.commons.math3.util.Pair;

import simulator.Simulator;
import simulator.entities.Flow;
import utility.Keywords;
import utility.Mathematics;
import utility.Statistics;
import utility.excel.ExcelHandler;
import utility.excel.charts.datastructures.NumericFactorScatterTableData;

public class Validation {

	public Validation() {

	}

	private Statistics doubleFlowTest() {

		Simulator sim = new Simulator();

		// Creating Switches
		sim.createSwitch("Sw_0", (float) Mathematics.microToBase(1), (float) Mathematics.gigaToBase(8));
		sim.createSwitch("Sw_1", (float) Mathematics.microToBase(1), (float) Mathematics.gigaToBase(8));
		sim.createSwitch("Sw_2", (float) Mathematics.microToBase(1), (float) Mathematics.gigaToBase(8));
		sim.createSwitch("Sw_3", (float) Mathematics.microToBase(1), (float) Mathematics.gigaToBase(8));

		// Creating Hosts
		sim.createHost("S_0");
		sim.createHost("S_1");
		sim.createHost("R_0");
		sim.createHost("R_1");

		// Creating Access Links
		sim.createAccessLink("SL_0", "S_0", "Sw_0", (float) Mathematics.microToBase(1),
				(float) Mathematics.gigaToBase(8), 10, Keywords.Buffers.Policy.FIFO);
		sim.createAccessLink("SL_1", "S_1", "Sw_0", (float) Mathematics.microToBase(1),
				(float) Mathematics.gigaToBase(8), 10, Keywords.Buffers.Policy.FIFO);
		sim.createAccessLink("RL_0", "R_0", "Sw_3", (float) Mathematics.microToBase(1),
				(float) Mathematics.gigaToBase(8), 10, Keywords.Buffers.Policy.FIFO);
		sim.createAccessLink("RL_1", "R_1", "Sw_3", (float) Mathematics.microToBase(1),
				(float) Mathematics.gigaToBase(8), 10, Keywords.Buffers.Policy.FIFO);

		// Creating Network Links
		sim.createLink("NL_0", "Sw_0", "Sw_1", (float) Mathematics.microToBase(1), (float) Mathematics.gigaToBase(8),
				10, Keywords.Buffers.Policy.FIFO, true);
		sim.createLink("NL_1", "Sw_1", "Sw_3", (float) Mathematics.microToBase(1), (float) Mathematics.gigaToBase(8),
				10, Keywords.Buffers.Policy.FIFO, false);
		sim.createLink("NL_2", "Sw_0", "Sw_2", (float) Mathematics.microToBase(1), (float) Mathematics.gigaToBase(4),
				10, Keywords.Buffers.Policy.FIFO, false);
		sim.createLink("NL_3", "Sw_2", "Sw_3", (float) Mathematics.microToBase(1), (float) Mathematics.gigaToBase(4),
				10, Keywords.Buffers.Policy.FIFO, false);

		// Creating Controller
		sim.createController("Controller", 1, Keywords.RoutingAlgorithms.Dijkstra);

		// Generating the Flows
		sim.generateFlow("Flow_0", Keywords.Agents.Types.SDTCP, "S_0", "R_0", 10, 0);
		sim.generateFlow("Flow_1", Keywords.Agents.Types.SDTCP, "S_1", "R_1", 10, 0);

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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private Statistics singleFlowTest() {

		Simulator sim = new Simulator();

		// Creating Switches
		sim.createSwitch("Sw_0", 1, (float) Mathematics.gigaToBase(8));
		sim.createSwitch("Sw_1", 1, (float) Mathematics.gigaToBase(8));
		sim.createSwitch("Sw_2", 1, (float) Mathematics.gigaToBase(8));
		sim.createSwitch("Sw_3", 1, (float) Mathematics.gigaToBase(8));

		// Creating Hosts
		sim.createHost("S_0");
		sim.createHost("R_0");

		// Creating Access Links
		sim.createAccessLink("SL_0", "S_0", "Sw_0", 1, (float) Mathematics.gigaToBase(8), Integer.MAX_VALUE,
				Keywords.Buffers.Policy.FIFO);
		sim.createAccessLink("RL_0", "R_0", "Sw_3", 1, (float) Mathematics.gigaToBase(8), Integer.MAX_VALUE,
				Keywords.Buffers.Policy.FIFO);

		// Creating Network Links
		sim.createLink("NL_0", "Sw_0", "Sw_1", 1, (float) Mathematics.gigaToBase(8), Integer.MAX_VALUE, Keywords.Buffers.Policy.FIFO,
				true);
		sim.createLink("NL_1", "Sw_1", "Sw_3", 1, (float) Mathematics.gigaToBase(8), Integer.MAX_VALUE, Keywords.Buffers.Policy.FIFO,
				false);
		sim.createLink("NL_2", "Sw_0", "Sw_2", 1, (float) Mathematics.gigaToBase(4), Integer.MAX_VALUE, Keywords.Buffers.Policy.FIFO,
				false);
		sim.createLink("NL_3", "Sw_2", "Sw_3", 1, (float) Mathematics.gigaToBase(4), Integer.MAX_VALUE, Keywords.Buffers.Policy.FIFO,
				false);

		// Creating Controller
		sim.createController("Controller", 1, Keywords.RoutingAlgorithms.Dijkstra);

		// Generating the Flows
		sim.generateFlow("Flow_0", Keywords.Agents.Types.SDTCP, "S_0", "R_0", 10, 0);

		// Running the Simulation
		return sim.run(0, Float.MAX_VALUE);

	}

	public void validate() {
		generateOutput("Single Flow Validation", singleFlowTest());
	}

}
