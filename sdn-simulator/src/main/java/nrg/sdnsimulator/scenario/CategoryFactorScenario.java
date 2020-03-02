package nrg.sdnsimulator.scenario;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import nrg.sdnsimulator.core.utility.Debugger;
import nrg.sdnsimulator.core.utility.Statistics;

public abstract class CategoryFactorScenario extends Scenario {

	protected LinkedHashMap<String, LinkedHashMap<String, Statistics>> result;
	protected LinkedHashMap<String, Statistics> studyStats;
	protected ArrayList<Float> firstFactorValues;
	protected ArrayList<Float> secondFactorValues;

	public CategoryFactorScenario(String studyName, String mainFactorName) {
		super(studyName, mainFactorName);
		result = new LinkedHashMap<String, LinkedHashMap<String, Statistics>>();
		studyStats = new LinkedHashMap<String, Statistics>();
		firstFactorValues = new ArrayList<Float>();
		secondFactorValues = new ArrayList<Float>();
	}

	protected void generateOutput() {
		generateCategoryFactorOutput(result);
		Debugger.debugOutPut();
	}

	protected void resetStudyStats() {
		studyStats = new LinkedHashMap<String, Statistics>();

	}
}
