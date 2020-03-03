package nrg.sdnsimulator.scenario;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.TreeMap;

import nrg.sdnsimulator.core.utility.Statistics;

public abstract class NumericalFactorScenario extends Scenario {

	protected LinkedHashMap<String, TreeMap<Float, Statistics>> result;
	protected TreeMap<Float, Statistics> studyStats;
	protected ArrayList<Float> firstFactorValues;
	protected ArrayList<Float> secondFactorValues;

	public NumericalFactorScenario(String studyName, String mainFactorName) {
		super(studyName, mainFactorName);
		result = new LinkedHashMap<String, TreeMap<Float, Statistics>>();
		studyStats = new TreeMap<Float, Statistics>();
		firstFactorValues = new ArrayList<Float>();
		secondFactorValues = new ArrayList<Float>();
	}

	protected void generateOutput() {
		generateNumericalFactorOutput(result);
	}

	protected void resetStudyStats() {
		studyStats = new TreeMap<Float, Statistics>();
	}

}
